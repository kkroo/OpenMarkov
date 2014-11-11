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
package org.openmarkov.core.gui.component;


import javax.swing.JMenuItem;


/**
 * This is a convenience class to distinguish between a normal MenuItem and the
 * LastRecentFile MenuItem lines (that will help for i18n working well).
 * 
 * @author jlgozalo
 * @version 1.0 25 Jul 2009
 */
public class LastRecentFilesMenuItem extends JMenuItem {

	/**
	 * default serial id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * default constructor
	 */
	public LastRecentFilesMenuItem() {

		setName("LastRecentFilesMenuItem");
	}
}