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

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.gui.window.edition.EditorPanel;
import org.openmarkov.core.model.network.ProbNet;

@EditionState(name="Edit.Mode.Link", icon="link.gif", cursor="link.gif")
public class LinkEditionMode extends EditionMode
{

    public LinkEditionMode (EditorPanel editorPanel, ProbNet probNet)
    {
        super (editorPanel, probNet);
    }

    @Override
    public void mousePressed (MouseEvent e, Point2D.Double cursorPosition, Graphics2D g)
    {
        
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
            if (Utilities.noMouseModifiers(e)) {
                visualNetwork.startLinkCreation(cursorPosition, g);
            }
        }
    }

    @Override
    public void mouseReleased (MouseEvent e, Point2D.Double position, Graphics2D g)
    {
        if (SwingUtilities.isLeftMouseButton (e))
        {
            PNEdit linkEdit = visualNetwork.finishLinkCreation (position, g);
            if (linkEdit != null)
            {
                try
                {
                    probNet.doEdit (linkEdit);
                }
                catch (Exception ex)
                {
                    JOptionPane.showMessageDialog (Utilities.getOwner (editorPanel),
                                                   ex.getMessage (),
                                                   "Error while creating link",
                                                   JOptionPane.ERROR_MESSAGE);
                }
            }
            editorPanel.repaint ();
        }
    }

    @Override
    public void mouseDragged (MouseEvent e, Point2D.Double cursorPosition, double diffX, double diffY, Graphics2D g)
    {
        if (SwingUtilities.isLeftMouseButton (e))
        {
            visualNetwork.updateLinkCreation (cursorPosition);
            editorPanel.repaint ();
        }
        
    }
}
