/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/


package org.openmarkov.core.model.network.type;

import java.util.HashMap;

import org.openmarkov.core.model.network.constraint.ConstraintBehavior;
import org.openmarkov.core.model.network.constraint.ConstraintManager;
import org.openmarkov.core.model.network.constraint.PNConstraint;

public abstract class NetworkType
{
    protected HashMap<Class<? extends PNConstraint>, ConstraintBehavior> 
    	constraints;
    
    public NetworkType() {
        constraints = new HashMap<Class<? extends PNConstraint>, ConstraintBehavior>();
    }
    
    public boolean isApplicableConstraint(PNConstraint constraint)
    {
        ConstraintBehavior behavior = 
        		(constraints.get (constraint.getClass ()) != null) ? 
        				constraints.get (constraint.getClass ()) 
        			: 
        				ConstraintManager.getUniqueInstance ().
        					getDefaultBehavior (constraint.getClass ());
        return (behavior != ConstraintBehavior.NO);
    }
    
    protected void overrideConstraintBehavior(
    		Class<? extends PNConstraint> constraintClass, 
    		ConstraintBehavior behavior) {
        constraints.put (constraintClass, behavior);
    }    
    
    public HashMap<Class<? extends PNConstraint>, ConstraintBehavior> 
    		getOverwrittenConstraints () {
        return constraints;
    }
    
    /** @return An identifier that can be used in exception messages or text 
     * files. <code>String</code>*/
    public abstract String toString();
    
}
