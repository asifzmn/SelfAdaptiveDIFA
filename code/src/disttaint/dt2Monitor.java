package disttaint;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;

//import MciaUtil.MethodEventComparator;
import java.util.concurrent.atomic.AtomicInteger;

import MciaUtil.MethodEventComparator;
/** Monitoring method events in runtime upon 
 * invocations by instrumented probes in the subject
 *
 * For Diver, this monitor captures full sequence of method events. For a uniform basis, also
 * to track two kinds of events only: entrance (first event) and return-into (last event), as EAS does
 */

public class dt2Monitor {
	protected static final long CN_LIMIT = 1000*1000*1000;
	protected static int g_traceCnt = 0;
	
	/* the full list of functions which the full event map will index for retrieving the functions (their signature) themselves */
	protected static HashMap<String, Integer> S = new LinkedHashMap<String, Integer>();
	protected static Integer g_index = 1;
	protected static Integer g_counter = 0;
	/* all events */
	protected static HashMap<Integer, Integer> A = new LinkedHashMap<Integer, Integer>();
	
//	/* last events */
//	protected static HashMap<String, Integer> L = new HashMap<String, Integer>();
//	// distEA events
//	/* first events */
//	protected static HashMap<String, Integer> F = new HashMap<String, Integer>();
//	/* all events */
//	protected static HashMap<Integer, String> A_distEA = new LinkedHashMap<Integer, String>();
	
	
	/* two special events */
	public static final int PROGRAM_START = Integer.MIN_VALUE;
	public static final int PROGRAM_END = Integer.MAX_VALUE;
	
	/* debug flag: e.g. for dumping event sequence to human-readable format for debugging purposes, etc. */
	protected static boolean debugOut = false;
	public static void turnDebugOut(boolean b) { debugOut = b; }

	/* output file for serializing the two event maps */
	protected static String fnEventMaps = "";

    // dist variables
	/* the global counter for time-stamping each method event */
//	protected static Integer g_counter_distEA = 0;
//	protected static String fnEventMaps_distEA = "";
	/* a flag ensuring the initialization and termination are both executed exactly once and they are paired*/
	protected static boolean bInitialized = false;
	
	// distEA variables
//	protected static boolean bFullSequence = false;
//	protected static boolean dumpEvents = false;
//	protected static boolean forcingMirrorIO = false;
//	private static boolean useSingleFlag = false; // force mirror i/o for both socket and NIO network I/Os or mirror each type separately only
//	protected static boolean usingToken = true; // use a token at the end of each clock message for identification and verification
//	
//	protected static boolean trackingSender = true; // send the identify of message sender with messages being sent
//	protected static boolean runDiverOut = false; // runDiver
	public static String receivedMessages="";
	/* The name of serialization target file will be set by EARun via this setter */
//	public static void setEventMapSerializeFile(String fname) {
//		fnEventMaps = fname;
//		
//		if (cachingOIDs) {
//			fnObjectIds = fname + "o";
//		}
//		
//		resetInternals();
////		F.clear();
////		L.clear();
////		A_distEA.clear();		
////		g_lgclock.initClock(1);
//	}
	
	
	/* for DUAF/Soot to access this class */
	public static void __link() { }
	
	/* clean up internal data structures that should be done so for separate dumping of them, a typical such occasion is doing this per test case */
	private synchronized static void resetInternals() {
		S.clear();
		A.clear();
		if (cachingOIDs) {
			if (!instanceLevel) memCache.clear();
			else memCacheIns.clear();
		}
		
		synchronized (g_index) {
			g_index = 1;
		}
		
		synchronized (g_counter) {
			g_counter = 1;
		}
		
		g_traceCnt = 0;
	}
	
	/* initialize the two maps and the global counter upon the program start event */		
	public synchronized static void initialize() throws Exception{
		S.clear();
		A.clear();
		
		
		if (cachingOIDs) {
			if (!instanceLevel) memCache.clear();
			else memCacheIns.clear();
		}
		
		synchronized (g_index) {
			g_index = 1;
		}
		
		synchronized (g_counter) {
			g_counter = 1;
			A.put(g_counter, PROGRAM_START);
//			if (bFullSequence) {
//				A_distEA.put(g_counter, "program start");
//			}
			g_counter++;
		}
		
		g_traceCnt = 0;
		bInitialized = true;
		
//		//distE initialize()
//		F.clear();
//		L.clear();
//		A_distEA.clear();
		bInitialized = true;
		//g_lgclock.initClock(1);
		//System.out.println("**************dt2Monitor::initialize()  2th");
		/** add hook to catch SIGKILL/SIGTERM */
		Runtime.getRuntime().addShutdownHook( new Thread()
        {
          public void run()
          {
            // System.out.println( "Shutdown signal caught!" ) ;
        	/** guarantee that the trace, if any collected, gets dumped */
        	if (debugOut) {
        		System.out.println("\nDumping method event trace of current process execution ...");
        	}
    		try {
    			bInitialized = true;
				dt2Monitor.terminate("Forced upon external termination");
			} catch (Exception e) {
				e.printStackTrace();
			}
          }
        } ) ;
		
		String debugFlag = System.getProperty("ltsDebug");
		System.out.println("debugFlag="+debugFlag);
		if (null != debugFlag) {
			debugOut = debugFlag.equalsIgnoreCase("true");
		}
		String dumpFlag = System.getProperty("ltsDump");
//		if (null != dumpFlag) {
//			dumpEvents = dumpFlag.equalsIgnoreCase("true");
//		}
//		String fmFlag = System.getProperty("forceMirror");
//		if (null != fmFlag) {
//			forcingMirrorIO = fmFlag.equalsIgnoreCase("true");
//		}
//		String mfFlag = System.getProperty("uniformMirror");
//		if (null != mfFlag) {
//			useSingleFlag = mfFlag.equalsIgnoreCase("true");
//		}
//		
//		String tkFlag = System.getProperty("useToken");
//		if (null != tkFlag) {
//			usingToken = tkFlag.equalsIgnoreCase("true");
//		}
//		
//		String ttFlag = System.getProperty("trackSender");
//		if (null != ttFlag) {
//			trackingSender = ttFlag.equalsIgnoreCase("true");
//		}
//		
//		String rdFlag = System.getProperty("ltsRunDiver");
//		System.out.println("rdFlag="+rdFlag);
//		if (null != rdFlag) {
//			runDiverOut = rdFlag.equalsIgnoreCase("true");
//		}
		
		//debugOut=true;
		//usingToken=true;
		//System.out.println("runDiverOut="+runDiverOut);
		System.out.println("debugOut="+debugOut);
		System.out.println("dt2Monitor starts working ......");
		//trackingSender=true;
		dtSocketInputStream.debugOut = debugOut;
		//dtSocketInputStream.usingToken = usingToken;
		
		dtSocketInputStream.intercept = g_intercept;
		dtSocketOutputStream.intercept = g_intercept;
		
//		if (trackingSender) {
//			// just record the ID of local process 
//			//System.out.println("dt2Monitor S.put(getProcessID(), Integer.MAX_VALUE) getProcessID()="+getProcessID());
//			S.put(getProcessID(), Integer.MAX_VALUE);
//		}
		
	}
	
	public synchronized static void enter(String methodname){
		//System.out.println("\n enter(String methodname)="+methodname);
		try {
			synchronized (g_index) {
				if (!S.containsKey(methodname)) {
					S.put(methodname, g_index);
					g_index ++;
				}
			}
			synchronized (g_counter) {
				assert S.containsKey(methodname);
				A.put(g_counter, S.get(methodname)*-1);  // negative index for entry event
				g_counter ++;
				
				if (g_counter > CN_LIMIT) {
					serializeEvents();
				}
			}

//			if (active) return;
//			active = true;
//			try { enter_impl(methodname); }
//			finally { active = false; }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	public synchronized static void enter_impl(String methodname) {
//		//System.out.println("\n enter_impl(String methodname)="+methodname);
//		try {
//			synchronized (g_counter_distEA) {
//				Integer curTS = (Integer) F.get(methodname);
//				if (null == curTS) {					
//					curTS = 0;
//					//System.out.println("\n enter_impl F.put(methodname, g_counter_distEA) methodname="+methodname+" g_counter_distEA="+g_counter_distEA);
//					F.put(methodname, g_counter_distEA);
//				}
//				g_counter_distEA = g_lgclock.getLTS();
//				//System.out.println("\n enter_impl L.put(methodname, g_counter_distEA) methodname="+methodname+" g_counter_distEA="+g_counter_distEA);
//				L.put(methodname, g_counter_distEA);	
//				if (bFullSequence) {
//					//System.out.println("\n enter_impl A_distEA.put(g_counter_distEA, methodname:e) methodname="+methodname+" g_counter_distEA="+g_counter_distEA);
//					A_distEA.put(g_counter_distEA, methodname+":e");
//				}
//				g_counter_distEA ++;
//				g_lgclock.increment();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
//	public synchronized static void returnFrom(String methodname, String calleeName) {
//		if (active) return;
//		active = true;
//		try { returnFrom_impl(methodname, calleeName); }
//		finally { active = false; }
//	}
//
//	/** returnFrom events are handled the same as are returnInto events */
//	public synchronized static void returnFrom_impl(String methodname, String calleeName){
//		try {
//			synchronized (g_counter_distEA) {
//				Integer curTS = (Integer) L.get(methodname);
//				if (null == curTS) {
//					curTS = 0;
//				}
//				g_counter_distEA = g_lgclock.getLTS();
//				
//				L.put(methodname, g_counter_distEA);
//				if (bFullSequence) {
//					A_distEA.put(g_counter_distEA, methodname+":x");
//				}
//	
//				g_counter_distEA ++;
//				g_lgclock.increment();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	/* the callee could be either an actual method called or a trap */
	
	public synchronized static void returnInto(String methodname, String calleeName){
		try {
			synchronized (g_counter) {
				assert S.containsKey(methodname);
				A.put(g_counter, S.get(methodname)*1);  // positive index for returned-into event
				g_counter ++;
				
				if (g_counter > CN_LIMIT) {
					serializeEvents();
				}
			}
			

//			if (active) return;
//			active = true;
//			try { returnInto_impl(methodname, calleeName); }
//			finally { active = false; }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	/* the callee could be either an actual method called or a trap */
//	public synchronized static void returnInto_impl(String methodname, String calleeName){
//		try {
//			synchronized (g_counter_distEA) {
//				Integer curTS = (Integer) L.get(methodname);
//				if (null == curTS) {
//					curTS = 0;
//				}
//				g_counter_distEA = g_lgclock.getLTS();
//				
//				L.put(methodname, g_counter_distEA);
//				if (bFullSequence) {
//					A_distEA.put(g_counter_distEA, methodname+":i");
//				}
//	
//				g_counter_distEA ++;
//				g_lgclock.increment();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	public static Object getMap() { return A.clone(); }
	
	/* 
	 * dump the Execute-After sequence that is converted from the two event maps 
	 * upon program termination event 
	 * this is, however, not required but useful for debugging 
	 *
	 */
	public synchronized static void terminate(String where) throws Exception {
		/** NOTE: we cannot call simply forward this call to super class even though we do the same thing as the parent, because
		 * we need take effect the overloaded SerializeEvents() here below 
		 */
		//Monitor.terminate(where);
		if (bInitialized) {
			bInitialized = false;
		}
		else {
			return;
		}

		synchronized (g_counter) {
			A.put(g_counter, PROGRAM_END);
		}
		
		if (debugOut) {
			dumpEvents();
		}
		//if (runDiverOut) 
		{		
		// set Diver trace file name
			System.out.println("fnEventMaps="+fnEventMaps);
			if (fnEventMaps.length()<1) {
				String fndefault = System.getProperty("user.dir") + File.separator + "Local";
				fndefault += System.currentTimeMillis() + ".em";
				fnEventMaps = fndefault;
			}				
			serializeEvents();
		}
		// distEA trace file
//		synchronized (g_counter_distEA) {
//			g_counter_distEA = g_lgclock.getLTS();
//			
//			A_distEA.put(g_counter, "program end");
//		}
//		if (dumpEvents) {
//			dist_dumpEvents();
//		}
//		if (fnEventMaps_distEA.length()<1) {
//			String fndefault = System.getProperty("user.dir") + File.separator + "test1/Remote";
//			fndefault += System.currentTimeMillis() + ".em";
//			fnEventMaps_distEA = fndefault;
//		}
//		dist_serializeEvents();
	}
	
	protected synchronized static void dumpEvents() {
		System.out.println("\n[ Method Index ]\n");
		System.out.println(S);
		System.out.println("\n[ Full Sequence of Method Entry and Returned-into Events]\n");
		TreeMap<Integer, Integer> treeA = new TreeMap<Integer, Integer> ( A );
		System.out.println(treeA);
		// DEBUG ONLY
		Map<Integer, String> idx2me = new LinkedHashMap<Integer, String>();
		for (String me : S.keySet()) {
			idx2me.put(S.get(me), me);
		}
		for (Integer ts : treeA.keySet()) {
			int idx = treeA.get(ts);
			String mname = idx2me.get(Math.abs(idx)) + (idx>0?":i":":e");
			if (idx == PROGRAM_START) mname = "program start";
			if (idx == PROGRAM_END) mname = "program end";
			System.out.println(ts+"\t"+ mname);
		}
	}
//	protected synchronized static void dist_dumpEvents() {
//		MethodEventComparator mecF = new MethodEventComparator(F);
//		MethodEventComparator mecL = new MethodEventComparator(L);
//		TreeMap<String, Integer> sortedF = new TreeMap<String, Integer> ( mecF );
//		TreeMap<String, Integer> sortedL = new TreeMap<String, Integer> ( mecL );
//		sortedF.putAll(F);
//		sortedL.putAll(L);
//		
//		System.out.println("\n\n[ First events ]\n" + sortedF );
//		System.out.println("\n[ Last events ]\n" + sortedL );
//
//		/* put two maps into one but reversed map for producing the EA sequence */
//		HashMap<Integer, String> FL = new HashMap<Integer, String>();
//
//		//ArrayList<String> allMethods = new ArrayList<String>(F.keySet());
//		//allMethods.addAll( L.keySet() );
//		for( Map.Entry<String, Integer> entry : F.entrySet() ) {
//			FL.put( entry.getValue(), entry.getKey() );
//		}
//		for( Map.Entry<String, Integer> entry : L.entrySet() ) {
//			FL.put( entry.getValue(), entry.getKey() );
//		}
//
//		System.out.println("\n[ Whole Execute-After Sequence ]\n");
//		TreeMap<Integer, String> sortedFL = new TreeMap<Integer, String> ( FL );
//		for ( Integer ts : sortedFL.keySet() ) {
//			String m = (String) sortedFL.get( ts );
//			if ( F.containsValue( ts ) ) {
//				System.out.println(m + ":f");
//				if ( L.containsValue( ts ) ) {
//					// according to the entry event monitor as it is designed, if two method have equal time stamps,
//					// they must be the same method
//					System.out.println(m + ":l");
//				}
//			}
//			else if ( L.containsValue( ts ) ) {
//				System.out.println(m + ":l");
//			}
//			else {
//				System.out.println(m + ":?");
//			}
//		}
//
//		if (bFullSequence) {
//			System.out.println("\n[ Full Sequence of Method Entry and Returned-into Events]\n");
//			TreeMap<Integer, String> treeA = new TreeMap<Integer, String> ( A_distEA );
//			System.out.println(treeA);
//			
//			// DEBUG ONLY
//			for (Integer ts : treeA.keySet()) {
//				System.out.println(ts+"\t"+treeA.get(ts));
//			}
//		}
//	}
	
	protected synchronized static void serializeEvents() {
		/* serialize for later deserialization in the post-processing phase when impact set is to be computed*/
		if ( !fnEventMaps.isEmpty() ) {
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
				// we won't allow the confusion of overwriting the file with the event maps from multiple executions 
				//fnEventMaps = "";
				A.clear();
				g_counter = 1;
				g_traceCnt ++;
			}
		}
	}
	
//	private static void dist_serializeEvents() {
//		/* serialize for later deserialization in the post-processing phase when impact set is to be computed*/
//		if ( !fnEventMaps_distEA.isEmpty() ) {
//			FileOutputStream fos;
//			try {
//				//System.out.println("fnEventMaps_distEA="+fnEventMaps_distEA);
//				Process process = Runtime.getRuntime().exec("mkdir -p test1");
//				fos = new FileOutputStream(fnEventMaps_distEA);
//				ObjectOutputStream oos = new ObjectOutputStream(fos);
//				// TreeMap is not serializable as is HashMap
//				oos.writeObject(F);
//				oos.writeObject(L);
//				if (trackingSender) {
//					oos.writeObject(S);
//				}
//				//System.out.println("\n\n[ First events ]\n" + F );
//				//System.out.println("\n[ Last events ]\n" + L );
//				oos.flush();
//				oos.close();
//				fos.flush();
//				fos.close();
//			}
//			catch (Exception e) {
//				e.printStackTrace();
//			}
//			finally {
//				// we won't allow the confusion of overwriting the file with the event maps from multiple executions 
//				fnEventMaps_distEA = "";
//			}
//		}
//	}
//	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** dump the unique object id of a given object at runtime
	 *	used by DynAliasInst 
	 */
	/** Used to avoid infinite recursion */
	private static boolean active = false;
	public static PrintStream outStream = null;
	/** cache all object ids until the termination of the current execution, when the cached content is to be dumped */
	// At the method level
	public static final Map<dua.util.Pair<Integer, String>, Set<Integer>> memCache = new LinkedHashMap<dua.util.Pair<Integer, String>, Set<Integer>>();
	// At the method-occurrence level
	public static final Map<dua.util.Pair<Integer, String>, Map<Integer, Set<Integer>>> memCacheIns = 
		new LinkedHashMap<dua.util.Pair<Integer, String>, Map<Integer, Set<Integer>>>();
	/** the name of the file to save the dumped cache content */
	protected static String fnObjectIds = "";
	public static void setIdFile(String fname) { fnObjectIds = fname; }
	/* if caching the object ids until the end of execution, or dumping in an immediate manner at each monitoring point */
	public static boolean cachingOIDs = true;
	/* record object ids at method level or method instance level */
	public static boolean instanceLevel = true;
	
	// at the method level
	public synchronized static void reportOID(int sid, String var, Object o) {
	// public synchronized static void reportOID(String m, int sid, String var, Object o) {
		if (active) return;
		active = true;
		if (outStream == null) {
			outStream = System.out;
		}
		try {
			reportOID_impl(sid, var, o);
			// reportOID_impl(m, sid, var, o);
		}
		catch (Throwable t) {
			t.printStackTrace(outStream);
		}
		finally {
			active = false;
		}
	}

	public synchronized static void reportOID_impl(int sid, String var, Object o) {
	//private static volatile String prem = "";
	//private static volatile int mocc = 0;
	//public synchronized static void reportOID_impl(String m, int sid, String var, Object o) {
		int id = 0;
		if (o != null) {
			id = System.identityHashCode(o);
			
			if (cachingOIDs) {
				dua.util.Pair<Integer, String> sidval = new dua.util.Pair<Integer, String>(sid, var);
				// at the method level
				if (!instanceLevel) {
					Set<Integer> ids = memCache.get(sidval);
					if (ids == null) {
						ids = new LinkedHashSet<Integer>();
						memCache.put(sidval, ids);
					}
					ids.add(id);
				}
				// at the method-occurrence level
				else {
					Map<Integer, Set<Integer>> num2ids = memCacheIns.get(sidval);
					if (num2ids == null) {
						num2ids = new LinkedHashMap<Integer, Set<Integer>>();
						memCacheIns.put(sidval, num2ids);
					}
					Set<Integer> ids = num2ids.get(g_counter-1);
					if (ids == null) {
						ids = new LinkedHashSet<Integer>();
						num2ids.put(g_counter-1, ids);
					}
					ids.add(id);
				}
			}
			else {
				//outStream.println(sid+":"+var+" " + id);
				System.out.println(sid+":"+var+" " + id);
			}
		}
	}
	
	
	public synchronized static void serializeObjIDCache() {
		if ( !fnObjectIds.isEmpty() ) {
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(fnObjectIds);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				// at the method level
				if (!instanceLevel) {
					//oos.writeObject(memCache);
					oos.writeInt(memCache.size());
					for (Map.Entry<dua.util.Pair<Integer, String>, Set<Integer>> en : memCache.entrySet()) {
						oos.writeObject(en.getKey().first());
						oos.writeObject(en.getKey().second());
						oos.writeObject(en.getValue());
					}
				}
				// at the method-occurrence level
				else {
					//oos.writeObject(memCacheIns);
					oos.writeInt(memCacheIns.size());
					for (Map.Entry<dua.util.Pair<Integer, String>, Map<Integer, Set<Integer>>> en : memCacheIns.entrySet()) {
						oos.writeObject(en.getKey().first());
						oos.writeObject(en.getKey().second());
						oos.writeObject(en.getValue());
					}
				}
				oos.flush();
				oos.close();
				fos.flush();
				fos.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				fnObjectIds = "";
			}
		}
	}
	////////////////////////////////////////////////////////////////
	// 2. communication Events
	////////////////////////////////////////////////////////////////
	private static boolean threadAsProcess = false;
	public static final int BUFLEN = 4;
	public static final int PIDLEN = 32;
	
//	protected static final logicClock g_lgclock = new logicClock(new AtomicInteger(0), getMAC()+getProcessID());
//	public synchronized static logicClock getlgclock() { return g_lgclock; }
	
	public static void setThreadAsProcess(boolean flag) {
		threadAsProcess = flag;
	}
	public static byte[] intToByteArray(int value, ByteOrder ord) {
		return ByteBuffer.allocate(BUFLEN).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
		//return ByteBuffer.allocate(BUFLEN).order(ByteOrder.BIG_ENDIAN).putInt(value).array();
		//return ByteBuffer.allocate(BUFLEN).order(ord).putInt(value).array();
	}
	public static int byteArrayToInt(byte[] b, ByteOrder ord) {
		return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getInt();
		//return ByteBuffer.wrap(b).order(ByteOrder.BIG_ENDIAN).getInt();
		//return ByteBuffer.wrap(b).order(ord).getInt();
	}
	public static String getMAC() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i],
                        (i < mac.length - 1) ? "-" : ""));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
	public static String getProcessID() {
		return ManagementFactory.getRuntimeMXBean().getName()+'\0';
	}
	
	private static boolean g_intercept = true;
	public static void disable() { 
		g_intercept = false; 
		dtSocketInputStream.intercept = g_intercept;
		dtSocketOutputStream.intercept = g_intercept;
	}
	public static void enable() { 
		g_intercept = true;
		dtSocketInputStream.intercept = g_intercept;
		dtSocketOutputStream.intercept = g_intercept;
	}
	public static boolean isEnabled() { return g_intercept;}
	////----------------------------------- empirical tests show that the following, same as ShiVector did, can deal with NIO traffics at synchronous mode only 
	// probe for NIO reads
//	public synchronized static void dist_nioread(SocketChannel s){
//		if (debugOut)
//			System.out.println("******************** [dist_nioread " + s + "]: getProcessID = " + getProcessID());
//		receivedMessages+="[dist_nioread " + s + "]: getProcessID = " + getProcessID()+"\n";
//		if (!isEnabled()) { return; }
//		
//		try {
//			g_lgclock.retrieveClock(s);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//	}
//	// probe for NIO writes	
//	public synchronized static void dist_niowrite(SocketChannel s){
//		if (!isEnabled()) { return; }
//		try {
//			g_lgclock.packClock(s);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	////----------------------------------- proprietary probes for IPC data transfers through ObjectIn/OutputStream:read/writeObject ---- 
//	// probe for objectInputStream readObject
//	public synchronized static void dist_objstreamread(InputStream is){
//		if (debugOut)
//			System.out.println("******************** [dist_objstreamread " + is + "]: getProcessID = " + getProcessID());
//		receivedMessages+="[dist_objstreamread " + is + "]: getProcessID = " + getProcessID()+"\n";
//		//System.out.println("did nothing.");
//		if (!isEnabled()) { return; }
//		
//		try {
//			g_lgclock.retrieveClock(is);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	// probe for NIO writes	
//	public synchronized static void dist_objstreamwrite(OutputStream os){
//		//System.out.println("did nothing.");
//		if (debugOut)
//			System.out.println("******************** [dist_objstreamwrite " + os + "]: getProcessID = " + getProcessID());
//		if (!isEnabled()) { return; }
//		try {
//			g_lgclock.packClock(os,0);
//			os.flush();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	//// ----------------------------------- deal with NIOs traffics working at asynchronous mode
//	public synchronized static int dist_async_nioread(SocketChannel s, ByteBuffer dst) throws IOException {
//		if (debugOut)
//		System.out.println("******************** [dist_async_nioread1 " + s +" dst=" + dst + "]: getProcessID = " + getProcessID());
//		receivedMessages+="[dist_async_nioread1 " + s +" dst=" + dst + "]: getProcessID = " + getProcessID()+"\n";
//		if (!isEnabled()) {
//			//g_lgclock.retrieveClock(s);
//			return s.read(dst);
//		}
//		
//		int ret = 0;
//		try {
//			ret = g_lgclock.retrieveClock(s, dst);
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//		}
//		return ret;
//	}
//	public synchronized static long dist_async_nioread(SocketChannel s, ByteBuffer[] dsts) throws IOException {
//		if (debugOut)
//		System.out.println("******************** [dist_async_nioread2 " + s +" dsts=" + dsts + "]: getProcessID = " + getProcessID());
//		receivedMessages+="[dist_async_nioread2 " + s +" dsts=" + dsts + "]: getProcessID = " + getProcessID()+"\n";
//		if (!isEnabled()) {
//			//g_lgclock.retrieveClock(s);
//			return s.read(dsts);
//		}
//		return dist_async_nioread(s, dsts, 0, dsts.length);
//	}
//	public synchronized static long dist_async_nioread(SocketChannel s, ByteBuffer[] dsts, int offset, int length) throws IOException {
//		if (debugOut)
//		System.out.println("******************** [dist_async_nioread3 " + s +" dsts=" + dsts +" offset=" + offset +" length=" + length + "]: getProcessID = " + getProcessID());
//		receivedMessages+="[dist_async_nioread3 " + s +" dsts=" + dsts +" offset=" + offset +" length=" + length + "]: getProcessID = " + getProcessID()+"\n";
//		if (!isEnabled()) {
//			//g_lgclock.retrieveClock(s);
//			return s.read(dsts, offset, length);
//		}
//		
//		long ret = 0;
//		try {			
//			ret = g_lgclock.retrieveClock(s, dsts, offset, length);
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//		}
//		return ret;
//	}
//	
//	public synchronized static int dist_async_niowrite(SocketChannel s, ByteBuffer src) throws IOException {
//		//System.out.println("******************** [dist_async_niowrite1 " + s +" src=" + src + "]: getProcessID = " + getProcessID());
//		if (!isEnabled()) {
//			//g_lgclock.packClock(s);
//			return s.write(src);
//		}
//		
//		int ret = 0;
//		try {
//			ret = g_lgclock.packClock(s, src);
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//		}
//		return ret;
//		
//	}
//	public synchronized static long dist_async_niowrite(SocketChannel s, ByteBuffer[] srcs) throws IOException {
//		//System.out.println("******************** [dist_async_niowrite2 " + s +" srcs=" + srcs + "]: getProcessID = " + getProcessID());
//		if (!isEnabled()) {
//			//g_lgclock.retrieveClock(s);
//			return s.write(srcs);
//		}
//		
//		return dist_async_niowrite(s, srcs, 0, srcs.length);
//	}
//	public synchronized static long dist_async_niowrite(SocketChannel s, ByteBuffer[] srcs, int offset, int length) throws IOException {
//		//System.out.println("******************** [dist_async_niowrite3 " + s +" srcs=" + srcs + "]: getProcessID = " + getProcessID());
//		if (!isEnabled()) {
//			//g_lgclock.retrieveClock(s);
//			return s.write(srcs, offset, length);
//		}
//		
//		long ret = 0;
//		try {
//			ret = g_lgclock.packClock(s, srcs, offset, length);
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//		}
//		return ret;
//	}

	/** give the full EAS trace length */
	public synchronized static int getFullTraceLength() {
		synchronized (g_counter) {
			return g_counter;
		}
	}
}