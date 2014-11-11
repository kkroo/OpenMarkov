/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.loader.menu;


import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import javax.swing.JFrame;
import javax.swing.JMenuItem;


/*
 * This class will do the work when the items were activated/deselected/checked
 * @author jlgozalo
 * 
 * @version 1.0
 */
public class MenuItemAdapter implements MenuItemHandler {

	/**
	 * the parent Frame where this menu item is located
	 */
	public JFrame aParentFrame = null;

	/**
	 * constructor
	 */
	public MenuItemAdapter() {

	}

	/**
	 * Called when a JMenuItem is activated.
	 */
	public void itemActivated(JMenuItem item, ActionEvent event, String sCommand) {

		System.out.println("Item activado " + item.getName() + " y evento "
			+ event.toString());
	}

	/**
	 * Called when a CheckboxMenuItem is deselected.
	 */
	public void itemDeselected(JMenuItem item, ItemEvent event, String sCommand) {

	}

	/**
	 * Called when a CheckboxMenuItem is selected.
	 */
	public void itemSelected(JMenuItem item, ItemEvent event, String sCommand) {

	}
}