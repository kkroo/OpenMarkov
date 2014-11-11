/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.inference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import org.openmarkov.core.exception.WrongGraphStructureException;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;

/**
 * Stores decision nodes and random variables in a list of sets. Used by
 * variable elimination algorithm.
 * <p>
 * Algorithm to get the partial order:
 * <ol>
 * <li>Gets decision in order:
 * <ol type="a">
 * <li>Gets a random node
 * <li>Recursively gets all the parents of that node
 * <li>If a Node has no parents:
 * <ol type="i">
 * <li>If it is a Decision node stores it in <code>decisions</code>
 * <li>In any case removes Node
 * </ol>
 * <li>If there is at least one decision node left go to <code>a.</code>
 * </ol>
 * <li>For each decision in <code>decisions</code>:
 * <ol type="a">
 * <li>Gets the chance nodes predecessors of each decision and creates two
 * <code>ArrayList</code>, one for the variable and the other one for the
 * predecessors chance nodes.
 * <li>Stores them in <code>order</code>
 * </ol>
 * <li>Remaining chance nodes are the last variables in <code>order</code>
 * because they are no parents of decisions.
 * </ol>
 */
public class PartialOrder {

    /** A partial order is a list of lists of variables. */
    private List<List<Variable>> order;

    public List<List<Variable>> getOrder() {
        return order;
    }

    public void setOrder(List<List<Variable>> order) {
        this.order = order;
    }

    /**
     * Builds the list.
     * 
     * @param id
     *            . <code>ProbNet</code>
     */
    public PartialOrder(ProbNet id) throws WrongGraphStructureException {
        calculatePartialOrder(id);
    }

    /**
     * @param id
     * @return <code>ArrayList</code> of <code>ArrayList</code> of
     *         <code>Variables</code>.
     */
    private void calculatePartialOrder(ProbNet id) {
        ProbNet idCopy = id.copy(); // Copy influence diagram

        // Get decisions (only) in elimination order
        int numDecisions = idCopy.getNumNodes(NodeType.DECISION);
        Stack<Variable> decisions = new Stack<Variable>();
        do {
            List<ProbNode> probNodes = idCopy.getProbNodes();
            for (ProbNode probNode : probNodes) {
                if (probNode.getNode().getNumChildren() == 0) {
                    if (probNode.getNodeType() == NodeType.DECISION) {
                        decisions.push(probNode.getVariable());
                        numDecisions--;
                    }
                    idCopy.removeProbNode(probNode);
                }
            }
        } while (numDecisions > 0);

        // Create elimination order adding chance nodes
        order = new ArrayList<>(numDecisions * 2 + 1);
        List<ProbNode> chanceProbNodes = id.getProbNodes(NodeType.CHANCE);
        HashSet<Variable> chanceVariables = new HashSet<Variable>();
        for (ProbNode chanceProbNode : chanceProbNodes) {
            chanceVariables.add(chanceProbNode.getVariable());
        }
        while (!decisions.empty()) {
            Variable decision = decisions.pop();
            ProbNode decisionProbNode = id.getProbNode(decision);
            List<Node> decisionNodeParents = decisionProbNode.getNode().getParents();
            // Get ProbNodes of the decision parents
            List<ProbNode> decisionProbNodeParents = new ArrayList<ProbNode>(
                    decisionNodeParents.size());
            for (Node node : decisionNodeParents) {
                ProbNode probNode = (ProbNode) node.getObject();
                if (probNode.getNodeType() != NodeType.DECISION) {
                    if (chanceVariables.contains(probNode.getVariable())) {
                        decisionProbNodeParents.add(probNode);
                        chanceVariables.remove(probNode.getVariable());
                    }
                }
            }
            // Add parents and decision
            int numParents = decisionProbNodeParents.size();
            if (numParents > 0) {
                List<Variable> decisionVariableParents = new ArrayList<Variable>(numParents);
                for (ProbNode parent : decisionProbNodeParents) {
                    decisionVariableParents.add(parent.getVariable());
                }
                order.add(decisionVariableParents);
            }
            // Add decision variable
            order.add(Arrays.asList(decision));
        }
        List<Variable> remainingVariables = new ArrayList<Variable>(chanceVariables.size());
        for (Variable remainingVariable : chanceVariables) {
            remainingVariables.add(remainingVariable);
        }
        order.add(remainingVariables);

    }

    /**
     * @param order
     * @param queryVariables
     * @param evidenceVariables
     * @param conditioningVariables
     * @return An order that has been pruned by eliminating the variables that
     *         are not in 'variables
     */
    public List<List<Variable>> projectPartialOrder(List<Variable> queryVariables,
            List<Variable> evidenceVariables, List<Variable> conditioningVariables) {
        List<List<Variable>> newOrder;
        List<List<Variable>> newOrder2;
        // Remove variables
        newOrder = new ArrayList<>();
        for (int i = 0; i < order.size(); i++) {
            List<Variable> auxArray = order.get(i);
            List<Variable> cloneAuxArray;
            cloneAuxArray = new ArrayList<Variable>(auxArray);
            for (Variable auxVar : auxArray) {
                if ((queryVariables.contains(auxVar) || evidenceVariables.contains(auxVar) || conditioningVariables
                        .contains(auxVar))) {
                    cloneAuxArray.remove(auxVar);
                }
            }
            newOrder.add(cloneAuxArray);

        }
        // Copy the non empty array lists
        newOrder2 = new ArrayList<>();

        for (List<Variable> auxArray : newOrder) {
            if (auxArray.size() > 0) {
                newOrder2.add(auxArray);
            }
        }
        return newOrder2;
    }

    /** @return A <code>String</code> with an array of arrays. */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        int numArrays = order.size();
        for (int i = 0; i < numArrays; i++) {
            List<Variable> array = order.get(i);
            int arraySize = array.size();
            if (arraySize > 1) {
                buffer.append("{");
            }
            for (int j = 0; j < arraySize; j++) {
                buffer.append(array.get(j));
                if (j < arraySize - 1) {
                    buffer.append(", ");
                }
            }
            if (arraySize > 1) {
                buffer.append("}");
            }
            if (i < numArrays - 1) {
                buffer.append(", ");
            }
        }
        return buffer.toString();
    }

    public int getNumVariables() {
        int num = 0;

        if (order != null) {
            for (List<Variable> auxArray : order) {
                if (auxArray != null) {
                    num = num + auxArray.size();
                }
            }
        } else {
            num = 0;
        }
        return num;
    }

}
