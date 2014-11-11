/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

/**
 * LocaleChangeEvent
 */
package org.openmarkov.core.gui.localize;


import java.util.EventObject;
import java.util.Locale;

/**
 * Event for Locale Changes in the OPENMARKOV project
 * 
 * @author jlgozalo
 * @version 1.0 26 Jun 2009
 */
public class LocaleChangeEvent extends EventObject {

	/**
	 * internal serial id
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * the language of the locale
	 */
	private String language = "";
	/** 
	 * the locale
	 */
	private Locale locale = null;

	/**
	 * default LocaleChangeEvent constructor when the language is set. In this 
	 * case, the locale for the LocaleChangeEvent is internally set.
	 * 
	 * @param language
	 */
	public LocaleChangeEvent(Object source, final String language) {

		super(source);
		this.language = language;
		if (language.equals(Locale.ENGLISH.getLanguage ())) {
			locale = Locale.ENGLISH;
		} else if (language.equals("es")) { 
			locale = new Locale("es");
		} else {
			//System.out.println("LocaleChangeEvent failure for locale " 
			//                   + locale.toString() + ": not defined");
			//System.out.println("Setting english as default locale...");
			locale = Locale.ENGLISH;
	}
}

	/**
	 * default LocaleChangeEvent constructor when the locale is set. In this 
	 * case, the language for the LocaleChangeEvent is internally set.
	 * 
	 * @param locale
	 */
	public LocaleChangeEvent(Object source, final Locale locale) {

		super(source);
		this.locale = locale;
		if (locale.equals(Locale.ENGLISH)) {
			language = "en";
		} else if (locale.toString().equals("es")) { 
			language = "es";
		} else {
			//System.out.println("LocaleChangeEvent failure for locale " 
			//                   + locale.toString() + ": not defined");
			//System.out.println("Setting english as default locale...");
			language = "en";
		}

	}
	/**
	 * get the language of the LocaleChangeEvent
	 */
	public String getLanguage() {

		return language;
	}

	
	/**
	 * @return the locale
	 */
	public Locale getLocale() {
	
		return locale;
	}
}