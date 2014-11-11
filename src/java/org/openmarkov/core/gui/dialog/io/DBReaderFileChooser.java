/*
 * Copyright 2012 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.gui.dialog.io;

import java.util.HashMap;

@SuppressWarnings("serial")
public class DBReaderFileChooser extends DBFileChooser
{
    public DBReaderFileChooser (boolean acceptAllFiles)
    {
        super(acceptAllFiles);
        HashMap<String, String> writersInfo = caseDbManager.getAllReaders ();
        for(String extension : writersInfo.keySet ())
        {
            addChoosableFileFilter(new FileFilterAll(extension, writersInfo.get (extension)));
        }        
    }
    
    public DBReaderFileChooser ()
    {
        this(false);
    }
}
