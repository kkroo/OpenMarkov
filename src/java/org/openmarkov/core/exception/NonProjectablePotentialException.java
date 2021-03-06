/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.exception;

/** Thrown when the <code>Potential</code> cannot be projected into a set of 
 * <code>TablePotential</code>s given the evidence supplied.*/
@SuppressWarnings("serial")
public class NonProjectablePotentialException extends Exception {

	public NonProjectablePotentialException(String string) {
		super(string);
	}


}
