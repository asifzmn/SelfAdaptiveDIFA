import java.lang.reflect.Method;

public class Test2 {
	public static void main(String[] args) throws Exception {
		Class c = Class.forName("org.apache.thrift.transport.TIOStreamTransport");
        Method[] methods1=  c.getMethods();
        Method[] methods2= c.getDeclaredMethods();
        System.out.println("\n------------method.toString--------------");
        for (Method method1 : methods1) {
            System.out.println(method1);
        }
        System.out.println("\n");
        for (Method method2 : methods2) {
            System.out.println(method2);
        }
        System.out.println("\n------------method.getName--------------");
        for (Method method1 : methods1) {
            System.out.println(method1.getName());
        }
        System.out.println("\n");
        for (Method method2 : methods2) {
            System.out.println(method2.getName());
        }
    }

}
