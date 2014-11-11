/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network.modelUncertainty;


@ProbDensFunctionType(name="Gamma", isValidForProbabilities = false, parameters = {"k", "theta"})
public class GammaFunction extends GammaAbstract
{
    private double k;
    private double theta;

    /**
     * @param k
     * @param theta
     */
    public GammaFunction (double k, double theta)
    {
        this.k = k;
        this.theta = theta;
        this.kAbstract = k;
        this.thetaAbstract = theta;
    }

    public GammaFunction ()
    {
        this (0.0, 0.0);
    }

    @Override
    public void setParameters (double[] parameters)
    {
        k = parameters[0];
        theta = parameters[1];
        this.kAbstract = k;
        this.thetaAbstract = theta;
    }

    @Override
    public boolean verifyParametersDomain (boolean isChanceVariable)
    {
        return (k > 0) && (theta > 0);
    }

    @Override
    public double[] getParameters ()
    {
        double[] a = new double[2];
        a[0] = k;
        a[1] = theta;
        return a;
    }
}
