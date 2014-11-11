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
import java.util.List;

import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.UniformPotential;

/** Inverts an existing link. */
@SuppressWarnings("serial")
public class InvertLinkEdit extends BaseLinkEdit {

    /**
     * parent node
     */
    protected ProbNode node1;
    /**
     * child node
     */
    protected ProbNode node2;
    
    /**
     * Parent node's old potentials
     */
    protected List<Potential> parentsOldPotentials;    
    /**
     * Child node's old potentials
     */
    protected List<Potential> childsOldPotentials;    
    
    
	// Constructor
	/** @param probNet <code>ProbNet</code>
	 * @param variable1 <code>Variable</code>
	 * @param variable2 <code>Variable</code>
	 * @param isDirected <code>boolean</code> */
    public InvertLinkEdit (ProbNet probNet,
                           Variable variable1,
                           Variable variable2,
                           boolean isDirected)
    {
        super (probNet, variable1, variable2, isDirected);
        try
        {
            node1 = probNet.getProbNode (variable1.getName());
            node2 = probNet.getProbNode (variable2.getName());
        }
        catch (ProbNodeNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }      
    }

	// Methods
	@Override
	/** @throws exception <code>Exception</code> */
	public void doEdit() throws DoEditException {
		// Remove links first
		probNet.removeLink(node1, node2, isDirected);
		if (node2.getNodeType() != NodeType.DECISION) {
			// Update potentials
			List<Potential> newPotentials = new ArrayList<Potential>();
			this.childsOldPotentials = node2.getPotentials();
			for (Potential oldPotential : childsOldPotentials) {
				Potential newPotential = oldPotential.removeVariable(node1
						.getVariable());
				if (newPotential == null) {// It has not been implemented yet
											// for this type of
											// potential
					List<Variable> variables = oldPotential.getVariables();
					variables.add(node1.getVariable());
					newPotential = new UniformPotential(variables,
							oldPotential.getPotentialRole());
				}
				newPotential.setUtilityVariable(oldPotential
						.getUtilityVariable());
				newPotentials.add(newPotential);
			}
			node2.setPotentials(newPotentials);
		}

		// Add inverse link
		probNet.addLink(node2, node1, isDirected);
		if (node2.getNodeType() != NodeType.DECISION) {
			this.parentsOldPotentials = node1.getPotentials();
			List<Potential> newPotentials = new ArrayList<Potential>();
			for (Potential oldPotential : parentsOldPotentials) {
				// Update potential
				Potential newPotential = oldPotential.addVariable(node2
						.getVariable());
				if (newPotential == null) {// It has not been implemented yet
											// for this type of potential
					List<Variable> variables = oldPotential.getVariables();
					if (!variables.contains(node2.getVariable())) {
						variables.add(node2.getVariable());
					}
					newPotential = new UniformPotential(variables,
							oldPotential.getPotentialRole());
				}
				newPotential.setUtilityVariable(oldPotential
						.getUtilityVariable());
				newPotentials.add(newPotential);
			}
			node1.setPotentials(newPotentials);
		}
	}

	public void undo() {
		super.undo();
		try {
			probNet.removeLink(variable2, variable1, isDirected);
			probNet.addLink(variable1, variable2, isDirected);
            node1.setPotentials(parentsOldPotentials);
            node2.setPotentials(childsOldPotentials);
		} catch (Exception exc){}
	}
		
    /** Method to compare two InvertLinkEdits comparing the names of
     * the source and destination variable alphabetically.
     * @param obj
     * @return
     */
    public int compareTo(InvertLinkEdit obj){
        int result;

        if (( result = variable1.getName().compareTo(obj.getVariable1().
                getName())) != 0)
            return result;
        if (( result = variable2.getName().compareTo(obj.getVariable2().
                getName())) != 0)
            return result;
        else
            return 0;
    }

	@Override
	public String getOperationName() {
		return "Invert link";
	}
	
	/** This method assumes that the link is directed, otherwise has no sense.
	 * @return <code>String</code> */
	public String toString() {
		return new StringBuffer(getOperationName()).append(": ").append(variable1).append("-->")
				.append(variable2).append(" ==> ").append(variable2).append("-->")
				.append(variable1).toString();
	}

    @Override
    public BaseLinkEdit getUndoEdit ()
    {
        return new InvertLinkEdit (getProbNet (), getVariable2 (), getVariable1 (), isDirected ());
    }

}