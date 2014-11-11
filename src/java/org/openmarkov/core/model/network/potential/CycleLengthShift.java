/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.model.network.potential;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.inference.InferenceOptions;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.plugin.RelationPotentialType;

/** Potential identical to another but moved to another temporal slice.
 * @author marias
 * @version 1.0 */
@RelationPotentialType(name = "CycleLengthShift", family = "")
public class CycleLengthShift extends Potential {

    protected int       timeDifference = 1;

	// Constructor
	/** @param potential
	 * @param slice */
	public CycleLengthShift(List<Variable> variables) {
		super(variables, PotentialRole.CONDITIONAL_PROBABILITY);
		type = PotentialType.CYCLE_LENGTH_SHIFT;
	}
	
    public CycleLengthShift(Potential potential) {
        super(potential);
        type = PotentialType.CYCLE_LENGTH_SHIFT;
    }
	
    /**
     * Returns if an instance of a certain Potential type makes sense given the
     * variables and the potential role
     * 
     * @param variables
     * @param role
     */
    public static boolean validate (ProbNode probNode, List<Variable> variables, PotentialRole role)
    {
        return role == PotentialRole.CONDITIONAL_PROBABILITY && variables.size () == 2
                // child = variables.get (0)
                // parent = variables.get (1)
               && variables.get (0).isTemporal () && variables.get (1).isTemporal ()
               && variables.get (0).getBaseName ().equals (variables.get (1).getBaseName ())
               && variables.get (0).getTimeSlice () == variables.get (1).getTimeSlice () + 1;
    }       

	// Methods
	@Override
	public List<TablePotential> tableProject(EvidenceCase evidenceCase, 
			InferenceOptions inferenceOptions,
            List<TablePotential> projectedPotentials)
			throws NonProjectablePotentialException {
        Variable conditionedVariable = getConditionedVariable();
        Variable conditioningVariable = variables.get((conditionedVariable == variables.get(0))? 1 : 0);
        TablePotential projectedPotential = null; 
	    if(conditionedVariable.getVariableType() == VariableType.NUMERIC)
	    {
    		for (Variable variable : variables) {
    			if (!variable.equals(conditionedVariable) && !evidenceCase.contains(variable)) {
    				throw new Error("Variable " + variable.getName() + 
    				" is not included in EvidenceCase.");
    			}
    		}
    		projectedPotential = new TablePotential(new ArrayList<Variable>(), role);
    		projectedPotential.values[0] = evidenceCase.getNumericalValue(conditioningVariable) + timeDifference;
	    }else
	    {
	        // Build projected potential based on parent's potential
	        TablePotential projectedParentPotential = findPotentialByVariable(conditioningVariable, projectedPotentials);
	        List<Variable> projectedVariables = projectedParentPotential.getVariables();
	        if(role != PotentialRole.UTILITY)
	        {
                // replace parent variable with child variable in the list of
                // variables of the projected potential
                projectedVariables.remove(conditioningVariable);                
                projectedVariables.add(0, conditionedVariable);
	            projectedPotential = new TablePotential(projectedVariables, role);
	        }else
	        {
                projectedPotential = new TablePotential(projectedVariables, role, conditionedVariable);
	        }
	        
            int numStates = conditionedVariable.getNumStates();
	        int numStatesParent = conditioningVariable.getNumStates();
            int configurationIndex = 0; 
            // Copy values from parent's projected potential, shifting values one state
	        for (int i = 0; i < projectedParentPotential.values.length; i+=numStatesParent) {
	            projectedPotential.values[configurationIndex * numStates] = 0;
	            for (int j = 0; j < numStatesParent; ++j) {
                    projectedPotential.values[configurationIndex * numStates + j + 1] = projectedParentPotential.values[i
                            + j];
	            }
	            configurationIndex++;
	        }
	    }
		return Arrays.asList(projectedPotential);
	}


    @Override
    public Collection<Finding> getInducedFindings(EvidenceCase evidenceCase, double cycleLength) {
        Variable conditionedVariable = getConditionedVariable();
        Variable conditioningVariable = variables.get((conditionedVariable == variables.get(0))? 1 : 0);
        List<Finding> inducedFindings = new ArrayList<Finding>();
        if (evidenceCase.contains(conditioningVariable)
                && !evidenceCase.contains(conditionedVariable)) {
            double numericalValue = evidenceCase.getFinding(
                    conditioningVariable).getNumericalValue() + cycleLength;
            inducedFindings.add(new Finding(conditionedVariable, numericalValue));
        }
        return inducedFindings;
    }

    @Override
    public Potential copy ()
    {
        return new CycleLengthShift(this);
    }

	@Override
	public boolean isUncertain() {
		return false;
	}

    @Override
    public String toString() {
        return super.toString() + " = CycleLengthShift";
    }

    public int getTimeDifference() {
        return timeDifference;
    }

    public void setTimeDifference(int timeDifference) {
        this.timeDifference = timeDifference;
    }	
	
	

}
