/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network.modelUncertainty;

import java.util.Random;

@ProbDensFunctionType(name="Exact", isValidForNumeric = true, parameters = {"nu"})
public class ExactFunction extends ProbDensFunction
{
    private double nu;

    public ExactFunction ()
    {
    }

    public ExactFunction (double nu)
    {
        this.nu = nu;
    }
    
    @Override
    public void setParameters (double[] params)
    {
        nu = params[0];
    }

    @Override
    public boolean verifyParametersDomain (boolean isChanceVariable)
    {
        return ((!isChanceVariable) || ((0 <= nu) && (nu <= 1)));
    }

    /**
     * Some subclasses can override this method.
     * @return
     */
    public double getMean ()
    {
        return nu;
    }

    @Override
    public double[] getParameters ()
    {
        double[] a = new double[1];
        a[0] = nu;
        return a;
    }

    @Override
    public double getMaximum ()
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
        return 0;
    }
}
