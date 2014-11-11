/*
* Copyright 2012 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.oopn.action;

import java.util.HashSet;

import javax.swing.undo.CannotUndoException;

import org.openmarkov.core.action.CRemoveProbNodeEdit;
import org.openmarkov.core.action.CompoundPNEdit;
import org.openmarkov.core.action.RemoveLinkEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.oopn.Instance;
import org.openmarkov.core.oopn.InstanceReferenceLink;
import org.openmarkov.core.oopn.OOPNet;
import org.openmarkov.core.oopn.ReferenceLink;
import org.openmarkov.core.oopn.exception.InstanceAlreadyExistsException;

/**
 * @author ibermejo
 *
 */
@SuppressWarnings("serial")
public class RemoveInstanceEdit extends CompoundPNEdit {
	
	private Instance instance;
	private HashSet<ProbNode> nodesToRemove;
	private HashSet<Link> linksToRemove;
	private HashSet<ReferenceLink> instanceLinksToRemove;

	/**
	 * @param probNet
	 */
	public RemoveInstanceEdit(ProbNet probNet, String instanceName) {
		super(probNet);
		this.instance = ((OOPNet)probNet).getInstances().get(instanceName);
		
		nodesToRemove = new HashSet<ProbNode>();
		linksToRemove = new HashSet<Link>();
		instanceLinksToRemove = new HashSet<ReferenceLink>();
		for(ProbNode probNode : instance.getNodes())
		{
			nodesToRemove.add(probNode);
			linksToRemove.addAll(probNode.getNode().getLinks());
		}
		for(ReferenceLink link : ((OOPNet)probNet).getReferenceLinks())
		{
            if(link instanceof InstanceReferenceLink)
            {
            	InstanceReferenceLink instanceLink = (InstanceReferenceLink)link;    			
			
				if (instanceLink.getSourceInstance().equals(this.instance)
						|| instanceLink.getDestInstance().equals(this.instance)) {
					instanceLinksToRemove.add(link);
				}
            }
		}
	}

	@Override
	public void generateEdits() throws
			NonProjectablePotentialException, WrongCriterionException {
		
		for(Link link : linksToRemove)
		{
			edits.add(new RemoveLinkEdit(probNet, 
						((ProbNode) link.getNode1().getObject()).getVariable(),
						((ProbNode) link.getNode2().getObject()).getVariable(), 
						link.isDirected()));
		}

		for(ProbNode probNode : nodesToRemove)
		{
			edits.add ( new CRemoveProbNodeEdit( probNet, probNode));
		}		
	}


	@Override
	public void doEdit() throws DoEditException,
			NonProjectablePotentialException, WrongCriterionException {
		super.doEdit();
		((OOPNet)probNet).getInstances().remove(instance.getName());
		for(ReferenceLink instanceLink: instanceLinksToRemove)
		{
		    ((OOPNet)probNet).getReferenceLinks().remove(instanceLink);
		}
	}

	@Override
	public void undo() throws CannotUndoException {
		// TODO Auto-generated method stub
		super.undo();
		try {
		    ((OOPNet)probNet).addInstance(instance);
		} catch (InstanceAlreadyExistsException e) {
			//Impossible to get here
		}
		for(ReferenceLink instanceLink: instanceLinksToRemove)
		{
		    ((OOPNet)probNet).getReferenceLinks().add(instanceLink);
		}		
	}

	
	

}
