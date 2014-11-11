package org.openmarkov.core.gui.dialog.network;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.dialog.common.KeyTablePanel;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.StringWithProperties;

@SuppressWarnings("serial")
public class AdvancedPropertiesTablePanel extends KeyTablePanel implements TableModelListener,PNUndoableEditListener{

	private String keyPrefix;
	private AdvancedPropertiesTableModel advancedPropertiestableModel;
	protected Object dataTable [][];
	/**
	 * Each time an agent has been edited the corresponding edit would be stored 
	 */
	private List<PNEdit> edits = new ArrayList<PNEdit>();

	public AdvancedPropertiesTablePanel(String[] newColumns, ProbNet probNet){
		this(newColumns, new Object[0][0], "a");
	}
	
	public AdvancedPropertiesTablePanel(String[] newColumns, Object[][] noKeyData,
			String newKeyPrefix) {
		super(newColumns, new Object[0][0], true, true);
			initialize();
			setData(noKeyData);
			defineTableLookAndFeel();			// define specific listeners
			//defineTableSpecificListeners();
			//getTableModel().addTableModelListener(this);
	}
	/**
	 * Sets a new table model with new data.
	 * 
	 * @param newData
	 *            new data for the table without the key column.
	 */
	@Override
	public void setData(Object[][] newData) {

		if (newData != null) {
			//dataTable = newData;
			data = fillDataKeys(newData);
			//tableModel = new DefaultTableModel(data, columns);
			advancedPropertiestableModel = new AdvancedPropertiesTableModel (data, columns); 
			//valuesTable.setModel(tableModel);
			valuesTable.setModel(advancedPropertiestableModel);
			valuesTable.getModel().addTableModelListener(this);
			this.defineTableLookAndFeel();
		}
	}
	
	public void setDataTable (Object [][] dataTable){
		this.dataTable = dataTable;
	}
	protected void defineTableLookAndFeel() {

		// center the data in all columns
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
		tcr.setHorizontalAlignment(SwingConstants.LEFT);
		
		DefaultTableCellRenderer statesRender = new DefaultTableCellRenderer();
		statesRender.setHorizontalAlignment(SwingConstants.LEFT);
		
		int maxColumn = valuesTable.getColumnModel().getColumnCount();
		
		for (int i = 1; i < maxColumn; i++) {
			TableColumn aColumn = valuesTable.getColumnModel().getColumn(i);
			aColumn.setCellRenderer(tcr);
			valuesTable.getTableHeader().getColumnModel().getColumn(i)
							.setCellRenderer(tcr);
		}
	}
	/**
	 * This method takes a data object and creates a new column that content a
	 * row key. This key begins with the key prefix following a number that
	 * starts at 0.
	 * 
	 * @param oldData
	 *            data to add a key column.
	 * @return a data object with one more column that contains the keys.
	 */
	protected Object[][] fillDataKeys(Object[][] oldData) {

		Object[][] newData = null;
		int i1 = 0; // aux int
		int i2 = 0; // aux int
		int l1 = 0; // num of rows
		int l2 = 0; // num of columns

		l1 = oldData.length;
		if (l1 > 0) {
			l2 = oldData[0].length + 1;
			newData = new Object[l1][l2];
			for (i1 = 0; i1 < l1; i1++) {
				newData[i1][0] = getKeyString(i1);
				for (i2 = 1; i2 < l2; i2++) {
					newData[i1][i2] = oldData[i1][i2 - 1];
				}
			}
			return newData;
		}
		return new Object[0][0];
	}
	/**
	 * Returns a key represented by an index.
	 * 
	 * @param index
	 *            index of the key which will be returned
	 * @return the string that content the key.
	 */
	protected String getKeyString(int index) {

		return keyPrefix + index;

	}
		
	protected void setDataFromAdvancedProperties( List<StringWithProperties> advancedProperties) {
		 if (advancedProperties != null) {
			 Object [][] tableData =new Object [advancedProperties.size()][1];
				
			  for (int i = 0; i < advancedProperties.size(); i++) {
				  tableData[i][0] = advancedProperties.get(i).getString();
			  }
				setData(tableData);
		 } else if (advancedProperties == null) {
			 Object [][] tableData =new Object [0][0];
			 setData(tableData);
		 }
	 }
	
	@Override
	public void undoableEditHappened(UndoableEditEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void undoableEditWillHappen(UndoableEditEvent event)
			throws ConstraintViolationException, CanNotDoEditException,
			NonProjectablePotentialException,
			WrongCriterionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void undoEditHappened(UndoableEditEvent event) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 
	 * @return
	 */
	public List<PNEdit> getEdits () {
		return edits;
	}

	@Override
	public void tableChanged(TableModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	

}
