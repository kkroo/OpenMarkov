/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.common.com.hexidec.ekit;


import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.URL;

import javax.swing.JFrame;


public class Ekit extends JFrame implements WindowListener {

	private EkitCore ekitCore;

	private File currentFile = (File) null;

	/**
	 * Master Constructor
	 * 
	 * @param sDocument
	 *            [String] A text or HTML document to load in the editor upon
	 *            startup.
	 * @param sStyleSheet
	 *            [String] A CSS stylesheet to load in the editor upon startup.
	 * @param sRawDocument
	 *            [String] A document encoded as a String to load in the editor
	 *            upon startup.
	 * @param urlStyleSheet
	 *            [URL] A URL reference to the CSS style sheet.
	 * @param includeToolBar
	 *            [boolean] Specifies whether the app should include the
	 *            toolbar.
	 * @param showViewSource
	 *            [boolean] Specifies whether or not to show the View Source
	 *            window on startup.
	 * @param showMenuIcons
	 *            [boolean] Specifies whether or not to show icon pictures in
	 *            menus.
	 * @param editModeExclusive
	 *            [boolean] Specifies whether or not to use exclusive edit mode
	 *            (recommended on).
	 * @param sLanguage
	 *            [String] The language portion of the Internationalization
	 *            Locale to run Ekit in.
	 * @param sCountry
	 *            [String] The country portion of the Internationalization
	 *            Locale to run Ekit in.
	 * @param base64
	 *            [boolean] Specifies whether the raw document is Base64 encoded
	 *            or not.
	 * @param debugMode
	 *            [boolean] Specifies whether to show the Debug menu or not.
	 * @param useSpellChecker
	 *            [boolean] Specifies whether to include the spellchecker or
	 *            not.
	 * @param multiBar
	 *            [boolean] Specifies whether to use multiple toolbars or one
	 *            big toolbar.
	 */
	public Ekit(String sDocument, String sStyleSheet, String sRawDocument,
				URL urlStyleSheet, boolean includeToolBar,
				boolean showViewSource, boolean showMenuIcons,
				boolean editModeExclusive, String sLanguage, String sCountry,
				boolean base64, boolean debugMode, boolean useSpellChecker,
				boolean multiBar) {

		if (useSpellChecker) {
			ekitCore =
				new EkitCoreSpell(sDocument, sStyleSheet, sRawDocument, null,
					urlStyleSheet, includeToolBar, showViewSource,
					showMenuIcons, editModeExclusive, sLanguage, sCountry,
					base64, debugMode, true, multiBar, (multiBar
						? EkitCore.TOOLBAR_DEFAULT_MULTI
						: EkitCore.TOOLBAR_DEFAULT_SINGLE));
		} else {
			ekitCore =
				new EkitCore(sDocument, sStyleSheet, sRawDocument, null,
					urlStyleSheet, includeToolBar, showViewSource,
					showMenuIcons, editModeExclusive, sLanguage, sCountry,
					base64, debugMode, false, multiBar, (multiBar
						? EkitCore.TOOLBAR_DEFAULT_MULTI
						: EkitCore.TOOLBAR_DEFAULT_SINGLE));
		}

		ekitCore.setFrame(this);

		/* Add the components to the app */
		if (includeToolBar) {
			if (multiBar) {
				this.getContentPane().setLayout(new GridBagLayout());
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.anchor = GridBagConstraints.NORTH;
				gbc.gridheight = 1;
				gbc.gridwidth = 1;
				gbc.weightx = 1.0;
				gbc.weighty = 0.0;
				gbc.gridx = 1;

				gbc.gridy = 1;
				this.getContentPane().add(
					ekitCore.getToolBarMain(includeToolBar), gbc);

				gbc.gridy = 2;
				this.getContentPane().add(
					ekitCore.getToolBarFormat(includeToolBar), gbc);

				gbc.gridy = 3;
				this.getContentPane().add(
					ekitCore.getToolBarStyles(includeToolBar), gbc);

				gbc.anchor = GridBagConstraints.SOUTH;
				gbc.fill = GridBagConstraints.BOTH;
				gbc.weighty = 1.0;
				gbc.gridy = 4;
				this.getContentPane().add(ekitCore, gbc);
			} else {
				this.getContentPane().setLayout(new BorderLayout());
				this.getContentPane().add(ekitCore, BorderLayout.CENTER);
				this.getContentPane().add(
					ekitCore.getToolBar(includeToolBar), BorderLayout.NORTH);
			}
		} else {
			this.getContentPane().setLayout(new BorderLayout());
			this.getContentPane().add(ekitCore, BorderLayout.CENTER);
		}

		this.setJMenuBar(ekitCore.getMenuBar());

		this.addWindowListener(this);

		this.updateTitle();
		this.pack();
		this.setVisible(true);
	}

	public Ekit() {

		this(null, null, null, null, true, false, true, true, null, null,
			false, false, false, true);
	}

	/* WindowListener methods */
	public void windowClosing(WindowEvent we) {

		this.dispose();
		System.exit(0);
	}

	public void windowOpened(WindowEvent we) {

		;
	}

	public void windowClosed(WindowEvent we) {

		;
	}

	public void windowActivated(WindowEvent we) {

		;
	}

	public void windowDeactivated(WindowEvent we) {

		;
	}

	public void windowIconified(WindowEvent we) {

		;
	}

	public void windowDeiconified(WindowEvent we) {

		;
	}

	/**
	 * Convenience method for updating the application title bar
	 */
	private void updateTitle() {

		this.setTitle(ekitCore.getAppName()
			+ (currentFile == null ? "" : " - " + currentFile.getName()));
	}

	/**
	 * Main method
	 */
	public static void main(String[] args) {

		String sDocument = null;
		String sStyleSheet = null;
		String sRawDocument = null;
		URL urlStyleSheet = null;
		boolean includeToolBar = true;
		boolean multibar = false;
		boolean includeViewSource = false;
		boolean includeMenuIcons = true;
		boolean modeExclusive = true;
		String sLang = null;
		String sCtry = null;
		boolean base64 = false;
		boolean debugOn = false;
		boolean spellCheck = false;

		Ekit ekit =
			new Ekit(sDocument, sStyleSheet, sRawDocument, urlStyleSheet,
				includeToolBar, includeViewSource, includeMenuIcons,
				modeExclusive, sLang, sCtry, base64, debugOn, spellCheck,
				multibar);
	}

}