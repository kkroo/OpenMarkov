/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

/**
 * OpenMarkov - ColorTableCellRenderer.java
 */
package org.openmarkov.core.gui.dialog.configuration;


import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;


/**
 * ColorTableCellRenderes handles the actions to allow users to select colors
 * from a ColorChooser
 * 
 * @author jlgozalo
 * @version 1.0 - 11 Mar 2010 initial version
 */
public class ColorTableCellRenderer extends JPanel implements TableCellRenderer {

	/**
	 * serialVersionUID 
	 */
	private static final long serialVersionUID = -803397354369463131L;

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value,
													boolean isSelected,
													boolean hasFocus, int row,
													int column) {

		setBackground( (Color) value );
		if (hasFocus) {
			setBorder( UIManager.getBorder( "Table.focusCellHighlightBorder" ) );
		} else {
			setBorder( null );
		}
		return this;
	}
}
