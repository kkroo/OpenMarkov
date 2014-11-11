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
 * This interface defines the method that menus and toolbars must implement to
 * manage zoom.
 * 
 * @author jmendoza
 */
public interface ZoomMenuToolBar {

	/**
	 * This method makes that the corresponding field show the zoom value.
	 * 
	 * @param value
	 */
	public void setZoom(double value);
}
