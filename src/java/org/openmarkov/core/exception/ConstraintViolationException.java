/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.exception;

/** Thrown when trying to do an edit that violates one of the 
 * <code>PNConstraints</code> of the <code>ProbNet</code>
 * @see openmarkov.graphs.Link#Link(openmarkov.Node, openmarkov.Node, boolean) */
@SuppressWarnings("serial")
public class ConstraintViolationException extends Exception {

	// Constructor
	/** @param message <code>String</code> */
	public ConstraintViolationException(String message) {
		super(message);
	}

}
