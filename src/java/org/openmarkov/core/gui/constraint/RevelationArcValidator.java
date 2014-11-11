package org.openmarkov.core.gui.constraint;

import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.constraint.NoRevelationArc;

/*****
 * This class validates if a link satisfies the conditions to be a revelation
 * arc.
 * 
 * @author ckonig
 * 
 */
public class RevelationArcValidator {
	/**********
	 * Checks if the link satisfies the conditions to have the condition of
	 * revelation arc
	 * 
	 * @param link
	 * @return <code>true</code> if a link restriction can be applied to the
	 *         link.
	 */
	public static boolean validate(Link link) {

		ProbNode node1 = ((ProbNode) link.getNode1().getObject());
		ProbNode node2 = ((ProbNode) link.getNode2().getObject());
		ProbNet net = node1.getProbNet();
		if (!net.hasConstraint(NoRevelationArc.class)) {

			if ((node1.getNodeType() == NodeType.CHANCE || node1.getNodeType() == NodeType.DECISION)
					&& (node2.getNodeType() == NodeType.CHANCE)) {

				return true;
			}
		}
		return false;
	}

}
