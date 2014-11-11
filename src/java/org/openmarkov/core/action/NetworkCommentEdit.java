/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.action;

import org.openmarkov.core.model.network.ProbNet;

/**
 * <code>NetworkCommentEdit</code> is a simple edit that allow modify a network 
 * comment.
 *   
 * @version 1.0 21/12/10
 * @author Miguel Palacios
 */
@SuppressWarnings("serial")
public class NetworkCommentEdit extends SimplePNEdit {
	/**
	 * The current network comment
	 */
	private String currentComment;
	/**
	 * The new network comment
	 */
	private String newComment;
	
	/**
	 * Creates a new <code>NetworkCommentEdit</code> with the network and new 
	 * comment specified.
	 * 
	 * @param probNet
	 *            the network that will be edited.
	 * @param newComment 
	 * 			the new comment
	 */
	public NetworkCommentEdit(ProbNet probNet,
			String newComment) {
		super(probNet);
		this.newComment = newComment;
		this.currentComment = probNet.getComment();
	}
	
	
	@Override
	public void doEdit() {
		probNet.setComment(newComment);
	}
	public void undo() {
		super.undo();
		probNet.setComment(currentComment);
		
	}
}


