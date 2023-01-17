import java.lang.reflect.Member;
import java.lang.reflect.Method;

//import sun.reflect.Reflection;

public class Test1 {
	public static void main(String[] args)  {
		try {
			Class c = Class.forName("org.apache.thrift.transport.TIOStreamTransport");
			Object o = c.newInstance();
			//Method m = c.getDeclareMethod("read", new Class[] {args.getClass()});
	//        Class[] cArg = new Class[3];
	//        cArg[0] = byte[].class;
	//        cArg[1] = Integer.class;
	//        cArg[2] = Integer.class;
			Method m = getDeclaredMethodByName(c,"read");
			byte[] b = {1,2,3}; 
			int i =5;
			int j=6;
			int i2 = (int) m.invoke(o,b,i,j);
		} 
		catch (Exception e)  {
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
