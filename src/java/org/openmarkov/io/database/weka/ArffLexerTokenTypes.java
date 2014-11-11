// $ANTLR : "arff.g" -> "ArffLexer.java"$

    package org.openmarkov.io.database.weka;
    import org.apache.log4j.Logger;
	import org.openmarkov.core.model.network.NodeType;
	import org.openmarkov.core.model.network.ProbNet;
	import org.openmarkov.core.model.network.ProbNode;
	import org.openmarkov.core.model.network.State;
	import org.openmarkov.core.model.network.Variable;
	import org.openmarkov.core.model.network.type.BayesianNetworkType;
    import java.util.*;

public interface ArffLexerTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int QUOTATION = 4;
	int COMMA = 5;
	int LEFTB = 6;
	int RIGHTB = 7;
	int WHITE = 8;
	int REMARK = 9;
	int SEPARATOR = 10;
	int IDENT = 11;
	int STRING = 12;
	int RELATION = 13;
	int ATTRIBUTE = 14;
	int DATA = 15;
}
