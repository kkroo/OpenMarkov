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

import javax.swing.JComponent;


/**
 * Menu XML uses this interface to notify client objects of ToolBar Component
 * events.
 * 
 * @author jlgozalo
 * @version 1.0
 */
public interface ToolBarItemHandler {

	/**
	 * Called when a ToolBarItem is activated.
	 */
	public void itemActivated(JComponent item, ActionEvent event,
								String sCommand);

	/**
	 * Called when a ToolBarItem is deselected.
	 */
	public void itemDeselected(JComponent item, ItemEvent event, String sCommand);

	/**
	 * Called when a ToolBarItem is selected.
	 */
	public void itemSelected(JComponent item, ItemEvent event, String sCommand);
}