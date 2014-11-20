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


/**
 * Keys for the OpenMarkov Preferences
 * 
 * @author jlgozalo
 * @version 1.0
 * 30 Oct 2009
 *
 */
public interface OpenMarkovPreferencesKeys {

	// access
	public String LAST_CONNECTION = "last connection";
	public String LAST_USER_CONNECTED = "last user connected";
	
	// directories
	public String INITIALIZED = "initialized";
	public String LAST_OPEN_DIRECTORY = "last open directory";
	public String LAST_OPEN_FILE ="last open file ";
	public String LAST_OPEN_FILE_1 ="last open file 1";
	public String LAST_OPEN_FILE_2 ="last open file 2";
	public String LAST_OPEN_FILE_3 ="last open file 3";
	public String LAST_OPEN_FILE_4 ="last open file 4";
	public String LAST_OPEN_FILE_5 ="last open file 5";
	public String STRING_LANGUAGES_PATH = "languages directory path";
    public String LAST_OPEN_DB_DIRECTORY = "last open db directory";
	
	// positions
	public String X_OPENMARKOV_MAIN_FRAME = "x openmarkov main frame";
	public String Y_OPENMARKOV_MAIN_FRAME = "y openmarkov main frame";
	public String X_OPEMARKOV_HELP_DIMENSION = "xDimension Camen HelpViewer";
	public String Y_OPENMARKOV_HELP_DIMENSION = "yDimension OpenMarkov HelpViewer";
	
	// colors
	public String ARROW_BACKGROUND_COLOR = "arrow background";
	public String ARROW_FOREGROUND_COLOR = "arrow foreground";
	public String NODECHANCE_BACKGROUND_COLOR = "node chance background";
	public String NODECHANCE_FOREGROUND_COLOR = "node chance foreground";
	public String NODECHANCE_TEXT_COLOR = "node chance text";
	public String NODEDECISION_BACKGROUND_COLOR = "node decision background";
	public String NODEDECISION_FOREGROUND_COLOR = "node decision foreground";
	public String NODEDECISION_TEXT_COLOR = "node decision text";
	public String NODEUTILITY_BACKGROUND_COLOR = "node utility background";
	public String NODEUTILITY_FOREGROUND_COLOR = "node utility foreground";
	public String NODEUTILITY_TEXT_COLOR = "node utility text";
	public String TABLE_HEADER_TEXT_COLOR_1 = "tableheader first row";
	public String TABLE_HEADER_TEXT_COLOR_2 = "tableheader second row";
	public String TABLE_HEADER_TEXT_COLOR_3 = "tableheader third row";
	public String TABLE_HEADER_TEXT_BACKGROUND_COLOR_1 = "tableheader background 1";
	public String TABLE_HEADER_TEXT_BACKGROUND_COLOR_2 = "tableheader background 2";
	public String TABLE_FIRST_COLUMN_FOREGROUND_COLOR = "table first column foreground";
	public String TABLE_FIRST_COLUMN_BACKGROUND_COLOR = "table first column background";
	public String TABLE_CELLS_FOREGROUND_COLOR = "table cells foreground";
	public String TABLE_CELLS_BACKGROUND_COLOR = "table cells background";
	public String ALWAYS_OBSERVED_VARIABLE= "always observed variable border color";
	public String REVELATION_ARC_VARIABLE= "revelation arc color";
	public String SELECTED_NODE_COLOR = "selected node color";
	
	// languages
	public String PREFERENCE_LANGUAGE = "user prefered language";
	
	// parsers & writers
    public String LAST_OPENED_FORMAT = "last opened format";
	public String LAST_SAVED_FORMAT = "last saved format";
    public String LAST_LOADED_EVIDENCE_FORMAT = "last loaded evidence format";

	
}
