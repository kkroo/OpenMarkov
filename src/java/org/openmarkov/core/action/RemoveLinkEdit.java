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

import org.apache.log4j.Logger;
import org.openmarkov.core.model.graph.Graph;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.SumPotential;
import org.openmarkov.core.model.network.potential.UniformPotential;

@SuppressWarnings("serial")
public class RemoveLinkEdit extends BaseLinkEdit {
	
	private Logger logger;
	
    private boolean                updatePotentials;
	/**
	 * Resulting link of addition or removal.
	 */
	protected Link link;
	/**
	 * The last <code>Potential</code> of the second node before the edition
	 * /**
	 * parent node
	 */
	public ProbNode node1;
	/**
	 * child node
	 */
	public ProbNode node2;

	/**
	 * The new <code>Potential</code> of the second node
	 */
	protected List<Potential> newPotentials = new ArrayList<Potential>() ;
	
	protected List<Potential> oldPotentials;
	// Constructor
	/** @param probNet <code>ProbNet</code>
	 * @param variable1 <code>Variable</code>
	 * @param variable2 <code>Variable</code>
	 * @param isDirected <code>boolean</code> */
	public RemoveLinkEdit(ProbNet probNet, Variable variable1, 
			Variable variable2,	boolean isDirected, boolean updatePotentials) {
		super(probNet, variable1, variable2, isDirected);
		
        node1 = probNet.getProbNode (variable1);
        node2 = probNet.getProbNode (variable2);
		
        this.updatePotentials = updatePotentials;
		this.link = null;
		this.logger = Logger.getLogger(RemoveLinkEdit.class);
	}
	
    public RemoveLinkEdit (ProbNet probNet,
                           Variable variable1,
                           Variable variable2,
                           boolean isDirected)
    {
        this (probNet, variable1, variable2, isDirected, true);
    }

	@Override
	public void doEdit () {
		probNet.removeLink (node1, node2, isDirected);
		Graph graph = probNet.getGraph();
		if (graph.useExplicitLinks()) {
			this.link = graph.getLink (node1.getNode (), node2.getNode (), isDirected);
		}
		if (updatePotentials)
		{
			this.oldPotentials = node2.getPotentials ();
			if (node2.getNodeType() == NodeType.UTILITY) {// supervalue nodes

				if (node2.onlyNumericalParents()) {// utility and numerical parents sum
					for (Potential oldPotential : oldPotentials)
					{
						// Update potential
						List<Variable> variables = oldPotential.getVariables ();
						variables.remove (node1.getVariable ());
						Potential newPotential = new SumPotential (variables,
						oldPotential.getPotentialRole ());
						newPotential.setUtilityVariable (oldPotential.getUtilityVariable ());
						newPotentials.add (newPotential);
					}
				}else if (!node2.onlyNumericalParents()) {//mixture of finite states and numerical Uniform
					for (Potential oldPotential : oldPotentials)
					{
						// Update potential
						List<Variable> variables = oldPotential.getVariables ();
						variables.remove (node1.getVariable ());
						Potential newPotential = new UniformPotential (variables,
								oldPotential.getPotentialRole ());
						newPotential.setUtilityVariable (oldPotential.getUtilityVariable ());
						newPotentials.add (newPotential);
					}
				}
				node2.setPotentials (newPotentials);
			} else {

				// Update potentials
				this.oldPotentials = node2.getPotentials ();
				for (Potential oldPotential : oldPotentials)
				{
					Potential newPotential = oldPotential.removeVariable (node1.getVariable ());
					if (newPotential == null)
					{// It has not been implemented yet for this type of
						// potential
						List<Variable> variables = oldPotential.getVariables ();
						variables.remove (node1.getVariable ());
						newPotential = new UniformPotential (variables,
								oldPotential.getPotentialRole ());
					} 
					newPotential.setUtilityVariable (oldPotential.getUtilityVariable ());
					newPotentials.add (newPotential);
				}
				node2.setPotentials (newPotentials);
			}
		}
			}

	
	@Override
	public void undo() {
		super.undo();

		if(updatePotentials)
		{
		    node2.setPotentials (oldPotentials);
		}
		try {
			probNet.addLink(variable1, variable2, isDirected);
			Graph graph = probNet.getGraph();
			if (graph.useExplicitLinks()) {
				Link newLink = graph.getLink (node1.getNode (), node2.getNode (), isDirected);
				if (link != null && newLink != null) {
					Potential restrictionsPotential = link.getRestrictionsPotential();
					newLink.setRestrictionsPotential(restrictionsPotential);
					List<State> revealingStates = link.getRevealingStates();
					newLink.setRevealingStates(revealingStates);
					List<PartitionedInterval> revealingIntervals = link.getRevealingIntervals();
					newLink.setRevealingIntervals(revealingIntervals);
				}
			}
		} catch (Exception e) {
			logger.fatal (e);
		}
	}

    /** Method to compare two RemoveLinkEdits comparing the names of
     * the source and destination variable alphabetically.
     * @param obj
     * @return
     */
    public int compareTo(RemoveLinkEdit obj){
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
		return "Remove link";
	}

    @Override
    public BaseLinkEdit getUndoEdit ()
    {
        return new AddLinkEdit (getProbNet (), getVariable1 (), getVariable2 (), isDirected ());
    }	

}
