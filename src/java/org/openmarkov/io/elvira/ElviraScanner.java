/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.io.elvira;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.openmarkov.core.exception.ParserException;

/**
 * Reads a Elvira file and generates tokens.
 * @author marias
 */
public class ElviraScanner
{
    // Attributes
    /** Singleton pattern. */
    private static ElviraScanner elviraScanner = null;
    private StreamTokenizer      streamTokenizer;
    private static final int     TT_LEFTP      = 40;
    private static final int     TT_RIGHTP     = 41;
    private static final int     TT_COMMA      = 44;
    private static final int     TT_SEMICOLON  = 59;
    private static final int     TT_ASSIGNMENT = 61;
    private static final int     TT_LEFTSB      = 91;
    private static final int     TT_RIGHTSB     = 93;
    private static final int     TT_LEFTCB     = 123;
    private static final int     TT_RIGHTCB    = 125;
    private boolean              readNextToken = true;
    private String               fileName;

    // Constructor
    /**
     * @param fullFileName. Path + file name with .elv extension.
     *            <code>String</code>
     * @throws FileNotFoundException
     */
    private ElviraScanner ()
    {
        fileName = null;
    }

    // Methods
    /**
     * @param fullFileName. Path + file name with .elv extension.
     *            <code>String</code>
     * @throws FileNotFoundException
     */
    public static ElviraScanner getUniqueInstance ()
        throws FileNotFoundException
    {
        // Without worrying about whether there is a scanner, it creates a new
        // one (slight modification of singleton pattern).
        elviraScanner = new ElviraScanner ();
        return elviraScanner;
    }

    public void initializeScanner (String fileName)
        throws FileNotFoundException
    {
        this.fileName = fileName;
        streamTokenizer = new StreamTokenizer (
                                               new InputStreamReader (
                                                                      new FileInputStream (fileName),
                                                                      Charset.forName ("windows-1252")));
        streamTokenizer.resetSyntax();
        streamTokenizer.wordChars('a', 'z');
        streamTokenizer.wordChars('A', 'Z');
        streamTokenizer.wordChars('0', '9');
        streamTokenizer.wordChars('#', '&');
        streamTokenizer.wordChars('-', '.');
        streamTokenizer.wordChars('?', '@');
        streamTokenizer.wordChars(128 + 32, 255);
        streamTokenizer.whitespaceChars(0, ' ');
        streamTokenizer.commentChar('/');
        streamTokenizer.quoteChar('"');
        streamTokenizer.wordChars ('_', '_');
        streamTokenizer.wordChars (':', ':');
        streamTokenizer.wordChars ('\'', '\'');
        streamTokenizer.quoteChar ('"'); // For strings
        streamTokenizer.slashSlashComments (true); // Consider // as comments
    }

    /**
     * @return <code>ElviraToken</code>
     * @throws IOException
     */
    public ElviraToken getNextToken ()
        throws ParserException,
        IOException
    {
        ReservedWord reservedWord = readNextToken (streamTokenizer);
        if (reservedWord != null)
        { // Is a reserved word
            if ((reservedWord == ReservedWord.IDIAGRAM) || (reservedWord == ReservedWord.BNET)
                || (reservedWord == ReservedWord.IDIAGRAMSV))
            {
                String netName = readIdentifier (streamTokenizer, true);
                return new ElviraToken (TokenType.RESERVED, reservedWord, netName);
            }
            else if (reservedWord == ReservedWord.KIND_OF_GRAPH)
            {
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                String kindOfGraph = readIdentifier (streamTokenizer, true);
                return new ElviraToken (TokenType.RESERVED, reservedWord, kindOfGraph);
            }
            else if (reservedWord == ReservedWord.WHOCHANGED)
            {
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                String whoChanged = readIdentifier (streamTokenizer, true);
                return new ElviraToken (TokenType.RESERVED, reservedWord, whoChanged);
            }
            else if (reservedWord == ReservedWord.WHENCHANGED)
            {
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                String whenChanged = readIdentifier (streamTokenizer, true);
                return new ElviraToken (TokenType.RESERVED, reservedWord, whenChanged);
            }
            else if (reservedWord == ReservedWord.VISUALPRECISION)
            {
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                String visualPrecision = readIdentifier (streamTokenizer, true);
                Double precision = Double.parseDouble (visualPrecision);
                return new ElviraToken (TokenType.RESERVED, reservedWord, precision);
            }
            else if (reservedWord == ReservedWord.PRECISION)
            {
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                int precision = readInt (streamTokenizer, true);
                return new ElviraToken (TokenType.RESERVED, reservedWord, precision);
            }
            else if (reservedWord == ReservedWord.UNIT)
            {
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                String unit = readIdentifier (streamTokenizer, true);
                return new ElviraToken (TokenType.RESERVED, reservedWord, unit);
            }
            else if (reservedWord == ReservedWord.MIN)
            {
                readToken (streamTokenizer);
                if (streamTokenizer.ttype == TT_ASSIGNMENT)
                {
                    double min = readInt (streamTokenizer, true);
                    return new ElviraToken (TokenType.RESERVED, reservedWord, min);
                }
                else if (streamTokenizer.ttype == TT_LEFTP)
                {
                    return getCanonicalToken (reservedWord);
                }
                else
                {
                    throw new ParserException ("Unexpected token reading MIN " + "in line: "
                                               + streamTokenizer.lineno ());
                }
            }
            else if (reservedWord == ReservedWord.MAX)
            {
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                double max = readInt (streamTokenizer, true);
                return new ElviraToken (TokenType.RESERVED, reservedWord, max);
            }
            else if (reservedWord == ReservedWord.VERSION)
            {
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                double version = readDouble (streamTokenizer, true);
                return new ElviraToken (TokenType.RESERVED, reservedWord, version);
            }
            else if (reservedWord == ReservedWord.DEFAULT)
            {
                checkToken (streamTokenizer, ReservedWord.NODE);
                checkToken (streamTokenizer, ReservedWord.STATES);
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                readTokenType (streamTokenizer, TT_LEFTP);
                List<String> defaultStates = new ArrayList<String> ();
                do
                {
                    defaultStates.add (readIdentifier (streamTokenizer, false));
                    if (streamTokenizer.ttype == TT_COMMA)
                    {
                        streamTokenizer.nextToken ();
                    }
                }
                while (streamTokenizer.ttype != TT_RIGHTP);
                readNextToken = true;
                readTokenType (streamTokenizer, TT_SEMICOLON);
                String[] states = new String[defaultStates.size ()];
                int i = 0;
                for (String stateName : defaultStates)
                {
                    states[i++] = stateName;
                }
                return new ElviraToken (TokenType.RESERVED, reservedWord, states);
            }
            else if (reservedWord == ReservedWord.NODE)
            {
                String nodeName = readIdentifier (streamTokenizer, true);
                return new ElviraToken (TokenType.RESERVED, reservedWord, nodeName);
            }
            else if (reservedWord == ReservedWord.FINITE_STATES)
            {
                streamTokenizer.nextToken (); // Skips ')' or ';'
                if (streamTokenizer.ttype == TT_RIGHTP)
                {
                    streamTokenizer.nextToken (); // Skips '{'
                }
                return new ElviraToken (TokenType.RESERVED, reservedWord);
            }
            else if (reservedWord == ReservedWord.CONTINUOUS)
            {
                streamTokenizer.nextToken (); // Skips ')' or ';'
                if (streamTokenizer.ttype == TT_RIGHTP)
                {
                    streamTokenizer.nextToken (); // Skips '{'
                }
                return new ElviraToken (TokenType.RESERVED, reservedWord);
            }
            else if (reservedWord == ReservedWord.COMMENT)
            {
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                String comment = readIdentifier (streamTokenizer, true);
                return new ElviraToken (TokenType.RESERVED, reservedWord, comment);
            }
            else if (reservedWord == ReservedWord.KIND_OF_NODE)
            {
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                return new ElviraToken (TokenType.RESERVED, reservedWord);
            }
            else if (reservedWord == ReservedWord.CHANCE)
            {
                readTokenType (streamTokenizer, TT_SEMICOLON);
                return new ElviraToken (TokenType.RESERVED, reservedWord);
            }
            else if (reservedWord == ReservedWord.DECISION)
            {
                readTokenType (streamTokenizer, TT_SEMICOLON);
                return new ElviraToken (TokenType.RESERVED, reservedWord);
            }
            else if (reservedWord == ReservedWord.UTILITY)
            {
                readTokenType (streamTokenizer, TT_SEMICOLON);
                return new ElviraToken (TokenType.RESERVED, reservedWord);
            }
            else if (reservedWord == ReservedWord.UTILITYCOMBINATION)
            {
                readTokenType (streamTokenizer, TT_SEMICOLON);
                return new ElviraToken (TokenType.RESERVED, reservedWord);
            }
            else if (reservedWord == ReservedWord.TYPE_OF_VARIABLE)
            {
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                return new ElviraToken (TokenType.RESERVED, reservedWord);
            }
            else if (reservedWord == ReservedWord.POSX)
            {
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                int posX = readInt (streamTokenizer, true);
                return new ElviraToken (TokenType.RESERVED, reservedWord, posX);
            }
            else if (reservedWord == ReservedWord.POSY)
            {
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                int posY = readInt (streamTokenizer, true);
                return new ElviraToken (TokenType.RESERVED, reservedWord, posY);
            }
            else if (reservedWord == ReservedWord.RELEVANCE)
            {
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                double relevance = readDouble (streamTokenizer, true);
                return new ElviraToken (TokenType.RESERVED, reservedWord, relevance);
            }
            else if (reservedWord == ReservedWord.PURPOSE)
            {
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                String purpose = readIdentifier (streamTokenizer, true);
                return new ElviraToken (TokenType.RESERVED, reservedWord, purpose);
            }
            else if (reservedWord == ReservedWord.NUM_STATES)
            {
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                int numStates = readInt (streamTokenizer, true);
                return new ElviraToken (TokenType.RESERVED, reservedWord, numStates);
            }
            else if (reservedWord == ReservedWord.STATES)
            {
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                readTokenType (streamTokenizer, TT_LEFTP);
                List<String> states = new ArrayList<String> ();
                do
                {
                    String stateName = readIdentifier (streamTokenizer, false);
                    states.add (stateName);
                    if (streamTokenizer.ttype == TT_COMMA)
                    {
                        streamTokenizer.nextToken ();
                    }
                }
                while (streamTokenizer.ttype != TT_RIGHTP);
                String[] stateList = new String[states.size ()];
                for (int i = 0; i < stateList.length; i++)
                {
                    stateList[i] = states.get (i);
                }
                readNextToken = true;
                readTokenType (streamTokenizer, TT_SEMICOLON);
                return new ElviraToken (TokenType.RESERVED, reservedWord, stateList);
            }
            else if (reservedWord == ReservedWord.LINK)
            {
                String node1Name = readIdentifier (streamTokenizer, false);
                String node2Name = readIdentifier (streamTokenizer, false);
                readTokenType (streamTokenizer, TT_SEMICOLON);
                return new ElviraToken (TokenType.RESERVED, reservedWord, node1Name, node2Name);
            }
            else if (reservedWord == ReservedWord.RELATION)
            {
                ArrayList<String> variablesNames = new ArrayList<String> ();
                streamTokenizer.nextToken ();
                readNextToken = false;
                while (streamTokenizer.ttype != TT_LEFTCB)
                {
                    variablesNames.add (readIdentifier (streamTokenizer, false));
                }
                readNextToken = true; // Skips LEFTB: {
                String[] variables = new String[variablesNames.size ()];
                for (int i = 0; i < variables.length; i++)
                {
                    variables[i] = variablesNames.get (i);
                }
                return new ElviraToken (TokenType.RESERVED, reservedWord, variables);
            }
            else if (reservedWord == ReservedWord.KIND_OF_RELATION)
            {
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                String relationType = readIdentifier (streamTokenizer, true);
                return new ElviraToken (TokenType.RESERVED, reservedWord, relationType);
            }
            else if (reservedWord == ReservedWord.NAME_OF_RELATION)
            {
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                String relationType = readIdentifier (streamTokenizer, true);
                return new ElviraToken (TokenType.RESERVED, reservedWord, relationType);
            }
            else if (reservedWord == ReservedWord.DETERMINISTIC)
            {
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                String booleanString = readIdentifier (streamTokenizer, true);
                Boolean booleanValue = Boolean.parseBoolean (booleanString);
                return new ElviraToken (TokenType.RESERVED, reservedWord, booleanValue);
            }
            else if (reservedWord == ReservedWord.ACTIVE)
            {
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                String booleanString = readIdentifier (streamTokenizer, true);
                Boolean booleanValue = Boolean.parseBoolean (booleanString);
                return new ElviraToken (TokenType.RESERVED, reservedWord, booleanValue);
            }
            else if (reservedWord == ReservedWord.VALUES)
            {
                readTokenType (streamTokenizer, TT_ASSIGNMENT);
                return new ElviraToken (TokenType.RESERVED, reservedWord);
            }
            else if ((reservedWord == ReservedWord.TABLE)
                     || (reservedWord == ReservedWord.GENERALIZED_TABLE))
            {
                readTokenType (streamTokenizer, TT_LEFTP);
                ArrayList<Double> arrayTable = new ArrayList<Double> ();
                streamTokenizer.nextToken ();
                int i = 0;
                while (streamTokenizer.ttype != TT_RIGHTP)
                {
                    String tokenString = streamTokenizer.sval;
                    if ((tokenString != null) && (tokenString.toUpperCase ().startsWith ("E"))
                        && (i > 0))
                    {
                        String newDoubleValue = arrayTable.get (i - 1).toString () + tokenString;
                        arrayTable.set (i - 1, Double.parseDouble (newDoubleValue));
                    }
                    else if (streamTokenizer.ttype == TT_LEFTSB)
                    {
                        while (streamTokenizer.ttype != TT_RIGHTSB)
                        {
                            streamTokenizer.nextToken ();
                        }
                        streamTokenizer.nextToken ();
                    }
                    else
                    {
                        i++;
                        arrayTable.add (Double.parseDouble (streamTokenizer.sval));
                    }
                    streamTokenizer.nextToken ();
                    if (streamTokenizer.ttype == TT_COMMA)
                    { // Comma separated
                        streamTokenizer.nextToken ();
                    }
                }
                readTokenType (streamTokenizer, TT_SEMICOLON);
                double[] table = new double[arrayTable.size ()];
                i = 0;
                for (Double doubleValue : arrayTable)
                {
                    table[i++] = doubleValue;
                }
                return new ElviraToken (TokenType.RESERVED, reservedWord, table);
            }
            else if ((reservedWord == ReservedWord.OR) || (reservedWord == ReservedWord.CAUSAL_MAX)
                     || (reservedWord == ReservedWord.GENERALIZED_MAX)
                     || (reservedWord == ReservedWord.AND)
                     || (reservedWord == ReservedWord.CAUSAL_MIN)
                     || (reservedWord == ReservedWord.CAUSAL_MIN))
            {
                readTokenType (streamTokenizer, TT_LEFTP);
                return getCanonicalToken (reservedWord);
            }
            else
            {// FUNCTION, RIGHTB
                return new ElviraToken (TokenType.RESERVED, reservedWord);
            }
        }
        else
        { // Is an identifier or other type
            if (streamTokenizer.ttype == TT_RIGHTCB)
            { 
                return new ElviraToken (TokenType.RESERVED, ReservedWord.RIGHTCB);
            }        
            else
            {
                String identifier = readIdentifier (streamTokenizer, false);
                if (identifier != null)
                {
                    streamTokenizer.nextToken ();
                    readNextToken = false;
                    return new ElviraToken (TokenType.IDENTIFIER, identifier);
                }
                else
                {
                    readNextToken = true;
                    return getNextToken ();
                }
            }
        }
    }

    /**
     * @param reservedWord. <code>ReservedWord</code>
     * @return ElviraToken
     */
    private ElviraToken getCanonicalToken (ReservedWord reservedWord)
        throws IOException,
        ParserException
    {
        ArrayList<String> relationsNames = new ArrayList<String> ();
        String relationName;
        while (streamTokenizer.ttype != TT_RIGHTP)
        {
            relationName = readIdentifier (streamTokenizer, true);
            relationsNames.add (relationName);
        }
        readTokenType (streamTokenizer, TT_SEMICOLON);
        int numRelations = relationsNames.size ();
        String[] relationsNames2 = new String[numRelations];
        for (int i = 0; i < numRelations; i++)
        {
            relationsNames2[i] = relationsNames.get (i);
        }
        return new ElviraToken (TokenType.RESERVED, reservedWord, relationsNames2);
    }

    /**
     * Reads next token and tries to identify it as a reserved word. Considere
     * the case that the token can have underline characters.
     * @param streamTokenizer. <code>StreamTokenizer</code>
     * @return The reserved word or <code>null</code>. <code>ReservedWord</code>
     * @throws IOException launch by <code>StreamTokenizer</code> if an I/O
     *             error occurs.
     */
    private ReservedWord readNextToken (StreamTokenizer streamTokenizer)
        throws IOException
    {
        if (readNextToken)
        {
            streamTokenizer.nextToken ();
        }
        readNextToken = true;
        if (streamTokenizer.sval != null)
        {
            ReservedWord reservedWord = ReservedWordTokens.getReservedWord (streamTokenizer.sval);
            if (reservedWord == null)
            {
                String identifier = new String (streamTokenizer.sval);
                streamTokenizer.nextToken ();
                readNextToken = false;
                reservedWord = ReservedWordTokens.getReservedWord (identifier);
            }
            return reservedWord;
        }
        return null;
    }

    /**
     * Reads a token and ensures that it has a given type
     * @throws Exception if the readed token is not the expected one.
     * @param streamTokenizer. <code>StreamTokenizer/code>
     * @param ttype <code>StreamTokenizer</code> descriptor. <code>int</code>
     * @throws IOException launch by <code>StreamTokenizer</code> if an I/O
     *             error occurs.
     * @throws ParserException
     */
    private void readTokenType (StreamTokenizer streamTokenizer, int ttype)
        throws IOException,
        ParserException
    {
        readToken (streamTokenizer);
        if (streamTokenizer.ttype != ttype)
        {
            throw new ParserException ("Wrong token type. Expected: " + ttype + ". Readed: "
                                       + streamTokenizer.ttype + ". " + streamTokenizer.toString ());
        }
    }

    /**
     * Reads a token.
     * @param streamTokenizer. <code>StreamTokenizer/code>
     * @throws IOException
     */
    private void readToken (StreamTokenizer streamTokenizer)
        throws IOException
    {
        if (readNextToken)
        {
            streamTokenizer.nextToken ();
        }
        readNextToken = true;
    }

    /**
     * Reads a token and checks that it is equals to an expected token.
     * @param streamTokenizer. <code>StreamTokenizer/code>
     * @param reservedTokenExpected. <code>ReservedWord</code>
     * @throws ParserException
     */
    private void checkToken (StreamTokenizer streamTokenizer, ReservedWord reservedWordExpected)
        throws IOException,
        ParserException
    {
        streamTokenizer.nextToken ();
        ReservedWord reservedWordReaded = ReservedWordTokens.getReservedWord (streamTokenizer.sval);
        if (reservedWordReaded != reservedWordExpected)
        {
            throw new ParserException ("Unexpected token: " + streamTokenizer.sval + ". Expected: "
                                       + reservedWordExpected.toString ().toLowerCase ()
                                       + ". At line: " + streamTokenizer.lineno ());
        }
    }

    /**
     * Reads an identifier string with quotations or not.
     * @param streamTokenizer. <code>StreamTokenizer</code>
     * @param skipNextToken Put this parameter to <code>true</code> when the
     *            identifiers are separates with one symbol (i.e. commas).
     *            <code>boolean</code>
     * @return <code>String</code>
     * @throws <code>IOException</code>
     */
    private String readIdentifier (StreamTokenizer streamTokenizer, boolean skipNextToken)
        throws IOException
    {
        String identifier = null;
        if (readNextToken)
        {
            streamTokenizer.nextToken ();
        }
        if (streamTokenizer != null)
        {
            identifier = streamTokenizer.sval;
            streamTokenizer.nextToken ();
            readNextToken = skipNextToken;
        }
        return identifier;
    }

    /**
     * Reads a double with quotations or not.
     * @param streamTokenizer. <code>StreamTokenizer/code>
     * @param skipNextToken. <code>boolean</code>
     * @return Readed double. <code>double</code>
     * @throws IOException
     */
    private double readDouble (StreamTokenizer streamTokenizer, boolean skipNextToken)
        throws IOException
    {
        streamTokenizer.nextToken ();
        double value = Double.parseDouble (streamTokenizer.sval);
        if (skipNextToken)
        {
            streamTokenizer.nextToken ();
        }
        return value;
    }

    /**
     * Reads an integer with quotations or not.
     * @param streamTokenizer. <code>StreamTokenizer/code>
     * @param skipNextToken. <code>boolean</code>
     * @return Readed int. <code>int</code>
     * @throws IOException
     */
    private int readInt (StreamTokenizer streamTokenizer, boolean skipNextToken)
        throws IOException
    {
        streamTokenizer.nextToken ();
        int value = new Double (streamTokenizer.sval).intValue ();
        if (skipNextToken)
        {
            streamTokenizer.nextToken ();
        }
        return value;
    }

    public int lineno ()
    {
        return streamTokenizer.lineno ();
    }

    public String toString ()
    {
        String scanner = new String ();
        if (fileName != null)
        {
            scanner = scanner + "File: " + fileName + ".\n" + "ReadNextToken: " + readNextToken
                      + ". " + "Token: " + streamTokenizer.toString ();
        }
        else
        {
            scanner = scanner + "No file";
        }
        return scanner;
    }
}
