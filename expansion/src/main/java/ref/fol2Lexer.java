// Generated from ref/fol2.g4 by ANTLR 4.7.1
package ref;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class fol2Lexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, IN=8, FOR=9, VAR=10, 
		BIND=11, LPAR=12, RPAR=13, LT=14, GT=15, STRING=16, WS=17, COMMENT=18, 
		LINE_COMMENT=19, VALUE=20;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "IN", "FOR", "VAR", 
		"BIND", "LPAR", "RPAR", "LT", "GT", "STRING", "CHAR_NO_NL", "WS", "COMMENT", 
		"LINE_COMMENT", "VALUE"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "';'", "'.'", "'{'", "'}'", "','", "'('", "')'", "'in'", "'expand'", 
		"'var'", "'forall'", "'['", "']'", "'<'", "'>'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, "IN", "FOR", "VAR", "BIND", 
		"LPAR", "RPAR", "LT", "GT", "STRING", "WS", "COMMENT", "LINE_COMMENT", 
		"VALUE"
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


	public fol2Lexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "fol2.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\26\u0088\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\3\2\3\2\3\3\3\3\3\4\3\4"+
		"\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3"+
		"\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\16\3\16\3"+
		"\17\3\17\3\20\3\20\3\21\6\21Z\n\21\r\21\16\21[\3\22\3\22\3\23\6\23a\n"+
		"\23\r\23\16\23b\3\23\3\23\3\24\3\24\3\24\3\24\7\24k\n\24\f\24\16\24n\13"+
		"\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\7\25w\n\25\f\25\16\25z\13\25\3"+
		"\25\3\25\3\26\3\26\3\26\3\26\7\26\u0082\n\26\f\26\16\26\u0085\13\26\3"+
		"\26\3\26\3l\2\27\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31"+
		"\16\33\17\35\20\37\21!\22#\2%\23\'\24)\25+\26\3\2\7\4\2C\\c|\5\2\13\f"+
		"\17\17\"\"\4\2\f\f\17\17\6\2\f\f\17\17$$^^\4\2$$^^\2\u008c\2\3\3\2\2\2"+
		"\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2"+
		"\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2"+
		"\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2%\3\2\2\2\2\'\3\2"+
		"\2\2\2)\3\2\2\2\2+\3\2\2\2\3-\3\2\2\2\5/\3\2\2\2\7\61\3\2\2\2\t\63\3\2"+
		"\2\2\13\65\3\2\2\2\r\67\3\2\2\2\179\3\2\2\2\21;\3\2\2\2\23>\3\2\2\2\25"+
		"E\3\2\2\2\27I\3\2\2\2\31P\3\2\2\2\33R\3\2\2\2\35T\3\2\2\2\37V\3\2\2\2"+
		"!Y\3\2\2\2#]\3\2\2\2%`\3\2\2\2\'f\3\2\2\2)t\3\2\2\2+}\3\2\2\2-.\7=\2\2"+
		".\4\3\2\2\2/\60\7\60\2\2\60\6\3\2\2\2\61\62\7}\2\2\62\b\3\2\2\2\63\64"+
		"\7\177\2\2\64\n\3\2\2\2\65\66\7.\2\2\66\f\3\2\2\2\678\7*\2\28\16\3\2\2"+
		"\29:\7+\2\2:\20\3\2\2\2;<\7k\2\2<=\7p\2\2=\22\3\2\2\2>?\7g\2\2?@\7z\2"+
		"\2@A\7r\2\2AB\7c\2\2BC\7p\2\2CD\7f\2\2D\24\3\2\2\2EF\7x\2\2FG\7c\2\2G"+
		"H\7t\2\2H\26\3\2\2\2IJ\7h\2\2JK\7q\2\2KL\7t\2\2LM\7c\2\2MN\7n\2\2NO\7"+
		"n\2\2O\30\3\2\2\2PQ\7]\2\2Q\32\3\2\2\2RS\7_\2\2S\34\3\2\2\2TU\7>\2\2U"+
		"\36\3\2\2\2VW\7@\2\2W \3\2\2\2XZ\5#\22\2YX\3\2\2\2Z[\3\2\2\2[Y\3\2\2\2"+
		"[\\\3\2\2\2\\\"\3\2\2\2]^\t\2\2\2^$\3\2\2\2_a\t\3\2\2`_\3\2\2\2ab\3\2"+
		"\2\2b`\3\2\2\2bc\3\2\2\2cd\3\2\2\2de\b\23\2\2e&\3\2\2\2fg\7\61\2\2gh\7"+
		",\2\2hl\3\2\2\2ik\13\2\2\2ji\3\2\2\2kn\3\2\2\2lm\3\2\2\2lj\3\2\2\2mo\3"+
		"\2\2\2nl\3\2\2\2op\7,\2\2pq\7\61\2\2qr\3\2\2\2rs\b\24\3\2s(\3\2\2\2tx"+
		"\7%\2\2uw\n\4\2\2vu\3\2\2\2wz\3\2\2\2xv\3\2\2\2xy\3\2\2\2y{\3\2\2\2zx"+
		"\3\2\2\2{|\b\25\3\2|*\3\2\2\2}\u0083\7$\2\2~\u0082\n\5\2\2\177\u0080\7"+
		"^\2\2\u0080\u0082\t\6\2\2\u0081~\3\2\2\2\u0081\177\3\2\2\2\u0082\u0085"+
		"\3\2\2\2\u0083\u0081\3\2\2\2\u0083\u0084\3\2\2\2\u0084\u0086\3\2\2\2\u0085"+
		"\u0083\3\2\2\2\u0086\u0087\7$\2\2\u0087,\3\2\2\2\t\2[blx\u0081\u0083\4"+
		"\2\3\2\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}