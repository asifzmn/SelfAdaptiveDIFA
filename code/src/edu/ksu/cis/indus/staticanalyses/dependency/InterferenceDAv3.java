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

import disttaint.StaticTransferGraph;
import MciaUtil.VTEdge.VTEType;
import dua.method.CFGDefUses.Variable;
import edu.ksu.cis.indus.common.datastructures.Pair;

import edu.ksu.cis.indus.interfaces.IEscapeInfo;

import edu.ksu.cis.indus.staticanalyses.InitializationException;

import soot.SootField;
import soot.SootMethod;
import soot.Value;

import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;

/**
 * This class uses symbolic- and escape-analysis information as calculated by {@link
 * edu.ksu.cis.indus.staticanalyses.concurrency.escape.EquivalenceClassBasedEscapeAnalysis
 * EquivalenceClassBasedEscapeAnalysis} to prune the interference dependence edges as calculated by it's parent class. This
 * can be further spruced by symbolic-analysis.
 * 
 * @author <a href="http://www.cis.ksu.edu/~rvprasad">Venkatesh Prasad Ranganath</a>
 * @author $Author: rvprasad $
 * @version $Revision: 1.23 $
 * @see InterferenceDAv1
 */
public class InterferenceDAv3
		extends InterferenceDAv2 {
	//StaticTransferGraph vtg;
	/**
	 * Creates an instance of this class.
	 */
	public InterferenceDAv3() {
		super();
	}
	
	public InterferenceDAv3(StaticTransferGraph vtg) {
		super(vtg);
	}
	/**
	 * @see InterferenceDAv1#isArrayDependentOn(Pair, Pair, ArrayRef, ArrayRef)
	 */
	@Override protected boolean isArrayDependentOn(final Pair<AssignStmt, SootMethod> dependent,
			final Pair<AssignStmt, SootMethod> dependee, final ArrayRef dependentArrayRef, final ArrayRef dependeeArrayRef) {
		boolean _result = super.isArrayDependentOn(dependent, dependee, dependentArrayRef, dependeeArrayRef);

		if (_result) {
			final SootMethod _deMethod = dependee.getSecond();
			final SootMethod _dtMethod = dependent.getSecond();
			final Value _de = dependeeArrayRef.getBase();
			final Value _dt = dependentArrayRef.getBase();
			_result = ecba.fieldAccessShared(_de, _deMethod, _dt, _dtMethod, IEscapeInfo.READ_WRITE_SHARED_ACCESS);
			if (_result)
			{
				Stmt _deStmt=dependee.getFirst();
				Stmt _dtStmt=dependent.getFirst();
				Variable _vde=vtg.makeVariable(_de,_deStmt);
				Variable _vdt=vtg.makeVariable(_dt,_dtStmt);
				System.out.println("InterferenceDAv3() ArrayDependency edge: "+_vde+", "+_deMethod+" - "+_deStmt+" --> "+_vdt+", "+_dtMethod+" - "+_dtStmt);
				vtg.createTransferEdge(_vde, _deMethod, _deStmt, _vdt, _dtMethod, _dtStmt, VTEType.VTE_ARRAYELE, false);
			}
		}
		return _result;
	}

	/**
	 * @see InterferenceDAv1#isInstanceFieldDependentOn(Pair, Pair, InstanceFieldRef, InstanceFieldRef)
	 */
	@Override protected boolean isInstanceFieldDependentOn(final Pair<AssignStmt, SootMethod> dependent,
			final Pair<AssignStmt, SootMethod> dependee, final InstanceFieldRef dependentFieldRef,
			final InstanceFieldRef dependeeFieldRef) {
		boolean _result = super.isInstanceFieldDependentOn(dependent, dependee, dependentFieldRef, dependeeFieldRef);

		if (_result) {
			final SootMethod _deMethod = dependee.getSecond();
			final SootMethod _dtMethod = dependent.getSecond();
			final Value _de = dependeeFieldRef.getBase();
			final Value _dt = dependentFieldRef.getBase();
			if (_deMethod==null || _dtMethod==null || _de==null || _dt==null)
				return false;
			_result = ecba.fieldAccessShared(_de, _deMethod, _dt, _dtMethod, IEscapeInfo.READ_WRITE_SHARED_ACCESS);
			if (_result)
			{
				Stmt _deStmt=dependee.getFirst();
				Stmt _dtStmt=dependent.getFirst();
				Variable _vde=vtg.makeVariable(_de,_deStmt);
				Variable _vdt=vtg.makeVariable(_dt,_dtStmt);
				System.out.println("InterferenceDAv3() InstanceDependency edge: "+_vde+", "+_deMethod+" - "+_deStmt+" --> "+_vdt+", "+_dtMethod+" - "+_dtStmt);
				vtg.createTransferEdge(_vde, _deMethod, _deStmt, _vdt, _dtMethod, _dtStmt, VTEType.VTE_INSVAR, false);	
			}
		}
		return _result;
	}

	/**
	 * @see edu.ksu.cis.indus.staticanalyses.dependency.InterferenceDAv2#isStaticFieldDependentOn(Pair,
	 *      edu.ksu.cis.indus.common.datastructures.Pair, soot.jimple.StaticFieldRef, soot.jimple.StaticFieldRef)
	 */
	@Override protected boolean isStaticFieldDependentOn(final Pair<AssignStmt, SootMethod> dependent,
			final Pair<AssignStmt, SootMethod> dependee, final StaticFieldRef dependentFieldRef,
			final StaticFieldRef dependeeFieldRef) {
		boolean _result = super.isStaticFieldDependentOn(dependent, dependee, dependentFieldRef, dependeeFieldRef);

		if (_result) {
			final SootField _field = dependeeFieldRef.getField();
			_result = ecba.staticfieldAccessShared(_field.getDeclaringClass(), dependee.getSecond(), _field.getSignature(),
					IEscapeInfo.READ_WRITE_SHARED_ACCESS);
			if (_result)
			{
				final SootMethod _deMethod = dependee.getSecond();
				final SootMethod _dtMethod = dependent.getSecond();

				Stmt _deStmt=dependee.getFirst();
				Stmt _dtStmt=dependent.getFirst();
				final Value _de = dependee.getFirst().getLeftOp();
				final Value _dt = dependent.getFirst().getRightOp();
				Variable _vde=vtg.makeVariable(_de,_deStmt);
				Variable _vdt=vtg.makeVariable(_dt,_dtStmt);
				System.out.println("InterferenceDAv3() ArrayDependency edge: "+_vde+", "+_deMethod+" - "+_deStmt+" --> "+_vdt+", "+_dtMethod+" - "+_dtStmt);
				vtg.createTransferEdge(_vde, _deMethod, _deStmt, _vdt, _dtMethod, _dtStmt, VTEType.VTE_STVAR, false);		
			}
		}
		return _result;
	}

	/**
	 * Extracts information provided by the environment via <code>info</code> parameter to {@link #initialize(java.util.Map)
	 * initialize}.
	 * 
	 * @throws InitializationException when and instance of equivalence class based escape analysis is not provided.
	 * @pre info.get(IEscapeInfo.ID) != null
	 * @see InterferenceDAv1#setup()
	 */
	@Override protected void setup() throws InitializationException {
		super.setup();

		ecba = (IEscapeInfo) info.get(IEscapeInfo.ID);

		if (pairMgr == null) {
			throw new InitializationException(IEscapeInfo.ID + " was not provided in info.");
		}
	}
}

// End of File
