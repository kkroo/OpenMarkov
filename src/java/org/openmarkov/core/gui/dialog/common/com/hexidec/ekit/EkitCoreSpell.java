/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

/*
 * GNU Lesser General Public License EkitCore - Base Java Swing HTML Editor &
 * Viewer Class (Spellcheck Version) Copyright (C) 2000 Howard Kistler This
 * library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */

package org.openmarkov.core.gui.dialog.common.com.hexidec.ekit;


import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import org.openmarkov.core.gui.dialog.common.com.hexidec.util.Translatrix;
import org.openmarkov.core.gui.dialog.common.com.swabunga.spell.engine.SpellDictionary;
import org.openmarkov.core.gui.dialog.common.com.swabunga.spell.event.DocumentWordTokenizer;
import org.openmarkov.core.gui.dialog.common.com.swabunga.spell.event.SpellCheckEvent;
import org.openmarkov.core.gui.dialog.common.com.swabunga.spell.event.SpellCheckListener;
import org.openmarkov.core.gui.dialog.common.com.swabunga.spell.event.SpellChecker;
import org.openmarkov.core.gui.dialog.common.com.swabunga.spell.swing.JSpellDialog;
import org.openmarkov.core.gui.localize.StringDatabase;




/**
 * EkitCoreSpell Extended main application class with additional spellchecking
 * feature
 * 
 * @author Howard Kistler
 * @version 1.1 REQUIREMENTS Java 2 (JDK 1.3 or 1.4) Swing Library
 */

@SuppressWarnings("serial")
public class EkitCoreSpell extends EkitCore implements SpellCheckListener {

	/* Spell Checker Settings */
	private static String dictFile;
	private SpellChecker spellCheck = null;
	private JSpellDialog spellDialog;

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
	 * @param sdocSource
	 *            [StyledDocument] Optional document specification, using
	 *            javax.swing.text.StyledDocument.
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
	 * @param hasSpellChecker
	 *            [boolean] Specifies whether or not this uses the SpellChecker
	 *            module
	 * @param multiBar
	 *            [boolean] Specifies whether to use multiple toolbars or one
	 *            big toolbar.
	 */
	public EkitCoreSpell(String sDocument, String sStyleSheet,
							String sRawDocument, StyledDocument sdocSource,
							URL urlStyleSheet, boolean includeToolBar,
							boolean showViewSource, boolean showMenuIcons,
							boolean editModeExclusive, String sLanguage,
							String sCountry, boolean base64, boolean debugMode,
							boolean useSpellChecker, boolean multiBar,
							String toolbarSeq) {

		super(sDocument, sStyleSheet, sRawDocument, sdocSource, urlStyleSheet,
			includeToolBar, showViewSource, showMenuIcons, editModeExclusive,
			sLanguage, sCountry, base64, debugMode, true, multiBar, toolbarSeq);

		/* Create spell checker */
		try {
			dictFile = Translatrix.getTranslationString("DictionaryFile");
			SpellDictionary dictionary = new SpellDictionary(dictFile); // uses
																		// my
																		// custom
																		// loader
																		// in
																		// SpellDictionary
			spellCheck = new SpellChecker(dictionary);
			spellCheck.addSpellCheckListener(this);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, StringDatabase.getUniqueInstance()
					.getString( e.getMessage() ),
					StringDatabase.getUniqueInstance().getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		}
		spellDialog =
			new JSpellDialog(this.getFrame(), Translatrix
				.getTranslationString("ToolSpellcheckDialog"), true);
	}

	/**
	 * Raw/Base64 Document & Style Sheet URL Constructor (Ideal for EkitApplet)
	 * 
	 * @param sRawDocument
	 *            [String] A document encoded as a String to load in the editor
	 *            upon startup.
	 * @param sRawDocument
	 *            [String] A document encoded as a String to load in the editor
	 *            upon startup.
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
	 */
	public EkitCoreSpell(String sRawDocument, URL urlStyleSheet,
							boolean includeToolBar, boolean showViewSource,
							boolean showMenuIcons, boolean editModeExclusive,
							String sLanguage, String sCountry, boolean base64,
							boolean multiBar, String toolbarSeq) {

		this(null, null, sRawDocument, null, urlStyleSheet, includeToolBar,
			showViewSource, showMenuIcons, editModeExclusive, sLanguage,
			sCountry, base64, false, true, multiBar, toolbarSeq);
	}

	/**
	 * Empty Constructor
	 */
	public EkitCoreSpell() {

		this(null, null, null, null, null, true, false, true, true, null, null,
			false, false, true, false, EkitCore.TOOLBAR_DEFAULT_SINGLE);
	}

	/* SpellCheckListener methods */
	public void spellingError(SpellCheckEvent event) {

		spellDialog.show(event);
	}

	/* Spell checking method (overrides empty method in basic core) */
	public void checkDocumentSpelling(Document doc) {

		spellCheck.checkSpelling(new DocumentWordTokenizer(doc));
	}

}
