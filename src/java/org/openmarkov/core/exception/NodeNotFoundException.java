/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.exception;

import org.openmarkov.core.model.network.ProbNode;

@SuppressWarnings("serial")
public class NodeNotFoundException extends WrongGraphStructureException {

	// Constructor
	/** @param message <code>String</code> */
	public NodeNotFoundException(String message) {
		super(message);
	}

    public NodeNotFoundException(ProbNode node) {
       super("Node: " + node.getName() + 
               " not found in network " + node.getProbNet().getName() + ".");
    }

}
