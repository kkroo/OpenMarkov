/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.menutoolbar.toolbar;


import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;

import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.gui.menutoolbar.common.MenuToolBarBasic;
import org.openmarkov.core.gui.menutoolbar.common.MenuToolBarBasicImpl;




/**
 * This class is used to set the common features of all toolbars of the
 * application.
 * 
 * @author jmendoza
 */
public abstract class ToolBarBasic extends JToolBar implements MenuToolBarBasic {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3038855074348761900L;


	/**
     * Suffix to retrieve tooltip strings from a string resource.
     */
    protected String STRING_TOOLTIP_SUFFIX = ".ToolTip.Label";
    
    /**
     * String database 
     */
    protected StringDatabase stringDatabase = StringDatabase.getUniqueInstance ();    
    
	/**
	 * Object that listen to the user's actions.
	 */
	protected ActionListener listener;

	/**
	 * This method initialises this instance.
	 * 
	 * @param newListener
	 *            listener that listen to the user's actions.
	 */
	public ToolBarBasic(ActionListener newListener) {

		super();
		listener = newListener;
		initialize();
	}

	/**
	 * This method initialises this instance.
	 */
	private void initialize() {

		setFloatable(false);
		setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		setOrientation(JToolBar.HORIZONTAL);
		setRollover(false);
	}

	/**
	 * Returns the component that correspond to an action command.
	 * 
	 * @param actionCommand
	 *            action command that identifies the component.
	 * @return a components identified by the action command.
	 */
	protected abstract JComponent getJComponentActionCommand(
																String actionCommand);

	/**
	 * Enables or disabled an option identified by an action command.
	 * 
	 * @param actionCommand
	 *            action command that identifies the option.
	 * @param b
	 *            true to enable the option, false to disable.
	 */
	public void setOptionEnabled(String actionCommand, boolean b) {

		MenuToolBarBasicImpl.setOptionEnabled(
			getJComponentActionCommand(actionCommand), b);
	}

	/**
	 * Selects or unselects an option identified by an action command. Only
	 * selects or unselects the components that are AbstractButton.
	 * 
	 * @param actionCommand
	 *            action command that identifies the option.
	 * @param b
	 *            true to select the option, false to unselect.
	 */
	public void setOptionSelected(String actionCommand, boolean b) {

		MenuToolBarBasicImpl.setOptionSelected(
			getJComponentActionCommand(actionCommand), b);
	}

	/**
	 * Adds a text to the label of an option identified by an action command.
	 * Only adds a text to the components that are AbstractButton.
	 * 
	 * @param actionCommand
	 *            action command that identifies the option.
	 * @param text
	 *            text to add to the label of the options. If null, nothing is
	 *            added.
	 */
	public void addOptionText(String actionCommand, String text) {

	}
	/**
	 * changes a text to the label of an option identified by an action command.
	 * 
	 * @param actionCommand
	 *            action command that identifies the option.
	 * @param text
	 *            text to set to Item. If null, nothing is
	 *            added.
	 */
	public void setText(String actionCommand, String text) {

	}

}

