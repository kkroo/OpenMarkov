/*
 * Copyright 2012 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.gui.costeffectiveness;

/**
 * Each intervention has a name, a cost and an effectiveness. An intervention
 * represents an strategy, which is a possible configuration of the policies of
 * the decision nodes
 * 
 * @author myebra
 * 
 */
public class Intervention {

    // Attributes
    public String name;

    public double cost;

    public double effectiveness;

    // ICER = incremental cost-effectiveness ratio
    public double iCER = 0;

    /**
     * @param name
     *            . <code>String</code>
     * @param cost
     *            . <code>double</code>
     * @param effectiveness
     *            . <code>double</code>
     */
    public Intervention(String name, double cost, double effectiveness) {
        this.name = name;
        this.cost = cost;
        this.effectiveness = effectiveness;
    }

    /**
     * Calculates the incremental CE Ratio from this intervention to a reference
     * intervention.
     * 
     * @param referenceIntervention
     *            . <code>Intervention</code>
     */
    public void calculateICER(Intervention referenceIntervention) {
        iCER = (cost - referenceIntervention.cost)
                / (effectiveness - referenceIntervention.effectiveness);

    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public double getEffectiveness() {
        return effectiveness;
    }

    public double getICER() {
        return iCER;
    }

    public String toString() {
        return new String("Intervention: " + name + "; cost = " + cost + "; effectiveness = "
                + effectiveness);
    }

}
