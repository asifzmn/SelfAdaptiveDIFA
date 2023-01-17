import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketAddress;

import org.junit.Test;

import edu.columbia.cs.psl.phosphor.runtime.Tainter;

public class NettyTest {
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
	
//	@Test
//	public void testAbstractNioChannel_doBeginRead_NioSocketChannel_doConnect() {
//		try {
//
//			Class c = Class.forName("io.netty.channel.nio.AbstractNioChannel");
//			Object o = c.newInstance();
//			Method m = getDeclaredMethodByName(c,"doBeginRead");
//			m.invoke(o);
//			//System.out.println("testTIOStreamTransportTSocket()");			
//			Class c2 = Class.forName("io.netty.channel.socket.nio.NioSocketChannel");
//			Object o2 = c2.newInstance();
//			Method m2 = getDeclaredMethodByName(c2,"doConnect");
//			Integer i = (Integer) m2.invoke(o2);
//			String s=""+i;			
//			assertTrue(getTaint(s) != 0);			
//		} catch (InstantiationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}		
//	
//	@Test
//	public void testNioEventLoop_processSelectedKey_AbstractNioChannel_doBeginRead() {
//		try {
//	        		
//			Class c = Class.forName("io.netty.channel.nio.NioEventLoop");
//			Object o = c.newInstance();
//			Method m = getDeclaredMethodByName(c,"processSelectedKey");
//			m.invoke(o);
//			//System.out.println("testTIOStreamTransportTSocket()");			
//			Class c2 = Class.forName("io.netty.channel.nio.AbstractNioChannel");
//			Object o2 = c2.newInstance();
//			Method m2 = getDeclaredMethodByName(c2,"doBeginRead");
//			Integer i = (Integer) m2.invoke(o2);
//			String s=""+i;			
//			assertTrue(getTaint(s) != 0);			
//		} catch (InstantiationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}		
//	
//	@Test
//	public void testNioEventLoop_processSelectedKey_NioSocketChannel_doConnect() {
//		try {
//	        		
//			Class c = Class.forName("io.netty.channel.nio.NioEventLoop");
//			Object o = c.newInstance();
//			Method m = getDeclaredMethodByName(c,"processSelectedKey");
//			m.invoke(o);
//			//System.out.println("testTIOStreamTransportTSocket()");			
//			Class c2 = Class.forName("io.netty.channel.socket.nio.NioSocketChannel");
//			Object o2 = c2.newInstance();
//			Method m2 = getDeclaredMethodByName(c2,"doConnect");
//			Integer i = (Integer) m2.invoke(o2);
//			String s=""+i;		
//			assertTrue(getTaint(s) != 0);			
//		} catch (InstantiationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}	

	@Test
	public void test9112() {
		try {
	        		//io.netty.channel.nio.AbstractNioChannel$NioUnsafe unsafe()> - $r1 = specialinvoke r0.<io.netty.channel.AbstractChannel: io.netty.channel.Channel$Unsafe unsafe()>()
			Class c = Class.forName("io.netty.channel.nio.AbstractNioChannel$NioUnsafe");
			Object o = c.newInstance();
			Method m = getDeclaredMethodByName(c,"unsafe");
			m.invoke(o);
			//System.out.println("testTIOStreamTransportTSocket()");		
			Class c2 = Class.forName("io.netty.channel.nio.NioEventLoop");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2,"run");
			Integer i = (Integer) m2.invoke(o2);
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
		}  catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	@Test
	public void test9229() {
		try {
	        		//io.netty.channel.nio.AbstractNioChannel$NioUnsafe unsafe()> - $r1 = specialinvoke r0.<io.netty.channel.AbstractChannel: io.netty.channel.Channel$Unsafe unsafe()>()
			Class c = Class.forName("io.netty.channel.nio.NioEventLoop");
			Object o = c.newInstance();
			Method m = getDeclaredMethodByName(c,"run");
			m.invoke(o);
			//System.out.println("testTIOStreamTransportTSocket()");		
			Class c2 = Class.forName("io.netty.channel.nio.AbstractNioChannel");
			Object o2 = c2.newInstance();
			Method m2 = getDeclaredMethodByName(c2,"doBeginRead");
			Integer i = (Integer) m2.invoke(o2);
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
		}  catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	

	@Test
	public void test9243() {
		try {
			Class c = Class.forName("io.netty.channel.nio.AbstractNioChannel");
			Object o = c.newInstance();
			Method m = getDeclaredMethodByName(c,"doBeginRead");
			m.invoke(o);
			//System.out.println("testTIOStreamTransportTSocket()");		
			Class c2 = Class.forName("io.netty.channel.socket.nio.NioSocketChannel");
			Object o2 = c2.newInstance();
			SocketAddress remoteAddress = null;
			SocketAddress localAddress= null;
			
			Method m2 = getDeclaredMethodByName(c2,"doConnect");
			Integer i = (Integer) m2.invoke(o2,remoteAddress, localAddress);
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
		}  catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

	@Test
	public void test9291() {
		try {
			Class c = Class.forName("io.netty.channel.nio.AbstractNioChannel");
			Object o = c.newInstance();
			Method m = getDeclaredMethodByName(c,"doBeginRead");
			m.invoke(o);
			//System.out.println("testTIOStreamTransportTSocket()");		
			Class c2 = Class.forName("io.netty.channel.nio.NioEventLoop");
			Object o2 = c2.newInstance();
			java.nio.channels.SelectionKey selectionKey = null;
			io.netty.channel.nio.AbstractNioChannel abstractNioChannel = null;
			
			Method m2 = getDeclaredMethodByName(c2,"processSelectedKey(java.nio.channels.SelectionKey,io.netty.channel.nio.AbstractNioChannel)");
			Integer i = (Integer) m2.invoke(o2,selectionKey, abstractNioChannel);
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
		}  catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	


	@Test
	public void test9362() {
		try {
			Class c = Class.forName("io.netty.channel.nio.AbstractNioChannel");
			Object o = c.newInstance();
			Method m = getDeclaredMethodByName(c,"doBeginRead");
			m.invoke(o);
			//System.out.println("testTIOStreamTransportTSocket()");		
			Class c2 = Class.forName("io.netty.channel.socket.nio.NioSocketChannel");
			Object o2 = c2.newInstance();
			SocketAddress remoteAddress = null;
			SocketAddress localAddress= null;
			
			Method m2 = getDeclaredMethodByName(c2,"doConnect");
			Integer i = (Integer) m2.invoke(o2,remoteAddress, localAddress);
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
