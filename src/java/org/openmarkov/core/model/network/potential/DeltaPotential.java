/*
* Copyright 2013 CISIAD, UNED, Spain
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

import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.InferenceOptions;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.plugin.RelationPotentialType;

@RelationPotentialType(name = "Delta", family = "")
public class DeltaPotential extends Potential{

    private State state = null; 
    private double numericValue = Double.NaN;
    private int stateIndex = -1;
    
    public DeltaPotential(List<Variable> variables, PotentialRole role, double numericValue)
    {
        this(variables, role);
        this.numericValue = numericValue;
    }
    
    public DeltaPotential(List<Variable> variables, PotentialRole role, State state)
    {
        this(variables, role);
        this.state = state;
        Variable conditionedVariable = getConditionedVariable();
        stateIndex = conditionedVariable.getStateIndex(state);
    }
    
    public DeltaPotential(List<Variable> variables, PotentialRole role)
    {
        super(variables, role);
        // set default values
        Variable conditionedVariable = variables.get(0);
        if(conditionedVariable.getVariableType() == VariableType.NUMERIC)
        {
            numericValue = conditionedVariable.getPartitionedInterval().getMin();
        }else
        {
            state = conditionedVariable.getStates()[0];
            stateIndex = conditionedVariable.getStateIndex(state);
        }
    }
    
    public DeltaPotential(DeltaPotential potential)
    {
        super(potential);
        if(potential.state != null)
        {
            state = potential.state;
            stateIndex = getConditionedVariable().getStateIndex(state);
        }else
        {
            numericValue = potential.numericValue;
        }
    }
    
    /**
     * Returns whether this type of Potential is suitable for the list of
     * variables and the potential role given.
     * 
     * @param probNode
     *            . <code>ProbNode</code>
     * @param variables
     *            . <code>List</code> of <code>Variable</code>.
     * @param role
     *            . <code>PotentialRole</code>.
     */
    public static boolean validate(ProbNode probNode, List<Variable> variables, PotentialRole role) {
        return variables.size() <= 1 && role != PotentialRole.UTILITY;
    }

    @Override
    public List<TablePotential> tableProject(EvidenceCase evidenceCase,
            InferenceOptions inferenceOptions,
            List<TablePotential> projectedPotentials)
            throws NonProjectablePotentialException, WrongCriterionException {
        TablePotential projectedPotential = null;
        
        if(state != null)
        {
            projectedPotential = new TablePotential (Arrays.asList(getConditionedVariable()),
                    PotentialRole.UNSPECIFIED);
            for(int i=0; i < projectedPotential.values.length; ++i)
            {
                projectedPotential.values[i] = (i == stateIndex)? 1 : 0;
            }
        }else
        {
            projectedPotential = new TablePotential (new ArrayList<Variable> (),
                                                     PotentialRole.UNSPECIFIED);
            projectedPotential.values[0] = numericValue;
        }
        
        return Arrays.asList(projectedPotential);
    }

    @Override
    public Potential copy() {
        return new DeltaPotential(this);
    }

    @Override
    public boolean isUncertain() {
        return false;
    }

    public State getState() {
        return state;
    }

    public double getNumericValue() {
        return numericValue;
    }

    public int getStateIndex() {
        return stateIndex;
    }
    
    public void setValue(State state)
    {
    	this.state = state;
    	stateIndex = getConditionedVariable().getStateIndex(state);
    }
    
    public void setValue(double numericValue)
    {
    	this.numericValue = numericValue;
    }    
    
    @Override
    public String toString() {
        return super.toString() + " = Delta (" + (state!=null? state.getName() : numericValue) + ")";
    }

    @Override
    public Collection<Finding> getInducedFindings(EvidenceCase evidenceCase, double cycleLength)
            throws IncompatibleEvidenceException, WrongCriterionException {
        Finding inducedFinding = null;
        if(state !=null)
        {
            inducedFinding = new Finding(getConditionedVariable(), state);
        }else
        {
            inducedFinding = new Finding(getConditionedVariable(), numericValue);
        }
        return Arrays.asList(inducedFinding);
    }   
    
    
        
}
