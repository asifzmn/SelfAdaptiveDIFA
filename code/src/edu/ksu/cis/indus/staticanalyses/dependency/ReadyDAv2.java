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

import edu.ksu.cis.indus.common.datastructures.Pair;
import edu.ksu.cis.indus.interfaces.IEscapeInfo;

import edu.ksu.cis.indus.staticanalyses.InitializationException;
import edu.ksu.cis.indus.staticanalyses.dependency.direction.BackwardDirectionSensitiveInfo;
import edu.ksu.cis.indus.staticanalyses.dependency.direction.ForwardDirectionSensitiveInfo;
import edu.ksu.cis.indus.staticanalyses.dependency.direction.IDirectionSensitiveInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import disttaint.StaticTransferGraph;
import MciaUtil.VTEdge.VTEType;
import dua.method.CFGDefUses.Variable;
import soot.SootMethod;
import soot.Value;

import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Stmt;
import soot.jimple.VirtualInvokeExpr;

/**
 * This class uses escape-analysis as calculated by <code>EquivalenceClassBasedEscapeAnalysis</code> to prune the ready
 * dependency information calculated by it's parent class. This class will also use OFA information if it is configured to do
 * so.
 * 
 * @author <a href="http://www.cis.ksu.edu/~rvprasad">Venkatesh Prasad Ranganath</a>
 * @author $Author: rvprasad $
 * @version $Revision: 1.30 $
 * @see edu.ksu.cis.indus.staticanalyses.dependency.ReadyDAv1
 */
public class ReadyDAv2
		extends ReadyDAv1 {
	//StaticTransferGraph vtg;
	/**
	 * The logger used by instances of this class to log messages.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ReadyDAv2.class);

	/**
	 * This provides information to prune ready dependence edges.
	 */
	protected IEscapeInfo ecba;

	/**
	 * Creates an instance of this class.
	 * 
	 * @param directionSensitiveInfo that controls the direction.
	 * @param direction of the analysis
	 * @pre info != null and direction != null
	 */
	protected ReadyDAv2(final IDirectionSensitiveInfo directionSensitiveInfo, final Direction direction) {
		super(directionSensitiveInfo, direction);
	}
	protected ReadyDAv2(final IDirectionSensitiveInfo directionSensitiveInfo, final Direction direction, StaticTransferGraph vtg) {
		super(directionSensitiveInfo, direction);
		this.vtg=vtg;
	}
	/**
	 * Retrieves an instance of ready dependence analysis that calculates information in backward direction.
	 * 
	 * @return an instance of ready dependence.
	 * @post result != null
	 */
	public static ReadyDAv1 getBackwardReadyDA() {
		return new ReadyDAv2(new BackwardDirectionSensitiveInfo(), Direction.BACKWARD_DIRECTION);
	}

	/**
	 * Retrieves an instance of ready dependence analysis that calculates information in forward direction.
	 * 
	 * @return an instance of ready dependence.
	 * @post result != null
	 */
	public static ReadyDAv1 getForwardReadyDA() {
		return new ReadyDAv2(new ForwardDirectionSensitiveInfo(), Direction.FORWARD_DIRECTION);
	}

	/**
	 * @see edu.ksu.cis.indus.staticanalyses.interfaces.AbstractAnalysis#analyze()
	 */
	@Override public void analyze() {
		unstable();

		if (ecba.isStable()) {
			super.analyze();
		}
	}

	/**
	 * Checks if the given enter monitor statement/synchronized method is dependent on the exit monitor statement/synchronized
	 * method according to rule 2. The results of escape analysis info calculated {@link
	 * edu.ksu.cis.indus.staticanalyses.concurrency.escape.EquivalenceClassBasedEscapeAnalysis
	 * EquivalenceClassBasedEscapeAnalysis} analysis is used to determine the dependence.
	 * 
	 * @param enterPair is the enter monitor statement and containg statement pair.
	 * @param exitPair is the exit monitor statement and containg statement pair.
	 * @return <code>true</code> if there is a dependence; <code>false</code>, otherwise.
	 * @pre enterPair.getSecond() != null and exitPair.getSecond() != null
	 * @see ReadyDAv1#ifDependentOnByRule2(Pair, Pair)
	 */
	@Override protected boolean ifDependentOnByRule2(final Pair<EnterMonitorStmt, SootMethod> enterPair,
			final Pair<ExitMonitorStmt, SootMethod> exitPair) {
		boolean _result = super.ifDependentOnByRule2(enterPair, exitPair);
		Value _enter = null;
		Value _exit = null;
		if (_result) {
			final SootMethod _enterMethod = enterPair.getSecond();
			final SootMethod _exitMethod = exitPair.getSecond();
			final EnterMonitorStmt _o1 = enterPair.getFirst();
			final ExitMonitorStmt _o2 = exitPair.getFirst();
			boolean _flag1;
			boolean _flag2;
			
			if (_o1 == null) {
				_flag1 = ecba.thisEscapes(_enterMethod);
			} else {
				_enter = _o1.getOp();
				_flag1 = ecba.escapes(_enter, _enterMethod);
			}

			if (_o2 == null) {
				_flag2 = ecba.thisEscapes(_exitMethod);
			} else {
				_exit = _o2.getOp();
				_flag2 = ecba.escapes(_exit, _exitMethod);
			}
			_result = _flag1 && _flag2;
			if (_result) 
			{	
				System.out.println("ReadyDAv2() EnterExitMonitor edge: "+", "+_enterMethod+", "+_o1+" --> "+", "+_exitMethod+", "+_o2);
				Variable _deV=vtg.makeVariable(soot.jimple.StringConstant.v("Enter-Exit-Monitor"),_o1);
				Variable _dtV=vtg.makeVariable(soot.jimple.StringConstant.v("Enter-Exit-Monitor"),_o2);
				vtg.createTransferEdge(_deV, _enterMethod, _o1, _dtV, _exitMethod, _o2, VTEType.VTE_CONTROL_INTRA, false);
				//vtg.createTransferEdge(null, _sm1, _deStmt, null, (SootMethod) exitPair.getSecond(), _dtStmt, VTEType.VTE_NOTIFYWAIT, false);
			}	
		}
		return _result;
	}

	/**
	 * Checks if the given <code>wait()</code> call-site is dependent on the <code>notifyXX()</code> call-site according
	 * to rule 2. The results of escape analysis info calculated by <code>EquivalenceClassbasedAnalysis</code>analysis is
	 * used to determine the dependence.
	 * 
	 * @param wPair is the statement in which <code>java.lang.Object.wait()</code> is invoked.
	 * @param nPair is the statement in which <code>java.lang.Object.notifyXX()</code> is invoked.
	 * @return <code>true</code> if there is a dependence; <code>false</code>, otherwise.
	 * @pre wPair.getSecond() != null and nPair.getSecond() != null
	 * @see ReadyDAv1#ifDependentOnByRule4(Pair, Pair)
	 */
	@Override protected boolean ifDependentOnByRule4(final Pair<InvokeStmt, SootMethod> wPair,
			final Pair<InvokeStmt, SootMethod> nPair) {
		boolean _result = super.ifDependentOnByRule4(wPair, nPair);

		if (_result) {
			final Value _notify = ((InstanceInvokeExpr) (nPair.getFirst()).getInvokeExpr()).getBase();
			final Value _wait = ((InstanceInvokeExpr) (wPair.getFirst()).getInvokeExpr()).getBase();
			final SootMethod _wMethod = wPair.getSecond();
			final SootMethod _nMethod = nPair.getSecond();
			_result = ecba.escapes(_notify, _nMethod) && ecba.escapes(_wait, _wMethod);
			if (_result) 
			{	
				Stmt _wStmt=wPair.getFirst();
				Stmt _nStmt=nPair.getFirst();
				System.out.println("ReadyDAv2() WaitNotify edge: "+_wMethod+" - "+_wStmt+" --> "+_nMethod+" - "+_nStmt);
				Variable _deV=vtg.makeVariable(soot.jimple.StringConstant.v("wait-notify"), _wStmt);
				Variable _dtV=vtg.makeVariable(soot.jimple.StringConstant.v("wait-notify"), _nStmt);
				vtg.createTransferEdge(_deV, _wMethod, _wStmt, _dtV, _nMethod, _nStmt, VTEType.VTE_CONTROL_INTER, false);
				//vtg.createTransferEdge(null, _sm1, _deStmt, null, (SootMethod) exitPair.getSecond(), _dtStmt, VTEType.VTE_NOTIFYWAIT, false);
			}	
		}
		return _result;
	}

	/**
	 * Extracts information as provided by environment at initialization time. It collects <code>wait</code> and
	 * <code>notifyXX</code> methods as represented in the AST system. It also extract call graph info, pair manaing
	 * service, and environment from the <code>info</code> member.
	 * 
	 * @throws InitializationException when call graph info, pair managing service, or environment is not available in
	 *             <code>info</code> member.
	 * @see edu.ksu.cis.indus.staticanalyses.interfaces.AbstractAnalysis#setup()
	 */
	@Override protected void setup() throws InitializationException {
		super.setup();

		ecba = (IEscapeInfo) info.get(IEscapeInfo.ID);

		if (ecba == null) {
			LOGGER.error(IEscapeInfo.ID + " was not provided in info.");
			throw new InitializationException(IEscapeInfo.ID + " was not provided in info.");
		}
	}
}

// End of File
