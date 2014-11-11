/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.inference;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialType;
import org.openmarkov.core.model.network.potential.SameAsPrevious;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;

public class BasicOperations {
    /**
     * The source probNet
     */
    // private static ProbNet sourceProbNet;
    private static TablePotential getUtilityFunction(ProbNode utilityProbNode, EvidenceCase evidence) {
        ProbNode probNode;
        TablePotential newPotential = null;
        Hashtable<ProbNode, TablePotential> hashtable = new Hashtable<>();
        if (!isSuperValueNode(utilityProbNode)) {
            try {
                newPotential = utilityProbNode.getPotentials().get(0).tableProject(evidence, null)
                        .get(0);
            } catch (NonProjectablePotentialException | WrongCriterionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            for (Node node : utilityProbNode.getNode().getParents()) {
                probNode = (ProbNode) node.getObject();
                hashtable.put(probNode, getUtilityFunction(probNode, evidence));
            }
            List<TablePotential> potentials = new ArrayList<TablePotential>(hashtable.values());
            Potential utilityPotential = utilityProbNode.getPotentials().get(0);
            if (utilityPotential.getPotentialType() == PotentialType.SUM
                    || (utilityPotential.getPotentialType() == PotentialType.SAME_AS_PREVIOUS && ((SameAsPrevious) utilityPotential)
                            .getOriginalPotential().getPotentialType() == PotentialType.SUM)) {
                newPotential = DiscretePotentialOperations.sum(potentials);
            } else {
                newPotential = DiscretePotentialOperations.multiply(potentials);
            }
            newPotential.setUtilityVariable(utilityProbNode.getVariable());
        }
        return newPotential;
    }

    private static boolean isSumSuperValueNode(ProbNet network, Variable utilityVariable) {
        List<Potential> potentials = network.getProbNode(utilityVariable).getPotentials();
        return (!potentials.isEmpty() && potentials.get(0).getPotentialType() == PotentialType.SUM);
    }

    /**
     * @param network
     * @return A list of utility nodes that have no children
     */
    public static List<Variable> getTerminalUtilityVariables(ProbNet network) {
        List<Variable> utilityVariables = network.getVariables(NodeType.UTILITY);
        List<Variable> terminalUtilityNodes = new ArrayList<Variable>();
        for (Variable utilityVariable : utilityVariables) {
            Node utilityNode = network.getProbNode(utilityVariable).getNode();
            if (utilityNode.getChildren().size() == 0) {
                terminalUtilityNodes.add(utilityVariable);
            }
        }
        return terminalUtilityNodes;
    }

    /**
     * @param network
     * @return A list of utility nodes that have no children
     */
    public static List<ProbNode> getTerminalUtilityNodes(ProbNet network) {
        List<ProbNode> utilityNodes = network.getProbNodes(NodeType.UTILITY);
        List<ProbNode> terminalUtilityNodes = new ArrayList<>();
        for (ProbNode utilityNode : utilityNodes) {
            if (utilityNode.getNode().getChildren().size() == 0) {
                terminalUtilityNodes.add(utilityNode);
            }
        }
        return terminalUtilityNodes;
    }

    /**
     * @param sourceProbNet
     * @return A copy of the probNet after removing utility nodes.
     */
    public static ProbNet removeUtilityNodes(ProbNet sourceProbNet) {
        ProbNet network = sourceProbNet.copy();
        for (Variable utilityVariable : network.getVariables(NodeType.UTILITY)) {
            ProbNode probNode = network.getProbNode(utilityVariable);
            network.removeProbNode(probNode);
        }
        return network;
    }

    /**
     * @param sourceProbNet
     * @param keepComponents
     * @param utilityVariableToKeep
     * @return A copy of the probNet by removing super-value nodes. When
     *         keepComponents is false the output network is equivalent to
     *         'sourceProbNet'. However, when keepComponents is true the output
     *         network has a utility node without children corresponding to each
     *         utility node in 'sourceProbNet', and the utility function is
     *         given explicitly in terms of the ancestors chance and decision
     *         nodes. Parameter 'leaveImplicitSum' only applies when
     *         'keepComponents' is false. When 'leaveImplicitSum' is true then
     *         the output is in the form of influence diagrams with an implicit
     *         sum like those processed by Jensen's variable elimination
     *         algorithm; otherwise the structure of super-value nodes is
     *         reduced into an only utility node. If 'utilityVariableToKeep' is
     *         different from null then it is the only potential to keep.
     *         Otherwise all the variables are considered.
     * @throws NodeNotFoundException
     * @throws ProbNodeNotFoundException
     */
    public static ProbNet removeSuperValueNodes(ProbNet sourceProbNet, EvidenceCase evidence,
            boolean keepComponents, boolean leaveImplicitSum, Variable utilityVariableToKeep) {
        ProbNet network = sourceProbNet.copy();
        List<ProbNode> utilityNodes = network.getProbNodes(NodeType.UTILITY);
        for (ProbNode utilityNode : utilityNodes) {
            Variable utilityVariable = utilityNode.getVariable();
            if ((isSuperValueNode(utilityNode) && utilityVariableToKeep == null)
                    || utilityVariable == utilityVariableToKeep) {
                TablePotential potential = getUtilityFunction(utilityNode, evidence);
                List<Node> parents = utilityNode.getNode().getParents();
                // remove links between supervalue nodes and their utility
                // parents
                for (Node parent : parents) {
                    ProbNode probNode = ((ProbNode) parent.getObject());
                    if (probNode.getNodeType() == NodeType.UTILITY) {
                        network.removeLink(probNode.getVariable(), utilityVariable, true);
                    }
                }
                // add links between of new potential of supervalue nodes
                for (Variable variable : potential.getVariables()) {
                    try {
                        network.addLink(variable, utilityVariable, true);
                    } catch (NodeNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                // sets the new potential
                List<Potential> newPotentials = new ArrayList<Potential>();
                newPotentials.add(potential);
                network.getProbNode(utilityVariable).setPotentials(newPotentials);
            }
        }
        if (!keepComponents) {
            List<Variable> nodesToKeep;
            if (utilityVariableToKeep == null) {
                if (leaveImplicitSum) {
                    // Get the probNodes such as there is an implicit sum
                    // between them
                    nodesToKeep = getUtilityNodesToKeepImplicitSum(sourceProbNet);
                } else {
                    nodesToKeep = getTerminalUtilityVariables(sourceProbNet);
                }
            } else {
                nodesToKeep = new ArrayList<Variable>();
                nodesToKeep.add(utilityVariableToKeep);
            }
            for (ProbNode utilityNode : utilityNodes) {
                if (!nodesToKeep.contains(utilityNode.getVariable())) {
                    network.removeProbNode(utilityNode);
                }
            }
        }
        return network;
    }
    
    public static ProbNet removeSuperValueNodes(ProbNet sourceProbNet, EvidenceCase evidence) {
        return removeSuperValueNodes(sourceProbNet, evidence, false, false, null);
    }

    /**
     * Assumes the structure of super value verifies that there are no more than
     * one path between two utility nodes.
     * 
     * @param sourceProbNet
     * @return A list of utility nodes that must be kept when we want to have a
     *         set of utility nodes with an implicit sum
     */
    private static List<Variable> getUtilityNodesToKeepImplicitSum(ProbNet sourceProbNet) {
        List<Variable> nodesToKeep = getTerminalUtilityVariables(sourceProbNet);
        while (thereAreSumNodesInTheList(sourceProbNet, nodesToKeep)) {
            removeASumNode(sourceProbNet, nodesToKeep);
        }
        return nodesToKeep;
    }

    /**
     * @param sourceProbNet
     * @param nodesToKeep
     * @return true if there are some sum node in the list 'nodesToKeep'
     */
    private static boolean thereAreSumNodesInTheList(ProbNet sourceProbNet,
            List<Variable> nodesToKeep) {
        boolean thereAre = false;
        for (int i = 0; (i < nodesToKeep.size()) && !thereAre; i++) {
            Variable auxVar = nodesToKeep.get(i);
            thereAre = (isSumSuperValueNode(sourceProbNet, auxVar));
        }
        return thereAre;
    }

    /**
     * @param sourceProbNet
     * @param nodesToKeep
     *            Removes a sum node of the list and add its parents to the list
     */
    private static void removeASumNode(ProbNet sourceProbNet, List<Variable> nodesToKeep) {
        boolean removed = false;
        for (int i = 0; (i < nodesToKeep.size()) && !removed; i++) {
            Variable auxVar = nodesToKeep.get(i);
            removed = (isSumSuperValueNode(sourceProbNet, auxVar));
            if (removed) {
                nodesToKeep.remove(auxVar);
                List<Node> parentNodes = null;
                try {
                    parentNodes = sourceProbNet
                            .getProbNode(sourceProbNet.getVariable(auxVar.getName())).getNode()
                            .getParents();
                } catch (ProbNodeNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                nodesToKeep.addAll(ProbNet.getVariablesOfNodes(parentNodes));
            }
        }
    }

    /**
     * Gets if the variable parameter is a supervalue node
     * 
     * @param utilityVariable
     *            the variable to test
     * @return true if the variable is a supervalue node. False if does not
     */
    private static boolean isSuperValueNode(ProbNode utilityNode) {
        int numOfUtilityParents = 0;
        List<Node> parents = utilityNode.getNode().getParents();
        for (Node parent : parents) {
            if (((ProbNode) parent.getObject()).getNodeType() == NodeType.UTILITY) {
                // if the node has two or more utility parents then is a super
                // value node
                if ((numOfUtilityParents++) >= 1) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * @param network
     * @param strategy
     * @param keepUtilityNodes
     * @return A copy of a network, where decision nodes are replaced by chance
     *         nodes whose probability tables are provided by the strategy. If
     *         'keepUtilityNodes' is true then utility nodes are kept in the
     *         network (the output would be an influence diagram). Otherwise the
     *         output is a Bayesian network.
     * @throws NodeNotFoundException
     */
    /*
     * public static ProbNet constructPolicyNetwork(ProbNet network,Strategy
     * strategy,boolean keepUtilityNodes) throws NodeNotFoundException
     * { ProbNet policyNetwork; policyNetwork =
     * network.copy(); //Remove utility nodes if keepUtilityNodes is false if
     * (!keepUtilityNodes){ ArrayList<ProbNode> utilities =
     * policyNetwork.getProbNodes(NodeType.UTILITY); for (ProbNode
     * util:utilities){ policyNetwork.removeProbNode(util); } } //Change
     * decision nodes by chance nodes whose probability potential //is given by
     * the corresponding policy ArrayList<ProbNode> decisions =
     * policyNetwork.getProbNodes(NodeType.DECISION); for (ProbNode
     * decision:decisions){ Variable varDecision = decision.getVariable();
     * //Remove decision policyNetwork.removeProbNode(decision); //Create a
     * chance node for the same variable policyNetwork.addVariable(varDecision,
     * NodeType.CHANCE); //Add the links to the children (chance) of decision
     * node ArrayList<Node> childrenOfDecision =
     * network.getProbNode(varDecision).getNode().getChildren();
     * ArrayList<ProbNode> probNodesChildrenOfDecision =
     * ProbNet.getProbNodesOfNodes(childrenOfDecision); for (ProbNode
     * child:probNodesChildrenOfDecision){ NodeType type = child.getNodeType();
     * if (type == NodeType.CHANCE){
     * policyNetwork.addLink(varDecision,child.getVariable(),true); } }
     * //Incoming Links for the variable ArrayList<Variable> domainPolicy =
     * strategy.getDomainOfPolicy(varDecision); for (Variable
     * varInDomain:domainPolicy){
     * policyNetwork.addLink(varInDomain,varDecision,true); } //Potential
     * probability for the variable ArrayList<Variable> domainPotential = new
     * ArrayList<Variable>(); domainPotential.add(varDecision);
     * domainPotential.addAll(domainPolicy); TablePotential tp = new
     * TablePotential(domainPotential,PotentialRole.CONDITIONAL_PROBABILITY);
     * GTablePotential<Choice> policy =
     * strategy.getPolicy(varDecision).getPotential(); int numElemsPolicy =
     * policy.elementTable.size(); for (int i = 0; i < numElemsPolicy ; i++){
     * int[] configurationPolicy = policy.getConfiguration(i); int
     * lenghtConfigurationPolicy = configurationPolicy.length; int[]
     * configurationTP = new int[lenghtConfigurationPolicy+1]; int[] choices =
     * policy.elementTable.get(i).getValues(); int choicesLength =
     * choices.length; double probabilityChoices = 1.0/choicesLength; for (int
     * indexChoice=0;indexChoice<choicesLength;indexChoice++){
     * configurationTP[0] = choices[indexChoice]; for (int j=0; j <
     * lenghtConfigurationPolicy ; j++){ configurationTP[j+1] =
     * configurationPolicy[j]; } int posConfigurationTP =
     * tp.getPosition(configurationTP); tp.values[posConfigurationTP] =
     * probabilityChoices; } policyNetwork.addPotential(tp); } } if
     * (keepUtilityNodes){ try { policyNetwork =
     * removeSuperValueNodes(policyNetwork,true,false); } catch
     * (ProbNodeNotFoundException e) { e.printStackTrace(); } } return
     * policyNetwork; }
     */
}
