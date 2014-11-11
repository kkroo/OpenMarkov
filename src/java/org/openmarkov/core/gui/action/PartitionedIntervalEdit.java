package org.openmarkov.core.gui.action;

import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNode;

@SuppressWarnings("serial")
public class PartitionedIntervalEdit extends SimplePNEdit{

	private PartitionedInterval currentPartitionedInterval;
	
	private PartitionedInterval newPartitionedInterval;
	
	private ProbNode probNode = null;
		
	public PartitionedIntervalEdit(ProbNode probNode, PartitionedInterval newPartitionedInterval) {
		super(probNode.getProbNet());
		this.probNode = probNode;
		this.newPartitionedInterval = newPartitionedInterval;
		this.currentPartitionedInterval = probNode.getVariable().getPartitionedInterval();
	}

	@Override
	public void doEdit() throws DoEditException {
		probNode.getVariable().setPartitionedInterval(newPartitionedInterval);
		
	}
	@Override
	public void undo(){
		super.undo();
		probNode.getVariable().setPartitionedInterval(currentPartitionedInterval);
	}

}
