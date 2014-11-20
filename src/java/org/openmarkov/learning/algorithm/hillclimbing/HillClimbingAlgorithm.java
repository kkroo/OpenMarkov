/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/


package org.openmarkov.learning.algorithm.hillclimbing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import org.openmarkov.core.action.AddLinkEdit;
import org.openmarkov.core.action.BaseLinkEdit;
import org.openmarkov.core.action.InvertLinkEdit;
import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.action.RemoveLinkEdit;
import org.openmarkov.core.io.database.CaseDatabase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.learning.algorithm.hillclimbing.util.HillClimbingEditProposal;
import org.openmarkov.learning.algorithm.scoreAndSearch.ScoreAndSearchAlgorithm;
import org.openmarkov.learning.algorithm.scoreAndSearch.metric.Metric;
import org.openmarkov.learning.core.algorithm.LearningAlgorithmType;
import org.openmarkov.learning.core.util.LearningEditMotivation;
import org.openmarkov.learning.core.util.LearningEditProposal;
import org.openmarkov.learning.core.util.ScoreEditMotivation;

/** This class implements the basic structure of the classic hill climber 
 * algorithm.
 * @author joliva
 * @author manuel
 * @author fjdiez
 * @author ibermejo
 * @version 1.1
 * @since OpenMarkov 1.0 */
@LearningAlgorithmType (
		   name = "Hill climbing",
		   supportsLatentVariables = false
)
public class HillClimbingAlgorithm extends ScoreAndSearchAlgorithm{	

    /** Metric used as heuristic */
    protected Metric metric;
    
    /** Net we are generating edits for */
    protected ProbNet probNet;   
    
    /** List with the best edits that have been not been done by the
     * algorithm because they violate the ModelNetworkConstraint
     */
    protected List<PNEdit> lastBestEdits;    
    
    // Constructor
    /**
     * @param learnedNet <code>ProbNet</code> The graph to which the algorithm 
     * will be applied.
     * @param modelNet <code>ProbNet</code> Initial graph. 
     * @param alpha double parameter alpha
     * gives the best operation in each iteration of the algorithm.
     * @param metric for the learning.
     **/
    public HillClimbingAlgorithm (ProbNet probNet, 
                                  CaseDatabase caseDatabase,
                                  Double alpha,
                                  Metric metric)
    {
        super (probNet, caseDatabase, metric, alpha);
        this.probNet = probNet;
        this.metric = metric;
        this.lastBestEdits = new ArrayList<PNEdit> ();

        resetHistory ();        
    }

    @Override
    public LearningEditMotivation getMotivation (PNEdit edit)
    {
        // TODO: Review. Perhaps score should come from inference?
        return new ScoreEditMotivation (metric.getScore (edit));
    }

    /**
     * This method returns the best edit (and its associated score)
     * that can be done to the network that is being learnt. 
     * 
     * @param onlyAllowededits If this parameter is true, only those edits
     * that do not provoke a ConstraintViolationException are returned
     * @param onlyPositiveedits If this parameter is true, only those 
     * edits with a positive associated score are returned.
     * @return <code>LearningEditProposal</code> with the best edit and its score. 
     */    
    public LearningEditProposal getBestEdit (boolean onlyAllowedEdits,
                                     boolean onlyPositiveEdits)
    {
        resetHistory ();
        return getNextEdit (onlyAllowedEdits, onlyPositiveEdits);
    }

    /**
     * This method returns the next best edit (and its associated score)
     * that can be done to the network that is being learnt. 
     * 
     * @param onlyAllowededits If this parameter is true, only those edits
     * that do not provoke a ConstraintViolationException are returned
     * @param onlyPositiveedits If this parameter is true, only those 
     * edits with a positive associated score are returned.
     * @return <code>LearningEditProposal</code> with the best edit and its score. 
     */    
    public LearningEditProposal getNextEdit (boolean onlyAllowedEdits,
                                     boolean onlyPositiveEdits)
    {
        LearningEditProposal bestEdit = getOptimalEdit (probNet, onlyAllowedEdits, onlyPositiveEdits);
        while(bestEdit != null && isBlocked(bestEdit.getEdit()))
        {
            bestEdit = getOptimalEdit (probNet, onlyAllowedEdits, onlyPositiveEdits);
        }
        return bestEdit;
    }       

    protected void resetHistory ()
    {
        lastBestEdits.clear();
    }
    
    /**
     * Store last best edit
     * @param edit
     * @param score
     */
    protected void markEditAsConsidered (BaseLinkEdit edit)
    {
        lastBestEdits.add (edit);
    }
    
    /**
     * 
     * @param addLinkEdit
     * @return
     */
    protected boolean isEditAlreadyConsidered (BaseLinkEdit edit)
    {
        return lastBestEdits.contains (edit);
    }       

    public Collection<LearningEditProposal> getProposedEditsForVariable(ProbNet learnedNet, 
													   Variable head,
    												   boolean onlyAllowedEdits, 
    												   boolean onlyPositiveEdits)
    {
    	PriorityQueue<LearningEditProposal> proposals = new PriorityQueue<LearningEditProposal>(5, Collections.reverseOrder());
    	ProbNode headNode = learnedNet.getProbNode (head);
    	for (Variable tail : learnedNet.getVariables())
    	{
    		if(head.equals(tail))
    			continue;
            
            ProbNode tailNode = learnedNet.getProbNode (tail);
            
            LearningEditProposal proposal;
            BaseLinkEdit edit;
            double score;
            
            if (!headNode.isParent(tailNode)){
            	edit = new AddLinkEdit (learnedNet, tail, head, true);
            	evaluateEdit(proposals, edit, onlyAllowedEdits, onlyPositiveEdits);
            	
            }
            else {
            	edit = new RemoveLinkEdit ( learnedNet, tail, head, true);
            	evaluateEdit(proposals, edit, onlyAllowedEdits, onlyPositiveEdits);
            	
            	edit = new InvertLinkEdit ( learnedNet, tail, head, true);
            	evaluateEdit(proposals, edit, onlyAllowedEdits, onlyPositiveEdits);
            }
    		
    	}
    	
		return proposals;
    	
    }

    
    /**
     * Method to obtain node specific optimal edit.
     */
    
    private LearningEditProposal getNodeOptimalEdit(ProbNet learnedNet, Variable source, Variable target) {
    	double bestScore = Double.NEGATIVE_INFINITY;
    	LearningEditProposal bestEditProposal = null;
    	BaseLinkEdit bestEdit = null;
    	if (source.equals(target)) {
    		return bestEditProposal;
    	}
    	ProbNode sourceNode = learnedNet.getProbNode (source);
        ProbNode targetNode = learnedNet.getProbNode (target);
    	//AddLinkEdit
    	if (!sourceNode.isParent(targetNode) && !targetNode.isParent(sourceNode)) {
    		AddLinkEdit addLinkEdit = new AddLinkEdit (learnedNet,
                    source, target, true);
    		if (metric.getScore (addLinkEdit) > bestScore) {
    			bestScore = metric.getScore (addLinkEdit);
    			bestEdit = addLinkEdit;
    		}
    	} else {
    		//removeLinkEdit
    		RemoveLinkEdit removeLinkEdit = new RemoveLinkEdit ( learnedNet, source, target, true);
    		if (metric.getScore (removeLinkEdit) > bestScore) {
    			bestScore = metric.getScore (removeLinkEdit);
    			bestEdit = removeLinkEdit;
    		}
    		
    		//invertLinkEdit
			 InvertLinkEdit invertLinkEdit = new InvertLinkEdit (learnedNet, source, target, true);
	         if (metric.getScore (invertLinkEdit) > bestScore) {
	        	 bestScore = metric.getScore (invertLinkEdit);
	        	 bestEdit = invertLinkEdit;
	         }
    	}
    	
    	if (bestEdit != null) {
    		bestEditProposal = new HillClimbingEditProposal (bestEdit, bestScore);
    	}
    	return bestEditProposal;
    }
    
	private void evaluateEdit(PriorityQueue<LearningEditProposal> proposals, 
    						  BaseLinkEdit edit,
    						  boolean onlyAllowedEdits, 
							  boolean onlyPositiveEdits)
    {
        double score = metric.getScore (edit);
        if((!onlyAllowedEdits || isAllowed(edit)) &&
                (!onlyPositiveEdits || score > 0)  &&
                !isBlocked(edit))
        {
        	LearningEditProposal proposal = new HillClimbingEditProposal (edit, score);
        	proposals.add(proposal);
        }
    }
    /**
     * Method to obtain the edit with the highest associated score.
     * @param learnedNet net to learn.
     * @return <code>PNEdit</code> edit with the highest associated score.
     */
    private LearningEditProposal getOptimalEdit (ProbNet learnedNet,
                                                boolean onlyAllowedEdits,
                                                boolean onlyPositiveEdits)
    {
        double bestPartialScore = Double.NEGATIVE_INFINITY;
        LearningEditProposal bestEditProposal = null;
        BaseLinkEdit bestEdit = null;
        for (Variable head : learnedNet.getVariables ())
        {
            for (Variable tail : learnedNet.getVariables ())
            {
                if(!head.equals (tail))
                {
                    ProbNode headNode = learnedNet.getProbNode (head);
                    ProbNode tailNode = learnedNet.getProbNode (tail);
    
                    if(!headNode.isParent (tailNode))
                    {
                        AddLinkEdit addLinkEdit = new AddLinkEdit (learnedNet,
                                                                   tail, head, true);
                        double addScore = metric.getScore (addLinkEdit);
                        /*
                         * Check whether the score is the best to the moment and
                         * whether this edit has not been already considered
                         */
                        if ((addScore > bestPartialScore)
                            && !isEditAlreadyConsidered (addLinkEdit)
                            )
                        {
                            if((!onlyAllowedEdits || isAllowed(addLinkEdit)) &&
                                    (!onlyPositiveEdits || addScore > 0)  &&
                                    !isBlocked(addLinkEdit))
                            {
                                bestEdit = addLinkEdit;
                                bestPartialScore = addScore;
                            }
                        }
                    }
                    else
                    {
                        RemoveLinkEdit removeLinkEdit = new RemoveLinkEdit ( learnedNet, tail, head, true);
                        double removeScore = metric.getScore (removeLinkEdit);
                        /*
                         * Check whether the score is the best to the moment and
                         * whether this edit has not been already considered
                         */
                        if ((removeScore > bestPartialScore)
                            && !isEditAlreadyConsidered (removeLinkEdit))
                        {
                            if((!onlyAllowedEdits || isAllowed(removeLinkEdit)) &&
                                    (!onlyPositiveEdits || removeScore > 0) &&
                                    !isBlocked(removeLinkEdit))
                            {                           
                                bestEdit = removeLinkEdit;
                                bestPartialScore = removeScore;
                            }
                        }
                        
                        InvertLinkEdit invertLinkEdit = new InvertLinkEdit (learnedNet, tail, head, true);
                        double invertScore = metric.getScore (invertLinkEdit);
                        /*
                         * Check whether the score is the best to the moment and
                         * whether this edit has not been already considered
                         */
                        if ((invertScore > bestPartialScore)
                            && !isEditAlreadyConsidered (invertLinkEdit))
                        {
                            if((!onlyAllowedEdits || isAllowed(invertLinkEdit)) &&
                                    (!onlyPositiveEdits || invertScore > 0) &&
                                    !isBlocked(invertLinkEdit))
                            {                           
                                bestEdit = invertLinkEdit;
                                bestPartialScore = invertScore;
                            }
                        }                              
                    }
                }
            }
        }
        if(bestEdit != null)
        {
            bestEditProposal = new HillClimbingEditProposal (bestEdit, bestPartialScore);
            markEditAsConsidered(bestEdit);                               
        }
        return bestEditProposal;
    }

    
}
