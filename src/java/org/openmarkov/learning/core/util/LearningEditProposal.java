/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.learning.core.util;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.exception.ConstraintViolationException;

/** An <code>LearningEditProposal</code> stores a <code>PNEdit</code> and the
 * increment of score associated to this edition. Also it stores a pointer
 * to the constraint violated by this edition.
 * @author joliva
 * @author manuel
 * @author fjdiez
 * @version 1.0
 * @since Carmen 1.0 */
public class LearningEditProposal implements Comparable<LearningEditProposal>{

    protected PNEdit edit;
    
    protected LearningEditMotivation motivation;
    
    protected ConstraintViolationException violatedConstraint;
    
    public LearningEditProposal(PNEdit edit, LearningEditMotivation motivation){
        this.edit = edit; 
        this.motivation = motivation;
        this.violatedConstraint = null;
    }
    
    public LearningEditProposal(PNEdit edit, LearningEditMotivation motivation, ConstraintViolationException e){
        this(edit, motivation);
        this.violatedConstraint = e;
    }    
    
    public PNEdit getEdit(){
        return edit;
    }
    
    public LearningEditMotivation getMotivation(){
        return motivation;
    }
    
    public Exception getViolatedConstraint(){
    	return violatedConstraint;
    }
    
    public boolean isAllowed()
    {
    	return violatedConstraint == null;
    }
    
    public boolean equals(Object obj){
        if(this == obj)
            return true;
        if((obj == null) || (obj.getClass() != this.getClass()))
            return false;
        return (this.edit.equals(((LearningEditProposal)obj).edit));
    }
    
    public String toString()
    {
        return new StringBuilder().append (edit.toString () + " " + motivation).toString (); 
    }

	@Override
	public int compareTo(LearningEditProposal editProposal) {
		return motivation.compareTo(editProposal.getMotivation());
	}
}
