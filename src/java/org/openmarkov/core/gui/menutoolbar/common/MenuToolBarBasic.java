/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.menutoolbar.common;


/**
 * This interface defines the methods that all menus and toolbars must
 * implement.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 * @version 1.1 jlgozalo Fixing semantic errors as this class is an interface
 */
public interface MenuToolBarBasic {

	/**
	 * Suffix that has label string resources.
	 */
	String LABEL_SUFFIX = ".Label";

	/**
	 * Suffix that has mnemonic string resources.
	 */
	String MNEMONIC_SUFFIX = ".Mnemonic";

	/**
	 * Suffix to retrieve tooltip strings from a string resource.
	 */
	String STRING_TOOLTIP_SUFFIX = ".ToolTip.Label";

	/**
	 * Enables or disabled an option identified by an action command.
	 * 
	 * @param actionCommand
	 *            action command that identifies the option.
	 * @param b
	 *            true to enable the option, false to disable.
	 */
	void setOptionEnabled(String actionCommand, boolean b);

	/**
	 * Selects or unselects an option identified by an action command. Only
	 * selects or unselects the components that are AbstractButton.
	 * 
	 * @param actionCommand
	 *            action command that identifies the option.
	 * @param b
	 *            true to select the option, false to unselect.
	 */
	void setOptionSelected(String actionCommand, boolean b);

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
	void addOptionText(String actionCommand, String text);
	
	/**
	 * Sets a text of an option identified by an action command.
	 * Only adds a text to the components that are JMenuItem.
	 * 
	 * @param actionCommand
	 *            action command that identifies the option.
	 * @param text
	 *            text to add to the label of the options. If null, nothing is
	 *            added.
	 */
	void setText(String actionCommand, String text);
}
