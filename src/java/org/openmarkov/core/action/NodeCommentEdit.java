/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.action;

import java.util.List;

import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.potential.Potential;

/**
 * <code>NetworkCommentEdit</code> is a simple edit that allow modify a network 
 * comment.
 *   
 * @version 1.0 21/12/10
 * @author Miguel Palacios
 */
@SuppressWarnings("serial")
public class NodeCommentEdit extends SimplePNEdit{
	/**
	 * Current node comment
	 */
	private String currentComment = "";
	/**
	 * New node comment
	 */
	private String newComment;
	
	/**
	 * Comment type, could be "DefinitionComment" or "ProbsTableComment"
	 */
	private String typeComment="";
	/**
	 * The node
	 */
	private ProbNode probNode;
	
	/**
	 * Creates a <code>NodeCommentEdit</code> with the node, new comment and 
	 * type of comment specified.
	 */
	public NodeCommentEdit(ProbNode probNode,String newComment,
			String typeComment) {
		super(probNode.getProbNet());
		this.newComment = newComment;
		this.typeComment = typeComment;
		this.probNode = probNode;
		if (typeComment.equals("DefinitionComment")){
			this.currentComment = probNode.getComment();
		}else{
			this.currentComment = probNode.getPotentials().get(0).getComment();

		}
	}
	// Methods
	@Override
	public void doEdit() {
		if (typeComment.equals("DefinitionComment")){
			 probNode.setComment(newComment);
		}else{
		    List<Potential> potential = probNode.getPotentials();
			potential.get(0).setComment(newComment);
			probNode.setPotentials(potential); 
			
		}
	}
	
	public void undo() {
		super.undo();
		if (typeComment.equals("DefinitionComment")){
			probNode.setComment(currentComment);
		}else{
		    List<Potential> potential = probNode.getPotentials();
			potential.get(0).setComment(currentComment);
			probNode.setPotentials(potential); 
		}
	}
}
