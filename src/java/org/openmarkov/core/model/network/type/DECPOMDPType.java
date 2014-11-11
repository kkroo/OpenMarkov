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
import org.openmarkov.core.model.network.constraint.OnlyOneAgent;
import org.openmarkov.core.model.network.type.plugin.ProbNetType;

@ProbNetType(name="DEC_POMDP")
public class DECPOMDPType extends POMDPType
{
    private static DECPOMDPType instance = null;

    // Constructor
    private DECPOMDPType ()
    {
        super();
        
        overrideConstraintBehavior (OnlyOneAgent.class, ConstraintBehavior.NO);
    }

    // Methods
    public static DECPOMDPType getUniqueInstance ()
    {
        if (instance == null)
        {
            instance = new DECPOMDPType ();
        }
        return instance;
    }

    /** @return String "DECPOMDP" */
    public String toString() {
    	return "DEC_POMDP";
    }
    
}

