/*
* Copyright 2012 CISIAD, UNED, Spain
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

@SuppressWarnings("serial")
public class MarkAsInputEdit extends SimplePNEdit{

	private ProbNode probNode = null;
	private Instance instance = null;
	private boolean isInput = false;
	private boolean wasInput = false;
	
	public MarkAsInputEdit(ProbNet probNet, boolean isInput, ProbNode probNode) {
		super(probNet);
		this.isInput = isInput;
		this.probNode = probNode;
	}

	public MarkAsInputEdit(ProbNet probNet, boolean isInput, Instance instance) {
		super(probNet);
		this.isInput = isInput;
		this.instance = instance;
	}

	@Override
	public void doEdit() throws DoEditException {
		if(probNode != null)
		{
			probNode.setInput(isInput);
			wasInput = probNode.isInput();
		}
		if(instance != null)
		{
			instance.setInput(isInput);
			wasInput = instance.isInput();
		}
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		if(probNode != null)
		{
			probNode.setInput(wasInput);
		}
		if(instance != null)
		{
			instance.setInput(wasInput);
		}
	}
	
}
