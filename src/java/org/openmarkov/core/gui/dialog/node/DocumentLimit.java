/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.node;


import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


/**
 * It is used to control the character format in Relevance ComboBox when the
 * user select the option "Other... "
 * 
 * @author Alberto Manuel Ruiz Lafuente UCLM 2008
 * @version 1.0 30/05/2009
 * @version 1.1 25/07/2009 jlgozalo set variables in english language
 */
public class DocumentLimit extends PlainDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * "Editor" which will control the string
	 */
	private JTextField editor;

	/**
	 * Max number of characters to control
	 */
	private int maxCharNumber;

	/**
	 * Constructor
	 * 
	 * @param editor
	 * @param maxCharNumber
	 */
	public DocumentLimit(JTextField editor, int maxCharNumber) {

		this.editor = editor;
		this.maxCharNumber = maxCharNumber;
	}

	/**
	 * Method that is called by the editor each time that the user introduce a
	 * character. The method check that is not over the limit.
	 */
	public void insertString(int arg0, String arg1,
								javax.swing.text.AttributeSet arg2)
					throws BadLocationException {

		if ((editor.getText().length() + arg1.length()) > this.maxCharNumber)
			return;
		super.insertString(arg0, arg1, arg2);
	}

}
