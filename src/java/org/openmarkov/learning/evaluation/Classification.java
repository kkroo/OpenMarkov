package org.openmarkov.learning.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NoFindingException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.InferenceAlgorithm;
import org.openmarkov.core.inference.annotation.InferenceManager;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.learning.algorithm.scoreAndSearch.cache.Cache;

public class Classification implements UndoableEditListener, PNUndoableEditListener{
	
    /** Net we are testing */
    protected ProbNet probNet;   
    
    /** Case database we are using for evaluation*/
    protected List<EvidenceCase> evidence;      
    
    /** Current classification */
    protected double cachedScore = Double.NEGATIVE_INFINITY;

	protected InferenceAlgorithm inferenceAlgorithm;

       
    
    // Constructor
    /**
     */
    public Classification(InferenceAlgorithm inferenceAlgorithm) {
    	this.inferenceAlgorithm = inferenceAlgorithm;
    }
    
    //Methods
    public void init(ProbNet probNet, List<EvidenceCase> evidence)
    {
        this.probNet = probNet;
        this.evidence = evidence;
        probNet.getPNESupport ().addUndoableEditListener (this);
    }

	@Override
	public void undoableEditHappened(UndoableEditEvent e) {
		// TODO Auto-generated method stub
	}
	
	@SuppressWarnings("unused")
	public double classify(Variable testVariable, double classifyThreshold) {
		if ( cachedScore != Double.NEGATIVE_INFINITY || evidence.isEmpty()) {
			return cachedScore;
		}
		List<Variable> variablesOfInterest = new ArrayList<Variable>();
		variablesOfInterest.add(testVariable);
		cachedScore = 0.0;
		int N = 0;
		for (int i=0; i<evidence.size(); i++){
			try {
				EvidenceCase testCase = evidence.get(i);
				if (!testCase.contains(testVariable)) {
					continue;
				}
				
				EvidenceCase testCaseMissingFinding = new EvidenceCase(testCase);
				if (testCaseMissingFinding.contains(testVariable)) {
					testCaseMissingFinding.removeFinding(testVariable);
				}
				
				this.inferenceAlgorithm.setPreResolutionEvidence(testCaseMissingFinding);
				TablePotential probs = inferenceAlgorithm.getJointProbability(variablesOfInterest);
				if ( probs.contains(testVariable) ) {
					if ( probs.getProbability(testCase) > classifyThreshold ) {
						cachedScore += 1.0;
					}
					N += 1;
				}
			} catch (IncompatibleEvidenceException | UnexpectedInferenceException | NoFindingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//inferenceAlgorithm.setConditioningVariables(conditioningVariables);
		return cachedScore / N;
		
	}

	@Override
	public void undoableEditWillHappen(UndoableEditEvent event)
			throws ConstraintViolationException, CanNotDoEditException,
			NonProjectablePotentialException, WrongCriterionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void undoEditHappened(UndoableEditEvent event) {
		// TODO Auto-generated method stub
		
	}
}
