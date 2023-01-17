import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import edu.columbia.cs.psl.phosphor.runtime.Tainter;

public class ThriftTest {
	public static int getTaint(String description) {
		//System.out.println(" description="+description+" description.toCharArray()[0]="+description.toCharArray()[0]+" Tainter.getTaint(description.toCharArray()[0])="+Tainter.getTaint(description.toCharArray()[0]));		
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
	public void testTIOStreamTransport_TSocket() {
		try {
			Byte b1 = Tainter.taintedByte((byte) 4, 4);
			Byte b2 = Tainter.taintedByte((byte) 5, 5);

			Byte[] b = {b1, b2};
			Integer i1 = Tainter.taintedInt(4, 4);
			Integer i2 = Tainter.taintedInt(5, 5);
			Class c = Class.forName("org.apache.thrift.transport.TIOStreamTransport");
			Object o = c.newInstance();
			Method m = getDeclaredMethodByName(c,"read");
			Integer i3= (Integer) m.invoke(o, b, i1, i2);

			//System.out.println("testTIOStreamTransportTSocket()");
			
			Class c2 = Class.forName("org.apache.thrift.transport.TSocket");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2,"open");
			String s = (String) m2.invoke(o2);
			
			String s1=""+i1;
			String s2=""+i2;	
			String s3=""+i3;			
			assertTrue(s.equals(s1) || s.equals(s2) ||s.equals(s3) ||getTaint(s) != 0);
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

	
	@Test
	public void testTSocket_TIOStreamTransport() {
		try {
			Class c = Class.forName("org.apache.thrift.transport.TSocket");
			Object o = c.newInstance();
			Method m = getDeclaredMethodByName(c,"open");
			m.invoke(o);
			
			Byte b1 = Tainter.taintedByte((byte) 4, 4);
			Byte b2 = Tainter.taintedByte((byte) 5, 5);

			Byte[] b = {b1, b2};
			Integer i1 = Tainter.taintedInt(4, 4);
			Integer i2 = Tainter.taintedInt(5, 5);
			Class c2 = Class.forName("org.apache.thrift.transport.TIOStreamTransport");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c,"write");
			m2.invoke(o2, b, i1, i2);
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
		}  catch (IllegalArgumentException e) {
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
