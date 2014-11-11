/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/


package org.openmarkov.learning.metric.bayesian;

import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.learning.algorithm.scoreAndSearch.metric.Metric;
import org.openmarkov.learning.algorithm.scoreAndSearch.metric.annotation.MetricType;
import org.openmarkov.learning.algorithm.scoreAndSearch.metric.util.MathUtils;

/**
 * This class implements the Bayesian metric.
 * @author joliva
 * @author manuel
 * @author fjdiez
 * @author ibermejo
 * @version 1.0
 * @since OpenMarkov 1.0
 */
@MetricType(name = "Bayesian")
public class BayesianMetric extends Metric
{
    //Members
    /** Parameter alpha */
    protected double alpha = 0.5;    
    
    // Constructor
    /**
     * After constructing the metric, we evaluate the given net.
     * @param probNet <code>ProbNet</code> to evaluate.
     * @param cases <code>double[][]</code> database cases.
     * @param alpha <code>double</code> alpha parameter.
     */
    public BayesianMetric (double alpha)
    {
        this.alpha = alpha;
    }

    @Override
    /**
     * Scores potential table
     * @return <code>double</code> score 
     */
    public double score (TablePotential tablePotential)
    {
        double nodeScore = 0;
        int numStates = tablePotential.getVariable (0).getNumStates ();
        double[] freq = tablePotential.getValues ();
        int position = 0;
        double n_ij;
        double n_ijk;

        while (position < freq.length)
        {
            n_ij = 0;
            // k-th state of the node
            for (int k = 0; k < numStates; k++)
            {
                if (alpha + freq[position] != 0)
                {
                    n_ijk = freq[position];
                    n_ij += n_ijk + alpha;
                    nodeScore += (MathUtils.lnGamma (alpha + n_ijk));
                }
                position++;
            }
            if (n_ij != 0) 
                nodeScore -= MathUtils.lnGamma (n_ij);
            if (alpha != 0)
            {
                nodeScore += MathUtils.lnGamma (numStates * alpha);
                nodeScore -= numStates * MathUtils.lnGamma (alpha);
            }
        }
        return nodeScore;
    }
}
