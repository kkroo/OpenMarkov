/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.exception;

public class NotEvaluableNetworkException extends Exception {

	private static final long serialVersionUID = -6555375975623328551L;

	// Constructor
	/** @param message <code>String</code> */
	public NotEvaluableNetworkException(Exception e) {
		super(e.getMessage());
	}

	public NotEvaluableNetworkException(String message) {
		super(message);
	}

}
