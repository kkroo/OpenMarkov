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
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.constraint.annotation.Constraint;

@Constraint(name = "NoMultipleLinks", defaultBehavior = ConstraintBehavior.YES)
/*****
 * This class implements the NoMultipleLinks constraint, which establishes the following rules:
 * - The undirected link between A and B is both incompatible with any directed link between A and B.
 * - The directed link between A and B is compatible with the directed link between B and A 
 *@author ckonig
 */
public class NoMultipleLinks extends PNConstraint
{

    @Override
    public boolean checkProbNet (ProbNet probNet)
    {
        Graph graph = probNet.getGraph ();
        List<Node> nodesGraph = graph.getNodes ();
        for (Node node : nodesGraph)
        {
            for (Link link : node.getLinks ())
            {
                Node node1 = link.getNode1 ();
                Node node2 = link.getNode2 ();
                boolean directed = link.isDirected ();
                if (!checkLink (graph, node1, node2, directed))
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
        /*List<PNEdit> edits2 = UtilConstraints.getEditsType (edit, LinkEdit.class);
        for (PNEdit simpleEdit : edits2)
        {
            Node node1 = ((LinkEdit) simpleEdit).getProbNode1 ().getNode ();
            Node node2 = ((LinkEdit) simpleEdit).getProbNode2 ().getNode ();
            boolean directed = ((LinkEdit) simpleEdit).isDirected ();
            if (!checkLink (graph, node1, node2, directed))
            {
                return false;
            }
        }*/
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

    /*****
     * Checks if a link between <code>node1</code> and <code>node2</code>
     * satisfies the restriction of noMultipleLinks
     * @param graph
     * @param node1
     * @param node2
     * @param directed - true if the link is directed
     * @return code>true</code> if the link between <code>node1</code> and
     *         <code>node2</code>has no multipleLinks
     */
    private boolean checkLink (Graph graph, Node node1, Node node2, boolean directed)
    {
        if (directed)
        {
            return checkDirectedLink (graph, node1, node2);
        }
        else
        {
            return checkUndirectedLink (graph, node1, node2);
        }
    }

    /*********
     * Checks if a directed link between <code>node1</code> and
     * <code>node2</code> satisfies the restriction of noMultipleLinks
     * @param graph
     * @param node1
     * @param node2
     * @return <code>true</code> if the link between <code>node1</code> and
     *         <code>node2</code>has no multipleLinks
     */
    private boolean checkDirectedLink (Graph graph, Node node1, Node node2)
    {
        if (graph.getLink (node1, node2, false) != null)
        {
            return false;
        }
        return true;
    }

    /*****
     * Checks if a undirected link between <code>node1</code> and
     * <code>node2</code> satisfies the restriction of noMultipleLinks
     * @param graph
     * @param node1
     * @param node2
     * @return <code>true</code> if the link between <code>node1</code> and
     *         <code>node2</code>has no multipleLinks
     */
    private boolean checkUndirectedLink (Graph graph, Node node1, Node node2)
    {
        // neither a directed link from node1 -> node2 nor node2 ->
        // node1 may exist
        if ((graph.getLink (node1, node2, true) != null)
            || (graph.getLink (node2, node1, true) != null))
        {
            return false;
        }
        return true;
    }

    @Override
    protected String getMessage ()
    {
        return " no multiple links allowed.";
    }
}