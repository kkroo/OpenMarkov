/*
 * Copyright 2012 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.window.dt;

import java.awt.Color;

import javax.swing.JScrollPane;

import org.openmarkov.core.dt.DecisionTreeBuilder;
import org.openmarkov.core.dt.DecisionTreeElement;
import org.openmarkov.core.model.network.ProbNet;

@SuppressWarnings("serial")
public class DecisionTreePanel extends JScrollPane
{
    protected DecisionTree jTree;    
      
    public DecisionTreePanel(ProbNet probNet)
    {
        DecisionTreeElement root = DecisionTreeBuilder.buildDecisionTree (probNet); 
        DecisionTreeModel model = new DecisionTreeModel (root);
        jTree = new DecisionTree (model);
        for (int i = 0; i < jTree.getRowCount (); i++)
        {
            jTree.expandRow (i);
        }        
        setViewportView (jTree);
        setBackground (Color.white);
    }

    /**
     * Returns the zoom.
     * @return the zoom.
     */
    protected double getZoom ()
    {
        return jTree.getZoom ();
    }

    /**
     * Sets the zoom.
     * @param zoom the zoom to set.
     */
    protected void setZoom (Double zoom)
    {
        jTree.setZoom (zoom);
        repaint();
    }    
}
