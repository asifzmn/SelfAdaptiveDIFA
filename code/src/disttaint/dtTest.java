package disttaint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

//import soot.*;
//import soot.jimple.*;
//import soot.util.*;
//import soot.Scene;
//import soot.jimple.spark.SparkTransformer;
//import soot.options.*;
//import soot.Pack;
//import soot.PackManager;
//import soot.PhaseOptions;
//import soot.Scene;
//import soot.SootClass;
//import soot.SootMethod;
//import soot.Transform;
//import soot.options.Options;
//import soot.*;
//import soot.tools.CFGViewer;
//import soot.util.cfgcmd.CFGOptionMatcher.CFGOption;






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



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.LongType;
import soot.Modifier;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.Transform;
import soot.jimple.GotoStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.util.Chain;



public class dtTest {
	  public static Map<Stmt, Integer> sToId = null;
	  public static Stmt[] idToS = null;
	 static boolean debugOut = true;
	 public static void main(String []args) {
		System.out.println("args="+args);
//        if(args.length == 0)
//        {
//            System.out.println("Usage: java MyAnalysis class_to_analyse");
//            System.exit(0);
//        }            
//        else
//			System.out.println("[mainClass]"+args[0]);	
//        if (debugOut)
//        {	
//			System.out.println("args[0]="+args[0]);	
//			System.out.println("args[1]="+args[1]);	
//			System.out.println("args[2]="+args[2]);	
//        } 
        PackManager.v().getPack("jap").add(new Transform("jap.myTransform", new MyAnalysisdtTest()));

        //String paras[] = new String[] {"-cp",".;C:/JDK1872/jre/lib/rt.jar;C:/soot/soot-2.5.0.jar;C:/Research/duafWK1/InstrReporters/bin","-allow-phantom-refs","-pp","profile.BranchReporter"}; 
        //String paras[] = new String[] {"-cp",".;C:/JDK1872/jre/lib/rt.jar;C:/soot/soot-2.5.0.jar;C:/Soot/In","-allow-phantom-refs","-pp","C"}; 
        //String paras[] = new String[] {"-cp",".","-allow-phantom-refs","-pp","-process-dir", "C:/Soot/In"};    
        String paras[] = new String[] {"-cp",".","-allow-phantom-refs","-pp","-process-dir", "C:/Soot/In"};     
        soot.Main.main(paras);		 
    }
	 
}

class MyAnalysisdtTest extends BodyTransformer
{
	static boolean debugOut = true;
//	static HashSet<String> sources = new HashSet<>(); 
//	static HashSet<String> sinks = new HashSet<>(); 
////	static TreeSet<String> sourceMethods = new TreeSet<>(); 
////	static TreeSet<String> sinkMethods = new TreeSet<>(); 		
////	static String sourceMsg=""; 
////	static String sinkMsg="";
//	static String sourceFile=System.getProperty("user.dir") + File.separator + "source_"+System.currentTimeMillis() + ".txt";
//	static String sinkFile=System.getProperty("user.dir") + File.separator + "sink_"+System.currentTimeMillis() + ".txt";
//	static String allFile=System.getProperty("user.dir") + File.separator + "all_"+System.currentTimeMillis() + ".txt";
//	static String methodPairFile=System.getProperty("user.dir") + File.separator + "sourceSinkMethodPair_"+System.currentTimeMillis() + ".txt";
//	static String stmtPairFile=System.getProperty("user.dir") + File.separator + "sourceSinkStmtPair_"+System.currentTimeMillis() + ".txt";
//	ArrayList  sourceMethods = new ArrayList();
//	ArrayList  sinkMethods = new ArrayList();
//	ArrayList  sourceStmts = new ArrayList();
//	ArrayList  sinkStmts = new ArrayList();
    protected void internalTransform(Body myBody, String phaseName, Map options)
    {
    	String midStr1="";
		for (SootClass c:Scene.v().getApplicationClasses()) 
		//SootClass c = Scene.v().getSootClass("C");
    	//SootClass c = myBody.getMethod().getDeclaringClass();
    	//SootClass c = Scene.v().loadClassAndSupport("C");
		{
			System.out.println("getApplicationClasses()="+c);
//			if(m.isConcrete())
			for(SootMethod m:c.getMethods())
			{
				System.out.println("[sootMethod]="+m);
				if(m.isConcrete())
				{
					Body b=m.retrieveActiveBody();
					//System.out.println("[body]"+b);
					Iterator<Unit> stmts=b.getUnits().snapshotIterator();
					while(stmts.hasNext())
					{
						Unit u=stmts.next();
						midStr1=u.toString();
						//System.out.println("midStr1="+midStr1);
					
					}
				}
			}			
		}
    }

}