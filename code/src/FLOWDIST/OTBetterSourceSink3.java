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

public class OTBetterSourceSink3 {
	public static String path = ""; //"C:/Research/nioecho/bin";  ///voldemort/rest/coordinator/admin
	static boolean debugOut = true;
	public static void main(String args[]) {
      if(args.length == 0)
      {
          System.out.println("Usage: OTBetterSourceSink directory");
          System.exit(0);
      }            
      else
			System.out.println("[mainClass]"+args[0]);	
      path = args[0]; 
		
		HashSet<String> sources = new HashSet<>(); 
		HashSet<String> sinks = new HashSet<>(); 
		LinkedHashMap<String, String> sourceClassMethods=new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> sinkClassMethods=new LinkedHashMap<String, String>();
		ArrayList  sourceMethods = new ArrayList();
		ArrayList  sinkMethods = new ArrayList();
		ArrayList  sourceStmts = new ArrayList();
		ArrayList  sinkStmts = new ArrayList();
//		TreeSet<String> sourceMethods = new TreeSet<>(); 
//		TreeSet<String> sinkMethods = new TreeSet<>(); 		
//	    String sourceMethodMsg=""; 
//		String sinkMethodMsg="";
		Set<String> coveredSourceMethods = new HashSet<String>();
		Set<String> coveredSinkMethods = new HashSet<String>();
		String sourceFile=System.getProperty("user.dir") + File.separator + "source2_1.txt";
		String sinkFile=System.getProperty("user.dir") + File.separator + "sink2_1.txt"; 
		// set soot options 1 and 2
		initial(path);
		enableSpark(path);
   	 	sources=dtConfig.getFirstHashSet("data/Sources.txt","\t"); 
//   	 	for (String s:sources) 
//   	 	{
//   	 		System.out.println("sources=" +s);
//   	 	}
   	 		
   	    Set<SootClass> sourceSootClses = new HashSet<SootClass>();
		for (String clsname : sources) {
			sourceSootClses.add( Scene.v().getSootClass(clsname) );
		}
		
		sinks=dtConfig.getFirstHashSet("data/Sinks.txt","\t"); 
     	Set<SootClass> sinkSootClses = new HashSet<SootClass>();
		for (String clsname : sinks) {
			sinkSootClses.add( Scene.v().getSootClass(clsname) );
		}
     	
   	    sourceClassMethods=dtConfig.getClassesMethods("data/Sources.txt", "\t");
   	    sinkClassMethods=dtConfig.getClassesMethods("data/Sinks.txt", "\t");
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
			String mgetSignature="";
			for(SootMethod m:sClass.getMethods())
			{
				if(!m.isConcrete()) {	continue; }
				
				
				methodSource2=m.toString();
				methodSink2=methodSource2;			
				
				mgetSignature=m.getSignature();
				//System.out.println("m.getSignature()=" + mgetSignature);
				if (mgetSignature.equals("<java.net.Socket: java.io.InputStream getInputStream()>")) {
					System.out.println("coveredSourceMethods.getSignature()=" + mgetSignature);
					coveredSourceMethods.add(methodSource2);
				}
				else if (mgetSignature.equals("<java.net.Socket: java.io.OutputStream getOutputStream()>")) {
					System.out.println("coveredSinkMethods.getSignature()=" + mgetSignature);
					coveredSinkMethods.add(methodSource2);
				}	else if (mgetSignature.equals("<java.io.ObjectInputStream: java.lang.Object readObject()>")) {
					System.out.println("coveredSourceMethods.getSignature()=" + mgetSignature);
					coveredSourceMethods.add(methodSource2);
				}
				else if (mgetSignature.equals("<java.io.ObjectOutputStream: void writeObject(java.lang.Object)>")) {
					System.out.println("coveredSinkMethods.getSignature()=" + mgetSignature);
					coveredSinkMethods.add(methodSource2);
				}	
				
				//System.out.println("m.getDeclaringClass().getName()=" + m.getDeclaringClass().getName());
				if (m.getDeclaringClass().getName().equals("java.nio.channels.SocketChannel")) {
					//System.out.println("m.getName()=" + m.getName());
					if (m.getName().equals("read")) {
						coveredSourceMethods.add(methodSource2);
					}
					else if (m.getName().equals("write")) {
						coveredSinkMethods.add(methodSource2);
					}
						
				}

				
				//System.out.println("methodSource2="+methodSource2+"methodSource2.indexOf(sourceClassHierarchy)="+methodSource2.indexOf(sourceClassHierarchy));
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

					//System.out.println("methodSource2="+methodSource2+"midStr1="+midStr1);
					if (midStr1.indexOf(" goto ")>=0 || midStr1.indexOf(" if ")>=0)  // || midStr1.indexOf("?")>=0 || midStr1.indexOf("(null)")>=0 || midStr1.indexOf("(\"")>=0 || midStr1.indexOf("\")")>=0)
					{
						continue;
					}

					if (dtUtil.isIncludeSetItem(midStr1, sources))	{	
						System.out.println("adding source methodSource2="+methodSource2+"methodSink2="+methodSink2);
						sourceMethods.add(methodSource2);
						sourceStmts.add(methodSource2 +" - "+midStr1);
						coveredSourceMethods.add(methodSource2);
					}
					if (dtUtil.isIncludeSetItem(midStr1, sinks))	{	
						System.out.println("adding sink methodSource2="+methodSource2+"methodSink2="+methodSink2);
						sinkMethods.add(methodSource2);
						sinkStmts.add(methodSource2 +" - "+midStr1);
						coveredSinkMethods.add(methodSource2);
					}
//					if (sourceClassHierarchy.length()>0)
//					{
//						methodItem=dtConfig.getMethodItem(sourceClassMethods, sourceClassHierarchy,methodSource2, " | "); 
//						if (methodItem.length()>1)  
//						{
//							System.out.println("Source: "+methodSource2 +" - "+midStr1+" :("+sourceClassHierarchy+": "+methodItem+")");
//							sourceMsg+="Source: "+methodSource2 +" - "+midStr1+" :("+sourceClassHierarchy+": "+methodItem+") \n";
//	    					if (!sourceMethods.contains(methodSource2))
//	    						sourceMethods.add(methodSource2);
//	    					if (!sourceStmts.contains(methodSource2 +" - "+midStr1))
//	    						sourceStmts.add(methodSource2 +" - "+midStr1);
//	    					coveredSourceMethods.add(methodSource2);
//	    					break;
//						}						
//					}
//					sourceItem= dtConfig.itemInList(midStr1,sources);			
//					//System.out.println("midStr1="+midStr1+" sourceItem="+sourceItem+" methodSource2="+methodSource2);
//					//System.out.println(" getMethodItem1="+dtConfig.getMethodItem(sourceClassMethods, sourceItem,midStr1, " | "));
//					if (sourceItem.length()>1)  // && !methodSource1.equals(methodSource2))
//					{
//						
//						methodItem=dtConfig.getMethodItem(sourceClassMethods, sourceItem,midStr1, " | ");
//						//System.out.println(" getMethodItem1="+methodItem+" midStr1="+midStr1+ " indexOf="+midStr1.indexOf(methodItem));
//						if (methodItem.length()>1 && midStr1.indexOf(methodItem)>=0)  
//						{
//							System.out.println("Source: "+methodSource2 +" - "+midStr1+" :("+sourceItem+": "+methodItem+")");
//							sourceMsg+="Source: "+methodSource2 +" - "+midStr1+" :("+sourceItem+": "+methodItem+") \n";
//	    					if (!sourceMethods.contains(methodSource2))
//	    						sourceMethods.add(methodSource2);
//	    					if (!sourceStmts.contains(methodSource2 +" - "+midStr1))
//	    						sourceStmts.add(methodSource2 +" - "+midStr1);
//	    					coveredSourceMethods.add(methodSource2);
//	    					
//						}
//					}		
//					if (sinkClassHierarchy.length()>0)
//					{
//						methodItem=dtConfig.getMethodItem(sinkClassMethods, sinkClassHierarchy,methodSink2, " | "); 
//						if (methodItem.length()>1)  
//						{
//							System.out.println("Sink: "+methodSink2 +" - "+midStr1+" :("+sinkClassHierarchy+": "+methodItem+")");
//							sinkMsg+="Sink: "+methodSink2 +" - "+midStr1+" :("+sinkClassHierarchy+": "+methodItem+") \n";
//	    					if (!sinkMethods.contains(methodSink2))
//	    						sinkMethods.add(methodSink2);
//	    					if (!sinkStmts.contains(methodSink2 +" - "+midStr1))
//	    						sinkStmts.add(methodSink2 +" - "+midStr1);
//	    					coveredSinkMethods.add(methodSink2);
//	    					break;
//						}						
//					}
//					sinkItem= dtConfig.itemInList(midStr1,sinks);
//					//System.out.println("sinkItem="+sinkItem+" methodSink1="+methodSink1+" methodSink2="+methodSink2);
//					if (sinkItem.length()>1)  // && !methodSink1.equals(methodSink2))
//					{
//						methodItem=dtConfig.getMethodItem(sinkClassMethods, sinkItem,midStr1, " | ");
//						//System.out.println(" getMethodItem2="+methodItem+" midStr1="+midStr1+ " indexOf="+midStr1.indexOf(methodItem));
//						if (methodItem.length()>1 && midStr1.indexOf(methodItem)>=0)  
//						{
//							System.out.println("Sink: "+methodSink2 +" - "+midStr1+" :("+sinkItem+": "+methodItem+")");
//							sinkMsg+="Sink: "+methodSink2 +" - "+midStr1+" :("+sinkItem+": "+methodItem+") \n";
//							//System.out.println("sinkMsg="+sinkMsg);
//							//sinkMethods.add(methodSink2+" ("+sinkItem+") in "+midStr1);
//							//methodSink1=methodSink2;
//							//sinkMethods.add(methodSink2);				
//							//sinkStmts.add(methodSink2 +" - "+midStr1);
//	    					if (!sinkMethods.contains(methodSink2))
//	    						sinkMethods.add(methodSink2);
//	    					if (!sinkStmts.contains(methodSink2 +" - "+midStr1))
//	    						sinkStmts.add(methodSink2 +" - "+midStr1);
//	    					coveredSinkMethods.add(methodSink2);
//
//						}
//					} 
					
					
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
		
		System.out.println("coveredSourceMethods().size()=" + coveredSourceMethods.size());
		System.out.println("coveredSinkMethods().size()=" + coveredSinkMethods.size());
		dtConfig.writeSet(System.getProperty("user.dir") + File.separator + "coveredSourceMethods.txt", coveredSourceMethods);
		dtConfig.writeSet(System.getProperty("user.dir") + File.separator + "coveredSinkMethods.txt", coveredSinkMethods);
		
    	String methodPairFile=System.getProperty("user.dir") + File.separator + "sourceSinkMethodPair.txt";
    	String stmtPairFile=System.getProperty("user.dir") + File.separator + "sourceSinkStmtPair.txt";
    	String methodPairDiffFile=System.getProperty("user.dir") + File.separator + "sourceSinkMethodPairDiffClass.txt";
    	String stmtPairDiffFile=System.getProperty("user.dir") + File.separator + "sourceSinkStmtPairDiffClass.txt";
    	System.out.println("Write pair file: "+methodPairFile);
    	dtConfig.partCaetesianTwoArraysToFile(sourceMethods,sinkMethods,methodPairFile,"method");
    	System.out.println("Write pair file: "+methodPairDiffFile);
    	dtConfig.partCaetesianTwoArraysToFile(sourceMethods,sinkMethods,methodPairDiffFile,"methodDiff");
    	System.out.println("Write pair file: "+stmtPairFile);
    	dtConfig.partCaetesianTwoArraysToFile(sourceStmts,sinkStmts,stmtPairFile,"stmt");       
    	System.out.println("Write pair file: "+stmtPairDiffFile);
    	dtConfig.partCaetesianTwoArraysToFile(sourceStmts,sinkStmts,stmtPairDiffFile,"stmtDiff");     
	}
 
//	private static HashSet<String> coveredSourceMethods() {
//		// TODO Auto-generated method stub
//		return null;
//	}

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