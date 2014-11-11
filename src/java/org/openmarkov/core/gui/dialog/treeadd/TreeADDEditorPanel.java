/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.treeadd;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.core.gui.dialog.node.NodePropertiesDialog;
import org.openmarkov.core.gui.dialog.node.PotentialEditDialog;
import org.openmarkov.core.gui.localize.LocalizedMenuItem;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.UniformPotential;
import org.openmarkov.core.model.network.potential.treeadd.Threshold;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;

/**
 * <code>JScrollPane<code> for creating and modifying <code>TreeADDPotential<code>s
 * 
 * @author jfernandez
 * @author myebra
 */
public class TreeADDEditorPanel extends JScrollPane implements ActionListener {
    private static final long  serialVersionUID    = -6230911169585766424L;
    private StringDatabase     stringDatabase      = StringDatabase.getUniqueInstance();

    protected JPopupMenu       contextualMenu      = new JPopupMenu();
    // menu to start painting the treeADD with the panel in blank
    protected JMenu            submenuAddStartTree = new JMenu();
    // when clicking a branch you can set a potential or add a subtree to that
    // branch
    protected JMenu            addSubtree          = new JMenu(stringDatabase.getString("TreeADD.AddSubtree"));
    protected JMenu            changeRootVariable  = new JMenu(stringDatabase.getString("TreeADD.ChangeVariable"));
    protected JMenuItem        editPotential       = new LocalizedMenuItem("TreeADD.EditPotential",
                                                           ActionCommands.EDIT_POTENTIAL);
    protected JMenuItem        associateStates     = new LocalizedMenuItem("TreeADD.JoinBranches",
                                                           ActionCommands.JOIN_BRANCHES);
    protected JMenuItem        dissociateStates    = new LocalizedMenuItem("TreeADD.DissociateStates",
                                                           ActionCommands.REMOVE_STATES);
    protected JMenuItem        removeVariables     = new LocalizedMenuItem("TreeADD.RemoveVariables",
                                                           ActionCommands.REMOVE_VARIABLES);
    protected JMenuItem        removeSubtree       = new LocalizedMenuItem("TreeADD.RemoveSubtree",
                                                           ActionCommands.REMOVE_SUBTREE);
    protected JMenuItem        addVariables        = new LocalizedMenuItem("TreeADD.AddVariables",
                                                           ActionCommands.ADD_VARIABLES);
    protected JMenuItem        splitInterval       = new LocalizedMenuItem("TreeADD.SplitInterval",
                                                           ActionCommands.SPLIT_INTERVAL);
    protected JMenuItem        changeInterval      = new LocalizedMenuItem("TreeADD.ChangeInterval",
                                                           ActionCommands.CHANGE_INTERVAL);
    protected JMenuItem        setLabel            = new LocalizedMenuItem("TreeADD.SetLabel",
                                                           ActionCommands.SET_LABEL);
    protected JMenuItem        removeLabel         = new LocalizedMenuItem("TreeADD.RemoveLabel",
                                                           ActionCommands.REMOVE_LABEL);
    protected JMenuItem        setReference        = new LocalizedMenuItem("TreeADD.SetReference",
                                                           ActionCommands.SET_REFERENCE);
    protected JMenuItem        removeReference     = new LocalizedMenuItem("TreeADD.RemoveReference",
                                                           ActionCommands.REMOVE_REFERENCE);

    protected TreeADDPotential rootTreeADDPotential;
    protected JTree            jTree;
    protected boolean          readOnlyMode;
    // Variables of the treeADDPotential root of the tree
    protected List<Variable>   treeVariables;
    // Mouse event detection
    private int                xx, yy;
    private ProbNode           probNode;

    /**
     * Shows the tree in read only mode
     * 
     * @param probNet
     * @param treeADDPotential
     */
    public TreeADDEditorPanel(TreeADDCellRenderer cellRenderer, ProbNode probNode) {
        // A copy of the potential
        this.probNode = probNode;
        this.rootTreeADDPotential = new TreeADDPotential((TreeADDPotential) probNode.getPotentials().get(0));
        readOnlyMode = false;
        setupUserInterface(cellRenderer);
    }

    private void setupUserInterface(TreeADDCellRenderer cellRenderer) {
        TreeADDModel model = new TreeADDModel(rootTreeADDPotential);
        jTree = new JTree(model);
        jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree.addTreeExpansionListener(new TreeADDExpansionAdapter(this));
        jTree.addTreeWillExpandListener(new TreeADDWillExpandAdapter(this));
        // Allows JTree nodes to accept CR/LF codes
        jTree.setShowsRootHandles(true);
        jTree.setRowHeight(0);
        jTree.setCellRenderer(cellRenderer);
        jTree.setUI(new TreeADDUserInterface());
        for (int i = 0; i < jTree.getRowCount(); i++) {
            jTree.expandRow(i);
        }
        setViewportView(jTree);
        // Menu text initialization
        if (!readOnlyMode) {
            jTree.addMouseListener(new TreeADDMouseAdapter(this));
            contextualMenu.setInvoker(jTree);
            // menu to create the root treeADD start painting the tree
            submenuAddStartTree.setText(stringDatabase.getString("TreeADD.StartNode"));
            // menu to add a subtree to a branch
            addVariables.addActionListener(this);
            editPotential.addActionListener(this);
            associateStates.addActionListener(this);
            dissociateStates.addActionListener(this);
            removeVariables.addActionListener(this);
            removeSubtree.addActionListener(this);
            splitInterval.addActionListener(this);
            changeInterval.addActionListener(this);
            setLabel.addActionListener(this);
            setReference.addActionListener(this);
            removeLabel.addActionListener(this);
            removeReference.addActionListener(this);
        }
    }

    public TreeADDPotential getTreePotential() {
        return rootTreeADDPotential;
    }

    public void treeExpanded(TreeExpansionEvent event) {
        TreePath treepath = event.getPath();
        Object tn1 = treepath.getLastPathComponent();
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        if (!model.isLeaf(tn1)) {// the object is a treeADDPotential
            // Expand path adding branches to the treeADD path
            for (int i = 0; i < model.getChildCount(tn1); i++) {
                Object child = model.getChild(tn1, i);// this must be a branch
                jTree.expandPath(treepath.pathByAddingChild(child));
            }
        }
    }

    /**
     * @param event
     * @throws ExpandVetoException
     */
    public void treeWillCollapse(TreeExpansionEvent event)
            throws ExpandVetoException {
        Object triedToExpand = event.getPath().getLastPathComponent();
        if (!(triedToExpand instanceof TreeADDPotential)
                && !(triedToExpand instanceof TreeADDBranch)) {
            // Exception used to stop and expand/collapse from happening.
            throw new ExpandVetoException(event);
        }
    }

    // When clicking on a tree only can change top variable. If the tree does
    // not have subtrees trees are only permitted to change buttom up order
    protected void setContextualMenuTreeADD(MouseEvent e, TreeADDPotential treeADD) {
        contextualMenu.removeAll();
        changeRootVariable.removeAll();
        // change root variable
        List<TreeADDBranch> branches = treeADD.getBranches();
        boolean hasSubTrees = false;
        for (TreeADDBranch branch : branches) {
            if (branch.getPotential() instanceof TreeADDPotential) {
                hasSubTrees = true;
            }
        }
        Variable currentRootVariable = treeADD.getRootVariable();
        Variable conditionedVariable = treeADD.getConditionedVariable();
        List<Variable> newPosibleRootVariables = new ArrayList<Variable>();
        for (Variable variable : treeADD.getVariables()) {
            if (variable != currentRootVariable && variable != conditionedVariable) {
                newPosibleRootVariables.add(variable);
            }
        }
        if (!hasSubTrees && newPosibleRootVariables.size() != 0) {
            for (Variable variable : treeADD.getVariables()) {
                if (variable != currentRootVariable && variable != conditionedVariable) {
                    JMenuItem posibleRootVariable = new JMenuItem(variable.getName());
                    posibleRootVariable.addActionListener(this);
                    posibleRootVariable.setActionCommand(ActionCommands.CHANGE_ROOT_VARIABLE);
                    changeRootVariable.add(posibleRootVariable);
                }
            }
            contextualMenu.add(changeRootVariable);
        }
    }

    /**
     * @param e
     * @param branch
     */
    protected void setContextualMenuBranch(MouseEvent e, TreeADDBranch branch, TreePath branchPath) {
        contextualMenu.removeAll();
        addSubtree.removeAll();
        associateStates.removeAll();
        dissociateStates.removeAll();
        removeSubtree.removeAll();
        splitInterval.removeAll();
        changeInterval.removeAll();

        List<Variable> possibleVariables = possibleTopVariables(branch, branchPath);

        // if {variables}-{rootVariable}-{conditionedVariable} is not empty
        // so you can add also a subtree to the branch
        if (!possibleVariables.isEmpty() && !(branch.getPotential() instanceof TreeADDPotential)) {
            for (Variable variable : possibleVariables) {
                // To add possible rootVariables popups
                JMenuItem posibleRootVariable = new JMenuItem(variable.getName());
                posibleRootVariable.setActionCommand(ActionCommands.ADD_SUBTREE);
                posibleRootVariable.addActionListener(this);
                addSubtree.add(posibleRootVariable);
            }
            contextualMenu.add(addSubtree);
            contextualMenu.add(new JSeparator());
        }
        // remove subtree
        if (branch.getPotential() instanceof TreeADDPotential) {
            contextualMenu.add(removeSubtree);
            contextualMenu.add(new JSeparator());
        }
        // dissociate branches
        if (branch.getRootVariable().getVariableType() == VariableType.FINITE_STATES) {
            // joining branches
            contextualMenu.add(associateStates);
            if (branch.getBranchStates().size() > 1) {
                contextualMenu.add(dissociateStates);
            }
        } else if (branch.getRootVariable().getVariableType() == VariableType.NUMERIC) {
            // Split intervals
            contextualMenu.add(splitInterval);
            // Change interval
            double minDomainLimit = branch.getRootVariable().getPartitionedInterval().getMin();
            double maxDomainLimit = branch.getRootVariable().getPartitionedInterval().getMax();
            double min = branch.getLowerBound().getLimit();
            double max = branch.getUpperBound().getLimit();
            if (minDomainLimit == min && maxDomainLimit == max) {
                // do not offer the possibility to change domain
            } else {
                contextualMenu.add(changeInterval);
            }
        }
        if (!branch.isReference() && !branch.isLabeled()) {
            contextualMenu.add(new JSeparator());
            contextualMenu.add(setLabel);
        }
        if (branch.isLabeled()) {
            contextualMenu.add(new JSeparator());
            contextualMenu.add(removeLabel);
        }

        Map<String, TreeADDBranch> labeledPotentials = rootTreeADDPotential.getLabeledBranches();
        boolean suitableLabeledPotentialFound = false;
        for (TreeADDBranch labeledBranch : labeledPotentials.values()) {
            Potential labeledPotential = labeledBranch.getPotential();
            suitableLabeledPotentialFound |= branch.getParentVariables().containsAll(labeledPotential.getVariables());
        }
        if (suitableLabeledPotentialFound) {
            contextualMenu.add(new JSeparator());
            contextualMenu.add(setReference);
        }
        if (branch.isReference()) {
            contextualMenu.add(new JSeparator());
            contextualMenu.add(removeReference);
        }
    }

    /**
     * @param e
     * @param branch
     */
    protected void setContextualMenuPotential(MouseEvent e,
            TreeADDBranch branch,
            TreePath branchPath) {
        contextualMenu.removeAll();
        addVariables.removeAll();
        removeVariables.removeAll();

        Potential potential = branch.getPotential();
        // Adding treeADD
        List<Variable> variables = branch.getParentVariables();
        if (potential.isUtility()) {
            variables.add(potential.getUtilityVariable());
        }
        List<Variable> possibleTopVariables = possibleTopVariables(branch, branchPath);
        List<Variable> addableVariables = branch.getAddableVariables();
        possibleTopVariables.addAll(addableVariables);

        // Potential Edition, any case it is possible to edit branch's potential
        if (!(potential instanceof TreeADDPotential)) {
            contextualMenu.add(editPotential);
            contextualMenu.add(new JSeparator());
            // Adding Variables to potential
            if (!addableVariables.isEmpty()) {
                contextualMenu.add(addVariables);
            }
            // remove potential variables
            if (potential.getVariables().size() > 1) {
                contextualMenu.add(removeVariables);
            }
        }
    }

    /**
     * Finds the list of variables that can be added to the potential
     * 
     * @param branch
     * @param branchPath
     * @return
     */
    private List<Variable> possibleTopVariables(TreeADDBranch branch, TreePath branchPath) {
        List<Variable> possibleTopVariables = new ArrayList<>(branch.getParentVariables());

        possibleTopVariables.remove(branch.getRootVariable());
        possibleTopVariables.remove(branch.getPotential().getConditionedVariable());

        // Also it could be selected a top variable that has appeared
        // previously in the tree but only if
        // in current path that variable groups different states
        TreePath parentPath = branchPath.getParentPath(); // treeADD
        while (parentPath.getLastPathComponent() != rootTreeADDPotential) {
            TreePath grandParentPath = parentPath.getParentPath();// branch
            if (grandParentPath.getLastPathComponent() instanceof TreeADDBranch) {
                // if a branch has more then one state is possible to offer, as
                // top variable, the top variable of this branch
                TreeADDBranch treeBranch = (TreeADDBranch) grandParentPath.getLastPathComponent();
                if (treeBranch.getRootVariable().getVariableType() == VariableType.FINITE_STATES) {
                    if (treeBranch.getBranchStates().size() > 1) {
                        possibleTopVariables.add(treeBranch.getRootVariable());
                    }
                }
            }
            parentPath = grandParentPath;
        }
        // Also it could be selected as a top variable a numeric variable that
        // has already appeared in the tree
        TreePath path = branchPath.getParentPath();
        while (path.getLastPathComponent() != rootTreeADDPotential) {
            if (path.getLastPathComponent() instanceof TreeADDBranch) {
                TreeADDBranch treeBranch = (TreeADDBranch) path.getLastPathComponent();
                if (treeBranch.getRootVariable().getVariableType() == VariableType.NUMERIC) {
                    if (treeBranch.getLowerBound().getLimit() != treeBranch.getUpperBound().getLimit()) {
                        possibleTopVariables.add(treeBranch.getRootVariable());
                    }
                }
            }
            path = path.getParentPath();
        }
        return possibleTopVariables;
    }

    /**
	 * 
	 */
    public void actionPerformed(ActionEvent ae) {
        String actionComand = ae.getActionCommand();
        TreePath path = jTree.getPathForLocation(xx, yy);
        Object node = path.getLastPathComponent();
        if (actionComand.equals(ActionCommands.ADD_SUBTREE)) {
            if (node instanceof Potential) {
                path = path.getParentPath();
                node = path.getLastPathComponent();
            }
            // node must be a branch
            addSubtree(ae, (TreeADDBranch) node, path);
        } else if (actionComand.equals(ActionCommands.EDIT_POTENTIAL)) {
            if (node instanceof Potential) {
                path = path.getParentPath();
                node = path.getLastPathComponent();
            }
            // node must be a branch
            editPotential(ae, (TreeADDBranch) node, path);

        } else if (actionComand.equals(ActionCommands.CHANGE_ROOT_VARIABLE)) {
            // node must be a TreeADDPotential
            changeRootVariable(ae, (TreeADDPotential) node, path);
        } else if (actionComand.equals(ActionCommands.JOIN_BRANCHES)) {
            if (node instanceof Potential) {
                path = path.getParentPath();
                node = path.getLastPathComponent();
            }
            // node must be a branch
            associateStates(ae, (TreeADDBranch) node, path);
        } else if (actionComand.equals(ActionCommands.REMOVE_SUBTREE)) {
            if (node instanceof Potential) {
                path = path.getParentPath();
                node = path.getLastPathComponent();
            }
            // node must be a branch
            removeSubtree(ae, (TreeADDBranch) node, path);
        } else if (actionComand.equals(ActionCommands.ADD_VARIABLES)) {
            if (node instanceof Potential) {
                path = path.getParentPath();
                node = path.getLastPathComponent();
            }
            // node must be a branch
            addVariablesToPotential(ae, (TreeADDBranch) node, path);
        } else if (actionComand.equals(ActionCommands.REMOVE_STATES)) {
            if (node instanceof Potential) {
                path = path.getParentPath();
                node = path.getLastPathComponent();
            }
            // node must be a branch
            dissociateStates(ae, (TreeADDBranch) node, path);
        } else if (actionComand.equals(ActionCommands.REMOVE_VARIABLES)) {
            if (node instanceof Potential) {
                path = path.getParentPath();
                node = path.getLastPathComponent();
            }
            // node must be a branch
            removeVariablesFromPotential(ae, (TreeADDBranch) node, path);
        } else if (actionComand.equals(ActionCommands.SPLIT_INTERVAL)) {
            if (node instanceof Potential) {
                path = path.getParentPath();
                node = path.getLastPathComponent();
            }
            // node must be a branch
            splitInterval(ae, (TreeADDBranch) node, path);
        } else if (actionComand.equals(ActionCommands.CHANGE_INTERVAL)) {
            if (node instanceof Potential) {
                path = path.getParentPath();
                node = path.getLastPathComponent();
            }
            // node must be a branch
            changeInterval(ae, (TreeADDBranch) node, path);
        } else if (actionComand.equals(ActionCommands.SET_LABEL)) {
            setLabel(ae, (TreeADDBranch) node, path);
        } else if (actionComand.equals(ActionCommands.REMOVE_LABEL)) {
            removeLabel(ae, (TreeADDBranch) node, path);
        } else if (actionComand.equals(ActionCommands.SET_REFERENCE)) {
            setReference(ae, (TreeADDBranch) node, path);
        } else if (actionComand.equals(ActionCommands.REMOVE_REFERENCE)) {
            removeReference(ae, (TreeADDBranch) node, path);
        } else {
            throw new RuntimeException("Unexpected menu action found: " + actionComand);
        }
    }

    private void changeInterval(ActionEvent ae, TreeADDBranch branch, TreePath path) {

        TreePath parentPath = path.getParentPath();
        TreeADDPotential parentTreeADD = (TreeADDPotential) ((TreePath) parentPath).getLastPathComponent();
        ChangeIntervalDialog dialog = new ChangeIntervalDialog(Utilities.getOwner(this), branch);
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        boolean minBelongsToLeft = false;
        boolean maxBelongsToLeft = false;
        double minDomainLimit = parentTreeADD.getRootVariable().getPartitionedInterval().getMin();
        double maxDomainLimit = parentTreeADD.getRootVariable().getPartitionedInterval().getMax();
        boolean isLeftClosed = branch.getRootVariable().getPartitionedInterval().isLeftClosed(); // true
                                                                                                 // ->
                                                                                                 // [)
        boolean isRightClosed = branch.getRootVariable().getPartitionedInterval().isRightClosed();
        boolean minBelongsToLeftDomain = !isLeftClosed;
        boolean maxBelongsToLeftDomain = isRightClosed;
        if (dialog.requestValues() == ChangeIntervalDialog.OK_BUTTON) {
            ChangeIntervalPanel panel = (ChangeIntervalPanel) dialog.getChangeIntervalPanel();
            Double lowerBound = Double.parseDouble(panel.getMin().getText());
            Double upperBound = Double.parseDouble(panel.getMax().getText());
            JComboBox<String> minLimit = panel.minBelongsToLeft();// ( or [
            if (minLimit.getSelectedItem() == "(") {
                minBelongsToLeft = true;
            } else if (minLimit.getSelectedItem() == "[") {
                minBelongsToLeft = false;
            }
            JComboBox<String> maxLimit = panel.maxBelongsToLeft();// ) or ]
            if (maxLimit.getSelectedItem() == ")") {
                maxBelongsToLeft = false;
            } else if (maxLimit.getSelectedItem() == "]") {
                maxBelongsToLeft = true;
            }
            List<TreeADDBranch> parentBranches = parentTreeADD.getBranches();
            int branchIndex = 0;
            for (int i = 0; i < parentBranches.size(); i++) {
                if (parentBranches.get(i).equals(branch)) {
                    branchIndex = i;
                }
            }
            // limit situations
            if (!(lowerBound >= minDomainLimit && lowerBound <= branch.getUpperBound().getLimit())
                    || !(upperBound.floatValue() >= branch.getLowerBound().getLimit() && upperBound <= maxDomainLimit)) {
                JOptionPane.showMessageDialog(this.getParent(), "Not permited values");
            } else if (upperBound.floatValue() > maxDomainLimit
                    || lowerBound.floatValue() < minDomainLimit) {
                JOptionPane.showMessageDialog(this.getParent(),
                        stringDatabase.getString("TreeADD.DomainChangeWarning"));
            } else if (upperBound == maxDomainLimit && !maxBelongsToLeftDomain && maxBelongsToLeft) {
                JOptionPane.showMessageDialog(this.getParent(),
                        stringDatabase.getString("TreeADD.DomainChangeWarning"));
            } else if (lowerBound == minDomainLimit && minBelongsToLeftDomain && !minBelongsToLeft) {
                JOptionPane.showMessageDialog(this.getParent(),
                        stringDatabase.getString("TreeADD.DomainChangeWarning"));
            } else {
                // max limit
                if (!(upperBound == branch.getUpperBound().getLimit() && maxBelongsToLeft == branch.getUpperBound().belongsToLeft())) {
                    List<TreeADDBranch> followingBranches = new ArrayList<TreeADDBranch>();
                    parentBranches.get(branchIndex).setUpperBound(new Threshold(upperBound,
                            maxBelongsToLeft));
                    if (branchIndex != parentBranches.size()) {
                        // branch selected to change interval is not the last
                        // one
                        for (int i = branchIndex + 1; i < parentBranches.size(); i++) {
                            followingBranches.add(parentBranches.get(i));
                        }
                        for (int i = 0; i < followingBranches.size(); i++) {
                            if (upperBound.equals(followingBranches.get(i).getUpperBound().getLimit())
                                    && maxBelongsToLeft == followingBranches.get(i).getUpperBound().belongsToLeft()) {
                                for (int j = branchIndex + 1; j <= i + branchIndex + 1; j++) {
                                    parentBranches.remove(branchIndex + 1);
                                }
                                break;
                            } else if (upperBound.equals(followingBranches.get(i).getUpperBound().getLimit())
                                    && !followingBranches.get(i).getUpperBound().belongsToLeft()
                                    && maxBelongsToLeft) {
                                for (int j = branchIndex + 1; j <= i + branchIndex + 1; j++) {
                                    parentBranches.remove(branchIndex + 1);
                                }
                                // Change the next
                                if (i + branchIndex + 1 <= parentBranches.size()) {
                                    parentBranches.get(branchIndex + 1).getLowerBound().setBelongsToLeft(true);
                                }
                                break;
                            } else if (upperBound.equals(followingBranches.get(i).getUpperBound().getLimit())
                                    && followingBranches.get(i).getUpperBound().belongsToLeft()
                                    && !maxBelongsToLeft) {
                                for (int j = branchIndex + 1; j <= i + branchIndex; j++) {
                                    parentBranches.remove(branchIndex + 1);
                                }
                                // Change the next
                                if (i + branchIndex + 1 <= parentBranches.size()) {
                                    parentBranches.get(branchIndex + 1).setLowerBound(new Threshold(upperBound,
                                            false));
                                }
                                break;
                            } else if (upperBound.floatValue() < followingBranches.get(i).getUpperBound().getLimit()) {
                                for (int j = branchIndex + 1; j <= i + branchIndex; j++) {
                                    parentBranches.remove(branchIndex + 1);
                                }
                                parentBranches.get(branchIndex + 1).setLowerBound(new Threshold(upperBound,
                                        maxBelongsToLeft));
                                break;
                            }
                        }
                    }
                }
                // min limit
                if (!(lowerBound.floatValue() == branch.getLowerBound().getLimit() && minBelongsToLeft == branch.getLowerBound().belongsToLeft())) {
                    ArrayList<TreeADDBranch> previousBranches = new ArrayList<TreeADDBranch>();
                    parentBranches.get(branchIndex).setLowerBound(new Threshold(lowerBound,
                            minBelongsToLeft));
                    if (branchIndex != 0) {// branch selected to change
                                           // interval is not the first
                                           // one
                        for (int i = branchIndex - 1; i >= 0; i--) {
                            previousBranches.add(parentBranches.get(i));
                        }
                    }
                    ArrayList<TreeADDBranch> aux = new ArrayList<TreeADDBranch>();
                    int initialParentSize = parentBranches.size();
                    for (int i = parentBranches.size() - 1; i >= 0; i--) {
                        aux.add(parentBranches.get(i));
                    }
                    int auxIndex = parentBranches.size() - 1 - branchIndex;
                    for (int i = 0; i < previousBranches.size(); i++) {
                        if (lowerBound.equals(previousBranches.get(i).getLowerBound().getLimit())
                                && minBelongsToLeft == previousBranches.get(i).getLowerBound().belongsToLeft()) {
                            for (int j = auxIndex + 1; j <= i + (auxIndex + 1); j++) {
                                aux.remove(auxIndex + 1);
                            }
                            break;
                        } else if (lowerBound.equals(previousBranches.get(i).getLowerBound().getLimit())
                                && !previousBranches.get(i).getLowerBound().belongsToLeft()
                                && minBelongsToLeft) {
                            for (int j = auxIndex + 1; j <= i + (auxIndex + 1); j++) {
                                aux.remove(auxIndex + 1);
                            }
                            // Change the next
                            if (i + auxIndex + 1 <= parentBranches.size()) {
                                aux.get(auxIndex + 1).getUpperBound().setBelongsToLeft(true);
                            }
                            break;
                        } else if (lowerBound.equals(previousBranches.get(i).getLowerBound().getLimit())
                                && previousBranches.get(i).getLowerBound().belongsToLeft()
                                && !minBelongsToLeft) {
                            for (int j = auxIndex + 1; j <= i + (auxIndex + 1); j++) {
                                aux.remove(auxIndex + 1);
                            }
                            // Change the next
                            if (i + auxIndex + 1 <= parentBranches.size()) {
                                aux.get(auxIndex + 1).getUpperBound().setBelongsToLeft(false);
                            }
                            break;
                        } else if (lowerBound > previousBranches.get(i).getLowerBound().getLimit()) {
                            for (int j = auxIndex + 1; j <= i + auxIndex; j++) {
                                aux.remove(auxIndex + 1);
                            }
                            aux.get(auxIndex + 1).setUpperBound(new Threshold(lowerBound,
                                    minBelongsToLeft));
                            break;
                        }
                    }
                    for (int i = aux.size() - 1; i >= 0; i--) {
                        parentBranches.set(i, aux.get(aux.size() - 1 - i));
                    }
                    for (int i = aux.size(); i < initialParentSize; i++) {
                        parentBranches.remove(aux.size());
                    }
                }
                model.notifyTreeStructureChanged((TreePath) parentPath);
            }
            for (int i = 0; i < jTree.getRowCount(); i++) {
                jTree.expandRow(i);
            }
        }
    }

    /**
     * Splits interval in a branch which top variable is continuous
     * 
     * @param ae
     * @param branch
     * @param path
     */
    private void splitInterval(ActionEvent ae, TreeADDBranch branch, TreePath path) {
        TreePath parentPath = path.getParentPath();
        TreeADDPotential parentTreeADD = (TreeADDPotential) ((TreePath) parentPath).getLastPathComponent();
        SplitIntervalDialog dialog = new SplitIntervalDialog(Utilities.getOwner(this));
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        if (dialog.requestValues() == SplitIntervalDialog.OK_BUTTON) {
            boolean belongsToLeft = false;
            SplitIntervalPanel panel = (SplitIntervalPanel) dialog.getJPanelSplitInterval();
            if (panel.belongsToLeft().isSelected()) {
                belongsToLeft = true;
            } else if (panel.belongsToRight().isSelected()) {
                belongsToLeft = false;
            }
            Float introducedLimit = Float.parseFloat(panel.getLimit().getText());
            Threshold minFirstInterval = branch.getLowerBound();
            Threshold maxSecondInterval = branch.getUpperBound();
            List<Variable> potentialVariables = new ArrayList<Variable>();
            if (parentTreeADD.getPotentialRole() == PotentialRole.CONDITIONAL_PROBABILITY) {
                potentialVariables.add(parentTreeADD.getVariables().get(0));
            }
            UniformPotential potential = new UniformPotential(potentialVariables,
                    branch.getPotential().getPotentialRole());
            if (parentTreeADD.isUtility()) {
                potential.setUtilityVariable(parentTreeADD.getUtilityVariable());
            }
            List<TreeADDBranch> parentBranches = parentTreeADD.getBranches();
            List<TreeADDBranch> newBranches = new ArrayList<TreeADDBranch>();
            // Top variable domain
            double minDomainLimit = parentTreeADD.getRootVariable().getPartitionedInterval().getMin();
            double maxDomainLimit = parentTreeADD.getRootVariable().getPartitionedInterval().getMax();
            boolean isLeftClosed = branch.getRootVariable().getPartitionedInterval().isLeftClosed(); // true
                                                                                                     // ->
                                                                                                     // [)
            boolean isRightClosed = branch.getRootVariable().getPartitionedInterval().isRightClosed();
            boolean minBelongsToLeftDomain = !isLeftClosed;
            boolean maxBelongsToLeftDomain = isRightClosed;
            if (minDomainLimit == minFirstInterval.getLimit()
                    && introducedLimit.floatValue() == minFirstInterval.getLimit()
                    && minBelongsToLeftDomain
                    && belongsToLeft) {
                JOptionPane.showMessageDialog(this.getParent(),
                        "Be careful, you are trying to change variable domain");
            } else if (maxDomainLimit == maxSecondInterval.getLimit()
                    && introducedLimit.floatValue() == maxSecondInterval.getLimit()
                    && !maxBelongsToLeftDomain
                    && !belongsToLeft) {
                JOptionPane.showMessageDialog(this.getParent(),
                        "Be careful, you are trying to change variable domain");
            } else if (minFirstInterval.getLimit() == introducedLimit.floatValue()
                    && minFirstInterval.belongsToLeft()
                    && belongsToLeft) {
                JOptionPane.showMessageDialog(this.getParent(), "This is not a valid action");
            } else if (minFirstInterval.getLimit() == introducedLimit.floatValue()
                    && !minFirstInterval.belongsToLeft()
                    && !belongsToLeft) {
                JOptionPane.showMessageDialog(this.getParent(), "This is not a valid action");
            } else if (maxSecondInterval.getLimit() == introducedLimit.floatValue()
                    && !maxSecondInterval.belongsToLeft()
                    && !belongsToLeft) {
                JOptionPane.showMessageDialog(this.getParent(), "This is not a valid action");
            } else if (maxSecondInterval.getLimit() == introducedLimit.floatValue()
                    && maxSecondInterval.belongsToLeft()
                    && belongsToLeft) {
                JOptionPane.showMessageDialog(this.getParent(), "This is not a valid action");
            } else if (minFirstInterval.getLimit() == introducedLimit.floatValue()
                    && !minFirstInterval.belongsToLeft()
                    && belongsToLeft) {
                TreeADDBranch newBranch = new TreeADDBranch(minFirstInterval,
                        new Threshold(minFirstInterval.getLimit(), true),
                        branch.getRootVariable(),
                        potential,
                        branch.getParentVariables());
                minFirstInterval.setBelongsToLeft(true);
                for (TreeADDBranch parentBranch : parentBranches) {
                    if (branch == parentBranch) {
                        newBranches.add(newBranch);
                        newBranches.add(branch);
                    } else {
                        newBranches.add(parentBranch);
                    }
                }
                parentTreeADD.setBranches(newBranches);
                model.notifyTreeStructureChanged(parentPath);
            } else if (maxSecondInterval.getLimit() == introducedLimit.floatValue()
                    && maxSecondInterval.belongsToLeft()
                    && !belongsToLeft) {
                maxSecondInterval.setBelongsToLeft(false);
                TreeADDBranch newBranch = new TreeADDBranch(new Threshold(maxSecondInterval.getLimit(),
                        false),
                        new Threshold(maxSecondInterval.getLimit(), true),
                        branch.getRootVariable(),
                        potential,
                        branch.getParentVariables());
                for (TreeADDBranch parentBranch : parentBranches) {
                    if (branch == parentBranch) {
                        newBranches.add(branch);
                        newBranches.add(newBranch);
                    } else {
                        newBranches.add(parentBranch);
                    }
                }
                parentTreeADD.setBranches(newBranches);
                model.notifyTreeStructureChanged(parentPath);
            } else if (minFirstInterval.isBelow(introducedLimit)
                    && maxSecondInterval.isAbove(introducedLimit)) {
                Threshold maxFirstInterval = new Threshold(introducedLimit, belongsToLeft);
                Threshold minSecondInterval = new Threshold(introducedLimit, belongsToLeft);
                TreeADDBranch firstBranch = new TreeADDBranch(minFirstInterval,
                        maxFirstInterval,
                        branch.getRootVariable(),
                        potential,
                        branch.getParentVariables());
                TreeADDBranch secondBranch = new TreeADDBranch(minSecondInterval,
                        maxSecondInterval,
                        branch.getRootVariable(),
                        potential,
                        branch.getParentVariables());
                for (TreeADDBranch parentBranch : parentBranches) {
                    if (branch == parentBranch) {
                        newBranches.add(firstBranch);
                        newBranches.add(secondBranch);
                    } else {
                        newBranches.add(parentBranch);
                    }
                }
                parentTreeADD.setBranches(newBranches);
                model.notifyTreeStructureChanged(parentPath);
            } else {// a message to indicate that is not a permitted value
                JOptionPane.showMessageDialog(this.getParent(),
                        "Introduced value does not belong to the interval selected");
            }
            for (int i = 0; i < jTree.getRowCount(); i++) {
                jTree.expandRow(i);
            }
        }
    }

    /**
     * Removes a subtree from a branch
     * 
     * @param ae
     * @param branch
     * @param path
     */
    private void removeSubtree(ActionEvent ae, TreeADDBranch branch, TreePath path) {
        Object parentPath = path.getParentPath();
        TreeADDPotential parentTreeADD = (TreeADDPotential) ((TreePath) parentPath).getLastPathComponent();
        Potential subPotential = branch.getPotential();
        if (!(subPotential instanceof TreeADDPotential)) {
            throw new RuntimeException("Expected TreeADDPotential class, found: "
                    + subPotential.getClass().getName());
        }
        List<Variable> potentialVariables = new ArrayList<Variable>();
        if (parentTreeADD.getPotentialRole() == PotentialRole.CONDITIONAL_PROBABILITY) {
            Variable conditionedVariable = branch.getParentVariables().get(0);
            potentialVariables.add(conditionedVariable);
        } else if (parentTreeADD.isUtility()) {
            // potentialVariables.add(parentTreeADD.getUtilityVariable());
        }
        UniformPotential newPotential = new UniformPotential(potentialVariables,
                parentTreeADD.getPotentialRole());
        if (parentTreeADD.getPotentialRole() == PotentialRole.UTILITY) {
            newPotential.setUtilityVariable(parentTreeADD.getUtilityVariable());
        }
        branch.setPotential(newPotential);
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        model.notifyTreeStructureChanged(path);
        for (int i = 0; i < jTree.getRowCount(); i++) {
            jTree.expandRow(i);
        }
    }

    private void addVariablesToPotential(ActionEvent ae, TreeADDBranch branch, TreePath path) {
        Object parentPath = path.getParentPath();
        TreeADDPotential parentTreeADD = (TreeADDPotential) ((TreePath) parentPath).getLastPathComponent();
        AddVariablesDialog dialog = new AddVariablesDialog(Utilities.getOwner(this),
                branch,
                parentTreeADD);
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        if (dialog.requestValues() == AddVariablesDialog.OK_BUTTON) {
            AddVariablesCheckBoxPanel panel = dialog.getJPanelVariables();
            List<JCheckBox> checkBoxes = panel.getCheckBoxes();
            Potential branchPotential = branch.getPotential();
            List<Variable> branchPotentialVariables = branchPotential.getVariables();
            List<Variable> newVariables = new ArrayList<Variable>();
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    String variableName = checkBox.getText();
                    for (Variable variable : parentTreeADD.getVariables()) {
                        if (variable.getName().equals(variableName)) {
                            newVariables.add(variable);
                        }
                    }
                }
            }
            branchPotentialVariables.addAll(newVariables);
            UniformPotential potential = new UniformPotential(branchPotentialVariables,
                    parentTreeADD.getPotentialRole());
            if (parentTreeADD.isUtility()) {
                potential.setUtilityVariable(parentTreeADD.getUtilityVariable());
            }
            branch.setPotential(potential);
            model.notifyTreeInsert(path, potential);
            model.notifyTreeStructureChanged(path);
            jTree.expandPath(path);
        }
    }

    private void removeVariablesFromPotential(ActionEvent ae, TreeADDBranch branch, TreePath path) {
        Object parentPath = path.getParentPath();
        TreeADDPotential parentTreeADD = (TreeADDPotential) ((TreePath) parentPath).getLastPathComponent();
        RemoveVariablesDialog dialog = new RemoveVariablesDialog(Utilities.getOwner(this),
                branch,
                parentTreeADD);
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        if (dialog.requestValues() == RemoveVariablesDialog.OK_BUTTON) {
            List<JCheckBox> checkBoxes = ((RemoveVariablesCheckBoxPanel) dialog.getJPanelVariables()).getCheckBoxes();
            List<Variable> variablesToEliminate = new ArrayList<Variable>();
            List<Variable> branchVariables = branch.getPotential().getVariables();
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    String variableName = checkBox.getText();
                    for (Variable variable : branchVariables) {
                        if (variable.getName() == variableName) {
                            variablesToEliminate.add(variable);
                        }
                    }
                }
            }
            for (Variable variable : variablesToEliminate) {
                branchVariables.remove(variable);
            }
            UniformPotential newPotential = new UniformPotential(branchVariables,
                    parentTreeADD.getPotentialRole());
            if (parentTreeADD.isUtility()) {
                newPotential.setUtilityVariable(parentTreeADD.getUtilityVariable());
            }
            branch.setPotential(newPotential);
            model.notifyTreeInsert(path, newPotential);
            model.notifyTreeStructureChanged(path);
            jTree.expandPath(path);
        }
    }

    /**
     * @param ae
     * @param branch
     * @param path
     */
    private void dissociateStates(ActionEvent ae, TreeADDBranch branch, TreePath path) {
        if (!(branch instanceof TreeADDBranch)) {
            throw new RuntimeException("Expected TreeADDBranch class, found: "
                    + branch.getClass().getName());
        }
        Object parentPath = path.getParentPath();
        TreeADDPotential parentTreeADD = (TreeADDPotential) ((TreePath) parentPath).getLastPathComponent();
        RemoveStatesDialog dialog = new RemoveStatesDialog(Utilities.getOwner(this),
                branch,
                parentTreeADD);
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        if (dialog.requestValues() == RemoveStatesDialog.OK_BUTTON) {
            List<JCheckBox> checkBoxes = ((RemoveStatesCheckBoxPanel) dialog.getJPanelRemoveStates()).getCheckBoxes();
            List<State> statesToEliminate = new ArrayList<State>();
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    String stateName = checkBox.getText();
                    for (State state : parentTreeADD.getRootVariable().getStates()) {
                        if (state.getName().equals(stateName)) {
                            statesToEliminate.add(state);
                        }
                    }
                }
            }
            // Check if all checkboxes has been selected, it is an inconsistency
            if (checkBoxes.size() == statesToEliminate.size()) {
                JOptionPane.showMessageDialog(this.getParent(),
                        "You have selected all states to remove, you must leave at least one in each branch");
            } else {
                List<TreeADDBranch> newTreeADDBranches = new ArrayList<TreeADDBranch>();
                for (TreeADDBranch treeParentBranch : parentTreeADD.getBranches()) {
                    List<State> states = treeParentBranch.getBranchStates();
                    if (branch.getBranchStates().containsAll(states)) {
                        continue;
                    } else {
                        newTreeADDBranches.add(treeParentBranch);
                    }
                }
                // Updating branches
                List<State> branchStates = branch.getBranchStates();
                for (State state : statesToEliminate) {
                    branchStates.remove(state);
                }
                branch.setStates(branchStates);
                List<Variable> variables = new ArrayList<Variable>();
                if (parentTreeADD.isUtility()) {
                    variables.add(parentTreeADD.getUtilityVariable());
                } else if (parentTreeADD.getPotentialRole() == PotentialRole.CONDITIONAL_PROBABILITY) {
                    variables.add(parentTreeADD.getVariables().get(0));
                }
                UniformPotential potential = new UniformPotential(variables,
                        parentTreeADD.getPotentialRole());
                if (parentTreeADD.getPotentialRole() == PotentialRole.UTILITY) {
                    potential.setUtilityVariable(parentTreeADD.getUtilityVariable());
                }
                TreeADDBranch newBranch = new TreeADDBranch(statesToEliminate,
                        branch.getRootVariable(),
                        potential,
                        branch.getParentVariables());
                newTreeADDBranches.add(branch);
                newTreeADDBranches.add(newBranch);
                // Updating tree
                parentTreeADD.setBranches(newTreeADDBranches);
                model.notifyTreeStructureChanged((TreePath) parentPath);
                jTree.expandPath((TreePath) parentPath);
                for (int i = 0; i < jTree.getRowCount(); i++) {
                    jTree.expandRow(i);
                }
            }
        }
    }

    /**
     * @param ae
     * @param treeADD
     * @param path
     */
    private void associateStates(ActionEvent ae, TreeADDBranch branch, TreePath path) {
        Object parentPath = path.getParentPath();
        TreeADDPotential parentTreeADD = (TreeADDPotential) ((TreePath) parentPath).getLastPathComponent();
        // BranchStatesCheckBoxPanel checkBoxPanel = new
        // BranchStatesCheckBoxPanel(treeADDBranch, parentTreeADD);
        AddStatesToBranchDialog dialog = new AddStatesToBranchDialog(Utilities.getOwner(this),
                branch,
                parentTreeADD);
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        // This must be a treeADD
        if (dialog.requestValues() == AddStatesToBranchDialog.OK_BUTTON) {
            List<JCheckBox> checkBoxes = ((AddStatesCheckBoxPanel) dialog.getJPanelBranchStates()).getCheckBoxes();
            List<State> newBranchStates = new ArrayList<State>();
            for (State state : branch.getBranchStates()) {
                newBranchStates.add(state);
            }
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    String stateName = checkBox.getText();
                    for (State state : parentTreeADD.getRootVariable().getStates()) {
                        if (state.getName() == stateName) {
                            newBranchStates.add(state);
                        }
                    }
                }
            }
            // Reorder new states
            List<State> newOrderedStates = new ArrayList<State>();
            State[] correctOrderStates = parentTreeADD.getRootVariable().getStates();
            for (State state : correctOrderStates) {
                for (State newState : newBranchStates) {
                    if (state == newState) {
                        newOrderedStates.add(state);
                    }
                }
            }
            // Updating branches
            List<TreeADDBranch> newTreeADDBranches = new ArrayList<TreeADDBranch>();
            branch.setStates(newOrderedStates);
            newTreeADDBranches.add(branch);
            for (TreeADDBranch treeBranch : parentTreeADD.getBranches()) {
                List<State> states = treeBranch.getBranchStates();
                if (newBranchStates.containsAll(states)) {
                    continue;
                } else {
                    for (State state : newBranchStates) {
                        if (states.contains(state)) {
                            states.remove(state);
                        }
                    }
                    treeBranch.setStates(states);
                    newTreeADDBranches.add(treeBranch);
                }
            }
            // Updating tree
            parentTreeADD.setBranches(newTreeADDBranches);
            model.notifyTreeStructureChanged((TreePath) parentPath);
            jTree.expandPath(path);
            for (int i = 0; i < jTree.getRowCount(); i++) {
                jTree.expandRow(i);
            }
        }
    }

    /**
     * @param ae
     * @param treeADDPotential
     * @param path
     */
    private void changeRootVariable(ActionEvent ae, TreeADDPotential treeADDPotential, TreePath path) {
        List<Variable> variables = treeADDPotential.getVariables();
        JMenuItem menuRootVariable = (JMenuItem) ae.getSource();
        // to get the variable
        Variable newRootVariable = null;
        for (Variable variable : variables) {
            if (variable.getName().equals(menuRootVariable.getText())) {
                newRootVariable = variable;
            }
        }
        List<Variable> potentialVariables = new ArrayList<Variable>();
        if (treeADDPotential.getPotentialRole() == PotentialRole.CONDITIONAL_PROBABILITY) {
            potentialVariables.add(treeADDPotential.getVariables().get(0));
        } else if (treeADDPotential.isUtility()) {
            // potentialVariables.add(treeADDPotential.getUtilityVariable());
        }
        UniformPotential potential = new UniformPotential(potentialVariables,
                treeADDPotential.getPotentialRole());
        if (treeADDPotential.getPotentialRole() == PotentialRole.UTILITY) {
            potential.setUtilityVariable(treeADDPotential.getUtilityVariable());
        }
        treeADDPotential.setRootVariable(newRootVariable);
        List<TreeADDBranch> newBranches = new ArrayList<TreeADDBranch>();
        if (newRootVariable.getVariableType() == VariableType.FINITE_STATES
                || newRootVariable.getVariableType() == VariableType.DISCRETIZED) {
            // for (State state : newTopVariable.getStates()) {
            for (int i = newRootVariable.getStates().length - 1; i >= 0; i--) {
                List<State> branchStates = new ArrayList<State>();
                // branchStates.add(state);
                branchStates.add(newRootVariable.getStates()[i]);
                newBranches.add(new TreeADDBranch(branchStates,
                        newRootVariable,
                        potential,
                        variables));
            }
        } else if (newRootVariable.getVariableType() == VariableType.NUMERIC) {
            // Top variable domain
            double minDomainLimit = newRootVariable.getPartitionedInterval().getMin();
            double maxDomainLimit = newRootVariable.getPartitionedInterval().getMax();
            // true -> [)
            boolean isLeftClosed = newRootVariable.getPartitionedInterval().isLeftClosed();
            boolean isRightClosed = newRootVariable.getPartitionedInterval().isRightClosed();
            boolean minBelongsToLeftDomain = !isLeftClosed;
            boolean maxBelongsToLeftDomain = isRightClosed;
            newBranches.add(new TreeADDBranch(new Threshold(minDomainLimit, minBelongsToLeftDomain),
                    new Threshold(maxDomainLimit, maxBelongsToLeftDomain),
                    newRootVariable,
                    potential,
                    variables));
        }
        treeADDPotential.setBranches(newBranches);
        // treeADDPotential = newTree;
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        // model.fireNodesChanged(path);
        model.notifyTreeStructureChanged(path);
        jTree.expandPath(path);
        for (int i = 0; i < jTree.getRowCount(); i++) {
            jTree.expandRow(i);
        }
    }

    // when clicking on a brach
    private void addSubtree(ActionEvent ae, TreeADDBranch branch, TreePath path) {
        List<Variable> parentVariables = branch.getParentVariables();
        Variable parentRootVariable = branch.getRootVariable();
        JMenuItem menuRootVariable = (JMenuItem) ae.getSource();
        // to get the variable
        Variable newRootVariable = null;
        for (Variable variable : parentVariables) {
            if (variable.getName() == menuRootVariable.getText()) {
                newRootVariable = variable;
            }
        }
        List<Variable> newTreeVariables = new ArrayList<Variable>();
        // initialize variables of the new tree
        for (Variable variable : parentVariables) {
            if (variable != parentRootVariable) {
                newTreeVariables.add(variable);
            }
        }
        if (newRootVariable == null) {
            // that means that is a previous variable somewhere in the path to
            // treeADDPotetentialRoot that grouped two or more states in a
            // previous branch
            // or that is a continuous variable that appears before in the tree
            for (Variable variable : rootTreeADDPotential.getVariables()) {
                if (variable.getName() == menuRootVariable.getText()) {
                    newRootVariable = variable;
                }
            }
            if (newRootVariable.getVariableType() == VariableType.FINITE_STATES
                    || newRootVariable.getVariableType() == VariableType.DISCRETIZED) {
                List<State> groupedStates = null;
                TreePath parentPath = path.getParentPath(); // treeADD
                while (parentPath.getLastPathComponent() != rootTreeADDPotential) {
                    TreePath grandParentPath = parentPath.getParentPath();// branch
                    if (grandParentPath.getLastPathComponent() instanceof TreeADDBranch) {
                        // if a branch has more then one state is possible to
                        // offer, as top variable, the top variable of this
                        // branch
                        TreeADDBranch treeADDBranch = (TreeADDBranch) grandParentPath.getLastPathComponent();
                        TreePath greatGrandFatherPath = grandParentPath.getParentPath();
                        if (treeADDBranch.getBranchStates().size() > 1
                                && ((TreeADDPotential) greatGrandFatherPath.getLastPathComponent()).getRootVariable() == newRootVariable) {
                            groupedStates = treeADDBranch.getBranchStates();
                            break;
                        }
                    }
                    parentPath = grandParentPath;
                }
                State[] states = new State[groupedStates.size()];
                for (int i = 0; i < groupedStates.size(); i++) {
                    states[i] = groupedStates.get(i);
                }
                newRootVariable.setStates(states);
                newTreeVariables.add(newRootVariable);
            } else if (newRootVariable.getVariableType() == VariableType.NUMERIC) {
                PartitionedInterval partitionedInterval = null;
                TreePath parentPath = path.getParentPath(); // treeADD
                while (parentPath.getLastPathComponent() != rootTreeADDPotential) {
                    TreePath grandParentPath = parentPath.getParentPath();// branch
                    if (grandParentPath.getLastPathComponent() instanceof TreeADDBranch) {
                        // if a branch has more then one state is possible to
                        // offer, as top variable, the top variable of this
                        // branch
                        TreeADDBranch treeADDBranch = (TreeADDBranch) grandParentPath.getLastPathComponent();
                        // TreePath greatGrandFatherPath =
                        // grandParentPath.getParentPath();
                        if (treeADDBranch.getRootVariable() == newRootVariable) {
                            Threshold min = treeADDBranch.getLowerBound();
                            Threshold max = treeADDBranch.getUpperBound();
                            partitionedInterval = new PartitionedInterval(min.belongsToLeft(),
                                    (double) min.getLimit(),
                                    (double) max.getLimit(),
                                    max.belongsToLeft());
                            break;
                        }
                    }
                    parentPath = grandParentPath;
                }
                newRootVariable.setPartitionedInterval(partitionedInterval);
                newTreeVariables.add(newRootVariable);
            }
        }
        TreeADDPotential newTreeADD = new TreeADDPotential(newTreeVariables,
                newRootVariable,
                rootTreeADDPotential.getPotentialRole());
        if (rootTreeADDPotential.getPotentialRole() == PotentialRole.CONDITIONAL_PROBABILITY) {
            newTreeADD = new TreeADDPotential(newTreeVariables,
                    newRootVariable,
                    rootTreeADDPotential.getPotentialRole());
        } else if (rootTreeADDPotential.getPotentialRole() == PotentialRole.UTILITY) {
            newTreeADD = new TreeADDPotential(newTreeVariables,
                    newRootVariable,
                    rootTreeADDPotential.getPotentialRole(),
                    rootTreeADDPotential.getUtilityVariable());
        }
        // set the new tree to its owner branch
        branch.setPotential(newTreeADD);
        // update the tree recursively bottom-up
        Object parentPath = path.getParentPath();
        Object previousParentPath = path.getLastPathComponent();
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        while (parentPath != null) {
            previousParentPath = parentPath;
            parentPath = ((TreePath) parentPath).getParentPath();
        }
        model.notifyTreeStructureChanged((TreePath) previousParentPath);
        for (int i = 0; i < jTree.getRowCount(); i++) {
            jTree.expandRow(i);
        }
        jTree.expandPath((TreePath) parentPath);
        model.notifyTreeInsert(path, newTreeADD);
        jTree.expandPath(path);
        jTree.expandPath(path.pathByAddingChild(newTreeADD));
    }

    /**
     * Action to edit a potential
     * 
     * @param ae
     * @param branch
     * @param path
     */
    private void editPotential(ActionEvent ae, TreeADDBranch branch, TreePath path) {
        Object parentPath = path.getParentPath();
        TreeADDPotential parentTreeADD = (TreeADDPotential) ((TreePath) parentPath).getLastPathComponent();
        Potential potential = branch.getPotential();
        ProbNet probNet = probNode.getProbNet();
        ProbNet dummyProbNet = new ProbNet();
        dummyProbNet.addPotential(potential);
        ProbNode dummy = null;
        Variable conditionedVariable = parentTreeADD.getConditionedVariable();
        dummy = dummyProbNet.getProbNode(conditionedVariable);
        for (Variable variable : potential.getVariables()) {
            if (variable.equals(conditionedVariable)) {
                continue;
            }
            try {
                List<Potential> originalPotentials = probNet.getProbNode(variable).getPotentials();
                dummyProbNet.getProbNode(variable).setPotentials(originalPotentials);
                dummyProbNet.addLink(variable, conditionedVariable, true);
            } catch (NodeNotFoundException e) {
                throw new RuntimeException("Node not found: " + e.getMessage());
            }
        }
        PotentialEditDialog dialog = new PotentialEditDialog(Utilities.getOwner(this), dummy, false);
        if (dialog.requestValues() == NodePropertiesDialog.OK_BUTTON) {
            Potential retPotential = dummy.getPotentials().get(0);
            if (potential.isUtility()) {
                retPotential.setUtilityVariable(parentTreeADD.getUtilityVariable());
            }
            if (parentTreeADD.getPotentialRole() != retPotential.getPotentialRole()) {
                throw new RuntimeException("Expected role "
                        + parentTreeADD.getPotentialRole()
                        + ", found: "
                        + retPotential.getPotentialRole());
            }
            branch.setPotential(retPotential);
            TreeADDModel model = (TreeADDModel) jTree.getModel();
            model.notifyTreeStructureChanged(path);
            jTree.expandPath(path);
            int i = jTree.getRowForPath(path);
            jTree.expandRow(i);
        }
    }

    /**
     * Sets label for branch
     * 
     * @param node
     * @param path
     */
    private void setLabel(ActionEvent ae, TreeADDBranch branch, TreePath path) {
        String label = JOptionPane.showInputDialog(null, "Enter label name: ", "Set label", 1);
        if (label != null && !label.isEmpty()) {
            branch.setLabel(label);
            TreeADDModel model = (TreeADDModel) jTree.getModel();
            model.notifyTreeStructureChanged(path);
        }
    }

    /**
     * Removes the label from a branch
     * 
     * @param ae
     * @param node
     * @param path
     */
    private void removeLabel(ActionEvent ae, TreeADDBranch branch, TreePath path) {
        branch.setLabel(null);
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        model.notifyTreeStructureChanged(path);
    }

    /**
     * Sets a reference to another branch
     * 
     * @param node
     * @param path
     */
    private void setReference(ActionEvent ae, TreeADDBranch branch, TreePath path) {
        SetReferenceDialog dialog = new SetReferenceDialog(Utilities.getOwner(this),
                branch,
                rootTreeADDPotential);
        dialog.setVisible(true);
        if (dialog.getSelectedButton() == OkCancelHorizontalDialog.OK_BUTTON) {
            TreeADDModel model = (TreeADDModel) jTree.getModel();
            model.notifyTreeStructureChanged(path);
        }
    }

    /**
     * Remove reference from a branch
     * 
     * @param ae
     * @param node
     * @param path
     */
    private void removeReference(ActionEvent ae, TreeADDBranch branch, TreePath path) {
        Potential referencedPotential = branch.getPotential();
        branch.setPotential(referencedPotential.copy());
        branch.setReference(null);
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        model.notifyTreeStructureChanged(path);
    }

    /**
     * TODO: Convert to Inner Class of the Viewer?
     */
    private class TreeADDExpansionAdapter implements TreeExpansionListener {
        private TreeADDEditorPanel treeADDEditorPanel;

        TreeADDExpansionAdapter(TreeADDEditorPanel treeADDEditorPanel) {
            this.treeADDEditorPanel = treeADDEditorPanel;
        }

        public void treeExpanded(TreeExpansionEvent event) {
            treeADDEditorPanel.treeExpanded(event);
        }

        public void treeCollapsed(TreeExpansionEvent event) {
            // Ignore
        }
    }

    /**
     * TODO: Convert to Inner Class of the Viewer?
     */
    private class TreeADDWillExpandAdapter implements TreeWillExpandListener {
        private TreeADDEditorPanel treeADDEditorPanel;

        TreeADDWillExpandAdapter(TreeADDEditorPanel treeADDEditorPanel) {
            this.treeADDEditorPanel = treeADDEditorPanel;
        }

        public void treeWillExpand(TreeExpansionEvent event) {
            // Ignore
        }

        public void treeWillCollapse(TreeExpansionEvent event)
                throws ExpandVetoException {
            treeADDEditorPanel.treeWillCollapse(event);
        }
    }

    /**
     * @author jfernandez
     * @author myebra
     */
    private class TreeADDMouseAdapter extends MouseAdapter {
        private TreeADDEditorPanel treeADDEditorPanel;

        TreeADDMouseAdapter(TreeADDEditorPanel adaptee) {
            this.treeADDEditorPanel = adaptee;
        }

        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                showContextualMenu(e);
            }
        }

        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                showContextualMenu(e);
            }
        }

        private void showContextualMenu(MouseEvent e) {
            treeADDEditorPanel.xx = e.getX();
            treeADDEditorPanel.yy = e.getY();
            TreePath path = treeADDEditorPanel.jTree.getPathForLocation(treeADDEditorPanel.xx,
                    treeADDEditorPanel.yy);
            if (path != null) {
                Object node = path.getLastPathComponent();
                if (node instanceof TreeADDBranch) {
                    TreeADDBranch branch = (TreeADDBranch) node;
                    treeADDEditorPanel.setContextualMenuBranch(e, branch, path);
                } else if (node instanceof Potential) {
                    if (node instanceof TreeADDPotential) {
                        TreeADDPotential potential = (TreeADDPotential) node;
                        treeADDEditorPanel.setContextualMenuTreeADD(e, potential);
                    } else {
                        TreePath parentPath = path.getParentPath();
                        Object parent = parentPath.getLastPathComponent();
                        if (parent instanceof TreeADDBranch) {
                            treeADDEditorPanel.setContextualMenuPotential(e,
                                    (TreeADDBranch) parent,
                                    parentPath);
                        }
                    }
                }
                treeADDEditorPanel.contextualMenu.show(e.getComponent(),
                        treeADDEditorPanel.xx,
                        treeADDEditorPanel.yy);
            }

        }
    }
}