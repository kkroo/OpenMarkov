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

import javax.swing.JComponent;


/*
 * This class will do the work when the items were activated/deselected/checked
 * @author jlgozalo
 * 
 * @version 1.0
 */
public class ToolBarItemAdapter implements ToolBarItemHandler {

	/**
	 * constructor
	 */
	public ToolBarItemAdapter() {

	}

	/**
	 * Called when a ToolBatItem(component) is activated.
	 */
	public void itemActivated(JComponent item, ActionEvent event,
								String sCommand) {

		System.out.println("que pasaaaaa");
	}

	/**
	 * Called when a ToolBatItem(component) is deselected.
	 */
	public void itemDeselected(JComponent item, ItemEvent event, String sCommand) {

	}

	/**
	 * Called when a ToolBatItem(component) is selected.
	 */
	public void itemSelected(JComponent item, ItemEvent event, String sCommand) {

	}
}