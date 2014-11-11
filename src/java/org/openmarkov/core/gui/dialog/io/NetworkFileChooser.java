/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.io;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.util.HashMap;

import javax.swing.JFileChooser;

import org.openmarkov.core.gui.configuration.OpenMarkovPreferences;
import org.openmarkov.core.io.format.annotation.FormatManager;

/**
 * This class implements a file chooser dialog file to select OpenMarkov files.
 * @author ibermejo
 */
@SuppressWarnings("serial")
public class NetworkFileChooser extends FileChooser
{
    /**
     * Creates a new file chooser that starts in the current directory,
     * filtering the files with the file filters.
     */
    public NetworkFileChooser (boolean acceptAllfile)
    {
        super (acceptAllfile);
        FormatManager formatManager = FormatManager.getInstance ();
        HashMap<String, String> writers = formatManager.getWriters ();
        for (String item : writers.keySet ())
        {
            addChoosableFileFilter (new FileFilterAll (writers.get (item), item));
        }
        File currentDirectory = new File (
                                          OpenMarkovPreferences.get (OpenMarkovPreferences.LAST_OPEN_DIRECTORY,
                                                                     OpenMarkovPreferences.OPENMARKOV_DIRECTORIES,
                                                                     "."));
        setCurrentDirectory (currentDirectory);
        setFileFilter (OpenMarkovPreferences.get (OpenMarkovPreferences.LAST_OPENED_FORMAT,
                                                  OpenMarkovPreferences.OPENMARKOV_FORMATS, "pgmx"));
    }

    public NetworkFileChooser ()
    {
        this (false);
    }

    @Override
    public int showOpenDialog (Component parent)
        throws HeadlessException
    {
        int result = super.showOpenDialog (parent);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            OpenMarkovPreferences.set (OpenMarkovPreferences.LAST_OPEN_DIRECTORY,
                                       getSelectedFile ().getAbsolutePath (),
                                       OpenMarkovPreferences.OPENMARKOV_DIRECTORIES);            
            OpenMarkovPreferences.set (OpenMarkovPreferences.LAST_OPENED_FORMAT,
                                       ((FileFilterBasic) getFileFilter ()).getFilterExtension (),
                                       OpenMarkovPreferences.OPENMARKOV_FORMATS);
        }
        return result;
    }

    @Override
    public int showSaveDialog (Component parent)
        throws HeadlessException
    {
        int result = super.showSaveDialog (parent);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            OpenMarkovPreferences.set (OpenMarkovPreferences.LAST_OPEN_DIRECTORY,
                                       getSelectedFile ().getAbsolutePath (),
                                       OpenMarkovPreferences.OPENMARKOV_DIRECTORIES);            
            OpenMarkovPreferences.set (OpenMarkovPreferences.LAST_OPENED_FORMAT,
                                       ((FileFilterBasic) getFileFilter ()).getFilterExtension (),
                                       OpenMarkovPreferences.OPENMARKOV_FORMATS);
        }
        return result;
    }
}
