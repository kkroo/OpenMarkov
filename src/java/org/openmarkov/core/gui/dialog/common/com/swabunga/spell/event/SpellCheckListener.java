/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.common.com.swabunga.spell.event;


import java.util.EventListener;


/**
 * This is the event based listener interface.
 * 
 * @author Jason Height (jheight@chariot.net.au)
 */
public interface SpellCheckListener extends EventListener {

	public void spellingError(SpellCheckEvent event);
}
