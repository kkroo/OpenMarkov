/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.openmarkov.core.action.PNESupport;
import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NoFindingException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.InferenceOptions;
import org.openmarkov.core.model.graph.Graph;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.constraint.ConstraintManager;
import org.openmarkov.core.model.network.constraint.OnlyAtemporalVariables;
import org.openmarkov.core.model.network.constraint.OnlyChanceNodes;
import org.openmarkov.core.model.network.constraint.OnlyDirectedLinks;
import org.openmarkov.core.model.network.constraint.OnlyOneAgent;
import org.openmarkov.core.model.network.constraint.OnlyTemporalVariables;
import org.openmarkov.core.model.network.constraint.OnlyUndirectedLinks;
import org.openmarkov.core.model.network.constraint.PNConstraint;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.type.BayesianNetworkType;
import org.openmarkov.core.model.network.type.NetworkType;

/**
 * A <code>ProbNet</code> stores <code>ProbNode</code>s in a efficient manner.
 * It has the operations to manage <code>Variables, ProbNodes</code> and <code>
 *  Potentials</code>.
 * 
 * @author marias
 * @author fjdiez
 * @author mpalacios
 * @author mluque
 * @see openmarkov.graphs.Graph
 * @see org.openmarkov.core.model.network.ProbNode
 * @version 1.0
 * @since OpenMarkov 1.0
 */
public class ProbNet implements Cloneable {
    // Attributes
    /**
     * This object contains all the information that the parser reads from disk
     * that does not have a direct connection with the attributes stored in the
     * <code>ProbNet</code> object.
     */
    public HashMap<String, String>     additionalProperties = new HashMap<String, String>();
    /**
     * Network type of this <code>ProbNet</code>.
     */
    private NetworkType                networkType;
    /**
     * <code>ArrayList</code> of <code>Constraints</code> that defines this
     * <code>ProbNet</code>. This attribute is not frozen to allow conversions
     */
    private List<PNConstraint>         constraints;
    /** Associated graph */
    protected Graph                    graph;
    /** Set of agents, defined by a name. Each one may have several properties. */
    // private StringsWithProperties agents;
    private List<StringWithProperties> agents;
    // TODO Cambiar nombre a decisionCriteria y eliminar el otro
    // decisionCriteria
    /**
     * Set of criteria for decision, defined by a name. Each one may have
     * several properties.
     */
    private List<StringWithProperties> decisionCriteria2;

    /**
     * Nodes are stored in several HashMaps to accelerate the access. The type
     * of node determines the <code>HashMap</code> in which the node is stored.
     */
    protected ProbNodeDepot            probNodeDepot;
    /**
     * Each value of the decision criteria variable represents one criterion,
     * used in multicriteria decision analysis
     */
    public Variable                    decisionCriteria;
    private PNESupport                 pNESupport;
    /** The file where the network has been saved */
    private String                     name;
    /** ProbNet comment */
    private String                     comment              = "";
    /** Default States of the probNet */
    private State[]                    defaultStates        = { new State("absent"),
            new State("present")                           };

    /** Number of steps looked ahead */
    private int lookAheadSteps;
    
    private boolean isLookAheadButtonClicked;
    
    private List<PNEdit> lookAheadEdits;
    
    private List<PNEdit> tempRemoval;
    
    // Constructors
    public ProbNet(NetworkType networkType) {
        this.graph = new Graph();
        this.setpNESupport(new PNESupport(false));
        this.constraints = new ArrayList<PNConstraint>();
        this.probNodeDepot = new ProbNodeDepot();
        this.lookAheadSteps = 0;
        this.isLookAheadButtonClicked = false;
        this.lookAheadEdits = new ArrayList<PNEdit>();
        try {
            this.setNetworkType(networkType);
        } catch (ConstraintViolationException e) {
            // Impossible to reach here as the net is empty
        }
    }

    /**
     * Creates a probabilistic network. NetworkTypeConstraint defines the
     * network type. If NetworkTypeConstraint is null the network type will be
     * Bayesian Network
     */
    public ProbNet() {
        this(BayesianNetworkType.getUniqueInstance());
    }

    // Methods
    /**
     * Applies edit to the probNet
     * 
     * @param edit
     * @throws ConstraintViolationException
     * @throws CanNotDoEditException
     * @throws NonProjectablePotentialException
     * @throws WrongCriterionException
     * @throws DoEditException
     */
    public void doEdit(PNEdit edit)
            throws ConstraintViolationException, CanNotDoEditException,
            NonProjectablePotentialException, WrongCriterionException, DoEditException {
        getpNESupport().announceEdit(edit);
        getpNESupport().doEdit(edit);
    }

    /**
     * @param constraint
     *            <code>PNConstraint</code>
     * @param check
     *            . when <code>false</code>, constraint is added to the
     *            constraints list without testing. Otherwise,
     *            <code>constraint</code> is added only when it is full-filled.
     *            <code>boolean</code>
     * @throws ConstraintViolationException
     */
    public void addConstraint(PNConstraint constraint, boolean check)
            throws ConstraintViolationException {
        if (!this.networkType.isApplicableConstraint(constraint)) {
            throw new ConstraintViolationException("Can not apply "
                    + constraint.toString()
                    + " to a probNet of type "
                    + this.networkType.getClass());
        } else if (!constraints.contains(constraint)) {
            if (check && !constraint.checkProbNet(this)) {
                throw new ConstraintViolationException("Can not apply "
                        + constraint.toString()
                        + " to this probNet.");
            }
            constraints.add(constraint);
            getpNESupport().addUndoableEditListener(constraint);
        }
    }

    public void addConstraint(PNConstraint constraint)
            throws ConstraintViolationException {
        addConstraint(constraint, true);
    }

    /**
     * @param constraints
     *            <code>ArrayList<PNConstraint></code>
     * @param check
     *            . when <code>false</code>, constraint is added to the
     *            constraints list without testing. Otherwise,
     *            <code>constraint</code> is added only when it is full-filled.
     *            <code>boolean</code>
     * @throws ConstraintViolationException
     */
    public void addConstraints(List<PNConstraint> constraints, boolean check)
            throws ConstraintViolationException {
        for (PNConstraint constraint : constraints) {
            addConstraint(constraint, check);
        }
    }

    /**
     * @param constraint
     *            <code>PNConstraint</code>
     */
    public void removeConstraint(PNConstraint constraint) {
        if (constraints.contains(constraint)) {
            constraints.remove(constraint);
            getpNESupport().removeUndoableEditListener(constraint);
        }
    }

    /**
     * @param constraints
     *            <code>ArrayList<PNConstraint></code>
     */
    public void removeConstraints(List<PNConstraint> constraints) {
        for (PNConstraint constraint : constraints) {
            removeConstraint(constraint);
        }
    }

    /**
     * @param constraintClass
     *            <code>Class</code>
     */
    public void removeAllConstraints(Class<PNConstraint> constraintClass) {
        List<PNConstraint> constraintsToRemove = new ArrayList<PNConstraint>();
        for (PNConstraint constraint : constraints) {
            if (constraint.getClass().equals(constraintClass)) {
                constraintsToRemove.add(constraint);
            }
        }
        constraints.removeAll(constraintsToRemove);
    }

    /** @return <code>ArrayList</code> of <code>PNConstraint</code>s */
    public List<PNConstraint> getConstraints() {
        return new ArrayList<PNConstraint>(constraints);
    }

    /** @return <code>ArrayList</code> of <code>PNConstraint</code>s */
    public List<PNConstraint> getAdditionalConstraints() {
        List<PNConstraint> additionalConstraints = new ArrayList<PNConstraint>(constraints);
        List<PNConstraint> networkTypeConstraints = ConstraintManager.getUniqueInstance().buildConstraintList(networkType);
        additionalConstraints.removeAll(networkTypeConstraints);
        return additionalConstraints;
    }

    /**
     * Sets Network type
     * 
     * @param networkType
     *            <code>NetworkType</code>
     * @throws ConstraintViolationException
     */
    public void setNetworkType(NetworkType networkType)
            throws ConstraintViolationException {
        NetworkType oldNetworkType = this.networkType;
        this.networkType = networkType;
        List<PNConstraint> constraints = new ArrayList<PNConstraint>();
        try {
            constraints = ConstraintManager.getUniqueInstance().buildConstraintList(networkType);
            // Add new constraints implied by the network type
            addConstraints(constraints, true);
            // Remove those constraints that are no longer applicable to the new
            // network type
            List<PNConstraint> constraintsToRemove = new ArrayList<PNConstraint>();
            for (PNConstraint constraint : this.constraints) {
                if (!networkType.isApplicableConstraint(constraint)) {
                    constraintsToRemove.add(constraint);
                }
            }
            removeConstraints(constraintsToRemove);
        } catch (ConstraintViolationException e) {
            // Revert
            this.networkType = oldNetworkType;
            throw e;
        }
    }

    /**
     * Gets Network type constraint. There is only one and it is stored in first
     * position.
     * 
     * @return constraint. <code>NetworkType
     */
    public NetworkType getNetworkType() {
        return networkType;
    }

    /**
     * Checks all the constraints applied to this <code>probNet</code>.
     * 
     * @return <code>true</code> when all the constraints are full filled,
     *         otherwise <code>false</code>.
     */
    public boolean checkProbNet() {
        for (PNConstraint constraint : constraints) {
            if ((constraint != null) && (!constraint.checkProbNet(this))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether this <code>probNet</code> is temporal or not.
     * 
     * @return <code>true</code> when this network has not associated
     *         OnlyAtemporalVariables constraint, otherwise <code>false</code>.
     */
    public boolean variablesCouldBeTemporal() {
        for (PNConstraint constraint : constraints) {
            if (constraint instanceof OnlyAtemporalVariables) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether this <code>probNet</code> is multiagent or not.
     * 
     * @return <code>true</code> when this network has not associated
     *         OnlyOneAgent constraint, otherwise <code>false</code>.
     */
    public boolean isMultiagent() {
        for (PNConstraint constraint : constraints) {
            if (constraint instanceof OnlyOneAgent) {
                return false;
            }
        }
        return true;
    }

    public boolean thereAreTemporalNodes() {
        boolean thereAreTemporalNodes = false;
        for (int i = 0; i < getProbNodes().size(); i++) {
            if (getProbNodes().get(i).getVariable().isTemporal()) {
                thereAreTemporalNodes = true;
                break;
            }
        }
        return thereAreTemporalNodes;
    }

    /**
     * Checks whether this <code>probNet</code> is temporal or not.
     * 
     * @return <code>true</code> when this network has not associated
     *         OnlyAtemporalVariables constraint, otherwise <code>false</code>.
     */
    public boolean onlyTemporal() {
        for (PNConstraint constraint : constraints) {
            if (constraint instanceof OnlyTemporalVariables) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether this <code>probNet</code> has only chance node or not.
     * 
     * @return <code>true</code> when this network has not associated
     *         OnlyChanceNodes constraint, otherwise <code>false</code>.
     */
    public boolean onlyChanceNodes() {
        for (PNConstraint constraint : constraints) {
            if (constraint instanceof OnlyChanceNodes) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a low deep copy of <code>this ProbNet</code>: copy the
     * <code>graph</code> and the <code>probNodes</code> but do not copy nor
     * variables nor potentials.
     * 
     * @return <code>this probNet</code> copied.
     * @throws ConstraintViolationException
     */
    public ProbNet copy() {
        ProbNet copyNet = new ProbNet(this.networkType);
        copyNet.setName(name);
        // copy constraints
        int numConstraints = constraints.size();
        for (int i = 1; i < numConstraints; i++) {
            try {
                copyNet.addConstraint(constraints.get(i), false);
            } catch (ConstraintViolationException e) {
                // Unreachable code because constraints are not tested in copy
            }
        }
        List<ProbNode> probNodes = getProbNodes();
        // Adds variables and create corresponding nodes. Also add potentials
        for (ProbNode probNode : probNodes) {
            // Add variables and create corresponding nodes
            Variable variable = probNode.getVariable();
            ProbNode newProbNode = null;
            newProbNode = copyNet.addProbNode(variable, probNode.getNodeType());
            Node newNode = newProbNode.getNode();
            Node node = probNode.getNode();
            newNode.setCoordinateX(node.getCoordinateX());
            newNode.setCoordinateY(node.getCoordinateY());
            newProbNode.setPotentials(probNode.getPotentials());
            // TODO Hacer clon para probNode y quitar estas lineas
            newProbNode.setPurpose(probNode.getPurpose());
            newProbNode.setRelevance(probNode.getRelevance());
            newProbNode.setComment(probNode.getComment());
            newProbNode.setCanonicalParameters(probNode.isCanonicalParameters());
            newProbNode.additionalProperties = additionalProperties;
            newProbNode.setAlwaysObserved(probNode.isAlwaysObserved());
        }
        // Adds links
        List<ProbNode> nodes = this.getProbNodes();
        Graph copyGraph = copyNet.getGraph();
        if (graph.useExplicitLinks()) {
            copyGraph.makeLinksExplicit(false);
        }
        for (ProbNode node : nodes) {
            ProbNode copyNode = copyNet.getProbNode(node.getVariable());
            List<ProbNode> siblings = getProbNodesOfNodes(node.getNode().getSiblings());
            for (ProbNode sibling : siblings) {
                ProbNode copySibling = copyNet.getProbNode(sibling.getVariable());
                if (!copyNode.getNode().isSibling(copySibling.getNode())) {
                    copyGraph.addLink(copyNode.getNode(), copySibling.getNode(), false);
                }
            }
            List<ProbNode> children = getProbNodesOfNodes(node.getNode().getChildren());
            for (ProbNode child : children) {
                ProbNode copyChild = copyNet.getProbNode(child.getVariable());
                copyGraph.addLink(copyNode.getNode(), copyChild.getNode(), true);
            }
        }
        // Copy explicit links' properties
        if (graph.useExplicitLinks()) {
            for (Link originalLink : graph.getLinks()) {
                Node copyNode1 = copyNet.getProbNode(((ProbNode) originalLink.getNode1().getObject()).getVariable()).getNode();
                Node copyNode2 = copyNet.getProbNode(((ProbNode) originalLink.getNode2().getObject()).getVariable()).getNode();
                Link copyLink = copyGraph.getLink(copyNode1, copyNode2, originalLink.isDirected());
                copyLink.setRestrictionsPotential(originalLink.getRestrictionsPotential());
                copyLink.setRevealingIntervals(originalLink.getRevealingIntervals());
                copyLink.setRevealingStates(originalLink.getRevealingStates());
            }
        }
        // copy listeners
        copyNet.getPNESupport().setListeners(getpNESupport().getListeners());
        // Copy additionalProperties
        Set<String> keys = additionalProperties.keySet();
        HashMap<String, String> copyProperties = new HashMap<String, String>();
        for (String key : keys) {
            copyProperties.put(key, additionalProperties.get(key));
        }
        copyNet.additionalProperties = copyProperties;
        // Copy decisionCriteria variable
        // copy decision criteria
        if (this.getDecisionCriteria() != null) {
            copyNet.setDecisionCriteria2(this.getDecisionCriteria());
        }
        if (this.getDecisionCriteriaVariable() != null) {
            copyNet.setDecisionCriteriaVariable(this.getDecisionCriteriaVariable());
        }
        return copyNet;
    }

    /**
     * Inserts a link (<code>directed = true</code> or <code>false</code>)
     * between the nodes <code>node1</code> and <code>node2</code> in
     * <code>this</code> graph.
     * 
     * @param node1
     *            <code>ProbNode</code>
     * @param node2
     *            <code>ProbNode</code>
     * @param directed
     *            <code>boolean</code>
     * @throws NodeNotFoundException
     */
    public void addLink(ProbNode node1, ProbNode node2, boolean directed) {
        // Add link between nodes. This can throw an exception
        graph.addLink(node1.getNode(), node2.getNode(), directed);
    }

    /**
     * Inserts a link (<code>directed = true</code> or <code>false</code>)
     * between the nodes associated to <code>variable1</code> and
     * <code>variable2</code> in <code>this</code> graph.
     * 
     * @param variable1
     *            <code>Variable</code>
     * @param variable2
     *            <code>Variable</code>
     * @param directed
     *            <code>boolean</code>
     * @throws NodeNotFoundException
     * @throws An
     *             exception when the addition of this link is not consistent
     *             with the restrictions applied to the graph or when one or
     *             both variables does not belong to <code>this</code> graph.
     */
    public void addLink(Variable variable1, Variable variable2, boolean directed)
            throws NodeNotFoundException {
        // Get nodes
        ProbNode node1 = getProbNode(variable1);
        ProbNode node2 = getProbNode(variable2);
        if (node1 == null) {
            throw new NodeNotFoundException(node1);
        }
        if (node2 == null) {
            throw new NodeNotFoundException(node2);
        }
        addLink(node1, node2, directed);
    }

    /**
     * Inverts the link (<code>directed = true</code> or <code>false</code>)
     * that goes from the nodes associated to <code>variable1</code> and
     * <code>variable2</code> in <code>this</code> graph.
     * 
     * @param variable1
     *            <code>Variable</code>
     * @param variable2
     *            <code>Variable</code>
     * @param directed
     *            <code>boolean</code>
     * @throws An
     *             exception when the inversion of this link is not consistent
     *             with the restrictions applied to the graph or when one or
     *             both variables does not belong to <code>this</code> graph.
     */
    public void invertLink(Variable variable1, Variable variable2, boolean directed)
            throws Exception {
        removeLink(variable1, variable2, true);
        addLink(variable2, variable1, true);
    }

    public String getName() {
        return name;
    }

    /** @return Number of nodes in <code>probNet</code>. <code>int</code> */
    public int getNumNodes() {
        return probNodeDepot.getNumNodes();
    }

    /**
     * @param nodeType
     *            - <code>NodeType</code>
     * @return Number of nodes with <code>NodeType = nodeType</code>.
     *         <code>int</code>
     */
    public int getNumNodes(NodeType nodeType) {
        return probNodeDepot.getNumNodes(nodeType);
    }

    /**
     * @param evidenceCase
     * @return The potentials of the network projected on the evidence
     * @throws NonProjectablePotentialException
     * @throws WrongCriterionException
     * @throws NoFindingException
     */
    public List<TablePotential> tableProjectPotentials(EvidenceCase evidenceCase)
            throws NonProjectablePotentialException, WrongCriterionException {
        List<Potential> originalPotentials = getSortedPotentials();
        List<TablePotential> projectedPotentials = new ArrayList<TablePotential>();
        // each original potential may yield several projected potentials;
        List<TablePotential> potentials;
        for (Potential potential : originalPotentials) {
            InferenceOptions inferenceOptions = new InferenceOptions(this, null);
            potentials = potential.tableProject(evidenceCase, inferenceOptions, projectedPotentials);
            projectedPotentials.addAll(potentials);
        }
        return projectedPotentials;
    }

    /**
     * @return All the potentials of this network. <code>List</code> of
     *         <code>Potential</code>s.
     * @consultation
     */
    public List<Potential> getPotentials() {
        List<ProbNode> nodes = getProbNodes();
        List<Potential> potentials = new ArrayList<Potential>();
        for (ProbNode node : nodes) {
            potentials.addAll(node.getPotentials());
        }
        return potentials;
    }
    
    /**
     * @return All the potentials of this network sorted topologically.
     *         <code>List</code> of <code>Potential</code>s.
     * @consultation
     */
    public List<Potential> getSortedPotentials() {
        List<ProbNode> nodes = ProbNetOperations.sortTopologically(this);
        List<Potential> potentials = new ArrayList<Potential>();
        for (ProbNode node : nodes) {
            potentials.addAll(node.getPotentials());
        }
        return potentials;
    }    

    /**
     * @return All the potentials of this network except those assigned to
     *         utility nodes that are parents of super-value utility nodes.
     *         <code>ArrayList</code> of <code>Potential</code>
     * @consultation
     */
    // TODO find a better name for this method
    public List<Potential> getPotentials2() {
        List<ProbNode> nodes = getProbNodes();
        List<Potential> potentials = new ArrayList<Potential>();
        for (ProbNode node : nodes) {
            if ((node.getNodeType() != NodeType.UTILITY || node.getNode().getNumChildren() == 0)) {
                potentials.addAll(node.getPotentials());
            }
        }
        return potentials;
    }

    /**
     * @return <code>ArrayList</code> with all <code>ProbNode</code>s
     * @consultation
     */
    public List<ProbNode> getProbNodes() {
        return probNodeDepot.getProbNodes();
    }

    /**
     * @return All the nodes corresponding to variables in same order.
     *         <code>ArrayList</code> of <code>ProbNode</code>
     * @param variables
     *            <code>ArrayList</code> of <code>Variable</code>
     * @consultation
     */
    public List<ProbNode> getProbNodes(List<Variable> variables) {
        List<ProbNode> probNodes = new ArrayList<ProbNode>(variables.size());
        for (Variable variable : variables) {
            probNodes.add(getProbNode(variable));
        }
        return probNodes;
    }

    /**
     * @param nodes
     *            <code>ArrayList</code> of <code>Node</code>
     * @return <code>ArrayList</code> of <code>ProbNode</code>
     */
    public static List<ProbNode> getProbNodesOfNodes(List<Node> nodes) {
        List<ProbNode> probNodes = new ArrayList<ProbNode>(nodes.size());
        for (Node node : nodes) {
            probNodes.add((ProbNode) node.getObject());
        }
        return probNodes;
    }

    /**
     * @param probNodes
     *            <code>List</code> of <code>ProbNode</code>
     * @return <code>List</code> of <code>ProbNode</code>
     */
    public static List<Node> getNodesOfProbNodes(List<ProbNode> probNodes) {
        List<Node> nodes = new ArrayList<Node>(probNodes.size());
        for (ProbNode probNode : probNodes) {
            nodes.add(probNode.getNode());
        }
        return nodes;
    }

    /**
     * @return All the nodes of certain kind
     * @param nodeType
     * @consultation
     */
    public List<ProbNode> getProbNodes(NodeType nodeType) {
        return probNodeDepot.getProbNodes(nodeType);
    }

    /**
     * The potentials that contain <code>variable</code> are stored in the node
     * associated to the <code>variable</code> or in the neighbors of that node.
     * This method returns as well the constant potentials (i.e., the potentials
     * that do not depend on any variable) stored in the node associated to
     * <code>variable</code>.
     * 
     * @param variable
     *            <code>Variable</code>.
     * @return <code>ArrayList</code> of all the <code>Potential</code>s in this
     *         network that contains <code>variable</code>
     */
    public List<Potential> getPotentials(Variable variable) {
        List<Potential> potentials = new ArrayList<Potential>();
        ProbNode probNode = getProbNode(variable);
        // potentials associated to this node
        if (probNode != null) { // Variable exists in this ProbNet
            potentials.addAll(probNode.getPotentials());
            // potentials in neighbors that contains variable
            List<ProbNode> neighbors = getProbNodesOfNodes(probNode.getNode().getNeighbors());
            for (ProbNode node : neighbors) {
                List<Potential> nodePotentials = node.getPotentials();
                for (Potential potential : nodePotentials) {
                    if (potential.contains(variable)) {
                        potentials.add(potential);
                    }
                }
            }
        }
        return potentials;
    }

    /**
     * @param nodeType
     * @return All the utility potentials when <code>isUtility</code> param =
     *         <code>true</code> otherwise returns all chance potentials.
     * @consultation
     */
    public List<Potential> getPotentialsByType(NodeType nodeType) {
        return probNodeDepot.getPotentialsByType(nodeType);
    }

    /**
     * @param role
     * @return All the potentials of a role.
     */
    public List<Potential> getPotentialsByRole(PotentialRole role) {
        return probNodeDepot.getPotentialsByRole(role);
    }

    /**
     * Gets all the probability potentials that contain the
     * <code>Variable</code> received. The potentials that can contain that
     * variable are in the node associated to the variable and its neighbors.
     * 
     * @param variable
     * @return <code>ArrayList</code> of potentials containing
     *         <code>variable</code>.
     * @argCondition variable belongs to this <code>ProbNet</code>
     */
    public List<Potential> getProbPotentials(Variable variable) {
        ProbNode nodeVariable = getProbNode(variable);
        List<ProbNode> allNodes = getProbNodesOfNodes(nodeVariable.getNode().getNeighbors());
        allNodes.add(nodeVariable);
        List<Potential> potentialsVariable = new ArrayList<Potential>();
        for (ProbNode node : allNodes) {
            List<Potential> potentialsNode = node.getPotentials();
            for (Potential potential : potentialsNode) {
                if ((potential.getVariables().contains(variable)) && !potential.isUtility()) {
                    potentialsVariable.add(potential);
                }
            }
        }
        return potentialsVariable;
    }

    /**
     * Gets all the utility potentials that contain the <code>variable</code>
     * received. Constant utility potentials are also returned by this method.
     * <p>
     * The potentials that can contain that variable are in the node associated
     * to the variable and its neighbors.
     * 
     * @param variable
     *            <code>Variable</code>.
     * @return <code>ArrayList</code> of potentials containing
     *         <code>variable</code>.
     * @argCondition variable belongs to this <code>ProbNet</code>
     */
    public List<Potential> getUtilityPotentials(Variable variable) {
        ProbNode nodeVariable = getProbNode(variable);
        List<ProbNode> allNodes = getProbNodesOfNodes(nodeVariable.getNode().getNeighbors());
        allNodes.add(nodeVariable);
        List<Potential> potentialsVariable = new ArrayList<Potential>();
        for (ProbNode node : allNodes) {
            List<Potential> potentialsNode = node.getPotentials();
            for (Potential potential : potentialsNode) {
                List<Variable> variables = potential.getVariables();
                if ((variables.size() == 0 || variables.contains(variable))
                        && potential.isUtility()) {
                    potentialsVariable.add(potential);
                }
            }
        }
        return potentialsVariable;
    }

    /**
     * @param variable
     *            <code>Variable</code>
     * @return <code>ArrayList</code> of <code>Potentials</code> that contains
     *         the variable received.
     * @argCondition variable belongs to this <code>ProbNet</code>
     */
    public List<Potential> extractPotentials(Variable variable) {
        // get the nodes that contains potentials associated to the variable
        List<ProbNode> nodes = new ArrayList<ProbNode>();
        // node associated to variable
        ProbNode nodeContainer = getProbNode(variable);
        nodes.add(nodeContainer);
        // and its siblings
        nodes.addAll(getProbNodesOfNodes(nodeContainer.getNode().getNeighbors()));
        List<Potential> potentialsVariable = new ArrayList<Potential>();
        // for each node extract its potentials ...
        for (ProbNode node : nodes) {
            List<Potential> potentialsNode = node.getPotentials();
            for (Potential potential : potentialsNode) {
                // ... and selects the potentials that contains the variable
                if (potential.getVariables().contains(variable)) {
                    potentialsVariable.add(potential);
                }
            }
        }
        return potentialsVariable;
    }

    /**
     * Removes <code>potential</code> from this <code>ProbNet</code>
     * 
     * @return The node where the potential was located or <code>null</code> if
     *         it did not exists
     */
    public ProbNode removePotential(Potential potential) {
        List<Variable> variables = potential.getVariables();
        List<ProbNode> candidateNodes = new ArrayList<ProbNode>();
        // gets probNodes that could contain the potential
        if (!potential.isUtility()) { // chance potential
                                      // find nodes corresponding to variables
            for (Variable variable : variables) {
                ProbNode probNode = getProbNode(variable);
                if (probNode != null) {
                    candidateNodes.add(getProbNode(variable));
                }
            }
        } else { // utility potential.
            if (variables.size() == 0) {// Constant potentials can be in any
                                        // probNode
                candidateNodes = this.getProbNodes();
            } else {
                List<ProbNode> utilityNodes = getProbNodes(NodeType.UTILITY);
                candidateNodes.addAll(utilityNodes);
                ProbNode firstProbNode = getProbNode(variables.get(0));
                candidateNodes.add(firstProbNode);
            }
        }
        // find in such nodes the potential to remove
        for (ProbNode probNode : candidateNodes) {
            if (probNode != null) {
                List<Potential> potentialsNode = probNode.getPotentials();
                for (Potential potentialNode : potentialsNode) {
                    if (potentialNode == potential) {
                        if (probNode.removePotential(potentialNode)) {
                            return probNode;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Removes all the potentials that contains the <code>variable</code>
     * associated to <code>node</code>
     * 
     * @param probNode
     *            <code>ProbNode</code>
     */
    public void removePotentials(ProbNode probNode) {
        // get the nodes that contains potentials associated to the variable
        List<ProbNode> nodes = new ArrayList<ProbNode>();
        Variable variable = probNode.getVariable();
        nodes.add(probNode);
        // and its siblings
        nodes.addAll(getProbNodesOfNodes(probNode.getNode().getSiblings()));
        // for each node extract its potentials ...
        for (ProbNode node : nodes) {
            List<Potential> potentialsNode = new ArrayList<Potential>(node.getPotentials());
            for (Potential potential : potentialsNode) {
                // ... and removes the potentials that contains the variable
                if (potential.getVariables().contains(variable)) {
                    node.removePotential(potential);
                }
            }
        }
    }

    /**
     * Removes all the potentials in the array of potentials received.
     * 
     * @param toRemovePotentials
     *            <code>ArrayList</code> of <code>Potential</code>
     */
    public void removePotentials(List<Potential> toRemovePotentials) {
        if (toRemovePotentials != null) {
            for (Potential potential : toRemovePotentials) {
                removePotential(potential);
            }
        }
    }

    /**
     * @param variable
     *            . <code>Variable</code>
     * @param nodeType
     *            . <code>NodeType</code>
     * @return The <code>probNode</code> that points to <code>variable</code> in
     *         <code>this</code> network.
     * @argCondition the variable must not be in the ProbNet.
     */
    public ProbNode addProbNode(Variable variable, NodeType nodeType) {
        ProbNode probNode = probNodeDepot.getProbNode(nodeType, variable);
        if (probNode == null) {
            probNode = new ProbNode(this, variable, nodeType);
        }
        probNodeDepot.addProbNode(variable, probNode);
        return probNode;
    }

    /**
     * @param probNode
     *            . <code>ProbNode</code>
     * @argCondition the variable must not be in the ProbNet. This method is
     *               used to redo the <code>AddVariableEdit</code>, i.e., to
     *               reinsert a ProbNode that has been removed.
     */
    public void addProbNode(ProbNode probNode) {
        Variable variable = probNode.getVariable();
        probNodeDepot.addProbNode(variable, probNode);
        this.getGraph().uf_addNode(probNode.getNode());
    }

    /**
     * @param nameOfVariable
     *            <code>String</code>
     * @return The <code>ProbNode</code> that matches the
     *         <code>nameOfVariable</code>
     * @consultation
     */
    public ProbNode getProbNode(String nameOfVariable)
            throws ProbNodeNotFoundException {
        ProbNode probNode = probNodeDepot.getProbNode(nameOfVariable);
        if (probNode == null) {
            throw new ProbNodeNotFoundException(this, nameOfVariable);
        }
        return probNode;
    }

    /**
     * @param nameOfVariable
     *            <code>String</code>
     * @return The <code>ProbNode</code> that matches the
     *         <code>nameOfVariable</code>
     * @consultation
     */
    public ProbNode getProbNode(Node node)
            throws ProbNodeNotFoundException {
        ProbNode probNode = probNodeDepot.getProbNode(node);
        if (probNode == null) {
            throw new ProbNodeNotFoundException(this, node.toString());
        }
        return probNode;
    }

    /**
     * @param nameOfVariable
     *            <code>String</code>
     * @param nodeType
     *            <code>NodeType</code>
     * @return The node with <code>nameOfVariable</code> and
     *         <code>kindOfNode</code> if exists otherwhise null
     * @throws ProbNodeNotFoundException
     * @consultation
     */
    public ProbNode getProbNode(String nameOfVariable, NodeType nodeType)
            throws ProbNodeNotFoundException {
        ProbNode probNode = probNodeDepot.getProbNode(nameOfVariable, nodeType);
        if (probNode == null) {
            throw new ProbNodeNotFoundException(this, nameOfVariable);
        }
        return probNode;
    }

    /**
     * @param variable
     *            <code>Variable</code>
     * @return The <code>ProbNode</code> that matches the <code>Variable</code>
     * @consultation
     */
    public ProbNode getProbNode(Variable variable) {
        return probNodeDepot.getProbNode(variable);
    }

    /**
     * @param nameOfVariable
     *            . <code>String</code>
     * @return variable that matches <code>variableName</code> if exists,
     *         otherwise <code>null</code>. <code>Variable</code>
     * @consultation
     */
    public Variable getVariable(String variableName)
            throws ProbNodeNotFoundException {
        ProbNode probNode = getProbNode(variableName);
        return probNode.getVariable();
    }

    /**
     * Returns variable on a certain timeSlice
     * 
     * @param variable
     * @param timeSlice
     * @return
     * @throws ProbNodeNotFoundException
     */
    public Variable getVariable(String baseName, int timeSlice)
            throws ProbNodeNotFoundException {
        return getVariable(baseName + " [" + timeSlice + "]");
    }

    /**
     * @param variable
     *            . a <code>Variable</code>
     * @argCondition variable must be in the network and must be temporal
     * @param int timeSlice
     * @return a new variable having the same base name as the first argument
     *         but in the time slice indicated by the second argument
     * @throws ProbNodeNotFoundException
     * @consultation
     */
    public Variable getShiftedVariable(Variable variable, int timeDifference)
            throws ProbNodeNotFoundException {
        return getVariable(variable.getBaseName(), variable.getTimeSlice() + timeDifference);
    }

    // TODO Con este nuevo metodo podemos evitar la chapuza hecha en
    // varios lugares de invocar getVariable para ver si lanzaba una excepcion.
    // Revisar el uso de esa excepcion y evitarla en lo posible.
    public boolean containsVariable(String variableName) {
        ProbNode probNode = null;
        try {
            probNode = getProbNode(variableName);
        } catch (ProbNodeNotFoundException e) {
        }
        return (probNode != null);
    }

    public boolean containsVariable(Variable variable) {
        return getProbNode(variable) != null;
    }

    /**
     * Returns true if this probNet contains the shifted variable
     * 
     * @param variableName
     * @param timeDifference
     * @return
     */
    public boolean containsShiftedVariable(Variable variable, int timeDifference) {
        int timeSlice = variable.getTimeSlice() + timeDifference;
        String baseName = variable.getBaseName();
        return containsVariable(baseName + " [" + timeSlice + "]");
    }

    /**
     * Adds the received potential to the list of potentials of the conditioned
     * variable (the first one).
     * 
     * @preCondition network contains at least one chance variable
     * @argCondition potential type must correspond with the roles (discrete or
     *               continuous) of the variables in the network
     * @argCondition If A is the first variable in the potential and
     *               B<sub>0</sub> ... B<sub>n</sub> the remainders, there must
     *               be a directed link B<sub>i</sub> -> A for every variable
     *               B<sub>i</sub> in the potential (other than A)
     * @param potential
     *            . <code>Potential</code>
     * @return The <code>ProbNode</code> in which the <code>potential</code>
     *         received has been added.
     */
    public ProbNode addPotential(Potential potential) {
        List<Variable> potentialVariables = potential.getVariables();
        addPotentialVariables(potential);
        ProbNode probNode = null; // The potential will be added here
        if (potential.isUtility()) { // Create probNode without variable
            probNode = addUtilityPotential(potential, potentialVariables);
        } else {
            if (potentialVariables.size() > 0) {
                probNode = addProbabilityPotential(potential, potentialVariables);
            } else {// potential does not depend on any variable (is a constant)
                List<ProbNode> chanceProbNodes = getProbNodes(NodeType.CHANCE);
                if (chanceProbNodes.size() > 0) {
                    probNode = chanceProbNodes.get(0);
                    probNode.addPotential(potential);
                    // If there are no chance nodes there is no reason to
                    // add the constant potential
                }
            }
        }
        return probNode;
    }

    /**
     * If there are missing variables (variables that exists in the
     * <code>potential</code> but not in the <code>probNet</code>), the method
     * adds all those variables to the <code>probNet</code>.
     * 
     * @param potential
     *            . <code>Potential</code>
     */
    private void addPotentialVariables(Potential potential) {
        // Common part
        List<Variable> potentialVariables = potential.getVariables();
        for (Variable variable : potentialVariables) {
            // add the variables that were not yet in the network
            if (getProbNode(variable) == null) {
                addProbNode(variable, NodeType.CHANCE);
            }
        }
        // Only for utility potentials
        if (potential.isUtility()) {
            Variable utilityVariable = potential.getUtilityVariable();
            if (getProbNode(utilityVariable) == null) {
                addProbNode(utilityVariable, NodeType.UTILITY);
            }
        }
    }

    /**
     * @param potential
     *            . <code>Potential</code>
     * @param potentialVariables
     *            . <code>ArrayList</code> of <code>Variable</code>s
     * @return The <code>ProbNode</code> in which the <code>potential</code> is
     *         stored
     */
    private ProbNode addUtilityPotential(Potential potential, List<Variable> potentialVariables) {
        Variable utilityVariable = potential.getUtilityVariable();
        ProbNode utilityProbNode = this.getProbNode(utilityVariable);
        if (utilityProbNode == null) { // The variable does not exists yet
            utilityProbNode = new ProbNode(this, utilityVariable, NodeType.UTILITY);
        }
        utilityProbNode.addPotential(potential);
        if (!hasConstraint(OnlyUndirectedLinks.class)) {
            Node utilityNode = utilityProbNode.getNode();
            for (Variable variable : potentialVariables) {
                Node parent = getProbNode(variable).getNode();
                if (!utilityNode.isParent(parent)) {
                    graph.addLink(parent, utilityNode, true);
                }
            }
        }
        return utilityProbNode;
    }

    /**
     * @param potential
     *            - <code>Potential</code>
     * @param potentialVariables
     *            - <code>ArrayList</code> of <code>Variable</code>s
     */
    private ProbNode addProbabilityPotential(Potential potential, List<Variable> potentialVariables) {
        Variable conditionedVariable = potentialVariables.get(0);
        ProbNode conditionedProbNode = getProbNode(conditionedVariable);
        conditionedProbNode.addPotential(potential);
        if (hasConstraint(OnlyUndirectedLinks.class)) {
            createClique(potential);
        } else {
            if (hasConstraint(OnlyDirectedLinks.class)) {
                Node conditionedNode = conditionedProbNode.getNode();
                for (int i = 1; i < potentialVariables.size(); i++) {
                    Node conditioningNode = getProbNode(potentialVariables.get(i)).getNode();
                    if (!conditionedNode.isParent(conditioningNode)) {
                        graph.addLink(conditioningNode, conditionedNode, true);
                    }
                }
            }
        }
        return conditionedProbNode;
    }

    /**
     * @param constraint
     *            <code>Class</code>
     * @return <code>true</code> if this probabilistic network contains the
     *         received constraint type.
     */
    public boolean hasConstraint(Class<?> constraint) {
        for (PNConstraint constraintProbNet : constraints) {
            if (constraintProbNet.getClass() == constraint) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return All <code>Variable</code>s except utility nodes variables.
     *         <code>ArrayList</code> of <code>Variable</code>.
     */
    public List<Variable> getChanceAndDecisionVariables() {
        List<Variable> variables = new ArrayList<Variable>();
        List<ProbNode> nodes = getProbNodes();
        for (ProbNode node : nodes) {
            if (node.getNodeType() != NodeType.UTILITY) {
                variables.add(node.getVariable());
            }
        }
        return variables;
    }

    /**
     * @return Variables corresponding to the node type received.
     *         <code>ArrayList</code> of <code>Variable</code>
     * @param nodeType
     *            <code>NodeType</code>
     */
    public List<Variable> getVariables(NodeType nodeType) {
        List<Variable> variablesType = new ArrayList<Variable>();
        List<ProbNode> probNodesType = getProbNodes(nodeType);
        for (ProbNode probNode : probNodesType) {
            variablesType.add(probNode.getVariable());
        }
        return variablesType;
    }

    /**
     * @param nodes
     *            list of <code>Node</code>s
     * @return variables corresponding to the received nodes.
     *         <code>List</code> of <code>Variable</code>
     */
    public static List<Variable> getVariablesOfNodes(List<Node> nodes) {
        List<Variable> variables = new ArrayList<Variable>(nodes.size());
        for (Node node : nodes) {
                ProbNode probNode = (ProbNode) node.getObject();
                variables.add(probNode.getVariable());
        }
        return variables;
    }
    
    /**
     * @param nodes
     *            list of <code>ProbNode</code>s
     * @return variables corresponding to the received nodes.
     *         <code>List</code> of <code>Variable</code>
     */
    public static List<Variable> getVariables(List<ProbNode> nodes) {
        List<Variable> variables = new ArrayList<Variable>(nodes.size());
        for (ProbNode node : nodes) {
            variables.add(node.getVariable());
        }
        return variables;
    }    

    /**
     * @return All the variables. <code>ArrayList</code> of
     *         <code>Variable</code>
     */
    public List<Variable> getVariables() {
        List<Variable> variables = new ArrayList<Variable>();
        for (ProbNode probNode : probNodeDepot.getProbNodes()) {
            variables.add(probNode.getVariable());
        }
        return variables;
    }

    /**
     * Removes <code>probNode</code> from <code>this ProbNet</code> and removes
     * also the associated <code>node</code> from the associated
     * <code>Graph</code>.
     * 
     * @param probNode
     *            <code>Node</code>
     */
    public void removeProbNode(ProbNode probNode) {
        if (probNode != null) {
            probNodeDepot.removeProbNode(probNode);
            graph.removeNode(probNode.getNode());
        }
    }

    /**
     * @param node1
     *            <code>ProbNode</code>
     * @param node2
     *            <code>ProbNode</code>
     * @param directed
     *            <code>boolean</code>
     */
    public void removeLink(ProbNode node1, ProbNode node2, boolean directed) {
        graph.removeLink(node1.getNode(), node2.getNode(), directed);
    }

    /**
     * @param variable1
     *            <code>Variable</code>
     * @param variable2
     *            <code>Variable</code>
     * @param directed
     *            <code>boolean</code>
     */
    public void removeLink(Variable variable1, Variable variable2, boolean directed) {
        ProbNode node1 = getProbNode(variable1);
        ProbNode node2 = getProbNode(variable2);
        removeLink(node1, node2, directed);
    }

    /** @return <code>graph</code> associated to this <code>probNet</code>. */
    public Graph getGraph() {
        return graph;
    }

    /** @return Number of potentials. <code>int</code> */
    public int getNumPotentials() {
        return probNodeDepot.getNumPotentials();
    }

    /**
     * Creates a clique by adding undirected links between the nodes that
     * represent the variables of the <code>potential</code>.
     * 
     * @param potential
     *            <code>Potential</code>.
     */
    private void createClique(Potential potential) {
        List<Variable> variablesPotential = potential.getVariables();
        Node node1, node2;
        int potentialSize = variablesPotential.size();
        for (int i = 0; i < potentialSize - 1; i++) {
            ProbNode probNode1 = getProbNode(variablesPotential.get(i));
            if (probNode1.getNodeType() != NodeType.CHANCE) {
                continue;
            }
            node1 = probNode1.getNode();
            for (int j = i + 1; j < potentialSize; j++) {
                ProbNode probNode2 = getProbNode(variablesPotential.get(j));
                if (probNode2.getNodeType() != NodeType.CHANCE) {
                    continue;
                }
                node2 = probNode2.getNode();
                if (!node1.isSibling(node2)) {
                    new Link(node1, node2, false);
                }
            }
        }
    }

    public PNESupport getPNESupport() {
        return getpNESupport();
    }

    /** @return String */
    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append("Type: " + networkType.toString() + "\n");
        List<ProbNode> nodes = getProbNodes();
        int numPotentials = getNumPotentials();
        int numNodes = nodes.size();
        if (numNodes == 0) {
            out.append("No nodes.\n");
        } else {
            out.append("Nodes (" + numNodes + "): ");
            for (ProbNode probNode : nodes) {
                out.append("\n  " + probNode.toString());
            }
            out.append("\n");
        }
        if (numPotentials == 0) {
            out.append("No potentials.\n");
        } else {
            out.append("Number of potentials: " + numPotentials + "\n");
        }
        if (constraints.size() == 0) {
            out.append("No constraints\n");
        } else {
            out.append("Constraints: ");
            for (int i = 0; i < constraints.size(); i++) {
                String strConstraint = constraints.get(i).toString();
                strConstraint = strConstraint.substring(strConstraint.lastIndexOf('.') + 1,
                        strConstraint.length());
                out.append(strConstraint);
                if (i < constraints.size() - 1) {
                    out.append(", ");
                }
            }
            out.append("\n");
        }
        if (agents != null) {
            out.append("\n");
            out.append("Agents:\n" + agents.toString());
        }
        return out.toString();
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param comment
     *            the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param defaultStates
     *            the defaultStates to set
     */
    public void setDefaultStates(State[] defaultStates) {
        this.defaultStates = defaultStates;
    }

    /**
     * @return the defaultStates
     */
    public State[] getDefaultStates() {
        return defaultStates;
    }

    /** @argCondition oldProbNode belongs to this probNet */
    public ProbNode addShiftedProbNode(ProbNode oldProbNode,
            int timeDifference,
            double coordinateXOffset,
            double coordinateYOffset) {
        Variable oldVariable = oldProbNode.getVariable();
        Variable newVariable = (Variable) oldVariable.clone();
        newVariable.setTimeSlice(oldVariable.getTimeSlice() + timeDifference);
        ProbNode newProbNode = addProbNode(newVariable, oldProbNode.getNodeType());
        Node oldNode = oldProbNode.getNode();
        Node newNode = newProbNode.getNode();
        newNode.setCoordinateX(oldNode.getCoordinateX() + coordinateXOffset);
        newNode.setCoordinateY(oldNode.getCoordinateY() + coordinateYOffset);
        // TODO Hacer clon para probNode y quitar estas lineas
        newProbNode.setPurpose(oldProbNode.getPurpose());
        newProbNode.setRelevance(oldProbNode.getRelevance());
        newProbNode.setComment(oldProbNode.getComment());
        newProbNode.setCanonicalParameters(oldProbNode.isCanonicalParameters());
        newProbNode.additionalProperties = additionalProperties;
        return newProbNode;
    }

    public void setDecisionCriteriaVariable(Variable decisionCriteriaVariable) {
        this.decisionCriteria = decisionCriteriaVariable;
    }

    public void setDecisionCriteria(List<String> criteriaNames) {
        State[] states = new State[criteriaNames.size()];
        for (int i = 0; i < criteriaNames.size(); i++) {
            states[i] = new State(criteriaNames.get(i));
        }
        decisionCriteria = new Variable("Decision Criteria", states);
    }

    /**
     * Returns true if and only if there is a path between nodes a and b
     * 
     * @param a
     * @param b
     * @param directed
     * @return
     */
    public boolean existsPath(ProbNode a, ProbNode b, boolean directed) {
        return graph.existsPath(a.getNode(), b.getNode(), directed);
    }

    /** @return <code>ArrayList</code> of <code>StringsWithProperties</code> */
    public List<StringWithProperties> getAgents() {
        return agents;
    }

    /** @return <code>StringsWithProperties</code> */
    public List<StringWithProperties> getDecisionCriteria() {
        return decisionCriteria2;
    }

    public Variable getDecisionCriteriaVariable() {
        return decisionCriteria;
    }

    /**
     * @param decisionCriteria2
     *            . <code>StringsWithProperties</code>
     */
    public void setDecisionCriteria2(List<StringWithProperties> decisionCriteria2) {
        this.decisionCriteria2 = decisionCriteria2;
    }

    /**
     * @param agents
     *            . <code>StringsWithProperties</code>
     */
    public void setAgents(List<StringWithProperties> agents) {
        this.agents = agents;
    }
    
    /**
     * set Look ahead steps
     */
    public void setLookAheadSteps(int num) {
    	lookAheadSteps = num;
    }
    
    /**
     * get look ahead steps
     * @return
     */
    public int getLookAheadSteps() {
    	return lookAheadSteps;
    }

	public PNESupport getpNESupport() {
		return pNESupport;
	}

	public void setpNESupport(PNESupport pNESupport) {
		this.pNESupport = pNESupport;
	}
	
	public void setLookAheadButton(boolean b) {
		isLookAheadButtonClicked = b;
	}
	
	public boolean getLookAheadButton(){
		return isLookAheadButtonClicked;
	}
	
	public List<PNEdit> getLookaheadStepsList() {
		return lookAheadEdits;
	}
	
	public void addLookAheadSteps(PNEdit newEdit) {
		lookAheadEdits.add(newEdit);
	}
	
	public void clearLookAheadSteps() {
		lookAheadEdits.clear();
	}
	
	public List<PNEdit> getTempRemoval() {
		return tempRemoval;
	}
	
	public void addTempRemoval(PNEdit newEdit){
		tempRemoval.add(newEdit);
	}
	
	public void restore(ProbNet pn) {
		this.graph = pn.graph;
        this.setpNESupport(pn.getPNESupport());
        this.constraints = pn.constraints;
        this.probNodeDepot = pn.probNodeDepot;
        this.lookAheadSteps = pn.getLookAheadSteps();
        this.isLookAheadButtonClicked = false;
        this.additionalProperties = pn.additionalProperties;
        this.agents = pn.agents;
        this.decisionCriteria2 = pn.decisionCriteria2;
        this.decisionCriteria = pn.decisionCriteria;
        this.defaultStates = pn.defaultStates;
        this.name = pn.name;
        this.comment = pn.comment;
        
        //this.lookAheadEdits = new ArrayList<PNEdit>();
        try {
            this.setNetworkType(pn.getNetworkType());
        } catch (ConstraintViolationException e) {
            // Impossible to reach here as the net is empty
        }
	}
}
