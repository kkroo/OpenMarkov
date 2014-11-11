/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package org.openmarkov.core.oopn.action;

import javax.swing.undo.CannotUndoException;

import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.oopn.Instance;
import org.openmarkov.core.oopn.InstanceReferenceLink;
import org.openmarkov.core.oopn.NodeReferenceLink;
import org.openmarkov.core.oopn.OOPNet;
import org.openmarkov.core.oopn.ReferenceLink;

@SuppressWarnings("serial")
public class AddReferenceLinkEdit extends SimplePNEdit{

	private ReferenceLink referenceLink;
	
	public AddReferenceLinkEdit(ProbNet probNet, Instance sourceInstance, Instance destinationInstance,
			Instance destinationParameter) {
		super(probNet);
		
		referenceLink = new InstanceReferenceLink(sourceInstance, destinationInstance, destinationParameter);
	}
	
	public AddReferenceLinkEdit(ProbNet probNet, ProbNode sourceNode, ProbNode destinationNode) {
		super(probNet);
		
		referenceLink = new NodeReferenceLink(sourceNode, destinationNode);
	}	
	
	@Override
	public void doEdit() throws DoEditException {
		((OOPNet)probNet).addReferenceLink(referenceLink);
	}	
	

	@Override
	public void undo() throws CannotUndoException {
		// TODO Auto-generated method stub
	    ((OOPNet)probNet).removeReferenceLink(referenceLink);
	}

}
