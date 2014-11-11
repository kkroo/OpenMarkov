/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network.modelUncertainty;

import java.util.Random;

public abstract class GammaAbstract extends ProbDensFunction {
    protected double kAbstract;
    protected double thetaAbstract;

    @Override
    public final double getMaximum() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public final double getMean() {
        return (kAbstract * thetaAbstract);
    }

    @Override
    public final double getSample(Random randomGenerator) {
        double sample;
        double r;
        double lambdaErlang;
        double u;
        double epsilon = 0.00001;
        int kForSampling;
        lambdaErlang = 1.0 / thetaAbstract;
        // Integer part of kAbstract
        kForSampling = (int) (Math.ceil(kAbstract));
        if (!isAnErlangFunction(epsilon)) {
            r = kForSampling - kAbstract;
            u = (new RangeFunction(0.0, 1.0)).getSample(randomGenerator);
            if (u < r) {
                kForSampling = kForSampling - 1;
            }
        }
        sample = (new ErlangFunction(kForSampling, lambdaErlang)).getSample(randomGenerator);
        return sample;
    }

    public boolean isAnErlangFunction(double epsilon) {
        return (Math.abs(kAbstract - Math.ceil(kAbstract))) < epsilon;
    }

    @Override
    public final double getVariance() {
        return kAbstract * Math.pow(thetaAbstract, 2.0);
    }
}
