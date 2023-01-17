import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.junit.Test;

import edu.columbia.cs.psl.phosphor.runtime.Tainter;

public class NioEchoTest  {
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
	public void testNioClient_initiateConnection_RspHandler_waitForResponse() {
		try {
			//String imei = taintedString();
			Class c = Class.forName("NioClient");
			Object o = c.newInstance();			
			Method m = getDeclaredMethodByName(c,"initiateConnection");
			SocketChannel socketChannel =(SocketChannel) m.invoke(o);

			//System.out.println("testNioClientRspHandler()");
			
			Class c2 = Class.forName("RspHandler");
			//System.out.println("testSourceSink() class 3");
			Object o2 = c2.newInstance();
			//System.out.println("testSourceSink() class 4");
			Method m2 = c2.getMethod("waitForResponse()");
			//System.out.println("testSourceSink() class 5");
			String s = (String) m2.invoke(o2);
//			System.out.println(" imei = "+imei);
//			System.out.println(" s = "+s);
//			System.out.println(" getTaint(s)= "+getTaint(s));
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
	public void testNioClient_initiateConnection_EchoWorker_run() {
		try {
			Class c = Class.forName("NioClient");
			Object o = c.newInstance();			
			Method m = getDeclaredMethodByName(c,"initiateConnection");
			SocketChannel socketChannel =(SocketChannel) m.invoke(o);

			
			Class c2 = Class.forName("EchoWorker");
			Object o2 = c2.newInstance();
			Method m2 = c2.getMethod("run");
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
	public void testNioClient_initiateConnection_EchoWorker_processData() {
		try {
			Class c = Class.forName("NioClient");
			Object o = c.newInstance();			
			Method m = getDeclaredMethodByName(c,"initiateConnection");
			SocketChannel socketChannel =(SocketChannel) m.invoke(o);

			
			Class c2 = Class.forName("EchoWorker");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2,"processData");
			Byte b1 = Tainter.taintedByte((byte) 4, 4);
			Byte b2 = Tainter.taintedByte((byte) 5, 5);
			Byte[] b = {b1, b2};
			Integer i1 = Tainter.taintedInt(4, 4);
			String s = (String) m2.invoke(o2,socketChannel,b, i1 );
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
		} 
//		catch (NoSuchMethodException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
		catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testNioClient_initiateConnection_NioServer_main() {
		try {
			Class c = Class.forName("NioClient");
			Object o = c.newInstance();			
			Method m = getDeclaredMethodByName(c,"initiateConnection");
			SocketChannel socketChannel =(SocketChannel) m.invoke(o);
	
			Class c2 = Class.forName("NioServer");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2,"main");
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
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	@Test
	public void testNioClient_initiateConnection_NioServer_run() {
		try {
			Class c = Class.forName("NioClient");
			Object o = c.newInstance();			
			Method m = getDeclaredMethodByName(c,"initiateConnection");
			SocketChannel socketChannel =(SocketChannel) m.invoke(o);
	
			Class c2 = Class.forName("NioServer");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2,"run");
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
	public void testNioClient_initiateConnection_NioServer_send() {
		try {
			Class c = Class.forName("NioClient");
			Object o = c.newInstance();			
			Method m = getDeclaredMethodByName(c,"initiateConnection");
			SocketChannel socketChannel =(SocketChannel) m.invoke(o);
	
			Class c2 = Class.forName("NioServer");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2,"send");
			
			Byte b1 = Tainter.taintedByte((byte) 4, 4);
			Byte b2 = Tainter.taintedByte((byte) 5, 5);
			Byte[] b = {b1, b2};
			
			String s = (String) m2.invoke(o2, socketChannel, b);
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
	public void testNioServerNioClient_handleResponse() {
		try {
			String imei = taintedString();
			Class c = Class.forName("NioServer");
			Object o = c.newInstance();
			Method m = c.getMethod("setImei", String.class);
			m.invoke(o, imei);
			
			Class c2 = Class.forName("NioClient");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2,"handleResponse");
			
			Method m1 = getDeclaredMethodByName(c2,"initiateConnection");
			SocketChannel socketChannel =(SocketChannel) m1.invoke(o2);			
			Byte b1 = Tainter.taintedByte((byte) 4, 4);
			Byte b2 = Tainter.taintedByte((byte) 5, 5);
			Byte[] b = {b1, b2};
			int i = Tainter.taintedInt(5,5);
			String s = (String) m2.invoke(o2,socketChannel,b,i);
			assertTrue(s.equals(imei) || getTaint(s) != 0);
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
	public void testNioServerNioClient_main() {
		try {
			String imei = taintedString();
			Class c = Class.forName("NioServer");
			Object o = c.newInstance();
			Method m = c.getMethod("setImei", String.class);
			m.invoke(o, imei);
			
			Class c2 = Class.forName("NioClient");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2,"main");
			Object[] args = new Object[1];
			args[0]=new String [] { "1", "2"};					
			String s = (String) m2.invoke(null, args);
			assertTrue(s.equals(imei) || getTaint(s) != 0);
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
	public void testNioServerRspHandler_waitForResponse() {
		try {
			String imei = taintedString();
			Class c = Class.forName("NioServer");
			Object o = c.newInstance();
			Method m = c.getMethod("setImei", String.class);
			m.invoke(o, imei);
			
			Class c2 = Class.forName("RspHandler");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2,"waitForResponse");
			String s = (String) m2.invoke(o2);
			assertTrue(s.equals(imei) || getTaint(s) != 0);
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
	public void testNioServerEchoWorker_run() {
		try {
			String imei = taintedString();
			Class c = Class.forName("NioServer");
			Object o = c.newInstance();
			Method m = c.getMethod("setImei", String.class);
			m.invoke(o, imei);
			Class c2 = Class.forName("EchoWorker");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2,"run");
			String s = (String) m2.invoke(o2);
			assertTrue(s.equals(imei) || getTaint(s) != 0);
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
	public void testNioServerEchoWorker_processData() {
		try {
			String imei = taintedString();
			Class c = Class.forName("NioServer");
			Object o = c.newInstance();
			Method m = c.getMethod("setImei", String.class);
			m.invoke(o, imei);
			
			Class c1 = Class.forName("NioClient");
			Object o1 = c.newInstance();			
			Method m1 = getDeclaredMethodByName(c1,"initiateConnection");
			SocketChannel socketChannel =(SocketChannel) m1.invoke(o);
			
			Class c2 = Class.forName("EchoWorker");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2,"processData");
			Byte b1 = Tainter.taintedByte((byte) 4, 4);
			Byte b2 = Tainter.taintedByte((byte) 5, 5);
			Byte[] b = {b1, b2};
			Integer i1 = Tainter.taintedInt(4, 4);
			String s = (String) m2.invoke(o2,socketChannel,b, i1 );
			assertTrue(s.equals(imei) || getTaint(s) != 0);
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
	public void testNioServer_send_NioClient_initiateConnection() {
		try {
			Class c = Class.forName("NioClient");
			Object o = c.newInstance();			
			Method m = getDeclaredMethodByName(c,"initiateConnection");
			SocketChannel socketChannel =(SocketChannel) m.invoke(o);
	
			Class c2 = Class.forName("NioServer");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2,"send");			
			Byte b1 = Tainter.taintedByte((byte) 4, 4);
			Byte b2 = Tainter.taintedByte((byte) 5, 5);
			Byte[] b = {b1, b2};
			m2.invoke(o2, socketChannel, b);
			String s = ""+ socketChannel;
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
	public void testNioServer_send_NioClient_handleResponse() {
		try {
			Class c = Class.forName("NioClient");
			Object o = c.newInstance();			
			Method m = getDeclaredMethodByName(c,"initiateConnection");
			SocketChannel socketChannel =(SocketChannel) m.invoke(o);
	
			Class c2 = Class.forName("NioServer");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2,"send");
			Byte b1 = Tainter.taintedByte((byte) 4, 4);
			Byte b2 = Tainter.taintedByte((byte) 5, 5);
			Byte[] b = {b1, b2};
			m2.invoke(o2, socketChannel, b);			
			
			Method m3 = getDeclaredMethodByName(c2,"handleResponse");			
			int i = Tainter.taintedInt(5,5);
			String s = (String) m3.invoke(o2,socketChannel,b,i);
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
	public void testNioServer_send_NioClient_main() {
		try {
			Class c = Class.forName("NioClient");
			Object o = c.newInstance();			
			Method m = getDeclaredMethodByName(c,"initiateConnection");
			SocketChannel socketChannel =(SocketChannel) m.invoke(o);
	
			Class c2 = Class.forName("NioServer");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2,"send");
			Byte b1 = Tainter.taintedByte((byte) 4, 4);
			Byte b2 = Tainter.taintedByte((byte) 5, 5);
			Byte[] b = {b1, b2};
			m2.invoke(o2, socketChannel, b);			
			
			Method m3 = getDeclaredMethodByName(c2,"main");		
			Object[] args = new Object[1];
			args[0]=new String [] { "1", "2"};		
			
			String s = (String) m3.invoke(null, args);
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
	public void testNioServer_send_RspHandler_waitForResponse() {
		try {
			Class c = Class.forName("NioClient");
			Object o = c.newInstance();			
			Method m = getDeclaredMethodByName(c,"initiateConnection");
			SocketChannel socketChannel =(SocketChannel) m.invoke(o);
	
			Class c2 = Class.forName("NioServer");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2,"send");
			Byte b1 = Tainter.taintedByte((byte) 4, 4);
			Byte b2 = Tainter.taintedByte((byte) 5, 5);
			Byte[] b = {b1, b2};
			m2.invoke(o2, socketChannel, b);			
			
			Class c3 = Class.forName("RspHandler");
			Object o3 = c3.newInstance();
			Method m3 = getDeclaredMethodByName(c3, "waitForResponse");
			String s = (String) m3.invoke(o3);
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
	public void testNioServer_send_EchoWorker_run() {
		try {
			Class c = Class.forName("NioClient");
			Object o = c.newInstance();			
			Method m = getDeclaredMethodByName(c,"initiateConnection");
			SocketChannel socketChannel =(SocketChannel) m.invoke(o);
	
			Class c2 = Class.forName("NioServer");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2,"send");
			Byte b1 = Tainter.taintedByte((byte) 4, 4);
			Byte b2 = Tainter.taintedByte((byte) 5, 5);
			Byte[] b = {b1, b2};
			m2.invoke(o2, socketChannel, b);			
			
			Class c3 = Class.forName("EchoWorker");
			Object o3 = c3.newInstance();
			Method m3 = getDeclaredMethodByName(c3, "run");
			String s = (String) m3.invoke(o3);
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
	public void testNioServer_send_EchoWorker_processData() {
		try {
			Class c = Class.forName("NioClient");
			Object o = c.newInstance();			
			Method m = getDeclaredMethodByName(c,"initiateConnection");
			SocketChannel socketChannel =(SocketChannel) m.invoke(o);
	
			Class c2 = Class.forName("NioServer");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2,"send");
			Byte b1 = Tainter.taintedByte((byte) 4, 4);
			Byte b2 = Tainter.taintedByte((byte) 5, 5);
			Byte[] b = {b1, b2};
			m2.invoke(o2, socketChannel, b);			
			
			Class c3 = Class.forName("EchoWorker");
			Object o3 = c3.newInstance();
			Method m3 = getDeclaredMethodByName(c3, "processData");
			Integer i1 = Tainter.taintedInt(4, 4);
			String s = (String) m2.invoke(o3,o2,socketChannel,b, i1 );
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
	public void testNioServer_send_NioClient_run() {
		try {
			Class c = Class.forName("NioClient");
			Object o = c.newInstance();			
			Method m = getDeclaredMethodByName(c,"initiateConnection");
			SocketChannel socketChannel =(SocketChannel) m.invoke(o);
	
			Class c2 = Class.forName("NioServer");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2,"send");
			Byte b1 = Tainter.taintedByte((byte) 4, 4);
			Byte b2 = Tainter.taintedByte((byte) 5, 5);
			Byte[] b = {b1, b2};
			m2.invoke(o2, socketChannel, b);			
			
			Method m3 = getDeclaredMethodByName(c2,"open");
			String s = (String) m3.invoke(o2);
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
    public static Method getDeclaredMethodByName(Class c, String name)  {
   	 Method[] methods= c.getDeclaredMethods();
   	 Method method =null;
        for (Method method2 : methods) {
            if (method2.getName().equals(name));
        }
        return method;
   }
}
