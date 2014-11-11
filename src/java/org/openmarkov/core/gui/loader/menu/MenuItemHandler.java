/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.loader.menu;


/*
 * Interface
 */
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import javax.swing.JMenuItem;


/**
 * Menu XML uses this interface to notify client objects of MenuItem events.
 * 
 * @author jlgozalo
 * @version 1.0
 */
public interface MenuItemHandler {

	/**
	 * Called when a JMenuItem is activated.
	 */
	public void itemActivated(JMenuItem item, ActionEvent event, String sCommand);

	/**
	 * Called when a CheckboxMenuItem is deselected.
	 */
	public void itemDeselected(JMenuItem item, ItemEvent event, String sCommand);

	/**
	 * Called when a CheckboxMenuItem is selected.
	 */
	public void itemSelected(JMenuItem item, ItemEvent event, String sCommand);
}