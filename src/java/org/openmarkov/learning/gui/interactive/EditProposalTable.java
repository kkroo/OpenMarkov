/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EditionsTable.java
 *
 * Created on 17-abr-2011, 12:09:05
 */
package org.openmarkov.learning.gui.interactive;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.learning.core.util.LearningEditMotivation;
import org.openmarkov.learning.core.util.LearningEditProposal;

/**
 * 
 * @author Inigo
 */
@SuppressWarnings("serial")
public class EditProposalTable extends javax.swing.JTable {

	public static final boolean ASCENDING = false;
	public static final boolean DESCENDING = true;

	private boolean sortStatus = DESCENDING;
	private int sortColumn = 1;
	private List<EditionsTableRow> rows;
	private List<EditSelectionListener> listeners;	
	private EditionsTableModel tableModel;
	

	/** Creates new EditionsTable */
	public EditProposalTable() {
		super();
		listeners = new ArrayList<EditSelectionListener>();
		

		tableModel = new EditionsTableModel(new String[] { StringDatabase.getUniqueInstance ().getString ("Learning.Interactive.EditDescription"), 
		                                                   StringDatabase.getUniqueInstance ().getString ("Learning.Interactive.Motivation"),});

		this.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		MouseListener headerListener = new TableHeaderMouseListener();
		this.getTableHeader().addMouseListener(headerListener);
		MouseListener rowListener = new DoubleClickMouseListener();
		this.addMouseListener(rowListener);
		this.setModel(tableModel);
		this.setDefaultRenderer(Object.class, new EditionsTableRenderer());
		this.getColumnModel ().getColumn (0).setPreferredWidth (90);
        setAutoResizeMode (AUTO_RESIZE_LAST_COLUMN);
	}

	public void fill(List<LearningEditProposal> edits)
	{
		rows = new ArrayList<EditProposalTable.EditionsTableRow>();
		
		for (LearningEditProposal edit : edits) {
			rows.add(new EditionsTableRow(edit));
		}
		
		populateTable(rows);
		sortTable(sortStatus);
	}
	
	public void addEditSelectionListener(EditSelectionListener listener)
	{
		listeners.add(listener);
	}
	
	public void notifyListeners()
	{
		EditionEvent action = new EditionEvent(this, getSelectedEdit());
		for(EditSelectionListener listener : listeners)
			listener.editionPerformed(action);
	}
	
	public PNEdit getSelectedEdit()
	{
		
		return (getSelectedRow()>= 0)? rows.get(getSelectedRow()).edit : null;
	}
	
	public void removeSelectedRow()
	{
		this.tableModel.removeRow(getSelectedRow());
	}
	
	private void populateTable(List<EditionsTableRow> rows)
	{
		tableModel.setRowCount(0);
		
		for(EditionsTableRow row : rows)
			tableModel.addRow(row.toArray());
		
		getSelectionModel().setSelectionInterval(0, 0);
	}
	
	@SuppressWarnings("unchecked")
    private void sortTable(boolean sortMode) {
		
		Collections.sort(rows);
		populateTable(rows);
	}
	
	private class EditionsTableRenderer extends DefaultTableCellRenderer{

		public Component getTableCellRendererComponent(  
			JTable table, Object value, boolean isSelected, 
			boolean hasFocus, int row, int col)
		{
		     Component comp = super.getTableCellRendererComponent(
		                      table,  value, isSelected, hasFocus, row, col);
		
		     if(!rows.get(row).isAllowed)
		     {
		         comp.setForeground(Color.red);
		     }
             else if(isSelected) 
             {
                 comp.setForeground(Color.white);
             }
		     else 
		     {
		         comp.setForeground(null);
		     }
		     
		     if(value != null)
		     {
		         setToolTipText (value.toString ());
		     }
		     return( comp );
		}
	}
	
	@SuppressWarnings("rawtypes")
    private class EditionsTableRow implements Comparable{
		
		private PNEdit edit;
		private String description;
		private boolean isAllowed;
		private LearningEditMotivation motivation;
		public EditionsTableRow(LearningEditProposal editAndScore)
		{
			this.edit = editAndScore.getEdit();
		    this.description = this.edit.toString ();
			this.isAllowed = editAndScore.isAllowed();
			this.motivation = editAndScore.getMotivation ();
		}
		
		public Object[] toArray() {
			return new Object[] {this.description, this.motivation};
		}

		@SuppressWarnings("unused")
        public PNEdit getEdit() {
			return this.edit;
		}

		public int compareTo(Object o) {
			
			EditionsTableRow row = (EditionsTableRow) o;

            int comparison = 0;
            // Define null less than everything, except null.
            if (o == null) {
                comparison = -1;
            } else {
            	switch(sortColumn)
            	{
	            	case 0:
	                	comparison = description.compareTo(row.description);
	                	break;
	            	case 1:
	            	    comparison = (motivation!=null)? motivation.compareTo (row.motivation): -1;
	                	break;
            	}
            }
            if (comparison != 0) {
                return sortStatus == DESCENDING ? -comparison : comparison;
            }
            return 0;	
		}
	}

	private class EditionsTableModel extends DefaultTableModel {
		public EditionsTableModel(String[] header) {
			super(header, 0);
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}
	}

	private class TableHeaderMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			JTableHeader h = (JTableHeader) e.getSource();
			TableColumnModel columnModel = h.getColumnModel();
			int viewColumn = h.columnAtPoint(e.getPoint());
			int column = columnModel.getColumn(viewColumn).getModelIndex();
			if (column != -1) {
				// Cycle the sorting states through {ASCENDING, DESCENDING}
				sortStatus = !sortStatus;
				sortColumn =  column;
				sortTable(sortStatus);
			}
		}
	}

	private class DoubleClickMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			EditProposalTable table = (EditProposalTable)e.getSource();
			if(e.getClickCount()>1)
			{
				table.notifyListeners();
			}
		}
	}
	
	public class EditionEvent
	{
		private Object source;
		private PNEdit edition;
		
		public EditionEvent(Object source, PNEdit edition)
		{
			this.source = source;
			this.edition = edition;
		}
		
		public Object getSource()
		{ return this.source; }

		public PNEdit getEdition()
		{ return this.edition; }
	}
	
	public interface EditSelectionListener
	{
		public void editionPerformed(EditionEvent edition);
	}
}
