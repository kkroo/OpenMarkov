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
import org.openmarkov.core.model.network.constraint.NoLinkRestriction;
import org.openmarkov.core.model.network.type.plugin.ProbNetType;

@ProbNetType(name="TuningNetwork")
public class TuningNetworkType extends NetworkType
{
    private static TuningNetworkType instance = null;

    // Constructor
    protected TuningNetworkType ()
    {
        super ();
        overrideConstraintBehavior (NoLinkRestriction.class, ConstraintBehavior.NO);
    }

    // Methods
    public static TuningNetworkType getUniqueInstance ()
    {
        if (instance == null)
        {
            instance = new TuningNetworkType ();
        }
        return instance;
    }

    /** @return String "Tuning" */
    public String toString() {
        return "TuningNetwork";
    }
}
