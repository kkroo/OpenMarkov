/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.model.network.type;

import org.openmarkov.core.model.network.constraint.ConstraintBehavior;
import org.openmarkov.core.model.network.constraint.OnlyChanceNodes;
import org.openmarkov.core.model.network.constraint.OnlyDirectedLinks;
import org.openmarkov.core.model.network.constraint.OnlyUndirectedLinks;
import org.openmarkov.core.model.network.type.plugin.ProbNetType;

@ProbNetType(name="MarkovNetwork")
public class MarkovNetworkType extends NetworkType
{
    private static MarkovNetworkType instance = null;

    // Constructor
    private MarkovNetworkType ()
    {
        super();
        overrideConstraintBehavior (OnlyChanceNodes.class, ConstraintBehavior.YES);
        overrideConstraintBehavior (OnlyDirectedLinks.class, ConstraintBehavior.NO);
        overrideConstraintBehavior (OnlyUndirectedLinks.class, ConstraintBehavior.YES);        
    }

    // Methods
    public static MarkovNetworkType getUniqueInstance ()
    {
        if (instance == null)
        {
            instance = new MarkovNetworkType ();
        }
        return instance;
    }

    /** @return String "MarkovNetwork" */
    public String toString() {
    	return "MARKOV_NET";
    }
    
}

