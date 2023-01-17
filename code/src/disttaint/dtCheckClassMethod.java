package disttaint;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.spark.SparkTransformer;
import soot.options.Options;

public class dtCheckClassMethod {
	public static String path = ""; //"C:/Research/nioecho/bin";  ///voldemort/rest/coordinator/admin
	static boolean debugOut = true;
	public static int classNumber = 0;
	//public static int errClassNumber = 0;
	public static int methodNumber = 0;
	public static int errMethodNumber = 0;
	public static void main(String args[]) {
		if(args.length == 0)
		  {
		      System.out.println("Usage: dtSourceSink directory");
		      System.exit(0);
		  }            
		  else
				System.out.println("[mainClass]"+args[0]);	
		  path = args[0]; 
		  
		initial(path);
		enableSpark(path);
		HashSet<String> errorClasses = new HashSet<String>(); 	
		for (SootClass sClass:Scene.v().getApplicationClasses()) 
		{
			int methodsInClass=0;
			try {
				System.out.println(" sClass:"+sClass);	
				if ( sClass.isPhantom() ) {	continue; }
				if ( !sClass.isConcrete() ) {	continue; }
				classNumber++;
				
				for(SootMethod m:sClass.getMethods())
				{
					try {
							System.out.println(" m:"+m);	
							methodNumber++;
							methodsInClass++;
						} catch (Exception ex) {
							errMethodNumber++;
							//ex.printStackTrace();
						}

				}	
			}  
			catch (Exception ex) {
				errorClasses.add(sClass.toString());
				//errClassNumber++;
				//ex.printStackTrace();
			}
			System.out.println(" methodsInClass:"+methodsInClass);
			if (methodsInClass<1)
				errorClasses.add(sClass.toString());
		}
		System.out.println("There are "+classNumber+" classes, "+methodNumber+" methods, "+errorClasses.size()+" wrong classes, "+errMethodNumber+" methods.");
		System.out.println("------------------------errorClasses: ");
		for (String cls:errorClasses) {
			System.out.println(cls);
		}
			
	}
	
	// soot option 1
	private static void initial(String classPath) {
		soot.G.reset();
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_process_dir(Collections.singletonList(classPath));//
		Options.v().set_whole_program(true);
		Scene.v().loadNecessaryClasses();
		
	}
	
	// soot option 2
    private static void enableSpark(String path){
        HashMap opt = new HashMap();
        //opt.put("verbose","true");
        //opt.put("propagator","worklist");
        opt.put("simple-edges-bidirectional","false");
        //opt.put("on-fly-cg","true");
        opt.put("apponly", "true");
//        opt.put("set-impl","double");
//        opt.put("double-set-old","hybrid");
//        opt.put("double-set-new","hybrid");
//        opt.put("allow-phantom-refs", "true");
        opt.put("-process-dir",path);
        
        SparkTransformer.v().transform("",opt);
    }
}
