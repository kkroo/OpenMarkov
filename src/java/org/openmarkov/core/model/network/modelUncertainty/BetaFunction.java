/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network.modelUncertainty;

import java.util.Random;

@ProbDensFunctionType(name = "Beta", parameters = { "alpha", "beta" })
public class BetaFunction extends ProbDensFunction {
    private double alpha;
    private double beta;

    public BetaFunction() {
        this.alpha = 0;
        this.beta = 0;        
    }

    public BetaFunction(double alpha, double beta) {
        this.alpha = alpha;
        this.beta = beta;
    }

    @Override
    public void setParameters(double[] params) {
        alpha = params[0];
        beta = params[1];
    }

    @Override
    public boolean verifyParametersDomain(boolean isChanceVariable) {
        return ((alpha > 0) && (beta > 0));
    }

    @Override
    public double[] getParameters() {
        double[] a = new double[2];
        a[0] = alpha;
        a[1] = beta;
        return a;
    }

    @Override
    public double getMaximum() {
        return 1;
    }

    @Override
    public double getMean() {
        return alpha / (alpha + beta);
    }

    @Override
    public double getSample(Random randomGenerator) {
        double[] alphas = new double[] { alpha, beta };
        // We use a Dirichlet family for obtaining the sample
        DirichletFamily family = new DirichletFamily(alphas);
        return family.getSample(randomGenerator)[0];
    }

    @Override
    public double getVariance() {
        double sumAlphaBeta = alpha + beta;
        return (alpha * beta) / (Math.pow(sumAlphaBeta, 2.0) * (sumAlphaBeta + 1));
    }
}
