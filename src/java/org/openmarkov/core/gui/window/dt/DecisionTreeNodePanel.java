package org.openmarkov.core.gui.window.dt;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import org.openmarkov.core.dt.DecisionTreeNode;
import org.openmarkov.core.gui.dialog.treeadd.IconFactory;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNode;

@SuppressWarnings("serial")
public class DecisionTreeNodePanel extends DecisionTreeElementPanel
{
    private DecisionTreeNode treeNode;
    
    private static Map<String, Icon> chanceNodeIconPool = new HashMap<>();  
    private static Map<String, Icon> decisionNodeIconPool = new HashMap<>();  
    private static Map<String, Icon> utilityNodeIconPool = new HashMap<>();  
    
    /**
     * Constructor for DecisionTreeNodePanel.
     * @param treeNode
     */
    public DecisionTreeNodePanel (DecisionTreeNode treeNode)
    {
        this.treeNode = treeNode;
        leftLabel.setIcon (createNodeIcon (treeNode.getProbNode ()));
    }

    /**
     * Create a new icon for a node of the ADD/Tree
     * @return
     */
    protected Icon createNodeIcon (ProbNode node)
    {
        Font textIconFont = new Font ("Helvetica", Font.BOLD, 15);
        Icon icon = null;
        switch (node.getNodeType ())
        {
            case CHANCE :
            {
                icon = chanceNodeIconPool.get (node.getName ());
                if(icon == null)
                {
                    icon = IconFactory.createChanceIcon (node.getName (), textIconFont);
                    chanceNodeIconPool.put (node.getName (), icon);
                }
                break;
            }
            case DECISION :
            {
                if(icon == null)
                {
                    icon = IconFactory.createDecisionIcon (node.getName (), textIconFont);
                    decisionNodeIconPool.put (node.getName (), icon);
                }
                break;
            }
            case UTILITY :
            {
                if(icon == null)
                {
                    icon = IconFactory.createUtilityIcon (node.getName (), textIconFont);
                    utilityNodeIconPool.put (node.getName (), icon);
                }
                break;
            }
        }
        return icon;
    }    


    @Override
    public void update (boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        if(treeNode.getProbNode ().getNodeType () == NodeType.UTILITY)
        {
            rightLabel.setText (" U ="+ treeNode.getUtility ());
        }
    }
}
