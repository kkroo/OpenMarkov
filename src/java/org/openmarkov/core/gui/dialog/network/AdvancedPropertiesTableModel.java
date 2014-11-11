package org.openmarkov.core.gui.dialog.network;

import javax.swing.table.DefaultTableModel;
/**
 * 
 * @author myebra
 *
 */
@SuppressWarnings("serial")
public class AdvancedPropertiesTableModel extends DefaultTableModel {
	
	private static final int COLUMN_AGENT_NAME = 1;
	//private static final int COLUMN_AGENT_ADITIONAL_PROPERTIES = 2;
	
	/**
	 * constructor for the model
	 * @param data - values to set in the table
	 * @param columns - name of the colums of the table
	 */
	public AdvancedPropertiesTableModel(Object[][] data, String[] columns) {
		super (data, columns);
		
	}
	
	/**
	 * retrieve the type of the object in a particular column of the table
	 * 
	 * @param columnId
	 *            position of the column in the table
	 * @return class of the objects in the column
	 */
	@Override
	public Class<?> getColumnClass(int columnId) {
		Class<?> value = String.class;
		switch (columnId) {
		case COLUMN_AGENT_NAME:
			value = String.class;
			break;
		/*case COLUMN_AGENT_ADITIONAL_PROPERTIES:
			value = String.class;
			break;*/
		default:
			//value = String.class;
		    break;
		}
		return value;
	}

}
