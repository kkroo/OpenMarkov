package org.openmarkov.core.gui.component;

import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;

import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.action.RevelationIntervalEdit;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNode;
/******
 * This class implements a Discretize table for the edition of intervals. The intervals can be continuous or discontinuous.
 * @author caroline
 *
 */
@SuppressWarnings("serial")
public class RevelationArcDiscretizeTablePanel extends DiscretizeTablePanel {
	/****
	 * Link for which the revelation conditions are stores.
	 */
	private Link link;

	/**
	 * default constructor
	 * 
	 * @wbp.parser.constructor
	 */
	public RevelationArcDiscretizeTablePanel(String[] newColumns, Link link) {
		this(newColumns, new Object[0][0], "s", (ProbNode) link.getNode1()
				.getObject());
		this.link = link;

	}

	public RevelationArcDiscretizeTablePanel(String[] newColumns,
			Object[][] noKeyData, String newKeyPrefix, ProbNode probNode) {
		super(newColumns, noKeyData, newKeyPrefix, probNode);
		super.getDownValueButton().setVisible(false);
		super.getUpValueButton().setVisible(false);
		super.getNegativeInfinityButton().setVisible(false);
		super.getPositiveInfinityButton().setVisible(false);
		super.getStandardDomainButton().setVisible(false);
	}

	/**
	 * Method to define the specific listeners in this table (not defined in the
	 * common KeyTable hierarchy
	 */
	protected void defineTableSpecificListeners() {

		valuesTable.addMouseListener(this);
	}

	public void setPartitionedInterval() {

		int subIntervals = 0;
		for (PartitionedInterval partitionInterval : link
				.getRevealingIntervals()) {
			subIntervals += partitionInterval.getNumSubintervals();
		}

		Object[][] allIntervalTable = new Object[subIntervals][6];
		int accumulatedIndex = 0;
		for (PartitionedInterval partitionInterval : link
				.getRevealingIntervals()) {
			Object[][] intervalTable = partitionInterval.convertToTableFormat();
			for (int i = 0; i < intervalTable.length; i++) {
				System.arraycopy(intervalTable[i], 0, allIntervalTable[i
						+ accumulatedIndex], 0, intervalTable[0].length);
			}
			accumulatedIndex += intervalTable.length;
		}
		setData(allIntervalTable);
	}

	/**
	 * Invoked when the button 'add' is pressed.
	 */
	@Override
	protected void actionPerformedAddValue() {

		int rowCount = 0;
		rowCount = valuesTable.getRowCount();
		int newIndex = 0;
		newIndex = valuesTable.getRowCount();
		RevelationIntervalEdit revelationArcStateEdit = new RevelationIntervalEdit(
				link, StateAction.ADD, newIndex, 0, false);
		try {
			probNode.getProbNet().doEdit(revelationArcStateEdit);
			setPartitionedInterval();
		} catch (ConstraintViolationException | CanNotDoEditException
				| NonProjectablePotentialException | WrongCriterionException
				| DoEditException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
			                              stringDatabase.getString(e.getMessage()),
			                              stringDatabase.getString(e.getMessage()),
					JOptionPane.ERROR_MESSAGE);

		}

		valuesTable.getSelectionModel()
				.setSelectionInterval(rowCount, rowCount);

	}

	/**
	 * Invoked when the button 'remove' is pressed.
	 */
	@Override
	protected void actionPerformedRemoveValue() {
		int selectedRow = valuesTable.getSelectedRow();

		RevelationIntervalEdit revelationArcStateEdit = new RevelationIntervalEdit(
				link, StateAction.REMOVE, selectedRow, 0, false);
		try {
			probNode.getProbNet().doEdit(revelationArcStateEdit);
			cancelCellEditing();
			setPartitionedInterval();
		} catch (ConstraintViolationException | CanNotDoEditException
				| NonProjectablePotentialException | WrongCriterionException
				| DoEditException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
			                              stringDatabase.getString(e.getMessage()),
			                              stringDatabase.getString(e.getMessage()),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/****
	 * Invoked when the table cells are edited
	 */
	public void tableChanged(TableModelEvent arg0) {
		int column = arg0.getColumn();
		int row = arg0.getLastRow();
		int numRows = ((DiscretizeTableModel) arg0.getSource()).getRowCount();
		boolean lower = (column - 1 == LOWER_BOUND_SYMBOL_COLUMN_INDEX ? true : false);
		if (arg0.getType() == TableModelEvent.UPDATE
				&& ((DiscretizeTableModel) arg0.getSource()).getValueAt(row,
						column) instanceof Double) {
			double newValue = (Double) ((DiscretizeTableModel) arg0.getSource())
					.getValueAt(row, column);
			if (lower) {
				double upperLimit = (Double) ((DiscretizeTableModel) arg0
						.getSource()).getValueAt(row, UPPER_BOUND_VALUE_COLUMN_INDEX);
				if (upperLimit < newValue) {
					JOptionPane.showMessageDialog(this, stringDatabase
							.getString("IntervalInconsistent.Text.Label"),
							stringDatabase
									.getString("IntervalEditError.Text.Label"),
							JOptionPane.ERROR_MESSAGE);
				}

			} else {
				double lowerLimit = (Double) ((DiscretizeTableModel) arg0
						.getSource()).getValueAt(row, LOWER_BOUND_VALUE_COLUMN_INDEX);
				if (lowerLimit > newValue) {
					JOptionPane.showMessageDialog(this, stringDatabase
							.getString("IntervalInconsistent.Text.Label"),
							stringDatabase
									.getString("IntervalEditError.Text.Label"),
							JOptionPane.ERROR_MESSAGE);

				}
			}

			if (lower && row > 0) {
				double previousLimit = (Double) ((DiscretizeTableModel) arg0
						.getSource()).getValueAt(row - 1,
						        UPPER_BOUND_VALUE_COLUMN_INDEX);
				if (previousLimit > newValue)
					JOptionPane.showMessageDialog(this, stringDatabase
							.getString("IntervalOverlap.Text.Label"),
							stringDatabase
									.getString("IntervalEditError.Text.Label"),
							JOptionPane.ERROR_MESSAGE);

			}
			if (!lower && row < numRows) {
				double nextLimit = (Double) ((DiscretizeTableModel) arg0
						.getSource()).getValueAt(row + 1,
						LOWER_BOUND_VALUE_COLUMN_INDEX);
				if (nextLimit < newValue)
					JOptionPane.showMessageDialog(this, stringDatabase
							.getString("IntervalOverlap.Text.Label"),
							stringDatabase
									.getString("IntervalEditError.Text.Label"),
							JOptionPane.ERROR_MESSAGE);

			}

			RevelationIntervalEdit nodePartitionedIntervalEdit = new RevelationIntervalEdit(
					link, StateAction.MODIFY_VALUE_INTERVAL, row, newValue, lower);
			try {
				probNode.getProbNet().doEdit(nodePartitionedIntervalEdit);
				setPartitionedInterval();
			} catch (ConstraintViolationException | CanNotDoEditException
					| NonProjectablePotentialException
					| WrongCriterionException | DoEditException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this,
				                              stringDatabase.getString(e.getMessage()),
				                              stringDatabase.getString(e.getMessage()),
						JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	public void mouseClicked(MouseEvent e) {
		int fila = valuesTable.rowAtPoint(e.getPoint());
		int columna = valuesTable.columnAtPoint(e.getPoint());
		if ((fila > -1) && (columna > -1)) {
			changeIntervalDiscretize(fila, columna);
		}

	}

	/****
	 * Invoked when the interval delimiters are edited
	 * 
	 * @param fila
	 * @param column
	 */
	private void changeIntervalDiscretize(int fila, int column) {
		if (column == LOWER_BOUND_SYMBOL_COLUMN_INDEX
				|| column == UPPER_BOUND_SYMBOL_COLUMN_INDEX) {
			boolean lower = false;
			String aux = (String) valuesTable.getValueAt(fila, column);
			RevelationIntervalEdit relatedIntervalEdit = null;
			if (column == LOWER_BOUND_SYMBOL_COLUMN_INDEX) {
				lower = true;
				if (aux.equals("(")) {
					valuesTable.setValueAt("[", fila, column);

					if (fila > 0) {
						Double lowerLimit = (Double) valuesTable.getValueAt(
								fila, LOWER_BOUND_VALUE_COLUMN_INDEX);
						Double upperLimit = (Double) valuesTable.getValueAt(
								fila - 1, UPPER_BOUND_VALUE_COLUMN_INDEX);
						if (lowerLimit.equals(upperLimit))
							valuesTable.setValueAt(")", fila - 1,
									UPPER_BOUND_SYMBOL_COLUMN_INDEX);
						relatedIntervalEdit = new RevelationIntervalEdit(link,
								StateAction.MODIFY_DELIMITER_INTERVAL, fila - 1,
								0, false);
					}
					// checkIntervalDiscretize("[", fila, columna,upMonotony);
				} else {
					valuesTable.setValueAt("(", fila, column);
					if (fila > 0) {
						Double lowerLimit = (Double) valuesTable.getValueAt(
								fila, LOWER_BOUND_VALUE_COLUMN_INDEX);
						Double upperLimit = (Double) valuesTable.getValueAt(
								fila - 1, UPPER_BOUND_VALUE_COLUMN_INDEX);
						if (lowerLimit.equals(upperLimit))
							valuesTable.setValueAt("]", fila - 1,
									UPPER_BOUND_SYMBOL_COLUMN_INDEX);
						relatedIntervalEdit = new RevelationIntervalEdit(link,
								StateAction.MODIFY_DELIMITER_INTERVAL, fila - 1,
								0, false);
					}
					// checkIntervalDiscretize("(", fila, columna,upMonotony);
				}
			}
			if (column == UPPER_BOUND_SYMBOL_COLUMN_INDEX) {
				if (aux.equals(")")) {
					valuesTable.setValueAt("]", fila, column);
					if (fila < valuesTable.getRowCount() - 1) {
						Double lowerLimit = (Double) valuesTable.getValueAt(
								fila, UPPER_BOUND_VALUE_COLUMN_INDEX);
						Double upperLimit = (Double) valuesTable.getValueAt(
								fila + 1, LOWER_BOUND_VALUE_COLUMN_INDEX);
						if (lowerLimit.equals(upperLimit))
							valuesTable.setValueAt("(", fila + 1,
									LOWER_BOUND_SYMBOL_COLUMN_INDEX);
						relatedIntervalEdit = new RevelationIntervalEdit(link,
								StateAction.MODIFY_DELIMITER_INTERVAL, fila + 1,
								0, true);
					}
					// checkIntervalDiscretize("]", fila, columna,upMonotony);
				} else {
					valuesTable.setValueAt(")", fila, column);
					if (fila < valuesTable.getRowCount() - 1) {
						Double lowerLimit = (Double) valuesTable.getValueAt(
								fila, UPPER_BOUND_VALUE_COLUMN_INDEX);
						Double upperLimit = (Double) valuesTable.getValueAt(
								fila + 1, LOWER_BOUND_VALUE_COLUMN_INDEX);
						if (lowerLimit.equals(upperLimit))
							valuesTable.setValueAt("[", fila + 1,
									LOWER_BOUND_SYMBOL_COLUMN_INDEX);
						relatedIntervalEdit = new RevelationIntervalEdit(link,
								StateAction.MODIFY_DELIMITER_INTERVAL, fila + 1,
								0, true);
					}
					// checkIntervalDiscretize(")", fila, columna,upMonotony);
				}
			}

			RevelationIntervalEdit intervalEdit = new RevelationIntervalEdit(
					link, StateAction.MODIFY_DELIMITER_INTERVAL, fila, 0, lower);
			try {
				probNode.getProbNet().doEdit(intervalEdit);
				if (relatedIntervalEdit != null) {
					probNode.getProbNet().doEdit(relatedIntervalEdit);
				}

			} catch (ConstraintViolationException | CanNotDoEditException
					| NonProjectablePotentialException
					| WrongCriterionException | DoEditException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this,
				                              stringDatabase.getString(e.getMessage()),
				                              stringDatabase.getString(e.getMessage()),
						JOptionPane.ERROR_MESSAGE);

			}
		} else if (column == LOWER_BOUND_VALUE_COLUMN_INDEX
				|| column == UPPER_BOUND_VALUE_COLUMN_INDEX) {
			double j = (Double) valuesTable.getValueAt(fila, column);
			System.out.println(j);
			System.out.println("DiscretizeTablePanel.changeIntervalDiscretize");
			System.out
					.println(">> check here the values of the interval with the other intervals");
		}
	}

}
