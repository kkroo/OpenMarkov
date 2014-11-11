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
import org.openmarkov.core.model.network.constraint.OnlyAtemporalVariables;
import org.openmarkov.core.model.network.constraint.OnlyTemporalVariables;
import org.openmarkov.core.model.network.type.plugin.ProbNetType;

@ProbNetType(name="POMDP")
public class POMDPType extends NetworkType
{
    private static POMDPType instance = null;

    // Constructor
    protected POMDPType ()
    {
        super ();
        overrideConstraintBehavior (OnlyAtemporalVariables.class, ConstraintBehavior.NO);
        overrideConstraintBehavior (OnlyTemporalVariables.class, ConstraintBehavior.YES);
    }

    // Methods
    public static POMDPType getUniqueInstance ()
    {
        if (instance == null)
        {
            instance = new POMDPType ();
        }
        return instance;
    }

    /** @return String "POMDP" */
    public String toString() {
    	return "POMDP";
    }
    
}

