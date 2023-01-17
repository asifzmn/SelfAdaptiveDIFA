package FLOWDIST;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import soot.jimple.spark.SparkTransformer;
import soot.options.Options;
import soot.*;
import soot.jimple.*;
//import soot.util.*;

public class dtSourceSink3 {
	public static String path = ""; //"C:/Research/nioecho/bin";  ///voldemort/rest/coordinator/admin
	static boolean debugOut = true;
	public static void main(String args[]) {
      if(args.length == 0)
      {
          System.out.println("Usage: dtSourceSink2 directory");
          System.exit(0);
      }            
      else
			System.out.println("[mainClass]"+args[0]);	
      path = args[0]; 
		
		HashSet<String> sources = new HashSet<>(); 
		HashSet<String> sinks = new HashSet<>(); 
//		LinkedHashMap<String, String> sourceClassMethods=new LinkedHashMap<String, String>();
//		LinkedHashMap<String, String> sinkClassMethods=new LinkedHashMap<String, String>();
		ArrayList  sourceMethods = new ArrayList();
		ArrayList  sinkMethods = new ArrayList();
		ArrayList  sourceStmts = new ArrayList();
		ArrayList  sinkStmts = new ArrayList();
//		TreeSet<String> sourceMethods = new TreeSet<>(); 
//		TreeSet<String> sinkMethods = new TreeSet<>(); 		
//		String sourceMsg=""; 
//		String sinkMsg="";
		String sourceFile=System.getProperty("user.dir") + File.separator + "source2_1.txt";
		String sinkFile=System.getProperty("user.dir") + File.separator + "sink2_1.txt"; 
		// set soot options 1 and 2
		initial(path);
		enableSpark(path);
   	 	sources=dtUtil.getSet("data/Sources2.txt"); 
   	 	System.out.println("sources.size()="+sources.size()+" sources="+sources);
//   	 	for (String s:sources) 
//   	 	{
//   	 		System.out.println("sources=" +s);
//   	 	}
//   	 		
//   	    Set<SootClass> sourceSootClses = new HashSet<SootClass>();
//		for (String clsname : sources) {
//			sourceSootClses.add( Scene.v().getSootClass(clsname) );
//		}
//		
		sinks=dtUtil.getSet("data/Sinks2.txt"); 
   	 	System.out.println("sinks.size()="+sinks.size()+" sinks="+sinks);
//     	Set<SootClass> sinkSootClses = new HashSet<SootClass>();
//		for (String clsname : sinks) {
//			sinkSootClses.add( Scene.v().getSootClass(clsname) );
//		}
//     	
//   	    sourceClassMethods=dtConfig.getClassesMethods("data/Sources.txt", "\t");
//   	    System.out.println("sourceClassMethods=:" + sourceClassMethods);
//   	    sinkClassMethods=dtConfig.getClassesMethods("data/Sinks.txt", "\t");
//   	    System.out.println("sinkClassMethods=:" + sinkClassMethods);
        String sourceMsg=""; 
        String sinkMsg="";
//        String allMsg="";
        String methodPairMsg="";
        String stmtPairMsg="";
		String midStr1="";
		String sourceItem="";
		String sinkItem="";
		//String methodSource1="";
		String methodSource2="";
		//String methodSink1="";
		String methodSink2="";
		String methodItem="";
//		boolean useSourceInterfaceFirstStmt=false;
//		boolean useSinkInterfaceFirstStmt=false;
//		String sourceClassHierarchy="";
//		String sinkClassHierarchy="";
//		String sourceClassHierarchyMethod="";
//		String sinkClassHierarchyMethod="";
		
		for (SootClass sClass:Scene.v().getApplicationClasses()) 
		{
			if ( sClass.isPhantom() ) {	continue; }
			if ( !sClass.isConcrete() ) {	continue; }
			//System.out.println("sClass="+sClass);
//			useSourceInterfaceFirstStmt=false;
//			useSinkInterfaceFirstStmt=false;
//			sourceClassHierarchyMethod="";
//			sourceClassHierarchy=getHierarchy(sClass, sourceSootClses);
//			if (sourceClassHierarchy.length()>0)  //org.jboss.netty.channel.ChannelPipelineFactory
//			{
//				sourceClassHierarchyMethod=dtConfig.getMethods(sourceClassMethods, sourceClassHierarchy).replace("|", " "); 
//				System.out.println("The source hierarchy class for "+sourceClassHierarchy+" with method" +sourceClassHierarchyMethod +" is :" + sClass.getName());
//				//useSourceInterface=true;
//			}
//			sinkClassHierarchyMethod="";
//			sinkClassHierarchy=getHierarchy(sClass, sinkSootClses);
//			if (sinkClassHierarchy.length()>0)
//			{
//				System.out.println("The sink hierarchy class for "+sinkClassHierarchy+" is :" + sClass.getName());
//				//useSinkInterface=true;
//			}
		
			for(SootMethod m:sClass.getMethods())
			{
				if(!m.isConcrete()) {	continue; }
				methodSource2=m.toString();
				methodSink2=methodSource2;			
				//System.out.println("methodSource2="+methodSource2+"methodSink2="+methodSink2);
				//Body b=m.retrieveActiveBody();
				//System.out.println("[body]"+b);
				//methodSource2=<voldemort.rest.coordinator.admin.CoordinatorAdminPipelineFactory: org.jboss.netty.channel.ChannelPipeline getPipeline()>midStr1=r0 := @this: voldemort.rest.coordinator.admin.CoordinatorAdminPipelineFactory
				Iterator<Unit> stmts=m.retrieveActiveBody().getUnits().snapshotIterator();
				while(stmts.hasNext())
				{
					Unit u=stmts.next();
					if(!(u instanceof IdentityStmt) && !(u instanceof AssignStmt) && !(u instanceof InvokeStmt) && !(u instanceof DefinitionStmt) && !(u instanceof RetStmt)) 
					{	continue; }
					midStr1=u.toString();

					//System.out.println("midStr1="+midStr1);
					if (dtUtil.isIncludeSetItem(midStr1, sources))	{	
						System.out.println("adding source methodSource2="+methodSource2+"methodSink2="+methodSink2);
						sourceMethods.add(methodSource2);
						sourceStmts.add(methodSource2 +" - "+midStr1);
					}
					if (dtUtil.isIncludeSetItem(midStr1, sinks))	{	
						System.out.println("adding sink methodSource2="+methodSource2+"methodSink2="+methodSink2);
						sinkMethods.add(methodSource2);
						sinkStmts.add(methodSource2 +" - "+midStr1);
					}		
				}

			}	
			
		}
//		System.out.println("sourceFile="+sourceFile);
////		System.out.println("sourceMsg="+sourceMsg);
//		dtConfig.writeMessage(sourceFile, sourceMsg);
//		System.out.println("sinkFile="+sinkFile);
////		System.out.println("sinkMsg="+sinkMsg);
//		dtConfig.writeMessage(sinkFile, sinkMsg);
		HashSet<String> sourceSet= new HashSet<String>();
		sourceSet.addAll(sourceMethods);
//		System.out.println("sourceMethods="+sourceMethods);
//		System.out.println("sourceSet="+sourceSet);
		dtUtil.writeSet(sourceSet, sourceFile);
		
		HashSet<String> sinkSet= new HashSet<String>();
		sinkSet.addAll(sinkMethods);
//		System.out.println("sinkMethods="+sinkMethods);
//		System.out.println("sinkSet="+sinkSet);
		dtUtil.writeSet(sinkSet, sinkFile);
			
		
		
    	String methodPairFile=System.getProperty("user.dir") + File.separator + "sourceSink3MethodPair.txt";
    	String stmtPairFile=System.getProperty("user.dir") + File.separator + "sourceSink3StmtPair.txt";
    	String methodPairDiffFile=System.getProperty("user.dir") + File.separator + "sourceSink3MethodPairDiffClass.txt";
    	String stmtPairDiffFile=System.getProperty("user.dir") + File.separator + "sourceSink3StmtPairDiffClass.txt";
    	System.out.println("Write pair file: "+methodPairFile);
    	dtConfig.partCaetesianTwoArraysToFile(sourceMethods,sinkMethods,methodPairFile,"method");
    	System.out.println("Write pair file: "+methodPairDiffFile);
    	dtConfig.partCaetesianTwoArraysToFile(sourceMethods,sinkMethods,methodPairDiffFile,"methodDiff");
    	System.out.println("Write pair file: "+stmtPairFile);
    	dtConfig.partCaetesianTwoArraysToFile(sourceStmts,sinkStmts,stmtPairFile,"stmt");       
    	System.out.println("Write pair file: "+stmtPairDiffFile);
    	dtConfig.partCaetesianTwoArraysToFile(sourceStmts,sinkStmts,stmtPairDiffFile,"stmtDiff");     
 

		HashSet<String> sourceStmtSet= new HashSet<String>();
		sourceStmtSet.addAll(sourceStmts);
//		System.out.println("sourceMethods="+sourceMethods);
//		System.out.println("sourceSet="+sourceSet);
		dtUtil.writeSet(sourceStmtSet, System.getProperty("user.dir") + File.separator + "sourceStmts_1.txt");
		
		HashSet<String> sinkStmtSet= new HashSet<String>();
		sinkStmtSet.addAll(sinkStmts);
//		System.out.println("sinkMethods="+sinkMethods);
//		System.out.println("sinkSet="+sinkSet);
		dtUtil.writeSet(sinkStmtSet, System.getProperty("user.dir") + File.separator + "sinkStmts_1.txt");
	}
 
	// soot option 1
	private static void initial(String classPath) {
		soot.G.reset();
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_process_dir(Collections.singletonList(classPath));//
		Options.v().set_whole_program(true);
		Scene.v().loadNecessaryClasses();
		
	}
	
	// soot option 2
    private static void enableSpark(String path){
        HashMap opt = new HashMap();
        //opt.put("verbose","true");
        //opt.put("propagator","worklist");
        opt.put("simple-edges-bidirectional","false");
        //opt.put("on-fly-cg","true");
        opt.put("apponly", "true");
//        opt.put("set-impl","double");
//        opt.put("double-set-old","hybrid");
//        opt.put("double-set-new","hybrid");
//        opt.put("allow-phantom-refs", "true");
        opt.put("-process-dir",path);
        
        SparkTransformer.v().transform("",opt);
    }

	public static String getHierarchy(SootClass cls, Set<SootClass> sourceOrSinks) {
		Hierarchy har = Scene.v().getActiveHierarchy();	
		for (SootClass scls : sourceOrSinks) {
			//System.out.println("The sourceSink class is :" + scls.getName());
			if (!scls.isInterface())   { continue; }
			//List<SootClass> sclsSub=har.getSubinterfacesOf(scls);
			if (har.getSubinterfacesOf(scls).contains(cls)) {
				return scls.toString();
			}
			//List<SootClass> sclsImpl=har.getImplementersOf(scls);
			if (har.getImplementersOf(scls).contains(cls)) {
				return scls.toString();
			}
		}
		return "";
	}

}