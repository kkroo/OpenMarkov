/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.action;

import java.util.ArrayList;

import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PolicyType;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;

public class RemovePolicyEdit extends SimplePNEdit {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Potential lastPotential;
	private Variable variable;
	private PolicyType lastPolicyType;
	
	/**
	 * 
	 * @param probNode
	 */
	public RemovePolicyEdit(ProbNode probNode) {
		super(probNode.getProbNet());
		this.variable = probNode.getVariable();
		if (probNode.getNodeType() == NodeType.DECISION && 
				probNode.getPolicyType() != PolicyType.OPTIMAL){
			lastPotential = probNode.getPotentials().get( 0 );
			lastPolicyType = probNode.getPolicyType();
		}
		
	}
	@Override
	public void doEdit() throws DoEditException {
		ArrayList<Potential> potentials = new ArrayList <Potential>();
		if ( probNet.getProbNode(variable).getNodeType()== NodeType.DECISION && 
				lastPolicyType != PolicyType.OPTIMAL){
			probNet.getProbNode(variable).setPolicyType(PolicyType.OPTIMAL);
			probNet.getProbNode(variable).setPotentials(potentials);
		}
	}
	
	public void undo(){
		super.undo();
		ArrayList<Potential> potentials = new ArrayList <Potential>();
		if ( probNet.getProbNode(variable).getNodeType()== NodeType.DECISION &&
				lastPolicyType != PolicyType.OPTIMAL){
			potentials.add(lastPotential);
			probNet.getProbNode(variable).setPotentials(potentials);
			probNet.getProbNode(variable).setPolicyType(lastPolicyType);
		}
	}
}
