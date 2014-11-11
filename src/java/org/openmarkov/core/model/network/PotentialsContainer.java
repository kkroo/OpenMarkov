/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

/**
 * 
 */
package org.openmarkov.core.model.network;

import java.util.List;

import org.openmarkov.core.model.network.potential.Potential;


/**
 * @author Miguel Palacios
 *
 */
public interface PotentialsContainer {
	
	public List<Potential> getPotentials(Variable variable);
	/** @param potential. <code>Potential</code> */
    public void addPotential(Potential potential);
    /** @param potential. <code>Potential</code> */
    public void setPotentials(List <Potential> potential);
    /** @param potential. <code>Potential</code>
     * @return <code>true</code> if <code>potentialList</code> contained the
     *   specified element; otherwise <code>false</code>. */
    public boolean removePotential(Potential potential);

}
