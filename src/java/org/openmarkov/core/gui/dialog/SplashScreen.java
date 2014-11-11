/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

/**
 * Splash Screen Window. It is not using Java 6 SE SplashScreen functionality to
 * prevent backward compatibility on User desktop
 */
package org.openmarkov.core.gui.dialog;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;



/**
 * SplashScreen shows the OpenMarkov logo and the progress bar meantime OpenMarkov is
 * loaded.
 * 
 * @author jlgozalo
 * @version 1.0 22/11/2008
 */
public class SplashScreen extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6227174335233774982L;
	/** Component to store the image to splash */
	JLabel imageLabel = new JLabel();
	/** Component to present the progress of the loading */
	JProgressBar progressBar = new JProgressBar();
	/** Image to be displayed */
	ImageIcon imageIcon;
	
	private Logger logger;

	/**
	 * Constructor
	 * 
	 * @param imageIcon
	 *            The image to be used as Splash Screen
	 */
	public SplashScreen(ImageIcon imageIcon) {
		this.logger = Logger.getLogger(SplashScreen.class);
		this.imageIcon = imageIcon;
		try {
			jbInit();
		} catch (Exception ex) {
			//ExceptionsHandler.handleException(ex, null, true);
			logger.info(ex);
		}
	}

	/**
	 * Main initialization method to display visual components
	 * 
	 * @throws Exception
	 */
	void jbInit() throws Exception {

		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			this.setUndecorated(true);
			this.getContentPane().setLayout(new BorderLayout());

			progressBar.setStringPainted(true);
			progressBar.setForeground(new Color(10,110,230));
			this.getContentPane().add(progressBar, BorderLayout.SOUTH);

			imageLabel.setIcon(imageIcon);
			this.getContentPane().add(imageLabel, BorderLayout.CENTER);

			this.pack();
			this.setVisible(true);
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * States which will be the maximum progress to be displayed
	 * 
	 * @param maxProgress
	 */
	public void setProgressMax(int maxProgress) {

		progressBar.setMaximum(maxProgress);
	}

	/**
	 * Update the progress of the loading of the main program
	 * 
	 * @param progress
	 */
	public void setProgress(int progress) {

		final int theProgress = progress;
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {

				progressBar.setValue(theProgress);
			}
		});
	}

	/**
	 * Display the progress of the loading in a Progress Bar
	 * 
	 * @param message
	 *            The underlying message with the progress
	 * @param progress
	 *            The graphical bar with the progress
	 */
	public void setProgress(String message, int progress) {

		final int theProgress = progress;
		final String theMessage = message;
		setProgress(progress);
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {

				progressBar.setValue(theProgress);
				progressBar.setString(theMessage);
			}
		});
	}

	/**
	 * Show SplashScreen
	 * 
	 * @param b
	 *            True to put SplashScreen visible, false otherwise
	 */
	public void setScreenVisible(boolean b) {

		final boolean boo = b;
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {

				setVisible(boo);
			}
		});
	}
}