import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import edu.columbia.cs.psl.phosphor.runtime.Tainter;
//CVE-2018-8012
public class ZookeeperTest {
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
	public void testCVE20140085() {
		try {

			Class c = Class.forName("org.apache.zookeeper.ClientCnxn");
			Object o = c.newInstance();
			Method  m = getDeclaredMethodByName(c, "run");		
			m.invoke(o);
			
			System.out.println("testSourceSink() class 1");
			
			Class c2 = Class.forName("org.apache.jute.BinaryOutputArchive");
			Object o2 = c2.newInstance();
			Method m2 = c2.getMethod("writeBuffer");
			Byte b1 = Tainter.taintedByte((byte) 4, 4);
			Byte b2 = Tainter.taintedByte((byte) 5, 5);
			Byte[] b = {b1, b2};
			String imei = taintedString();
			String s = (String) m2.invoke(o2,b, imei );
			assertTrue(getTaint(s) != 0);
			System.out.println(" imei = "+imei);
			System.out.println(" s = "+s);
			//System.out.println(" getTaint(s)= "+getTaint(s));
			assertTrue( getTaint(s) != 0);   // || getTaint(s) != 0			
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
	public void testCVE20188012() {
		try {

			Class c = Class.forName("org.apache.zookeeper.ClientCnxn");
			Object o = c.newInstance();
			Method m = getDeclaredMethodByName(c, "run");			
			m.invoke(o);
			
			System.out.println("testSourceSink() class 1");
			
			Class c2 = Class.forName("org.apache.zookeeper.server.ZKDatabase");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2, "loadDataBase");	
			String s = (String) m2.invoke(o2);
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
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testBug2569() {
		try {
			String imei = taintedString();
			Class c = Class.forName("org.apache.jute.BinaryInputArchive");
			Object o = c.newInstance();
			Method m = c.getMethod("readByte", String.class); 
			m.invoke(o, imei);
			
			Class c2 = Class.forName("org.apache.jute.BinaryOutputArchive");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2, "writeByte");
			Byte b2 = Tainter.taintedByte((byte) 4, 4);
			String imei2 = taintedString();
			String s2 = (String) m2.invoke(o2, b2, imei2);
			assertTrue(s2.equals(imei)|| getTaint(s2) != 0 );			
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
	public void testCVE20190201() {
		try {
			String imei = taintedString();
			Class c = Class.forName("org.apache.jute.BinaryInputArchive");
			Object o = c.newInstance();
			Method m = c.getMethod("readByte", String.class); 
			m.invoke(o, imei);
			
			Class c2 = Class.forName("org.apache.jute.BinaryOutputArchive");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2, "writeByte");
			Byte b2 = Tainter.taintedByte((byte) 4, 4);
			String imei2 = taintedString();
			String s2 = (String) m2.invoke(o2, b2, imei2);
			assertTrue(s2.equals(imei)|| getTaint(s2) != 0 );			
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
}
