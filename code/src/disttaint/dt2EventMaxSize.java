package disttaint;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import soot.jimple.Stmt;
import fault.StmtMapper;


public class dt2EventMaxSize{
	static Set<String> changeSet = new LinkedHashSet<String>();
	//static Map<String, Set<String> > impactSets = new LinkedHashMap<String, Set<String>>();
	//static HashMap<String, Integer> impactSet_dist = new HashMap<String, Integer> ( );
	static DynTransferGraph dvtg = new DynTransferGraph();
	static DynTransferGraph dvtgExercised = new DynTransferGraph();
	static DynTransferGraph dvtgPruned= new DynTransferGraph();
	static boolean debugOut = false;
	static String method1 = "";
	static String stmt1 = "";
	static String method2 = "";
	static String stmt2 = "";
//	static DVTNode startNode=null;
//	static DVTNode endNode=null;
//	static String sourceMessage="";
//	static String sinkMessage="";
	static ArrayList stmtStrs=new ArrayList();
	static ArrayList stmtAll=new ArrayList();
	static List<Integer> coveredStmts = new ArrayList<Integer>();
	static List<String> coveredStmtsStr = new ArrayList<String>();
	// or compute impact sets for multiple queries at the same time when traversing the execution trace for only once
	static boolean matchingDynVTGForAllQueries = false;
	// include the pruning approach just for a comparison
	static boolean pruningDynVTGForAllQueries = false;
	
	/** if applying runtime statement coverage information to prune statements not executed, examined per test case */
	public static boolean applyStatementCoverage = false;
	/** prune non-covered/non-aliased nodes and edges prior to or after basic querying process: 
	 * both are equivalent in terms of eventual impact set but can be disparate in performance
	 */
	public static boolean postPrune = true;
	public static boolean prePrune = false;
	
	/** if applying runtime object alias checking to prune heap value edges on which the source 
	 * and target nodes are not dynamically aliased 
	 */
	public static boolean applyDynAliasChecking = false;
//	/** if pruning based on the dynamic alias information at the method instance level, or just the method level */
	public static boolean instancePrune = true; 
//	static Map<String, Set<String>> localImpactSets = new LinkedHashMap<String, Set<String>>();
//	// distEA variables
//	static boolean separateReport = true;
	static boolean reportCommon = false;
	static boolean strictComponent = false; // strictly two different components won't have common traces --- they have to communicate by message passing
	static boolean applyMethodCoverage = false; // choose between the purely EA-based and precision-improved versions
	//static boolean runDiver = false;
	static Set<String> impactSet = new LinkedHashSet<String>();
	
	static HashSet sourceNodes = new HashSet();
	static HashSet sinkNodes = new HashSet();
	static HashSet sourceMethods = new HashSet();
	static HashSet sinkMethods = new HashSet();
	static HashSet<DVTNode> sendMsgNodes = new HashSet();
	static HashSet<DVTNode> receiveMsgNodes = new HashSet();
	static String sourceMessage="";
	static String sinkMessage="";
	
	static boolean isLocal = false;
	static boolean isNoDependency = false;
	public static ArrayList remoteMethods = new ArrayList();
	
	public static void main(String args[]){
	
		if (args.length < 1) {
			System.err.println("Too few arguments: \n\t " +
					"dtAnalysisAll pairFile\n\n");
			return;
		}
		//System.out.println("0th\n");
		String queryFile = args[0]; // tell the changed methods, separated by comma if there are more than one
		String traceDir = args[1]; // tell the directory where execution traces can be accessed
		String binDir = args[2]; // tell the directory where the static value transfer graph binary can be found

//		String query="<C: void main(java.lang.String[])> - virtualinvoke r2.<B: void printString(int,java.lang.String,java.lang.String)>(-1, \"positive\", \"negative\");<B: void printString(int,java.lang.String,java.lang.String)> - r1 := @parameter1: java.lang.String";
//		String binDir="C:\\TEST\\DTInstrumented";
		
		if (args.length > 3) {
			applyMethodCoverage  = args[3].startsWith("-method");
		}
		if (args.length > 4) {
			applyStatementCoverage = args[4].equalsIgnoreCase("-stmtcov");
			applyDynAliasChecking = args[4].equalsIgnoreCase("-dynalias");
		}
		
		if (args.length > 5) {
			prePrune = args[5].equalsIgnoreCase("-preprune");
			postPrune = args[5].equalsIgnoreCase("-postprune"); // secondary option working with only "-stmtcov"
			instancePrune = args[5].equalsIgnoreCase("-instanceprune"); // secondary option working with only "-dynalias"
		}
		
		// apply both statement coverage and dynamic alias data, using the best secondary options (postprune and instanceprune respectively) for each
		if (args.length > 4 && args[4].equalsIgnoreCase("-stmtcovdynalias")) {
			applyStatementCoverage = applyDynAliasChecking = true;
			postPrune /*= instancePrune*/ = true;
		}
		
		if (args.length > 6) {
			debugOut = args[6].equalsIgnoreCase("-debug");
		}
		
		if (args.length > 7) {
			matchingDynVTGForAllQueries = args[7].equalsIgnoreCase("-matchingForAll");
			pruningDynVTGForAllQueries = args[7].equalsIgnoreCase("-pruningForAll");
		}

//		if (args.length > 8) {
//			runDiver  = args[8].equalsIgnoreCase("-runDiver");
//		}	
		if (debugOut)
		{
			System.out.println(" queryFile="+queryFile+" traceDir="+traceDir+" binDir="+binDir);
		}			


//		if (init(binDir) != 0) {
//			// something wrong during the initialization of the dynamic graph
//			return;
//		}
//		DynTransferGraph orig_dvtg = new DynTransferGraph();
//		orig_dvtg.CopyFrom(dvtg);
//		List<Integer> coveredStmts = new ArrayList<Integer>();
//		if (readStmtCoverage(traceDir, "1", coveredStmts) <= 0) {
//			// nothing to do further along
//			System.err.println("Error: empty coverage with stmtCoverage1.out.");
//		}	
        File file1 = new File(queryFile);
        if (!file1.exists()) {
        	file1 = new File(binDir + File.separator + queryFile);
        }
        try {
			int EAMaxSizes=dtDiver.getEAMaxSizes(traceDir, "Local", ".em");
			System.err.println("EAMaxSizes: "+EAMaxSizes);
        } catch (Exception e) {  
            e.printStackTrace(); 
        }  
	}


}