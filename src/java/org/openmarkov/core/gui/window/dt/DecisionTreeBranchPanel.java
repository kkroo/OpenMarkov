package org.openmarkov.core.gui.window.dt;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.openmarkov.core.dt.DecisionTreeBranch;
import org.openmarkov.core.dt.DecisionTreeNode;
import org.openmarkov.core.model.network.NodeType;

@SuppressWarnings("serial")
public class DecisionTreeBranchPanel extends DecisionTreeElementPanel
{
    private DecisionTreeBranch treeBranch;
    
    public DecisionTreeBranchPanel(DecisionTreeBranch treeBranch)
    {
        super();
        this.treeBranch = treeBranch;
    }
    
    /**
     * Builds the text to be shown in the branch
     */
    public String getBranchDescriptiontHTML ()
    {
        DecimalFormat df = new DecimalFormat("0.0000", new DecimalFormatSymbols(Locale.US));        
        StringBuilder txtLeft = new StringBuilder("<html><table border=1>");
        DecisionTreeNode parent = treeBranch.getParent();
        if(parent != null && parent.getProbNode ().getNodeType () == NodeType.DECISION)
        {
            if(parent.isBestDecision(treeBranch)) {
                txtLeft.append ("<td width=10px bgcolor=red border=0></td>");
            }
            else {
                txtLeft.append ("<td width=10px border=0></td>");                   
            }
        }
        txtLeft.append ("<td align=center border=0>");
        if (treeBranch.getBranchVariable() != null)
        {
            txtLeft.append (treeBranch.getBranchVariable().getName () + "=");
            txtLeft.append (treeBranch.getBranchState().getName ());
            txtLeft.append (" / ");
        }
        if(parent != null && parent.getProbNode ().getNodeType () == NodeType.CHANCE)
        {
            txtLeft.append (" P=" + df.format (treeBranch.getBranchProbability()));
            txtLeft.append (" / ");
        }
        txtLeft.append ("U=" + df.format (treeBranch.getChild().getUtility ()));
        txtLeft.append ("</td>");
        txtLeft.append ("</table></html>");
        return txtLeft.toString ();
    }    

    @Override
    public void update (boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        leftLabel.setText (getBranchDescriptiontHTML ());
    }}
