/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network.constraint;

import java.util.List;

import org.openmarkov.core.action.AddLinkEdit;
import org.openmarkov.core.action.InvertLinkEdit;
import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.constraint.annotation.Constraint;

@Constraint(name = "NoMixedParents", defaultBehavior = ConstraintBehavior.OPTIONAL)
/******
 * This class implements the NoMixedParents constraint, which establishes that all the parents
 * of a utility node belong to only one of these two sets of parents: 
 * - chance and decision nodes
 * - utility nodes
 * @author ckonig
 *
 */
public class NoMixedParents extends PNConstraint
{

    @Override
    public boolean checkProbNet (ProbNet probNet)
    {
        List<ProbNode> utilityNodes = probNet.getProbNodes (NodeType.UTILITY);
        for (ProbNode utilNode : utilityNodes)
        {
            boolean utilityParent = false;
            boolean chanceOrDecisionParent = false;
            List<Node> parents = utilNode.getNode ().getParents ();
            for (Node parent : parents)
            {
                ProbNode probParent = (ProbNode) parent.getObject ();
                if (probParent.getNodeType () == NodeType.UTILITY)
                {
                    utilityParent = true;
                }
                if (probParent.getNodeType () == NodeType.CHANCE
                    || probParent.getNodeType () == NodeType.DECISION)
                {
                    chanceOrDecisionParent = true;
                }
                if (utilityParent && chanceOrDecisionParent)
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean checkEdit (ProbNet probNet, PNEdit edit)
        throws NonProjectablePotentialException,
        WrongCriterionException
    {
        List<PNEdit> edits = UtilConstraints.getSimpleEditsByType (edit, AddLinkEdit.class);
        for (PNEdit simpleEdit : edits)
        {
            if (((AddLinkEdit) simpleEdit).isDirected ())
            {
                Variable variable2 = ((AddLinkEdit) simpleEdit).getVariable2 ();
                ProbNode node2 = probNet.getProbNode (variable2);
                if (node2.getNodeType () == NodeType.UTILITY)
                {
                    Variable variable1 = ((AddLinkEdit) simpleEdit).getVariable1 ();
                    ProbNode node1 = probNet.getProbNode (variable1);
                    return !hasMixedParents (node1, node2);
                }
            }
        }
        /**List<PNEdit> edits2 = UtilConstraints.getEditsType (edit, LinkEdit.class);
        for (PNEdit simpleEdit : edits2)
        {
            if (((LinkEdit) simpleEdit).isDirected ())
            {
                ProbNode node2 = ((LinkEdit) simpleEdit).getProbNode2 ();
                if (node2.getNodeType () == NodeType.UTILITY)
                {
                    ProbNode node1 = ((LinkEdit) simpleEdit).getProbNode1 ();
                    return !hasMixedParents (node1, node2);
                }
            }
        }**/
        List<PNEdit> edits3 = UtilConstraints.getSimpleEditsByType (edit, InvertLinkEdit.class);
        for (PNEdit simpleEdit : edits3)
        {
            Variable variable2 = ((InvertLinkEdit) simpleEdit).getVariable2 ();
            ProbNode node2 = probNet.getProbNode (variable2);
            if (node2.getNodeType () == NodeType.UTILITY)
            {
                Variable variable1 = ((InvertLinkEdit) simpleEdit).getVariable1 ();
                ProbNode node1 = probNet.getProbNode (variable1);
                return !hasMixedParents (node2, node1);
            }
        }
        return true;
    }

    /******
     * Checks if a node has mixed parents.
     * @param parentNode the parent node
     * @param childNode the child node
     * @return <code>true</code> if the <code>childNode</code> has mixedParents
     */
    private boolean hasMixedParents (ProbNode parentNode, ProbNode childNode)
    {
        boolean utilityParent = false;
        boolean chanceOrDecisionParent = false;
        if (parentNode.getNodeType () == NodeType.UTILITY)
        {
            utilityParent = true;
        }
        if (parentNode.getNodeType () == NodeType.DECISION
            || parentNode.getNodeType () == NodeType.CHANCE)
        {
            chanceOrDecisionParent = true;
        }
        for (Node parent : childNode.getNode ().getParents ())
        {
            ProbNode probParent = (ProbNode) parent.getObject ();
            if (probParent.getNodeType () == NodeType.UTILITY)
            {
                utilityParent = true;
            }
            if (probParent.getNodeType () == NodeType.CHANCE
                || probParent.getNodeType () == NodeType.DECISION)
            {
                chanceOrDecisionParent = true;
            }
            if (utilityParent && chanceOrDecisionParent)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    protected String getMessage ()
    {
        return "utility nodes can not have mixed parents.";
    }
}
