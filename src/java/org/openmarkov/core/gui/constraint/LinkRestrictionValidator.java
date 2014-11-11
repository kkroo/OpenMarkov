package org.openmarkov.core.gui.constraint;

import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.constraint.NoLinkRestriction;

/******
 * This class validates if a link satisfies the conditions to have a link
 * restriction
 * 
 * @author ckonig
 * 
 */
public class LinkRestrictionValidator {

	/******
	 * Checks if the link satisfies the conditions to have a link restriction
	 * associated.
	 * 
	 * @param link
	 * @return <code>true</code> if a link restriction can be applied to the
	 *         link.
	 */
	public static boolean validate(Link link) {

		ProbNode node1 = ((ProbNode) link.getNode1().getObject());
		ProbNode node2 = ((ProbNode) link.getNode2().getObject());
		ProbNet net = node1.getProbNet();
		if (!net.hasConstraint(NoLinkRestriction.class)) {
			if ((node1.getNodeType() == NodeType.CHANCE || node1.getNodeType() == NodeType.DECISION)
					&& (node2.getNodeType() == NodeType.CHANCE || node2
							.getNodeType() == NodeType.DECISION)
					|| node2.getNodeType() == NodeType.UTILITY) {

				Variable var1 = node1.getVariable();
				Variable var2 = node2.getVariable();

				if (var1.getVariableType() == VariableType.FINITE_STATES)
					if (node2.getNodeType() != NodeType.UTILITY) {
						if (var2.getVariableType() == VariableType.FINITE_STATES) {
							return true;
						}
					} else {
						return true;
					}
			}

		}
		return false;
	}
}
