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
import org.openmarkov.core.oopn.Instance;
import org.openmarkov.core.oopn.Instance.ParameterArity;

@SuppressWarnings("serial")
public class ChangeParameterArityEdit extends SimplePNEdit{

	private Instance instance = null;
	private ParameterArity arity = null;
	private ParameterArity previousArity = null;
	
	public ChangeParameterArityEdit(ProbNet probNet, Instance instance, ParameterArity arity) {
		super(probNet);
		this.instance = instance;
		this.arity = arity;
		this.previousArity = instance.getArity();
	}

	@Override
	public void doEdit() throws DoEditException {
		instance.setArity(arity);
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		instance.setArity(previousArity);
	}

	
}
