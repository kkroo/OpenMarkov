/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
/**
 * 
 */

package org.openmarkov.core.gui.localize;

import java.util.EventListener;

/**
 * Interface for LocaleChangeEvent Listeners
 * @author jlgozalo
 * @version 1.0 25 Jun 2009
 */
public interface LocaleChangeListener
    extends
        EventListener
{
    public abstract void processLocaleChange (LocaleChangeEvent event);
}