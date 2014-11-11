/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.action;

import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;

@SuppressWarnings("serial")
public class OrientLinkEdit extends BaseLinkEdit{
	
	/** @param probNet <code>ProbNet</code>
	 * @param variable1 <code>Variable</code>
	 * @param variable2 <code>Variable</code>
	 * @param isDirected <code>boolean</code> */
	public OrientLinkEdit(ProbNet probNet, Variable variable1, 
			Variable variable2, boolean isDirected) {
		super(probNet, variable1, variable2, isDirected);
	}

	// Methods
	@Override
	/** Do the edition by removing the existing link and adding
	 * a new directed link between the same two variables. 
	 * @throws exception <code>DoEditException</code> */
	public void doEdit() throws DoEditException {
		try {
			probNet.removeLink(variable1, variable2, false);
			probNet.addLink(variable1, variable2, true);
		} catch (NodeNotFoundException e) {
			throw new DoEditException(e.getMessage() + e.getStackTrace());
		}
	}

	/** Undo the edition by removing the existing link and adding
	 * a new undirected link between the same two variables. */ 
	public void undo() {
		super.undo();
		try {
			probNet.removeLink(variable1, variable2, true);
			probNet.addLink(variable1, variable2, false);
		} catch (NodeNotFoundException e) {
			System.err.println(e.getMessage() + e.getStackTrace());
		}
	}
   
    /** Method to compare two directLinkEdits comparing the names of
     * the source and destination variables alphabetically.
     */
    public int compareTo(OrientLinkEdit obj){
        int result;

        if (( result = variable1.getName().compareTo(obj.getVariable1().
                getName())) != 0)
            return result;
        if (( result = variable2.getName().compareTo(obj.getVariable2().
                getName())) != 0)
            return result;
        else
            return 0;
    }

	@Override
	public String getOperationName() {
		return "Orient link";
	}

    @Override
    public BaseLinkEdit getUndoEdit ()
    {
        return this;
    }
}
