/*
 * Copyright 2012 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.treeadd;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;

/**
 * 
 * @author myebra
 * 
 */
@SuppressWarnings("serial")
public class AddStatesCheckBoxPanel extends JPanel {
    private List<JCheckBox>  checkBoxes = new ArrayList<JCheckBox>();
    private TreeADDBranch    branch;
    private TreeADDPotential treeADD;

    public AddStatesCheckBoxPanel(TreeADDBranch branch, TreeADDPotential treeADD) {
        this.branch = branch;
        this.treeADD = treeADD;
        initialize();
        repaint();
    }

    public void initialize() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // setLayout(new BorderLayout());
        Variable topVariable = branch.getRootVariable();
        State[] states = topVariable.getStates();

        for (State state : states) {
            if (branch.getBranchStates().contains(state)) {
                continue;
            }
            JCheckBox checkBox = new JCheckBox(state.getName());
            // checkBox.setLayout(new BorderLayout());
            checkBoxes.add(checkBox);
            // checkBox.setAlignmentY((float) 0.0);
            // checkBox.setAlignmentX((float) 0.5);
            add(checkBox, BorderLayout.CENTER);

        }

    }

    public TreeADDBranch getBranch() {
        return this.branch;
    }

    public TreeADDPotential getTreeADDPotential() {
        return this.treeADD;
    }

    public List<JCheckBox> getCheckBoxes() {
        return this.checkBoxes;
    }

}
