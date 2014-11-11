/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.inference;

import java.util.HashMap;
import java.util.List;

import org.openmarkov.core.action.PNESupport;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NormalizeNullVectorException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.TablePotential;

/**
 * @author mluque
 * @author marias
 * @author fjdiez
 *
 */
public abstract class InferenceAlgorithm
{
    /** This is a copy of the <code>ProbNet</code> received. */
    protected ProbNet                     probNet;
    /** For undo/redo operations. */
    protected PNESupport                  pNESupport;
   
  
	/**
	 * Evidence introduced before the network is resolved. 
	 * In influence diagrams this is Ezawa's evidence.
	 */
	private EvidenceCase preResolutionEvidence;
	
	/**
	 * Evidence when the network has been resolved. 
	 * In influence diagrams this is Luque and Diez's evidence.
	 */
	private EvidenceCase postResolutionEvidence;
	
	/**
	 * @return The post-resolution evidence.
	 */
	public EvidenceCase getPostResolutionEvidence() {
		return postResolutionEvidence;
	}

	/**
	 * @param postResolutionEvidence
	 */
	public void setPostResolutionEvidence(EvidenceCase postResolutionEvidence) {
		this.postResolutionEvidence = postResolutionEvidence;
	}

	/**
     * Policies set by the user. The optimal policy would only be calculated for the decisions
     * without imposed policies.
     * Each policy is stochastic, which implies it is a probability potential whose domain
     * contains the decision.
     */
   // private ArrayList<TablePotential> imposedPolicies;
    
    /**
     * Variables that will not be eliminated during the inference, and therefore all the results
     * contain these variables in the domain.
     */
    private List<Variable> conditioningVariables;
    
  
    /**
     * @return The pre-resolution evidence
     */
    public EvidenceCase getPreResolutionEvidence() {
		return preResolutionEvidence;
	}

	/**
	 * @param preResolutionEvidence The pre-resolution evidence to set
	 */
	public void setPreResolutionEvidence(EvidenceCase preResolutionEvidence) {
		this.preResolutionEvidence = preResolutionEvidence;
	}

	/**
	 * @return The conditioning variables
	 */
	public List<Variable> getConditioningVariables() {
		return conditioningVariables;
	}

	/**
	 * @param conditioningVariables The conditioning variables to set
	 */
	public void setConditioningVariables(List<Variable> conditioningVariables) {
		this.conditioningVariables = conditioningVariables;
	}

	/**
	 * @return The imposed policies
	 *//*
	protected ArrayList<TablePotential> getImposedPolicies() {
		return imposedPolicies;
	}*/
  
    /**
     * @param probNet The network used in the inference
     * @throws NotEvaluableNetworkException
     */
    public InferenceAlgorithm (ProbNet probNet)
        throws NotEvaluableNetworkException
    {
        this.probNet = probNet;
        preResolutionEvidence = new EvidenceCase();
        postResolutionEvidence = new EvidenceCase();
        if (!isEvaluable (probNet))
        {
            throw new NotEvaluableNetworkException (probNet.toString ());
        }
    }

    /**
     * @param probNet
     * @return True if the network can be evaluated.
     */
    public boolean isEvaluable (ProbNet probNet){
    		boolean isEvaluable;
    		
    		isEvaluable = true;
    	
    		try {
				checkEvaluability(probNet);
			} catch (NotEvaluableNetworkException e) {
				isEvaluable = false;
			}
    		return isEvaluable;
    	
    }

      
       
  	/**
     * @return The optimal policy for the decision that does not have any imposed policy.
     * The domain of the policy also includes the decision and the conditioning variables.
     */
    public abstract Potential getOptimizedPolicy(Variable decisionVariable) throws
	IncompatibleEvidenceException,
	UnexpectedInferenceException;
    
    
    /**
     * @return The expected utilities of the optimal policy for the decision that does not have any imposed policy.
     * The domain of the policy also includes the decision and the conditioning variables.
     */
    public abstract Potential getExpectedUtilities(Variable decisionVariable) throws
	IncompatibleEvidenceException,
	UnexpectedInferenceException;
    
  
	/**
     * @return The global expected utility of the influence diagram. It is a potential
     * defined over the conditioning variables.
     */
    public abstract TablePotential getGlobalUtility() throws
	IncompatibleEvidenceException,
	UnexpectedInferenceException;
    
    
    /**
     * @return The posterior probabilities and utilities of the network.
     * @throws IncompatibleEvidenceException
     * @throws NormalizeNullVectorException
     */
    public abstract HashMap<Variable,TablePotential> getProbsAndUtilities() throws
    	IncompatibleEvidenceException,
        UnexpectedInferenceException;
    
   
    /**
     * @param variablesOfInterest
     * @return The posterior probabilities and utilities of the network.
     * @throws IncompatibleEvidenceException
     * @throws NormalizeNullVectorException
     */
    public abstract HashMap<Variable,TablePotential> getProbsAndUtilities(List<Variable> variablesOfInterest) throws
	IncompatibleEvidenceException,
	UnexpectedInferenceException;
    
    /**
     * @param variables
     * @return The joint probability of a list of variables
     * @throws IncompatibleEvidenceException
     * @throws NormalizeNullVectorException
     */
    public abstract TablePotential getJointProbability(List<Variable> variables)throws
	IncompatibleEvidenceException,
	UnexpectedInferenceException;
    
  
	
	
	/**
	 * @param decision
	 * @return The imposed policy of the decision
	 */
	protected Potential getImposedPolicy(Variable decision) {
		Potential policy = null;
		
		ProbNode decisionNode = probNet.getProbNode(decision);
		if (decisionNode==null){
			policy = null;
		}
		else{
		    List<Potential> potentials = decisionNode.getPotentials();
			if ((potentials == null)||(potentials.size()==0)){
				policy = null;
			}
			else{
				policy = potentials.get(0);
			}
		}
		return policy;
	}
    /**
     * @param decision
     * @return True if the decision has an imposed policy.
     */
    public boolean hasImposedPolicy(Variable decision){
    	return (getImposedPolicy(decision)!=null);
    }

    protected static void checkEvaluability (ProbNet probNet)
        throws NotEvaluableNetworkException
    {
    }
}