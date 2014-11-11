/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

/**
 * OpenMarkov - ColorTableCellEditor.java
 */
package org.openmarkov.core.gui.dialog.configuration;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;


/**
 * ColorTableCellEditor handles the edition for colors in the Preferences Editor
 * 
 * @author jlgozalo
 * @version 1.0 - 11 Mar 2010 - initial version
 *
 */

@SuppressWarnings("serial")
public class ColorTableCellEditor extends AbstractCellEditor 
								implements TableCellEditor{
    
	/**
	 * colorChooser for the panel
	 */
	private JColorChooser colorChooser;
	/**
	 * dialog to implement the Color Chooser
	 */
	private JDialog colorDialog;
	/**
	 * panel to include the dialog
	 */
	private JPanel panel;

	
	public ColorTableCellEditor () {
		panel = new JPanel();
		// prepare color dialog
		colorChooser = new JColorChooser();
		colorDialog = JColorChooser.createDialog( null, "OpenMarkov colors", false, colorChooser, 
			new ActionListener() { //OK button listener
		         public void actionPerformed(ActionEvent event) {
		        	 stopCellEditing();
		         }
		    },
		    new ActionListener() { //Cancel button listener
		    	public void actionPerformed(ActionEvent event) {
		    		cancelCellEditing();
		    	}
		    }
		);
		colorDialog.addWindowListener( new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				cancelCellEditing();
			}
		});
	}
	
	/** 
	 * This is where we get the current Color value. We store it in the dialog
	 * in case the user starts editing
	 * 
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		colorChooser.setColor( (Color)value);
		return panel;
	}
	/**
	 * @param anEvent event of the table
	 * @return true if the cell should selected
	 */
	public boolean shouldSelectedCell(EventObject anEvent) {
		//starts editing
		colorDialog.setVisible( true );
		//tell caller it is ok to select this cell
		return true;
	}
	
	public void cancelCellEditing() {
		//editing is canceled-hide dialog
		colorDialog.setVisible( false );
		super.cancelCellEditing();
	}
	
	public boolean stopCellEditing () {
		// editing is complete-hide dialog
		colorDialog.setVisible( false );
		super.stopCellEditing();
	    // tell caller if is ok to use color value
		return true;
	}
	
	public Object getCellEditorValue() {
		return colorChooser.getColor();
	}
	
}
