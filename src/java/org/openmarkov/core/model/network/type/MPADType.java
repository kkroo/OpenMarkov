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

@ProbNetType(name="MPAD")
public class MPADType extends NetworkType
{
    // Attributes
    private static MPADType instance = null;

    // Constructor
    private MPADType ()
    {
        super ();
        overrideConstraintBehavior (OnlyAtemporalVariables.class, ConstraintBehavior.NO);
        overrideConstraintBehavior (OnlyTemporalVariables.class, ConstraintBehavior.NO);
    }

    // Methods
    public static MPADType getUniqueInstance ()
    {
        if (instance == null)
        {
            instance = new MPADType ();
        }
        return instance;
    }

    /** @return String "MPAD" */
    public String toString() {
        return "MARKOV_PROCESS_WITH_ATEMPORAL_DECISIONS";
    }
    
}
