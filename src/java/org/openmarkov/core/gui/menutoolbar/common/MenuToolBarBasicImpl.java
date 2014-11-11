/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.menutoolbar.common;


import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;


/**
 * This class implements the methods to modify the state and texts of the
 * components of the menus and toolbars.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 */
public class MenuToolBarBasicImpl {

	/**
	 * private constructor for a class with only static members
	 */
	private MenuToolBarBasicImpl() {

	}

	/**
	 * Enables or disabled a component.
	 * 
	 * @param component
	 *            component to be enabled or disabled.
	 * @param b
	 *            true to enable the component, false to disable.
	 */
	public static void setOptionEnabled(JComponent component, boolean b) {

		if (component != null) {
			component.setEnabled(b);
			if (!b) {
				clearSelection(component);
			}
		}

	}

	/**
	 * This method clears the selection of a button group if all the elements in
	 * it are disabled. This operation is carried out only if the component is
	 * an instance of AbstractButton, its model is an instance of
	 * JToggleButton.ToggleButtonModel and it belongs to a button group.
	 * 
	 * @param component
	 *            component whose group will be processed.
	 */
	private static void clearSelection(JComponent component) {

		ButtonModel model = null;
		ButtonGroup group = null;
		boolean enabled = false;
		Enumeration<AbstractButton> elements = null;

		if (component instanceof AbstractButton) {
			model = ((AbstractButton) component).getModel();
			if (model instanceof JToggleButton.ToggleButtonModel) {
				group = ((JToggleButton.ToggleButtonModel) model).getGroup();
				if (group != null) {
					elements = group.getElements();
					while (!enabled && elements.hasMoreElements()) {
						enabled = elements.nextElement().isEnabled();
					}
					if (!enabled) {
						group.clearSelection();
					}
				}
			}
		}

	}

	/**
	 * Selects or unselects a component. Only selects or unselects the component
	 * if it is an instance of AbstractButton.
	 * 
	 * @param component
	 *            component to be selected or unselected.
	 * @param b
	 *            true to select the component, false to unselect.
	 */
	public static void setOptionSelected(JComponent component, boolean b) {

		if (component != null) {
			if (component instanceof AbstractButton) {
				((AbstractButton) component).setSelected(b);
			}
		}

	}

	/**
	 * Adds a text to the default label of a component. Only adds a text to the
	 * component if it is an instance of AbstractButton.
	 * 
	 * @param component
	 *            component whose text is going to be modified.
	 * @param defaultLabel
	 *            default label of the component.
	 * @param text
	 *            text to add to the label of the options. If null, nothing is
	 *            added.
	 */
	public static void addOptionText(JComponent component, String defaultLabel,
										String text) {

		AbstractButton abstractButton = null;
		String newText = "";

		if (component != null) {
			if (component instanceof AbstractButton) {
				abstractButton = ((AbstractButton) component);
				newText = defaultLabel;
				newText =
					((newText == null) ? "" : newText) + " "
						+ ((text == null) ? "" : text);
				abstractButton.setText(newText);
			}
		}

	}
	/**
	 * Changes the caption to menu item. 
	 * 
	 * @param component
	 *            component whose text is going to be modified.
	 * @param text
	 *            The new text to set.
	 */
	public static void setText(JComponent component, String newCaption) {
		if (component != null) {
			if (component instanceof JMenuItem) {
				JMenuItem jMenuItem = ((JMenuItem) component);
				if (newCaption != null ){
					jMenuItem.setText(newCaption);
				}
			}
		}

	}
}
