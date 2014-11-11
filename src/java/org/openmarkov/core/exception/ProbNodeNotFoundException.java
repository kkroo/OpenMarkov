/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.exception;

import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;

@SuppressWarnings("serial")
public class ProbNodeNotFoundException extends Exception {

	// Constructor
	/** @param networkName TODO
	 * @param message */
	public ProbNodeNotFoundException(ProbNet network, String variableName) {
		super("Variable: " + variableName + 
				" not found in network " + network.getName() + ".");
	}

    public ProbNodeNotFoundException(ProbNode node) {
        this(node.getProbNet(), node.getName());
    }

}
