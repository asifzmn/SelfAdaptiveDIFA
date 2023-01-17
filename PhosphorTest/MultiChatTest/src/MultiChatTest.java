import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import edu.columbia.cs.psl.phosphor.runtime.Tainter;

public class MultiChatTest {
	public static int getTaint(String description) {
		System.out.println(" description="+description+" description.toCharArray()[0]="+description.toCharArray()[0]+" Tainter.getTaint(description.toCharArray()[0])="+Tainter.getTaint(description.toCharArray()[0]));		
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

	public static String taintedString2() {
		char[] c = Tainter.taintedCharArray("abcdefghi2".toCharArray(), 5);
		String r = new String(c);
		return r;
	}

	public static String taintedString3() {
		char[] c = Tainter.taintedCharArray("abcdefghi2".toCharArray(), 5);
		String r = new String(c);
		return r;
	}
	@Test
	public void testSender_run_MainClient_run() {
		try {
			Class c = Class.forName("ChatClient.core.Sender");
			//c.setAccessible(true);			
			Object o = c.newInstance();
			Method m = c.getMethod("run");
			m.setAccessible(true);

			Class c2 = Class.forName("ChatClient.core.MainClient");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2, "run");
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
	public void testSender_run_ClientSender_sendMessageToClient() {
		try {
			Class c = Class.forName("ChatClient.core.Sender");
			//c.setAccessible(true);			
			Object o = c.newInstance();
			Method m = c.getMethod("run");
			m.setAccessible(true);

			Class c2 = Class.forName("ChatServer.handler.ClientSender");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2, "sendMessageToClient");
			String s = (String) m2.invoke(o2, String.class);
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
	public void testSender_run_DataBaseConnection_creareConexiune() {
		try {
			Class c = Class.forName("ChatClient.core.Sender");
			//c.setAccessible(true);			
			Object o = c.newInstance();
			Method m = c.getMethod("run");
			m.setAccessible(true);

			Class c2 = Class.forName("ChatServer.core.DataBaseConnection");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2, "creareConexiune");
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
	public void testSender_run_DataBaseConnection_inchidereConexiune() {
		try {
			Class c = Class.forName("ChatClient.core.Sender");
			//c.setAccessible(true);			
			Object o = c.newInstance();
			Method m = c.getMethod("run");
			m.setAccessible(true);

			Class c2 = Class.forName("ChatServer.core.DataBaseConnection");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2, "inchidereConexiune");
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
	public void testSender_run_MainServer_main() {
		try {
			Class c = Class.forName("ChatClient.core.Sender");
			//c.setAccessible(true);			
			Object o = c.newInstance();
			Method m = c.getMethod("run");
			m.setAccessible(true);

			Class c2 = Class.forName("ChatServer.core.MainServer");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2, "main");
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
	public void testMainClient_main_Sender_run() {
		try {
			String str1 = taintedString();
			String str2 = taintedString2();
			Class c = Class.forName("ChatClient.core.MainClient");
			Object o = c.newInstance();
			Method m = getDeclaredMethodByName(c, "main");
			Object[] args = new Object[1];
			args[0]=new String [] { str1, str2};					
			m.invoke(null, args);
			
			Class c2 = Class.forName("ChatClient.core.Sender");
			Object o2 = c2.newInstance();
			Method m2 = c.getMethod("run");
			String s = (String) m2.invoke(o);
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
	public void testMainClient_main_ClientSender_sendMessageToClient() {
		try {
			String str1 = taintedString();
			String str2 = taintedString2();
			Class c = Class.forName("ChatClient.core.MainClient");
			Object o = c.newInstance();
			Method m = getDeclaredMethodByName(c, "main");
			Object[] args = new Object[1];
			args[0]=new String [] { str1, str2};					
			m.invoke(null, args);

			String str3 = taintedString3();
			Class c2 = Class.forName("ChatServer.handler.ClientSender");
			Object o2 = c2.newInstance();
			Method m2 = c.getMethod("sendMessageToClient");
			String s = (String) m2.invoke(o, str3);
			assertTrue(s.equals(str1) || s.equals(str2) || s.equals(str3) || getTaint(s) != 0);			
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
	public void testMainClient_main_ClientSender_creareConexiune() {
		try {
			String str1 = taintedString();
			String str2 = taintedString2();
			Class c = Class.forName("ChatClient.core.MainClient");
			Object o = c.newInstance();
			Method m = getDeclaredMethodByName(c, "main");
			Object[] args = new Object[1];
			args[0]=new String [] { str1, str2};					
			m.invoke(null, args);
			

			Class c2 = Class.forName("ChatServer.core.DataBaseConnection");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2, "creareConexiune");
			String s = (String) m2.invoke(o2);
			assertTrue(s.equals(str1) || s.equals(str2) ||  getTaint(s) != 0);
			
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
	public void testMainClient_main_ClientSender_inchidereConexiune() {
		try {
			String str1 = taintedString();
			String str2 = taintedString2();
			Class c = Class.forName("ChatClient.core.MainClient");
			Object o = c.newInstance();
			Method m = getDeclaredMethodByName(c, "main");
			Object[] args = new Object[1];
			args[0]=new String [] { str1, str2};					
			m.invoke(null, args);
			

			Class c2 = Class.forName("ChatServer.core.DataBaseConnection");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2, "inchidereConexiune");
			String s = (String) m2.invoke(o2);
			assertTrue(s.equals(str1) || s.equals(str2) ||  getTaint(s) != 0);
			
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
	public void testMainClient_main_MainServer_main() {
		try {
			String str1 = taintedString();
			String str2 = taintedString2();
			Class c = Class.forName("ChatClient.core.MainClient");
			Object o = c.newInstance();
			Method m = getDeclaredMethodByName(c, "main");
			Object[] args = new Object[1];
			args[0]=new String [] { str1, str2};					
			m.invoke(null, args);
			

			Class c2 = Class.forName("ChatServer.core.MainServer");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2, "main");
			String s = (String) m2.invoke(null, args);
			assertTrue(s.equals(str1) || s.equals(str2) ||  getTaint(s) != 0 || getTaint(s) != 0);
			
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
//	@Test
//	public void testMainClientSender() {
//		try {
//			String imei = taintedString();
//			Class c = Class.forName("ChatClient.core.MainClient");
//			Object o = c.newInstance();
//			Method m = c.getMethod("setImei", String.class);
//			m.invoke(o, imei);
//			
//			Class c2 = Class.forName("ChatClient.core.handler.Sender");
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
	public void testMainClientClientSender() {
		try {
			String imei = taintedString();
			Class c = Class.forName("ChatClient.core.MainClient");
			Object o = c.newInstance();
			Method m = c.getMethod("setImei", String.class);
			m.invoke(o, imei);
			
			Class c2 = Class.forName("ChatServer.handler.ClientSender");
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
	public void testMainClientDataBaseConnection() {
		try {
			String imei = taintedString();
			Class c = Class.forName("ChatClient.core.MainClient");
			Object o = c.newInstance();
			Method m = c.getMethod("setImei", String.class);
			m.invoke(o, imei);
			
			Class c2 = Class.forName("ChatServer.core.DataBaseConnection");
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
	public void testMainClientMainServer() {
		try {
			String imei = taintedString();
			Class c = Class.forName("ChatClient.core.MainClient");
			Object o = c.newInstance();
			Method m = c.getMethod("setImei", String.class);
			m.invoke(o, imei);
			
			Class c2 = Class.forName("ChatServer.core.MainServer");
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
	public void testClientListenerMainClientShutDownHandler() {
		try {
			String imei = taintedString();
			Class c = Class.forName("ChatServer.handler.ClientListener");
			Object o = c.newInstance();
			Method m = c.getMethod("setImei", String.class);
			m.invoke(o, imei);
			
			Class c2 = Class.forName("ChatClient.core.MainClient");
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
//	public void testClientListenerSender() {
//		try {
//			String imei = taintedString();
//			Class c = Class.forName("ChatServer.handler.ClientListener");
//			Object o = c.newInstance();
//			Method m = c.getMethod("setImei", String.class);
//			m.invoke(o, imei);
//			
//			Class c2 = Class.forName("ChatClient.core.Sender");
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
	public void testClientListenerMainClient() {
		try {
			String imei = taintedString();
			Class c = Class.forName("ChatServer.handler.ClientListener");
			Object o = c.newInstance();
			Method m = c.getMethod("setImei", String.class);
			m.invoke(o, imei);
			
			Class c2 = Class.forName("ChatClient.core.MainClient");
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
	public void testClientListenerClientSender() {
		try {
			String imei = taintedString();
			Class c = Class.forName("ChatServer.handler.ClientListener");
			Object o = c.newInstance();
			Method m = c.getMethod("setImei", String.class);
			m.invoke(o, imei);
			
			Class c2 = Class.forName("ChatServer.handler.ClientSender");
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
	public void testClientListenerDataBaseConnection() {
		try {
			String imei = taintedString();
			Class c = Class.forName("ChatServer.handler.ClientListener");
			Object o = c.newInstance();
			Method m = c.getMethod("setImei", String.class);
			m.invoke(o, imei);
			
			Class c2 = Class.forName("ChatServer.core.DataBaseConnection");
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
	public void testClientListenerMainServer() {
		try {
			String imei = taintedString();
			Class c = Class.forName("ChatServer.handler.ClientListener");
			Object o = c.newInstance();
			Method m = c.getMethod("setImei", String.class);
			m.invoke(o, imei);
			
			Class c2 = Class.forName("ChatServer.core.MainServer");
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
	
    public static Method getDeclaredMethodByName(Class c, String name)  {
      	 Method[] methods= c.getDeclaredMethods();
      	 Method method =null;
           for (Method method2 : methods) {
               if (method2.getName().equals(name));
           }
           return method;
      }
}
