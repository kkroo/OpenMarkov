/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network.modelUncertainty;

import java.util.Random;

public class ErlangFunction extends ProbDensFunction
{
    private int    k;
    private double lambda;
    private ExponentialFunction exponentialFunction; 
    
    /**
     * @param type
     * @param lambda
     */
    public ErlangFunction (int k, double lambda)
    {
        this.k = k;
        this.lambda = lambda;
        this.exponentialFunction = new ExponentialFunction(lambda);
    }

    public ErlangFunction ()
    {
        this(0 ,0.0);
    }

    @Override
    public double[] getParameters ()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setParameters (double[] args)
    {
    	  k = (int) Math.round(args[0]);
          lambda = args[1];
          exponentialFunction = new ExponentialFunction(lambda);
    }

    @Override
    public boolean verifyParametersDomain (boolean isChanceVariable)
    {
        return (k >= 0) && (lambda > 0);
    }

    @Override
    public double getMean ()
    {
        return k / lambda;
    }

    @Override
    public double getMaximum ()
    {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getSample (Random randomGenerator)
    {
        double sumSamples;
        sumSamples = 0.0;
        for (int i = 0; i < k; i++)
        {
            sumSamples = sumSamples + exponentialFunction.getSample (randomGenerator);
        }
        return sumSamples;
    }

    @Override
    public double getVariance ()
    {
        return k / Math.pow (lambda, 2.0);
    }
}
