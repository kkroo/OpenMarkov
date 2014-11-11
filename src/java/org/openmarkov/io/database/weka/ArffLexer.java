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

import java.io.InputStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.ANTLRException;
import java.io.Reader;
import java.util.Hashtable;
import antlr.CharScanner;
import antlr.InputBuffer;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.Token;
import antlr.CommonToken;
import antlr.RecognitionException;
import antlr.NoViableAltForCharException;
import antlr.MismatchedCharException;
import antlr.TokenStream;
import antlr.ANTLRHashString;
import antlr.LexerSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.SemanticException;

public class ArffLexer extends antlr.CharScanner implements ArffLexerTokenTypes, TokenStream
 {
public ArffLexer(InputStream in) {
	this(new ByteBuffer(in));
}
public ArffLexer(Reader in) {
	this(new CharBuffer(in));
}
public ArffLexer(InputBuffer ib) {
	this(new LexerSharedInputState(ib));
}
public ArffLexer(LexerSharedInputState state) {
	super(state);
	caseSensitiveLiterals = true;
	setCaseSensitive(true);
	literals = new Hashtable();
}

public Token nextToken() throws TokenStreamException {
	Token theRetToken=null;
tryAgain:
	for (;;) {
		Token _token = null;
		int _ttype = Token.INVALID_TYPE;
		setCommitToPath(false);
		resetText();
		try {   // for char stream error handling
			try {   // for lexical error handling
				switch ( LA(1)) {
				case ',':
				{
					mCOMMA(true);
					theRetToken=_returnToken;
					break;
				}
				case '{':
				{
					mLEFTB(true);
					theRetToken=_returnToken;
					break;
				}
				case '}':
				{
					mRIGHTB(true);
					theRetToken=_returnToken;
					break;
				}
				case '\t':  case ' ':
				{
					mWHITE(true);
					theRetToken=_returnToken;
					break;
				}
				case '%':
				{
					mREMARK(true);
					theRetToken=_returnToken;
					break;
				}
				case '\n':  case '\r':
				{
					mSEPARATOR(true);
					theRetToken=_returnToken;
					break;
				}
				case '$':  case '0':  case '1':  case '2':
				case '3':  case '4':  case '5':  case '6':
				case '7':  case '8':  case '9':  case 'A':
				case 'B':  case 'C':  case 'D':  case 'E':
				case 'F':  case 'G':  case 'H':  case 'I':
				case 'J':  case 'K':  case 'L':  case 'M':
				case 'N':  case 'O':  case 'P':  case 'Q':
				case 'R':  case 'S':  case 'T':  case 'U':
				case 'V':  case 'W':  case 'X':  case 'Y':
				case 'Z':  case '_':  case 'a':  case 'b':
				case 'c':  case 'd':  case 'e':  case 'f':
				case 'g':  case 'h':  case 'i':  case 'j':
				case 'k':  case 'l':  case 'm':  case 'n':
				case 'o':  case 'p':  case 'q':  case 'r':
				case 's':  case 't':  case 'u':  case 'v':
				case 'w':  case 'x':  case 'y':  case 'z':
				{
					mIDENT(true);
					theRetToken=_returnToken;
					break;
				}
				default:
					if ((LA(1)=='"'||LA(1)=='\'') && (_tokenSet_0.member(LA(2)))) {
						mSTRING(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='@') && (LA(2)=='R'||LA(2)=='r')) {
						mRELATION(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='@') && (LA(2)=='A'||LA(2)=='a')) {
						mATTRIBUTE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='@') && (LA(2)=='D'||LA(2)=='d')) {
						mDATA(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='"'||LA(1)=='\'') && (true)) {
						mQUOTATION(true);
						theRetToken=_returnToken;
					}
				else {
					if (LA(1)==EOF_CHAR) {uponEOF(); _returnToken = makeToken(Token.EOF_TYPE);}
				else {consume(); continue tryAgain;}
				}
				}
				if ( _returnToken==null ) continue tryAgain; // found SKIP token
				_ttype = _returnToken.getType();
				_returnToken.setType(_ttype);
				return _returnToken;
			}
			catch (RecognitionException e) {
				if ( !getCommitToPath() ) {consume(); continue tryAgain;}
				throw new TokenStreamRecognitionException(e);
			}
		}
		catch (CharStreamException cse) {
			if ( cse instanceof CharStreamIOException ) {
				throw new TokenStreamIOException(((CharStreamIOException)cse).io);
			}
			else {
				throw new TokenStreamException(cse.getMessage());
			}
		}
	}
}

	public final void mQUOTATION(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = QUOTATION;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case '"':
		{
			match('"');
			break;
		}
		case '\'':
		{
			match('\'');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCOMMA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COMMA;
		int _saveIndex;
		
		match(',');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLEFTB(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LEFTB;
		int _saveIndex;
		
		match('{');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mRIGHTB(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = RIGHTB;
		int _saveIndex;
		
		match('}');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mWHITE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = WHITE;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case ' ':
		{
			match(' ');
			break;
		}
		case '\t':
		{
			match('\t');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		_ttype = Token.SKIP;
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mREMARK(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = REMARK;
		int _saveIndex;
		
		{
		match('%');
		{
		_loop594:
		do {
			if ((_tokenSet_1.member(LA(1)))) {
				{
				match(_tokenSet_1);
				}
			}
			else {
				break _loop594;
			}
			
		} while (true);
		}
		{
		switch ( LA(1)) {
		case '\n':
		{
			match('\n');
			break;
		}
		case '\r':
		{
			match('\r');
			{
			if ((LA(1)=='\n')) {
				match('\n');
			}
			else {
			}
			
			}
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		}
		_ttype = Token.SKIP; newline();
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSEPARATOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SEPARATOR;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case '\n':
		{
			match('\n');
			break;
		}
		case '\r':
		{
			match('\r');
			{
			if ((LA(1)=='\n')) {
				match('\n');
			}
			else {
			}
			
			}
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		_ttype = Token.SKIP; newline();
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mIDENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = IDENT;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case 'a':  case 'b':  case 'c':  case 'd':
		case 'e':  case 'f':  case 'g':  case 'h':
		case 'i':  case 'j':  case 'k':  case 'l':
		case 'm':  case 'n':  case 'o':  case 'p':
		case 'q':  case 'r':  case 's':  case 't':
		case 'u':  case 'v':  case 'w':  case 'x':
		case 'y':  case 'z':
		{
			matchRange('a','z');
			break;
		}
		case 'A':  case 'B':  case 'C':  case 'D':
		case 'E':  case 'F':  case 'G':  case 'H':
		case 'I':  case 'J':  case 'K':  case 'L':
		case 'M':  case 'N':  case 'O':  case 'P':
		case 'Q':  case 'R':  case 'S':  case 'T':
		case 'U':  case 'V':  case 'W':  case 'X':
		case 'Y':  case 'Z':
		{
			matchRange('A','Z');
			break;
		}
		case '_':
		{
			match('_');
			break;
		}
		case '$':
		{
			match('$');
			break;
		}
		case '0':  case '1':  case '2':  case '3':
		case '4':  case '5':  case '6':  case '7':
		case '8':  case '9':
		{
			matchRange('0','9');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		{
		_loop603:
		do {
			switch ( LA(1)) {
			case 'a':  case 'b':  case 'c':  case 'd':
			case 'e':  case 'f':  case 'g':  case 'h':
			case 'i':  case 'j':  case 'k':  case 'l':
			case 'm':  case 'n':  case 'o':  case 'p':
			case 'q':  case 'r':  case 's':  case 't':
			case 'u':  case 'v':  case 'w':  case 'x':
			case 'y':  case 'z':
			{
				matchRange('a','z');
				break;
			}
			case '\u00e1':
			{
				match('\u00E1');
				break;
			}
			case '\u00e9':
			{
				match('\u00E9');
				break;
			}
			case '\u00ed':
			{
				match('\u00ED');
				break;
			}
			case '\u00f3':
			{
				match('\u00F3');
				break;
			}
			case '\u00fa':
			{
				match('\u00FA');
				break;
			}
			case '\u00fc':
			{
				match('\u00FC');
				break;
			}
			case 'A':  case 'B':  case 'C':  case 'D':
			case 'E':  case 'F':  case 'G':  case 'H':
			case 'I':  case 'J':  case 'K':  case 'L':
			case 'M':  case 'N':  case 'O':  case 'P':
			case 'Q':  case 'R':  case 'S':  case 'T':
			case 'U':  case 'V':  case 'W':  case 'X':
			case 'Y':  case 'Z':
			{
				matchRange('A','Z');
				break;
			}
			case '\u00c1':
			{
				match('\u00C1');
				break;
			}
			case '\u00c9':
			{
				match('\u00C9');
				break;
			}
			case '\u00cd':
			{
				match('\u00CD');
				break;
			}
			case '\u00d3':
			{
				match('\u00D3');
				break;
			}
			case '\u00da':
			{
				match('\u00DA');
				break;
			}
			case '\u00dc':
			{
				match('\u00DC');
				break;
			}
			case '_':
			{
				match('_');
				break;
			}
			case '$':
			{
				match('$');
				break;
			}
			case '&':
			{
				match('&');
				break;
			}
			case '-':
			{
				match('-');
				break;
			}
			case '0':  case '1':  case '2':  case '3':
			case '4':  case '5':  case '6':  case '7':
			case '8':  case '9':
			{
				matchRange('0','9');
				break;
			}
			case '%':
			{
				match('%');
				break;
			}
			case '.':
			{
				match('.');
				break;
			}
			default:
			{
				break _loop603;
			}
			}
		} while (true);
		}
		_ttype = testLiteralsTable(_ttype);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSTRING(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = STRING;
		int _saveIndex;
		
		_saveIndex=text.length();
		mQUOTATION(false);
		text.setLength(_saveIndex);
		{
		_loop607:
		do {
			switch ( LA(1)) {
			case 'a':  case 'b':  case 'c':  case 'd':
			case 'e':  case 'f':  case 'g':  case 'h':
			case 'i':  case 'j':  case 'k':  case 'l':
			case 'm':  case 'n':  case 'o':  case 'p':
			case 'q':  case 'r':  case 's':  case 't':
			case 'u':  case 'v':  case 'w':  case 'x':
			case 'y':  case 'z':
			{
				matchRange('a','z');
				break;
			}
			case '\u00e1':
			{
				match('\u00E1');
				break;
			}
			case '\u00e9':
			{
				match('\u00E9');
				break;
			}
			case '\u00ed':
			{
				match('\u00ED');
				break;
			}
			case '\u00f3':
			{
				match('\u00F3');
				break;
			}
			case '\u00fa':
			{
				match('\u00FA');
				break;
			}
			case '\u00fc':
			{
				match('\u00FC');
				break;
			}
			case 'A':  case 'B':  case 'C':  case 'D':
			case 'E':  case 'F':  case 'G':  case 'H':
			case 'I':  case 'J':  case 'K':  case 'L':
			case 'M':  case 'N':  case 'O':  case 'P':
			case 'Q':  case 'R':  case 'S':  case 'T':
			case 'U':  case 'V':  case 'W':  case 'X':
			case 'Y':  case 'Z':
			{
				matchRange('A','Z');
				break;
			}
			case '\u00c1':
			{
				match('\u00C1');
				break;
			}
			case '\u00c9':
			{
				match('\u00C9');
				break;
			}
			case '\u00cd':
			{
				match('\u00CD');
				break;
			}
			case '\u00d3':
			{
				match('\u00D3');
				break;
			}
			case '\u00da':
			{
				match('\u00DA');
				break;
			}
			case '\u00dc':
			{
				match('\u00DC');
				break;
			}
			case '_':
			{
				match('_');
				break;
			}
			case '$':
			{
				match('$');
				break;
			}
			case '&':
			{
				match('&');
				break;
			}
			case '-':
			{
				match('-');
				break;
			}
			case '0':  case '1':  case '2':  case '3':
			case '4':  case '5':  case '6':  case '7':
			case '8':  case '9':
			{
				matchRange('0','9');
				break;
			}
			case '.':
			{
				match('.');
				break;
			}
			case '%':
			{
				match('%');
				break;
			}
			case '>':
			{
				match('>');
				break;
			}
			case '<':
			{
				match('<');
				break;
			}
			case '=':
			{
				match('=');
				break;
			}
			case '/':
			{
				match('/');
				break;
			}
			case '|':
			{
				match('|');
				break;
			}
			case '\\':
			{
				match('\\');
				break;
			}
			case '?':
			{
				match('?');
				break;
			}
			case ',':
			{
				match(',');
				break;
			}
			case ' ':
			{
				match(' ');
				break;
			}
			case '(':
			{
				match('(');
				break;
			}
			case ')':
			{
				match(')');
				break;
			}
			case ':':
			{
				match(':');
				break;
			}
			case '\r':
			{
				match('\r');
				{
				switch ( LA(1)) {
				case '\n':
				{
					match('\n');
					break;
				}
				case '\r':  case ' ':  case '"':  case '$':
				case '%':  case '&':  case '\'':  case '(':
				case ')':  case ',':  case '-':  case '.':
				case '/':  case '0':  case '1':  case '2':
				case '3':  case '4':  case '5':  case '6':
				case '7':  case '8':  case '9':  case ':':
				case '<':  case '=':  case '>':  case '?':
				case 'A':  case 'B':  case 'C':  case 'D':
				case 'E':  case 'F':  case 'G':  case 'H':
				case 'I':  case 'J':  case 'K':  case 'L':
				case 'M':  case 'N':  case 'O':  case 'P':
				case 'Q':  case 'R':  case 'S':  case 'T':
				case 'U':  case 'V':  case 'W':  case 'X':
				case 'Y':  case 'Z':  case '\\':  case '_':
				case 'a':  case 'b':  case 'c':  case 'd':
				case 'e':  case 'f':  case 'g':  case 'h':
				case 'i':  case 'j':  case 'k':  case 'l':
				case 'm':  case 'n':  case 'o':  case 'p':
				case 'q':  case 'r':  case 's':  case 't':
				case 'u':  case 'v':  case 'w':  case 'x':
				case 'y':  case 'z':  case '|':  case '\u00c1':
				case '\u00c9':  case '\u00cd':  case '\u00d3':  case '\u00da':
				case '\u00dc':  case '\u00e1':  case '\u00e9':  case '\u00ed':
				case '\u00f3':  case '\u00fa':  case '\u00fc':
				{
					break;
				}
				default:
				{
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				}
				}
				break;
			}
			default:
			{
				break _loop607;
			}
			}
		} while (true);
		}
		_saveIndex=text.length();
		mQUOTATION(false);
		text.setLength(_saveIndex);
		_ttype = testLiteralsTable(_ttype);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mRELATION(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = RELATION;
		int _saveIndex;
		
		{
		if ((LA(1)=='@') && (LA(2)=='r')) {
			match("@relation");
		}
		else if ((LA(1)=='@') && (LA(2)=='R')) {
			match("@RELATION");
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mATTRIBUTE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ATTRIBUTE;
		int _saveIndex;
		
		{
		if ((LA(1)=='@') && (LA(2)=='a')) {
			match("@attribute");
		}
		else if ((LA(1)=='@') && (LA(2)=='A')) {
			match("@ATTRIBUTE");
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDATA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DATA;
		int _saveIndex;
		
		{
		if ((LA(1)=='@') && (LA(2)=='d')) {
			match("@data");
		}
		else if ((LA(1)=='@') && (LA(2)=='D')) {
			match("@DATA");
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	
	private static final long[] mk_tokenSet_0() {
		long[] data = new long[8];
		data[0]=-576473993687588864L;
		data[1]=1729382250870472702L;
		data[3]=1443441072893600258L;
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = new long[8];
		data[0]=-9217L;
		data[1]=-1L;
		data[3]=1443441072893600258L;
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	
	}
