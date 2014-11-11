/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.window.edition;


/**
 * This class is used to translate the coordinates of the screen to the
 * coordinates of a panel, according to a zoom value.
 * 
 * @author jmendoza
 * @version 1.0
 * @version 1.1 jlgozalo - fix constants values adding final modifiers
 */
public class Zoom {

	/**
	 * Maximum value of zoom.
	 */
	public static final double MAX_VALUE = 5.0;

	/**
	 * Minimum value of zoom.
	 */
	public static final double MIN_VALUE = 0.1;

	/**
	 * Default value of zoom.
	 */
	public static final double DEFAULT_VALUE = 1.0;

	/**
	 * Value of zoom.
	 */
	private double zoom;

	/**
	 * Default constructor.
	 */
	public Zoom() {

		zoom = DEFAULT_VALUE;

	}

	/**
	 * Constructor that sets the zoom value.
	 * 
	 * @param newZoom
	 *            new zoom value.
	 */
	public Zoom(final double newZoom) {

		if (newZoom == 0) {
			setZoom(DEFAULT_VALUE);
		} else {
			setZoom(newZoom);
		}

	}

	/**
	 * Return the zoom value.
	 * 
	 * @return the zoom value of the object.
	 */
	public double getZoom() {

		return zoom;

	}

	/**
	 * Sets the zoom value.
	 * 
	 * @param value
	 *            new value of zoom.
	 */
	public void setZoom(double value) {

		if (value < MIN_VALUE) {
			zoom = MIN_VALUE;
		} else if (value > MAX_VALUE) {
			zoom = MAX_VALUE;
		} else {
			zoom = value;
		}

	}

	/**
	 * Converts a component of a coordinate of the screen to a component of a
	 * coordinate in the panel. The result must be rounded because if not, a
	 * little variation is added if the zoom isn't 1.0.
	 * 
	 * @param value
	 *            a component of a coordinate of the screen.
	 * @return a component of a coordinate of the panel.
	 */
	public double screenToPanel(double value) {

		return value / zoom;

	}

	/**
	 * Converts a component of a coordinate of the panel to a component of a
	 * coordinate in the screen. The result must be rounded because if not, a
	 * little variation is added if the zoom isn't 1.0.
	 * 
	 * @param value
	 *            a component of a coordinate of the panel.
	 * @return a component of a coordinate of the screen.
	 */
	public double panelToScreen(double value) {

		return value * zoom;

	}
}
