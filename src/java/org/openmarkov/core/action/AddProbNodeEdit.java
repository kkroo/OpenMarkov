/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.action;

import java.awt.geom.Point2D;

import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PolicyType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.operation.PotentialOperations;

/**
 * <code>AddProbNodeEdit</code> is a edit that allow add a node to
 * <code>ProbNet</code> object.
 * @version 1 21/12/10
 * @author mpalacios
 */
@SuppressWarnings("serial")
public class AddProbNodeEdit extends SimplePNEdit
{

	// Atribbutes
    /**
     * The new Variable object that match the new node.
     */
    protected Variable       variable;
    /**
     * The node type of the new node.
     */
    protected NodeType       nodeType;
    /**
     * The network where the new node will be inserted.
     */
    protected ProbNet        probNet;
    /**
     * Graphic position of the new node
     */
    protected Point2D.Double cursorPosition;
    /**
     * The new node
     */
    protected ProbNode       newNode;

    /**
     * Creates a new <code>AddProbNodeEdit</code> with the network where the new
     * new node will be added and basic information about it.
     * @param probNet the <code>ProbNet</code> where the new node will be added.
     * @param variable the variable contained in the new node
     * @param nodeType The new node type.
     * @param cursorposition the position (coordinates X,Y) of the node.
     */
    public AddProbNodeEdit (ProbNet probNet,
                            Variable variable,
                            NodeType nodeType,
                            Point2D.Double cursorPosition)
    {
        super (probNet);
        this.cursorPosition = (Point2D.Double) cursorPosition.clone ();
        this.probNet = probNet;
        this.nodeType = nodeType;
        this.variable = variable;
    }
    
    /**
     * Creates a new <code>AddProbNodeEdit</code> with the network where the new
     * new node will be added and basic information about it.
     * @param probNet the <code>ProbNet</code> where the new node will be added.
     * @param variable the variable contained in the new node
     * @param nodeType The new node type.
     */
    public AddProbNodeEdit (ProbNet probNet,
                            Variable variable,
                            NodeType nodeType)
    {
        this (probNet, variable, nodeType, new Point2D.Double());
    }    
    

    @Override
    public void doEdit ()
    {
        // Adds the new variable to network ( creates a probNode instance )
        newNode = probNet.addProbNode (variable, nodeType);
        // TODO revisar si es conveniente utilizar una constraint
        // Sets a uniformPotential for the new probNode
        // Decision node has no potential when is created
        if (nodeType != NodeType.DECISION)
        {
           newNode = probNet.addPotential (PotentialOperations.getUniformPotential (probNet, variable,
                                                                           nodeType));
        }
        else
        {
            newNode.setPolicyType (PolicyType.OPTIMAL);
        }
        // Sets the visual node position
        newNode.getNode ().setCoordinateX ((int) cursorPosition.getX ());
        newNode.getNode ().setCoordinateY ((int) cursorPosition.getY ());
    }

    public void undo ()
    {
        super.undo ();
        probNet.removeProbNode (newNode);
    }

    /** @return newNode the new <code>ProbNode</code> added */
    public ProbNode getProbNode ()
    {
        return newNode;
    }
    
    public Variable getVariable ()
    {
        return variable;
    }
    
    public NodeType getNodeType ()
    {
        return nodeType;
    }
    
    public Point2D.Double getCursorPosition() {
		return cursorPosition;
	}    

    public String getPresentationName ()
    {
        return "Edit.AddProbNodeEdit";
    }

    public String getUndoPresentationName ()
    {
        return "Edit.AddProbNodeEdit.Undo";
    }

    public String getRedoPresentationName ()
    {
        return "Edit.AddProbNodeEdit.Redo";
    }

    public void redo ()
    {
        setTypicalRedo (false);
        super.redo ();
        probNet.addProbNode (newNode);
    }
}
