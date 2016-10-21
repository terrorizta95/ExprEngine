// Generated from MatlabGrammar.g4 by ANTLR 4.5.3

package io.lambdacloud.matlab;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MatlabGrammarLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, ADD=15, SUB=16, MUL=17, 
		DIV=18, POW=19, SOL=20, DADD=21, DSUB=22, DMUL=23, DRDIV=24, DLDIV=25, 
		DPOW=26, AND=27, OR=28, NOT=29, TRUE=30, FALSE=31, GT=32, GE=33, LT=34, 
		LE=35, EQ=36, NEQ=37, NEQ2=38, SHL=39, SHR=40, USHR=41, INC=42, DESC=43, 
		ADD_ASSIGN=44, SUB_ASSIGN=45, MUL_ASSIGN=46, DIV_ASSIGN=47, REM_ASSIGN=48, 
		ASSIGN=49, LPAREN=50, RPAREN=51, LBRK=52, RBRK=53, LCB=54, RCB=55, INTEGER=56, 
		FLOAT=57, IDENTIFIER=58, COMMA=59, COLON=60, SEMI=61, PERIOD=62, SQUOTE=63, 
		DQUOTE=64, DPRIME=65, COMMENT=66, SKIP_TOKEN=67, WS=68, StringLiteral=69;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "ADD", "SUB", "MUL", "DIV", 
		"POW", "SOL", "DADD", "DSUB", "DMUL", "DRDIV", "DLDIV", "DPOW", "AND", 
		"OR", "NOT", "TRUE", "FALSE", "GT", "GE", "LT", "LE", "EQ", "NEQ", "NEQ2", 
		"SHL", "SHR", "USHR", "INC", "DESC", "ADD_ASSIGN", "SUB_ASSIGN", "MUL_ASSIGN", 
		"DIV_ASSIGN", "REM_ASSIGN", "ASSIGN", "LPAREN", "RPAREN", "LBRK", "RBRK", 
		"LCB", "RCB", "INTEGER", "FLOAT", "IDENTIFIER", "COMMA", "COLON", "SEMI", 
		"PERIOD", "SQUOTE", "DQUOTE", "DPRIME", "COMMENT", "SKIP_TOKEN", "WS", 
		"StringLiteral", "Characters", "Character", "EscapeSeq"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'\n'", "'function'", "'end'", "'if'", "'elseif'", "'else'", "'for'", 
		"'in'", "'while'", "'return'", "'tic'", "'toc'", "'nargin'", "'@'", "'+'", 
		"'-'", "'*'", "'/'", null, "'\\'", "'.+'", "'.-'", "'.*'", "'./'", "'.\\'", 
		null, null, null, null, "'true'", "'false'", "'>'", "'>='", "'<'", "'<='", 
		"'=='", "'!='", "'~='", "'<<'", "'>>'", "'>>>'", "'++'", "'--'", "'+='", 
		"'-='", "'*='", "'/='", "'%='", "'='", "'('", "')'", "'['", "']'", "'{'", 
		"'}'", null, null, null, "','", "':'", "';'", "'.'", "'''", "'\"'", "'.''"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, "ADD", "SUB", "MUL", "DIV", "POW", "SOL", "DADD", "DSUB", 
		"DMUL", "DRDIV", "DLDIV", "DPOW", "AND", "OR", "NOT", "TRUE", "FALSE", 
		"GT", "GE", "LT", "LE", "EQ", "NEQ", "NEQ2", "SHL", "SHR", "USHR", "INC", 
		"DESC", "ADD_ASSIGN", "SUB_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", "REM_ASSIGN", 
		"ASSIGN", "LPAREN", "RPAREN", "LBRK", "RBRK", "LCB", "RCB", "INTEGER", 
		"FLOAT", "IDENTIFIER", "COMMA", "COLON", "SEMI", "PERIOD", "SQUOTE", "DQUOTE", 
		"DPRIME", "COMMENT", "SKIP_TOKEN", "WS", "StringLiteral"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public MatlabGrammarLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "MatlabGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2G\u01c1\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\5\3"+
		"\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b"+
		"\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3"+
		"\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\24\5\24\u00e2"+
		"\n\24\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\27\3\30\3\30\3\30\3\31\3\31"+
		"\3\31\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\33\5\33\u00fa\n\33\3\34\3\34"+
		"\3\34\3\34\3\34\5\34\u0101\n\34\3\35\3\35\3\35\3\35\5\35\u0107\n\35\3"+
		"\36\3\36\3\36\3\36\5\36\u010d\n\36\3\37\3\37\3\37\3\37\3\37\3 \3 \3 \3"+
		" \3 \3 \3!\3!\3\"\3\"\3\"\3#\3#\3$\3$\3$\3%\3%\3%\3&\3&\3&\3\'\3\'\3\'"+
		"\3(\3(\3(\3)\3)\3)\3*\3*\3*\3*\3+\3+\3+\3,\3,\3,\3-\3-\3-\3.\3.\3.\3/"+
		"\3/\3/\3\60\3\60\3\60\3\61\3\61\3\61\3\62\3\62\3\63\3\63\3\64\3\64\3\65"+
		"\3\65\3\66\3\66\3\67\3\67\38\38\39\69\u015b\n9\r9\169\u015c\39\59\u0160"+
		"\n9\3:\7:\u0163\n:\f:\16:\u0166\13:\3:\3:\6:\u016a\n:\r:\16:\u016b\3:"+
		"\6:\u016f\n:\r:\16:\u0170\3:\3:\7:\u0175\n:\f:\16:\u0178\13:\5:\u017a"+
		"\n:\3;\3;\7;\u017e\n;\f;\16;\u0181\13;\3<\3<\3=\3=\3>\3>\3?\3?\3@\3@\3"+
		"A\3A\3B\3B\3B\3C\3C\3C\5C\u0195\nC\3C\7C\u0198\nC\fC\16C\u019b\13C\3C"+
		"\5C\u019e\nC\3C\3C\3D\6D\u01a3\nD\rD\16D\u01a4\3D\3D\3E\3E\3F\3F\5F\u01ad"+
		"\nF\3F\5F\u01b0\nF\3F\3F\5F\u01b4\nF\3G\6G\u01b7\nG\rG\16G\u01b8\3H\3"+
		"H\5H\u01bd\nH\3I\3I\3I\2\2J\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25"+
		"\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32"+
		"\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a"+
		"\62c\63e\64g\65i\66k\67m8o9q:s;u<w=y>{?}@\177A\u0081B\u0083C\u0085D\u0087"+
		"E\u0089F\u008bG\u008d\2\u008f\2\u0091\2\3\2\13\4\2##\u0080\u0080\3\2\62"+
		";\5\2C\\aac|\6\2\62;C\\aac|\4\2\f\f\17\17\4\2\13\13\16\17\4\2\13\13\""+
		"\"\6\2\f\f$$))^^\n\2$$))^^ddhhppttvv\u01d3\2\3\3\2\2\2\2\5\3\2\2\2\2\7"+
		"\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2"+
		"\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2"+
		"\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2"+
		"\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2"+
		"\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2"+
		"\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M"+
		"\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2"+
		"\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2"+
		"\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s"+
		"\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177"+
		"\3\2\2\2\2\u0081\3\2\2\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2"+
		"\2\2\u0089\3\2\2\2\2\u008b\3\2\2\2\3\u0093\3\2\2\2\5\u0095\3\2\2\2\7\u009e"+
		"\3\2\2\2\t\u00a2\3\2\2\2\13\u00a5\3\2\2\2\r\u00ac\3\2\2\2\17\u00b1\3\2"+
		"\2\2\21\u00b5\3\2\2\2\23\u00b8\3\2\2\2\25\u00be\3\2\2\2\27\u00c5\3\2\2"+
		"\2\31\u00c9\3\2\2\2\33\u00cd\3\2\2\2\35\u00d4\3\2\2\2\37\u00d6\3\2\2\2"+
		"!\u00d8\3\2\2\2#\u00da\3\2\2\2%\u00dc\3\2\2\2\'\u00e1\3\2\2\2)\u00e3\3"+
		"\2\2\2+\u00e5\3\2\2\2-\u00e8\3\2\2\2/\u00eb\3\2\2\2\61\u00ee\3\2\2\2\63"+
		"\u00f1\3\2\2\2\65\u00f9\3\2\2\2\67\u0100\3\2\2\29\u0106\3\2\2\2;\u010c"+
		"\3\2\2\2=\u010e\3\2\2\2?\u0113\3\2\2\2A\u0119\3\2\2\2C\u011b\3\2\2\2E"+
		"\u011e\3\2\2\2G\u0120\3\2\2\2I\u0123\3\2\2\2K\u0126\3\2\2\2M\u0129\3\2"+
		"\2\2O\u012c\3\2\2\2Q\u012f\3\2\2\2S\u0132\3\2\2\2U\u0136\3\2\2\2W\u0139"+
		"\3\2\2\2Y\u013c\3\2\2\2[\u013f\3\2\2\2]\u0142\3\2\2\2_\u0145\3\2\2\2a"+
		"\u0148\3\2\2\2c\u014b\3\2\2\2e\u014d\3\2\2\2g\u014f\3\2\2\2i\u0151\3\2"+
		"\2\2k\u0153\3\2\2\2m\u0155\3\2\2\2o\u0157\3\2\2\2q\u015a\3\2\2\2s\u0179"+
		"\3\2\2\2u\u017b\3\2\2\2w\u0182\3\2\2\2y\u0184\3\2\2\2{\u0186\3\2\2\2}"+
		"\u0188\3\2\2\2\177\u018a\3\2\2\2\u0081\u018c\3\2\2\2\u0083\u018e\3\2\2"+
		"\2\u0085\u0194\3\2\2\2\u0087\u01a2\3\2\2\2\u0089\u01a8\3\2\2\2\u008b\u01ac"+
		"\3\2\2\2\u008d\u01b6\3\2\2\2\u008f\u01bc\3\2\2\2\u0091\u01be\3\2\2\2\u0093"+
		"\u0094\7\f\2\2\u0094\4\3\2\2\2\u0095\u0096\7h\2\2\u0096\u0097\7w\2\2\u0097"+
		"\u0098\7p\2\2\u0098\u0099\7e\2\2\u0099\u009a\7v\2\2\u009a\u009b\7k\2\2"+
		"\u009b\u009c\7q\2\2\u009c\u009d\7p\2\2\u009d\6\3\2\2\2\u009e\u009f\7g"+
		"\2\2\u009f\u00a0\7p\2\2\u00a0\u00a1\7f\2\2\u00a1\b\3\2\2\2\u00a2\u00a3"+
		"\7k\2\2\u00a3\u00a4\7h\2\2\u00a4\n\3\2\2\2\u00a5\u00a6\7g\2\2\u00a6\u00a7"+
		"\7n\2\2\u00a7\u00a8\7u\2\2\u00a8\u00a9\7g\2\2\u00a9\u00aa\7k\2\2\u00aa"+
		"\u00ab\7h\2\2\u00ab\f\3\2\2\2\u00ac\u00ad\7g\2\2\u00ad\u00ae\7n\2\2\u00ae"+
		"\u00af\7u\2\2\u00af\u00b0\7g\2\2\u00b0\16\3\2\2\2\u00b1\u00b2\7h\2\2\u00b2"+
		"\u00b3\7q\2\2\u00b3\u00b4\7t\2\2\u00b4\20\3\2\2\2\u00b5\u00b6\7k\2\2\u00b6"+
		"\u00b7\7p\2\2\u00b7\22\3\2\2\2\u00b8\u00b9\7y\2\2\u00b9\u00ba\7j\2\2\u00ba"+
		"\u00bb\7k\2\2\u00bb\u00bc\7n\2\2\u00bc\u00bd\7g\2\2\u00bd\24\3\2\2\2\u00be"+
		"\u00bf\7t\2\2\u00bf\u00c0\7g\2\2\u00c0\u00c1\7v\2\2\u00c1\u00c2\7w\2\2"+
		"\u00c2\u00c3\7t\2\2\u00c3\u00c4\7p\2\2\u00c4\26\3\2\2\2\u00c5\u00c6\7"+
		"v\2\2\u00c6\u00c7\7k\2\2\u00c7\u00c8\7e\2\2\u00c8\30\3\2\2\2\u00c9\u00ca"+
		"\7v\2\2\u00ca\u00cb\7q\2\2\u00cb\u00cc\7e\2\2\u00cc\32\3\2\2\2\u00cd\u00ce"+
		"\7p\2\2\u00ce\u00cf\7c\2\2\u00cf\u00d0\7t\2\2\u00d0\u00d1\7i\2\2\u00d1"+
		"\u00d2\7k\2\2\u00d2\u00d3\7p\2\2\u00d3\34\3\2\2\2\u00d4\u00d5\7B\2\2\u00d5"+
		"\36\3\2\2\2\u00d6\u00d7\7-\2\2\u00d7 \3\2\2\2\u00d8\u00d9\7/\2\2\u00d9"+
		"\"\3\2\2\2\u00da\u00db\7,\2\2\u00db$\3\2\2\2\u00dc\u00dd\7\61\2\2\u00dd"+
		"&\3\2\2\2\u00de\u00df\7,\2\2\u00df\u00e2\7,\2\2\u00e0\u00e2\7`\2\2\u00e1"+
		"\u00de\3\2\2\2\u00e1\u00e0\3\2\2\2\u00e2(\3\2\2\2\u00e3\u00e4\7^\2\2\u00e4"+
		"*\3\2\2\2\u00e5\u00e6\7\60\2\2\u00e6\u00e7\7-\2\2\u00e7,\3\2\2\2\u00e8"+
		"\u00e9\7\60\2\2\u00e9\u00ea\7/\2\2\u00ea.\3\2\2\2\u00eb\u00ec\7\60\2\2"+
		"\u00ec\u00ed\7,\2\2\u00ed\60\3\2\2\2\u00ee\u00ef\7\60\2\2\u00ef\u00f0"+
		"\7\61\2\2\u00f0\62\3\2\2\2\u00f1\u00f2\7\60\2\2\u00f2\u00f3\7^\2\2\u00f3"+
		"\64\3\2\2\2\u00f4\u00f5\7\60\2\2\u00f5\u00f6\7,\2\2\u00f6\u00fa\7,\2\2"+
		"\u00f7\u00f8\7\60\2\2\u00f8\u00fa\7`\2\2\u00f9\u00f4\3\2\2\2\u00f9\u00f7"+
		"\3\2\2\2\u00fa\66\3\2\2\2\u00fb\u00fc\7c\2\2\u00fc\u00fd\7p\2\2\u00fd"+
		"\u0101\7f\2\2\u00fe\u00ff\7(\2\2\u00ff\u0101\7(\2\2\u0100\u00fb\3\2\2"+
		"\2\u0100\u00fe\3\2\2\2\u01018\3\2\2\2\u0102\u0103\7q\2\2\u0103\u0107\7"+
		"t\2\2\u0104\u0105\7~\2\2\u0105\u0107\7~\2\2\u0106\u0102\3\2\2\2\u0106"+
		"\u0104\3\2\2\2\u0107:\3\2\2\2\u0108\u0109\7p\2\2\u0109\u010a\7q\2\2\u010a"+
		"\u010d\7v\2\2\u010b\u010d\t\2\2\2\u010c\u0108\3\2\2\2\u010c\u010b\3\2"+
		"\2\2\u010d<\3\2\2\2\u010e\u010f\7v\2\2\u010f\u0110\7t\2\2\u0110\u0111"+
		"\7w\2\2\u0111\u0112\7g\2\2\u0112>\3\2\2\2\u0113\u0114\7h\2\2\u0114\u0115"+
		"\7c\2\2\u0115\u0116\7n\2\2\u0116\u0117\7u\2\2\u0117\u0118\7g\2\2\u0118"+
		"@\3\2\2\2\u0119\u011a\7@\2\2\u011aB\3\2\2\2\u011b\u011c\7@\2\2\u011c\u011d"+
		"\7?\2\2\u011dD\3\2\2\2\u011e\u011f\7>\2\2\u011fF\3\2\2\2\u0120\u0121\7"+
		">\2\2\u0121\u0122\7?\2\2\u0122H\3\2\2\2\u0123\u0124\7?\2\2\u0124\u0125"+
		"\7?\2\2\u0125J\3\2\2\2\u0126\u0127\7#\2\2\u0127\u0128\7?\2\2\u0128L\3"+
		"\2\2\2\u0129\u012a\7\u0080\2\2\u012a\u012b\7?\2\2\u012bN\3\2\2\2\u012c"+
		"\u012d\7>\2\2\u012d\u012e\7>\2\2\u012eP\3\2\2\2\u012f\u0130\7@\2\2\u0130"+
		"\u0131\7@\2\2\u0131R\3\2\2\2\u0132\u0133\7@\2\2\u0133\u0134\7@\2\2\u0134"+
		"\u0135\7@\2\2\u0135T\3\2\2\2\u0136\u0137\7-\2\2\u0137\u0138\7-\2\2\u0138"+
		"V\3\2\2\2\u0139\u013a\7/\2\2\u013a\u013b\7/\2\2\u013bX\3\2\2\2\u013c\u013d"+
		"\7-\2\2\u013d\u013e\7?\2\2\u013eZ\3\2\2\2\u013f\u0140\7/\2\2\u0140\u0141"+
		"\7?\2\2\u0141\\\3\2\2\2\u0142\u0143\7,\2\2\u0143\u0144\7?\2\2\u0144^\3"+
		"\2\2\2\u0145\u0146\7\61\2\2\u0146\u0147\7?\2\2\u0147`\3\2\2\2\u0148\u0149"+
		"\7\'\2\2\u0149\u014a\7?\2\2\u014ab\3\2\2\2\u014b\u014c\7?\2\2\u014cd\3"+
		"\2\2\2\u014d\u014e\7*\2\2\u014ef\3\2\2\2\u014f\u0150\7+\2\2\u0150h\3\2"+
		"\2\2\u0151\u0152\7]\2\2\u0152j\3\2\2\2\u0153\u0154\7_\2\2\u0154l\3\2\2"+
		"\2\u0155\u0156\7}\2\2\u0156n\3\2\2\2\u0157\u0158\7\177\2\2\u0158p\3\2"+
		"\2\2\u0159\u015b\t\3\2\2\u015a\u0159\3\2\2\2\u015b\u015c\3\2\2\2\u015c"+
		"\u015a\3\2\2\2\u015c\u015d\3\2\2\2\u015d\u015f\3\2\2\2\u015e\u0160\7N"+
		"\2\2\u015f\u015e\3\2\2\2\u015f\u0160\3\2\2\2\u0160r\3\2\2\2\u0161\u0163"+
		"\t\3\2\2\u0162\u0161\3\2\2\2\u0163\u0166\3\2\2\2\u0164\u0162\3\2\2\2\u0164"+
		"\u0165\3\2\2\2\u0165\u0167\3\2\2\2\u0166\u0164\3\2\2\2\u0167\u0169\7\60"+
		"\2\2\u0168\u016a\t\3\2\2\u0169\u0168\3\2\2\2\u016a\u016b\3\2\2\2\u016b"+
		"\u0169\3\2\2\2\u016b\u016c\3\2\2\2\u016c\u017a\3\2\2\2\u016d\u016f\t\3"+
		"\2\2\u016e\u016d\3\2\2\2\u016f\u0170\3\2\2\2\u0170\u016e\3\2\2\2\u0170"+
		"\u0171\3\2\2\2\u0171\u0172\3\2\2\2\u0172\u0176\7\60\2\2\u0173\u0175\t"+
		"\3\2\2\u0174\u0173\3\2\2\2\u0175\u0178\3\2\2\2\u0176\u0174\3\2\2\2\u0176"+
		"\u0177\3\2\2\2\u0177\u017a\3\2\2\2\u0178\u0176\3\2\2\2\u0179\u0164\3\2"+
		"\2\2\u0179\u016e\3\2\2\2\u017at\3\2\2\2\u017b\u017f\t\4\2\2\u017c\u017e"+
		"\t\5\2\2\u017d\u017c\3\2\2\2\u017e\u0181\3\2\2\2\u017f\u017d\3\2\2\2\u017f"+
		"\u0180\3\2\2\2\u0180v\3\2\2\2\u0181\u017f\3\2\2\2\u0182\u0183\7.\2\2\u0183"+
		"x\3\2\2\2\u0184\u0185\7<\2\2\u0185z\3\2\2\2\u0186\u0187\7=\2\2\u0187|"+
		"\3\2\2\2\u0188\u0189\7\60\2\2\u0189~\3\2\2\2\u018a\u018b\7)\2\2\u018b"+
		"\u0080\3\2\2\2\u018c\u018d\7$\2\2\u018d\u0082\3\2\2\2\u018e\u018f\7\60"+
		"\2\2\u018f\u0190\7)\2\2\u0190\u0084\3\2\2\2\u0191\u0192\7\61\2\2\u0192"+
		"\u0195\7\61\2\2\u0193\u0195\7\'\2\2\u0194\u0191\3\2\2\2\u0194\u0193\3"+
		"\2\2\2\u0195\u0199\3\2\2\2\u0196\u0198\n\6\2\2\u0197\u0196\3\2\2\2\u0198"+
		"\u019b\3\2\2\2\u0199\u0197\3\2\2\2\u0199\u019a\3\2\2\2\u019a\u019d\3\2"+
		"\2\2\u019b\u0199\3\2\2\2\u019c\u019e\7\2\2\3\u019d\u019c\3\2\2\2\u019d"+
		"\u019e\3\2\2\2\u019e\u019f\3\2\2\2\u019f\u01a0\bC\2\2\u01a0\u0086\3\2"+
		"\2\2\u01a1\u01a3\t\7\2\2\u01a2\u01a1\3\2\2\2\u01a3\u01a4\3\2\2\2\u01a4"+
		"\u01a2\3\2\2\2\u01a4\u01a5\3\2\2\2\u01a5\u01a6\3\2\2\2\u01a6\u01a7\bD"+
		"\2\2\u01a7\u0088\3\2\2\2\u01a8\u01a9\t\b\2\2\u01a9\u008a\3\2\2\2\u01aa"+
		"\u01ad\5\u0081A\2\u01ab\u01ad\5\177@\2\u01ac\u01aa\3\2\2\2\u01ac\u01ab"+
		"\3\2\2\2\u01ad\u01af\3\2\2\2\u01ae\u01b0\5\u008dG\2\u01af\u01ae\3\2\2"+
		"\2\u01af\u01b0\3\2\2\2\u01b0\u01b3\3\2\2\2\u01b1\u01b4\5\u0081A\2\u01b2"+
		"\u01b4\5\177@\2\u01b3\u01b1\3\2\2\2\u01b3\u01b2\3\2\2\2\u01b4\u008c\3"+
		"\2\2\2\u01b5\u01b7\5\u008fH\2\u01b6\u01b5\3\2\2\2\u01b7\u01b8\3\2\2\2"+
		"\u01b8\u01b6\3\2\2\2\u01b8\u01b9\3\2\2\2\u01b9\u008e\3\2\2\2\u01ba\u01bd"+
		"\n\t\2\2\u01bb\u01bd\5\u0091I\2\u01bc\u01ba\3\2\2\2\u01bc\u01bb\3\2\2"+
		"\2\u01bd\u0090\3\2\2\2\u01be\u01bf\7^\2\2\u01bf\u01c0\t\n\2\2\u01c0\u0092"+
		"\3\2\2\2\31\2\u00e1\u00f9\u0100\u0106\u010c\u015c\u015f\u0164\u016b\u0170"+
		"\u0176\u0179\u017f\u0194\u0199\u019d\u01a4\u01ac\u01af\u01b3\u01b8\u01bc"+
		"\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}