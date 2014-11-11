/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.action;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.gui.graphic.VisualNode;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.model.network.ProbNode;

/**
 * <code>MoveNodeEdi</code> is a simple edit that allows to modify the position
 * of a group of nodes
 * @version 1.0 21/12/10
 * @author Miguel Palacios
 */
public class MoveNodeEdit extends SimplePNEdit
{
    /**
	 * 
	 */
    private static final long    serialVersionUID = 7578733825996342882L;
    /**
     * The nodes last positions before the action
     */
    private List<Point2D.Double> lastPositions    = new ArrayList<Point2D.Double> ();
    /**
     * The new positions of the nodes to move
     */
    private List<Point2D.Double> newPositions     = new ArrayList<Point2D.Double> ();
    /**
     * The node's name to move
     */
    private List<String>         namesNode        = new ArrayList<String> ();

    /**
     * Creates a new <code>MoveNodeEdit</code> with the nodes, and new X, Y
     * coordinates.
     * @param movedNodes the nodes that will be edited, with their new
     *            positions.
     */
    public MoveNodeEdit (List<VisualNode> movedNodes)
    {
        super (movedNodes.get (0).getProbNode ().getProbNet ());
        for (VisualNode visualNode : movedNodes)
        {
            lastPositions.add ((Point2D.Double) visualNode.getPosition ().clone ());
            newPositions.add ((Point2D.Double) visualNode.getTemporalPosition ().clone ());
            namesNode.add (visualNode.getProbNode ().getName ());
        }
    }

    @Override
    public void doEdit ()
    {
        ProbNode probNode = null;
        int i = 0;
        for (String name : namesNode)
        {
            try
            {
                probNode = probNet.getProbNode (name);
                probNode.getNode ().setCoordinateX (newPositions.get (i).getX ());
                probNode.getNode ().setCoordinateY (newPositions.get (i).getY ());
            }
            catch (ProbNodeNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace ();
                JOptionPane.showMessageDialog (null,
                                               StringDatabase.getUniqueInstance ().getString (e.getMessage ()),
                                               StringDatabase.getUniqueInstance ().getString (e.getMessage ()),
                                               JOptionPane.ERROR_MESSAGE);
            }
            i++;
        }
    }

    public void undo ()
    {
        super.undo ();
        int i = 0;
        ProbNode probNode = null;
        for (String name : namesNode)
        {
            try
            {
                probNode = probNet.getProbNode (name);
                probNode.getNode ().setCoordinateX (lastPositions.get (i).getX ());
                probNode.getNode ().setCoordinateY (lastPositions.get (i).getY ());
            }
            catch (ProbNodeNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace ();
                JOptionPane.showMessageDialog (null,
                                               StringDatabase.getUniqueInstance ().getString (e.getMessage ()),
                                               StringDatabase.getUniqueInstance ().getString (e.getMessage ()),
                                               JOptionPane.ERROR_MESSAGE);
            }
            i++;
        }
    }
}
