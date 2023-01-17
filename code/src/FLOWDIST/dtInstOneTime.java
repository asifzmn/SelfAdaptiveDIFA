package FLOWDIST;

import dua.Forensics;
import fault.StmtMapper;
import soot.*;
import soot.util.dot.DotGraph;
import EAS.*;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Iterator;
import java.util.Set;

import profile.InstrumManager;
import dua.global.ProgramFlowGraph;
import dua.method.CFG;
import dua.method.CFG.CFGNode;
import dua.util.Util;
import soot.jimple.*;
import EAS.EAInst;
import MciaUtil.utils;


public class dtInstOneTime extends EAInst {
	
	protected static dtOptions opts = new dtOptions();
	
	//distEA variables
	protected SootMethod mReturnFrom;
	
	protected SootMethod mNioRead;
	protected SootMethod mNioWrite;
	protected SootClass cDistSockInStream;
	protected SootClass cDistSockOutStream;
	
	protected SootMethod mObjStreamRead;
	protected SootMethod mObjStreamWrite;
	public static int methodNum=0;
	protected SootClass clsMonitor;
	public static void main(String args[]){
		args = preProcessArgs(opts, args);

		dtInstOneTime dvInst = new dtInstOneTime();
		// examine catch blocks
		dua.Options.ignoreCatchBlocks = false;
		
		if (opts.monitor_per_thread()) {
			Scene.v().addBasicClass("disttaint.dtThreadMonitor");
		}
		else {
			Scene.v().addBasicClass("disttaint.dtMonitor");
		}
		if (opts.use_socket()) {
			Scene.v().addBasicClass("disttaint.dtSocketInputStream");
			Scene.v().addBasicClass("disttaint.dtSocketOutputStream");
		}
		
		Forensics.registerExtension(dvInst);
		Forensics.main(args);
	}
	
	@Override protected void init() {
		
//		if (opts.monitor_per_thread()) {
//			clsMonitor = Scene.v().getSootClass("disttaint.dtThreadMonitor");
//		}
//		else 
		{
			clsMonitor = Scene.v().getSootClass("disttaint.dtMonitor");
		}
		clsMonitor.setApplicationClass();
		mInitialize = clsMonitor.getMethodByName("initialize");
		mEnter = clsMonitor.getMethodByName("enter");
		mReturnInto = clsMonitor.getMethodByName("returnInto");
		mTerminate = clsMonitor.getMethodByName("terminate");
		
		mReturnFrom = clsMonitor.getMethodByName("returnFrom");
		
		cDistSockInStream = Scene.v().getSootClass("disttaint.dtSocketInputStream");
		cDistSockOutStream = Scene.v().getSootClass("disttaint.dtSocketOutputStream");
		mNioRead = clsMonitor.getMethodByName("dist_nioread");
		mNioWrite = clsMonitor.getMethodByName("dist_niowrite");
		
		mObjStreamRead = clsMonitor.getMethodByName("dist_objstreamread");
		mObjStreamWrite = clsMonitor.getMethodByName("dist_objstreamwrite");
	}
	
	@Override public void run() {
		instrument();
		this.instRetEvents();
	    this.instCommEvents();
	}
	
	/** monitoring return events, in addition to monitoring returned-into events, is needed in presence of multi-threaded executions */
	public void instRetEvents() {
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
				
				PatchingChain<Unit> pchn = body.getUnits();
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
					System.out.println("\nNow instrumenting method for return Events: " + meId + "...");
				}

				Set<Stmt> fTargets = new LinkedHashSet<Stmt>();
				for (Unit u : pchn) {
					Stmt s = (Stmt)u;
					// In Jimple IR, any method has exactly one return statement; unless it ends with an infinite loop (like service daemon)
					if (dua.util.Util.isReturnStmt(s)) {
						fTargets.add(s);
					}
				}
				Stmt laststmt = (Stmt)pchn.getLast();
				if (!fTargets.contains(laststmt)) {
					fTargets.add(laststmt);
				}
				if (fTargets.size() < 1) {
					System.out.println("WARNING: no return statement found in method: " + meId);
					continue;
				}
				for (Stmt tgt : fTargets) {
					List<Stmt> retProbes = new ArrayList<Stmt>();
					List<StringConstant> retArgs = new ArrayList<StringConstant>();
					retArgs.add(StringConstant.v(meId));
					retArgs.add(StringConstant.v(tgt.hashCode() + ": return from " + sMethod.getNumber()));
					Stmt sReturnCall = Jimple.v().newInvokeStmt( Jimple.v().newStaticInvokeExpr(
							mReturnFrom.makeRef(), retArgs ));
					retProbes.add(sReturnCall);
					
					// -- DEBUG
					if (opts.debugOut()) {
						System.out.println("monitor returnFrom instrumented before the statement " + 
								tgt + " in method " + meId);
					}
					
					Stmt pretgt = (Stmt)pchn.getPredOf(tgt);
					if (pretgt == null) {
						InstrumManager.v().insertBeforeRedirect (pchn, retProbes, tgt);
					}
					else {
						InstrumManager.v().insertBeforeRedirect (pchn, retProbes, pretgt);
					}
				}
			} // -- while (meIt.hasNext()) 
		} // -- while (clsIt.hasNext())
	} // -- void instRetEvents
	
	/** instrumentation for communication event monitoring */
	protected static boolean is_getSocketInputStream (Stmt u) {
		if (!opts.use_socket()) return false;
		
		if (!u.containsInvokeExpr()) {
			return false;
		}
		InvokeExpr inv = u.getInvokeExpr();
		// simple and naive decision based on textual matching
		return inv.getMethod().getSignature().equals("<java.net.Socket: java.io.InputStream getInputStream()>");
	}
	protected static boolean is_getSocketOutputStream (Stmt u) {
		if (!opts.use_socket()) return false;
		
		if (!u.containsInvokeExpr()) {
			return false;
		}
		InvokeExpr inv = u.getInvokeExpr();
		// simple and naive decision based on textual matching
		return inv.getMethod().getSignature().equals("<java.net.Socket: java.io.OutputStream getOutputStream()>");
	}
	
	protected static boolean is_readObjInputStream (Stmt u) {
		if (!opts.use_objstream()) return false;
		
		if (!u.containsInvokeExpr()) {
			return false;
		}
		InvokeExpr inv = u.getInvokeExpr();
		// simple and naive decision based on textual matching
		return inv.getMethod().getSignature().equals("<java.io.ObjectInputStream: java.lang.Object readObject()>");
	}
	protected static boolean is_writeObjOutputStream (Stmt u) {
		if (!opts.use_objstream()) return false;
		
		if (!u.containsInvokeExpr()) {
			return false;
		}
		InvokeExpr inv = u.getInvokeExpr();
		// simple and naive decision based on textual matching
		return inv.getMethod().getSignature().equals("<java.io.ObjectOutputStream: void writeObject(java.lang.Object)>");
	}
	
	protected static boolean is_nioRead(Stmt u) {
		if (!opts.use_nio()) return false;
		
		if (!u.containsInvokeExpr()) {
			return false;
		}
		InvokeExpr inv = u.getInvokeExpr();
		if (!inv.getMethod().getDeclaringClass().getName().equals("java.nio.channels.SocketChannel")) {
			return false;
		}
		if (!inv.getMethod().getName().equals("read")) {
			return false;
		}
		return true;
	}
	protected static boolean is_nioWrite(Stmt u) {
		if (!opts.use_nio()) return false;
		
		if (!u.containsInvokeExpr()) {
			return false;
		}
		InvokeExpr inv = u.getInvokeExpr();
		if (!inv.getMethod().getDeclaringClass().getName().equals("java.nio.channels.SocketChannel")) {
			return false;
		}
		if (!inv.getMethod().getName().equals("write")) {
			return false;
		}
		return true;
	}
	
	protected void probeNIORead(SootMethod m, Stmt s) {
		PatchingChain<Unit> pchn = m.getActiveBody().getUnits();
		
		assert s.containsInvokeExpr();
		InvokeExpr invexpr = s.getInvokeExpr();
		assert invexpr instanceof InstanceInvokeExpr;
		InstanceInvokeExpr insinv = (InstanceInvokeExpr) invexpr;
		Value invobj = insinv.getBase();
		assert invobj.getType().equals(Scene.v().getRefType("java.nio.channels.SocketChannel"));
		
		List<Object> commProbes = new ArrayList<Object>();
		Value vsc = utils.makeBoxedValue(m, invobj, commProbes);
		InvokeExpr sinvexpr = Jimple.v().newStaticInvokeExpr(mNioRead.makeRef(), vsc);
		Stmt invs = Jimple.v().newInvokeStmt(sinvexpr);
		commProbes.add(invs);
		
		// -- DEBUG
		if (opts.debugOut()) {
			System.out.println("adding SocketChannel.read instrumented before the statement " + 
					s + " in method " + m.getSignature());
		}
		//InstrumManager.v().insertAfter(pchn, commProbes, s);
		InstrumManager.v().insertBeforeRedirect(pchn, commProbes, s);
	}
	
	protected void probeNIOWrite(SootMethod m, Stmt s) {
		PatchingChain<Unit> pchn = m.getActiveBody().getUnits();
		
		assert s.containsInvokeExpr();
		InvokeExpr invexpr = s.getInvokeExpr();
		assert invexpr instanceof InstanceInvokeExpr;
		InstanceInvokeExpr insinv = (InstanceInvokeExpr) invexpr;
		Value invobj = insinv.getBase();
		assert invobj.getType().equals(Scene.v().getRefType("java.nio.channels.SocketChannel"));
		
		List<Object> commProbes = new ArrayList<Object>();
		Value vsc = utils.makeBoxedValue(m, invobj, commProbes);
		InvokeExpr sinvexpr = Jimple.v().newStaticInvokeExpr(mNioWrite.makeRef(), vsc);
		Stmt invs = Jimple.v().newInvokeStmt(sinvexpr);
		commProbes.add(invs);
		
		// -- DEBUG
		if (opts.debugOut()) {
			System.out.println("adding SocketChannel.write instrumented before the statement " + 
					s + " in method " + m.getSignature());
		}
		//InstrumManager.v().insertAfter(pchn, commProbes, s);
		InstrumManager.v().insertBeforeRedirect(pchn, commProbes, s);
	}
	
	/** dealing with asynchronous I/Os, using which logic clocks piggyback on the message sent may not be correctly received in order */
	protected void probeNIOReadAsync(SootMethod m, Stmt s) {
		PatchingChain<Unit> pchn = m.getActiveBody().getUnits();
		
		assert s.containsInvokeExpr();
		InvokeExpr invexpr = s.getInvokeExpr();
		assert invexpr instanceof InstanceInvokeExpr;
		InstanceInvokeExpr insinv = (InstanceInvokeExpr) invexpr;
		Value invobj = insinv.getBase();
		assert invobj.getType().equals(Scene.v().getRefType("java.nio.channels.SocketChannel"));
		
		List<Object> commProbes = new ArrayList<Object>();
		Value vsc = utils.makeBoxedValue(m, invobj, commProbes);
		
		SootMethod tgtmm = null;
		for (SootMethod mm : clsMonitor.getMethods()) {
			if (!mm.getName().startsWith("dist_async_nioread")) { continue; }
			if (mm.getParameterCount()-1 != insinv.getMethod().getParameterCount()) { continue; }
			String params = "Ljava/nio/channels/SocketChannel;";
			params += insinv.getMethod().getBytecodeParms();
			if (!mm.getBytecodeParms().equals(params)) {
				//System.out.println(mm.getBytecodeParms());
				//System.out.println();
				continue;
			}
			
			tgtmm = mm;
			break;
		}
		if (tgtmm == null) {
			System.err.println("could not find a matching monitor for the NIO read call " + s);
			return;
		}
		
		Value lv = null;
		if (s instanceof AssignStmt) {
			lv = ((AssignStmt)s).getLeftOp();
		}
		
		List<Value> args = new ArrayList<Value>();
		args.add(vsc);
		args.addAll(insinv.getArgs());
		/*
		for (int k = 0; k < insinv.getArgCount(); k++) {
			args.add(insinv.getArg(k));
		}
		*/
		InvokeExpr sinvexpr = Jimple.v().newStaticInvokeExpr(tgtmm.makeRef(), args);
		if (lv == null) {
			Stmt invs = Jimple.v().newInvokeStmt(sinvexpr);
			commProbes.add(invs);
		}
		else {
			Stmt ass = Jimple.v().newAssignStmt(lv, sinvexpr);
			commProbes.add(ass);
		}
		
		// -- DEBUG
		if (opts.debugOut()) {
			System.out.println("adding SocketChannel.read instrumented before the statement " + 
					s + " in method " + m.getSignature());
		}
		InstrumManager.v().insertAfter(pchn, commProbes, s);
		pchn.remove(s); // replace the original NIO read call
	}
	protected void probeNIOWriteAsync(SootMethod m, Stmt s) {
		PatchingChain<Unit> pchn = m.getActiveBody().getUnits();
		
		assert s.containsInvokeExpr();
		InvokeExpr invexpr = s.getInvokeExpr();
		assert invexpr instanceof InstanceInvokeExpr;
		InstanceInvokeExpr insinv = (InstanceInvokeExpr) invexpr;
		Value invobj = insinv.getBase();
		assert invobj.getType().equals(Scene.v().getRefType("java.nio.channels.SocketChannel"));
		
		List<Object> commProbes = new ArrayList<Object>();
		Value vsc = utils.makeBoxedValue(m, invobj, commProbes);
		
		SootMethod tgtmm = null;
		for (SootMethod mm : clsMonitor.getMethods()) {
			if (!mm.getName().startsWith("dist_async_niowrite")) { continue; }
			if (mm.getParameterCount()-1 != insinv.getMethod().getParameterCount()) { continue; }
			String params = "Ljava/nio/channels/SocketChannel;";
			params += insinv.getMethod().getBytecodeParms();
			if (!mm.getBytecodeParms().equals(params)) {
				continue; 
			}
			
			tgtmm = mm;
			break;
		}
		if (tgtmm == null) {
			System.err.println("could not find a matching monitor for the NIO write call " + s);
			return;
		}
		
		Value lv = null;
		if (s instanceof AssignStmt) {
			lv = ((AssignStmt)s).getLeftOp();
		}
		
		List<Value> args = new ArrayList<Value>();
		args.add(vsc);
		args.addAll(insinv.getArgs());
		/*
		for (int k = 0; k < insinv.getArgCount(); k++) {
			args.add(insinv.getArg(k));
		}
		*/
		InvokeExpr sinvexpr = Jimple.v().newStaticInvokeExpr(tgtmm.makeRef(), args);
		if (lv == null) {
			Stmt invs = Jimple.v().newInvokeStmt(sinvexpr);
			commProbes.add(invs);
		}
		else {
			Stmt ass = Jimple.v().newAssignStmt(lv, sinvexpr);
			commProbes.add(ass);
		}
		
		// -- DEBUG
		if (opts.debugOut()) {
			System.out.println("adding SocketChannel.write instrumented before the statement " + 
					s + " in method " + m.getSignature());
		}
		InstrumManager.v().insertAfter(pchn, commProbes, s);
		pchn.remove(s); // replace the original NIO write call
	}

	public void instCommEvents() {
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
				
				PatchingChain<Unit> pchn = body.getUnits();
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
					System.out.println("\nNow instrumenting method for network interprocess communication Events: " + meId + "...");
				}
				
				//List<StringConstant> commArgs = new ArrayList<StringConstant>();
				Local lInstream = null, lOutstream = null;
				// for (Unit u : pchn) {
					// Stmt s = (Stmt)u;
				for (CFGNode cn : cfg.getNodes()) {
					if (cn.isSpecial()) {continue;}
					Stmt s = cn.getStmt();
					// socket getinputstream
					if (is_getSocketInputStream(s)) {
						if (!(s instanceof AssignStmt)) {
							System.err.println("WARNING: statement " + s + " is an assignment statement as expected.");
							continue;
						}
						if (lInstream == null) {
							lInstream = utils.createUniqueLocal(body, "distInsStream", cDistSockInStream.getType());
						}
						AssignStmt as = (AssignStmt)s;
						Value instream = as.getLeftOp();
						assert instream.getType().equals(Scene.v().getRefType("java.io.InputStream"));
						
						List<Object> commProbes = new ArrayList<Object>();
						Stmt nst = Jimple.v().newAssignStmt( lInstream, Jimple.v().newNewExpr( cDistSockInStream.getType() ) );
						SootMethod ctorInstream = cDistSockInStream.getMethod("void <init>(java.io.InputStream)");
						commProbes.add(nst);
						InvokeExpr sinvexpr = Jimple.v().newSpecialInvokeExpr(lInstream, ctorInstream.makeRef(),
								utils.makeBoxedValue(sMethod, instream, commProbes));
								//instream);
						Stmt invs = Jimple.v().newInvokeStmt(sinvexpr);
						commProbes.add(invs);
						Stmt nas = Jimple.v().newAssignStmt(instream, lInstream);
						commProbes.add(nas);
						
						// -- DEBUG
						if (opts.debugOut()) {
							System.out.println("replacing socket.getInputStream instrumented before the statement " + 
									s + " in method " + meId);
						}
						InstrumManager.v().insertAfter(pchn, commProbes, s);
					}
					// socket getonputstream
					else if (is_getSocketOutputStream(s)) {
						if (!(s instanceof AssignStmt)) {
							System.err.println("WARNING: statement " + s + " is an assignment statement as expected.");
							continue;
						}
						if (lOutstream == null) {
							lOutstream = utils.createUniqueLocal(body, "distOutStream", cDistSockOutStream.getType());
						}
						AssignStmt as = (AssignStmt)s;
						Value outstream = as.getLeftOp();
						assert outstream.getType().equals(Scene.v().getRefType("java.io.OutputStream"));
						
						List<Object> commProbes = new ArrayList<Object>();
						Stmt nst = Jimple.v().newAssignStmt( lOutstream, Jimple.v().newNewExpr( cDistSockOutStream.getType() ) );
						SootMethod ctorOutstream = cDistSockOutStream.getMethod("void <init>(java.io.OutputStream)");
						commProbes.add(nst);
						InvokeExpr sinvexpr = Jimple.v().newSpecialInvokeExpr(lOutstream, ctorOutstream.makeRef(),
								utils.makeBoxedValue(sMethod, outstream, commProbes));
						Stmt invs = Jimple.v().newInvokeStmt(sinvexpr);
						commProbes.add(invs);
						Stmt nas = Jimple.v().newAssignStmt(outstream, lOutstream);
						commProbes.add(nas);
						
						// -- DEBUG
						if (opts.debugOut()) {
							System.out.println("replacing socket.getOutputStream instrumented before the statement " + 
									s + " in method " + meId);
						}
						InstrumManager.v().insertAfter(pchn, commProbes, s);
					}
					else if (is_readObjInputStream(s)) {
						assert s.containsInvokeExpr();
						InvokeExpr invexpr = s.getInvokeExpr();
						assert invexpr instanceof VirtualInvokeExpr;
						VirtualInvokeExpr virinv = (VirtualInvokeExpr) invexpr;
						Value invobj = virinv.getBase();
						assert invobj.getType().equals(Scene.v().getRefType("java.io.ObjectInputStream"));
						
						List<Object> commProbes = new ArrayList<Object>();
						Value vsc = utils.makeBoxedValue(sMethod, invobj, commProbes);
						InvokeExpr sinvexpr = Jimple.v().newStaticInvokeExpr(mObjStreamRead.makeRef(), vsc);
						Stmt invs = Jimple.v().newInvokeStmt(sinvexpr);
						commProbes.add(invs);
						
						// -- DEBUG
						if (opts.debugOut()) {
							System.out.println("adding ObjectInputStream.read instrumented before the statement " + 
									s + " in method " + meId);
						}
						InstrumManager.v().insertAfter(pchn, commProbes, s);
						//InstrumManager.v().insertBeforeRedirect(pchn, commProbes, s);
					}
					else if (is_writeObjOutputStream(s)) {
						assert s.containsInvokeExpr();
						InvokeExpr invexpr = s.getInvokeExpr();
						assert invexpr instanceof VirtualInvokeExpr;
						VirtualInvokeExpr virinv = (VirtualInvokeExpr) invexpr;
						Value invobj = virinv.getBase();
						assert invobj.getType().equals(Scene.v().getRefType("java.io.ObjectOutputStream"));
						
						List<Object> commProbes = new ArrayList<Object>();
						Value vsc = utils.makeBoxedValue(sMethod, invobj, commProbes);
						InvokeExpr sinvexpr = Jimple.v().newStaticInvokeExpr(mObjStreamWrite.makeRef(), vsc);
						Stmt invs = Jimple.v().newInvokeStmt(sinvexpr);
						commProbes.add(invs);
						
						// -- DEBUG
						if (opts.debugOut()) {
							System.out.println("adding ObjectOutputStream.write instrumented before the statement " + 
									s + " in method " + meId);
						}
						InstrumManager.v().insertAfter(pchn, commProbes, s);
						//InstrumManager.v().insertBeforeRedirect(pchn, commProbes, s);
					}
					else if (is_nioRead(s)) {
						if (opts.probe_sync_nio()) {
							probeNIORead(sMethod, s);
						}
						else {
							probeNIOReadAsync(sMethod, s);
						}
					}
					else if (is_nioWrite(s)) {
						if (opts.probe_sync_nio()) {
							probeNIOWrite(sMethod, s);
						}
						else {
							probeNIOWriteAsync(sMethod, s);
						}
					}
					else {
						//TODO: future extension dealing with other network I/O cases
					}
				}
			} // -- while (meIt.hasNext()) 
		} // -- while (clsIt.hasNext())
		
		if (opts.dumpJimple()) {
			fJimpleInsted = new File(Util.getCreateBaseOutPath() + "JimpleInstrumented.out");
			if (fJimpleInsted.exists()) {
				// remove the incomplete file possibly dumped by parent class already
				fJimpleInsted.delete();
			}
			utils.writeJimple(fJimpleInsted);
		}
		
	} // -- void instCommEvents
} 