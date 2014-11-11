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
public class AddPotentialEdit extends SimplePNEdit {

	protected Potential potential;
	
	// Constructor
	/** @param probNet <code>ProbNet</code>
	 * @param potential <code>Potential</code> */
	public AddPotentialEdit(
			ProbNet probNet, Potential potential) {
		super(probNet);
		this.potential = potential;
	}

	// Methods
	@Override
	public void doEdit() {
		probNet.addPotential(potential);
	}

	public void undo() {
		super.undo();
		probNet.removePotential(potential);
	}

	/** @return potential <code>Potential</code> */
	public Potential getPotential() {
		return potential;
	}

	/** @return A <code>String</code> with the potential variables. */
	public String toString() {
		StringBuffer buffer = new StringBuffer("AddPotentialEdit: ");
		if (potential != null) {
			buffer.append(potential.getVariables());
			if (potential.isUtility()) {
				buffer.append("-Utility");
			}
		} else {
			buffer.append("null !!!!");
		}
		return buffer.toString();
	}

}
