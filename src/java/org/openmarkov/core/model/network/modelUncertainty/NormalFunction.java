/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network.modelUncertainty;

import java.util.Random;

public class NormalFunction extends ProbDensFunction {
    private double                 mu;
    private double                 sigma;
    private StandardNormalFunction standard;

    public NormalFunction() {
        this(0.0, 1.0);
    }

    public NormalFunction(double mu, double sigma) {
        this.mu = mu;
        this.sigma = sigma;
        standard = new StandardNormalFunction();
    }

    @Override
    public void setParameters(double[] args) {
        mu = args[0];
        sigma = args[1];
    }

    @Override
    public boolean verifyParametersDomain(boolean isChanceVariable) {
        return (sigma > 0);
    }

    @Override
    public double[] getParameters() {
        double[] a = new double[2];
        a[0] = mu;
        a[1] = sigma;
        return a;
    }

    @Override
    public double getMaximum() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getMean() {
        // TODO Auto-generated method stub
        return mu;
    }

    @Override
    public double getSample(Random randomGenerator) {
        return sigma * standard.getSample(randomGenerator) + mu;
    }

    @Override
    public double getVariance() {
        return Math.pow(sigma, 2.0);
    }
}
