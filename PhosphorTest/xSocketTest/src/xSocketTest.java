import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.NonBlockingConnection;

import edu.columbia.cs.psl.phosphor.runtime.Tainter;

public class xSocketTest {
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
	public void testIoConnector_handleConnect_XSocketClient2_main() {
		try {
			
			Class c = Class.forName("org.xsocket.connection.IoConnector");
			Object o = c.newInstance();
			Method m = getDeclaredMethodByName(c,"handleConnect");
			m.invoke(o,m);

			//System.out.println("testTIOStreamTransportTSocket()");
			
			Class c2 = Class.forName("org.xsocket.connection.IoSocketDispatcher");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2,"run");
			String s = (String) m2.invoke(o2,m2);
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
