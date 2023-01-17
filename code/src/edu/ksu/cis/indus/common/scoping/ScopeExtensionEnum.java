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

package edu.ksu.cis.indus.common.scoping;

/**
 * The enumeration to identify various scope extensions.
 * 
 * @author <a href="http://www.cis.ksu.edu/~rvprasad">Venkatesh Prasad Ranganath</a>
 * @author $Author: rvprasad $
 * @version $Revision: 1.3 $ $Date: 2007/02/10 19:08:39 $
 */
public enum ScopeExtensionEnum {
	/** 
	 * This correspond to the enumeration constants used in java-xml binding under <code>scopeExtension</code> element.
	 */
	EXCLUSIVE_ANCESTORS,

	/** 
	 * This correspond to the enumeration constants used in java-xml binding under <code>scopeExtension</code> element.
	 */
	EXCLUSIVE_DESCENDANTS,

	/** 
	 * This correspond to the enumeration constants used in java-xml binding under <code>scopeExtension</code> element.
	 */
	INCLUSIVE_ANCESTORS,

	/** 
	 * This correspond to the enumeration constants used in java-xml binding under <code>scopeExtension</code> element.
	 */
	INCLUSIVE_DESCENDANTS,

	/** 
	 * This correspond to the enumeration constants used in java-xml binding under <code>scopeExtension</code> element.
	 */
	IDENTITY,

	/** 
	 * This correspond to the enumeration constants used in java-xml binding under <code>scopeExtension</code> element.
	 */
	PRIMITIVE;
}