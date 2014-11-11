/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.learning.algorithm.scoreAndSearch;

import org.openmarkov.core.io.database.CaseDatabase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.learning.algorithm.scoreAndSearch.metric.Metric;
import org.openmarkov.learning.core.algorithm.LearningAlgorithm;

/** This class implements the basic structure of any algorithm based on the
 * score-and-search approach.
 * The particular behavior of each algorithm is given by the 
 * <code>editionsGenerator</code> class.
 * @author joliva
 * @author manuel
 * @author fjdiez
 * @version 1.0
 * @since OpenMarkov 1.0 */
public abstract class ScoreAndSearchAlgorithm extends LearningAlgorithm {
    
    /** Metric used as heuristic */
    protected Metric metric;    
    
    /**
     * Constructor for ScoreAndSearchAlgorithm.
     * @param learnedNet
     * @param modelNet
     * @param editionsGenerator
     * @param cases
     */
    public ScoreAndSearchAlgorithm (ProbNet probNet, 
                                    CaseDatabase caseDatabase,
                                    Metric metric,
                                    Double alpha)
    {
        super (probNet, caseDatabase, alpha);
        this.metric = metric;
        this.metric.init (probNet, caseDatabase.getCases ());
    }

}
