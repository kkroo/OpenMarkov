/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network.potential;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.inference.InferenceOptions;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.plugin.RelationPotentialType;

/**
 * Potential with discrete and/or continuous variables.
 * @author marias
 * @version 1.0
 */
@RelationPotentialType(name = "Uniform", family = "")
public class UniformPotential extends Potential
{
    // Attributes
    /**
     * Value of a potential configuration when all the variables are discrete.
     */
    private double discreteValue = 0.0;

    // Constructors
    /**
     * @param variables. <code>ArrayList</code> of <code>Variable</code>
     * @param role. <code>PotentialRole</code>
     */
    public UniformPotential (List<Variable> variables, PotentialRole role)
    {
        super (variables, role);
        if (allVariablesAreDiscrete (variables))
        {
            discreteValue = calculateDiscreteValue (variables);
        }
        type = PotentialType.UNIFORM;
    }

    /**
     * @param variables. <code>ArrayList</code> of <code>Variable</code>
     * @param role. <code>PotentialRole</code>
     * @param utilityVariable. <code>Variable</code>
     */
    public UniformPotential (List<Variable> variables, PotentialRole role, Variable utilityVariable)
    {
        super (variables, role, utilityVariable);
        if (allVariablesAreDiscrete (variables))
        {
            discreteValue = calculateDiscreteValue (variables);
        }
        type = PotentialType.UNIFORM;
    }

    /**
     * @param role. <code>PotentialRole</code>
     * @param variables... <code>Variable</code>
     */
    public UniformPotential (PotentialRole role, Variable... variables)
    {
        this (toList (variables), role);
    }

    /**
     * Copy constructor for UniformPotential
     * @param potential
     */
    public UniformPotential (UniformPotential potential)
    {
        super (potential);
        type = PotentialType.UNIFORM;
        if (allVariablesAreDiscrete (variables))
        {
            discreteValue = calculateDiscreteValue (variables);
        }
    }

    // Methods
    /**
     * Returns if an instance of a certain Potential type makes sense given the
     * variables and the potential role
     * @param probNode. <code>ProbNode</code>
     * @param variables. <code>ArrayList</code> of <code>Variable</code>
     * @param role. <code>PotentialRole</code>
     */
    public static boolean validate (ProbNode probNode, List<Variable> variables, PotentialRole role)
    {
        // TODO
        return true;
    }

    // Methods
    @Override
    /** @return If this is a utility potential, it represents the case in 
     * which all the utilities are zero; therefore, it suffices to return
     * an empty list. If this is a conditional probability P(Y|X1,...,Xn), it 
     * returns a <code>TablePotential<code> that is uniform potential P(y). 
     * If this is a joint probability, P(X1,...,Xn), it returns a 
     * <code>TablePotential<code> that is equal to this potential.
     * In all cases, the argument <code>evidenceCase</code> is irrelevant.
     * @param evidenceCase. <code>evidenceCase</code>
     * @throws NonProjectablePotentialException when this is a conditional
     * probability potential and the conditioned variable is numeric. */
    public List<TablePotential> tableProject (EvidenceCase evidenceCase,
                                              InferenceOptions inferenceOptions,
                                              List<TablePotential> projectedPotentials)
        throws NonProjectablePotentialException
    {
        List<TablePotential> newProjectedPotentials = new ArrayList<TablePotential> ();
        switch (role)
        {
            case CONDITIONAL_PROBABILITY :
            case JOINT_PROBABILITY :
            case POLICY :
                TablePotential projectedPotential = null;
                Variable conditionedVariable = variables.get (0);
                if (evidenceCase != null && evidenceCase.contains (conditionedVariable))
                {
                    if (conditionedVariable.getVariableType () == VariableType.NUMERIC)
                    {
                        // returns an empty list of potentials
                        return new ArrayList<TablePotential> ();
                    }
                    else
                    {
                        // returns a constant
                        projectedPotential = new TablePotential (new ArrayList<Variable> (),
                                                                 PotentialRole.UNSPECIFIED);
                        projectedPotential.values[0] = 1.0 / conditionedVariable.getNumStates ();
                    }
                }
                else
                {
                    // the conditioned variable does not make part of the
                    // evidence
                    if (conditionedVariable.getVariableType () == VariableType.NUMERIC)
                    {
                        throw new NonProjectablePotentialException (
                                                                    "Numeric variable "
                                                                            + conditionedVariable.getName ()
                                                                            + " makes it impossible "
                                                                            + "to project this uniform potential into a table.");
                    }
                    else
                    {
                        // returns a uniform potential
                        List<Variable> potentialVariables = new ArrayList<Variable> (variables);
                        if (evidenceCase != null)
                        {
                            potentialVariables.removeAll (evidenceCase.getVariables ());
                        }
                        projectedPotential = new TablePotential (potentialVariables,
                                                                 getPotentialRole ());
                    }
                }
                newProjectedPotentials.add (projectedPotential);
                break;
            // In case of utility potentials, return an empty potential
            case UTILITY :
                ArrayList<Variable> potentialVariables = new ArrayList<Variable> (variables);
                if (evidenceCase != null)
                {
                    potentialVariables.removeAll (evidenceCase.getVariables ());
                }
                projectedPotential = new TablePotential (potentialVariables, PotentialRole.UTILITY);
                newProjectedPotentials.add (projectedPotential);
                break;
            default :
                break;
        } // end of switch/case statement
        return newProjectedPotentials;
    }

    /**
     * @return <code>true</code> if all the variables are FINITE_STATES.
     * @param variables. <code>ArrayList</code> of <code>Variable</code>
     */
    private boolean allVariablesAreDiscrete (List<Variable> variables)
    {
        for (Variable variable : variables)
        {
            if (variable.getVariableType () != VariableType.FINITE_STATES)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * @param variables. <code>ArrayList</code> of <code>Variable</code>
     * @return 1 / multiplication of the number of states of conditioning
     *         variables.
     */
    private double calculateDiscreteValue (List<Variable> variables)
    {
        int statesSpace = 1;
        for (int i = 1; i < variables.size (); i++)
        {
            statesSpace *= variables.get (i).getNumStates ();
        }
        return 1 / new Double (statesSpace);
    }

    /** @return discreteValue. <code>double</code> */
    public double getDiscreteValue ()
    {
        return discreteValue;
    }

    @Override
    public Potential copy ()
    {
        return new UniformPotential(this);
    }

    @Override
    public int sample (Random randomGenerator, Map<Variable, Integer> parentStateIndexes)
    {
        return randomGenerator.nextInt (variables.get (0).getNumStates ());
    }

    public double getProbability (HashMap<Variable, Integer> sampledStateIndexes)
    {
        return 1.0 / variables.get (0).getNumStates ();
    }

    @Override
    public boolean isUncertain ()
    {
        return false;
    }

    /**
     * Used to apply discount rates in cost effectiveness analysis for utility
     * variables has no sense in chance nodes
     */
    public void setDiscreteValue (double discreteValue)
    {
        this.discreteValue = discreteValue;
    }
    
    @Override
    public String toString() {
        return super.toString() + " = Uniform";
    }    
}
