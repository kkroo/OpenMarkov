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
import org.openmarkov.core.action.InvertLinkEdit;
import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.graph.Graph;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.constraint.annotation.Constraint;

@Constraint (name = "NoCycle", defaultBehavior = ConstraintBehavior.YES)
public class NoCycle extends PNConstraint {

	@Override
	public boolean checkProbNet(ProbNet probNet) {
		Graph graph = probNet.getGraph();
		List<Node> nodesGraph = graph.getNodes();
		for (Node parent : nodesGraph) {
			List<Node> children = parent.getChildren();
			for (Node child : children) {
				if (graph.existsPath(child, parent, true)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	/** @param event <code>UndoableEditEvent</code>
	 * @return <code>true</code> if <code>event</code> comply with this 
	 *   constraint */
	public boolean checkEdit(ProbNet probNet, PNEdit edit) 
	throws NonProjectablePotentialException, 
	WrongCriterionException {
		List<PNEdit> edits = 
			UtilConstraints.getSimpleEditsByType(edit, AddLinkEdit.class);
		//int u=0;
		Graph graph = probNet.getGraph();
		for (PNEdit simpleEdit : edits) {
			if (((AddLinkEdit)simpleEdit).isDirected()) { // checks constraint
				Variable variable1 = ((AddLinkEdit)simpleEdit).getVariable1(); 
				Node node1 = probNet.getProbNode(variable1).getNode();
				Variable variable2 = ((AddLinkEdit)simpleEdit).getVariable2(); 
				Node node2 = probNet.getProbNode(variable2).getNode();
				if (graph.existsPath(node2, node1, true)) {
					return false;
				}
			}
		}
		List<PNEdit> edits2 = 
                UtilConstraints.getSimpleEditsByType(edit, InvertLinkEdit.class);
        for (PNEdit simpleEdit : edits2) {
            if (((InvertLinkEdit)simpleEdit).isDirected()) { // checks constraint
                Variable variable1 = ((InvertLinkEdit)simpleEdit).getVariable1(); 
                Node node1 = probNet.getProbNode(variable1).getNode();
                Variable variable2 = ((InvertLinkEdit)simpleEdit).getVariable2(); 
                Node node2 = probNet.getProbNode(variable2).getNode();
                probNet.getGraph ().removeLink (node1, node2, true);
                boolean existsPath = graph.existsPath(node1, node2, true);
                probNet.getGraph ().addLink (node1, node2, true);
                if (existsPath)
                {
                    return false;
                }
            }
        }		
		
		/**ArrayList<PNEdit> edits3 = 
			UtilConstraints.getEditsType(edit, LinkEdit.class);
		for (PNEdit simpleEdit : edits3) {
			if (((LinkEdit)simpleEdit).isDirected()) { // checks constraint
				Node node1 = ((LinkEdit)simpleEdit).getProbNode1().getNode();
				Node node2 = ((LinkEdit)simpleEdit).getProbNode2().getNode();
				if (graph.existsPath(node2, node1, true)) {
					return false;
				}
			}
		}*/
		return true;
	}

    @Override
    protected String getMessage ()
    {
        return "no cycles allowed";
    }
	
	
	
	

}