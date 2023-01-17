package disttaint;

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

import edu.ksu.cis.indus.staticanalyses.dependency.DependencyXMLizer;
import edu.ksu.cis.indus.staticanalyses.dependency.IDependencyAnalysis;
import edu.ksu.cis.indus.staticanalyses.dependency.InterferenceDAv1;
import edu.ksu.cis.indus.staticanalyses.dependency.InterferenceDAv2;
import edu.ksu.cis.indus.staticanalyses.dependency.InterferenceDAv3;
import edu.ksu.cis.indus.staticanalyses.dependency.ReadyDAv1;
import edu.ksu.cis.indus.staticanalyses.dependency.ReadyDAv2;
import edu.ksu.cis.indus.staticanalyses.dependency.ReadyDAv3;
import edu.ksu.cis.indus.staticanalyses.dependency.SynchronizationDA;
import edu.ksu.cis.indus.staticanalyses.interfaces.IValueAnalyzer;
import edu.ksu.cis.indus.staticanalyses.tokens.ITokens;
import edu.ksu.cis.indus.staticanalyses.dependency.DependencyXMLizerCLI;

public class OT3Inst extends EAInst {
	protected static dtOptions opts = new dtOptions();

	public static ArrayList coveredMethods = new ArrayList();
	public static ArrayList branchStmts = new ArrayList();
	
	protected SootClass clsMonitor;
	public static void main(String args[]){
		args = preProcessArgs(opts, args);

		OT3Inst o3Inst = new OT3Inst();
		// examine catch blocks
		dua.Options.ignoreCatchBlocks = false;
		if (opts.monitor_per_thread()) {
			Scene.v().addBasicClass("disttaint.OTMonitor");
		}
		else {
			Scene.v().addBasicClass("disttaint.dt2Monitor");
		}
		if (opts.use_socket()) {
			Scene.v().addBasicClass("disttaint.dtSocketInputStream");
			Scene.v().addBasicClass("disttaint.dtSocketOutputStream");
		}	
		Forensics.registerExtension(o3Inst);
		Forensics.main(args);
	}
	
	@Override protected void init() {
		if (opts.monitor_per_thread()) {
			clsMonitor = Scene.v().getSootClass("disttaint.dt2ThreadMonitor");
		}
		else {
			clsMonitor = Scene.v().getSootClass("disttaint.dt2Monitor");
		}
		clsMonitor.setApplicationClass();
//		clsBr = Scene.v().getSootClass("profile.BranchReporter");
//		clsBr.setApplicationClass();
		
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
//		    	 	
		    	 //System.out.println("idToS="+idToS);
		    	 //for (int i=0; i<idToS.length; i++)
		    		 //System.out.println("idToS["+i+"]="+idToS[i]);
	    	 System.out.println("Running FLOWDIST extension of DUA-Forensics");
	     }
		
		// 1. create the static value transfer graph
	    
		 
	     //coveredMethods=dtUtil.getArrayList(System.getProperty("user.dir") + File.separator + "methodList.out");	
	     coveredMethods=dtUtil.getArrayListFromTwo(System.getProperty("user.dir") + File.separator + "methodList.out",System.getProperty("user.dir") + File.separator + "coveredMethods.txt");
	     branchStmts=dtUtil.getArrayList(System.getProperty("user.dir") + File.separator + "entitystmt.out.branch");
	     
	     //coveredStmts = (ArrayList<Integer>) dtStmtUtil.readStmtCoverageOTInt(System.getProperty("user.dir"), 1); 	
	     
	     //if (opts.debugOut()) 
	     {
	    	 System.out.println("coveredMethods.size()="+coveredMethods.size());  
	    	 //System.out.println("coveredMethods="+coveredMethods); 
	    	 System.out.println("coveredStmts.size()="+branchStmts.size());  
	    	 //System.out.println("coveredStmts="+branchStmts); 
	    	 
	     }
	     //System.exit(0);
	     int ret = createVTGWithIndus();
	     
	     System.out.println("createVTGWithIndus() took " + (System.currentTimeMillis() - startTime) + " ms");
	     System.exit(0);
//		// 2. instrument EAS events
//	     final long instrumentStartTime = System.currentTimeMillis();
//	     instrument(); 
//	     System.out.println("instrument() took " + (System.currentTimeMillis() - instrumentStartTime) + " ms");
//	     System.out.println("All run() took " + (System.currentTimeMillis() - startTime) + " ms");
	    // instrument(coveredMethods);
//		this.instRetEvents();
//	    this.instCommEvents();
	}
	

	private int createVTGWithIndus() {
		StaticTransferGraphOT vtg = new StaticTransferGraphOT();
		//StaticTransferGraphOT vtg2 = new StaticTransferGraphOT();
		try {
			final long startTime = System.currentTimeMillis();
			//if (0==vtg.buildGraph(opts.debugOut())) return 0;
			vtg.setIncludeIntraCD(opts.intraCD);
			vtg.setIncludeInterCD(opts.interCD);
			
			vtg.setExInterCD(opts.exceptionalInterCD);
			vtg.setIgnoreRTECD(opts.ignoreRTECD);
			
			ArrayList branchStmts = new ArrayList();
			branchStmts=dtUtil.getArrayList(System.getProperty("user.dir") + File.separator + "entitystmt.out.branch");
			//if (branchStmts.size()>1)  
			{
				vtg.buildGraphWithMethodsStmts(opts.debugOut(),coveredMethods,branchStmts);
			}
//			else	if (coveredMethods.size()>1)
//			{
//				vtg.buildGraph(opts.debugOut(),coveredMethods);
//			}
//			else
//				vtg.buildGraph(opts.debugOut());
//				
//			System.out.println("vtg before Indus");
//			System.out.println(vtg);
			
//			final long AddEdgesWithIndusstartTime = System.currentTimeMillis();
//			System.out.println("	createVTGWithIndus_buildGraph  took " + (AddEdgesWithIndusstartTime - startTime) + " ms");
//			
//			try {
//				vtg2=AddEdgesWithIndus(vtg);
//			} catch (Exception e)  
//			{
//				vtg2=vtg;
//			}
			
			
			//System.out.println("vtg after Indus");
			//System.out.println(vtg);
//			System.out.println("vtg2 after Indus");
//			System.out.println(vtg2);
//			//vtg.AddThreadEdge(opts.debugOut(),"Dependence.log");
//			final long AddEdgesWithIndusstopTime = System.currentTimeMillis();
//			System.out.println("	createVTGWithIndus_AddEdgesWithIndus took " + (AddEdgesWithIndusstopTime - AddEdgesWithIndusstartTime) + " ms");
			
			// DEBUG: validate the static VTG against static forward slice
			if (opts.validateVTG) {
				// as a part of the static VTG validation, automatically check if VTG misses any dependences involving object variables, including
				// library objects
				vtg.checkObjvarDeps();
			}
			
		}
		catch (Exception e) {
			System.out.println("Error occurred during the construction of VTG");
			e.printStackTrace();
			return -1;
		}
		
		if (opts.debugOut()) 
		{
			vtg.dumpGraphInternals(true);
		}
		else 
		{
			System.out.println(vtg);
		}
		final long serializeVTGstartTime = System.currentTimeMillis();
		
		// DEBUG: test serialization and deserialization
		System.out.println("opts.serializeVTG="+opts.serializeVTG);
		// if (opts.serializeVTG) 
		{
			String fn = dua.util.Util.getCreateBaseOutPath() + "staticVtg.dat";
			System.out.println("fn = "+fn);
			if ( 0 == vtg.SerializeToFile(fn) ) 
			{
				if (opts.debugOut()) 
				{
					System.out.println("======== VTG successfully serialized to " + fn + " ==========");
					StaticTransferGraphOT g = new StaticTransferGraphOT();
					if (null != g.DeserializeFromFile (fn)) {
						System.out.println("======== VTG loaded from disk file ==========");
						//g.dumpGraphInternals(true);
						System.out.println(g);
					}
				}
			} // test serialization/deserialization
		} // test static VTG construction
		final long stopTime = System.currentTimeMillis();
		System.out.println("	createVTGWithIndus_serializeVTG took " + (stopTime - serializeVTGstartTime) + " ms");
		return 0;
	} // -- createVTG
//	private StaticTransferGraphOT AddEdgesWithIndus(StaticTransferGraphOT svtg) {
//		Object[][] _dasOptions = {//				
//				{"sda", "Synchronization dependence", new SynchronizationDA(svtg)},
//				{"frda1", "Forward Ready dependence v1", ReadyDAv1.getForwardReadyDA(svtg)},
////				{"brda1", "Backward Ready dependence v1", ReadyDAv1.getBackwardReadyDA(svtg)},
////				{"frda2", "Forward Ready dependence v2", ReadyDAv2.getForwardReadyDA(svtg)},
////				{"brda2", "Backward Ready dependence v2", ReadyDAv2.getBackwardReadyDA(svtg)},
////				{"frda3", "Forward Ready dependence v3", ReadyDAv3.getForwardReadyDA(svtg)},
////				{"brda3", "Backward Ready dependence v3", ReadyDAv3.getBackwardReadyDA(svtg)},
//				{"ida1", "Interference dependence v1", new InterferenceDAv1(svtg)},
////				{"ida2", "Interference dependence v2", new InterferenceDAv2(svtg)},
////				{"ida3", "Interference dependence v3", new InterferenceDAv3(svtg)},
//				};
//		DependencyXMLizerCLI _xmlizerCLI = new DependencyXMLizerCLI();
//		_xmlizerCLI.xmlizer.setXmlOutputDir("/tmp");
//		_xmlizerCLI.dumpJimple = false;
//		_xmlizerCLI.useAliasedUseDefv1 = false;
//		_xmlizerCLI.useSafeLockAnalysis = false;
//		_xmlizerCLI.exceptionalExits = false;
//		_xmlizerCLI.commonUncheckedException = false;
//		final List<String> _classNames = new ArrayList<String>();
//		for (SootClass sClass:Scene.v().getApplicationClasses()) 
//		{
//			_classNames.add(sClass.toString());
//		}	
//		if (_classNames.isEmpty()) {
//			System.out.println("Please specify at least one class.");
//		}
//		System.out.println("_classNames="+_classNames);
//		_xmlizerCLI.setClassNames(_classNames);
//		if (!_xmlizerCLI.parseForDependenceOptions(_dasOptions,_xmlizerCLI)) {
//			System.out.println("At least one dependence analysis must be requested.");
//		}
//		System.out.println("_xmlizerCLI.das.size(): " + _xmlizerCLI.das.size());
//		
//		_xmlizerCLI.<ITokens> execute();
//		System.out.println("svtg");
//		System.out.println(svtg);
//		return svtg;
//	}
} 