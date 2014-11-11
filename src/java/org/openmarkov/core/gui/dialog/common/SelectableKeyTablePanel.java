/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.common;

import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.openmarkov.core.action.RevelationStateEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;

/**
 * This class implements a key table with a table model which renders the cells
 * according to the class type.
 ***/
public class SelectableKeyTablePanel extends PrefixedKeyTablePanel implements
		TableModelListener {
	/***
	 * Link containing the revelation conditions
	 */
	private Link link;
	/****
	 * Node whose values are revealing
	 */
	private ProbNode node;
	/***
	 * Preferred column width
	 */
	private static final int CHECKBOX_COLUMN_WIDTH = 60;
	private static final int STATENAME_COLUMN_WIDTH = 440;

	public SelectableKeyTablePanel(String[] newColumns, Object[][] noKeyData,
			String newKeyPrefix, boolean firstColumnHidden, Link link) {
		super(newColumns, new Object[0][0], newKeyPrefix, true);
		this.link = link;
		this.node = (ProbNode) link.getNode1().getObject();
		super.getAddValueButton().setVisible(false);
		super.getRemoveValueButton().setVisible(false);
		super.getDownValueButton().setVisible(false);
		super.getUpValueButton().setVisible(false);

	}

	/****
	 * Adjusts the column width. The checkbox column is thinner than the node's state column
	 */
	public void adjustColumnSize() {

		getValuesTable().getColumnModel().getColumn(0)
				.setPreferredWidth(CHECKBOX_COLUMN_WIDTH);
		getValuesTable().getColumnModel().getColumn(1)
				.setMaxWidth(CHECKBOX_COLUMN_WIDTH);
		getValuesTable().getColumnModel().getColumn(1)
				.setPreferredWidth(STATENAME_COLUMN_WIDTH);

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method initializes tableModel.
	 * 
	 * @return a new tableModel.
	 */
	protected DefaultTableModel getTableModel() {

		if (tableModel == null) {

			tableModel = new SelectableTableModel(data, columns);
		}
		return tableModel;
	}

	class SelectableTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 4478294244055128574L;

		public SelectableTableModel(Object[][] data, String[] columns) {
			super(data, columns);

		}

		public boolean isCellEditable(int row, int col) {
			if (col == 1) {
				return true;
			} else
				return false;
		}

		public Class<?> getColumnClass(int c) {
			if (getRowCount() > 0) {
				return getValueAt(0, c).getClass();
			} else
				return Object.class;
		}

	}

	/**
	 * Invoked when the row selection changes.
	 * 
	 * @param e
	 *            selection event information.
	 */
	public void tableChanged(TableModelEvent e) {
		int row = e.getFirstRow();
		int column = e.getColumn();
		TableModel model = (TableModel) e.getSource();
		Object data = model.getValueAt(row, column);
		State[] states = node.getVariable().getStates();
		if (states.length > 0) {
			State selectedState = states[states.length - row - 1];
			RevelationStateEdit arcEdit = new RevelationStateEdit(link,
					selectedState, Boolean.valueOf((Boolean) data));
			try {
				node.getProbNet().doEdit(arcEdit);
			} catch (ConstraintViolationException e1) {
			} catch (CanNotDoEditException e3) {
				e3.printStackTrace();
				JOptionPane.showMessageDialog(this,
				                              stringDatabase.getString(e3.getMessage()),
				                              stringDatabase.getString(e3.getMessage()),
						JOptionPane.ERROR_MESSAGE);
			} catch (NonProjectablePotentialException e4) {
				e4.printStackTrace();
				JOptionPane.showMessageDialog(this,
				                              stringDatabase.getString(e4.getMessage()),
				                              stringDatabase.getString(e4.getMessage()),
						JOptionPane.ERROR_MESSAGE);
			} catch (WrongCriterionException e5) {
				e5.printStackTrace();
				JOptionPane.showMessageDialog(this,
				                              stringDatabase.getString(e5.getMessage()),
				                              stringDatabase.getString(e5.getMessage()),
						JOptionPane.ERROR_MESSAGE);
			} catch (DoEditException e6) {
				e6.printStackTrace();
				JOptionPane.showMessageDialog(this,
				                              stringDatabase.getString(e6.getMessage()),
				                              stringDatabase.getString(e6.getMessage()),
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
