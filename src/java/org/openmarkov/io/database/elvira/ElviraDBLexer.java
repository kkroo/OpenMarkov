// $ANTLR : "elviraDB.g" -> "ElviraDBLexer.java"$

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

public class ElviraDBLexer extends antlr.CharScanner implements ElviraDBLexerTokenTypes, TokenStream
 {
public ElviraDBLexer(InputStream in) {
	this(new ByteBuffer(in));
}
public ElviraDBLexer(Reader in) {
	this(new CharBuffer(in));
}
public ElviraDBLexer(InputBuffer ib) {
	this(new LexerSharedInputState(ib));
}
public ElviraDBLexer(LexerSharedInputState state) {
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
				case '\t':  case ' ':
				{
					mWHITE(true);
					theRetToken=_returnToken;
					break;
				}
				case '/':
				{
					mREMARK(true);
					theRetToken=_returnToken;
					break;
				}
				case ';':
				{
					mSEPARATOR(true);
					theRetToken=_returnToken;
					break;
				}
				case '.':
				{
					mPOINT(true);
					theRetToken=_returnToken;
					break;
				}
				case '(':
				{
					mLEFTP(true);
					theRetToken=_returnToken;
					break;
				}
				case ')':
				{
					mRIGHTP(true);
					theRetToken=_returnToken;
					break;
				}
				case '[':
				{
					mLEFTC(true);
					theRetToken=_returnToken;
					break;
				}
				case ']':
				{
					mRIGHTC(true);
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
				case ',':
				{
					mCOMMA(true);
					theRetToken=_returnToken;
					break;
				}
				case '-':
				{
					mHYPHEN(true);
					theRetToken=_returnToken;
					break;
				}
				case '=':
				{
					mASSIGNMENT(true);
					theRetToken=_returnToken;
					break;
				}
				default:
					if ((LA(1)=='d') && (LA(2)=='a') && (LA(3)=='t') && (LA(4)=='a') && (LA(5)=='-') && (LA(6)=='b')) {
						mDATABASE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='c') && (LA(2)=='o') && (LA(3)=='m') && (LA(4)=='m') && (LA(5)=='e') && (LA(6)=='n')) {
						mCOMMENT(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='m') && (LA(2)=='e') && (LA(3)=='m') && (LA(4)=='o') && (LA(5)=='r') && (LA(6)=='y')) {
						mMEMORY(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='v') && (LA(2)=='i') && (LA(3)=='s') && (LA(4)=='u') && (LA(5)=='a') && (LA(6)=='l')) {
						mVISUALPRECISION(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='v') && (LA(2)=='e') && (LA(3)=='r') && (LA(4)=='s') && (LA(5)=='i') && (LA(6)=='o')) {
						mVERSION(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='d') && (LA(2)=='e') && (LA(3)=='f') && (LA(4)=='a') && (LA(5)=='u') && (LA(6)=='l')) {
						mDEFAULT(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='c') && (LA(2)=='h') && (LA(3)=='a') && (LA(4)=='n') && (LA(5)=='c') && (LA(6)=='e')) {
						mCHANCE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='d') && (LA(2)=='e') && (LA(3)=='c') && (LA(4)=='i') && (LA(5)=='s') && (LA(6)=='i')) {
						mDECISION(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='u') && (LA(2)=='t') && (LA(3)=='i') && (LA(4)=='l') && (LA(5)=='i') && (LA(6)=='t')) {
						mUTILITY(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='v') && (LA(2)=='a') && (LA(3)=='r') && (LA(4)=='i') && (LA(5)=='a') && (LA(6)=='b')) {
						mVARIABLE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='r') && (LA(2)=='e') && (LA(3)=='l') && (LA(4)=='a') && (LA(5)=='t') && (LA(6)=='i')) {
						mRELATION(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='f') && (LA(2)=='i') && (LA(3)=='n') && (LA(4)=='i') && (LA(5)=='t') && (LA(6)=='e')) {
						mFINITE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='s') && (LA(2)=='t') && (LA(3)=='a') && (LA(4)=='t') && (LA(5)=='e') && (LA(6)=='s')) {
						mSTATES(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='c') && (LA(2)=='o') && (LA(3)=='n') && (LA(4)=='t') && (LA(5)=='i') && (LA(6)=='n')) {
						mCONTINUOUS(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='r') && (LA(2)=='e') && (LA(3)=='l') && (LA(4)=='e') && (LA(5)=='v') && (LA(6)=='a')) {
						mRELEVANCE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='p') && (LA(2)=='u') && (LA(3)=='r') && (LA(4)=='p') && (LA(5)=='o') && (LA(6)=='s')) {
						mPURPOSE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='p') && (LA(2)=='r') && (LA(3)=='e') && (LA(4)=='c') && (LA(5)=='i') && (LA(6)=='s')) {
						mPRECISION(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='a') && (LA(2)=='u') && (LA(3)=='t') && (LA(4)=='h') && (LA(5)=='o') && (LA(6)=='r')) {
						mAUTHOR(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='w') && (LA(2)=='h') && (LA(3)=='o') && (LA(4)=='c') && (LA(5)=='h') && (LA(6)=='a')) {
						mWHOCHANGED(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='w') && (LA(2)=='h') && (LA(3)=='e') && (LA(4)=='n') && (LA(5)=='c') && (LA(6)=='h')) {
						mWHENCHANGED(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='k') && (LA(2)=='i') && (LA(3)=='n') && (LA(4)=='d') && (LA(5)=='o') && (LA(6)=='f')) {
						mKINDOFGRAPH(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='n') && (LA(2)=='u') && (LA(3)=='m') && (LA(4)=='b') && (LA(5)=='e') && (LA(6)=='r')) {
						mNUMBER(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='f') && (LA(2)=='a') && (LA(3)=='l') && (LA(4)=='s') && (LA(5)=='e') && (true)) {
						mFALSE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='p') && (LA(2)=='o') && (LA(3)=='s') && (LA(4)=='_') && (LA(5)=='x') && (true)) {
						mPOSX(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='p') && (LA(2)=='o') && (LA(3)=='s') && (LA(4)=='_') && (LA(5)=='y') && (true)) {
						mPOSY(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='t') && (LA(2)=='i') && (LA(3)=='t') && (LA(4)=='l') && (LA(5)=='e') && (true)) {
						mTITLE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='c') && (LA(2)=='a') && (LA(3)=='s') && (LA(4)=='e') && (LA(5)=='s') && (true)) {
						mCASES(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='t') && (LA(2)=='r') && (LA(3)=='u') && (LA(4)=='e') && (true) && (true)) {
						mTRUE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='n') && (LA(2)=='o') && (LA(3)=='d') && (LA(4)=='e') && (true) && (true)) {
						mNODE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='k') && (LA(2)=='i') && (LA(3)=='n') && (LA(4)=='d') && (true) && (true)) {
						mKIND(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='t') && (LA(2)=='y') && (LA(3)=='p') && (LA(4)=='e') && (true) && (true)) {
						mTYPE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='m') && (LA(2)=='i') && (LA(3)=='n') && (true) && (true) && (true)) {
						mMIN(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='m') && (LA(2)=='a') && (LA(3)=='x') && (true) && (true) && (true)) {
						mMAX(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='n') && (LA(2)=='u') && (LA(3)=='m') && (true) && (true) && (true)) {
						mNUM(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='"') && (_tokenSet_0.member(LA(2)))) {
						mSTRING(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='o') && (LA(2)=='f') && (true) && (true) && (true) && (true)) {
						mOF(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='"') && (true)) {
						mQUOTATION(true);
						theRetToken=_returnToken;
					}
					else if ((_tokenSet_1.member(LA(1))) && (true) && (true) && (true) && (true) && (true)) {
						mIDENT(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='_') && (true) && (true) && (true) && (true) && (true)) {
						mUNDERLINING(true);
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
		
		match('"');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDATABASE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DATABASE;
		int _saveIndex;
		
		match("data-base");
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
		match('/');
		match('/');
		{
		_loop1458:
		do {
			if ((_tokenSet_2.member(LA(1)))) {
				{
				match(_tokenSet_2);
				}
			}
			else {
				break _loop1458;
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
	
	public final void mCOMMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COMMENT;
		int _saveIndex;
		
		match("comment");
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
		match(';');
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
		_loop1467:
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
				break _loop1467;
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
		_loop1471:
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
			case '[':
			{
				match('[');
				break;
			}
			case ']':
			{
				match(']');
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
				case '%':  case '&':  case '(':  case ')':
				case ',':  case '-':  case '.':  case '/':
				case '0':  case '1':  case '2':  case '3':
				case '4':  case '5':  case '6':  case '7':
				case '8':  case '9':  case ':':  case '<':
				case '=':  case '>':  case '?':  case 'A':
				case 'B':  case 'C':  case 'D':  case 'E':
				case 'F':  case 'G':  case 'H':  case 'I':
				case 'J':  case 'K':  case 'L':  case 'M':
				case 'N':  case 'O':  case 'P':  case 'Q':
				case 'R':  case 'S':  case 'T':  case 'U':
				case 'V':  case 'W':  case 'X':  case 'Y':
				case 'Z':  case '[':  case '\\':  case ']':
				case '_':  case 'a':  case 'b':  case 'c':
				case 'd':  case 'e':  case 'f':  case 'g':
				case 'h':  case 'i':  case 'j':  case 'k':
				case 'l':  case 'm':  case 'n':  case 'o':
				case 'p':  case 'q':  case 'r':  case 's':
				case 't':  case 'u':  case 'v':  case 'w':
				case 'x':  case 'y':  case 'z':  case '|':
				case '\u00c1':  case '\u00c9':  case '\u00cd':  case '\u00d3':
				case '\u00da':  case '\u00dc':  case '\u00e1':  case '\u00e9':
				case '\u00ed':  case '\u00f3':  case '\u00fa':  case '\u00fc':
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
				break _loop1471;
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
	
	public final void mTRUE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TRUE;
		int _saveIndex;
		
		match("true");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mFALSE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = FALSE;
		int _saveIndex;
		
		match("false");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPOINT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = POINT;
		int _saveIndex;
		
		match('.');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLEFTP(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LEFTP;
		int _saveIndex;
		
		match('(');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mRIGHTP(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = RIGHTP;
		int _saveIndex;
		
		match(')');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLEFTC(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LEFTC;
		int _saveIndex;
		
		match('[');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mRIGHTC(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = RIGHTC;
		int _saveIndex;
		
		match(']');
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
	
	public final void mHYPHEN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = HYPHEN;
		int _saveIndex;
		
		match('-');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mUNDERLINING(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = UNDERLINING;
		int _saveIndex;
		
		match('_');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mASSIGNMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ASSIGNMENT;
		int _saveIndex;
		
		match('=');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mMEMORY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MEMORY;
		int _saveIndex;
		
		match("memory");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mVISUALPRECISION(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = VISUALPRECISION;
		int _saveIndex;
		
		match("visualprecision");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mVERSION(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = VERSION;
		int _saveIndex;
		
		match("version");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDEFAULT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DEFAULT;
		int _saveIndex;
		
		match("default");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mNODE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NODE;
		int _saveIndex;
		
		match("node");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mKIND(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = KIND;
		int _saveIndex;
		
		match("kind");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mOF(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = OF;
		int _saveIndex;
		
		match("of");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCHANCE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CHANCE;
		int _saveIndex;
		
		match("chance");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDECISION(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DECISION;
		int _saveIndex;
		
		match("decision");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mUTILITY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = UTILITY;
		int _saveIndex;
		
		match("utility");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mTYPE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TYPE;
		int _saveIndex;
		
		match("type");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mVARIABLE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = VARIABLE;
		int _saveIndex;
		
		match("variable");
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
		
		match("relation");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mFINITE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = FINITE;
		int _saveIndex;
		
		match("finite");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSTATES(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = STATES;
		int _saveIndex;
		
		match("states");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPOSX(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = POSX;
		int _saveIndex;
		
		match("pos_x");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPOSY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = POSY;
		int _saveIndex;
		
		match("pos_y");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCONTINUOUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CONTINUOUS;
		int _saveIndex;
		
		match("continuous");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mRELEVANCE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = RELEVANCE;
		int _saveIndex;
		
		match("relevance");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPURPOSE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PURPOSE;
		int _saveIndex;
		
		match("purpose");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mMIN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MIN;
		int _saveIndex;
		
		match("min");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mMAX(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MAX;
		int _saveIndex;
		
		match("max");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mNUM(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NUM;
		int _saveIndex;
		
		match("num");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPRECISION(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PRECISION;
		int _saveIndex;
		
		match("precision");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mTITLE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TITLE;
		int _saveIndex;
		
		match("title");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mAUTHOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = AUTHOR;
		int _saveIndex;
		
		match("author");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mWHOCHANGED(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = WHOCHANGED;
		int _saveIndex;
		
		match("whochanged");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mWHENCHANGED(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = WHENCHANGED;
		int _saveIndex;
		
		match("whenchanged");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mKINDOFGRAPH(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = KINDOFGRAPH;
		int _saveIndex;
		
		match("kindofgraph");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mNUMBER(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NUMBER;
		int _saveIndex;
		
		match("number");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCASES(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CASES;
		int _saveIndex;
		
		match("cases");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	
	private static final long[] mk_tokenSet_0() {
		long[] data = new long[8];
		data[0]=-576474543443402752L;
		data[1]=1729382251541561342L;
		data[3]=1443441072893600258L;
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 287948969894477824L, 576460745995190270L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = new long[8];
		data[0]=-9217L;
		data[1]=-1L;
		data[3]=1443441072893600258L;
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	
	}
