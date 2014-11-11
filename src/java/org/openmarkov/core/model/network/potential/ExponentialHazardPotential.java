/*
* Copyright 2013 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package org.openmarkov.core.model.network.potential;

import java.util.List;

import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.InferenceOptions;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.plugin.RelationPotentialType;

@RelationPotentialType(name = "Hazard (Exponential)", family = "Regression")
public class ExponentialHazardPotential extends WeibullHazardPotential {

    public ExponentialHazardPotential(List<Variable> variables, PotentialRole role) {
        super(variables, role);
    }
    
    public ExponentialHazardPotential(List<Variable> variables, PotentialRole role,
            double[] coefficients) {
        this(variables, role, getDefaultCovariates(variables, role), null, null);
    }    
    
    public ExponentialHazardPotential(List<Variable> variables, PotentialRole role,
            String[] covariates, double[] coefficients, double[] covarianceMatrix) {
        super(variables, role, covariates, coefficients, covarianceMatrix);
    }
    
    public ExponentialHazardPotential(List<Variable> variables, PotentialRole role,
            double[] coefficients, double[] covarianceMatrix) {
        super(variables, role, getDefaultCovariates(variables, role), coefficients, covarianceMatrix);
    }
    
    public ExponentialHazardPotential(List<Variable> variables, PotentialRole role,
            String[] covariates, double[] coefficients, double[] uncertaintyMatrix, MatrixType matrixType) {
        super(variables, role, covariates, coefficients, uncertaintyMatrix, matrixType);
    }
    
    public ExponentialHazardPotential(List<Variable> variables, PotentialRole role,
            double[] coefficients, double[] uncertaintyMatrix, MatrixType matrixType) {
        super(variables, role, getDefaultCovariates(variables, role), coefficients, uncertaintyMatrix, matrixType);
    }    
    
    public ExponentialHazardPotential(ExponentialHazardPotential potential) {
        super(potential);
    }

    /**
     * Returns if an instance of a certain Potential type makes sense given the
     * variables and the potential role.
     * 
     * @param probNode
     *            . <code>ProbNode</code>
     * @param variables
     *            . <code>List</code> of <code>Variable</code>.
     * @param role
     *            . <code>PotentialRole</code>.
     */
    public static boolean validate(ProbNode probNode, List<Variable> variables, PotentialRole role) {
        return variables.get(0).isTemporal()
                && variables.get(0).getVariableType() == VariableType.FINITE_STATES
                && variables.get(0).getNumStates() == 2;
    }
    
    @Override
    public List<TablePotential> tableProject(EvidenceCase evidenceCase,
            InferenceOptions inferenceOptions,
            double[] coefficients,
            String[] covariates)
            throws NonProjectablePotentialException, WrongCriterionException {
        double[] weibullCoeficients = new double[coefficients.length+1];
        String[] weibullCovariates = new String[covariates.length+1];
        // The exponential is a special case of Weibull where k=1 (gamma= ln(k));
        weibullCoeficients[0] = 0;
        weibullCovariates[0] = GAMMA;
        for(int i = 0; i < coefficients.length; ++i)
        {
            weibullCoeficients[i+1] = coefficients[i];
            weibullCovariates[i+1] = covariates[i];
        }
        return super.tableProject(evidenceCase, inferenceOptions, weibullCoeficients, weibullCovariates);
    }

    @Override
    public Potential copy() {
        return new ExponentialHazardPotential(this);
    }

    @Override
    public String toString() {
        return super.toShortString() + " = Hazard (Exponential)";
    }    
}
