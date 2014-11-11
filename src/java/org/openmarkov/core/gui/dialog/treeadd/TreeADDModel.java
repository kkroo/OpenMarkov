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

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;

/**
 * Model of a <code>TreeADDPotential</code>. It is used by a <code>JTree</code> and by <code>TreeADDController</code>.
 * 
 * @author jfernandez
 * @author myebra
 *
 */
public class TreeADDModel implements TreeModel {
	
	/**
	 * This is the root of the tree, within it could exists another subtrees or other type of potentials
	 */
	protected TreeADDPotential treeADDPotentialRoot;
	
	/**
	 * Tree model listeners
	 */
	protected List<TreeModelListener> treeModelListeners = new ArrayList<TreeModelListener>();
	
	/**
	 * Constructor
	 * @param treeADDPotential  The treeADDPotential represented by this model
	 */
	public TreeADDModel(TreeADDPotential treeADDPotential) {
		this.treeADDPotentialRoot = treeADDPotential;
	}

	/**
	 * 
	 */
	public Object getRoot() {
		return treeADDPotentialRoot;
	}
	
	public int getChildCount(Object node) {
		if ( isLeaf(node) ) {
			return 0;
		} else if (node instanceof TreeADDPotential) {
			return ((TreeADDPotential)node).getBranches().size();
		} else if (node instanceof TreeADDBranch) {
			return 1;
			
		}
		return 0;
	}

	
	public boolean isLeaf(Object node) {
		return !(node instanceof TreeADDPotential) && !(node instanceof TreeADDBranch);
	}


	public void addTreeModelListener(TreeModelListener listener) {
		treeModelListeners.add(listener);
	}
	
	public void removeTreeModelListener(TreeModelListener listener) {
		treeModelListeners.remove(listener);
	}
	
	/**
	 * It is assumed that Object parent is a TreeADDPotential
	 * It could returns two different kind of Potentials: a TreeADDPotential or a Table potential
	 * It is assumed that object parent will be always a TreeADDPotential, it could be the root
	 * or a subtree.
	 * It children could be a TablePotential, another TreADDPotential (subtree) or a reference to another TreADDPotential (subtree) or a TablePotential
	 * linked by a TreeADDBranch 
	 * 
	 */
	//if parent is a TreeADD the child must be a potential a tablePotential or a treeADDPotential (subtree)	
	//if parent is branch it returns a potential that could be a treeADDPotential or a TablePotential
	public Object getChild(Object parent, int index) {
		if (isLeaf(parent)){
			return null;
		} 
		if (parent instanceof TreeADDBranch) {
		    TreeADDBranch parentBranch = (TreeADDBranch)parent;
			return (!parentBranch.isReference())? parentBranch.getPotential() : parentBranch.getReference();
		} 
		if (parent instanceof TreeADDPotential) {
			return ((TreeADDPotential)parent).getBranches().get(index);
		}
		return null;
	}
	
	/**
	 * @param parent , It is usually a TreeADDPotential 
	 * @param objChild, It is usually a TreeADDBranch
	 */
	//parent must be a treeADD and child a treeADD or a potential
	public int getIndexOfChild(Object parent, Object objChild) {
		if (parent==null || objChild==null ) {
			return -1;//If either parent or child is null, returns -1
		}
		if (isLeaf(parent)) {//getChildrenCount(parent)=0
			return 0;
		} else {
			for (int i = 0; i < getChildCount(parent); i++) {
				if (getChild(parent,i) == objChild) {
					return i;
				}
			}
		}
		return -1;//If either parent or child don't belong to this tree model, returns -1.
	}
	
	/**
	 * Alerts tree model listeners that a node (or a set of siblings) has changed in some way 
	 * node´s attributes have changed and may affect presentation
	 * The node(s) have not changed locations in the tree or altered their children 
	 * 
	 * @param path
	 */
	public void notifyNodesChanged(TreePath path) {
        TreeModelEvent e = new TreeModelEvent(this, path);
        
        for (TreeModelListener treeModelListener : treeModelListeners) {
            treeModelListener.treeNodesChanged (e);
        }
	}
	
	/**
	 * Alerts tree model listeners that the tree has drastically changed structure from a given node down
	 *  
	 * @param path
	 */
	public void notifyTreeStructureChanged(TreePath path) {
        TreeModelEvent e = new TreeModelEvent(this, path);
        
        for (TreeModelListener treeModelListener : treeModelListeners) {
            treeModelListener.treeStructureChanged (e);
        }
	}
	
	/**
	 * Alerts tree model listeners that a node has been inserted in the tree
	 * Tree nodes could be treeADDs or potentials
	 * 
	 * @param path
	 * @param child
	 */
	public void notifyTreeInsert(TreePath path, Object child) {
        int index = this.getIndexOfChild(path.getLastPathComponent(), child);
        TreeModelEvent e = new TreeModelEvent(this, path, new int[] {index}, new Object[]{child});
  
        for (TreeModelListener treeModelListener : treeModelListeners) {
            treeModelListener.treeNodesInserted (e);
        }
	}
	
	/**
	 * Alerts tree model listeners that a node has been removed in the tree
	 * 
	 * @param path
	 * @param child
	 */
	public void notifyTreeRemove(TreePath path, Object child) {
		int index = this.getIndexOfChild(path.getLastPathComponent(), child);
		TreeModelEvent e = new TreeModelEvent(this, path, new int[] {index}, new Object[]{child});
  
		for (TreeModelListener treeModelListener : treeModelListeners) {
		    treeModelListener.treeNodesRemoved (e);
		}
	}	
	
	/**
	 * 
	 * @param obj
	 * @param goal
	 * @param path
	 */
	@SuppressWarnings("unused")
    private void notifyRecursiveNodeChanged (Object obj, Object goal, TreePath path) {
		TreePath newPath= path.pathByAddingChild(obj);
		
		if (obj==goal) {
			notifyNodesChanged (path);		// maybe the leaf is contracted: we might need to refresh the summarybox node
			notifyNodesChanged (newPath);
			return;
		}
		
		for (int i=0; i< getChildCount(obj); i++) {
			Object child= getChild(obj, i);
			notifyRecursiveNodeChanged (child, goal, newPath);
		}
	}
	
	/**
	 * 
	 * @param goal
	 */
	/*public void fireRecursiveNodeChanged (Node goal) {
		Node root= (Node) getRoot();
		
		TreePath path= new TreePath (root);

		if (root==goal) {
			fireNodesChanged (path);
			return;
		}
		
		for (int i=0; i< getChildCount(root); i++) {
			Object child= getChild (root, i);
			fireRecursiveNodeChanged (child, goal, path);
		}
	}*/
	
	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {
		throw new UnsupportedOperationException();
	}
}
