/**
 * File: src/EAS/EAInst.java
 * -------------------------------------------------------------------------------------------
 * Date			Author      Changes
 * -------------------------------------------------------------------------------------------
 * 05/17/13		hcai		created; for instrumenting EAS method events
 * 05/20/13		hcai		add all probes required;
 * 05/21/13		hcai		fix all bugs till it works for normal returns and catch blocks;
 *							add Jimple code output before and after instrumentation for debugging purposes
 * 05/22/13		hcai		identify finally blocks and insert probes in each one of them; tested and working 
 * 05/23/13		hcai		move program start event probe from entry method to <clinit> to fix the exception
 *							thrown in the call to the entry probe in <clinit> before the monitor initialization, 
 *							which executes before the program entry
 * 05/24/13		hcai		fix program end event probe insertions in all the three possible places, 
 *							normal returns+catch block + finally block so that the termination monitor 
 *							is executed exactly once - this bug is found when running the implementation with
 *							XML-security; 
 *							fix program start probe to ensure monitor initialization is executed before each run by
 *							moving the probe back to the entry method, reset global timestamp in the initialization 
 *							instead to fix the previous bug for which the probe was moved to <clinit>; a "-sclinit" option
 *							is added for strictly probing the REAL program start in <clinit> when the instrumented
 *							subject is expected to run independently rather than via EARun
 * 05/25/13		hcai		critical fix: in the entry method, END event probe can be misplaced in catch blocks in the 
 *							absence of any finally block; fixed by ensuring each such probe is inserted at the end of
 *							each catch block; in the presence of finally blocks, no such probe is inserted in the
 *							related catch blocks and the probe is placed at the end of the finally block after which
 *							there is no more call sites
 * 05/26/13		hcai		replace lengthy strings used as parameters in various probes with the hashCode of the probe's 
 *							contextual object such as traps or call sites, to reduce the size of instrumented code
 * 06/07/13		hcai		important change: simplified program END probes by always simply inserting probe before the 
 *							returnstmt
 * 06/10/13		hcai		fix the assertion for the number of returns in the entry method - there might be multiple
 *							returns 
 * 07/04/13		hcai		if pertinent option specified, wrap around the whole body of a given method the outermost
 *							try-catch blocks like try{ original body ...} catch(Exception e) { throw e }
 * 07/05/13		hcai		factor reusable code out as common utilities for the whole mcia project 
 * 07/17/13		hcai		add missing probes for call sites within catch blocks and finally blocks
 * 10/09/13		hcai		add callee signature in the returnedInto probe invocations as the second argument, instead
 *							just the method name alone, for dumping dynamic call maps
 * 07/17/14		hcai		fixed import inconsistency bug at line 552 and 572: change the string pattern to be matched to 
 * 							from "EAS.Monitor: void returnedInto" to "Monitor: void returnedInto" so that Diver's customized monitors
 * 							can be matched too! (this issue caused, for the first time with PDFBox and then with BCEL, different lines of 
 * 							instrumented code between EAInstr and DiverInstr and subsequently diverged method sequences between
 * 							EARun and DiverRun, for the same input bytecode and test suite. (debugging on this cost almost a day!)
 * 02/05/15		hcai		for entry methods having 'return' statements, instrument 'terminate' before the last unit of their cfgs 
 * 02/16/15		hcai		added instrumentation dealing with forcible program termination via "System.exit" invocation 
*/
package IFM;

import IFM.EAOptions;
import MciaUtil.TryCatchWrapper;
import MciaUtil.utils;
import dua.Extension;
import dua.Forensics;
import dua.global.ProgramFlowGraph;
import dua.method.CFG;
import dua.method.CFG.CFGNode;
import dua.method.CallSite;
import dua.util.Util;
import profile.InstrumManager;
import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.ExceptionalBlockGraph;
import soot.util.Chain;

import java.io.File;
import java.util.*;

public class EAInst implements Extension {
	
	protected SootClass clsMonitor;
	
	protected SootMethod mInitialize;
	protected SootMethod mEnter;
	protected SootMethod mReturnInto;
	protected SootMethod mTerminate;
	
	protected File fJimpleOrig = null;
	protected File fJimpleInsted = null;
	
	protected static boolean bProgramStartInClinit = false;
	protected static EAOptions opts = new EAOptions();
	
	public static void main(String args[]){
		args = preProcessArgs(opts, args);

		EAInst eaInst = new EAInst();
		// examine catch blocks
//		dua.Options.ignoreCatchBlocks = false;
		dua.Options.ignoreCatchBlocks = Variant.isExceptionalFlow();

		Scene.v().addBasicClass("EAS.Monitor");
		
		Forensics.registerExtension(eaInst);
		Forensics.main(args);
	}
	
	protected static String[] preProcessArgs(EAOptions _opts, String[] args) {
		opts = _opts;
		args = opts.process(args);
		
		String[] argsForDuaF;
		int offset = 0;

		argsForDuaF = new String[args.length + 2 - offset];
		System.arraycopy(args, offset, argsForDuaF, 0, args.length-offset);
		argsForDuaF[args.length+1 - offset] = "-paramdefuses";
		argsForDuaF[args.length+0 - offset] = "-keeprepbrs";
		
		return argsForDuaF;
	}
	
	/**
	 * Descendants may want to use customized event monitors
	 */
	protected void init() {
		clsMonitor = Scene.v().getSootClass("EAS.Monitor");
		clsMonitor.setApplicationClass();
		
		
		mInitialize = clsMonitor.getMethodByName("initialize");
		mEnter = clsMonitor.getMethodByName("enter");
		mReturnInto = clsMonitor.getMethodByName("returnInto");
		mTerminate = clsMonitor.getMethodByName("terminate");
	}
	
	public void run() {
		System.out.println("Running EAS extension of DUA-Forensics");
		//StmtMapper.getCreateInverseMap();

		instrument();
	}
		
	public void instrument() {
		if (opts.dumpJimple()) {
			fJimpleOrig = new File(Util.getCreateBaseOutPath() + "JimpleOrig.out");
			utils.writeJimple(fJimpleOrig);
		}
		
		if (opts.dumpFunctionList()) {
			utils.dumpFunctionList(Util.getCreateBaseOutPath() + "functionList.out");
			//utils.dumpEntryReachableFunctionList(Util.getCreateBaseOutPath() + "functionList.out");
		}
		
		init();
		
		List<SootMethod> entryMes = ProgramFlowGraph.inst().getEntryMethods();
		//List<SootMethod> entryMes = utils.getEntryMethods(true);
		List<SootClass> entryClses = null;
		if (opts.sclinit()) {
			entryClses = new ArrayList<SootClass>();
			for (SootMethod m:entryMes) {
				entryClses.add(m.getDeclaringClass());
			}
		}
		System.out.println("EAInst instrument() /* traverse all classes */");
		/* traverse all classes */
		Iterator<SootClass> clsIt = Scene.v().getClasses().iterator();
		while (clsIt.hasNext()) {
			SootClass sClass = (SootClass) clsIt.next();
			if ( sClass.isPhantom() ) {
				// skip phantom classes
				continue;
			}
			if ( !sClass.isApplicationClass() ) {
				// skip library classes
				continue;
			}
			
			// when there is a static initializer in the entry class, we will instrument the monitor initialize in there instead of the entry method
			boolean hasCLINIT = false;
			if (opts.sclinit()) {
				if (entryClses.contains(sClass)) {
					try {
						SootMethod cl = sClass.getMethodByName("<clinit>");
						hasCLINIT = (cl != null);
					}
					catch (Exception e) {
						// if exception was thrown, either because there are more than one such method (ambiguous) or there is none
						hasCLINIT = (e instanceof RuntimeException && e.toString().contains("ambiguous method"));
					}
				}
			}

			System.out.println("EAInst instrument() /* traverse all methods of the class */");
			/* traverse all methods of the class */
			Iterator<SootMethod> meIt = sClass.getMethods().iterator();
			while (meIt.hasNext()) {
				SootMethod sMethod = (SootMethod) meIt.next();
				try
				{
				if ( !sMethod.isConcrete() ) {
					// skip abstract methods and phantom methods, and native methods as well
					continue; 
				}
				if ( sMethod.toString().indexOf(": java.lang.Class class$") != -1 ) {
					// don't handle reflections now either
					continue;
				}
				
				// cannot instrument method event for a method without active body
				if ( !sMethod.hasActiveBody() ) {
					continue;
				}
				
				//Body body = sMethod.getActiveBody();
				Body body = sMethod.retrieveActiveBody();
				
				/* the ID of a method to be used for identifying and indexing a method in the event maps of EAS */
				//String meId = sClass.getName() +	"::" + sMethod.getName();
				String meId = sMethod.getSignature();
				
				// wrap Try-Catch blocks before instrumenting EA monitors if Required
				if (opts.wrapTryCatch()) {
					TryCatchWrapper.wrapTryCatchAllBlocks(sMethod, opts.statUncaught(), opts.debugOut());
					//TryCatchWrapper.wrapTryCatchBlocks(sMethod, opts.debugOut());
				}
				
				PatchingChain<Unit> pchn = body.getUnits();
				//Chain<Local> locals = body.getLocals();
				
				/* 1. instrument method entry events and program start event */
				CFG cfg = ProgramFlowGraph.inst().getCFG(sMethod);
				
				if (cfg == null || !cfg.isReachableFromEntry()) {
					// skip dead CFG (method)
					if (opts.debugOut()) {
						System.out.println("\nSkipped method unreachable from entry: " + meId + "!");
					}
					continue;
				}
				
				// -- DEBUG
				if (opts.debugOut()) {
					System.out.println("\nNow instrumenting method " + meId + "...");
				}

				//List<CFGNode> cfgnodes = cfg.getFirstRealNonIdNode().getSuccs();
				/* the first non-Identity-Statement node is right where we instrument method entrance event */
				CFGNode firstNode = cfg.getFirstRealNonIdNode()/*, firstSuccessor = cfgnodes.get(0)*/;
				Stmt firstStmt = firstNode.getStmt();


				List<Stmt> enterProbes = new ArrayList<Stmt>();
				List<StringConstant> enterArgs = new ArrayList<StringConstant>();
				
				boolean bInstProgramStart = false;
				if (opts.sclinit()) {
					bInstProgramStart = (hasCLINIT && sMethod.getName().equalsIgnoreCase("<clinit>")) || 
														(!hasCLINIT && entryMes.contains(sMethod));
					if (!bProgramStartInClinit) {
						bProgramStartInClinit = (hasCLINIT && sMethod.getName().equalsIgnoreCase("<clinit>"));
					}
				}
				else {
					bInstProgramStart = entryMes.contains(sMethod);
				}
				
				// when "-sclinit" option specified, instrument monitor initialize in <clinit> if any, otherwise in entry method
				if ( bInstProgramStart ) {
					// before the entry method executes, the monitor needs be initialized
					List<StringConstant> initializeArgs = new ArrayList<StringConstant>();
					Stmt sInitializeCall = Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr(
							mInitialize.makeRef(), initializeArgs ));
					enterProbes.add(sInitializeCall);
					
					// -- DEBUG
					if (opts.debugOut()) {
						System.out.println("monitor initialization instrumented at the beginning of Entry method: " + meId);
					}
				} // -- if (entryMes.contains(sMethod))
				
				enterArgs.add(StringConstant.v(meId));
				Stmt sEnterCall = Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr(
						mEnter.makeRef(), enterArgs ));
				enterProbes.add(sEnterCall);
				
				// -- DEBUG
				if (opts.debugOut()) {
					System.out.println("monitor enter instrumented at the beginning of application method: " + meId);
				}
				if ( firstStmt != null ) {
					InstrumManager.v().insertBeforeNoRedirect(pchn, enterProbes, firstStmt);
				}
				else {
					InstrumManager.v().insertProbeAtEntry(sMethod, enterProbes);
				}
				
				//System.out.println("EAInst instrument() 2.1 normal returns from call sites OUTSIDE any traps (exception catchers)");
				// 2.1 normal returns from call sites OUTSIDE any traps (exception catchers) 
				List<CallSite> callsites = cfg.getCallSites();
				int nCandCallsites = 0;
				for (CallSite cs : callsites) {
					if (cs.hasAppCallees() && !cs.isInCatchBlock()) {
						nCandCallsites ++;
					}
					
					/** instrument 'terminate' monitor before 'System.exit' as a special treatment for Java programs */
					for (SootMethod ce : cs.getAllCallees()) {
						if (!ce.getSignature().equals("<java.lang.System: void exit(int)>")) {
							continue;
						}
						List<Stmt> terminateProbe = new ArrayList<Stmt>();
						List<StringConstant> terminateArgs = new ArrayList<StringConstant>();
						terminateArgs.add(StringConstant.v("terminate before System.exit"));
						Stmt sTerminateCall = Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr(
								mTerminate.makeRef(), terminateArgs ));
						terminateProbe.add(sTerminateCall);
						// -- DEBUG
						if (opts.debugOut()) {
							System.out.println("monitor termination instrumented at the end of " + 
									meId + " before System.exit");
						}
						InstrumManager.v().insertBeforeRedirect (pchn, terminateProbe, cs.getLoc().getStmt());
					}
				}
				boolean havingFinally = false;
				String bodySource = body.toString();
				if (bodySource.contains("throw") && 
					bodySource.contains(":= @caughtexception") &&
					bodySource.contains("catch java.lang.Throwable from")) {
					// a very naive, probably imprecise but safe, way to determine if there is any finally block in this method
					havingFinally = true;
					if (opts.debugOut) {
						System.out.println("finally block exists in method: " + meId);
					}
				}
				
				int nCurCallsite = 0;
				for (CallSite cs : callsites) {
					if (!cs.hasAppCallees()) {
						// only care about application calls
						continue;
					}
					
					if (cs.isInCatchBlock()) {
						if (opts.debugOut()) {
							System.out.println("[To instrument callsite in a Catch Block]");
						}
					}
					else {
						nCurCallsite ++;
					}
					// -- DEBUG
					if (opts.debugOut()) {
						System.out.println("monitor returnInto instrumented at call site " +
								cs + " in method: " + meId);
					}
					
					List<Stmt> retinProbes = new ArrayList<Stmt>();
					List<StringConstant> retinArgs = new ArrayList<StringConstant>();
					retinArgs.add(StringConstant.v(meId));
					retinArgs.add(StringConstant.v(/*cs.getLoc().toString()*/
							cs.hashCode() + ": returnInto after calling " + cs.getAppCallees().get(0).getSignature()/*.getName()*/));
					Stmt sReturnIntoCall = Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr(
							mReturnInto.makeRef(), retinArgs ));
					retinProbes.add(sReturnIntoCall);
					
					// before the entry method finishes, method events are to be dumped
					// -- the exit point of the entry method is regarded as the program end point

					// if there is no any finally block in the entry method, we probe for monitoring program termination after the last call site
					if (!havingFinally && nCurCallsite==nCandCallsites && entryMes.contains(sMethod)) {	
						List<StringConstant> terminateArgs = new ArrayList<StringConstant>();
						terminateArgs.add(StringConstant.v(/*cs.getLoc().toString()*/
								cs.hashCode() + ": terminate after calling " + cs.getAppCallees().get(0).getSignature()/*.getName()*/));
						Stmt sTerminateCall = Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr(
								mTerminate.makeRef(), terminateArgs ));
						//retinProbes.add(sTerminateCall);
						
					    // -- DEBUG
						if (opts.debugOut()) {
							System.out.println("monitor terminate instrumented at last app call site " +
									cs + " in method: " + meId);
						}
					}
					InstrumManager.v().insertAfter(pchn, retinProbes, cs.getLoc().getStmt());
				}
				
				BlockGraph bg = new ExceptionalBlockGraph(body);
				//List<Block> exitBlocks = bg.getTails();
				Map<Stmt, Stmt> cbExitStmts = new LinkedHashMap<Stmt, Stmt>(); // a map from head to exit of each catch block
				for (Block block : bg.getBlocks()) {
					//block.getTail();
					Stmt head = (Stmt)block.getHead(), curunit = head, exit = curunit;
					//if ( ProgramFlowGraph.inst().getNode((head)).isInCatchBlock() ) { // exception blocks are not handled in DuaF for now
					if ( head.toString().contains(":= @caughtexception") ) {	
						while (curunit != null) {
							exit = curunit;
							curunit = (Stmt)block.getSuccOf(curunit);
						}
						cbExitStmts.put(head, exit);
					}
				}
				

				//System.out.println("EAInst instrument() 2.2 catch blocks and finally blocks");
				// 2.2 catch blocks and finally blocks
				Chain<Trap> traps = body.getTraps();
				if (traps.size() < 1) {
					// no catchers, no catch block instrumentation
					//continue;
				}
				
				//System.out.println("EAInst instrument() // record instrumentation targets to avoid repetitive instrumentation");
				// record instrumentation targets to avoid repetitive instrumentation
				Set<Unit> instTargets = new LinkedHashSet<Unit>();
				for (Iterator<Trap> trapIt = traps.iterator(); trapIt.hasNext(); ) {
				    Trap trap = trapIt.next();

				    // instrument at the beginning of each catch block
				    // -- DEBUG
					if (opts.debugOut()) {
						System.out.println("monitor returnInto instrumented at the beginning of catch block " +
								trap + " in method: " + meId);
					}
					
					List<Stmt> retinProbes = new ArrayList<Stmt>();
					List<StringConstant> retinArgs = new ArrayList<StringConstant>();
					retinArgs.add(StringConstant.v(meId));
					retinArgs.add(StringConstant.v(/*trap.toString()*/
							trap.hashCode() + ": returnInto in catch block for " + trap.getException().getName()));
					Stmt sReturnIntoCall = Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr(
							mReturnInto.makeRef(), retinArgs ));
					retinProbes.add(sReturnIntoCall);
					
				    //InstrumManager.v().insertBeforeNoRedirect(pchn, retinProbes, (Stmt)trap.getHandlerUnit());
					Stmt instgt = (Stmt)trap.getHandlerUnit();
					if (!instTargets.contains(instgt)) {
						InstrumManager.v().insertAfter (pchn, retinProbes, instgt);
						instTargets.add(instgt);
					}
					System.out.println("EAInst instrument() // instrument at the beginning of each finally block if any");
					// instrument at the beginning of each finally block if any
					if (havingFinally) {
						if (trap.getHandlerUnit().equals(trap.getEndUnit())) {
							// we don't need instrument the probes at the same point twice
							// -- note that for a same TRY statement, the beginning of the finally block, which is trap.getEndUnit(),
							// might be equal to the beginning of a catch block
							continue;
						}
						/* whenever there is a finally block for a TRY statement (maybe without any catch block),
						 *   the block will be copied to the end of every trap; So we can just instrument at the end of
						 *   each of these traps to meet the requirement of instrumenting in each finally block
						 */
						List<Stmt> retinProbesF = new ArrayList<Stmt>();
						List<StringConstant> retinArgsF = new ArrayList<StringConstant>();
						retinArgsF.add(StringConstant.v(meId));
						retinArgsF.add(StringConstant.v(/*trap.toString() */
								trap.getEndUnit().hashCode() + ": returnInto in finally block for " + trap.getException().getName()));
						Stmt sReturnIntoCallF = Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr(
								mReturnInto.makeRef(), retinArgsF ));
						retinProbesF.add(sReturnIntoCallF);
						// -- DEBUG
						if (opts.debugOut()) {
							System.out.println("monitor returnInto instrumented at the beginning of the finally block " +
									trap.getEndUnit() + " in method: " + meId);
						}

						
						// again, avoid repetitive instrumentation although it does not affect the algorithm's correctness of EAS
						/** BUT IT DOES AFFECT Diver which uses full sequence of method events, at least for its current design */
						if (!((Stmt)trap.getEndUnit()).toString().contains("Monitor: void returnInto")) {
							InstrumManager.v().insertBeforeRedirect (pchn, retinProbesF, (Stmt)trap.getEndUnit());
						}
					}
					else if (entryMes.contains(sMethod)) {
						// if there is no finally block in the entry method, we can safely insert the probe for termination event
						// after the end of each catch block
						List<Stmt> terminateProbes = new ArrayList<Stmt>();
						List<StringConstant> terminateArgs = new ArrayList<StringConstant>();
						terminateArgs.add(StringConstant.v(/*trap.toString()*/
								trap.hashCode() + ": terminate in catch block for " + trap.getException().getName()));
						Stmt sTerminateCall = Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr(
								mTerminate.makeRef(), terminateArgs ));
						terminateProbes.add(sTerminateCall);
						// -- DEBUG
						if (opts.debugOut()) {
							System.out.println("monitor terminate instrumented after the end of catch block " +
									trap + " in method: " + meId);
						}
						// if a monitor probe has already become the end unit, we should insert this termination probe after that point
						if (((Stmt)trap.getEndUnit()).toString().contains("Monitor: void returnInto")) {
							//InstrumManager.v().insertAfter (pchn, terminateProbes, (Stmt)trap.getEndUnit());
						}
						// otherwise, just insert right before the last unit of the catch block
						else {

							Stmt cbHead = (Stmt)trap.getHandlerUnit(); // the handler unit is the beginning unit of the catch block
							assert cbExitStmts.containsKey(cbHead);
							Stmt tgt = cbExitStmts.get(cbHead);
							if (tgt instanceof GotoStmt /*&& !(tgt instanceof IdentityStmt)*/) {
								//InstrumManager.v().insertBeforeRedirect (pchn, terminateProbes, tgt);
							}
							else {
								//InstrumManager.v().insertAfter (pchn, terminateProbes, tgt);
							}
						}
					}
				}

				//System.out.println("EAInst instrument() // // Thus far, we have not insert probe for termination event in the entry method if there is any finally blocks in there");
				// Thus far, we have not insert probe for termination event in the entry method if there is any finally blocks in there
				if (entryMes.contains(sMethod) /*&& havingFinally*/) {
					Set<Stmt> fTargets = new LinkedHashSet<Stmt>();
					Stmt lastThrow = null;
					for (Unit u : pchn) {
						Stmt s = (Stmt)u;
						if (havingFinally) {
							// if the finally block is to be executed after an uncaught exception was thrown, a throw statement 
							// will be the exit point; However, we only probe at the last such finally block if there is no any app call site following it  
							if (s instanceof ThrowStmt) {
								//System.out.println("Got a throw statement " + s);
								lastThrow = s;
							}
							if (lastThrow != null) {
								try {
									// ProgramFlowGraph has no way to collect the nodes in the catch block we possibly added for outermost try-catch
									// block wrapping
									if (ProgramFlowGraph.inst().getNode(s).hasAppCallees()) {
										lastThrow = null;
									}
								}catch (Exception e) {}
							}
						}
						
						// In Jimple IR, any method has exactly one return statement; unless it ends with an infinite loop (like service daemon)
						if (Util.isReturnStmt(s)) {
							fTargets.add(s);
						}

					}
					if (lastThrow != null) {
						//fTargets.add(lastThrow);
					}
					Stmt laststmt = (Stmt)pchn.getLast();
					if (!fTargets.contains(laststmt)) {
						fTargets.add(laststmt);
					}
					if (fTargets.size() < 1) {
						System.out.println("WARNING: no return statement found in method: " + meId);
					}
					// assert fTargets.size() >= 1;
					for (Stmt tgt : fTargets) {
						List<Stmt> terminateProbe = new ArrayList<Stmt>();
						List<StringConstant> terminateArgs = new ArrayList<StringConstant>();
						String flag =  (Util.isReturnStmt(tgt)?"return":"throw") + " statement";
						terminateArgs.add(StringConstant.v("terminate before " + flag));
						Stmt sTerminateCall = Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr(
								mTerminate.makeRef(), terminateArgs ));
						terminateProbe.add(sTerminateCall);
						// -- DEBUG
						if (opts.debugOut()) {
							System.out.println("monitor termination instrumented at the end of " + 
									meId + " before " + flag);
						}
						InstrumManager.v().insertBeforeRedirect (pchn, terminateProbe, tgt);
					}
				}
		         } catch (Exception e) { }
			} // -- while (meIt.hasNext()) 
			
		} // -- while (clsIt.hasNext())
		System.out.println("After -- while (clsIt.hasNext())");
		if (opts.dumpJimple()) {
			fJimpleInsted = new File(Util.getCreateBaseOutPath() + "JimpleInstrumented.out");
			utils.writeJimple(fJimpleInsted);
		}
		System.out.println("---------------------------// important warning concerning the later runs of the instrumented subject");
		// important warning concerning the later runs of the instrumented subject
		if (bProgramStartInClinit) {
			System.out.println("\n***NOTICE***: program start event probe has been inserted in EntryClass::<clinit>(), " +
					"the instrumented subject MUST thereafter run independently rather than via EARun!");
		}
	} // -- void instrument
	
	public void instrument(HashSet<String> methodArray) {
		System.out.println("instrument 0");
		if (opts.dumpJimple()) {
			fJimpleOrig = new File(Util.getCreateBaseOutPath() + "JimpleOrig.out");
			utils.writeJimple(fJimpleOrig);
		}
		System.out.println("instrument 1st");
		if (opts.dumpFunctionList()) {
			utils.dumpFunctionList(Util.getCreateBaseOutPath() + "functionList.out");
			//utils.dumpEntryReachableFunctionList(Util.getCreateBaseOutPath() + "functionList.out");
		}

		System.out.println("instrument 2nd");
		init();
		System.out.println("instrument 3rd");
		
		List<SootMethod> entryMes = ProgramFlowGraph.inst().getEntryMethods();
		//List<SootMethod> entryMes = utils.getEntryMethods(true);
		List<SootClass> entryClses = null;
		if (opts.sclinit()) {
			entryClses = new ArrayList<SootClass>();
			for (SootMethod m:entryMes) {
				entryClses.add(m.getDeclaringClass());
			}
		}
		System.out.println("instrument 4th");
		/* traverse all classes */
		Iterator<SootClass> clsIt = Scene.v().getClasses().iterator();
		while (clsIt.hasNext()) {
			SootClass sClass = (SootClass) clsIt.next();
			if ( sClass.isPhantom() ) {
				// skip phantom classes
				continue;
			}
			if ( !sClass.isApplicationClass() ) {
				// skip library classes
				continue;
			}
			
			// when there is a static initializer in the entry class, we will instrument the monitor initialize in there instead of the entry method
			boolean hasCLINIT = false;
			if (opts.sclinit()) {
				if (entryClses.contains(sClass)) {
					try {
						SootMethod cl = sClass.getMethodByName("<clinit>");
						hasCLINIT = (cl != null);
					}
					catch (Exception e) {
						// if exception was thrown, either because there are more than one such method (ambiguous) or there is none
						hasCLINIT = (e instanceof RuntimeException && e.toString().contains("ambiguous method"));
					}
				}
			}
			System.out.println("instrument 5th");
			
			/* traverse all methods of the class */
			Iterator<SootMethod> meIt = sClass.getMethods().iterator();
			while (meIt.hasNext()) {
				SootMethod sMethod = (SootMethod) meIt.next();
				if ( !sMethod.isConcrete() ) {
					// skip abstract methods and phantom methods, and native methods as well
					continue; 
				}
				if ( sMethod.toString().indexOf(": java.lang.Class class$") != -1 ) {
					// don't handle reflections now either
					continue;
				}
				
				// cannot instrument method event for a method without active body
				if ( !sMethod.hasActiveBody() ) {
					continue;
				}
				//System.out.println("sMethod="+sMethod+" sMethod.getName()="+sMethod.getName()+" sMethod.getSignature()="+sMethod.getSignature());
				if ( !(methodArray.contains(sMethod) || methodArray.contains(sMethod.getName()) || methodArray.contains(sMethod.getSignature() ))) {
					continue;
				}
				//Body body = sMethod.getActiveBody();
				Body body = sMethod.retrieveActiveBody();
				
				/* the ID of a method to be used for identifying and indexing a method in the event maps of EAS */
				//String meId = sClass.getName() +	"::" + sMethod.getName();
				String meId = sMethod.getSignature();
				
				// wrap Try-Catch blocks before instrumenting EA monitors if Required
				if (opts.wrapTryCatch()) {
					TryCatchWrapper.wrapTryCatchAllBlocks(sMethod, opts.statUncaught(), opts.debugOut());
					//TryCatchWrapper.wrapTryCatchBlocks(sMethod, opts.debugOut());
				}
				
				PatchingChain<Unit> pchn = body.getUnits();
				//Chain<Local> locals = body.getLocals();
				
				/* 1. instrument method entry events and program start event */
				CFG cfg = ProgramFlowGraph.inst().getCFG(sMethod);
				
				if (cfg == null || !cfg.isReachableFromEntry()) {
					// skip dead CFG (method)
					if (opts.debugOut()) {
						System.out.println("\nSkipped method unreachable from entry: " + meId + "!");
					}
					continue;
				}
				
				// -- DEBUG
				if (opts.debugOut()) {
					System.out.println("\nNow instrumenting method " + meId + "...");
				}

				//List<CFGNode> cfgnodes = cfg.getFirstRealNonIdNode().getSuccs();
				/* the first non-Identity-Statement node is right where we instrument method entrance event */
				CFGNode firstNode = cfg.getFirstRealNonIdNode()/*, firstSuccessor = cfgnodes.get(0)*/;
				Stmt firstStmt = firstNode.getStmt();
			

				List<Stmt> enterProbes = new ArrayList<Stmt>();
				List<StringConstant> enterArgs = new ArrayList<StringConstant>();
				
				boolean bInstProgramStart = false;
				if (opts.sclinit()) {
					bInstProgramStart = (hasCLINIT && sMethod.getName().equalsIgnoreCase("<clinit>")) || 
														(!hasCLINIT && entryMes.contains(sMethod));
					if (!bProgramStartInClinit) {
						bProgramStartInClinit = (hasCLINIT && sMethod.getName().equalsIgnoreCase("<clinit>"));
					}
				}
				else {
					bInstProgramStart = entryMes.contains(sMethod);
				}
				
				// when "-sclinit" option specified, instrument monitor initialize in <clinit> if any, otherwise in entry method
				if ( bInstProgramStart ) {
					// before the entry method executes, the monitor needs be initialized
					List<StringConstant> initializeArgs = new ArrayList<StringConstant>();
					Stmt sInitializeCall = Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr(
							mInitialize.makeRef(), initializeArgs ));
					enterProbes.add(sInitializeCall);
					
					// -- DEBUG
					if (opts.debugOut()) {
						System.out.println("monitor initialization instrumented at the beginning of Entry method: " + meId);
					}
				} // -- if (entryMes.contains(sMethod))
				
				enterArgs.add(StringConstant.v(meId));
				Stmt sEnterCall = Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr(
						mEnter.makeRef(), enterArgs ));
				enterProbes.add(sEnterCall);
				
				// -- DEBUG
				if (opts.debugOut()) {
					System.out.println("monitor enter instrumented at the beginning of application method: " + meId);
				}
				if ( firstStmt != null ) {
					InstrumManager.v().insertBeforeNoRedirect(pchn, enterProbes, firstStmt);
				}
				else {
					InstrumManager.v().insertProbeAtEntry(sMethod, enterProbes);
				}
				
							
				/* 2. instrument method returned-into events and program termination event */
			

				
				// 2.1 normal returns from call sites OUTSIDE any traps (exception catchers) 
				List<CallSite> callsites = cfg.getCallSites();
				int nCandCallsites = 0;
				for (CallSite cs : callsites) {
					if (cs.hasAppCallees() && !cs.isInCatchBlock()) {
						nCandCallsites ++;
					}
					
					/** instrument 'terminate' monitor before 'System.exit' as a special treatment for Java programs */
					for (SootMethod ce : cs.getAllCallees()) {
						if (!ce.getSignature().equals("<java.lang.System: void exit(int)>")) {
							continue;
						}
						List<Stmt> terminateProbe = new ArrayList<Stmt>();
						List<StringConstant> terminateArgs = new ArrayList<StringConstant>();
						terminateArgs.add(StringConstant.v("terminate before System.exit"));
						Stmt sTerminateCall = Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr(
								mTerminate.makeRef(), terminateArgs ));
						terminateProbe.add(sTerminateCall);
						// -- DEBUG
						if (opts.debugOut()) {
							System.out.println("monitor termination instrumented at the end of " + 
									meId + " before System.exit");
						}
						InstrumManager.v().insertBeforeRedirect (pchn, terminateProbe, cs.getLoc().getStmt());
					}
				}
				if ( !(methodArray.contains(sMethod) || methodArray.contains(sMethod.getName()) || methodArray.contains(sMethod.getSignature() ))) {
					continue;
				}
				boolean havingFinally = false;
				String bodySource = body.toString();
				if (bodySource.contains("throw") && 
					bodySource.contains(":= @caughtexception") &&
					bodySource.contains("catch java.lang.Throwable from")) {
					// a very naive, probably imprecise but safe, way to determine if there is any finally block in this method
					havingFinally = true;
					if (opts.debugOut) {
						System.out.println("finally block exists in method: " + meId);
					}
				}
				
				int nCurCallsite = 0;
				for (CallSite cs : callsites) {
					if (!cs.hasAppCallees()) {
						// only care about application calls
						continue;
					}
					
					if (cs.isInCatchBlock()) {
						/*
						// for catch blocks, we always instrument at the beginning of each block, without looking into
						// the call sites inside the block
						continue;
						*/
						if (opts.debugOut()) {
							System.out.println("[To instrument callsite in a Catch Block]");
						}
					}
					else {
						nCurCallsite ++;
					}
					// -- DEBUG
					if (opts.debugOut()) {
						System.out.println("monitor returnInto instrumented at call site " +
								cs + " in method: " + meId);
					}
					
					List<Stmt> retinProbes = new ArrayList<Stmt>();
					List<StringConstant> retinArgs = new ArrayList<StringConstant>();
					retinArgs.add(StringConstant.v(meId));
					retinArgs.add(StringConstant.v(/*cs.getLoc().toString()*/
							cs.hashCode() + ": returnInto after calling " + cs.getAppCallees().get(0).getSignature()/*.getName()*/));
					Stmt sReturnIntoCall = Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr(
							mReturnInto.makeRef(), retinArgs ));
					retinProbes.add(sReturnIntoCall);
					
					// before the entry method finishes, method events are to be dumped
					// -- the exit point of the entry method is regarded as the program end point

					// if there is no any finally block in the entry method, we probe for monitoring program termination after the last call site
					if (!havingFinally && nCurCallsite==nCandCallsites && entryMes.contains(sMethod)) {	
						List<StringConstant> terminateArgs = new ArrayList<StringConstant>();
						terminateArgs.add(StringConstant.v(/*cs.getLoc().toString()*/
								cs.hashCode() + ": terminate after calling " + cs.getAppCallees().get(0).getSignature()/*.getName()*/));
						Stmt sTerminateCall = Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr(
								mTerminate.makeRef(), terminateArgs ));
						//retinProbes.add(sTerminateCall);
						
					    // -- DEBUG
						if (opts.debugOut()) {
							System.out.println("monitor terminate instrumented at last app call site " +
									cs + " in method: " + meId);
						}
					}
					InstrumManager.v().insertAfter(pchn, retinProbes, cs.getLoc().getStmt());
					/*
					InstrumManager.v().insertAtProbeBottom(pchn, retinProbes, cs.getLoc().getStmt());
					InstrumManager.v().insertRightBeforeNoRedirect(pchn, retinProbes, cs.getLoc().getStmt());
					*/
				}
				
				BlockGraph bg = new ExceptionalBlockGraph(body);
				//List<Block> exitBlocks = bg.getTails();
				Map<Stmt, Stmt> cbExitStmts = new LinkedHashMap<Stmt, Stmt>(); // a map from head to exit of each catch block
				for (Block block : bg.getBlocks()) {
					//block.getTail();
					Stmt head = (Stmt)block.getHead(), curunit = head, exit = curunit;
					//if ( ProgramFlowGraph.inst().getNode((head)).isInCatchBlock() ) { // exception blocks are not handled in DuaF for now
					if ( head.toString().contains(":= @caughtexception") ) {	
						while (curunit != null) {
							exit = curunit;
							curunit = (Stmt)block.getSuccOf(curunit);
						}
						cbExitStmts.put(head, exit);
					}
				}

				// 2.2 catch blocks and finally blocks
				Chain<Trap> traps = body.getTraps();
				if (traps.size() < 1) {
					// no catchers, no catch block instrumentation
					//continue;
				}
				

				// record instrumentation targets to avoid repetitive instrumentation
				Set<Unit> instTargets = new LinkedHashSet<Unit>();
				for (Iterator<Trap> trapIt = traps.iterator(); trapIt.hasNext(); ) {
				    Trap trap = trapIt.next();

				    // instrument at the beginning of each catch block
				    // -- DEBUG
					if (opts.debugOut()) {
						System.out.println("monitor returnInto instrumented at the beginning of catch block " +
								trap + " in method: " + meId);
					}
					
					List<Stmt> retinProbes = new ArrayList<Stmt>();
					List<StringConstant> retinArgs = new ArrayList<StringConstant>();
					retinArgs.add(StringConstant.v(meId));
					retinArgs.add(StringConstant.v(/*trap.toString()*/
							trap.hashCode() + ": returnInto in catch block for " + trap.getException().getName()));
					Stmt sReturnIntoCall = Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr(
							mReturnInto.makeRef(), retinArgs ));
					retinProbes.add(sReturnIntoCall);

				    //InstrumManager.v().insertBeforeNoRedirect(pchn, retinProbes, (Stmt)trap.getHandlerUnit());
					Stmt instgt = (Stmt)trap.getHandlerUnit();
					if (!instTargets.contains(instgt)) {
						InstrumManager.v().insertAfter (pchn, retinProbes, instgt);
						instTargets.add(instgt);
					}
					
					// instrument at the beginning of each finally block if any
					if (havingFinally) {
						if (trap.getHandlerUnit().equals(trap.getEndUnit())) {
							// we don't need instrument the probes at the same point twice
							// -- note that for a same TRY statement, the beginning of the finally block, which is trap.getEndUnit(),
							// might be equal to the beginning of a catch block
							continue;
						}
						/* whenever there is a finally block for a TRY statement (maybe without any catch block),
						 *   the block will be copied to the end of every trap; So we can just instrument at the end of
						 *   each of these traps to meet the requirement of instrumenting in each finally block
						 */
						List<Stmt> retinProbesF = new ArrayList<Stmt>();
						List<StringConstant> retinArgsF = new ArrayList<StringConstant>();
						retinArgsF.add(StringConstant.v(meId));
						retinArgsF.add(StringConstant.v(/*trap.toString() */
								trap.getEndUnit().hashCode() + ": returnInto in finally block for " + trap.getException().getName()));
						Stmt sReturnIntoCallF = Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr(
								mReturnInto.makeRef(), retinArgsF ));
						retinProbesF.add(sReturnIntoCallF);
						// -- DEBUG
						if (opts.debugOut()) {
							System.out.println("monitor returnInto instrumented at the beginning of the finally block " +
									trap.getEndUnit() + " in method: " + meId);
						}

						// again, avoid repetitive instrumentation although it does not affect the algorithm's correctness of EAS
						/** BUT IT DOES AFFECT Diver which uses full sequence of method events, at least for its current design */
						if (!((Stmt)trap.getEndUnit()).toString().contains("Monitor: void returnInto")) {
							InstrumManager.v().insertBeforeRedirect (pchn, retinProbesF, (Stmt)trap.getEndUnit());
						}
					}
					else if (entryMes.contains(sMethod)) {
						// if there is no finally block in the entry method, we can safely insert the probe for termination event
						// after the end of each catch block
						List<Stmt> terminateProbes = new ArrayList<Stmt>();
						List<StringConstant> terminateArgs = new ArrayList<StringConstant>();
						terminateArgs.add(StringConstant.v(/*trap.toString()*/
								trap.hashCode() + ": terminate in catch block for " + trap.getException().getName()));
						Stmt sTerminateCall = Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr(
								mTerminate.makeRef(), terminateArgs ));
						terminateProbes.add(sTerminateCall);
						// -- DEBUG
						if (opts.debugOut()) {
							System.out.println("monitor terminate instrumented after the end of catch block " +
									trap + " in method: " + meId);
						}
						// if a monitor probe has already become the end unit, we should insert this termination probe after that point
						if (((Stmt)trap.getEndUnit()).toString().contains("Monitor: void returnInto")) {
							//InstrumManager.v().insertAfter (pchn, terminateProbes, (Stmt)trap.getEndUnit());
						}
						// otherwise, just insert right before the last unit of the catch block
						else {

							Stmt cbHead = (Stmt)trap.getHandlerUnit(); // the handler unit is the beginning unit of the catch block
							assert cbExitStmts.containsKey(cbHead);
							Stmt tgt = cbExitStmts.get(cbHead);
							if (tgt instanceof GotoStmt /*&& !(tgt instanceof IdentityStmt)*/) {
								//InstrumManager.v().insertBeforeRedirect (pchn, terminateProbes, tgt);
							}
							else {
								//InstrumManager.v().insertAfter (pchn, terminateProbes, tgt);
							}
						}
					}
				}
				
				// Thus far, we have not insert probe for termination event in the entry method if there is any finally blocks in there
				if (entryMes.contains(sMethod) /*&& havingFinally*/) {
					Set<Stmt> fTargets = new LinkedHashSet<Stmt>();
					Stmt lastThrow = null;
					for (Unit u : pchn) {
						Stmt s = (Stmt)u;
						if (havingFinally) {
							// if the finally block is to be executed after an uncaught exception was thrown, a throw statement 
							// will be the exit point; However, we only probe at the last such finally block if there is no any app call site following it  
							if (s instanceof ThrowStmt) {
								//System.out.println("Got a throw statement " + s);
								lastThrow = s;
							}
							if (lastThrow != null) {
								try {
									// ProgramFlowGraph has no way to collect the nodes in the catch block we possibly added for outermost try-catch
									// block wrapping
									if (ProgramFlowGraph.inst().getNode(s).hasAppCallees()) {
										lastThrow = null;
									}
								}catch (Exception e) {}
							}
						}
						
						// In Jimple IR, any method has exactly one return statement; unless it ends with an infinite loop (like service daemon)
						if (Util.isReturnStmt(s)) {
							fTargets.add(s);
						}

					}
					if (lastThrow != null) {
						//fTargets.add(lastThrow);
					}
					Stmt laststmt = (Stmt)pchn.getLast();
					if (!fTargets.contains(laststmt)) {
						fTargets.add(laststmt);
					}
					if (fTargets.size() < 1) {
						System.out.println("WARNING: no return statement found in method: " + meId);
					}
					// assert fTargets.size() >= 1;
					for (Stmt tgt : fTargets) {
						List<Stmt> terminateProbe = new ArrayList<Stmt>();
						List<StringConstant> terminateArgs = new ArrayList<StringConstant>();
						String flag =  (Util.isReturnStmt(tgt)?"return":"throw") + " statement";
						terminateArgs.add(StringConstant.v("terminate before " + flag));
						Stmt sTerminateCall = Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr(
								mTerminate.makeRef(), terminateArgs ));
						terminateProbe.add(sTerminateCall);
						// -- DEBUG
						if (opts.debugOut()) {
							System.out.println("monitor termination instrumented at the end of " + 
									meId + " before " + flag);
						}
						InstrumManager.v().insertBeforeRedirect (pchn, terminateProbe, tgt);
					}
				}
				
			} // -- while (meIt.hasNext()) 
			
		} // -- while (clsIt.hasNext())
		
		if (opts.dumpJimple()) {
			fJimpleInsted = new File(Util.getCreateBaseOutPath() + "JimpleInstrumented.out");
			utils.writeJimple(fJimpleInsted);
		}
	
		// important warning concerning the later runs of the instrumented subject
		if (bProgramStartInClinit) {
			System.out.println("\n***NOTICE***: program start event probe has been inserted in EntryClass::<clinit>(), " +
					"the instrumented subject MUST thereafter run independently rather than via EARun!");
		}
	} // -- void instrument(ArrayList)
	
	public void instrumentTerminate() {
		if (opts.dumpJimple()) {
			fJimpleOrig = new File(Util.getCreateBaseOutPath() + "JimpleOrig.out");
			utils.writeJimple(fJimpleOrig);
		}
		
		if (opts.dumpFunctionList()) {
			utils.dumpFunctionList(Util.getCreateBaseOutPath() + "functionList.out");
			//utils.dumpEntryReachableFunctionList(Util.getCreateBaseOutPath() + "functionList.out");
		}
		
		init();
		
		List<SootMethod> entryMes = ProgramFlowGraph.inst().getEntryMethods();
		//List<SootMethod> entryMes = utils.getEntryMethods(true);
		List<SootClass> entryClses = null;
		if (opts.sclinit()) {
			entryClses = new ArrayList<SootClass>();
			for (SootMethod m:entryMes) {
				entryClses.add(m.getDeclaringClass());
			}
		}
		
		/* traverse all classes */
		Iterator<SootClass> clsIt = Scene.v().getClasses().iterator();
		while (clsIt.hasNext()) {
			SootClass sClass = (SootClass) clsIt.next();
			if ( sClass.isPhantom() ) {
				// skip phantom classes
				continue;
			}
			if ( !sClass.isApplicationClass() ) {
				// skip library classes
				continue;
			}
			
			// when there is a static initializer in the entry class, we will instrument the monitor initialize in there instead of the entry method
			boolean hasCLINIT = false;
			if (opts.sclinit()) {
				if (entryClses.contains(sClass)) {
					try {
						SootMethod cl = sClass.getMethodByName("<clinit>");
						hasCLINIT = (cl != null);
					}
					catch (Exception e) {
						// if exception was thrown, either because there are more than one such method (ambiguous) or there is none
						hasCLINIT = (e instanceof RuntimeException && e.toString().contains("ambiguous method"));
					}
				}
			}
			
			/* traverse all methods of the class */
			Iterator<SootMethod> meIt = sClass.getMethods().iterator();
			while (meIt.hasNext()) {
				SootMethod sMethod = (SootMethod) meIt.next();
				if ( !sMethod.isConcrete() ) {
					// skip abstract methods and phantom methods, and native methods as well
					continue; 
				}
				if ( sMethod.toString().indexOf(": java.lang.Class class$") != -1 ) {
					// don't handle reflections now either
					continue;
				}
				
				// cannot instrument method event for a method without active body
				if ( !sMethod.hasActiveBody() ) {
					continue;
				}
				
				//Body body = sMethod.getActiveBody();
				Body body = sMethod.retrieveActiveBody();
				
				/* the ID of a method to be used for identifying and indexing a method in the event maps of EAS */
				//String meId = sClass.getName() +	"::" + sMethod.getName();
				String meId = sMethod.getSignature();
				
				// wrap Try-Catch blocks before instrumenting EA monitors if Required
				if (opts.wrapTryCatch()) {
					TryCatchWrapper.wrapTryCatchAllBlocks(sMethod, opts.statUncaught(), opts.debugOut());
					//TryCatchWrapper.wrapTryCatchBlocks(sMethod, opts.debugOut());
				}
				
				PatchingChain<Unit> pchn = body.getUnits();
				//Chain<Local> locals = body.getLocals();
				
				/* 1. instrument method entry events and program start event */
				CFG cfg = ProgramFlowGraph.inst().getCFG(sMethod);
				
				if (cfg == null || !cfg.isReachableFromEntry()) {
					// skip dead CFG (method)
					if (opts.debugOut()) {
						System.out.println("\nSkipped method unreachable from entry: " + meId + "!");
					}
					continue;
				}
				
				// -- DEBUG
				if (opts.debugOut()) {
					System.out.println("\nNow instrumenting method " + meId + "...");
				}

				//List<CFGNode> cfgnodes = cfg.getFirstRealNonIdNode().getSuccs();
				/* the first non-Identity-Statement node is right where we instrument method entrance event */
				CFGNode firstNode = cfg.getFirstRealNonIdNode()/*, firstSuccessor = cfgnodes.get(0)*/;
				Stmt firstStmt = firstNode.getStmt();

				List<Stmt> enterProbes = new ArrayList<Stmt>();
				List<StringConstant> enterArgs = new ArrayList<StringConstant>();
				
				boolean bInstProgramStart = false;
				if (opts.sclinit()) {
					bInstProgramStart = (hasCLINIT && sMethod.getName().equalsIgnoreCase("<clinit>")) || 
														(!hasCLINIT && entryMes.contains(sMethod));
					if (!bProgramStartInClinit) {
						bProgramStartInClinit = (hasCLINIT && sMethod.getName().equalsIgnoreCase("<clinit>"));
					}
				}
				else {
					bInstProgramStart = entryMes.contains(sMethod);
				}
				
				// when "-sclinit" option specified, instrument monitor initialize in <clinit> if any, otherwise in entry method
				if ( bInstProgramStart ) {
					// before the entry method executes, the monitor needs be initialized
					List<StringConstant> initializeArgs = new ArrayList<StringConstant>();
					Stmt sInitializeCall = Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr(
							mInitialize.makeRef(), initializeArgs ));
					enterProbes.add(sInitializeCall);
					
					// -- DEBUG
					if (opts.debugOut()) {
						System.out.println("monitor initialization instrumented at the beginning of Entry method: " + meId);
					}
				} // -- if (entryMes.contains(sMethod))
				
				enterArgs.add(StringConstant.v(meId));
				Stmt sEnterCall = Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr(
						mEnter.makeRef(), enterArgs ));
				enterProbes.add(sEnterCall);
				
				// -- DEBUG
				if (opts.debugOut()) {
					System.out.println("monitor enter instrumented at the beginning of application method: " + meId);
				}
				if ( firstStmt != null ) {
					InstrumManager.v().insertBeforeNoRedirect(pchn, enterProbes, firstStmt);
				}
				else {
					InstrumManager.v().insertProbeAtEntry(sMethod, enterProbes);
				}
				

				// Thus far, we have not insert probe for termination event in the entry method if there is any finally blocks in there
				if (entryMes.contains(sMethod) /*&& havingFinally*/) {
					Set<Stmt> fTargets = new LinkedHashSet<Stmt>();
					Stmt lastThrow = null;
					for (Unit u : pchn) {
						Stmt s = (Stmt)u;

						
						// In Jimple IR, any method has exactly one return statement; unless it ends with an infinite loop (like service daemon)
						if (Util.isReturnStmt(s)) {
							fTargets.add(s);
						}

					}
					if (lastThrow != null) {
						//fTargets.add(lastThrow);
					}
					Stmt laststmt = (Stmt)pchn.getLast();
					if (!fTargets.contains(laststmt)) {
						fTargets.add(laststmt);
					}
					if (fTargets.size() < 1) {
						System.out.println("WARNING: no return statement found in method: " + meId);
					}
					// assert fTargets.size() >= 1;
					for (Stmt tgt : fTargets) {
						List<Stmt> terminateProbe = new ArrayList<Stmt>();
						List<StringConstant> terminateArgs = new ArrayList<StringConstant>();
						String flag =  (Util.isReturnStmt(tgt)?"return":"throw") + " statement";
						terminateArgs.add(StringConstant.v("terminate before " + flag));
						Stmt sTerminateCall = Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr(
								mTerminate.makeRef(), terminateArgs ));
						terminateProbe.add(sTerminateCall);
						// -- DEBUG
						if (opts.debugOut()) {
							System.out.println("monitor termination instrumented at the end of " + 
									meId + " before " + flag);
						}
						InstrumManager.v().insertBeforeRedirect (pchn, terminateProbe, tgt);
					}
				}
				
			} // -- while (meIt.hasNext()) 
			
		} // -- while (clsIt.hasNext())

		System.out.println("instrument 6th");
		
		if (opts.dumpJimple()) {
			fJimpleInsted = new File(Util.getCreateBaseOutPath() + "JimpleInstrumented.out");
			utils.writeJimple(fJimpleInsted);
		}
	
		// important warning concerning the later runs of the instrumented subject
		if (bProgramStartInClinit) {
			System.out.println("\n***NOTICE***: program start event probe has been inserted in EntryClass::<clinit>(), " +
					"the instrumented subject MUST thereafter run independently rather than via EARun!");
		}

		System.out.println("instrument 7th");
	} // -- void instrument
} // -- public class EAInst  

/* vim :set ts=4 tw=4 tws=4 */

