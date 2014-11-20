package org.openmarkov.core.gui.window.edition;

import java.util.Collection;
import java.util.PriorityQueue;

import org.openmarkov.core.gui.window.MainPanel;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.learning.core.algorithm.LearningAlgorithm;
import org.openmarkov.learning.core.util.LearningEditProposal;

public class LearningPanel extends NetworkPanel {

	protected LearningAlgorithm learningAlgorithm;
	
	public LearningPanel(ProbNet probNet, MainPanel mainPanel, LearningAlgorithm learningAlgorithm) {
		super(probNet, mainPanel);
		this.learningAlgorithm = learningAlgorithm;
	}
	
    /**
     * Sets the modification state of the network to a new value.
     * @param value new value of the modification state of the network.
     */
    public void setModified (boolean value)
    {
        super.setModified(value);
        if (value == true) {
        	boolean onlyPositiveEdits = true;
        	learningAlgorithm.updateProposedEdits(getProbNet(), true, onlyPositiveEdits);
        }
    }


}