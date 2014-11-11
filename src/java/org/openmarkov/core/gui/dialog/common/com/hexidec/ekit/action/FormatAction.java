/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

/*
 * GNU Lesser General Public License FormatAction Copyright (C) 2000 Howard
 * Kistler This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version. This library is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */

package org.openmarkov.core.gui.dialog.common.com.hexidec.ekit.action;


import java.awt.event.ActionEvent;

import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;

import org.openmarkov.core.gui.dialog.common.com.hexidec.ekit.EkitCore;
import org.openmarkov.core.gui.dialog.common.com.hexidec.ekit.compoment.SimpleInfoDialog;
import org.openmarkov.core.gui.dialog.common.com.hexidec.util.Translatrix;




/**
 * Class for implementing HTML format actions (NOTE : Does not toggle. User must
 * use the "Clear Format" option to remove formatting correctly.)
 */
public class FormatAction extends StyledEditorKit.StyledTextAction {

	protected EkitCore parentEkit;
	HTML.Tag htmlTag;

	public FormatAction(EkitCore ekit, String actionName, HTML.Tag inTag) {

		super(actionName);
		parentEkit = ekit;
		htmlTag = inTag;
	}

	public void actionPerformed(ActionEvent ae) {

		JTextPane parentTextPane = parentEkit.getTextPane();
		String selText = parentTextPane.getSelectedText();
		int textLength = -1;
		if (selText != null) {
			textLength = selText.length();
		}
		if (selText == null || textLength < 1) {
			SimpleInfoDialog sidWarn =
				new SimpleInfoDialog(parentEkit.getFrame(), "", true,
					Translatrix.getTranslationString("ErrorNoTextSelected"),
					SimpleInfoDialog.ERROR);
		} else {
			SimpleAttributeSet sasText =
				new SimpleAttributeSet(parentTextPane.getCharacterAttributes());
			sasText.addAttribute(htmlTag, new SimpleAttributeSet());
			int caretOffset = parentTextPane.getSelectionStart();
			parentTextPane.select(caretOffset, caretOffset + textLength);
			parentTextPane.setCharacterAttributes(sasText, false);
			parentEkit.refreshOnUpdate();
			parentTextPane.select(caretOffset, caretOffset + textLength);
		}
	}
}
