/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.menutoolbar.menu;

import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.openmarkov.core.gui.loader.element.IconLoader;
import org.openmarkov.core.gui.localize.LocalizedMenuItem;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.core.gui.menutoolbar.common.MenuItemNames;

/**
 * This class implements a contextual menu that shows when a user click on tha
 * background of a network panel.
 * @author jmendoza
 * @author jlgozalo
 * @version 1.1 jlgozalo - Add change locale management setting the item names.
 */
class NetworkContextualMenu extends ContextualMenu
{
    /**
     * Static field for serializable class.
     */
    private static final long serialVersionUID          = 3673127766586232832L;
    /**
     * Object that represents the item 'Paste'.
     */
    private JMenuItem         pasteMenuItem             = null;
    /**
     * Object that represents the item 'Network additionalProperties'.
     */
    private JMenuItem         networkPropertiesMenuItem = null;
    private JMenuItem         expandNetworkMenuItem;
    private JMenuItem         expandNetworCEkMenuItem;
    private boolean           canBeExpanded;

    /**
     * This constructor creates a new instance.
     * @param newListener object that listens to the menu events.
     */
    public NetworkContextualMenu (ActionListener newListener, boolean canBeExpanded)
    {
        super (newListener);
        this.canBeExpanded = canBeExpanded;
        initialize ();
    }

    /**
     * This method initializes this instance.
     */
    private void initialize ()
    {
        add (getPasteMenuItem ());
        addSeparator ();
        add (getNetworkPropertiesMenuItem ());
        if (canBeExpanded)
        {
            addSeparator ();
            add (getExpandedNetworkMenuItem ());
            addSeparator ();
            add (getExpandedNetworkCEMenuItem ());
        }
    }

    /**
     * This method initializes pasteMenuItem.
     * @return a new 'Paste' menu item.
     */
    private JMenuItem getExpandedNetworkMenuItem ()
    {
        if (expandNetworkMenuItem == null)
        {
            expandNetworkMenuItem = new LocalizedMenuItem (MenuItemNames.EXPAND_NETWORK_MENUITEM,
                                                           ActionCommands.EXPAND_NETWORK);
            expandNetworkMenuItem.addActionListener (listener);
        }
        return expandNetworkMenuItem;
    }

    /**
     * This method initializes pasteMenuItem.
     * @return a new 'Paste' menu item.
     */
    private JMenuItem getExpandedNetworkCEMenuItem ()
    {
        if (expandNetworCEkMenuItem == null)
        {
            expandNetworCEkMenuItem = new LocalizedMenuItem (
                                                             MenuItemNames.EXPAND_NETWORK_CE_MENUITEM,
                                                             ActionCommands.EXPAND_NETWORK_CE);
            expandNetworCEkMenuItem.addActionListener (listener);
        }
        return expandNetworCEkMenuItem;
    }

    /**
     * This method initializes pasteMenuItem.
     * @return a new 'Paste' menu item.
     */
    private JMenuItem getPasteMenuItem ()
    {
        if (pasteMenuItem == null)
        {
            pasteMenuItem = new LocalizedMenuItem (
                                                   MenuItemNames.EDIT_PASTE_MENUITEM,
                                                   ActionCommands.CLIPBOARD_PASTE,
                                                   IconLoader.ICON_PASTE_ENABLED,
                                                   KeyStroke.getKeyStroke (KeyEvent.VK_V,
                                                                           InputEvent.CTRL_DOWN_MASK));
            pasteMenuItem.addActionListener (listener);
        }
        return pasteMenuItem;
    }

    /**
     * This method initializes networkPropertiesMenuItem.
     * @return a new 'Network additionalProperties' menu item.
     */
    private JMenuItem getNetworkPropertiesMenuItem ()
    {
        if (networkPropertiesMenuItem == null)
        {
            networkPropertiesMenuItem = new LocalizedMenuItem (
                                                               MenuItemNames.FILE_NETWORKPROPERTIES_MENUITEM,
                                                               ActionCommands.NETWORK_PROPERTIES);
            networkPropertiesMenuItem.addActionListener (listener);
        }
        return networkPropertiesMenuItem;
    }

    /**
     * Returns the component that corresponds to an action command.
     * @param actionCommand action command that identifies the component.
     * @return a components identified by the action command.
     */
    @Override
    protected JComponent getJComponentActionCommand (String actionCommand)
    {
        JComponent component = null;
        if (actionCommand.equals (ActionCommands.CLIPBOARD_PASTE))
        {
            component = pasteMenuItem;
        }
        else if (actionCommand.equals (ActionCommands.NETWORK_PROPERTIES))
        {
            component = networkPropertiesMenuItem;
        }
        return component;
    }
}
