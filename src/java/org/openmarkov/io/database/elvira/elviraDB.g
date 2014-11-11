header {
    package org.openmarkov.learning.io;
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
}

class ElviraDBLexer extends Lexer;

options {filter=true;
testLiterals=false;    // don't automatically test for literals
k=6;}

QUOTATION : '"';

DATABASE: "data-base";

WHITE	: (' '|'\t')
		{ $setType(Token.SKIP); };

REMARK :	( '/' '/' (~('\n'|'\r'))* ('\n'|'\r'('\n')?) )
		{  $setType(Token.SKIP); newline(); };

COMMENT : "comment";

SEPARATOR: (';')
		{  $setType(Token.SKIP); newline(); };


IDENT
	options {testLiterals=true;}
		:	('a'..'z'|'A'..'Z'|'_'|'$'|'0'..'9')
		('a'..'z'|'\u00E1'|'\u00E9'|'\u00ED'|'\u00F3'|'\u00FA'|'\u00FC'|'A'..'Z'|'\u00C1'|'\u00C9'|'\u00CD'|'\u00D3'|'\u00DA'|'\u00DC'|'_'|'$'|'&'|'-'|'0'..'9'|'%'|'.')*;

STRING
	options {testLiterals=true;}
	: QUOTATION! ('a'..'z'|'\u00E1'|'\u00E9'|'\u00ED'|'\u00F3'|'\u00FA'|'\u00FC'|'A'..'Z'|'\u00C1'|'\u00C9'|'\u00CD'|'\u00D3'|'\u00DA'|'\u00DC'|'_'|'$'|'&'|'-'|'0'..'9'|'['|']'|
	    '.'|'%'|'>'|'<'|'='|'/'|'|'|'\\'|'?'|','|' '|'('|')'|':'|'\r'('\n')?)*
		QUOTATION!;
		
TRUE : "true";

FALSE : "false";

POINT : '.';

LEFTP	: '(';

RIGHTP	: ')';

LEFTC	: '[';

RIGHTC	: ']';

LEFTB	: '{';

RIGHTB	: '}';

COMMA	: ',';

HYPHEN : '-';

UNDERLINING : '_';

ASSIGNMENT : '=';

MEMORY : "memory"; 

VISUALPRECISION: "visualprecision";

VERSION	: "version";

DEFAULT	: "default";

NODE	: "node";

KIND: "kind";

OF: "of";

CHANCE : "chance";

DECISION : "decision";

UTILITY : "utility";

TYPE: "type";

VARIABLE : "variable";

RELATION : "relation";

FINITE : "finite";

STATES : "states";

POSX : "pos_x";

POSY : "pos_y";

CONTINUOUS: "continuous";

RELEVANCE: "relevance";

PURPOSE	: "purpose";

MIN : "min";

MAX : "max";

NUM : "num";

PRECISION : "precision";

TITLE : "title";

AUTHOR : "author";

WHOCHANGED : "whochanged";

WHENCHANGED : "whenchanged";

KINDOFGRAPH : "kindofgraph";

NUMBER: "number";

CASES: "cases";


class ElviraDBParser extends Parser;

options {
	buildAST = true;
	k = 7;
}

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
	private Logger logFile = Logger.getLogger (ArffParser.class);
	
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
			probNode = probNet.addVariable(fsVariable, nodeType);
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
}


kindofgraph returns [String kindOfGraph=null]: KINDOFGRAPH! ASSIGNMENT!
        (s:STRING | i:IDENT)
{
	if (s != null) {
		kindOfGraph = s.getText();
	} else if (i != null) {
		kindOfGraph = i.getText();
	}
};

database returns [HashMap<String, Object> bn=null]
{
	String titleNet = null, commentNet = null, authorNet = null,
		whochangedNet = null, whenchangedNet = null, name = null,
		kindOfGraph = null;
	double visualprecisionNet = Double.MIN_VALUE, versionNet = Double.MIN_VALUE;
	
    ioNet.put("BayesNet", bayesNet);
    ioNet.put("Name", name);
    ioNet.put("ProbNet", bayesNet);
    probNet = bayesNet;
}
 : b:DATABASE^ (s1:STRING | s2:IDENT) LEFTB!
 		(numberOfCases = n:numberofcases)
		(kindOfGraph = k:kindofgraph)? (titleNet = t:title)?
		(commentNet = c:comment)? (authorNet = a:author)?
		(whochangedNet = who:whochanged)? (whenchangedNet = when:whenchanged)?
		(visualprecisionNet = vp:visualprecision)?! (versionNet = v: version)?!
		(defaultnodestates)?! nodes //links
		relation RIGHTB! {

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
};

kindofvariable: KIND HYPHEN! OF! HYPHEN! VARIABLE;

typeofnode: KIND HYPHEN! OF! HYPHEN! NODE;

numstatestokens: NUM HYPHEN! STATES;

finitestates: FINITE HYPHEN! STATES;

continuousstate: CONTINUOUS;

numberofcases returns [int numberCases=0] :
		NUMBER! HYPHEN! OF! HYPHEN! CASES! ASSIGNMENT! numberCases = n:integer;
		
visualprecision returns [double realNumber=0] :
		VISUALPRECISION ASSIGNMENT r:STRING {
    String f = r.getText();
    realNumber = Double.parseDouble(f);
    logFile.debug("visual precision = "+f);
};

version returns [double realNumber=0] {double f;} :
		VERSION ASSIGNMENT f=r:real {
    realNumber = f;
    logFile.debug("version = "+f);
};

defaultnodestates returns [String[] states=null] :
        DEFAULT! NODE! strlist:STATES^ ASSIGNMENT! LEFTP! commastates RIGHTP!
{
    String[] commaStates;
    logFile.debug("default node states = ");

	int numStr = #strlist.getNumberOfChildren(), actualString = 0;
	commaStates = new String[numStr];
	AST auxAST = #strlist.getFirstChild();
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
};

nodes : {
	fsVariables = new ArrayList<Variable>();
} (node)+ {
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
};

node { HashMap<String, Object> infoNode = null;}:
         n:NODE! id:IDENT ((LEFTP! (kindstate!)? RIGHTP!)?
         LEFTB! infoNode = b:bodynode RIGHTB!)? {
	
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

} | n3:NODE! id3:STRING ((LEFTP! (kindstate!)? RIGHTP!)?
         LEFTB! infoNode = b3:bodynode RIGHTB!)? {

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

} | n2:NODE! v:VARIABLE id2:IDENT ((LEFTP! (kindstate!)? RIGHTP!)?
         LEFTB! infoNode = b2:bodynode RIGHTB!)? {

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
	
} | n4:NODE! v4:VARIABLE id4:STRING ((LEFTP! (kindstate!)? RIGHTP!)?
         LEFTB! infoNode = b4:bodynode RIGHTB!)? {

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

};

bodynode returns [HashMap<String, Object> infoNode = null]
{
	int posX = 0 ,posY = 0;
	String titleNode = null, commentNode = null, purposeNode = null;
	String[] statesVar = null;
	NodeType nodeType = null;
	double relevanceNode = Double.MIN_VALUE, minNode = Double.POSITIVE_INFINITY, 
	    maxNode = Double.NEGATIVE_INFINITY;
	int precisionNode = Integer.MIN_VALUE;
	VariableType variableType = null;
} :
			(titleNode = t:title)?
			(commentNode = c:comment)?
			(nodeType = k:typenode)?
			(variableType = v:typevariable)?
			(posX = x:posx)?
			(posY = y:posy)?
			(relevanceNode = r:relevance)?
			(purposeNode = p:purpose)?
			(minNode = mi:min)?
			(maxNode = ma:max)?
			(precisionNode = pr:precision)?
			(numstates)?
			(statesVar = s:states)? {
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
};

typenode returns [NodeType typeOfNode=NodeType.CHANCE]:
        typeofnode a:ASSIGNMENT c:CHANCE {
    typeOfNode = NodeType.CHANCE;
	logFile.debug("Type of node: "+a.getText()+" "+c.getText());
} |
	    typeofnode a2:ASSIGNMENT d:DECISION {
	typeOfNode = NodeType.DECISION;
    logFile.debug("Type of node: "+a2.getText()+" "+d.getText());
} |
		typeofnode a3:ASSIGNMENT u:UTILITY {
	typeOfNode = NodeType.UTILITY;
    logFile.debug("Kind of node: "+a3.getText()+" "+u.getText());
};

typeofv: TYPE HYPHEN! OF! HYPHEN! VARIABLE;

typevariable returns [VariableType typeOfVariable=VariableType.FINITE_STATES]:
		typeofv a:ASSIGNMENT finitestates {
	typeOfVariable = VariableType.FINITE_STATES;
    logFile.debug("Type of variable: "+a.getText()+" finite states");
}|
		typeofv a2:ASSIGNMENT c:CONTINUOUS {
	typeOfVariable = VariableType.NUMERIC;
   logFile.debug("Type of variable: "+a2.getText()+" "+c.getText());
};

posx returns [int pos_x=0] : p:POSX^ a:ASSIGNMENT! i:IDENT {
	pos_x = Integer.parseInt(i.getText());
	logFile.debug(p.getText()+" "+a.getText()+" "+i.getText());
};

posy returns [int pos_y=0] : p:POSY^ a:ASSIGNMENT! i:IDENT {
	pos_y = Integer.parseInt(i.getText());
	logFile.debug(p.getText()+" "+a.getText()+" "+i.getText());
};

relevance returns [double relevance=0]: re:RELEVANCE^ a:ASSIGNMENT!
        relevance=r:real {
	logFile.debug(re.getText()+" "+a.getText()+" "+relevance);
};

precision returns [int prec=0]: p:PRECISION^ a:ASSIGNMENT! i:IDENT {
	prec = Integer.parseInt(i.getText());
	logFile.debug(p.getText()+" "+a.getText()+" "+prec);
};
	
purpose returns [String cad=null] :
        p:PURPOSE a:ASSIGNMENT s:STRING {
    String pur = s.getText();
    cad = pur;
    logFile.debug(p.getText()+" "+a.getText()+" "+pur);
};

min returns [double min=0]: mi:MIN^ a:ASSIGNMENT!
        min=r:real {
	logFile.debug(mi.getText()+" "+a.getText()+" "+min);
};

max returns [double max=0]: ma:MAX^ a:ASSIGNMENT!
        max=r:real {
	logFile.debug(ma.getText()+" "+a.getText()+" "+max);
};

numstates returns [int numStates=0]: nu:numstatestokens a:ASSIGNMENT n:IDENT {
	numStates=Integer.parseInt(n.getText());
	logFile.debug("Num states: "+a.getText()+" "+n.getText());
};

stringlist : (STRING | IDENT)+;

states returns [String[] listOfStates=null]
               : s:STATES^ a:ASSIGNMENT! lp:LEFTP!
                 stringlist rp:RIGHTP! {

    int numstr = #s.getNumberOfChildren();
    AST auxAST = #s.getFirstChild();
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
};

relation : r:RELATION^ 
		LEFTB! memory cases RIGHTB! {

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
};

memory : MEMORY! ASSIGNMENT! (TRUE | FALSE)! {
	example = new int[probNet.getChanceAndDecisionVariables().size()];
};

cases : CASES! ASSIGNMENT! LEFTP! (example)* RIGHTP!; 

example : LEFTC! (caseData COMMA!)* caseData RIGHTC! {
	index=0;
	cases[casesCont]=example;
	casesCont++;
	example = new int[probNet.getChanceAndDecisionVariables().size()];
};

integer returns [int result=0] : l:IDENT {
   	result = Integer.parseInt(l.getText());
};

caseData returns [int result=0] : l:IDENT {
   	example[index] = Integer.parseInt(l.getText());
   	index++;
};

comment returns [String com=null] :
	    c:COMMENT a:ASSIGNMENT s:STRING {
	String co = s.getText();
	com = co;
	logFile.debug(c.getText()+" "+a.getText()+" "+co);
};

kindstate : finitestates | continuousstate;

commastates : STRING ((COMMA!)? STRING)* |
        IDENT ((COMMA!)? IDENT)*;

real returns [double result = 0] : l:IDENT {
   	result = Double.parseDouble(l.getText());
};

title returns [String title = null] :
	TITLE! ASSIGNMENT! s:STRING {
	String returnString = s.getText();
	logFile.debug("Titulo: "+returnString);
	title = returnString;
};

author returns [String aut=null] : AUTHOR! ASSIGNMENT! s:STRING {
	String auth = s.getText();
	logFile.debug("Autor: "+auth);
	aut = auth;
};

whochanged returns [String aut=null] : WHOCHANGED! ASSIGNMENT! s:STRING {
    String auth = s.getText();
    logFile.debug("Whochanged: "+auth);
    aut = auth;
};

whenchanged returns [String aut=null] : WHENCHANGED! ASSIGNMENT! s:STRING {
    String auth = s.getText();
    logFile.debug("Whenchanged: "+auth);
    aut = auth;
};

configuration : LEFTC! IDENT! (COMMA! IDENT!)* RIGHTC! ASSIGNMENT!;

