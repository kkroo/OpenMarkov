/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.common.com.swabunga.spell.engine;


import java.util.Comparator;


/**
 * The Word object holds both the string and the score.
 * <p>
 * This class is now immutable.
 * </p>
 */
public class Word implements Comparator {

	private String word;
	private int score;

	public Word() {

	}

	public Word(String word, int score) {

		this.word = word;
		this.score = score;
	}

	/** The comparator interface */
	public int compare(Object o1, Object o2) {

		if (((Word) o1).getScore() < ((Word) o2).getScore())
			return -1;
		if (((Word) o1).getScore() == ((Word) o2).getScore())
			return 0;
		return 1;
	}

	public String getWord() {

		return word;
	}

	public int getScore() {

		return score;
	}

	public String toString() {

		return word;
	}
}
