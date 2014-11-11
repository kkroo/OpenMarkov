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
import org.openmarkov.core.model.network.constraint.OnlyChanceNodes;
import org.openmarkov.core.model.network.constraint.OnlyTemporalVariables;
import org.openmarkov.core.model.network.type.plugin.ProbNetType;

@ProbNetType(name="DBN")
public class DynamicBayesianNetwork extends NetworkType
{
    private static DynamicBayesianNetwork instance = null;

    // Constructor
    private DynamicBayesianNetwork ()
    {
        super();
        overrideConstraintBehavior (OnlyChanceNodes.class, ConstraintBehavior.YES);
        overrideConstraintBehavior (OnlyAtemporalVariables.class, ConstraintBehavior.NO);
        overrideConstraintBehavior (OnlyTemporalVariables.class, ConstraintBehavior.YES);
    }

    // Methods
    public static DynamicBayesianNetwork getUniqueInstance ()
    {
        if (instance == null)
        {
            instance = new DynamicBayesianNetwork ();
        }
        return instance;
    }
    
    /** @return String "DynamicBayesianNetwork" */
    public String toString() {
    	return "DYN_BAYESIAN_NET";
    }

}

