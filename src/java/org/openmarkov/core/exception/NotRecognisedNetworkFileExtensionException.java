/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.exception;

// ESCA-JAVA0234: suppress warning serial
/**
 * this exception is thrown by the Network.java when having problems saving network to file
 * @author jlgozalo
 * @version 1.0 - jlgozalo - initial version 9 May 2010
 *
 */
@SuppressWarnings("serial")
public class NotRecognisedNetworkFileExtensionException extends Exception {

	// Constructor
	/** @param fileName */
	public NotRecognisedNetworkFileExtensionException(String fileName) {
		super("Not recognised network filename extension: " + fileName);
	}

}
