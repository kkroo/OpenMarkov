/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.loader.element;

import java.net.URL;
import java.util.MissingResourceException;

import javax.swing.ImageIcon;

import org.openmarkov.core.gui.localize.StringDatabase;

/**
 * This class is used to load icons from a folder.
 * @author jlgozalo
 * @version 1.0 jlgozalo 25/08 based on IconLoader
 */
public class ImageLoader
{
    /**
     * Folder where icons are saved.
     */
    // TODO must be externalize in a property
    private static final String RESOURCE_IMAGES_PATH = "images/";

    /**
     * This method loads an image resource.
     * @param imageName name of the image to load.
     * @return a reference to the image resource.
     * @throws MissingResourceException if the resource doesn't exist.
     */
    public ImageIcon load (String imageName)
        throws MissingResourceException
    {
        URL icon = getClass ().getClassLoader ().getResource (RESOURCE_IMAGES_PATH + imageName);
        if (icon == null)
        {
            throw new MissingResourceException (
                                                StringDatabase.getUniqueInstance ().getString ("ImageResourceNotExists.Text.Label")
                                                        + " " + RESOURCE_IMAGES_PATH + imageName,
                                                getClass ().getName (), imageName);
        }
        return new ImageIcon (icon);
    }
}
