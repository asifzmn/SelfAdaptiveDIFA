package disttaint;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

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



public class dt2SourceSink3 {

	 static boolean debugOut = false;
	 static boolean dynMethodFilter = false;
	 static boolean dynStmtFilter = false;
	 static 	ArrayList  sourceMethods = new ArrayList();
	 static 	ArrayList  sinkMethods = new ArrayList();
	 static 	ArrayList  sourceStmts = new ArrayList();
	 static 	ArrayList  sinkStmts = new ArrayList();
	 public static void main(String []args) {
		String traceDir = args[0]; // tell the directory where execution traces can be accessed
		String binDir = args[1]; 
		if (args.length > 2) {
			dynMethodFilter  = args[2].startsWith("-method");
		} 
		if (args.length > 3) {
			dynStmtFilter  = args[3].startsWith("-stmt") || args[3].startsWith("-statement");
		} 
		if (args.length > 4) {
			debugOut = args[4].equalsIgnoreCase("-debug");
		}
		//String binDir ="C:/TEST/DT2Instrumented"; 

        //static List<String> coveredStmtsStr = new ArrayList<String>();
        File file0 = new File(binDir + File.separator + "stmtids.out");
        //System.out.println("stmtids.out file0.exists()="+file0.exists());
        if (!file0.exists()) {
        	System.out.println("There is no file stmtids.out!");
        	return;
        }
        File file = new File(binDir + File.separator + "methodList.out");
        //System.out.println("methodList.out file.exists()="+file.exists());        
        if (dynMethodFilter && !file.exists()) {
        	System.out.println("There is no file methodList.out! No dynamic method checking!");
        	dynMethodFilter = false;
        }
        
        File file2 = new File(binDir + File.separator + "stmtCoverage1.out");
        //System.out.println("stmtCoverage1.out file2.exists()="+file2.exists());   
        if (dynStmtFilter && !file2.exists()) {
        	System.out.println("There is no file stmtCoverage1.out! No dynamic statement checking!");
        	dynStmtFilter = false;
        }

        String sourceFile=System.getProperty("user.dir") + File.separator + "source_2.txt";
        String sinkFile=System.getProperty("user.dir") + File.separator + "sink_2.txt";
        souceSinkFromStmtFile(binDir, dynMethodFilter, dynStmtFilter, sourceFile,sinkFile);
    	String methodPairFile=System.getProperty("user.dir") + File.separator + "sourceSinkMethodPair2.txt";
    	String stmtPairFile=System.getProperty("user.dir") + File.separator + "sourceSinkStmtPair2.txt";
    	String methodPairDiffFile=System.getProperty("user.dir") + File.separator + "sourceSinkMethodPairDiffClass2.txt";
    	String stmtPairDiffFile=System.getProperty("user.dir") + File.separator + "sourceSinkStmtPairDiffClass2.txt";
    	System.out.println("Write pair file: "+methodPairFile);
    	if (sourceMethods.size()<1) {
    		sourceMethods=dtUtil.getArrayListFromTwo(System.getProperty("user.dir") + File.separator + "coveredSourceMethods.txt", System.getProperty("user.dir") + File.separator + "methodList.out");
    		System.out.println("sourceMethods.size(): "+sourceMethods.size());
    	}	
    	if (sinkMethods.size()<1) {
    		sinkMethods=dtUtil.getArrayListFromTwo(System.getProperty("user.dir") + File.separator + "coveredSinkMethods.txt", System.getProperty("user.dir") + File.separator + "methodList.out");
    		System.out.println("sinkMethods.size(): "+sinkMethods.size());
    	}	
    	dtConfig.partCaetesianTwoArraysToFile(sourceMethods,sinkMethods,methodPairFile,"method");
    	System.out.println("Write pair file: "+methodPairDiffFile);
    	dtConfig.partCaetesianTwoArraysToFile(sourceMethods,sinkMethods,methodPairDiffFile,"methodDiff");
    	
    	if (sourceStmts.size()<1) {
    		sourceStmts=dtUtil.getArrayListFromTwo(System.getProperty("user.dir") + File.separator + "source2_1.txt", sourceFile);
    		System.out.println("sourceStmts.size(): "+sourceStmts.size());
    	}	
    	if (sinkStmts.size()<1) {
    		sinkStmts=dtUtil.getArrayListFromTwo(System.getProperty("user.dir") + File.separator + "sink2_1.txt", sinkFile);
    		System.out.println("sinkStmts.size(): "+sinkStmts.size());
    	}	
    	System.out.println("Write pair file: "+stmtPairFile);
    	dtConfig.partCaetesianTwoArraysToFile(sourceStmts,sinkStmts,stmtPairFile,"stmt");       
    	System.out.println("Write pair file: "+stmtPairDiffFile);
    	dtConfig.partCaetesianTwoArraysToFile(sourceStmts,sinkStmts,stmtPairDiffFile,"stmtDiff");     
	}

	   public static void souceSinkFromStmtFile(String binDir,boolean checkMethod, boolean checkStmt, String sourceFile, String sinkFile) {  	   	
		   String sourceMsg=""; 
           String sinkMsg="";
		   try {  
	    		//boolean debugOut = true;
	        	String midStr1="";
	        	String stmtAllStmt="";
//	        	String sourceItem="";
//	    		String sinkItem="";
//	    		String methodItem="";
	    		HashSet<String> sources = dtConfig.getFirstHashSet("data/Sources2.txt","\t"); 
	    		HashSet<String> sinks = dtConfig.getFirstHashSet("data/Sinks2.txt","\t"); 
	    		LinkedHashMap<String, String> sourceClassMethods=dtConfig.getClassesMethods("data/Sources2.txt", "\t");
	    		LinkedHashMap<String, String> sinkClassMethods=dtConfig.getClassesMethods("data/Sinks2.txt", "\t");
	    		ArrayList remoteMethods = new ArrayList();
	    		if (checkMethod) {
	    			remoteMethods=dtUtil.getArrayListFromTwo(System.getProperty("user.dir") + File.separator + "methodList.out",System.getProperty("user.dir") + File.separator + "coveredMethods.txt");
//	    	    	for (int i=0; i<remoteMethods.size(); i++)
//	    	    		System.out.println("remoteMethods["+i+"]="+remoteMethods.get(i));
	    			if (remoteMethods.size()<1)  {
	    				System.out.println("File methodList.out is empty! No dynamic method checking!");
	    				checkMethod=false;
	    			}	
	    		}
	    		//System.out.println("remoteMethods="+remoteMethods);
	    		HashMap stmtStrses=new HashMap<>();
	    		ArrayList stmtAll=new ArrayList();
	    		List<String> scs= new ArrayList<String>();	 
	        	stmtStrses=dtStmtUtil.getStmtStrsFromFile(binDir + File.separator + "stmtids.out");
	        	stmtAll=(ArrayList) stmtStrses.get(2);	
	        	if (debugOut)
	        		System.out.println("stmtAll.size()="+stmtAll.size());		
		    	if (checkStmt)  {	
		        	//scs= dtStmtUtil.readStmtCoverageStr(binDir, 1, stmtAll);
		    		scs= dtStmtUtil.readStmtCoverageStrFromTwo(binDir, 1, stmtAll);
        			if (debugOut) {
        				System.out.println("scs.size()="+scs.size());	
        				System.out.println("scs="+scs);
        			}	
	    			if (scs.size()<1)  {
	    				System.out.println("File stmtCoverage1.out is empty! No dynamic statement checking!");
	    				checkStmt=false;
	    			}	    				
	    		}
//		    	if (debugOut)
//		    		System.out.println("stmtAll="+stmtAll);
	    		//System.out.println("methodItem="+dtConfig.getMethodItem(sourceClassMethods, "java.util.Scanner","<A: java.lang.String input3()> - r2 = virtualinvoke r1.<java.util.Scanner: java.lang.String nextLine()>()", " | ",remoteMethods));
	        	String stmtAllMethod="";
	        	if (debugOut)
	        		System.out.println("checkStmt="+checkStmt+" sources.size()="+sources.size()+" sinks.size()="+sinks.size());	
	        	if (checkStmt)  {
	        		for (int i=0; i<scs.size();i++)  {	        		
		        		midStr1=scs.get(i).toString();	  
		        		if (midStr1.indexOf(" goto ")>=0 || midStr1.indexOf(" if ")>=0 || midStr1.indexOf("?")>=0 || midStr1.indexOf("(null)")>=0 || midStr1.indexOf("(\"")>=0 || midStr1.indexOf("\")")>=0)
		    			{
		    				continue;
		    			}
		        		stmtAllMethod=dtStmtUtil.getMethodFromStr(midStr1);
		        		if (checkMethod)  {
	//	        			if (debugOut)
	//	        				System.out.println("stmtAll["+i+"]="+stmtAll.get(i)+" stmtAllMethod="+stmtAllMethod);		
		        			if (!remoteMethods.contains(stmtAllMethod))  {
		        				continue;
		        			}
		        		}        		
		        		stmtAllStmt=dtStmtUtil.getStmtFromStr(midStr1);
		        		//System.out.println("midStr1="+midStr1+" stmtAllMethod="+stmtAllMethod+" stmtAllStmt="+stmtAllStmt);
		        		String sourceItem=dtUtil.includeSetItem(midStr1, sources);
		        		//System.out.println("sourceItem="+sourceItem);
						if (sourceItem.length()>0)	{	
	    					System.out.println("Source: "+midStr1+" :("+sourceItem+")");
	    					sourceMsg+="Source: "+midStr1+" :("+sourceItem+") \n";
	    					if (!sourceMethods.contains(stmtAllMethod))
	    						sourceMethods.add(stmtAllMethod);
	    					if (!sourceStmts.contains(midStr1))
	    						sourceStmts.add(midStr1);
						}
		        		String sinkItem=dtUtil.includeSetItem(midStr1, sinks);
		        		//System.out.println("sinkItem="+sinkItem);
						if (sinkItem.length()>0)	{	
	    					System.out.println("Sink: "+midStr1+" :("+sinkItem+")");
	    					sinkMsg+="Sink: "+midStr1+" :("+sinkItem+") \n";
	    					if (!sinkMethods.contains(stmtAllMethod))
	    						sinkMethods.add(stmtAllMethod);
	    					if (!sinkStmts.contains(midStr1))
	    						sinkStmts.add(midStr1);
						}
//		    			sourceItem= dtConfig.itemInList(stmtAllStmt,sources);
////	        			if (debugOut)
////	        				System.out.println("stmtAllStmt="+stmtAllStmt+" sourceItem="+sourceItem);	
//		    			if (sourceItem.length()>1)  // && !methodSource1.equals(methodSource2))
//		    			{
//		    				methodItem=dtConfig.getMethodItem(sourceClassMethods, sourceItem,stmtAllStmt, " | ");
//		        			if (debugOut)
//			    				System.out.println("i="+i+" getMethodItem1="+methodItem+" sourceItem="+sourceItem+" stmtAllStmt="+stmtAllStmt+ " indexOf="+midStr1.indexOf(methodItem));
//		    				if (methodItem.length()>1 && stmtAllStmt.indexOf(methodItem)>=0)  	    				{
//		    						
//			    					System.out.println("Source: "+midStr1+" :("+sourceItem+": "+methodItem+")");
//			    					sourceMsg+="Source: "+midStr1+" :("+sourceItem+": "+methodItem+") \n";
//			    					if (!sourceMethods.contains(stmtAllMethod))
//			    						sourceMethods.add(stmtAllMethod);
//			    					if (!sourceStmts.contains(midStr1))
//			    						sourceStmts.add(midStr1);
//		    				}
//		    			}
//		    			sinkItem= dtConfig.itemInList(stmtAllStmt,sinks);
//		    			{
//		    				methodItem=dtConfig.getMethodItem(sinkClassMethods, sinkItem,stmtAllStmt, " | ");
//		    				//System.out.println(" getMethodItem2="+methodItem+" midStr1="+midStr1+ " indexOf="+midStr1.indexOf(methodItem));
//		    				if (methodItem.length()>1 && midStr1.indexOf(methodItem)>=0)  
//		    				{
//	    						
//			    					System.out.println("Sink: "+midStr1+" :("+sinkItem+": "+methodItem+")");
//			    					sinkMsg+="Sink: "+midStr1+" :("+sinkItem+": "+methodItem+") \n";
//			    					if (!sinkMethods.contains(stmtAllMethod))
//			    						sinkMethods.add(stmtAllMethod);
//			    					if (!sinkStmts.contains(midStr1))
//			    						sinkStmts.add(midStr1);
//		    				}
//		    			} 
		        	}
	        		
	        	}
	        	else  {
	        		//System.out.println("stmtAll.size()="+stmtAll.size());
			        for (int i=0; i<stmtAll.size();i++)  {	        		
		        		midStr1=stmtAll.get(i).toString();	  
		        		if (midStr1.indexOf(" goto ")>=0 || midStr1.indexOf(" if ")>=0 || midStr1.indexOf("?")>=0 || midStr1.indexOf("(null)")>=0 || midStr1.indexOf("(\"")>=0 || midStr1.indexOf("\")")>=0)
		    			{
		    				continue;
		    			}
		        		stmtAllMethod=dtStmtUtil.getMethodFromStr(midStr1);
		        		if (checkMethod)  {
		        			
	//	        			if (debugOut)
	//	        				System.out.println("stmtAll["+i+"]="+stmtAll.get(i)+" stmtAllMethod="+stmtAllMethod);		
		        			if (!remoteMethods.contains(stmtAllMethod))  {
		        				continue;
		        			}
	//	        			if (i==257)
	//	        				System.out.println("stmtAll["+i+"]="+stmtAll.get(i)+" stmtAllMethod="+stmtAllMethod);	
		        		}  
		        		stmtAllStmt=dtStmtUtil.getStmtFromStr(midStr1);
		        		//System.out.println("midStr1="+midStr1+" stmtAllStmt="+stmtAllStmt);
		        		String sourceItem=dtUtil.includeSetItem(midStr1, sources);
		        		//System.out.println("sourceItem="+sourceItem);
						if (sourceItem.length()>0)	{	
	    					//System.out.println("Source: "+midStr1+" :("+sourceItem+")");
	    					sourceMsg+="Source: "+midStr1+" :("+sourceItem+") \n";
	    					if (!sourceMethods.contains(stmtAllMethod))
	    						sourceMethods.add(stmtAllMethod);
	    					if (!sourceStmts.contains(midStr1))
	    						sourceStmts.add(midStr1);
						}
		        		String sinkItem=dtUtil.includeSetItem(midStr1, sinks);
		        		//System.out.println("sinkItem="+sinkItem);
						if (sinkItem.length()>0)	{	
	    					//System.out.println("Sink: "+midStr1+" :("+sinkItem+")");
	    					sinkMsg+="Sink: "+midStr1+" :("+sinkItem+") \n";
	    					if (!sinkMethods.contains(stmtAllMethod))
	    						sinkMethods.add(stmtAllMethod);
	    					if (!sinkStmts.contains(midStr1))
	    						sinkStmts.add(midStr1);
						} 		
//		    			sourceItem= dtConfig.itemInList(stmtAllStmt,sources);
//	        			if (debugOut)
//	        				System.out.println("stmtAllStmt="+stmtAllStmt+" sourceItem="+sourceItem);	
//		    			if (sourceItem.length()>1)  // && !methodSource1.equals(methodSource2))
//		    			{
//		    				methodItem=dtConfig.getMethodItem(sourceClassMethods, sourceItem,stmtAllStmt, " | ");
//		        			if (debugOut)
//			    				System.out.println(" getMethodItem1="+methodItem+" sourceItem="+sourceItem+" stmtAllStmt="+stmtAllStmt+ " indexOf="+midStr1.indexOf(methodItem));
//		    				if (methodItem.length()>1 && stmtAllStmt.indexOf(methodItem)>=0)  	    				{
//			    					System.out.println("Source: "+midStr1+" :("+sourceItem+": "+methodItem+")");
//			    					sourceMsg+="Source: "+midStr1+" :("+sourceItem+": "+methodItem+") \n";
//			    					if (!sourceMethods.contains(stmtAllMethod))
//			    						sourceMethods.add(stmtAllMethod);
//			    					if (!sourceStmts.contains(midStr1))
//			    						sourceStmts.add(midStr1);
//		    				}
//		    			}
//		    			sinkItem= dtConfig.itemInList(stmtAllStmt,sinks);
//		    			{
//		    				methodItem=dtConfig.getMethodItem(sinkClassMethods, sinkItem,stmtAllStmt, " | ");
//		    				//System.out.println(" getMethodItem2="+methodItem+" midStr1="+midStr1+ " indexOf="+midStr1.indexOf(methodItem));
//		    				if (methodItem.length()>1 && midStr1.indexOf(methodItem)>=0)  
//		    				{
//	    						
//			    					System.out.println("Sink: "+midStr1+" :("+sinkItem+": "+methodItem+")");
//			    					sinkMsg+="Sink: "+midStr1+" :("+sinkItem+": "+methodItem+") \n";
//			    					if (!sinkMethods.contains(stmtAllMethod))
//			    						sinkMethods.add(stmtAllMethod);
//			    					if (!sinkStmts.contains(midStr1))
//			    						sinkStmts.add(midStr1);
//		    				}
//		    			} 
		        	}
	        	}	
	       } catch (Exception e) {  
	           e.printStackTrace();
	       }  
		   
		   
		   dtConfig.writeMessage(sourceFile, sourceMsg);
		   dtConfig.writeMessage(sinkFile, sinkMsg);
	   } 
}