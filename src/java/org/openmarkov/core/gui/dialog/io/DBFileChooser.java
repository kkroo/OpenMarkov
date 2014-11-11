/*
 * Copyright 2012 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.gui.dialog.io;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;

import javax.swing.JFileChooser;

import org.openmarkov.core.gui.configuration.OpenMarkovPreferences;
import org.openmarkov.core.io.database.plugin.CaseDatabaseManager;

@SuppressWarnings("serial")
public class DBFileChooser extends FileChooser
{
    protected static CaseDatabaseManager caseDbManager = new CaseDatabaseManager ();

    public DBFileChooser (boolean acceptAllFiles)
    {
        super (acceptAllFiles);
        File currentDirectory = new File (OpenMarkovPreferences.get (OpenMarkovPreferences.LAST_OPEN_DB_DIRECTORY,
                                                                     OpenMarkovPreferences.OPENMARKOV_DIRECTORIES,
                                                                     "."));
        setCurrentDirectory (currentDirectory);
    }

    @Override
    public int showOpenDialog (Component parent)
        throws HeadlessException
    {
        int result = super.showOpenDialog (parent);
        if(result == JFileChooser.APPROVE_OPTION)
        {
            OpenMarkovPreferences.set (OpenMarkovPreferences.LAST_OPEN_DB_DIRECTORY,
                                       getSelectedFile ().getAbsolutePath (),
                                       OpenMarkovPreferences.OPENMARKOV_DIRECTORIES);            
        }
        return result;
    }

    @Override
    public int showSaveDialog (Component parent)
        throws HeadlessException
    {
        int result = super.showSaveDialog (parent);
        if(result == JFileChooser.APPROVE_OPTION)
        {
            OpenMarkovPreferences.set (OpenMarkovPreferences.LAST_OPEN_DB_DIRECTORY,
                                       getSelectedFile ().getAbsolutePath (),
                                       OpenMarkovPreferences.OPENMARKOV_DIRECTORIES);            
        }
        return result;
    }

    
}
