/*******************************************************************************
 * Indus, a program analysis and transformation toolkit for Java.
 * Copyright (c) 2001, 2007 Venkatesh Prasad Ranganath
 * 
 * All rights reserved.  This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * the distribution containing this program, and is available at 
 * http://www.opensource.org/licenses/eclipse-1.0.php.
 * 
 * For questions about the license, copyright, and software, contact 
 * 	Venkatesh Prasad Ranganath at venkateshprasad.ranganath@gmail.com
 *                                 
 * This software was developed by Venkatesh Prasad Ranganath in SAnToS Laboratory 
 * at Kansas State University.
 *******************************************************************************/

package edu.ksu.cis.indus.staticanalyses.dependency;

import edu.ksu.cis.indus.common.collections.IPredicate;
import edu.ksu.cis.indus.common.collections.InstanceOfPredicate;
import edu.ksu.cis.indus.common.collections.MapUtils;
import edu.ksu.cis.indus.common.collections.SetUtils;
import edu.ksu.cis.indus.common.datastructures.HistoryAwareLIFOWorkBag;
import edu.ksu.cis.indus.common.datastructures.IWorkBag;
import edu.ksu.cis.indus.common.datastructures.Pair;
import edu.ksu.cis.indus.common.datastructures.Triple;
import edu.ksu.cis.indus.common.datastructures.Pair.PairManager;
import edu.ksu.cis.indus.common.soot.BasicBlockGraph;
import edu.ksu.cis.indus.common.soot.Util;
import edu.ksu.cis.indus.common.soot.BasicBlockGraph.BasicBlock;
import edu.ksu.cis.indus.interfaces.ICallGraphInfo;
import edu.ksu.cis.indus.interfaces.IEnvironment;
import edu.ksu.cis.indus.interfaces.IMonitorInfo;
import edu.ksu.cis.indus.interfaces.IThreadGraphInfo;
import edu.ksu.cis.indus.processing.AbstractProcessor;
import edu.ksu.cis.indus.processing.Context;
import edu.ksu.cis.indus.processing.ProcessingController;
import edu.ksu.cis.indus.staticanalyses.InitializationException;
import edu.ksu.cis.indus.staticanalyses.concurrency.SafeLockAnalysis;
import edu.ksu.cis.indus.staticanalyses.dependency.direction.BackwardDirectionSensitiveInfo;
import edu.ksu.cis.indus.staticanalyses.dependency.direction.ForwardDirectionSensitiveInfo;
import edu.ksu.cis.indus.staticanalyses.dependency.direction.IDirectionSensitiveInfo;
import edu.ksu.cis.indus.staticanalyses.flow.instances.ofa.OFAnalyzer;
import edu.ksu.cis.indus.staticanalyses.flow.modes.sensitive.allocation.AllocationContext;
import edu.ksu.cis.indus.staticanalyses.interfaces.IValueAnalyzer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import ODD.StaticTransferGraph;
import disttaint.StaticTransferGraph;
import MciaUtil.VTEdge.VTEType;
import dua.method.CFGDefUses.Variable;
import soot.FastHierarchy;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.JastAddJ.ReturnStmt;
import soot.JastAddJ.ThrowStmt;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.MonitorStmt;
import soot.jimple.NopStmt;
import soot.jimple.NullConstant;
import soot.jimple.RetStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.TableSwitchStmt;
import soot.jimple.VirtualInvokeExpr;

/**
 * This class provides ready dependency information. This implementation refers to the technical report <a
 * href="http://www.cis.ksu.edu/santos/papers/technicalReports.html">A Formal Study of Slicing for Multi-threaded Program with
 * JVM Concurrency Primitives"</a>. This implementation by default does not consider call-sites for dependency calculation.
 * <p>
 * <i>Ready Dependence</i>: In a thread, all statements reachable from an enter-monitor statement or a <code>wait()</code>
 * call-site via intra-procedural control-flow path with no intervening enter-monitor statement or <code>wait()</code>
 * call-site are ready dependent on the enter-monitor statement. Across different threads, enter-monitor statements and
 * <code>wait()</code> call-sites in a thread are ready dependent on corresponding exit-monitor statements and
 * <code>notify()/notifyAll()</code> call-sites, respectively, occurring in a different thread.
 * </p>
 * <p>
 * By default, all rules are considered for the analysis. This can be changed via <code>setRules()</code>. This class will
 * also use OFA information if it is configured to do so.
 * </p>
 * <p>
 * Ready dependence information pertaining to entry and exit points of synchronized methods cause a divergence in the way
 * information is provided via <code>getDependees</code> and <code>getDependents</code>.
 * </p>
 * <p>
 * In case the body of the synchronized method is unavailable, then any dependence involving entry and exit points of the
 * method will use null as the dependee/dependent statement.
 * </p>
 * 
 * @author <a href="http://www.cis.ksu.edu/~rvprasad">Venkatesh Prasad Ranganath</a>
 * @author $Author: rvprasad $
 * @version $Revision: 1.91 $
 */
public class ReadyDAv1
		extends
		AbstractDependencyAnalysis<Stmt, SootMethod, Pair<Stmt, SootMethod>, SootMethod, Map<Stmt, Collection<Pair<Stmt, SootMethod>>>, Stmt, SootMethod, Pair<Stmt, SootMethod>, SootMethod, Map<Stmt, Collection<Pair<Stmt, SootMethod>>>> {
	StaticTransferGraph vtg;
	/**
	 * This preprocesses information before ready dependence is calculated. Information required during the analysis is
	 * collected by this class.
	 * 
	 * @author <a href="http://www.cis.ksu.edu/~rvprasad">Venkatesh Prasad Ranganath</a>
	 * @author $Author: rvprasad $
	 * @version $Revision: 1.91 $
	 */
	private class PreProcessor
			extends AbstractProcessor {

		/**
		 * Collects monitor statements and statements with <code>Object.wait()</code> and <code>Object.notifyXX()</code>
		 * call-sites.
		 * 
		 * @param stmt to be preprocessed.
		 * @param context in which <code>stmt</code> occurs.
		 * @pre stmt.oclIsTypeOf(ExitMonitorStmt) or stmt.oclIsTypeOf(EnterMonitorStmt) or stmt.oclIsTypeOf(InvokeStmt)
		 * @pre stmt != null and context.getCurrentMethod() != null
		 */
		@Override public void callback(final Stmt stmt, final Context context) {
			final SootMethod _method = context.getCurrentMethod();
			final InvokeExpr _expr = stmt.getInvokeExpr();

			if (_expr instanceof VirtualInvokeExpr) {
				final VirtualInvokeExpr _invokeExpr = (VirtualInvokeExpr) _expr;
				final SootMethod _callee = _invokeExpr.getMethod();

				Map<SootMethod, Collection<InvokeStmt>> _method2stmts = null;

				if (Util.isWaitMethod(_callee)) {
					_method2stmts = waits;
				} else if (Util.isNotifyMethod(_callee)) {
					_method2stmts = notifies;
				}

				if (_method2stmts != null) {
					MapUtils.putIntoCollectionInMap(_method2stmts, _method, (InvokeStmt) stmt);
				}
			}
		}

		/**
		 * Collects all the methods that encloses an enter-monitor statement or a call to <code>wait()</code> method.
		 */
		@Override public void consolidate() {
			readyMethods.addAll(waits.keySet());
		}

		/**
		 * @see edu.ksu.cis.indus.processing.IProcessor#hookup(ProcessingController)
		 */
		public void hookup(final ProcessingController ppc) {
			ppc.register(InvokeStmt.class, this);
		}

		/**
		 * @see edu.ksu.cis.indus.processing.IProcessor#unhook(ProcessingController)
		 */
		public void unhook(final ProcessingController ppc) {
			ppc.unregister(InvokeStmt.class, this);
		}
	}

	/**
	 * This predicate can be used to check if an object of this class type.
	 */
	public static final IPredicate<IDependencyAnalysis<?, ?, ?, ?, ?, ?>> INSTANCEOF_PREDICATE = new InstanceOfPredicate<ReadyDAv1, IDependencyAnalysis<?, ?, ?, ?, ?, ?>>(
			ReadyDAv1.class);

	/**
	 * This indicates intra-thread intra-procedural monitor aquisition based ready dependence.
	 */
	public static final int RULE_1 = 1;

	/**
	 * This indicates inter-thread monitor aquisition based ready dependence.
	 */
	public static final int RULE_2 = 2;

	/**
	 * This indicates intra-thread intra-procedural <code>Object.wait()</code> based ready dependence.
	 */
	public static final int RULE_3 = 4;

	/**
	 * This indicates inter-thread <code>Object.wait()</code> based ready dependence.
	 */
	public static final int RULE_4 = 8;

	/**
	 * The logger used by instances of this class to log messages.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ReadyDAv1.class);

	/**
	 * This is the logical OR of the <code>RULE_XX</code> as provided by the user. This indicates the rules which need to be
	 * considered while calculating ready dependency.
	 */
	protected int rules = RULE_1 | RULE_2 | RULE_3 | RULE_4;

	/**
	 * This maps a method to a collection of enter monitor statements in that method.
	 */
	final Map<SootMethod, Collection<EnterMonitorStmt>> enterMonitors = new HashMap<SootMethod, Collection<EnterMonitorStmt>>();

	/**
	 * This maps a method to a collection of exit monitor statements in that method.
	 */
	final Map<SootMethod, Collection<ExitMonitorStmt>> exitMonitors = new HashMap<SootMethod, Collection<ExitMonitorStmt>>();

	/**
	 * This maps methods to <code>Object.notifyXX</code> method calls in them.
	 */
	final Map<SootMethod, Collection<InvokeStmt>> notifies = new HashMap<SootMethod, Collection<InvokeStmt>>();

	/**
	 * The collection of methods (readyMethods) which contain at least an enter-monitor statement or a <code>wait()</code>
	 * call-site.
	 */
	final Collection<SootMethod> readyMethods = new HashSet<SootMethod>();

	/**
	 * This maps methods to <code>Object.wait</code> method calls in them.
	 */
	final Map<SootMethod, Collection<InvokeStmt>> waits = new HashMap<SootMethod, Collection<InvokeStmt>>();

	/**
	 * This provides call graph of the system being analyzed.
	 */
	private ICallGraphInfo callgraph;

	/**
	 * This indicates if dependence should be considered across call-sites. Depending on the application, one may choose to
	 * ignore ready dependence across call-sites and rely on other dependence analysis to include the call-site. This only
	 * affects how rule 1 and 3 are interpreted.
	 */
	private boolean considerCallSites;

	/**
	 * This provides direction-sensitive information to make the analysis direction sensitive.
	 */
	private final IDirectionSensitiveInfo directionSensInfo;

	/**
	 * This provides information such as the classes occurring in the system being analyzed.
	 */
	private IEnvironment env;

	/**
	 * This provides monitor information in the system.
	 */
	private IMonitorInfo<?> monitorInfo;

	/**
	 * The object flow analysis to be used.
	 */
	private IValueAnalyzer<?> ofa;

	/**
	 * This manages pairs.
	 */
	private PairManager pairMgr;

	/**
	 * This provides safe lock information.
	 */
	private SafeLockAnalysis safelockAnalysis;

	/**
	 * This provides call graph of the system being analyzed.
	 */
	private IThreadGraphInfo threadgraph;

	/**
	 * This indicates if object flow analysis should be used.
	 */
	private boolean useOFA;

	/**
	 * This indicates if safe lock analysis should be used.
	 */
	private boolean useSafeLockAnalysis;

	/**
	 * Creates an instance of this class.
	 * 
	 * @param directionSensitiveInfo that controls the direction.
	 * @param direction of the analysis
	 * @pre info != null and direction != null
	 */
	protected ReadyDAv1(final IDirectionSensitiveInfo directionSensitiveInfo, final Direction direction) {
		super(direction);
		directionSensInfo = directionSensitiveInfo;
		preprocessor = new PreProcessor();
		considerCallSites = false;
	}
	protected ReadyDAv1(final IDirectionSensitiveInfo directionSensitiveInfo, final Direction direction, StaticTransferGraph vtg) {
		super(direction);
		directionSensInfo = directionSensitiveInfo;
		preprocessor = new PreProcessor();
		considerCallSites = false;
		this.vtg=vtg;
	}
	/**
	 * Retrieves an instance of ready dependence analysis that calculates information in backward direction.
	 * 
	 * @return an instance of ready dependence.
	 * @post result != null
	 */
	public static ReadyDAv1 getBackwardReadyDA() {
		return new ReadyDAv1(new BackwardDirectionSensitiveInfo(), Direction.BACKWARD_DIRECTION);
	}
	public static ReadyDAv1 getBackwardReadyDA(StaticTransferGraph vtg) {
		return new ReadyDAv1(new BackwardDirectionSensitiveInfo(), Direction.BACKWARD_DIRECTION, vtg);
	}
	/**
	 * Retrieves an instance of ready dependence analysis that calculates information in forward direction.
	 * 
	 * @return an instance of ready dependence.
	 * @post result != null
	 */
	public static ReadyDAv1 getForwardReadyDA() {
		return new ReadyDAv1(new ForwardDirectionSensitiveInfo(), Direction.FORWARD_DIRECTION);
	}
	public static ReadyDAv1 getForwardReadyDA(StaticTransferGraph vtg) {
		return new ReadyDAv1(new ForwardDirectionSensitiveInfo(), Direction.FORWARD_DIRECTION, vtg);
	}
	/**
	 * Calculates ready dependency for the methods provided at initialization. It considers only the rules specified by via
	 * <code>setRules</code> method. By default, all rules are considered for the analysis.
	 * 
	 * @see edu.ksu.cis.indus.staticanalyses.dependency.AbstractDependencyAnalysis#analyze()
	 */
	@Override public void analyze() {
		unstable();

		if (monitorInfo.isStable() && callgraph.isStable() && threadgraph.isStable()
				&& (!useSafeLockAnalysis || safelockAnalysis.isStable())) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("BEGIN: Ready Dependence [" + this.getClass() + "] processing");
			}

			if (!threadgraph.getCreationSites().isEmpty()) {
				processRules();
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("analyze() - " + toString());
			}

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("END: Ready Dependence processing");
			}

			stable();
		} else {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Delaying execution as prerequisites are unsatisfied.");
			}
		}
	}

	/**
	 * Returns the statements on which the <code>dependentStmt</code> depends on. Refer to class level documentation for
	 * important details. A pair of null and method in the result indicates that the dependence is due to the synchronized
	 * nature of the method in the pair.
	 * 
	 * @param dependentStmt is the statement for which the dependee info is requested.
	 * @param method in which the statement occurs.
	 * @return a collection of statement.
	 * @see AbstractDependencyAnalysis#getDependees( java.lang.Object, java.lang.Object)
	 */
	public Collection<Pair<Stmt, SootMethod>> getDependees(final Stmt dependentStmt, final SootMethod method) {
		return getDependenceHelper(dependentStmt, method, dependent2dependee);
	}

	/**
	 * Returns the statements which depend on <code>dependeeStmt</code>. Refer to class level documentation for important
	 * details. A pair of null and method in the result indicates that the dependence is due to the synchronized nature of the
	 * method in the pair.
	 * 
	 * @param dependeeStmt is the statement for which the dependent info is requested.
	 * @param method in which the statement occurs.
	 * @return a collection of statement.
	 * @see edu.ksu.cis.indus.staticanalyses.dependency.AbstractDependencyAnalysis#getDependents( java.lang.Object,
	 *      java.lang.Object)
	 */
	public Collection<Pair<Stmt, SootMethod>> getDependents(final Stmt dependeeStmt, final SootMethod method) {
		return getDependenceHelper(dependeeStmt, method, dependee2dependent);
	}

	/**
	 * @see edu.ksu.cis.indus.staticanalyses.dependency.AbstractDependencyAnalysis#getIds()
	 */
	public Collection<IDependencyAnalysis.DependenceSort> getIds() {
		return Collections.singleton(IDependencyAnalysis.DependenceSort.READY_DA);
	}

	/**
	 * Provides the rules that are active at present.
	 * 
	 * @return the active rules as a logical OR of <i>RULE_XX</i> constants.
	 * @post result and not (RULE_1 or RULE_2 or RULE_3 or RULE_4) == 0
	 */
	public int getRules() {
		return rules;
	}

	/**
	 * Resets internal data structures. <i>The rules are not reset.</i> Also, the data acquired at setup time is not erased.
	 * 
	 * @see edu.ksu.cis.indus.staticanalyses.interfaces.AbstractAnalysis#reset()
	 */
	@Override public void reset() {
		super.reset();
		enterMonitors.clear();
		exitMonitors.clear();
		waits.clear();
		notifies.clear();
	}

	/**
	 * Records if ready dependency should be interprocedural or otherwise.
	 * 
	 * @param consider <code>true</code> indicates that any call-site leading to wait() call-site or enter-monitor statement
	 *            should be considered as a ready dependeee; <code>false</code>, otherwise. This only affects how rule 1
	 *            and 3 are interpreted
	 */
	public void setConsiderCallSites(final boolean consider) {
		considerCallSites = consider;
	}

	/**
	 * Sets the rules to be processed.
	 * 
	 * @param rulesParam is the logical OR of <i>RULE_XX</i> constants defined in this class.
	 * @throws IllegalArgumentException when rules is not a valid combination of <i>RULE_XX</i> constants.
	 */
	public void setRules(final int rulesParam) {
		if ((rulesParam & ~(RULE_1 | RULE_2 | RULE_3 | RULE_4)) != 0) {
			throw new IllegalArgumentException("rules has to be a combination of RULE_XX constants defined in this class.");
		}
		rules = rulesParam;
	}

	/**
	 * Sets if object flow analysis should be used or not.
	 * 
	 * @param flag <code>true</code> indicates that object flow analysis should be used; <code>false</code>, otherwise.
	 */
	public final void setUseOFA(final boolean flag) {
		useOFA = flag;
	}

	/**
	 * Controls if safe lock analysis should be used.
	 * 
	 * @param b <code>true</code> indicates the analysis should be used; <code>false</code>, otherwise.
	 */
	public void setUseSafeLockAnalysis(final boolean b) {
		useSafeLockAnalysis = b;
	}

	/**
	 * Returns a stringified representation of the analysis information.
	 * 
	 * @return a string containing the analysis info.
	 * @post result != null
	 */
	@Override public String toString() {
		final StringBuffer _result = new StringBuffer("Statistics for Ready dependence as calculated by "
				+ getClass().getName() + "\n");
		int _edgeCount1 = 0;
		final StringBuffer _temp = new StringBuffer();
		String _methodStr= "";
		for (final Iterator<Map.Entry<SootMethod, Map<Stmt, Collection<Pair<Stmt, SootMethod>>>>> _i = dependent2dependee
				.entrySet().iterator(); _i.hasNext();) {
			final Map.Entry<SootMethod, Map<Stmt, Collection<Pair<Stmt, SootMethod>>>> _entry = _i.next();
			final Object _method = _entry.getKey();
			_methodStr=_method.toString();
//			if (_methodStr.startsWith("<java.") || _methodStr.startsWith("<sun."))
//				continue;
			_result.append("In method " + _method + "\n ");

			for (final Iterator<Map.Entry<Stmt, Collection<Pair<Stmt, SootMethod>>>> _k = _entry.getValue().entrySet()
					.iterator(); _k.hasNext();) {
				final Map.Entry<Stmt, Collection<Pair<Stmt, SootMethod>>> _entry1 = _k.next();
				final Object _dependent = _entry1.getKey();

				int _localEdgeCount = 0;

				for (final Iterator<Pair<Stmt, SootMethod>> _j = _entry1.getValue().iterator(); _j.hasNext();) {
					final Object _dependee = _j.next();
					_temp.append("\t\t" + _dependee + " <-- " + _dependent + "\n");
				}
				_localEdgeCount += ((Collection) _entry1.getValue()).size();

				final Object _key = _entry1.getKey();
				_result.append("\tFor " + _key + "[");

				if (_key != null) {
					_result.append(_key.hashCode());
				} else {
					_result.append(0);
				}
				if (_localEdgeCount>0)
					_result.append("] there are " + _localEdgeCount + " Ready dependence edges.\n");
				_result.append(_temp);
				_temp.delete(0, _temp.length());
				_edgeCount1 += _localEdgeCount;
			}
		}
		_result.append("A total of " + _edgeCount1 + " Ready dependence edges exist.");
		return _result.toString();
	}

	// /CLOVER:OFF

	/**
	 * @see edu.ksu.cis.indus.staticanalyses.dependency.AbstractDependencyAnalysis#getDependenceRetriever()
	 */
	@Override protected IDependenceRetriever<Stmt, SootMethod, Pair<Stmt, SootMethod>, Stmt, SootMethod, Pair<Stmt, SootMethod>> getDependenceRetriever() {
		return new PairRetriever<Stmt, SootMethod, Stmt, Stmt, SootMethod, Stmt>();
	}

	// /CLOVER:ON

	/**
	 * Checks if the given wait invocation is dependent on the notify invocation according to rule 4 based of OFA information.
	 * 
	 * @param waitPair is the wait invocation statement and containg method pair.
	 * @param notifyPair is the notify invocation statement and containg method pair.
	 * @return <code>true</code> if there is a dependence; <code>false</code>, otherwise.
	 * @pre waitPair.getFirst() != null and waitPair.getSecond() != null
	 * @pre notifyPair.getFirst() != null and notifyPair.getSecond() != null
	 */
	protected final boolean ifDependentOnBasedOnOFAByRule4(final Pair<InvokeStmt, SootMethod> waitPair,
			final Pair<InvokeStmt, SootMethod> notifyPair) {
		boolean _result;

		final SootMethod _waitMethod = waitPair.getSecond();
		final SootMethod _notifyMethod = notifyPair.getSecond();
		final InvokeStmt _wStmt = waitPair.getFirst();
		final InvokeStmt _nStmt = notifyPair.getFirst();
		final Context _context = new AllocationContext();

		final InstanceInvokeExpr _wExpr = (InstanceInvokeExpr) _wStmt.getInvokeExpr();
		_context.setProgramPoint(_wExpr.getBaseBox());
		_context.setStmt(_wStmt);
		_context.setRootMethod(_waitMethod);

		final Collection<?> _col1 = ofa.getValues(_wExpr.getBase(), _context);

		final InstanceInvokeExpr _nExpr = (InstanceInvokeExpr) _nStmt.getInvokeExpr();
		_context.setProgramPoint(_nExpr.getBaseBox());
		_context.setStmt(_nStmt);
		_context.setRootMethod(_notifyMethod);

		final Collection<?> _col2 = ofa.getValues(_nExpr.getBase(), _context);

		final NullConstant _n = NullConstant.v();
		final Collection<?> _temp = SetUtils.intersection(_col1, _col2);

		while (_temp.remove(_n)) {
			// does nothing
		}
		_result = !_temp.isEmpty();

		return _result;
	}

	/**
	 * Checks if the given enter monitor statement/synchronized method is dependent on the exit monitor statement/synchronized
	 * method according to rule 2. The dependence is determined based on the relation between the classes of the objects on
	 * which synchronization is being performed.
	 * 
	 * @param enterPair is the enter monitor statement and containg statement pair.
	 * @param exitPair is the exit monitor statement and containg statement pair.
	 * @return <code>true</code> if there is a dependence; <code>false</code>, otherwise.
	 * @pre enterPair != null and exitPair != null
	 * @pre enterPair.getSecond() != null and exitPair.getSecond() != null
	 * @pre enterPair.getSecond().oclIsKindOf(SootMethod) and exitPair.getSecond().oclIsKindOf(SootMethod)
	 * @pre enterPair.getFirst() != null
	 * @pre enterPair.getFirst().oclIsKindOf(EnterMonitorStmt) or enterPair.getFirst().equals(null)
	 * @pre exitPair.getFirst() != null
	 * @pre exitPair.getFirst().oclIsKindOf(ExitMonitorStmt) or exitPair.getFirst().equals(null)
	 */
	protected boolean ifDependentOnByRule2(final Pair<EnterMonitorStmt, SootMethod> enterPair,
			final Pair<ExitMonitorStmt, SootMethod> exitPair) {
		Type _enterMonitorOptype;
		Type _exitMonitorOpType;
		final SootMethod _sm1 = enterPair.getSecond();

		boolean _result = isLockUnsafe(enterPair.getFirst(), _sm1);

		if (_result) {
			final SootMethod _sm2 = exitPair.getSecond();

			boolean _syncedStaticMethod1 = false;
			boolean _syncedStaticMethod2 = false;
			final EnterMonitorStmt _o1 = enterPair.getFirst();

			if (_o1 == null) {
				_enterMonitorOptype = _sm1.getDeclaringClass().getType();
				_syncedStaticMethod1 = _sm1.isStatic();
			} else {
				_enterMonitorOptype = _o1.getOp().getType();
			}

			final ExitMonitorStmt _o2 = exitPair.getFirst();

			if (_o2 == null) {
				_exitMonitorOpType = _sm2.getDeclaringClass().getType();
				_syncedStaticMethod2 = _sm2.isStatic();
			} else {
				_exitMonitorOpType = _o2.getOp().getType();
			}

			if (_syncedStaticMethod1 && _syncedStaticMethod2) {
				// if we are dealing with synchronized static methods, then they will lock the class object, hence,
				// inheritance relation should not be considered.
				_result = _enterMonitorOptype.equals(_exitMonitorOpType);
			} else if (_syncedStaticMethod1 ^ _syncedStaticMethod2) {
				/*
				 * if only one of the methods is static and synchronized then we cannot determine RDA as it is possible that
				 * the monitor in the non-static method may actually be on the class object of the class in which the static
				 * method is defined. There are many combinations which can be pruned. No time now. THINK
				 */
				_result = true;
			} else {
				_result = Util.isSameOrSubType(_enterMonitorOptype, _exitMonitorOpType, env);

				if (_result && useOFA) {
					_result = ifDependentOnBasedOnOFAByRule2(enterPair, exitPair);
				}
			}
		}
//		if (_result) 
//		{	
//			String strDe=_sm1.toString();
//			String strDt=exitPair.getSecond().toString();
//			//System.out.println("InterferenceDAv1() strDe: "+strDe+" strDe.startsWith(\"<java.\")"+strDe.startsWith("<java.")+" strDe.startsWith(\"<sun.\")"+strDe.startsWith("<sun."));
//			//System.out.println("InterferenceDAv1() strDt: "+strDe+" strDt.startsWith(\"<java.\")"+strDt.startsWith("<java.")+" strDt.startsWith(\"<sun.\")"+strDt.startsWith("<sun."));
//			//
//				Stmt _deStmt = enterPair.getFirst();
//				Stmt _dtStmt = exitPair.getFirst();
//				if ((!strDe.startsWith("<java.") && !strDe.startsWith("<sun.")) || (!strDt.startsWith("<java.") && !strDt.startsWith("<sun.")))
//				{	
//				    System.out.println("ReadyDAv1() edge: "+", "+strDe+", "+_deStmt+" --> "+", "+strDt+", "+_dtStmt);
//				}
//				//vtg.createTransferEdge(null, _sm1, _deStmt, null, (SootMethod) exitPair.getSecond(), _dtStmt, VTEType.VTE_NOTIFYWAIT, false);
//				//System.out.println("ReadyDAv1() adds NOTIFYWAIT edge: "+vtg);
//			//createTransferEdge(_de, _deMethod, _deStmt, _dt, _dtMethod, _dtStmt, false);
//			
//		}	
		return _result;
	}

	/**
	 * Checks if the given <code>wait()</code> call-site is dependent on the <code>notifyXX()</code> call-site according
	 * to rule 2. The dependence is determined based on the relation between the classes immediately enclosing the given
	 * statements occur.
	 * 
	 * @param wPair is the statement in which <code>java.lang.Object.wait()</code> is invoked.
	 * @param nPair is the statement in which <code>java.lang.Object.notifyXX()</code> is invoked.
	 * @return <code>true</code> if there is a dependence; <code>false</code>, otherwise.
	 * @pre wPair.getFirst() != null and nPair.getFirst() != null
	 */
	protected boolean ifDependentOnByRule4(final Pair<InvokeStmt, SootMethod> wPair, final Pair<InvokeStmt, SootMethod> nPair) {
		try
		{
			final InvokeStmt _notify = nPair.getFirst();
			final SootClass _notifyClass = env.getClass(((RefType) ((InstanceInvokeExpr) _notify.getInvokeExpr()).getBase()
					.getType()).getClassName());
			final InvokeStmt _wait = wPair.getFirst();
			final SootClass _waitClass = env
					.getClass(((RefType) ((InstanceInvokeExpr) _wait.getInvokeExpr()).getBase().getType()).getClassName());
			boolean _result = Util.isHierarchicallyRelated(_notifyClass, _waitClass);
	
			if (_result && useOFA) {
				_result = ifDependentOnBasedOnOFAByRule4(wPair, nPair);
			}
			return _result;
		}
		catch (Exception _e)
		{
			//System.out.println("Exception "+_e);
			return false;
		}
	}

	/**
	 * Extracts information provided by environment at initialization time.
	 * 
	 * @throws InitializationException when call graph info, pair managing service, or environment is not available in
	 *             <code>info</code> member.
	 * @pre info.get(IEnvironment.ID) != null and info.get(ICallGraphInfo.ID) != null
	 * @pre info.get(IThreadGraphInfo.ID) != null and info.get(PairManager.ID) != null
	 * @pre useOFA implies info.get(IValueAnalyzer.ID) != null and info.get(IValueAnalyzer.ID).oclIsKindOf(OFAnalyzer)
	 * @pre info.get(IMonitorInfo.ID) != null
	 * @pre useSafeLockAnalysis implies info.get(SafeLockAnalysis.ID) != null
	 */
	@Override protected void setup() throws InitializationException {
		super.setup();

		env = (IEnvironment) info.get(IEnvironment.ID);

		if (env == null) {
			throw new InitializationException(IEnvironment.ID + " was not provided in info.");
		}

		callgraph = (ICallGraphInfo) info.get(ICallGraphInfo.ID);

		if (callgraph == null) {
			throw new InitializationException(ICallGraphInfo.ID + " was not provided in info.");
		}
		threadgraph = (IThreadGraphInfo) info.get(IThreadGraphInfo.ID);

		if (threadgraph == null) {
			throw new InitializationException(IThreadGraphInfo.ID + " was not provided in info.");
		}
		pairMgr = (PairManager) info.get(PairManager.ID);

		if (pairMgr == null) {
			throw new InitializationException(PairManager.ID + " was not provided in info.");
		}

		ofa = (OFAnalyzer) info.get(IValueAnalyzer.ID);

		if (useOFA && ofa == null) {
			throw new InitializationException(IValueAnalyzer.ID + " was not provided in the info.");
		}

		monitorInfo = (IMonitorInfo) info.get(IMonitorInfo.ID);

		if (monitorInfo == null) {
			final String _msg = "An interface with id, " + IMonitorInfo.ID + ", was not provided.";
			LOGGER.error(_msg);
			throw new InitializationException(_msg);
		}

		safelockAnalysis = (SafeLockAnalysis) info.get(SafeLockAnalysis.ID);

		if (useSafeLockAnalysis && safelockAnalysis == null) {
			final String _msg = "An interface with id, " + SafeLockAnalysis.ID + ", was not provided.";
			LOGGER.error(_msg);
			throw new InitializationException(_msg);
		}
	}

	/**
	 * Checks if the given stmt contains a call-site. If so, it checks if it results in the invocation of a method
	 * (ready-method) that has atleast an enter-monitor statement or a <code>wait()</code> call-site.
	 * 
	 * @param stmt that could result in the invocation of ready-method via a call-chain.
	 * @param caller in which <code>stmt</code> occurs.
	 * @return <code>true</code> if <code>stmt</code> results in the invocation of a ready-method via a call-chain;
	 *         <code>false</code>, otherwise.
	 * @pre stmt != null and caller != null and stmt.containsInvokeExpr() == true
	 */
	private boolean callsReadyMethod(final Stmt stmt, final SootMethod caller) {
		final boolean _result;

		if (considerCallSites && stmt.containsInvokeExpr()) {
			_result = callgraph.areAnyMethodsReachableFrom(readyMethods, stmt, caller);
		} else {
			_result = false;
		}
		return _result;
	}

	/**
	 * Collects the dependees in each method.
	 * 
	 * @return a collection of dependees in each method.
	 * @post result != null
	 */
	private Map<SootMethod, Collection<Stmt>> collectDependeesInMethods() {
		final Map<SootMethod, Collection<Stmt>> _method2dependeeMap = new HashMap<SootMethod, Collection<Stmt>>();

		if ((rules & RULE_1) != 0) {
			for (final Iterator<SootMethod> _i = enterMonitors.keySet().iterator(); _i.hasNext();) {
				final SootMethod _method = _i.next();

				// if the method is not concrete then there can be no intra-procedural ready dependence. So, don't bother.
				if (_method.isConcrete()) {
					final Collection<EnterMonitorStmt> _enterMonitorStmts = enterMonitors.get(_method);
					final Collection<Stmt> _col = new HashSet<Stmt>();
					final Iterator<EnterMonitorStmt> _j = _enterMonitorStmts.iterator();
					final int _jEnd = _enterMonitorStmts.size();

					for (int _jIndex = 0; _jIndex < _jEnd; _jIndex++) {
						final EnterMonitorStmt _enter = _j.next();
						final boolean _unsafe = isLockUnsafe(_enter, _method);

						if (_unsafe) {
							_col.add(_enter);
						}
					}
					_method2dependeeMap.put(_method, _col);
				}
			}
		}

		if ((rules & RULE_3) != 0) {
			for (final Iterator<SootMethod> _i = waits.keySet().iterator(); _i.hasNext();) {
				final SootMethod _method = _i.next();
				MapUtils.putAllIntoCollectionInMap(_method2dependeeMap, _method, waits.get(_method));
			}
		}
		return _method2dependeeMap;
	}

	/**
	 * A helper method to retrieve dependence information.
	 * 
	 * @param stmt of interest.
	 * @param method containing <code>stmt</code>.
	 * @param map containing dependence information.
	 * @return a collection of dependence entities.
	 * @pre stmt != null and method != null and map != null
	 */
	private Collection<Pair<Stmt, SootMethod>> getDependenceHelper(final Stmt stmt, final SootMethod method,
			final Map<SootMethod, Map<Stmt, Collection<Pair<Stmt, SootMethod>>>> map) {
		final Map<Stmt, Collection<Pair<Stmt, SootMethod>>> _temp = map.get(method);
		Collection<Pair<Stmt, SootMethod>> _result = Collections.emptyList();

		if (_temp != null && _temp.containsKey(stmt)) {
			_result = Collections.unmodifiableCollection(_temp.get(stmt));
		}
		return _result;
	}

	/**
	 * Retrieves pairs of exitmonitor statements and the methods that containing the statement.
	 * 
	 * @return a collection of pairs.
	 * @post result != null and result.oclIsKindOf(Collection(Pair(ExitMonitorStmt, SootMethod)))
	 */
	private Collection<Pair<ExitMonitorStmt, SootMethod>> getExitMonitorStmtMethodPairs() {
		final Collection<Pair<ExitMonitorStmt, SootMethod>> _temp = new HashSet<Pair<ExitMonitorStmt, SootMethod>>();

		for (final Iterator<Map.Entry<SootMethod, Collection<ExitMonitorStmt>>> _i = exitMonitors.entrySet().iterator(); _i
				.hasNext();) {
			final Map.Entry<SootMethod, Collection<ExitMonitorStmt>> _entry = _i.next();
			final SootMethod _method = _entry.getKey();

			for (final Iterator<ExitMonitorStmt> _j = _entry.getValue().iterator(); _j.hasNext();) {
				final ExitMonitorStmt _o = _j.next();
				_temp.add(pairMgr.getPair(_o, _method));
			}
		}
		return _temp;
	}

	/**
	 * Checks if the given enter monitor statement/synchronized method is dependent on the exit monitor statement/synchronized
	 * method according to rule 2 based on OFA.
	 * 
	 * @param enterPair is the enter monitor statement and containg method pair.
	 * @param exitPair is the exit monitor statement and containg method pair.
	 * @return <code>true</code> if there is a dependence; <code>false</code>, otherwise.
	 * @pre enterPair.getSecond() != null and exitPair.getSecond() != null
	 * @see ReadyDAv2#ifDependentOnByRule2(Pair, Pair)
	 */
	private boolean ifDependentOnBasedOnOFAByRule2(final Pair<EnterMonitorStmt, SootMethod> enterPair,
			final Pair<ExitMonitorStmt, SootMethod> exitPair) {
		boolean _result;

		final SootMethod _enterMethod = enterPair.getSecond();
		final SootMethod _exitMethod = exitPair.getSecond();
		final EnterMonitorStmt _enterStmt = enterPair.getFirst();
		final ExitMonitorStmt _exitStmt = exitPair.getFirst();
		Collection<?> _col1;
		Collection<?> _col2;
		boolean _syncedStaticMethod1 = false;
		boolean _syncedStaticMethod2 = false;
		final Context _context = new Context();

		if (_enterStmt == null) {
			_syncedStaticMethod1 = _enterMethod.isStatic();

			if (!_syncedStaticMethod1) {
				_context.setRootMethod(_enterMethod);
				_col1 = ofa.getValuesForThis(_context);
			} else {
				_col1 = Collections.emptyList();
			}
		} else {
			_context.setProgramPoint(_enterStmt.getOpBox());
			_context.setStmt(_enterStmt);
			_context.setRootMethod(_enterMethod);
			_col1 = ofa.getValues(_enterStmt.getOp(), _context);
		}

		if (_exitStmt == null) {
			_syncedStaticMethod2 = _exitMethod.isStatic();

			if (!_syncedStaticMethod2) {
				_context.setProgramPoint(null);
				_context.setStmt(null);
				_context.setRootMethod(_exitMethod);
				_col2 = ofa.getValuesForThis(_context);
			} else {
				_col2 = Collections.emptyList();
			}
		} else {
			_context.setProgramPoint(_exitStmt.getOpBox());
			_context.setStmt(_exitStmt);
			_context.setRootMethod(_exitMethod);
			_col2 = ofa.getValues(_exitStmt.getOp(), _context);
		}

		if (_syncedStaticMethod1 ^ _syncedStaticMethod2) {
			/*
			 * if only one of the methods is static and synchronized then we cannot determine RDA as it is possible that the
			 * monitor in the non-static method may actually be on the class object of the class in which the static method is
			 * defined. There are many combinations which can be pruned. No time now. THINK
			 */
			_result = true;
		} else {
			final NullConstant _n = NullConstant.v();
			final Collection<?> _temp = SetUtils.intersection(_col1, _col2);

			while (_temp.remove(_n)) {
				// does nothing
			}
			_result = !_temp.isEmpty();
		}
//		if (_result) 
//		{	
//			String strDe=enterPair.getSecond().toString();
//			String strDt=exitPair.getSecond().toString();
//			//System.out.println("InterferenceDAv1() strDe: "+strDe+" strDe.startsWith(\"<java.\")"+strDe.startsWith("<java.")+" strDe.startsWith(\"<sun.\")"+strDe.startsWith("<sun."));
//			//System.out.println("InterferenceDAv1() strDt: "+strDe+" strDt.startsWith(\"<java.\")"+strDt.startsWith("<java.")+" strDt.startsWith(\"<sun.\")"+strDt.startsWith("<sun."));
//
//				Stmt _deStmt = enterPair.getFirst();
//				Stmt _dtStmt = exitPair.getFirst();
//				if (_col1!=null && _deStmt!=null && _col2!=null && _dtStmt!=null)
//				{	
//					Variable _vde=vtg.makeVariable((Value) _col1,_deStmt);
//					Variable _vdt=vtg.makeVariable((Value) _col2,_dtStmt);
//					if ((!strDe.startsWith("<java.") && !strDe.startsWith("<sun.")) || (!strDt.startsWith("<java.") && !strDt.startsWith("<sun.")))
//					{	
//					   System.out.println("ReadyDAv1() edge: "+_vde+", "+strDe+", "+_deStmt+" --> "+", "+_vdt+", "+strDt+", "+_dtStmt);
//					}
//					vtg.createTransferEdge(_vde, (SootMethod) enterPair.getSecond(), _deStmt, _vdt, (SootMethod) exitPair.getSecond(), _dtStmt, VTEType.VTE_ENTEREXIT,false);
//					System.out.println("ReadyDAv1() adds ENTEREXIT edge: "+vtg);
//				}
//			//createTransferEdge(_de, _deMethod, _deStmt, _dt, _dtMethod, _dtStmt, false);
//			
//		}
		return _result;
	}

	/**
	 * This checks if the lock associated the given monitor is unsafe.
	 * 
	 * @param monitorStmt obviously.
	 * @param method in which <code>monitorStmt</code> occurs.
	 * @return <code>true</code> if the lock is unsafe; <code>false</code>, otherwise.
	 * @pre monitorStmt != null and method != null
	 */
	private boolean isLockUnsafe(final MonitorStmt monitorStmt, final SootMethod method) {
		boolean _result = true;

		if (useSafeLockAnalysis) {
			if (monitorStmt == null) {
				_result = !safelockAnalysis.isLockSafe(method);
			} else {
				_result = !safelockAnalysis.isLockSafe(monitorStmt, method);
			}
		}
		return _result;
	}

	/**
	 * Process monitor info.
	 * 
	 * @return <code>true</code> if the system has any synchronization in it; <code>false</code>, otherwise.
	 */
	private boolean processMonitorInfo() {
		boolean _result = false;
		final Collection<SootMethod> _reachableMethods = callgraph.getReachableMethods();
		final Iterator<SootMethod> _i = _reachableMethods.iterator();
		final int _iEnd = _reachableMethods.size();

		for (int _iIndex = 0; _iIndex < _iEnd; _iIndex++) {
			final SootMethod _method = _i.next();
			final Collection<Triple<EnterMonitorStmt, ExitMonitorStmt, SootMethod>> _monitorTriplesIn = monitorInfo
					.getMonitorTriplesIn(_method);
			final Iterator<Triple<EnterMonitorStmt, ExitMonitorStmt, SootMethod>> _j = _monitorTriplesIn.iterator();
			final int _jEnd = _monitorTriplesIn.size();

			for (int _jIndex = 0; _jIndex < _jEnd; _jIndex++) {
				final Triple<EnterMonitorStmt, ExitMonitorStmt, SootMethod> _monitor = _j.next();
				final EnterMonitorStmt _enter = _monitor.getFirst();

				if (_enter == null) {
					MapUtils.putIntoCollectionInMap(enterMonitors, _method, (EnterMonitorStmt) null);
					MapUtils.putIntoCollectionInMap(exitMonitors, _method, (ExitMonitorStmt) null);
				} else {
					MapUtils.putIntoCollectionInMap(enterMonitors, _method, _enter);
					MapUtils.putIntoCollectionInMap(exitMonitors, _method, _monitor.getSecond());
				}
			}

			if (_jEnd > 0) {
				_result = true;
				readyMethods.add(_method);
			}
		}
		return _result;
		
		
	}

	/**
	 * Processes the system as per to rule 1 and rule 3 as discussed in the report. For each <code>Object.wait</code> call
	 * site or synchronized block in a method, the dependency is calculated for all dominated statements in the same method.
	 */
	private void processRule1And3() {
		final Collection<BasicBlock> _processed = new HashSet<BasicBlock>();
		final IWorkBag<BasicBlock> _workbag = new HistoryAwareLIFOWorkBag<BasicBlock>(_processed);
		final Map<SootMethod, Collection<Stmt>> _method2dependeeMap = collectDependeesInMethods();

		if ((waits.size() == 0 ^ notifies.size() == 0) && LOGGER.isWarnEnabled()) {
			LOGGER.warn("There are wait()s and/or notify()s in this program without corresponding notify()s and/or "
					+ "wait()s that occur in different threads.");
		}

		final Collection<Pair<Stmt, SootMethod>> _dependents = new HashSet<Pair<Stmt, SootMethod>>();
		

		Map<SootMethod, Collection<Stmt>> myMethod2EEStmt = new HashMap<SootMethod, Collection<Stmt>>();
		Collection<Triple<EnterMonitorStmt, ExitMonitorStmt, SootMethod>> myEE2Methods = new HashSet<Triple<EnterMonitorStmt, ExitMonitorStmt, SootMethod>>();
		SootClass THREAD_CLASS = Scene.v().getSootClass("java.lang.Thread"); 
		FastHierarchy har = new FastHierarchy();
		for (SootClass sClass:Scene.v().getApplicationClasses()) 
		{
			if (!har.isSubclass(sClass, THREAD_CLASS) && !sClass.implementsInterface("java.lang.Runnable"))
				continue;
			if ( sClass.isPhantom() ) {
				// skip phantom classes
				continue;
			}
			for(SootMethod m:sClass.getMethods())
			{
				try
				{
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
		
					Collection<Stmt> stmtInMethod= new HashSet<Stmt>();
					Collection<InvokeStmt> waitStmtInMethod= new HashSet<InvokeStmt>();
					Collection<InvokeStmt> notifyStmtInMethod= new HashSet<InvokeStmt>();				
					boolean hasEnter=false;
					boolean hasExit=false;
					EnterMonitorStmt myEnter=null;
					ExitMonitorStmt myExit=null;
					Collection<Stmt> myStmtInEnterExit= new HashSet<Stmt>();
					Iterator<Unit> units=m.retrieveActiveBody().getUnits().iterator();				
					while(units.hasNext())
					{
						try
						{
							Stmt st = (Stmt) units.next();
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
							}
							else if (st instanceof EnterMonitorStmt)
							{
								hasEnter=true;
								myEnter= (EnterMonitorStmt) st;
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
					_method2dependeeMap.put(m, stmtInMethod);
					if (hasEnter && hasExit)
					{
						myMethod2EEStmt.put(m, myStmtInEnterExit);	
						Triple<EnterMonitorStmt, ExitMonitorStmt, SootMethod> myEE2Method = new Triple<EnterMonitorStmt, ExitMonitorStmt, SootMethod>(myEnter, myExit, m);
						myEE2Methods.add(myEE2Method);
					}	
				}
	            catch (Exception _e)
	            {
	            	//System.out.println("Exception: "+_e);
	            }	
			}
		}	
		

	}

	/**
	 * Processes the system as per to rule 2 in the report. For each possible enter- and exit-monitor statements occurring in
	 * different threads, the combination of these to be considered is determined by <code>ifRelatedByRule2()</code>.
	 */
	private void processRule2() {
	
		final Collection<Pair<Stmt, SootMethod>> _dependents = new HashSet<Pair<Stmt, SootMethod>>();
		final Collection<Pair<ExitMonitorStmt, SootMethod>> _temp = getExitMonitorStmtMethodPairs();
		final Iterator<Pair<ExitMonitorStmt, SootMethod>> _i = _temp.iterator();
		final int _iEnd = _temp.size();

		String showed="";		
		SootClass THREAD_CLASS = Scene.v().getSootClass("java.lang.Thread"); 
		FastHierarchy har = new FastHierarchy();
		for (SootClass sClass:Scene.v().getApplicationClasses()) 
		{
			//System.out.println("**************************sClass: "+sClass);
			if (!har.isSubclass(sClass, THREAD_CLASS) && !sClass.implementsInterface("java.lang.Runnable"))
				continue;
			if ( sClass.isPhantom() ) {
				// skip phantom classes
				continue;
			}
			for(SootMethod m:sClass.getMethods())
			{
				//System.out.println("---------- method= "+m);
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
				try
				{
					Iterator<Unit> units=m.retrieveActiveBody().getUnits().iterator();
					//System.out.println("units="+units);
					while(units.hasNext())
					{
						try
						{
							Stmt st =(Stmt) units.next();
							//System.out.println("st="+st);
							if(st instanceof EnterMonitorStmt ) 
							{
								//System.out.println("EnterMonitorStmt st="+st);
								for (SootClass sClass2:Scene.v().getApplicationClasses()) 
								{ 
									if ((!har.isSubclass(sClass2, THREAD_CLASS) && !sClass2.implementsInterface("java.lang.Runnable")) || sClass2.equals(sClass))
										continue;		
									if ( sClass2.isPhantom() ) {
										// skip phantom classes
										continue;
									}
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
										Iterator<Unit> units2=m2.retrieveActiveBody().getUnits().iterator();
										while(units2.hasNext())
										{
										try
										{
											Stmt st2 =(Stmt) units2.next();
											//System.out.println("	st2="+st2);
		//									if (st2.equals(st))
		//										continue;
											if(st2 instanceof ExitMonitorStmt ) 
											{
												//System.out.println("ExitMonitorStmt st2="+st2);
												Pair<EnterMonitorStmt, SootMethod> _waitPair = new Pair<EnterMonitorStmt, SootMethod>((EnterMonitorStmt)st, m);
												Pair<ExitMonitorStmt, SootMethod> _notifyPair = new Pair<ExitMonitorStmt, SootMethod>((ExitMonitorStmt)st2, m2);
												//System.out.println("	_de  _dt");
												//System.out.println(" _de="+_de+" _dt="+_dt);
												if (ifDependentOnByRule2(_waitPair, _notifyPair))
												{
													if (!showed.contains("ReadyDAv1() EnterExitMonitor Dependence: "+m+" - "+st+" --> "+m2+" - "+st2) && 
															!showed.contains("ReadyDAv1() EnterExitMonitor Dependence: "+m+" - "+st+" <-- "+m2+" - "+st2) && 
															!showed.contains("ReadyDAv1() EnterExitMonitor Dependence: "+m2+" - "+st2+" --> "+m+" - "+st) && 
															!showed.contains("ReadyDAv1() EnterExitMonitor Dependence: "+m2+" - "+st2+" <-- "+m+" - "+st))
													{	
														System.out.println("ReadyDAv1() EnterExitMonitor Dependence: "+m+" - "+st+" --> "+m2+" - "+st2);
														showed+="ReadyDAv1() EnterExitMonitor Dependence: "+m+" - "+st+" --> "+m2+" - "+st2;
														Variable _deV=vtg.makeVariable(soot.jimple.StringConstant.v("Enter-Exit-Monitor"),st);
														Variable _dtV=vtg.makeVariable(soot.jimple.StringConstant.v("Enter-Exit-Monitor"),st2);
														vtg.createTransferEdge(_deV, m, st, _dtV, m2, st2, VTEType.VTE_CONTROL_INTER, false);
														
													}
													//else
														
												}  //if
												
											}  //if
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
						
						}
			            catch (Exception _e)
			            {
			            	System.out.println("Exception: "+_e);
			            }		
					}			//while
				}
	            catch (Exception _e)
	            {
	            	System.out.println("Exception: "+_e);
	            }					
				
			}
		}		
	}

	/**
	 * Processes the system as per to rule 4 in the report. For each possible wait and notifyXX call-sites in different
	 * threads, the combination of these to be considered is determined by <code>ifRelatedByRule4()</code>.
	 */
	private void processRule4() {
		final Collection<Pair<Stmt, SootMethod>> _dependents = new HashSet<Pair<Stmt, SootMethod>>();

		SootClass THREAD_CLASS = Scene.v().getSootClass("java.lang.Thread"); 
		FastHierarchy har = new FastHierarchy();
		String showed="";   // remove repeated dependency edges
		for (SootClass sClass:Scene.v().getApplicationClasses()) 
		{
			//System.out.println("**************************sClass: "+sClass);
			if (!har.isSubclass(sClass, THREAD_CLASS) && !sClass.implementsInterface("java.lang.Runnable"))
				continue;
			if ( sClass.isPhantom() ) {
				// skip phantom classes
				continue;
			}
			for(SootMethod m:sClass.getMethods())
			{
				//System.out.println("---------- method= "+m);
//				if ( !m.hasActiveBody() ) {
//					continue;
//				}
				try
				{
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
					Iterator<Unit> units=m.retrieveActiveBody().getUnits().iterator();
					//System.out.println("units="+units);
					while(units.hasNext())
					{
						Stmt st =(Stmt) units.next();
						//System.out.println("st="+st);
						if(st instanceof InvokeStmt)      // || st.containsInvokeExpr()
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
									Iterator<Unit> units2=m2.retrieveActiveBody().getUnits().iterator();
									while(units2.hasNext())
									{
									try
									{
										Stmt st2 =(Stmt) units2.next();
										//System.out.println("	st2="+st2);
	//									if (st2.equals(st))
	//										continue;
										if(st2 instanceof InvokeStmt)   //|| st2.containsInvokeExpr()
										{
											//System.out.println("	InvokeStmt  st2="+st2);
											Pair<InvokeStmt, SootMethod> _waitPair = pairMgr.getPair((InvokeStmt)st, m);
											Pair<InvokeStmt, SootMethod> _notifyPair = pairMgr.getPair((InvokeStmt)st2, m2);
											//System.out.println("	_de  _dt");
											//System.out.println(" _de="+_de+" _dt="+_dt);
											if (ifDependentOnByRule4(_waitPair, _notifyPair))
											{
												if (!showed.contains("ReadyDAv1() WaitNotify Dependence: "+m+" - "+st+" --> "+m2+" - "+st2) && 
												!showed.contains("ReadyDAv1() WaitNotify Dependence: "+m+" - "+st+" <-- "+m2+" - "+st2) && 
												!showed.contains("ReadyDAv1() WaitNotify Dependence: "+m2+" - "+st2+" --> "+m+" - "+st) && 
												!showed.contains("ReadyDAv1() WaitNotify Dependence: "+m2+" - "+st2+" <-- "+m+" - "+st))
												{
													System.out.println("ReadyDAv1() WaitNotify Dependence: "+m+" - "+st+" --> "+m2+" - "+st2);
													showed+="ReadyDAv1() WaitNotify Dependence: "+m+" - "+st+" --> "+m2+" - "+st2;
													Variable _deV=vtg.makeVariable(soot.jimple.StringConstant.v("wait-notify"), st);
													Variable _dtV=vtg.makeVariable(soot.jimple.StringConstant.v("wait-notify"), st2);
													//System.out.println("vtg before ReadyDAv1() WaitNotify Dependence");
													//System.out.println(vtg);
													vtg.createTransferEdge(_deV, m, st, _dtV, m2, st2, VTEType.VTE_CONTROL_INTER, false);
													//System.out.println("vtg after ReadyDAv1() WaitNotify Dependence");
													//System.out.println(vtg);
												}
													
											}
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
	
					}		//while
					
				}
	            catch (Exception _e)
	            {
	            	System.out.println("Exception: "+_e);
	            }	
			}
		}				
		
	}

	/**
	 * Processes various rules during analysis.
	 */
	private void processRules() {
		final boolean _syncExists = processMonitorInfo();

		if (_syncExists) {
			if ((rules & (RULE_1 | RULE_3)) != 0) {
				processRule1And3();
			}

			if ((rules & RULE_2) != 0) {
				processRule2();
			}
		}

		if ((rules & RULE_4) != 0 && (!waits.isEmpty() && !notifies.isEmpty())) {
			if (!_syncExists && LOGGER.isWarnEnabled()) {
				LOGGER.warn("All waits and in this system are unsafe - they are not embedded in a synchronized block.");
			}
			processRule4();
		}
	}

	/**
	 * Records dependent to dependee information while capturing the dependents for recording information in the other
	 * direction.
	 * 
	 * @param dependents is the collection of dependents. This is an <code>out</code> parameter.
	 * @param method in which the dependees occur.
	 * @param dependees is the collection of dependees.
	 * @param dents2dees is the map which needs to be updated.
	 * @param stmts is the collection of statements to be processed for dependent status.
	 * @param pair is the dependee for which current execution is recording dependence.
	 * @return <code>true</code> if the following basic blocks of the basic block in which <code>stmts</code> occur should
	 *         be considered for dependence; <code>false</code>, otherwise.
	 * @pre dependents != null
	 * @pre method != null and pair != null
	 * @pre dependees != null
	 * @pre stmts != null
	 * @pre dents2dees != null
	 */
	private boolean recordDependent2DependeeInfo(final Collection<Pair<Stmt, SootMethod>> dependents,
			final SootMethod method, final Collection<Stmt> dependees,
			final Map<Stmt, Collection<Pair<Stmt, SootMethod>>> dents2dees, final Collection<Stmt> stmts,
			final Pair<Stmt, SootMethod> pair) {
		boolean _shouldContinue;
		_shouldContinue = true;

		// add dependent to dependee direction information.
		for (final Iterator<Stmt> _k = stmts.iterator(); _k.hasNext() && _shouldContinue;) {
			final Stmt _stmt = _k.next();

			MapUtils.putIntoCollectionInMap(dents2dees, _stmt, pair);

			// record dependee to dependent direction information
			dependents.add(pairMgr.getPair(_stmt, method));

			/*
			 * In case there is a statement that is a wait() call-site, enter-monitor, or a ready-method call-site, flag that
			 * the following successors should not be considered for this dependence and break.
			 */
			_shouldContinue = !(dependees.contains(_stmt) || callsReadyMethod(_stmt, method));
		}
		return _shouldContinue;
	}

	/**
	 * Checks if the monitors are type compatible.
	 * 
	 * @param enter is an enter monitor statement.
	 * @param enterMethod is the method in which <code>enter</code> occurs.
	 * @param exit is an exit monitor statement.
	 * @param exitMethod is the method in which <code>exit</code> occurs.
	 * @return <code>true</code> if they are compatible; <code>false</code>, otherwise.
	 */
	private boolean typesAreCompatible(final EnterMonitorStmt enter, final SootMethod enterMethod,
			final ExitMonitorStmt exit, final SootMethod exitMethod) {
		final boolean _result;
		if (enter == null) { // synchronized enter method
			final SootClass _nClass = enterMethod.getDeclaringClass();
			if (enterMethod.isStatic()) { // static enter method
				if (exit == null) { // synchronized method
					final SootClass _xClass = exitMethod.getDeclaringClass();
					if (exitMethod.isStatic()) { // static enter method
						_result = _xClass.equals(_nClass);
					} else { // instance enter method
						_result = Util.isDescendentOf(_xClass, "java.lang.Class");
					}
				} else { // synchronized block
					final Type _xType = exit.getOp().getType();
					if (_xType instanceof RefType) {
						final SootClass _xClass = ((RefType) _xType).getSootClass();
						_result = Util.isDescendentOf(_xClass, "java.lang.Class");
					} else { // _xType is an ArrayType
						_result = false;
					}
				}
			} else { // instance enter method
				if (exit == null) { // synchronized exit method
					final SootClass _xClass = exitMethod.getDeclaringClass();
					if (exitMethod.isStatic()) { // static exit method
						_result = false;
					} else { // instance exit method
						_result = Util.isHierarchicallyRelated(_nClass, _xClass);
					}
				} else { // synchronized exit block
					final Type _xType = exit.getOp().getType();
					if (_xType instanceof RefType) { // synchronization on non-array type
						final SootClass _xClass = ((RefType) _xType).getSootClass();
						_result = Util.isHierarchicallyRelated(_nClass, _xClass);
					} else { // synchronization on array type
						_result = _nClass.getName().equals("java.lang.Object");
					}
				}
			}
		} else { // synchronized enter block
			final Type _nType = enter.getOp().getType();
			if (_nType instanceof RefType) { // synchronization on non-array type
				final SootClass _nClass = enterMethod.getDeclaringClass();
				if (exit == null) { // synchronized exit method
					final SootClass _xClass = exitMethod.getDeclaringClass();
					if (exitMethod.isStatic()) { // static exit method
						_result = Util.isDescendentOf(_nClass, "java.lang.Class");
					} else { // instance exit method
						_result = Util.isDescendentOf(_xClass, _nClass);
					}
				} else { // synchronized exit block
					final Type _xType = exit.getOp().getType();
					if (_xType instanceof RefType) { // synchronization on non-array type
						final SootClass _xClass = ((RefType) _xType).getSootClass();
						_result = Util.isDescendentOf(_xClass, _nClass);
					} else { // synchronization on array type
						_result = _nClass.getName().equals("java.lang.Object");
					}
				}
			} else { // synchronization on array type
				if (exit == null) { // synchronized exit method
					final SootClass _xClass = exitMethod.getDeclaringClass();
					if (exitMethod.isStatic()) { // static exit method
						_result = false;
					} else { // instance exit method
						_result = _xClass.getName().equals("java.lang.Object");
					}
				} else { // synchronized exit block
					final Type _xType = exit.getOp().getType();
					if (_xType instanceof RefType) { // synchronization on non-array type
						final SootClass _xClass = ((RefType) _xType).getSootClass();
						_result = _xClass.getName().equals("java.lang.Object");
					} else { // synchronization on array type
						_result = _xType.equals(_nType);
					}
				}
			}
		}
		return _result;
	}
}

// End of File
