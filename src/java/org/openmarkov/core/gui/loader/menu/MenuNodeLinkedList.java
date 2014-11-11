/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

/**
 * MenuNodeLinkedList class
 */
package org.openmarkov.core.gui.loader.menu;


import java.util.LinkedList;


/**
 * MenuNodeLinkedList defines an object to manipulate MenuNode objects prior to
 * define the final menus in the OpenMarkov Tool
 * 
 * @author jlgozalo
 * @version 1.0 18/11/2008
 */
public class MenuNodeLinkedList {

	private LinkedList<MenuNode> menuList = null;

	/**
	 * constructor
	 */
	public MenuNodeLinkedList() {

		this.menuList = new LinkedList<MenuNode>();
	}

	/**
	 * Look for the Menunode element in the list and returns the position
	 * 
	 * @param menuNode
	 * @return the position of the first ocurrence of a MenuNode element equal
	 *         to the specified element or -1 if no matching element is found
	 */
	public int indexOf(MenuNode menuNode) {

		return this.menuList.indexOf(menuNode);
	}

	/**
	 * Look for the name of the Menunode element in the list and returns the
	 * position
	 * 
	 * @param menuNode
	 * @return the position of the first ocurrence of a MenuNode element equal
	 *         to the specified element or -1 if no matching element is found
	 */
	public int indexOf(String sName) {

		int pos = -1;
		for (MenuNode nodo : menuList) {
			pos++;
			if (nodo.getName().equals(sName)) {
				// found!!!
				return pos;
			}
		}
		return pos;
	}

	/**
	 * @return the menuList
	 */
	public LinkedList<MenuNode> getMenuList() {

		return menuList;
	}

	/**
	 * @param menuList
	 *            the menuList to set
	 */
	public void setMenuList(LinkedList<MenuNode> menuList) {

		this.menuList = menuList;
	}

	/**
	 * @return the size
	 */
	public int getSize() {

		if (menuList != null) {
			return menuList.size();
		}
		return 0;

	}

	/**
	 * @return true if the element is empty (list=null)
	 */
	public boolean isEmpty() {

		return (getSize() == 0 ? true : false);
	}

	/**
	 * get the element at the required position
	 */
	public MenuNode getElementAt(int indexPos) {

		return this.menuList.get(indexPos);
	}

	/**
	 * toString method
	 * 
	 * @return String the element
	 */
	public String toString() {

		StringBuffer buf = new StringBuffer();
		buf.append("[MenuNodeLinkedList ->");
		buf.append(" size= " + getSize());
		buf.append(" ,elements= [");
		if (!this.isEmpty()) {
			for (MenuNode node : this.menuList) {
				buf.append("\n\t\t " + node.toString());
			}
		}
		buf.append(" ]");
		buf.append(" ]");
		return buf.toString();
	}

}
