/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.window.mdi;


/**
 * This interface defines the methods that a frame observer must implement.
 * 
 * @author jmendoza
 * @version 1.0
 * @version 1.1 jlgozalo - remove public identifier to methods (not required in
 *          an interface definition)
 */
public interface MDIListener {

	/**
	 * This method executes when a frame has been opened.
	 * 
	 * @param contentPanel
	 *            content panel of the frame that has been opened.
	 */
	void frameOpened(FrameContentPanel contentPanel);
	
	/**
	 * This method executes when a frame has been closed.
	 * 
	 * @param contentPanel
	 *            content panel of the frame that has been closed.
	 */
	void frameClosed(FrameContentPanel contentPanel);

	/**
	 * This method executes when a frame has been selected.
	 * 
	 * @param contentPanel
	 *            content panel of the frame that has been selected.
	 */
	void frameSelected(FrameContentPanel contentPanel);
	
	/**
	 * This method executes when a frame's title has changed.
	 * 
	 * @param contentPanel
	 *            content panel of the frame whose title has been changed.
	 */
	void frameTitleChanged(FrameContentPanel contentPanel, String oldName, String newName);	

	/**
	 * This method executes when a frame is going to be closed.
	 * 
	 * @param contentPanel
	 *            content panel of the frame that is trying to be closed.
	 * @return true, if the frame that contents the panel can be closed;
	 *         otherwise, false.
	 */
	boolean frameClosing(FrameContentPanel contentPanel);
}
