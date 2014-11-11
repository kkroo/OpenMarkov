/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.treeadd;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;

public class TreeADDUserInterface extends BasicTreeUI
{
    /*
     * (non-Javadoc)
     * @see
     * javax.swing.plaf.basic.BasicTreeUI#paintExpandControl(java.awt.Graphics,
     * java.awt.Rectangle, java.awt.Insets, java.awt.Rectangle,
     * javax.swing.tree.TreePath, int, boolean, boolean, boolean)
     */
    protected void paintExpandControl (Graphics g,
                                       Rectangle clipBounds,
                                       Insets insets,
                                       Rectangle bounds,
                                       TreePath path,
                                       int row,
                                       boolean isExpanded,
                                       boolean hasBeenExpanded,
                                       boolean isLeaf)
    {
        Object value = path.getLastPathComponent ();
        // Draw icons if not a leaf and either hasn't been loaded,
        // or the model child count is > 0.
        if (!isLeaf && (!hasBeenExpanded || treeModel.getChildCount (value) > 0))
        {
            int middleXOfKnob;
            if (tree.getComponentOrientation ().isLeftToRight ())
            {
                middleXOfKnob = bounds.x - (getRightChildIndent () - 1);
            }
            else
            {
                middleXOfKnob = bounds.x + bounds.width + getRightChildIndent ();
            }
            int middleYOfKnob = bounds.y + (bounds.height / 2);
            if (isExpanded)
            {
                Icon expandedIcon = getExpandedIcon ();
                if (expandedIcon != null
                    && (value instanceof TreeADDBranch || value instanceof Potential))
                {
                    drawCentered (tree, g, expandedIcon, middleXOfKnob, middleYOfKnob);
                }
            }
            else
            {
                Icon collapsedIcon = getCollapsedIcon ();
                if (collapsedIcon != null
                    && (value instanceof TreeADDBranch || value instanceof Potential))
                {
                    drawCentered (tree, g, collapsedIcon, middleXOfKnob, middleYOfKnob);
                }
            }
        }
    }
}
