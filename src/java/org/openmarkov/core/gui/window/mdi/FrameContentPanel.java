/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.window.mdi;


import javax.swing.JPanel;


/**
 * This class represents the content pane of a frame of the multidocument
 * interface. The classes that can be set as content pane of a frame of the MDI
 * must extends this class.
 * 
 * @author jmendoza
 * @version 1.0
 */
public abstract class FrameContentPanel extends JPanel {
	public FrameContentPanel() {
	}

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 6808692603537287168L;

	/**
	 * Object that contains this panel.
	 */
	protected FrameContentPanelContainer container = null;

	/**
	 * This method allows to an object to be registered as title listener.
	 * 
	 * @param newContainer
	 *            container title listener.
	 */
	public void setFrameContentPanelContainer(FrameContentPanelContainer newContainer) {

		container = newContainer;
	}

	/**
	 * Returns the title of the content panel.
	 * 
	 * @return the title of the content panel.
	 */
	public abstract String getTitle();
	
	/**
	 * Prepares the frame for closing
	 */
	public abstract void close();

    public abstract double getZoom ();

    public abstract void setZoom (double zoom);

}
