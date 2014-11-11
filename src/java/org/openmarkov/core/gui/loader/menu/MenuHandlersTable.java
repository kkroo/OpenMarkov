/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.loader.menu;

import java.util.Hashtable;

import javax.swing.JMenuItem;

/**
 * The table stores the defined menu handlers associated to the menu items. Even
 * if there is any element that has not an already proper element (default
 * settings), try to find the corresponding class. If not, there will be an
 * error
 * 
 * @author jlgozalo
 * @version 1.0 06/12/2008
 */
public class MenuHandlersTable {

    /** MenuHandlersTable unique instance. Used in singleton pattern. */
    private static MenuHandlersTable                  menuHandlersTable = null;
    /** htMenuItemHandlers table to store the set of menu handlers */
    private static Hashtable<Object, MenuItemHandler> htMenuItemHandlers;

    /** @return MenuHandlersTable unique instance (singleton pattern). */
    public static MenuHandlersTable getUniqueInstance() {

        if (menuHandlersTable == null) {
            menuHandlersTable = new MenuHandlersTable();
        }
        return menuHandlersTable;
    }

    /**
     * constructor
     */

    private MenuHandlersTable() {

        htMenuItemHandlers = new Hashtable<Object, MenuItemHandler>();
    }

    /**
     * Register a menu item handler in the handler table by name
     * 
     * @param sHandlerName
     *            the name of the handler for the element
     * @param mih
     *            the MenuItem handler
     */
    public void registerMenuItemHandler(String sHandlerName, MenuItemHandler mih) {

        htMenuItemHandlers.put(sHandlerName, mih);
    }

    /**
     * Register a menu item handler in the handler table by menu item
     * 
     * @param mi
     *            the menu item
     * @param mih
     *            the handler for the menu item
     */
    public void registerMenuItemHandler(JMenuItem mi, MenuItemHandler mih) {

        htMenuItemHandlers.put(mi, mih);
    }

    /**
     * Given a MenuItem, return its handler
     * 
     * @param mi
     *            the menu item which handler we are looking for
     * @return the menu item handler
     */
    public MenuItemHandler menuitemhandlerFind(JMenuItem mi) {

        Object oHandler = htMenuItemHandlers.get(mi);
        return (MenuItemHandler) oHandler;
    }

    /**
     * Get a MenuItemHandler by name
     * 
     * @param sName
     *            the name of the menu item handler we are looking for
     * @return the menu item handler
     */
    public MenuItemHandler menuitemhandlerFind(String sName) {

        if (sName == null) {
            return null;
        }
        MenuItemHandler mih = htMenuItemHandlers.get(sName);

        // Not registered. See if it's a class name, and if it is, create an
        // instance of that class and register it.
        if (mih == null) {
            try {
                Class<?> classOfHandler = Class.forName(sName);
                MenuItemHandler newHandler = (MenuItemHandler) classOfHandler.newInstance();
                registerMenuItemHandler(sName, newHandler);
                mih = newHandler;
            } catch (Exception ex) {
                System.err.println("Couldn't find menu item handler '"
                        + sName
                        + ": no such registered handler, and couldn't create");
                System.err.println(sName + ": " + ex.getClass().getName() + ": " + ex.getMessage());
            }
        }
        return mih;
    }

}
