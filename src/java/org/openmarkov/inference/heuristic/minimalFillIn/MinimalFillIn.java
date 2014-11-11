/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.inference.heuristic.minimalFillIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.event.UndoableEditEvent;

import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.heuristic.EliminationHeuristic;
import org.openmarkov.core.model.graph.Graph;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;

/** Chooses the node that adds less links in network when eliminated.
 * @author marias */
public class MinimalFillIn extends EliminationHeuristic {

	// Attributes
	/** Stores a coy of the <code>ProbNode</code> <code>Graph</code> */
	private Graph graph;
	
	/** Localize each <code>Node</code> in the <code>Graph</code> given a <code>Variable</code> */
	private HashMap<Variable, Node> variablesNodes;
	
	// Constructor
	public MinimalFillIn(ProbNet probNet, 
			List<List<Variable>> variablesToEliminate) {
		super(probNet, variablesToEliminate);
		graph = probNet.getGraph().copy();
		variablesNodes = new HashMap<Variable, Node>(); 
		List<Node> nodes = graph.getNodes();
		for (Node node : nodes) {
			// Sets the variable as object
			Variable variable = ((ProbNode)node.getObject()).getVariable(); 
			node.setObject(variable);
			variablesNodes.put(variable, node);
		}
	}

	// Methods
	/** @return Variable with minimal fill-in. */
	public Variable getVariableToDelete() {
		int numVariablesToEliminate = variablesToEliminate.size();
		List<Variable> lastArray = null;
		do {
			lastArray = variablesToEliminate.get(--numVariablesToEliminate);
		} while (lastArray.size() == 0 && numVariablesToEliminate > 0);
		
		List<Node> nodesToDelete = new ArrayList<Node>();
		for (Variable variable : lastArray) {
			nodesToDelete.add(variablesNodes.get(variable));
		}

		int minimalFillIn = Integer.MAX_VALUE;
		Node minNode = null;
		int numNodesToDelete = nodesToDelete.size();
		for (int i = 0; i < numNodesToDelete && minimalFillIn > 0; i++) {
			Node node = nodesToDelete.get(i);
			int fillIn = getFillIn(node);
			if (fillIn < minimalFillIn) {
				minimalFillIn = fillIn;
				minNode = node;
			}
		}
		
		Variable variable = null;
		if (minNode != null) {
			variable = (Variable)(minNode.getObject());
		}
		return variable;
	}

	/** @param node. <code>Node</code>
	 * @return Fill-in of node = number of links that need to be added to the 
	 *  graph due to its elimination. */
	private int getFillIn(Node node) {
		List<Node> neighbors = node.getNeighbors();
		int fillIn = 0;
		int numNeighbors = neighbors.size();
		for (int i = 0; i < numNeighbors - 1; i++) {
			Node nodeI = neighbors.get(i);
			for (int j = i + 1; j < numNeighbors; j++) {
				Node nodeJ = neighbors.get(j);
				if (!nodeI.isNeighbor(nodeJ)) {
					fillIn++;
				}
			}
		}
		return fillIn;
	}

	@Override
	public void undoableEditWillHappen(UndoableEditEvent event)
			throws ConstraintViolationException, CanNotDoEditException,
			NonProjectablePotentialException, WrongCriterionException {
		// Does nothing
	}

	@Override
	public void undoableEditHappened(UndoableEditEvent event) {
		super.undoableEditHappened(event);
		Variable variable = getEventVariable(event);
		// Create links in graph
		Node nodeToRemove = variablesNodes.get(variable);
		List<Node> neighborsOfNodeToRemove = nodeToRemove.getNeighbors();
		int numNeighbors = neighborsOfNodeToRemove.size();
		for (int i = 0; i < numNeighbors - 1; i ++) {
			Node neighborI = neighborsOfNodeToRemove.get(i);
			for (int j = i + 1; j < numNeighbors; j++) {
				Node neighborJ = neighborsOfNodeToRemove.get(j);
				if (!neighborI.isNeighbor(neighborJ)) {
					graph.addLink(neighborI, neighborJ, false);
				}
			}
		}
		// Update internal data structures
		graph.removeNode(variablesNodes.get(variable));
		variablesNodes.remove(variable);
	}
	
	public void undoEditHappened(UndoableEditEvent event) {
	}
	
}
