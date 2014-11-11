/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.treeadd;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;

import org.openmarkov.core.gui.dialog.common.OkCancelApplyUndoRedoHorizontalDialog;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;

/**
 * @author myebra
 *
 */
@SuppressWarnings("serial")
public class AddVariablesDialog extends OkCancelApplyUndoRedoHorizontalDialog {

	private AddVariablesCheckBoxPanel variableCheckBoxPanel;
	private TreeADDBranch treeADDBranch;
	private TreeADDPotential parentTreeADD;
	
	public AddVariablesDialog(Window owner, TreeADDBranch treeADDBranch, TreeADDPotential parentTreeADD) {
		super(owner);
		this.treeADDBranch = treeADDBranch;
		this.parentTreeADD = parentTreeADD;
		initialize();
		setLocationRelativeTo(owner);
		setMinimumSize(new Dimension( 100, 100 ));
		setResizable(true);
		pack();
		
	}
	private void initialize() {

		configureComponentsPanel();
		pack();
	}
	/**
	 * Sets up the panel where all components, except the buttons of the buttons
	 * panel, will be appear.
	 */
	private void configureComponentsPanel() {
		/*dialogStringResource =
				StringResourceLoader.getUniqueInstance().getBundleDialogs();
		messageStringResource =
				StringResourceLoader.getUniqueInstance().getBundleMessages();
		setTitle(dialogStringResource
				.getString("NodePotentialDialog.Title.Label"));*/
		getComponentsPanel().setLayout(new BorderLayout(5, 5));
		getComponentsPanel().add( getJPanelVariables(), BorderLayout.CENTER );
		
	}
	
	protected AddVariablesCheckBoxPanel getJPanelVariables() {
	
		if (variableCheckBoxPanel == null) {
			variableCheckBoxPanel = new AddVariablesCheckBoxPanel(treeADDBranch, parentTreeADD);
			//variableCheckBoxPanel.setLayout( new FlowLayout() );
			variableCheckBoxPanel.setName( "jPanelPotentialVariables" );
			
		}
		return variableCheckBoxPanel;

		
	}
	public int requestValues() {
		
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
		
		return true;
	}

	/**
	 * This method carries out the actions when the user press the Cancel button
	 * before hide the dialog.
	 */
	protected void doCancelClickBeforeHide() {

	}

}
