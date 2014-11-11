package org.openmarkov.core.gui.component;

import java.awt.Color;

import javax.swing.JTable;

import org.openmarkov.core.model.network.potential.TablePotential;

@SuppressWarnings("serial")
public class LinkRestrictionCellRenderer extends ValuesTableCellRenderer {

	private static Color INCOMPATIBILITY_COLOR = new Color(255, 88, 88);
	private static Color COMPATIBILITY_COLOR =new Color(174, 255, 174);
	private final String INCOMPATIBILITY_VALUE = "0";
	private final String COMPATIBILITY_VALUE = "1";

	public LinkRestrictionCellRenderer(int firstEditableRow,
			boolean[] uncertaintyInColumns, TablePotential potential) {
		super(firstEditableRow, uncertaintyInColumns);

	}

	@Override
	protected void setCellColors(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		super.setCellColors(table, value, isSelected, hasFocus, row, column);

		if ((column >= ValuesTable.FIRST_EDITABLE_COLUMN)
				&& firstEditableRow >= 0 && (row >= firstEditableRow)) {
			if (value.toString().equalsIgnoreCase(INCOMPATIBILITY_VALUE)) {
				setBackground(INCOMPATIBILITY_COLOR);
			}
			if (value.toString().equalsIgnoreCase(COMPATIBILITY_VALUE)) {
				setBackground(COMPATIBILITY_COLOR);
			}
		}
	}

}
