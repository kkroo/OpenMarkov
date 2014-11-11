/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network.modelUncertainty;

import java.util.List;

public class ComplementFamily extends FamilyDistribution {

    /**
     * Probability mass for this family
     */
    private double probMass;

    public double getProbMass() {
        return probMass;
    }

    public void setProbMass(double probMass) {
        this.probMass = probMass;
    }

    public ComplementFamily(List<UncertainValue> uncertainValues) {
        super(filterByFunction(ComplementFunction.class, uncertainValues));
    }

    public ComplementFamily() {
        // super(TypeProbDensityFunction.COMPLEMENT);
    }

    public double[] getMean() {

        int sizeFamily = family.size();
        double[] nu = new double[sizeFamily];

        for (int i = 0; i < sizeFamily; i++) {
            nu[i] = ((ComplementFunction) (family.get(i).getProbDensFunction())).getNu();
        }
        return Tools.normalize(nu, probMass);
    }

    public double[] getSample() {

        int sizeFamily = family.size();
        double[] nu = new double[sizeFamily];

        for (int i = 0; i < sizeFamily; i++) {
            nu[i] = ((ComplementFunction) (family.get(i).getProbDensFunction())).getNu();
        }
        return Tools.normalize(nu, probMass);
    }

}
