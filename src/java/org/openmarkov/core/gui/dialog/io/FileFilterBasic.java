/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.io;


import java.io.File;

import javax.swing.filechooser.FileFilter;


/**
 * This class implements the base code for all the file filters of the
 * application. By default, it accepts all the directories and initialises the
 * string resource.
 * 
 * @author jmendoza
 * @version 1.0
 */
public class FileFilterBasic extends FileFilter {

	/**
	 * Returns the extension of the given file.
	 * 
	 * @param file
	 *            file of which obtain the extension.
	 * @return extension of the file.
	 */
	protected String getExtension(File file) {

		String extension = "";
		String name = file.getName();
		int i = name.lastIndexOf('.');

		if ((i > 0) && (i < (name.length() - 1))) {
			extension = name.substring(i + 1).toLowerCase();
		}
		return extension;
	}

	/**
	 * This method accepts every directories by default.
	 * 
	 * @return true if the File parameter is a directory; false otherwise
	 */
	@Override
	public boolean accept(File file) {

		return (file.isDirectory());
	}

	/**
	 * Returns a string containing the name of the file adding to it the
	 * extension. If the file still contains the extension, then the file name
	 * is returned without modifications.
	 * 
	 * @param fileName
	 *            name of the file.
	 * @return the file name modified, if necessary.
	 */
	public String addExtension(String fileName) {

		String fileExtension = getExtension(new File(fileName));
		String extension = getFilterExtension();

		return ((fileExtension == null) || (!fileExtension.equals(extension)))
			? fileName + "." + extension : fileName;
	}

	/**
	 * Returns the extension of the files that match this filter.
	 * 
	 * @return accepted extension by the filter.
	 */
	public String getFilterExtension() {

		return null;
	};

	@Override
	public String getDescription() {

		// TODO Auto-generated method stub
		return null;
	}
}
