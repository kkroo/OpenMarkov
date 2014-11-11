/*
* Copyright 2012 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package org.openmarkov.core.gui.action;

import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.StringWithProperties;

@SuppressWarnings("serial")
public class NodeDecisionCriteriaEdit extends SimplePNEdit{
	
	private StringWithProperties  currentDecisionCriteria;
	private StringWithProperties newDecisionCriteria;
	private ProbNode probNode;

	public NodeDecisionCriteriaEdit (ProbNode probNode, StringWithProperties decisionCriteria) {
		super(probNode.getProbNet());
		this.probNode = probNode;
		this.currentDecisionCriteria = probNode.getVariable().getDecisionCriteria();
		this.newDecisionCriteria = decisionCriteria;
	}
	@Override
	public void doEdit() throws DoEditException {
		probNode.getVariable().setDecisionCriteria(newDecisionCriteria);
	}
	
	@Override
	public void undo() {
		super.undo();
		probNode.getVariable().setDecisionCriteria(currentDecisionCriteria);
	}

}

