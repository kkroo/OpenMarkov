package org.openmarkov.core.gui.window.edition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

<<<<<<< HEAD

=======
import org.openmarkov.core.exception.NormalizeNullVectorException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
>>>>>>> origin/or_testing
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.gui.window.MainPanel;
import org.openmarkov.core.inference.InferenceAlgorithm;
import org.openmarkov.core.io.database.CaseDatabase;
<<<<<<< HEAD
import org.openmarkov.core.gui.window.MainPanel;
=======
import org.openmarkov.core.model.network.EvidenceCase;
>>>>>>> origin/or_testing
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.learning.core.algorithm.LearningAlgorithm;
import org.openmarkov.learning.core.util.LearningEditProposal;
import org.openmarkov.learning.evaluation.Classification;

public class LearningPanel extends NetworkPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected LearningAlgorithm learningAlgorithm;
	
	public LearningPanel(ProbNet probNet, MainPanel mainPanel, LearningAlgorithm learningAlgorithm) {
		super(probNet, mainPanel);
		this.learningAlgorithm = learningAlgorithm;
		recomputeProposedEdits();

	}
	
    public LearningPanel(ProbNet probNet, CaseDatabase cases,
			MainPanel mainPanel, LearningAlgorithm learningAlgorithm) {
		super(probNet, cases, mainPanel);
		this.learningAlgorithm = learningAlgorithm;
		recomputeProposedEdits();
	}

	
    /**
     * Sets the modification state of the network to a new value.
     * @param value new value of the modification state of the network.
     */
    public void setModified (boolean value)
    {
        super.setModified(value);
        if (value == true) {

        	if (probNet.getLookAheadButton() == false) {
        		recomputeProposedEdits();
        	}
        }
    }
    

    private void recomputeProposedEdits()
    {
    	boolean onlyPositiveEdits = true;
    	learningAlgorithm.updateProposedEdits(getProbNet(), true, onlyPositiveEdits);
    	getEditorPanel().getVisualNetwork().resetMotivationExtrema();
    	calculateIndependence();
<<<<<<< HEAD
=======
//    	InferenceAlgorithm inferenceAlgorithm;
//    	double k = 0;
//		try {
//			inferenceAlgorithm = this.getEditorPanel().getInferenceManager().getDefaultInferenceAlgorithm(getProbNet());
//	        Classification test = new Classification(inferenceAlgorithm);
//	        ArrayList<EvidenceCase> evidence = getEditorPanel().getEvidence();
//	        test.init(learningAlgorithm.parametricLearning(), evidence);
//			k =	 test.classify(probNet.getVariable("TuberculosisOrCancer"), 0.5);
//		} catch (NotEvaluableNetworkException | ProbNodeNotFoundException | NormalizeNullVectorException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		System.out.println(k);


>>>>>>> origin/or_testing
    }
    	
    public LearningAlgorithm getLearningAlgorithm() {
    	return this.learningAlgorithm;
    }


}
