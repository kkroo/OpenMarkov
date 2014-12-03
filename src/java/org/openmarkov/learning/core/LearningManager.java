/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/


package org.openmarkov.learning.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NormalizeNullVectorException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.InferenceAlgorithm;
import org.openmarkov.core.io.database.CaseDatabase;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.learning.algorithm.pc.independencetester.CrossEntropyIndependenceTester;
import org.openmarkov.learning.algorithm.pc.independencetester.IndependenceTester;
import org.openmarkov.learning.core.algorithm.LearningAlgorithm;
import org.openmarkov.learning.core.algorithm.LearningAlgorithmManager;
import org.openmarkov.learning.core.algorithm.LearningAlgorithmType;
import org.openmarkov.learning.core.constraint.ModelNetworkConstraint;
import org.openmarkov.learning.core.exception.EmptyModelNetException;
import org.openmarkov.learning.core.exception.LatentVariablesException;
import org.openmarkov.learning.core.util.LearningEditMotivation;
import org.openmarkov.learning.core.util.LearningEditProposal;
import org.openmarkov.learning.core.util.ModelNetUse;
import org.openmarkov.learning.evaluation.Classification;

/** This class launches the learning algorithm and receives the results of
 * the learning.
 * @author joliva
 * @author manuel
 * @author fjdiez
 * @version 1.0
 * @since OpenMarkov 1.0 */
public class LearningManager {
    
    private static LearningAlgorithmManager learningAlgorithmManager = new LearningAlgorithmManager ();
    
    /**  Learning algorithm */
    private LearningAlgorithm learningAlgorithm = null;
    
    
    /** ProbNet to learn. */
    private ProbNet learnedNet = null;
    
    /** Structure that specifies use of model net */
    private ModelNetUse modelNetUse;
    
    /** Case database */
    private CaseDatabase caseDatabase = null;
    
    /**
     * Constructor
     * @param preprocessedNet <code>ProbNet</code> Net with the variables of
     * interest after preprocessing.
     * @param algorithm <code>LearningAlgorithm</code> indicating the algorithm
     *            selected by the user.
     * @param modelNet <code>ProbNet</code> Net from which take the
     *            information of the nodes and links
     * @param modelNetUse <code>boolean[]</code> use the positions of the nodes,
     *            use also the initial links or use them fixed
     * @throws NormalizeNullVectorException
     * @throws EmptyModelNetException
     * @throws LatentVariablesException 
     * @throws ProbNodeNotFoundException
     * @throws NodeNotFoundException
     */
    public LearningManager (CaseDatabase caseDatabase,
                            String algorithmName,
                            ProbNet modelNet,
                            ModelNetUse modelNetUse)
        throws NormalizeNullVectorException,
        EmptyModelNetException, LatentVariablesException
    {
        this.caseDatabase = caseDatabase;
        /* Check ModelNet is not null */
        if (modelNetUse != null && modelNetUse.isUseModelNet ())
        {
            if (modelNet == null)
            {
                throw new EmptyModelNetException ();
            }
            this.learnedNet = applyModelNet (learningAlgorithmManager.getByName (algorithmName),
                                             caseDatabase, modelNet, modelNetUse);
        }
        else
        {
            this.learnedNet = new ProbNet ();
            for (Variable variable : caseDatabase.getVariables ())
            {
                learnedNet.addProbNode (variable, NodeType.CHANCE);
            }
        }
            
        this.addElviraProperties (learnedNet);
        this.modelNetUse = modelNetUse;
    }  

    /**
     * Initialize the learning algorithm.
     */
    public void init (LearningAlgorithm learningAlgorithm)
    {
        this.learningAlgorithm = learningAlgorithm;
        learningAlgorithm.init (modelNetUse);
    }

    /**
     * Main method to launch the learning process.
     * @throws NodeNotFoundException
     * @throws NormalizeNullVectorException
     * @throws ProbNodeNotFoundException
     */
    public void learn ()
        throws NormalizeNullVectorException
    {
        learningAlgorithm.run (modelNetUse);
    }

    /**
     * Returns learned net
     * @return <code>ProbNet</code> containing learned net
     */
	public ProbNet getLearnedNet() {
		return this.learnedNet;
	}
	
    /**
     * Returns the learningAlgorithm.
     * @return the learningAlgorithm.
     */
    public LearningAlgorithm getLearningAlgorithm ()
    {
        return learningAlgorithm;
    }
    
    /**
     * Scores the associated network with the given edition.
     * @param edit <code>PNEdit</code> 
     * @return <code>double</code> score of the net with the given edition
     */
    public LearningEditMotivation getMotivation(PNEdit edit)  {
        return learningAlgorithm.getMotivation (edit);
    }
    
    /**
     * Retrieves the best edition suggested by the learning algorithm
     * @param onlyAllowedEdits
     * @param onlyPositiveEdits
     */
    public LearningEditProposal getBestEdit (boolean onlyAllowedEdits, boolean onlyPositiveEdits)
    {
        
        return this.learningAlgorithm.getBestEdit (onlyAllowedEdits, onlyPositiveEdits);        
    }
    
    /**
     * Retrieves the next best edition suggested by the learning algorithm
     * @param onlyAllowedEdits
     * @param onlyPositiveEdits
     */
    public LearningEditProposal getNextEdit (boolean onlyAllowedEdits, boolean onlyPositiveEdits)
    {
        
        return this.learningAlgorithm.getNextEdit (onlyAllowedEdits, onlyPositiveEdits);        
    }
    
    public double getNetworkScore(InferenceAlgorithm inferenceAlgorithm, ArrayList<EvidenceCase> evidence, Variable testVariable, double threshold){
    	double k = 0;
		try {
			Classification test = new Classification(inferenceAlgorithm);
	        test.init(learningAlgorithm.parametricLearning(), evidence);
			k =	 test.classify(testVariable, threshold);
		} catch (NormalizeNullVectorException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return k*100;
    }
    
    /**
     * Tells the learning algorithm to advance until the next phase
     */
    public void goToNextPhase ()
		throws NormalizeNullVectorException
    {
        this.learningAlgorithm.runTillNextPhase ();        
    }   
    
    /**
     * Retrieves whether the LearningAlgorithm is in the last phase
     */
    public boolean isLastPhase ()
    {
        return this.learningAlgorithm.isLastPhase ();        
    }       
    
    /**
     *  Applies the edit passed to the learnedNet and updates parameters
     * @param edit
     * @throws DoEditException 
     * @throws WrongCriterionException 
     * @throws NonProjectablePotentialException 
     * @throws CanNotDoEditException 
     * @throws ConstraintViolationException 
     * @throws NormalizeNullVectorException 
     */
    public void applyEdit (PNEdit edit)
        throws ConstraintViolationException,
        CanNotDoEditException,
        NonProjectablePotentialException,
        WrongCriterionException,
        DoEditException, NormalizeNullVectorException
    {
        this.learnedNet.doEdit (edit);
    }
    
    /**
     * Adds elvira properties to the learned net.
     * @param learnedNet <code>ProbNet</code> which receives the elvira
     * properties.
     */
    private void addElviraProperties(ProbNet learnedNet) 
    {
                                
        HashMap<String, String> newIO = learnedNet.additionalProperties;
        State[] defaultNodeStates = {new State("present"), new State("absent")};
        learnedNet.setDefaultStates(defaultNodeStates);
        newIO.put("hasElviraProperties", new String("yes"));
        learnedNet.additionalProperties = newIO;
    }
    
    /**
     * Adds links and constraints depending on the structure of the model net
     * and the option selected by the user.
     * @param algorithmClass 
     * @param modelNetUse use of the model net selected by the user.
     * @param modelNet structure of the net to add the constraints
     * @throws LatentVariablesException 
     * @throws ProbNodeNotFoundException
     * @throws NodeNotFoundException
     */
    private ProbNet applyModelNet (Class<? extends LearningAlgorithm> algorithmClass,
                                   CaseDatabase database,
                                   ProbNet modelNet,
                                   ModelNetUse modelNetUse) throws LatentVariablesException
    {
        ProbNet probNet = null;
        
        if(!algorithmClass.getAnnotation (LearningAlgorithmType.class).supportsLatentVariables () && 
                !database.getVariables ().containsAll (modelNet.getVariables ()))
        {
            List<Variable> latentVariables = new ArrayList<>(modelNet.getVariables ());
            latentVariables.removeAll (database.getVariables ());
            throw new LatentVariablesException(latentVariables);
        }
        
        if ( modelNetUse.isUseNodePositions () )
        {
            probNet = new ProbNet ();
            for (Variable variable : database.getVariables ())
            {
                probNet.addProbNode (variable, NodeType.CHANCE);
            }
            copyNodeInformationFromModelNet(modelNet, probNet);
        }
        if ( modelNetUse.isStartFromModelNet() )
        {
            probNet = modelNet.copy ();
        
            // If the database includes variables that are not in the model net, add them 
            for (Variable databaseVariable : database.getVariables ())
            {
                if(!probNet.containsVariable (databaseVariable.getName ()))
                {
                    probNet.addProbNode (databaseVariable, NodeType.CHANCE);
                }
            }
            
            // ModelNetworkConstraint
            try
            {
                probNet.addConstraint (new ModelNetworkConstraint (modelNetUse, modelNet), false);
            }
            catch (ConstraintViolationException e)
            {
            }
        }
        return probNet;
    }
    
    public static Set<String> getAlgorithmNames ()
    {
        return learningAlgorithmManager.getLearningAlgorithmNames ();
    }    
    
    public LearningAlgorithm getAlgorithmInstance (String name)
    {
        List<Object> parameters = new ArrayList<> ();
        parameters.add (learnedNet);
        parameters.add (caseDatabase);
        return learningAlgorithmManager.getByName (name, parameters);
    }      
    
    /**
     * Blocks edit
     * @param edit to block
     */
    public void blockEdit(PNEdit edit)
    {
    	learningAlgorithm.blockEdit(edit);
    }
    
    /**
     * Blocks edit
     * @param edit to block
     */
    public void unblockEdit(PNEdit edit)
    {
    	learningAlgorithm.unblockEdit(edit);
    }
    
	/**
	 * @return the blocked edits
	 */
	public List<PNEdit> getBlockedEdits() {
		return learningAlgorithm.getBlockedEdits();
	}   
	
    /** Given a modelNet, applies the node positions and the order of the 
     * states of the nodes of the modelNet to the nodes of the current probNet
     * @param modelNet - the modelNet to copy the node positions from
     */
    private void copyNodeInformationFromModelNet(ProbNet modelNet, ProbNet learnedNet)
    {
        ProbNode positionNode = null;
        
        /* Take the positions of the nodes */
        if(modelNet != null){
            for (ProbNode node : modelNet.getProbNodes ()){
                try {
                    positionNode = learnedNet.getProbNode (node.getVariable ().getName ());
                    if (positionNode != null){
                        positionNode.getNode ().setCoordinateX (node.getNode ().
                                getCoordinateX ());
                        positionNode.getNode ().setCoordinateY (node.getNode ().
                                getCoordinateY ());
                        
                        /* Check wether the variables are discretized or not before
                         * copying the states order. If both are discretized, they
                         * have to share the same intervals.
                         */
                        if ((positionNode.getVariable ().getVariableType () != VariableType.DISCRETIZED) &&
                        		(node.getVariable ().getVariableType () != VariableType.DISCRETIZED)){
	                        updateCases (learnedNet.getProbNodes ().indexOf (positionNode),
	                        		positionNode, node);
	                        positionNode.getVariable ().setStates (node.getVariable ().getStates ());
                        }
                    }
                } catch (ProbNodeNotFoundException e) {}
            }
        }        
    }
    
    private void updateCases (int variableIndex, ProbNode originalNode, ProbNode modelNode){
    	State state;
    	
    	for (int i = 0; i < caseDatabase.getCases ().length; i++){
    		state = originalNode.getVariable ().getStates ()[caseDatabase.getCases ()[i][variableIndex]];
    		try {
				caseDatabase.getCases ()[i][variableIndex] = modelNode.getVariable ().getStateIndex (state.getName ());
			} catch (InvalidStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace ();
			}
    	}
    }
	
}
