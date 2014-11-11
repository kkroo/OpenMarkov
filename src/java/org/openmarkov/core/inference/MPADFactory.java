/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.inference;

import java.util.ArrayList;
import java.util.List;

import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.CycleLengthShift;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialType;
import org.openmarkov.core.model.network.potential.SameAsPrevious;

public class MPADFactory {
	// Attributes
	/** Vertical separation in pixels between slices. */
	private final double VERTICAL_OFFSET = 0;

	/** Horizontal separation between slices */
	private final double MARGIN_BETWEEN_SLICES = 150;

	private ProbNet probNet;
	/** Set of probNodes that will be cloned in each slice. */
	private List<ProbNode> generatedNodes;
	/** Each <ArrayList<ProbNode> contains the nodes of a time slice */
	private List<List<ProbNode>> classifiedNodes;

	// Constructor
	/**
	 * @param conciseNet
	 *            . <code>ProbNet</code>
	 * @param numSlices
	 *            . <code>int</code>
	 * @param simulationIndexVariable
	 *            . <code>Variable</code>
	 * @param coordinateXOffset
	 *            . <code>int</code>
	 */
	public MPADFactory(ProbNet conciseNet, int numSlices) {
		// probNet must be the original network and
		// expandedNetwork the probNet expanded numSlices times
		probNet = conciseNet.copy();
		// if some of the slices of the concise net miss a node present
		// in previous slices, adds the node to that slice
		compactNetwork();
		// expands the net
		while (classifiedNodes.size() <= numSlices) {
			generateNextSlice();
		}
	}

	// Methods
	public ProbNet getExtendedNetwork() {
		return probNet;
	}

	/**
	 * When invoking this method, probNet is a copy of the concise net. We add
	 * new nodes, links, and potentials to make it a compact net.
	 */
	private void compactNetwork() {
		classifiedNodes = classifyNodesbySlices(probNet, probNet.getVariables());
		// generate the new nodes of the compact net
		List<ProbNode> generatingNodes = new ArrayList<ProbNode>();
		generatedNodes = new ArrayList<ProbNode>();
		for (int slice = 0; slice < classifiedNodes.size() - 1; slice++) {
			double sliceWidth = getSliceWidth(classifiedNodes.get(slice));
			List<ProbNode> generatedNodesInThisSlice = new ArrayList<ProbNode>(classifiedNodes.get(
					slice).size());
			for (ProbNode generatingProbNode : classifiedNodes.get(slice)) {
				if (!probNet.containsShiftedVariable(generatingProbNode.getVariable(), 1)) {
					ProbNode newProbNode = probNet.addShiftedProbNode(generatingProbNode, 1,
							sliceWidth + MARGIN_BETWEEN_SLICES, VERTICAL_OFFSET);
					generatingNodes.add(generatingProbNode);
					generatedNodes.add(newProbNode);
					generatedNodesInThisSlice.add(newProbNode);
				}
			}
			for (ProbNode probNode : generatedNodesInThisSlice) {
				classifiedNodes.get(probNode.getVariable().getTimeSlice()).add(probNode);
			}
		}
		// assign potentials to the new nodes of the compact net
		ProbNode generatingNode, generatedNode;
		for (int i = 0; i < generatedNodes.size(); i++) {
			generatingNode = generatingNodes.get(i);
			generatedNode = generatedNodes.get(i);
			try {
				expandPotentialAndLinks(generatingNode, generatedNode, 1);
			} catch (ProbNodeNotFoundException e) {
				// If we get here is because we have not generated the nodes as
				// we should
				e.printStackTrace();
			}
		}
	}

	/**
	 * Assigns nodes to slices in a collection of slices. Each slice is a
	 * collection of nodes.
	 * 
	 * @return <code>List</code> of <code>List</code> of <code>ProbNode</code>
	 */
	private static List<List<ProbNode>> classifyNodesbySlices(ProbNet probNet,
			List<Variable> variables) {
		List<List<ProbNode>> classifiedNodes;
		int firstSliceIndex = Integer.MAX_VALUE;
		int lastSliceIndex = Integer.MIN_VALUE;
		// find the indexes of the first and last slice
		int timeSlice;
		for (Variable variable : variables) {
			if (variable.isTemporal()) {
				timeSlice = variable.getTimeSlice();
				if (timeSlice < firstSliceIndex) {
					firstSliceIndex = timeSlice;
				}
				if (timeSlice > lastSliceIndex) {
					lastSliceIndex = timeSlice;
				}
			}
		}
		int numSlices = lastSliceIndex - firstSliceIndex + 1;
		// initializes the variable classifiedNodes
		classifiedNodes = new ArrayList<>(numSlices);
		for (int slice = 0; slice < numSlices; slice++) {
			classifiedNodes.add(new ArrayList<ProbNode>());
		}
		// assigns each node to its slice
		Variable variable;
		for (ProbNode node : probNet.getProbNodes()) {
			variable = node.getVariable();
			if (variable.isTemporal()) {
				classifiedNodes.get(variable.getTimeSlice()).add(node);
			}
		}
		return classifiedNodes;
	}

	/**
	 * @precondition extendedNet in this class must be a compact net
	 */
	private void generateNextSlice() {
		List<ProbNode> lastSliceNodes = classifiedNodes.get(classifiedNodes.size() - 1);
		List<ProbNode> newSliceNodes = new ArrayList<ProbNode>();
		// generates the new nodes
		double sliceWidth = getSliceWidth(lastSliceNodes);
		for (ProbNode generatingProbNode : lastSliceNodes) {
			ProbNode newProbNode = probNet.addShiftedProbNode(generatingProbNode, 1, sliceWidth
					+ MARGIN_BETWEEN_SLICES, VERTICAL_OFFSET);
			newSliceNodes.add(newProbNode);
		}
		// generates new slices
		// assign potentials to the new nodes
		ProbNode generatingNode, generatedNode;
		for (int i = 0; i < lastSliceNodes.size(); i++) {
			generatingNode = lastSliceNodes.get(i);
			generatedNode = newSliceNodes.get(i);
			try {
				expandPotentialAndLinks(generatingNode, generatedNode, 1);
			} catch (ProbNodeNotFoundException e) {
				// If we get here is because we have not generated the nodes as
				// we should
				e.printStackTrace();
			}
		}
		classifiedNodes.add(newSliceNodes);
	}

	/**
	 * TODO document: oldNode is a node in the last slice of the compact net
	 * TODO We are assuming that there is only one potential per node. Revise
	 * 
	 * @throws ProbNodeNotFoundException
	 */
	private void expandPotentialAndLinks(ProbNode oldNode, ProbNode newNode, int timeDifference)
			throws ProbNodeNotFoundException {
		Potential oldPotential = oldNode.getPotentials().get(0);
		Potential newPotential = null;
		if (oldPotential.getPotentialType() == PotentialType.CYCLE_LENGTH_SHIFT) {
			newPotential = new CycleLengthShift(oldPotential.getShiftedVariables(probNet,
					timeDifference));
		} else {
			int timeDifferenceWithNew = timeDifference;
			Potential referencePotentialForNewPotential = oldPotential;
			if (oldPotential.getPotentialType() == PotentialType.SAME_AS_PREVIOUS) {
				Potential originalPotential = ((SameAsPrevious) oldPotential)
						.getOriginalPotential();
				// Sets time difference respect to the original potential
				Variable originalConditionedVariable = originalPotential.getConditionedVariable();
				Variable newVariable = newNode.getVariable();
				timeDifferenceWithNew = newVariable.getTimeSlice()
						- originalConditionedVariable.getTimeSlice();
				referencePotentialForNewPotential = originalPotential;
			} 
			newPotential = new SameAsPrevious(referencePotentialForNewPotential, probNet,
					timeDifferenceWithNew);
		}
		newNode.addPotential(newPotential);
		newPotential.createDirectedLinks(probNet);
	}

	private double getSliceWidth(List<ProbNode> nodes) {
		double minX = Double.POSITIVE_INFINITY;
		double maxX = 0.0;
		for (ProbNode probNode : nodes) {
			if (probNode.getNode().getCoordinateX() > maxX) {
				maxX = probNode.getNode().getCoordinateX();
			}
			if (probNode.getNode().getCoordinateX() < minX) {
				minX = probNode.getNode().getCoordinateX();
			}
		}
		return maxX - minX;
	}

}
