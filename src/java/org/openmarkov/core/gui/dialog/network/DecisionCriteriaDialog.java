package org.openmarkov.core.gui.dialog.network;

import java.awt.Window;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.openmarkov.core.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.StringWithProperties;

@SuppressWarnings("serial")
public class DecisionCriteriaDialog extends OkCancelHorizontalDialog{

	private DecisionCriteriaTablePanel decisionCriteriaTablePanel;

	private JPanel componentsPanel;
	
	private ProbNet probNet;
	
	public DecisionCriteriaDialog(Window owner, ProbNet probNet, boolean newElement) {
		super(owner);
		this.probNet = probNet;

		probNet.getPNESupport().setWithUndo(true);
		probNet.getPNESupport().openParenthesis();
		initialize();
		setName("DecisionCriteriaDialog");
		setLocationRelativeTo(owner);
		pack();
	}

	/**
	 * This method configures the dialog box.
	 */
	private void initialize() {

		setTitle(stringDatabase.getString("DecisionCriteria.Title.Label"));
		configureComponentsPanel();
		pack();
	}

	private void configureComponentsPanel() {

		getComponentsPanel().add(getDecisionCriteriaPanel());
		//setFieldFromProperties(probNet);
	}
	/**
	 * This method initialises componentsPanel.
	 * 
	 * @return a new components panel.
	 */
	protected JPanel getComponentsPanel() {

		if (componentsPanel == null) {
			componentsPanel = new JPanel();
		}

		return componentsPanel;

	}

	private DecisionCriteriaTablePanel getDecisionCriteriaPanel() {
		if (decisionCriteriaTablePanel == null) {
			String[] columnNames = {"Key", "DecisionCriteria"};
			decisionCriteriaTablePanel = new DecisionCriteriaTablePanel(columnNames, probNet);
			decisionCriteriaTablePanel.setName("DecisionCriteriaPanel");
			decisionCriteriaTablePanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		}

		return decisionCriteriaTablePanel;

	}
	
	public void setFieldFromProperties (ProbNet probNet) {
		 
		// StringsWithProperties agents = probNet.getAgents();
		 List<StringWithProperties> decisionCriteria = probNet.getDecisionCriteria();
		if (decisionCriteria != null) {
			Object [][] data = new Object [decisionCriteria.size()][1];
			for (int i = 0; i < decisionCriteria.size(); i++) {
				data[i][0] = decisionCriteria.get(i).getString();
			}
			 //initializing data structure for the table model
			getDecisionCriteriaPanel().setData(data);
			// initializing data structure for supervising data order in GUI 
			getDecisionCriteriaPanel().setDataTable(data);
		 }
	}
	 
	 public int requestValues() {
		 setFieldFromProperties(probNet);
	       setVisible(true);
	       return selectedButton;
	    }
		/**
		 * This method carries out the actions when the user press the Ok button
		 * before hide the dialog.
		 * 
		 * @return true if the dialog box can be closed.
			 */
		protected boolean doOkClickBeforeHide() {
			probNet.getPNESupport().closeParenthesis();
			return true;
		}

		/**
		 * This method carries out the actions when the user press the Cancel button
		 * before hide the dialog.
		 */
		protected void doCancelClickBeforeHide() {
			probNet.getPNESupport().closeParenthesis();
			//TODO PNESupport must support more depth levels parenthesis 
			//As current performance edits from NetworkAgentsPanel only be undone when cancel
			//NodesPropertiesDialog
			for (int i = getDecisionCriteriaPanel().getEdits().size()-1; i >=0; i--) {
				getDecisionCriteriaPanel().getEdits().get(i).undo();
			}
			
		}


}
