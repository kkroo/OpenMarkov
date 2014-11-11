/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.action;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import org.openmarkov.core.action.AddLinkEdit;
import org.openmarkov.core.action.AddProbNodeEdit;
import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.graphic.VisualNetwork;
import org.openmarkov.core.gui.window.edition.SelectedContent;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;

@SuppressWarnings("serial")
public class PasteEdit extends CompoundEdit
    implements
        PNEdit
{
    private SelectedContent clipboardContent;
    private SelectedContent pastedContent;
    private VisualNetwork   visualNetwork;

    public PasteEdit (VisualNetwork visualNetwork, SelectedContent clipboardContent)
    {
        this.clipboardContent = clipboardContent;
        this.visualNetwork = visualNetwork;
        this.pastedContent = null;
    }

    // Methods
    /**
     * Generate edits and does them
     * @throws DoEditException
     * @throws WrongCriterionException
     * @throws NonProjectablePotentialException
     */
    public void doEdit ()
        throws DoEditException,
        NonProjectablePotentialException,
        WrongCriterionException
    {
        HashMap<String, String> newVariables = new HashMap<String, String> ();
        ProbNet probNet = visualNetwork.getNetwork ();
        // Gather new node creation edits
        for (ProbNode probNode : clipboardContent.getNodes ())
        {
            String oldName = probNode.getName ();
            String newName = oldName;
            while (probNet.containsVariable (newName))
            {
                newName += "'";
            }
            Variable variable = new Variable (probNode.getVariable ());
            variable.setName (newName);
            newVariables.put (oldName, newName);
            
            Point2D.Double position = new Point2D.Double (probNode.getNode ().getCoordinateX () + 3.0,
                                                          probNode.getNode ().getCoordinateY ());
            edits.add (new AddProbNodeEdit (probNet, variable, probNode.getNodeType (), position));
            
        }
        
        // Apply node generation edits
        ArrayList<ProbNode> pastedNodes = new ArrayList<ProbNode> ();
        for (UndoableEdit edit : edits)
        {
        	try {
				probNet.doEdit(((PNEdit) edit));
				pastedNodes.add (((AddProbNodeEdit) edit).getProbNode ());
			} catch (ConstraintViolationException | CanNotDoEditException e) {
				e.printStackTrace();
			}
        }
        
        //Gather link creation edits
        for (Link link : clipboardContent.getLinks ())
        {
            try
            {
                String originalSourceNodeName = probNet.getProbNode (link.getNode1 ()).getName ();
                String originalDestinationNodeName = probNet.getProbNode (link.getNode2 ()).getName ();
                /**edits.add (new LinkEdit (probNet,
                                         newVariables.get (originalSourceNodeName),
                                         newVariables.get (originalDestinationNodeName),
                                         link.isDirected (), true));**/
                edits.add(new AddLinkEdit (probNet,
                        probNet.getVariable(newVariables.get (originalSourceNodeName)),
                        probNet.getVariable(newVariables.get (originalDestinationNodeName)),
                        link.isDirected ()));
            }
            catch (ProbNodeNotFoundException e)
            {/* Can not possibly happen */
            }
        }
        
        //Apply link creation edits
        ArrayList<Link> pastedLinks = new ArrayList<Link> ();
        for (UndoableEdit edit : edits)
        {
            if (edit instanceof AddLinkEdit)
            {
                AddLinkEdit linkEdit = ((AddLinkEdit) edit);
                try {
					probNet.doEdit(linkEdit);
	                pastedLinks.add (linkEdit.getLink ());
				} catch (ConstraintViolationException | CanNotDoEditException e) {
					e.printStackTrace();
				}
            }
        }
        super.end ();
        pastedContent = new SelectedContent (pastedNodes, pastedLinks);
        
        //Replace potentials to already created nodes with copies of copied nodes
        for (ProbNode originalNode : clipboardContent.getNodes ())
        {
            ArrayList<Potential> newPotentials = new ArrayList<Potential>();
            try
            {
                ProbNode newNode = probNet.getProbNode (newVariables.get (originalNode.getName ()));
                for(Potential originalPotential: originalNode.getPotentials ())
                {
                    Potential potential = originalPotential.copy ();
                    for (int i = 0; i < potential.getNumVariables (); ++i)
                    {
                        String variableName = potential.getVariable (i).getName ();
                        if (newVariables.containsKey (variableName))
                        {
                            Variable variable = probNet.getVariable (newVariables.get (variableName));
                            potential.replaceVariable (i, variable);
                        }
                    }
                    if(potential.isUtility())
                    {
                    	Variable utilityVariable = potential.getUtilityVariable();
                    	if(newVariables.containsKey (utilityVariable.getName()))
                    	{
                    		potential.replaceVariable (utilityVariable, probNet.getVariable (newVariables.get (utilityVariable.getName())));
                    	}
                    }
                    newPotentials.add (potential);
                }
                newNode.setPotentials (newPotentials);
                // Copy comment too!
                newNode.setComment (originalNode.getComment ());
                newNode.setRelevance (originalNode.getRelevance ());
                newNode.setPurpose (originalNode.getPurpose ());
                newNode.additionalProperties = new HashMap<String, String>(originalNode.additionalProperties);
            }
            catch (Exception e)
            {
                e.printStackTrace ();
            }
            
            
        }
    }

    //@Override
    public void setSignificant (boolean significant)
    {
        // Do nothing
    }
    
    /**
     * Returns the pasted content.
     * @return the pastedContent.
     */
    public SelectedContent getPastedContent ()
    {
        return pastedContent;
    }
    
   // @Override
    public ProbNet getProbNet() {
        return visualNetwork.getNetwork ();
    }       


}
