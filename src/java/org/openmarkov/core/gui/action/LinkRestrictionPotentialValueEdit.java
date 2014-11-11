package org.openmarkov.core.gui.action;

import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.potential.TablePotential;

@SuppressWarnings("serial")
public class LinkRestrictionPotentialValueEdit extends SimplePNEdit {

	/**
	 * The column of the table where is the potential
	 */
	private int col;
	/**
	 * The row of the table where is the potential
	 */
	private int row;
	/**
	 * The new value of the potential
	 */
	private Integer newValue;

	/***
	 * The link with the link restriction potential.
	 */
	private Link link;
	/****
	 * The parent node of the link.
	 */
	private ProbNode node1;
	/****
	 * The child node of the link.
	 */
	private ProbNode node2;

	/**
	 * the table potential before the edit
	 */
	private double[] lastTable;

	/***
	 * the table potential after the edit
	 */
	private double[] newTable;

	/**
	 * The potential of the link restriction
	 */
	private TablePotential tablePotential;

	public LinkRestrictionPotentialValueEdit(Link link, Integer newValue,
			int row, int col) {
		super(((ProbNode) link.getNode1().getObject()).getProbNet());
		this.link = link;
		this.node1 = (ProbNode) link.getNode1().getObject();
		this.node2 = (ProbNode) link.getNode2().getObject();
		this.col = col;
		this.row = row;
		this.tablePotential = (TablePotential) link
		.getRestrictionsPotential();
		this.newValue = newValue;
		this.lastTable = ((TablePotential) link.getRestrictionsPotential())
				.getValues().clone();
	}

	@Override
	public void doEdit() throws DoEditException {
		int numStates2 = node2.getVariable().getNumStates();
		int stateIndex1 = col - 1;
		int stateIndex2 = numStates2 - row;
		State state1 = node1.getVariable().getStates()[stateIndex1];
		State state2 = node2.getVariable().getStates()[stateIndex2];
		link.setCompatibilityValue(state1, state2, this.newValue.intValue());
		newTable = ((TablePotential) link.getRestrictionsPotential()).values
				.clone();

	}

	public void redo() {
		this.setTypicalRedo(false);
		super.redo();
		if (!link.hasRestrictions()) {
			link.initializesRestrictionsPotential();
			this.tablePotential = (TablePotential) link
					.getRestrictionsPotential();
		}
		tablePotential.setValues(newTable);
		checkRestrictionPotential(newTable);
	}

	public void undo() {
		super.undo();
		if (!link.hasRestrictions()) {
			link.initializesRestrictionsPotential();
			this.tablePotential = (TablePotential) link
					.getRestrictionsPotential();
		}
		tablePotential.setValues(lastTable);
		checkRestrictionPotential(lastTable);
	}

	public TablePotential getPotential() {
		return tablePotential;
	}

	/**
	 * Gets the row position associated to value edited if priorityList no
	 * exists
	 * 
	 * @param position
	 *            position of the value in the array of values
	 * @return the position in the table
	 */
	public int getRowPosition() {
		return row;
	}

	/**
	 * Gets the column where the value is edited
	 * 
	 * @return the column edited
	 */
	public int getColumnPosition() {
		return col;
	}

	public Integer getNewValue() {
		return newValue;
	}

	public void checkRestrictionPotential(double[] table) {
		boolean hasRestriction = false;

		for (int i = 0; i < table.length && !hasRestriction; i++) {
			if (table[i] == 0) {
				hasRestriction = true;
			}
		}
		if (!hasRestriction) {
			tablePotential = null;
			this.link.setRestrictionsPotential(tablePotential);
		}

	}

}
