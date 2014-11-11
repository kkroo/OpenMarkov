/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.inference.heuristic.simpleElimination;

import java.util.List;

import javax.swing.event.UndoableEditEvent;

import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.heuristic.EliminationHeuristic;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;

/**
 * This heuristic chooses a <code>ProbNode</code> to eliminate. The rule is:
 * Choose the node with fewer neighbors.
 * 
 * @author manuel
 * @author fjdiez
 */
public class SimpleElimination extends EliminationHeuristic {

    /**
     * @param probNet
     * @param queryVariables
     */
    public SimpleElimination(ProbNet probNet, List<List<Variable>> queryVariables) {
        super(probNet, queryVariables);
    }

    @Override
    /** This method returns the <code>ProbNode</code> that fulfils the rule 
     * defined in the heuristic: It chooses the node with less neighbours
     * @see openmarkov.inference.CanoMoralElimination#getNextNodeToDelete()
     * @see openmarkov.inference.EliminationHeuristic#getNodeToDelete() */
    public Variable getVariableToDelete() {
        ProbNode bestNode = null;
        int numNeighborsBestNode = Integer.MAX_VALUE;
        for (int i = nodesToEliminate.size() - 1; i >= 0 && bestNode == null; i--) {
            for (ProbNode node : nodesToEliminate.get(i)) {
                if (node != null) {
                    int numSiblings = node.getNode().getNumSiblings();
                    if (numSiblings < numNeighborsBestNode) {
                        bestNode = node;
                        numNeighborsBestNode = numSiblings;
                    }
                }
            }
        }
        return (bestNode!=null) ? bestNode.getVariable() : null;
    }

    @Override
    public void undoableEditWillHappen(UndoableEditEvent event)
            throws ConstraintViolationException, CanNotDoEditException,
            NonProjectablePotentialException, WrongCriterionException {
        // TODO Auto-generated method stub

    }

    @Override
    public void undoEditHappened(UndoableEditEvent event) {
        // TODO Auto-generated method stub

    }

}
