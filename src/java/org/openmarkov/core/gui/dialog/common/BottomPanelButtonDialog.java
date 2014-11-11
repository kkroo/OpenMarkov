/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.common;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;


/**
 * This class implements a dialog that has a horizontal button panel in the
 * bottom of the window.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 */
public class BottomPanelButtonDialog extends DialogBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4648589019411570235L;

	/**
	 * Content pane.
	 */
	private JPanel jContentPane = null;

	/**
	 * Panel where the rest of the components, except the buttons of the bottom
	 * line are placed.
	 */
	private JPanel componentsPanel = null;

	/**
	 * Panel that contains the button panel.
	 */
	private JPanel bottomPanel = null;

	/**
	 * Panel that contains the buttons.
	 */
	private JPanel buttonsPanel = null;

	/**
	 * Constructor that invokes the superclass' constructor and initialises the
	 * instance.
	 * 
	 * @param owner
	 *            window that owns the dialog box.
	 */
	public BottomPanelButtonDialog(Window owner) {

		super(owner);

		initialize();
		setName("BottomPanelButtonDialog");
	}

	/**
	 * This method initialises this instance.
	 */
	private void initialize() {

		setResizable(false);
		setModal(true);
		setContentPane(getJContentPane());

	}

	/**
	 * This method initialises jContentPane.
	 * 
	 * @return a new content panel.
	 */
	private JPanel getJContentPane() {

		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getComponentsPanel(), BorderLayout.CENTER);
			jContentPane.add(getBottomPanel(), BorderLayout.SOUTH);
		}

		return jContentPane;

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

	/**
	 * This method initialises bottomPanel.
	 * 
	 * @return a new bottom panel.
	 */
	protected JPanel getBottomPanel() {

		if (bottomPanel == null) {
			bottomPanel = new JPanel();
			bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			bottomPanel.add(getButtonsPanel());
		}

		return bottomPanel;

	}

	/**
	 * This method initialises buttonsPanel.
	 * 
	 * @return a new panel that contains the buttons.
	 */
	protected JPanel getButtonsPanel() {

		if (buttonsPanel == null) {
			buttonsPanel = new JPanel();
			buttonsPanel.setLayout(new GridLayout(1, 0, 10, 10));
			buttonsPanel.setBorder(BorderFactory.createEmptyBorder(
				10, 10, 10, 10));
		}

		return buttonsPanel;

	}

	/**
	 * This method adds a new button to the buttons panel and a space of 10
	 * units to the right of this button.
	 * 
	 * @param button
	 *            button that will be added to the panel.
	 */
	protected void addButtonToButtonsPanel(JButton button) {

		buttonsPanel.add(button);

	}
}
