package org.openmarkov.core.gui.constraint;

import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.constraint.NoRevelationArc;

public class AlwaysObservedPropertyValidator {
	/*****
	 * Checks if a node can have the alwaysObserved property.
	 * @param node
	 * @return <code>true</code> if the node can have the alwaysObserved property.
	 */
	public static boolean validate(ProbNode node) {
		if (!node.getProbNet().hasConstraint(NoRevelationArc.class)) {
			if (node.getNodeType() == NodeType.CHANCE) {
				return true;
			}
		}
		return false;
	}

}
