/*
* Copyright 2012 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package org.openmarkov.core.gui.window.edition.mode;

import org.openmarkov.core.gui.window.edition.EditorPanel;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;

@EditionState(name="Edit.Mode.Decision", icon="decision.gif", cursor="decision.gif")
public class DecisionNodeEditionMode extends NodeEditionMode
{

    public DecisionNodeEditionMode (EditorPanel editorPanel,
                                  ProbNet probNet)
    {
        super (editorPanel, probNet, NodeType.DECISION);
    }
}
