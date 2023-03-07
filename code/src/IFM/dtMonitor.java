package IFM;

import MciaUtil.MethodEventComparator;
//import disttaint.dtSocketInputStream;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/** Monitoring method events in runtime upon
 * invocations by instrumented probes in the subject
 *
 * For Diver, this monitor captures full sequence of method events. For a uniform basis, also
 * to track two kinds of events only: entrance (first event) and return-into (last event), as EAS does
 */

public class dtMonitor {
	protected static final long CN_LIMIT = 1000*1000*1000;
	protected static int g_traceCnt = 0;
	
	/* first message-receiving events */
	protected static HashMap<String, Integer> S = new HashMap<String, Integer>();
	//protected static Integer g_index = 1;
	//protected static Integer g_counter = 0;
	/* all events */
	//protected static HashMap<Integer, Integer> A = new LinkedHashMap<Integer, Integer>();
	
	/* last events */
	protected static HashMap<String, Integer> L = new HashMap<String, Integer>();
	// distEA events
	/* first events */
	protected static HashMap<String, Integer> F = new HashMap<String, Integer>();
	/* all events */
	protected static HashMap<Integer, String> A_distEA = new LinkedHashMap<Integer, String>();
	
	
	/* two special events */
	public static final int PROGRAM_START = Integer.MIN_VALUE;
	public static final int PROGRAM_END = Integer.MAX_VALUE;
	
	/* debug flag: e.g. for dumping event sequence to human-readable format for debugging purposes, etc. */
	protected static boolean debugOut = false;
	public static void turnDebugOut(boolean b) { debugOut = b; }

	/* output file for serializing the two event maps */
	//protected static String fnEventMaps = "";

    // dist variables
	/* the global counter for time-stamping each method event */
	protected static Integer g_counter_distEA = 0;
	protected static String fnEventMaps_distEA = "";
	/* a flag ensuring the initialization and termination are both executed exactly once and they are paired*/
	protected static boolean bInitialized = false;
	
	// distEA variables
	protected static boolean bFullSequence = false;
	protected static boolean dumpEvents = false;
	protected static boolean forcingMirrorIO = false;
	private static boolean useSingleFlag = false; // force mirror i/o for both socket and NIO network I/Os or mirror each type separately only
	protected static boolean usingToken = true; // use a token at the end of each clock message for identification and verification
	
	protected static boolean trackingSender = true; // send the identify of message sender with messages being sent
	protected static boolean runDiverOut = false; // runDiver
	public static String receivedMessages="";
	/* The name of serialization target file will be set by EARun via this setter */
	public static void setEventMapSerializeFile(String fname) {
		fnEventMaps_distEA = fname;
		
		if (cachingOIDs) {
			fnObjectIds = fname + "o";
		}
		
		//resetInternals();
		F.clear();
		L.clear();
		A_distEA.clear();		
		g_lgclock.initClock(1);
	}
	
	
	/* for DUAF/Soot to access this class */
	public static void __link() { }
	
	/* clean up internal data structures that should be done so for separate dumping of them, a typical such occasion is doing this per test case */
//	private synchronized static void resetInternals() {
//		S.clear();
//		A.clear();
//		if (cachingOIDs) {
//			if (!instanceLevel) memCache.clear();
//			else memCacheIns.clear();
//		}
//		
//		synchronized (g_index) {
//			g_index = 1;
//		}
//		
//		synchronized (g_counter) {
//			g_counter = 1;
//		}
//		
//		g_traceCnt = 0;
//	}
	
	/* initialize the two maps and the global counter upon the program start event */		
	public synchronized static void initialize() throws Exception{
//		S.clear();
//		A.clear();
//		
//		
//		if (cachingOIDs) {
//			if (!instanceLevel) memCache.clear();
//			else memCacheIns.clear();
//		}
//		
//		synchronized (g_index) {
//			g_index = 1;
//		}
//		
//		synchronized (g_counter) {
//			g_counter = 1;
//			A.put(g_counter, PROGRAM_START);
//			if (bFullSequence) {
//				A_distEA.put(g_counter, "program start");
//			}
//			g_counter++;
//		}
//		
//		g_traceCnt = 0;
//		bInitialized = true;
		
		//distE initialize()
		F.clear();
		L.clear();
		A_distEA.clear();
		bInitialized = true;
		g_lgclock.initClock(1);
		//System.out.println("**************dtMonitor::initialize()  2th");
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
				dtMonitor.terminate("Forced upon external termination");
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
		if (null != dumpFlag) {
			dumpEvents = dumpFlag.equalsIgnoreCase("true");
		}
		String fmFlag = System.getProperty("forceMirror");
		if (null != fmFlag) {
			forcingMirrorIO = fmFlag.equalsIgnoreCase("true");
		}
		String mfFlag = System.getProperty("uniformMirror");
		if (null != mfFlag) {
			useSingleFlag = mfFlag.equalsIgnoreCase("true");
		}
		
		String tkFlag = System.getProperty("useToken");
		if (null != tkFlag) {
			usingToken = tkFlag.equalsIgnoreCase("true");
		}
		
		String ttFlag = System.getProperty("trackSender");
		if (null != ttFlag) {
			trackingSender = ttFlag.equalsIgnoreCase("true");
		}
		
		String rdFlag = System.getProperty("ltsRunDiver");
		System.out.println("rdFlag="+rdFlag);
		if (null != rdFlag) {
			runDiverOut = rdFlag.equalsIgnoreCase("true");
		}
		
		//debugOut=true;
		//usingToken=true;
//		System.out.println("runDiverOut="+runDiverOut);
//		System.out.println("debugOut="+debugOut);
		System.out.println("distMonitor starts working ......");
		trackingSender=true;
		IFM.dtSocketInputStream.debugOut = debugOut;
		IFM.dtSocketInputStream.usingToken = usingToken;

		IFM.dtSocketInputStream.intercept = g_intercept;
		dtSocketOutputStream.intercept = g_intercept;
		
		if (trackingSender) {
			// just record the ID of local process 
			//System.out.println("dtMonitor S.put(getProcessID(), Integer.MAX_VALUE) getProcessID()="+getProcessID());
			S.put(getProcessID(), Integer.MAX_VALUE);
		}
		
	}
	
	public synchronized static void enter(String methodname){
		//System.out.println("\n enter(String methodname)="+methodname);
		try {
//			synchronized (g_index) {
//				if (!S.containsKey(methodname)) {
//					S.put(methodname, g_index);
//					g_index ++;
//				}
//			}
//			synchronized (g_counter) {
//				assert S.containsKey(methodname);
//				A.put(g_counter, S.get(methodname)*-1);  // negative index for entry event
//				g_counter ++;
//				
//				if (g_counter > CN_LIMIT) {
//					serializeEvents();
//				}
//			}

			if (active) return;
			active = true;
			try { enter_impl(methodname); }
			finally { active = false; }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static void enter_impl(String methodname) {
		//System.out.println("\n enter_impl(String methodname)="+methodname);
		try {
			synchronized (g_counter_distEA) {
				Integer curTS = (Integer) F.get(methodname);
				if (null == curTS) {					
					curTS = 0;
					//System.out.println("\n enter_impl F.put(methodname, g_counter_distEA) methodname="+methodname+" g_counter_distEA="+g_counter_distEA);
					F.put(methodname, g_counter_distEA);
				}
				g_counter_distEA = g_lgclock.getLTS();
				//System.out.println("\n enter_impl L.put(methodname, g_counter_distEA) methodname="+methodname+" g_counter_distEA="+g_counter_distEA);
				L.put(methodname, g_counter_distEA);	
				if (bFullSequence) {
					//System.out.println("\n enter_impl A_distEA.put(g_counter_distEA, methodname:e) methodname="+methodname+" g_counter_distEA="+g_counter_distEA);
					A_distEA.put(g_counter_distEA, methodname+":e");
				}
				g_counter_distEA ++;
				g_lgclock.increment();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static void returnFrom(String methodname, String calleeName) {
		//System.out.println("returnFrom methodname="+methodname+" calleename="+calleeName);
		if (active) return;
		active = true;
		try { returnFrom_impl(methodname, calleeName); }
		finally { active = false; }
	}

	/** returnFrom events are handled the same as are returnInto events */
	public synchronized static void returnFrom_impl(String methodname, String calleeName){
		try {
			synchronized (g_counter_distEA) {
				Integer curTS = (Integer) L.get(methodname);
				if (null == curTS) {
					curTS = 0;
				}
				g_counter_distEA = g_lgclock.getLTS();
				
				L.put(methodname, g_counter_distEA);
				if (bFullSequence) {
					A_distEA.put(g_counter_distEA, methodname+":x");
				}
	
				g_counter_distEA ++;
				g_lgclock.increment();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* the callee could be either an actual method called or a trap */
	
	public synchronized static void returnInto(String methodname, String calleeName){
//		if (methodname.indexOf("de.uniba.wiai.lspi.util.console.ConsoleThread: void run()")>=0)
//			System.out.println("returnInto methodname="+methodname+" calleename="+calleeName);
		try {
//			synchronized (g_counter) {
//				assert S.containsKey(methodname);
//				A.put(g_counter, S.get(methodname)*1);  // positive index for returned-into event
//				g_counter ++;
//				
//				if (g_counter > CN_LIMIT) {
//					serializeEvents();
//				}
//			}
			

			if (active) return;
			active = true;
			try { returnInto_impl(methodname, calleeName); }
			finally { active = false; }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* the callee could be either an actual method called or a trap */
	public synchronized static void returnInto_impl(String methodname, String calleeName){
		try {
			synchronized (g_counter_distEA) {
				Integer curTS = (Integer) L.get(methodname);
				if (null == curTS) {
					curTS = 0;
				}
				g_counter_distEA = g_lgclock.getLTS();
				
				L.put(methodname, g_counter_distEA);
				if (bFullSequence) {
					A_distEA.put(g_counter_distEA, methodname+":i");
				}
	
				g_counter_distEA ++;
				g_lgclock.increment();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//public static Object getMap() { return A.clone(); }
	
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
		System.out.println("terminate(String where)="+where);
		if (bInitialized) {
			bInitialized = false;
		}
		else {
			return;
		}

//		synchronized (g_counter) {
//			A.put(g_counter, PROGRAM_END);
//		}
		
//		if (debugOut) {
//			dumpEvents();
//		}
//		if (runDiverOut)  {		
//		// set Diver trace file name
//			System.out.println("fnEventMaps="+fnEventMaps);
//			if (fnEventMaps.length()<1) {
//				String fndefault = System.getProperty("user.dir") + File.separator + "Local";
//				fndefault += System.currentTimeMillis() + ".em";
//				fnEventMaps = fndefault;
//			}				
//			serializeEvents();
//		}
		//System.out.println("terminate(String where) 1th");
		// distEA trace file
		synchronized (g_counter_distEA) {
			g_counter_distEA = g_lgclock.getLTS();
			
			A_distEA.put(g_counter_distEA, "program end");
		}
		//System.out.println("terminate(String where) 2th");
		if (dumpEvents) {
			dist_dumpEvents();
		}
		//System.out.println("terminate(String where) 3th");
		if (fnEventMaps_distEA.length()<1) {
			String fndefault = System.getProperty("user.dir") + File.separator + "test1/Remote";
			fndefault += System.currentTimeMillis() + ".em";
			fnEventMaps_distEA = fndefault;
		}
		//System.out.println("terminate(String where) 4th");
		dist_serializeEvents();
		//System.out.println("terminate(String where) 5th");
	}
	
//	protected synchronized static void dumpEvents() {
//		System.out.println("\n[ Method Index ]\n");
//		System.out.println(S);
//		System.out.println("\n[ Full Sequence of Method Entry and Returned-into Events]\n");
//		TreeMap<Integer, Integer> treeA = new TreeMap<Integer, Integer> ( A );
//		System.out.println(treeA);
//		// DEBUG ONLY
//		Map<Integer, String> idx2me = new LinkedHashMap<Integer, String>();
//		for (String me : S.keySet()) {
//			idx2me.put(S.get(me), me);
//		}
//		for (Integer ts : treeA.keySet()) {
//			int idx = treeA.get(ts);
//			String mname = idx2me.get(Math.abs(idx)) + (idx>0?":i":":e");
//			if (idx == PROGRAM_START) mname = "program start";
//			if (idx == PROGRAM_END) mname = "program end";
//			System.out.println(ts+"\t"+ mname);
//		}
//	}
	protected synchronized static void dist_dumpEvents() {
		MethodEventComparator mecF = new MethodEventComparator(F);
		MethodEventComparator mecL = new MethodEventComparator(L);
		TreeMap<String, Integer> sortedF = new TreeMap<String, Integer> ( mecF );
		TreeMap<String, Integer> sortedL = new TreeMap<String, Integer> ( mecL );
		sortedF.putAll(F);
		sortedL.putAll(L);
		
		System.out.println("\n\n[ First events ]\n" + sortedF );
		System.out.println("\n[ Last events ]\n" + sortedL );

		/* put two maps into one but reversed map for producing the EA sequence */
		HashMap<Integer, String> FL = new HashMap<Integer, String>();

		//ArrayList<String> allMethods = new ArrayList<String>(F.keySet());
		//allMethods.addAll( L.keySet() );
		for( Map.Entry<String, Integer> entry : F.entrySet() ) {
			FL.put( entry.getValue(), entry.getKey() );
		}
		for( Map.Entry<String, Integer> entry : L.entrySet() ) {
			FL.put( entry.getValue(), entry.getKey() );
		}

		System.out.println("\n[ Whole Execute-After Sequence ]\n");
		TreeMap<Integer, String> sortedFL = new TreeMap<Integer, String> ( FL );
		for ( Integer ts : sortedFL.keySet() ) {
			String m = (String) sortedFL.get( ts );
			if ( F.containsValue( ts ) ) {
				System.out.println(m + ":f");
				if ( L.containsValue( ts ) ) {
					// according to the entry event monitor as it is designed, if two method have equal time stamps,
					// they must be the same method
					System.out.println(m + ":l");
				}
			}
			else if ( L.containsValue( ts ) ) {
				System.out.println(m + ":l");
			}
			else {
				System.out.println(m + ":?");
			}
		}

		if (bFullSequence) {
			System.out.println("\n[ Full Sequence of Method Entry and Returned-into Events]\n");
			TreeMap<Integer, String> treeA = new TreeMap<Integer, String> ( A_distEA );
			System.out.println(treeA);
			
			// DEBUG ONLY
			for (Integer ts : treeA.keySet()) {
				System.out.println(ts+"\t"+treeA.get(ts));
			}
		}
	}
	
//	protected synchronized static void serializeEvents() {
//		/* serialize for later deserialization in the post-processing phase when impact set is to be computed*/
//		if ( !fnEventMaps.isEmpty() ) {
//			FileOutputStream fos;
//			try {
//				ByteArrayOutputStream bos = new ByteArrayOutputStream();
//				//fos = new FileOutputStream(fnEventMaps+(g_traceCnt>0?g_traceCnt:""));
//				fos = new FileOutputStream(fnEventMaps);
//				GZIPOutputStream goos = new GZIPOutputStream(fos);
//				ObjectOutputStream oos = new ObjectOutputStream(bos);
//				// TreeMap is not serializable as is HashMap
//				// for Diver, we need the full sequence of events instead of the EA sequence only
//				
//				// First we need the method index for indexing methods because the full sequence does not contain method name
//				oos.writeObject(S);
//				oos.writeObject(A);
//				oos.flush();
//				oos.close();
//				
//				goos.write(bos.toByteArray());
//				bos.flush();
//				bos.close();
//				
//				goos.flush();
//				goos.close();
//				
//				fos.flush();
//				fos.close();
//			}
//			catch (Exception e) {
//				e.printStackTrace();
//			}
//			finally {
//				// we won't allow the confusion of overwriting the file with the event maps from multiple executions 
//				//fnEventMaps = "";
//				A.clear();
//				g_counter = 1;
//				g_traceCnt ++;
//			}
//		}
//	}
	
	private static void dist_serializeEvents() {
		/* serialize for later deserialization in the post-processing phase when impact set is to be computed*/
		if ( !fnEventMaps_distEA.isEmpty() ) {
			FileOutputStream fos;
			try {
				//System.out.println("fnEventMaps_distEA="+fnEventMaps_distEA);
				Process process = Runtime.getRuntime().exec("mkdir -p test1");
				fos = new FileOutputStream(fnEventMaps_distEA);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				// TreeMap is not serializable as is HashMap
				oos.writeObject(F);
				oos.writeObject(L);
				if (trackingSender) {
					oos.writeObject(S);
				}
				//System.out.println("\n\n[ First events ]\n" + F );
				//System.out.println("\n[ Last events ]\n" + L );
				oos.flush();
				oos.close();
				fos.flush();
				fos.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				// we won't allow the confusion of overwriting the file with the event maps from multiple executions 
				fnEventMaps_distEA = "";
			}
		}
	}
	
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
//	public synchronized static void reportOID(int sid, String var, Object o) {
//	// public synchronized static void reportOID(String m, int sid, String var, Object o) {
//		if (active) return;
//		active = true;
//		if (outStream == null) {
//			outStream = System.out;
//		}
//		try {
//			reportOID_impl(sid, var, o);
//			// reportOID_impl(m, sid, var, o);
//		}
//		catch (Throwable t) {
//			t.printStackTrace(outStream);
//		}
//		finally {
//			active = false;
//		}
//	}

//	public synchronized static void reportOID_impl(int sid, String var, Object o) {
//	//private static volatile String prem = "";
//	//private static volatile int mocc = 0;
//	//public synchronized static void reportOID_impl(String m, int sid, String var, Object o) {
//		int id = 0;
//		if (o != null) {
//			id = System.identityHashCode(o);
//			
//			if (cachingOIDs) {
//				dua.util.Pair<Integer, String> sidval = new dua.util.Pair<Integer, String>(sid, var);
//				// at the method level
//				if (!instanceLevel) {
//					Set<Integer> ids = memCache.get(sidval);
//					if (ids == null) {
//						ids = new LinkedHashSet<Integer>();
//						memCache.put(sidval, ids);
//					}
//					ids.add(id);
//				}
//				// at the method-occurrence level
//				else {
//					Map<Integer, Set<Integer>> num2ids = memCacheIns.get(sidval);
//					if (num2ids == null) {
//						num2ids = new LinkedHashMap<Integer, Set<Integer>>();
//						memCacheIns.put(sidval, num2ids);
//					}
//					Set<Integer> ids = num2ids.get(g_counter-1);
//					if (ids == null) {
//						ids = new LinkedHashSet<Integer>();
//						num2ids.put(g_counter-1, ids);
//					}
//					ids.add(id);
//				}
//			}
//			else {
//				//outStream.println(sid+":"+var+" " + id);
//				System.out.println(sid+":"+var+" " + id);
//			}
//		}
//	}
	
	
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
	
	protected static final logicClock g_lgclock = new logicClock(new AtomicInteger(0), getMAC()+getProcessID());
	public synchronized static logicClock getlgclock() { return g_lgclock; }
	
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
	
	private static void onRecvSenderID(String senderid) {
		/** for each process, only record the first message receiving event for each unique sender */
		Integer curval = S.get(senderid);
		if (debugOut)
			System.out.println("******************** onRecvSenderID senderid"+senderid +" curval = " + curval +" getProcessID = " + getProcessID());
		receivedMessages+="onRecvSenderID senderid"+senderid +" curval = " + curval +" getProcessID = " + getProcessID();
		if (null == curval) {
			if (debugOut && senderid.compareToIgnoreCase(getProcessID())==0) {
				System.out.println("WARNING: receive a message from the local process being sent to itself !!");
			}
			S.put(senderid, g_lgclock.getLTS());
			return;
		}
	}
	
	/** the logic clock used for Lamport timestamping */
	public static class logicClock {
		
		private AtomicInteger lts; // lamport time stamp
		private String pid; // process id --- unique identifier of a process
		
	    //private AtomicInteger readyToRead = new AtomicInteger(1); /** forcing mirrored read and write */
	    //private AtomicInteger readyToReadSocket = new AtomicInteger(1); /** forcing mirrored read and write */
	    //private AtomicInteger readyToReadNio = new AtomicInteger(1); /** forcing mirrored read and write */
	    //private AtomicInteger readyToReadANio = new AtomicInteger(1); /** forcing mirrored read and write */
		 private boolean readyToRead = true; /** forcing mirrored read and write */
		    private boolean readyToReadSocket = true; /** forcing mirrored read and write */
		    private boolean readyToReadNio = true; /** forcing mirrored read and write */
		    private boolean readyToReadANio = true; /** forcing mirrored read and write */
		
		final static Integer TOKEN_FLAG = 0xABCDDCBA;
		    
		public logicClock(AtomicInteger _lts, String _pid) {
			this.lts = _lts;
			this.pid = _pid;
			
			/*
			readyToRead.set(1);
			readyToReadSocket.set(1);
			readyToReadNio.set(1);
			readyToReadANio.set(1);
			*/
		}
		public void initClock(int iv) {
			synchronized (lts) {
				lts.set(iv);
			}
		}
		@Override public String toString() {
			return hostId();
		}
		public String hostId() {
			if (threadAsProcess) {
				return pid + Thread.currentThread();
			}
			return pid;
		}
		protected boolean isClock(int _lts, boolean rev) {
			//if (rev) _lts = Integer.reverseBytes(_lts);
			//if (usingToken)
			//	return (_lts >> 28) == flag;
			return true;
		}
		private int pickClock(int _lts, boolean rev) {
			//if (rev) _lts = Integer.reverseBytes(_lts);
			//if (usingToken)
			//	return _lts & 0x0fffffff;
			return _lts;
		}
		public synchronized int getLTS() {
			return lts.get();
		}
		public synchronized int getTimestamp() {
			// we use the first byte to identify clock and three bytes after for storing time stamp itself
			//if (usingToken)
			// return lts.get() | (flag << 28);
			return lts.get();
		}
		public synchronized int setTimestamp(int _lts) {
			return lts.getAndSet(_lts);
		}
		public synchronized int increment() {
			return lts.getAndIncrement();
		}
		public synchronized int updateClock(int other_lts) {
			// update the local (process) clock with the remote (process) clock
			int val = Math.max(other_lts, this.getTimestamp());
			this.setTimestamp(val);
			return this.increment();
		}
		
		protected int bytesToReadANIO = 0, bytesToReadNIO = 0, bytesAvailableSocket = 0;
		
	    // Socket read: for now, just read the integer lts
	    public void retrieveClock(InputStream in) throws IOException {
	    	if (forcingMirrorIO) {
		    	if (useSingleFlag) {
		    		if (!readyToRead) return;
		    		readyToRead = false;
		    	}
		    	else {
		    		if (!readyToReadSocket) return;
		    		readyToReadSocket = false;
		    	}
	    	}
	    	
	    	
	        byte[] buf = new byte[BUFLEN];
	        //if (in.markSupported()) in.mark(Integer.MAX_VALUE);
	        int bytesRead = in.read(buf);
	        if (bytesRead == -1 || bytesRead == 0) {
	           //if (in.markSupported()) in.reset();
	           return;
	        }

				
	        if (usingToken) {

	        	
	        	/** 1: read the token, namely the data length the peer sent in recent write operation */ 
		        int token = byteArrayToInt(buf, ByteOrder.LITTLE_ENDIAN);
		        bytesAvailableSocket = token;
		        if (debugOut) {
		        	System.out.println("[To Read]<= " + "socket token received is " + token);
	        		System.out.println("[Read]<= " + BUFLEN + " bytes read for socket token");
	        	}
		        
		        assert bytesRead == BUFLEN;
		        bytesAvailableSocket -= BUFLEN;
		        
		        bytesRead = in.read(buf);
		        if (debugOut) {
	        		System.out.println("[Read]<= " + bytesRead + " bytes read for socket clock");
	        	}
		        if (bytesRead == -1 || bytesRead == 0) {
		           System.err.println("!!!!!Unexpected ERROR when retrieving socket clock after getting token!!!!!");
		           return;
		        }
		        
		        assert bytesRead == BUFLEN;
		        bytesAvailableSocket -= BUFLEN;
	        }
	        
	        /** 2: read the clock */
	        int lts = byteArrayToInt(buf, ByteOrder.LITTLE_ENDIAN);
	        //if (!isClock(lts, false)) {
	        	//if (in.markSupported()) in.reset();
	        //	return;
	        //}
	        //lts = pickClock(lts, false);
	        
	        //this.setTimestamp(lts);
	        this.updateClock(lts);
	        
	        /** 3: read the sender id if opted on for it */
	        if (trackingSender) {
	        	byte[] snlenarray = new byte[BUFLEN];
	        	int snlenlen = in.read(snlenarray);
	        	assert snlenlen == BUFLEN;
	        	if (usingToken) {
	        		bytesAvailableSocket -= BUFLEN;
	        	}
	        	int snlen = byteArrayToInt(snlenarray, ByteOrder.LITTLE_ENDIAN);
	        	
	        	//byte[] senderidArray = new byte[PIDLEN]; // 16 bytes to hold a unique process id should be enough
	        	byte[] senderidArray = new byte[snlen]; // 16 bytes to hold a unique process id should be enough
	        	int actuallen = in.read(senderidArray,0,snlen);
	        	if (debugOut) {
	        		System.out.println(actuallen + " bytes retrieved for sender name.");
	        	}
		        String sender = new String(senderidArray).trim();
		        onRecvSenderID(sender);
		        //receivedMessages+="[receive message ProcessID"+getProcessID();
		        System.out.println("******************** [receive message ProcessID"+getProcessID());
		        
		        if (debugOut) {
		        	System.out.println("[Socket I/O Stream @ " + this.hostId() + "]: receive message from sender: " + sender);
		        	//System.out.println("******************** [receive message ProcessID"+getProcessID()+ " from sender: " + getProcessID())
		        }
		        if (usingToken) {
		        	bytesAvailableSocket -= actuallen;
		        }
	        }
	        
	        if (debugOut) {
	        	System.out.println("[Socket I/O Stream @ " + this.hostId() + "]: clock received = " + lts);
	        	if (lts > pickClock(this.getTimestamp(), false)) {
	        		System.out.println("\t ---> local clock updated to the remote one of " + lts);
	        	}
	        }
	    }

	    // Nio read: for now, just read the integer lts
	    public void retrieveClock(SocketChannel s) throws IOException {
	    	if (forcingMirrorIO) {
		    	/*
		    	if (useSingleFlag) {
		    		if (readyToRead.get()<1) return;
		    		readyToRead.decrementAndGet();
		    	}
		    	else {
		    		if (readyToReadNio.get()<1) return;
		    		readyToReadNio.decrementAndGet();
		    	}
		    	*/
		    	if (useSingleFlag) {
		    		if (!readyToRead) return;
		    		readyToRead = false;
		    	}
		    	else {
		    		if (!readyToReadNio) return;
		    		readyToReadNio = false;
		    	}
	    	}
	    	
	        ByteBuffer buf = ByteBuffer.allocate(BUFLEN);
	        //if (s.socket().getInputStream().markSupported()) s.socket().getInputStream().mark(Integer.MAX_VALUE);
	        
	        int bytesRead = s.read(buf);
	        //System.out.println(bytesRead + " bytes for clock read from socketChannel " +s);
	        if (bytesRead == -1 || bytesRead == 0) {
	        	//if (s.socket().getInputStream().markSupported()) s.socket().getInputStream().reset();
	            return;
	        }
	        /** 2: read the clock */
	        buf.rewind();
	        byte[] ltsArray = new byte[BUFLEN];
	        buf.get(ltsArray);
	        int lts = byteArrayToInt(ltsArray, buf.order());
	        
	        //if (!isClock(lts, buf.order()==ByteOrder.BIG_ENDIAN)) {
	        //	return;
	        //}
	        //lts = pickClock(lts, buf.order()==ByteOrder.BIG_ENDIAN);
	        
	        if (debugOut) {
	        	System.out.println("[NIO Channel/SocketChannel @ " + this.hostId() + "]: clock received = " + lts);
	        	if (lts > pickClock(this.getTimestamp(), false)) {
	        		System.out.println("\t ---> local clock updated to the remote one of " + lts);
	        	}
	        }
	        
	        //this.setTimestamp(lts);
	        this.updateClock(lts);
	        
	        /** 3: retrieve sender id if opted on for it */
	        if (trackingSender) {
	        	buf.clear();
	        	s.read(buf);
	        	buf.rewind();
	        	byte[] snlenarray = new byte[BUFLEN];
	        	buf.get(snlenarray);
	        	int snlen = byteArrayToInt(snlenarray, ByteOrder.LITTLE_ENDIAN);
	        	
	        	//byte[] senderidArray = new byte[PIDLEN]; // 16 bytes to hold a unique process id should be enough
	        	byte[] senderidArray = new byte[snlen];
	        	
	        	/*
	        	//byte[] senderidArray = new byte[PIDLEN]; // 16 bytes to hold a unique process id should be enough
		        buf.get(senderidArray);
		        */
	        	ByteBuffer buf2 = ByteBuffer.allocate(snlen);
	        	s.read(buf2);
	        	buf2.rewind();
	        	buf2.get(senderidArray);
		        
		        String sender = new String(senderidArray).trim();
		        onRecvSenderID(sender);
		        //receivedMessages+="retrieveClock receive message from sender: "+sender +"  getProcessID = " + getProcessID();
		        //System.out.println("******************** retrieveClock receive message from sender: "+sender +"  getProcessID = " + getProcessID());
		        if (debugOut) {
		        	System.out.println("[NIO Channel/SocketChannel @ " + this.hostId() + "]: receive message from sender: " + sender);
		        }
		        /*
		        if (usingToken) {
			        int actuallen = sender.getBytes().length;
			        bytesRead += actuallen;
			        bytesToReadNIO -= actuallen;
		        }
		        */
	        }
	    }
	    
	    // Nio Async read: for now, just read the integer lts
	    private int retrieveClockEx(SocketChannel s, ByteBuffer buf_recved) throws IOException {
	    	if (forcingMirrorIO) {
		    	/*
		    	if (useSingleFlag) {
		    		if (readyToRead.get()<1) return 0;
		    		readyToRead.decrementAndGet();
		    	}
		    	else {
		    		if (readyToReadANio.get()<1) return 0;
		    		readyToReadANio.decrementAndGet();
		    	}
		    	*/
		    	if (useSingleFlag) {
		    		if (!readyToRead) return 0;
		    		readyToRead = false;
		    	}
		    	else {
		    		if (!readyToReadANio) return 0;
		    		readyToReadANio = false;
		    	}
	    	}
	    	
	    	assert buf_recved.remaining() >= BUFLEN;
	    	//System.out.println("bytes remaining " + buf_recved.remaining());
	    	int bytesConsumed = 0;
	    	
	    	int nb = 0;
	        if (usingToken) {
	        	/** 1: retrieve token : bytes sent by peer recently */
	        	//buf_recved.mark();
		        //int pos = buf_recved.position();
	        	byte[] tokenarray = new byte[BUFLEN];
	        	buf_recved.get(tokenarray);
	        	int token = byteArrayToInt(tokenarray, buf_recved.order());
	        	bytesToReadANIO = token;
	        	if (debugOut) {
		        	//System.out.println("token received is " + Integer.toHexString(flag));
	        		System.out.println("<=[All to Read] " + "ANIO token received is " + token);
	        		System.out.println("<=[Read] " + BUFLEN + " bytes read for ANIO token");
	        	}
	        	bytesConsumed += BUFLEN;
	        	bytesToReadANIO -= BUFLEN;	        
	        
	        	/** 2: retrieve clock : recent LTS of peer process */
	        	assert bytesToReadANIO >= BUFLEN; 
		        if (buf_recved.remaining() < BUFLEN) {
		        	buf_recved.clear();
		        	nb = s.read(buf_recved);
		        	buf_recved.rewind();
		        	if (nb < BUFLEN) {
		        		System.err.println("!!!!!Unexpected ERROR when retrieving ANIO clock after getting token!!!!!");
		        		return bytesConsumed;
		        	}
		        }
	        }

	        byte[] ltsArray = new byte[BUFLEN];
	        buf_recved.get(ltsArray);
	        int lts = byteArrayToInt(ltsArray, buf_recved.order());
	        if (debugOut && usingToken) {
        		System.out.println("<=[Read] " + BUFLEN + " bytes read for ANIO clock");
        	}
	        
	        //lts = pickClock(lts,buf_recved.order()==ByteOrder.BIG_ENDIAN);
	        
	        if (debugOut) {
	        	System.out.println("[NIO Async Channel/SocketChannel @ " + this.hostId() + "]: clock received = " + lts);
	        	if (lts > pickClock(this.getTimestamp(), false)) {
	        		System.out.println("\t ---> local clock updated to the remote one of " + lts);
	        	}
	        }
	        
	        if (usingToken) {
	        	bytesConsumed += BUFLEN;
	        	bytesToReadANIO -= BUFLEN;
	        }
	        
	        //this.setTimestamp(lts);
	        this.updateClock(lts);
	        
	        /** 3: retrieve sender id if opted on for it */
	        if (trackingSender) {
	        	if (usingToken) {
		        	if (debugOut) {
		        		System.out.println(buf_recved.remaining() + " bytes remained before reading sender name length.");
		        	}
		        	if (bytesToReadANIO > 0 && buf_recved.remaining() == 0) {
		        		buf_recved.clear();
		    			nb = s.read(buf_recved);
		    			if (debugOut) {
		    				System.out.println(nb + " bytes fetched from the channel before reading sender name length.");
		    			}
		    			buf_recved.rewind();
		        	}
	        	}
	        	byte[] snlenarray = new byte[BUFLEN];
	        	buf_recved.get(snlenarray);
	        	if (usingToken) {
	        		bytesConsumed += BUFLEN;
		        	bytesToReadANIO -= BUFLEN;
	        	}
	        	
	        	int snlen = byteArrayToInt(snlenarray, buf_recved.order());
	        	byte[] senderidArray = new byte[snlen];
	        	
	        	/*
	        	//byte[] senderidArray = new byte[PIDLEN]; // 16 bytes to hold a unique process id should be enough
		        buf_recved.get(senderidArray);
		        */
	        	ByteBuffer buf = ByteBuffer.allocate(snlen);
	        	s.read(buf);
	        	buf.rewind();
	        	buf.get(senderidArray);
		        
		        String sender = new String(senderidArray).trim();
		        onRecvSenderID(sender);
		        if (debugOut) {
		        	System.out.println("[NIO Async Channel/SocketChannel @ " + this.hostId() + "]: receive message from sender: " + sender);
		        }
		        if (usingToken) {
			        int actuallen = snlen; //sender.getBytes().length;
			        bytesConsumed += actuallen;
		        	bytesToReadANIO -= actuallen;
		        }
	        }
	        
	        if (usingToken) {
	        	if (debugOut) {
	        		System.out.println("bytes remaining after retrieving ANIO clock: " + buf_recved.remaining());
	        	}
	        	/** 3: read the original message as intended by the instrumented I/O function call itself */
	        	if (bytesToReadANIO > 0 && buf_recved.remaining() == 0) {
	        		buf_recved.clear();
	    			nb = s.read(buf_recved);
	    			if (debugOut) {
	    				System.out.println("<=[Read] " + nb + " bytes read for original message with ANIO token+clock piggybacked");
	    			}
	    			bytesToReadANIO -= nb;
	    			bytesConsumed += nb;
	    			return nb;
		        }
	        	/** if the bytebuffer takes all data sent by peer in one read already; just compact the buffer now */
	        	buf_recved.compact();
	        	//return bytesConsumed;
	        	//return buf_recved.remaining();
	        	nb = bytesToReadANIO; // all data has been read through
	        	if (debugOut) {
    				System.out.println("<=[Read] " + nb + " bytes read for original message with ANIO token+clock piggybacked");
    			}
	        	bytesToReadANIO -= nb;
    			bytesConsumed += nb;
	        	return nb;
	        }
	        
	        return BUFLEN;
	    }
	    
	    public int retrieveClock (SocketChannel s, ByteBuffer dst) throws IOException {
	    	//assert dst.order() == ByteOrder.LITTLE_ENDIAN;
	    	//assert s.finishConnect() && s.isConnected() && s.isOpen();
	    	int nb = s.read(dst);
	    	//String name = new Object(){}.getClass().getEnclosingMethod().getName();
	    	//System.out.println(nb + " bytes read in " + name);
	    	//if (nb == 0) {
	    	if (nb == -1 || nb == 0) {
				//if (nb < BUFLEN) {
				//if (s.socket().getInputStream().markSupported()) s.socket().getInputStream().reset();
	    		//dst.rewind();
				return nb;
			}
	
			if (usingToken) {
				int ret = 0;
				if (debugOut) {
					System.out.println("<=[To Read] " + "ANIO bytes to read: " + bytesToReadANIO);
				}
				if (bytesToReadANIO > 0) {
					if (nb > 0) {
						bytesToReadANIO -= nb;
						if (bytesToReadANIO < 0) bytesToReadANIO = 0;
					}
					if (debugOut) {
						System.out.println("<=[Read] " + nb + " bytes read for original message without ANIO token+clock piggybacked");
					}
					return nb;
				}
				else {
					//System.out.println("FINISHED reading all data last sent by the peer: " + bytesToRead);
					
					dst.rewind();
					ret = this.retrieveClockEx(s,dst);
					//dst.compact();
					//System.out.println("bytes remaining after compacting: " + dst.remaining());
				}
				return ret;
			}
			
			//ByteBuffer ndst = ByteBuffer.allocate(dst.capacity()-BUFLEN);
			//ndst.put(dst);
			dst.rewind();
			int shift = this.retrieveClockEx(s,dst);
			dst.compact(); // this operation saves my life out of two-day hopeless debugging!
			assert nb - shift >= 0;
			return nb - shift; //BUFLEN;
	    }
	    public long retrieveClock (SocketChannel s, ByteBuffer[] dsts, int offset, int length) throws IOException {
	    	long nb = s.read(dsts, offset, length);
	    	if (nb == -1 || nb == 0) {
				//if (nb < BUFLEN) {
				//if (s.socket().getInputStream().markSupported()) s.socket().getInputStream().reset();
				return nb;
			}
	    	
	    	//if (s.socket().getInputStream().markSupported()) s.socket().getInputStream().mark(Integer.MAX_VALUE);
	    	
			//assert nb >= BUFLEN;
			/** use the first bytebuffer of the sequence for logic clock transmission */
	    	if (usingToken) {
				int ret = 0;
				System.out.println("<=[To Read] " + "ANIO bytes to read: " + bytesToReadANIO);
				if (bytesToReadANIO > 0) {
					if (nb > 0) {
						bytesToReadANIO -= nb;
						if (bytesToReadANIO < 0) bytesToReadANIO = 0;
					}
					if (debugOut) {
						System.out.println("<=[Read] " + nb + " bytes read for original message without ANIO token+clock piggybacked");
					}
					return nb;
				}
				else {
					//System.out.println("FINISHED reading all data last sent by the peer: " + bytesToRead);
					
					dsts[offset].rewind();
					ret = this.retrieveClockEx(s,dsts[offset]);
					//dst.compact();
					//System.out.println("bytes remaining after compacting: " + dst.remaining());
				}
				return ret;
			}
	    	
			dsts[offset].rewind();
			int shift = this.retrieveClockEx(s,dsts[offset]);
			dsts[offset].compact();
			return nb - shift; //BUFLEN;
	    }
	    
	    // Socket write: for now, just piggyback the original message with the integer lts
	    public void packClock(OutputStream out, int len) throws IOException {
	       	 int towrite = len + BUFLEN;
	    	 if (trackingSender) {
	    		 if (debugOut) {
	    			 System.out.println(getProcessID().getBytes().length + " bytes for sendername to pack.");
	    		 }
	    		 towrite += BUFLEN+getProcessID().getBytes().length;
	    	 }
	    	 
	    	 if (usingToken) {
	    		 towrite += BUFLEN;
	    		 byte[] tokenarray = intToByteArray(towrite,ByteOrder.LITTLE_ENDIAN);
	    		 if (debugOut) {
		        	System.out.println("[All to Write]=> " + "socket token to send " + towrite);
		         }
	    		 out.write(tokenarray);
	    	 }
	         
	         byte[] ltsarray = intToByteArray(getTimestamp(),ByteOrder.LITTLE_ENDIAN);
	         out.write(ltsarray);
	         if (trackingSender) {
	        	 byte[] snlenarray = intToByteArray(getProcessID().getBytes().length,ByteOrder.LITTLE_ENDIAN);
	        	 out.write(snlenarray);
	        	 out.write(getProcessID().getBytes());
	         }
	         
	         if (debugOut) {
	        	 System.out.println("[Socket I/O Stream @ " + this.hostId() + "]: clock sent = " + 
	        			 pickClock(this.getTimestamp(),false));
	        	 if (trackingSender) {
	        		 System.out.println("[Socket I/O Stream @ " + this.hostId() + "]: sender = " + getProcessID());
		         }
	         }
	         if (useSingleFlag) {
	        	 readyToRead = true;
	         }
	         else {
	        	 readyToReadSocket = true;
	         }
	         if (debugOut && usingToken) {
	        	 System.out.println("[Write]=> " + towrite + " socket bytes written");
		     }
	    }
	    
	    // Nio write: for now, just piggyback the original message with the integer lts 
	    public void packClock(SocketChannel s) throws IOException {
	    	int buflen = BUFLEN;

	    	if (trackingSender) {
	    		buflen += BUFLEN+getProcessID().getBytes().length;
		    }
	    	ByteBuffer buf = ByteBuffer.allocate(buflen);
	        byte[] ltsarray = intToByteArray(getTimestamp(), buf.order());

	        buf.put(ltsarray);
	        if (trackingSender) {
	        	byte[] snlenarray = intToByteArray(getProcessID().getBytes().length, buf.order());//ByteOrder.LITTLE_ENDIAN);
	        	buf.put(snlenarray);
	        	buf.put(getProcessID().getBytes());
	        }
	        buf.flip();
	        s.write(buf);
	        
	        if (debugOut) {
	        	 System.out.println("[NIO Channel/SocketChannel @ " + this.hostId() + "]: clock sent = " + 
	        			 pickClock(this.getTimestamp(), false));
	        	 if (trackingSender) {
	        		 System.out.println("[NIO Channel/SocketChannel @ " + this.hostId() + "]: sender = " + getProcessID());
		         }
	         }
	        if (useSingleFlag) {
	        	 readyToRead = true;
	         }
	         else {
	        	 readyToReadNio = true;
	         }
	    }
	    
	    // Nio Async write: for now, just piggyback the original message with the integer lts
	    // returned an augmented buffer which holds the logic clock appended with original buffer content
	    private ByteBuffer packClock(ByteBuffer buf_tosend) throws IOException {
	    	//byte[] lenarray = intToByteArray(buf_tosend.remaining());
	        byte[] ltsarray = intToByteArray(getTimestamp(), buf_tosend.order());
	        //ByteBuffer buf = ByteBuffer.allocate(BUFLEN*2 + buf_tosend.remaining());
	        int buflen = BUFLEN + buf_tosend.remaining();
	        if (usingToken) {
	        	buflen += BUFLEN;
	        }
	        if (trackingSender) {
	        	buflen += BUFLEN+getProcessID().getBytes().length;
	        }
	        ByteBuffer buf = ByteBuffer.allocate(buflen);
	        if (usingToken) {
	        	byte[] tokenarray = intToByteArray(buflen, buf_tosend.order());
	        	if (debugOut) {
	        		System.out.println("=>[All to Write] " + "ANIO token to send " + buflen);
	        	}
	        	buf.put(tokenarray);
	        }
	        //buf.put(lenarray);
	        buf.put(ltsarray);
	        //buf_tosend.rewind();
	        if (trackingSender) {
	        	byte[] snlenarray = intToByteArray(getProcessID().getBytes().length, buf_tosend.order()); //ByteOrder.LITTLE_ENDIAN);
	        	buf.put(snlenarray);
	        	buf.put(getProcessID().getBytes());
	        }
	        buf.put(buf_tosend);
	        
	        if (debugOut) {
	        	 System.out.println("[NIO Async Channel/SocketChannel @ " + this.hostId() + "]: clock sent = " + 
	        			 pickClock(this.getTimestamp(), false));
	        	 if (trackingSender) {
	        		 System.out.println("[NIO Async Channel/SocketChannel @ " + this.hostId() + "]: sender = " + getProcessID());
		         }
	        }
	        
	        return buf;
	    }
	    
	    public int packClock(SocketChannel s, ByteBuffer src) throws IOException {
	    	//System.out.println("src.remaining=" + src.remaining());
			ByteBuffer tosend = this.packClock(src);
			tosend.flip();
			int ret = s.write(tosend);
	        if (useSingleFlag) {
	        	 readyToRead = true;
	         }
	         else {
	        	 readyToReadANio = true;
	         }
	        //src.position(src.position() + src.remaining());
	        if (debugOut && usingToken) {
	        	System.out.println("=>[Write] " + ret + " ANIO bytes written");
	        }
	        return ret;
		}
	    public long packClock(SocketChannel s, ByteBuffer[] srcs, int offset, int length) throws IOException {
			/** use the first bytebuffer of the sequence for logic clock transmission */
			ByteBuffer tosend = this.packClock(srcs[offset]);
			srcs[offset] = tosend;
			srcs[offset].flip();
			long ret = s.write(srcs, offset, length);
	        if (useSingleFlag) {
	        	readyToRead = true;
	        }
	        else {
	        	readyToReadANio = true;
	        }
	        if (debugOut && usingToken) {
	        	System.out.println("=>[Write] " + ret + " ANIO bytes written");
	        }
			return ret;
		}
	}
	
	private static boolean g_intercept = true;
	public static void disable() { 
		g_intercept = false; 
		IFM.dtSocketInputStream.intercept = g_intercept;
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
	public synchronized static void dist_nioread(SocketChannel s){
		if (debugOut)
			System.out.println("******************** [dist_nioread " + s + "]: getProcessID = " + getProcessID());
		receivedMessages+="[dist_nioread " + s + "]: getProcessID = " + getProcessID()+"\n";
		if (!isEnabled()) { return; }
		
		try {
			g_lgclock.retrieveClock(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	// probe for NIO writes	
	public synchronized static void dist_niowrite(SocketChannel s){
		if (!isEnabled()) { return; }
		try {
			g_lgclock.packClock(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	////----------------------------------- proprietary probes for IPC data transfers through ObjectIn/OutputStream:read/writeObject ---- 
	// probe for objectInputStream readObject
	public synchronized static void dist_objstreamread(InputStream is){
		if (debugOut)
			System.out.println("******************** [dist_objstreamread " + is + "]: getProcessID = " + getProcessID());
		receivedMessages+="[dist_objstreamread " + is + "]: getProcessID = " + getProcessID()+"\n";
		//System.out.println("did nothing.");
		if (!isEnabled()) { return; }
		
		try {
			g_lgclock.retrieveClock(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// probe for NIO writes	
	public synchronized static void dist_objstreamwrite(OutputStream os){
		//System.out.println("did nothing.");
		if (debugOut)
			System.out.println("******************** [dist_objstreamwrite " + os + "]: getProcessID = " + getProcessID());
		if (!isEnabled()) { return; }
		try {
			g_lgclock.packClock(os,0);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//// ----------------------------------- deal with NIOs traffics working at asynchronous mode
	public synchronized static int dist_async_nioread(SocketChannel s, ByteBuffer dst) throws IOException {
		if (debugOut)
		System.out.println("******************** [dist_async_nioread1 " + s +" dst=" + dst + "]: getProcessID = " + getProcessID());
		receivedMessages+="[dist_async_nioread1 " + s +" dst=" + dst + "]: getProcessID = " + getProcessID()+"\n";
		if (!isEnabled()) {
			//g_lgclock.retrieveClock(s);
			return s.read(dst);
		}
		
		int ret = 0;
		try {
			ret = g_lgclock.retrieveClock(s, dst);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	public synchronized static long dist_async_nioread(SocketChannel s, ByteBuffer[] dsts) throws IOException {
		if (debugOut)
		System.out.println("******************** [dist_async_nioread2 " + s +" dsts=" + dsts + "]: getProcessID = " + getProcessID());
		receivedMessages+="[dist_async_nioread2 " + s +" dsts=" + dsts + "]: getProcessID = " + getProcessID()+"\n";
		if (!isEnabled()) {
			//g_lgclock.retrieveClock(s);
			return s.read(dsts);
		}
		return dist_async_nioread(s, dsts, 0, dsts.length);
	}
	public synchronized static long dist_async_nioread(SocketChannel s, ByteBuffer[] dsts, int offset, int length) throws IOException {
		if (debugOut)
		System.out.println("******************** [dist_async_nioread3 " + s +" dsts=" + dsts +" offset=" + offset +" length=" + length + "]: getProcessID = " + getProcessID());
		receivedMessages+="[dist_async_nioread3 " + s +" dsts=" + dsts +" offset=" + offset +" length=" + length + "]: getProcessID = " + getProcessID()+"\n";
		if (!isEnabled()) {
			//g_lgclock.retrieveClock(s);
			return s.read(dsts, offset, length);
		}
		
		long ret = 0;
		try {			
			ret = g_lgclock.retrieveClock(s, dsts, offset, length);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public synchronized static int dist_async_niowrite(SocketChannel s, ByteBuffer src) throws IOException {
		//System.out.println("******************** [dist_async_niowrite1 " + s +" src=" + src + "]: getProcessID = " + getProcessID());
		if (!isEnabled()) {
			//g_lgclock.packClock(s);
			return s.write(src);
		}
		
		int ret = 0;
		try {
			ret = g_lgclock.packClock(s, src);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
		
	}
	public synchronized static long dist_async_niowrite(SocketChannel s, ByteBuffer[] srcs) throws IOException {
		//System.out.println("******************** [dist_async_niowrite2 " + s +" srcs=" + srcs + "]: getProcessID = " + getProcessID());
		if (!isEnabled()) {
			//g_lgclock.retrieveClock(s);
			return s.write(srcs);
		}
		
		return dist_async_niowrite(s, srcs, 0, srcs.length);
	}
	public synchronized static long dist_async_niowrite(SocketChannel s, ByteBuffer[] srcs, int offset, int length) throws IOException {
		//System.out.println("******************** [dist_async_niowrite3 " + s +" srcs=" + srcs + "]: getProcessID = " + getProcessID());
		if (!isEnabled()) {
			//g_lgclock.retrieveClock(s);
			return s.write(srcs, offset, length);
		}
		
		long ret = 0;
		try {
			ret = g_lgclock.packClock(s, srcs, offset, length);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

//	/** give the full EAS trace length */
//	public synchronized static int getFullTraceLength() {
//		synchronized (g_counter) {
//			return g_counter;
//		}
//	}
}