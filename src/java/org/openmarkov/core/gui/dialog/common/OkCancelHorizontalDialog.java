/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.common;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.openmarkov.core.gui.loader.element.IconLoader;
import org.openmarkov.core.gui.localize.StringDatabase;

/**
 * This class implements a dialog box with a horizontal buttons panel placed in
 * the bottom of the window. This panel has two buttons: a 'OK' button that is
 * activated pressing the ENTER key and a 'CANCEL' button activated pressing the
 * ESC key.
 * 
 * @author jmendoza
 * @version 1.0
 * @version 1.1 jlgozalo - 07/02/2010 adding icons to button
 * @version 1.2 jlgozalo - 30/05/2010 set OK_BUTTON to default
 */
public class OkCancelHorizontalDialog extends BottomPanelButtonDialog {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1176820837760605949L;

    // ESCA-JAVA0007:
    /**
     * Constant that indicates that the user has pressed 'Ok' button.
     */
    public static int         OK_BUTTON        = JOptionPane.OK_OPTION;

    // ESCA-JAVA0007:
    /**
     * Constant that indicates that the user has pressed 'Cancel' button.
     */
    public static int         CANCEL_BUTTON    = JOptionPane.CANCEL_OPTION;

    /**
     * Button selected by the user.
     */
    protected int             selectedButton   = 0;

    /**
     * Ok button.
     */
    private JButton           jButtonOK        = null;

    /**
     * Cancel button.
     */
    private JButton           jButtonCancel    = null;

    /**
     * Icon loader.
     */
    protected IconLoader      iconLoader       = null;

    /**
     * String database
     */
    protected StringDatabase  stringDatabase   = StringDatabase.getUniqueInstance();

    /**
     * Constructor. initialises the instance.
     * 
     * @param owner
     *            window that owns the dialog.
     */
    public OkCancelHorizontalDialog(Window owner) {

        super(owner);
        initialize();
        pack();
    }

    /**
     * This method initialises this instance.
     */
    private void initialize() {

        // setSize(550, 310);
        setName("OKCancelHorizontalDialog");
        iconLoader = new IconLoader();
        configureButtonsPanel();
        setDefaultButton(getJButtonOK());
    }

    /**
     * Sets up the panel where the buttons of the buttons panel will be appear.
     */
    private void configureButtonsPanel() {

        addButtonToButtonsPanel(getJButtonOK());
        addButtonToButtonsPanel(getJButtonCancel());
    }

    /**
     * This method initialises jButtonApply.
     * 
     * @return a new Ok button.
     */
    protected JButton getJButtonOK() {

        if (jButtonOK == null) {
            jButtonOK = new JButton();
            jButtonOK.setName("jButtonApply");
            jButtonOK.setIcon(iconLoader.load(IconLoader.ICON_ACCEPT_ENABLED));
            jButtonOK.setText(stringDatabase.getString("OKCancelHorizontalDialog.jButtonOK.Text"));
            jButtonOK.setMnemonic(stringDatabase.getString("OKCancelHorizontalDialog.jButtonOK.Mnemonic").charAt(0));
            jButtonOK.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (doOkClickBeforeHide()) {
                        selectedButton = OK_BUTTON;
                        dispose();
                    }
                }
            });
        }
        return jButtonOK;
    }

    /**
     * This method initialises jButtonCancel.
     * 
     * @return a new Cancel button.
     */
    protected JButton getJButtonCancel() {

        if (jButtonCancel == null) {
            jButtonCancel = new JButton();
            jButtonCancel.setName("jButtonCancel");
            jButtonCancel.setIcon(iconLoader.load(IconLoader.ICON_REMOVE_ENABLED));
            jButtonCancel.setText(stringDatabase.getString("OKCancelHorizontalDialog.jButtonCancel.Text"));
            jButtonCancel.setMnemonic(stringDatabase.getString("OKCancelHorizontalDialog.jButtonCancel.Mnemonic").charAt(0));
            setCancelButton(jButtonCancel);
            jButtonCancel.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    doCancelClickBeforeHide();
                    selectedButton = CANCEL_BUTTON;
                    setVisible(false);
                    dispose();
                }
            });
        }
        return jButtonCancel;
    }

    /**
     * Shows or hides this Dialog depending on the value of parameter b. If b is
     * true, the selected button is set to OK_BUTTON.
     * 
     * @param b
     *            if true, makes the dialog visible, otherwise hides the dialog.
     */
    @Override
    public void setVisible(boolean b) {

        if (b) {
            selectedButton = OK_BUTTON;
        }
        super.setVisible(b);
    }

    /**
     * This method carries out the actions when the user presses the Ok button
     * before hiding the dialog.
     * 
     * @return true if the dialog box can be closed.
     */
    protected boolean doOkClickBeforeHide() {

        return false;
    }

    /**
     * This method carries out the actions when the user press the Cancel button
     * before hide the dialog.
     */
    protected void doCancelClickBeforeHide() {

    }

    public int getSelectedButton() {
        return selectedButton;
    }

}
