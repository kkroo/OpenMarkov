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

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.openmarkov.core.gui.localize.StringDatabase;

/**
 * This class implements a file chooser dialog file to select OpenMarkov files.
 * 
 * @author jmendoza
 * @author m.arias
 * @version 1.0 jmendoza, marias
 * @version 1.1 jlgozalo - set appropriate variables names and redo For loop to
 *          use enhanced loop syntax
 */
public abstract class FileChooser extends JFileChooser {

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 9076351651764305920L;

	/**
	 * Directory where the dialog box searchs the files.
	 */
	private static String directoryPath = System.getProperty("user.home");

	/**
	 * Creates a new file chooser that starts in the current directory,
	 * filtering the files with the file filters.
	 */
	public FileChooser(boolean acceptAllfile) {
		setTextsInLocale();
		setAcceptAllFileFilterUsed(acceptAllfile);
		setCurrentDirectory(new File(directoryPath));
		rescanCurrentDirectory();
	}

	/**
	 * to fix the bug in JFileChooser to display text in different languages the
	 * text of the components must be set explicitly
	 */
	private void setTextsInLocale() {

	    StringDatabase stringDb = StringDatabase.getUniqueInstance ();
	    
		UIManager.put("FileChooser.cancelButtonText",
		              stringDb.getString("FileChooser.cancelButtonText"));
		UIManager.put("FileChooser.cancelButtonToolTipText",
		              stringDb.getString("FileChooser.cancelButtonToolTipText"));
		UIManager.put("FileChooser.detailsViewActionLabelText",
		              stringDb.getString("FileChooser.detailsViewActionLabelText"));
		UIManager.put("FileChooser.detailsViewButtonToolTipText",
		              stringDb.getString("FileChooser.detailsViewButtonToolTipText"));
		UIManager.put("FileChooser.fileNameLabelText",
		              stringDb.getString("FileChooser.fileNameLabelText"));
		UIManager.put("FileChooser.filesOfTypeLabelText",
		              stringDb.getString("FileChooser.filesOfTypeLabelText"));
		UIManager.put("FileChooser.helpButtonText",
		              stringDb.getString("FileChooser.helpButtonText"));
		UIManager.put("FileChooser.helpButtonToolTipText",
		              stringDb.getString("FileChooser.helpButtonToolTipText"));
		UIManager.put("FileChooser.homeFolderToolTipText",
		              stringDb.getString("FileChooser.homeFolderToolTipText"));
		UIManager.put("FileChooser.listViewActionLabelText",
		              stringDb.getString("FileChooser.listViewActionLabelText"));
		UIManager.put("FileChooser.listViewButtonToolTipTextlist",
		              stringDb.getString("FileChooser.newFolderToolTipText"));
		UIManager.put("FileChooser.lookInLabelText",
		              stringDb.getString("FileChooser.lookInLabelText"));
		UIManager.put("FileChooser.newFolderActionLabelText",
		              stringDb.getString("FileChooser.newFolderActionLabelText"));
		UIManager.put("FileChooser.newFolderToolTipText",
		              stringDb.getString("FileChooser.newFolderToolTipText"));
		UIManager.put("FileChooser.openButtonTextOpen",
		              stringDb.getString("FileChooser.openButtonTextOpen"));
		UIManager.put("FileChooser.openButtonToolTipText",
		              stringDb.getString("FileChooser.openButtonToolTipText"));
		UIManager.put("FileChooser.refreshActionLabelText",
		              stringDb.getString("FileChooser.refreshActionLabelText"));
		UIManager.put("FileChooser.saveButtonTextSave",
		              stringDb.getString("FileChooser.saveButtonTextSave"));
		UIManager.put("FileChooser.saveButtonToolTipText",
		              stringDb.getString("FileChooser.saveButtonToolTipText"));
		UIManager.put("FileChooser.upFolderToolTipText",
		              stringDb.getString("FileChooser.upFolderToolTipText"));
		UIManager.put("FileChooser.updateButtonText",
		              stringDb.getString("FileChooser.updateButtonText"));
		UIManager.put("FileChooser.updateButtonToolTipText",
		              stringDb.getString("FileChooser.updateButtonToolTipText"));
		UIManager.put("FileChooser.viewMenuLabelText",
		              stringDb.getString("FileChooser.viewMenuLabelText"));

	}

	public void setFileFilter(String extension) {
		for(FileFilter filter : getChoosableFileFilters())
		{
			if(filter instanceof FileFilterBasic &&
					((FileFilterBasic)filter).getFilterExtension().equalsIgnoreCase(extension))
			{
				setFileFilter(filter);
			}
		}
	}
}
