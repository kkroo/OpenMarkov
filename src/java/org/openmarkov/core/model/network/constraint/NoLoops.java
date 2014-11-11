/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.model.network.constraint;

import java.util.List;

import org.openmarkov.core.action.AddLinkEdit;
import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.graph.Graph;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.constraint.annotation.Constraint;

@Constraint (name = "NoLoops", defaultBehavior = ConstraintBehavior.OPTIONAL)
public class NoLoops extends PNConstraint {

	@Override
    public boolean checkEdit (ProbNet probNet, PNEdit edit)
        throws NonProjectablePotentialException,
        WrongCriterionException{
	    List<PNEdit> edits = UtilConstraints.getSimpleEditsByType(edit,
				AddLinkEdit.class);
	
		Graph graph = probNet.getGraph();
		for (PNEdit simpleEdit : edits) {
			Variable variable1 = ((AddLinkEdit) simpleEdit).getVariable1();
			Node node1 = probNet.getProbNode(variable1).getNode();
			Variable variable2 = ((AddLinkEdit) simpleEdit).getVariable2();
			Node node2 = probNet.getProbNode(variable2).getNode();
			if (graph.existsPath(node2, node1, false)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean checkProbNet(ProbNet probNet) {
		Graph graph = probNet.getGraph();
		List<Node> nodesGraph = graph.getNodes();
		boolean probNetOK = true;
		boolean directed;
		for (Node node1 : nodesGraph) {
			List<Node> neighbors = node1.getNeighbors();
			for (Node node2 : neighbors) {
				if (node2.isChild(node1)) {
					graph.removeLink(node2, node1, true);
					directed = true;
				} else if (node1.isSibling(node2)) {
					graph.removeLink(node1, node2, false);
					directed = false;
				} else {
					continue;
				}
				if (graph.existsPath(node1, node2, false)) {
					probNetOK = false;
				}
				if (directed) {
					graph.addLink(node1, node2, true);
				} else {
					graph.addLink(node1, node2, false);
				}
				if (!probNetOK) {
					return probNetOK;
				}
			}
		}
		return probNetOK;
	}

    @Override
    protected String getMessage ()
    {
        return "no loops allowed";
    }

}
