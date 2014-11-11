/*
 * Copyright 2012 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.window.dt;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

public class DecisionTreeCellRenderer implements
TreeCellRenderer
{

    @Override
    public Component getTreeCellRendererComponent (JTree tree,
                                                   Object object,
                                                   boolean selected,
                                                   boolean expanded,
                                                   boolean leaf,
                                                   int row,
                                                   boolean hasFocus)
    {
        if(object instanceof DecisionTreeElementPanel)
        {
            ((DecisionTreeElementPanel)object).update(selected, expanded, leaf, row, hasFocus);
        }
        return (Component)object;
    }
    
    
}
