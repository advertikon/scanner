// Generated from /home/max/Documents/java/calc/Calc/src/ua/com/advertikon/calc/Calculator.g4 by ANTLR 4.7.2

package ua.com.advertikon.calc;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CalculatorLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, MUL=4, DIV=5, ADD=6, SUB=7, PERSENT=8, SIN=9, 
		COS=10, TAN=11, ASIN=12, ACOS=13, ATAN=14, LN=15, LOG=16, POW=17, PI=18, 
		E=19, SQRT=20, ROOT3=21, ROOT4=22, ROOT=23, FACTORIAL=24, ID=25, FLOAT=26, 
		INT=27, NEWLINE=28, WS=29;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "MUL", "DIV", "ADD", "SUB", "PERSENT", "SIN", 
			"COS", "TAN", "ASIN", "ACOS", "ATAN", "LN", "LOG", "POW", "PI", "E", 
			"SQRT", "ROOT3", "ROOT4", "ROOT", "FACTORIAL", "ID", "FLOAT", "INT", 
			"NEWLINE", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'='", "'('", "')'", "'*'", "'/'", "'+'", "'-'", "'%'", "'sin'", 
			"'cos'", "'tan'", "'asin'", "'acos'", "'atan'", "'ln'", "'log'", "'^'", 
			"'\u03C0'", "'e'", "'\u221A'", "'\u221B'", "'\u221C'", "'root'", "'!'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, "MUL", "DIV", "ADD", "SUB", "PERSENT", "SIN", 
			"COS", "TAN", "ASIN", "ACOS", "ATAN", "LN", "LOG", "POW", "PI", "E", 
			"SQRT", "ROOT3", "ROOT4", "ROOT", "FACTORIAL", "ID", "FLOAT", "INT", 
			"NEWLINE", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
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


	public CalculatorLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Calculator.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\37\u00a4\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\3\2\3\2\3\3\3"+
		"\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n\3\13"+
		"\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16"+
		"\3\16\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\22"+
		"\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\30"+
		"\3\30\3\30\3\31\3\31\3\32\6\32\u0084\n\32\r\32\16\32\u0085\3\33\7\33\u0089"+
		"\n\33\f\33\16\33\u008c\13\33\3\33\3\33\6\33\u0090\n\33\r\33\16\33\u0091"+
		"\3\34\6\34\u0095\n\34\r\34\16\34\u0096\3\35\5\35\u009a\n\35\3\35\3\35"+
		"\3\36\6\36\u009f\n\36\r\36\16\36\u00a0\3\36\3\36\2\2\37\3\3\5\4\7\5\t"+
		"\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23"+
		"%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37\3\2\5\4\2C\\"+
		"c|\3\2\62;\4\2\13\13\"\"\2\u00a9\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2"+
		"\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2"+
		"\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2"+
		"\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2"+
		"\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2"+
		"\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\3=\3\2\2\2\5?\3\2\2\2\7A\3\2\2\2"+
		"\tC\3\2\2\2\13E\3\2\2\2\rG\3\2\2\2\17I\3\2\2\2\21K\3\2\2\2\23M\3\2\2\2"+
		"\25Q\3\2\2\2\27U\3\2\2\2\31Y\3\2\2\2\33^\3\2\2\2\35c\3\2\2\2\37h\3\2\2"+
		"\2!k\3\2\2\2#o\3\2\2\2%q\3\2\2\2\'s\3\2\2\2)u\3\2\2\2+w\3\2\2\2-y\3\2"+
		"\2\2/{\3\2\2\2\61\u0080\3\2\2\2\63\u0083\3\2\2\2\65\u008a\3\2\2\2\67\u0094"+
		"\3\2\2\29\u0099\3\2\2\2;\u009e\3\2\2\2=>\7?\2\2>\4\3\2\2\2?@\7*\2\2@\6"+
		"\3\2\2\2AB\7+\2\2B\b\3\2\2\2CD\7,\2\2D\n\3\2\2\2EF\7\61\2\2F\f\3\2\2\2"+
		"GH\7-\2\2H\16\3\2\2\2IJ\7/\2\2J\20\3\2\2\2KL\7\'\2\2L\22\3\2\2\2MN\7u"+
		"\2\2NO\7k\2\2OP\7p\2\2P\24\3\2\2\2QR\7e\2\2RS\7q\2\2ST\7u\2\2T\26\3\2"+
		"\2\2UV\7v\2\2VW\7c\2\2WX\7p\2\2X\30\3\2\2\2YZ\7c\2\2Z[\7u\2\2[\\\7k\2"+
		"\2\\]\7p\2\2]\32\3\2\2\2^_\7c\2\2_`\7e\2\2`a\7q\2\2ab\7u\2\2b\34\3\2\2"+
		"\2cd\7c\2\2de\7v\2\2ef\7c\2\2fg\7p\2\2g\36\3\2\2\2hi\7n\2\2ij\7p\2\2j"+
		" \3\2\2\2kl\7n\2\2lm\7q\2\2mn\7i\2\2n\"\3\2\2\2op\7`\2\2p$\3\2\2\2qr\7"+
		"\u03c2\2\2r&\3\2\2\2st\7g\2\2t(\3\2\2\2uv\7\u221c\2\2v*\3\2\2\2wx\7\u221d"+
		"\2\2x,\3\2\2\2yz\7\u221e\2\2z.\3\2\2\2{|\7t\2\2|}\7q\2\2}~\7q\2\2~\177"+
		"\7v\2\2\177\60\3\2\2\2\u0080\u0081\7#\2\2\u0081\62\3\2\2\2\u0082\u0084"+
		"\t\2\2\2\u0083\u0082\3\2\2\2\u0084\u0085\3\2\2\2\u0085\u0083\3\2\2\2\u0085"+
		"\u0086\3\2\2\2\u0086\64\3\2\2\2\u0087\u0089\t\3\2\2\u0088\u0087\3\2\2"+
		"\2\u0089\u008c\3\2\2\2\u008a\u0088\3\2\2\2\u008a\u008b\3\2\2\2\u008b\u008d"+
		"\3\2\2\2\u008c\u008a\3\2\2\2\u008d\u008f\7\60\2\2\u008e\u0090\t\3\2\2"+
		"\u008f\u008e\3\2\2\2\u0090\u0091\3\2\2\2\u0091\u008f\3\2\2\2\u0091\u0092"+
		"\3\2\2\2\u0092\66\3\2\2\2\u0093\u0095\t\3\2\2\u0094\u0093\3\2\2\2\u0095"+
		"\u0096\3\2\2\2\u0096\u0094\3\2\2\2\u0096\u0097\3\2\2\2\u00978\3\2\2\2"+
		"\u0098\u009a\7\17\2\2\u0099\u0098\3\2\2\2\u0099\u009a\3\2\2\2\u009a\u009b"+
		"\3\2\2\2\u009b\u009c\7\f\2\2\u009c:\3\2\2\2\u009d\u009f\t\4\2\2\u009e"+
		"\u009d\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u009e\3\2\2\2\u00a0\u00a1\3\2"+
		"\2\2\u00a1\u00a2\3\2\2\2\u00a2\u00a3\b\36\2\2\u00a3<\3\2\2\2\t\2\u0085"+
		"\u008a\u0091\u0096\u0099\u00a0\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}