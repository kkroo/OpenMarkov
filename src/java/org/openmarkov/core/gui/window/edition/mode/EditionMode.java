/*
* Copyright 2012 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package org.openmarkov.core.gui.window.edition.mode;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.openmarkov.core.gui.graphic.VisualNetwork;
import org.openmarkov.core.gui.window.edition.EditorPanel;
import org.openmarkov.core.model.network.ProbNet;

/**
 * This class the defines the behaviour of the editor panel in a certain edition
 * state such as selection, node creation, link creation, etc.
 * @author ibermejo
 */
public abstract class EditionMode
{

    protected EditorPanel editorPanel;
    protected VisualNetwork visualNetwork;
    protected ProbNet probNet;
    
    
    public EditionMode(EditorPanel editorPanel, ProbNet probNet)
    {
        this.editorPanel = editorPanel;
        this.visualNetwork = editorPanel.getVisualNetwork ();
        this.probNet = probNet;
    }

    public abstract void mousePressed(MouseEvent e, Point2D.Double position, Graphics2D g);
    public abstract void mouseReleased(MouseEvent e, Point2D.Double position, Graphics2D g);
    public abstract void mouseDragged(MouseEvent e, Point2D.Double position, double diffX, double diffY, Graphics2D g);
}
