/**
 * File: src/ODD/ODDUtil.java
 * -------------------------------------------------------------------------------------------
 * Date			Author      Changes
 * -------------------------------------------------------------------------------------------
 * 03/21/2019	Developer		created; for DistODD tools
*/
package ODD;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ODDUtil {
	private static final String ManagementFactory = null;
	/** a map from method signature to index for the underlying static VTG */
	protected static Map< String, Integer > method2idx;
	/** a map from index to method signature for the underlying static VTG */
	protected static Map< Integer, String > idx2method;
	static String staticConfigurations="";
	static String dynamicConfigurations="";
	static String configurations="123456";
	static String mazeFileName= "C:/tp/Maze123456.txt";
	public static void main(String []args) {
//		System.out.println("g1="+getQueryFromImpactsetStr("==== DistODD impact set of [<NioServer: void main(java.lang.String[])>]  size=4 ==== ;<NioServer: void <init>(java.net.InetAddress,int,EchoWorker)> ; <NioServer: java.nio.channels.Selector initSelector()> ; <EchoWorker: void <init>()> ; <NioServer: void main(java.lang.String[])> ;",";"));
//		HashSet<String> resultSet = getSetFromImpactsetStr("==== DistODD impact set of [<NioServer: void main(java.lang.String[])>]  size=4 ==== ;<NioServer: void <init>(java.net.InetAddress,int,EchoWorker)> ; <NioServer: java.nio.channels.Selector initSelector()> ; <EchoWorker: void <init>()> ; <NioServer: void main(java.lang.String[])> ;", ";");
//		System.out.println("resultSet = "+resultSet+" resultSet.size() = "+resultSet.size());
//		printImpactSet("<NioServer: void main(java.lang.String[])>", resultSet);
		//HashMap<String,HashSet<String>> resultSet = new HashMap<String,HashSet<String>>();
		//		getHashMapFromFile("C:/Temp/allResult.txt");
		//System.out.println("resultSet = "+resultSet+" resultSet.size() = "+resultSet.size());
		//printImpactSetHashMap(resultSet);  getInter(String oldStr) 
		//clientOutputAll("C:/Temp/allResult.txt");
		//System.out.println("getInter(String oldStr) = "+getInter("<NioServer: void run()>"));
		// writeStaticDynamicTimeOutsToFile(360000, 360000, "C:/Temp/timeouts.txt");
//		Long l1=readTimeOutFromFile("Dynamic TimeOut:", "C:/temp/timeouts.txt");
//		System.out.println("l1="+l1);
//		establishMethodIdxMap("C:/Research/nioecho/functionList.out");
//		System.out.println("method2idx="+method2idx);
//		System.out.println("idx2method="+idx2method);
//		List<Integer> oldList = new LinkedList<Integer>();
//		oldList.add(1);
//		oldList.add(3);
//		oldList.add(2);
//		oldList.add(3);
//		oldList.add(3);
//		oldList.add(1);
//		oldList.add(1);
//		oldList.add(3);
//		oldList.add(2);
//		oldList.add(3);
//		oldList.add(3);
//		oldList.add(1);
//		oldList.add(3);
//		System.out.println("oldList="+oldList);
//		ArrayList<Long> newList=readTimesFromFile("C:/temp/times.txt"," ");
//		System.out.println("newList="+newList);
//		//writeTimesToFile("C:/temp/times2.txt"," ",newList);
		//	ArrayList<Long> newList2=updateTimes((long) 14, 4, newList);
//			updateTimeInFile("C:/temp/times.txt"," ", (long)171, 1);
//		System.out.println("newList2="+newList2);
		
		//initialTimesFile("C:/temp/times.txt",4, " ");
//		String dynamicConfigurations=ODDUtil.readToString("C:/temp/dynamicConfigurations.txt");
//    	if (dynamicConfigurations.length()>=4)
//    	{
//    		String dynamicFlag="";
//    		System.out.println("dynamicConfigurations.substring(0,1)=" + dynamicConfigurations.substring(0,1)); 
//    		dynamicFlag=dynamicConfigurations.substring(0,1);
//    		System.out.println("dynamicFlag=" + dynamicFlag); 
//    	}
			//System.out.println(0b101);
//			int dint=getDecimalFromBinaryTextFile("C:/temp/staticConfigurations.txt");
//			System.out.println("dintg=" + dint); 
			//initialTimesFile("C:/temp/staticTimes.txt", 4 , " ");
//			updateTimeFromConfigurationToFile("C:/temp/staticConfigurations.txt", "C:/temp/staticTimes.txt", (long) 720);
//			int resultI=Integer.parseInt("00", 2);
//			System.out.println("resultI=" + resultI); 
//			ArrayList<Long> list1=readTimesFromFile("C:/temp/staticTimes.txt", " ");
//			System.out.println("list1=" + list1);
//			//getMinPosDiffArrayIndex(list1, (long) 800); getFirst0ArrayIndex(list1);
//			int resultI=getNextIndex(list1, (long) 800);
					//System.out.println("resultI=" + Integer.toBinaryString(3)); 
					//Integer.toBinaryString(index);
		//System.out.println("resultI=" + getRealTimeFromFile("C:/temp/staticConfiguration.txt", "C:/temp/staticTimes.txt"));		
		//long long1=ODDUtil.readTimeOutFromFile(" StaticGraphCreate ", "C:/temp/timeouts.txt");
		//System.out.println("long1="+long1);
//		staticConfigurations=configurations.substring(0, 2);
//		dynamicConfigurations=configurations.substring(2, 6);
//		System.out.println("staticConfigurations="+staticConfigurations+" dynamicConfigurations="+dynamicConfigurations);
//		
		//saveMazeFile("C:/tp/maze1.txt", "85 68 66 60 65 54 63 55 52 55 52 52 58 47 51 52 57 52 59 57 51 60 67 48 58 55 57 57 51 49 51 54 52 54 61 48 62 52 98 56 75 62 59 56 50 61 50 49 28 0 0 0 0 0 0 0 0 0 0 0 0 0 0 108");
		//saveMazeFileRewardPenalty("C:/tp/maze2.txt", "85 68 66 60 65 54 63 55 52 55 52 52 58 47 51 52 57 52 59 57 51 60 67 48 58 55 57 57 51 49 51 54 52 54 61 48 62 52 98 56 75 62 59 56 50 61 50 49 28 0 0 0 0 0 0 0 0 0 0 0 0 0 0 108",100);
		//saveRewardPenaltyfromFiles("C:/tp/maze3.txt", "C:/tp/Times70989hcaidl5802.txt", 0, 100, false);
		//ODDUtil.createOrCopyFile("C:/tp/Maze.txt","0 0 0 0 \n0 0 0 0 \n0 0 0 0 \n0 0 0 0 \n0 0 0 0 \n0 0 0 0 \n0 0 0 0 \n0 0 0 0 \n",mazeFileName);
		//ODDUtil.copyFileJava7(mazeFileName, "C:/tp/Maze.txt");
//		String new1=getNewestFile ("", "");
//		System.out.println("new1="+new1);
//    	ArrayList querys=new ArrayList();
//    	
//    	querys = ODDUtil.getFindList(ODDUtil.getArrayList("C:/tp/functionList.out"), "run()");
//    	for(int i=0;i<querys.size();i++ ){
//			String query=querys.get(i).toString();
//			System.out.println("query="+query);
//    	}	
    	
		HashSet<String> onePortImpactSet = getQueryResultFromDir("<de.uniba.wiai.lspi.chord.service.PropertiesLoader: void <clinit>()>","");
		for (String s:onePortImpactSet) {
			System.out.println(s);
		}
	
	}
    public static String replaceIn1(String str) // 
    {
        int head = str.indexOf('<'); // 
        if (head == -1)
            ; //
        else {
            int next = head + 1; // 
            int count = 1; // 
            do {
                if (str.charAt(next) == '<')
                    count++;
                else if (str.charAt(next) == '>')
                    count--;
                next++; //
                if (count == 0) // 
                {
                    String temp = str.substring(head, next); //
                    str = str.replace(temp, ""); //
                    head = str.indexOf('<'); // 
                    next = head + 1; // 
                    count = 1; // 
                }
            } while (head != -1); // 
        }
        return str; //
    }
	   public static String transferPara(String str, HashMap<String,String> classePackages)
	   {
		   String resultStr="";
		   try {
			       	String[] strs=replaceIn1(str).replace(",", " ").replace("  ", " ").split(" "); 
			       	int strslength=strs.length;
			       	
			   		//System.out.print("strs.length="+strs.length+"\n");
			       	if (strslength<1)
			       	{
			       		return str;
			       	}
			       	else
			       	{
			       		//String fullClass="";
			       		String basicType="";
			       		String addStr="";
			       		for (int i=0; i<strslength; i++)
			       		{
			       			if (i % 2==0)
			       			{
			       				//System.out.print("strs[i]="+strs[i]+"\n");
			       				//System.out.print("transferClassType(strs[i],classePackages)="+transferClassType(strs[i],classePackages)+"\n");
			       				addStr="";
			       				basicType=transferBasicType(strs[i]);
			       				//System.out.print("basicType="+basicType+"\n");
			       				if (i+1<strslength)
			       				{
			       					String midStr=strs[i+1].replace(",", "").trim();
			       					//System.out.print("i="+i+" midStr="+midStr+"\n");
			       					int midStrlength=midStr.length();
			       					//System.out.print("midStrlength="+midStrlength+" midStr.substring(midStrlength-2, midStrlength="+midStr.substring(midStrlength-2, midStrlength)+"\n");
			       					if (midStrlength>2 && (midStr.substring(midStrlength-2, midStrlength).equals("[]")))
	       							{
			       						addStr="[]";
	       							}
				       				//System.out.print("addStr="+addStr+"\n");
			       				}
			       				if (basicType.indexOf(".")<0)
			       				{
			       					resultStr=resultStr+transferClassType(strs[i],classePackages)+addStr+",";
			       				}
			       				else
			       					resultStr=resultStr+basicType+addStr+",";
			       				//System.out.print("resultStr="+resultStr+"\n");
			       			}
			       		}
			       	}
			       	int resultStrlength=resultStr.length();
			       	//System.out.print("resultStrlength="+resultStrlength+"\n");
			       	if (resultStrlength<2)
			       		return resultStr; 
			       	//System.out.print("resultStr.substring(resultStrlength-1, resultStrlength)="+resultStr.substring(resultStrlength-1, resultStrlength)+"\n");
			       	if (resultStr.substring(resultStrlength-1, resultStrlength).equals(","))
			       	{	
			       		return resultStr.substring(0,resultStrlength-1);
			       	}
			       	else
			       		return resultStr; 
		   }catch (Exception e) {  
	           e.printStackTrace();  
	           return str;
	       }  
	   }
	   public static String transferPara(String str, String functionListFile)
	   {
		   String resultStr="";
		   try {
			       	String[] strs=replaceIn1(str).replace(",", " ").replace("  ", " ").split(" "); 
			       	int strslength=strs.length;
			       	
			   		//System.out.print("strs.length="+strs.length+"\n");
			       	if (strslength<1)
			       	{
			       		return str;
			       	}
			       	else
			       	{
			       		//String fullClass="";
			       		String basicType="";
			       		String addStr="";
			       		String itemStr="";
			       		HashMap<String,String> classePackages = new HashMap<>(); 
			       		classePackages=transferfunctionList(functionListFile, "");
			       		for (int i=0; i<strslength; i++)
			       		{
			       			if (i % 2==0)
			       			{
			       				//System.out.print("strs[i]="+strs[i]+"\n");
			       				//System.out.print("transferClassType(strs[i],classePackages)="+transferClassType(strs[i],classePackages)+"\n");
			       				addStr="";
			       				basicType=transferBasicType(strs[i]);
			       				//System.out.print("basicType="+basicType+"\n");
			       				if (i+1<strslength)
			       				{
			       					String midStr=strs[i+1].replace(",", "").trim();
			       					//System.out.print("i="+i+" midStr="+midStr+"\n");
			       					int midStrlength=midStr.length();
			       					//System.out.print("midStrlength="+midStrlength+" midStr.substring(midStrlength-2, midStrlength="+midStr.substring(midStrlength-2, midStrlength)+"\n");
			       					if (midStrlength>2 && (midStr.substring(midStrlength-2, midStrlength).equals("[]")))
	       							{
			       						addStr="[]";
	       							}
				       				//System.out.print("addStr="+addStr+"\n");
			       				}
			       				if (basicType.indexOf(".")<0)
			       				{
			       					itemStr=transferClassType(strs[i],classePackages);
			       				}
			       				else
			       					itemStr=basicType;
			       				resultStr=resultStr+itemStr+addStr+",";
			       				//System.out.print("resultStr="+resultStr+"\n");
			       			}
			       		}
			       	}
			       	int resultStrlength=resultStr.length();
			       	//System.out.print("resultStrlength="+resultStrlength+"\n");
			       	if (resultStrlength<2)
			       		return resultStr; 
			       	//System.out.print("resultStr.substring(resultStrlength-1, resultStrlength)="+resultStr.substring(resultStrlength-1, resultStrlength)+"\n");
			       	if (resultStr.substring(resultStrlength-1, resultStrlength).equals(","))
			       	{	
			       		return resultStr.substring(0,resultStrlength-1);
			       	}
			       	else
			       		return resultStr; 
		   }catch (Exception e) {  
	           e.printStackTrace();  
	           return str;
	       }  
	   }	   
	   public static String transferClassType(String str, String packageName, HashSet classes)
	   {
		   try {
			   if (classes.contains(str) && str.indexOf(".")<0)
			   {
				   return packageName+"."+str;
			   }
			   else
				   return str; 
		   }catch (Exception e) {  
	           e.printStackTrace();  
	           return "";
	       }  
	   }
	   public static String transferClassType(String str, HashMap classePackages)
	   {
		   try {
			   if (classePackages.containsKey(str) && str.indexOf(".")<0)
			   {
				   return  classePackages.get(str)+"."+str;
			   }
			   else
				   return str; 
		   }catch (Exception e) {  
	           e.printStackTrace();  
	           return "";
	       }  
	   }
	   public static String transferBasicType(String str)
	   {
		   try {
			   if (str.indexOf(".")>=0)
			   {
				   return str;
			   }
			   else if (str.equals("String"))
			   {
				   return "java.lang.String";
			   }
			   else if (str.equals("List"))
			   {
				   return "java.util.List";
			   }
			   else if (str.equals("Set"))
			   {
				   return "java.util.Set";
			   }
			   else if (str.equals("InetSocketAddress"))
			   {
				   return "java.net.InetSocketAddress";
			   }
			   else if (str.equals("Integer"))
			   {
				   return "java.lang.Integer";
			   }
			   else if (str.equals("Long"))
			   {
				   return "java.lang.Long";
			   }
			   else if (str.equals("Set"))
			   {
				   return "java.util.Set";
			   }
			   else if (str.equals("InputStream"))
			   {
				   return "java.io.InputStream";
			   }
			   else if (str.equals("OutputStream"))
			   {
				   return "java.io.OutputStream";
			   }
			   else if (str.equals("DataInput"))
			   {
				   return "java.io.DataInput";
			   }
			   else if (str.equals("DataOutput"))
			   {
				   return "java.io.DataOutput";
			   }
			   else if (str.equals("HashSet"))
			   {
				   return "java.util.HashSet";
			   }
			   else if (str.equals("Collection"))
			   {
				   return "java.util.Collection";
			   }
			   else if (str.equals("List"))
			   {
				   return "java.util.List";
			   }
			   else if (str.equals("HashSet"))
			   {
				   return "java.util.HashSet";
			   }
			   else if (str.equals("HashMap"))
			   {
				   return "java.util.HashMap";
			   }
			   else if (str.equals("ArrayList"))
			   {
				   return "java.util.ArrayList";
			   }
			   else if (str.equals("Map"))
			   {
				   return "java.util.Map";
			   }
			   return str;
		   }catch (Exception e) {  
	           e.printStackTrace();  
	           return "";
	       }  
	   }
	   public static String getClassFromString(String str) {  
		   	
	       try {  
	           // check the string str
	       	if (str.length()<3)
	       	{
	       		return "";
	       	}    	  
	   		//System.out.print("str="+str+"\n");
	       	String[] strs=str.split("\\."); 
	   		//System.out.print("strs.length="+strs.length+"\n");
	       	if (strs.length<1)
	       	{
	       		return "";
	       	}
	       	else
	       	{
	       		return strs[strs.length-1];
	       	}
	       } catch (Exception e) {  
	           e.printStackTrace();  
	           return "";
	       }  
	   }  
	   
	   public static String getPackageFromString(String str) {  
		   	
	       try {  
	           // check the string str
	       	if (str.length()<3)
	       	{
	       		return "";
	       	}    	  
	   		//System.out.print("str="+str+"\n");
	       	String[] strs=str.split("\\."); 
	   		//System.out.print("strs.length="+strs.length+"\n");
	       	if (strs.length<1)
	       	{
	       		return "";
	       	}
	       	else
	       	{   String resultStr="";
	       		for (int i=0; i<strs.length-1; i++)
	       			resultStr+=strs[i]+".";
	       		return resultStr+"*";
	       	}
	       } catch (Exception e) {  
	           e.printStackTrace();  
	           return "";
	       }  
	   } 
	   
	   public static String getPackageFromLineString(String str) {  
		   	
	       try {  
	           // check the string str
	       	if (str.indexOf("package ")!=0)
	       	{
	       		return "";
	       	}    	  
	   		//System.out.print("str="+str+"\n");
	       	String[] strs=str.split(" "); 
	   		//System.out.print("strs.length="+strs.length+"\n");
	       	if (strs.length<2)
	       	{
	       		return "";
	       	}
	       	else
	       	{   
	       		return strs[1].replace(";","");
	       	}
	       } catch (Exception e) {  
	           e.printStackTrace();  
	           return "";
	       }  
	   } 
	   public static String getClassFromLineString(String str) {  
		   	
	       try {  
	           // check the string str
	       	if (str.indexOf("public class ")!=0)
	       	{
	       		return "";
	       	}    	  
	   		//System.out.print("str="+str+"\n");
	       	String[] strs=str.split(" "); 
	   		//System.out.print("strs.length="+strs.length+"\n");
	       	if (strs.length<3)
	       	{
	       		return "";
	       	}
	       	else
	       	{   
	       		return strs[2].replace("{", "");
	       	}
	       } catch (Exception e) {  
	           e.printStackTrace();  
	           return "";
	       }  
	   } 
	   //public static void main(String[] args) throws InterruptedException
	   public static String getMethodFromLineString(String str) {  
		   	
	       try {  
	            String strIn="";
		       	if (str.indexOf("public ")!=0 && str.indexOf("protected ")!=0&& str.indexOf("private ")!=0)
		       	{
		       		return "";
		       	}    	  
		       	String midStr=str.replace("public","").replace("protected","").replace("private","").replace("static","").replace("synchronized","").replace("abstract","").replace("{", "").trim();
		   		//System.out.print("str="+str+"\n");
		       	String[] strs=midStr.split("\\)"); 
		   		System.out.print("strs.length="+strs.length+"\n");
		   		
		   		String midStr2=strs[0];
	       		String[] strs2=midStr2.split("\\("); 
		   		System.out.print("strs2.length="+strs2.length+"\n");
	       		if (strs2.length>1)
		       	{
		       		strIn=strs2[1];
		       		System.out.print("strIn="+strIn+"\n");
		       	}
		       	if (strs.length<2)
		       	{
		       		return midStr;
		       	}
		       	else 
		       	{   
		       		
		       		return strs[0]+")";
		       	}
	       } catch (Exception e) {  
	           e.printStackTrace();  
	           return "";
	       }  
	   } 
	   public static boolean isIncludePackageClass(String javaFile, String listStr) {  	   	
	       try {
	   		FileReader reader = null;      
	        BufferedReader br = null;    
	        reader = new FileReader(javaFile);   
	        br = new BufferedReader(reader);
	        String str = "";  
	        String strtrim="";
	        boolean hasPackage=false;
	        boolean hasClass=false;
//	        String packageStr1="import "+listStr+";";
//	        String packageStr2="import "+getPackageFromString(listStr)+";";
	        String packageStr1=listStr+";";
	        String packageStr2=getPackageFromString(listStr)+";";
	        String classStr=getClassFromString(listStr)+" ";
	        System.out.println(" packageStr1="+packageStr1+" packageStr2="+packageStr2+" classStr="+classStr);
	        while((str = br.readLine()) != null)
	        {	        	
	        	
	        	strtrim=str.trim();
	        	System.out.print(" strtrim="+strtrim+"\n");
	        	if (strtrim.indexOf("import ")==0)
	        	{

	        		System.out.println(" strtrim.indexOf(packageStr1)="+strtrim.indexOf(packageStr1)+" strtrim.indexOf(packageStr2)="+strtrim.indexOf(packageStr2));
	        		if (strtrim.indexOf(packageStr1)>6 || strtrim.indexOf(packageStr2)>6)
	        		{
	        			hasPackage=true;
	        		}
	        	}
	        	if (hasPackage)
	        	{
	        		if (strtrim.indexOf(classStr)==0)
	        		{
	        			hasClass=true;
	        		}
	        	}
	        	System.out.println(" hasPackage="+hasPackage+" hasClass="+hasClass);
	        	if (hasPackage && hasClass)
	        		return true;
	        	// read lines
	            //str = br.readLine();
	        }        

	       			return (hasPackage && hasClass);       
	       } catch (Exception e) {  
	           e.printStackTrace();  
	           return false;
	       }  
	   } 
	   
	   public static String methodIncludePackageClass(String javaFile, String listStr) {  	   	
	       try {  
	    	String methodName="";   
	    	String packageName="";   
	   		FileReader reader = null;      
	        BufferedReader br = null;    
	        reader = new FileReader(javaFile);   
	        br = new BufferedReader(reader);
	        String str = "";  
	        String strtrim="";
	        boolean hasPackage=false;
	        boolean hasClass=false;
//	        String packageStr1="import "+listStr+";";
//	        String packageStr2="import "+getPackageFromString(listStr)+";";
	        String packageStr1=listStr+";";
	        String packageStr2=getPackageFromString(listStr)+";";
	        String classStr=getClassFromString(listStr)+" ";
	        System.out.println(" packageStr1="+packageStr1+" packageStr2="+packageStr2+" classStr="+classStr);
	        while((str = br.readLine()) != null)
	        {	        	
	        	
	        	strtrim=str.trim();
	        	System.out.print(" strtrim="+strtrim+"\n");
	        	if (strtrim.indexOf("package ")==0)
	        	{
	        		
	        	}
	        	if (strtrim.indexOf("import ")==0)
	        	{

	        		System.out.println(" strtrim.indexOf(packageStr1)="+strtrim.indexOf(packageStr1)+" strtrim.indexOf(packageStr2)="+strtrim.indexOf(packageStr2));
	        		if (strtrim.indexOf(packageStr1)>6 || strtrim.indexOf(packageStr2)>6)
	        		{
	        			hasPackage=true;
	        		}
	        	}
	        	if (hasPackage)
	        	{
	        		if (strtrim.indexOf(classStr)==0)
	        		{
	        			hasClass=true;
	        		}
	        	}
	        	System.out.println(" hasPackage="+hasPackage+" hasClass="+hasClass);
	        	if (hasPackage && hasClass)
	        		return methodName;
	        	// read lines
	            //str = br.readLine();
	        }        

	       			return methodName;       
	       } catch (Exception e) {  
	           e.printStackTrace();  
	           return "";
	       }  
	   } 
	   
	    public static HashMap transferfunctionList(String oldFile, String newFile) {  
	        FileWriter writer = null;  
	        FileReader reader = null;  
	        BufferedReader br = null;  
	        BufferedWriter bw = null;  	   
	        //HashSet<String> classes = new HashSet<String>();	
	        HashMap<String, String> classPackages = new HashMap<>();  
	        try {  
	            
	               
	            //    
	            reader = new FileReader(oldFile);  	   
	            String str = null;  	   
	            br = new BufferedReader(reader);
	            String allName = "";      	   
	            String className = "";  
	            String packageName = ""; 
	            int strs2length=0;
	            while ((str = br.readLine()) != null) {  	  
	            	//System.out.print("str="+str+"\n");
	                StringBuffer sb = new StringBuffer("");   
	    	       	String[] strs=str.split(":"); 	    	  	  
	            	//System.out.print("strs[0]="+strs[0]+"\n");
	    	       	allName=strs[0].replace("<", "");   	  	  
	            	//System.out.print("allName="+allName+"\n");
	            	String[] strs2=allName.split("\\."); 
	            	strs2length=strs2.length;
	    	       	className=strs2[strs2length-1]; 	  	  
	            	//System.out.print("className="+className+"\n");
	    	       	packageName = "";
	    	       	for (int i=0; i<strs2length-2; i++)
	    	       		packageName = packageName+strs2[i]+".";
	    	       	if (strs2length>=2)
	    	       		packageName = packageName+strs2[strs2length-2]; 	  	  
	            	//System.out.print("packageName="+packageName+"\n");
	    	       	if (className.length()>1 &&  packageName.length()>1)
	    	       		classPackages.put(className, packageName);
	            }  
	   
	            br.close();  
	            reader.close();  
	            if (newFile.length()<1)
	            	return classPackages;
	            File file = new File(newFile);  
	            if (!file.exists()) {  
	                file.createNewFile();  
	            }  
	            writer = new FileWriter(newFile, true);  	
	            bw = new BufferedWriter(writer);
	            Iterator<Entry<String, String>> iterator = classPackages.entrySet().iterator();  
	            while (iterator.hasNext()) {  
	                Entry<String, String> entry = iterator.next();  
	                bw.write(entry.getValue()+"."+entry.getKey()+"\n"); 
	            }  
	            
	            bw.close();  
	            writer.close();  
	            return classPackages;
	        } catch (IOException e) {  
	            e.printStackTrace();  
	            return null;
	        }  
	    }
	    
	    public static HashMap transferfunctionList() {  
	    	return transferfunctionList("functionList.out", "classList.out");
	    }
	    
	    public static HashSet<String> getListSet(String listFile) {  
	        FileReader reader = null;  
	        BufferedReader br = null;    
	        //HashSet<String> classes = new HashSet<String>();	
	        HashSet<String> lists = new HashSet<>();  
	        try {  
	            
	               
	            //    
	            reader = new FileReader(listFile);  	   
	            String str = null;  	   
	            br = new BufferedReader(reader);

	            while ((str = br.readLine()) != null) {  	  
	    	       	if (str.length()>1)
	    	       		lists.add(str);
	            }  
	   
	            br.close();  
	            reader.close();  
	           
	            return lists;
	        } catch (IOException e) {  
	            e.printStackTrace();  
	            return null;
	        }  
	    }
	    
	    public static void writeListSet(HashMap<String, Integer> hm, String dest) {  
       
	        FileWriter writer = null;  
	        BufferedWriter bw = null;  
	   
	        try {  
	            File file = new File(dest);  
	            file.createNewFile(); 	           
	            // 
	            writer = new FileWriter(dest, true); 
	            bw = new BufferedWriter(writer);  
		    	Iterator iter = hm.entrySet().iterator();
				while (iter.hasNext()) {
					Entry entry = (Entry) iter.next();
					bw.write(entry.getKey()+"\n");
				}
	            bw.close();  
	            writer.close();  
	   
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }
	    public static void writeArrayList(ArrayList al, String dest) {  
	        
	        FileWriter writer = null;  
	        BufferedWriter bw = null;  
	   
	        try {  
	            File file = new File(dest);  
	            file.createNewFile(); 	           
	            // 
	            writer = new FileWriter(dest, true); 
	            bw = new BufferedWriter(writer);  
	            for (int i=0; i<al.size(); i++)
	            {
	            	bw.write(al.get(i).toString().trim()+"\n");
	            }            
		    	
	            bw.close();  
	            writer.close();  
	   
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }	   
	    public static void writeListMap(List<Entry<String, Integer>> lm, String dest, String LastItem, boolean isCheck) {
	        
	        FileWriter writer = null;  
	        BufferedWriter bw = null;  
	        String oldFileMsg="";
	        String lineStr="";
	        try {  
	            File file = new File(dest);  
	            file.createNewFile(); 	           
	            // 
	            writer = new FileWriter(dest, true); 
	            bw = new BufferedWriter(writer);  
	            oldFileMsg=readToString(dest);
	            for(Entry<String, Integer> entry: lm){
	            	lineStr=entry.getKey().trim();
	            	if (isCheck) {
	            		if (!oldFileMsg.contains(lineStr))
	            			bw.write(lineStr+"\n");
	            	}
	            	else
	            		bw.write(lineStr+"\n");
					if (lineStr.equals(LastItem))
					{						
						break;
					}
		        }   		    	
	            bw.close();  
	            writer.close();  
	   
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }	    
	    
	    public static void writeMethodPairListMap(String methodPairMsg, List<Entry<String, Integer>> lm, String dest, String LastItem, boolean isCheck) {
	        
	        FileWriter writer = null;  
	        BufferedWriter bw = null;  
	        String oldFileMsg="";
	        String lineStr="";
	        try {  
	            File file = new File(dest);  
	            file.createNewFile(); 	           
	            // 
	            writer = new FileWriter(dest, true); 
	            bw = new BufferedWriter(writer);  
	            bw.write(methodPairMsg+"\t");
	            oldFileMsg=readToString(dest);
	            for(Entry<String, Integer> entry: lm){
	            	lineStr=entry.getKey().trim();
	            	if (isCheck) {
	            		if (!oldFileMsg.contains(lineStr))
	            			bw.write(lineStr+" | ");
	            	}
	            	else
	            		bw.write(lineStr+" | ");
					if (lineStr.equals(LastItem))
					{						
						break;
					}
		        }   	
	            bw.write(lineStr+"\n");
	            bw.close();  
	            writer.close();  
	   
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }	    
		public static ArrayList getArrayList(String listFile) {
			ArrayList  resultA = new ArrayList(); 
	        FileReader reader = null;  
	        BufferedReader br = null;     
	        try {  
	   
	            reader = new FileReader(listFile);  
	            br = new BufferedReader(reader);              
	            String str = null;  
	            //int count=0;
	            while ((str = br.readLine()) != null) {  
	            	//System.out.println("str="+str);
	            	if (str.length()>1)
	            	{
	            		if (!resultA.contains(str))
	            			resultA.add(str);	            		
	            	}
	            }     
	            br.close();  
	            reader.close(); 
	        } catch (Exception e) {  
	            e.printStackTrace();   
	               
	        }  
	        return resultA; 
		}

		public static HashMap getSourceSinksFromFile(String stmtFile) {
			ArrayList  sources = new ArrayList(); 
			ArrayList  sinks = new ArrayList(); 
			HashMap map = new HashMap();
	        FileReader reader = null;  
	        BufferedReader br = null;     
	        try {  
	   
	            reader = new FileReader(stmtFile);  
	            br = new BufferedReader(reader);              
	            String str = null;  
	            //int count=0;
	            while ((str = br.readLine()) != null) {  
	            	//System.out.println("str="+str);
	            	if (str.length()>1)
	            	{
	            		//if (sinks.contains(str))
	        				//continue;
	            		String strs[]=str.split("; ");
	                	//System.out.println("strs.length="+strs.length+" strs[1]="+strs[1]);
	            		if (strs.length>1)
	            		{	
	            			sources.add(strs[0].trim());
	            			sinks.add(strs[1].trim());
	            		}

	            	}
	            }     
	            br.close();  
	            reader.close();    
	            map.put(1,sources);
	            map.put(2,sinks);
	            return map;   
	        } catch (Exception e) {  
	            e.printStackTrace();   
	            return map;    
	        }  
		}
		
		public static ArrayList getMethodItems(String pairName, String listFile) {
			ArrayList  resultA = new ArrayList(); 
			
			
	        FileReader reader = null;  
	        BufferedReader br = null;     
	        try {  
	        	String lineStr="";
	            reader = new FileReader(listFile);  
	            br = new BufferedReader(reader);              
	            String str = "";  
	            //int count=0;
	            while ((str = br.readLine()) != null) {  
	            	//System.out.println("str="+str);
	            	if (str.startsWith(pairName))
	            	{
	            		lineStr=str.substring(pairName.length(),str.length());
	            		break;
	            	}
	            }     
	            //System.out.println("lineStr="+lineStr);
	            br.close();  
	            reader.close();     
	            String tmpStr="";
	            String strs[]=lineStr.split(" \\| ");
	        	for (int i=0; i<strs.length;i++)  {     
	        		tmpStr=strs[i].trim();
	        		//System.out.println("tmpStr="+tmpStr);
	        		if (tmpStr.length()>0 && !resultA.contains(tmpStr))  {
	        			resultA.add(tmpStr);
	        		}	
	        	}	   
	            
	            return resultA;   
	        } catch (Exception e) {  
	            e.printStackTrace();   
	            return resultA;    
	        }  
		}

	    public static boolean itemInArrayList(ArrayList al, String str) {  
	        

            for (int i=0; i<al.size(); i++)
            {
            	if (str.indexOf(al.get(i).toString().trim())>=0)
            		return true;
            }            
		    return false;	
	          
	    }	 
	    
	    public static int getLineNum(String listFile) {  
	    	int lineNum=0;
	        FileReader reader = null;  
	        BufferedReader br = null;    
	       
	        try {  
	            reader = new FileReader(listFile);  	   
	            String str = null;  	   
	            br = new BufferedReader(reader);

	            while ((str = br.readLine()) != null) {  	  
	            	lineNum++;
	            }  
	   
	            br.close();  
	            reader.close();  
	          
	        } catch (IOException e) {  
	            e.printStackTrace();  
	            return 0;
	        }  
	        return lineNum;
	    }
	    
	    public static int getStmtNum(String longStr, String separation) {  
	    	if (longStr.length()<1)
	    		return 0;
	    	String midStr=longStr.trim();
	    	String[] strs = midStr.split(separation);
	    	return strs.length;
//	    	System.out.println("strs.length="+strs.length+" midStr.lastIndexOf="+midStr.lastIndexOf("-->")+" midStr.length()="+midStr.length());
//	    	if (midStr.lastIndexOf("-->")==(midStr.length()-3))  {
//	    		return strs.length;
//	    	}
//	    	else
//	    		return strs.length+1;
	       
	    }
		public static String readToString(String fileName) {  
	        String encoding = "UTF-8";  
	        File file = new File(fileName);  
	        Long filelength = file.length();  
	        byte[] filecontent = new byte[filelength.intValue()];  
	        try {  
	            FileInputStream in = new FileInputStream(file);  
	            in.read(filecontent);  
	            in.close();
	            return new String(filecontent, encoding);
	        } catch (Exception e) {  
	            //e.printStackTrace();  
	            return "";
	        } 
	        
	    }  
	    public static ArrayList<File> getFiles(String realpath, ArrayList<File> files, String startsWith, String endsWith) {
	        File realFile = new File(realpath);
	        if (realFile.isDirectory()) {
	            File[] subfiles = realFile.listFiles();
	            for (File file : subfiles) {
//	                if (file.isDirectory()) {
//	                    getFiles(file.getAbsolutePath(), files, startsWith, endsWith);
//	                } else 
	                {
	                	if (file.isFile() &&  file.getName().startsWith(startsWith) && file.getName().endsWith(endsWith))
	                		files.add(file);
	                }
	            }
	        }
	        return files;
	    }
	    
	    public static ArrayList<File> getFileSort(String path, String startsWith, String endsWith) {
	    	ArrayList<File> list = getFiles(path, new ArrayList<File>(), startsWith, endsWith);
	    	if (list != null && list.size() > 0) {
	    		Collections.sort(list, new Comparator<File>() {
	    			public int compare(File file, File newFile) {
	    				if (file.lastModified() < newFile.lastModified()) {
	    					return -1;
	    				} else if (file.lastModified() == newFile.lastModified()) {
	    					return 0;
	    				} else {
	    					return 1;
	    				}
	    			}

	    		});
	    	}
	    	return list;
	    }	

	    public static List<String> getFileIds(String realpath, List<String> files, String startsWith, String endsWith) {
	        File realFile = new File(realpath);
	        if (realFile.isDirectory()) {
	            File[] subfiles = realFile.listFiles();
	            for (File file : subfiles) {
//	                if (file.isDirectory()) {
//	                    getFileIds(file.getAbsolutePath(), files, startsWith, endsWith);
//	                } else 
	            	{
	                	if (file.isFile() &&  file.getName().startsWith(startsWith) && file.getName().endsWith(endsWith))
	                		files.add(file.getName().replace(startsWith, "").replace(endsWith, ""));
	                }
	            }
	        }
	        return files;
	    }

	    public static List<String> getFileIdSort(String path, String startsWith, String endsWith) {
	    	List<String> list=getFileIds(path, new ArrayList<String>(), startsWith, endsWith);
	    	if (list != null && list.size() > 0) {
	    	       Collections.sort(list,new Comparator<String>() {
	    	            @Override
	    	            public int compare(String o1, String o2) {
	    	                if(o1 == null || o2 == null){
	    	                    return -1;
	    	                }
	    	                if(o1.length() > o2.length()){
	    	                    return 1;
	    	                }
	    	                if(o1.length() < o2.length()){
	    	                    return -1;
	    	                }
	    	                if(o1.compareTo(o2) > 0){
	    	                    return 1;
	    	                }
	    	                if(o1.compareTo(o2) < 0){
	    	                    return -1;
	    	                }
	    	                if(o1.compareTo(o2) == 0){
	    	                    return 0;
	    	                }
	    	                return 0;
	    	            }
	    	        });
//	    	        for(String s:list){
//	    	            System.out.println(s);
//	    	        }

	    	}
	    	return list;
	    }	
	    
	    public static String getQueryFromImpactsetStr(String impactSetStr, String separator) {  
	    	String resultS="";
	    	String[] impactSets=impactSetStr.split(separator);
	    	String title=impactSets[0];
	    	//System.out.println("title = "+title);
	    	String[] titles=title.split("\\[<");
	    	if (titles.length<2)
	    	{
	    		return title;
	    	}
	    	//System.out.println("titles.length = "+titles.length);
	    	String title2=titles[1];
	    	//System.out.println("title2 = "+title2);
	    	String[] title2s=title2.split(">\\]");
	    	return "<"+title2s[0]+">";
	    		    }
	    
	    public static HashSet<String> getSetFromImpactsetStr(String impactSetStr, String separator) { 
	    	//System.out.println("getSetFromImpactsetStr impactSetStr = "+impactSetStr); 
	    	//System.out.println("separator = "+separator);
	    	HashSet<String> resultSet = new HashSet<>();  
	    	String[] impactSets=impactSetStr.split(separator);
	    	//System.out.println("impactSets.length = "+impactSets.length);
	    	if (!(impactSets[0].trim().startsWith("====")))
	    		resultSet.add(impactSets[0].trim());
	    	for (int i=1; i<impactSets.length; i++)
	    	{
	    		//System.out.println("impactSets["+i+"] = "+impactSets[i]);
	    		if (impactSets[i].trim().length()>0)
	    			resultSet.add(impactSets[i].trim());
	    	}	    		
	    	return resultSet;
	    }
	    
	    public static void printImpactSet(String query, HashSet<String> impactSet) {  
			System.out.println("==== DistODD impact set of [" + query +"]  size=" + impactSet.size() + " ====");
			for (String m : impactSet) {
				System.out.println(m);
			}
	    }
	    
	    public static void printImpactSetHashMap(HashMap<String,HashSet<String>> resultMap) {  
	    	//System.out.println("printImpactSetHashMap(HashMap<String,HashSet<String>> resultMap.size="+resultMap.size());
	    	System.out.println("All Dependency Sets:");   
	        for (Entry<String, HashSet<String>> entry : resultMap.entrySet()) {
	            String key = entry.getKey();
	            //System.out.println("entry.getKey()="+key);
	            HashSet<String> value = entry.getValue();
	            //System.out.println("entry.getValue()="+value);
	            printImpactSet(key, value);
	        }

	    }
	    // get methods which match the pattern
	    public static String getPatternMethods(String listFile, String pattern)
	    {
	    	String resultS="";
	    	//ArrayList  methods = getArrayList(listFile);
	    	ArrayList  querys = getFindList(getArrayList(listFile), pattern);
			for(int i=0;i<querys.size();i++ ){
				resultS+=querys.get(i).toString()+"\n";
			}
	    	return resultS;
	    }
	    
//	    // read all methods from listFile to the ArrayList
//		public static ArrayList getArrayList(String listFile) {
//			ArrayList  resultA = new ArrayList(); 
//	        FileReader reader = null;  
//	        BufferedReader br = null;     
//	        try {  
//	   
//	            reader = new FileReader(listFile);  
//	            br = new BufferedReader(reader);              
//	            String str = null;  
//	            //int count=0;
//	            while ((str = br.readLine()) != null) {  
//	            	//System.out.println("str="+str);
//	            	if (str.length()>1)
//	            	{
//	            		if (!resultA.contains(str))
//	            			resultA.add(str);	            		
//	            	}
//	            }     
//	            br.close();  
//	            reader.close(); 
//	        } catch (Exception e) {  
//	            e.printStackTrace();   
//	               
//	        }  
//	        return resultA; 
//		}
		// get method list via finding pattern
		public static ArrayList getFindList(ArrayList list, String pattern)  {
			boolean directlySearch=true;
			pattern=pattern.toLowerCase().trim();
//			if (pattern.indexOf("<")>=0 && pattern.indexOf(">")>0 || (pattern.indexOf("()")<0 && (pattern.indexOf("(")>0 || pattern.indexOf(")")>0)) || pattern.indexOf("[")>0 || pattern.indexOf("]")>0 ) { 
//				directlySearch=true;
//			}	
//			else
			if (pattern.indexOf("*")>=0 && pattern.indexOf("?")>=0) {
				directlySearch=false;
				pattern=pattern.replace("(*", "*").replace("*)", "*").replace("(?", "?").replace("?)", "?");
			}	
			//System.out.println("pattern = "+pattern+" directlySearch= "+directlySearch);
			ArrayList  resultArrayList = new ArrayList(); 
			
			Pattern p = null;
			if (!directlySearch)
				p=Pattern.compile(pattern); 
			String midStr="";
			String lowStr="";
			boolean bFound = false;
			for(int i=0;i<list.size();i++ ){
				midStr=list.get(i).toString();
				lowStr=midStr.toLowerCase();
				bFound = false;
				//System.out.println("midStr = "+midStr);
				if (!directlySearch)  {
					Matcher m = p.matcher(lowStr);
					//System.out.println("m.find() = "+m.find());
					bFound = m.find();
				}	
				else
					bFound=(lowStr.indexOf(pattern)>=0);
				if(bFound)  {
					resultArrayList.add(midStr);
					//System.out.println("resultArrayList = "+resultArrayList);
				}	
				
			}
			//System.out.println("resultArrayList = "+resultArrayList);
			return resultArrayList; 
		}
		
		public static HashMap<String,HashSet<String>> mergeResultMap(HashMap<String,HashSet<String>> map1, HashMap<String,HashSet<String>> map2)
		{
			HashMap<String,HashSet<String>> resultMap = new HashMap<String,HashSet<String>>();
//			HashMap<String,HashSet<String>> anotherMap = new HashMap<String,HashSet<String>>();
//			if (map1.size()>map2.size())  {				
//				resultMap.putAll(map1);
//				anotherMap = map2;
//			}
//			else  {
//				resultMap.putAll(map2);
//				anotherMap = map1;
//			}
	        for (Entry<String,HashSet<String>> entry : map1.entrySet()) {
	        	String oneKey=entry.getKey();
	        	HashSet<String> oneValue=entry.getValue();
	        	HashSet<String> newValue=new HashSet<String>();
	        	for (String oneMethod: oneValue)
	        	{
	        		newValue.add(oneMethod);
	        	}
	        	resultMap.put(oneKey, newValue);
	        }
	        
	        for (Entry<String,HashSet<String>> entry : map2.entrySet()) {
	        	String oneKey=entry.getKey();
	        	HashSet<String> oneValue=entry.getValue();
	        	HashSet<String> newValue=new HashSet<String>();
	        	for (String oneMethod: oneValue)
	        	{
	        		newValue.add(oneMethod);
	        	}
        		if (resultMap.containsKey(oneKey))
        		{
        			HashSet<String> oldQueryImpactSet = new HashSet<>();
        			oldQueryImpactSet=resultMap.get(oneKey);
    	        	for (String oneMethod: oldQueryImpactSet)
    	        	{
    	        		newValue.add(oneMethod);
    	        	}
        		} 
        		resultMap.put(oneKey, oneValue);
	        }
			return resultMap;
		}
		
		public static boolean isAllTrue(boolean[] booleans, int booleanLength)
		{
			boolean resultB=true;
			for (int i=0; i<booleanLength; i++) { 
				if (!booleans[i])
					return false;
			}
			return resultB;
		}
		public static boolean isAllFalse(boolean[] booleans, int booleanLength)
		{
			boolean resultB=true;
			for (int i=0; i<booleanLength; i++) {
				//System.out.println("i="+i+"booleans[i]="+booleans[i]);
				if (booleans[i])					
					return false;
			}
			return resultB;
		}
		public static boolean isAllTwoTrue(boolean[] bool1,boolean[] bool2, int booleanLength)
		{
			boolean resultB=true;
			for (int i=0; i<booleanLength; i++) { 
				//System.out.println("i="+i+" bool1[i]="+bool1[i]+" bool2[i]="+bool2[i]);
				if (bool1[i] && !bool2[i])
				{	
					resultB=false;
					//System.out.println("resultB="+resultB);
					return false;
				}	
			}
			//System.out.println("resultB="+resultB);
			return resultB;
		}
	    public static void writeStringToFile(String str, String dest) {  
	        FileWriter writer = null;  
	        BufferedWriter bw = null;  
	   
	        try {  
	            File file = new File(dest);  
	            if (!file.exists()) {  
	                file.createNewFile();  
	            }  
	            writer = new FileWriter(dest, false); 
	            bw = new BufferedWriter(writer);  
	            bw.write(str);    
	            bw.close();  
	            writer.close();  	
	            //System.out.println("str="+str+" dest="+dest);
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }  
	    
	    public static void writeStaticDynamicTimeOutsToFile(long timeOutTimeStatic, long timeOutTimeDynamic, String dest) {  
	    	String longStr="Static TimeOut: "+timeOutTimeStatic+" Dynamic TimeOut: "+timeOutTimeDynamic;
	         File file=new File(dest);
	         if(file.exists()&&file.isFile())
	             file.delete();
	    	writeStringToFile(longStr, dest);
	    }  
	    
	    public static long readTimeOutFromFile(String timeOutDes, String dest) {  
	    	try
	    	{
		    	String longStr=readToString(dest).replace("TimeOut:", "");
		    	//System.out.println("longStr="+longStr+" timeOutDes="+timeOutDes+ " dest="+dest);
		    	if (longStr.indexOf(timeOutDes)<0)
		    		return 0;
		    	//System.out.println("longStr="+longStr+" longStr.indexOf(timeOutDes)="+longStr.indexOf(timeOutDes));
		        //if  (!timeOutDes.trim().endsWith(":"))
		        //	timeOutDes=timeOutDes.trim()+":";
		    	String[] strs=longStr.split(timeOutDes); 
		       	int strslength=strs.length;	       	
		   		//System.out.println("longStr="+longStr+" strs.length="+strs.length);
		       	if (strslength<1)
		       	{
		       		return Long.parseLong(longStr);
		       	}
		       	else
		       	{
		       		String strs2=strs[1].trim();
		       		String[] strs2s=strs2.split(" "); 
			       	String resultS=strs2s[0].replaceAll("[^0-9]","");
			        //.trim().replace("/t", "").replace("/n", "").replace("/r", "");		       	
			       	//System.out.println("resultS="+resultS+" strs2s.length="+strs2s.length+" resultS.length()="+resultS.length());
			       	return Long.parseLong(resultS); 
//			       	if (resultS.length()<1)
//			       	{	
//			       		return Long.parseLong(strs2);
//			       	}
//			       	else
//			       		return Long.parseLong(resultS); 
		       	}
		    } catch (Exception e) {  
	            return 0;
	        }  
	    }  
	    
	    
		public static HashMap<String,HashSet<String>> getHashMapFromFile(String listFile) {
			HashMap<String,HashSet<String>> resultMap = new HashMap<String,HashSet<String>>();
			HashSet<String>  resultS = new HashSet<String>(); 
	        FileReader reader = null;  
	        BufferedReader br = null;  
	        try {  
	            reader = new FileReader(listFile); 
	            br = new BufferedReader(reader);              
	            String str = ""; 
	            while ((str = br.readLine()) != null) {  
	            	if (str.indexOf("====")<0 || str.indexOf("<")<0 || str.indexOf(">")<0 || str.indexOf("size=0")>0)
	            		continue;
	            	//System.out.println("str: "+str);
			    	HashSet<String> onePortImpactSet = new HashSet<>();
			        onePortImpactSet=getSetFromImpactsetStr(str, ";");
	            	//System.out.println("onePortImpactSet.size(): "+onePortImpactSet.size());
			        if (onePortImpactSet.size()<1)
			        	continue;
			        String oneKey=getQueryFromImpactsetStr(str, ";");	
	            	//System.out.println("oneKey.length(): "+oneKey.length());
			        if (oneKey.length()<1)
			        	continue;
	        		//System.out.println("oneKey= "+oneKey+" onePortImpactSet= "+onePortImpactSet);  
	        		if (resultMap.containsKey(oneKey))
	        		{
	        			HashSet<String> oldQueryImpactSet = new HashSet<>();
	        			oldQueryImpactSet=resultMap.get(oneKey);
	    	        	for (String oneMethod: oldQueryImpactSet)
	    	        	{
	    	        		onePortImpactSet.add(oneMethod);
	    	        	}
	        		} 
			        resultMap.put(oneKey, onePortImpactSet);  
	            }     
	            br.close();  
	            reader.close();  
	        } catch (Exception e) {  
	            e.printStackTrace();   
	                
	        }  
	        return resultMap;
		}   
	    
	    public static void clientOutputAll(String resultFile)
	    {
            File file1 = new File(resultFile);  
            if (!file1.exists()) {  
                return;
            }  
	    	HashMap<String,HashSet<String>> resultMap = new  HashMap<String,HashSet<String>>();
	    	try {
		    	if (resultMap.size()<1)
		    		resultMap= getHashMapFromFile(resultFile);   	
		    	if (resultMap!=null)
		    		printImpactSetHashMap(resultMap);
				//if (allResult.length()>1)
					//System.out.println("All Received Dependencey Sets: \n"+allResult);
	        } catch (Exception e) {  
	            e.printStackTrace();                   
	        }  
	    }
	    
	    public static String findMainClass() throws ClassNotFoundException{
	        for (Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
	            Thread thread = entry.getKey();
	            if (thread.getThreadGroup() != null && thread.getThreadGroup().getName().equals("main")) {
	                for (StackTraceElement stackTraceElement : entry.getValue()) {
	                    if (stackTraceElement.getMethodName().equals("main")) {

	                        try {
	                            Class<?> c = Class.forName(stackTraceElement.getClassName());
	                            Class[] argTypes = new Class[] { String[].class };
	                            //This will throw NoSuchMethodException in case of fake main methods
	                            c.getDeclaredMethod("main", argTypes);
	                            return stackTraceElement.getClassName();
	                        } catch (NoSuchMethodException e) {
	                            e.printStackTrace();
	                        }
	                    }
	                }
	            }
	        }
	        return null;
	    }	    

	    public static String getInter(String oldStr) 
	    {
	    	String newStr=oldStr;
	    	int leftNum=oldStr.length() - oldStr.replace("<", "").length();
	    	int rightNum=oldStr.length() - oldStr.replace(">", "").length();

    		//System.out.println("leftNum=="+leftNum);
    		//System.out.println("rightNum=="+rightNum);
	    	if (leftNum>=2 && rightNum>=2)
	    	{
	    		int leftLoc=oldStr.indexOf("<", oldStr.indexOf("<") + 1);
	    		int rightLoc=oldStr.indexOf(">");
	    		//System.out.println("leftLoc=="+leftLoc);
	    		//System.out.println("rightLoc=="+rightLoc);
	    		String tmpStr=oldStr.substring(leftLoc,rightLoc+1);
	    		if (tmpStr.indexOf(": ")>1)
	    			newStr=tmpStr;
	    	}
	    	
	    	return newStr;
	    }
	    
		   public static void establishMethodIdxMap(String methodFile) {  	   	
		       try {
		   		FileReader reader = null;      
		        BufferedReader br = null;    
		        reader = new FileReader(methodFile);   
		        br = new BufferedReader(reader);
		        String str = "";  
		        String mname="";
		        int index = 0;
		        method2idx=new HashMap< String, Integer >();
		        idx2method=new HashMap< Integer, String >();
		        while((str = br.readLine()) != null)
		        {  
		        	mname=str.trim();
					if (!method2idx.containsKey(mname)) {
						method2idx.put(mname, index);
						idx2method.put(index, mname);
						index ++;
					}
		        }        
   
		       } catch (Exception e) {  
		           e.printStackTrace(); 
		       }  
		   } 
		   
		   public static List<Integer> returnFirstLastEvents(List<Integer> oldList) { 
			   List<Integer> resultList = new LinkedList<Integer>();		 
			   HashSet<Integer> valueS=new HashSet<Integer>();
			   //HashMap<Integer,Integer> valueCount = new HashMap<>(); 		 	   	
			   //HashMap<Integer,Integer> valueLastPosition = new HashMap<>(); 
			   for (int i=0; i<oldList.size(); i++) {
				   Integer valueI=oldList.get(i);
				   if (!valueS.contains(valueI)) {
					   valueS.add(valueI);
					   resultList.add(valueI);
				   }
				   else  {	
					   //System.out.println("i="+i+" valueI="+valueI);
					   boolean isLastValue=true;
					   for (int j=i+1; j<oldList.size(); j++) {

						   //System.out.println("j="+j+" oldList.get(j)="+oldList.get(j)+"i="+i+" valueI="+valueI);
						   if (valueI==oldList.get(j)) {
							   isLastValue=false;
							   break;
						   }
					   }
					   if (isLastValue) {
						   resultList.add(valueI);
					   }
						   
				   }
				   //valueLastPosition.put(valueI, i);
			   }
			   return resultList;				   
		   } 
			public static boolean isHostConnectable(String host, int port) {
		        Socket socket = new Socket();
		        try {
		            socket.connect(new InetSocketAddress(host, port));
		        } catch (IOException e) {
		            //e.printStackTrace();
		        	System.out.println("The network address "+host+":"+port+" cannot be connected!" );
		            return false;
		        } finally {
		            try {
		                socket.close();
		            } catch (IOException e) {
		                //e.printStackTrace();
		            }
		        }
		        System.out.println("The network address "+host+":"+port+" can be connected." );
		        return true;
		    }
			public static boolean isHostConnectable(String hostPort) {
			    Socket socket = new Socket();
			    
			    try {
			    	String[] hostPorts=hostPort.split(":");
			    	if (hostPorts.length>1)
			    	{	
			        	String host=hostPorts[0].trim();        	
			        	int port=Integer.parseInt(hostPorts[1].trim());
			            socket.connect(new InetSocketAddress(host, port));
			    	}
			    	else
			    		return false;
			    } catch (IOException e) {
			        //e.printStackTrace();
			    	System.out.println("The network address "+hostPort+" cannot be connected!" );
			        return false;
			    } finally {
			        try {
			            socket.close();
			        } catch (IOException e) {
			            //e.printStackTrace();
			        }
			    }
		    	System.out.println("The network address "+hostPort+" can be connected." );
			    return true;
			}		   
			public static ArrayList getConnectedAddresses(String[] hostPorts) {
				ArrayList  resultA = new ArrayList(); 
				for (int i=0; i<hostPorts.length; i++) { 
					if (isHostConnectable(hostPorts[i]))
						resultA.add(hostPorts[i]);
				}
				return resultA;				
			}
			public static ArrayList<String> getConnectedAddresses(String[] hosts, String[] ports) {
				ArrayList<String>  resultA = new ArrayList<String>(); 
				int addresslength=hosts.length;
				if (ports.length<addresslength)
					addresslength=ports.length;
				for (int i=0; i<addresslength; i++) { 
					if (isHostConnectable(hosts[i],Integer.parseInt(ports[i])))
						resultA.add(hosts[i]+":"+ports[i]);
				}
				return resultA;				
			}
			
		    public static ArrayList<Long> readTimesFromFile(String dest, String separator) {  
		    	ArrayList<Long>  resultA = new ArrayList<Long>(); 
		    	try
		    	{
			    	String longStr=readToString(dest);
			    	//System.out.println("longStr="+longStr);
			    	String tmpStr="";
			    	String[] strs=longStr.split(separator); 
			       	int strslength=strs.length;	       	
			   		//System.out.println("longStr="+longStr+" strs.length="+strs.length);
					for (int i=0; i<strslength; i++) { 
						tmpStr=strs[i].replaceAll("[^0-9]","");
						try
						{
							resultA.add(Long.parseLong(tmpStr));
						}
						catch (Exception e) {  
				        }  
					}
			    } catch (Exception e) {  
		        }  
		    	return resultA;	
		    }  
		    
		    public static void writeTimesToFile(String dest, String separator, ArrayList<Long> longA) {  
		    	//System.out.println("dest="+dest+" separator="+separator+" longA="+longA);
		    	String longStr="";			       	
				for (int i=0; i<longA.size()-1; i++) { 
					longStr+=longA.get(i)+separator;					
				}
				if (longA.size()>0)  {
					longStr+=longA.get(longA.size()-1);
				}
				//System.out.println("longStr="+longStr);
				writeStringToFile(longStr, dest);
		    }  


		    public static ArrayList<Long> updateTime(Long newValue, int position, ArrayList<Long> oldA) {  
		    	ArrayList<Long>  resultA = new ArrayList<Long>(); 
		    	Long oldValue=(long) 0;

				for (int i=0; i<oldA.size(); i++) { 
					oldValue=oldA.get(i);		
					if (position==i) {
						resultA.add(newValue);
					}
					else
						resultA.add(oldValue);
						
				}		    	
		    	return resultA;	
		    }  
	
		    public static ArrayList<Long> updateLargeTime(Long newValue, int position, ArrayList<Long> oldA) {  
		    	ArrayList<Long>  resultA = new ArrayList<Long>(); 
		    	Long oldValue=(long) 0;

				for (int i=0; i<oldA.size(); i++) { 
					oldValue=oldA.get(i);		
					if (position==i) {
						if (newValue>oldValue)  {
							resultA.add(newValue);
						}
						else
							resultA.add(oldValue);
					}
					else
						resultA.add(oldValue);
						
				}		    	
		    	return resultA;	
		    }  
		    
		    public static ArrayList<Long> initialTimes(int ASize) {  
		    	ArrayList<Long>  resultA = new ArrayList<Long>(); 
		    	
				for (int i=0; i<ASize; i++) { 
					resultA.add((long) 0);
						
				}		    	
		    	return resultA;	
		    }  
		    
		    public static void initialTimesFile(String filePath, int ASize, String separator) {	
		    	ArrayList<Long>  AS=new ArrayList<Long>();
				File file = new File(filePath);
				if(!file.exists()){
					try {
							file.createNewFile();
							AS=initialTimes(ASize);
									
			            } catch (IOException e) {
			           // TODO Auto-generated catch block
			           e.printStackTrace();
			            }	
				}
				else
				{
					ArrayList<Long>  AS2=readTimesFromFile(filePath," ");
					int AS2Size=AS2.size();
					for (int i=0; i<ASize; i++) {						
						if (i<AS2Size) {
							AS.add(AS2.get(i));
						}
						else
							AS.add((long) 0);							
					}		
				}
				//System.out.println("AS="+AS);
				writeTimesToFile(filePath, " ", AS);
		    }  
		    
//			ArrayList<Long> newList=readTimesFromFile("C:/temp/times.txt"," ");
//			System.out.println("newList="+newList);
//			//writeTimesToFile("C:/temp/times2.txt"," ",newList);
			//	ArrayList<Long> newList2=updateTimes((long) 14, 4, newList);
		    public static void updateTimeInFile(String filePath, String separator, Long newValue, int position) {	
		    	//System.out.println("filePath="+filePath+" separator="+separator+" newValue="+newValue+" position="+position);
		    	ArrayList<Long> valueList=readTimesFromFile(filePath," ");
		    	int fileListSize=valueList.size();
		    	//System.out.println("fileListSize="+fileListSize+" position="+position);
		    	if ((position+1)>fileListSize)
		    		fileListSize=position+1;
		    	initialTimesFile(filePath, fileListSize, " ");
		    	//System.out.println("fileListSize="+fileListSize+" position="+position);
		    	
		    	valueList=readTimesFromFile(filePath," ");
		    	ArrayList<Long> newList=updateLargeTime(newValue, position, valueList);
		    	//System.out.println("valueList="+valueList+" newList="+newList);
		    	if (!newList.equals(valueList))
		    		writeTimesToFile(filePath, separator, newList); 
		    }

		    public static void updateTimeInFileForced(String filePath, String separator, Long newValue, int position) {	
		    	//System.out.println("filePath="+filePath+" separator="+separator+" newValue="+newValue+" position="+position);
		    	ArrayList<Long> valueList=readTimesFromFile(filePath," ");
		    	int fileListSize=valueList.size();
		    	//System.out.println("fileListSize="+fileListSize+" position="+position);
		    	if ((position+1)>fileListSize)
		    		fileListSize=position+1;
		    	initialTimesFile(filePath, fileListSize, " ");
		    	//System.out.println("fileListSize="+fileListSize+" position="+position);
		    	
		    	valueList=readTimesFromFile(filePath," ");
		    	ArrayList<Long> newList=updateTime(newValue, position, valueList);
		    	//System.out.println("valueList="+valueList+" newList="+newList);
		    	if (!newList.equals(valueList))
		    		writeTimesToFile(filePath, separator, newList); 
		    }

		    public static ArrayList<Long> updatedTimesInFileForced(String filePath, String separator, Long newValue, int position) {	
		    	//System.out.println("filePath="+filePath+" separator="+separator+" newValue="+newValue+" position="+position);
		    	ArrayList<Long> valueList=readTimesFromFile(filePath," ");
		    	int fileListSize=valueList.size();
		    	//System.out.println("fileListSize="+fileListSize+" position="+position);
		    	if ((position+1)>fileListSize)
		    		fileListSize=position+1;
		    	initialTimesFile(filePath, fileListSize, " ");
		    	//System.out.println("fileListSize="+fileListSize+" position="+position);
		    	
		    	valueList=readTimesFromFile(filePath," ");
		    	ArrayList<Long> newList=updateTime(newValue, position, valueList);
		    	//System.out.println("valueList="+valueList+" newList="+newList);
		    	return newList;
		    }
		    
		    public static int getDecimalFromBinaryTextFile(String configurationFile) {
		    	int resultI=-1;
		    	String configurations=ODDUtil.readToString(configurationFile).replaceAll("[^0-9]","");
		    	//		.trim().replace("\t", "").replace("\n", "").replace("\r", "");
		    	//System.out.println("configurations="+configurations+" configurations.length()="+configurations.length());
		    	try {
		    		resultI=Integer.parseInt(configurations, 2);
		    	} 
		    	catch (Exception e) {
		    		
		    	}
		    	return resultI;
		    }
		
		    public static void updateTimeFromConfigurationToFile(String configurationFile, String filePath, Long newValue) {		
		    	int position=getDecimalFromBinaryTextFile(configurationFile);
		    	//System.out.println("updateTimeFromConfigurationToFile newValue="+newValue+" position="+position);
		    	updateTimeInFile(filePath, " ", newValue, position);
		    }
		    
		    public static void updateTimeFromConfigurationToFileForced(String configurationFile, String filePath, Long newValue) {		
		    	int position=getDecimalFromBinaryTextFile(configurationFile);
		    	//System.out.println("updateTimeFromConfigurationToFile newValue="+newValue+" position="+position);
		    	updateTimeInFileForced(filePath, " ", newValue, position);
		    }
		    
		    public static long getRealTimeFromFile(String configurationFile, String filePath) {		
		    	int position=getDecimalFromBinaryTextFile(configurationFile);
		    	//System.out.println("updateTimeFromConfigurationToFile newValue="+newValue+" position="+position);		    	
		    	ArrayList<Long> valueList=readTimesFromFile(filePath," ");
		    	//System.out.println("filePath="+valueList+" filePath="+valueList+" position="+position);		
		    	if (position>=0 && position<=valueList.size())
		    	{	
		    		return valueList.get(position);
		    	}
		    	else
		    		return -1;
		    }
		    
			public static String readLastLine(String fileName) {  
				FileReader fileReader = null;
				try {
					fileReader = new FileReader(fileName);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				Scanner sc = new Scanner(fileReader);
				String line = null;
				while((sc.hasNextLine()&&(line=sc.nextLine())!=null)){
					if(!sc.hasNextLine()){
						return line;
					}
				}
				sc.close();
		        return "";
		    }  
		    
			public static void copyFileJava7(String src, String dst) {    
				File source=new File(src);
				File dest=new File(dst);
				try {
		            if (dest.exists()) {  
		                dest.delete();
		            } 
					Files.copy(source.toPath(), dest.toPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			public static void createOrCopyFile(String src, String dst) {    
				File source=new File(src);
	            if (!source.exists()) {  
	            	try {
						source.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
	            }  				
				File dest=new File(dst);
				try {
					Files.copy(source.toPath(), dest.toPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			public static void createOrCopyFile(String src, String initialStr, String dst) {    
				//System.out.println("createOrCopyFile  src="+src+" initialStr="+initialStr+" dst="+dst); 
				File source=new File(src);
	            if (!source.exists()) {  
	            	try {
						source.createNewFile();
						if (initialStr.length()>0)  {
							//System.out.println("createOrCopyFile  writeStringToFile initialStr="+initialStr);
							//writeStringToFile(initialStr, src);
					        FileWriter writer = null;  
					        BufferedWriter bw = null;  
				            writer = new FileWriter(src, true); 
				            bw = new BufferedWriter(writer);  
				            bw.write(initialStr);    
				            bw.close();  
				            writer.close();  	
						}	
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
	            }  				
				File dest=new File(dst);
				try {
					Files.copy(source.toPath(), dest.toPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		    public static ArrayList<Long> updatedTimeFromConfigurationForced(String configurationFile, String filePath, Long newValue) {		
		    	int position=getDecimalFromBinaryTextFile(configurationFile);
		    	//System.out.println("updateTimeFromConfigurationToFile newValue="+newValue+" position="+position);
		    	ArrayList<Long> resultA = new ArrayList<Long>();
		    	resultA=updatedTimesInFileForced(filePath, " ", newValue, position);
		    	return resultA;
		    }
		    
		    public static void saveMazeFile(String mazeFile, String oldStr) {
		        final int mazeWidth = 4;
		        FileWriter writer = null;  
		        BufferedWriter bw = null;  
		        String[] strs=oldStr.split(" ");
		        String lineStr="";
		        File file = new File(mazeFile);  
		        try {
			        if (!file.exists()) {  	            
							file.createNewFile();
						
			        }  
			        writer = new FileWriter(mazeFile, false);  	
			        bw = new BufferedWriter(writer);
			        for (int i=0; i<strs.length; i++) {
			        	if (i%mazeWidth==0)
			        		if (lineStr.trim().length()>1)
			        		{
			        			bw.write(lineStr+"\n"); 
			        			lineStr="";
			        		}
			        	lineStr+=strs[i]+" ";
			        }
		    		if (lineStr.trim().length()>1)
		    		{
		    			bw.write(lineStr+"\n"); 
		    			lineStr="";
		    		}
		            bw.close();  
		            writer.close();  
		        } catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}      	
		    }
		    
		    public static void saveMazeFileRewardPenalty (String mazeFile, String oldStr, long expected) {
		        final int mazeWidth = 4;
		        FileWriter writer = null;  
		        BufferedWriter bw = null;  
		        String[] strs=oldStr.split(" ");
		        String lineStr="";
		        File file = new File(mazeFile);  
		        double oldValue=0;
		        double newValue=0;
		        try {
			        if (!file.exists()) {  	            
							file.createNewFile();
						
			        }  
			        writer = new FileWriter(mazeFile, false);  	
			        bw = new BufferedWriter(writer);
			        for (int i=0; i<strs.length; i++) {
			        	if (i%mazeWidth==0)
			        		if (lineStr.trim().length()>1)
			        		{
			        			bw.write(lineStr+"\n"); 
			        			lineStr="";
			        		}
			        	oldValue=Integer.parseInt(strs[i].trim());
			        	newValue=1/(expected-oldValue)*1000;
			        	lineStr+=newValue+" ";
			        }
		    		if (lineStr.trim().length()>1)
		    		{
		    			bw.write(lineStr+"\n"); 
		    			lineStr="";
		    		}
		            bw.close();  
		            writer.close();  
		        } catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}      	
		    }
		    public static void saveRewardPenaltyfromFiles (String mazeFile, String timeFile, long staticLoadExpected, long dynamicExpected, long staticCreateExpected, boolean isStaticLoad, boolean isStaticCreate) {
		    	String times=readLastLine(timeFile);
		    	long expected=dynamicExpected;
		    	if (isStaticLoad) {
		    		expected+=staticLoadExpected;
		    	}

		    	if (isStaticCreate) {
		    		expected+=staticCreateExpected;
		    	}
		    	saveMazeFileRewardPenalty (mazeFile, times, expected);
		    }
		    
		    public static String getNewestFile (String dirName, String fileString) {

		    	File dir=new File(dirName);
		    	if (dirName.length()<1) {
		    		String dirName2=dir.getAbsolutePath();
		    		System.out.println("dirName2="+dirName2);
		    		dir=new File(dirName2);
		    	}	
		    	long modifyTime = (long) 0;
		    	String newFileName="";
				if(!dir.exists()) {
					System.out.println(dirName+ " doesn't exist!");
				}else if(dir.isFile()){
					System.out.println(dirName+ " is a file!");
				}else {
					File [] arr=dir.listFiles();
					for (File file : arr) {//
						if(file.isFile()&& file.getName().indexOf(fileString)>=0) {
							if (fileString.length()<1 || (fileString.length()>=1 && file.getName().indexOf(fileString)>=0)) {
								//System.out.println("file="+file+" file.lastModified()="+file.lastModified()+" modifyTime="+modifyTime);
								if (file.lastModified() > modifyTime) {
									modifyTime=file.lastModified();
								    newFileName=file.getName();
								    System.out.println("file="+file+" file.lastModified()="+file.lastModified()+" modifyTime="+modifyTime+" newFileName="+newFileName);
								}	
							}
						}
					}	
				}	
				return newFileName;
		    }
		    
		    public static HashSet<String> getQueryResult(String query, String resultFile)
		    {
		    	HashSet<String> oneSet = new HashSet<>();
		        FileReader reader = null;  
		        BufferedReader br = null;  		        
		        try { 

		            reader = new FileReader(resultFile);
		            String str = null;     
		            String midStr="";
		            br = new BufferedReader(reader);
			        boolean findQuery=false;
		            while ((str = br.readLine()) != null) {   
		            	if (str.indexOf(query)>0 && str.indexOf("size=")>1)
		            	{
		            		findQuery=true;
		            	}
		            	else if (str.indexOf(query)<0 && str.indexOf("DistODD impact set of")>1 && str.indexOf("size=")>1 )
		            	{
		            		findQuery=false;
		            	}		            	
		            	else
		            	{
		            		if (findQuery)  {
		            			midStr=str.trim().replace("\n","").replace("\r","").replace("\t","");
		            			if (midStr.startsWith("<") &&  midStr.endsWith(">")) 
		            				oneSet.add(midStr);
		            		}	
		            	}
		            
		            }  
		   
		        } catch (Exception e) {  
		            e.printStackTrace();  
		        }

		    	return oneSet;
		    }
		    
		    public static HashSet<String> getQueryResultFromDir(String query, String dirName) {
		    	HashSet<String> allSet = new HashSet<>();
		    	File dir=new File(dirName);

	    		String dirName2=dir.getAbsolutePath();
	    		//System.out.println("dirName2="+dirName2);
	    		dir=new File(dirName2);


				if(!dir.exists()) {
					System.out.println(dirName+ " doesn't exist!");
				}else if(dir.isFile()){
					System.out.println(dirName+ " is a file!");
				}else {
					File [] arr=dir.listFiles();
					for (File file : arr) {//
						if(file.isFile()&& file.getName().startsWith("allQueryResult")) {
							allSet.addAll(getQueryResult(query, dirName2+File.separator+file.getName()));
						}
					}	
				}	
		    	return allSet;
		    }
		    
//		    public static void assignBudgetToTimoutFiles(int msNumber, String timeFile)  {
//		    	if (msNumber<10)
//		    		return;
//		    	int timeStaticCreate=(int)(msNumber*0.7);
//		    	int timeStaticLoad=(int)(msNumber*0.1);
//		    	int timeDynamic=(int)(msNumber*0.2);
//		    	String timeoutsStr="StaticGraphCreate "+timeStaticCreate+" Dynamic "+timeDynamic+" StaticGraphLoad "+timeStaticLoad;
//		    	writeStringToFile(timeoutsStr, timeFile);
//		    	
//		    }
//		    public static void saveMazeFileRewardPenaltyXF (String mazeFile, String oldStr, int expected, int Xi, int Fi) {
//		        final int mazeWidth = 4;
//		        FileWriter writer = null;  
//		        BufferedWriter bw = null;  
//		        String[] strs=oldStr.split(" ");
//		        String lineStr="";
//		        File file = new File(mazeFile);  
//		        double oldValue=0;
//		        double newValue=0;
//		        double maxValue=0;
//		        int maxI=-1;
//		        try {
//			        if (!file.exists()) {  	            
//							file.createNewFile();
//						
//			        }  
//			        writer = new FileWriter(mazeFile, false);  	
//			        bw = new BufferedWriter(writer);
//			        for (int i=0; i<strs.length-1; i++) {
//			        	if (i%mazeWidth==0)
//			        		if (lineStr.trim().length()>1)
//			        		{
//			        			bw.write(lineStr+"\n"); 
//			        			lineStr="";
//			        		}
//			        	oldValue=Integer.parseInt(strs[i].trim());
//			        	newValue=1/(expected-oldValue)*1000;
//			        	if (newValue>maxValue) {
//			        		maxValue=newValue;
//			        		maxI=i;
//			        	}
//			        	if (i==Xi) {
//			        		lineStr+="X ";
//			        	}
//			        	else if (i==Fi) {
//			        		lineStr+="Fi ";
//			        	}
//			        	else	
//			        		lineStr+=(int)newValue+" ";
//			        }
//			        
//		    		if (lineStr.trim().length()>1)
//		    		{
//		    			bw.write(lineStr+"\n"); 
//		    			lineStr="";
//		    		}
//		            bw.close();  
//		            writer.close();  
//		        } catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}      	
//		    }


}
