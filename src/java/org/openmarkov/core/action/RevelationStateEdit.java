package org.openmarkov.core.action;

import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;

/*******
 * 
 * A simple edit which allows to add or remove a revealing state of a link.
 * 
 */
@SuppressWarnings("serial")
public class RevelationStateEdit extends SimplePNEdit {

	private Link link;

	private State newState;

	private boolean selected;

	public RevelationStateEdit(Link link, State state, boolean selected) {
		super(((ProbNode) link.getNode1().getObject()).getProbNet());
		this.link = link;
		this.selected = selected;
		this.newState = state;

	}

	@Override
	public void doEdit() throws DoEditException {
		if (selected) {
			link.addRevealingState(newState);
		} else {
			link.removeRevealingState(newState);

		}

	}

	public void undo() {
		super.undo();
		if (selected) {
			link.removeRevealingState(newState);
		} else {
			link.addRevealingState(newState);

		}
	}

}
