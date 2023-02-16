package IFM;

import  dua.Forensics;
import fault.StmtMapper;
import soot.*;
import soot.util.dot.DotGraph;
import EAS.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import profile.InstrumManager;
import dua.global.ProgramFlowGraph;
import dua.global.ReachabilityAnalysis;
import dua.method.CFG;
import dua.method.CFGDefUses;
import dua.method.MethodTag;
import dua.method.CFG.CFGNode;
import dua.method.CFGDefUses.Variable;
import dua.util.Pair;
import dua.util.Util;
import soot.jimple.*;
import EAS.EAInst;
import MciaUtil.utils;
import MciaUtil.VTEdge.VTEType;
import edu.ksu.cis.indus.staticanalyses.dependency.DependencyXMLizer;
import edu.ksu.cis.indus.staticanalyses.dependency.IDependencyAnalysis;
import edu.ksu.cis.indus.staticanalyses.dependency.InterferenceDAv1;
import edu.ksu.cis.indus.staticanalyses.dependency.InterferenceDAv2;
import edu.ksu.cis.indus.staticanalyses.dependency.InterferenceDAv3;
import edu.ksu.cis.indus.staticanalyses.dependency.ReadyDAv1;
import edu.ksu.cis.indus.staticanalyses.dependency.ReadyDAv2;
import edu.ksu.cis.indus.staticanalyses.dependency.ReadyDAv3;
import edu.ksu.cis.indus.staticanalyses.dependency.SynchronizationDA;
import edu.ksu.cis.indus.staticanalyses.interfaces.IValueAnalyzer;
import edu.ksu.cis.indus.staticanalyses.tokens.ITokens;
import edu.ksu.cis.indus.staticanalyses.dependency.DependencyXMLizerCLI;
import IFM.Variant.*;

//import org.jibx.runtime.*;

public class ComputeRelevantMethods extends EAInst {
    protected static dtOptions opts = new dtOptions();

    //	public static ArrayList coveredMethods = new ArrayList();
    //	public static ArrayList branchStmts = new ArrayList();
    HashSet<String> sourceMethods = new HashSet<String>();
    HashSet<String> sinkMethods = new HashSet<String>();
    Set<SootMethod> myMethods= new HashSet<SootMethod>();

    Set<SootMethod> soMS=new LinkedHashSet<SootMethod>();
    Set<SootMethod> siMS=new LinkedHashSet<SootMethod>();
    protected SootClass clsMonitor;
    public static void main(String args[]){
        args = preProcessArgs(opts, args);

        ComputeRelevantMethods oInst = new ComputeRelevantMethods();
        // examine catch blocks
        dua.Options.ignoreCatchBlocks = false;

        Forensics.registerExtension(oInst);
        Forensics.main(args);
    }

    @Override protected void init() {

    }

    @Override public void run() {
        final long startTime = System.currentTimeMillis();
        Stmt[] idToS= StmtMapper.getCreateInverseMap();
        if (opts.debugOut())
        {//
            //System.out.println("idToS="+idToS);
            //for (int i=0; i<idToS.length; i++)
            //System.out.println("idToS["+i+"]="+idToS[i]);
            System.out.println("Running FLOWDIST extension of DUA-Forensics");
        }

        if (Variant.isICFG())
        {
            System.exit(0);
        }

        sourceMethods=dtUtil.getListSet(System.getProperty("user.dir") + File.separator + "coveredSourceMethods.txt");
        sinkMethods=dtUtil.getListSet(System.getProperty("user.dir") + File.separator + "coveredSinkMethods.txt");
        //myMethods.addAll(sourceMethods);
        //myMethods.addAll(sinkMethods);
        if (sourceMethods.size()+sinkMethods.size()<1)
            System.exit(0);

        //int ret = createVTGWithIndus();
        Set<SootMethod> reachableMethods = new LinkedHashSet<SootMethod>();
        reachableMethods.addAll(ProgramFlowGraph.inst().getReachableAppMethods());

        System.out.println("sourceMethods.size()="+sourceMethods.size()+" sinkMethods.size()="+sinkMethods.size()+" myMethods.size()="+myMethods.size());
        //System.exit(0);
        List<SootMethod> entryMethods = ProgramFlowGraph.inst().getEntryMethods();
        ReachabilityAnalysis.computeReachability(entryMethods);

        for (SootMethod sMethod : reachableMethods) {
            String mSign = sMethod.getSignature();
            if (sourceMethods.contains(mSign))
                soMS.add(sMethod);
            if (sinkMethods.contains(mSign))
                siMS.add(sMethod);
        }
        System.out.println("sourceMethods.size()="+sourceMethods.size()+" sinkMethods.size()="+sinkMethods.size()+" myMethods.size()="+myMethods.size());
        System.out.println("reachableMethods.size()="+reachableMethods.size()+" soMS.size()="+soMS.size()+" siMS.size()="+siMS.size());
        getMethodsFromCFG(reachableMethods);
        System.out.println("getMethodsFromCFG soMS.size()="+soMS.size()+" siMS.size()="+siMS.size());
        final long afterCFGTime = System.currentTimeMillis();
        System.out.println("Detecting covered methods in ICFG took " + (afterCFGTime - startTime) + " ms");
//		boolean sizeIncremented=true;
//		while (sizeIncremented)
        {
//			int methodCount=soMS.size()+siMS.size();
            SynchronizationInterfereReadyAnalyses(reachableMethods);
//			System.out.println(" myMethods.size()="+myMethods.size()+" myMethods2.size()="+myMethods2.size());
            System.out.println("SynchronizationInterfereReadyAnalyses soMS.size()="+soMS.size()+" siMS.size()="+siMS.size());
//			if (methodCount==(soMS.size()+siMS.size()))
//				sizeIncremented=false;
        }	// while

        //System.out.println("SynchronizationInterfereReadyAnalyses soMS.size()="+soMS.size()+" siMS.size()="+siMS.size());

        //System.out.println("myMethods.size()="+myMethods.size());
        System.out.println("Detecting inter-thread methods took " + (System.currentTimeMillis() - afterCFGTime) + " ms");

        HashSet<String> myMethodStrs = new HashSet<String>();

//        if (ICFG)
//        {
//            myMethods.addAll(entryMethods);
//            myMethods.addAll(soMS);
//            myMethods.addAll(siMS);
//
//            for (SootMethod sMethod : reachableMethods) {
//                if (myMethods.contains(sMethod))
//                    myMethodStrs.add(sMethod.getSignature());
//            }
//        }
//
//        else
//        {
//            myMethods.addAll(reachableMethods);
//            for (SootMethod sMethod : reachableMethods) {
//                if (myMethods.contains(sMethod))
//                    myMethodStrs.add(sMethod.getSignature());
//            }
//        }

        myMethods.addAll(reachableMethods);
        for (SootMethod sMethod : reachableMethods) {
            if (myMethods.contains(sMethod))
                myMethodStrs.add(sMethod.getSignature());
        }

        System.out.println("myMethods.size()="+myMethods.size()+" myMethodStrs.size()="+myMethodStrs.size());
        dtUtil.writeSet(myMethodStrs, System.getProperty("user.dir") + File.separator + "coveredMethods.txt");
        System.out.println("Generating covered methods took " + (System.currentTimeMillis() - startTime) + " ms");
        System.exit(0);

    }

    private void  getMethodsFromCFG(Set<SootMethod> reachableMethods) {



        // having already found call sites, compute reachability
        //List<SootMethod> entryMethods = ProgramFlowGraph.inst().getEntryMethods();

        //ReachabilityAnalysis.computeReachability(ProgramFlowGraph.inst().getReachableAppMethods());



        //methodSet.addAll(soMS);
        //methodSet.addAll(siMS);
        //System.out.println("reachableMethods.size()="+reachableMethods.size()+" soMS.size()="+soMS.size()+" siMS.size()="+siMS.size()+" methodSet.size()="+methodSet.size());

        // traverse source and next methods
        boolean sourceIncremented=true;
        while (sourceIncremented)
        {
            Set<SootMethod> soMS2=new LinkedHashSet<SootMethod>();
            soMS2.addAll(soMS);
            for (SootMethod sMethod: soMS2) {
                for (SootMethod reachm : reachableMethods) {
                    //System.out.println("sourceMethods.contains(sMethod) reachm="+reachm+" forwardReaches(sMethod, reachm)="+ReachabilityAnalysis.forwardReaches(sMethod, reachm));
                    if (ReachabilityAnalysis.forwardReaches(sMethod, reachm))
                    {
                        soMS.add(reachm);
                    }

                }
            }
            //System.out.println(" soMS.size()="+soMS.size()+" soMS2.size()="+soMS2.size());
            if (soMS2.size()>=soMS.size())
                sourceIncremented=false;
        }	// while


        // traverse sink and previous methods
        boolean sinkIncremented=true;
        while (sinkIncremented)
        {
            Set<SootMethod> siMS2=new LinkedHashSet<SootMethod>();
            siMS2.addAll(siMS);
            for (SootMethod sMethod: siMS2) {
                for (SootMethod reachm : reachableMethods) {
                    //System.out.println("sourceMethods.contains(sMethod) reachm="+reachm+" forwardReaches(sMethod, reachm)="+ReachabilityAnalysis.forwardReaches(sMethod, reachm));
                    if (ReachabilityAnalysis.forwardReaches(reachm, sMethod))
                    {
                        siMS.add(reachm);
                    }
                }
            }
            //System.out.println(" siMS.size()="+siMS.size()+" siMS2.size()="+siMS2.size());
            if (siMS2.size()>=siMS.size())
                sinkIncremented=false;
        }	// while

//		methodSet.addAll(soMS);
//		methodSet.addAll(siMS);
        //System.out.println("reachableMethods.size()="+reachableMethods.size()+" soMS.size()="+soMS.size()+" siMS.size()="+siMS.size());
        //+" methodSet.size()="+methodSet.size());
        //return methodSet;
    }

    public void SynchronizationInterfereReadyAnalyses(Set<SootMethod> reachableMethods) {
//		Set<SootMethod> methodSet=new LinkedHashSet<SootMethod>();
//		methodSet.addAll(coveredMethods);

        Map<SootMethod, Collection<InvokeStmt>> waits = new HashMap<SootMethod, Collection<InvokeStmt>>();
        Map<SootMethod, Collection<InvokeStmt>> notifies = new HashMap<SootMethod, Collection<InvokeStmt>>();
        //Map<SootMethod, Collection<Stmt>> _method2dependeeMap = collectDependeesInMethods();
        SootClass THREAD_CLASS = Scene.v().getSootClass("java.lang.Thread");
        FastHierarchy har = new FastHierarchy();

        Set<SootMethod> AnalyzedMethods = new LinkedHashSet<SootMethod>();
        AnalyzedMethods.addAll(siMS);
        AnalyzedMethods.addAll(soMS);
        for (SootClass sClass:Scene.v().getApplicationClasses())
        {
            //System.out.println("Indus sClass="+sClass+" har.isSubclass(sClass, THREAD_CLASS)="+har.isSubclass(sClass, THREAD_CLASS)+" sClass.implementsInterfacejava.lang.Runnable="+sClass.implementsInterface("java.lang.Runnable"));
            if (!har.isSubclass(sClass, THREAD_CLASS) && !sClass.implementsInterface("java.lang.Runnable"))
                continue;
            if ( sClass.isPhantom() ) {
                // skip phantom classes
                continue;
            }

            for(SootMethod m:sClass.getMethods())
            {
//				Collection<Stmt> stmtInMethod= new HashSet<Stmt>();
//				Collection<InvokeStmt> waitStmtInMethod= new HashSet<InvokeStmt>();
//				Collection<InvokeStmt> notifyStmtInMethod= new HashSet<InvokeStmt>();
                if ( !m.isConcrete() ) {
                    // skip abstract methods and phantom methods, and native methods as well
                    continue;
                }
                if ( m.toString().indexOf(": java.lang.Class class$") != -1 ) {
                    // don't handle reflections now either
                    continue;
                }
                if ( !m.hasActiveBody() ) {
                    continue;
                }
                //SootClass sClass=m.getDeclaringClass();
                //System.out.println("	m="+m+"	sClass="+sClass);

//				if (!reachableMethods.contains(m)) {
//					// skip unreachable methods
//					continue;
//				}

                //m.getClass()

                // Set ReadyAnalysis
                Collection<Stmt> stmtInMethod= new HashSet<Stmt>();
                Collection<InvokeStmt> waitStmtInMethod= new HashSet<InvokeStmt>();
                Collection<InvokeStmt> notifyStmtInMethod= new HashSet<InvokeStmt>();
                boolean hasEnter=false;
                boolean hasExit=false;
                EnterMonitorStmt myEnter=null;
                ExitMonitorStmt myExit=null;
                Collection<Stmt> myStmtInEnterExit= new HashSet<Stmt>();

                // SynchronizationAnalysis
                try
                {
                    Iterator<Unit> units=m.retrieveActiveBody().getUnits().iterator();
                    Stmt lastSt=null;
                    while(units.hasNext())
                    {
                        try
                        {
                            Stmt st = (Stmt) units.next();
                            //System.out.println("Indus m="+m+" st="+st);
                            if (st instanceof EnterMonitorStmt)
                            {
                                hasEnter=true;
                                //myEnter= (EnterMonitorStmt) st;
                                //System.out.println("st instanceof EnterMonitorStmt");
                            }

                            if (hasEnter && !(st instanceof EnterMonitorStmt))
                            {
//				            	System.out.println("Synchronization edge: "+m+" - "+lastSt+" --> "+m+" - "+st);
//								Variable _deV=makeVariable(soot.jimple.StringConstant.v("SYNC-EnterExitMonitor"),lastSt);
//								Variable _dtV=makeVariable(soot.jimple.StringConstant.v("SYNC-EnterExitMonitor"),st);
//								createTransferEdge(_deV,m, lastSt, _dtV, m, st, VTEType.VTE_CONTROL_INTRA, false);
                                if (soMS.contains(m))
                                    siMS.add(m);
                                if (siMS.contains(m))
                                    soMS.add(m);
                            }
                            if (st instanceof ExitMonitorStmt)
                            {
                                hasEnter=false;
                                //break;
                                //System.out.println("st instanceof ExitMonitorStmt");
                            }
                            lastSt=st;

                            // InterferenceAnalysis
                            if(st instanceof AssignStmt)
                            {
                                //System.out.println("AssignStmt st="+st);
                                for (SootClass sClass2:Scene.v().getApplicationClasses())
                                {
                                    //System.out.println("**************************sClass2: "+sClass2);
                                    if ((!har.isSubclass(sClass2, THREAD_CLASS) && !sClass2.implementsInterface("java.lang.Runnable")) || sClass2.equals(sClass))
                                        continue;
//									if (sClass2.equals(sClass))
//										continue;
                                    if ( sClass2.isPhantom() ) {
                                        // skip phantom classes
                                        continue;
                                    }
                                    //System.out.println("	**************************Thread sClass2: "+sClass2);
                                    for(SootMethod m2:sClass2.getMethods())
                                    {
                                        try
                                        {
                                            if ( !m2.isConcrete() ) {
                                                // skip abstract methods and phantom methods, and native methods as well
                                                continue;
                                            }
                                            if ( m2.toString().indexOf(": java.lang.Class class$") != -1 ) {
                                                // don't handle reflections now either
                                                continue;
                                            }
                                            if ( !m2.hasActiveBody() ) {
                                                continue;
                                            }
//											if (!reachableMethods.contains(m)) {
//												// skip unreachable methods
//												continue;
//											}
                                            //SootClass sClass2=m2.getDeclaringClass();
                                            //System.out.println("	m2="+m2+"	sClass2="+sClass2);


                                            Iterator<Unit> units2=m2.retrieveActiveBody().getUnits().iterator();
                                            while(units2.hasNext())
                                            {
                                                try
                                                {
                                                    Stmt st2 =(Stmt) units2.next();
                                                    if(st2 instanceof AssignStmt)
                                                    {
                                                        //System.out.println("	AssignStmt st2="+st2);
//														Pair<AssignStmt, SootMethod> _de= new Pair<AssignStmt, SootMethod>((AssignStmt)st, m);
//														Pair<AssignStmt, SootMethod> _dt= new Pair<AssignStmt, SootMethod>((AssignStmt)st2, m2);
//														Value _dependee = ((DefinitionStmt) _de).getLeftOp();
//														Value _dependent = ((DefinitionStmt) _dt).getRightOp();
//														Variable _vde=makeVariable(_dependee,st2);
//														Variable _vdt=makeVariable(_dependent,st);
//														System.out.println("Interference edge: "+m+" - "+st+" --> "+m2+" - "+st2);
//														createTransferEdge(_vde, m, st, _vdt, m2, st2, VTEType.VTE_ARRAYELE, false);
//														createTransferEdge(_vde, m, st, _vdt, m2, st2, VTEType.VTE_INSVAR, false);
//														createTransferEdge(_vde, m, st, _vdt, m2, st2, VTEType.VTE_STVAR, false);
                                                        //methodSet.add(m2);
                                                        if (soMS.contains(m))
                                                            soMS.add(m2);
                                                        if (siMS.contains(m2))
                                                            siMS.add(m);
                                                    }

                                                }
                                                catch (Exception _e)
                                                {
                                                    //System.out.println("Exception: "+_e);
                                                }
                                            }   // while
                                        }
                                        catch (Exception _e)
                                        {
                                            //System.out.println("Exception: "+_e);
                                        }
                                    }  //for
                                }	//for
                            }    //if

                            //ReadyAnalysis processRule1And3
                            stmtInMethod.add(st);
                            if (st instanceof InvokeStmt)
                            {
                                InvokeStmt ist=(InvokeStmt) st;
                                String istStr=ist.toString();
                                if (istStr.indexOf(" void wait()")>1)
                                {
                                    waitStmtInMethod.add(ist);
                                }
                                else if (istStr.indexOf(" void notify()")>1 || istStr.indexOf(" void notifyAll()")>1)
                                {
                                    notifyStmtInMethod.add(ist);
                                }

                                //ReadyAnalysisDv1 processRule4
                                //if (!waits.isEmpty() && !notifies.isEmpty())
                                {
                                    //System.out.println("InvokeStmt st="+st);
                                    for (SootClass sClass2:Scene.v().getApplicationClasses())
                                    {
                                        if ((!har.isSubclass(sClass2, THREAD_CLASS) && !sClass2.implementsInterface("java.lang.Runnable")) || sClass2.equals(sClass))
                                            continue;
                                        if ( sClass.isPhantom() ) {
                                            // skip phantom classes
                                            continue;
                                        }
                                        //System.out.println("	**************************Thread sClass2: "+sClass2);
                                        for(SootMethod m2:sClass2.getMethods())
                                        {
                                            try
                                            {
                                                if ( !m2.isConcrete() ) {
                                                    // skip abstract methods and phantom methods, and native methods as well
                                                    continue;
                                                }
                                                if ( m2.toString().indexOf(": java.lang.Class class$") != -1 ) {
                                                    // don't handle reflections now either
                                                    continue;
                                                }
                                                if ( !m2.hasActiveBody() ) {
                                                    continue;
                                                }
//											SootClass sClass2=m2.getDeclaringClass();
//											//System.out.println("	m2="+m2+"	sClass2="+sClass2);
//											if ((!har.isSubclass(sClass2, THREAD_CLASS) && !sClass2.implementsInterface("java.lang.Runnable")) || sClass2.equals(sClass))
//												continue;
////											if (sClass2.equals(sClass))
////												continue;
//											if ( sClass2.isPhantom() ) {
//												// skip phantom classes
//												continue;
//											}
                                                Iterator<Unit> units2=m2.retrieveActiveBody().getUnits().iterator();
                                                while(units2.hasNext())
                                                {
                                                    try
                                                    {
                                                        Stmt st2 =(Stmt) units2.next();
                                                        //System.out.println("	st2="+st2);
                                                        //									if (st2.equals(st))
                                                        //										continue;

                                                        //ReadyAnalysisDv1 processRule4
                                                        if(st2 instanceof InvokeStmt)   //|| st2.containsInvokeExpr()
                                                        {
                                                            //System.out.println("	InvokeStmt  st2="+st2);
                                                            //System.out.println("	_de  _dt");
                                                            //System.out.println(" _de="+_de+" _dt="+_dt);
//													Variable _deV=makeVariable(soot.jimple.StringConstant.v("wait-notify"), st);
//													Variable _dtV=makeVariable(soot.jimple.StringConstant.v("wait-notify"), st2);
//													//System.out.println("vtg before ReadyDAv1() WaitNotify Dependence");
//													//System.out.println(vtg);
//													System.out.println("Ready Rule4 edge: "+m+" - "+st+" --> "+m2+" - "+st2);
//													createTransferEdge(_deV, m, st, _dtV, m2, st2, VTEType.VTE_CONTROL_INTER, false);
                                                            //methodSet.add(m2);
                                                            if (soMS.contains(m))
                                                                soMS.add(m2);
                                                            if (siMS.contains(m2))
                                                                siMS.add(m);
                                                        }
                                                    }
                                                    catch (Exception _e)
                                                    {
                                                        //System.out.println("Exception: "+_e);
                                                    }
                                                }   // while

                                            }
                                            catch (Exception _e)
                                            {
                                                //System.out.println("Exception: "+_e);
                                            }
                                        }  //for
                                    }	//for
                                }
                            }
                            else if (st instanceof EnterMonitorStmt)
                            {
                                hasEnter=true;
                                myEnter= (EnterMonitorStmt) st;
                                //ReadyAnalysisDv1 processRule2
                                //System.out.println("EnterMonitorStmt st="+st);
                                for (SootClass sClass2:Scene.v().getApplicationClasses())
                                {
                                    if ((!har.isSubclass(sClass2, THREAD_CLASS) && !sClass2.implementsInterface("java.lang.Runnable")) || sClass2.equals(sClass))
                                        continue;
                                    if ( sClass2.isPhantom() ) {
                                        // skip phantom classes
                                        continue;
                                    }
                                    //System.out.println("	**************************Thread sClass2: "+sClass2);
                                    for(SootMethod m2:sClass2.getMethods())
                                    {
                                        try
                                        {
                                            if ( !m2.isConcrete() ) {
                                                // skip abstract methods and phantom methods, and native methods as well
                                                continue;
                                            }
                                            if ( m2.toString().indexOf(": java.lang.Class class$") != -1 ) {
                                                // don't handle reflections now either
                                                continue;
                                            }
                                            if ( !m2.hasActiveBody() ) {
                                                continue;
                                            }
                                            if (m2.equals(m))
                                                continue;
//										SootClass sClass2=m2.getDeclaringClass();
//										//System.out.println("	m2="+m2+"	sClass2="+sClass2);
//										if ((!har.isSubclass(sClass2, THREAD_CLASS) && !sClass2.implementsInterface("java.lang.Runnable")) || sClass2.equals(sClass))
//											continue;
////										if (sClass2.equals(sClass))
////											continue;
//										if ( sClass2.isPhantom() ) {
//											// skip phantom classes
//											continue;
//										}
                                            Iterator<Unit> units2=m2.retrieveActiveBody().getUnits().iterator();
                                            while(units2.hasNext())
                                            {
                                                try
                                                {
                                                    Stmt st2 =(Stmt) units2.next();
                                                    //System.out.println("	st2="+st2);
                                                    //									if (st2.equals(st))
                                                    //										continue;
                                                    //ReadyAnalysisDv1 processRule2
                                                    if(st2 instanceof ExitMonitorStmt )
                                                    {
//												System.out.println("Ready Rule2 edge: "+m+" - "+st+" --> "+m2+" - "+st2);
//												Variable _deV=makeVariable(soot.jimple.StringConstant.v("Enter-Exit-Monitor"),st);
//												Variable _dtV=makeVariable(soot.jimple.StringConstant.v("Enter-Exit-Monitor"),st2);
//												createTransferEdge(_deV, m, st, _dtV, m2, st2, VTEType.VTE_CONTROL_INTER, false);
                                                        if (soMS.contains(m))
                                                            soMS.add(m2);
                                                        if (siMS.contains(m2))
                                                            siMS.add(m);
                                                    }

                                                }
                                                catch (Exception _e)
                                                {
                                                    //System.out.println("Exception: "+_e);
                                                }
                                            }   // while
                                        }
                                        catch (Exception _e)
                                        {
                                            //System.out.println("Exception: "+_e);
                                        }
                                    }  //for
                                }	//for

                            }
                            else if (st instanceof ExitMonitorStmt)
                            {
                                hasExit=true;
                                myExit= (ExitMonitorStmt) st;
                            }
                            else if (hasEnter && !hasExit)
                            {
                                myStmtInEnterExit.add(st);
                            }
                        }
                        catch (Exception _e)
                        {
                            //System.out.println("Exception: "+_e);
                        }
                    }
                    waits.put(m, waitStmtInMethod);
                    notifies.put(m, notifyStmtInMethod);
                }
                catch (Exception _e)
                {
                    //System.out.println("Exception: "+_e);
                }


            }
        }
        //return methodSet;
    }
//	public Set<SootMethod> SynchronizationInterfereReadyAnalyses(boolean debugOut) throws Exception {
//		/** the reachable methods retrieved by ProgramFlowGraph are actual those only when "-reachablility" option is enable
//		 * However, when that option is set, this facility will ignore all call sites in catch blocks when collecting CFGs;
//		 * to bypass this conflict of interest, use the reachable methods retrieved separately using Soot only, as is implemented in
//		 * MciaUtil.utils
//		 */
//		Map<SootMethod, Collection<InvokeStmt>> waits = new HashMap<SootMethod, Collection<InvokeStmt>>();
//		Map<SootMethod, Collection<InvokeStmt>> notifies = new HashMap<SootMethod, Collection<InvokeStmt>>();
//		//Map<SootMethod, Collection<Stmt>> _method2dependeeMap = collectDependeesInMethods();
//		SootClass THREAD_CLASS = Scene.v().getSootClass("java.lang.Thread");
//		System.out.println("Indus THREAD_CLASS="+THREAD_CLASS);
//		FastHierarchy har = new FastHierarchy();
//		for (SootClass sClass:Scene.v().getApplicationClasses())
//		{
//			System.out.println("Indus sClass="+sClass+" har.isSubclass(sClass, THREAD_CLASS)="+har.isSubclass(sClass, THREAD_CLASS)+" sClass.implementsInterfacejava.lang.Runnable="+sClass.implementsInterface("java.lang.Runnable"));
//			if (!har.isSubclass(sClass, THREAD_CLASS) && !sClass.implementsInterface("java.lang.Runnable"))
//				continue;
//			if ( sClass.isPhantom() ) {
//				// skip phantom classes
//				continue;
//			}
//			for(SootMethod m:sClass.getMethods())
//			{
////				Collection<Stmt> stmtInMethod= new HashSet<Stmt>();
////				Collection<InvokeStmt> waitStmtInMethod= new HashSet<InvokeStmt>();
////				Collection<InvokeStmt> notifyStmtInMethod= new HashSet<InvokeStmt>();
//				if ( !m.isConcrete() ) {
//					// skip abstract methods and phantom methods, and native methods as well
//					continue;
//				}
//				if ( m.toString().indexOf(": java.lang.Class class$") != -1 ) {
//					// don't handle reflections now either
//					continue;
//				}
//				if ( !m.hasActiveBody() ) {
//					continue;
//				}
//
//				if (!reachableMethods.contains(m)) {
//					// skip unreachable methods
//					continue;
//				}
//				// Set ReadyAnalysis
//				Collection<Stmt> stmtInMethod= new HashSet<Stmt>();
//				Collection<InvokeStmt> waitStmtInMethod= new HashSet<InvokeStmt>();
//				Collection<InvokeStmt> notifyStmtInMethod= new HashSet<InvokeStmt>();
//				boolean hasEnter=false;
//				boolean hasExit=false;
//				EnterMonitorStmt myEnter=null;
//				ExitMonitorStmt myExit=null;
//				Collection<Stmt> myStmtInEnterExit= new HashSet<Stmt>();
//
//				// SynchronizationAnalysis
//				try
//				{
//					Iterator<Unit> units=m.retrieveActiveBody().getUnits().iterator();
//					Stmt lastSt=null;
//					while(units.hasNext())
//					{
//						try
//						{
//							Stmt st = (Stmt) units.next();
//							System.out.println("Indus m="+m+" st="+st);
//							if (st instanceof EnterMonitorStmt)
//							{
//								hasEnter=true;
//								//myEnter= (EnterMonitorStmt) st;
//								System.out.println("st instanceof EnterMonitorStmt");
//							}
//
//							if (hasEnter && !(st instanceof EnterMonitorStmt))
//							{
//				            	System.out.println("Synchronization edge: "+m+" - "+lastSt+" --> "+m+" - "+st);
//								Variable _deV=makeVariable(soot.jimple.StringConstant.v("SYNC-EnterExitMonitor"),lastSt);
//								Variable _dtV=makeVariable(soot.jimple.StringConstant.v("SYNC-EnterExitMonitor"),st);
//								createTransferEdge(_deV,m, lastSt, _dtV, m, st, VTEType.VTE_CONTROL_INTRA, false);
//							}
//							if (st instanceof ExitMonitorStmt)
//							{
//								hasEnter=false;
//								//break;
//								System.out.println("st instanceof ExitMonitorStmt");
//							}
//							lastSt=st;
//
//							// InterferenceAnalysis
//							if(st instanceof AssignStmt)
//							{
//								//System.out.println("AssignStmt st="+st);
//								for (SootClass sClass2:Scene.v().getApplicationClasses())
//								{
//									//System.out.println("**************************sClass2: "+sClass2);
//									if ((!har.isSubclass(sClass2, THREAD_CLASS) && !sClass2.implementsInterface("java.lang.Runnable")) || sClass2.equals(sClass))
//										continue;
////									if (sClass2.equals(sClass))
////										continue;
//									if ( sClass2.isPhantom() ) {
//										// skip phantom classes
//										continue;
//									}
//									System.out.println("	**************************Thread sClass2: "+sClass2);
//									for(SootMethod m2:sClass2.getMethods())
//									{
//										try
//										{
//											if ( !m2.isConcrete() ) {
//												// skip abstract methods and phantom methods, and native methods as well
//												continue;
//											}
//											if ( m2.toString().indexOf(": java.lang.Class class$") != -1 ) {
//												// don't handle reflections now either
//												continue;
//											}
//											if ( !m2.hasActiveBody() ) {
//												continue;
//											}
//											Iterator<Unit> units2=m2.retrieveActiveBody().getUnits().iterator();
//											while(units2.hasNext())
//											{
//												try
//												{
//													Stmt st2 =(Stmt) units2.next();
//													if(st2 instanceof AssignStmt)
//													{
//														//System.out.println("	AssignStmt st2="+st2);
//														Pair<AssignStmt, SootMethod> _de= new Pair<AssignStmt, SootMethod>((AssignStmt)st, m);
//														Pair<AssignStmt, SootMethod> _dt= new Pair<AssignStmt, SootMethod>((AssignStmt)st2, m2);
//														Value _dependee = ((DefinitionStmt) _de).getLeftOp();
//														Value _dependent = ((DefinitionStmt) _dt).getRightOp();
//														Variable _vde=makeVariable(_dependee,st2);
//														Variable _vdt=makeVariable(_dependent,st);
//														System.out.println("Interference edge: "+m+" - "+st+" --> "+m2+" - "+st2);
//														createTransferEdge(_vde, m, st, _vdt, m2, st2, VTEType.VTE_ARRAYELE, false);
//														createTransferEdge(_vde, m, st, _vdt, m2, st2, VTEType.VTE_INSVAR, false);
//														createTransferEdge(_vde, m, st, _vdt, m2, st2, VTEType.VTE_STVAR, false);
//													}
//
//												}
//												 catch (Exception _e)
//										            {
//										            	//System.out.println("Exception: "+_e);
//										            }
//											}   // while
//										}
//							            catch (Exception _e)
//							            {
//							            	//System.out.println("Exception: "+_e);
//							            }
//									}  //for
//								}	//for
//							}    //if
//
//							//ReadyAnalysis processRule1And3
//							stmtInMethod.add(st);
//							if (st instanceof InvokeStmt)
//							{
//								InvokeStmt ist=(InvokeStmt) st;
//								String istStr=ist.toString();
//								if (istStr.indexOf(" void wait()")>1)
//								{
//									waitStmtInMethod.add(ist);
//								}
//								else if (istStr.indexOf(" void notify()")>1 || istStr.indexOf(" void notifyAll()")>1)
//								{
//									notifyStmtInMethod.add(ist);
//								}
//
//								//ReadyAnalysisDv1 processRule4
//								//if (!waits.isEmpty() && !notifies.isEmpty())
//								{
//								//System.out.println("InvokeStmt st="+st);
//									for (SootClass sClass2:Scene.v().getApplicationClasses())
//									{
//										if ((!har.isSubclass(sClass2, THREAD_CLASS) && !sClass2.implementsInterface("java.lang.Runnable")) || sClass2.equals(sClass))
//											continue;
//										if ( sClass.isPhantom() ) {
//											// skip phantom classes
//											continue;
//										}
//										System.out.println("	**************************Thread sClass2: "+sClass2);
//										for(SootMethod m2:sClass2.getMethods())
//										{
//										try
//										{
//											if ( !m2.isConcrete() ) {
//												// skip abstract methods and phantom methods, and native methods as well
//												continue;
//											}
//											if ( m2.toString().indexOf(": java.lang.Class class$") != -1 ) {
//												// don't handle reflections now either
//												continue;
//											}
//											if ( !m2.hasActiveBody() ) {
//												continue;
//											}
//											Iterator<Unit> units2=m2.retrieveActiveBody().getUnits().iterator();
//											while(units2.hasNext())
//											{
//											try
//											{
//												Stmt st2 =(Stmt) units2.next();
//												//System.out.println("	st2="+st2);
//			//									if (st2.equals(st))
//			//										continue;
//
//												//ReadyAnalysisDv1 processRule4
//												if(st2 instanceof InvokeStmt)   //|| st2.containsInvokeExpr()
//												{
//													//System.out.println("	InvokeStmt  st2="+st2);
//													//System.out.println("	_de  _dt");
//													//System.out.println(" _de="+_de+" _dt="+_dt);
//													Variable _deV=makeVariable(soot.jimple.StringConstant.v("wait-notify"), st);
//													Variable _dtV=makeVariable(soot.jimple.StringConstant.v("wait-notify"), st2);
//													//System.out.println("vtg before ReadyDAv1() WaitNotify Dependence");
//													//System.out.println(vtg);
//													System.out.println("Ready Rule4 edge: "+m+" - "+st+" --> "+m2+" - "+st2);
//													createTransferEdge(_deV, m, st, _dtV, m2, st2, VTEType.VTE_CONTROL_INTER, false);
//												}
//											}
//								            catch (Exception _e)
//								            {
//								            	//System.out.println("Exception: "+_e);
//								            }
//											}   // while
//
//										}
//										catch (Exception _e)
//							            {
//							            	//System.out.println("Exception: "+_e);
//							            }
//										}  //for
//									}	//for
//								}
//							}
//							else if (st instanceof EnterMonitorStmt)
//							{
//								hasEnter=true;
//								myEnter= (EnterMonitorStmt) st;
//								//ReadyAnalysisDv1 processRule2
//								//System.out.println("EnterMonitorStmt st="+st);
//								for (SootClass sClass2:Scene.v().getApplicationClasses())
//								{
//									if ((!har.isSubclass(sClass2, THREAD_CLASS) && !sClass2.implementsInterface("java.lang.Runnable")) || sClass2.equals(sClass))
//										continue;
//									if ( sClass2.isPhantom() ) {
//										// skip phantom classes
//										continue;
//									}
//									System.out.println("	**************************Thread sClass2: "+sClass2);
//									for(SootMethod m2:sClass2.getMethods())
//									{
//									try
//									{
//										if ( !m2.isConcrete() ) {
//											// skip abstract methods and phantom methods, and native methods as well
//											continue;
//										}
//										if ( m2.toString().indexOf(": java.lang.Class class$") != -1 ) {
//											// don't handle reflections now either
//											continue;
//										}
//										if ( !m2.hasActiveBody() ) {
//											continue;
//										}
//										if (m2.equals(m))
//											continue;
//										Iterator<Unit> units2=m2.retrieveActiveBody().getUnits().iterator();
//										while(units2.hasNext())
//										{
//										try
//										{
//											Stmt st2 =(Stmt) units2.next();
//											//System.out.println("	st2="+st2);
//		//									if (st2.equals(st))
//		//										continue;
//											//ReadyAnalysisDv1 processRule2
//											if(st2 instanceof ExitMonitorStmt )
//											{
//												System.out.println("Ready Rule2 edge: "+m+" - "+st+" --> "+m2+" - "+st2);
//												Variable _deV=makeVariable(soot.jimple.StringConstant.v("Enter-Exit-Monitor"),st);
//												Variable _dtV=makeVariable(soot.jimple.StringConstant.v("Enter-Exit-Monitor"),st2);
//												createTransferEdge(_deV, m, st, _dtV, m2, st2, VTEType.VTE_CONTROL_INTER, false);
//											}
//
//										}
//							            catch (Exception _e)
//							            {
//							            	//System.out.println("Exception: "+_e);
//							            }
//										}   // while
//									}
//						            catch (Exception _e)
//						            {
//						            	//System.out.println("Exception: "+_e);
//						            }
//									}  //for
//								}	//for
//
//							}
//							else if (st instanceof ExitMonitorStmt)
//							{
//								hasExit=true;
//								myExit= (ExitMonitorStmt) st;
//							}
//							else if (hasEnter && !hasExit)
//							{
//								myStmtInEnterExit.add(st);
//							}
//						 }
//			            catch (Exception _e)
//			            {
//			            	//System.out.println("Exception: "+_e);
//			            }
//					}
//					waits.put(m, waitStmtInMethod);
//					notifies.put(m, notifyStmtInMethod);
//	            }
//	            catch (Exception _e)
//	            {
//	            	//System.out.println("Exception: "+_e);
//	            }
//
//
//			}
//		}
//		return 0;
//	}
}