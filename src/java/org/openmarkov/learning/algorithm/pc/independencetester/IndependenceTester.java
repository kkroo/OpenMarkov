/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.learning.algorithm.pc.independencetester;

import java.util.List;

import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.ProbNet;

/**
 * This interface represents a general independence tester.
 * @author joliva
 *
 */
public interface IndependenceTester extends PNUndoableEditListener{
	
	/**
	 * Tests the dependency level of two variables.
     * @param learnedNet <code>ProbNet</code> learned net
	 * @param cases <code>int[][]</code> case database
	 * @param node2 <code>Node</code> second variable.
	 * @param adjacencySubset <code>ArrayList</code> of <code>Node</code> 
	 * representing the separation set (i.e. the conditional set).
	 * @return the score obtained in the independence test.
	 * @throws ProbNodeNotFoundException
	 */
    public double test (ProbNet learnedNet,
                        int[][] cases,
                        Node node1,
                        Node node2,
                        List<Node> adjacencySubset)
        throws ProbNodeNotFoundException;
}
