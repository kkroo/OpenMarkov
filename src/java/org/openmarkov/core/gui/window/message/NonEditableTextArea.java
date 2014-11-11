/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.window.message;


import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


/**
 * This class implements a non editable area where appear the data stream
 * destined for the standard output and standar error.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 */
public class NonEditableTextArea extends JTextPane implements MessageArea {

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 5324920647784157184L;

	/**
	 * Styled document.
	 */
	private StyledDocument styledDocument = null;

	/**
	 * Font attributes.
	 */
	private SimpleAttributeSet attributeSet = null;

	/**
	 * Color of normal messages.
	 */
	private Color normalMessageColor = Color.black;

	/**
	 * Color of error messages.
	 */
	private Color errorMessageColor = Color.red;

	/**
	 * Default constructor.
	 */
	public NonEditableTextArea() {

		// super();
		initialize();
	}

	/**
	 * This method initialises this instance.
	 */
	private void initialize() {

		setEditable(false);
		setBackground(Color.WHITE);
		styledDocument = getStyledDocument();
		attributeSet = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attributeSet, "Monospaced");
		StyleConstants.setFontSize(attributeSet, 13);
	}

	/**
	 * Writes an information message in the text area.
	 * 
	 * @param message
	 *            text to write.
	 */
	public void writeInformationMessage(String message) {

		StyleConstants.setForeground(attributeSet, normalMessageColor);
		// ESCA-JAVA0008: allows an empty catch block in the method
		try {
			styledDocument.insertString(
				styledDocument.getLength(), message, attributeSet);
		} catch (BadLocationException e) {
		}
	}

	/**
	 * Writes an error message in the text area.
	 * 
	 * @param message
	 *            text to write.
	 */
	public void writeErrorMessage(String message) {

		StyleConstants.setForeground(attributeSet, errorMessageColor);
		// ESCA-JAVA0008: allows an empty catch block in the method
		try {
			styledDocument.insertString(
				styledDocument.getLength(), message, attributeSet);
		} catch (BadLocationException e) {
		}
	}
}
