/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.treeadd;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.HashMap;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.treeadd.Threshold;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;

public class TreeADDCellRenderer extends JPanel
    implements
        TreeCellRenderer
{
    private static final long       serialVersionUID = 1L;
    /**
     * Container of SummaryBox' text or the variable icon
     */
    private JLabel                  leftLabel        = new JLabel ();
    /**
     * Container of leaf data: Potential description or value
     */
    private JLabel                  rightLabel       = new JLabel ();
    /**
     * Icon repository for every variable node
     */
    private HashMap<Variable, Icon> iconsPool        = new HashMap<Variable, Icon> ();
    /**
     * Font used in icon text
     */
    private Font                    textIconFont;

    private ProbNet probNet;
    /**
     * Precision Proxy: every node of the tree could have its own precision
     * (number of decimals)
     */
    // protected PrecisionProxy precisionProxy;
    /**
     * TODO: Add a new constructor with font and default precision values
     * @param probNet 
     */
    public TreeADDCellRenderer (ProbNet probNet)
    {
        super (new BorderLayout ());
        this.probNet = probNet;
        this.add (leftLabel, BorderLayout.WEST);
        this.add (rightLabel, BorderLayout.CENTER);
        leftLabel.setHorizontalAlignment (JLabel.CENTER);
        leftLabel.setHorizontalTextPosition (JLabel.LEADING);
        rightLabel.setHorizontalAlignment (JLabel.RIGHT);
        setBackground (Color.white);
        // TODO: Add a background color attribute
        textIconFont = new Font ("Helvetica", Font.BOLD, 15);
        // precisionProxy= new PrecisionProxy(2);
    }

    public Component getTreeCellRendererComponent (JTree tree,
                                                   Object value,
                                                   boolean selected,
                                                   boolean expanded,
                                                   boolean leaf,
                                                   int row,
                                                   boolean hasFocus)
    {
        leftLabel.setText (null);
        rightLabel.setText (null);
        Component retCode = null;

		if (value instanceof TreeADDBranch) {
			retCode = getTreeCellRendererBranch(tree, (TreeADDBranch) value,
					selected, expanded, leaf, row, hasFocus);
		} else if (value instanceof Potential) {
			retCode = getTreeCellRendererPotential(tree, (Potential) value,
					selected, expanded, leaf, row, hasFocus);
		} else if (value instanceof String) {
		    leftLabel.setText ("@"+value.toString());
		    retCode = this;
		}else {
			throw new RuntimeException("Class not allowed: " + value.getClass().getName());
		}
        return retCode;
    }

    /**
     * Draws a TreeADDBranch node
     * @param tree
     * @param branch TreeADDBranch being painted
     * @param selected Selection Flag: true when this treenode is selected
     * @param expanded true when this treenode is expanded
     * @param leaf true when this treenode is a leaf
     * @param row
     * @param hasFocus
     * @return
     */
    public Component getTreeCellRendererBranch (JTree tree,
                                                   TreeADDBranch branch,
                                                   boolean selected,
                                                   boolean expanded,
                                                   boolean leaf,
                                                   int row,
                                                   boolean hasFocus)
    {
        // This kind of nodes won't display an icon
        leftLabel.setIcon (null);
        // TreeADDBranch always have only one child: a potential that it would
        // be a TreeADD or a Potential
        Object child = tree.getModel ().getChild (branch, 0);
        boolean isLeaf = tree.getModel ().isLeaf (child);
        if (!leaf && child instanceof TreeADDPotential && !expanded)
        {
            rightLabel.setText (" " + ((Potential) child).treeADDString ());
        }
        if (isLeaf && !expanded)
        {
            getTreeCellRendererComponent (tree, child, selected, expanded, leaf, row, hasFocus);
        }
        if (branch.isLabeled())
        {
            String oldText = (rightLabel.getText()!=null)? rightLabel.getText() : "";
            rightLabel.setText (" {" + branch.getLabel() + "}" + oldText);
        }
        leftLabel.setText (getBranchDescriptiontHTML (branch));
        return this;
    }

    /**
     * Draws a TreeADDPotential or a TablePotential node
     * @param tree
     * @param potential Potential Node of the ADD/Tree
     * @param selected Selection Flag: true when this treenode is selected
     * @param expanded true when this treenode is expanded
     * @param leaf true when this treenode is a leaf
     * @param row
     * @param hasFocus
     * @return
     */
    public Component getTreeCellRendererPotential (JTree tree,
                                                   Potential potential,
                                                   boolean selected,
                                                   boolean expanded,
                                                   boolean leaf,
                                                   int row,
                                                   boolean hasFocus)
    {
        if (potential instanceof TreeADDPotential)
        {
            TreeADDPotential treeADDPotential = (TreeADDPotential) potential;
            Variable topVariable = treeADDPotential.getRootVariable ();
            leftLabel.setIcon(getIcon(topVariable));
        }
        else
        {
            rightLabel.setText (" " + potential.treeADDString ());
        }
        return this;
    }

    private Icon getIcon(Variable variable) {
        Icon icon = null;
    	if (iconsPool.containsKey (variable))
        {
    		icon = iconsPool.get (variable);
        }
        else
        {
            icon = createNodeIcon (variable);
            iconsPool.put (variable, icon);
        }
        return icon;
	}

	/**
     * Create a new icon for a node of the ADD/Tree
     * @return
     */
	protected Icon createNodeIcon(Variable variable) {
		Icon icon = null;
		ProbNode node = probNet.getProbNode(variable);
		switch (node.getNodeType()) {
		case CHANCE: {
			icon = IconFactory.createChanceIcon(variable.getName(), textIconFont);
			break;
		}
		case DECISION: {
			icon = IconFactory.createDecisionIcon(variable.getName(), textIconFont);
			break;
		}
		case UTILITY: {
			icon = IconFactory.createUtilityIcon(variable.getName(), textIconFont);
			break;
		}
		}
		return icon;
	}

    /**
     * Creates what is displayed in a branch for discretized or finite states
     * variables
     */
    public String getBranchDescriptiontHTML (TreeADDBranch treeBranch)
    {
        String txtLeft = "<html><table border=1>";
        Variable topVariable = treeBranch.getRootVariable ();
        if (topVariable == null) throw new RuntimeException ();
        if (topVariable.getVariableType () == VariableType.NUMERIC)
        {
            String varName = topVariable.getName ();
            Threshold min = treeBranch.getLowerBound ();
            Threshold max = treeBranch.getUpperBound ();
            String intervalString = "";
            String minimun = "";
            String maximun = "";
            intervalString += !min.belongsToLeft () ? "[" : "(";
            if (min.getLimit () == Double.NEGATIVE_INFINITY)
            {
                minimun = "-" + "\u221E";
            }
            else
            {
                minimun = String.valueOf (min.getLimit ());
            }
            intervalString += minimun;
            intervalString += ", ";
            if (max.getLimit () == Double.POSITIVE_INFINITY)
            {
                maximun = "\u221E";
            }
            else
            {
                maximun = String.valueOf (max.getLimit ());
            }
            intervalString += maximun;
            intervalString += max.belongsToLeft () ? "]" : ")";
            txtLeft += "<td align=center border=0>" + varName + "=" + intervalString + "</td>";
        }
        else
        {
            String varName = topVariable.getName ();
            List<State> branchStates = treeBranch.getBranchStates ();
            String varStateNames = "";
            if (branchStates.size () > 1)
            {
                varStateNames += "{";
            }
            boolean bFirst = true;
            for (State state : branchStates)
            {
                if (bFirst)
                {
                    bFirst = false;
                }
                else
                {
                    varStateNames += ", ";
                }
                varStateNames += state.getName ();
            }
            if (branchStates.size () > 1)
            {
                varStateNames += "}";
            }
            txtLeft += "<td align=center border=0>" + varName + "=" + varStateNames + "</td>";
        }
        txtLeft += "</table></html>";
        return txtLeft;
    }
}
