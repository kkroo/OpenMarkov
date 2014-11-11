package org.openmarkov.core.gui.costeffectiveness.heuristic;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.UndoableEditEvent;

import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.heuristic.EliminationHeuristic;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;

public class MPADHeuristic extends EliminationHeuristic {

    List<Variable> eliminationOrder;

    public MPADHeuristic(ProbNet probNet, List<List<Variable>> variablesToEliminate) {
        super(probNet, variablesToEliminate);
        eliminationOrder = getEliminationOrder(probNet, variablesToEliminate);
    }

    /**
     * Selects the variables that creates the minimum clique size when are
     * removed.
     */
    private List<Variable> getEliminationOrder(ProbNet probNet,
            List<List<Variable>> variablesToEliminate) {
        int numVariablesToEliminate = variablesToEliminate.size();
        List<Variable> plainVariableList = new ArrayList<>();
        for (List<Variable> variables : variablesToEliminate) {
            plainVariableList.addAll(variables);
        }

        List<Variable> eliminationOrder = new ArrayList<Variable>(numVariablesToEliminate);

        // In each iteration, select the variable that creates the
        // minimum clique size when is removed
        for (int i = 0; i < numVariablesToEliminate; i++) {
            int minClusterSize = Integer.MAX_VALUE;
            Variable candidateToRemove = plainVariableList.get(0);
            for (Variable variable : plainVariableList) {
                ProbNode probNode = probNet.getProbNode(variable);
                List<Node> neighbors = probNode.getNode().getNeighbors();
                int clusterSize = 1;
                for (Node node : neighbors) {
                    // Calculates clique size created removing a variable
                    ProbNode neighborProbNode = (ProbNode) node.getObject();
                    Variable neighborVariable = neighborProbNode.getVariable();
                    if (neighborVariable.getVariableType() != VariableType.NUMERIC) {
                        clusterSize *= neighborVariable.getNumStates();
                    }
                }
                if ((clusterSize < minClusterSize) && (probNode.getNodeType() == NodeType.UTILITY)) {
                    candidateToRemove = variable;
                }
            }
            eliminationOrder.add(candidateToRemove);
            variablesToEliminate.remove(candidateToRemove);
            probNet.removeProbNode(probNet.getProbNode(candidateToRemove));
        }
        return eliminationOrder;
    }

    public void undoableEditHappened(UndoableEditEvent event) {
        Variable removedVariable = getEventVariable(event);

        if (removedVariable != null) {
            eliminationOrder.remove(removedVariable);
            // Eliminate node from variablesToEliminate
            ProbNode toEliminateNode = probNet.getProbNode(removedVariable);
            variablesToEliminate.remove(removedVariable);
            probNet.removePotentials(toEliminateNode);
            probNet.removeProbNode(toEliminateNode);
        }
    }

    @Override
    public Variable getVariableToDelete() {
        Variable variableToDelete = null;
        if (eliminationOrder.size() > 0) {
            variableToDelete = eliminationOrder.get(0);
        }
        return variableToDelete;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        int size = eliminationOrder.size();
        for (int i = 0; i < size; i++) {
            buffer.append(eliminationOrder.get(i).getName());
            if (i < (size - 1)) {
                buffer.append(", ");
            }
        }
        return buffer.toString();
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
