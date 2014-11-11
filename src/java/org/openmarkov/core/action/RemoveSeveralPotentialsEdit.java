/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.action;

import java.util.ArrayList;

import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.potential.Potential;


@SuppressWarnings("serial")
/** Removes several potentials */
public class RemoveSeveralPotentialsEdit extends SimplePNEdit {
	
	private ArrayList<Potential> potentialsToDelete;
	
	/** @param probNet <code>ProbNet</code>
	 * @param potentials <code>ArrayList</code> of <code>Potential</code>s */
	public RemoveSeveralPotentialsEdit(ProbNet probNet, 
			ArrayList<Potential> potentials) {
		super(probNet);
		potentialsToDelete = new ArrayList<Potential>(potentials);
	}
	
	/** Adds more potentials to delete
	 * @param morePotentials <code>ArrayList</code> of <code>Potential</code>s*/
	public void addPotentials(ArrayList<Potential> morePotentials) {
		potentialsToDelete.addAll(morePotentials);
	}

	/** @return <code>String</code> */
	public String toString() {
		String auxString = new String(this.getClass().getSimpleName() + ":\n");
		for (Potential potential : potentialsToDelete) {
			auxString = auxString + potential.getVariables().toString() + " ";
		}
		return auxString;
	}

	@Override
	public void doEdit() {
		for (Potential potential : potentialsToDelete) {
			probNet.removePotential(potential);
		}
	}

}
