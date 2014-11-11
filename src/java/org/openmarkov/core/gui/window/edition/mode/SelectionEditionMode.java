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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.openmarkov.core.gui.action.MoveNodeEdit;
import org.openmarkov.core.gui.graphic.VisualElement;
import org.openmarkov.core.gui.graphic.VisualNode;
import org.openmarkov.core.gui.window.edition.EditorPanel;
import org.openmarkov.core.model.network.ProbNet;

@EditionState(name="Edit.Mode.Selection", icon="selection.gif")
public class SelectionEditionMode extends EditionMode
{
    /**
     * Indicates if a node has been moved.
     */
    private boolean nodeMoved = false;    

    /**
     * Information of the movement of the nodes.
     */
    private List<VisualNode> movedNodes = null;    
    /**
     * Current selection state.
     */
    private SelectionState selectionState = SelectionState.DEFAULT;
    
    public SelectionEditionMode  (EditorPanel editorPanel, ProbNet probNet)
    {
        super (editorPanel, probNet);
        movedNodes = new ArrayList<> ();
    }

    @Override
    public void mousePressed (MouseEvent e, Point2D.Double position, Graphics2D g)
    {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (e.isControlDown() || e.isShiftDown()) {
                visualNetwork.addToSelection(position, g);
            } else {
                VisualElement selectedElement =  visualNetwork.selectElementInPosition(position, g);
                setSelectionState((selectedElement != null)? SelectionState.MOVING : SelectionState.SELECTING);
                if(selectedElement == null)
                {
                    visualNetwork.startSelectionRectangle(position);
                }
            }
        }        
    }

    @Override
    public void mouseReleased (MouseEvent e, Point2D.Double position, Graphics2D g)
    {
        if (selectionState == SelectionState.MOVING) {
            if (nodeMoved) {
                movedNodes = visualNetwork.fillVisualNodesSelected();

                if(movedNodes.size() > 0)
                {
                    MoveNodeEdit moveNodeEdit = new MoveNodeEdit(movedNodes);

                    try {
                        probNet.getPNESupport().doEdit(moveNodeEdit);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(editorPanel,
                                e1.getMessage(),
                                "Error moving nodes",
                                JOptionPane.ERROR_MESSAGE);
                    } 
                }
                nodeMoved = false;
                editorPanel.adjustPanelDimension();
            }
        } else if (selectionState == SelectionState.SELECTING) {
            visualNetwork.finishSelectionRectangle(position);
        }
        setSelectionState(SelectionState.DEFAULT);
        editorPanel.repaint();        
    }

    @Override
    public void mouseDragged (MouseEvent e, Point2D.Double position, double diffX, double diffY, Graphics2D g)
    {

        if (SwingUtilities.isLeftMouseButton(e)) {
            if (selectionState == SelectionState.MOVING) {
                visualNetwork.moveSelectedElements(diffX, diffY);
                nodeMoved = true;
            } else if (selectionState == SelectionState.SELECTING) {
                visualNetwork.updateSelectionRectangle(diffX, diffY);
            }
            editorPanel.repaint();
        }        
    }
    
    /**
     * Changes the state of the selection and carries out the necessary actions
     * in each case.
     * 
     * @param newState
     *            new mouse state.
     */
    private void setSelectionState(SelectionState newState) {

        editorPanel.setCursor(newState.getCursor());
        selectionState = newState;

    }
    
}
