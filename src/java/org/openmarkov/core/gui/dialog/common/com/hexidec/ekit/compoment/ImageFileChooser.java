/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

/*
 * GNU Lesser General Public License ImageFileChooser Copyright (C) 2000 Frits
 * Jalvingh & Howard Kistler This library is free software; you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version. This library is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.openmarkov.core.gui.dialog.common.com.hexidec.ekit.compoment;


import java.io.File;

import javax.swing.JFileChooser;


/**
 * Class for providing a chooser that lets the user select an image to insert
 */
public class ImageFileChooser extends JFileChooser {

	/**
	 * Constructor that takes a default directory to start in, specified as a
	 * File
	 * 
	 * @param File
	 *            with the default path
	 */
	public ImageFileChooser(File fileCurrentDirectory) {

		this.setCurrentDirectory(fileCurrentDirectory);
		this.setAccessory(new ImageFileChooserPreview(this));
	}

	/**
	 * Constructor that takes a default directory to start in, specified as a
	 * String
	 * 
	 * @param String
	 *            current directory path.
	 */
	public ImageFileChooser(String strCurrentPath) {

		this(new File(strCurrentPath));
	}

	/**
	 * Empty constructor
	 */
	public ImageFileChooser() {

		this((File) null);
	}
}
