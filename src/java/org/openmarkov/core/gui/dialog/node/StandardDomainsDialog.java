package org.openmarkov.core.gui.dialog.node;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JPanel;

import org.openmarkov.core.gui.dialog.common.OkCancelApplyUndoRedoHorizontalDialog;

/**
 * 
 * @author maryebra
 *
 */
@SuppressWarnings("serial")
public class StandardDomainsDialog extends OkCancelApplyUndoRedoHorizontalDialog {

	private JPanel standardDomainsPanel;
	
	public StandardDomainsDialog(Window owner) {
		super(owner);
		initialize();
		setLocationRelativeTo(owner);
		setResizable(true);
		pack();
		
	}
	
private void initialize() {
		
		configureComponentsPanel();
		pack();
	}

private void configureComponentsPanel() {
	
	getComponentsPanel().setLayout(new BorderLayout(5, 5));
	getComponentsPanel().add( getJPanelStandardDomains(), BorderLayout.CENTER );
	
}

public JPanel getJPanelStandardDomains() {
	
	if (standardDomainsPanel == null) {
		standardDomainsPanel = new StandardDomainPanel();
		//statesCheckBoxPanel.setLayout( new FlowLayout());
		standardDomainsPanel.setName( "jPanelStandardDomains" );
	}
	return standardDomainsPanel;

	
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
