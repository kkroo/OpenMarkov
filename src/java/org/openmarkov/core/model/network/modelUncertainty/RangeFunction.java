/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network.modelUncertainty;

@ProbDensFunctionType(name="Range", parameters = {"lower bound", "upper bound"})
public class RangeFunction extends ProbDensFunctionWithKnownInverseCDF
{
    private double lowerBound;
    private double upperBound;

    public RangeFunction ()
    {
        this (0.0, 1.0);
    }

    /**
     * @param type
     * @param lowerBound
     * @param upperBound
     */
    public RangeFunction (double lowerBound, double upperBound)
    {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public void setParameters (double[] params)
    {
        lowerBound = params[0];
        upperBound = params[1];
    }

    @Override
    public boolean verifyParametersDomain (boolean isChanceVariable)
    {
        return ((0 <= lowerBound) && (lowerBound < upperBound) && (upperBound <= 1) && isChanceVariable)
               || ((lowerBound < upperBound) && !isChanceVariable);
    }

    @Override
    public double[] getParameters ()
    {
        return new double[]{lowerBound, upperBound};
    }

    @Override
    public double getMaximum ()
    {
        return upperBound;
    }

    @Override
    public double getMean ()
    {
        return (lowerBound + upperBound) / 2;
    }

    @Override
    public double getInverseCumulativeDistributionFunction (double y)
    {
        return lowerBound + (upperBound - lowerBound) * y;
    }

    @Override
    public double getVariance ()
    {
        return Math.pow (upperBound - lowerBound, 2.0) / 12;
    }
}
