/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network.modelUncertainty;

import java.util.Random;

@ProbDensFunctionType(name="LogNormal", isValidForProbabilities = false, parameters = {"mu", "sigma"})
public class LogNormalFunction extends ProbDensFunction
{
    private double         mu;
    private double         sigma;
    /**
     * Auxiliary normal distribution used for sampling
     */
    private NormalFunction normal;

    public LogNormalFunction ()
    {
        this(0.0, 1.0);
    }
    
    public LogNormalFunction (double mu, double sigma)
    {
        this.mu = mu;
        this.sigma = sigma;
    }

    @Override
    public void setParameters (double[] args)
    {
        mu = args[0];
        sigma = args[1];
        normal = new NormalFunction (mu, sigma);
    }

    @Override
    public boolean verifyParametersDomain (boolean isChanceVariable)
    {
        return (sigma > 0);
    }

    @Override
    public double[] getParameters ()
    {
        return new double[]{mu, sigma};
    }

    @Override
    public double getMaximum ()
    {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getMean ()
    {
        return Math.exp (mu + Math.pow (sigma, 2.0) / 2.0);
    }

    @Override
    public double getSample (Random randomGenerator)
    {
        return Math.exp (normal.getSample (randomGenerator));
    }

    @Override
    public double getVariance ()
    {
        double squareSigma = Math.pow (sigma, 2.0);
        return (Math.exp (squareSigma) - 1) * Math.exp (2 * mu + squareSigma);
    }
}
