// $ANTLR : "elviraDB.g" -> "ElviraDBParser.java"$

    package org.openmarkov.io.database.elvira;
    import org.apache.log4j.Logger;
	import org.openmarkov.core.model.network.NodeType;
	import org.openmarkov.core.model.network.ProbNet;
	import org.openmarkov.core.model.network.ProbNode;
	import org.openmarkov.core.model.network.State;
	import org.openmarkov.core.model.network.Variable;
	import org.openmarkov.core.model.network.type.BayesianNetworkType;
	import org.openmarkov.core.model.network.VariableType;
    import java.util.*;
    import java.lang.Integer;
    import java.util.Map.Entry;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.collections.AST;
import java.util.Hashtable;
import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;

public class ElviraDBParser extends antlr.LLkParser       implements ElviraDBLexerTokenTypes
 {

	/** Global variable */
	private ArrayList<Variable> fsVariables = new ArrayList<Variable>();

	/** Global variable to store the variables while they are reading from
	 * an input file */
	private ProbNet probNet = new ProbNet(BayesianNetworkType.getUniqueInstance());

	private ProbNet bayesNet = new ProbNet(BayesianNetworkType.getUniqueInstance());

	/** Stores information about Elvira nets */
	private HashMap<String, Object> ioNet = new HashMap<String, Object>();
	
	private int numberOfCases = 0;
	
	private int casesCont = 0;
	
	private int[][] cases;
	
	private int[] example;
	
	private int index=0;

	/** Default states. Global variable */
	private String[] defaultStates;
	
	/** A utility node has no states, for computational reasons we define a
      * set of one state */
	private String[] defaultUtilityStates={""};
	
	/** Messages file */
	private Logger logFile = Logger.getLogger (ElviraDBParser.class);
	
	// Chance = Potential; Utility = utility
	private NodeType kindRelation = NodeType.CHANCE;  
	
	public ProbNet getProbNet(){
		return probNet;
	}
	
	public int[][] getCases(){
		return cases;
	}
	
    /** Adds a <code>ProbNode</code> to the <code>ProbNet</code>. */
	public ProbNode addProbNode(HashMap<String, Object> infoNode, State[] states,
			NodeType nodeType, String nodeName) {

		Variable fsVariable = null;
		String statesSource;
		int numStates;
		HashMap <String,String> properties = new HashMap<String, String>();
		
		if (states.length != 0) {
			fsVariable = new Variable(nodeName, states);
		}
		ProbNode probNode = null;
		try {
			probNode = probNet.addProbNode(fsVariable, nodeType);
			/* Copy additionalProperties */
			for (Entry<String, Object> property : infoNode.entrySet())
            	properties.put(property.getKey(), property.getValue().toString());
			probNode.additionalProperties = properties;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return probNode;
	}

protected ElviraDBParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public ElviraDBParser(TokenBuffer tokenBuf) {
  this(tokenBuf,7);
}

protected ElviraDBParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public ElviraDBParser(TokenStream lexer) {
  this(lexer,7);
}

public ElviraDBParser(ParserSharedInputState state) {
  super(state,7);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final String  kindofgraph() throws RecognitionException, TokenStreamException {
		String kindOfGraph=null;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST kindofgraph_AST = null;
		Token  s = null;
		AST s_AST = null;
		Token  i = null;
		AST i_AST = null;
		
		try {      // for error handling
			match(KINDOFGRAPH);
			match(ASSIGNMENT);
			{
			switch ( LA(1)) {
			case STRING:
			{
				s = LT(1);
				s_AST = astFactory.create(s);
				astFactory.addASTChild(currentAST, s_AST);
				match(STRING);
				break;
			}
			case IDENT:
			{
				i = LT(1);
				i_AST = astFactory.create(i);
				astFactory.addASTChild(currentAST, i_AST);
				match(IDENT);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			
				if (s != null) {
					kindOfGraph = s.getText();
				} else if (i != null) {
					kindOfGraph = i.getText();
				}
			
			kindofgraph_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = kindofgraph_AST;
		return kindOfGraph;
	}
	
	public final HashMap<String, Object>  database() throws RecognitionException, TokenStreamException {
		HashMap<String, Object> bn=null;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST database_AST = null;
		Token  b = null;
		AST b_AST = null;
		Token  s1 = null;
		AST s1_AST = null;
		Token  s2 = null;
		AST s2_AST = null;
		AST n_AST = null;
		AST k_AST = null;
		AST t_AST = null;
		AST c_AST = null;
		AST a_AST = null;
		AST who_AST = null;
		AST when_AST = null;
		AST vp_AST = null;
		AST v_AST = null;
		
			String titleNet = null, commentNet = null, authorNet = null,
				whochangedNet = null, whenchangedNet = null, name = null,
				kindOfGraph = null;
			double visualprecisionNet = Double.MIN_VALUE, versionNet = Double.MIN_VALUE;
			
		ioNet.put("BayesNet", bayesNet);
		ioNet.put("Name", name);
		ioNet.put("ProbNet", bayesNet);
		probNet = bayesNet;
		
		
		try {      // for error handling
			b = LT(1);
			b_AST = astFactory.create(b);
			astFactory.makeASTRoot(currentAST, b_AST);
			match(DATABASE);
			{
			switch ( LA(1)) {
			case STRING:
			{
				s1 = LT(1);
				s1_AST = astFactory.create(s1);
				astFactory.addASTChild(currentAST, s1_AST);
				match(STRING);
				break;
			}
			case IDENT:
			{
				s2 = LT(1);
				s2_AST = astFactory.create(s2);
				astFactory.addASTChild(currentAST, s2_AST);
				match(IDENT);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(LEFTB);
			{
			numberOfCases=numberofcases();
			n_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			}
			{
			switch ( LA(1)) {
			case KINDOFGRAPH:
			{
				kindOfGraph=kindofgraph();
				k_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case COMMENT:
			case VISUALPRECISION:
			case VERSION:
			case DEFAULT:
			case NODE:
			case TITLE:
			case AUTHOR:
			case WHOCHANGED:
			case WHENCHANGED:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case TITLE:
			{
				titleNet=title();
				t_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case COMMENT:
			case VISUALPRECISION:
			case VERSION:
			case DEFAULT:
			case NODE:
			case AUTHOR:
			case WHOCHANGED:
			case WHENCHANGED:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case COMMENT:
			{
				commentNet=comment();
				c_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case VISUALPRECISION:
			case VERSION:
			case DEFAULT:
			case NODE:
			case AUTHOR:
			case WHOCHANGED:
			case WHENCHANGED:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case AUTHOR:
			{
				authorNet=author();
				a_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case VISUALPRECISION:
			case VERSION:
			case DEFAULT:
			case NODE:
			case WHOCHANGED:
			case WHENCHANGED:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case WHOCHANGED:
			{
				whochangedNet=whochanged();
				who_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case VISUALPRECISION:
			case VERSION:
			case DEFAULT:
			case NODE:
			case WHENCHANGED:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case WHENCHANGED:
			{
				whenchangedNet=whenchanged();
				when_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case VISUALPRECISION:
			case VERSION:
			case DEFAULT:
			case NODE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case VISUALPRECISION:
			{
				visualprecisionNet=visualprecision();
				vp_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case VERSION:
			case DEFAULT:
			case NODE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case VERSION:
			{
				versionNet=version();
				v_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case DEFAULT:
			case NODE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case DEFAULT:
			{
				defaultnodestates();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case NODE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			nodes();
			astFactory.addASTChild(currentAST, returnAST);
			relation();
			astFactory.addASTChild(currentAST, returnAST);
			match(RIGHTB);
			
			
				if (s1 == null) {
					name = s2.getText();
				} else {
					name = s1.getText();
				}
			ioNet.put("Name", name);
			
			if (kindOfGraph != null) {
				ioNet.put("KindOfGraph", kindOfGraph);
			}
			if (titleNet != null) {
				ioNet.put("TitleNet", titleNet);
			}
			if (commentNet != null) {
				ioNet.put("CommentNet", commentNet);
			}
			if (authorNet != null) {
				ioNet.put("AuthorNet", authorNet);
			}
			if (whochangedNet != null) {
				ioNet.put("WhoChanged", whochangedNet);
			}
			if (whenchangedNet != null) {
				ioNet.put("WhenChangedNet", whenchangedNet);
			}
			if (visualprecisionNet != Double.MIN_VALUE) {
				ioNet.put("VisualPrecisionNet", visualprecisionNet);
			}
			if (versionNet != Double.MIN_VALUE) {
				ioNet.put("VersionNet", versionNet);
			}
			
				logFile.debug("Bayes net name: " + name);
				logFile.debug("--------------------------");
				bn = ioNet; 
			
			database_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		returnAST = database_AST;
		return bn;
	}
	
	public final int  numberofcases() throws RecognitionException, TokenStreamException {
		int numberCases=0;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST numberofcases_AST = null;
		AST n_AST = null;
		
		try {      // for error handling
			match(NUMBER);
			match(HYPHEN);
			match(OF);
			match(HYPHEN);
			match(CASES);
			match(ASSIGNMENT);
			numberCases=integer();
			n_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			numberofcases_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		returnAST = numberofcases_AST;
		return numberCases;
	}
	
	public final String  title() throws RecognitionException, TokenStreamException {
		String title = null;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST title_AST = null;
		Token  s = null;
		AST s_AST = null;
		
		try {      // for error handling
			match(TITLE);
			match(ASSIGNMENT);
			s = LT(1);
			s_AST = astFactory.create(s);
			astFactory.addASTChild(currentAST, s_AST);
			match(STRING);
			
				String returnString = s.getText();
				logFile.debug("Titulo: "+returnString);
				title = returnString;
			
			title_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		returnAST = title_AST;
		return title;
	}
	
	public final String  comment() throws RecognitionException, TokenStreamException {
		String com=null;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST comment_AST = null;
		Token  c = null;
		AST c_AST = null;
		Token  a = null;
		AST a_AST = null;
		Token  s = null;
		AST s_AST = null;
		
		try {      // for error handling
			c = LT(1);
			c_AST = astFactory.create(c);
			astFactory.addASTChild(currentAST, c_AST);
			match(COMMENT);
			a = LT(1);
			a_AST = astFactory.create(a);
			astFactory.addASTChild(currentAST, a_AST);
			match(ASSIGNMENT);
			s = LT(1);
			s_AST = astFactory.create(s);
			astFactory.addASTChild(currentAST, s_AST);
			match(STRING);
			
				String co = s.getText();
				com = co;
				logFile.debug(c.getText()+" "+a.getText()+" "+co);
			
			comment_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_4);
		}
		returnAST = comment_AST;
		return com;
	}
	
	public final String  author() throws RecognitionException, TokenStreamException {
		String aut=null;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST author_AST = null;
		Token  s = null;
		AST s_AST = null;
		
		try {      // for error handling
			match(AUTHOR);
			match(ASSIGNMENT);
			s = LT(1);
			s_AST = astFactory.create(s);
			astFactory.addASTChild(currentAST, s_AST);
			match(STRING);
			
				String auth = s.getText();
				logFile.debug("Autor: "+auth);
				aut = auth;
			
			author_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_5);
		}
		returnAST = author_AST;
		return aut;
	}
	
	public final String  whochanged() throws RecognitionException, TokenStreamException {
		String aut=null;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST whochanged_AST = null;
		Token  s = null;
		AST s_AST = null;
		
		try {      // for error handling
			match(WHOCHANGED);
			match(ASSIGNMENT);
			s = LT(1);
			s_AST = astFactory.create(s);
			astFactory.addASTChild(currentAST, s_AST);
			match(STRING);
			
			String auth = s.getText();
			logFile.debug("Whochanged: "+auth);
			aut = auth;
			
			whochanged_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_6);
		}
		returnAST = whochanged_AST;
		return aut;
	}
	
	public final String  whenchanged() throws RecognitionException, TokenStreamException {
		String aut=null;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST whenchanged_AST = null;
		Token  s = null;
		AST s_AST = null;
		
		try {      // for error handling
			match(WHENCHANGED);
			match(ASSIGNMENT);
			s = LT(1);
			s_AST = astFactory.create(s);
			astFactory.addASTChild(currentAST, s_AST);
			match(STRING);
			
			String auth = s.getText();
			logFile.debug("Whenchanged: "+auth);
			aut = auth;
			
			whenchanged_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		returnAST = whenchanged_AST;
		return aut;
	}
	
	public final double  visualprecision() throws RecognitionException, TokenStreamException {
		double realNumber=0;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST visualprecision_AST = null;
		Token  r = null;
		AST r_AST = null;
		
		try {      // for error handling
			AST tmp19_AST = null;
			tmp19_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp19_AST);
			match(VISUALPRECISION);
			AST tmp20_AST = null;
			tmp20_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp20_AST);
			match(ASSIGNMENT);
			r = LT(1);
			r_AST = astFactory.create(r);
			astFactory.addASTChild(currentAST, r_AST);
			match(STRING);
			
			String f = r.getText();
			realNumber = Double.parseDouble(f);
			logFile.debug("visual precision = "+f);
			
			visualprecision_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_8);
		}
		returnAST = visualprecision_AST;
		return realNumber;
	}
	
	public final double  version() throws RecognitionException, TokenStreamException {
		double realNumber=0;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST version_AST = null;
		AST r_AST = null;
		double f;
		
		try {      // for error handling
			AST tmp21_AST = null;
			tmp21_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp21_AST);
			match(VERSION);
			AST tmp22_AST = null;
			tmp22_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp22_AST);
			match(ASSIGNMENT);
			f=real();
			r_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			
			realNumber = f;
			logFile.debug("version = "+f);
			
			version_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = version_AST;
		return realNumber;
	}
	
	public final String[]  defaultnodestates() throws RecognitionException, TokenStreamException {
		String[] states=null;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST defaultnodestates_AST = null;
		Token  strlist = null;
		AST strlist_AST = null;
		
		try {      // for error handling
			match(DEFAULT);
			match(NODE);
			strlist = LT(1);
			strlist_AST = astFactory.create(strlist);
			astFactory.makeASTRoot(currentAST, strlist_AST);
			match(STATES);
			match(ASSIGNMENT);
			match(LEFTP);
			commastates();
			astFactory.addASTChild(currentAST, returnAST);
			match(RIGHTP);
			
			String[] commaStates;
			logFile.debug("default node states = ");
			
				int numStr = strlist_AST.getNumberOfChildren(), actualString = 0;
				commaStates = new String[numStr];
				AST auxAST = strlist_AST.getFirstChild();
				do {
					commaStates[actualString++] = auxAST.getText();
					auxAST = auxAST.getNextSibling();
				} while (auxAST != null);
				states = commaStates;
				defaultStates = commaStates;
				ioNet.put("DefaultNodeStates", commaStates);
			
				for (int i=0; i<commaStates.length; i++) {
					logFile.debug(commaStates[i]);
					if (i<commaStates.length-1) {
						logFile.debug(", ");
					}
				}
				logFile.debug("");
			
			defaultnodestates_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_10);
		}
		returnAST = defaultnodestates_AST;
		return states;
	}
	
	public final void nodes() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST nodes_AST = null;
		
		try {      // for error handling
			
				fsVariables = new ArrayList<Variable>();
			
			{
			int _cnt1541=0;
			_loop1541:
			do {
				if ((LA(1)==NODE)) {
					node();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt1541>=1 ) { break _loop1541; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt1541++;
			} while (true);
			}
			
				int numVariables = probNet.getChanceAndDecisionVariables().size();
				
				cases = new int[numberOfCases][numVariables];
			
			for (int i=0; i<numVariables; i++)
			fsVariables.add((Variable) probNet.getChanceAndDecisionVariables().get(i));
				
				Variable fsVariable;
				for (int i = numVariables-1; i >= 0; i--) {
					fsVariable = fsVariables.get(i);
				State[] states = fsVariable.getStates();
				    logFile.debug("Variable " + fsVariable.getName() + " states: ");
				
				for (int j = 0; j < states.length; j++) {
					logFile.debug(states[j] + " ");		    
				}
					logFile.debug("");
				
			/*	    try {
				    probNet.addVariable(fsVariables.get(i), NodeType.CHANCE);
				} catch (Exception e) {
					System.err.println("Problems creating BayesNet");
					    System.err.println(e.getMessage());
				    }		*/
				}
			
			nodes_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_11);
		}
		returnAST = nodes_AST;
	}
	
	public final void relation() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST relation_AST = null;
		Token  r = null;
		AST r_AST = null;
		
		try {      // for error handling
			r = LT(1);
			r_AST = astFactory.create(r);
			astFactory.makeASTRoot(currentAST, r_AST);
			match(RELATION);
			match(LEFTB);
			memory();
			astFactory.addASTChild(currentAST, returnAST);
			cases();
			astFactory.addASTChild(currentAST, returnAST);
			match(RIGHTB);
			
			
				logFile.debug(r.getText()+": ");
				
				// goes round the parser tree to get the variables names
			/*AST auxAST = #r.getFirstChild();
			String ident1, ident2;
			// stringList contains variables names
			ArrayList<String> stringList = new ArrayList<String>();
			while (auxAST != null) {
				ident1 = auxAST.getText();
				auxAST = auxAST.getNextSibling();
				if (ident1.startsWith("variable") && (auxAST != null)) {
					ident2 = auxAST.getText();
				    	stringList.add(ident1 + ident2);
				    	auxAST = auxAST.getNextSibling();
				} else {
				    	stringList.add(ident1);
				}
			}
			
				// Copy variables names into an array called theVariables
			String[] theVariables = new String[stringList.size()];
			int position=0;
			for (String auxS : stringList) {
			theVariables[position++] = auxS;
			}
			
			// Gets the ProbNet
			// TODO Mirar esto a ver si se puede quitar una linea.
			ProbNet probNet = (InfluenceDiagram)ioNet.get("InfluenceDiagram");
			if (probNet == null) {
				probNet = (ProbNet)ioNet.get("BayesNet");
			}
			if (probNet == null) {
				probNet = (ProbNet)ioNet.get("ProbNet");
			}
			
			// Sets potential type: CHANCE or UTILITY
			boolean utilityPotential = false;
			if (kindRelation == NodeType.UTILITY) {
				utilityPotential = true;
				kindRelation = NodeType.CHANCE;
			}	
			
				// Create the TablePotential
				// 1. Use theVariables to create an ArrayList<Variable>
			ArrayList<Variable> auxFSVariables = new ArrayList<Variable>();
			for (int i=theVariables.length -1; i >= 0 ; i--) {
			ProbNode probNode = probNet.getNode(theVariables[i]);
			Variable fsVariable = probNode.getVariable();
			if (probNode.getNodeType() == NodeType.UTILITY) {
				continue;
			}
			auxFSVariables.add(fsVariable);
			}      
			try {// 2. Create the TablePotential
				TablePotential tablePotential =
				new TablePotential(auxFSVariables, theTable);
			// 3. Reorder the potential in the Carmen mode
			tablePotential = IOOperations.elvira2CarmenPotential(tablePotential);
			tablePotential.setUtility(utilityPotential);
			// 4. Inserts potential in the first node (position 0)
			probNet.addPotential(tablePotential);
			} catch (Exception e) {
				e.printStackTrace();
				    if (messagesImportance >= logFile.importanceThreshold) {
				    	logFile.debug("Problems translating elvira potentials to carmen " 
				    	    + "potentials");
				    }
			}
			for (int i=0; i<position; i++) {
				    if (messagesImportance >= logFile.importanceThreshold) {
				        logFile.debug(theVariables[i]+" ");
				    }
			}
			if (messagesImportance >= logFile.importanceThreshold) {
				    logFile.debug("");
				logFile.debug("");
			}*/
			
			relation_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		returnAST = relation_AST;
	}
	
	public final void kindofvariable() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST kindofvariable_AST = null;
		
		try {      // for error handling
			AST tmp30_AST = null;
			tmp30_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp30_AST);
			match(KIND);
			match(HYPHEN);
			match(OF);
			match(HYPHEN);
			AST tmp34_AST = null;
			tmp34_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp34_AST);
			match(VARIABLE);
			kindofvariable_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		returnAST = kindofvariable_AST;
	}
	
	public final void typeofnode() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeofnode_AST = null;
		
		try {      // for error handling
			AST tmp35_AST = null;
			tmp35_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp35_AST);
			match(KIND);
			match(HYPHEN);
			match(OF);
			match(HYPHEN);
			AST tmp39_AST = null;
			tmp39_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp39_AST);
			match(NODE);
			typeofnode_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_13);
		}
		returnAST = typeofnode_AST;
	}
	
	public final void numstatestokens() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST numstatestokens_AST = null;
		
		try {      // for error handling
			AST tmp40_AST = null;
			tmp40_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp40_AST);
			match(NUM);
			match(HYPHEN);
			AST tmp42_AST = null;
			tmp42_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp42_AST);
			match(STATES);
			numstatestokens_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_13);
		}
		returnAST = numstatestokens_AST;
	}
	
	public final void finitestates() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST finitestates_AST = null;
		
		try {      // for error handling
			AST tmp43_AST = null;
			tmp43_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp43_AST);
			match(FINITE);
			match(HYPHEN);
			AST tmp45_AST = null;
			tmp45_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp45_AST);
			match(STATES);
			finitestates_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_14);
		}
		returnAST = finitestates_AST;
	}
	
	public final void continuousstate() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST continuousstate_AST = null;
		
		try {      // for error handling
			AST tmp46_AST = null;
			tmp46_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp46_AST);
			match(CONTINUOUS);
			continuousstate_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_15);
		}
		returnAST = continuousstate_AST;
	}
	
	public final int  integer() throws RecognitionException, TokenStreamException {
		int result=0;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST integer_AST = null;
		Token  l = null;
		AST l_AST = null;
		
		try {      // for error handling
			l = LT(1);
			l_AST = astFactory.create(l);
			astFactory.addASTChild(currentAST, l_AST);
			match(IDENT);
			
				result = Integer.parseInt(l.getText());
			
			integer_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		returnAST = integer_AST;
		return result;
	}
	
	public final double  real() throws RecognitionException, TokenStreamException {
		double result = 0;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST real_AST = null;
		Token  l = null;
		AST l_AST = null;
		
		try {      // for error handling
			l = LT(1);
			l_AST = astFactory.create(l);
			astFactory.addASTChild(currentAST, l_AST);
			match(IDENT);
			
				result = Double.parseDouble(l.getText());
			
			real_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_16);
		}
		returnAST = real_AST;
		return result;
	}
	
	public final void commastates() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST commastates_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case STRING:
			{
				AST tmp47_AST = null;
				tmp47_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp47_AST);
				match(STRING);
				{
				_loop1600:
				do {
					if ((LA(1)==STRING||LA(1)==COMMA)) {
						{
						switch ( LA(1)) {
						case COMMA:
						{
							match(COMMA);
							break;
						}
						case STRING:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						AST tmp49_AST = null;
						tmp49_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp49_AST);
						match(STRING);
					}
					else {
						break _loop1600;
					}
					
				} while (true);
				}
				commastates_AST = (AST)currentAST.root;
				break;
			}
			case IDENT:
			{
				AST tmp50_AST = null;
				tmp50_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp50_AST);
				match(IDENT);
				{
				_loop1603:
				do {
					if ((LA(1)==IDENT||LA(1)==COMMA)) {
						{
						switch ( LA(1)) {
						case COMMA:
						{
							match(COMMA);
							break;
						}
						case IDENT:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						AST tmp52_AST = null;
						tmp52_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp52_AST);
						match(IDENT);
					}
					else {
						break _loop1603;
					}
					
				} while (true);
				}
				commastates_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_15);
		}
		returnAST = commastates_AST;
	}
	
	public final void node() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST node_AST = null;
		Token  n = null;
		AST n_AST = null;
		Token  id = null;
		AST id_AST = null;
		AST b_AST = null;
		Token  n3 = null;
		AST n3_AST = null;
		Token  id3 = null;
		AST id3_AST = null;
		AST b3_AST = null;
		Token  n2 = null;
		AST n2_AST = null;
		Token  v = null;
		AST v_AST = null;
		Token  id2 = null;
		AST id2_AST = null;
		AST b2_AST = null;
		Token  n4 = null;
		AST n4_AST = null;
		Token  v4 = null;
		AST v4_AST = null;
		Token  id4 = null;
		AST id4_AST = null;
		AST b4_AST = null;
		HashMap<String, Object> infoNode = null;
		
		try {      // for error handling
			if ((LA(1)==NODE) && (LA(2)==IDENT)) {
				n = LT(1);
				n_AST = astFactory.create(n);
				match(NODE);
				id = LT(1);
				id_AST = astFactory.create(id);
				astFactory.addASTChild(currentAST, id_AST);
				match(IDENT);
				{
				switch ( LA(1)) {
				case LEFTP:
				case LEFTB:
				{
					{
					switch ( LA(1)) {
					case LEFTP:
					{
						match(LEFTP);
						{
						switch ( LA(1)) {
						case FINITE:
						case CONTINUOUS:
						{
							kindstate();
							break;
						}
						case RIGHTP:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						match(RIGHTP);
						break;
					}
					case LEFTB:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					match(LEFTB);
					infoNode=bodynode();
					b_AST = (AST)returnAST;
					astFactory.addASTChild(currentAST, returnAST);
					match(RIGHTB);
					break;
				}
				case NODE:
				case RELATION:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				
					
					String[] statesNames = (String[])infoNode.get("NodeStates");
					State[] states = new State[statesNames.length];
					int i = 0;
					for(String state : statesNames){
						states[i] = new State(state);
						i++;
					} 
					ProbNode probNode =
						addProbNode(infoNode, states, (NodeType)infoNode.get("NodeType"),
							 id.getText());
					infoNode.put("ProbNode", probNode);
					infoNode.put("Name", id.getText());
				
				logFile.debug(n.getText() + ": " + id.getText());
				
				
				//	Variable fsVariable = null;
				if (infoNode != null) {
				//    	fsVariable = new Variable(id.getText(), infoNode.getStates());
					if (infoNode.get("NodeStates") != null) {
					    	logFile.debug(". Num states: " + 
					    	((String[])infoNode.get("NodeStates")).length);
					}
				} else {
				//    	fsVariable = new Variable(id.getText(),defaultStates);
					    logFile.debug(". Num states: "+defaultStates.length);
				}
				//    fsVariables.add(fsVariable);
					HashMap <String,String> properties = new HashMap<String, String>();
					for (Entry<String, Object> property : infoNode.entrySet())
					properties.put(property.getKey(), property.getValue().toString());
						probNode.additionalProperties = properties;	
				
				
				node_AST = (AST)currentAST.root;
			}
			else if ((LA(1)==NODE) && (LA(2)==STRING)) {
				n3 = LT(1);
				n3_AST = astFactory.create(n3);
				match(NODE);
				id3 = LT(1);
				id3_AST = astFactory.create(id3);
				astFactory.addASTChild(currentAST, id3_AST);
				match(STRING);
				{
				switch ( LA(1)) {
				case LEFTP:
				case LEFTB:
				{
					{
					switch ( LA(1)) {
					case LEFTP:
					{
						match(LEFTP);
						{
						switch ( LA(1)) {
						case FINITE:
						case CONTINUOUS:
						{
							kindstate();
							break;
						}
						case RIGHTP:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						match(RIGHTP);
						break;
					}
					case LEFTB:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					match(LEFTB);
					infoNode=bodynode();
					b3_AST = (AST)returnAST;
					astFactory.addASTChild(currentAST, returnAST);
					match(RIGHTB);
					break;
				}
				case NODE:
				case RELATION:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				
				
					String[] statesNames = (String[])infoNode.get("NodeStates");
					State[] states = new State[statesNames.length];
					int i = 0;
					for(String state : statesNames){
						states[i] = new State(state);
						i++;
					} 
					ProbNode probNode =
						addProbNode(infoNode, states, (NodeType)infoNode.get("NodeType"),
							 id3.getText());
					infoNode.put("ProbNode", probNode);
					infoNode.put("Name", id3.getText());
				
				logFile.debug(n3.getText() + ": " + id3.getText());
				
				//	Variable fsVariable = null;
				if (infoNode != null) {
				//    	fsVariable = new Variable(id3.getText(),infoNode.getStates());
					    logFile.debug(". Num states: " + 
					    ((String[])infoNode.get("NodeStates")).length);
				} else {
				//    	fsVariable = new Variable(id3.getText(),defaultStates);
					    logFile.debug(". Num states: "+defaultStates.length);
				}
				//    fsVariables.add(fsVariable);
					HashMap <String,String> properties = new HashMap<String, String>();
					for (Entry<String, Object> property : infoNode.entrySet())
					properties.put(property.getKey(), property.getValue().toString());
						probNode.additionalProperties = properties;	
				
				
				node_AST = (AST)currentAST.root;
			}
			else if ((LA(1)==NODE) && (LA(2)==VARIABLE) && (LA(3)==IDENT)) {
				n2 = LT(1);
				n2_AST = astFactory.create(n2);
				match(NODE);
				v = LT(1);
				v_AST = astFactory.create(v);
				astFactory.addASTChild(currentAST, v_AST);
				match(VARIABLE);
				id2 = LT(1);
				id2_AST = astFactory.create(id2);
				astFactory.addASTChild(currentAST, id2_AST);
				match(IDENT);
				{
				switch ( LA(1)) {
				case LEFTP:
				case LEFTB:
				{
					{
					switch ( LA(1)) {
					case LEFTP:
					{
						match(LEFTP);
						{
						switch ( LA(1)) {
						case FINITE:
						case CONTINUOUS:
						{
							kindstate();
							break;
						}
						case RIGHTP:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						match(RIGHTP);
						break;
					}
					case LEFTB:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					match(LEFTB);
					infoNode=bodynode();
					b2_AST = (AST)returnAST;
					astFactory.addASTChild(currentAST, returnAST);
					match(RIGHTB);
					break;
				}
				case NODE:
				case RELATION:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				
				
					String[] statesNames = (String[])infoNode.get("NodeStates");
					State[] states = new State[statesNames.length];
					int i = 0;
					for(String state : statesNames){
						states[i] = new State(state);
						i++;
					} 
					ProbNode probNode =
						addProbNode(infoNode, states, (NodeType)infoNode.get("NodeType"),
							 id2.getText());
					infoNode.put("ProbNode", probNode);
					infoNode.put("Name", id2.getText());
				
				logFile.debug(n2.getText()+": " + v.getText() + id2.getText());
				
				//	Variable fsVariable = null;
				if (infoNode != null) {
				//    	fsVariable = new Variable(v.getText() + id2.getText(),
				//    		infoNode.getStates());
					    logFile.debug(". Num states: " + 
					    ((String[])infoNode.get("NodeStates")).length);
				} else {
				//   	fsVariable = new Variable(v.getText() + id2.getText(), defaultStates);
					    logFile.debug(". Num states: " + defaultStates.length);
				}
				//    fsVariables.add(fsVariable);
					HashMap <String,String> properties = new HashMap<String, String>();
					for (Entry<String, Object> property : infoNode.entrySet())
					properties.put(property.getKey(), property.getValue().toString());
						probNode.additionalProperties = properties;	
					
				
				node_AST = (AST)currentAST.root;
			}
			else if ((LA(1)==NODE) && (LA(2)==VARIABLE) && (LA(3)==STRING)) {
				n4 = LT(1);
				n4_AST = astFactory.create(n4);
				match(NODE);
				v4 = LT(1);
				v4_AST = astFactory.create(v4);
				astFactory.addASTChild(currentAST, v4_AST);
				match(VARIABLE);
				id4 = LT(1);
				id4_AST = astFactory.create(id4);
				astFactory.addASTChild(currentAST, id4_AST);
				match(STRING);
				{
				switch ( LA(1)) {
				case LEFTP:
				case LEFTB:
				{
					{
					switch ( LA(1)) {
					case LEFTP:
					{
						match(LEFTP);
						{
						switch ( LA(1)) {
						case FINITE:
						case CONTINUOUS:
						{
							kindstate();
							break;
						}
						case RIGHTP:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						match(RIGHTP);
						break;
					}
					case LEFTB:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					match(LEFTB);
					infoNode=bodynode();
					b4_AST = (AST)returnAST;
					astFactory.addASTChild(currentAST, returnAST);
					match(RIGHTB);
					break;
				}
				case NODE:
				case RELATION:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				
				
					String[] statesNames = (String[])infoNode.get("NodeStates");
					State[] states = new State[statesNames.length];
					int i = 0;
					for(String state : statesNames){
						states[i] = new State(state);
						i++;
					} 
					ProbNode probNode =
						addProbNode(infoNode, states, (NodeType)infoNode.get("NodeType"),
							 id4.getText());
					infoNode.put("ProbNode", probNode);
					infoNode.put("Name", id4.getText());
				
				logFile.debug(n4.getText()+": "+ v4.getText() + id4.getText());
				
				//	Variable fsVariable = null;
				if (infoNode != null) {
				//    	fsVariable = new Variable(v4.getText() + id4.getText(),
				//    		infoNode.getStates());
				logFile.debug(". Num states: " + 
				((String[])infoNode.get("NodeStates")).length);
				} else {
				//    	fsVariable = new Variable(v4.getText() + id4.getText(),defaultStates);
					    logFile.debug(". Num states: " + defaultStates.length);
				}
				//    fsVariables.add(fsVariable);
					HashMap <String,String> properties = new HashMap<String, String>();
					for (Entry<String, Object> property : infoNode.entrySet())
					properties.put(property.getKey(), property.getValue().toString());
						probNode.additionalProperties = properties;	
				
				
				node_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_17);
		}
		returnAST = node_AST;
	}
	
	public final void kindstate() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST kindstate_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case FINITE:
			{
				finitestates();
				astFactory.addASTChild(currentAST, returnAST);
				kindstate_AST = (AST)currentAST.root;
				break;
			}
			case CONTINUOUS:
			{
				continuousstate();
				astFactory.addASTChild(currentAST, returnAST);
				kindstate_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_15);
		}
		returnAST = kindstate_AST;
	}
	
	public final HashMap<String, Object>  bodynode() throws RecognitionException, TokenStreamException {
		HashMap<String, Object> infoNode = null;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST bodynode_AST = null;
		AST t_AST = null;
		AST c_AST = null;
		AST k_AST = null;
		AST v_AST = null;
		AST x_AST = null;
		AST y_AST = null;
		AST r_AST = null;
		AST p_AST = null;
		AST mi_AST = null;
		AST ma_AST = null;
		AST pr_AST = null;
		AST s_AST = null;
		
			int posX = 0 ,posY = 0;
			String titleNode = null, commentNode = null, purposeNode = null;
			String[] statesVar = null;
			NodeType nodeType = null;
			double relevanceNode = Double.MIN_VALUE, minNode = Double.POSITIVE_INFINITY, 
			    maxNode = Double.NEGATIVE_INFINITY;
			int precisionNode = Integer.MIN_VALUE;
			VariableType variableType = null;
		
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case TITLE:
			{
				titleNode=title();
				t_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case COMMENT:
			case RIGHTB:
			case KIND:
			case TYPE:
			case STATES:
			case POSX:
			case POSY:
			case RELEVANCE:
			case PURPOSE:
			case MIN:
			case MAX:
			case NUM:
			case PRECISION:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case COMMENT:
			{
				commentNode=comment();
				c_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case RIGHTB:
			case KIND:
			case TYPE:
			case STATES:
			case POSX:
			case POSY:
			case RELEVANCE:
			case PURPOSE:
			case MIN:
			case MAX:
			case NUM:
			case PRECISION:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case KIND:
			{
				nodeType=typenode();
				k_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case RIGHTB:
			case TYPE:
			case STATES:
			case POSX:
			case POSY:
			case RELEVANCE:
			case PURPOSE:
			case MIN:
			case MAX:
			case NUM:
			case PRECISION:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case TYPE:
			{
				variableType=typevariable();
				v_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case RIGHTB:
			case STATES:
			case POSX:
			case POSY:
			case RELEVANCE:
			case PURPOSE:
			case MIN:
			case MAX:
			case NUM:
			case PRECISION:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case POSX:
			{
				posX=posx();
				x_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case RIGHTB:
			case STATES:
			case POSY:
			case RELEVANCE:
			case PURPOSE:
			case MIN:
			case MAX:
			case NUM:
			case PRECISION:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case POSY:
			{
				posY=posy();
				y_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case RIGHTB:
			case STATES:
			case RELEVANCE:
			case PURPOSE:
			case MIN:
			case MAX:
			case NUM:
			case PRECISION:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case RELEVANCE:
			{
				relevanceNode=relevance();
				r_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case RIGHTB:
			case STATES:
			case PURPOSE:
			case MIN:
			case MAX:
			case NUM:
			case PRECISION:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case PURPOSE:
			{
				purposeNode=purpose();
				p_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case RIGHTB:
			case STATES:
			case MIN:
			case MAX:
			case NUM:
			case PRECISION:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case MIN:
			{
				minNode=min();
				mi_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case RIGHTB:
			case STATES:
			case MAX:
			case NUM:
			case PRECISION:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case MAX:
			{
				maxNode=max();
				ma_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case RIGHTB:
			case STATES:
			case NUM:
			case PRECISION:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case PRECISION:
			{
				precisionNode=precision();
				pr_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case RIGHTB:
			case STATES:
			case NUM:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case NUM:
			{
				numstates();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case RIGHTB:
			case STATES:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case STATES:
			{
				statesVar=states();
				s_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case RIGHTB:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			
				infoNode = new HashMap<String, Object>();
				if (titleNode != null) {
					infoNode.put("Title", titleNode);
				}
				if (commentNode != null) {
					infoNode.put("Comment", commentNode);
				}
				if (nodeType != null) {
					infoNode.put("NodeType", nodeType);
				}
				if (variableType != null) {
					infoNode.put("TypeOfVariable", variableType);
				}
				if (precisionNode != Integer.MIN_VALUE) {
					infoNode.put("Precision", precisionNode);
				}
				if (minNode != Double.POSITIVE_INFINITY) {
					infoNode.put("Min", minNode);
				}
				if (maxNode != Double.NEGATIVE_INFINITY) {
					infoNode.put("Max", maxNode);
				}
				infoNode.put("CoordinateX", posX);
				infoNode.put("CoordinateY", posY);
				if (relevanceNode != Double.MIN_VALUE) {
					infoNode.put("Relevance", relevanceNode);
				}
				if (purposeNode != null) {
					infoNode.put("Purpose", purposeNode);
				}
				if (nodeType != NodeType.UTILITY) {
					if (statesVar == null) {
						statesVar = defaultStates;
						infoNode.put("NodeStates" ,statesVar);
						infoNode.put("UseDefaultStates", new Boolean(true));
					} else {
						infoNode.put("NodeStates" ,statesVar);
						infoNode.put("UseDefaultStates", new Boolean(false));
					}
				}
			
			bodynode_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		returnAST = bodynode_AST;
		return infoNode;
	}
	
	public final NodeType  typenode() throws RecognitionException, TokenStreamException {
		NodeType typeOfNode=NodeType.CHANCE;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typenode_AST = null;
		Token  a = null;
		AST a_AST = null;
		Token  c = null;
		AST c_AST = null;
		Token  a2 = null;
		AST a2_AST = null;
		Token  d = null;
		AST d_AST = null;
		Token  a3 = null;
		AST a3_AST = null;
		Token  u = null;
		AST u_AST = null;
		
		try {      // for error handling
			if ((LA(1)==KIND) && (LA(2)==HYPHEN) && (LA(3)==OF) && (LA(4)==HYPHEN) && (LA(5)==NODE) && (LA(6)==ASSIGNMENT) && (LA(7)==CHANCE)) {
				typeofnode();
				astFactory.addASTChild(currentAST, returnAST);
				a = LT(1);
				a_AST = astFactory.create(a);
				astFactory.addASTChild(currentAST, a_AST);
				match(ASSIGNMENT);
				c = LT(1);
				c_AST = astFactory.create(c);
				astFactory.addASTChild(currentAST, c_AST);
				match(CHANCE);
				
				typeOfNode = NodeType.CHANCE;
					logFile.debug("Type of node: "+a.getText()+" "+c.getText());
				
				typenode_AST = (AST)currentAST.root;
			}
			else if ((LA(1)==KIND) && (LA(2)==HYPHEN) && (LA(3)==OF) && (LA(4)==HYPHEN) && (LA(5)==NODE) && (LA(6)==ASSIGNMENT) && (LA(7)==DECISION)) {
				typeofnode();
				astFactory.addASTChild(currentAST, returnAST);
				a2 = LT(1);
				a2_AST = astFactory.create(a2);
				astFactory.addASTChild(currentAST, a2_AST);
				match(ASSIGNMENT);
				d = LT(1);
				d_AST = astFactory.create(d);
				astFactory.addASTChild(currentAST, d_AST);
				match(DECISION);
				
					typeOfNode = NodeType.DECISION;
				logFile.debug("Type of node: "+a2.getText()+" "+d.getText());
				
				typenode_AST = (AST)currentAST.root;
			}
			else if ((LA(1)==KIND) && (LA(2)==HYPHEN) && (LA(3)==OF) && (LA(4)==HYPHEN) && (LA(5)==NODE) && (LA(6)==ASSIGNMENT) && (LA(7)==UTILITY)) {
				typeofnode();
				astFactory.addASTChild(currentAST, returnAST);
				a3 = LT(1);
				a3_AST = astFactory.create(a3);
				astFactory.addASTChild(currentAST, a3_AST);
				match(ASSIGNMENT);
				u = LT(1);
				u_AST = astFactory.create(u);
				astFactory.addASTChild(currentAST, u_AST);
				match(UTILITY);
				
					typeOfNode = NodeType.UTILITY;
				logFile.debug("Kind of node: "+a3.getText()+" "+u.getText());
				
				typenode_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		returnAST = typenode_AST;
		return typeOfNode;
	}
	
	public final VariableType  typevariable() throws RecognitionException, TokenStreamException {
		VariableType typeOfVariable=VariableType.FINITE_STATES;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typevariable_AST = null;
		Token  a = null;
		AST a_AST = null;
		Token  a2 = null;
		AST a2_AST = null;
		Token  c = null;
		AST c_AST = null;
		
		try {      // for error handling
			if ((LA(1)==TYPE) && (LA(2)==HYPHEN) && (LA(3)==OF) && (LA(4)==HYPHEN) && (LA(5)==VARIABLE) && (LA(6)==ASSIGNMENT) && (LA(7)==FINITE)) {
				typeofv();
				astFactory.addASTChild(currentAST, returnAST);
				a = LT(1);
				a_AST = astFactory.create(a);
				astFactory.addASTChild(currentAST, a_AST);
				match(ASSIGNMENT);
				finitestates();
				astFactory.addASTChild(currentAST, returnAST);
				
					typeOfVariable = VariableType.FINITE_STATES;
				logFile.debug("Type of variable: "+a.getText()+" finite states");
				
				typevariable_AST = (AST)currentAST.root;
			}
			else if ((LA(1)==TYPE) && (LA(2)==HYPHEN) && (LA(3)==OF) && (LA(4)==HYPHEN) && (LA(5)==VARIABLE) && (LA(6)==ASSIGNMENT) && (LA(7)==CONTINUOUS)) {
				typeofv();
				astFactory.addASTChild(currentAST, returnAST);
				a2 = LT(1);
				a2_AST = astFactory.create(a2);
				astFactory.addASTChild(currentAST, a2_AST);
				match(ASSIGNMENT);
				c = LT(1);
				c_AST = astFactory.create(c);
				astFactory.addASTChild(currentAST, c_AST);
				match(CONTINUOUS);
				
					typeOfVariable = VariableType.NUMERIC;
				logFile.debug("Type of variable: "+a2.getText()+" "+c.getText());
				
				typevariable_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_19);
		}
		returnAST = typevariable_AST;
		return typeOfVariable;
	}
	
	public final int  posx() throws RecognitionException, TokenStreamException {
		int pos_x=0;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST posx_AST = null;
		Token  p = null;
		AST p_AST = null;
		Token  a = null;
		AST a_AST = null;
		Token  i = null;
		AST i_AST = null;
		
		try {      // for error handling
			p = LT(1);
			p_AST = astFactory.create(p);
			astFactory.makeASTRoot(currentAST, p_AST);
			match(POSX);
			a = LT(1);
			a_AST = astFactory.create(a);
			match(ASSIGNMENT);
			i = LT(1);
			i_AST = astFactory.create(i);
			astFactory.addASTChild(currentAST, i_AST);
			match(IDENT);
			
				pos_x = Integer.parseInt(i.getText());
				logFile.debug(p.getText()+" "+a.getText()+" "+i.getText());
			
			posx_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_20);
		}
		returnAST = posx_AST;
		return pos_x;
	}
	
	public final int  posy() throws RecognitionException, TokenStreamException {
		int pos_y=0;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST posy_AST = null;
		Token  p = null;
		AST p_AST = null;
		Token  a = null;
		AST a_AST = null;
		Token  i = null;
		AST i_AST = null;
		
		try {      // for error handling
			p = LT(1);
			p_AST = astFactory.create(p);
			astFactory.makeASTRoot(currentAST, p_AST);
			match(POSY);
			a = LT(1);
			a_AST = astFactory.create(a);
			match(ASSIGNMENT);
			i = LT(1);
			i_AST = astFactory.create(i);
			astFactory.addASTChild(currentAST, i_AST);
			match(IDENT);
			
				pos_y = Integer.parseInt(i.getText());
				logFile.debug(p.getText()+" "+a.getText()+" "+i.getText());
			
			posy_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_21);
		}
		returnAST = posy_AST;
		return pos_y;
	}
	
	public final double  relevance() throws RecognitionException, TokenStreamException {
		double relevance=0;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST relevance_AST = null;
		Token  re = null;
		AST re_AST = null;
		Token  a = null;
		AST a_AST = null;
		AST r_AST = null;
		
		try {      // for error handling
			re = LT(1);
			re_AST = astFactory.create(re);
			astFactory.makeASTRoot(currentAST, re_AST);
			match(RELEVANCE);
			a = LT(1);
			a_AST = astFactory.create(a);
			match(ASSIGNMENT);
			relevance=real();
			r_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			
				logFile.debug(re.getText()+" "+a.getText()+" "+relevance);
			
			relevance_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_22);
		}
		returnAST = relevance_AST;
		return relevance;
	}
	
	public final String  purpose() throws RecognitionException, TokenStreamException {
		String cad=null;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST purpose_AST = null;
		Token  p = null;
		AST p_AST = null;
		Token  a = null;
		AST a_AST = null;
		Token  s = null;
		AST s_AST = null;
		
		try {      // for error handling
			p = LT(1);
			p_AST = astFactory.create(p);
			astFactory.addASTChild(currentAST, p_AST);
			match(PURPOSE);
			a = LT(1);
			a_AST = astFactory.create(a);
			astFactory.addASTChild(currentAST, a_AST);
			match(ASSIGNMENT);
			s = LT(1);
			s_AST = astFactory.create(s);
			astFactory.addASTChild(currentAST, s_AST);
			match(STRING);
			
			String pur = s.getText();
			cad = pur;
			logFile.debug(p.getText()+" "+a.getText()+" "+pur);
			
			purpose_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_23);
		}
		returnAST = purpose_AST;
		return cad;
	}
	
	public final double  min() throws RecognitionException, TokenStreamException {
		double min=0;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST min_AST = null;
		Token  mi = null;
		AST mi_AST = null;
		Token  a = null;
		AST a_AST = null;
		AST r_AST = null;
		
		try {      // for error handling
			mi = LT(1);
			mi_AST = astFactory.create(mi);
			astFactory.makeASTRoot(currentAST, mi_AST);
			match(MIN);
			a = LT(1);
			a_AST = astFactory.create(a);
			match(ASSIGNMENT);
			min=real();
			r_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			
				logFile.debug(mi.getText()+" "+a.getText()+" "+min);
			
			min_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_24);
		}
		returnAST = min_AST;
		return min;
	}
	
	public final double  max() throws RecognitionException, TokenStreamException {
		double max=0;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST max_AST = null;
		Token  ma = null;
		AST ma_AST = null;
		Token  a = null;
		AST a_AST = null;
		AST r_AST = null;
		
		try {      // for error handling
			ma = LT(1);
			ma_AST = astFactory.create(ma);
			astFactory.makeASTRoot(currentAST, ma_AST);
			match(MAX);
			a = LT(1);
			a_AST = astFactory.create(a);
			match(ASSIGNMENT);
			max=real();
			r_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			
				logFile.debug(ma.getText()+" "+a.getText()+" "+max);
			
			max_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_25);
		}
		returnAST = max_AST;
		return max;
	}
	
	public final int  precision() throws RecognitionException, TokenStreamException {
		int prec=0;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST precision_AST = null;
		Token  p = null;
		AST p_AST = null;
		Token  a = null;
		AST a_AST = null;
		Token  i = null;
		AST i_AST = null;
		
		try {      // for error handling
			p = LT(1);
			p_AST = astFactory.create(p);
			astFactory.makeASTRoot(currentAST, p_AST);
			match(PRECISION);
			a = LT(1);
			a_AST = astFactory.create(a);
			match(ASSIGNMENT);
			i = LT(1);
			i_AST = astFactory.create(i);
			astFactory.addASTChild(currentAST, i_AST);
			match(IDENT);
			
				prec = Integer.parseInt(i.getText());
				logFile.debug(p.getText()+" "+a.getText()+" "+prec);
			
			precision_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_26);
		}
		returnAST = precision_AST;
		return prec;
	}
	
	public final int  numstates() throws RecognitionException, TokenStreamException {
		int numStates=0;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST numstates_AST = null;
		AST nu_AST = null;
		Token  a = null;
		AST a_AST = null;
		Token  n = null;
		AST n_AST = null;
		
		try {      // for error handling
			numstatestokens();
			nu_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			a = LT(1);
			a_AST = astFactory.create(a);
			astFactory.addASTChild(currentAST, a_AST);
			match(ASSIGNMENT);
			n = LT(1);
			n_AST = astFactory.create(n);
			astFactory.addASTChild(currentAST, n_AST);
			match(IDENT);
			
				numStates=Integer.parseInt(n.getText());
				logFile.debug("Num states: "+a.getText()+" "+n.getText());
			
			numstates_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_27);
		}
		returnAST = numstates_AST;
		return numStates;
	}
	
	public final String[]  states() throws RecognitionException, TokenStreamException {
		String[] listOfStates=null;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST states_AST = null;
		Token  s = null;
		AST s_AST = null;
		Token  a = null;
		AST a_AST = null;
		Token  lp = null;
		AST lp_AST = null;
		Token  rp = null;
		AST rp_AST = null;
		
		try {      // for error handling
			s = LT(1);
			s_AST = astFactory.create(s);
			astFactory.makeASTRoot(currentAST, s_AST);
			match(STATES);
			a = LT(1);
			a_AST = astFactory.create(a);
			match(ASSIGNMENT);
			lp = LT(1);
			lp_AST = astFactory.create(lp);
			match(LEFTP);
			stringlist();
			astFactory.addASTChild(currentAST, returnAST);
			rp = LT(1);
			rp_AST = astFactory.create(rp);
			match(RIGHTP);
			
			
			int numstr = s_AST.getNumberOfChildren();
			AST auxAST = s_AST.getFirstChild();
			String[] theStates = new String[numstr];
			int position = 0;
			do {
				    theStates[position++] = auxAST.getText();
			} while ((auxAST = auxAST.getNextSibling()) != null);
			listOfStates=theStates;
			
				logFile.debug(s.getText()+" "+a.getText()+" "+lp.getText()+" ");
			
			int i=0;
			for (i=0; i<position; i++) {
				    logFile.debug(theStates[i]+" ");
			}
				logFile.debug(rp.getText());
			
			states_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		returnAST = states_AST;
		return listOfStates;
	}
	
	public final void typeofv() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeofv_AST = null;
		
		try {      // for error handling
			AST tmp69_AST = null;
			tmp69_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp69_AST);
			match(TYPE);
			match(HYPHEN);
			match(OF);
			match(HYPHEN);
			AST tmp73_AST = null;
			tmp73_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp73_AST);
			match(VARIABLE);
			typeofv_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_13);
		}
		returnAST = typeofv_AST;
	}
	
	public final void stringlist() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST stringlist_AST = null;
		
		try {      // for error handling
			{
			int _cnt1582=0;
			_loop1582:
			do {
				switch ( LA(1)) {
				case STRING:
				{
					AST tmp74_AST = null;
					tmp74_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp74_AST);
					match(STRING);
					break;
				}
				case IDENT:
				{
					AST tmp75_AST = null;
					tmp75_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp75_AST);
					match(IDENT);
					break;
				}
				default:
				{
					if ( _cnt1582>=1 ) { break _loop1582; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				}
				_cnt1582++;
			} while (true);
			}
			stringlist_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_15);
		}
		returnAST = stringlist_AST;
	}
	
	public final void memory() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST memory_AST = null;
		
		try {      // for error handling
			match(MEMORY);
			match(ASSIGNMENT);
			{
			switch ( LA(1)) {
			case TRUE:
			{
				AST tmp78_AST = null;
				tmp78_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp78_AST);
				match(TRUE);
				break;
			}
			case FALSE:
			{
				AST tmp79_AST = null;
				tmp79_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp79_AST);
				match(FALSE);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			
				example = new int[probNet.getChanceAndDecisionVariables().size()];
			
			memory_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_28);
		}
		returnAST = memory_AST;
	}
	
	public final void cases() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cases_AST = null;
		
		try {      // for error handling
			match(CASES);
			match(ASSIGNMENT);
			match(LEFTP);
			{
			_loop1589:
			do {
				if ((LA(1)==LEFTC)) {
					example();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop1589;
				}
				
			} while (true);
			}
			match(RIGHTP);
			cases_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		returnAST = cases_AST;
	}
	
	public final void example() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST example_AST = null;
		
		try {      // for error handling
			match(LEFTC);
			{
			_loop1592:
			do {
				if ((LA(1)==IDENT) && (LA(2)==COMMA)) {
					caseData();
					astFactory.addASTChild(currentAST, returnAST);
					match(COMMA);
				}
				else {
					break _loop1592;
				}
				
			} while (true);
			}
			caseData();
			astFactory.addASTChild(currentAST, returnAST);
			match(RIGHTC);
			
				index=0;
				cases[casesCont]=example;
				casesCont++;
				example = new int[probNet.getChanceAndDecisionVariables().size()];
			
			example_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_29);
		}
		returnAST = example_AST;
	}
	
	public final int  caseData() throws RecognitionException, TokenStreamException {
		int result=0;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST caseData_AST = null;
		Token  l = null;
		AST l_AST = null;
		
		try {      // for error handling
			l = LT(1);
			l_AST = astFactory.create(l);
			astFactory.addASTChild(currentAST, l_AST);
			match(IDENT);
			
				example[index] = Integer.parseInt(l.getText());
				index++;
			
			caseData_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_30);
		}
		returnAST = caseData_AST;
		return result;
	}
	
	public final void configuration() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST configuration_AST = null;
		
		try {      // for error handling
			match(LEFTC);
			match(IDENT);
			{
			_loop1611:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					match(IDENT);
				}
				else {
					break _loop1611;
				}
				
			} while (true);
			}
			match(RIGHTC);
			match(ASSIGNMENT);
			configuration_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		returnAST = configuration_AST;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"QUOTATION",
		"DATABASE",
		"WHITE",
		"REMARK",
		"COMMENT",
		"SEPARATOR",
		"IDENT",
		"STRING",
		"TRUE",
		"FALSE",
		"POINT",
		"LEFTP",
		"RIGHTP",
		"LEFTC",
		"RIGHTC",
		"LEFTB",
		"RIGHTB",
		"COMMA",
		"HYPHEN",
		"UNDERLINING",
		"ASSIGNMENT",
		"MEMORY",
		"VISUALPRECISION",
		"VERSION",
		"DEFAULT",
		"NODE",
		"KIND",
		"OF",
		"CHANCE",
		"DECISION",
		"UTILITY",
		"TYPE",
		"VARIABLE",
		"RELATION",
		"FINITE",
		"STATES",
		"POSX",
		"POSY",
		"CONTINUOUS",
		"RELEVANCE",
		"PURPOSE",
		"MIN",
		"MAX",
		"NUM",
		"PRECISION",
		"TITLE",
		"AUTHOR",
		"WHOCHANGED",
		"WHENCHANGED",
		"KINDOFGRAPH",
		"NUMBER",
		"CASES"
	};
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 8444250307952896L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 17451449562693888L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 8439337940156672L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 8439337940156416L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 6755400447688704L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 4503600634003456L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 1006632960L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 939524096L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 805306368L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 536870912L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 137438953472L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 1048576L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { 16777216L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 558002152210432L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { 65536L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = { 545908329545728L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { 137975824384L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = { 558036511883264L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	private static final long[] mk_tokenSet_19() {
		long[] data = { 558002152144896L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	private static final long[] mk_tokenSet_20() {
		long[] data = { 556902640517120L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
	private static final long[] mk_tokenSet_21() {
		long[] data = { 554703617261568L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
	private static final long[] mk_tokenSet_22() {
		long[] data = { 545907524239360L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_22 = new BitSet(mk_tokenSet_22());
	private static final long[] mk_tokenSet_23() {
		long[] data = { 528315338194944L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_23 = new BitSet(mk_tokenSet_23());
	private static final long[] mk_tokenSet_24() {
		long[] data = { 493130966106112L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_24 = new BitSet(mk_tokenSet_24());
	private static final long[] mk_tokenSet_25() {
		long[] data = { 422762221928448L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_25 = new BitSet(mk_tokenSet_25());
	private static final long[] mk_tokenSet_26() {
		long[] data = { 141287245217792L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_26 = new BitSet(mk_tokenSet_26());
	private static final long[] mk_tokenSet_27() {
		long[] data = { 549756862464L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_27 = new BitSet(mk_tokenSet_27());
	private static final long[] mk_tokenSet_28() {
		long[] data = { 36028797018963968L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_28 = new BitSet(mk_tokenSet_28());
	private static final long[] mk_tokenSet_29() {
		long[] data = { 196608L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_29 = new BitSet(mk_tokenSet_29());
	private static final long[] mk_tokenSet_30() {
		long[] data = { 2359296L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_30 = new BitSet(mk_tokenSet_30());
	
	}
