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


public class dt2AnalysisAll{
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


		if (init(binDir) != 0) {
			// something wrong during the initialization of the dynamic graph
			return;
		}
//		DynTransferGraph orig_dvtg = new DynTransferGraph();
//		orig_dvtg.CopyFrom(dvtg);
		List<Integer> coveredStmts = new ArrayList<Integer>();
		if (readStmtCoverage(traceDir, "1", coveredStmts) <= 0) {
			// nothing to do further along
			System.err.println("Error: empty coverage with stmtCoverage1.out.");
		}	
        File file1 = new File(queryFile);
        if (!file1.exists()) {
        	file1 = new File(binDir + File.separator + queryFile);
        }
        try {
        	long timeBeforeEAs = System.currentTimeMillis();
			HashMap EAs=dtDiver.getEAs(traceDir, "Local", ".em");
			//System.out.println("EAs.size()"+EAs.size()+" EAs="+EAs);
			long timeAfterEAs = System.currentTimeMillis();
			System.out.println("[The dtDiver.getEAs time: " + (timeAfterEAs - timeBeforeEAs) + "ms\n");
	        FileReader reader = new FileReader(file1);  
	        BufferedReader br = new BufferedReader(reader); 
	        String query="";
	        String str = null;  
	        while ((str = br.readLine()) != null) { 
	        	if (str.length()<3)
	        		continue;
	        	long timePairStart = System.currentTimeMillis();
	        	//dvtg=orig_dvtg;
	        	query=str.trim();
				method1=dtStmtUtil.getFirstMethod(query);
				stmt1=dtStmtUtil.getFirstStmt(query);
				method2=dtStmtUtil.getSecondMethod(query);
				stmt2=dtStmtUtil.getSecondStmt(query);
				if (debugOut)
				{
					System.out.println(" method1="+method1);
					System.out.println(" stmt1="+stmt1);
					System.out.println(" method2="+method2);
					System.out.println(" stmt2="+stmt2);
					System.out.println("remoteMethods.size()="+remoteMethods.size());     
				}
				if (applyMethodCoverage)  {
					if (!remoteMethods.contains(method1)) 
					{
						System.out.println(" The method="+method1+" is not the source of the method-level flow path.");
						continue;
					}
					if (!remoteMethods.contains(method2))
					{
						System.out.println(" The method="+method2+" is not the sink of the method-level flow path.");
						continue;
					}
				}
				if (debugOut)
				{
					System.out.println("Source: "+method1+" - "+stmt1);
					System.out.println("Sink: "+method2+" - "+stmt2);
				}
				if (applyStatementCoverage)  {
					if (!coveredStmtsStr.contains(method1+" - "+stmt1))  {
						System.out.println("The source statement "+method1+" - "+stmt1+" is not in the executed statement list!");
						continue;
					}	
					if (!coveredStmtsStr.contains(method2+" - "+stmt2))   {
						System.out.println("The sink statement "+method2+" - "+stmt2+" is not in the executed statement list!");
						continue;
					}	
				}
				// dynamic dependency graph from Local Diver trace files
				//HashMap EAs=dtDiver.getEAs(traceDir, "Local", ".em");
				//System.out.println("EAs.size()"+EAs.size()+" EAs="+EAs);
				//System.out.println("applyStatementCoverage="+applyStatementCoverage+" postPrune="+postPrune);
				//System.out.println(" Before handling trace files,  dvtg edge size is "+dvtg.edgeSet().size()+" and dvtg node size is "+dvtg.nodeSet().size());
				parseSequence(method1, EAs);
				//System.out.println(" After handling trace files,  dvtg edge size is "+dvtgExercised.edgeSet().size()+" and dvtg node size is "+dvtgExercised.nodeSet().size());
		
				if (applyStatementCoverage && postPrune ) {    // && postPrune
					if (coveredStmts.size()>0)
					{
						// prune the exercised dynamic VTG
						//final DynTransferGraph dvtgPruned = new DynTransferGraph();
						
						int nPrunedEdges = dvtgExercised.postPruneByCoverage(dvtgPruned, coveredStmts);
						if (debugOut) {
							System.out.println("\n Statement coverage pruned " + nPrunedEdges + " edges in the dynamic graph after it being exercised.");
						}
						
//						if (applyDynAliasChecking && !instancePrune) {
//							dvtgExercised = dvtgPruned;
//						}
						
						//dvtg=dvtgPruned;
						System.out.println(" After statement coverage pruning, dvtg edge size is "+dvtgPruned.edgeSet().size()+" and dvtg node size is "+dvtgPruned.nodeSet().size());
					}
					else
						dvtgPruned=dvtgExercised;
				}
				else
					dvtgPruned=dvtgExercised;
				computeNodes(method1,stmt1,method2,stmt2,stmtStrs);
				if (isLocal)
				{
					remoteResults(method1+"; "+method2,"");					
				}
				else
				{	
					String remoteStr=getRemoteString(method1+"; "+method2);
					//System.out.println("remoteNum="+remoteNum);
					if (remoteStr.length()>0)
					{	
						//System.out.println("computeNodes(method1,stmt1,method2,stmt2,stmtStrs);");
						
						if (!isNoDependency)  
						{	
							//if (remoteNum>0)
							//System.out.println("remoteResults(method1");
							remoteResults(method1+"; "+method2,remoteStr);			
							
						}
					}
				}
				long timePairEnd = System.currentTimeMillis();
				System.out.println("[The tainted path checking time of "+query+" ]: " + (timePairEnd - timePairStart) + "ms\n");
	        }
	        br.close();
	        reader.close();
        } catch (Exception e) {  
            e.printStackTrace(); 
        }  
	}
	public static String getRemoteString(String methodPair)
	{
		long time0 = System.currentTimeMillis();
		ArrayList myRemoteMethods=dtUtil.getMethodItems(methodPair, System.getProperty("user.dir") + File.separator + "methodsInPair.out");
		long time1 = System.currentTimeMillis();
		System.out.println("getRemoteString	dtUtil.getMethodItems " + (time1 - time0) + "ms\n");
		String midMethod="";
		String midStr="";
		int remoteNum=0;
		String remoteStr="";
		//boolean startNow=false;
		boolean endNow=false;
		//System.out.println("method1="+method1+" method2="+method2+" remoteMethods="+remoteMethods+" myRemoteMethods.size()="+myRemoteMethods.size());
		for (int i=1; i<myRemoteMethods.size(); i++) {
			midMethod=myRemoteMethods.get(i).toString().trim();
			//System.out.println("midMethod="+midMethod+" endNow="+endNow);
			if (midMethod.length()<1 || midMethod.equals(method1) )
				continue;

			if (midMethod.equals(method2))
			{	
				endNow=true;
				continue;
			}	
			if (!endNow)   {
				midStr=getMethodStmtStr(midMethod, stmtStrs);
				//System.out.println("midStr="+midStr);
				if (midStr.length()>1)
				{
					remoteStr+=midStr;
					//System.out.print(midStr);
					//System.out.println("midStr="+midStr+" dtUtil.getStmtNum(midStr)="+dtUtil.getStmtNum(midStr,"\n"));
					remoteNum+=dtUtil.getStmtNum(midStr,"\n");
					//System.out.print("--> \n");
					remoteStr+="--> \n";
				}
			}
		}
		long time2 = System.currentTimeMillis();
		System.out.println("getRemoteString(String methodPair) " + (time2 - time0) + "ms\n");
		return remoteStr;
	}
	public static void remoteResults(String methodPair, String remoteStr) {
		System.out.println("\n -----------The result from \n source "+ method1+" - "+stmt1+ " \n   to \n sink "+ method2+" - "+stmt2);
		System.out.println("\n Source local path:");
			
		System.out.println(sourceMessage);
		int remoteNum=dtUtil.getStmtNum(remoteStr,"-->");
//		if (remoteMethods.size()<2 || remoteNum<1)
//		{
//			System.out.println(" There is no remote flow path!");
//			if (sinkMessage.length()>1)
//			{	
//				System.out.println("Sink local path:");
//				System.out.println(sinkMessage);
//			}
//			return;
//		}
		//if (remoteMethods.size()>0 || remoteNum>=1)
		if (!isLocal)
		{	
			System.out.println("\n Remote path:");
			System.out.print("--> \n");
			System.out.println(remoteStr+"\n");
			if (sinkMessage.length()>1)
			{	
				System.out.println("\n Sink local path:");
				System.out.println(sinkMessage+"\n");
			}		
		}
		int sourceNum=dtUtil.getStmtNum(sourceMessage,"-->");
		//if (remoteMethods.size()>0 || remoteNum>=1)
		if (!isLocal)	
		{	
			int sinkNum=dtUtil.getStmtNum(sinkMessage,"-->");
			System.out.println("[The statement path length]: "+sourceNum+"+"+remoteNum+"+"+sinkNum+"="+(sourceNum+remoteNum+sinkNum));
		}
		else
		{
			System.out.println("[The statement path length]: "+sourceNum+"+0+0="+sourceNum);
		}
		System.out.print("[The statement path type of "+method1+" - "+ stmt1+"; "+method2+" - "+stmt2+" ]: ");
		if (isLocal)  {
			System.out.print("Local");
		}	
		else
			System.out.print("Remote");
		if (isNoDependency)
			System.out.print(", Nodependency");
		System.out.println("");
	}

	public static int init(String binDir) {
		long time0 = System.currentTimeMillis();
		remoteMethods=dtUtil.getArrayList(System.getProperty("user.dir") + File.separator + "methodList.out");	
		long time1 = System.currentTimeMillis();
		System.out.println("init(String binDir)  remoteMethods=dtUtil.getArrayList " + (time1 - time0) + "ms\n");
//		if (debugOut)
//		{
//			System.out.println(" remoteMethods="+remoteMethods);
//		}
		DynTransferGraph.reachingImpactPropagation = false;
    	HashMap stmtStrses=dtStmtUtil.getStmtStrsFromFile(binDir+File.separator+"stmtids.out");
		long time2 = System.currentTimeMillis();
		System.out.println("init(String binDir)  stmtStrses=dtStmtUtil.getStmtStrsFromFile " + (time2 - time1) + "ms\n");
    	stmtStrs=(ArrayList) stmtStrses.get(1); 
    	stmtAll=(ArrayList) stmtStrses.get(2);
    	
		//stmtStrs=dtStmtUtil.getStmtStrsFromFile(binDir+File.separator+"stmtids.out");
    	if (debugOut)  {
    		System.out.println(" binDir="+binDir+" stmtStrs.size()="+stmtStrs.size()+" stmtAll.size()="+stmtAll.size());
    	}	
    	coveredStmts = dtStmtUtil.readStmtCoverageInt(binDir, 1);
		for (int i=0;i<coveredStmts.size();i++)
		{
			coveredStmtsStr.add((String) stmtAll.get(coveredStmts.get(i)));
		}		
		if (debugOut)  
			System.out.println(" coveredStmtsStr.size()="+coveredStmtsStr.size()+" applyStatementCoverage="+applyStatementCoverage+" prePrune="+prePrune);
		
        File file1 = new File(binDir+File.separator+"staticVtg.dat");
        if (file1.exists()) {
        	dvtg.setSVTG(binDir+File.separator+"staticVtg.dat");
        }
        else
        	dvtg.setSVTG("staticVtg.dat");
		int resultInt=-1;
		//System.out.println(" applyMethodCoverage="+applyMethodCoverage);
		if (applyMethodCoverage && remoteMethods != null && remoteMethods.size()>1)
		{	
			resultInt=dvtg.initializeGraph(false, remoteMethods);
		}
		else
			resultInt=dvtg.initializeGraph(false);
		if (debugOut)  {
			System.out.println("init(String binDir) RemoteMethods.size()="+remoteMethods.size());			
		}
		long time3 = System.currentTimeMillis();
		System.out.println("init(String binDir) dvtg.initializeGraph " + (time3 - time0) + "ms\n");
		System.out.println(" Firstly, dvtg edge size is "+dvtg.edgeSet().size()+" and dvtg node size is "+dvtg.nodeSet().size());
		
		if (0 != resultInt) {
			System.out.println("Unable to load the static value transfer graph, aborted now.");
			return -1;
		}		
		if (stmtStrs.size()>0 && coveredStmtsStr.size()>0 && applyStatementCoverage && prePrune)
		{	
			updateGraphWithCoverage(binDir, 1);
			System.out.println(" After statement coverage pruning, dvtg edge size is "+dvtg.edgeSet().size()+" and dvtg node size is "+dvtg.nodeSet().size());
			if (debugOut)  {
				System.out.println(" stmtStrs.size()="+stmtStrs.size()+" coveredStmts.size()="+coveredStmts.size()+" coveredStmtsStr.size()="+coveredStmtsStr.size());
			}	
		}
		long time4 = System.currentTimeMillis();
		System.out.println("init(String binDir): " + (time4 - time0) + "ms\n");
		return 0;
	}

		
	public static int updateGraphWithCoverage(String traceDir, int tId) {
		if (applyStatementCoverage) {
			long time0 = System.currentTimeMillis();
			if (coveredStmts.size()<= 1) {
				// nothing to do further along
				System.err.println("Error: empty coverage with test No. " + tId);
				return -1;
			}
			// prune the initial dynamic VTG
			int nPrunedEdges = dvtg.reInitializeGraph(DynTransferGraph.svtg, coveredStmts);
			if (debugOut) {
				System.out.println("\n Statement coverage pruned " + nPrunedEdges + 
						" edges in the static graph before querying.");
			}
			long time1 = System.currentTimeMillis();
			System.out.println("updateGraphWithCoverage(" +tId+"): "+(time1 - time0) + " milliseconds");
			return nPrunedEdges;
		}
		return 0;
	}

	
    public static void computeNodes(String method1, String stmt1, String method2, String stmt2, ArrayList stmtStrs)
    {
    	long time0 = System.currentTimeMillis();
    	HashSet ds1=getNodes(method1,stmt1, stmtStrs);
//    	if (ds1.size()<1)    	{	
//    		System.out.println("The source statement "+method1+" - "+stmt1+" is not in the executed statement list!");
//    		return;
//    	}	
		long time1 = System.currentTimeMillis();
		System.out.println("computeNodes ds1=getNodes(method1,stmt1, stmtStrs)" + (time1 - time0) + "ms\n");
    	if (debugOut)
    		System.out.println("computeNodes ds1.size()="+ds1.size());
    	HashSet ds2=getNodes(method2,stmt2, stmtStrs);
//    	if (ds2.size()<1)  {
//    		System.out.println("The sink statement "+method2+" - "+stmt2+" is not in the executed statement list!");
//    		return;
//    	}	
		long time2 = System.currentTimeMillis();
		System.out.println("computeNodes ds2=getNodes(method2,stmt2, stmtStrs)" + (time2 - time1) + "ms\n");
    	if (debugOut)
    		System.out.println("computeNodes ds2.size()="+ds2.size());
//    	HashSet ns1=getSourceNodes(ds1, stmtStrs);
//    	HashSet ns2=getSinkNodes(ds2, stmtStrs);
    	//HashSet pathSet = getPathNodes(ds1, ds2);
    	String localStr=findPathMessageToNodes(ds1,ds2);
    	if (localStr.indexOf(" TRUETRUE")>1)
    	{	
    		System.out.println("It is a local path!");
    		isLocal=true;
    	}	
    	else
    		isLocal=false;
//    	if (debugOut)
//    		System.out.println("pathSet.size()="+pathSet.size());
//		long time3 = System.currentTimeMillis();
//		System.out.println("computeNodes pathSet = getPathNodes(ds1, ds2)" + (time3 - time2) + "ms\n");
    	sourceNodes.clear();
    	sinkNodes.clear();
//    	sourceMethods.clear();
//    	sinkMethods.clear();
    	sendMsgNodes.clear();
    	receiveMsgNodes.clear();
   	    if (!isLocal)   //remote path
    	{
    		//isLocal=false;
    		sourceMessage=getSuccessorMessageToWrite(ds1, stmtStrs, true);
    		long time3 = System.currentTimeMillis();
    		System.out.println("computeNodes get successorMessage " + (time3 - time2) + "ms\n");
    		sinkMessage=getPredecessorMessageToRead(ds2, stmtStrs, true);
    		long time4 = System.currentTimeMillis();
    		System.out.println("computeNodes get sinkMessage " + (time4 - time3) + "ms\n");
    		if ((sourceMessage.length()<1 || sinkMessage.length()<1)) {
        		isNoDependency = true;
        	}
        	else
        		isNoDependency = false;
    	}
    	else
    	{
    		sourceMessage="";
    		sinkMessage="";
    		//isLocal=true;
    		sourceMessage=localStr.replace(" TRUETRUE", "");
    		//getSuccessorMessage(ds1, ds2, stmtStrs, true);
    		System.out.println("computeNodes sourceMessage=" + sourceMessage);
    		long time3 = System.currentTimeMillis();
    		System.out.println("computeNodes get successorMessage " + (time3 - time2) + "ms\n");
    	}	
//    		else
//    		{	
//    			isLocal=false;
//    			if (debugOut) System.out.println("isLocal=false");
//	    		if ((sourceMessage.length()<1 || sinkMessage.length()<1)) {
//	        		isNoDependency = true;
//	        		if (debugOut) System.out.println("isNoDependency = true");
//	        	}
//	        	else
//	        	{	
//	        		isNoDependency = false;
//	        		if (debugOut) System.out.println("isNoDependency = false");
//	        	}	
//    		}
//    	}
		
    	//System.out.println("sourceMessage="+sourceMessage);
    	//System.out.println("sinkMessage="+sinkMessage);
//    	if (sourceMessage.length()<1 && !sinkMessage.contains(method2+" - "+stmt2))
//    		sourceMessage=method1+" - "+stmt1;
//    	if (sinkMessage.length()<1 && !sinkMessage.contains(method1+" - "+stmt1))
//    		sinkMessage=method2+" - "+stmt2;  
		long time5 = System.currentTimeMillis();
		System.out.println("computeNodes " + (time5 - time0) + "ms\n");
    }
    
	public static HashSet<DVTNode> getNodes(String method1,String stmt1, ArrayList stmtStrs) {		
		HashSet<DVTNode> hs=new HashSet();
		try {  
			String midMethod="";
			String midStmt="";
//			String str1="";
//			String str2="";
			for (DVTNode dn: dvtgPruned.nodeSet()) {
				//System.out.println("dn.getMethod()="+dn.getMethod()+" dn.getStmt()="+dn.getStmt());
				midMethod=dvtgPruned.idx2method.get(dn.getMethod()).toString();
				midStmt=stmtStrs.get(dn.getStmt()).toString();
				//System.out.println("midMethod="+midMethod+" midStmt="+midStmt);
				if (method1.equals(midMethod) && stmt1.equals(midStmt))
				{
					hs.add(dn);
				}
			}
		}
		catch (Exception e) { 
			System.err.println("Exception e="+e);
		}
		return hs;
	}
	
	public static HashSet<DVTNode> getNodes(String variable1, String method1,String stmt1, ArrayList stmtStrs) {		
		HashSet<DVTNode> hs=new HashSet();
		try {  
			String midMethod="";
			String midStmt="";
//			String str1="";
//			String str2="";
			for (DVTNode dn: dvtgPruned.nodeSet()) {
				midMethod=dvtgPruned.idx2method.get(dn.getMethod()).toString();
				midStmt=stmtStrs.get(dn.getStmt()).toString();
				if (variable1.equals(dn.getVar()) && method1.equals(midMethod) && stmt1.equals(midStmt))
				{
					hs.add(dn);
					break;
				}
			}
		}
		catch (Exception e) { 
			System.err.println("Exception e="+e);
		}
		return hs;
	}
	public static String oneNodeInfoFull(DVTNode dn, ArrayList stmtStrs) {		
		return "("+dn.getVar()+","+dvtgPruned.idx2method.get(dn.getMethod())+","+stmtStrs.get(dn.getStmt())+")";
	}
	public static String oneNodeInfo(DVTNode dn, ArrayList stmtStrs) {		
		return dvtgPruned.idx2method.get(dn.getMethod())+" - "+stmtStrs.get(dn.getStmt());
	}
    public static HashSet<DVTNode> getSourceNodes(DVTNode dn1, ArrayList stmtStrs)
    {
    	HashSet<DVTNode> sendMsgNodes = new HashSet();
    	sendMsgNodes.clear();
    	HashSet<DVTNode> sourceNodes = getSuccessorsToWrite(dn1,stmtStrs);
    	//System.out.println("sourceNodes="+sourceNodes);
		if (sendMsgNodes.size()<1)
		{	
			sendMsgNodes.addAll(sourceNodes);
			sendMsgNodes.remove(dn1);
		}
		//System.out.println("sendMsgNodes="+sendMsgNodes);
		HashSet sourceNodes2 = new HashSet();
		for (DVTNode n: sendMsgNodes)
		{
			sourceNodes2.addAll(getPredecessorsToNode(n,dn1));
		}
		sourceNodes.retainAll(sourceNodes2);
		if (sourceNodes.size()<1)
		{	
			sourceNodes.add(dn1);
		}	
		return sourceNodes;
		
    }	
    public static HashSet<DVTNode> getSourceNodes(HashSet<DVTNode> ns, ArrayList stmtStrs)
    {
    	HashSet resultS = new HashSet();
    	for (DVTNode d1: ns)    		
    		resultS.addAll(getSourceNodes(d1,stmtStrs));		
    	return resultS;    	
    }	
    public static HashSet<DVTNode> getSinkNodes(DVTNode dn2, ArrayList stmtStrs)
    {
    	HashSet<DVTNode> receiveMsgNodes = new HashSet();
    	receiveMsgNodes.clear();
    	HashSet<DVTNode> sinkNodes = getPredecessorsToRead(dn2,stmtStrs);
		if (receiveMsgNodes.size()<1)
		{	
			receiveMsgNodes.addAll(sinkNodes);
			receiveMsgNodes.remove(dn2);
		}	
		if (receiveMsgNodes.size()<1)
		{	
			receiveMsgNodes.add(dn2);
		}	
		//System.out.println("receiveMsgNodes="+receiveMsgNodes);
		HashSet sinkNodes2 = new HashSet();
		for (DVTNode n: receiveMsgNodes)
		{
			sinkNodes2.addAll(getSucessorsToNode(n,dn2));		
		}
		sinkNodes.retainAll(sinkNodes2);
		if (sinkNodes.size()<1)
		{	
			sinkNodes.add(dn2);
		}	
		return sinkNodes;		
    }	
	
    public static HashSet<DVTNode> getSinkNodes(HashSet<DVTNode> ns, ArrayList stmtStrs)
    {
    	HashSet resultS = new HashSet();
    	for (DVTNode d1: ns)    		
    		resultS.addAll(getSinkNodes(d1,stmtStrs));		
    	return resultS;    	
    }	
    public static HashSet getPathNodes(DVTNode dn1, DVTNode dn2)
    {
    	HashSet Nodes1 = new HashSet();
		Nodes1.add(dn1);
    	if (dtStmtUtil.compareNode(dn1,dn2)==0) 
    	{
    		return Nodes1;
    	}
    	Nodes1=getSucessorsToNode(dn1,dn2);
    	HashSet Nodes2=getPredecessorsToNode(dn2,dn1);
    	Nodes1.retainAll(Nodes2);
    	return Nodes1;
    }
    
    
    public static HashSet getPathNodes(HashSet<DVTNode> ns1, HashSet<DVTNode> ns2)
    {
    	HashSet resultS = new HashSet();
    	for (DVTNode d1: ns1)
    		for (DVTNode d2: ns2)
    			if (dtStmtUtil.compareNode(d1,d2)!=0) 
    				resultS.addAll(getPathNodes(d1,d2));		
    	return resultS;
    }
	public static HashSet getSuccessorNodes(DVTNode dn, boolean debugOut) {	
		int oldSize=0;
		HashSet<DVTNode> resultS=new HashSet();
		String messages="";
		boolean sizeIncremented=true;
		
		{
			resultS.add(dn);
			while (sizeIncremented)
			{	
				Object[] nodeS=resultS.toArray();
				for (int i=0;i<nodeS.length;i++)
				{	
					DVTNode d0=(DVTNode)nodeS[i];
					HashSet<DVTNode> tmpHs=new HashSet();
					tmpHs=getNextNodes(d0);
					Iterator iterator = tmpHs.iterator();  
			        while (iterator.hasNext()) {  
			            //System.out.println(d0+" --> "+iterator.next()); 
			        	messages+=d0+" --> "+iterator.next()+"\n";
			        }   					
					resultS.addAll(tmpHs);
				}	
				messages+=" \n";
				if (resultS.size()==oldSize)
				{	
					sizeIncremented=true;
					break;
				}	
				oldSize=resultS.size();
			}
			
		}
		if (debugOut)
			System.out.println(messages);
		return resultS;
	}
	public static HashSet getPredecessorNodes(DVTNode dn, boolean debugOut) {	
		int oldSize=0;
		HashSet<DVTNode> resultS=new HashSet();
		String messages="";
		boolean sizeIncremented=true;
		//try 
		{
			resultS.add(dn);
			while (sizeIncremented)
			{	
				Object[] nodeS=resultS.toArray();
				for (int i=0;i<nodeS.length;i++)
				{	
					DVTNode d0=(DVTNode)nodeS[i];
					HashSet<DVTNode> tmpHs=new HashSet();
					tmpHs=getPrevNodes(d0);
					Iterator iterator = tmpHs.iterator();  
			        while (iterator.hasNext()) {  
			        	messages+=d0+" <-- "+iterator.next()+"\n";
			        }   					
					resultS.addAll(tmpHs);
				}	
				messages+=" \n";
				if (resultS.size()==oldSize)
				{	
					sizeIncremented=true;
					break;
				}	
				oldSize=resultS.size();
			}
			
		}	
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
		if (debugOut)
			System.out.println(messages);
		return resultS;
	}	
	
	public static HashSet getSuccessorsToWrite(DVTNode dn, ArrayList<String> stmtStrs) {	
		int oldSize=0;
		HashSet<DVTNode> resultS=new HashSet();
		//HashSet<DVTNode> visited=new HashSet();
		String stmtStr="";
		//String messages="";
		String midStr="";
		String preStr="";
		String resultMsg="";
		DVTNode tmpNode=null;
		//Set<String> messageSet=new HashSet();
		boolean sizeIncremented=true;
		//try 
		{
			//resultS.add(dn);
			resultS.add(dn);
			while (sizeIncremented)
			{	
				Object[] nodeS=resultS.toArray();
				for (int i=0;i<nodeS.length;i++)
				{						
					DVTNode d0=(DVTNode)nodeS[i];
					//if (visited.contains(d0))
					//	continue;
					//stmtStr=stmtStrs.get(d0.getStmt()).toLowerCase();
					if (stmtStr.indexOf("<java.net.Socket: java.io.OutputStream getOutputStream()>")<=-1 && 
							stmtStr.indexOf("<java.io.ObjectOutputStream: void writeObject(java.lang.Object)>")<=-1 &&
							stmtStr.indexOf("java.nio.channels.SocketChannel")<=-1 &&
							stmtStr.indexOf("java.io.PrintWriter")<=-1 &&
							stmtStr.indexOf("java.io.OutputStreamWriter")<=-1 	&&
							stmtStr.indexOf("sendMessage")<=-1 	&&
							stmtStr.indexOf("void write(byte[])")<=-1 &&
							stmtStr.indexOf("java.io.PrintStream")<=-1 	&&
							stmtStr.indexOf("java.io.FileOutputStream")<=-1 
						   )
					{						
						HashSet<DVTNode> tmpHs=new HashSet();
						tmpHs=getNextNodes(d0);
						resultS.addAll(tmpHs);				
					}
					else
					{
						sendMsgNodes.add(d0);
						resultS.add(d0);
						return resultS;
					}
					resultS.add(d0);
				}	
				if (resultS.size()==oldSize)
				{	
					sizeIncremented=false;
					break;
				}	
				oldSize=resultS.size();
			}	
			
		}		
		return resultS;
	}

	public static HashSet getPredecessorsToRead(DVTNode dn, ArrayList<String> stmtStrs) {
		int oldSize=0;
		HashSet<DVTNode> resultS=new HashSet();
		//HashSet<DVTNode> visited=new HashSet();
		String stmtStr="";
		//String messages="";
		String midStr="";
		String preStr="";
		String resultMsg="";
		DVTNode tmpNode=null;
		//Set<String> messageSet=new HashSet();
		boolean sizeIncremented=true;
		//try 
		{
			//resultS.add(dn);
			resultS.add(dn);
			while (sizeIncremented)
			{	
				Object[] nodeS=resultS.toArray();
				for (int i=0;i<nodeS.length;i++)
				{						
					DVTNode d0=(DVTNode)nodeS[i];
					//if (visited.contains(d0))
					//	continue;
					//stmtStr=stmtStrs.get(d0.getStmt()).toLowerCase();
					if (stmtStr.indexOf("<java.net.Socket: java.io.InputStream getInputStream()>")<=-1 && 
							stmtStr.indexOf("<java.io.ObjectInputStream: java.lang.Object readObject()>")<=-1 &&
							stmtStr.indexOf("java.nio.channels.SocketChannel")<=-1 && 
							stmtStr.indexOf("java.lang.String readLine()")<=-1 &&
							stmtStr.indexOf("java.io.BufferedReader")<=-1 &&
							stmtStr.indexOf("java.io.InputStreamReader")<=-1 &&
							stmtStr.indexOf("java.io.Reader")<=-1  && 
							stmtStr.indexOf("java.lang.String readLine()")<=-1 &&
							stmtStr.indexOf("java.net.Socket getSocket()")<=-1 
						   ) 
					{						
						HashSet<DVTNode> tmpHs=new HashSet();
						tmpHs=getPrevNodes(d0);
						resultS.addAll(tmpHs);
//						Iterator iterator = tmpHs.iterator();  
//				        while (iterator.hasNext()) {  
//				        	tmpNode=(DVTNode)iterator.next();
//				        	resultS.add(tmpNode);
//				        }  						
					}
					else
					{
						receiveMsgNodes.add(d0);
						resultS.add(d0);
						return resultS;
					}
					resultS.add(d0);
				}	
				if (resultS.size()==oldSize)
				{	
					sizeIncremented=false;
					break;
				}	
				oldSize=resultS.size();
			}	
			
		}		
		return resultS;
	}
//	public static boolean findPathToNodes(HashSet<DVTNode> sourceNodes, HashSet<DVTNode> sinkNodes) {
//		boolean findPath=false;
//		System.out.println(" findPathToNodes sourceNode.size()="+sourceNodes.size());
//		System.out.println(" findPathToNodes sinkNode.size()="+sinkNodes.size());
//		for (DVTNode d1: sourceNodes)
//			for (DVTNode d2: sinkNodes)
//			{
//				findPath=findPathToNode(d1, d2);
//				System.out.println(" findPathToNodes from "+d1+" to "+d2+" findPath:"+findPath);
//				if (findPath)
//				{	
//					return true;
//				}	
//			}				
//		return findPath;
//	}
	public static String findPathMessageToNodes(HashSet<DVTNode> sourceNodes, HashSet<DVTNode> sinkNodes) {
		String findPathStr="";
		//System.out.println(" findPathToNodes sourceNode.size()="+sourceNodes.size());
		//System.out.println(" findPathToNodes sinkNode.size()="+sinkNodes.size());
		for (DVTNode d1: sourceNodes)
			for (DVTNode d2: sinkNodes)
			{
				findPathStr=findPathMsgToNode(d1, d2,true);
				//System.out.println(" findPathToNodes from "+d1+" to "+d2+" findPath:"+findPathStr);
				//System.out.println("\n **********************************************************\n");
				if (findPathStr.indexOf(" TRUETRUE")>1)
				{	
					return findPathStr;
				}	
			}				
		return "";
	}
	
//	public static boolean findPathToNode(DVTNode dn, DVTNode dstN) {
//		int oldSize=0;
//		HashSet<DVTNode> resultS=new HashSet();
//		//HashSet<DVTNode> visited=new HashSet();
//		String stmtStr="";
//		//String messages="";
//		String midStr="";
//		String preStr="";
//		String resultMsg="";
//		DVTNode tmpNode=null;
//		boolean findDst=false;
//		//Set<String> messageSet=new HashSet();
//		boolean sizeIncremented=true;
//		//try 
//		{
//			//resultS.add(dn);
//			resultS.add(dn);
//			while (sizeIncremented)
//			{	
//				if (findDst)
//					break;
//				Object[] nodeS=resultS.toArray();
//				for (int i=0;i<nodeS.length;i++)
//				{
//					if (findDst)
//						break;						
//					DVTNode d0=(DVTNode)nodeS[i];
//					if (dtStmtUtil.compareNode(dn,dstN)!=0)
//					{						
//						HashSet<DVTNode> tmpHs=new HashSet();
//						tmpHs=getNextNodes(d0);
//						Iterator iterator = tmpHs.iterator();  
//				        while (iterator.hasNext()) {  
//				        	tmpNode=(DVTNode)iterator.next();
//							if (dtStmtUtil.compareNode(dn,tmpNode)!=0)
//							{	
//								resultS.add(tmpNode);
//								System.out.println(" findPathToNodes add "+tmpNode);
//							}	
//							else
//							{
//								findDst=true;
//								break;
//							}
//				        }  						
//					}
//					else
//					{
//						findDst=true;
//						break;
//					}
//					resultS.add(d0);
//				}	
//				if (resultS.size()==oldSize)
//				{	
//					sizeIncremented=false;
//					break;
//				}	
//				oldSize=resultS.size();
//			}	
//			
//		}		
//		return findDst;
//	}

	public static String findPathMsgToNode(DVTNode dn, DVTNode dstN, boolean showDetail) {	
		int oldSize=0;
		HashSet<DVTNode> resultS=new HashSet();
		HashSet<DVTNode> visitedS=new HashSet();
		//HashSet<DVTNode> visited=new HashSet();
		String stmtStr="";
		String messages="";
		String midStr="";
		String preStr="";
		String resultMsg="";
		DVTNode tmpNode=null;
		Set<String> messageSet=new HashSet();
		boolean sizeIncremented=true;
		//try 
		{
			resultS.add(dn);
			sourceNodes.add(dn);
			//System.out.println("sourceNodes="+sourceNodes);
			int count=1;
			HashSet<DVTNode> tmpS=new HashSet();
			while (sizeIncremented)
			{	
				tmpS.clear();
				tmpS.addAll(resultS);
				// skip visited node
				tmpS.removeAll(visitedS);
				//Object[] nodeS=tmpS.toArray();
				for (DVTNode d0: tmpS)
				{						
					//DVTNode d0=(DVTNode)nodeS[i];
					// skip visited node
//					if (visitedS.contains(d0))
//		        		continue;
					visitedS.add(d0);
					//if (visited.contains(d0))
					//	continue;
					//stmtStr=stmtStrs.get(d0.getStmt()).toLowerCase();
					if (dtStmtUtil.compareNode(d0,dstN)!=0)
					{						
						HashSet<DVTNode> tmpHs=new HashSet();
						tmpHs=getNextNodes(d0);
						Iterator iterator = tmpHs.iterator();  
				        while (iterator.hasNext()) {  
				        	tmpNode=(DVTNode)iterator.next();
				        	if (resultS.contains(tmpNode))
				        		continue;
				        	if (showDetail)  {
				        		midStr=oneNodeInfo(d0,stmtStrs)+" --> "+oneNodeInfo(tmpNode,stmtStrs)+"\n";
				        	}
				        	else
				        		midStr=d0+" --> "+tmpNode+"\n";
				        	if (!messageSet.contains(midStr))  {	
				        		messageSet.add(midStr);
				        		preStr=""; //+count+":";
					        	for (int j=0;j<count; j++)
					        	{
					        		preStr+=" ";
					        	}				        		
				        		resultMsg+=preStr+midStr;				        		
				        		sourceNodes.add(tmpNode);
				        	}
				        	if (dtStmtUtil.compareNode(tmpNode,dstN)==0)
			        			return resultMsg+" TRUETRUE";
				        	sourceMethods.add(tmpNode.getMethod());			        		
				        }   	
				        //visited.addAll(resultS);
						resultS.addAll(tmpHs);
						sourceNodes.add(d0);
						//System.out.println("sourceNodes="+sourceNodes);
					}
					else
					{
						//System.out.println("sendMsgNodes="+sendMsgNodes);
						return resultMsg+" TRUETRUE";
					}
				}				
				count++;
				if (resultS.size()==oldSize)
				{	
					sizeIncremented=false;
					break;
				}	
				oldSize=resultS.size();
			}	
			
		}		
		return resultMsg+" FALSEFALSE ";
	}
	

	public static HashSet getSucessorsToNode(DVTNode dn, DVTNode dstN) {
		int oldSize=0;
		HashSet<DVTNode> resultS=new HashSet();
		//HashSet<DVTNode> visited=new HashSet();
		String stmtStr="";
		//String messages="";
		String midStr="";
		String preStr="";
		String resultMsg="";
		DVTNode tmpNode=null;
		boolean findDst=false;
		//Set<String> messageSet=new HashSet();
		boolean sizeIncremented=true;
		//try 
		{
			//resultS.add(dn);
			resultS.add(dn);
			while (sizeIncremented)
			{	
				if (findDst)
					break;
				Object[] nodeS=resultS.toArray();
				for (int i=0;i<nodeS.length;i++)
				{
					if (findDst)
						break;						
					DVTNode d0=(DVTNode)nodeS[i];
					if (dtStmtUtil.compareNode(dn,dstN)!=0)
					{						
						HashSet<DVTNode> tmpHs=new HashSet();
						tmpHs=getNextNodes(d0);
						Iterator iterator = tmpHs.iterator();  
				        while (iterator.hasNext()) {  
				        	tmpNode=(DVTNode)iterator.next();
							if (dtStmtUtil.compareNode(dn,tmpNode)!=0)
							{	
								resultS.add(tmpNode);
							}	
							else
							{
								findDst=true;
								break;
							}
				        }  						
					}
					else
					{
						findDst=true;
						break;
					}
					resultS.add(d0);
				}	
				if (resultS.size()==oldSize)
				{	
					sizeIncremented=false;
					break;
				}	
				oldSize=resultS.size();
			}	
			
		}		
		return resultS;
	}	
	public static HashSet getPredecessorsToNode(DVTNode dn, DVTNode dstN) {
		int oldSize=0;
		HashSet<DVTNode> resultS=new HashSet();
		//HashSet<DVTNode> visited=new HashSet();
		String stmtStr="";
		//String messages="";
		String midStr="";
		String preStr="";
		String resultMsg="";
		DVTNode tmpNode=null;
		boolean findDst=false;
		//Set<String> messageSet=new HashSet();
		boolean sizeIncremented=true;
		//try 
		{
			//resultS.add(dn);
			resultS.add(dn);
			while (sizeIncremented)
			{	
				if (findDst)
					break;
				Object[] nodeS=resultS.toArray();
				for (int i=0;i<nodeS.length;i++)
				{
					if (findDst)
						break;						
					DVTNode d0=(DVTNode)nodeS[i];
					if (dtStmtUtil.compareNode(dn,dstN)!=0)
					{						
						HashSet<DVTNode> tmpHs=new HashSet();
						tmpHs=getPrevNodes(d0);
						Iterator iterator = tmpHs.iterator();  
				        while (iterator.hasNext()) {  
				        	tmpNode=(DVTNode)iterator.next();
							if (dtStmtUtil.compareNode(dn,tmpNode)!=0)
							{	
								resultS.add(tmpNode);
							}	
							else
							{
								findDst=true;
								break;
							}
				        }  						
					}
					else
					{
						findDst=true;
						break;
					}
					resultS.add(d0);
				}	
				if (resultS.size()==oldSize)
				{	
					sizeIncremented=false;
					break;
				}	
				oldSize=resultS.size();
			}	
			
		}		
		return resultS;
	}	
	public static HashSet getNextNodes(DVTNode dn) {	
		HashSet hs = new HashSet();
		try {
			for (DVTEdge de : dvtgPruned.edgeSet()) {
				//System.out.println("dn="+dn+" de.getTarget()="+de.getTarget());
				if (dtStmtUtil.compareNode(dn,de.getSource())==0 && dtStmtUtil.compareNode(dn,de.getTarget())!=0)
					hs.add(de.getTarget());
			}
		}
		catch (Exception e) { 
			System.err.println("Exception e="+e);
		}
		return hs;
	}
	public static HashSet getPrevNodes(DVTNode dn) {	
		HashSet hs = new HashSet();
		try {
			for (DVTEdge de : dvtgPruned.edgeSet()) {
				//System.out.println("dn="+dn+" de.getTarget()="+de.getTarget());
				if (dtStmtUtil.compareNode(dn,de.getTarget())==0 && dtStmtUtil.compareNode(dn,de.getSource())!=0)
					hs.add(de.getSource());
			}
		}
		catch (Exception e) { 
			System.err.println("Exception e="+e);
		}
		return hs;
	}
	public static String getSuccessorMessage(DVTNode dn, HashSet nodes, ArrayList<String> stmtStrs, boolean showDetail) {	
		int oldSize=0;
		System.out.println("getSuccessorMessage nodes.size()="+nodes.size());
		HashSet<DVTNode> resultS=new HashSet();
		//HashSet<DVTNode> visited=new HashSet();
		String stmtStr="";
		String messages="";
		String midStr="";
		String preStr="";
		String resultMsg="";
		DVTNode tmpNode=null;
		Set<String> messageSet=new HashSet();
		boolean sizeIncremented=true;
		//try 
		{
			resultS.add(dn);
			sourceNodes.add(dn);
			int count=1;
			while (sizeIncremented)
			{	
				Object[] nodeS=resultS.toArray();
				for (int i=0;i<nodeS.length;i++)
				{						
					DVTNode d0=(DVTNode)nodeS[i];
					//if (visited.contains(d0))
					//	continue;
					stmtStr=stmtStrs.get(d0.getStmt()).toLowerCase();
					if (nodes.contains(d0)) 
					{						
						HashSet<DVTNode> tmpHs=new HashSet();
						tmpHs=getNextNodes(d0);
						Iterator iterator = tmpHs.iterator();  
				        while (iterator.hasNext()) {  
				        	tmpNode=(DVTNode)iterator.next();
				        	if (resultS.contains(tmpNode))
				        		continue;
				        	if (showDetail)  {
				        		midStr=oneNodeInfo(d0,stmtStrs)+" --> "+oneNodeInfo(tmpNode,stmtStrs)+"\n";
				        	}
				        	else
				        		midStr=d0+" --> "+tmpNode+"\n";
				        	if (!messageSet.contains(midStr))  {	
				        		messageSet.add(midStr);
				        		preStr=""; //+count+":";
					        	for (int j=0;j<count; j++)
					        	{
					        		preStr+=" ";
					        	}				        		
				        		resultMsg+=preStr+midStr;
				        		sourceNodes.add(tmpNode);
				        	}
				        	sourceMethods.add(tmpNode.getMethod());			        		
				        }   	
				        //visited.addAll(resultS);
						resultS.addAll(tmpHs);
						sourceNodes.add(d0);
					}				
				}				
				count++;
				System.out.println("getSuccessorMessage resultS.size()="+resultS.size()+" oldSize"+oldSize);
				if (resultS.size()==oldSize)
				{	
					sizeIncremented=false;
					break;
				}	
				oldSize=resultS.size();
			}	
			
		}		
		return resultMsg;
	}

//	public static String getPredecessorMessage(DVTNode dn, HashSet nodes, ArrayList<String> stmtStrs, boolean showDetail) {	
//		int oldSize=0;
//		HashSet<DVTNode> resultS=new HashSet();
//		String stmtStr="";
//		String messages="";
//		String midStr="";
//		String preStr="";
//		String resultMsg="";
//		DVTNode tmpNode=null;
//		Set<String> messageSet=new HashSet();
//		ArrayList messageA = new ArrayList();
//		boolean sizeIncremented=true;
//		//try 
//		{
//			resultS.add(dn);
//			sinkNodes.add(dn);
//			int count=1;
//			while (sizeIncremented)
//			{	
//				Object[] nodeS=resultS.toArray();
//				for (int i=0;i<nodeS.length;i++)
//				{	
//					DVTNode d0=(DVTNode)nodeS[i];
//					stmtStr=stmtStrs.get(d0.getStmt()).toLowerCase();
//					if (nodes.contains(d0)) 
//					{						
//						HashSet<DVTNode> tmpHs=new HashSet();
//						tmpHs=getPrevNodes(d0);
//						Iterator iterator = tmpHs.iterator();  
//				        while (iterator.hasNext()) {    
//				        	tmpNode=(DVTNode)iterator.next();
//				        	if (resultS.contains(tmpNode))
//				        		continue;
//				        	if (showDetail)  {
//				        		//midStr=oneNodeInfo(d0,stmtStrs)+" <-- "+oneNodeInfo(tmpNode,stmtStrs)+"\n";
//				        		midStr=oneNodeInfo(tmpNode,stmtStrs)+" --> "+oneNodeInfo(d0,stmtStrs)+"\n";
//				        	}
//				        	else
//				        		midStr=tmpNode+" --> "+d0+"\n";
//				        		//midStr=d0+" <-- "+tmpNode+"\n";
//				        	if (!messageSet.contains(midStr))  {	
//				        		messageSet.add(midStr);
//				        		preStr=""; //+count+":";
//					        	for (int j=0;j<count; j++)
//					        	{
//					        		preStr+=" ";
//					        	}				        		
//				        		//resultMsg+=preStr+midStr;
//					        	//System.out.println("LineStr="+preStr+midStr);
//					        	messageA.add(preStr+midStr);
//				        	}
//				        	sinkMethods.add(tmpNode.getMethod());
//				        }   					
//						resultS.addAll(tmpHs);
//					}					
//				}				
//				count++;
//				if (resultS.size()==oldSize)
//				{	
//					sizeIncremented=true;
//					break;
//				}	
//				oldSize=resultS.size();
//			}	
//			
//		}		
//		Collections.reverse(messageA);
//		for(int i =0;i<messageA.size(); i++){
//			resultMsg+=messageA.get(i);           
//        }
////		System.out.println("messageA="+messageA);
////		System.out.println("resultMsg="+resultMsg);
//		return resultMsg;
//	}
	

	public static String getSuccessorMessage(HashSet<DVTNode> dns, HashSet nodes, ArrayList<String> stmtStrs, boolean showDetail) {	
		String resultMsg="";
		System.out.println("getSuccessorMessage dns.size()="+dns.size());
		for (DVTNode d1: dns)
				resultMsg+=getSuccessorMessage(d1, nodes, stmtStrs, showDetail);
		return resultMsg;
	}
	
//	public static String getPredecessorMessage(HashSet<DVTNode> dns, HashSet nodes, ArrayList<String> stmtStrs, boolean showDetail) {	
//		String resultMsg="";
//		for (DVTNode d1: dns)
//				resultMsg+=getPredecessorMessage(d1, nodes, stmtStrs, showDetail);
//		return resultMsg;
//	}
//	
	public static String getSuccessorMessageToWrite(DVTNode dn, ArrayList<String> stmtStrs, boolean showDetail) {	
		int oldSize=0;
		HashSet<DVTNode> resultS=new HashSet();
		HashSet<DVTNode> visitedS=new HashSet();
		//HashSet<DVTNode> visited=new HashSet();
		String stmtStr="";
		String messages="";
		String midStr="";
		String preStr="";
		String resultMsg="";
		DVTNode tmpNode=null;
		Set<String> messageSet=new HashSet();
		boolean sizeIncremented=true;
		//try 
		{
			resultS.add(dn);
			sourceNodes.add(dn);
			//System.out.println("sourceNodes="+sourceNodes);
			int count=1;
			HashSet<DVTNode> tmpS=new HashSet();
			while (sizeIncremented)
			{	
				tmpS.clear();
				tmpS.addAll(resultS);
				// skip visited node
				tmpS.removeAll(visitedS);
				//Object[] nodeS=tmpS.toArray();
				for (DVTNode d0: tmpS)
				{						
					//DVTNode d0=(DVTNode)nodeS[i];
					// skip visited node
//					if (visitedS.contains(d0))
//		        		continue;
					visitedS.add(d0);
					//if (visited.contains(d0))
					//	continue;
					//stmtStr=stmtStrs.get(d0.getStmt()).toLowerCase();
					if (stmtStr.indexOf("<java.net.Socket: java.io.OutputStream getOutputStream()>")<=-1 && 
							stmtStr.indexOf("<java.io.ObjectOutputStream: void writeObject(java.lang.Object)>")<=-1 &&
							stmtStr.indexOf("java.nio.channels.SocketChannel")<=-1 &&
							stmtStr.indexOf("java.io.PrintWriter")<=-1 &&
							stmtStr.indexOf("java.io.OutputStreamWriter")<=-1 	&&
							stmtStr.indexOf("sendMessage")<=-1 	&&
							stmtStr.indexOf("void write(byte[])")<=-1 &&
							stmtStr.indexOf("java.io.PrintStream")<=-1 	&&
							stmtStr.indexOf("java.io.FileOutputStream")<=-1 
						   )
					{						
						HashSet<DVTNode> tmpHs=new HashSet();
						tmpHs=getNextNodes(d0);
						Iterator iterator = tmpHs.iterator();  
				        while (iterator.hasNext()) {  
				        	tmpNode=(DVTNode)iterator.next();
				        	if (resultS.contains(tmpNode))
				        		continue;
				        	if (showDetail)  {
				        		midStr=oneNodeInfo(d0,stmtStrs)+" --> "+oneNodeInfo(tmpNode,stmtStrs)+"\n";
				        	}
				        	else
				        		midStr=d0+" --> "+tmpNode+"\n";
				        	if (!messageSet.contains(midStr))  {	
				        		messageSet.add(midStr);
				        		preStr=""; //+count+":";
					        	for (int j=0;j<count; j++)
					        	{
					        		preStr+=" ";
					        	}				        		
				        		resultMsg+=preStr+midStr;
				        		sourceNodes.add(tmpNode);
				        	}
				        	sourceMethods.add(tmpNode.getMethod());			        		
				        }  				       
				        //visited.addAll(resultS);
						resultS.addAll(tmpHs);
						sourceNodes.add(d0);
						//System.out.println("sourceNodes="+sourceNodes);
					}
					else
					{
						sendMsgNodes.add(d0);
						System.out.println("sendMsgNodes="+sendMsgNodes);
						return resultMsg;
					}
				}				
				count++;
				if (resultS.size()==oldSize)
				{	
					sizeIncremented=false;
					break;
				}	
				oldSize=resultS.size();
			}	
			
		}		
		return ""; //resultMsg;
	}

	public static String getPredecessorMessageToRead(DVTNode dn, ArrayList<String> stmtStrs, boolean showDetail) {	
		int oldSize=0;
		HashSet<DVTNode> resultS=new HashSet();
		HashSet<DVTNode> visitedS=new HashSet();
		String stmtStr="";
		String messages="";
		String midStr="";
		String preStr="";
		String resultMsg="";
		DVTNode tmpNode=null;
		Set<String> messageSet=new HashSet();
		ArrayList messageA = new ArrayList();
		boolean sizeIncremented=true;
		//try 
		{
			resultS.add(dn);
			sinkNodes.add(dn);
			int count=1;
			HashSet<DVTNode> tmpS=new HashSet();
			while (sizeIncremented)
			{	
				tmpS.clear();
				tmpS.addAll(resultS);
				// skip visited node
				tmpS.removeAll(visitedS);
				//Object[] nodeS=tmpS.toArray();
				for (DVTNode d0: tmpS)
				{	
					//DVTNode d0=(DVTNode)nodeS[i];					
//					if (visitedS.contains(d0))
//		        		continue;					
					visitedS.add(d0);
					//stmtStr=stmtStrs.get(d0.getStmt()).toLowerCase();
					if (stmtStr.indexOf("<java.net.Socket: java.io.InputStream getInputStream()>")<=-1 && 
							stmtStr.indexOf("<java.io.ObjectInputStream: java.lang.Object readObject()>")<=-1 &&
							stmtStr.indexOf("java.nio.channels.SocketChannel")<=-1 && 
							stmtStr.indexOf("java.lang.String readLine()")<=-1 &&
							stmtStr.indexOf("java.io.BufferedReader")<=-1 &&
							stmtStr.indexOf("java.io.InputStreamReader")<=-1 &&
							stmtStr.indexOf("java.io.Reader")<=-1  && 
							stmtStr.indexOf("java.lang.String readLine()")<=-1 &&
							stmtStr.indexOf("java.net.Socket getSocket()")<=-1 
						   ) 
					{						
						HashSet<DVTNode> tmpHs=new HashSet();
						tmpHs=getPrevNodes(d0);
						Iterator iterator = tmpHs.iterator();  
				        while (iterator.hasNext()) {    
				        	tmpNode=(DVTNode)iterator.next();
				        	if (resultS.contains(tmpNode))
				        		continue;
				        	if (showDetail)  {
				        		//midStr=oneNodeInfo(d0,stmtStrs)+" <-- "+oneNodeInfo(tmpNode,stmtStrs)+"\n";
				        		midStr=oneNodeInfo(tmpNode,stmtStrs)+" --> "+oneNodeInfo(d0,stmtStrs)+"\n";
				        	}
				        	else
				        		midStr=tmpNode+" --> "+d0+"\n";
				        		//midStr=d0+" <-- "+tmpNode+"\n";				        	
				        	if (!messageSet.contains(midStr))  {	
				        		messageSet.add(midStr);
				        		preStr=""; //+count+":";
					        	for (int j=0;j<count; j++)
					        	{
					        		preStr+=" ";
					        	}				        		
				        		//resultMsg+=preStr+midStr;
					        	//System.out.println("LineStr="+preStr+midStr);
					        	messageA.add(preStr+midStr);
				        	}
				        	sinkMethods.add(tmpNode.getMethod());
				        }   					
						resultS.addAll(tmpHs);
						sinkNodes.add(d0);
					}
					else
					{
						receiveMsgNodes.add(d0);
						System.out.println("receiveMsgNodes="+receiveMsgNodes);
						return resultMsg;
					}				
				}				
				count++;
				if (resultS.size()==oldSize || resultS.size()==visitedS.size())
				{	
					sizeIncremented=true;
					break;
				}	
				oldSize=resultS.size();
			}	
			
		}		
		Collections.reverse(messageA);
		for(int i = 0;i < messageA.size(); i++){
			resultMsg+=messageA.get(i);           
        }

		return ""; //resultMsg;
	}
	
	public static String getSuccessorMessageToWrite(HashSet<DVTNode> dns, ArrayList<String> stmtStrs, boolean showDetail) {	
		String resultMsg="";
		for (DVTNode d1: dns)
				resultMsg+=getSuccessorMessageToWrite(d1,stmtStrs, showDetail);
		return resultMsg;
	}
	
	public static String getPredecessorMessageToRead(HashSet<DVTNode> dns, ArrayList<String> stmtStrs, boolean showDetail) {	
		String resultMsg="";
		for (DVTNode d1: dns)
				resultMsg+=getPredecessorMessageToRead(d1, stmtStrs, showDetail);
		return resultMsg;
	}	
	public static String getMethodStmtStr(String methodStr, ArrayList stmtStrs) {	
		String messages="";
		String midStr="";
		Set messageSet=new HashSet();
		if (methodStr.length()<1)
			return "";
		try {
			try {  //for (SVTNode sn : dvtgPruned.svtg.nodeSet()) {
				for (DVTNode dn : dvtgPruned.nodeSet()) {
//					// statement should be in the set
//					if (coveredStmts.size()>1)
//					{
//						System.out.println("dn.getStmt()="+dn.getStmt());
//						if (!coveredStmts.contains(dn.getStmt()))
//							continue;
//					}
					// method should be in the set
					if (methodStr.equals(dvtgPruned.idx2method.get(dn.getMethod())))  {
						midStr=methodStr+" - "+stmtStrs.get(dn.getStmt())+"\n";						
						if (!messageSet.contains(midStr))
						{	
							messages+=midStr;
							messageSet.add(midStr);
						}
					}
				}
			}
			catch (Exception e) { 
				System.err.println("Exception e="+e);
			}
		}
		catch (Exception e) { 
			System.err.println("Exception e="+e);
		}
		return messages;
	}
	
	public static boolean doesNodeHas(DVTNode dn, ArrayList methods, ArrayList stmtStrs) {		
		
		String midMethod=dvtgPruned.idx2method.get(dn.getMethod()).toString();
		if (!methods.contains(midMethod) || dn.getStmt()>(stmtStrs.size()-1) || dn.getStmt()<0)
			return false;
		return true;
	}
	/** exercise the static graph and query impacts for a single execution trace */
	public static int parseSequence(String changedMethods, HashMap EAs) throws Exception {
		Map<String, Set<String>> localImpactSets = new LinkedHashMap<String, Set<String>>();
		int nret = obtainValidChangeSet(changedMethods);
		if ( nret <= 0 ) {
			return  0;
		}
		List<String> validChgSet=new LinkedList<String>(changeSet);
		for (String chg : validChgSet) 
		{	
			if (chg.length()<1)
				continue;
			try {				
				if (0 != dvtg.buildGraph(dvtgExercised, chg, false, EAs)) {
					System.out.println("\nExecution sequences  were NOT successfully processed, skipped therefore.");
					return -1;
				}
				System.out.println("dynamic VTG exercised by current trace and change query [" + chg + "] :");
				//System.out.println(" 2th,  dvtgExercised edge size is "+dvtgExercised.edgeSet().size()+" and dvtgExercised node size is "+dvtgExercised.nodeSet().size());
				if (debugOut) {
					//System.out.println("dynamic VTG exercised by current trace and change query [" + chg + "] :");
					//dvtgExercised.dumpGraphInternals(true);
					System.out.println(dvtgExercised);
					if (applyDynAliasChecking && instancePrune) {
						int nPrunedEdges = dvtg.prunedByOID.size(); //dvtg.nPrunedEdgeByObjID;
						System.out.println("\n Object-id matching pruned " + nPrunedEdges+" edges in the dynamic graph during the querying process.");
					}
				}
			
				 
				Set<String> is = localImpactSets.get(chg);
				if (null == is) {
					is = new LinkedHashSet<String>();
					localImpactSets.put(chg, is);
				}				
				//System.out.println(" After handling trace files,  dvtgExercised edge size is "+dvtgExercised.edgeSet().size()+" and dvtgExercised node size is "+dvtgExercised.nodeSet().size());
				//if (dvtgExercised.edgeSet().size()>1 && dvtgExercised.nodeSet().size()>1)
					//dvtg=dvtgExercised;
				is.addAll(dvtgExercised.getImpactSet(chg));
			}
			
			catch (Exception e) {
				throw e;
			}
			finally
			{
				if (dvtgExercised.edgeSet().size()<=0 || dvtgExercised.nodeSet().size()<=0)
					dvtgExercised=dvtg;
			}
		}
		return localImpactSets.size();
	}
	public static int obtainValidChangeSet(String changedMethods) {
		changeSet.clear();  // in case this method (startParseTraces) gets multiple invocations from external callers 
		List<String> Chglist = dua.util.Util.parseStringList(changedMethods, ';');
		if (Chglist.size() < 1) {
			// nothing to do
			System.err.println("Empty query, nothing to do.");
			return -1;
		}
		long time0 = System.currentTimeMillis();
		// determine the valid change set
		//System.out.println("obtainValidChangeSet(String changedMethods) determine the valid change set");
		Set<String> validChgSet = new LinkedHashSet<String>();
		for (String chg : Chglist) {
			validChgSet.add(chg);
			validChgSet.addAll(dvtg.getChangeSet(chg));
		}
		if (validChgSet.isEmpty()) {
			// nothing to do
			// System.out.println("Invalid queries, nothing to do.");
			return 0;
		}
		//long time1 = System.currentTimeMillis();
		//System.out.println("obtainValidChangeSet(String changedMethods) changeSet.addAll(validChgSet)");
		changeSet.addAll(validChgSet);
		//long time2 = System.currentTimeMillis();
		//System.out.println("obtainValidChangeSet(String changedMethods) changeSet.addAll(validChgSet) " + (time2 - time1) + "ms\n");
		//System.out.println("obtainValidChangeSet(String changedMethods) " + (time2 - time0) + "ms\n");
		return changeSet.size();
	}
	public static Set<String> getChangeSet() {
		return changeSet;
	}
	
	public static int readStmtCoverage(String traceDir, String tId, List<Integer> coveredStmts) {
		long time0 = System.currentTimeMillis();
		String fnOut = traceDir  + File.separator + "stmtCoverage" + tId + ".out";
		String startMark = "Statements covered (based on branch coverage):";
		String startMark2 = "Total statements covered:";
		coveredStmts.clear();
		try {
			FileReader frdOut = new FileReader(new File(fnOut));
			BufferedReader rin = new BufferedReader(frdOut);
			int tmpInt=-1;
			while (true) {
				String strLine = rin.readLine();
				if (strLine == null) break;
				
				//if (strLine.startsWith(startMark)) {
				if (strLine.contains(startMark)) {
					String sub = strLine.substring(strLine.indexOf(startMark)+startMark.length()+1);
					List<String> stmtIds = dua.util.Util.parseStringList(sub,' ');
					//String[] stmtIds = sub.split(" ");
					for (String id : stmtIds) {
						tmpInt=Integer.valueOf(id);
						if (!coveredStmts.contains(tmpInt))
							coveredStmts.add(Integer.valueOf(id));
					}
					//break;
				}
				else if (strLine.contains(startMark2)) {
					String strs[]=strLine.split("covered: ");
					if (strs.length<2)  { continue; }
					String sub = strs[strs.length-1];
					//System.out.println("sub="+sub);
					List<String> stmtIds = dua.util.Util.parseStringList(sub,' ');
					//System.out.println("stmtIds="+stmtIds);
					//String[] stmtIds = sub.split(" ");
					for (String id : stmtIds) {
						tmpInt=Integer.valueOf(id);
						if (!coveredStmts.contains(tmpInt))
							coveredStmts.add(Integer.valueOf(id));
					}
				} 
			}
			
			rin.close();
			frdOut.close();
		}
		catch (Exception e) { 
			System.err.println("Error occurred when reading runtime coverage report from " + fnOut);
			return -1;
		}
		long time2 = System.currentTimeMillis();
		System.out.println("getRemoteString(String methodPair) " + (time2 - time0) + "ms\n");
		return coveredStmts.size();
	}	
}