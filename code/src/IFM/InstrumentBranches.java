package IFM;

//import EAS.EAInst;
import MciaUtil.RDFCDBranchEx;
import MciaUtil.utils;
//import disttaint.dtOptions;
import disttaint.dtUtil;
import dua.Forensics;
import dua.global.ProgramFlowGraph;
import dua.method.CFGDefUses.Branch;
import dua.util.Util;
import fault.StmtMapper;
import profile.BranchInstrumenter;
import soot.Scene;
import soot.SootClass;
import soot.jimple.Stmt;

import java.io.*;
import java.util.*;


public class InstrumentBranches extends EAInst {
	protected static dtOptions opts = new dtOptions();
	//distEA variables
//	protected SootMethod mReturnFrom;
//	
//	protected SootMethod mNioRead;
//	protected SootMethod mNioWrite;
//	protected SootClass cDistSockInStream;
//	protected SootClass cDistSockOutStream;
//	
//	protected SootMethod mObjStreamRead;
//	protected SootMethod mObjStreamWrite;
//	public static int methodNum=0;
	//private List<SootMethod> remoteMethods = new ArrayList<SootMethod>();
	public static ArrayList remoteMethods = new ArrayList();
	
	// branch Instrument variables	
	/** the RDF/CD branch analyzer */
	private final static RDFCDBranchEx rdfCDBranchAnalyzer = RDFCDBranchEx.inst();
	
	/** if remove repeated branches in terms of same targets */
	public static boolean removeRepeatedBrs = true;
	protected SootClass clsMonitor;
	public static void main(String args[]){
		System.out.println("Running OTBranchInst 1");
		args = preProcessArgs(opts, args);

		InstrumentBranches d2r = new InstrumentBranches();
		// examine catch blocks
		dua.Options.ignoreCatchBlocks = false;
		dua.Options.ignoreCatchBlocks = Variant.isExceptionalFlow();
		Scene.v().addBasicClass("profile.BranchReporter");
		Scene.v().addBasicClass("disttaint.dt2BranchMonitor");
		Forensics.registerExtension(d2r);
		Forensics.main(args);
	}
	
	@Override protected void init() {
		clsMonitor = Scene.v().getSootClass("profile.BranchReporter");
		clsMonitor.setApplicationClass();
		clsMonitor = Scene.v().getSootClass("disttaint.dt2BranchMonitor");
		clsMonitor.setApplicationClass();
		
		mInitialize = clsMonitor.getMethodByName("initialize");
		mEnter = clsMonitor.getMethodByName("enter");
		mReturnInto = clsMonitor.getMethodByName("returnInto");
		mTerminate = clsMonitor.getMethodByName("terminate");
	}
	
	@Override public void run() {
        System.out.println("Running Diver branch coverage instrumenter of one time branch");
		
		// 1. dump branch->CD stmts 
		dumpBranchCDStmts();
		
		// 2. instrument branch coverage monitors
		if (opts.dumpJimple()) {
			fJimpleOrig = new File(Util.getCreateBaseOutPath() + "JimpleOrig.out");
			utils.writeJimple(fJimpleOrig);
		}
		System.exit(0);
//		rdfCDBranchAnalyzer.removeAssistantNodes();		
//		instrumentAllBranches();
//		instrumentTerminate();	   
//		if (opts.dumpJimple()) {
//			fJimpleInsted = new File(Util.getCreateBaseOutPath() + "JimpleInstrumented.out");
//			utils.writeJimple(fJimpleInsted);
//		}
	}
	protected void dumpBranchCDStmts() {
		// instantiate all intraprocedural CDGs, each per method
		/** don't use the standard algorithm for now: instead, follow the same algorithm of CD 
		 * computation as DuaF - RDF and "other (non-RDF)" CD branches, so the following is commented out*/
		//computeAllIntraCDs();
		
		// determine, for each branch, all stmts that are control dependent on it
		Map<Stmt, Integer> stmtIds = StmtMapper.getWriteGlobalStmtIds();
		ArrayList taintedMethods = new ArrayList();
		taintedMethods= disttaint.dtUtil.getArrayList(System.getProperty("user.dir") + File.separator + "methodList.out");
		System.out.println("taintedMethods.size()="+taintedMethods.size());
		List<Branch> allBranches = 	rdfCDBranchAnalyzer.getAllBranchesInMethods(taintedMethods);
		if (removeRepeatedBrs) {
			allBranches = rdfCDBranchAnalyzer.getAllUniqueBranchesInMethods(taintedMethods);
		}
		

		Map<Branch, Set<Stmt>> br2cdstmts = rdfCDBranchAnalyzer.buildBranchToCDStmtsMap(allBranches, stmtIds);
		 System.out.println("Saving entitystmt.out.");
		String suffix = "branch";
		File fBranchStmt = new File(Util.getCreateBaseOutPath() + "entitystmt.out." + suffix);
		Set<Stmt> branchStmts = new HashSet<Stmt>();
		try {
			// write always a new file, deleting previous contents (if any)
			BufferedWriter writer = new BufferedWriter(new FileWriter(fBranchStmt));
			
			// branches are assumed to be ordered by id
			for (Branch br : allBranches) {
				Set<Stmt> relStmts = br2cdstmts.get(br);
				for (Stmt s : relStmts) {
					writer.write(stmtIds.get(s) + " ");
					
				}
				branchStmts.addAll(relStmts); 
				writer.write("\n");
			}
			
			writer.flush();
			writer.close();
		}
		catch (FileNotFoundException e) { System.err.println("Couldn't write ENTITYSTMT '" + suffix + "' file: " + e); }
		catch (SecurityException e) { System.err.println("Couldn't write ENTITYSTMT '" + suffix + "' file: " + e); }
		catch (IOException e) { System.err.println("Couldn't write ENTITYSTMT '" + suffix + "' file: " + e); }
		
		File fBranchStmtStr = new File(Util.getCreateBaseOutPath() + "entitystmtStr.out." + suffix);
		File fBranchStmtId = new File(Util.getCreateBaseOutPath() + "entitystmtId.out." + suffix);
		System.out.println("Saving entitystmtStr.out.");
		try {			
			BufferedWriter writerStr = new BufferedWriter(new FileWriter(fBranchStmtStr));
			BufferedWriter writerId = new BufferedWriter(new FileWriter(fBranchStmtId));
			for (Stmt s : branchStmts) {
				writerStr.write(s + "\n");
				writerId.write(stmtIds.get(s) + "\n");
			}			
			writerStr.flush();
			writerStr.close();		
			writerId.flush();
			writerId.close();
		}
		catch (FileNotFoundException e) { System.err.println("Couldn't write ENTITYSTMTSTR or ID '" + suffix + "' file: " + e); }
		catch (SecurityException e) { System.err.println("Couldn't write ENTITYSTMTSTR or ID '" + suffix + "' file: " + e); }
		catch (IOException e) { System.err.println("Couldn't write ENTITYSTMTSTR or ID '" + suffix + "' file: " + e); }
	}
	/** instrument branch coverage monitors for all branches */
	protected int instrumentAllBranches() {
		List<Branch> allBranches = rdfCDBranchAnalyzer.getAllBranches();
		if (removeRepeatedBrs) {
			allBranches = rdfCDBranchAnalyzer.getAllUniqueBranches();
		}
		// instrument using DuaF facilities
		BranchInstrumenter brInstrumenter = new BranchInstrumenter(true);
		ArrayList remoteMethods= dtUtil.getArrayList(System.getProperty("user.dir") + File.separator + "methodList.out");
		if (remoteMethods.size()<1)  {
			brInstrumenter.instrumentDirect(allBranches, ProgramFlowGraph.inst().getEntryMethods());
		}
		else  {
			brInstrumenter.instrumentDirect(allBranches, ProgramFlowGraph.inst().getEntryMethods(),remoteMethods);
		}	
		return 0;
	} 

} 