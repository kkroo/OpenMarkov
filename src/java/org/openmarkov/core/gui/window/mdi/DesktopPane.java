/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.window.mdi;

import java.awt.SystemColor;
import java.beans.PropertyVetoException;
import java.util.Arrays;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import org.openmarkov.core.gui.localize.StringDatabase;

/**
 * This class is used to provide a multi-frame user interface
 * @author jmendoza
 * @version 1.0
 */
public class DesktopPane extends JDesktopPane
{
    /**
     * Static field for serializable class.
     */
    private static final long serialVersionUID = 1837410272306879488L;
    /**
     * Increment of space in the X position each time a new window is created.
     */
    private static final int  X_VARIATION      = 20;
    /**
     * Increment of space in the X position each time a new window is created.
     */
    private static final int  Y_VARIATION      = 20;
    /**
     * String database
     */
    protected StringDatabase  stringDatabase   = StringDatabase.getUniqueInstance ();

    /**
     * This is the default constructor.
     */
    public DesktopPane ()
    {
        initialize ();
    }

    /**
     * This method initialises this
     */
    private void initialize ()
    {
        setBackground (SystemColor.WHITE);
        // setBackground(SystemColor.controlShadow);
    }

    /**
     * This method creates a new internal frame, selects it and then returns it.
     * @param newContentPanel content panel of the frame.
     * @return the internal frame that has been created.
     */
    public JInternalFrame createNewInternalFrame (FrameContentPanel newContentPanel)
    {
        JInternalFrame frame = null;
        int posX = 0, posY = 0;
        // newContentPanel.add(splitPane);
        frame = new InternalFrame (newContentPanel);
        frame.setBounds (posX, posY, getWidth () / 2, getHeight () / 2);
        add (frame);
        return frame;
    }

    // ESCA-JAVA0126: allows unchecked exception UnsupportedOperationException
    /**
     * Closes the current internal frame and selects the previous one (if
     * exists).
     * @throws UnsupportedOperationException if selection is vetoed.
     */
    public void closeCurrentInternalFrame ()
        throws UnsupportedOperationException
    {
        JInternalFrame frame = getSelectedFrame ();
        JInternalFrame previousFrame = getPrevious ();
        if (frame != null)
        {
            frame.dispose ();
            if (previousFrame != null)
            {
                try
                {
                    previousFrame.setSelected (true);
                }
                catch (PropertyVetoException e)
                {
                    throw new UnsupportedOperationException (
                                                             stringDatabase.getString ("SelectionVetoed.Text.Label"));
                }
            }
        }
    }

    /**
     * Returns an array containing all the internal frames ordered by their
     * creation instants.
     * @return an array containing all the internal frames ordered.
     */
    @Override
    public JInternalFrame[] getAllFrames ()
    {
        JInternalFrame[] frames = super.getAllFrames ();
        Arrays.sort (frames);
        return frames;
    }

    // ESCA-JAVA0126: allows unchecked exception UnsupportedOperationException
    /**
     * Minimizes a frame in the desktoppane.
     * @param frame frame to be minimized.
     * @throws UnsupportedOperationException if iconification is vetoed.
     */
    private void minimize (JInternalFrame frame)
        throws UnsupportedOperationException
    {
        if (!frame.isIcon ())
        {
            try
            {
                frame.setIcon (true);
            }
            catch (PropertyVetoException ex)
            {
                throw new UnsupportedOperationException (
                                                         stringDatabase.getString ("IconificationVetoed.Text.Label"));
            }
        }
    }

    // ESCA-JAVA0126: allows unchecked exception UnsupportedOperationException
    /**
     * Restores a frame in the desktoppane.
     * @param frame frame to be restored.
     * @throws UnsupportedOperationException if restoration is vetoed.
     */
    private void restore (JInternalFrame frame)
        throws UnsupportedOperationException
    {
        if (frame.isIcon ())
        {
            try
            {
                frame.setIcon (false);
            }
            catch (PropertyVetoException ex)
            {
                throw new UnsupportedOperationException (
                                                         stringDatabase.getString ("RestorationVetoed.Text.Label"));
            }
        }
    }

    // ESCA-JAVA0126: allows unchecked exception UnsupportedOperationException
    /**
     * Minimizes all the internal frames in the desktoppane.
     * @throws UnsupportedOperationException if iconification is vetoed.
     */
    public void minimizeAll ()
        throws UnsupportedOperationException
    {
        JInternalFrame[] frames = getAllFrames ();
        JInternalFrame selected = getSelectedFrame ();
        int i = frames.length;
        int l = frames.length;
        for (i = 0; i < l; i++)
        {
            minimize (frames[i]);
        }
        if (selected != null)
        {
            selectFrame (selected);
        }
    }

    // ESCA-JAVA0126: allows unchecked exception UnsupportedOperationException
    /**
     * Restores all the minimized internal frames in the desktoppane.
     * @throws UnsupportedOperationException if restoration is vetoed.
     */
    public void restoreAll ()
        throws UnsupportedOperationException
    {
        JInternalFrame[] frames = getAllFrames ();
        JInternalFrame selected = getSelectedFrame ();
        int i = frames.length;
        int l = frames.length;
        for (i = 0; i < l; i++)
        {
            restore (frames[i]);
        }
        if (selected != null)
        {
            selectFrame (selected);
        }
    }

    // ESCA-JAVA0126: allows unchecked exception UnsupportedOperationException
    /**
     * All the internal frames will be resized to the same size and will be
     * moved one on top of the other, so that the top and left sides of all
     * internal frames are visible.
     * @throws UnsupportedOperationException if restoration is vetoed.
     */
    public void cascade ()
        throws UnsupportedOperationException
    {
        final int desktopPaneWidth = getWidth ();
        final int desktopPaneHeight = getHeight ();
        final int width = desktopPaneWidth / 2;
        final int height = desktopPaneHeight / 2;
        JInternalFrame[] frames = getAllFrames ();
        JInternalFrame selected = getSelectedFrame ();
        int x = 0;
        int y = 0;
        int i = frames.length;
        int l = frames.length;
        int initialX = 0;
        int initialY = 0;
        int delta = 100;
        for (i = 0; i < l; i++)
        {
            if ((x + delta) > desktopPaneWidth)
            {
                x = 0;
                initialY += X_VARIATION;
                y = initialY;
            }
            if ((y + Y_VARIATION) > desktopPaneHeight)
            {
                y = 0;
                initialX += delta;
                x = initialX;
            }
            restore (frames[i]);
            frames[i].setBounds (x, y, width, height);
            x += X_VARIATION;
            y += Y_VARIATION;
        }
        if (selected != null)
        {
            selectFrame (selected);
        }
    }

    // ESCA-JAVA0126: allows unchecked exception UnsupportedOperationException
    /**
     * All the internal frames will be resized and moved in order to they take
     * up the whole space of the multidocument environmet.
     * @throws UnsupportedOperationException if restoration is vetoed.
     */
    public void mosaic ()
        throws UnsupportedOperationException
    {
        JInternalFrame[] frames = getAllFrames ();
        JInternalFrame selected = getSelectedFrame ();
        int i = frames.length;
        int j = frames.length;
        int l = frames.length;
        int k = 0;
        int lines = 0;
        int cols = 0;
        int remaining = 0;
        int xPosition = 0;
        int yPosition = 0;
        int posX = 0;
        int posY = 0;
        if (l > 0)
        {
            lines = (int) Math.ceil (Math.sqrt (l));
            cols = (int) Math.ceil ((double) l / lines);
            remaining = l - lines * cols;
            xPosition = getWidth () / cols;
            yPosition = getHeight () / lines;
            posY = 0;
            for (i = 0; (i + 1) < lines; i++)
            {
                posX = 0;
                for (j = 0; j < cols; j++)
                {
                    restore (frames[k]);
                    frames[k].setBounds (posX, posY, xPosition, yPosition);
                    posX += xPosition;
                    k++;
                }
                posY += yPosition;
            }
            cols += remaining;
            xPosition = getWidth () / cols;
            posX = 0;
            for (j = 0; j < cols; j++)
            {
                restore (frames[k]);
                frames[k].setBounds (posX, posY, xPosition, yPosition);
                posX += xPosition;
                k++;
            }
            if (selected != null)
            {
                selectFrame (selected);
            }
        }
    }

    // ESCA-JAVA0126: allows unchecked exception UnsupportedOperationException
    /**
     * Unselects the actual internal frame and selects the next to it. If the
     * actual internal frame is the last one, the new selected frame will the
     * first one.
     * @throws UnsupportedOperationException if selection is vetoed.
     */
    public void next ()
        throws UnsupportedOperationException
    {
        JInternalFrame[] frames = getAllFrames ();
        boolean end = false;
        int i = 0, l = frames.length;
        if (l > 1)
        {
            while (!end)
            {
                if (frames[i].isSelected ())
                {
                    try
                    {
                        frames[(i + 1) % l].setSelected (true);
                    }
                    catch (PropertyVetoException e)
                    {
                        throw new UnsupportedOperationException (
                                                                 stringDatabase.getString ("SelectionVetoed.Text.Label"));
                    }
                    end = true;
                }
                i++;
            }
        }
    }

    /**
     * Returns the previous internal frame to the selected one.
     * @return the previous internal frame to the selected one or null if there
     *         isn't.
     */
    private JInternalFrame getPrevious ()
    {
        JInternalFrame[] frames = getAllFrames ();
        JInternalFrame previousFrame = null;
        int i = 0, l = frames.length;
        if (l > 1)
        {
            while ((previousFrame == null) && (i < l))
            {
                if (frames[i].isSelected ())
                {
                    previousFrame = frames[(i - 1 + l) % l];
                }
                i++;
            }
        }
        return previousFrame;
    }

    // ESCA-JAVA0126: allows unchecked exception UnsupportedOperationException
    /**
     * Unselects the actual internal frame and selects the previous to it. If
     * the actual internal frame is the first one, the new selected frame will
     * the last one.
     * @throws UnsupportedOperationException if selection is vetoed.
     */
    public void previous ()
        throws UnsupportedOperationException
    {
        JInternalFrame previousFrame = getPrevious ();
        if (previousFrame != null)
        {
            try
            {
                previousFrame.setSelected (true);
            }
            catch (PropertyVetoException e)
            {
                throw new UnsupportedOperationException (
                                                         stringDatabase.getString ("SelectionVetoed.Text.Label"));
            }
        }
    }

    /**
     * Returns the number of internal frames.
     * @return number of internal frames.
     */
    public int getInternalFramesNumber ()
    {
        return getAllFrames ().length;
    }

    // ESCA-JAVA0126: allows unchecked exception UnsupportedOperationException
    /**
     * Selects the internal frame passed as parameter.
     * @param frame internal frame that will be selected.
     * @throws UnsupportedOperationException if the selection is vetoed.
     */
    public void selectFrame (JInternalFrame frame)
        throws UnsupportedOperationException
    {
        try
        {
            frame.setSelected (true);
        }
        catch (PropertyVetoException e)
        {
            throw new UnsupportedOperationException (
                                                     stringDatabase.getString ("SelectionVetoed.Text.Label"));
        }
    }

    // ESCA-JAVA0126: allows unchecked exception UnsupportedOperationException
    /**
     * Selects the internal frame whose content panel is passed as parameter.
     * @param panel panel that is the content panel of the internal frame that
     *            will be selected.
     * @throws UnsupportedOperationException if the selection is vetoed.
     */
    public void selectFrame (JPanel panel)
        throws UnsupportedOperationException
    {
        JInternalFrame[] frames = getAllFrames ();
        int i = 0, l = frames.length;
        boolean found = false;
        while (!found && (i < l))
        {
            if (frames[i].getContentPane ().equals (panel))
            {
                found = true;
            }
            else
            {
                i++;
            }
        }
        if (found)
        {
            selectFrame (frames[i]);
        }
    }

    /**
     * Selects the internal frame whose title is passed as parameter.
     * @param title title of the frame to be selected.
     * @throws UnsupportedOperationException if the selection is vetoed.
     */
    public JInternalFrame getFrameByTitle (String title)
    {
        JInternalFrame[] frames = getAllFrames ();
        int i = 0, l = frames.length;
        boolean found = false;
        JInternalFrame frame = null;
        while (!found && (i < l))
        {
            if (frames[i].getTitle ().equals (title))
            {
                found = true;
                frame = frames[i];
            }
            else
            {
                i++;
            }
        }
        return frame;
    }
}
