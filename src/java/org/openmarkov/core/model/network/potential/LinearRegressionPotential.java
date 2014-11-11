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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.InferenceOptions;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.plugin.RelationPotentialType;

@RelationPotentialType(name = "Linear regression", family = "Regression")
public class LinearRegressionPotential extends RegressionPotential {

    public LinearRegressionPotential(List<Variable> variables, PotentialRole role) {
        super(variables, role, getDefaultCovariates(variables, role), new double[variables.size()]);
    }
    
    public LinearRegressionPotential(List<Variable> variables, PotentialRole role, Variable utilityVariable) {
        super(variables, role, getDefaultCovariates(variables, role), new double[variables.size()+1]);
        this.utilityVariable = utilityVariable;
    }      

    public LinearRegressionPotential(List<Variable> variables, PotentialRole role,
            String[] covariates, double[] coefficients) {
        super(variables, role, covariates, coefficients);
    }
    
    public LinearRegressionPotential(LinearRegressionPotential potential) {
        super(potential);
    }    
    
    /**
     * Returns if an instance of a certain Potential type makes sense given the
     * variables and the potential role.
     * 
     * @param probNode
     *            . <code>ProbNode</code>
     * @param variables
     *            . <code>ArrayList</code> of <code>Variable</code>.
     * @param role
     *            . <code>PotentialRole</code>.
     */
    public static boolean validate(ProbNode probNode, List<Variable> variables, PotentialRole role) {
        return variables.get(0).getVariableType() == VariableType.NUMERIC;
    }    

    @Override
    protected List<TablePotential> tableProject(EvidenceCase evidenceCase,
            InferenceOptions inferenceOptions,
            double[] coefficients,
            String[] covariates)
            throws NonProjectablePotentialException, WrongCriterionException {
        Variable conditionedVariable = getConditionedVariable(); 
        int numStates = conditionedVariable.getNumStates();
        Evaluator evaluator = new Evaluator();
        // Fill arrays numericValues and evidencelessVariables
        List<Variable> evidencelessVariables = new ArrayList<>();
        List<Integer> evidencelessVariablesIndex = new ArrayList<>();
        Map<String, String> variableValues = new HashMap<>();

        int constantIndex = -1;
        for(int i=0; i < covariates.length; ++i)
        {
            if(covariates[i].equals(CONSTANT))
            {
                constantIndex = i;
            }
        }        
        for (int i = 1; i < variables.size(); ++i) {
            Variable variable = variables.get(i);

            if (evidenceCase == null || !evidenceCase.contains(variable)) {
                if (variable.getVariableType() == VariableType.NUMERIC) {
                    throw new NonProjectablePotentialException("Can not project potential with numeric variable "
                            + variable.getName());
                }
                evidencelessVariables.add(variable);
                evidencelessVariablesIndex.add(i - 1);
                variableValues.put(variable.getName(), "0.0");
            } else {
                double numericValue = evidenceCase.getFinding(variable).getNumericalValue();
                variableValues.put(variable.getName(), String.valueOf(numericValue));
            }
        }

        List<Variable> projectedPotentialVariables = new ArrayList<>(evidencelessVariables);
        projectedPotentialVariables.add(0, variables.get(0));
        TablePotential projectedPotential = new TablePotential(projectedPotentialVariables, role);
        int[] offsets = projectedPotential.getOffsets();
        int[] dimensions = projectedPotential.getDimensions();
        for (int i = 0; i < projectedPotential.values.length; i += numStates) {
            // Set the values of variables without evidence
            for (int j = 1; j < projectedPotentialVariables.size(); ++j) {
                int index = (i / offsets[j]) % dimensions[j];
                double value = index;
                try
                {
                    value = Double.parseDouble(projectedPotentialVariables.get(j).getStates()[index].getName());
                } catch(NumberFormatException e)
                {
                    // ignore
                }
                variableValues.put(projectedPotentialVariables.get(j).getName(),String.valueOf(value));
            }
            evaluator.setVariables(variableValues);
            double regression = coefficients[constantIndex];
            for (int j = 0; j < coefficients.length; ++j) {
                double covariateValue = 0.0;
                if (j != constantIndex) {
                    try {
                        covariateValue = Double.parseDouble(evaluator.evaluate(covariates[j]));
                    } catch (NumberFormatException | EvaluationException e) {
                        e.printStackTrace();
                    }
                    regression += covariateValue * coefficients[j];
                }
            }
            try {
            	if(getConditionedVariable().getVariableType() == VariableType.NUMERIC)
            	{
            		projectedPotential.values[i] = regression;
            	}else
            	{
	                int stateIndex = getConditionedVariable().getStateIndex(regression);
	                for (int j = 0; j < numStates; ++j) {
	                    projectedPotential.values[i + j] = (j == stateIndex) ? 1 : 0;
	                }
            	}
            } catch (InvalidStateException e) {
                e.printStackTrace();
            }
        }
        return Arrays.asList(projectedPotential);
    }

    @Override
    public Potential copy() {
        return new LinearRegressionPotential(this);
    }

}
