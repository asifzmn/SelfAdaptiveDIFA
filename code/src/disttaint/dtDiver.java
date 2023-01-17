package disttaint;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class dtDiver {
	 public static void main(String []args) {
//		 LinkedHashMap<String,Integer> resultMethodSeq=getEASequence("C:/Research/nioecho/Local1533146325582.em");
//		 System.out.println("resultMethodSeq="+resultMethodSeq.size());
//		 for (Map.Entry<String, Integer> en : resultMethodSeq.entrySet()) 
//			 System.out.println(en.getKey()+" "+en.getValue());
//		 HashMap EAs=getEAs(resultMethodSeq);
//		LinkedHashMap  EAmethod2idx = (LinkedHashMap)EAs.get(1);
//		LinkedHashMap  EASeq = (LinkedHashMap)EAs.get(2);
//		System.out.println("EAmethod2idx.size()="+EAmethod2idx.size()+" EAmethod2idx="+EAmethod2idx);
//		System.out.println("EASeq.size()="+EASeq.size()+" EASeq="+EASeq);
//		serializeEvents("C:/Research/nioecho/Local1.em", EAmethod2idx, EASeq);
//		LinkedHashMap<String,Integer> resultMethodSeq2=getEASequence("C:/Research/nioecho/Local1533146319680.em");
//		 System.out.println("resultMethodSeq2="+resultMethodSeq2.size());
//		 for (Map.Entry<String, Integer> en : resultMethodSeq2.entrySet()) 
//			 System.out.println(en.getKey()+" "+en.getValue());
//		 LinkedHashMap<String,Integer> resultMethodSeq3=getMergedMethodSeq(resultMethodSeq, resultMethodSeq2); 
//		 System.out.println("resultMethodSeq3="+resultMethodSeq3.size());
//		 for (Map.Entry<String, Integer> en : resultMethodSeq3.entrySet()) 
//			 System.out.println(en.getKey()+" "+en.getValue());
//		 
//		LinkedHashMap<String,Integer> resultMethodSeq4=getEASequence("C:/Research/nioecho/Local1533146325582.em");
//		 System.out.println("resultMethodSeq4="+resultMethodSeq4.size());
//		 for (Map.Entry<String, Integer> en : resultMethodSeq4.entrySet()) 
//			 System.out.println(en.getKey()+" "+en.getValue());
//		 LinkedHashMap<String,Integer> resultMethodSeq5=getMergedMethodSeq(resultMethodSeq3, resultMethodSeq4); 
//		 System.out.println("resultMethodSeq5="+resultMethodSeq5.size());
//		 for (Map.Entry<String, Integer> en : resultMethodSeq5.entrySet()) 
//			 System.out.println(en.getKey()+" "+en.getValue());
//		 mergeTraceFile("C:/Research/nioecho", "Local", ".em", "C:/Research/nioecho/Local1.em");
//		 LinkedHashMap<String,Integer> resultMethodSeq=getEASequence("C:/Research/nioecho/Local1.em");
//		 System.out.println("resultMethodSeq="+resultMethodSeq.size());
//		 for (Map.Entry<String, Integer> en : resultMethodSeq.entrySet()) 
//			 System.out.println(en.getKey()+" "+en.getValue());
		 HashMap EAs=getEAs("C:/Research/nioecho", "Local", ".em");
		LinkedHashMap  EAmethod2idx = (LinkedHashMap)EAs.get(1);
		LinkedHashMap  EASeq = (LinkedHashMap)EAs.get(2);
		System.out.println("EAmethod2idx.size()="+EAmethod2idx.size()+" EAmethod2idx="+EAmethod2idx);
		System.out.println("EASeq.size()="+EASeq.size()+" EASeq="+EASeq);
		 
	 }
	public static void mergeTraceFile(String traceDir, String startsWith, String endsWith, String sumFile)  {
		 File file = new File(sumFile);	     
	     if (file.exists() && file.isFile())
	    	 file.delete();
		List<String> fileIds=dtUtil.getFileIdSort(traceDir, "Local", ".em");	
//		System.out.println("fileIds="+fileIds);
		LinkedHashMap<String,Integer> allMethodSeq=new LinkedHashMap<String,Integer>();
		String fileName="";
		for (String idStr : fileIds) {
			fileName=traceDir  + File.separator + startsWith + idStr + endsWith;
			//System.out.println("traceDir="+traceDir+" idStr="+idStr+" fileName="+fileName);
			LinkedHashMap<String,Integer> oneTraceMethodSeq=getEASequence(fileName);
			LinkedHashMap<String,Integer> newMethodSeq=getMergedMethodSeq(allMethodSeq, oneTraceMethodSeq);
			allMethodSeq=(LinkedHashMap)newMethodSeq.clone();
		}
		System.out.println("allMethodSeq.size()="+allMethodSeq.size());
		for (Map.Entry<String, Integer> en : allMethodSeq.entrySet()) 
			 System.out.println(en.getKey()+" "+en.getValue());
		HashMap EAs=getEAs(allMethodSeq);
		LinkedHashMap  EAmethod2idx = (LinkedHashMap)EAs.get(1);
		LinkedHashMap  EASeq = (LinkedHashMap)EAs.get(2);
		System.out.println("EAmethod2idx.size()="+EAmethod2idx.size()+" EAmethod2idx="+EAmethod2idx);
		System.out.println("EASeq.size()="+EASeq.size()+" EASeq="+EASeq);
		serializeEvents(sumFile, EAmethod2idx, EASeq);
	}
	 
	public static HashMap getEAs(String traceDir, String startsWith, String endsWith) {
		//System.out.println("traceDir="+traceDir);
		HashMap map = new HashMap();
		List<String> fileIds=dtUtil.getFileIdSort(traceDir, "Local", ".em");	
		//System.out.println("fileIds="+fileIds);
		LinkedHashMap<String,Integer> allMethodSeq=new LinkedHashMap<String,Integer>();
		String fileName="";
		for (String idStr : fileIds) {
			if (traceDir.length()>0)   {
				fileName=traceDir  + File.separator + startsWith + idStr + endsWith;
			}
			else
				fileName=startsWith + idStr + endsWith;
			//System.out.println("traceDir="+traceDir+" idStr="+idStr+" fileName="+fileName);
			LinkedHashMap<String,Integer> oneTraceMethodSeq=getEASequence(fileName);
			//System.out.println("oneMethodSeq.size()="+oneTraceMethodSeq.size());
			LinkedHashMap<String,Integer> newMethodSeq=getMergedMethodSeq(allMethodSeq, oneTraceMethodSeq);
			//System.out.println("newMethodSeq.size()="+newMethodSeq.size());
			allMethodSeq=(LinkedHashMap)newMethodSeq.clone();
			//System.out.println("allMethodSeq.size()="+allMethodSeq.size());
		}
//		for (Map.Entry<String, Integer> en : allMethodSeq.entrySet()) 
//			 System.out.println(en.getKey()+" "+en.getValue());
		HashMap EAs=getEAs(allMethodSeq);
		LinkedHashMap  EAmethod2idx = (LinkedHashMap)EAs.get(1);
		LinkedHashMap  EASeq = (LinkedHashMap)EAs.get(2);
		map.put(1,EAmethod2idx);
        map.put(2,EASeq);
        return map;   
	}
	
	public static int getEAMaxSizes(String traceDir, String startsWith, String endsWith) {
		HashMap map = new HashMap();
		List<String> fileIds=dtUtil.getFileIdSort(traceDir, "Local", ".em");	
		String fileName="";
		int maxSize=0;
		for (String idStr : fileIds) {
			if (traceDir.length()>0)   {
				fileName=traceDir  + File.separator + startsWith + idStr + endsWith;
			}
			else
				fileName=startsWith + idStr + endsWith;
			//System.out.println("traceDir="+traceDir+" idStr="+idStr+" fileName="+fileName);
			int localMax=getEASequenceSize(fileName);
			if (localMax>maxSize)
				maxSize=localMax;
			
		}
        return maxSize;   
	}
	
	protected static int getEASequenceSize(String fnSource) {
		FileInputStream fis;
		int resultSize=0;
		try {
			fis = new FileInputStream(fnSource);
			
			GZIPInputStream gis = new GZIPInputStream(fis);
			int len = 1024;
			int ziplen = (int)new File(fnSource).length();
			byte[] bs = new byte[ziplen*20]; // Gzip won't have more than 20 compression ratio for the binary data
			int off = 0;
			while (gis.available()!=0) {
				off += gis.read(bs, off, len);
			}
			gis.close();
			fis.close();

			ByteArrayInputStream bis = new ByteArrayInputStream(bs);
			
			//ObjectInputStream ois = new ObjectInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(bis);
			System.out.println(fnSource+" ois="+ois);
			
			@SuppressWarnings("unchecked")
			LinkedHashMap<String,Integer> readObject1 = (LinkedHashMap<String,Integer>) ois.readObject();
			System.out.println(fnSource+" readObject1.size()="+readObject1.size());
			//Map<String,Integer> EAmethod2idx = new LinkedHashMap<String,Integer>();		
			
			LinkedHashMap<Integer, Integer> readObject2 = (LinkedHashMap<Integer, Integer>) ois.readObject();
			System.out.println(fnSource+" readObject2.size()="+readObject2.size());
			resultSize=readObject2.size();
			
			ois.close();
			bis.close();
			// --
		}
		catch (FileNotFoundException e) { 
			System.err.println("Failed to locate the given input EAS trace file " + fnSource);
		}
		catch (ClassCastException e) {
			System.err.println("Failed to cast the object deserialized to LinkedHashMap<Integer, String>!");
		}
		catch (IOException e) {
			System.err.println("No GZIP!");
			//throw new RuntimeException(e); 
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultSize;
	}
	/**
	 * load execution trace to be used for exercising the static VTG into a dynamic one
	 * @param fnSource the source disk file holding an EA execution trace 
	 * @return 0 for success and others for failure, different values indicating different reasons
	 */
	protected static LinkedHashMap<String, Integer> getEASequence(String fnSource) {
		LinkedHashMap<Integer, Integer> EASeq = new LinkedHashMap<Integer, Integer>();
		LinkedHashMap<Integer, String> EAMethodMap = new LinkedHashMap<Integer, String>();
		LinkedHashMap<String,Integer> EAmethod2idx = new LinkedHashMap<String,Integer>();
		LinkedHashMap<String,Integer> resultMethodSeq = new LinkedHashMap<String,Integer>();
		FileInputStream fis;
		try {
			fis = new FileInputStream(fnSource);
			
			GZIPInputStream gis = new GZIPInputStream(fis);
			int len = 1024;
			int ziplen = (int)new File(fnSource).length();
			byte[] bs = new byte[ziplen*20]; // Gzip won't have more than 20 compression ratio for the binary data
			int off = 0;
			while (gis.available()!=0) {
				off += gis.read(bs, off, len);
			}
			gis.close();
			fis.close();

			ByteArrayInputStream bis = new ByteArrayInputStream(bs);
			
			//ObjectInputStream ois = new ObjectInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(bis);
			
			//Map<String,Integer> EAmethod2idx = new LinkedHashMap<String,Integer>();
			@SuppressWarnings("unchecked")
			LinkedHashMap<String,Integer> readObject1 = (LinkedHashMap<String,Integer>) ois.readObject();
			EAmethod2idx = readObject1;
			//System.out.println("getEASequence EAmethod2idx().size()="+EAmethod2idx.size()+" getEASequence EAmethod2idx()="+EAmethod2idx);
			for (Map.Entry<String, Integer> en : EAmethod2idx.entrySet()) {
				// create an inverse map for facilitating quick retrieval later on
				EAMethodMap.put(en.getValue(), en.getKey());
			}
			//System.out.println("getEASequence EAMethodMap().size()="+EAMethodMap.size()+" getEAMethodMapuence EAMethodMap()="+EAMethodMap);
			@SuppressWarnings("unchecked")
			LinkedHashMap<Integer, Integer> readObject2 = (LinkedHashMap<Integer, Integer>) ois.readObject();
			EASeq = readObject2;
			//System.out.println("getEASequence EASeq().size()="+EASeq.size()+" getEASequence EASeq()="+EASeq);
			String emstr = "";
			for (Map.Entry<Integer, Integer> _event : EASeq.entrySet()) {
				Integer va = _event.getValue();
				//if (va.equalsIgnoreCase("program start") || va.equalsIgnoreCase("program end")) {
				if (va == Integer.MIN_VALUE || va == Integer.MAX_VALUE) {
					// these are just two special events marking start and termination of the run
					continue;
				}			
				emstr = EAMethodMap.get(Math.abs(va));
				resultMethodSeq.put(emstr,va);
			}	
			ois.close();
			bis.close();
			// --
		}
		catch (FileNotFoundException e) { 
			System.err.println("Failed to locate the given input EAS trace file " + fnSource);
		}
		catch (ClassCastException e) {
			System.err.println("Failed to cast the object deserialized to LinkedHashMap<Integer, String>!");
		}
		catch (IOException e) {
			System.err.println("No GZIP!");
			//throw new RuntimeException(e); 
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultMethodSeq;
	}

	public static HashMap getEAs(LinkedHashMap<String, Integer> methodSeq) {
		LinkedHashMap  EAmethod2idx = new LinkedHashMap(); 
		LinkedHashMap  EASeq = new LinkedHashMap(); 
		HashMap map = new HashMap();
        FileReader reader = null;  
        BufferedReader br = null;     
        String emstr = "";
        int count=1;
        try {  
			for (Map.Entry<String, Integer> en : methodSeq.entrySet()) {
				// create an inverse map for facilitating quick retrieval later on
				emstr = en.getKey();
				EAmethod2idx.put(emstr, count);
				EASeq.put(count,en.getValue());
				count++;
			}  
            map.put(1,EAmethod2idx);
            map.put(2,EASeq);
            return map;   
        } catch (Exception e) {  
            e.printStackTrace();   
            return map;    
        }  
	}
	
	protected static void serializeEvents(String fnEventMaps, LinkedHashMap<String,Integer> S, LinkedHashMap<Integer, Integer> A) {
		System.out.println("fnEventMaps="+fnEventMaps);
		FileOutputStream fos;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			//fos = new FileOutputStream(fnEventMaps+(g_traceCnt>0?g_traceCnt:""));
			fos = new FileOutputStream(fnEventMaps);
			GZIPOutputStream goos = new GZIPOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			// TreeMap is not serializable as is HashMap
			// for Diver, we need the full sequence of events instead of the EA sequence only
			
			// First we need the method index for indexing methods because the full sequence does not contain method name
			oos.writeObject(S);
			oos.writeObject(A);
			oos.flush();
			oos.close();
			
			goos.write(bos.toByteArray());
			bos.flush();
			bos.close();
			
			goos.flush();
			goos.close();
			
			fos.flush();
			fos.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {

		}		
	}
	
	public static LinkedHashMap<String, Integer> getMergedMethodSeq(LinkedHashMap<String, Integer> methodSeq1,LinkedHashMap<String, Integer> methodSeq2) {
		LinkedHashMap<String,Integer> resultMethodSeq = new LinkedHashMap<String,Integer>();
    	Set<String> Strings1=new HashSet<String>();
    	Set<Integer> Integers1=new HashSet<Integer>();
    	Set<String> Strings2=new HashSet<String>();
    	Set<Integer> Integers2=new HashSet<Integer>();
        String emStr1 = "";
        Integer event1=Integer.MIN_VALUE;
        String emStr2 = "";
        Integer event2=Integer.MAX_VALUE;
        try {  
			for (Map.Entry<String, Integer> en : methodSeq1.entrySet()) {
				// get key and value sets of first method sequence
				Strings1.add(en.getKey());	
				Integers1.add(en.getValue());
			}    
			for (Map.Entry<String, Integer> en : methodSeq2.entrySet()) {
				// get key and value sets of second method sequence
				Strings2.add(en.getKey());	
				Integers2.add(en.getValue());
			}    
			// add events from first method sequence
			for (Map.Entry<String, Integer> en : methodSeq1.entrySet()) {
				emStr1=en.getKey();
				event1=en.getValue();
				// two method sequences have same methods
				if (Strings2.contains(emStr1))  {
					emStr2=emStr1;
					event2= methodSeq2.get(emStr2);
					// add small event from first method sequence
					if (event1<event2)  {
						resultMethodSeq.put(emStr1, event1);
					}
					else
						resultMethodSeq.put(emStr1, event2);
				}
				else	// two method sequences have different methods
					resultMethodSeq.put(emStr1, event1);						
			}        
			// add events from second method sequence
			for (Map.Entry<String, Integer> en : methodSeq2.entrySet()) {
				emStr2=en.getKey();
				event2=en.getValue();
				// two method sequences have same methods
				if (Strings1.contains(emStr2))  {
					emStr1=emStr2;
					event1= methodSeq1.get(emStr1);
					if (event1<event2)  {   // add big event from second method sequence
						resultMethodSeq.put(emStr2, event2);
					}
					else
						resultMethodSeq.put(emStr2, event1);
				}
				else	
					resultMethodSeq.put(emStr2, event2);						
			}    
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return resultMethodSeq;
	}
}

