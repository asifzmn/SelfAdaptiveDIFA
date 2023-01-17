import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import edu.columbia.cs.psl.phosphor.runtime.Tainter;

public class OpenChordTest {
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
	public void testConsoleThreadInvocationThread() {
		try {
			String imei = taintedString();
			Class c = Class.forName("de.uniba.wiai.lspi.util.console.ConsoleThread");
			Object o = c.newInstance();
			Method m = c.getMethod("setImei", String.class);
			m.invoke(o, imei);

			Class c2 = Class.forName("de.uniba.wiai.lspi.chord.com.socket.InvocationThread");
			Object o2 = c2.newInstance();
			Method m2 = c2.getMethod("getImei");
			String s = (String) m2.invoke(o2);
			assertTrue(s.equals(imei));  // || getTaint(s) != 0);
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
//	@Test
//	public void testConsoleThreadRequestHandler() {
//		try {
//			String imei = taintedString();
//			Class c = Class.forName("de.uniba.wiai.lspi.util.console.ConsoleThread");
//			Object o = c.newInstance();
//			Method m = c.getMethod("setImei", String.class);
//			m.invoke(o, imei);
//
//			Class c2 = Class.forName("de.uniba.wiai.lspi.chord.com.socket.RequestHandler");
//			Object o2 = c2.newInstance();
//			Method m2 = c2.getMethod("getImei");
//			String s = (String) m2.invoke(o2);
//			assertTrue(s.equals(imei));  // || getTaint(s) != 0);
//		} catch (InstantiationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	@Test
	public void testConsoleThreadSocketProxy() {
		try {
			String imei = taintedString();
			Class c = Class.forName("de.uniba.wiai.lspi.util.console.ConsoleThread");
			Object o = c.newInstance();
			Method m = c.getMethod("setImei", String.class);
			m.invoke(o, imei);

			Class c2 = Class.forName("de.uniba.wiai.lspi.chord.com.socket.SocketProxy");
			Object o2 = c2.newInstance();
			Method m2 = c2.getMethod("getImei");
			String s = (String) m2.invoke(o2);
			assertTrue(s.equals(imei));  // || getTaint(s) != 0);
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
	public void testConsoleThreadJoinNetwork() {
		try {
			String imei = taintedString();
			Class c = Class.forName("de.uniba.wiai.lspi.util.console.ConsoleThread");
			Object o = c.newInstance();
			Method m = c.getMethod("setImei", String.class);
			m.invoke(o, imei);

			Class c2 = Class.forName("de.uniba.wiai.lspi.chord.console.command.JoinNetwork");
			Object o2 = c2.newInstance();
			Method m2 = c2.getMethod("getImei");
			String s = (String) m2.invoke(o2);
			assertTrue(s.equals(imei));  // || getTaint(s) != 0);
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
	public void testConsoleThreadRemoveNetwork() {
		try {
			String imei = taintedString();
			Class c = Class.forName("de.uniba.wiai.lspi.util.console.ConsoleThread");
			Object o = c.newInstance();
			Method m = c.getMethod("setImei", String.class);
			m.invoke(o, imei);
			Class c2 = Class.forName("de.uniba.wiai.lspi.chord.console.command.RemoveNetwork");
			Object o2 = c2.newInstance();
			Method m2 = c2.getMethod("getImei");
			String s = (String) m2.invoke(o2);
			assertTrue(s.equals(imei));  // || getTaint(s) != 0);
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
	public void testConsoleThreadRetrieveNetwork() {
		try {
			String imei = taintedString();
			Class c = Class.forName("de.uniba.wiai.lspi.util.console.ConsoleThread");
			Object o = c.newInstance();
			Method m = c.getMethod("setImei", String.class);
			m.invoke(o, imei);

			Class c2 = Class.forName("de.uniba.wiai.lspi.chord.console.command.RetrieveNetwork");
			Object o2 = c2.newInstance();
			Method m2 = c2.getMethod("getImei");
			String s = (String) m2.invoke(o2);
			assertTrue(s.equals(imei));  // || getTaint(s) != 0);
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
	public void testConsoleThreadShowEntriesNetwork() {
		try {
			String imei = taintedString();
			Class c = Class.forName("de.uniba.wiai.lspi.util.console.ConsoleThread");
			Object o = c.newInstance();
			Method m = c.getMethod("setImei", String.class);
			m.invoke(o, imei);

			Class c2 = Class.forName("de.uniba.wiai.lspi.chord.console.command.ShowEntriesNetwork");
			Object o2 = c2.newInstance();
			Method m2 = c2.getMethod("getImei");
			String s = (String) m2.invoke(o2);
			assertTrue(s.equals(imei));  // || getTaint(s) != 0);
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
}
