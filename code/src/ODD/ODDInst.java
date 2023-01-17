/**
 * File: src/ODD/ODDInst.java
 * -------------------------------------------------------------------------------------------
 * Date			Author      Changes
 * -------------------------------------------------------------------------------------------
 * 03/22/2019	Developer		created; for DistODD instrument
*/
package ODD;

import EAS.EAInst;
import dua.Forensics;
import fault.StmtMapper;
import soot.Scene;
import soot.jimple.Stmt;
//import MciaUtil.utils;
//
//import edu.ksu.cis.indus.staticanalyses.dependency.DependencyXMLizer;
//import edu.ksu.cis.indus.staticanalyses.dependency.IDependencyAnalysis;
//import edu.ksu.cis.indus.staticanalyses.dependency.InterferenceDAv2;
//import edu.ksu.cis.indus.staticanalyses.dependency.InterferenceDAv3;
//import edu.ksu.cis.indus.staticanalyses.dependency.ReadyDAv2;
//import edu.ksu.cis.indus.staticanalyses.dependency.ReadyDAv3;
//import edu.ksu.cis.indus.staticanalyses.interfaces.IValueAnalyzer;


public class ODDInst extends EAInst {
	static boolean staticIndus = true;
	static boolean staticContextSensity = true;	
	static boolean staticFlowSensity = true;	
	protected static ODDOptions opts = new ODDOptions();

	// a flag ensuring timeout of static  
	static boolean timeOutStatic = false;
	// timeOut time of static 
	static long timeOutTimeStatic=Long.MAX_VALUE;            // 
	
	public static void main(String args[]){
		args = preProcessArgs(opts, args);

		ODDInst inst = new ODDInst();
		// examine catch blocks
		dua.Options.ignoreCatchBlocks = false;
		Scene.v().addBasicClass("ODD.ODDMonitor");
		Forensics.registerExtension(inst);
		Forensics.main(args);
	}
	
	@Override protected void init() {
		clsMonitor = Scene.v().getSootClass("ODD.ODDMonitor");
		clsMonitor.setApplicationClass();
		mInitialize = clsMonitor.getMethodByName("initialize");
		mEnter = clsMonitor.getMethodByName("enter");
		mReturnInto = clsMonitor.getMethodByName("returnInto");
		mTerminate = clsMonitor.getMethodByName("terminate");

    	
	}


	@Override public void run() {
		final long startTime = System.currentTimeMillis();
		Stmt[] idToS= StmtMapper.getCreateInverseMap();		
	     if (opts.debugOut()) 
	     {
	    	 System.out.println("Running DistODD Instrumentation");
	     }

		// Instrument EAS events
	     final long instrumentStartTime = System.currentTimeMillis();
	     instrument(); 
	     System.out.println("instrument() took " + (System.currentTimeMillis() - instrumentStartTime) + " ms");
	     System.out.println("All run() took " + (System.currentTimeMillis() - startTime) + " ms");
	}

} 