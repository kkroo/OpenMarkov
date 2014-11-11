/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog;


import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;

import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;

import org.apache.log4j.Logger;
import org.openmarkov.core.gui.configuration.OpenMarkovPreferences;
import org.openmarkov.core.gui.localize.LocaleChangeEvent;
import org.openmarkov.core.gui.localize.LocaleChangeListener;
import org.openmarkov.core.gui.localize.StringDatabase;


/**
 * Help Viewer using standard JavaHelp
 * 
 * @author jlgozalo
 * @version 1.0 15/03/2009 jlgozalo
 */
public class HelpViewer extends javax.swing.JFrame implements
						LocaleChangeListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 332731030432142803L;
	/** 
	 * HelpViewer unique instance. Used in singleton pattern. */
	private static HelpViewer helpViewer = null;
	/**
	 * the Helpset used to display the help
	 */
	private HelpSet theHelpSet = null;
	/**
	 * the HelpBroker used to display the help
	 */
	private HelpBroker theHelpBroker = null;
	/**
	 * X Dimension of the Help Viewer
	 */
	private static final int HELP_VIEWER_X_DIMENSION  = 
		OpenMarkovPreferences.getInteger(OpenMarkovPreferences.X_OPEMARKOV_HELP_DIMENSION, 
		                             OpenMarkovPreferences.OPENMARKOV_POSITIONS,640);
	/**
	 * Y Dimension of the Help Viewer
	 */
	private static final int HELP_VIEWER_Y_DIMENSION  = 
		OpenMarkovPreferences.getInteger(OpenMarkovPreferences.Y_OPENMARKOV_HELP_DIMENSION, 
		                             OpenMarkovPreferences.OPENMARKOV_POSITIONS,480);
	
	private Logger logger;
	
	/**
	 * private constructor
	 */
	private HelpViewer() {
		StringDatabase.getUniqueInstance()
	              	.addLocaleChangeListener( this );
    	this.theHelpBroker = myHelpBroker();
    	this.logger = Logger.getLogger(HelpViewer.class);
    	
	}

	/** 
	 * @return HelpViewer unique instance (singleton pattern) 
	 */
	public static HelpViewer getUniqueInstance() {
		if (helpViewer == null) {
			helpViewer = new HelpViewer();
		}
		return helpViewer;
	}
	/**
	 * Open HelpSet and send a message to Log in case it is not possible
	 * 
	 * @return myHelpBroker to display the Help System
	 */
	public HelpBroker myHelpBroker() {

		HelpBroker aHelpBroker = null;
		String aHelpSet = "helpset.hs";
		theHelpBroker = null;
		
		try {
			// find HelpSet from within the library of OpenMarkov
			URL hsURL = HelpSet.findHelpSet(getClass().getClassLoader(), aHelpSet);
			URL realHsURL = hsURL;
			String language = StringDatabase.getUniqueInstance().getLanguage();
   		    if (language.equals("en") ) {
				if (hsURL.toString().contains( "_es" )) {
				   realHsURL = new URL(hsURL.toString().replace("_es","_en"));
				}
			} else if (language.equals( "es")) { 
				if (hsURL.toString().contains( "_en" )) {
					realHsURL = new URL(hsURL.toString().replace("_en","_es"));
				}
			} else {
				System.out.println("HelpSet " + aHelpSet + " for language " 
				                   + language + " not found");
				System.out.println("Opening english as default help...");
			}
			System.out.println("HelpSet originalURL " + hsURL );
			System.out.println("HelpSet realURL " + realHsURL );
			theHelpSet = new HelpSet(null, realHsURL);
		} catch (MalformedURLException ex) {
			logger.info(ex.getMessage() + " URL for Help is bad formed");
			//ExceptionsHandler.handleException( ex, "URL for Help is bad formed", false );
			theHelpSet = null;
		} catch (HelpSetException ex) {
			logger.info( ex.getMessage() + "Helpset "+aHelpSet + " not found" );
			//ExceptionsHandler.handleException( ex, "Helpset "+aHelpSet + " not found", false );
			theHelpSet = null;
		}
		
		if (theHelpSet == null) {
			aHelpBroker = null;
		} else {
			// create HelpBroker from HelpSet
			aHelpBroker = theHelpSet.createHelpBroker();
			aHelpBroker.setSize(new Dimension(HELP_VIEWER_X_DIMENSION,
			                                  HELP_VIEWER_Y_DIMENSION));
		}
		return aHelpBroker;
	}

	/**
	 * @return the helpset
	 */
	public HelpSet getHs() {

		return theHelpSet;
	}

	/**
	 * @param aHelpSet
	 *            the helpset to set
	 */
	public void setHs(HelpSet aHelpSet) {

		this.theHelpSet = aHelpSet;
	}

	/**
	 * @return the helpBroker
	 */
	public HelpBroker getHb() {

		return theHelpBroker;
	}

	/**
	 * @param aHelpBroker
	 *            the helpbroker to set
	 */
	public void setHb(HelpBroker aHelpBroker) {

		this.theHelpBroker = aHelpBroker;
	}
	
	/**
	 * process a change in the String Resource Locale, settings the help 
	 * to the new selected language
	 */
	public void processLocaleChange(LocaleChangeEvent event) {

		helpViewer.dispose();
		helpViewer = new HelpViewer();
		//repaint();
	}

}
