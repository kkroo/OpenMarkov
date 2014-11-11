/*
* Copyright 2012 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package org.openmarkov.learning.algorithm.pc.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.learning.core.util.LearningEditMotivation;
import org.openmarkov.learning.core.util.ScoreEditMotivation;

public class PCEditMotivation extends ScoreEditMotivation
{

	protected List<ProbNode> separationSet;
	
    public PCEditMotivation (double score, List<ProbNode> separationSet)
    {
        super (score);
        this.separationSet = separationSet;
    }
    
    public List<ProbNode> getSeparationSet()
    {
    	return separationSet;
    }
    
    public String toString()
    {
    	
    	String description ="{";
    	for (ProbNode node : separationSet)
    	{
    		description += node.getName()+", ";
    	}
    	
    	DecimalFormat df = new DecimalFormat("0.0000", new DecimalFormatSymbols(Locale.US));
    	
    	if (description.length() > 1)
    		description = description.substring (0, description.length () - 2) + 
    			"} p: " + df.format (getScore());
    	else
    		description += "} p: " + df.format (getScore());
    
        return description;
    }
    
    public int compareTo ( LearningEditMotivation edit ){
        int returnValue = 0;
        
        if (edit == null)
        {
        	returnValue = 1;
        }
        else if(edit instanceof PCEditMotivation)
        {
            if(((PCEditMotivation)edit).getSeparationSet ().size () > separationSet.size ())
            {
                returnValue = 1;
            }else if(((PCEditMotivation)edit).getSeparationSet ().size () < separationSet.size ()){
                returnValue = -1;
            }else{
                returnValue = super.compareTo (edit);
            }
        }
        return returnValue;
    }     
}
