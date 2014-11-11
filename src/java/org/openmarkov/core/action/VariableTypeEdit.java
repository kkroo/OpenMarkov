/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.action;

import java.util.ArrayList;
import java.util.List;

import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.SumPotential;
import org.openmarkov.core.model.network.potential.UniformPotential;

@SuppressWarnings("serial")
public class VariableTypeEdit extends SimplePNEdit {
	// private ProbNet probNet;
	private ProbNode probNode;
	private VariableType newType;
	private VariableType currentType;

	public VariableTypeEdit(ProbNode probNode, VariableType newType) {
		super(probNode.getProbNet());
		this.probNode = probNode;
		this.newType = newType;
		this.currentType = probNode.getVariable().getVariableType();

	}

	@Override
	public void doEdit() throws DoEditException {
		List<Node> nodes;
		probNode.getVariable().setVariableType(newType);
		if (currentType != newType) {
			if ((newType.compareTo(VariableType.DISCRETIZED) == 0 && currentType
					.compareTo(VariableType.FINITE_STATES) == 0)
					|| newType.compareTo(VariableType.FINITE_STATES) != 0
					&& currentType.compareTo(VariableType.DISCRETIZED) == 0) {
				// from discretized to finite states or vice versa
			} else {
				// from numeric to finite states or discretized or vice versa
				//if child is utility to potential to be set depends on the type of the other parents
				//it is not always uniform
				probNode.setUniformPotential2ProbNode();
				nodes = probNode.getNode().getChildren();
				for (Node node : nodes) {
					ProbNode child = (ProbNode) node.getObject();
					if (child.getNodeType() == NodeType.UTILITY) {
						List<Potential> newPotentials = new ArrayList<Potential>();
						if (child.onlyNumericalParents()) {// utility and numerical parents sum
							for (Potential oldPotential : child.getPotentials ())
							{
								// Update potential
								Potential newPotential = new SumPotential ( oldPotential.getVariables (),
										oldPotential.getPotentialRole ());
								newPotential.setUtilityVariable (oldPotential.getUtilityVariable ());
								newPotentials.add (newPotential);
							}
						}else if (!child.onlyNumericalParents()) {//mixture of finite states and numerical Uniform
							for (Potential oldPotential : child.getPotentials ())
							{
								// Update potential
								Potential newPotential = new UniformPotential (oldPotential.getVariables (),
										oldPotential.getPotentialRole ());
								newPotential.setUtilityVariable (oldPotential.getUtilityVariable ());
								newPotentials.add (newPotential);
							}
						}
						child.setPotentials (newPotentials);
					} else {
						//if child is not utility always change potential to Uniform
						child.setUniformPotential2ProbNode();
					}
				}
			}
		}
		if (currentType.compareTo(VariableType.NUMERIC) == 0 ) // if current type
			// is numeric
		{
			probNode.getVariable().setStates(
					probNode.getProbNet().getDefaultStates());
			ArrayList<Variable> variables = new ArrayList<Variable>();
			if (probNode.getNodeType() != NodeType.UTILITY) {
				variables.add(probNode.getVariable());
			}
			for (Node node : probNode.getNode().getParents()) {
				variables.add(((ProbNode) node.getObject()).getVariable());
			}
			UniformPotential uniformPotential = new UniformPotential(variables,
					probNode.getPotentials().get(0).getPotentialRole());
			ArrayList<Potential> potentials = new ArrayList<Potential>(1);
			potentials.add(uniformPotential);
			probNode.setPotentials(potentials);
			probNode.setUniformPotential();
			/*nodes = probNode.getNode().getChildren();
			for (Node node : nodes) {
				ProbNode child = (ProbNode) node.getObject();
				child.setUniformPotential();
			}*/
		}

		resetLink(probNode.getNode());

	}

	@Override
	public void undo() {
		super.undo();
		probNode.getVariable().setVariableType(currentType);
	}

	public VariableType getNewVariableType() {
		return newType;
	}

	public ProbNode getProbNode() {

		return this.probNode;
	}

	/****
	 * This method resets the link restriction of the links of the node
	 * 
	 * @param node
	 */
	private void resetLink(Node node) {

		List<Node> children = node.getChildren();
		for (Node child : children) {
			Link link = node.getGraph().getLink(node, child, true);
			if (link.hasRevealingConditions()) {
				link.setRevealingIntervals(new ArrayList<PartitionedInterval>());
				link.setRevealingStates(new ArrayList<State>());
			}
		}

		for (Link link : node.getLinks()) {
			if (link.hasRestrictions()) {
				link.setRestrictionsPotential(null);
			}

		}
	}

}
