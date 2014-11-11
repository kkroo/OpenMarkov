/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network;

import java.util.List;

import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.WrongGraphStructureException;
import org.openmarkov.core.inference.PartialOrder;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.constraint.OnlyUndirectedLinks;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.type.MarkovNetworkType;

/**
 * This class is a type of Markov network created from an influence diagram that
 * can generate and store the partial order.
 */
public class MarkovDecisionNetwork extends ProbNet {

	// Attributes
	/** Partial partialOrder of chance and decision nodes */
	private PartialOrder partialOrder;

	public void setPartialOrder(PartialOrder partialOrder) {
		this.partialOrder = partialOrder;
	}

	// Constructor
	/**
	 * Creates a <code>MarkovDecisionNetwork</code> without utility nodes from
	 * the received influence diagram and computes the partial order.
	 * 
	 * @param originalID
	 *            <code>ProbNet</code>
	 * @throws WrongGraphStructureException
	 */
	public MarkovDecisionNetwork(ProbNet originalID)
			throws WrongGraphStructureException {
		super();
		partialOrder = new PartialOrder(originalID);
		addVariablesAndLinks(originalID);
	}

	/**
	 * Adds the variables in the received <code>Potential</code> to this
	 * <code>MarkovNet</code>, creates links between those variables creating
	 * cliques and assigns the <code>potential</code> to the conditioned
	 * variable (the first one).
	 * 
	 * @argCondition At least one potential depends on at least one variable
	 *               (otherwise the network would have no node, and it would be
	 *               impossible to assign constant potentials)
	 * @param projectedTablePotentials
	 *            <code>ArrayList</code> of <code>Potential</code>s
	 * @return A Markov Network in witch potentials are used to create cliques.
	 *         (<code>ProbNet</code>).
	 */
	public MarkovDecisionNetwork(ProbNet originalNet,
			List<? extends Potential> projectedTablePotentials) {
		super(MarkovNetworkType.getUniqueInstance());
		try {
			addConstraint(new OnlyUndirectedLinks(), true);
			// addConstraint (new OnlyDiscreteVariables (), false);
		} catch (ConstraintViolationException e) {
			// Unreachable code
			e.printStackTrace();
		}
		for (Potential potential : projectedTablePotentials) {
			addPotential(originalNet, potential);
		}
	}

	/**
	 * Adds the received potential to the list of potentials of the first
	 * variable.
	 * 
	 * @param originalNet
	 * 
	 * @preCondition network contains at least one variable
	 * @argCondition If A is the first variable in the potential and
	 *               B<sub>0</sub> ... B<sub>n</sub> are the others, there must
	 *               be a directed link B<sub>i</sub> -> A for every variable
	 *               B<sub>i</sub> in the potential (other than A)
	 * @param potential
	 *            . <code>Potential</code>
	 * @return The <code>ProbNode</code> in which the <code>potential</code>
	 *         received has been added.
	 */
	public void addPotential(ProbNet originalNet, Potential potential) {
		List<Variable> potentialVariables = potential.getVariables();
		// the probNode where the potential will be stored
		// TODO hacerlo con edits
		if (potential.getVariables().size() == 0) {
			// it is a constant potential;
			// adds it to any variable of the network
			getProbNodes().get(0).addPotential(potential);
		} else {
			// the potential depends on several variables
			for (Variable variable : potentialVariables) {
				// if (originalNet.getProbNode(variable)!=null){
				if (getProbNode(variable) == null) {
					ProbNode node = originalNet.getProbNode(variable);
					NodeType nodeType = node.getNodeType();
					addProbNode(variable, nodeType);
				}
				// }
			}
			getProbNode(potentialVariables.get(0)).addPotential(potential);
			int numVariables = potentialVariables.size();
			for (int i = 0; i < numVariables - 1; i++) {
				Variable variable1 = potentialVariables.get(i);
				for (int j = i + 1; j < numVariables; j++) {
					Variable variable2 = potentialVariables.get(j);
					try {
						addLink(variable1, variable2, false);
					} catch (NodeNotFoundException e) {
						// Unreachable code because the variables are in the net
					}
				}
			}
		}
	}

	/**
	 * Adds chance and decision nodes to this object from originalID
	 * 
	 * @param originalID
	 *            . <code>ProbNet</code>
	 */
	private void addVariablesAndLinks(ProbNet originalID) {
		for (List<Variable> variables : partialOrder.getOrder()) {
			for (Variable variable : variables) {
				ProbNode node = originalID.getProbNode(variable);
				NodeType nodeType = node.getNodeType();
				addProbNode(variable, nodeType);
			}
		}

		List<Potential> potentials = originalID.getPotentials();
		for (Potential potential : potentials) {
			addPotential(potential);
		}
	}

	// Methods
	/**
	 * @param potential
	 *            <code>Potential</code>
	 * @argCondition All potential variables already exists in this network
	 */
	public ProbNode addPotential(Potential potential) {
		int numVariables = potential.getNumVariables();
		ProbNode probNode;
		if (numVariables >= 1) {
			if (numVariables > 1) { // creates a clique using undirected links
				addLinks(potential);
			}
			// add the potential to the corresponding node in the MarkovNet
			probNode = getProbNode(potential.getVariable(0));
			probNode.addPotential(potential);
		} else { // The potential is a constant.
			TablePotential constantPotential = (TablePotential) potential;
			double constant = constantPotential.values[0];
			probNode = getChanceNode();
			PotentialRole potentialRole = constantPotential.getPotentialRole();
			if (potentialRole != PotentialRole.UTILITY) {
				if (Math.abs(constant - 1.0) > 0.00000001) { // Constant != 1.0
					// Gets randomly a chance node with a potential ...
					TablePotential firstPotential = (TablePotential) probNode
							.getPotentials().get(0);
					// ... and multiplies the first potential by the constant.
					double[] table = firstPotential.values;
					for (int i = 0; i < table.length; i++) {
						table[i] *= constant;
					}
				}
			} else {
				probNode.addPotential(potential);
			}
		}
		return probNode;
	}

	/**
	 * @return The first chance node in the partial order. <code>ProbNode</code>
	 */
	private ProbNode getChanceNode() {
		for (List<Variable> array : partialOrder.getOrder()) {
			for (Variable variable : array) {
				ProbNode probNode = getProbNode(variable);
				if ((probNode != null)
						&& (probNode.getNodeType() == NodeType.CHANCE)
						&& (probNode.getPotentials().size() != 0)) {
					return probNode;
				}
			}
		}
		return null;
	}

	/**
	 * @return the partial order
	 */
	public PartialOrder getPartialOrder() {
		return partialOrder;
	}

	/**
	 * @param originalID
	 *            influence diagram. <code>ProbNet</code>
	 */
	/*
	 * private void calculatePartialOrder(ProbNet originalID) throws
	 * WrongGraphStructureException { partialOrder = new
	 * ArrayList<ArrayList<Variable>>(); ProbNet idCopy =
	 * originalID.copy();//The copy will be destroyed ArrayList<Variable>
	 * decisionVariables = idCopy.getVariables(NodeType.DECISION);
	 * ArrayList<Variable> chanceVariables =
	 * idCopy.getVariables(NodeType.CHANCE);
	 * 
	 * // Build an array of arrays containing the parents // of each decision
	 * ArrayList<ArrayList<Variable>> parentsOfDecisions =
	 * getParentsOfDecisions(idCopy, decisionVariables);
	 * 
	 * // Gets the unobservable variables, i.e., the variables that are not // a
	 * parent of any decision ArrayList<Variable> unobservableVariables =
	 * getUnobservableVariables(chanceVariables, parentsOfDecisions);
	 * 
	 * // Iteratively remove from the influence diagram // chance nodes without
	 * parents or without // children, and decision nodes without parents, //
	 * and add them and their potential to this MarkovDecisionNetwork. int
	 * numVariablesToRemove = chanceVariables.size() + decisionVariables.size();
	 * for (int i = 0; i < numVariablesToRemove; i++) { // gets the node
	 * ProbNode node = getNextNodeToDelete(idCopy); NodeType nodeType =
	 * node.getNodeType(); Variable variable = node.getVariable(); // adds the
	 * node to this MarkovDecisionNetwork addVariable(variable, nodeType); //
	 * adds decision nodes and its parents to partialOrder if (nodeType ==
	 * NodeType.DECISION) { int index = decisionVariables.indexOf(variable); if
	 * (parentsOfDecisions.get(index).size() > 0) {
	 * partialOrder.add(parentsOfDecisions.get(index)); } ArrayList<Variable>
	 * oneDecisionArray = new ArrayList<Variable>();
	 * oneDecisionArray.add(variable); partialOrder.add(oneDecisionArray); }
	 * idCopy.removeProbNode(node); // remove from influenceDiagram } if
	 * (unobservableVariables.size() > 0) {
	 * partialOrder.add(unobservableVariables); }
	 * 
	 * // adds the potentials to this MarkovDecisionNetwork, // which entails
	 * adding links among the nodes ArrayList<Potential> potentials =
	 * originalID.getPotentials(); for (Potential potential : potentials) {
	 * addPotential(potential); }
	 * 
	 * // Add restriction applied in Markov networks: only undirected links. try
	 * { addConstraint (new OnlyUndirectedLinks (), true); } catch
	 * (ConstraintViolationException e) { logger.fatal (e); } }
	 */
	/*	*//**
	 * If there is a chance node without parents or without children,
	 * returns that node. Otherwise, if there is just one decision node without
	 * nodes, returns that node. Otherwise, i.e., if there are more than one
	 * decision nodes without parents, throws an exception.
	 * 
	 * @param influenceDiagram
	 *            <code>InfluenceDiagram</code>
	 * @return A <code>ProbNode</code> without parents or without children.
	 *         <p>
	 *         It first tries to get a chance node;
	 * @argCondition The influence diagram contains at least one chance or
	 *               decision node
	 * @throws <code>WrongGraphStructureException</code>
	 */
	/*
	 * private ProbNode getNextNodeToDelete(ProbNet influenceDiagram) throws
	 * WrongGraphStructureException { // looks for a chance node
	 * ArrayList<ProbNode> chanceNodes =
	 * influenceDiagram.getProbNodes(NodeType.CHANCE); for (ProbNode probNode :
	 * chanceNodes) { Node node = probNode.getNode(); if ((node.getNumChildren()
	 * == 0) || (node.getNumParents() == 0)) { return probNode; } } // looks for
	 * a decision node ArrayList<ProbNode> decisionNodes =
	 * influenceDiagram.getProbNodes(NodeType.DECISION); ProbNode decisionNode =
	 * null; for (ProbNode probNode : decisionNodes) { if
	 * (probNode.getNode().getNumParents() == 0) { if (decisionNode != null) {
	 * //More than two decision without parents throw new
	 * WrongGraphStructureException(
	 * "No partial order for decision nodes in this " + "influence diagram"); }
	 * decisionNode = probNode; } } return decisionNode; }
	 */
	/**
	 * @param chanceVariables
	 *            <code>ArrayList</code> of <code>Variable</code>
	 * @param parentsVariables
	 *            <code>ArrayList</code> of <code>ArrayList</code> of
	 *            <code>Variable</code>
	 * @return An <code>ArrayList</code> of <code>Variable</code> that are not
	 *         parents of any decision.
	 */
	/*
	 * private ArrayList<Variable> getUnobservableVariables( ArrayList<Variable>
	 * chanceVariables, ArrayList<ArrayList<Variable>> parentsVariables) {
	 * ArrayList<Variable> unobservableVariables = new
	 * ArrayList<Variable>(chanceVariables); for (ArrayList<Variable> variables
	 * : parentsVariables) { unobservableVariables.removeAll(variables); }
	 * return unobservableVariables; }
	 */

	/*	*//**
	 * @return An <code>ArrayList</code> of <code>ArrayList</code> of
	 *         <code>Variable</code>. It contains the parents of each decision.
	 *         The <code>ArrayList</code> nested in the i-th position contains
	 *         the parents of the i-th decision node.
	 * @param influenceDiagram
	 *            <code>InfluenceDiagram</code>.
	 * @param decisionVariables
	 *            <code>ArrayList</code> of <code>Variable</code>
	 */
	/*
	 * @SuppressWarnings({ "unchecked", "static-access" }) private
	 * ArrayList<ArrayList<Variable>> getParentsOfDecisions( ProbNet
	 * influenceDiagram, ArrayList<Variable> decisionVariables) {
	 * ArrayList<ArrayList<Variable>> parentsVariables = new
	 * ArrayList<ArrayList<Variable>>(); for(Variable decision :
	 * decisionVariables) { // Remove decision nodes from node parents ProbNode
	 * probNode =influenceDiagram.getProbNode(decision); ArrayList<Node>
	 * nodeParents = probNode.getNode().getParents(); ArrayList<Node>
	 * nodeParentCloned = (ArrayList<Node>)nodeParents.clone(); for (Node node :
	 * nodeParentCloned) { if (((ProbNode)node.getObject()).getNodeType() ==
	 * NodeType.DECISION) { nodeParents.remove(node); } } ArrayList<Variable>
	 * parentVariables = influenceDiagram.getVariables(nodeParents);
	 * parentsVariables.add(parentVariables); } return parentsVariables; }
	 */

	/**
	 * Creates a clique with undirected links between the nodes of the received
	 * <code>potential</code>.
	 * 
	 * @argCondition All the potential variables belongs to this network.
	 * @param potential
	 *            <code>Potential</code>
	 */
	private void addLinks(Potential potential) {
		List<Variable> variablesPotential = potential.getVariables();
		int potentialSize = variablesPotential.size();
		for (int i = 0; i < potentialSize - 1; i++) {
			Node node1 = getProbNode(variablesPotential.get(i)).getNode();
			for (int j = i + 1; j < potentialSize; j++) {
				Node node2 = getProbNode(variablesPotential.get(j)).getNode();
				if (!node1.isSibling(node2)) {
					graph.addLink(node1, node2, false);
				}
			}
		}
	}

}
