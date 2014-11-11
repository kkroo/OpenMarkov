/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.inference;

import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;

/** Stores attributes for use in <code>SimpleMarkovEvaluation</code> */
public class InferenceOptions {
	
	// Attributes
	/**  */
	// TODO eliminar este atributo
	public Variable decisionCriteria;
	
	/** */
	public Variable simulationIndexVariable;
	
	/** */
	public double discountRate = 1.0;

	public ProbNet probNet;
	
	// Constructor
	public InferenceOptions(ProbNet probNet, Variable simulationIndexVariable) {
		this.probNet = probNet;
		this.decisionCriteria = probNet.decisionCriteria;
		this.simulationIndexVariable = simulationIndexVariable;
	}
	
	// Methods
	/** Prints decision criteria, simulation indices and discount rate */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		if (decisionCriteria != null) {
			buffer.append("Decision criteria: ");
			printVariable(buffer, decisionCriteria);
		} else {
			buffer.append("No decision criteria.\n");
		}
		if (simulationIndexVariable != null) {
			buffer.append("Simulation indices: ");
			printVariable(buffer, simulationIndexVariable);
		} else {
			buffer.append("No simulation indices.\n");
		}
		buffer.append("Discount rate = " + discountRate);
		return buffer.toString();
	}
	
	/** Inserts in buffer the name and states of the received variable */
	private void printVariable(StringBuffer buffer, Variable variable) {
		buffer.append(variable.getName());
		if (variable.getVariableType() != VariableType.NUMERIC) {
			buffer.append("(");
			State[] states = variable.getStates();
			for (int i = 0; i < states.length - 1; i++) {
				buffer.append(states[i].getName() + ", ");
			}
			buffer.append(states[states.length - 1].getName() + ")\n");
		} else {
			buffer.append("Continuous variable!\n");
		}
	}

	/** Sets the attribute simulationIndexVariable and returns the variable. 
	 * If numSimulations = 0, it returns null. */
	public static Variable setNumSimulations(int numSimulations) {
		if (numSimulations == 0) {
			return  null;
		} else {
			return new Variable("###SimulationIndexes###", numSimulations);
		}
	}
}
