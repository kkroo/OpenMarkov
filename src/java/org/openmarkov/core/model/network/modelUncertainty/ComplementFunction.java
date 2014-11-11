/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network.modelUncertainty;

import java.util.Random;

@ProbDensFunctionType(name="Complement", isValidForNumeric = false, parameters = {"nu"})
public class ComplementFunction extends ProbDensFunction
{
    private double nu;

    public ComplementFunction ()
    {
        this.nu = 0;
    }

    public ComplementFunction (double nu)
    {
        this.nu = nu;
    }

    public double getNu ()
    {
        return nu;
    }
    
    @Override
    public void setParameters (double[] params)
    {
        nu = params[0];
    }

    @Override
    public boolean verifyParametersDomain (boolean isChanceVariable)
    {
        return (nu > 0);
    }

    @Override
    public double[] getParameters ()
    {
        return new double[]{nu};
    }

    @Override
    public double getMaximum ()
    {
        return 1;
    }

    @Override
    public double getMean ()
    {
        return nu;
    }

    @Override
    public double getSample (Random randomGenerator)
    {
        return nu;
    }

    @Override
    public double getVariance ()
    {
        // TODO Auto-generated method stub
        return 0;
    }
}
