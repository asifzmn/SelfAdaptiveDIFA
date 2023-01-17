package FLOWDIST;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import dua.util.Util;
import soot.SootMethod;

public class dtOTUtil {
//	static StaticTransferGraph dvtg = new StaticTransferGraph();
//	static StaticTransferGraph dvtgExercised = new StaticTransferGraph();
//	static StaticTransferGraph dvtgPruned= new StaticTransferGraph();
	public static void main(String []args) {
		//System.out.println("Util.getCreateBaseOutPath()="+Util.getCreateBaseOutPath());
		
//		String queryFile = args[0]; // tell the changed methods, separated by comma if there are more than one
//		String traceDir = args[1]; // tell the directory where execution traces can be accessed
//		String binDir = args[2]; // tell the directory where the static value transfer graph binary can be found
//        File file1 = new File(binDir+File.separator+"staticVtg.dat");
//        if (file1.exists()) {
//        	sg.setSVTG(binDir+File.separator+"staticVtg.dat");
//        }
//        else
//        	sg.setSVTG("staticVtg.dat");
//		sg.setSVTG("C:/Research/nioecho/staticVtg.dat");
//		sg.initializeGraph(false);
//		
		HashSet<String> hs0 = new HashSet<String>();
		hs0.add("<NioServer: void run()>");
//		hs0.add("<RspHandler: void waitForResponse()>");
//		hs0.add("<RspHandler: void <init>()>");
        //HashSet hs=getPrevMethodStrs(dvtg, "<RspHandler: void waitForResponse()>");
//        HashSet hs=getAllPrevMethodStrs(dvtg, hs0);
//        System.out.println("hs.size()="+hs.size()+" hs="+hs);
        
        StaticTransferGraph svtg = new StaticTransferGraph();
        svtg.DeserializeFromFile("C:/Research/nioecho/staticVtg.dat");
    	/** a map from method signature to index for the underlying static VTG */
//    	/*static*/ HashMap< String, Integer > method2idx = new HashMap< String, Integer>();
//    	/** a map from index to method signature for the underlying static VTG */
//    	/*static*/ HashMap<Integer, String> idx2method = new HashMap<Integer, String>();
//    	int index = 0;
//		for (SVTNode sn : svtg.nodeSet()) {
//			String mname = sn.getMethod().getName();
//			if (!method2idx.containsKey(mname)) {
//				method2idx.put(mname, index);
//				idx2method.put(index, mname);
//				index ++;
//			}
//		}
		//System.out.println("idx2method.size()="+idx2method.size()+" idx2method="+idx2method);
        HashSet<String> hs=getAllPrevMethodStrs(svtg, hs0);
        System.out.println("hs.size()="+hs.size()+" hs="+hs);
	}
	
	public static HashSet<SVTNode> getNodes(StaticTransferGraph sg, String method1) {		
		HashSet<SVTNode> hs=new HashSet();
		try {  
			String midMethod="";
			String midSign="";
			//String midStmt="";
//			String str1="";
//			String str2="";
			for (SVTNode dn: sg.nodeSet()) {
				//System.out.println("dn.getMethod()="+dn.getMethod()+" dn.getStmt()="+dn.getStmt());
				
				midMethod=dn.getMethod().getName();
				midSign=dn.getMethod().getSignature();
				//System.out.println("midMethod="+midMethod+" dn.getMethod()="+dn.getMethod());
				if (method1.equals(midSign) || method1.equals(midMethod))
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
	
	public static HashSet<SootMethod> getNextMethods(StaticTransferGraph sg, String method1) {	
		HashSet hs = new HashSet();
		try {
			String midMethod="";
			String midSign="";
			String targetMethod="";
			String targetSign="";
			//String targetMethod="";
			for (SVTEdge de : sg.edgeSet()) {
				//System.out.println("dn="+dn+" de.getTarget()="+de.getTarget());
				midMethod=de.getSource().getMethod().getName();
				midSign=de.getSource().getMethod().getSignature();	
				//System.out.println("midMethod="+midMethod+" dn.getMethod()="+dn.getMethod());
				if (method1.equals(midSign) || method1.equals(midMethod))
				{
					targetMethod=de.getTarget().getMethod().getName();
					targetSign=de.getTarget().getMethod().getSignature();
					if (targetMethod.startsWith("<") && targetMethod.endsWith(">") && targetMethod.indexOf(": ")>1) {
						hs.add(targetMethod);
					}	
					else 
						hs.add(targetSign);
					//hs.add(de.getTarget().getMethod());
				}
			}
		}
		catch (Exception e) { 
			System.err.println("Exception e="+e);
		}
		return hs;
	}

	public static HashSet<SootMethod> getNextMethods(StaticTransferGraph sg, HashSet<String> methods) {	
		HashSet hs = new HashSet();
		try {
			String midMethod="";
			String midSign="";
			String targetMethod="";
			String targetSign="";
			//String targetMethod="";
			for (SVTEdge de : sg.edgeSet()) {
				//System.out.println("dn="+dn+" de.getTarget()="+de.getTarget());
				midMethod=de.getSource().getMethod().getName();
				midSign=de.getSource().getMethod().getSignature();
				if (methods.contains(midMethod) || methods.contains(midSign))
				{
					targetMethod=de.getTarget().getMethod().getName();
					targetSign=de.getTarget().getMethod().getSignature();
					if (targetMethod.startsWith("<") && targetMethod.endsWith(">") && targetMethod.indexOf(": ")>1) {
						hs.add(targetMethod);
					}	
					else 
						hs.add(targetSign);
					//hs.add(de.getTarget().getMethod());
				}
			}
		}
		catch (Exception e) { 
			System.err.println("Exception e="+e);
		}
		return hs;
	}
	public static HashSet<String> getNextMethodStrs(StaticTransferGraph sg, String method1) {	
		HashSet<String> hs = new HashSet<String>();
		try {
			String midMethod="";
			String midSign="";
			String targetMethod="";
			String targetSign="";
			//String targetMethod="";
			for (SVTEdge de : sg.edgeSet()) {
				//System.out.println("dn="+dn+" de.getTarget()="+de.getTarget());
				midMethod=de.getSource().getMethod().getName();
				midSign=de.getSource().getMethod().getSignature();				
				//System.out.println("midMethod="+midMethod);
				if (method1.equals(midMethod) || method1.equals(midSign))
				{
					targetMethod=de.getTarget().getMethod().getName();
					targetSign=de.getTarget().getMethod().getSignature();
					if (targetMethod.startsWith("<") && targetMethod.endsWith(">") && targetMethod.indexOf(": ")>1) {
						hs.add(targetMethod);
					}	
					else 
						hs.add(targetSign);
					//hs.add(de.getTarget().getMethod().getName());
				}
			}
		}
		catch (Exception e) { 
			System.err.println("Exception e="+e);
		}
		return hs;
	}

	public static HashSet<String> getNextMethodStrs(StaticTransferGraph sg, HashSet<String> methods) {	
		HashSet<String> hs = new HashSet<String>();
		//try {
			String midMethod="";
			String midSign="";
			String targetMethod="";
			String targetSign="";
			//System.out.println("NextMethodStrs sg.edgeSet().size()="+sg.edgeSet().size());
			//String targetMethod="";
			for (SVTEdge de : sg.edgeSet()) {
				//System.out.println("dn="+dn+" de.getTarget()="+de.getTarget());
				midMethod=de.getSource().getMethod().getName();
				
				//System.out.println("NextMethodStrs de.getSource().getMethod()="+de.getSource().getMethod()+" midMethod="+midMethod+" de.getSource().getMethod().getSignature()="+de.getSource().getMethod().getSignature());
				//System.out.println("midMethod="+midMethod);
				midSign=de.getSource().getMethod().getSignature();
				if (methods.contains(midMethod) || methods.contains(midSign))
				{
					targetMethod=de.getTarget().getMethod().getName();
					targetSign=de.getTarget().getMethod().getSignature();
					if (targetMethod.startsWith("<") && targetMethod.endsWith(">") && targetMethod.indexOf(": ")>1) {
						hs.add(targetMethod);
					}	
					else 
						hs.add(targetSign);
					//hs.add(de.getTarget().getMethod().getName());
				}
			}
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
		return hs;
	}
	
	public static HashSet<String> getAllNextMethodStrs(StaticTransferGraph sg, String method1) {	
		HashSet<String> hs = new HashSet<String>();
		boolean sizeIncremented=true;

		hs.add(method1);
		while (sizeIncremented) {
			HashSet<String> hs2 = new HashSet<String>();
			hs2=(HashSet<String>) hs.clone();
			for (String m: hs2) {
				hs.addAll(getNextMethodStrs(sg,m));
			}
			if (hs.size()==hs2.size())
				sizeIncremented=false;
		}		
		return hs;
	}
	
	public static HashSet<String> getAllNextMethodStrs(StaticTransferGraph sg, HashSet<String> methods) {	
		HashSet<String> hs = new HashSet<String>();
		boolean sizeIncremented=true;

		hs.addAll(methods);
		while (sizeIncremented) {
			HashSet<String> hs2 = new HashSet<String>();
			hs2=(HashSet<String>) hs.clone();
			hs.addAll(getNextMethodStrs(sg,hs2));
//			for (String m: hs2) {
//				hs.addAll(getNextMethodStrs(dg,hs2));
//			}
			if (hs.size()==hs2.size())
				sizeIncremented=false;
		}		
		return hs;
	}
	

	public static HashSet<SootMethod> getPrevMethods(StaticTransferGraph sg, String method1) {	
		HashSet hs = new HashSet();
		try {
			String midMethod="";
			String midSign="";
			String sourceMethod="";
			String sourceSign="";
			//String targetMethod="";
			for (SVTEdge de : sg.edgeSet()) {
				//System.out.println("dn="+dn+" de.getTarget()="+de.getTarget());
				midMethod=de.getTarget().getMethod().getName();
				midSign=de.getTarget().getMethod().getSignature();				
				//System.out.println("midMethod="+midMethod);
				if (method1.equals(midMethod) || method1.equals(midSign))
				{
					sourceMethod=de.getSource().getMethod().getName();
					sourceSign=de.getSource().getMethod().getSignature();
					if (sourceMethod.startsWith("<") && sourceMethod.endsWith(">") && sourceMethod.indexOf(": ")>1) {
						hs.add(sourceMethod);
					}	
					else 
						hs.add(sourceSign);
					//hs.add(de.getSource().getMethod());
				}
			}
		}
		catch (Exception e) { 
			System.err.println("Exception e="+e);
		}
		return hs;
	}

	public static HashSet<String> getPrevMethodStrs(StaticTransferGraph sg, String method1) {	
		HashSet<String> hs = new HashSet<String>();
		try {
			String midMethod="";
			String midSign="";
			String sourceMethod="";
			String sourceSign="";
			//String targetMethod="";
			for (SVTEdge de : sg.edgeSet()) {
				//System.out.println("dn="+dn+" de.getTarget()="+de.getTarget());
				midMethod=de.getTarget().getMethod().getName();
				midSign=de.getTarget().getMethod().getSignature();				
				//System.out.println("midMethod="+midMethod);
				if (method1.equals(midMethod) || method1.equals(midSign))
				{
					sourceMethod=de.getSource().getMethod().getName();
					sourceSign=de.getSource().getMethod().getSignature();
					if (sourceMethod.startsWith("<") && sourceMethod.endsWith(">") && sourceMethod.indexOf(": ")>1) {
						hs.add(sourceMethod);
					}	
					else 
						hs.add(sourceSign);
					//hs.add(de.getSource().getMethod().getName());
				}
			}
		}
		catch (Exception e) { 
			System.err.println("Exception e="+e);
		}
		return hs;
	}

	public static HashSet<String> getPrevMethodStrs(StaticTransferGraph sg, HashSet<String> methods) {	
		HashSet<String> hs = new HashSet<String>();
		try {
			String midMethod="";
			String midSign="";
			String sourceMethod="";
			String sourceSign="";
			//String targetMethod="";
			//System.out.println("getPrevMethodStrs sg.edgeSet().size()="+sg.edgeSet().size());
			for (SVTEdge de : sg.edgeSet()) {
				//System.out.println("dn="+dn+" de.getTarget()="+de.getTarget());
				midMethod=de.getTarget().getMethod().getName();
				midSign=de.getTarget().getMethod().getSignature();	
				//System.out.println("getPrevMethodStrs midMethod="+midMethod+"  midSign="+midSign);
				if (methods.contains(midMethod) || methods.contains(midSign))
				{
					sourceMethod=de.getSource().getMethod().getName();
					sourceSign=de.getSource().getMethod().getSignature();
					if (sourceMethod.startsWith("<") && sourceMethod.endsWith(">") && sourceMethod.indexOf(": ")>1) {
						hs.add(sourceMethod);
					}	
					else 
						hs.add(sourceSign);
					//hs.add(de.getSource().getMethod().getName());
				}
			}
		}
		catch (Exception e) { 
			System.err.println("Exception e="+e);
		}
		return hs;
	}
	public static HashSet<String> getAllPrevMethodStrs(StaticTransferGraph sg, String method1) {	
		HashSet<String> hs = new HashSet<String>();
		boolean sizeIncremented=true;

		hs.add(method1);
		while (sizeIncremented) {
			HashSet<String> hs2 = new HashSet<String>();
			hs2=(HashSet<String>) hs.clone();
			for (String m: hs2) {
				hs.addAll(getPrevMethodStrs(sg,m));
			}
			if (hs.size()==hs2.size())
				sizeIncremented=false;
		}		
		return hs;
	}
	public static HashSet<String> getAllPrevMethodStrs(StaticTransferGraph sg, HashSet<String> methods) {	
		HashSet<String> hs = new HashSet<String>();
		boolean sizeIncremented=true;

		hs.addAll(methods);
		while (sizeIncremented) {
			HashSet<String> hs2 = new HashSet<String>();
			hs2=(HashSet<String>) hs.clone();
			hs.addAll(getPrevMethodStrs(sg,hs2));
//			for (String m: hs2) {
//				hs.addAll(getPrevMethodStrs(dg,m));
//			}
			if (hs.size()==hs2.size())
				sizeIncremented=false;
		}		
		return hs;
	}
}
