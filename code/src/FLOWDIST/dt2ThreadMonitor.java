package FLOWDIST;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.Map;

import MciaUtil.MethodEventComparator;

import java.util.concurrent.atomic.AtomicInteger;

/** 
*	compared to distMonitor which works assuming that each party involved in the message
*   passing (either via socket or via channels) runs in a separate process,
*   distThreadMonitor can work in situations where each such party runs in a thread and all
*   parties (i.e., all distributed components) can be simply executing in a single (JVM) process;
*   this may not represent real-world distributed computing cases, but is useful for our experimental
*   purposes: in the unit-test inputs of Zookeeper for instance, all parties are executed in a single process 
*   while the message passing happens among threads instead of processes.
*/
public class dt2ThreadMonitor {

	/* for DUAF/Soot to access this class */
	public static void __link() { 
		disttaint.dtSocketOutputStream.__link();
		disttaint.dtSocketInputStream.__link();
	}
	
	////////////////////////////////////////////////////////////////
	// 1. Internal Events
	////////////////////////////////////////////////////////////////
	
	/* first events */
	protected static Map<Thread, HashMap<String, Integer>> allF = new HashMap<Thread, HashMap<String, Integer>>();
	/* last events */
	protected static Map<Thread, HashMap<String, Integer>> allL = new HashMap<Thread, HashMap<String, Integer>>();
	
	/* all events */
	protected static Map<Thread, LinkedHashMap<Integer, String>> allA = new HashMap<Thread, LinkedHashMap<Integer, String>>();

	/* first message-receiving events */
	protected static Map<Thread, HashMap<String, Integer>> allS = new HashMap<Thread, HashMap<String, Integer>>();
	
	/* output file for serializing the two event maps */
	protected static String fnEventMaps =  "";

	/* a flag ensuring the initialization and termination are both executed exactly once and they are paired*/
	protected static boolean bInitialized = false;
	
	protected static boolean bFullSequence = false;
	
	/* debug flag: e.g. for dumping event sequence to human-readable format for debugging purposes, etc. */
	protected static boolean debugOut = true;
	protected static boolean dumpEvents = false;
	protected static boolean forcingMirrorIO = false;
	private static boolean useSingleFlag = false; // force mirror i/o for both socket and NIO network I/Os or mirror each type separately only
	protected static boolean usingToken = true; // use a token at the end of each clock message for identification and verification
	protected static boolean trackingSender = false; // send the identify of message sender with messages being sent
	public static void turnDebugOut(boolean b) { debugOut = b; }
	
	private static Map<String, Integer> getCreateF() {
		synchronized (allF) {
			Thread t = Thread.currentThread();
			HashMap<String, Integer> f = allF.get(t);
			if (f == null) {
				f = new HashMap<String, Integer>();
				allF.put(t, f);
			}
			return f;
		}		
	}
	private static Map<String, Integer> getCreateL() {
		synchronized (allL) {
			Thread t = Thread.currentThread();
			HashMap<String, Integer> l = allL.get(t);
			if (l == null) {
				l = new HashMap<String, Integer>();
				allL.put(t, l);
			}
			return l;
		}		
	}
	private static Map<Integer, String> getCreateA() {
		synchronized (allA) {
			Thread t = Thread.currentThread();
			LinkedHashMap<Integer, String> a = allA.get(t);
			if (a == null) {
				a = new LinkedHashMap<Integer, String>();
				allA.put(t, a);
			}
			return a;
		}		
	}
	private static Map<String,Integer> getCreateS() {
		synchronized (allS) {
			Thread t = Thread.currentThread();
			HashMap<String,Integer> a = allS.get(t);
			if (a == null) {
				a = new HashMap<String,Integer>();
				allS.put(t, a);
			}
			return a;
		}		
	}
	public static logicClock getCreateClock() {
		synchronized (g_lgclocks) {
			Thread t = Thread.currentThread();
			logicClock g = g_lgclocks.get(t);
			if (g == null) {
				g = new logicClock(new AtomicInteger(0), getMAC()+getProcessID()+t);
				g_lgclocks.put(t, g);
			}
			return g;
		}
	}
		
	/* The name of serialization target file will be set by EARun via this setter */
	public static void setEventMapSerializeFile(String fname) {
		fnEventMaps = fname;
		
		getCreateF().clear();
		getCreateL().clear();
		getCreateA().clear();
		getCreateClock().initClock(1);
	}
	
	/** Used to avoid infinite recursion */
	private static boolean active = false;
	
	/* initialize the two maps and the global counter upon the program start event */		
	public synchronized static void initialize() throws Exception{
		System.out.println("**************dt2ThreadMonitor::initialize()  0th");
		getCreateF().clear();
		getCreateL().clear();
		getCreateA().clear();
		getCreateS().clear();
		
		logicClock g_lgclock = getCreateClock();
		Integer g_counter = g_lgclock.getTimestamp();
		synchronized (g_counter) {
			g_counter = 1;
		
			System.out.println("**************dt2ThreadMonitor::initialize()  1th");
			if (bFullSequence) {
				getCreateA().put(g_counter, "program start");
				g_counter++;
			}
		}
		bInitialized = true;
		g_lgclock.initClock(1);
		System.out.println("**************dt2ThreadMonitor::initialize()  2th");
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
				dt2ThreadMonitor.terminate("Forced upon external termination");
			} catch (Exception e) {
				e.printStackTrace();
			}
          }
        } ) ;
		
		String debugFlag = System.getProperty("ltsDebug");
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
		
		System.out.println("distThreadMonitor[prec-imp] starts working ......");
		
		dtSocketInputStream.debugOut = debugOut;
		dtSocketInputStream.usingToken = usingToken;
		dtSocketInputStream.intercept = g_intercept;
		dtSocketOutputStream.intercept = g_intercept;
		
		
		
		if (trackingSender) {
			System.out.println("distThreadMonitor getCreateS().put(getProcessID(), Integer.MAX_VALUE) getProcessID()="+getProcessID());
			// just record the ID of local process 			
			getCreateS().put(getProcessID(), Integer.MAX_VALUE);
		}
	}
	
	public synchronized static void enter(String methodname) {
		if (active) return;
		active = true;
		try { enter_impl(methodname); }
		finally { active = false; }
	}
	public synchronized static void enter_impl(String methodname) {
		try {
			Map<String, Integer> F = getCreateF();
			logicClock g_lgclock = getCreateClock();
			Integer g_counter = g_lgclock.getTimestamp();
			synchronized (g_counter) {
				Integer curTS = (Integer) F.get(methodname);
				if (null == curTS) {
					curTS = 0;
					F.put(methodname, g_counter);
				}
				getCreateL().put(methodname, g_counter);
	
				if (bFullSequence) {
					getCreateA().put(g_counter, methodname+":e");
				}
				g_lgclock.increment();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static void returnFrom(String methodname, String calleeName) {
		if (active) return;
		active = true;
		try { returnFrom_impl(methodname, calleeName); }
		finally { active = false; }
	}

	/** returnFrom events are handled the same as are returnInto events */
	public synchronized static void returnFrom_impl(String methodname, String calleeName){
		try {
			Map<String, Integer> L = getCreateL();
			logicClock g_lgclock = getCreateClock();
			Integer g_counter = g_lgclock.getTimestamp();
			synchronized (g_counter) {
				Integer curTS = (Integer) L.get(methodname);
				if (null == curTS) {
					curTS = 0;
				}
				
				L.put(methodname, g_counter);
				if (bFullSequence) {
					getCreateA().put(g_counter, methodname+":x");
				}
	
				g_lgclock.increment();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static void returnInto(String methodname, String calleeName){
		if (active) return;
		active = true;
		try { returnInto_impl(methodname, calleeName); }
		finally { active = false; }
	}
	/* the callee could be either an actual method called or a trap */
	public synchronized static void returnInto_impl(String methodname, String calleeName){
		try {
			Map<String, Integer> L = getCreateF();
			logicClock g_lgclock = getCreateClock();
			Integer g_counter = g_lgclock.getTimestamp();
			synchronized (g_counter) {
				Integer curTS = (Integer) L.get(methodname);
				if (null == curTS) {
					curTS = 0;
				}
				
				L.put(methodname, g_counter);
				if (bFullSequence) {
					getCreateA().put(g_counter, methodname+":i");
				}
	
				g_lgclock.increment();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* 
	 * dump the Execute-After sequence that is converted from the two event maps 
	 * upon program termination event 
	 * this is, however, not required but useful for debugging 
	 *
	 */
	public synchronized static void terminate(String where) throws Exception {
		if (bInitialized) {
			bInitialized = false;
		}
		else {
			return;
		}

		Integer g_counter = getCreateClock().getTimestamp();
		synchronized (g_counter) {
			getCreateA().put(g_counter, "program end");
		}
		if (dumpEvents) {
			dumpEvents();
		}
		
		// set default trace file name
		if (fnEventMaps.length()<1) {
			String fndefault = System.getProperty("user.dir") + File.separator + "distEAtrace_";
			fndefault += System.currentTimeMillis() + ".em";
			fnEventMaps = fndefault;
		}
		serializeEvents();
	}
	
	////////////////////////////////////////////////////////////////
	// 2. communication Events
	////////////////////////////////////////////////////////////////
	private static boolean threadAsProcess = true;
	public static final int BUFLEN = 4;
	
	protected static final Map<Thread, logicClock> g_lgclocks = new HashMap<Thread, logicClock>(); 
		//new logicClock(new AtomicInteger(0), getMAC()+getProcessID());
	public synchronized static logicClock getlgclock() { return g_lgclocks.get(Thread.currentThread()); }
	
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
		return ManagementFactory.getRuntimeMXBean().getName()+'-'+Thread.currentThread().getId();
	}
	private static void onRecvSenderID(String senderid) {
		/** for each process, only record the first message receiving event for each unique sender */
		Map<String,Integer> tm = getCreateS();
		if (tm.isEmpty()) {
			tm.put(getProcessID(), Integer.MAX_VALUE);
		}
		Integer curval = tm.get(senderid);
		System.out.println("******************** onRecvSenderID senderid"+senderid +" curval = " + curval +" getProcessID = " + getProcessID());
		if (null == curval) {
			if (debugOut && senderid.compareToIgnoreCase(getProcessID())==0) {
				System.out.println("WARNING: receive a message from the local process being sent to itself !!");
			}
			tm.put(senderid, getCreateClock().getTimestamp());
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
	        
	        /*
	        if (usingToken) {
		        if (in.markSupported()) in.mark(Integer.MAX_VALUE);
		        int flag = byteArrayToInt(buf, ByteOrder.LITTLE_ENDIAN);
		        if (flag != TOKEN_FLAG) {
		        	System.out.println("[NIO Async Channel/SocketChannel @ " + this.hostId() + "]: no clock received.");
		        	if (in.markSupported()) in.reset();
		        	return;
		        }
		        
		        if (in.available() < BUFLEN) return;
	        }
	        */
				
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
		        System.out.println("******************** [receive message ProcessID"+getProcessID()+ " from sender: " + getProcessID());
		        if (debugOut) {
		        	System.out.println("[Socket I/O Stream @ " + this.hostId() + "]: receive message from sender: " + sender);
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
	        
	        /* CANNOT adopt the token strategy because we don't know how many bytes to be read next by the original i/o call before which 
	         * this interception is instrumented
	        if (usingToken) {
	        	if (debugOut) {
					System.out.println("NIO bytes to read: " + bytesToReadNIO);
				}
	        	if (bytesToReadNIO > 0) {
	        		bytesToReadNIO -= bytesRead;
	        		if (bytesToReadNIO < 0) bytesToReadNIO = 0;
	        		if (debugOut) {
						System.out.println(bytesRead + " bytes read for original message without NIO token+clock piggybacked");
					}
	        		return;
	        	}
	        	
	        	// 1: read the token, namely the data length the peer sent in recent write operation 
	        	buf.rewind();
	        	byte[] tokenarray = new byte[BUFLEN];
	        	buf.get(tokenarray);
		        int len = byteArrayToInt(tokenarray, buf.order());
		        bytesToReadNIO = len;
		        if (debugOut) {
	        		System.out.println(BUFLEN + " bytes read for NIO token");
		        	System.out.println("NIO token received is " + len);
	        	}
		        
		        bytesToReadNIO -= BUFLEN;
		        
		        buf.clear();
		        bytesRead = s.read(buf);
		        if (debugOut) {
	        		System.out.println(bytesRead + " bytes read for NIO clock");
	        	}
		        if (bytesRead == -1 || bytesRead == 0) {
		           System.err.println("!!!!!Unexpected ERROR when retrieving NIO clock after getting token!!!!!");
		           return;
		        }
		        
		        bytesToReadNIO -= BUFLEN;
	        }
	    	*/
	        
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
	        	/*
	        	if (flag != TOKEN_FLAG) {
		        //if (!isClock(lts, buf_recved.order()==ByteOrder.BIG_ENDIAN)) {
		        	//buf_recved.reset();
		        	buf_recved.position(pos);
		        	//lts = pickClock(lts,buf_recved.order()==ByteOrder.BIG_ENDIAN);
		        	//System.out.println("[NIO Async Channel/SocketChannel @ " + this.hostId() + "]: invalid clock " + lts + " ignored.");
		        	System.out.println("[NIO Async Channel/SocketChannel @ " + this.hostId() + "]: no clock received.");
		        	return 0;
		        }
		        */
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
	        	/*
	        	if (usingToken) {
		        	if (debugOut) {
		        		System.out.println(buf_recved.remaining() + " bytes remained before reading sender name itself. limit=" + buf_recved.limit());
		        	}
		        	if (bytesToReadANIO > 0 && buf_recved.remaining() == 0) {
		        		buf_recved.clear();
		    			nb = s.read(buf_recved);
		    			System.out.println(nb + " bytes fetched from the channel before reading sender name itself.");
		    			buf_recved.rewind();
		        	}
	        	}
	        	*/
	        	
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
	    	//dst = ByteBuffer.allocate(BUFLEN + dst.remaining());
	    	//dst.rewind();
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
	    	
	    	//System.out.println(nb + " bytes read.");
	    	/*
	    	if (bytesToRead > 0) {
	    		System.out.println(bytesToRead + " bytes available.");
	    		if (nb > 0) bytesToRead -= nb;
	    		if (bytesToRead < 0) bytesToRead = 0;
	    		return nb;
	    	}
	    	*/
	    	
	    	//if (s.socket().getInputStream().markSupported()) s.socket().getInputStream().mark(Integer.MAX_VALUE);
				
			//assert nb >= BUFLEN;
			//dst.rewind();
			
			//byte[] lenArray = new byte[BUFLEN];
		    //dst.get(lenArray);
		    //bytesToRead = byteArrayToInt(lenArray);
			
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
	    	/*
	    	 ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
	         ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
	         objOut.writeObject();
	         byte[] darray = byteOut.toByteArray();
	         */
	    	/*
	    	 if (usingToken) {
	    		 byte[] flagarray = intToByteArray(TOKEN_FLAG,ByteOrder.LITTLE_ENDIAN);
		         out.write(flagarray);
	    	 }
	    	 */
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
	         /*
	         if (useSingleFlag) {
	        	 readyToRead.incrementAndGet();
	         }
	         else {
	        	 readyToReadSocket.incrementAndGet();
	         }
	         */
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
	    	//if (!s.isOpen()) return;
	    	/*
	    	ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
	        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
	        objOut.writeObject();
	        byte[] darray = byteOut.toByteArray();
	        */
	    	int buflen = BUFLEN;
	    	/*
	    	if (usingToken) {
	    		buflen += BUFLEN;
	    	}
	    	*/
	    	if (trackingSender) {
	    		buflen += BUFLEN+getProcessID().getBytes().length;
		    }
	    	ByteBuffer buf = ByteBuffer.allocate(buflen);
	        byte[] ltsarray = intToByteArray(getTimestamp(), buf.order());
	        /*
	        if (usingToken) {
	        	byte[] tokenarray = intToByteArray(buflen, buf.order());
	        	if (debugOut) {
	        		System.out.println("NIO token to send " + buflen);
	        	}
	        	buf.put(tokenarray);
	        }
	        */
	        buf.put(ltsarray);
	        if (trackingSender) {
	        	byte[] snlenarray = intToByteArray(getProcessID().getBytes().length, buf.order());//ByteOrder.LITTLE_ENDIAN);
	        	buf.put(snlenarray);
	        	buf.put(getProcessID().getBytes());
	        }
	        buf.flip();
	        s.write(buf);
	        //int ret = s.write(buf);
	        //System.out.println(ret + " bytes for clock written to socketChannel " +s);

	        /*
	        buf = ByteBuffer.allocate(darray.length);
	        buf.put(darray);
	        buf.rewind();
	        s.write(buf);
	        */
	        
	        if (debugOut) {
	        	 System.out.println("[NIO Channel/SocketChannel @ " + this.hostId() + "]: clock sent = " + 
	        			 pickClock(this.getTimestamp(), false));
	        	 if (trackingSender) {
	        		 System.out.println("[NIO Channel/SocketChannel @ " + this.hostId() + "]: sender = " + getProcessID());
		         }
	         }
	        /*
	        if (useSingleFlag) {
	        	readyToRead.incrementAndGet();
	        }
	        else {
	        	readyToReadNio.incrementAndGet();
	        }
	        */
	        if (useSingleFlag) {
	        	 readyToRead = true;
	         }
	         else {
	        	 readyToReadNio = true;
	         }
	        /*
	        if (debugOut && usingToken) {
	        	System.out.println(ret + " NIO bytes written");
	        }
	        */
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
	        if (trackingSender) {
	        	byte[] snlenarray = intToByteArray(getProcessID().getBytes().length, buf_tosend.order()); //ByteOrder.LITTLE_ENDIAN);
	        	buf.put(snlenarray);
	        	buf.put(getProcessID().getBytes());
	        }
	        //buf_tosend.rewind();
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
			/*
	        if (useSingleFlag) {
	        	readyToRead.incrementAndGet();
	        }
	        else {
	        	readyToReadANio.incrementAndGet();
	        }
	        */
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
			/*
	        if (useSingleFlag) {
	        	readyToRead.incrementAndGet();
	        }
	        else {
	        	readyToReadANio.incrementAndGet();
	        }
	        */
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
	public synchronized static void dist_nioread(SocketChannel s){
		if (!isEnabled()) { return; }
		
		try {
			getCreateClock().retrieveClock(s);
			System.out.println("******************** [dist_nioread " + s + "]: getProcessID = " + getProcessID());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	// probe for NIO writes	
	public synchronized static void dist_niowrite(SocketChannel s){
		System.out.println("******************** [dist_niowrite " + s + "]: getProcessID = " + getProcessID());
		if (!isEnabled()) { return; }
		try {
			getCreateClock().packClock(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	////----------------------------------- proprietary probes for IPC data transfers through ObjectIn/OutputStream:read/writeObject ---- 
	// probe for objectInputStream readObject
	public synchronized static void dist_objstreamread(InputStream is){
		System.out.println("******************** [dist_objstreamread " + is + "]: getProcessID = " + getProcessID());
		//System.out.println("did nothing.");
		if (!isEnabled()) { return; }
		
		try {
			getCreateClock().retrieveClock(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// probe for NIO writes	
	public synchronized static void dist_objstreamwrite(OutputStream os){
		System.out.println("******************** [dist_objstreamwrite " + os + "]: getProcessID = " + getProcessID());
		//System.out.println("did nothing.");
		if (!isEnabled()) { return; }
		try {
			getCreateClock().packClock(os,0);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//// ----------------------------------- deal with NIOs traffics working at asynchronous mode
	public synchronized static int dist_async_nioread(SocketChannel s, ByteBuffer dst) throws IOException {
		System.out.println("******************** [dist_async_nioread1 " + s +" dst=" + dst + "]: getProcessID = " + getProcessID());
		if (!isEnabled()) {
			//getCreateClock().retrieveClock(s);
			return s.read(dst);
		}
		
		int ret = 0;
		try {
			ret = getCreateClock().retrieveClock(s, dst);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	public synchronized static long dist_async_nioread(SocketChannel s, ByteBuffer[] dsts) throws IOException {

		System.out.println("******************** [dist_async_nioread2 " + s +" dsts=" + dsts + "]: getProcessID = " + getProcessID());
		if (!isEnabled()) {
			//getCreateClock().retrieveClock(s);
			return s.read(dsts);
		}
		
		return dist_async_nioread(s, dsts, 0, dsts.length);
	}
	public synchronized static long dist_async_nioread(SocketChannel s, ByteBuffer[] dsts, int offset, int length) throws IOException {
		System.out.println("******************** [dist_async_nioread3 " + s +" dsts=" + dsts +" offset=" + offset +" length=" + length + "]: getProcessID = " + getProcessID());
		if (!isEnabled()) {
			//getCreateClock().retrieveClock(s);
			return s.read(dsts, offset, length);
		}
		
		long ret = 0;
		try {
			ret = getCreateClock().retrieveClock(s, dsts, offset, length);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public synchronized static int dist_async_niowrite(SocketChannel s, ByteBuffer src) throws IOException {
		System.out.println("******************** [dist_async_niowrite1 " + s +" src=" + src + "]: getProcessID = " + getProcessID());
		if (!isEnabled()) {
			//getCreateClock().packClock(s);
			return s.write(src);
		}
		
		int ret = 0;
		try {
			ret = getCreateClock().packClock(s, src);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
		
	}
	public synchronized static long dist_async_niowrite(SocketChannel s, ByteBuffer[] srcs) throws IOException {
		System.out.println("******************** [dist_async_niowrite2 " + s +" srcs=" + srcs + "]: getProcessID = " + getProcessID());
		if (!isEnabled()) {
			//getCreateClock().retrieveClock(s);
			return s.write(srcs);
		}
		
		return dist_async_niowrite(s, srcs, 0, srcs.length);
	}
	public synchronized static long dist_async_niowrite(SocketChannel s, ByteBuffer[] srcs, int offset, int length) throws IOException {
		System.out.println("******************** [dist_async_niowrite3 " + s +" srcs=" + srcs + "]: getProcessID = " + getProcessID());
		if (!isEnabled()) {
			//getCreateClock().retrieveClock(s);
			return s.write(srcs, offset, length);
		}
		
		long ret = 0;
		try {
			ret = getCreateClock().packClock(s, srcs, offset, length);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	////////////////////////////////////////////////////////////////
	// 3. common serialization utilities
	////////////////////////////////////////////////////////////////
	
	protected synchronized static void dumpEvents() {
		for (Thread t : allF.keySet()) {
			System.out.println("\n ******* Thread " + t.getName() + " *******\n");
			
			Map<String, Integer> F = allF.get(t);
			assert allL.containsKey(t);
			Map<String, Integer> L = allL.get(t);
			
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
				assert allA.containsKey(t);
				Map<Integer, String> A = allA.get(t);
				System.out.println("\n[ Full Sequence of Method Entry and Returned-into Events]\n");
				TreeMap<Integer, String> treeA = new TreeMap<Integer, String> ( A );
				System.out.println(treeA);
				
				// DEBUG ONLY
				for (Integer ts : treeA.keySet()) {
					System.out.println(ts+"\t"+treeA.get(ts));
				}
			}
		}
	}
	
	/**
	 * since the static member will be hidden when inherited by descendants, there is no point of declaring it as
	 * public/protected to let it be inheritable; Simply speaking, it is associated with no memory object, thus there is
	 * no way to implement polymorphism, which relies on a memory block where the virtual table can reside for 
	 * implementing the polymorphism
	 */
	/*protected*/ private synchronized static void serializeEvents() {
		/* serialize for later deserialization in the post-processing phase when impact set is to be computed*/
		int nt = 0;
		for (Thread t : allF.keySet()) {
			if (!allS.containsKey(t) || !allL.containsKey(t)) continue;
			if (debugOut) {
				System.out.println("******* Dumping Trace for Thread " + t.getName() + " *******");
			}
			
			Map<String, Integer> F = allF.get(t);
			assert allL.containsKey(t);
			Map<String, Integer> L = allL.get(t);
			
			Map<String, Integer> S = allS.get(t);
			
			if ( !fnEventMaps.isEmpty() ) {
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(fnEventMaps+"_thread"+ (++nt) + ".em");
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					// TreeMap is not serializable as is HashMap
					oos.writeObject(F);
					oos.writeObject(L);
					if (trackingSender) {
						oos.writeObject(S);
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
					// we won't allow the confusion of overwriting the file with the event maps from multiple executions 
					// fnEventMaps = "";
				}
			}
		}
	}
	
	/** give the full EAS trace length */
	public synchronized static int getFullTraceLength() {
		Integer g_counter = getCreateClock().getTimestamp();
		synchronized (g_counter) {
			return g_counter;
		}
	}
}

/* vim :set ts=4 tw=4 tws=4 */

