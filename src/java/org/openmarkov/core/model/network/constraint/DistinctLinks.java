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
import org.openmarkov.core.model.graph.Graph;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.constraint.annotation.Constraint;

@Constraint(name = "DistinctLinks", defaultBehavior = ConstraintBehavior.YES)
/****
 * This class implements the DistinctLinks constraint, which establishes that the network can not have two equal links.
 * @author ckonig
 *
 */
public class DistinctLinks extends PNConstraint
{

    @Override
    public boolean checkProbNet (ProbNet probNet)
    {
        Graph graph = probNet.getGraph ();
        List<Node> nodesGraph = graph.getNodes ();
        for (Node node : nodesGraph)
        {
            if (node.getNumLinks () > (node.getNumChildren () + node.getNumParents () + node.getNumSiblings ()))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkEdit (ProbNet probNet, PNEdit edit)
        throws NonProjectablePotentialException,
        WrongCriterionException
    {
        Graph graph = probNet.getGraph ();
        List<PNEdit> edits = UtilConstraints.getSimpleEditsByType (edit, AddLinkEdit.class);
        for (PNEdit simpleEdit : edits)
        {
            Variable variable1 = ((AddLinkEdit) simpleEdit).getVariable1 ();
            Node node1 = probNet.getProbNode (variable1).getNode ();
            Variable variable2 = ((AddLinkEdit) simpleEdit).getVariable2 ();
            Node node2 = probNet.getProbNode (variable2).getNode ();
            boolean directed = ((AddLinkEdit) simpleEdit).isDirected ();
            if (!checkLink (graph, node1, node2, directed))
            {
                return false;
            }
        }
        /**List<PNEdit> edits2 = UtilConstraints.getEditsType (edit, LinkEdit.class);
        for (PNEdit simpleEdit : edits2)
        {
            LinkEdit linkEdit = (LinkEdit) simpleEdit;
            Node node1 = linkEdit.getProbNode1 ().getNode ();
            Node node2 = linkEdit.getProbNode2 ().getNode ();
            boolean directed = linkEdit.isDirected ();
            if (linkEdit.isAdd () && !checkLink (graph, node1, node2, directed))
            {
                return false;
            }
        }**/
        List<PNEdit> edits3 = UtilConstraints.getSimpleEditsByType (edit, InvertLinkEdit.class);
        for (PNEdit simpleEdit : edits3)
        {
            Variable variable1 = ((InvertLinkEdit) simpleEdit).getVariable1 ();
            Node node1 = probNet.getProbNode (variable1).getNode ();
            Variable variable2 = ((InvertLinkEdit) simpleEdit).getVariable2 ();
            Node node2 = probNet.getProbNode (variable2).getNode ();
            boolean directed = ((InvertLinkEdit) simpleEdit).isDirected ();
            if (!checkLink (graph, node2, node1, directed))
            {
                return false;
            }
        }
        return true;
    }

    /*******
     * Checks if a link between <code>node1</code> and <code>node2</code>
     * satisfies the restriction of distinctLinks
     * @param graph
     * @param node1
     * @param node2
     * @param directed
     * @return code>true</code> if the link between <code>node1</code> and
     *         <code>node2</code>has distinctLinks
     */
    private boolean checkLink (Graph graph, Node node1, Node node2, boolean directed)
    {
        if (directed)
        {
            if ((graph.getLink (node1, node2, true) != null))
            {
                return false;
            }
        }
        else
        {
            if ((graph.getLink (node1, node2, false) != null)
                || (graph.getLink (node2, node1, false) != null))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    protected String getMessage ()
    {
        return " no equal links allowed.";
    }
}
