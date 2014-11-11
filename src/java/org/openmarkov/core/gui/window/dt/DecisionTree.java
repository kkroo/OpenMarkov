/*
 * Copyright 2012 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.gui.window.dt;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

import org.openmarkov.core.gui.window.edition.Zoom;

@SuppressWarnings("serial")
public class DecisionTree extends JTree {

    /**
     * Object to convert coordinates of the screen to the panel and vice versa.
     */
    protected Zoom zoom;   	
	
	public DecisionTree(DecisionTreeModel model) {
		super(model);
		getSelectionModel ().setSelectionMode (TreeSelectionModel.SINGLE_TREE_SELECTION);
        // Allows JTree nodes to accept CR/LF codes
        setShowsRootHandles (true);
        setRowHeight (0);
        setCellRenderer (new DecisionTreeCellRenderer ());
        setUI(new DecisionTreeUI());
        zoom = new Zoom ();
	}


    /**
     * Overwrite 'paint' method to avoid to call it explicitly.
     * 
     * @param g
     *            the graphics context in which to paint.
     */
    @Override
    public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
	    g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	    
	    g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2D.scale(zoom.getZoom(), zoom.getZoom());
        super.paint(g2D);
    }  

    
    public double getZoom()
    {
    	return zoom.getZoom();
    }
    
    /**
     * Sets the zoom.
     * @param zoom the zoom to set.
     */
    protected void setZoom (Double zoom)
    {
        this.zoom.setZoom (zoom);
    }      
}
