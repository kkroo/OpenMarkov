package org.openmarkov.core.gui.costeffectiveness;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;

/**
 * Table to show temporal evolution of temporal variables
 * 
 * @author myebra
 */
@SuppressWarnings("serial")
public class TemporalEvolutionTablePane extends JScrollPane {
    public TemporalEvolutionTablePane(Map<Variable, TablePotential> temporalEvolution,
            ProbNet expandedNetwork, Variable variableOfInterest, int numSlices, boolean isUtility,
            boolean isCumulative) {
        super();
        
        Map<Integer, double[]> temporalEvolutionValues = new LinkedHashMap<>();
        Set<Variable> variables = temporalEvolution.keySet();
        int timeSlice = 0;
        for (int i = 0; i < variables.size(); i++) {
            boolean found = false;
            while (!found) {
                for (Variable variable : variables) {
                    if (variable.getTimeSlice() == timeSlice) {
                        found = true;
                        temporalEvolutionValues.put(timeSlice, temporalEvolution.get(variable)
                                .getValues());
                    }
                }
                timeSlice++;
            }
        }        
        int numColumns = timeSlice + 1;
        
        NonEditableModel model = new NonEditableModel();
        JTable table = new JTable(model);
        
        model.setColumnCount(numColumns);
        model.setNumRows(variableOfInterest.getNumStates());
        model.setRowCount(variableOfInterest.getNumStates());
        final Object[][] info = new Object[variableOfInterest.getNumStates()][numColumns];
        // first column
        for (int i = 0; i < variableOfInterest.getNumStates(); i++) {
            if (isUtility) {
                info[i][0] = variableOfInterest.getBaseName();
                model.setValueAt(variableOfInterest.getBaseName(), i, 0);
            } else {
                info[i][0] = variableOfInterest.getStateName(i);
                model.setValueAt(variableOfInterest.getStateName(i), i, 0);
            }
        }
        final String[] columnNames = new String[numColumns];
        columnNames[0] = " ";
        table.getColumnModel().getColumn(0).setHeaderValue("");
        TableCellRenderer cellRenderer = new CEResultsCellRenderer();

        Double value = 0.0;
        for (int cycle = 0; cycle < timeSlice; ++cycle) { // column
            int columnIndex = cycle+1;
            table.getColumnModel().getColumn(columnIndex).setHeaderValue(cycle);
            table.getColumnModel().getColumn(columnIndex).setCellRenderer(cellRenderer);
            for (int i = 0; i < variableOfInterest.getNumStates(); i++) {// row
                if (isUtility && isCumulative) {
                    if(temporalEvolutionValues.containsKey(cycle))
                    {
                        value += temporalEvolutionValues.get(cycle)[i];
                    }
                    // cell(row, column) = cell(i+1, j+1)
                    info[i][columnIndex] = value;
                    model.setValueAt(value, i, columnIndex);
                } else {
                    if(temporalEvolutionValues.containsKey(cycle))
                    {
                        value = temporalEvolutionValues.get(cycle)[i];
                    }else
                    {
                        value = 0.0;
                    }
                    // cell(row, column) = cell(i+1, j+1)
                    info[i][columnIndex] = value;
                    model.setValueAt(value, i, columnIndex);
                }
            }
        }
        // table.setTableHeader(null);
        // table.getTableHeader().setVisible(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setDefaultRenderer(Double.class, new CEResultsCellRenderer());
        setViewportView(table);
        setAutoscrolls(true);
    }

    public class NonEditableModel extends DefaultTableModel {
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
