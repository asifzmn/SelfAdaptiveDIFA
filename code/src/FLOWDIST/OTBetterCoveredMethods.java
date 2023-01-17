package FLOWDIST;

import dua.Forensics;
import fault.StmtMapper;
import soot.*;
import soot.util.dot.DotGraph;
import EAS.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
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

public class OTBetterCoveredMethods   extends EAInst{
	protected static dtOptions opts = new dtOptions(); 

	public static HashSet<String> sourceMethods = new HashSet<String>();
	public static HashSet<String> sinkMethods = new HashSet<String>();
	public static HashSet<SootMethod> coveredMethods= new HashSet<SootMethod>();
//	public static ArrayList sourceStmts = new ArrayList();
//	public static ArrayList sinkStmts = new ArrayList();
	
	protected SootClass clsMonitor;
	public static void main(String args[]){
		args = preProcessArgs(opts, args);

		OTBetterCoveredMethods dvInst = new OTBetterCoveredMethods();
		// examine catch blocks
		dua.Options.ignoreCatchBlocks = false;
		Scene.v().addBasicClass("disttaint.OTMonitor");
//		if (opts.monitor_per_thread()) {
//			Scene.v().addBasicClass("disttaint.OTMonitor");
//		}
//		else {
//			Scene.v().addBasicClass("disttaint.dtMonitor");
//		}
//		if (opts.use_socket()) {
//			Scene.v().addBasicClass("disttaint.dtSocketInputStream");
//			Scene.v().addBasicClass("disttaint.dtSocketOutputStream");
		//		}
		
		Forensics.registerExtension(dvInst);
		Forensics.main(args);
		
		
	     
	}
	
	@Override protected void init() {
	}
	
	@Override public void run() {
		final long startTime = System.currentTimeMillis();
		//Stmt[] idToS= StmtMapper.getCreateInverseMap();	
	     sourceMethods=dtUtil.getListSet(System.getProperty("user.dir") + File.separator + "coveredSourceMethods.txt");		
		 		
	     sinkMethods=dtUtil.getListSet(System.getProperty("user.dir") + File.separator + "coveredSinkMethods.txt");
	     //if (opts.debugOut()) 
	     {
	    	 System.out.println("sourceMethods.size()="+sourceMethods.size());  
	    	 System.out.println("sinkMethods.size()="+sinkMethods.size()); 	    	 
//	    	 System.out.println("sourceStmts.size()="+sourceStmts.size());  
//	    	 System.out.println("sinkStmts.size()="+sinkStmts.size()); 
	     }
	     
	     coveredMethods = getMethodsFromCFGWithIndus();
	     System.out.println("coveredMethods.size()="+coveredMethods.size()); 
	     dtUtil.writeSet(coveredMethods, System.getProperty("user.dir") + File.separator + "coveredMethods.txt");
	     System.out.println("main() took " + (System.currentTimeMillis() - startTime) + " ms");
	     System.exit(0);
	}
	HashSet<SootMethod> getMethodsFromCFGWithIndus() {
		StaticTransferGraphOT123 vtg = new StaticTransferGraphOT123();
		//StaticTransferGraphOT123 vtg2 = new StaticTransferGraphOT123();
		HashSet<SootMethod> coveredMethods= new HashSet<SootMethod>();
		try {
			final long startTime = System.currentTimeMillis();
			//if (0==vtg.buildGraph(opts.debugOut())) return 0;
			vtg.setIncludeIntraCD(opts.intraCD);
			vtg.setIncludeInterCD(opts.interCD);
			
			vtg.setExInterCD(opts.exceptionalInterCD);
			vtg.setIgnoreRTECD(opts.ignoreRTECD);
//			vtg.buildGraph(opts.debugOut());
//			if (coveredMethods.size()>1)
//			{
//				vtg.buildGraph(opts.debugOut(),coveredMethods);
//			}
//			if (sourceMethods.size()>1 && sinkMethods.size()>1)
//			{
//				vtg.buildGraph(opts.debugOut(),sourceMethods, sinkMethods);
//			}
//			else
//				vtg.buildGraph(opts.debugOut());
				
			System.out.println("vtg before Indus");
			System.out.println(vtg);
//			final long AddEdgesWithIndusstartTime = System.currentTimeMillis();
//			StaticTransferGraphOT123 vtg2=AddEdgesWithIndus(vtg);
//
//			System.out.println("vtg2 after Indus");
//			System.out.println(vtg2);
//			//vtg.AddThreadEdge(opts.debugOut(),"Dependence.log");
//			final long AddEdgesWithIndusstopTime = System.currentTimeMillis();
			
			coveredMethods=vtg.getMethodsFromGraph(false, sourceMethods, sinkMethods);
		}
		catch (Exception e) {
			System.out.println("Error occurred during the construction of VTG");
			e.printStackTrace();
			return null;
		}
	
		return coveredMethods;
	} // -- createVTG
	
	
	private static StaticTransferGraphOT123 AddEdgesWithIndus(StaticTransferGraphOT123 svtg) {
		Object[][] _dasOptions = {//				
				{"sda", "Synchronization dependence", new SynchronizationDA(svtg)},
				{"frda1", "Forward Ready dependence v1", ReadyDAv1.getForwardReadyDA(svtg)},
//				{"brda1", "Backward Ready dependence v1", ReadyDAv1.getBackwardReadyDA(svtg)},
//				{"frda2", "Forward Ready dependence v2", ReadyDAv2.getForwardReadyDA(svtg)},
//				{"brda2", "Backward Ready dependence v2", ReadyDAv2.getBackwardReadyDA(svtg)},
//				{"frda3", "Forward Ready dependence v3", ReadyDAv3.getForwardReadyDA(svtg)},
//				{"brda3", "Backward Ready dependence v3", ReadyDAv3.getBackwardReadyDA(svtg)},
				{"ida1", "Interference dependence v1", new InterferenceDAv1(svtg)},
//				{"ida2", "Interference dependence v2", new InterferenceDAv2(svtg)},
//				{"ida3", "Interference dependence v3", new InterferenceDAv3(svtg)},
				};
		DependencyXMLizerCLI _xmlizerCLI = new DependencyXMLizerCLI();
		_xmlizerCLI.xmlizer.setXmlOutputDir("/tmp");
		_xmlizerCLI.dumpJimple = false;
		_xmlizerCLI.useAliasedUseDefv1 = false;
		_xmlizerCLI.useSafeLockAnalysis = false;
		_xmlizerCLI.exceptionalExits = false;
		_xmlizerCLI.commonUncheckedException = false;
		final List<String> _classNames = new ArrayList<String>();
		for (SootClass sClass:Scene.v().getApplicationClasses()) 
		{
			_classNames.add(sClass.toString());
		}	
		if (_classNames.isEmpty()) {
			System.out.println("Please specify at least one class.");
		}
		System.out.println("_classNames="+_classNames);
		_xmlizerCLI.setClassNames(_classNames);
		if (!_xmlizerCLI.parseForDependenceOptions(_dasOptions,_xmlizerCLI)) {
			System.out.println("At least one dependence analysis must be requested.");
		}
		System.out.println("_xmlizerCLI.das.size(): " + _xmlizerCLI.das.size());
		
		_xmlizerCLI.<ITokens> execute();
		System.out.println("svtg");
		System.out.println(svtg);
		return svtg;
	}
} 