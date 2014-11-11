package org.openmarkov.core.gui.dialog.network;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.action.DecisionCriteriaEdit;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.StringWithProperties;

@SuppressWarnings("serial")
public class DecisionCriteriaTablePanel extends AdvancedPropertiesTablePanel {

	private ProbNet probNet;
	/**
	 * Each time an agent has been edited the corresponding edit would be stored
	 */
	private List<PNEdit> edits = new ArrayList<PNEdit>();

	public DecisionCriteriaTablePanel(String[] newColumns, ProbNet probNet) {
		super(newColumns, new Object[0][0], "a");
		this.probNet = probNet;

	}

	@Override
	public void tableChanged(TableModelEvent tableEvent) {
		int column = tableEvent.getColumn();
		int row = tableEvent.getLastRow();
		if (tableEvent.getType() == TableModelEvent.UPDATE) {
			String criteriaName = (String) dataTable[row][0];
			String newName = (String) ((AdvancedPropertiesTableModel) tableEvent.getSource())
					.getValueAt(row, column);
			dataTable[row][0] = newName;
			if (criteriaName != newName) {
				DecisionCriteriaEdit criteriaEdit = new DecisionCriteriaEdit(probNet,
						StateAction.RENAME, newName, criteriaName, dataTable);
				try {
					probNet.doEdit(criteriaEdit);
					edits.add(criteriaEdit);
				} catch (DoEditException | ConstraintViolationException | CanNotDoEditException
						| NonProjectablePotentialException | WrongCriterionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				setData(dataTable);
				valuesTable.getSelectionModel().setSelectionInterval(row, row);
			}
		}
	}

	@Override
	protected void actionPerformedAddValue() {

		String option = JOptionPane.showInputDialog(this,
				"Proporcione el nuevo criterio de decision", "Agregar criterio de decision",
				JOptionPane.QUESTION_MESSAGE);

		if (option != null) {
			int newIndex = 0;
			newIndex = valuesTable.getRowCount();

			DecisionCriteriaEdit criteriaEdit = new DecisionCriteriaEdit(probNet, StateAction.ADD,
					"", option, null);

			// doEdit
			try {
				probNet.doEdit(criteriaEdit);
				edits.add(criteriaEdit);

			} catch (DoEditException | ConstraintViolationException | CanNotDoEditException
					| NonProjectablePotentialException | WrongCriterionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			 * getTableModel().insertRow(newIndex, new Object[]
			 * {getKeyString(newIndex), option });
			 * valuesTable.getSelectionModel().setSelectionInterval(newIndex,
			 * newIndex);
			 */

			// StringsWithProperties agents = probNet.getAgents();
			// setDataFromNetworkAgents(agents);
			List<StringWithProperties> criterias = probNet.getDecisionCriteria();
			setDataFromAdvancedProperties(criterias);
			// getTableModel().insertRow(newIndex, new Object[]
			// {getKeyString(newIndex), option });
			valuesTable.getSelectionModel().setSelectionInterval(newIndex, newIndex);

			dataTable = new Object[valuesTable.getRowCount()][1];
			for (int i = 0; i < valuesTable.getRowCount(); i++) {
				dataTable[i][0] = valuesTable.getValueAt(i, 1);
			}
			/*
			 * getTableModel().insertRow(newIndex, new Object[]
			 * {getKeyString(newIndex), option });
			 * //valuesTable.getSelectionModel().setSelectionInterval(newIndex,
			 * newIndex); valuesTable.setValueAt(option, newIndex, 1);
			 */
		}
	}

	@Override
	protected void actionPerformedRemoveValue() {
		int selectedRow = valuesTable.getSelectedRow();
		String criteriaName = (String) valuesTable.getValueAt(selectedRow, 1);

		DecisionCriteriaEdit criteriaEdit = new DecisionCriteriaEdit(probNet, StateAction.REMOVE,
				"", criteriaName, null);

		try {
			probNet.doEdit(criteriaEdit);
			edits.add(criteriaEdit);
		} catch (DoEditException | ConstraintViolationException | CanNotDoEditException
				| NonProjectablePotentialException | WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// StringsWithProperties agents = probNet.getAgents();
		List<StringWithProperties> criterias = probNet.getDecisionCriteria();
		setDataFromAdvancedProperties(criterias);
		valuesTable.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
		// dataTable = new Object [agents.getNames().size()][1];
		if (criterias != null) {
			dataTable = new Object[criterias.size()][1];
			for (int i = 0; i < valuesTable.getRowCount(); i++) {
				dataTable[i][0] = valuesTable.getValueAt(i, 1);
			}
		}
	}

	@Override
	protected void actionPerformedUpValue() {
		int selectedRow = valuesTable.getSelectedRow();
		Object swap = null;
		swap = dataTable[selectedRow][0];
		dataTable[selectedRow][0] = dataTable[selectedRow - 1][0];
		dataTable[selectedRow - 1][0] = swap;

		DecisionCriteriaEdit criteriaEdit = new DecisionCriteriaEdit(probNet, StateAction.UP, "",
				"", dataTable);

		try {
			probNet.doEdit(criteriaEdit);
			edits.add(criteriaEdit);
			setData(dataTable);
			/*
			 * swap = valuesTable.getValueAt(selectedRow, 1);
			 * valuesTable.setValueAt( valuesTable.getValueAt(selectedRow - 1,
			 * 1), selectedRow, 1); valuesTable.setValueAt(swap, selectedRow -
			 * 1, 1);
			 */
			valuesTable.getSelectionModel().setSelectionInterval(selectedRow - 1, selectedRow - 1);
		} catch (DoEditException | ConstraintViolationException | CanNotDoEditException
				| NonProjectablePotentialException | WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < valuesTable.getRowCount(); i++) {
			dataTable[i][0] = valuesTable.getValueAt(i, 1);
		}

	}

	@Override
	protected void actionPerformedDownValue() {
		int selectedRow = valuesTable.getSelectedRow();
		Object swap = null;
		swap = dataTable[selectedRow][0];
		dataTable[selectedRow][0] = dataTable[selectedRow + 1][0];
		dataTable[selectedRow + 1][0] = swap;

		DecisionCriteriaEdit criteriaEdit = new DecisionCriteriaEdit(probNet, StateAction.DOWN, "",
				"", dataTable);
		try {
			probNet.doEdit(criteriaEdit);
			edits.add(criteriaEdit);
			setData(dataTable);
			/*
			 * swap = valuesTable.getValueAt(selectedRow, 1);
			 * valuesTable.setValueAt( valuesTable.getValueAt(selectedRow + 1,
			 * 1), selectedRow, 1); valuesTable.setValueAt(swap, selectedRow +
			 * 1, 1);
			 */
			valuesTable.getSelectionModel().setSelectionInterval(selectedRow + 1, selectedRow + 1);
		} catch (DoEditException | ConstraintViolationException | CanNotDoEditException
				| NonProjectablePotentialException | WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < valuesTable.getRowCount(); i++) {
			dataTable[i][0] = valuesTable.getValueAt(i, 1);
		}
	}

}
