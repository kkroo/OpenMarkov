package org.openmarkov.core.gui.oopn;

/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

import java.util.List;

import org.openmarkov.core.gui.graphic.SelectionListener;
import org.openmarkov.core.gui.graphic.VisualLink;
import org.openmarkov.core.gui.graphic.VisualNode;

/**
 * This interface is implemented by classes interested in objects selected in 
 * a VisualOONetwork
 * 
 * @author ibermejo
 * @version 1.0
 *             
 */
public interface OOSelectionListener extends SelectionListener {

    /**
     * This method indicates the selected elements
     * 
     * @param selectedNodes
     *            array of nodes that are currently selected
     * @param selectedLinks
     *            array of links that are currently selected
     * @param selectedInstances
     *            array of instances that are currently selected
     */
    void objectsSelected(List<VisualNode> selectedNodes,
            List<VisualLink> selectedLinks,
            List<VisualInstance> selectedInstances,
            List<VisualReferenceLink> selectedReferenceLinks);
    
}


