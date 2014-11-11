/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.learning.metric.entropy;

import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.learning.algorithm.scoreAndSearch.metric.Metric;
import org.openmarkov.learning.algorithm.scoreAndSearch.metric.annotation.MetricType;

/** This class implements the Entropy metric.
 * @author joliva
 * @author manuel
 * @author fjdiez
 * @version 1.0
 * @since OpenMarkov 1.0 */
@MetricType(name = "Entropy")
public class EntropyMetric extends Metric {
	
    //Constructor
    /**
     * After constructing the metric, we evaluate the given net.
     * @param probNet <code>ProbNet</code> to evaluate.
     * @param cases <code>double[][]</code> database cases.
     * @throws openmarkov.exceptions.NotEnoughMemoryException
     */
    public EntropyMetric() {
        super();
    }

	@Override
	public double score(TablePotential tablePotential) {
		double n_ij;
		double n_ijk;
		int position = 0;
		int numStates = tablePotential.getVariable(0).getNumStates();
		double[] freq = tablePotential.getValues();
		double nodeEntropy = 0.0;

		while (position < freq.length) {
			n_ij = 0;
			for (int k = 0; k < numStates; k++)
				n_ij += freq[position + k];

			for (int k = 0; k < numStates; k++) {
				n_ijk = freq[position];
				if (n_ijk > 0) {
					nodeEntropy += n_ijk * Math.log(n_ijk / n_ij);
				}
				position++;
			}
		}
		return nodeEntropy;
	}
}
