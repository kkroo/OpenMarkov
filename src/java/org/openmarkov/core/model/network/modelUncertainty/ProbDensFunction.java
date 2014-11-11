/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network.modelUncertainty;

import java.util.Random;

public abstract class ProbDensFunction
{
    public abstract double[] getParameters ();

    public abstract void setParameters (double[] args);

    public abstract boolean verifyParametersDomain (boolean isChanceVariable);

    public abstract double getMean ();

    public final double getStandardDeviation ()
    {
        return Math.sqrt (getVariance ());
    }

    public abstract double getVariance ();

    public abstract double getMaximum ();

    public abstract double getSample (Random randomGenerator);
    
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		ProbDensFunctionType probDensAnnotation = getClass().getAnnotation(ProbDensFunctionType.class);
		if(probDensAnnotation != null)
		{
			sb.append(probDensAnnotation.name());
			sb.append(" :");
		}
		for(double parameter : getParameters())
		{
			sb.append(parameter + " ");
		}
		return sb.toString();
	}    
}
