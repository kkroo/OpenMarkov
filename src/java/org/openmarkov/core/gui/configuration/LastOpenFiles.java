/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

/**
 * 
 */
package org.openmarkov.core.gui.configuration;
import static org.openmarkov.core.gui.configuration.OpenMarkovPreferencesKeys.LAST_OPEN_FILE;


/**
 * Utility class to store the last open files
 * @author jlgozalo
 * @version 1.0 25 Jul 2009
 */
public class LastOpenFiles {

	/**
	 * maximum number of last open files per OPENMARKOV session
	 */
	// TODO to be configured by an external configuration file
	public static final int MAX_LAST_OPEN_FILES = 5;

	/**
	 * retrieves the name of the file that is located in the position index
	 * @param index - the position of file in the list of last open files
	 * @return the fileName or empty
	 */
	public String getFileNameAt(int index) {

		return OpenMarkovPreferences.get(LAST_OPEN_FILE + index,
										OpenMarkovPreferences.OPENMARKOV_DIRECTORIES,"");
	}

	/**
	 * @param fileName
	 *            the FileName to set
	 * @param index
	 *            position of the file
	 */
	public void setFileNameAt(String fileName, int index) {

		OpenMarkovPreferences.set(LAST_OPEN_FILE + index, fileName,
								OpenMarkovPreferences.OPENMARKOV_DIRECTORIES);
	}

	/**
	 * reorder the list of last open files considering that if the file was
	 * already open, only some of the files must be reorder
	 * @param fileName - name of the file to find
	 */
	public void setLastFileName(String fileName) {

		int aux = 0;
		int index = -1;
		int lastIndex = 0;
		if (existLastOpenFiles()) {
			index = getIndexForFilename(fileName);
			lastIndex = getOldestOpenFileIndex();
			if (lastIndex < MAX_LAST_OPEN_FILES) {
				lastIndex++;
			} else {
				lastIndex = MAX_LAST_OPEN_FILES;
			}
			index = (index == -1 ? lastIndex : index);
			for (int i = index; i > 1; i--) {
				aux = i - 1;
				OpenMarkovPreferences.set(LAST_OPEN_FILE + i, 
				                      OpenMarkovPreferences.get(
				                         LAST_OPEN_FILE + aux,
										 OpenMarkovPreferences.OPENMARKOV_DIRECTORIES,""),
									  OpenMarkovPreferences.OPENMARKOV_DIRECTORIES);
			}
		}
		OpenMarkovPreferences.set(LAST_OPEN_FILE + 1, 
		                      fileName,
		                      OpenMarkovPreferences.OPENMARKOV_DIRECTORIES);
	}

	/**
	 * retrieves the position of a specific file in the list of last open files 
	 * 
	 * @param fileName - name of the file to find the position
	 * @return index for the filename if exist; otherwise, return -1
	 */
	public int getIndexForFilename(String fileName) {

		int result = -1;
		int index = 1;

		for (index = 1; index <= MAX_LAST_OPEN_FILES; index++) {
			if (fileName.equals(OpenMarkovPreferences
							.get(LAST_OPEN_FILE + index,
									OpenMarkovPreferences.OPENMARKOV_DIRECTORIES,""))) {
				result = index;
				break;
			}
		}
		return result;
	}

	/**
	 * @return true if there are some last open files; false otherwise
	 */
	public boolean existLastOpenFiles() {

		boolean result = false;
		String fileName = "";
		fileName = OpenMarkovPreferences
						.get(LAST_OPEN_FILE + 1,
								OpenMarkovPreferences.OPENMARKOV_DIRECTORIES,"");
		if (!fileName.equals("")) {
			result = true;
		}

		return result;
	}

	/**
	 * @return index the index for the oldest open file
	 */
	public int getOldestOpenFileIndex() {

		int index = 1;

		for (index = 1; index < MAX_LAST_OPEN_FILES; index++) {
			if (OpenMarkovPreferences.get(LAST_OPEN_FILE + index,
										OpenMarkovPreferences.OPENMARKOV_DIRECTORIES,"")
							.equals("")) {
				index--; // the last one is the previous index
				break;
			}
		}
		return index;
	}
}
