/*
 * Copyright 2013 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.gui.dialog.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;

import org.openmarkov.core.model.network.potential.RegressionPotential;
import org.openmarkov.core.model.network.potential.WeibullHazardPotential;


@SuppressWarnings("serial")
public class RegressionPanel extends KeyTablePanel {

    private List<ActionListener> listeners; 
    private RegressionPotential potential = null;
    
    public RegressionPanel()
    {
        super(new String[]{"Covariate", "Coefficient"}, new Object[0][2], true, true, true);
        getValuesTable().setDefaultRenderer(String.class, new CoefficientTableCellRenderer());
        listeners = new ArrayList<>();
        valuesTable.addMouseListener(new CovariatesTableMouseListener());
        initialize();
    }
    
    /**
     * Invoked when the button 'add' is pressed.
     */
    @Override
    protected void actionPerformedAddValue() {
        ExpressionDialog expressionDialog = new ExpressionDialog(null, potential.getVariables());
        expressionDialog.setVisible(true);
        if (expressionDialog.getSelectedButton() == OkCancelHorizontalDialog.OK_BUTTON) {
            int selectedRow = valuesTable.getSelectedRow();
            int rowCount = valuesTable.getRowCount();
            tableModel.addRow(new Object[]{expressionDialog.getExpression(), 0.0});
            tableModel.moveRow(rowCount, rowCount, selectedRow+1);
            valuesTable.setRowSelectionInterval(selectedRow+1, selectedRow+1);            
        }        
        notifyActionListeners(new ActionEvent(this, 1, "Add"));
    };
    
    /**
     * Invoked when the button 'remove' is pressed.
     */
    protected void actionPerformedRemoveValue() {
        super.actionPerformedRemoveValue();
        notifyActionListeners(new ActionEvent(this, 2, "Remove"));
    };

    /**
     * Invoked when the button 'up' is pressed.
     */
    protected void actionPerformedUpValue() {
        super.actionPerformedUpValue();
        notifyActionListeners(new ActionEvent(this, 3, "Up"));
    };

    /**
     * Invoked when the button 'down' is pressed.
     */
    protected void actionPerformedDownValue() {
        super.actionPerformedDownValue();
        notifyActionListeners(new ActionEvent(this, 4, "Down"));
    };    

    public void addActionListener(ActionListener listener)
    {
        listeners.add(listener);
    }
    
    public boolean removeActionListener(ActionListener listener)
    {
        return listeners.remove(listener);
    }
    
    private void notifyActionListeners(ActionEvent event)
    {
        for(ActionListener listener : listeners)
        {
            listener.actionPerformed(event);
        }
    }
    
    public void setData(RegressionPotential potential) {
        this.potential = potential;
        double[] coefficients = potential.getCoefficients();
        String[] covariates = potential.getCovariates();
        Object[][] data = new Object[covariates.length][2]; 
        for (int i = 0; i < covariates.length; ++i) {
            data[i][0] = covariates[i];
            data[i][1] = coefficients[i];
        }
        setData(data);
    }    
    
    public double[] getCoefficients()
    {
        int rowCount = tableModel.getRowCount();
        double[] coefficients = new double[rowCount];
        for (int i = 0; i < rowCount; ++i) {
            coefficients[i] = Double.parseDouble(tableModel.getValueAt(i, 1).toString());
        }
        return coefficients;
    }
    
    public String[] getCovariates() {
        int rowCount = tableModel.getRowCount();
        String[] covariates = new String[rowCount];
        for (int i = 0; i < rowCount; ++i) {
            covariates[i] = tableModel.getValueAt(i, 0).toString();
        }
        return covariates;
    }    
    
    public void valueChanged(ListSelectionEvent e) {
        super.valueChanged(e);
        int row = valuesTable.getSelectedRow();
        String covariate = tableModel.getValueAt(row, 0).toString();
        boolean isMandatory = false;
        String[] mandatoryCovariates = (potential instanceof WeibullHazardPotential) ? WeibullHazardPotential.getMandatoryCovariates()
                : RegressionPotential.getMandatoryCovariates();
        for(String mandatoryCovariate : mandatoryCovariates)
        {
            isMandatory |= mandatoryCovariate.equals(covariate);
        }
        setEnabledRemoveValue(!isMandatory);
        setEnabledAddValue(!isMandatory);
    }
    
    private class CoefficientTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            Color backgroundColor = Color.WHITE;
            if (column == 0) {
                backgroundColor = new Color(207, 227, 253);
            }
            setBackground(backgroundColor);

            return super.getTableCellRendererComponent(table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column);
        }
    }
    
    private class CovariatesTableMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2 && valuesTable.getSelectedColumn() == 0) {
                int selectedRow = valuesTable.getSelectedRow();
                String covariate = tableModel.getValueAt(selectedRow, 0).toString();
                boolean isMandatory = false;
                String[] mandatoryCovariates = (potential instanceof WeibullHazardPotential) ? WeibullHazardPotential.getMandatoryCovariates()
                        : RegressionPotential.getMandatoryCovariates();
                for(String mandatoryCovariate : mandatoryCovariates)
                {
                    isMandatory |= mandatoryCovariate.equals(covariate);
                }
                if(!isMandatory)
                {
                    ExpressionDialog expressionDialog = new ExpressionDialog(null, potential.getVariables(), covariate);
                    expressionDialog.setVisible(true);
                    if (expressionDialog.getSelectedButton() == OkCancelHorizontalDialog.OK_BUTTON) {
                        tableModel.setValueAt(expressionDialog.getExpression(), selectedRow, 0);
                    }
                }
            }
        }
    }

}
