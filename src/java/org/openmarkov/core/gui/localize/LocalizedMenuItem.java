/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.localize;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.openmarkov.core.gui.loader.element.IconLoader;

@SuppressWarnings("serial")
public class LocalizedMenuItem extends JMenuItem
{
    /**
     * Icon loader.
     */
    private static IconLoader iconLoader = new IconLoader();

    
    public LocalizedMenuItem(String name)
    {
        this.setName(name);
        this.setText(MenuLocalizer.getLabel(name));
        this.setMnemonic(MenuLocalizer.getMnemonic(name).charAt(0));
    }

    public LocalizedMenuItem(String name, String actionCommand)
    {
        this (name);
        this.setActionCommand (actionCommand);
    }
    
    public LocalizedMenuItem(String name, String actionCommand, String iconName)
    {
        this (name, actionCommand);
        this.setIcon (iconLoader.load(iconName));
    }
    

    public LocalizedMenuItem(String name, String actionCommand, String iconName, KeyStroke keyStroke)
    {
        this (name, actionCommand, iconName);
        this.setAccelerator (keyStroke);
    }
    
    
}
