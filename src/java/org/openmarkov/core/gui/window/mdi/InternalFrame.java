/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.window.mdi;

import java.awt.BorderLayout;
import java.util.Date;
import java.util.HashSet;

import javax.swing.JInternalFrame;
import javax.swing.WindowConstants;

import org.openmarkov.core.gui.loader.element.OpenMarkovLogoIcon;
import org.openmarkov.core.gui.localize.StringDatabase;

/**
 * Class that represents an internal frame.
 * @author jmendoza
 * @version 1.0
 * @version 1.1 jlgozalo - sets the class to public
 */
public class InternalFrame extends JInternalFrame
    implements
        FrameContentPanelContainer,
        Comparable<InternalFrame>
{
    /**
     * Static field for serializable class.
     */
    private static final long           serialVersionUID    = 6751914742634482688L;
    /**
     * Panel to show the information.
     */
    private FrameContentPanel           contentPanel        = null;
    /**
     * This variable contains the instant of creation of this internal frame. It
     * will allow to order the internal frames on the main window.
     */
    private Date                        creationInstant;
    /**
     * Set of listeners that listen to the frame title.
     */
    private HashSet<FrameTitleListener> frameTitleListeners = new HashSet<FrameTitleListener> ();

    /**
     * This is the default constructor.
     * @param newContentPanel content panel of the frame.
     */
    public InternalFrame (FrameContentPanel newContentPanel)
    {
        contentPanel = newContentPanel;
        initialize ();
        creationInstant = new Date ();
    }

    /**
     * This method configures, by default, the internal frame.
     */
    private void initialize ()
    {
        setDefaultCloseOperation (WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable (true);
        setMaximizable (true);
        setIconifiable (true);
        setClosable (true);
        this.setFrameIcon (OpenMarkovLogoIcon.getUniqueInstance ().getOpenMarkovLogoIcon16 ());
        setLayout (new BorderLayout ());
        setContentPane (contentPanel);
        contentPanel.setFrameContentPanelContainer (this);
    }

    /**
     * Returns the instant of creation of this object.
     * @return instant of creation.
     */
    public Date getCreationInstant ()
    {
        return creationInstant;
    }

    /**
     * Compares two objects and determines which is greater according to their
     * creation date-
     * @param o object to compare.
     * @return -1 if this object is less than o; 1 if it is greater; or 0 if it
     *         is equals than o.
     */
    public int compareTo (InternalFrame o)
    {
        return creationInstant.compareTo (o.getCreationInstant ());
    }

    /**
     * This method allows to an object to be registered as a listener.
     * @param l listener to set as the registered listener.
     */
    public void addFrameTitleListener (FrameTitleListener l)
    {
        frameTitleListeners.add (l);
    }

    /**
     * Notifies to the registered listener that the title has changed.
     */
    private void notifyTitleChanged (String oldTitle, String newTitle)
    {
        for (FrameTitleListener listener : frameTitleListeners)
        {
            listener.titleChanged (this, oldTitle, newTitle);
        }
    }

    /**
     * Sets the JInternalFrame title and notifies the change.
     * @param newTitle the string to display in the title bar.
     */
    @Override
    public void setTitle (String newTitle)
    {
        String oldTitle = getTitle ();
        newTitle = (newTitle == null) ? StringDatabase.getUniqueInstance ().getString ("InternalFrame.Title.Label")
                                     : newTitle;
        super.setTitle (newTitle);
        notifyTitleChanged (oldTitle, newTitle);
    }
}
