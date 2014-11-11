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
import java.util.List;
import java.util.Random;

import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.InferenceOptions;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.modelUncertainty.NormalFunction;
import org.openmarkov.core.model.network.modelUncertainty.XORShiftRandom;

public abstract class RegressionPotential extends Potential {
    public enum MatrixType {
        COVARIANCE, CHOLESKY
    };
    
    protected static final String CONSTANT = "Constant";
    
    /**
     * Covariates
     */
    protected String[] covariates;

    /**
     * Covariates processed as to be understood by jeval
     */
    protected String[] processedCovariates;

    /**
     * Coefficients for the parameters of the function
     */
    protected double[] coefficients;
    /**
     * Sampled coefficients
     */
    protected double[] sampledCoefficients;
    /**
     * Covariance matrix
     */
    protected double[] covarianceMatrix      = null;
    /**
     * Colesky decomposition
     */
    protected double[] choleskyDecomposition = null;

    public RegressionPotential(List<Variable> variables, PotentialRole role) {
        super(variables, role);
        this.sampledCoefficients = null;
        setCovariates(getDefaultCovariates(variables, role));
        setCoefficients(new double[covariates.length]);
    }

    public RegressionPotential(List<Variable> variables, PotentialRole role, String[] covariates,
            double[] coefficients) {
        super(variables, role);
        this.sampledCoefficients = null;
        setCoefficients(coefficients);
        setCovariates(covariates);
    }

    public RegressionPotential(List<Variable> variables, PotentialRole role, String[] covariates,
            double[] coefficients, double[] uncertaintyMatrix, MatrixType matrixType) {
        this(variables, role, covariates, coefficients);
        if (matrixType == MatrixType.COVARIANCE) {
            this.covarianceMatrix = uncertaintyMatrix;
            this.choleskyDecomposition = calculateCholesky(uncertaintyMatrix);
        } else {
            this.choleskyDecomposition = uncertaintyMatrix;
        }
    }

    public RegressionPotential(List<Variable> variables, PotentialRole role, String[] covariates,
            double[] coefficients, double[] covarianceMatrix) {
        this(variables, role, covariates, coefficients, covarianceMatrix, MatrixType.COVARIANCE);
    }

    public RegressionPotential(RegressionPotential potential) {
        super(potential);
        setCovariates(potential.covariates.clone());
        setCoefficients(potential.coefficients.clone());
        if(potential.covarianceMatrix != null)
        {
            setCovarianceMatrix(potential.covarianceMatrix.clone());
        }else if(potential.choleskyDecomposition != null)
        {
            setCholeskyDecomposition(potential.choleskyDecomposition.clone());
        }
        sampledCoefficients = potential.sampledCoefficients;

    }

    public String[] getCovariates() {
        return covariates;
    }

    public void setCovariates(String[] covariates) {
        this.covariates = covariates;
        this.processedCovariates = processCovariates(variables, covariates);
    }

    public double[] getCoefficients() {
        return coefficients;
    }

    public void setCoefficients(double[] coefficients) {
        this.coefficients = coefficients;
    }

    public double getGamma() {
        return coefficients[0];
    }

    public void setGamma(double gamma) {
        this.coefficients[0] = gamma;
    }

    public double getConstant() {
        return coefficients[1];
    }

    public void setConstant(double constant) {
        this.coefficients[1] = constant;
    }

    public double[] getCovarianceMatrix() {
        return covarianceMatrix;
    }

    public void setCovarianceMatrix(double[] covarianceMatrix) {
        this.covarianceMatrix = covarianceMatrix;
        this.choleskyDecomposition = calculateCholesky(covarianceMatrix);
    }

    public double[] getCholeskyDecomposition() {
        return choleskyDecomposition;
    }

    public void setCholeskyDecomposition(double[] choleskyDecomposition) {
        this.choleskyDecomposition = choleskyDecomposition;
    }

    @Override
    public boolean isUncertain() {
        return this.covarianceMatrix != null || this.choleskyDecomposition != null;
    }

    @Override
    public List<TablePotential> tableProject(EvidenceCase evidenceCase,
            InferenceOptions inferenceOptions,
            List<TablePotential> projectedPotentials)
            throws NonProjectablePotentialException, WrongCriterionException {
        double[] coefficients = (sampledCoefficients == null) ? this.coefficients
                : this.sampledCoefficients;
        return tableProject(evidenceCase, inferenceOptions, coefficients, processedCovariates);
    }

    protected abstract List<TablePotential> tableProject(EvidenceCase evidenceCase,
            InferenceOptions inferenceOptions,
            double[] coefficients,
            String[] covariates)
            throws NonProjectablePotentialException, WrongCriterionException;

    @Override
    public Potential sample() {
        if (choleskyDecomposition != null) {
            if (this.sampledCoefficients == null) {
                this.sampledCoefficients = new double[coefficients.length];
            }

            Random randomGenerator = new XORShiftRandom();
            NormalFunction normalDistribution = new NormalFunction(0, 1);
            double[] normalSamples = new double[coefficients.length];
            for (int i = 0; i < normalSamples.length; ++i) {
                double sample = normalDistribution.getSample(randomGenerator);
                normalSamples[i] = sample;
            }

            int index = 0;
            for (int i = 0; i < coefficients.length; ++i) {
                double value = 0.0;
                for (int j = 0; j <= i; ++j) {
                    value += choleskyDecomposition[index] * normalSamples[j];
                    index++;
                }
                sampledCoefficients[i] = value + coefficients[i];
            }
        }
        return this;
    }

    private static double[] calculateCholesky(double[] covarianceMatrix) {
        double[] cholesky = null;
        if (covarianceMatrix != null) {
            // Cholesky decomposition using the the Cholesky–Banachiewicz
            // algorithm
            cholesky = new double[covarianceMatrix.length];
            // Solve quadratic equation to get n, the number of coefficients
            int n = (int) (Math.sqrt(covarianceMatrix.length * 8 + 1) - 1) / 2;
            double[] diagonals = new double[n];
            int[] firstIndexOfRow = new int[n];
            int index = 0;
            for (int i = 0; i < n; ++i) {
                double sumOfSquares = 0.0;
                firstIndexOfRow[i] = index;
                for (int j = 0; j <= i; ++j) {
                    if (i == j) {
                        diagonals[i] = Math.sqrt(covarianceMatrix[index] - sumOfSquares);
                        cholesky[index] = diagonals[i];
                    } else {
                        double sumOfMul = 0.0;
                        for (int k = 0; k < j; ++k) {
                            sumOfMul += cholesky[firstIndexOfRow[i] + k]
                                    * cholesky[firstIndexOfRow[j] + k];
                        }
                        cholesky[index] = (covarianceMatrix[index] - sumOfMul) / diagonals[j];
                    }
                    sumOfSquares += Math.pow(cholesky[index], 2);
                    ++index;
                }
            }
        }
        return cholesky;
    }

    protected String[] shiftCovariates(String[] covariates,
            List<Variable> variables,
            List<Variable> shiftedVariables) {
        String[] shiftedCovariates = new String[covariates.length];
        for(int i=0; i<covariates.length; ++i)
        {
            String shiftedCovariate = covariates[i];
            for(int j=0; j< variables.size(); ++j)
            {
                shiftedCovariate = shiftedCovariate.replace(variables.get(j).getName(),
                        shiftedVariables.get(j).getName());
            }
            shiftedCovariates [i] = shiftedCovariate;
        }
        return shiftedCovariates;
    }
    
    private String[] processCovariates(List<Variable> variables, String[] covariates) {
        String[] processedCovariates = new String[covariates.length];

        for (int i = 0; i < covariates.length; ++i) {
            String covariate = covariates[i];
            for (Variable variable : variables) {
                covariate = covariate.replace(variable.getName(), "#{" + variable.getName() + "}");
            }
            processedCovariates[i] = covariate;
        }
        return processedCovariates;
    }
    
    public static String[] getMandatoryCovariates()
    {
        return new String[]{CONSTANT};
    }

    protected static String[] getDefaultCovariates(List<Variable> variables, PotentialRole role)
    {
        return getDefaultCovariates(variables, role, getMandatoryCovariates());
    }
    
    protected static String[] getDefaultCovariates(List<Variable> variables, PotentialRole role, String[] mandatoryCovariates) {
        int firstParentIndex = (role == PotentialRole.UTILITY)? 0 : 1;
        String[] covariates = new String[mandatoryCovariates.length + variables.size()-firstParentIndex];
        
        int j = 0;
        while(j< mandatoryCovariates.length)
        {
            covariates[j] = mandatoryCovariates[j];
            ++j;
        }
        for(int i = firstParentIndex; i< variables.size(); ++i)
        {
            covariates[j++] = variables.get(i).getName();
        }
        return covariates;
    }

    @Override
    public void shift(ProbNet probNet, int timeDifference)
            throws ProbNodeNotFoundException {
        List<Variable> unshiftedVariables = new ArrayList<>(variables);
        super.shift(probNet, timeDifference);
        setCovariates(shiftCovariates(covariates, unshiftedVariables, variables));
    }    
    
    
}
