/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.learning.metric.k2;


import org.openmarkov.learning.algorithm.scoreAndSearch.metric.annotation.MetricType;
import org.openmarkov.learning.metric.bayesian.BayesianMetric;

/** This class implements the K2 metric. Note that the K2 metric is
 * exactly the BayesianMetric with parameter alpha set to 1.
 * @author joliva
 * @author manuel
 * @author fjdiez
 * @version 1.0
 * @since OpenMarkov 1.0 */
@MetricType(name = "K2")
public class K2Metric extends BayesianMetric {
    
    //Constructor
    /**
     * After constructing the metric, we evaluate the given net.
     * @param probNet <code>ProbNet</code> to evaluate.
     * @param cases <code>double[][]</code> database cases.
     * @throws openmarkov.exceptions.NotEnoughMemoryException
     */
    public K2Metric() {
        super(1);
    }
}
