/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

/*
 * GNU Lesser General Public License SetFontFamilyAction Copyright (C) 2004
 * Howard Kistler This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.openmarkov.core.gui.dialog.common.com.hexidec.ekit.action;


import java.awt.event.ActionEvent;

import javax.swing.text.StyledEditorKit;

import org.openmarkov.core.gui.dialog.common.com.hexidec.ekit.EkitCore;
import org.openmarkov.core.gui.dialog.common.com.hexidec.ekit.compoment.FontSelectorDialog;
import org.openmarkov.core.gui.dialog.common.com.hexidec.util.Translatrix;




/**
 * Class for implementing custom Font Family formating actions
 */
public class SetFontFamilyAction extends StyledEditorKit.FontFamilyAction {

	protected String name;
	protected EkitCore parentEkit;

	public SetFontFamilyAction(EkitCore ekit, String actionName) {

		super(actionName, "");
		this.name = actionName;
		parentEkit = ekit;
	}

	public void actionPerformed(ActionEvent ae) {

		if (this.name.equals("[EKITFONTSELECTOR]")) {
			StyledEditorKit.FontFamilyAction newFontFamilyAction =
				new StyledEditorKit.FontFamilyAction("fontFamilyAction",
					parentEkit.getFontNameFromSelector());
			newFontFamilyAction.actionPerformed(ae);
		} else {
			FontSelectorDialog fsdInput =
				new FontSelectorDialog(parentEkit.getFrame(), Translatrix
					.getTranslationString("FontDialogTitle"), true, "face",
					parentEkit.getTextPane().getSelectedText());
			String newFace = fsdInput.getFontName();
			if (newFace != null) {
				StyledEditorKit.FontFamilyAction newFontFamilyAction =
					new StyledEditorKit.FontFamilyAction("fontFamilyAction",
						newFace);
				newFontFamilyAction.actionPerformed(ae);
			}
		}
	}
}
