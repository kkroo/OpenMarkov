package org.openmarkov.core.gui.action;

import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNode;

/*****
 * A simple edit which allows to add and modify intervals and modify them.
 * 
 * @author caroline
 * 
 */
@SuppressWarnings("serial")
public class RevelationIntervalEdit extends SimplePNEdit {

	/***
	 * Object which stores the revelation conditions
	 */
	private Link link;

	/**
	 * The action to carry out
	 */
	private StateAction stateAction;

	/****
	 * The new value of the limit
	 */
	private double newValue;

	/***
	 * The old value of the limit
	 */
	private double oldValue;
	/**
	 * index of the row selected
	 */
	private int rowSelected;
	/***
	 * Indicates whether the lower limit or upper limit is modified
	 */
	private boolean isLower;
	/*****
	 * The interval prior to the modification
	 */
	private PartitionedInterval lastInterval;

	/*****
	 * Creates a RevelationConditionEdit which carries out the modifications of
	 * a revealing condition interval.
	 * 
	 * @param link
	 * @param stateAction
	 * @param row
	 * @param newValue
	 * @param isLower
	 */

	public RevelationIntervalEdit(Link link, StateAction stateAction, int row,
			double newValue, boolean isLower) {
		super(((ProbNode) link.getNode1().getObject()).getProbNet());
		this.link = link;
		this.stateAction = stateAction;
		this.rowSelected = row;
		this.newValue = newValue;
		this.isLower = isLower;
	}

	@Override
	public void doEdit() throws DoEditException {

		switch (stateAction) {
		case ADD:
			PartitionedInterval newPartitionedInterval = getNewPartitionedInterval();
			lastInterval = newPartitionedInterval;
			link.addRevealingInterval(newPartitionedInterval);

			break;

		case REMOVE: {
			lastInterval = link.getRevealingIntervals().get(rowSelected);
			link.getRevealingIntervals().remove(rowSelected);
		}
			break;
		case MODIFY_VALUE_INTERVAL: {
			PartitionedInterval currentPartitionedInterval = link
					.getRevealingIntervals().get(rowSelected);
			int intervalIndex = isLower ? 0 : 1;
			oldValue = currentPartitionedInterval.getLimits()[intervalIndex];
			currentPartitionedInterval.getLimits()[intervalIndex] = newValue;

		}
			break;

		case MODIFY_DELIMITER_INTERVAL: {
			PartitionedInterval currentPartitionedInterval = link
					.getRevealingIntervals().get(rowSelected);
			int intervalIndex = isLower ? 0 : 1;
			currentPartitionedInterval.getBelongsToLeftSide()[intervalIndex] = !currentPartitionedInterval
					.getBelongsToLeftSide(intervalIndex);
		}
			break;

		}

	}

	public void undo() {
		super.undo();
		switch (stateAction) {
		case ADD:
			link.getRevealingIntervals().remove(lastInterval);
			break;
		case REMOVE:
			link.getRevealingIntervals().add(rowSelected, lastInterval);
			break;
		case MODIFY_VALUE_INTERVAL: {
			PartitionedInterval interval = link.getRevealingIntervals().get(
					rowSelected);
			int intervalIndex = isLower ? 0 : 1;
			interval.getLimits()[intervalIndex] = oldValue;
		}
			break;
		case MODIFY_DELIMITER_INTERVAL: {
			PartitionedInterval interval = link.getRevealingIntervals().get(
					rowSelected);
			int intervalIndex = isLower ? 0 : 1;
			interval.getBelongsToLeftSide()[intervalIndex] = !interval
					.getBelongsToLeftSide(intervalIndex);
		}
			break;
		}

	}

	/**
	 * This method add a new default subInterval, in the current
	 * PartitionedInterval object
	 * 
	 * @return The PartitionedInterval object with a new default subInterval
	 */

	private PartitionedInterval getNewPartitionedInterval() {
		if (link.getRevealingIntervals().isEmpty()) {
			return new PartitionedInterval(false, Double.NEGATIVE_INFINITY,
					Double.POSITIVE_INFINITY, false);
		} else {
			PartitionedInterval interval = link.getRevealingIntervals().get(
					link.getRevealingIntervals().size() - 1);
			return new PartitionedInterval(false, interval.getLimit(1),
					Double.POSITIVE_INFINITY, false);
		}
	}

}
