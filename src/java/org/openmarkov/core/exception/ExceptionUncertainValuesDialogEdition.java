/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.exception;

import javax.swing.JOptionPane;

public class ExceptionUncertainValuesDialogEdition extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExceptionUncertainValuesDialogEdition(String message) {
		JOptionPane.showMessageDialog(null,message,"Error",
			    JOptionPane.ERROR_MESSAGE);

	}

}
