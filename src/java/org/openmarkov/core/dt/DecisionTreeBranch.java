/*
 * Copyright 2012 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.dt;

import java.util.LinkedList;
import java.util.List;

import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;

public class DecisionTreeBranch implements DecisionTreeElement
{

    private Variable         branchVariable;
    private State            branchState;
    private DecisionTreeNode parent;
    private DecisionTreeNode child;
    private ProbNet			 probNet;
    private double           utility = Double.NEGATIVE_INFINITY; 
    private double           scenarioProbability = Double.NEGATIVE_INFINITY; 

    public DecisionTreeBranch (ProbNet probNet,
                               Variable branchVariable,
                               State branchState)
    {
        this.probNet = probNet;
        this.branchState = branchState;
        this.branchVariable = branchVariable;
    }

    public DecisionTreeBranch (ProbNet probNet)
    {
        this (probNet, null, null);
    }

     public List<DecisionTreeElement> getChildren ()
    {
        List<DecisionTreeElement> children = new LinkedList<> ();
        children.add (child);
        return children;
    }
    
    public double getUtility ()
    {
        if(utility == Double.NEGATIVE_INFINITY)
        {
            utility = (child != null)? child.getUtility () : 0;
            if(parent != null && ((DecisionTreeNode)parent).getProbNode ().getNodeType () == NodeType.CHANCE)
            {
                utility *= getBranchProbability ();
            }
        }
        return utility;
    } 
    
    public double getBranchProbability ()
    {
    	double parentScenarioProb = parent.getScenarioProbability();
    	return (parentScenarioProb!=0)?getScenarioProbability()/parentScenarioProb:0;
    }   
    
    public EvidenceCase getBranchStates()
    {
        EvidenceCase evidenceCase = (parent!=null)? new EvidenceCase(parent.getBranchStates()) : new EvidenceCase();
        if(branchVariable != null)
        {
            try
            {
                evidenceCase.addFinding (new Finding(branchVariable, branchState));
            }
            catch (InvalidStateException | IncompatibleEvidenceException e)
            {
                e.printStackTrace();
            }
        }
        return evidenceCase;
    }
    
    /**
     * Returns the branchVariable.
     * @return the branchVariable.
     */
    public Variable getBranchVariable ()
    {
        return branchVariable;
    }

    /**
     * Returns the branch state
     * @return
     */
    public State getBranchState ()
    {
        return branchState;
    }
    
    public double getScenarioProbability()
    {
        if(scenarioProbability == Double.NEGATIVE_INFINITY)
        {
        	scenarioProbability = 1;    	
        	if(child.getProbNode().getNodeType() == NodeType.UTILITY)
        	{
            	EvidenceCase evidenceCase = getBranchStates();
    	    	for(Finding finding : evidenceCase.getFindings())
    	    	{
    	    		ProbNode probNode = probNet.getProbNode(finding.getVariable());
    	    		if(probNode != null && probNode.getNodeType() == NodeType.CHANCE)
    	    		{
    	    			Potential potential = probNode.getPotentials().get(0);
    	    			scenarioProbability *= potential.getProbability(evidenceCase);
    	    		}
    	    	}
        	}else
        	{
        		scenarioProbability = child.getScenarioProbability();
        	}
        }
        return scenarioProbability;
    }       

    public DecisionTreeNode getChild ()
    {
        return child;
    }
    
    /**
     * Sets the child.
     * @param child the child to set.
     */
    protected void setChild (DecisionTreeNode child)
    {
        this.child = child;
        child.setParent (this);
    }

    @Override
    public String toString ()
    {
        StringBuilder builder = new StringBuilder ();
        builder.append ("DecisionTreeBranch [branchVariable=").append (branchVariable).append (", branchState=").append (branchState).append ("]");
        return builder.toString ();
    }

    @Override
    public void setParent (DecisionTreeElement parent)
    {
        this.parent = (DecisionTreeNode) parent;
    }

    public DecisionTreeNode getParent ()
    {
        return parent;
    }
}
