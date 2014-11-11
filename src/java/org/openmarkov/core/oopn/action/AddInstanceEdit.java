/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package org.openmarkov.core.oopn.action;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;

import org.openmarkov.core.action.AddLinkEdit;
import org.openmarkov.core.action.AddProbNodeEdit;
import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.oopn.Instance;
import org.openmarkov.core.oopn.OOPNet;
import org.openmarkov.core.oopn.exception.InstanceAlreadyExistsException;

@SuppressWarnings("serial")
public class AddInstanceEdit extends AbstractUndoableEdit implements PNEdit {
	private String instanceName;
	private OOPNet oopNet;
	private ProbNet classNet;
	private java.awt.geom.Point2D.Double cursorPositon;
	private List<PNEdit> edits = null;
	private int doneEditCounter;

	public AddInstanceEdit(OOPNet probNet, ProbNet classNet,
			String instanceName, java.awt.geom.Point2D.Double cursorPosition) {
		this.oopNet = probNet;
		this.classNet = classNet;
		this.instanceName = instanceName;
		this.cursorPositon = cursorPosition;
		edits = new ArrayList<>();
	}

	@Override
	public void doEdit() throws DoEditException,
			NonProjectablePotentialException, WrongCriterionException	{
		doneEditCounter = 0;
		
		if (oopNet.getInstances().containsKey(instanceName)) {
			throw new DoEditException("An instance with name " + instanceName
					+ " alreadyExists");
		}
		// Calculate top left corner of net
		double topCorner = Double.POSITIVE_INFINITY;
		double leftCorner = Double.POSITIVE_INFINITY;
		for(ProbNode probNode : classNet.getProbNodes())
		{
			if(probNode.getNode ().getCoordinateX () < leftCorner)
			{
				leftCorner = probNode.getNode ().getCoordinateX ();
			}
			if(probNode.getNode ().getCoordinateY () < topCorner)
			{
				topCorner = probNode.getNode ().getCoordinateY ();
			}
		}
		
		// Add nodes to the probNet class
		for(ProbNode probNode : classNet.getProbNodes())
		{
			Variable variable = new Variable (probNode.getVariable ());
	        variable.setName (instanceName + "." + variable.getName());	
	        Point2D.Double position = new Point2D.Double (probNode.getNode ().getCoordinateX () - leftCorner + cursorPositon.getX(),
	                probNode.getNode ().getCoordinateY () - topCorner + cursorPositon.getY());
	        
	        edits.add (new AddProbNodeEdit (oopNet, variable, probNode.getNodeType (), position));			
		}
	    // Apply node generation edits
        for (PNEdit edit : edits)
        {
            try {
				oopNet.doEdit(edit);
				++doneEditCounter;
			} catch (ConstraintViolationException | CanNotDoEditException e) {
				this.undo();
				throw new DoEditException(e);
			}
        }
		
		// Add links to the probNet class
        // Gather link creation edits
        for (Link link : classNet.getGraph().getLinks())
        {
            try
            {
                String originalSourceNodeName = classNet.getProbNode (link.getNode1 ()).getName ();
                String originalDestinationNodeName = classNet.getProbNode (link.getNode2 ()).getName ();

                edits.add(new AddLinkEdit (oopNet,
                        oopNet.getVariable(instanceName + "." + originalSourceNodeName),
                        oopNet.getVariable(instanceName + "." + originalDestinationNodeName),
                        link.isDirected ()));
            }
            catch (ProbNodeNotFoundException e)
            {/* Can not possibly happen */
            }
        }
        
        //Apply link creation edits
        ArrayList<Link> pastedLinks = new ArrayList<Link> ();
        for (PNEdit edit : edits)
        {
            if (edit instanceof AddLinkEdit)
            {
                AddLinkEdit linkEdit = ((AddLinkEdit) edit);
                try {
					oopNet.doEdit(linkEdit);
					++doneEditCounter;
	                pastedLinks.add (linkEdit.getLink ());
				} catch (ConstraintViolationException | CanNotDoEditException e) {
					this.undo();
					throw new DoEditException(e);
				}
            }
        }
        
        ArrayList<ProbNode> instanceNodes = new ArrayList<ProbNode>(); 
        //Replace potentials to already created nodes with copies of copied nodes
        for (ProbNode originalNode : classNet.getProbNodes())
        {
            ArrayList<Potential> newPotentials = new ArrayList<Potential>();
            try
            {
                ProbNode newNode = oopNet.getProbNode (instanceName + "." + originalNode.getName ());
                for(Potential originalPotential: originalNode.getPotentials ())
                {
                    Potential potential = originalPotential.copy ();
                    for (int i = 0; i < potential.getNumVariables (); ++i)
                    {
                        String variableName = potential.getVariable (i).getName ();
                        Variable variable = oopNet.getVariable (instanceName + "."  + variableName);
                        potential.replaceVariable (i, variable);
                    }
                    if(potential.getPotentialRole() == PotentialRole.UTILITY)
                    {
                    	potential.setUtilityVariable (oopNet.getVariable (instanceName + "."  + potential.getUtilityVariable ().getName ()));
                    }
                    newPotentials.add (potential);
                }
                newNode.setPotentials (newPotentials);
                // Copy comment too!
                newNode.setComment (originalNode.getComment ());
                newNode.setRelevance (originalNode.getRelevance ());
                newNode.setPurpose (originalNode.getPurpose ());
                newNode.additionalProperties = new HashMap<String, String>(originalNode.additionalProperties);
                newNode.setInput(originalNode.isInput());
                instanceNodes.add(newNode);
            }
            catch (Exception e)
            {
                e.printStackTrace ();
            }
        }
        
		try {
			Instance instance = new Instance(instanceName, classNet, instanceNodes); 
			oopNet.addInstance(instance);
		} catch (InstanceAlreadyExistsException e) {
			throw new DoEditException("An instance with name " + instanceName
					+ "alreadyExists");
		}        

	}
	@Override
	public void setSignificant(boolean significant) {
	}
	
	@Override
	public ProbNet getProbNet() {
		return this.oopNet;
	}

	@Override
	public void undo() throws CannotUndoException {
		for(int i = 0 ; i < doneEditCounter; ++i)
		{
			oopNet.getPNESupport().undo();
		}
		doneEditCounter = 0;
	}	
}
