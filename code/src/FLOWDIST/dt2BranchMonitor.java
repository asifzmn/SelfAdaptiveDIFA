package FLOWDIST;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;




import profile.BranchReporter;
//import MciaUtil.MethodEventComparator;
import profile.InstrumManager;
import profile.BranchInstrumenter;
import profile.AuxClassInstrumenter;
import soot.Body;
import soot.Local;
import soot.PatchingChain;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Trap;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.GotoStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.ThrowStmt;
import soot.jimple.VirtualInvokeExpr;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.ExceptionalBlockGraph;
import soot.util.Chain;
import soot.util.dot.DotGraph;
import EAS.EAInst;
import MciaUtil.CompleteUnitGraphEx;
import MciaUtil.RDFCDBranchEx;
import MciaUtil.TryCatchWrapper;
import MciaUtil.utils;
import dua.Extension;
import dua.Forensics;
import dua.Options;
import dua.global.ProgramFlowGraph;
import dua.method.CFG.CFGNode;
import dua.method.CFGDefUses.Branch;
import dua.method.CFGDefUses.Branch.BranchComparator;
import dua.method.CallSite;
import dua.util.Pair;
import dua.util.Util;
import fault.StmtMapper;
import dua.method.CFG;
/** Monitoring method events in runtime upon 
 * invocations by instrumented probes in the subject
 *
 * For Diver, this monitor captures full sequence of method events. For a uniform basis, also
 * to track two kinds of events only: entrance (first event) and return-into (last event), as EAS does
 */

public class dt2BranchMonitor {

	
	
	/* two special events */
	public static final int PROGRAM_START = Integer.MIN_VALUE;
	public static final int PROGRAM_END = Integer.MAX_VALUE;
	
	/* debug flag: e.g. for dumping event sequence to human-readable format for debugging purposes, etc. */
	protected static boolean debugOut = true;
	public static void turnDebugOut(boolean b) { debugOut = b; }

	/* a flag ensuring the initialization and termination are both executed exactly once and they are paired*/
	protected static boolean bInitialized = false;
	

	public static String receivedMessages="";
	
	
	/* for DUAF/Soot to access this class */
	public static void __link() { }
	
	
	protected static SootClass clsBr;
	

	/* The array will be used to record stmt branches */
	//public static int[] covBranches;
	
	/* initialize  */		
	public synchronized static void initialize() throws Exception{
		
		System.out.println("**************dt2BranchMonitor::initialize()  0th");		
		//	clsBr = Scene.v().getSootClass("profile.BranchReporter");
		//	clsBr.setApplicationClass();
		//System.out.println("dtUtil.getLineNum="+dtUtil.getLineNum("entitystmt.out.branch"));
		//covBranches=new int[dtUtil.getLineNum("entitystmt.out.branch")];
		
		/** add hook to catch SIGKILL/SIGTERM */
		Runtime.getRuntime().addShutdownHook( new Thread()
        {
          public void run()
          {
            System.out.println( "Shutdown signal caught!" ) ;
        	//  System.out.println("**************dt2BranchMonitor::initialize() addShutdownHook run() 0th");	  
        	/** guarantee that the trace, if any collected, gets dumped */
        	if (debugOut) {
        		System.out.println("\nDumping method event trace of current process execution ...");
        	}
    		try {
    			bInitialized = true;
    			System.out.println("**************dt2BranchMonitor::initialize() addShutdownHook run() 1th");	  
				dt2BranchMonitor.terminate("Forced upon external termination");
				System.out.println("**************dt2BranchMonitor::initialize() addShutdownHook run() 2th");	  
			} catch (Exception e) {
				e.printStackTrace();
			}
          }
        } ) ;
		
		String debugFlag = System.getProperty("ltsDebug");
		System.out.println("debugFlag="+debugFlag);
		if (null != debugFlag) {
			debugOut = debugFlag.equalsIgnoreCase("true");
		}

		
		//debugOut=true;
		System.out.println("debugOut="+debugOut);
		System.out.println("dt2BranchMonitor starts working ......");
		
		
	}
	
	public synchronized static void enter(String methodname){
		//System.out.println("enter(String methodname)="+methodname);
	}
	
	public synchronized static void returnInto(String methodname, String calleeName){
		//System.out.println("returnInto(String methodname)="+methodname+" calleeName="+calleeName);
	}
	

	public synchronized static void terminate(String where) throws Exception {
		/** NOTE: we cannot call simply forward this call to super class even though we do the same thing as the parent, because
		 * we need take effect the overloaded SerializeEvents() here below 
		 */
		//Monitor.terminate(where);
//		if (bInitialized) {
//			bInitialized = false;
//		}
//		else {
//			return;
//		}
		//if (debugOut)
		System.out.println("terminate(String where)="+where);		
		int[] covArray = profile.BranchReporter.getBrCovArray();
		System.out.println("covArray="+covArray);
		if (debugOut)
		{	
			System.out.println("covArray.length="+covArray.length);
			for (int i = 0; i < covArray.length; i++) {
				System.out.print(covArray[i]+" ");
			}
			System.out.println();
		}
		profile.BranchReporter br=new profile.BranchReporter();
		br.writeReportMsg(covArray, "stmtCoverage1.out");
	}
//	private static void writeMessage(String fileName, String message) {
//		try {
//        FileWriter fw = new FileWriter(fileName, true);
//        BufferedWriter bw = new BufferedWriter(fw);
//        bw.append(message);
//        bw.close();
//        fw.close();
//		}
//        catch (Exception e) {
//        	System.out.println("Cannot write message to" + fileName );
//			e.printStackTrace();
//		}
//	}

}