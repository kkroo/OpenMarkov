/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.exception;

import org.openmarkov.core.model.network.Variable;

@SuppressWarnings("serial")
public class WrongCriterionException extends Exception {

	public WrongCriterionException(Variable utilityVariable, String criterion,
			Variable decisionCriteria) {
		super("The criterion for the utility variable " + 
				utilityVariable.getName() + ", which is " + criterion +
				", does not match any of the values of the decisionCriteria" +
				"variable.");
	}

	
	
}
