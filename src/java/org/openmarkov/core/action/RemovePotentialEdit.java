/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.action;

import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.potential.Potential;

@SuppressWarnings("serial")
public class RemovePotentialEdit extends SimplePNEdit implements PNEdit { 

	// Attributes
	protected Potential oldPotential;

	// Constructor
	/** @param probNet <code>ProbNet</code>
	 * @param potential <code>Potential</code> */
	public RemovePotentialEdit(ProbNet probNet, Potential potential) {
		super(probNet);
		oldPotential = potential;
	}

	// Methods
	@Override
	public void doEdit() {
		probNet.removePotential(oldPotential);
	}
	
	public void undo() {
		super.undo();
		probNet.addPotential(oldPotential);
	}
    
    public String toString() {
		StringBuffer buffer = new StringBuffer("RemovePotentialEdit: "+ 
				oldPotential.getVariables());
		if (oldPotential.isUtility()) {
			buffer.append("-Utility");
		}
		return buffer.toString();
    }
	
}
