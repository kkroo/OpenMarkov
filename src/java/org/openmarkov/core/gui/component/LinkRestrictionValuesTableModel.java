package org.openmarkov.core.gui.component;

@SuppressWarnings("serial")
public class LinkRestrictionValuesTableModel extends ValuesTableModel {

	/**
	 * constructor
	 */
	public LinkRestrictionValuesTableModel(Object[][] data, String[] columns,
			int firstEditableRow) {
		super(data, columns, firstEditableRow);

	}

	/**
	 * This method determines the default renderer/editor for each cell. First
	 * column is a String class type and the others are integer type.
	 */
	public Class<?> getColumnClass(int c) {

		Integer integerExample = 0;
		String stringExample = "";

		if (c == 0) {
			return stringExample.getClass();
		} else {
			return integerExample.getClass();
		}
	}
	
	
	public boolean isCellEditable(int row, int col)
    { return false; }

}
