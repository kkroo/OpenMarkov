/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.action;

import java.util.ArrayList;

import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.StringWithProperties;
import org.openmarkov.core.model.network.type.NetworkType;

/**
 * <code>ChangeNetworkTypeEdit</code> is a edit that allow to change the 
 * <code>NetworkTypeConstraint</code> object to one network.
 * 
 *  
 * @version 1.0 21/12/10
 * @author mpalacios
 * 
 */
//TODO verify the performance when undo is executed.  
@SuppressWarnings("serial")
public class ChangeNetworkTypeEdit extends SimplePNEdit {
	
	/**
	 * The current NetworkTypeConstraint associated with the network
	 */
	private NetworkType currentNetworkType;
	
	/**
	 * The new NetworkTypeConstraint associated with the network
	 */
	private NetworkType newNetworkType;

	
	/**
	 * Creates a new <code>ChangeNetworkTypeEdit</code> that allow to change the 
	 *<code>NetworkTypeConstraint</code> object in the network.
	 * @param probNet the network that will be edited.
	 * @param newNetworkTypeConstraint the new <code>NetworkTypeConstraint</code>
	 * object
	 *      
	 */
    public ChangeNetworkTypeEdit (ProbNet probNet,
                                  NetworkType newNetworkTypeConstraint)
    {
        super (probNet);
        this.currentNetworkType = probNet.getNetworkType ();
        this.newNetworkType = newNetworkTypeConstraint;
    }

	// Methods
	@Override
	public void doEdit() throws DoEditException {
		if (newNetworkType != null) {
			try
            {
                probNet.setNetworkType(newNetworkType);
                if (probNet.isMultiagent()) {
                	ArrayList<StringWithProperties> agents = new ArrayList<StringWithProperties>();
        			agents.add(new StringWithProperties(" Agent 1"));
        			agents.add(new StringWithProperties(" Agent 2"));
        			probNet.setAgents(agents);
                }
            }
            catch (ConstraintViolationException e)
            {
                throw new DoEditException(e.getMessage ());
            }
		}
	}

	public void undo() {
		super.undo();
		if (newNetworkType != null) {
			try
            {
                probNet.setNetworkType(currentNetworkType);
                if (!probNet.isMultiagent()) {
                	probNet.setAgents(null);
                }
            }
            catch (ConstraintViolationException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		}
	}
}
