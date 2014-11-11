/*
 * Copyright 2012 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.dt;

import java.util.List;

import org.openmarkov.core.model.network.EvidenceCase;

public interface DecisionTreeElement
{
    public abstract List<DecisionTreeElement> getChildren();
    
    public abstract double getUtility ();
    
    public abstract EvidenceCase getBranchStates ();
    
    public abstract double getScenarioProbability();
    
    public abstract void setParent(DecisionTreeElement parent);
}
