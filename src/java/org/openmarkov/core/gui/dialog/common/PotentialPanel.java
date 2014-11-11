/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package org.openmarkov.core.gui.dialog.common;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.openmarkov.core.model.network.ProbNode;

@SuppressWarnings("serial")
public abstract class PotentialPanel extends JPanel
{
    private List<PanelResizeEventListener> listeners;
	/**
	 * If true, values inside the panel will not be editable
	 */
	private boolean readOnly;
	
	public PotentialPanel()
	{
	    listeners = new ArrayList<>();
	}

    /**
     * Fill the panel with the data from the node
     * @param probNode
     */
    public abstract void setData (ProbNode probNode);
    
    /**
     * Modify the node according to the changes entered by the user in the panel
     */

    /**
     * Modify the node according to the changes entered by the user in the panel
     */
    public boolean saveChanges()
    {
    	close();
    	return true;
    }
    
    
    public abstract void close();

	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * @param readOnly the readOnly to set
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	public void suscribePanelResizeEventListener(PanelResizeEventListener listener)
	{
	    listeners.add(listener);
	}
	
    public boolean unsuscribePanelResizeEventListener(PanelResizeEventListener listener)
    {
        return listeners.remove(listener);
    }
    
    public void notifyPanelResizeEventListeners()
    {
        PanelResizeEvent event  = new PanelResizeEvent(this, getSize());
        for(PanelResizeEventListener listener : listeners)
        {
            listener.panelSizeChanged(event);
        }
    }

    
}
