/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

/**
 * 
 */
package org.openmarkov.core.gui.loader.menu;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JMenuItem;


/**
 * @author jlgozalo
 * @version 1.0 07/12/2008
 */
public class DefaultMenuItemListener implements ActionListener, ItemListener {

	/**
	 * Dispatch check/uncheck events from CheckboxMenuItems.
	 */
	public void itemStateChanged(ItemEvent e) {

		Object oSource = e.getSource();
		if (oSource instanceof JMenuItem) {
			JMenuItem mi = (JMenuItem) oSource;
			MenuItemHandler mih =
				MenuHandlersTable.getUniqueInstance().menuitemhandlerFind(mi);
			if (mih != null) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					mih.itemSelected(mi, e, mi.getActionCommand());
				} else {
					mih.itemDeselected(mi, e, mi.getActionCommand());
				}
			}
		}
	}

	/**
	 * When a MenuItem is activated, this method finds and calls the MenuItem's
	 * MenuItemHandler.
	 */
	public void actionPerformed(ActionEvent e) {

		Object oSource = e.getSource();
		if (oSource instanceof JMenuItem) {
			JMenuItem mi = (JMenuItem) oSource;
			MenuItemHandler mih =
				MenuHandlersTable.getUniqueInstance().menuitemhandlerFind(mi);
			if (mih != null) {
				mih.itemActivated(mi, e, mi.getActionCommand());
			}
		}
	}

	/**
	 * Default constructor
	 */
	public DefaultMenuItemListener() {

	}

}
