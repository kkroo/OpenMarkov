/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.window.message;


import java.io.PrintStream;


/**
 * This class forwards the character stream to the text area.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 * @version 1.1 jlgozalo - adding exceptions for code quality checking
 */
public class StandardStream extends PrintStream {

	/**
	 * Object where to write messages.
	 */
	protected MessageArea messageArea = null;

	/**
	 * Constructor that links this object with the text area.
	 * 
	 * @param newMessageArea
	 *            area where the messages are written.
	 */
	public StandardStream(MessageArea newMessageArea) {

		// ESCA-JAVA0266: use of System.out allowed in this point
		super(System.out);
		messageArea = newMessageArea;
	}

	// ESCA-JAVA0025: Allowing method with no statement inside
	// ESCA-JAVA0132:Allowing overriding of print(Object) with print(String)
	/**
	 * Prints a string.
	 * 
	 * @param x
	 *            the string to be printed.
	 */
	@Override
	public void print(String x) {

	};

	/**
	 * Terminate the current line by writing the line separator string. The line
	 * separator string is defined by the system property line.separator.
	 */
	@Override
	public void println() {

	};

	/**
	 * Prints a boolean.
	 */
	@Override
	public void print(boolean x) {

		// ESCA-JAVA0278: ensures a correct boolean value is printed
		print(new Boolean(x).toString());
	}

	/**
	 * Prints a character.
	 */
	@Override
	public void print(char x) {

		print(new Character(x).toString());
	}

	/**
	 * Prints an integer.
	 */
	@Override
	public void print(int x) {

		// ESCA-JAVA0153: ensures the correct integer value is printed
		print(new Integer(x).toString());
	}

	/**
	 * Prints a long.
	 */
	@Override
	public void print(long x) {

		print(new Long(x).toString());
	}

	/**
	 * Prints a float.
	 */
	@Override
	public void print(float x) {

		print(new Float(x).toString());
	}

	/**
	 * Prints a double.
	 */
	@Override
	public void print(double x) {

		print(new Double(x).toString());
	}

	/**
	 * Prints an array.
	 */
	@Override
	public void print(char[] x) {

		print(new String(x));
	}

	/**
	 * Prints an object.
	 */
	@Override
	public void print(Object x) {

		if (x == null) {
			print("null");
		} else {
			print(x.toString());
		}
	}

	/**
	 * Prints a boolean and then terminates the line.
	 */
	@Override
	public void println(boolean x) {

		println(new Boolean(x).toString());
	}

	/**
	 * Prints a character and then terminates the line.
	 */
	@Override
	public void println(char x) {

		println(new Character(x).toString());
	}

	/**
	 * Prints an integer and then terminates the line.
	 */
	@Override
	public void println(int x) {

		// ESCA-JAVA0153: ensures the correct integer value is printed
		println(new Integer(x).toString());
	}

	/**
	 * Prints a long and then terminates the line.
	 */
	@Override
	public void println(long x) {

		println(new Long(x).toString());
	}

	/**
	 * Prints a float and then terminates the line.
	 */
	@Override
	public void println(float x) {

		println(new Float(x).toString());
	}

	/**
	 * Prints a double and then terminates the line.
	 */
	@Override
	public void println(double x) {

		println(new Double(x).toString());
	}

	/**
	 * Prints an array of characters and then terminates the line.
	 */
	@Override
	public void println(char[] x) {

		println(new String(x));
	}

	/**
	 * Prints an object and then terminates the line.
	 */
	@Override
	public void println(Object x) {

		if (x == null) {
			println("null");
		} else {
			println(x.toString());
		}
	}

	/**
	 * Prints a string and then terminates the line.
	 * 
	 * @param x
	 *            the string to be printed.
	 */
	@Override
	public void println(String x) {

		print(x);
		println();
	}
}
