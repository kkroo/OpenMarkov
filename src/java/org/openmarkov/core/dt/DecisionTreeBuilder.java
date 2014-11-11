/*
 * Copyright 2013 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.dt;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongGraphStructureException;
import org.openmarkov.core.inference.PartialOrder;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.SumPotential;
import org.openmarkov.core.model.network.type.DecisionAnalysisNetworkType;
import org.openmarkov.core.model.network.type.InfluenceDiagramType;

public class DecisionTreeBuilder
{
    public static DecisionTreeElement buildDecisionTree (ProbNet probNet)
    {
        DecisionTreeElement root = null;
        if (probNet.getNetworkType () instanceof InfluenceDiagramType)
        {
            root = buildDecisionTreeFromID (probNet);
        }
        else if (probNet.getNetworkType () instanceof DecisionAnalysisNetworkType)
        {
            root =  new DecisionTreeBranch (probNet);
            ((DecisionTreeBranch)root).setChild((DecisionTreeNode)buildDecisionTreeFromDAN (probNet, probNet));
        }
        return root;
    }

    /**
     * Builds a decision tree from a decision analysis network
     * @param probNet
     * @return
     */    
    private static DecisionTreeElement buildDecisionTreeFromDAN (ProbNet originalProbNet, ProbNet probNet)
    {
        List<ProbNode> alwaysObservedVariables = getAlwaysObservedVariables (probNet);
        List<ProbNode> parentlessDecisions = getParentlessDecisions (probNet);
        List<ProbNode> neverObservedVariables = getNeverObservedVariables (probNet);
        DecisionTreeElement root = null;
        try{
            if(!alwaysObservedVariables.isEmpty ()) // Always observed variables 
            {
                // Get first node in the list
                ProbNode alwaysObservedNode = alwaysObservedVariables.get (0); 
                Variable alwaysObservedVariable = alwaysObservedNode.getVariable ();
                
                DecisionTreeNode treeNode = new DecisionTreeNode (alwaysObservedNode);
                ProbNet probNetWithoutObservedVariable = probNet.copy ();
                probNetWithoutObservedVariable.removeProbNode (probNetWithoutObservedVariable.getProbNode (alwaysObservedVariable));
                for (State state : alwaysObservedVariable.getStates ())
                {
                    DecisionTreeBranch treeBranch = new DecisionTreeBranch (originalProbNet,
                                                                            originalProbNet.getVariable (alwaysObservedVariable.getName ()), 
                                                                            state);
                    treeNode.addChild (treeBranch);
                    ProbNet restrictedProbNet = applyRestrictionsAndReveal(probNetWithoutObservedVariable, alwaysObservedNode, state, originalProbNet);
                    treeBranch.setChild ((DecisionTreeNode)buildDecisionTreeFromDAN (originalProbNet, restrictedProbNet));
                }         
                root = treeNode;
            }else if(!parentlessDecisions.isEmpty ()) // Parentless decision nodes
            {
                if(parentlessDecisions.size () == 1)
                {
                    ProbNode decisionNode = parentlessDecisions.get (0);
                    DecisionTreeNode treeNode = new DecisionTreeNode (decisionNode);
                    Variable decisionVariable = decisionNode.getVariable ();
                    ProbNet probNetWithoutDecisionNode = probNet.copy ();
                    probNetWithoutDecisionNode.removeProbNode (probNetWithoutDecisionNode.getProbNode (decisionNode.getVariable ()));
                    for (State state : decisionVariable.getStates ())
                    {
                        DecisionTreeBranch treeBranch = new DecisionTreeBranch (originalProbNet,
                                                                                originalProbNet.getVariable (decisionVariable.getName ()),
                                                                                state);
                        treeNode.addChild (treeBranch);
                        ProbNet restrictedProbNet = applyRestrictionsAndReveal (probNetWithoutDecisionNode, decisionNode, state, originalProbNet);
                        treeBranch.setChild ((DecisionTreeNode) buildDecisionTreeFromDAN (originalProbNet, restrictedProbNet));
                    }          
                    root = treeNode;
                }else // If more than one parentless decision, introduce metadecision
                {
                    Variable orderDecisionVariable = new  Variable("OD");
                    State[] states = new State[parentlessDecisions.size ()];
                    for(int i = 0; i < parentlessDecisions.size (); ++i)
                    {
                        states[i] = new State (parentlessDecisions.get (i).getName ());
                    }
                    orderDecisionVariable.setStates (states);
                    ProbNode orderDecisionNode = new ProbNode (probNet, orderDecisionVariable, NodeType.DECISION);
                    DecisionTreeNode treeNode = new DecisionTreeNode (orderDecisionNode);
                    int i= 0;
                    for (State metaState : orderDecisionVariable.getStates ())
                    {
                        DecisionTreeBranch treeBranch = new DecisionTreeBranch (originalProbNet,
                                                                                orderDecisionVariable, 
                                                                                metaState);
                        treeNode.addChild (treeBranch);
                        ProbNode parentlessDecisionNode = parentlessDecisions.get (i);
                        Variable parentlessDecisionVariable = parentlessDecisionNode.getVariable ();
                        DecisionTreeNode decisionTreeNode = new DecisionTreeNode (parentlessDecisionNode);
                        treeBranch.setChild (decisionTreeNode);
                        ProbNet probNetWithoutDecisionNode = probNet.copy ();
                        probNetWithoutDecisionNode.removeProbNode (probNetWithoutDecisionNode.getProbNode (parentlessDecisionVariable));
                        for (State state : parentlessDecisionVariable.getStates ())
                        {
                            DecisionTreeBranch subTreeBranch = new DecisionTreeBranch (originalProbNet,
                                                                                       originalProbNet.getVariable (parentlessDecisionVariable.getName ()), 
                                                                                    state);
                            decisionTreeNode.addChild (subTreeBranch);
                            ProbNet restrictedProbNet = applyRestrictionsAndReveal (probNetWithoutDecisionNode, parentlessDecisionNode, state, originalProbNet);
                            subTreeBranch.setChild ((DecisionTreeNode)buildDecisionTreeFromDAN (originalProbNet, restrictedProbNet));
                        }          
                        ++i;
                        
                    }
                    root = treeNode;
                }
            }else if(!neverObservedVariables.isEmpty ()) // Never observed variables
            {
                ProbNet dtProbNet = probNet.copy ();
                ProbNode neverObservedNode = neverObservedVariables.get (0);
                Variable neverObservedVariable = neverObservedNode.getVariable ();
                DecisionTreeNode treeNode = new DecisionTreeNode (neverObservedNode);
                dtProbNet.removeProbNode (dtProbNet.getProbNode (neverObservedVariable));
                for (State state : neverObservedVariable.getStates ())
                {
                    DecisionTreeBranch treeBranch = new DecisionTreeBranch (originalProbNet,
                                                                            originalProbNet.getVariable (neverObservedVariable.getName ()), 
                                                                            state);
                    treeNode.addChild (treeBranch);
                    treeBranch.setChild ((DecisionTreeNode)buildDecisionTreeFromDAN (originalProbNet, dtProbNet));
                }         
                root = treeNode;            
            }else // Utility nodes
            {
                ProbNet dtProbNet = probNet.copy ();
                ProbNode svNode = getSuperValueNode (dtProbNet);
                root = addUtilityNodes (svNode);
            }
        }catch(ProbNodeNotFoundException ignoreException)
        {
            ignoreException.printStackTrace ();
        }
        
        return root;
    }
    
    /**
     * Builds a decision tree from an influence diagram
     * @param probNet
     * @return
     */
    private static DecisionTreeElement buildDecisionTreeFromID (ProbNet probNet)
    {
        ProbNet dtProbNet = probNet.copy ();
        ProbNode svNode = getSuperValueNode (dtProbNet);
        List<Variable> variables = getPartiallySortedVariables (dtProbNet);
        DecisionTreeElement root = new DecisionTreeBranch (dtProbNet);
        Stack<DecisionTreeElement> treeStack = new Stack<> ();
        treeStack.push (root);
        List<DecisionTreeBranch> leaves = new ArrayList<> ();
        // Build tree with decision & utility nodes
        while (!treeStack.isEmpty ())
        {
            DecisionTreeElement treeElement = treeStack.pop ();
            // If a node
            if (treeElement instanceof DecisionTreeNode)
            {
                ProbNode probNode = ((DecisionTreeNode) treeElement).getProbNode ();
                // Get next variable in the list
                Variable variable = probNode.getVariable ();
                for (State state : variable.getStates ())
                {
                    DecisionTreeBranch treeBranch = new DecisionTreeBranch (dtProbNet, 
                                                                            variable,
                                                                            state);
                    ((DecisionTreeNode) treeElement).addChild (treeBranch);
                    treeStack.push (treeBranch);
                }
            }
            // If a branch
            else if (treeElement instanceof DecisionTreeBranch)
            {
                Variable branchVariable = ((DecisionTreeBranch) treeElement).getBranchVariable ();
                Variable childVariable = null;
                // If this is the root 
                if (branchVariable == null)
                {
                    childVariable = variables.get (0);
                }
                // If this neither the root nor a leaf                
                else if (variables.indexOf (branchVariable) + 1 < variables.size ())
                {
                    childVariable = variables.get (variables.indexOf (branchVariable) + 1);
                }
                // If this is a leaf
                else
                {
                    leaves.add ((DecisionTreeBranch) treeElement);
                }
                if (childVariable != null)
                {
                    DecisionTreeNode child = new DecisionTreeNode (dtProbNet.getProbNode (childVariable));
                    ((DecisionTreeBranch) treeElement).setChild (child);
                    treeStack.push (child);
                }
            }
        }

        // Add utility trees at the tip of each leaf
        for (DecisionTreeBranch leaf : leaves)
        {
            leaf.setChild (addUtilityNodes (svNode));
        }
        return root;
    }

    /**
     * Looks for the super value node. If there is none, it creates it.
     * @param probNet
     * @return
     */
    private static ProbNode getSuperValueNode (ProbNet probNet)
    {
        ProbNode svNode = null;
        // Look for leaves
        List<ProbNode> leaves = getUtilityLeaves (probNet);
        // if there is more than one leave, create a new super value node
        if (leaves.size () > 1)
        {
            Variable svVariable = new Variable ("Global Utility");
            svNode = probNet.addProbNode (svVariable, NodeType.UTILITY);
            List<Variable> leafVariables = new ArrayList<> (leaves.size ());
            for (ProbNode leafNode : leaves)
            {
                leafVariables.add (leafNode.getVariable ());
            }
            svNode.addPotential (new SumPotential (leafVariables, PotentialRole.UTILITY));
            for (ProbNode leaf : leaves)
            {
                probNet.addLink (leaf, svNode, true);
            }
        }
        else if (leaves.size () == 1)
        {
            svNode = leaves.get (0);
        }
        return svNode;
    }

    private static List<ProbNode> getUtilityLeaves (ProbNet probNet)
    {
        List<ProbNode> leaves = new ArrayList<> ();
        for (ProbNode node : probNet.getProbNodes ())
        {
            if (node.getNodeType () == NodeType.UTILITY
                && node.getNode ().getChildren ().isEmpty ())
            {
                leaves.add (node);
            }
        }
        return leaves;
    }

    /**
     * Using PartialOrder generates a sorted plain list of decision and chance variables
     * @param probNet
     * @return
     */
    private static List<Variable> getPartiallySortedVariables (ProbNet probNet)
    {
        List<Variable> variables = null;
        PartialOrder partialOrder = null;
        try
        {
            partialOrder = new PartialOrder (probNet);
        }
        catch (WrongGraphStructureException e)
        {
            e.printStackTrace ();
        }
        variables = new ArrayList<Variable> (partialOrder.getNumVariables ());
        for (List<Variable> variableSubList : partialOrder.getOrder ())
        {
            variables.addAll (variableSubList);
        }
        return variables;
    }
    
    /**
     * Adds a utility tree at the tip of each leaf
     * @param leaves
     * @param svNode
     */
    private static DecisionTreeNode addUtilityNodes (ProbNode svNode)
    {
        // Add utility nodes
        DecisionTreeNode svTreeNode = new DecisionTreeNode (svNode);

        Stack<DecisionTreeNode> utilityTreeStack = new Stack<> ();
        utilityTreeStack.push (svTreeNode);
        while (!utilityTreeStack.isEmpty ())
        {
            DecisionTreeNode utilityTreeNode = utilityTreeStack.pop ();
            ProbNode utilityNode = utilityTreeNode.getProbNode ();
            for (Node parentNode : utilityNode.getNode ().getParents ())
            {
                ProbNode parentProbNode = (ProbNode) parentNode.getObject ();
                if (parentProbNode.getNodeType () == NodeType.UTILITY)
                {
                    DecisionTreeNode treeNode = new DecisionTreeNode (parentProbNode);
                    utilityTreeNode.addChild (treeNode);
                    utilityTreeStack.push (treeNode);
                }
            }
        }
        return svTreeNode;
   }
    
    private static ProbNet applyRestrictionsAndReveal(ProbNet probNet, ProbNode probNode, State state, ProbNet originalProbNet)
    {
        ProbNet probNetCopy = probNet.copy ();
        
        for (Link link : probNode.getNode ().getLinks ())
        {
            if(link.getNode1 ().getObject ().equals (probNode)) // Our node is the source node
            {
                ProbNode destinationNode = probNetCopy.getProbNode (((ProbNode) link.getNode2 ().getObject ()).getVariable ());
                if(destinationNode.getNodeType () == NodeType.CHANCE)
                {
                    if (link.hasRevealingConditions ())
                    {
                        if (link.getRevealingStates ().contains (state))
                        {
                            destinationNode.setAlwaysObserved (true);
                        }
                    }
                }
                if(link.hasRestrictions ())
                {
                    List<State> nonRestrictedStates = new ArrayList<State>();
                    Potential linkRestrictions = link.getRestrictionsPotential ();
                    EvidenceCase configuration = new EvidenceCase ();
                    try
                    {
                        try
                        {
                            configuration.addFinding (new Finding (originalProbNet.getVariable (probNode.getVariable ().getName ()), state));
                        }
                        catch (ProbNodeNotFoundException e){
                            e.printStackTrace();
                        }
                        for(State destState : destinationNode.getVariable ().getStates ())
                        {
                            configuration.changeFinding (new Finding (destinationNode.getVariable (), destState));
                            if (linkRestrictions.getProbability (configuration) > 0)
                            {
                                nonRestrictedStates.add (destState);
                            }
                        }
                    }
                    catch (InvalidStateException | IncompatibleEvidenceException e)
                    {
                        // Not going to happen
                    }
                
                    if(nonRestrictedStates.isEmpty ())
                    {
                        // Remove destination node and its descendants!
                        Stack<ProbNode> disposableNodes = new Stack<> ();
                        disposableNodes.push (destinationNode);
                        while(!disposableNodes.isEmpty ())
                        {
                            ProbNode disposableNode = disposableNodes.pop ();
                            
                            for(Node descendant : disposableNode.getNode ().getChildren ())
                            {
                                disposableNodes.push((ProbNode)descendant.getObject ());
                            }
                            
                            probNetCopy.removeProbNode (disposableNode);
                        }
                        
                    }else if(nonRestrictedStates.size () == 1) // Remove variables with a single variable
                    {
                        ProbNet probNetWithoutSingleStateVariable = probNetCopy.copy ();
                        probNetWithoutSingleStateVariable.removeProbNode (probNetWithoutSingleStateVariable.getProbNode (destinationNode.getVariable ()));
                        probNetCopy = applyRestrictionsAndReveal(probNetWithoutSingleStateVariable, destinationNode, nonRestrictedStates.get (0), originalProbNet);
                    }else if(nonRestrictedStates.size () < destinationNode.getVariable ().getStates ().length)
                    {
                        // At least one of the states of the destination node is restricted.
                        // Make a copy of the variable and remove the restricted states
                        State[] unrestrictedStates = nonRestrictedStates.toArray (new State[0]);
                        Variable restrictedVariable = new Variable (destinationNode.getVariable ().getName (), unrestrictedStates);
                        restrictedVariable.setVariableType (destinationNode.getVariable ().getVariableType ());
                        destinationNode.setVariable(restrictedVariable);
                    }else
                    {
                        // No state restricted, leave destinationNode as it is 
                    }
                }
            }
         } 
        return probNetCopy;
    }

    private static List<ProbNode> getNeverObservedVariables (ProbNet dtProbNet)
    {
        List<ProbNode> neverObservedVariables = new ArrayList<> ();
        for (ProbNode probNode : dtProbNet.getProbNodes (NodeType.CHANCE))
        {
            if (probNode.getNode ().getParents ().isEmpty ())
            {
                neverObservedVariables.add (probNode);
            }
        }
        return neverObservedVariables;    
    }

    /**
     * Generates a list of decision nodes that don't have parent decisions
     * @param probNet
     * @return
     */
    private static List<ProbNode> getParentlessDecisions (ProbNet probNet)
    {
        List<ProbNode> parentlessDecisions = new ArrayList<> ();
        for (ProbNode probNode : probNet.getProbNodes (NodeType.DECISION))
        {
            boolean hasParentDecisions = false;
            Stack<ProbNode> parentNodes = new Stack<> ();
            parentNodes.push (probNode);
            while (!hasParentDecisions && !parentNodes.isEmpty ())
            {
                ProbNode node = parentNodes.pop ();
                for (Node parent : node.getNode ().getParents ())
                {
                    ProbNode parentNode = (ProbNode) parent.getObject ();
                    hasParentDecisions |= parentNode.getNodeType () == NodeType.DECISION;
                    parentNodes.push (parentNode);
                }
            }
            if (!hasParentDecisions)
            {
                parentlessDecisions.add (probNode);
            }
        }
        return parentlessDecisions;
    }

    /**
     * Gets the list of always-observed-variables in the DAN 
     * @param dtProbNet
     * @return
     */
    private static List<ProbNode> getAlwaysObservedVariables (ProbNet dtProbNet)
    {
        List<ProbNode> alwaysObservedVariables = new ArrayList<> ();
        for (ProbNode probNode : dtProbNet.getProbNodes ())
        {
            if (probNode.isAlwaysObserved ())
            {
                alwaysObservedVariables.add (probNode);
            }
        }
        return alwaysObservedVariables;
    }    
}
