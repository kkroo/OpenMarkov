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

@ProbNetType(name="DynamicLIMID")
public class DynamicLimidType extends NetworkType
{
    private static DynamicLimidType instance = null;

    // Constructor
    private DynamicLimidType ()
    {
        super();
        
        overrideConstraintBehavior (OnlyAtemporalVariables.class, ConstraintBehavior.NO);
        overrideConstraintBehavior (OnlyTemporalVariables.class, ConstraintBehavior.YES);
    }

    // Methods
    public static DynamicLimidType getUniqueInstance ()
    {
        if (instance == null)
        {
            instance = new DynamicLimidType ();
        }
        return instance;
    }
    
    /** @return String "DynamicLIMID" */
    public String toString() {
    	return "DYN_LIMID";
    }
    
}


