/*
 * Copyright 2013 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.costeffectiveness;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.openmarkov.core.gui.dialog.common.CPTablePanel;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;

@SuppressWarnings("serial")
public class CostEffectivenessAnalysisPane extends JScrollPane
{
    public CostEffectivenessAnalysisPane (TablePotential globalUtility)
    {
        // create a dummy ProbNet
        ProbNet dummyProbNet = new ProbNet ();
        // make sure first variable in globalUtility is decisionCriteria one
        List<Variable> correctOrder = new ArrayList<> (globalUtility.getVariables ());
        for (int i = 0; i < correctOrder.size (); i++)
        {
            if (correctOrder.get (i).getName ().equalsIgnoreCase ("Decision Criteria"))
            {
                Variable decisionCriteriaVariable = correctOrder.remove (i);
                correctOrder.add (0, decisionCriteriaVariable);
            }
        }
        globalUtility = DiscretePotentialOperations.reorder (globalUtility, correctOrder);
        ProbNode dummyNode = new ProbNode (dummyProbNet, globalUtility.getVariables ().get (0),
                                       NodeType.CHANCE);
        for (int i = 1; i < globalUtility.getVariables ().size (); i++)
        {
            ProbNode newProbNode = new ProbNode (dummyProbNet, globalUtility.getVariables ().get (i), NodeType.CHANCE);
            dummyProbNet.addLink (newProbNode, dummyNode, true);
        }
        List<Potential> potentials = new ArrayList<> ();
        TablePotential potentialCopy = new TablePotential (globalUtility.getVariables (),
                                                 PotentialRole.CONDITIONAL_PROBABILITY);
        potentialCopy.setValues (globalUtility.getValues ());
        potentials.add (potentialCopy);
        dummyNode.setPotentials (potentials);
        
        CPTablePanel cpTablePanel = new CPTablePanel (dummyNode);
        JTable table = cpTablePanel.getValuesTable ();

        // Adjust column sizes
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for(int column = 0; column < table.getColumnCount (); ++column)
        {
            int width = 0;
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max (comp.getPreferredSize().width, width);
            }
            table.getColumnModel().getColumn(column).setMinWidth (width + 10);
        }

        setViewportView (cpTablePanel);
    }
}
