/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.gui.util.GUIDefaultStates;
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
public class NodeStateEdit extends SimplePNEdit {
    /**
	 * 
	 */
    private static final long   serialVersionUID = 4325259909756103849L;

    /**
     * The new state
     */
    private State               newState;
    /**
     * The last state before the edition
     */
    private State               oldState         = new State("");
    /**
     * index of the state selected in the view
     */
    private int                 selectedStateIndex;
    /**
     * The node that the stats belongs to
     */
    private ProbNode            probNode         = null;
    /**
     * The action to carry out
     */
    private StateAction         stateAction;
    /**
     * The last partitioned interval before the edition
     */
    private PartitionedInterval currentPartitionedInterval;
    /**
     * The last states before the edition
     */
    private State[]             oldStates;
    /***
     * Map with the link restriction potential for each link.
     */
    private Map<Link, double[]> linkRestrictionMap;
    /***
     * Map with the revelation condition list for each link.
     */
    private Map<Link, List>     revelationConditionMap;

    private String              oldName;

    /**
     * Creates a new <code>NodeStateEdit</code> to carry out the specified
     * action on the specified state.
     * 
     * @param probNode
     *            the node that will be edited.
     * @param stateAction
     *            the action to carry out
     * @param stateIndex
     *            the index (in the table) associated to the state to edit
     * @param newName
     *            a new string for the state edited if the action is ADD.
     */
    public NodeStateEdit(ProbNode probNode, StateAction stateAction, int stateIndex, String newName) {
        super(probNode.getProbNet());
        this.probNode = probNode;
        this.newState = new State(newName);
        if (stateAction != StateAction.ADD) {
            this.oldName = probNode.getVariable().getStateName(stateIndex);
        }
        this.selectedStateIndex = probNode.getVariable().getNumStates() - (stateIndex + 1);
        this.stateAction = stateAction;
        this.currentPartitionedInterval = probNode.getVariable().getPartitionedInterval();
        this.oldStates = probNode.getVariable().getStates();
        this.linkRestrictionMap = new HashMap<Link, double[]>();
        this.revelationConditionMap = new HashMap<>();
    }

    @Override
    public void doEdit()
            throws DoEditException {
        State[] newStates = null;
        Variable variable = probNode.getVariable();
        List<Node> children = probNode.getNode().getChildren();
        Potential uniformPotential;
        List<Potential> potentials;
        switch (stateAction) {
        case ADD:
            // assume that the new state is added in last position
            newStates = new State[variable.getNumStates() + 1];
            newStates[variable.getNumStates()] = newState;
            for (int i = 0; i < oldStates.length; i++) {
                newStates[i] = oldStates[i];
            }

            variable.setStates(newStates);

            // set uniform potential for the edited node and children
            uniformPotential = PotentialOperations.getUniformPotential(probNet,
                    variable,
                    probNode.getNodeType());
            potentials = new ArrayList<Potential>();
            potentials.add(uniformPotential);

            probNode.setPotentials(potentials);

            for (Node node : children) {
                potentials = new ArrayList<Potential>();
                ProbNode child = (ProbNode) node.getObject();
                uniformPotential = PotentialOperations.getUniformPotential(probNet,
                        child.getVariable(),
                        child.getNodeType());
                potentials.add(uniformPotential);
                child.setPotentials(potentials);
            }
            // if the node is discretized add a new row in partitionedInterval
            // field of the node
            if (variable.getVariableType() == VariableType.DISCRETIZED) {
                PartitionedInterval newPartitionedInterval = getNewPartitionedInterval();
                variable.setPartitionedInterval(newPartitionedInterval);
            }
            selectedStateIndex++;
            resetLink(probNode.getNode());
            break;
        case REMOVE:
            newStates = new State[variable.getNumStates() - 1];
            int i1 = 0;
            boolean found = false;
            for (State states : variable.getStates()) {
                if (i1 != selectedStateIndex || found == true) {
                    newStates[i1] = states;
                    i1++;
                } else
                    found = true;
            }
            variable.setStates(newStates);

            // set uniform potential for the edited node and children
            uniformPotential = PotentialOperations.getUniformPotential(probNet,
                    variable,
                    probNode.getNodeType());
            potentials = new ArrayList<Potential>();
            potentials.add(uniformPotential);

            probNode.setPotentials(potentials);

            for (Node node : children) {
                potentials = new ArrayList<Potential>();
                ProbNode child = (ProbNode) node.getObject();
                uniformPotential = PotentialOperations.getUniformPotential(probNet,
                        child.getVariable(),
                        child.getNodeType());
                potentials.add(uniformPotential);
                child.setPotentials(potentials);
            }

            // change current partitioned interval
            if (variable.getVariableType() == VariableType.NUMERIC
                    || variable.getVariableType() == VariableType.DISCRETIZED) {

                double[] oldLimits = currentPartitionedInterval.getLimits();
                boolean[] oldBelongs = currentPartitionedInterval.getBelongsToLeftSide();

                int positionToRemove = selectedStateIndex;

                List<Double> newLimits = new ArrayList<Double>(oldLimits.length - 1);
                List<Boolean> newBelongs = new ArrayList<Boolean>(oldLimits.length - 1);

                for (int j = 0; j < oldLimits.length; j++) {
                    if (j != positionToRemove) {
                        newLimits.add(oldLimits[j]);
                        newBelongs.add(oldBelongs[j]);
                    }
                }
                double[] limits = new double[oldBelongs.length - 1];
                boolean[] belongs = new boolean[oldBelongs.length - 1];
                for (int j = 0; j < newLimits.size(); j++) {
                    limits[j] = newLimits.get(j);
                    belongs[j] = newBelongs.get(j);
                }

                variable.setPartitionedInterval(new PartitionedInterval(limits, belongs));

            }
            resetLink(probNode.getNode());
            break;
        case DOWN:
            if (selectedStateIndex > 0) {
                newStates = new State[variable.getStates().length];
                State state = variable.getStates()[selectedStateIndex - 1];
                State swapState = variable.getStates()[selectedStateIndex];
                for (int i = 0; i < oldStates.length; i++) {
                    if (i == selectedStateIndex - 1) {
                        newStates[i] = swapState;
                    } else if (i == selectedStateIndex) {
                        newStates[i] = state;
                    } else {
                        newStates[i] = oldStates[i];
                    }
                }
                if (probNode.getNodeType() == NodeType.CHANCE
                        || probNode.getNodeType() == NodeType.UTILITY) {
                    Potential oldPotential = probNode.getPotentials().get(0);
                    if (oldPotential instanceof TablePotential) {
                        TablePotential newPotential = DiscretePotentialOperations.reorder((TablePotential) oldPotential,
                                variable,
                                newStates);
                        probNode.setPotential(newPotential);
                    }
                }
                for (Node node : children) {
                    ProbNode child = (ProbNode) node.getObject();
                    if (child.getNodeType() == NodeType.CHANCE
                            || child.getNodeType() == NodeType.UTILITY) {
                        Potential oldPotential = child.getPotentials().get(0);
                        if (oldPotential instanceof TablePotential) {
                            TablePotential newPotential = DiscretePotentialOperations.reorder((TablePotential) oldPotential,
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
            if (selectedStateIndex < variable.getNumStates()) {
                newStates = new State[variable.getStates().length];
                State state = variable.getStates()[selectedStateIndex + 1];
                State swapState = variable.getStates()[selectedStateIndex];
                for (int i = 0; i < oldStates.length; i++) {
                    if (i == selectedStateIndex) {
                        newStates[i] = state;
                    } else if (i == selectedStateIndex + 1) {
                        newStates[i] = swapState;
                    } else {
                        newStates[i] = oldStates[i];
                    }
                }
                if (probNode.getNodeType() == NodeType.CHANCE
                        || probNode.getNodeType() == NodeType.UTILITY) {
                    Potential oldPotential = probNode.getPotentials().get(0);
                    if (oldPotential instanceof TablePotential) {
                        TablePotential newPotential = DiscretePotentialOperations.reorder((TablePotential) oldPotential,
                                variable,
                                newStates);
                        probNode.setPotential(newPotential);
                    }
                }
                for (Node node : children) {
                    ProbNode child = (ProbNode) node.getObject();
                    if (child.getNodeType() == NodeType.CHANCE
                            || child.getNodeType() == NodeType.UTILITY) {
                        Potential oldPotential = child.getPotentials().get(0);
                        if (oldPotential instanceof TablePotential) {
                            TablePotential newPotential = DiscretePotentialOperations.reorder((TablePotential) oldPotential,
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
            if (selectedStateIndex >= 0 && selectedStateIndex < variable.getNumStates()) {

                newStates = new State[variable.getStates().length];
                for (int i = 0; i < variable.getStates().length; i++) {
                    if (i == selectedStateIndex) {
                        newStates[i] = newState;
                    } else {
                        newStates[i] = variable.getStates()[i];
                    }

                }

                // if there is any child with a tree potential the correspondent
                // branch must change
                String oldName = variable.getStates()[selectedStateIndex].getName();
                for (Node node : children) {
                    ProbNode child = (ProbNode) node.getObject();
                    for (Potential childPotential : child.getPotentials()) {
                        if (childPotential instanceof TreeADDPotential) {
                            renameBranchesStates((TreeADDPotential) child.getPotentials().get(0),
                                    oldName,
                                    newState.getName());
                        }
                    }
                }

                variable.setStates(newStates);
            }
            break;
        }

    }

    public void renameBranchesStates(TreeADDPotential tree, String oldName, String newName) {
        if (tree.getRootVariable().equals(probNode.getVariable())) {
            for (int i = 0; i < tree.getBranches().size(); i++) {
                ArrayList<State> newBranchStates = new ArrayList<>();
                for (int j = 0; j < tree.getBranches().get(i).getBranchStates().size(); j++) {
                    if (tree.getBranches().get(i).getBranchStates().get(j).getName().equals(oldName)) {
                        newBranchStates.add(new State(newName));
                        // tree.getBranches().get(i).getBranchStates().get(j).setName(newName);
                    } else {
                        newBranchStates.add(tree.getBranches().get(i).getBranchStates().get(j));
                    }
                }
                tree.getBranches().get(i).setStates(newBranchStates);
                if (tree.getBranches().get(i).getPotential() instanceof TreeADDPotential) {
                    renameBranchesStates((TreeADDPotential) tree.getBranches().get(i).getPotential(),
                            oldName,
                            newName);
                }

            }

        } else {// look if there are more subtrees within the tree
            for (int i = 0; i < tree.getBranches().size(); i++) {

                if (tree.getBranches().get(i).getPotential() instanceof TreeADDPotential) {
                    renameBranchesStates((TreeADDPotential) tree.getBranches().get(i).getPotential(),
                            oldName,
                            newName);
                }

            }
        }
    }

    @Override
    public void undo() {
        super.undo();
        List<Node> nodes;
        switch (stateAction) {
        case RENAME:
            oldState.setName(oldName);
            break;
        default:
            probNode.getVariable().setStates(oldStates);
            probNode.setUniformPotential();
            // Update children information
            nodes = probNode.getNode().getChildren();
            for (Node node : nodes) {
                ProbNode child = (ProbNode) node.getObject();
                child.setUniformPotential();
            }

            if (probNode.getVariable().getVariableType() == VariableType.DISCRETIZED) {
                probNode.getVariable().setPartitionedInterval(currentPartitionedInterval);
            }

            for (Link link : linkRestrictionMap.keySet()) {
                link.initializesRestrictionsPotential();
                TablePotential restrictionPotential = (TablePotential) link.getRestrictionsPotential();
                restrictionPotential.setValues(linkRestrictionMap.get(link));
            }
            for (Link link : revelationConditionMap.keySet()) {
                VariableType varType = ((ProbNode) link.getNode1().getObject()).getVariable().getVariableType();
                if ((varType == VariableType.NUMERIC)) {
                    link.setRevealingIntervals(revelationConditionMap.get(link));
                } else {
                    link.setRevealingStates(revelationConditionMap.get(link));
                }

            }

        }

    }

    // TODO redo() implementation

    /**
     * Gets the new state created if the action was ADD
     * 
     * @return the new state
     */
    public State getNewState() {
        return newState;
    }

    /**
     * Gets the new state created if the action was ADD
     * 
     * @return the new state
     */
    public State getLastState() {
        return oldState;
    }

    public ProbNode getProbNode() {
        return probNode;
    }

    public StateAction getStateAction() {
        return stateAction;
    }

    /**
     * This method add a new default subInterval, in the current
     * PartitionedInterval object
     * 
     * @return The PartitionedInterval object with a new default subInterval
     */

    private PartitionedInterval getNewPartitionedInterval() {
        double limits[] = currentPartitionedInterval.getLimits();
        double newLimits[] = new double[limits.length + 1];
        boolean belongsToLeftSide[] = currentPartitionedInterval.getBelongsToLeftSide();
        boolean newBelongsToLeftSide[] = new boolean[limits.length + 1];
        for (int i = 0; i < limits.length; i++) {
            newLimits[i] = limits[i];
            newBelongsToLeftSide[i] = belongsToLeftSide[i];
        }

        if (currentPartitionedInterval.getMax() == Double.POSITIVE_INFINITY) {
            newLimits[limits.length - 1] = newLimits[limits.length - 2]
                    + probNode.getVariable().getPrecision();
            newLimits[limits.length] = Double.POSITIVE_INFINITY;
        } else {
            newLimits[limits.length] = currentPartitionedInterval.getMax()
                    + probNode.getVariable().getPrecision();
        }
        newBelongsToLeftSide[limits.length] = false;
        return new PartitionedInterval(newLimits, newBelongsToLeftSide);
    }

    /**
     * This method gets the new row data when new state is inserted in a
     * discretized variable.
     * 
     * @return The row data of the new state
     */
    public Object[] getNewRowOfData() {
        String firstSymbol = null;
        String secondSymbol = null;
        double limits[] = null;
        boolean belongsToLeftSide[];
        if (stateAction == StateAction.ADD) {
            limits = probNode.getVariable().getPartitionedInterval().getLimits();
            belongsToLeftSide = probNode.getVariable().getPartitionedInterval().getBelongsToLeftSide();
            firstSymbol = (belongsToLeftSide[limits.length - 2] ? "(" : "[");
            secondSymbol = (belongsToLeftSide[limits.length - 1] ? "]" : ")");
        } else if (stateAction == StateAction.REMOVE) {
            limits = probNode.getVariable().getPartitionedInterval().getLimits();
            belongsToLeftSide = probNode.getVariable().getPartitionedInterval().getBelongsToLeftSide();

            firstSymbol = (belongsToLeftSide[selectedStateIndex] ? "(" : "[");
            secondSymbol = (belongsToLeftSide[selectedStateIndex + 1] ? "]" : ")");

        }
        return new Object[] {
                "",
                GUIDefaultStates.getString(probNode.getVariable().getStates()[selectedStateIndex].getName()),
                firstSymbol, limits[selectedStateIndex], ",", limits[selectedStateIndex + 1],
                secondSymbol };

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
                double[] lastPotential = ((TablePotential) link.getRestrictionsPotential()).values.clone();
                linkRestrictionMap.put(link, lastPotential);
                link.setRestrictionsPotential(null);

            }
        }

        List<Node> children = node.getChildren();
        for (Node child : children) {
            Link link = node.getGraph().getLink(node, child, true);
            if (link.hasRevealingConditions()) {
                VariableType varType = ((ProbNode) link.getNode1().getObject()).getVariable().getVariableType();
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
