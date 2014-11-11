/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.common.com.swabunga.spell.swing;


import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;

import org.openmarkov.core.gui.dialog.common.com.swabunga.spell.event.SpellCheckEvent;




/**
 * Implementation of a spell check dialog.
 * 
 * @author Jason Height (jheight@chariot.net.au)
 */
public class JSpellDialog extends JDialog implements ActionListener,
				WindowListener {

	private JSpellForm form = new JSpellForm();
	private SpellCheckEvent event = null;

	public JSpellDialog(Frame owner, String title, boolean modal) {

		super(owner, title, modal);
		initialiseDialog();
	}

	public JSpellDialog(Dialog owner, String title, boolean modal) {

		super(owner, title, modal);
		initialiseDialog();
	}

	private void initialiseDialog() {

		getContentPane().add(form);
		form.addActionListener(this);
		addWindowListener(this);
		// setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		pack();
	}

	public void show(SpellCheckEvent e) {

		// System.out.println("Show");
		this.event = e;
		form.setSpellEvent(e);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {

		setVisible(false);
	}

	public void windowOpened(WindowEvent e) {

	}

	/** Cancel the event if the Dialog Close button is pressed */
	public void windowClosing(WindowEvent e) {

		if (event != null)
			event.cancel();
	}

	public void windowClosed(WindowEvent e) {

	}

	public void windowIconified(WindowEvent e) {

	}

	public void windowDeiconified(WindowEvent e) {

	}

	public void windowActivated(WindowEvent e) {

	}

	public void windowDeactivated(WindowEvent e) {

	}
}
