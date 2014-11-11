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
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;


/**
 * This class implements a dialog where a the programmer can set a default
 * button and a cancel button.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 */
public class DialogBase extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6121463474893584183L;
	/**
	 * Cancel button.
	 */
	protected JButton jButtonCancel = null;

	/**
	 * Constructor that invokes its superclass constructor and registers a
	 * listener that executes the 'click' of the cancel button when the key ESC
	 * is pressed.
	 * 
	 * @param owner
	 *            window that owns the dialog box.
	 */
	public DialogBase(Window owner) {

		super(owner);

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setName("DialogBase");
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {

				if (jButtonCancel != null) {
					jButtonCancel.doClick();
				}
			}
		});
		ActionListener listener = new ActionListener() {

			public void actionPerformed(ActionEvent evt) {

				if (jButtonCancel != null) {
					jButtonCancel.doClick();
				}
			}
		};
		getRootPane().registerKeyboardAction(
			listener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
			JComponent.WHEN_IN_FOCUSED_WINDOW);

	}

	/**
	 * Sets the button whose 'click' will be called when the key ENTER is
	 * pressed.
	 * 
	 * @param defaultButton
	 *            button invoked when the key ENTER is pressed.
	 */
	public void setDefaultButton(JButton defaultButton) {

		getRootPane().setDefaultButton(defaultButton);

	}

	/**
	 * Sets the button whose 'click' will be called when the key ESC is pressed.
	 * 
	 * @param button
	 *            button invoked when the key ESC is pressed.
	 */
	public void setCancelButton(JButton button) {

		jButtonCancel = button;

	}
}
