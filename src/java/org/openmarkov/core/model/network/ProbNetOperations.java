/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NoFindingException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.InferenceOptions;
import org.openmarkov.core.model.graph.Graph;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.modelUncertainty.UncertainValue;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.TablePotential;

/**
 * This class performs prune on <code>ProbNet</code>
 * 
 * @author marias
 */
public class ProbNetOperations {

    // Methods
    /**
     * Performs prune operation in these steps:
     * <ol>
     * <li>Copy the received <code>ProbNet</code>.
     * <li>Remove barren nodes from the copied <code>ProbNet</code>.
     * <li>Remove unreachable nodes from <code>variablesOfInterest</code> given
     * the <code>variablesOfEvidence</code>.
     * </ol>
     * 
     * @return <code>ProbNet</code>. Evidence variables are removed in serial
     *         connections
     */
    public static ProbNet getPruned(ProbNet probNet,
            Collection<Variable> variablesOfInterest,
            EvidenceCase evidence) {
        ProbNet prunedProbNet = probNet.copy();
        HashSet<Variable> variablesOfInterest2 = new HashSet<Variable>(variablesOfInterest);
        HashSet<Variable> variablesOfEvidence2 = new HashSet<Variable>(evidence.getVariables());
        prunedProbNet = removeBarrenNodes(prunedProbNet, variablesOfInterest2, variablesOfEvidence2);
        prunedProbNet = removeUnreachableNodes(prunedProbNet,
                variablesOfInterest2,
                variablesOfEvidence2);
        return prunedProbNet;
    }

    /**
     * Projects the evidence in the <code>probNet</code> potentials and remove
     * evidence variables
     * 
     * @param probNet
     *            . <code>ProbNet</code>
     * @param evidence
     *            . <code>EvidenceCase</code>
     */
    public static void projectEvidence(ProbNet probNet, EvidenceCase evidence) {
        List<Variable> variables = evidence.getVariables();
        for (Variable variable : variables) {
            List<Potential> potentials = probNet.getPotentials(variable);
            for (Potential potential : potentials) {
                probNet.removePotential(potential);
                try {
                    for (Potential newPotential : potential.tableProject(evidence, null)) {
                        if (newPotential.getNumVariables() > 0) {
                            boolean containVariables = true;
                            for (Variable potentialVariable : newPotential.getVariables()) {
                                containVariables &= (probNet.getProbNode(potentialVariable) != null);
                            }
                            if (containVariables) {
                                probNet.addPotential(newPotential);
                            }
                        }
                    }
                } catch (NonProjectablePotentialException e) {
                    e.printStackTrace(); // Unreachable code
                } catch (WrongCriterionException e) {
                    e.printStackTrace(); // Unreachable code
                }
            }
            probNet.removeProbNode(probNet.getProbNode(variable));
        }
    }

    /**
     * Remove nodes that:
     * <ol>
     * <li>Are not included in <code>variablesOfInterest</code>
     * <li>Are not included in <code>variablesOfEvidence</code>
     * <li>Have no children or all its children are barren nodes.
     * </ol>
     * 
     * @param variablesOfEvidence2
     * @param variablesOfInterest2
     * @param prunedProbNet
     *            . <code>ProbNet</code>
     * @return <code>ProbNet</code> without barren nodes.
     */
    public static ProbNet removeBarrenNodes(ProbNet prunedProbNet,
            Collection<Variable> variablesOfInterest,
            HashSet<Variable> variablesOfEvidence) {
        HashSet<ProbNode> barrenNodes = new HashSet<ProbNode>();
        List<ProbNode> probNodes = prunedProbNet.getProbNodes();
        for (ProbNode probNode : probNodes) {
            Node node = probNode.getNode();
            if (node.getNumChildren() == 0) {
                Variable variable = probNode.getVariable();
                if (!variablesOfInterest.contains(variable)
                        && !variablesOfEvidence.contains(variable)) {
                    barrenNodes.add(probNode);
                }
            }
        }
        HashSet<ProbNode> newBarrenNodes = new HashSet<ProbNode>(barrenNodes);
        boolean foundBarrenNodes = newBarrenNodes.size() > 0;
        while (foundBarrenNodes) {
            foundBarrenNodes = false;
            List<ProbNode> listNewBarrenNodes = new ArrayList<ProbNode>(newBarrenNodes);
            for (ProbNode probNode : listNewBarrenNodes) {
                newBarrenNodes.remove(probNode);
                Node node = probNode.getNode();
                List<Node> parents = node.getParents();
                for (Node parent : parents) {
                    ProbNode parentProbNode = (ProbNode) parent.getObject();
                    Variable parentVariable = parentProbNode.getVariable();
                    if (!variablesOfInterest.contains(parentVariable)
                            && !variablesOfEvidence.contains(parentVariable)
                            && !barrenNodes.contains(parentProbNode)) {
                        List<Node> childrenOfParent = parent.getChildren();
                        boolean allChildrenBarren = true;
                        int numChildren = childrenOfParent.size();
                        if (numChildren > 1) { // at least one children is
                                               // barren
                            for (int i = 0; allChildrenBarren && i < numChildren; i++) {
                                Node child = childrenOfParent.get(i);
                                ProbNode probNodeChild = (ProbNode) child.getObject();
                                allChildrenBarren &= barrenNodes.contains(probNodeChild);
                            }
                        }
                        if (allChildrenBarren) {
                            newBarrenNodes.add(parentProbNode);
                            foundBarrenNodes = true;
                        }
                    }
                }
            }
            if (foundBarrenNodes) {
                barrenNodes.addAll(newBarrenNodes);
            }
        }
        // Remove barren nodes
        for (ProbNode probNode : barrenNodes) {
            prunedProbNet.removeProbNode(probNode);
        }
        return prunedProbNet;
    }

    /**
     * Removes the nodes that are not connected to the variables of interest by
     * any path
     * 
     * @param probNet
     *            . <code>ProbNet</code>
     * @param variablesOfInterest
     *            . <code>Collection</code> of <code>Variable</code>
     * @param variablesOfEvidence
     *            . <code>HashSet</code> of <code>Variable</code>
     * @return <code>ProbNet</code>
     */
    public static ProbNet removeUnreachableNodes(ProbNet probNet,
            Collection<Variable> variablesOfInterest,
            HashSet<Variable> variablesOfEvidence) {
        // Gets nodes of interest and adds nodes connected to them
        UniqueStack<Node> nodesToExplore = new UniqueStack<Node>();
        HashSet<Node> nodesToKeep = new HashSet<Node>();

        // Store nodes of variablesOfInterest in nodesToKeep
        for (Variable variable : variablesOfInterest) {
            Node node = probNet.getProbNode(variable).getNode();
            nodesToKeep.add(node);
        }

        // Add neighbors of variablesOfInterest as nodesToKeep and store them in
        // nodesToExplore
        HashSet<Node> nodesToKeepClon = new HashSet<Node>(nodesToKeep);
        for (Node node : nodesToKeepClon) {
            List<Node> neighbors = node.getNeighbors();
            for (Node neighbor : neighbors) {
                if (!nodesToKeep.contains(neighbor)) {
                    nodesToKeep.add(neighbor);
                    nodesToExplore.push(neighbor);
                }
            }
        }

        // Store evidence nodes and probNodes in collections
        HashSet<Node> hashEvidenceNodes = getEvidenceNodes(probNet, variablesOfEvidence);
        HashSet<Node> evidenceAndAncestors = getNodesAndAncestors(hashEvidenceNodes);

        // For each interest node, finds connected nodes via valid paths.
        while (!nodesToExplore.empty()) {
            Node node = nodesToExplore.pop();

            // Find head to head connected nodes: X->Y<-Z and
            // Y is evidence or Y has a descendent that is evidence
            if (evidenceAndAncestors.contains(node)) {
                List<Node> parents = node.getParents();
                int parentsSize = parents.size();
                for (int i = 0; i < parentsSize - 1; i++) {
                    Node parentI = parents.get(i);
                    boolean toKeepI = nodesToKeep.contains(parentI);
                    for (int j = i + 1; j < parentsSize; j++) {
                        Node parentJ = parents.get(j);
                        boolean toKeepJ = nodesToKeep.contains(parentJ);
                        if (toKeepI && !toKeepJ) {
                            pushInExploreAndAddToKeep(parentJ, nodesToExplore, nodesToKeep);
                        } else if (!toKeepI && toKeepJ) {
                            pushInExploreAndAddToKeep(parentI, nodesToExplore, nodesToKeep);
                            toKeepI = true;
                        }
                    }
                }
            }
            // X has a children Y that is part of the evidence
            List<Node> xChildren = node.getChildren();
            for (Node child : xChildren) {
                if (evidenceAndAncestors.contains(child)) {
                    pushInExploreAndAddToKeep(child, nodesToExplore, nodesToKeep);
                }
            }

            // Find not head to head connected nodes:
            // X->Y->Z, X<-Y<-Z and X<-Y->Z
            if (!hashEvidenceNodes.contains(node)) {
                List<Node> children = node.getChildren();
                List<Node> parents = node.getParents();
                int numChildren = children.size();
                for (int i = 0; i < numChildren; i++) {
                    Node child = children.get(i);
                    boolean childInNodesToKeep = nodesToKeep.contains(child);
                    // X->Y->Z and X<-Y<-Z
                    for (Node parent : parents) {
                        boolean parentInNodesToKeep = nodesToKeep.contains(parent);
                        if (childInNodesToKeep && !parentInNodesToKeep) {
                            pushInExploreAndAddToKeep(parent, nodesToExplore, nodesToKeep);
                            parentInNodesToKeep = true;
                        } else if (parentInNodesToKeep && !childInNodesToKeep) {
                            pushInExploreAndAddToKeep(child, nodesToExplore, nodesToKeep);
                            childInNodesToKeep = true;
                        }
                    }
                    // X<-Y->Z
                    for (int j = i + 1; j < numChildren; j++) {
                        Node child2 = children.get(j);
                        boolean child2InNodesToKeep = nodesToKeep.contains(child2);
                        if (child2InNodesToKeep && !childInNodesToKeep) {
                            pushInExploreAndAddToKeep(child, nodesToExplore, nodesToKeep);
                            childInNodesToKeep = true;
                        } else if (childInNodesToKeep && !child2InNodesToKeep) {
                            pushInExploreAndAddToKeep(child2, nodesToExplore, nodesToKeep);
                        }
                    }
                }
            }
        }

        // remove nodes that are not in nodesToKeep in prunedProbNet
        List<Node> prunedNodes = ProbNet.getNodesOfProbNodes(probNet.getProbNodes());
        for (Node node : prunedNodes) {
            if (!nodesToKeep.contains(node)) {
                probNet.removeProbNode((ProbNode) node.getObject());
            }
        }

        return probNet;
    }

    /**
     * @param node
     *            . <code>Node</code>
     * @param nodesToExplore
     *            . <code>UniqueStack</code> of <code>Node</code>
     * @param nodesToKeep
     *            . <code>HashSet</code> of <code>Node</code>
     */
    private static void pushInExploreAndAddToKeep(Node node,
            UniqueStack<Node> nodesToExplore,
            HashSet<Node> nodesToKeep) {
        nodesToExplore.push(node);
        nodesToKeep.add(node);
    }

    /**
     * @param probNet
     *            . <code>ProbNet</code>
     * @param variablesOfEvidence
     *            . <code>Collection</code> of <code>Variable</code>
     * @return <code>HashSet</code> of <code>Node</code>
     */
    private static HashSet<Node> getEvidenceNodes(ProbNet probNet,
            Collection<Variable> variablesOfEvidence) {
        HashSet<Node> hashEvidenceNodes = new HashSet<Node>();
        for (Variable variable : variablesOfEvidence) {
            ProbNode evidenceProbNode = probNet.getProbNode(variable);
            if (evidenceProbNode != null) {
                Node evidenceNode = evidenceProbNode.getNode();
                hashEvidenceNodes.add(evidenceNode);
            }
        }
        return hashEvidenceNodes;
    }

    /**
     * @param nodes
     *            . <code>ArrayList</code> of <code>Node</code>.
     * @return <code>nodes</code> and its ancestors. <code>ArrayList</code> of
     *         <code>Node</code>.
     */
    private static HashSet<Node> getNodesAndAncestors(Collection<Node> nodes) {
        HashSet<Node> ancestors = new HashSet<Node>(nodes);

        Stack<Node> noExploredNodes = new Stack<Node>();
        noExploredNodes.addAll(nodes);

        while (!noExploredNodes.empty()) {
            Node node = noExploredNodes.pop();
            List<Node> parents = node.getParents();
            for (Node parent : parents) {
                if (ancestors.add(parent)) {
                    noExploredNodes.push(parent);
                }
            }
        }
        return ancestors;
    }

    /**
     * Uses the algorithm by Kahn (1962)
     * 
     * @param probNet
     * @param variablesToSort
     */
    public static List<Variable> sortTopologically(ProbNet probNet, List<Variable> variablesToSort) {
        List<ProbNode> sortedNodes = sortTopologically(probNet);
        List<Variable> sortedVariables = new ArrayList<Variable>(sortedNodes.size());
        // fill sortedVariables list with filtering the l list with the list of
        // variables to sort
        for (ProbNode node : sortedNodes) {
            if (variablesToSort.contains(node.getVariable())) {
                sortedVariables.add(node.getVariable());
            }
        }
        return sortedVariables;
    }

    /**
     * Uses the algorithm by Kahn (1962)
     * 
     * @param probNet
     */
    public static List<ProbNode> sortTopologically(ProbNet probNet) {
        Graph graph = probNet.getGraph().copy();
        List<ProbNode> sortedNodes = new ArrayList<ProbNode>();
        // Empty list that will contain the sorted elements
        Stack<Node> s = new Stack<Node>();
        // Set of all nodes with no incoming edges
        List<Node> l = new ArrayList<Node>();
        // Look for variables/nodes with no parents
        for (Node node : graph.getNodes()) {
            if (node.getParents().size() == 0) {
                s.push(node);
            }
        }
        // while S is non-empty do
        while (!s.isEmpty()) {
            // remove a node n from S
            Node n = s.pop();
            // insert n into L
            l.add(n);
            // for each node m with an edge e from n to m do
            for (Node m : n.getChildren()) {
                // remove edge e from the graph
                graph.removeLink(n, m, true);
                // if m has no other incoming edges then insert m into S
                if (m.getParents().isEmpty()) {
                    s.push(m);
                }
            }
        }
        for (Node node : l) {
            sortedNodes.add((ProbNode) node.getObject());
        }
        return sortedNodes;
    }

    /**
     * Converts numerical variables with neither evidence nor induced findings
     * (such as a delta potential) that are deterministically defined by their
     * parents into finite state variables. It also adapts the potentials
     * affected by these conversions.
     * 
     * @param probNet
     * @param evidence
     * @return
     */
    public static ProbNet convertNumericalVariablesToFS(ProbNet probNet, EvidenceCase evidence) {
        ProbNet convertedNet = probNet.copy();
        List<ProbNode> sortedNodes = sortTopologically(convertedNet);
        List<ProbNode> convertedNodes = new ArrayList<>();
        Map<Variable, Variable> originalVariables = new LinkedHashMap<>();
        Map<Variable, Variable> convertedVariables = new LinkedHashMap<>();

        for (ProbNode node : sortedNodes) {
            Variable oldVariable = node.getVariable();
            // Should the node be converted
            if (oldVariable.getVariableType() == VariableType.NUMERIC
                    && node.getNodeType() == NodeType.CHANCE) {
                EvidenceCase configuration = new EvidenceCase(evidence);
                Potential oldPotential = node.getPotentials().get(0);
                if (configuration.contains(oldVariable)) {
                    // Convert numerical variables with evidence to one-state
                    // variables
                    double value = configuration.getFinding(oldVariable).numericalValue;
                    Variable newVariable = new Variable(oldVariable.getName(),
                            String.valueOf(value));
                    node.setVariable(newVariable);
                    originalVariables.put(newVariable, oldVariable);
                    convertedVariables.put(oldVariable, newVariable);
                    convertedNodes.add(node);
                    TablePotential potential = new TablePotential(Arrays.asList(newVariable),
                            oldPotential.getPotentialRole());
                    potential.values[0] = 1;
                    node.setPotential(potential);
                } else {
                    List<Double> newStates = new ArrayList<>();
                    // For each configuration x, add f(x) to the list (if it is
                    // not already in)
                    List<ProbNode> parents = ProbNet.getProbNodesOfNodes(node.getNode().getParents());
                    // Set initial configuration
                    int numConfigurations = 1;
                    int[] parentIndices = new int[parents.size()];
                    for (int i = 0; i < parents.size(); ++i) {
                        Variable parentVariable = parents.get(i).getVariable();
                        numConfigurations *= parentVariable.getNumStates();
                        parentIndices[i] = 0;
                        try {
                            if (originalVariables.containsKey(parentVariable)) {
                                Variable originalVariable = originalVariables.get(parentVariable);
                                double numericalValue = Double.valueOf(parentVariable.getStates()[0].getName());
                                configuration.addFinding(new Finding(originalVariable,
                                        numericalValue));
                            } else if (parentVariable.getVariableType() == VariableType.FINITE_STATES) {
                                configuration.addFinding(new Finding(parentVariable, 0));
                            } else {
                                // TODO throw some exception
                            }
                        } catch (InvalidStateException | IncompatibleEvidenceException e) {
                            e.printStackTrace();
                        }
                    }
                    boolean nextConfiguration = true;
                    double[] projectedValues = new double[numConfigurations];
                    int index = 0;
                    int parentIndex = 0;
                    InferenceOptions inferenceOptions = new InferenceOptions(convertedNet, null);
                    while (nextConfiguration) {

                        // Calculate scalar value projecting configuration
                        double scalarValue = Double.NEGATIVE_INFINITY;
                        try {
                            scalarValue = oldPotential.tableProject(configuration, inferenceOptions).get(0).values[0];
                            scalarValue = oldVariable.round(scalarValue);
                            projectedValues[index++] = scalarValue;
                        } catch (NonProjectablePotentialException | WrongCriterionException e) {
                            e.printStackTrace();
                        }
                        if (!newStates.contains(scalarValue)) {
                            newStates.add(scalarValue);
                        }

                        // Get next configuration
                        nextConfiguration = false;
                        parentIndex = 0;
                        while (!nextConfiguration && parentIndex < parents.size()) {
                            ProbNode parent = parents.get(parentIndex);
                            Variable parentVariable = parent.getVariable();
                            Variable findingVariable = (originalVariables.containsKey(parent.getVariable())) ? originalVariables.get(parent.getVariable())
                                    : parent.getVariable();
                            int nextStateIndex = ++parentIndices[parentIndex];
                            if (nextStateIndex < parent.getVariable().getNumStates()) {
                                nextConfiguration = true;
                                try {
                                    if (originalVariables.containsKey(parentVariable)) {
                                        Variable originalVariable = originalVariables.get(parentVariable);
                                        double numericalValue = Double.valueOf(parentVariable.getStates()[nextStateIndex].getName());
                                        configuration.changeFinding(new Finding(originalVariable,
                                                numericalValue));
                                    } else if (parentVariable.getVariableType() == VariableType.FINITE_STATES) {
                                        configuration.changeFinding(new Finding(findingVariable,
                                                nextStateIndex));
                                    }
                                } catch (InvalidStateException | IncompatibleEvidenceException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                parentIndices[parentIndex] = 0;
                                parentIndex++;
                            }
                        }
                    }

                    Collections.sort(newStates);
                    State[] states = new State[newStates.size()];
                    Map<Double, Integer> stateIndices = new HashMap<>();
                    for (int i = 0; i < newStates.size(); ++i) {
                        states[i] = new State(String.valueOf(newStates.get(i)));
                        stateIndices.put(newStates.get(i), i);
                    }

                    Variable newVariable = new Variable(oldVariable.getName(), states);
                    node.setVariable(newVariable);
                    originalVariables.put(newVariable, oldVariable);
                    convertedVariables.put(oldVariable, newVariable);
                    convertedNodes.add(node);

                    List<Variable> newPotentialVariables = new ArrayList<>();
                    newPotentialVariables.add(newVariable);
                    for (ProbNode parent : parents) {
                        newPotentialVariables.add(parent.getVariable());
                    }

                    TablePotential newPotential = new TablePotential(newPotentialVariables,
                            oldPotential.getPotentialRole());
                    double[] values = newPotential.values;
                    int newVariableNumStates = newVariable.getNumStates();
                    for (int i = 0; i < numConfigurations; i++) {
                        int stateIndex = stateIndices.get(projectedValues[i]);
                        for (int j = 0; j < newVariableNumStates; j++) {
                            values[i * newVariableNumStates + j] = (stateIndex == j) ? 1 : 0;
                        }
                    }
                    node.setPotential(newPotential);
                }
            } else if (!node.getPotentials().isEmpty()
                    && potentialContainsConvertedNodes(node.getPotentials().get(0),
                            convertedVariables.keySet())) {
            	// Node is not numeric but contains numeric parents
                // Adapt potential to numeric finite states variables
                Potential newPotential = node.getPotentials().get(0).copy();
                List<Variable> convertedParentVariables = getConvertedParentVariables(newPotential,
                        convertedVariables);
                for(Variable convertedParentVariable : convertedParentVariables)
                {
                	newPotential.replaceNumericVariable(convertedParentVariable);
                }
                node.setPotential(newPotential);
            }
        }
        
        // Update evidence, replacing references to old variables with new ones
        List<Finding> findings = evidence.getFindings();
        for(Finding finding: findings)
        {
    		Variable originalVariable = finding.getVariable();
        	if(convertedVariables.containsKey(originalVariable))
        	{
        		try {
					evidence.removeFinding(originalVariable);
					Variable convertedVariable = convertedVariables.get(originalVariable);
					double numericalValue = convertedVariable.round(finding.getNumericalValue());
					int stateIndex = convertedVariable.getStateIndex(String.valueOf(numericalValue));
					evidence.addFinding(new Finding(convertedVariable,stateIndex));
				} catch (NoFindingException | InvalidStateException | IncompatibleEvidenceException e) {
					e.printStackTrace();
				}
        	}
        }

        return convertedNet;
    }
    
    public static ProbNet convertNumericalVariablesToFS(ProbNet probNet)
    {
    	return convertNumericalVariablesToFS(probNet, new EvidenceCase());
    }


    /**
     * 
     * @param potential
     * @param projectedPotential
     * @param configuration - configuration of projected variables
     */
    public static void sumProjectedPotential(TablePotential potential,
			TablePotential projectedPotential, EvidenceCase configuration) {
    	List<Variable> variables = potential.getVariables();
    	List<Variable> unprojectedVariables = projectedPotential.getVariables();
    	int[] unprojectedVariablesIndices = new int[unprojectedVariables.size()];
        int[] potentialVariableIndices = new int[variables.size()];
        // Set indices for initial configuration
        for(int i=0; i< potentialVariableIndices.length; ++i)
        {
        	Variable variable = variables.get(i);
        	if(configuration.contains(variable))
        	{
        		potentialVariableIndices[i] = configuration.getState(variable);
        	}else
        	{
        		unprojectedVariablesIndices[unprojectedVariables.indexOf(variable)] = i;
        		potentialVariableIndices[i] = 0;
        	}
        }
        // Add uncertain values if projected potential has them
        if(projectedPotential.isUncertain() && !potential.isUncertain())
        {
        	potential.uncertainValues = new UncertainValue[potential.getTableSize()];
        }
        
        Variable conditionedVariable = potential.getConditionedVariable();
        // Index of the current configuration in the projected potential
    	int projectedConfigIndex = 0;
        // Index of the current configuration in the original potential
        int configIndex = 0;
        boolean nextConfiguration = true;
        while(nextConfiguration)
        {
	        configIndex = potential.getPosition(potentialVariableIndices);
	        // TODO update potentialVariableIndices
	        for(int i = 0; i< conditionedVariable.getNumStates(); ++i)
	        {
				potential.values[configIndex + i] = projectedPotential.values[projectedConfigIndex + i];
				if(projectedPotential.isUncertain())
				{
					potential.uncertainValues[configIndex + i] = projectedPotential.uncertainValues[projectedConfigIndex + i];
				}
	        }
	        // TODO update projectedConfigIndex
	        projectedConfigIndex += conditionedVariable.getNumStates();		
	        
	        // Get next configuration
	        nextConfiguration = false;
	        int unprojectedParentIndex = (conditionedVariable == variables.get(unprojectedVariablesIndices[0]))? 1 : 0;
	        while(!nextConfiguration && unprojectedParentIndex < unprojectedVariablesIndices.length)
	        {
	        	int parentIndex = unprojectedVariablesIndices[unprojectedParentIndex];
	        	if(potentialVariableIndices[parentIndex]+1 < variables.get(parentIndex).getNumStates())
	        	{
	        		potentialVariableIndices[parentIndex]++;
	        		nextConfiguration = true;
	        	}else
	        	{
	        		potentialVariableIndices[parentIndex] = 0;
	        		unprojectedParentIndex++;
	        	}
	        }
        }
	}
    
    public static void sumProjectedPotential(TablePotential potential,
			TablePotential projectedPotential, List<Variable> projectedVariables, int[] projectedIndices) {
    	EvidenceCase configuration = new EvidenceCase();
    	for(int i=0; i<projectedVariables.size();++i)
    	{
    		try {
				configuration.addFinding(new Finding(projectedVariables.get(i), projectedIndices[i]));
			} catch (InvalidStateException | IncompatibleEvidenceException e) {
				e.printStackTrace();
			}
    	}
    	sumProjectedPotential(potential, projectedPotential, configuration);
    }
    

	@SuppressWarnings("unused")
	private static List<Variable> getConvertedPotentialVariables(Potential oldPotential,
			Map<Variable, Variable> convertedVariables) {
    	List<Variable> originalVariables = oldPotential.getVariables();
		List<Variable> convertedPotentialVariables = new ArrayList<>(originalVariables.size());
		for(Variable originalVariable : originalVariables)
		{
			if(convertedVariables.containsKey(originalVariable))
			{
				convertedPotentialVariables.add(convertedVariables.get(originalVariable));
			} else
			{
				convertedPotentialVariables.add(originalVariable);
			}
		}
		return convertedPotentialVariables;
	}

	private static List<Variable> getConvertedParentVariables(Potential potential,
            Map<Variable, Variable> convertedVariables) {
        List<Variable> convertedParentVariables = new ArrayList<>();
        for (Variable parentVariable : potential.getVariables()) {
            if (convertedVariables.containsKey(parentVariable)) {
                convertedParentVariables.add(convertedVariables.get(parentVariable));
            }
        }
        convertedParentVariables.remove(potential.getConditionedVariable());
        return convertedParentVariables;
    }

    private static boolean potentialContainsConvertedNodes(Potential potential,
            Set<Variable> convertedVariables) {
        boolean contains = false;
        List<Variable> variables = potential.getVariables();
        int i = 0;
        while (i < variables.size() && !contains) {
            contains = convertedVariables.contains(variables.get(i++));
        }
        return contains;
    }
}
