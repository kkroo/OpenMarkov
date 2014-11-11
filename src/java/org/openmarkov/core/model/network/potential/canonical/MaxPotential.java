/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.model.network.potential.canonical;

import java.util.ArrayList;
import java.util.List;

import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.PotentialType;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.plugin.RelationPotentialType;

@RelationPotentialType(name="OR / MAX", family="ICI")
public class MaxPotential extends MinMaxPotential {

	/** @param model. <code>ICIModel</code>.
	 * @param variables. <code>ArrayList</code> of <code>Variable</code>. */
	public MaxPotential(
			ICIModelType model, List<Variable> variables) {
		super(model, variables);
		type = PotentialType.MAX;
	}
	
	/**
	 * 
	 * Constructor for MaxPotential that assumes the ICIModelType is GENERAL_MAX
	 * @param variables
	 */
	public MaxPotential (List<Variable> variables)
    {
        this (ICIModelType.GENERAL_MAX, variables);
    }
	
	public MaxPotential(MaxPotential potential)
	{
	    super(potential);
	    type = PotentialType.MAX;
	}
	
    /** Returns if an instance of a certain Potential type makes sense given 
     * the variables and the potential role.
     * @param probNode. <code>ProbNode</code> 
     * @param variables. <code>ArrayList</code> of <code>Variable</code>.
     * @param role. <code>PotentialRole</code>. */
	public static boolean validate(ProbNode probNode, List<Variable> variables, 
			PotentialRole role) {
		boolean valid = ICIPotential.validate(probNode, variables, role) && 
		        ((role == PotentialRole.CONDITIONAL_PROBABILITY)
				|| (role == PotentialRole.POLICY));
		int i = 0;
		
		while(valid && i < variables.size())
		{
			valid &= variables.get(i).getVariableType() == VariableType.FINITE_STATES 
					|| variables.get(i).getVariableType() == VariableType.DISCRETIZED;
			++i;
		}
        return valid;
    }    	
	
    public TablePotential getDefaultLeakyPotential ()
    {
        ArrayList<Variable> leakyVariables = new ArrayList<Variable> ();
        leakyVariables.add (variables.get (0));
        TablePotential tablePotential = new TablePotential (leakyVariables,
                                                            PotentialRole.CONDITIONAL_PROBABILITY);
        double[] leakyParameters = new double[variables.get (0).getNumStates ()];
        leakyParameters[0] = 1.0;
        for (int i = 1; i < leakyParameters.length; ++i)
        {
            leakyParameters[0] = 0.0;
        }
        tablePotential.values = leakyParameters;
        return tablePotential;
    }	

	@Override
	/** @returns A <code>TablePotential</code> with two variables: 
	 *  <code>conditionedVariable</code> and <code>pseudoVariable</code>. */
	public TablePotential getDeltaPotential() {
		Variable conditionedVariable = variables.get(0);
		List<Variable> deltaVariables = new ArrayList<Variable>();
		deltaVariables.add(pseudoVariable);
		deltaVariables.add(conditionedVariable);
		TablePotential deltaPotential = new TablePotential(
				deltaVariables, PotentialRole.JOINT_PROBABILITY);
		int numStatesConditioned = conditionedVariable.getNumStates();
		int numStatesPseudo = pseudoVariable.getNumStates(); // same number
		int actualConfiguration = 0;
		for (int i = 0; i < numStatesConditioned; i++) {
			for (int j = 0; j < numStatesPseudo; j++) {
				if (i == j) {
					deltaPotential.values[actualConfiguration] = 1;
				} else if (j == (i - 1)) {
					deltaPotential.values[actualConfiguration] = -1;
				} else {
					deltaPotential.values[actualConfiguration] = 0;
				}
				actualConfiguration++;
			}
		}
		return deltaPotential;
	}

	@Override
	/** @param subPotential. <code>TablePotential</code> 
	 *  In general it will be the conditional probability associated with 
	 *  a link of the ICI model (i.e., a conditional probability of the child 
	 *  node given the parent node) or the leak probability.
	 * @return The accrued potential. <code>TablePotential</code>. I.e., if 
	 *  subPotential is P(y) then the accrued potential is P(Y>=y), and if
	 *  the subPotential is P(y|x) then the accrued potential is P(Y>=y|x).
	 * @reference Efficient computation for the Noisy MAX
	 * @argCondition subPotential is a probability table of one variable
	 *  or a probability table of one variable given another variable. */
	public TablePotential getAccruedPotential(TablePotential subPotential) {
		// TODO Revisar este metodo para el caso de un potential proyectado
	    List<Variable> subPotentialVariables = subPotential.getVariables();
	    List<Variable> accruedPotentialVariables = new ArrayList<Variable>(subPotentialVariables);
		accruedPotentialVariables.set(0, pseudoVariable);

		TablePotential accruedPotential = new TablePotential(
				accruedPotentialVariables, PotentialRole.JOINT_PROBABILITY);
		
		// number of states in the pseudovariable
		int numStates = variables.get(0).getNumStates();
		
		double accumulator = 0;
		for (int i = 0; i < subPotential.values.length; i++) {
			accumulator += subPotential.values[i];
			accruedPotential.values[i] = accumulator;
			if ((i + 1) % numStates == 0) {
				accumulator = 0;
			}
		}
		return accruedPotential;
	}

    @Override
    public double[] getDefaultLeakyParameters (int numStates)
    {
        double[] leakyParameters = new double[numStates];
        
        leakyParameters[0] = 1.0;
        for(int i=1; i<numStates; ++i)
        {
            leakyParameters[i] = 0.0;
        }
        return leakyParameters;
    }
    
    @Override
    public Potential copy ()
    {
        return new MaxPotential(this);
    }       
    
    @Override
    public Potential addVariable(Variable newVariable){
    	List<Variable> newVariables = new ArrayList<Variable> (variables);
    	newVariables.add(newVariable);
    	MaxPotential newICIPotential = new MaxPotential(this.modelType, newVariables);
    	
		for (int i = 1; i < variables.size(); i++) {
			double []noisyParameters = this.getNoisyParameters(variables.get(i));
			newICIPotential.setNoisyParameters(variables.get(i), noisyParameters);
		}
		Variable conditionedVariable = variables.get(0);
		double[] noisyParameters = newICIPotential.initializeNoisyParameters(conditionedVariable, newVariable);
		newICIPotential.setNoisyParameters(newVariable, noisyParameters);
		
		newICIPotential.setLeakyParameters(getLeakyParameters());
		return newICIPotential;
    }
    
    @Override
	public Potential removeVariable(Variable variable) {
    	List<Variable> newVariables = new ArrayList<Variable>();
    	for (int i = 0; i < variables.size(); i++){
    		if (variable == variables.get(i)) {
    			continue;
    		}else{
    			newVariables.add(variables.get(i));
    		}
    	}
    	
    	MaxPotential newICIPotential = new MaxPotential(this.modelType, newVariables);
    	
    	for (int i = 1; i < newVariables.size(); i++) {
			double []noisyParameters = this.getNoisyParameters(newVariables.get(i));
			newICIPotential.setNoisyParameters(newVariables.get(i), noisyParameters);
		}
    	newICIPotential.setLeakyParameters(getLeakyParameters());
    	return newICIPotential;
    }
    
    @Override
    protected int computeFFunction (int[] parentStates)
    {
        int resultingState = 0;
        for(Integer parentState: parentStates)
        {
            if(parentState > resultingState)
            {
                resultingState = parentState;
            }
        }
        return resultingState;
    }

	@Override
	public boolean isUncertain() {
		// TODO Auto-generated method stub
		return false;
	}     
}
