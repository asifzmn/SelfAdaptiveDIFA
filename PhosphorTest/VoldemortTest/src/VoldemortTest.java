import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import edu.columbia.cs.psl.phosphor.runtime.Tainter;
import org.easymock.EasyMock; 
import org.easymock.IMocksControl; 
import org.junit.Test;

import com.google.common.collect.Lists;

import voldemort.cluster.Cluster; import voldemort.cluster.failuredetector.NoopFailureDetector; 
import voldemort.serialization.SlopSerializer; 
import voldemort.server.StoreRepository; 
import voldemort.server.VoldemortConfig; 
import voldemort.store.memory.InMemoryStorageEngine; 
import voldemort.store.metadata.MetadataStore; 
import voldemort.store.metadata.MetadataStore.VoldemortState; 
import voldemort.store.slop.Slop; import voldemort.store.slop.Slop.Operation; 
import voldemort.store.slop.SlopStorageEngine; 
import voldemort.utils.ByteArray; import voldemort.versioning.Versioned;

public class VoldemortTest {
	public static int getTaint(String description) {
		return Tainter.getTaint(description.toCharArray()[0]);
	}

	public static String taintedString(String string) {
		char[] c = Tainter.taintedCharArray(string.toCharArray(), 5);
		String r = new String(c);
		return r;
	}

	public static String taintedString() {
		char[] c = Tainter.taintedCharArray("abcdefghi".toCharArray(), 5);
		String r = new String(c);
		return r;
	}
	
	@Test
	public void test101() {
		try {

			Class c = Class.forName("voldemort.store.configuration.FileBackedCachingStorageEngine");
			Object o = c.newInstance();
			Method  m = getDeclaredMethodByName(c, "loadData");		
			m.invoke(o);
			
			
			Class c2 = Class.forName("voldemort.serialization.json.JsonReader");
			Object o2 = c2.newInstance();
			Method m2 = c2.getMethod("next");
			int i = (int) m2.invoke(o2);
			String s=""+i;
			assertTrue(getTaint(s) != 0);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void test155() {
		try {

			Class c = Class.forName("voldemort.store.configuration.FileBackedCachingStorageEngine");
			Object o = c.newInstance();
			Method  m = getDeclaredMethodByName(c, "loadData");		
			m.invoke(o);
			
			
			Class c2 = Class.forName("voldemort.server.socket.SocketServerSession");
			Object o2 = c2.newInstance();
			Method m2 = c2.getMethod("run");
			int i = (int) m2.invoke(o2);
			String s2=""+i;
			assertTrue(getTaint(s2) != 0);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    public static Method getDeclaredMethodByName(Class c, String name)  {
      	 Method[] methods= c.getDeclaredMethods();
      	 Method method =null;
           for (Method method2 : methods) {
               if (method2.getName().equals(name));
           }
           return method;
      }
    
    @Test
	public void test352() {
		try {

			Class c = Class.forName("voldemort.store.configuration.FileBackedCachingStorageEngine");
			Object o = c.newInstance();
			Method  m = getDeclaredMethodByName(c, "loadData");		
			m.invoke(o);
			
			
			Class c2 = Class.forName("voldemort.client.protocol.vold.VoldemortNativeClientRequestFormat");
			Object o2 = c2.newInstance();
			Method m2 = c2.getMethod("writeGetRequest");
			int i = (int) m2.invoke(o2);
			String s=""+i;
			assertTrue(getTaint(s) != 0);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    

    
    @Test
	public void test377() {
		try {

			Class c = Class.forName("voldemort.store.configuration.FileBackedCachingStorageEngine");
			Object o = c.newInstance();
			Method  m = getDeclaredMethodByName(c, "loadData");		
			m.invoke(o);
			
			
			Class c2 = Class.forName("voldemort.client.protocol.vold.VoldemortNativeClientRequestFormat");
			Object o2 = c2.newInstance();
			Method m2 = c2.getMethod("writeGetRequest");
			int i = (int) m2.invoke(o2);
			String s=""+i;
			assertTrue(getTaint(s) != 0);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    @Test
	public void test378() {
		try {

			Class c = Class.forName("voldemort.store.readonly.swapper.AdminStoreSwapper$1");
			Object o = c.newInstance();
			Method  m = getDeclaredMethodByName(c, "call");		
			String s0 =(String) m.invoke(o);
			
			
			Class c2 = Class.forName("voldemort.VoldemortAdminTool");
			Object o2 = c2.newInstance();
			Method m2 = c2.getMethod("main");  //executeClearRebalancing
			Object[] args = new Object[1];
			args[0]=new String [] { "1", "2"};		
			
			String s = (String) m2.invoke(null, args);
			assertTrue(getTaint(s) != 0);

		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    @Test
 	public void test381() {
 		try {

 			Class c = Class.forName("voldemort.store.configuration.FileBackedCachingStorageEngine");
 			Object o = c.newInstance();
 			Method  m = getDeclaredMethodByName(c, "loadData");		
 			m.invoke(o);
 			
 			
 			Class c2 = Class.forName("voldemort.client.protocol.vold.VoldemortNativeClientRequestFormat");
 			Object o2 = c2.newInstance();
 			Method m2 = c2.getMethod("writeGetRequest");
 			int i = (int) m2.invoke(o2);
 			String s=""+i;
 			assertTrue(getTaint(s) != 0);
 		} catch (InstantiationException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (IllegalAccessException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (ClassNotFoundException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (NoSuchMethodException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (IllegalArgumentException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (InvocationTargetException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 	}
    

    
    @Test
 	public void test387() {
 		try {

 			Class c = Class.forName("voldemort.store.configuration.FileBackedCachingStorageEngine");
 			Object o = c.newInstance();
 			Method  m = getDeclaredMethodByName(c, "loadData");		
 			m.invoke(o);
 			
 			
 			Class c2 = Class.forName("voldemort.client.protocol.vold.VoldemortNativeClientRequestFormat");
 			Object o2 = c2.newInstance();
 			Method m2 = c2.getMethod("writeGetRequest");
 			int i = (int) m2.invoke(o2);
 			String s=""+i;
 			assertTrue(getTaint(s) != 0);
 		} catch (InstantiationException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (IllegalAccessException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (ClassNotFoundException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (NoSuchMethodException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (IllegalArgumentException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (InvocationTargetException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 	}
//  @Test
//  public void testShouldCleanDataForInvalidSlopData() {
//      IMocksControl control = EasyMock.createControl();
//      StoreRepository storeRepo = control.createMock(StoreRepository.class);
//      MetadataStore metadataStore = control.createMock(MetadataStore.class);
//      VoldemortConfig voldemortConfig = control.createMock(VoldemortConfig.class);
//      Cluster cluster = new Cluster(" test ", Lists.newArrayList(node(1, 0), node(3, 1)));
//      EasyMock.expect(metadataStore.getCluster()).andReturn(cluster).anyTimes();
//
//      EasyMock.expect(voldemortConfig.getSlopMaxReadBytesPerSec()).andReturn(1000L).anyTimes();
//      EasyMock.expect(metadataStore.getServerState())
//              .andReturn(VoldemortState.NORMAL_SERVER)
//              .anyTimes();
//
//      InMemoryStorageEngine&lt;ByteArray, byte[], byte[]&gt; innerStorageEngine = new InMemoryStorageEngine&lt;ByteArray, byte[], byte[]&gt;(&quot;slop&quot;,
//                                                                                                                                 new ConcurrentHashMap&lt;ByteArray, List&lt;Versioned&lt;byte[]&gt;&gt;&gt;());

//      SlopStorageEngine slopEngine = new SlopStorageEngine(innerStorageEngine, cluster);
//      SlopSerializer slopSerializer=new SlopSerializer();
//      Slop slop=new Slop(&quot;test&quot;, Operation.PUT, new byte[]{1,1}, new byte[]{1,1}, 1, new Date());
//      Versioned&lt;byte[]&gt; versionedSlop=versioned(slopSerializer.toBytes(slop), vc(ce(1, 8)));
//      slopEngine.put(slop.getKey(), versionedSlop, null);
//
//      EasyMock.expect(storeRepo.getSlopStore()).andReturn(slopEngine).anyTimes();
//
//      EasyMock.expect(voldemortConfig.getSlopZonesDownToTerminate()).andReturn(0).anyTimes();
//      EasyMock.expect(voldemortConfig.getSlopMaxWriteBytesPerSec()).andReturn(1000L).anyTimes();
//      control.replay();
//
//      StreamingSlopPusherJob job = new StreamingSlopPusherJob(storeRepo,
//                                                              metadataStore,
//                                                              new NoopFailureDetector(),
//                                                              voldemortConfig,
//                                                              new Semaphore(1));
//      job.run();
//      control.verify();
//  }
}
