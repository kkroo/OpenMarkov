/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.window.edition;


/**
 * This class assists to a network panel in operations with the clipboard.
 * @author jmendoza
 * @version 1.0
 */
public class EditorPanelClipboardAssistant
{
    private SelectedContent content = null;
    /**
     * Constructor of EditorPanelClipboardAssistant
     */
    public EditorPanelClipboardAssistant ()
    {
    }

    /**
     * @param nodes nodes to copy to the clipboard.
     */
    public void copyToClipboard (SelectedContent copiedContent)
    {
        this.content = copiedContent; 
    }

    /**
     * This method retrieves the content of the clipboard
     */
    public SelectedContent paste ()
    {
        return new SelectedContent(this.content);
    }

    /**
     * This method says if there is data stored in the clipboard.
     * @return true if there is data stored in the clipboard; otherwise, false.
     */
    public boolean isThereDataStored ()
    {
        return content != null;
    }
}
