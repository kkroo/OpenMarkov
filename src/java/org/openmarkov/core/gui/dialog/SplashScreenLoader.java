/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog;



import java.net.URL;

import javax.swing.ImageIcon;

/**
 * SplashScreenOpenMarkov Splash Screen Loader in OpenMarkov to prevent impatient user
 * and to show the progress of loading elements in the Main Program
 * 
 * @author jlgozalo
 * @version 1.0 16/11/2008
 */
public class SplashScreenLoader {

	private SplashScreen splash;

	/**
	 * the logo file
	 */
	private final String logoFile =	"images/OpenMarkovSplash2.jpg" ;
	
	/**
	 * start the splash screen, do work and destroy
	 * @wbp.parser.entryPoint
	 */
	public SplashScreenLoader() {

		/*
		 * splashScreenInit(); simulateDoingWork(); splashScreenDestroy();
		 */
	}

	/**
	 * This method draws on the splash screen.
	 * @wbp.parser.entryPoint
	 */
	public void splashScreenInit() {

		// TODO externalize to OpenMarkov Properties the string for the icon
		
		
		
		URL url = this.getClass().getClassLoader().getResource(logoFile);
		ImageIcon myImage =
				new ImageIcon(url);
			splash = new SplashScreen(myImage);
			splash.setLocationRelativeTo(null);
			splash.setProgressMax(100);
			splash.setScreenVisible(true);
	
	}	

	/**
	 * simulate the main program is being loaded
	 */
	public void doingWork() {

		// do something here to simulate the program doing something that
		// is time consuming
		/*String poop = "";
		for (int i = 0; i <= 1000; i++) {
			for (long j = 0; j < 2000; ++j) {
				poop = " " + (j + i);
			}
			
		}
		*/

	}

	/**
	 * destroy the splash Screen turning not visible
	 */
	public void splashScreenDestroy() {

		splash.setScreenVisible(false);
	}

	/**
	 * get splash
	 * 
	 * @return aSplash The real splash screen
	 * @wbp.parser.entryPoint
	 */
	public SplashScreen getSplash() {

		return splash;
	}

}
