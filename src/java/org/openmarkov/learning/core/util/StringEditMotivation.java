/*
* Copyright 2012 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.learning.core.util;

public class StringEditMotivation extends LearningEditMotivation
{
    private String motivation;
    
    public StringEditMotivation(String motivation)
    {
        this.motivation = motivation;
    }
    
    public int compareTo ( LearningEditMotivation edit ){
        return 0;
    }
    
    @Override
    public String toString()
    {
       return motivation;
    }

}
