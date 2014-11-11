/*
 * Copyright 2012 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.gui.costeffectiveness;

import java.awt.Color;
import java.awt.Component;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * Table to show frontiers interventions and ICER
 * 
 * @author myebra
 */
@SuppressWarnings("serial")
public class FrontierInterventionsPanel extends JScrollPane {
	private List<Intervention> frontierInterventions;

	public FrontierInterventionsPanel(
			CostEffectivenessAnalysis costEffectivenessAnalysis) {
		super();
		this.frontierInterventions = costEffectivenessAnalysis
				.getFrontierInterventions();
		NonEditableModel model = new NonEditableModel();
		JTable table = new JTable(model);
		model.setColumnCount(4);
		model.setNumRows(frontierInterventions.size());
		model.setRowCount(frontierInterventions.size());
		table.getColumnModel().getColumn(0).setHeaderValue("Strategy");
		table.getColumnModel().getColumn(1).setHeaderValue("Effectiveness");
		table.getColumnModel().getColumn(2).setHeaderValue("Cost");
		table.getColumnModel().getColumn(3).setHeaderValue("ICER");
		for (int i = 0; i < frontierInterventions.size(); i++) {
			model.setValueAt(frontierInterventions.get(i).getName(), i, 0);
			model.setValueAt(frontierInterventions.get(i).getEffectiveness(),
					i, 1);
			model.setValueAt(frontierInterventions.get(i).getCost(), i, 2);
			if (i != 0) {
				model.setValueAt(frontierInterventions.get(i).getICER(), i, 3);
			}
		}
		DefaultTableCellRenderer tcr = new CEResultsCellRenderer();
		for (int i = 0; i < model.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(tcr);
		}
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setForeground(Color.blue);
		// table.setBackground(Color.pink);
		setViewportView(table);
		setAutoscrolls(true);
		fitColumnsWidthToContent(table);
	}
	
	  /**
     * Adjusts columns width to its content
     * 
     * @param table
     */
    public void fitColumnsWidthToContent(JTable table) {
        JTableHeader header = table.getTableHeader();

        TableCellRenderer headerRenderer = null;

        if (header != null)
        {
            headerRenderer = header.getDefaultRenderer();
        }

        TableColumnModel columns = table.getColumnModel();
        TableModel tableModel = table.getModel();
        int margin = columns.getColumnMargin();
        int rowCount = tableModel.getRowCount();
        int columnCount = tableModel.getColumnCount();

        for (int columnIndex = 0; columnIndex < columnCount; ++columnIndex) {
            TableColumn column = columns.getColumn(columnIndex);
            column.setMinWidth(60);
            int width = -1;

            TableCellRenderer tableCellRenderer = column.getHeaderRenderer();

            if (tableCellRenderer == null)
            {
                tableCellRenderer = headerRenderer;
            }

            if (tableCellRenderer != null) {
                Component component = tableCellRenderer.getTableCellRendererComponent(table,
                        column.getHeaderValue(),
                        false,
                        false,
                        -1,
                        columnIndex);

                width = component.getPreferredSize().width;
            } 

            for (int rowIndex = 0; rowIndex < rowCount; ++rowIndex) {
                TableCellRenderer cellRenderer = table.getCellRenderer(rowIndex, columnIndex);

                Component c = cellRenderer.getTableCellRendererComponent(table,
                        tableModel.getValueAt(rowIndex, columnIndex),
                        false,
                        false,
                        rowIndex,
                        columnIndex);

                width = Math.max(width, c.getPreferredSize().width);
            }

            if (width >= 0)
            {
                column.setMinWidth(width + margin);
            }
        }
    }    	

	public class NonEditableModel extends DefaultTableModel {
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}
}
