/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.window.mdi;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.HashSet;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import org.openmarkov.core.gui.localize.StringDatabase;

/**
 * This class encapsulates the MultiDocument Interface in order to it is
 * transparent and easily replaced by another MDI, as JTabbedPane.
 * @author jmendoza
 * @version 1.0 jmendoza
 * @version 1.1 jlgozalo Minor changes to comments and all variables initialized
 */
public class MDI extends JPanel
    implements
        FrameTitleListener,
        InternalFrameListener,
        ActionListener
{
    /**
     * Static field for serializable class.
     */
    private static final long    serialVersionUID = 8696128332066743296L;
    /**
     * Object that represents the menu 'Window'.
     */
    private MDIMenu              mdiMenu          = null;
    /**
     * Main object of the MDI.
     */
    private DesktopPane          desktopPane      = null;
    /**
     * Listener that listen to the MDI.
     */
    private HashSet<MDIListener> mdiListeners     = new HashSet<MDIListener> ();

    /**
     * This is the default constructor. Beside initializing this object, it
     * saves a reference to the menu that is dependent of the multi-document
     * environment.
     * @param menu menu where put the MDI options.
     */
    public MDI (JMenu menu)
    {
        // super();
        initialize ();
        mdiMenu = new MDIMenu (menu, this);
    }

    /**
     * This method initialises this
     */
    private void initialize ()
    {
        // Dimension size =
        // Toolkit.getDefaultToolkit().getScreenSize().getSize();
        this.setSize (300, 200);
        setLayout (new BorderLayout ());
        add (getDesktopPane (), BorderLayout.CENTER);
    }

    /**
     * This method initialises desktopPane.
     * @return a new desktop pane.
     */
    private DesktopPane getDesktopPane ()
    {
        if (desktopPane == null)
        {
            desktopPane = new DesktopPane ();
        }
        return desktopPane;
    }

    // ESCA-JAVA0126: Method declares exception that is not checked
    /**
     * This method creates a new frame.
     * @param newContentPanel content panel of the frame.
     * @throws UnsupportedOperationException if selection is vetoed.
     */
    public void createNewFrame (FrameContentPanel newContentPanel, boolean maximized)
        throws UnsupportedOperationException
    {
        InternalFrame frame = null;
        frame = (InternalFrame) desktopPane.createNewInternalFrame (newContentPanel);
        frame.addInternalFrameListener (this);
        frame.addFrameTitleListener (this);
        frame.setTitle (newContentPanel.getTitle ());
        frame.setBounds ((maximized) ? desktopPane.getBounds () : newContentPanel.getBounds ());
        frame.setVisible (true);
        try
        {
            frame.setSelected (true);
        }
        catch (PropertyVetoException e)
        {
            throw new UnsupportedOperationException (
                                                     StringDatabase.getUniqueInstance ().getString ("SelectionVetoed.Text.Label"));
        }
    }

    public void createNewFrame (FrameContentPanel newContentPanel)
        throws UnsupportedOperationException
    {
        createNewFrame (newContentPanel, true);
    }

    /**
     * Closes the current frame and selects the previous one (if exists).
     */
    public void closeCurrentFrame ()
    {
        try
        {
            desktopPane.closeCurrentInternalFrame ();
        }
        catch (UnsupportedOperationException e)
        {
            System.err.println (e.getMessage ());
        }
    }

    /**
     * Returns the content panel of the actual frame.
     * @return the content panel of the selected frame.
     */
    public FrameContentPanel getCurrentPanel ()
    {
        return (FrameContentPanel) desktopPane.getSelectedFrame ().getContentPane ();
    }

    /**
     * Returns the number of open frames.
     * @return number of open frames.
     */
    public int getOpenFramesNumber ()
    {
        return desktopPane.getInternalFramesNumber ();
    }

    /**
     * This method allows to an object to be registered as frame state listener.
     * @param l listener to set as the registered listener.
     */
    public void addFrameStateListener (MDIListener l)
    {
        mdiListeners.add (l);
    }

    /**
     * Notifies to all registered observers that a frame has been closed.
     * @param frame frame that has been closed.
     */
    private void notifyFrameClosed (JInternalFrame frame)
    {
        for (MDIListener listener : mdiListeners)
        {
            listener.frameClosed ((FrameContentPanel) frame.getContentPane ());
        }
    }

    /**
     * Notifies to the registered listener that a frame is going to be closed.
     * @param frame frame that is going to be closed.
     * @return true if the frame can be closed.
     */
    private boolean notifyFrameClosing (JInternalFrame frame)
    {
        boolean canBeClosed = true;
        for (MDIListener listener : mdiListeners)
        {
            canBeClosed = listener.frameClosing ((FrameContentPanel) frame.getContentPane ());
        }
        return canBeClosed;
    }

    /**
     * Notifies to the registered listener that a frame has been selected.
     * @param frame frame that has been closed.
     */
    private void notifyFrameSelected (JInternalFrame frame)
    {
        for (MDIListener listener : mdiListeners)
        {
            listener.frameSelected ((FrameContentPanel) frame.getContentPane ());
        }
    }

    /**
     * Notifies to the registered listener that the title of a frame has
     * changed.
     * @param frame frame that has been closed.
     */
    private void notifyFrameTitleChanged (JInternalFrame frame, String oldName, String newName)
    {
        for (MDIListener listener : mdiListeners)
        {
            listener.frameTitleChanged ((FrameContentPanel) frame.getContentPane (), oldName,
                                        newName);
        }
    }

    /**
     * Notifies to the registered listener that a frame has been opened
     * @param frame frame that has been opened.
     */
    private void notifyFrameOpened (JInternalFrame frame)
    {
        for (MDIListener listener : mdiListeners)
        {
            listener.frameOpened ((FrameContentPanel) frame.getContentPane ());
        }
    }

    /**
     * This method carries out the actions when an frame is going to be closed.
     * If the frame is closed, notify to all observers this event.
     * @param e event information.
     */
    public void internalFrameClosing (InternalFrameEvent e)
    {
        JInternalFrame frame = e.getInternalFrame ();
        try
        {
            desktopPane.selectFrame (frame);
            if (notifyFrameClosing (frame))
            {
                desktopPane.closeCurrentInternalFrame ();
            }
        }
        catch (UnsupportedOperationException exc)
        {
            System.err.println (exc.getMessage ());
        }
    }

    /**
     * When a internal frame has been opened, a new menu item, whose text is the
     * title of the new internal frame, is added to the window menu. When the
     * user selects this menu item, then the internal frame is selected.
     * @param e event information.
     */
    public void internalFrameOpened (InternalFrameEvent e)
    {
        JInternalFrame frame = e.getInternalFrame ();
        if (desktopPane.getInternalFramesNumber () > 0)
        {
            mdiMenu.enableMenuItems ();
        }
        mdiMenu.addPanelMenuItem ((JPanel) frame.getContentPane (), frame.getTitle ());
        notifyFrameOpened (frame);
    }

    /**
     * When a internal frame is selected, all listener are informed and the menu
     * item associated with this internal frame is selected.
     * @param e event information.
     */
    public void internalFrameActivated (InternalFrameEvent e)
    {
        JInternalFrame frame = e.getInternalFrame ();
        notifyFrameSelected (frame);
        mdiMenu.selectMenuItemByPanel ((JPanel) frame.getContentPane ());
    }

    /**
     * When a internal frame has been closed, the menu item associated with it
     * is removed from the window menu.
     * @param e event information.
     */
    public void internalFrameClosed (InternalFrameEvent e)
    {
        JInternalFrame frame = e.getInternalFrame ();
        mdiMenu.removePanelMenuItem ((JPanel) frame.getContentPane ());
        if (desktopPane.getInternalFramesNumber () == 0)
        {
            mdiMenu.disableMenuItems ();
        }
        notifyFrameClosed (frame);
    }

    /**
     * Default implementation. This method does nothing.
     * @param e event information.
     */
    public void internalFrameDeiconified (InternalFrameEvent e)
    {
    }

    /**
     * Default implementation. This method does nothing.
     * @param e event information.
     */
    public void internalFrameDeactivated (InternalFrameEvent e)
    {
    }

    /**
     * Default implementation. This method does nothing.
     * @param e event information.
     */
    public void internalFrameIconified (InternalFrameEvent e)
    {
    }

    /**
     * This method listens to the user actions on the main menu.
     * @param e menu event information.
     */
    public void actionPerformed (ActionEvent e)
    {
        String actionCommand = e.getActionCommand ();
        try
        {
            if (actionCommand.equals (MDIMenu.WINDOW_MINIMIZEALL_MENUITEM))
            {
                desktopPane.minimizeAll ();
            }
            else if (actionCommand.equals (MDIMenu.WINDOW_RESTOREALL_MENUITEM))
            {
                desktopPane.restoreAll ();
            }
            else if (actionCommand.equals (MDIMenu.WINDOW_CASCADE_MENUITEM))
            {
                desktopPane.cascade ();
            }
            else if (actionCommand.equals (MDIMenu.WINDOW_MOSAIC_MENUITEM))
            {
                desktopPane.mosaic ();
            }
            else if (actionCommand.equals (MDIMenu.WINDOW_PREVIOUS_MENUITEM))
            {
                desktopPane.previous ();
            }
            else if (actionCommand.equals (MDIMenu.WINDOW_NEXT_MENUITEM))
            {
                desktopPane.next ();
            }
            else
            {
                desktopPane.selectFrame (mdiMenu.getPanelByMenuItem ((JCheckBoxMenuItem) e.getSource ()));
            }
        }
        catch (UnsupportedOperationException exc)
        {
            System.err.println (exc.getMessage ());
        }
    }

    /**
     * This method executes when the title of a frame has changed.
     * @param frame frame whose title has been changed.
     */
    public void titleChanged (JInternalFrame frame, String oldTitle, String newTitle)
    {
        FrameContentPanel panel = (FrameContentPanel) frame.getContentPane ();
        mdiMenu.modifyPanelMenuItem (panel, panel.getTitle ());
        notifyFrameTitleChanged (frame, oldTitle, newTitle);
    }

    /**
     * This method makes a panel to be shown at first plane.
     * @param panel panel to be shown at first plane.
     */
    public void selectFrame (JPanel panel)
    {
        try
        {
            desktopPane.selectFrame (panel);
        }
        catch (UnsupportedOperationException exc)
        {
            System.err.println (exc.getMessage ());
        }
    }

    public Container getFrameByTitle (String title)
    {
        InternalFrame internalFrame = (InternalFrame) desktopPane.getFrameByTitle (title);
        return (internalFrame != null) ? internalFrame.getContentPane () : null;
    }

    public JInternalFrame[] getFrames ()
    {
        return desktopPane.getAllFrames ();
    }
}
