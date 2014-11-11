/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.action;

import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoableEdit;

import org.openmarkov.core.model.network.ProbNet;


@SuppressWarnings("serial")
/** The different between <code>PNUndoableEditEvent</code> and 
 *  <code>UndoableEditEvent</code> is that a <code>PNUndoableEditEvent</code>
 *  use a <code>ProbNet</code>. */
public class PNUndoableEditEvent extends UndoableEditEvent {

	// Attributes
	private ProbNet probNet;
	
	// Constructor
	/** @param source. The <code>Object</code> that originated the event.
	 * @param edit. An <code>UndoableEdit</code> object.
	 * @param probNet. The <code>ProbNet</code> on witch the event will operate 
	 */
	public PNUndoableEditEvent(
			Object source, UndoableEdit edit, ProbNet probNet) {
		super(source, edit);
		this.probNet = probNet;
	}
	
	// Methods
	/** @return probNet. <code>ProbNet</code> */
	public ProbNet getProbNet() {
		return probNet;
	}

}
