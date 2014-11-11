/*
 * Copyright 2012 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.component;

import java.awt.Color;

import javax.swing.JTable;

@SuppressWarnings("serial")
public class ValuesTableWithLinkRestrictionCellRenderer extends
		ValuesTableCellRenderer {

	private static Color INCOMPATIBILITY_COLOR = new Color(255, 122, 122);

	public ValuesTableWithLinkRestrictionCellRenderer(int firstEditableRow,
			boolean[] uncertaintyInColumns) {
		super(firstEditableRow, uncertaintyInColumns);
	}

	@Override
	protected void setCellColors(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		super.setCellColors(table, value, isSelected, hasFocus, row, column);
		if ((column >= ValuesTable.FIRST_EDITABLE_COLUMN)
				&& firstEditableRow >= 0 && (row >= firstEditableRow)) {
			if (!table.isCellEditable(row, column)) {
				setBackground(INCOMPATIBILITY_COLOR);
			}
		}
	}

}
