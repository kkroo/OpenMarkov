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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;
import org.openmarkov.core.model.network.potential.operation.PotentialOperations;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;

/**
 * <code>NodeStateEdit</code> is a simple edit that allow modify the states of
 * one node.
 * 
 * @author Miguel Palacios
 * @version 1.0 21/12/10
 * 
 */
@SuppressWarnings("serial")
public class NodeStateEdit extends SimplePNEdit {
	// Default increment between discretized intervals
	private final int increment = 2;
	/**
	 * The new state
	 */
	private String newStateName;
	/**
	 * The last state before the edition
	 */
	private State lastState = new State("");
	/**
	 * the index (in the table) associated to the state to edit
	 */
	private int indexState;
	/**
	 * The node that the state belongs to
	 */
	private ProbNode probNode = null;
	/**
	 * The last potential before the edition
	 */
	private List<Potential> oldPotentials;
	/**
	 * The action to carry out
	 */
	private StateAction stateAction;
	/**
	 * The last partitioned interval before the edition
	 */
	private PartitionedInterval oldPartitionedInterval;
	private State[] oldStates;
	/***
	 * Map with the link restriction potential for each link.
	 */
	private Map<Link, double[]> linkRestrictionMap;
	/***
	 * Map with the revelation condition list for each link.
	 */
	private Map<Link, List> revelationConditionMap;

	/**
	 * Creates a new <code>NodeStateEdit</code> to carry out the specified
	 * action on the specified state.
	 * 
	 * @param probNode
	 *            the node that will be edited.
	 * @param stateAction
	 *            the action to carry out
	 * @param indexState
	 *            the index (in the table) associated to the state to edit
	 * @param newState
	 *            a new string for the state edited if the action is ADD.
	 */
	public NodeStateEdit(ProbNode probNode, StateAction stateAction, int indexState, String newState) {
		super(probNode.getProbNet());
		this.probNode = probNode;
		this.newStateName = newState;
		this.stateAction = stateAction;
		this.oldPotentials = probNode.getPotentials();
		this.oldPartitionedInterval = probNode.getVariable().getPartitionedInterval();
		this.oldStates = probNode.getVariable().getStates().clone();
		this.linkRestrictionMap = new HashMap<Link, double[]>();
		this.revelationConditionMap = new HashMap<Link, List>();
	}

	@Override
	public void doEdit() throws DoEditException {
		State[] newObjectState = null;
		List<Node> children = probNode.getNode().getChildren();
		Variable variable = probNode.getVariable();
		Potential uniformPotential;
		List<Potential> potentials;
		int stateSelected = variable.getNumStates() - (indexState + 1);
		switch (stateAction) {
		case ADD:
			// assume that the new state is added in last position
			newObjectState = new State[variable.getNumStates() + 1];
			int i = 0;
			for (State states : variable.getStates()) {
				newObjectState[i] = states;
				i++;
			}
			newObjectState[i] = new State(newStateName);
			variable.setStates(newObjectState);

			// set uniform potential for the edited node and children
			uniformPotential = PotentialOperations.getUniformPotential(probNet, variable,
					probNode.getNodeType());
			potentials = new ArrayList<Potential>();
			potentials.add(uniformPotential);

			probNode.setPotentials(potentials);

			for (Node node : children) {
				potentials = new ArrayList<Potential>();
				ProbNode child = (ProbNode) node.getObject();
				uniformPotential = PotentialOperations.getUniformPotential(probNet,
						child.getVariable(), child.getNodeType());
				potentials.add(uniformPotential);
				child.setPotentials(potentials);
			}
			// if the node is discretized add a new row in partitionedInterval
			// field of the node
			if (variable.getVariableType() == VariableType.DISCRETIZED) {
				PartitionedInterval newPartitionedInterval = getNewPartitionedInterval();
				variable.setPartitionedInterval(newPartitionedInterval);
			}
			stateSelected++;
			resetLink(probNode.getNode());
			break;
		case REMOVE:
			newObjectState = new State[variable.getNumStates() - 1];
			int i1 = 0;
			boolean found = false;
			for (State states : variable.getStates()) {
				if (i1 != stateSelected || found == true) {
					newObjectState[i1] = states;
					i1++;
				} else
					found = true;
			}
			variable.setStates(newObjectState);

			// set uniform potential for the edited node and children
			uniformPotential = PotentialOperations.getUniformPotential(probNet, variable,
					probNode.getNodeType());
			probNode.setPotentials(Arrays.asList(uniformPotential));

			for (Node node : children) {
				ProbNode child = (ProbNode) node.getObject();
				uniformPotential = PotentialOperations.getUniformPotential(probNet,
						child.getVariable(), child.getNodeType());
				child.setPotentials(Arrays.asList(uniformPotential));
			}
			resetLink(probNode.getNode());
			break;
		case DOWN:
			if (stateSelected > 0) {
			    State[] oldStates = variable.getStates();
			    State[] newStates = new State[oldStates.length];
			    for(int j=0; j<oldStates.length; ++j)
			    {
    				if(j == stateSelected - 1)
    				{
    				    newStates[j] = oldStates[stateSelected];
    				}else if(j == stateSelected)
    				{
    				    newStates[j] = oldStates[stateSelected - 1];
    				}else
    				{
    				    newStates[j] = oldStates[j];
    				}
			    }
			    if(probNode.getNodeType() == NodeType.CHANCE ||
			            probNode.getNodeType() == NodeType.UTILITY)
			    {
			        Potential oldPotential = probNode.getPotentials().get(0);
			        if(oldPotential instanceof TablePotential)
			        {
                        TablePotential newPotential = DiscretePotentialOperations.reorder((TablePotential) oldPotential,
                                variable,
                                newStates);
                        probNode.setPotential(newPotential);
			        }
			    }
			    for (Node node : children) {
	                ProbNode child = (ProbNode) node.getObject();
	                if(child.getNodeType() == NodeType.CHANCE ||
	                        child.getNodeType() == NodeType.UTILITY)
	                {
	                    Potential oldPotential = child.getPotentials().get(0);
	                    if(oldPotential instanceof TablePotential)
	                    {
	                        TablePotential newPotential = DiscretePotentialOperations.reorder((TablePotential)oldPotential,
	                                variable,
	                                newStates);
	                        child.setPotential(newPotential);
	                    }
	                }
	            }
			    variable.setStates(newStates);
				resetLink(probNode.getNode());
			}
			break;
		case UP:
			if (stateSelected < variable.getNumStates()) {
                State[] oldStates = variable.getStates();
                State[] newStates = new State[oldStates.length];
                for(int j=0; j<oldStates.length; ++j)
                {
                    if(j == stateSelected + 1)
                    {
                        newStates[j] = oldStates[stateSelected];
                    }else if(j == stateSelected)
                    {
                        newStates[j] = oldStates[stateSelected + 1];
                    }else
                    {
                        newStates[j] = oldStates[j];
                    }
                }
                if(probNode.getNodeType() == NodeType.CHANCE ||
                        probNode.getNodeType() == NodeType.UTILITY)
                {
                    Potential oldPotential = probNode.getPotentials().get(0);
                    if(oldPotential instanceof TablePotential)
                    {
                        TablePotential newPotential = DiscretePotentialOperations.reorder((TablePotential) oldPotential,
                                variable,
                                newStates);
                        probNode.setPotential(newPotential);
                    }
                }
                for (Node node : children) {
                    ProbNode child = (ProbNode) node.getObject();
                    if(child.getNodeType() == NodeType.CHANCE ||
                            child.getNodeType() == NodeType.UTILITY)
                    {
                        Potential oldPotential = child.getPotentials().get(0);
                        if(oldPotential instanceof TablePotential)
                        {
                            TablePotential newPotential = DiscretePotentialOperations.reorder((TablePotential)oldPotential,
                                    variable,
                                    newStates);
                            child.setPotential(newPotential);
                        }
                    }
                }
                variable.setStates(newStates);
				resetLink(probNode.getNode());
			}

			break;
		case RENAME:
			if (stateSelected >= 0 && stateSelected < variable.getNumStates()) {
				State state = variable.getStates()[stateSelected];

				// if there is any child with a tree potential the correspondent
				// branch must change
				String oldName = variable.getStates()[stateSelected].getName();
				for (Node node : children) {
					potentials = new ArrayList<Potential>();
					ProbNode child = (ProbNode) node.getObject();

					if (child.getPotentials().get(0) instanceof TreeADDPotential) {
						renameBranchesStates((TreeADDPotential) child.getPotentials().get(0),
								oldName, newStateName);
					}
				}
				state.setName(newStateName);
			}
			break;
        default:
            break;
		}

	}

	public void renameBranchesStates(TreeADDPotential tree, String oldName, String newName) {
		if (tree.getRootVariable().equals(probNode.getVariable())) {
			for (int i = 0; i < tree.getBranches().size(); i++) {
				for (int j = 0; j < tree.getBranches().get(i).getBranchStates().size(); j++) {
					if (tree.getBranches().get(i).getBranchStates().get(j).getName()
							.equals(oldName)) {
						tree.getBranches().get(i).getBranchStates().get(j).setName(newName);
					}
				}
				if (tree.getBranches().get(i).getPotential() instanceof TreeADDPotential) {
					renameBranchesStates((TreeADDPotential) tree.getBranches().get(i)
							.getPotential(), oldName, newName);
				}

			}

		} else {// look if there are more subtrees within the tree
			for (int i = 0; i < tree.getBranches().size(); i++) {

				if (tree.getBranches().get(i).getPotential() instanceof TreeADDPotential) {
					renameBranchesStates((TreeADDPotential) tree.getBranches().get(i)
							.getPotential(), oldName, newName);
				}

			}
		}
	}

	@Override
	public void undo() {
		super.undo();
		Variable variable = probNode.getVariable();
		variable.setStates(oldStates);
		probNode.setPotentials(oldPotentials);
		// Update children information
		for (Node node : probNode.getNode().getChildren()) {
			ProbNode child = (ProbNode) node.getObject();
			child.setUniformPotential();
		}

		if (variable.getVariableType() == VariableType.DISCRETIZED) {
			variable.setPartitionedInterval(oldPartitionedInterval);
		}
		for (Link link : linkRestrictionMap.keySet()) {
			link.initializesRestrictionsPotential();
			TablePotential restrictionPotential = (TablePotential) link.getRestrictionsPotential();
			restrictionPotential.setValues(linkRestrictionMap.get(link));
		}
		for (Link link : revelationConditionMap.keySet()) {
			VariableType varType = ((ProbNode) link.getNode1().getObject()).getVariable()
					.getVariableType();
			if ((varType == VariableType.NUMERIC)) {
				link.setRevealingIntervals(revelationConditionMap.get(link));
			} else {
				link.setRevealingStates(revelationConditionMap.get(link));
			}
		}
	}

	/**
	 * Gets the new state name if the action was ADD
	 * 
	 * @return the new state
	 */
	public String getNewStateName()
	{
		return newStateName;
	}

	/**
	 * Gets the new state created if the action was ADD
	 * 
	 * @return the new state
	 */
	public State getLastState() {
		return lastState;
	}

	public ProbNode getProbNode() {
		return probNode;
	}

	public StateAction getStateAction() {
		return stateAction;
	}

	public int getIndexState() {
		return indexState;
	}

	/**
	 * This method add a new default subInterval, in the current
	 * PartitionedInterval object
	 * 
	 * @return The PartitionedInterval object with a new default subInterval
	 */

	private PartitionedInterval getNewPartitionedInterval() {
		double limits[] = oldPartitionedInterval.getLimits();
		double newLimits[] = new double[limits.length + 1];
		boolean belongsToLeftSide[] = oldPartitionedInterval.getBelongsToLeftSide();
		boolean newBelongsToLeftSide[] = new boolean[limits.length + 1];
		for (int i = 0; i < limits.length; i++) {
			newLimits[i] = limits[i];
			newBelongsToLeftSide[i] = belongsToLeftSide[i];
		}
		newLimits[limits.length] = oldPartitionedInterval.getMax() + increment;
		newBelongsToLeftSide[limits.length] = false;
		return new PartitionedInterval(newLimits, newBelongsToLeftSide);
	}

	/****
	 * This method resets the link restriction and revelation conditions of the
	 * links of the node
	 * 
	 * @param node
	 */
	private void resetLink(Node node) {

		for (Link link : node.getLinks()) {
			if (link.hasRestrictions()) {
				double[] lastPotential = ((TablePotential) link.getRestrictionsPotential()).values
						.clone();
				linkRestrictionMap.put(link, lastPotential);
				link.setRestrictionsPotential(null);
			}
		}

		List<Node> children = node.getChildren();
		for (Node child : children) {
			Link link = node.getGraph().getLink(node, child, true);
			if (link.hasRevealingConditions()) {
				VariableType varType = ((ProbNode) link.getNode1().getObject()).getVariable()
						.getVariableType();
				if (varType == VariableType.NUMERIC) {
					this.revelationConditionMap.put(link, link.getRevealingIntervals());
					link.setRevealingIntervals(new ArrayList<PartitionedInterval>());
				} else {
					this.revelationConditionMap.put(link, link.getRevealingStates());
					link.setRevealingStates(new ArrayList<State>());
				}
			}
		}

	}

}
