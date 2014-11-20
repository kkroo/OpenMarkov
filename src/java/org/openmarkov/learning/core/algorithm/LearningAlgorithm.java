/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.learning.core.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.NormalizeNullVectorException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.io.database.CaseDatabase;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;
import org.openmarkov.learning.algorithm.pc.independencetester.CrossEntropyIndependenceTester;
import org.openmarkov.learning.algorithm.pc.independencetester.IndependenceTester;
import org.openmarkov.learning.core.util.LearningEditMotivation;
import org.openmarkov.learning.core.util.LearningEditProposal;
import org.openmarkov.learning.core.util.ModelNetUse;

/**
 * Abstract learning algorithm.
 */
public abstract class LearningAlgorithm {
    
    /** Parameter for the parametric learning. */
    protected double alpha;    
    
    /** Net to learn */
    protected ProbNet probNet;
    
    /** Independence tester */
    private CrossEntropyIndependenceTester independenceTester = null;
    
    /** Case database */
    protected CaseDatabase caseDatabase;
    
    /** List of blocked edits */
    protected List<PNEdit> blockedEdits = new ArrayList<PNEdit>();  
    
    protected int phase = 0;    
    
    
    // Constructor
    /**
     * @param editionsGenerator <code>EditionsGenerator</code> The object that
     * gives the best operation in each iteration of the algorithm.
     **/
    public LearningAlgorithm (ProbNet probNet, CaseDatabase caseDatabase, double alpha)
    {
        this.probNet = probNet;
        this.caseDatabase = caseDatabase;
        this.independenceTester = new CrossEntropyIndependenceTester ();
        this.alpha = alpha;
    }
    
    /** Method invoked to run the algorithm.
     * @param modelNetUse 
     * 
     * @return <code>ProbNet</code> learned.
     * @throws NormalizeNullVectorException
     */
    public void run (ModelNetUse modelNetUse)
        throws NormalizeNullVectorException
    {
        init(modelNetUse);
        /* Main loop */
       LearningEditProposal bestEdition = getBestEdit(true,true);
        while (bestEdition != null)
        {
            step (bestEdition.getEdit ());
            bestEdition = getBestEdit (true, true);
        }
       /* Parametric Learning */
       parametricLearning();
    }
    
    /**
     * Tells the learning algorithm to advance until the next phase
     */
    public void runTillNextPhase () throws NormalizeNullVectorException
    {
    	int currentPhase = getPhase ();
    	LearningEditProposal bestEditProposal = getBestEdit (true, true);
        while ((bestEditProposal != null) && (currentPhase == getPhase ()))
        {
            step ( bestEditProposal.getEdit ());
            bestEditProposal = getBestEdit (true, true);
        }
    }
    
    /**
     * Initializes the algorithm
     * @param modelNetUse
     */
    public void init (ModelNetUse modelNetUse)
    {
        // Do nothing
    }
    
			   
    /**
     * This method returns the best edition (and its associated score)
     * that can be done to the network that is being learnt. 
     * 
     * @param onlyAllowedEditions If this parameter is true, only those editions
     * that do not provoke a ConstraintViolationException are returned
     * @param onlyPositiveEditions If this parameter is true, only those 
     * editions with a positive associated score are returned.
     * @return <code>LearningEditProposal</code> with the best edition and its score. 
     */
    public abstract LearningEditProposal getBestEdit (boolean onlyAllowedEdits, boolean onlyPositiveEdits);
    
    /**
     * This method returns the next best edition (and its associated score)
     * that can be done to the network that is being learnt. 
     * 
     * @param onlyAllowedEditions If this parameter is true, only those editions
     * that do not provoke a ConstraintViolationException are returned
     * @param onlyPositiveEditions If this parameter is true, only those 
     * editions with a positive associated score are returned.
     * @return <code>LearningEditProposal</code> with the best edition and its score. 
     */
    public LearningEditProposal getNextEdit (boolean onlyAllowedEdits, boolean onlyPositiveEdits)
    {
        return null;
    }
    
    /**
     * Calculates the score associated to the given edit.
     * @param edit <code>PNEdit</code> 
     * @return <code>LearningEditMotivation</code> motivation for the given edit
     */    
    public LearningEditMotivation getMotivation (PNEdit edit)
    {
        return null;
    }
    
    /** Takes a step in the algorithm
     * 
     * @throws java.lang.Exception
     */
    protected ProbNet step (PNEdit bestEdition) throws NormalizeNullVectorException {

    /* If there have been any improvements on the score, we update
     * the learnedNet. */
        try{
            probNet.doEdit(bestEdition);
        } catch (ConstraintViolationException ex){
            /* If the edition was not allowed (ModelNetworkconstraint)
             * the algorithm just goes through the next iteration of the
             * loop, asking the cache for the next best edition.
             */
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
        return probNet;
    }
            
    /**
     * This function creates the Potentials associated to each node,
     * normalizing the absolute frequencies of the configurations of 
     * the parents.
     * @throws NormalizeNullVectorException 
     */
    public ProbNet parametricLearning() 
            throws NormalizeNullVectorException{
        int[][] cases = caseDatabase.getCases ();
        TablePotential absoluteFrequencies;
        
        for (ProbNode node : probNet.getProbNodes()) {
            if(!node.getPotentials ().isEmpty ())
            {
                probNet.removePotential (node.getPotentials ().get (0));
            }
            absoluteFrequencies = calculateAbsoluteFrequencies(probNet, cases, node);
            for (int j = 0; j < absoluteFrequencies.getTableSize(); j++)
                absoluteFrequencies.values[j] += alpha;
            probNet.addPotential (DiscretePotentialOperations.normalize(absoluteFrequencies));
        }
        
        return probNet;
    }
    

       
    /**
     * Blocks edit
     * @param edit to block
     */
    public void blockEdit(PNEdit edit)
    {
        blockedEdits.add(edit);
    }
    
    /**
     * Blocks edit
     * @param edit to block
     */
    public void unblockEdit(PNEdit edit)
    {
        blockedEdits.remove(edit);
    }

    /**
     * @return the blockedEdits
     */
    public List<PNEdit> getBlockedEdits() {
        return blockedEdits;
    }    
    
    /**
     * Blocks edit
     * @param edit to block
     */
    public boolean isBlocked(PNEdit edit)
    {
        return blockedEdits.contains(edit);
    }    
    
    protected boolean isAllowed(PNEdit edit)
    {
        boolean isAllowed = true;
        try
        {
            //Announce edit to check whether it is allowed or not
            try
            {
                edit.getProbNet ().getPNESupport ().announceEdit (edit);
            }
            catch (ConstraintViolationException e)
            {
                isAllowed = false;
            }
        }
        catch (Exception e1)
        {
            e1.printStackTrace ();
        }       
        return isAllowed;
    }    
    
    public int getPhase ()
    {
        return phase;
    }

    /**
     * Retrieves whether the LearningAlgorithm is in the last phase. 
     * True by default
     */    
    public boolean isLastPhase ()
    {
        return true;
    }
    
    /**
     * Calculate the absolute frequencies in the database of each of the
     * configurations of the given node and its parents and a given extra
     * parent.
     * @param node <code>ProbNode</code> whose frequencies we want to calculate.
     * @return <code>TablePotential</code> with the absolute frequencies in
     * the database of each of the configurations of the given node and its
     * parents and a given extra parent.
     */
    private TablePotential calculateAbsoluteFrequencies (ProbNet probNet,
                                                         int[][] cases,
                                                         ProbNode node)
    {
        int parentsConfigurations = 1;
        int indexOfParent = 0;
        int numParents = node.getNode ().getNumParents ();
        int[] indexesOfParents = new int[numParents];
        List<Variable> variables = new ArrayList<Variable> ();
        variables.add ((Variable) node.getVariable ());
        if (numParents == 0)
        {
            parentsConfigurations = 1;
        }
        else
        {
            for (ProbNode parent : ProbNet.getProbNodesOfNodes (node.getNode ().getParents ()))
            {
                variables.add ((Variable) parent.getVariable ());
                indexesOfParents[indexOfParent] = probNet.getProbNodes ().indexOf (probNet.getProbNode (parent.getVariable ()));
                parentsConfigurations *= ((Variable) parent.getVariable ()).getNumStates ();
                indexOfParent++;
            }
        }
        return calculateAbsoluteFreqPotential (probNet,
                                               cases,
                                               node,
                                               parentsConfigurations,
                                               variables,
                                               indexesOfParents,
                                               node.getVariable ().getNumStates ());
    }
    
    /**
     * Calculate the absolute frequencies in the database of each of the
     * configurations of the given node and its parents.
     * @param probNode <code>ProbNode</code> whose frequencies we want to 
     * calculate.
     * @param parentsConfigurations product of the number of states of the
     * parents.
     * @param variables <code>ArrayList</code> formed by the variable associated
     * to the given node and the variables associated to its parents.
     * @param indexesOfParents <code>int[]</code> indexes of the parents in the
     * probNet list of nodes.
     * @param numValues number of states of the given node
     * @return <code>TablePotential</code> with the absolute frequencies in
     * the database of each of the configurations of the given node and its
     * parents.
     */
    private TablePotential calculateAbsoluteFreqPotential (ProbNet probNet,
                                                          int[][] cases,
                                                          ProbNode probNode,
                                                          int parentsConfigurations,
                                                          List<Variable> variables,
                                                          int[] indexesOfParents,
                                                          int numValues
                                                          ) {
        TablePotential absoluteFreqPotential = new TablePotential(
                variables, PotentialRole.CONDITIONAL_PROBABILITY);
        double[] absoluteFreqs = absoluteFreqPotential.getValues();
        double iCPT;
        int iNode = probNet.getProbNodes().indexOf(
                probNet.getProbNode(probNode.getVariable())); 

        // Initialize the table
        for (int i = 0; i < parentsConfigurations * numValues; i++) {
            absoluteFreqs[i] = 0;
        }
        
        variables.remove(0);
        // Compute the absolute frequencies
        for (int i = 0; i < cases.length; i++) {
            iCPT = 0;
            int j = 0;
            for (ProbNode parent : probNet.getProbNodes(variables)) {
                iCPT = iCPT * parent.getVariable().getNumStates() + cases[i][indexesOfParents[j]];
                j++;
            }
            absoluteFreqs[numValues * ((int) iCPT) + (int) cases[i][iNode]]++;
        }
        return absoluteFreqPotential;
    }

    public void updateProposedEdits(ProbNet learnedNet, boolean onlyAllowedEdits, boolean onlyPositiveEdits)
    {
    	Collection<LearningEditProposal> proposals;
    	for (Variable variable : learnedNet.getVariables()){
    		proposals = getProposedEditsForVariable(learnedNet, variable, onlyAllowedEdits, onlyPositiveEdits);
    		learnedNet.getProbNode(variable).setProposedEdits(proposals);
    	}
    }
    
	public abstract Collection<LearningEditProposal> getProposedEditsForVariable(
			ProbNet learnedNet, Variable head, boolean onlyAllowedEdits,
			boolean onlyPositiveEdits);
}
