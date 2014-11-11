/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.action;

import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.potential.Potential;

/** Changes an old potential for a new potential */
@SuppressWarnings("serial")
public class PotentialChangeEdit extends SimplePNEdit {

	// Attribute
	private Potential newPotential;

	private Potential oldPotential;

	// Constructor
	/** @param probNet <code>ProbNet</code>
	 * @param oldPotential <code>Potential</code>
	 * @param newPotential <code>Potential</code> */
	public PotentialChangeEdit(ProbNet probNet, Potential oldPotential, 
			Potential newPotential) {
		super(probNet);
		this.newPotential = newPotential;
		this.oldPotential = oldPotential;
	}
	
	@Override
	public void doEdit() throws DoEditException {
		if (probNet.removePotential(oldPotential) == null) {
			throw new DoEditException("Can not remove potential: "
					+ oldPotential.toString());
		}
		probNet.addPotential(newPotential);
	}
	
	public void undo() {
		super.undo();
		probNet.removePotential(newPotential);
		probNet.addPotential(oldPotential);
	}

	/** @return A <code>String</code> with the variables of both potentials. */
	public String toString() {
		StringBuffer buffer = new StringBuffer(
				"ChangePotentialEdit: " +	oldPotential.getVariables());
		if (oldPotential.isUtility()) {
			buffer.append("-utility");
		}
		buffer.append(" --> " + newPotential.getVariables());
		if (newPotential.isUtility()) {
			buffer.append("-utility");
		}
		return buffer.toString();
	}

	public Potential getNewPotential() {
		return newPotential;
	}

	public Potential getOldPotential() {
		return oldPotential;
	}

}
