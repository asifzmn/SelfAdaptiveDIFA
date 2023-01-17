package disttaint;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import java.util.Map;

import soot.Scene;
import soot.jimple.spark.SparkTransformer;
import soot.options.*;
import soot.Body;
import soot.BodyTransformer;
import soot.Pack;
import soot.PackManager;
import soot.PhaseOptions;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.options.Options;
import soot.*;
import soot.tools.CFGViewer;
import soot.util.cfgcmd.CFGOptionMatcher.CFGOption;

public class sootMain {
	   private static boolean SOOT_INITIALIZED=false;
	//  private final static String androidJAR="./lib/android.jar";
	    //private final static String appApk="./activity.apk";
	    public static void initialiseSoot(String mainclass)
	    {
	        if(SOOT_INITIALIZED)
	            return;

	        setClassPath();
	        enableSpark();
	        Options.v().set_allow_phantom_refs(true);
	        Options.v().set_prepend_classpath(true);
	        Options.v().set_validate(true);
	        Options.v().set_output_format(Options.output_format_jimple);
	        //Options.v().set_process_dir(Collections.singletonList(appApk));
	        //Options.v().set_force_android_jar(androidJAR);
	        Options.v().set_src_prec(Options.src_prec_java);
	    //  Options.v().set_soot_classpath(androidJAR);

	         Options.v().set_keep_line_number(true);
	          Options.v().set_whole_program(true);

	        //this option must be disabled for a sound call graph
	          Options.v().set_no_bodies_for_excluded(true);
	          Options.v().set_app(true);
	      //   Options.v().set_verbose(true);
	          //Options.v().set_interactive_mode(true);    
	          List<String> cfg=new ArrayList<String>();
	            cfg.add("D:\\eclipse-kepler-workshop\\soot-test-1\\src\\Test.java");
	          Options.v().set_dump_cfg(cfg);
	          SootClass appclass=Scene.v().loadClassAndSupport(mainclass);
	          Scene.v().setMainClass(appclass);
//	          Scene.v().addBasicClass("java.io.PrintStream",SootClass.SIGNATURES);
//	          Scene.v().addBasicClass("java.lang.System",SootClass.SIGNATURES);
//	          Scene.v().addBasicClass("java.lang.Thread",SootClass.SIGNATURES);
	          Scene.v().loadNecessaryClasses();

	        SOOT_INITIALIZED=true;
	    }
	     public static void setClassPath()
	      {
	          String bootpath = System.getProperty("sun.boot.class.path");
	          String javapath = System.getProperty("java.class.path");
	          String path ="C:/Soot/In";
	          System.out.println("bootpath"+bootpath);
	          System.out.println("javapath"+javapath);
	          Scene.v().setSootClassPath(path);
	          System.out.println("Scene.v()"+Scene.v());
	          System.out.println("path"+path);
	              }
	     public static void enableSpark()
	      {
	            //Enable Spark
	          HashMap<String,String> opt = new HashMap<String,String>();
	          //opt.put("verbose","true");
	          opt.put("propagator","worklist");
	          opt.put("simple-edges-bidirectional","false");
	          opt.put("on-fly-cg","true");
	          opt.put("set-impl","double");
	          opt.put("double-set-old","hybrid");
	          opt.put("double-set-new","hybrid");
	          opt.put("pre_jimplify", "true");
	          SparkTransformer.v().transform("",opt);
	          PhaseOptions.v().setPhaseOption("cg.spark", "enabled:true");
	      }
	    public static void main(String[] args)
	    {
	        args = new String[] {"Test0"};

	        if (args.length == 0) {
	            System.out.println("Usage: java RunVeryBusyAnalysis class_to_analyse");
	            System.exit(0);
	        }
	        else
	        {
	            System.out.println("no one");
	        }
	        String mainclass=args[0];
	        System.out.println("[mainClass]"+args[0]);
	        initialiseSoot(mainclass);
	        PackManager.v().getPack("jtp").add(new Transform("jtp.myAnalysis", new MyAnalysis()));
	        PackManager.v().runPacks();
//	        System.out.println(Scene.v().getCallGraph().size());
//	        PackManager.v().writeOutput();
	    }
 
}

class MyAnalysis extends BodyTransformer {
	  protected void internalTransform(final Body body,String phase, @SuppressWarnings("rawtypes")Map options){      
	      //for (SootClass c:Scene.v().getApplicationClasses()) 
		  SootClass c = body.getMethod().getDeclaringClass();
	      {
	          System.out.println("[sootClass]"+c);
//	        for(SootMethod m:c.getMethods())
//	        {
//	              System.out.println("[sootMethod]"+m);
//
//	            if(m.isConcrete())
//	            {
//	                Body b=m.retrieveActiveBody();
//	                  System.out.println("[body]"+b);
//
//	                Iterator<Unit> i=b.getUnits().snapshotIterator();
//	                while(i.hasNext())
//	                {
//	                    Unit u=i.next();
//	                }
//	            }
//	        }
	    }

	  }
	}