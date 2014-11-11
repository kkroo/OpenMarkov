/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

/**
 * MenuNode class
 */
package org.openmarkov.core.gui.loader.menu;


import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;


/**
 * MenuNode encapsulates the definition for a entry in the OpenMarkov Menu when
 * reading from files prior to generate the definitive JMenuXX components.
 * 
 * @author jlgozalo
 * @version 1.0 18/11/2008
 */
public class MenuNode {

	/**
	 * name of the node of the menu
	 */
	private String name = "";
	/**
	 * kind of object that is stored in the MenuNode <br>
	 * the object could be: JMenu, JMenuItem, JSeparator, JRadioMenuItem,
	 * JCheckBoxMenuItem
	 */
	private JComponent object = null;
	/**
	 * If object is JMenu, then list will be used to stored the future SubMenu
	 * structure
	 */
	private MenuNodeLinkedList list = null;

	/**
	 * constructor
	 */
	public MenuNode() {

	}

	/**
	 * constructor
	 * 
	 * @param name
	 *            name of the node of the menu
	 * @param object
	 *            kind of object to store
	 */
	public MenuNode(String name, JComponent object) {

		this.setName(name);
		this.setObject(object);
		if (object instanceof JMenu || object instanceof JMenuBar) {
			this.setList(new MenuNodeLinkedList());
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {

		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {

		this.name = name;
	}

	/**
	 * @return the object
	 */
	public JComponent getObject() {

		return object;
	}

	/**
	 * @param object
	 *            the object to set
	 */
	public void setObject(JComponent object) {

		this.object = object;
	}

	/**
	 * @return the list
	 */
	public MenuNodeLinkedList getList() {

		return list;
	}

	/**
	 * @param list
	 *            the list to set
	 */
	public void setList(MenuNodeLinkedList list) {

		this.list = list;
	}

	/**
	 * toString
	 */
	public String toString() {

		StringBuffer buf = new StringBuffer();
		buf.append("[MenuNode ->");
		buf.append(" name= " + this.name);
		buf.append(" ,object=" + this.getObject().getName());
		if (this.list != null) {
			buf.append(" ,listSize= " + this.getList().getSize());
			buf.append("\n\t\t" + this.getList().toString());
		} else {
			buf.append(" ,listSize= 0");
		}
		buf.append(" ]");
		return buf.toString();
	}
}
