/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;
import org.openmarkov.core.gui.loader.element.ImageLoader;
import org.openmarkov.core.gui.localize.StringDatabase;

/**
 * Class to show an About Box window for the OpenMarkov Project
 * @author jlgozalo
 * @version 1.0 17/03/2009
 */
public class AboutBox extends JDialog
    implements
        ActionListener
{
    // TODO to be consider to re-write as an standard dialog using the
    // OpenMarkov dialog resource bundle
    /**
     * default id
     */
    private static final long serialVersionUID     = -2926600957370532009L;
    /**
     * String database
     */
    private StringDatabase    stringDatabase       = StringDatabase.getUniqueInstance ();
    /**
     * Image Loader
     */
    private ImageLoader       imageLoader          = null;
    private Logger            logger;
    /**
     * AboutBox visual components
     */
    private AboutBox          anAboutBox           = null;
    String                    product              = "";
    String                    version              = "";
    String                    copyright            = "";
    String                    copyright2           = "";
    String                    authors              = "";
    String                    advertisement        = "";
    String                    trademark            = "";
    String                    openMarkovLogoImage  = "";
    String                    lineSeparatorImage   = "";
    BorderLayout              borderLayoutAboutBox = new BorderLayout ();
    JPanel                    jPanelAboutText      = new JPanel ();
    JPanel                    jPanelAboutButton    = new JPanel ();
    ImageIcon                 openMarkovLogo       = new ImageIcon ();
    ImageIcon                 lineSeparator        = new ImageIcon ();
    JLabel                    jLabelLogo           = new JLabel ();
    JLabel                    jLabelProduct        = new JLabel ();
    JLabel                    jLabelVersion        = new JLabel ();
    JLabel                    jLabelCopyright      = new JLabel ();
    JLabel                    jLabelCopyright2     = new JLabel ();
    JLabel                    jLabelAuthors        = new JLabel ();
    JLabel                    jLabelLineSeparators = new JLabel ();
    JLabel                    jLabelAdvertisement  = new JLabel ();
    JLabel                    jLabelTrademark      = new JLabel ();
    JButton                   jButtonOK            = new JButton ();
    GridLayout                gridLayoutText       = new GridLayout ();
    FlowLayout                flowLayoutButtons    = new FlowLayout ();
    /**
     * size of the window and position
     */
    int                       height               = 0;
    int                       width                = 0;
    int                       x                    = 0;
    int                       y                    = 0;

    /**
     * singleton for AboutBox
     * @return anAboutBox dialog
     */
    public AboutBox getUniqueInstance ()
    {
        return getUniqueInstance (null);
    }

    /**
     * singleton for AboutBox
     * @param parent the parent for the AboutBox frame
     * @return anAboutBox dialog
     */
    public AboutBox getUniqueInstance (JFrame parent)
    {
        this.logger = Logger.getLogger (AboutBox.class);
        if (anAboutBox == null)
        { // singleton
            new AboutBox (parent);
        }
        else
        { // it is already created and not visible
            this.setVisible (true);
        }
        return anAboutBox;
    }

    /**
     * constructor on a open window
     */
    public AboutBox ()
    {
        this (null);
    }

    /**
     * constructor on a parent JFrame
     * @param parent
     */
    public AboutBox (JFrame parent)
    {
        super (parent, "", true);
        try
        {
            this.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
            this.setTitle (stringDatabase.getString ("AboutBox.Title.Text"));
            imageLoader = new ImageLoader ();
            jbInit ();
            this.setVisible (true);
            anAboutBox = this;
        }
        catch (Exception e)
        {
            // ExceptionsHandler.handleException(e, null, true);
            logger.info (e);
        }
    }

    /**
     * Component initialization.
     * @throws Exception if any problem on components initialization
     */
    private void jbInit ()
        throws Exception
    {
        product = stringDatabase.getString ("AboutBox.Product.Text");
        version = stringDatabase.getString ("AboutBox.Version.Text");
        copyright = stringDatabase.getString ("AboutBox.Copyright.Text");
        copyright2 = stringDatabase.getString ("AboutBox.Copyright.AllRightsReserved.Text");
        authors = stringDatabase.getString ("AboutBox.Authors.Text");
        advertisement = stringDatabase.getString ("AboutBox.Advertisement.Text");
        trademark = stringDatabase.getString ("AboutBox.Trademark.Text");
        openMarkovLogoImage = stringDatabase.getString ("AboutBox.OpenMarkovLogoImage.URL");
        lineSeparatorImage = stringDatabase.getString ("AboutBox.LineSeparatorImage.URL");
        try
        {
            // look for the images to show in the box
            openMarkovLogo = imageLoader.load (openMarkovLogoImage);
            lineSeparator = imageLoader.load (lineSeparatorImage);
            // put the title of the box
            setTitle (product);
            // mark the layout and the size for the About box
            getContentPane ().setLayout (borderLayoutAboutBox);
            height = openMarkovLogo.getIconHeight () + 200;
            width = openMarkovLogo.getIconWidth ();
            x = getParent ().getX ();
            y = getParent ().getY ();
            x = x + ((getParent ().getWidth () - width) / 2);
            y = y + ((getParent ().getHeight () - height) / 2);
            this.setBounds (x, y, width, height);
            // set the logo and add to the top of the box
            jLabelLogo.setHorizontalAlignment (SwingConstants.CENTER);
            jLabelLogo.setIcon (openMarkovLogo);
            jLabelLogo.setHorizontalTextPosition (SwingConstants.CENTER);
            jLabelLogo.setText (stringDatabase.getString ("AboutBox.jLabelLogo.BlankSpace.Text"));
            this.getContentPane ().add (jLabelLogo, java.awt.BorderLayout.NORTH);
            // set the items and add to the panel and then to the box
            setTextInLabelAligned (jLabelProduct, product, JLabel.CENTER);
            jLabelProduct.setHorizontalAlignment (SwingConstants.CENTER);
            jLabelProduct.setHorizontalTextPosition (SwingConstants.LEADING);
            jLabelProduct.setFont (new Font ("", Font.PLAIN, 16));
            setTextInLabelAligned (jLabelVersion, version, JLabel.CENTER);
            setTextInLabelAligned (jLabelCopyright, copyright, JLabel.CENTER);
            setTextInLabelAligned (jLabelCopyright2, copyright2, JLabel.CENTER);
            setTextInLabelAligned (jLabelLineSeparators,
                                   stringDatabase.getString ("AboutBox.jLabelLogo.BlankSpace.Text"),
                                   JLabel.CENTER);
            jLabelLineSeparators.setHorizontalAlignment (SwingConstants.CENTER);
            jLabelLineSeparators.setHorizontalTextPosition (SwingConstants.CENTER);
            jLabelLineSeparators.setIcon (lineSeparator);
            setTextInLabelAligned (jLabelAdvertisement, advertisement, JLabel.LEFT);
            setTextInLabelAligned (jLabelTrademark, trademark, JLabel.LEFT);
            jPanelAboutText.setLayout (gridLayoutText);
            gridLayoutText.setColumns (1);
            gridLayoutText.setRows (8);
            jPanelAboutText.add (jLabelProduct);
            jPanelAboutText.add (jLabelVersion);
            jPanelAboutText.add (jLabelCopyright);
            jPanelAboutText.add (jLabelCopyright2);
            jPanelAboutText.add (jLabelAuthors);
            jPanelAboutText.add (jLabelLineSeparators);
            jPanelAboutText.add (jLabelAdvertisement);
            jPanelAboutText.add (jLabelTrademark);
            this.getContentPane ().add (jPanelAboutText, java.awt.BorderLayout.CENTER);
            // set the OK button and action associated
            jButtonOK.setText (stringDatabase.getString ("AboutBox.OK.Text"));
            jButtonOK.addActionListener (this);
            jPanelAboutButton.setLayout (flowLayoutButtons);
            jPanelAboutButton.add (jButtonOK);
            this.getContentPane ().add (jPanelAboutButton, java.awt.BorderLayout.SOUTH);
        }
        catch (Exception exception)
        {
            exception.printStackTrace ();
            JOptionPane.showMessageDialog (null,
                                           stringDatabase.getString (exception.getMessage ()),
                                           stringDatabase.getString (exception.getMessage ()),
                                           JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Close the dialog on a button event. Really, this action only hides the
     * dialog to be reused if required.
     * @param actionEvent ActionEvent
     */
    public void actionPerformed (ActionEvent actionEvent)
    {
        if (actionEvent.getActionCommand ().equals (stringDatabase.getString ("AboutBox.OK.Text")))
        {
            this.setVisible (false);
        }
    }

    /**
     * align a text in a label with a center alignment
     */
    private static void setTextInLabelAligned (JLabel theLabel, String theText, int alignment)
    {
        theLabel.setHorizontalAlignment (alignment);
        theLabel.setText (theText);
    }
}
