/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.treeadd;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;

/**
 * 
 * @author myebra
 * 
 */
@SuppressWarnings("serial")
public class AddVariablesCheckBoxPanel extends JPanel {

    private ArrayList<JCheckBox> checkBoxes = new ArrayList<JCheckBox>();
    private TreeADDBranch branch;
    private TreeADDPotential treeADD;

    public AddVariablesCheckBoxPanel(TreeADDBranch branch, TreeADDPotential treeADD) {
        // super();
        this.branch = branch;
        this.treeADD = treeADD;
        initialize();
        repaint();
    }

    public void initialize() {
        // setLayout(new BorderLayout());
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        List<Variable> possibleVariables = branch.getAddableVariables();
        for (Variable variable : possibleVariables) {
            JCheckBox checkBox = new JCheckBox(variable.getName());
            checkBoxes.add(checkBox);
            add(checkBox, CENTER_ALIGNMENT);
        }
    }

    public TreeADDBranch getBranch() {
        return this.branch;
    }

    public TreeADDPotential getTreeADDPotential() {
        return this.treeADD;
    }

    public ArrayList<JCheckBox> getCheckBoxes() {
        return this.checkBoxes;
    }
}
