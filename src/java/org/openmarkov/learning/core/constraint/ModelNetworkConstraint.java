/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.learning.core.constraint;

import java.util.ArrayList;
import java.util.List;

import org.openmarkov.core.action.AddLinkEdit;
import org.openmarkov.core.action.InvertLinkEdit;
import org.openmarkov.core.action.OrientLinkEdit;
import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.action.RemoveLinkEdit;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.constraint.ConstraintBehavior;
import org.openmarkov.core.model.network.constraint.PNConstraint;
import org.openmarkov.core.model.network.constraint.UtilConstraints;
import org.openmarkov.core.model.network.constraint.annotation.Constraint;
import org.openmarkov.learning.core.util.ModelNetUse;

/** This constraint ensures that the editions done during the learning of a
 * network respect the structure of the model net and the constraints
 * selected by the user. */
@Constraint (name = "ModelNetworkConstraint", defaultBehavior = ConstraintBehavior.OPTIONAL)
public class ModelNetworkConstraint extends PNConstraint {

	// Attributes.
	ModelNetUse modelNetUse;
	ProbNet modelNet;

	
	// Constructors
    public ModelNetworkConstraint(ModelNetUse modelNetUse, ProbNet modelNet) {
    	
    	this.modelNet = modelNet.copy();
        this.modelNetUse = modelNetUse;
    }    
    
    public ModelNetworkConstraint(ProbNet modelNet) {
    	this.modelNet = null;
        this.modelNetUse = null;
    }

	@Override
	public boolean checkProbNet(ProbNet probNet) {
		return true;
	}

	@Override
	public boolean checkEdit(ProbNet probNet, PNEdit edit)  
	throws NonProjectablePotentialException, 
	WrongCriterionException {
        List<PNEdit> edits = new ArrayList<PNEdit> ();
        Node source, destination;
        if (modelNetUse.isStartFromModelNet())
        {
            try
            {
                /*
                 * Check for prohibited additions. If the link we want to add
                 * was not present in the model net, it is not allowed.
                 */
                if (!modelNetUse.isLinkAdditionAllowed ())
                {
                    edits = UtilConstraints.getSimpleEditsByType (edit, AddLinkEdit.class);
                    for (PNEdit simpleEdit : edits)
                    {
                        source = modelNet.getProbNode (((AddLinkEdit) simpleEdit).getVariable1 ().getName ()).getNode ();
                        destination = modelNet.getProbNode (((AddLinkEdit) simpleEdit).getVariable2 ().getName ()).getNode ();
                        if ((modelNet.getGraph ().getLink (source, destination, true) == null)
                            && (modelNet.getGraph ().getLink (source, destination, true) == null))
                        {
                            return false;
                        }
                    }
                }
                /*
                 * Check for prohibited deletions. If the link we want to remove
                 * was in the model net, the elimination is not allowed.
                 */
                if (!modelNetUse.isLinkRemovalAllowed ())
                {
                    edits = UtilConstraints.getSimpleEditsByType (edit, RemoveLinkEdit.class);
                    for (PNEdit simpleEdit : edits)
                    {
                        source = modelNet.getProbNode (((RemoveLinkEdit) simpleEdit).getVariable1 ().getName ()).getNode ();
                        destination = modelNet.getProbNode (((RemoveLinkEdit) simpleEdit).getVariable2 ().getName ()).getNode ();
                        if ((modelNet.getGraph ().getLink (source, destination, true) != null)
                            || (modelNet.getGraph ().getLink (destination, source, true) != null))
                        {
                            return false;
                        }
                    }
                }
                /*
                 * Check for prohibited inversions. If the link we want to
                 * invert was in the model net, it is not allowed.
                 */
                if (!modelNetUse.isLinkInversionAllowed ())
                {
                    edits = UtilConstraints.getSimpleEditsByType (edit, InvertLinkEdit.class);
                    for (PNEdit simpleEdit : edits)
                    {
                        source = modelNet.getProbNode (((InvertLinkEdit) simpleEdit).getVariable1 ().getName ()).getNode ();
                        destination = modelNet.getProbNode (((InvertLinkEdit) simpleEdit).getVariable2 ().getName ()).getNode ();
                        if ((modelNet.getGraph ().getLink (source, destination, true) != null))
                        {
                            return false;
                        }
                    }
                    edits = UtilConstraints.getSimpleEditsByType (edit, OrientLinkEdit.class);
                    for (PNEdit simpleEdit : edits)
                    {
                        source = modelNet.getProbNode (((OrientLinkEdit) simpleEdit).getVariable1 ().getName ()).getNode ();
                        destination = modelNet.getProbNode (((OrientLinkEdit) simpleEdit).getVariable2 ().getName ()).getNode ();
                        if ((modelNet.getGraph ().getLink (destination, source, true) != null))
                        {
                            return false;
                        }
                    }
                }
            }
            catch (ProbNodeNotFoundException e)
            {
                return (false);
            }
        }
		return true;
	}

    @Override
    protected String getMessage ()
    {
        return "tried to add, remove or invert the wrong link";
    }
    
    /**
     * Sets the modelNetUse.
     * @param modelNetUse the modelNetUse to set.
     */
    public void setModelNetUse (ModelNetUse modelNetUse)
    {
        this.modelNetUse = modelNetUse;
    }
    /**
     * Sets the modelNet.
     * @param modelNet the modelNet to set.
     */
    public void setModelNet (ProbNet modelNet)
    {
        this.modelNet = modelNet;
    }

}
