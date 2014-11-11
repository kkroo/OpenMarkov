/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

/*
 * GNU Lesser General Public License Translatrix - General Access To Language
 * Resource Bundles Copyright (C) 2002 Howard A Kistler This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.openmarkov.core.gui.dialog.common.com.hexidec.util;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.gui.localize.XMLResourceBundle;


public class Translatrix {

	private static ResourceBundle langResources;
	private static String bundleName;

	public Translatrix(String bundle) {

		bundleName = new String(bundle);
		try {
			langResources = getBundle(bundleName);
		} catch (MissingResourceException mre) {
			logException(
				"MissingResourceException while loading language file", mre);
		}
	}
	
	
	/**
	 * @param bundleName
	 * @param locale
	 * @return The Bundle by calling the method 'createXMLResourceBundle' from the unique
	 * instance of StringResourceLoader
	 */
	private static ResourceBundle getBundle(String bundleName,Locale ...locale){
		
		Locale tempLocale;
		
		if ((locale!=null)&&(locale.length>0)){
			tempLocale = locale[0];
		}
		else{
			tempLocale = new Locale("en_US");
		}
		
		return ResourceBundle.getBundle (bundleName, tempLocale, new ResourceBundle.Control ()
        {
            public java.util.List<String> getFormats (String baseName)
            {
                if (baseName == null) throw new NullPointerException ();
                return Arrays.asList ("xml");
            }

            public ResourceBundle newBundle (String baseName,
                                             Locale locale,
                                             String format,
                                             ClassLoader loader,
                                             boolean reload)
                throws IllegalAccessException,
                InstantiationException,
                IOException
            {
                if (baseName == null || locale == null || format == null || loader == null) throw new NullPointerException ();
                ResourceBundle bundle = null;
                if (format.equals ("xml"))
                {
                    String bundleName = toBundleName (baseName, locale);
                    String resourceName = toResourceName (bundleName, format);
                    InputStream stream = null;
                    if (reload)
                    {
                        URL url = loader.getResource (resourceName);
                        if (url != null)
                        {
                            URLConnection connection = url.openConnection ();
                            if (connection != null)
                            {
                                // Disable caches to get fresh data for
                                // reloading.
                                connection.setUseCaches (false);
                                stream = connection.getInputStream ();
                            }
                        }
                    }
                    else
                    {
                        stream = loader.getResourceAsStream (resourceName);
                    }
                    if (stream != null)
                    {
                        BufferedInputStream bis = new BufferedInputStream (stream);
                        bundle = new XMLResourceBundle (bis);
                        bis.close ();
                    }
                }
                return bundle;
            }
        });
	}

	public static void setBundleName(String bundle) {

		bundleName = new String(bundle);
		try {
			langResources = getBundle(bundleName);
		} catch (MissingResourceException mre) {
			logException(
				"MissingResourceException while loading language file", mre);
		}
	}

	public static void setLocale(Locale locale) {

		if (bundleName == null) {
			return;
		}
		if (locale != null) {
			try {
				langResources = getBundle(bundleName, locale);
			} catch (MissingResourceException mre1) {
				try {
					langResources = getBundle(bundleName);
				} catch (MissingResourceException mre2) {
					logException(
						"MissingResourceException while loading language file",
						mre2);
				}
			}
		} else {
			try {
				langResources = getBundle(bundleName);
			} catch (MissingResourceException mre) {
				logException(
					"MissingResourceException while loading language file", mre);
			}
		}
	}

	public static void setLocale(String sLanguage, String sCountry) {

		if (sLanguage != null && sCountry != null) {
			setLocale(new Locale(sLanguage, sCountry));
		}
	}

	public static String getTranslationString(String originalText) {

		if (langResources == null || bundleName == null) {
			return originalText;
		} else {
			try {
				return langResources.getString(originalText);
			} catch (Exception e) {
				return originalText;
			}
		}
	}

	private static void logException(String internalMessage, Exception e) {

		System.err.println(internalMessage);
		e.printStackTrace(System.err);
	}

}