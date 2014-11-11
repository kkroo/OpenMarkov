/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.io.elvira;

import java.util.Hashtable;

/** @author marias */
public class ReservedWordTokens {
	
	/** @return The enum that corresponds to the reserved word if it exists.
	 *  <code>ReservedWord</code>.
	 * @param word. <code>String</code> */
	public static ReservedWord getReservedWord(String word) {
		if (!hastablePresent) {
			createReservedWordTokens();
			hastablePresent = true;
		}
		return hashtable.get(word.toLowerCase());
	}
	
	/** Initial capacity for </code>Hashtable</code> (it works better with 
	 * prime numbers and much more than enough capacity.) */
	private final static int initialCapacity = 151;
	
	private static Hashtable<String, ReservedWord> hashtable;
	
	private static boolean hastablePresent = false;
	
	private static void createReservedWordTokens() {
		hashtable = new Hashtable<String, ReservedWord>(initialCapacity);
		hashtable.put("active", ReservedWord.ACTIVE);
		hashtable.put("and", ReservedWord.AND);
		hashtable.put("=", ReservedWord.ASSIGNMENT);
		hashtable.put("author", ReservedWord.AUTHOR);
        hashtable.put("autor", ReservedWord.AUTHOR);
		hashtable.put("bnet", ReservedWord.BNET);
		hashtable.put("causalmax", ReservedWord.CAUSAL_MAX);
		hashtable.put("causalmin", ReservedWord.CAUSAL_MIN);
		hashtable.put("chance", ReservedWord.CHANCE);
		hashtable.put(",", ReservedWord.COMMA);
		hashtable.put("comment", ReservedWord.COMMENT);
		hashtable.put("|", ReservedWord.CONDITIONED);
		hashtable.put("continuous", ReservedWord.CONTINUOUS);
		hashtable.put("decision", ReservedWord.DECISION);
		hashtable.put("default", ReservedWord.DEFAULT);
		hashtable.put("deterministic", ReservedWord.DETERMINISTIC);
		hashtable.put("false", ReservedWord.FALSE);
		hashtable.put("finite", ReservedWord.FINITE);
		hashtable.put("finite-states", ReservedWord.FINITE_STATES);
		hashtable.put("function", ReservedWord.FUNCTION);
		hashtable.put("generalizedmax", ReservedWord.GENERALIZED_MAX);
		hashtable.put("generalizedtable", ReservedWord.GENERALIZED_TABLE);		
		hashtable.put("henrionvsdiez", ReservedWord.HENRIONVSDIEZ);
		hashtable.put("idiagram", ReservedWord.IDIAGRAM);
		hashtable.put("id-with-svnodes", ReservedWord.IDIAGRAMSV);
		hashtable.put("kind-of-node", ReservedWord.KIND_OF_NODE);
		hashtable.put("kind-of-relation", ReservedWord.KIND_OF_RELATION);
		hashtable.put("kindofgraph", ReservedWord.KIND_OF_GRAPH);
		hashtable.put("{", ReservedWord.LEFTCB);
		hashtable.put("[", ReservedWord.LEFTSB);
		hashtable.put("(", ReservedWord.LEFTP);
		hashtable.put("link", ReservedWord.LINK);
		hashtable.put("max", ReservedWord.MAX);
		hashtable.put("min", ReservedWord.MIN);
		hashtable.put("name", ReservedWord.NAME);
		hashtable.put("name-of-relation", ReservedWord.NAME_OF_RELATION);
		hashtable.put("node", ReservedWord.NODE);
		hashtable.put("num-states", ReservedWord.NUM_STATES);
		hashtable.put("or", ReservedWord.OR);
		hashtable.put("pos_x", ReservedWord.POSX);
		hashtable.put("pos_y", ReservedWord.POSY);
		hashtable.put("potential", ReservedWord.POTENTIAL);
		hashtable.put("precision", ReservedWord.PRECISION);
		hashtable.put("product", ReservedWord.PRODUCT);
		hashtable.put("purpose", ReservedWord.PURPOSE);
		hashtable.put("relation", ReservedWord.RELATION);
		hashtable.put("relevance", ReservedWord.RELEVANCE);
		hashtable.put("//", ReservedWord.REMARK);
		hashtable.put("}", ReservedWord.RIGHTCB);
		hashtable.put("]", ReservedWord.RIGHTSB);
		hashtable.put(")", ReservedWord.RIGHTP);
		hashtable.put("states", ReservedWord.STATES);
		hashtable.put("sum", ReservedWord.SUM);
		hashtable.put("super-value", ReservedWord.SUPERVALUE);
		hashtable.put("table", ReservedWord.TABLE);
		hashtable.put("title", ReservedWord.TITLE);
		hashtable.put("true", ReservedWord.TRUE);
		hashtable.put("type-of-variable", ReservedWord.TYPE_OF_VARIABLE);
		hashtable.put("utility", ReservedWord.UTILITY);
		hashtable.put("version", ReservedWord.VERSION);
		hashtable.put("visualprecision", ReservedWord.VISUALPRECISION);
        hashtable.put("unit", ReservedWord.UNIT);
		hashtable.put("utility-combination", ReservedWord.UTILITYCOMBINATION);
		hashtable.put("values", ReservedWord.VALUES);
		hashtable.put("variable", ReservedWord.VARIABLE);
		hashtable.put("whenchanged", ReservedWord.WHENCHANGED);
		hashtable.put("whochanged", ReservedWord.WHOCHANGED);
	}

}
