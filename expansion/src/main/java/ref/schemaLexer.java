// Generated from ref/schema.g4 by ANTLR 4.7.1
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
public class schemaLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, TRANSFER=8, TRYEXPAND=9, 
		RELATION=10, FUTURECHECK=11, EVENT=12, UNIQUE=13, ENTITY=14, FD=15, RULE=16, 
		FOR=17, DECLARE=18, SET=19, WITH=20, BEGIN=21, END=22, EXIST=23, EXCEPT=24, 
		MACRO=25, MARROW=26, IMPLIES=27, LPAR=28, RPAR=29, LT=30, GT=31, LEQ=32, 
		GEQ=33, COMMA=34, TYPE=35, NEG=36, BOT=37, VALUE=38, STRING=39, WS=40, 
		COMMENT=41, LINE_COMMENT=42;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "TRANSFER", "TRYEXPAND", 
		"RELATION", "FUTURECHECK", "EVENT", "UNIQUE", "ENTITY", "FD", "RULE", 
		"FOR", "DECLARE", "SET", "WITH", "BEGIN", "END", "EXIST", "EXCEPT", "MACRO", 
		"MARROW", "IMPLIES", "LPAR", "RPAR", "LT", "GT", "LEQ", "GEQ", "COMMA", 
		"TYPE", "NEG", "BOT", "VALUE", "STRING", "CHAR_NO_NL", "WS", "COMMENT", 
		"LINE_COMMENT"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "';'", "'.'", "'in time'", "'and space'", "'\\forall'", "'\\exists'", 
		"'notnull'", "'transfer'", "'try-expand'", "'relation'", "'future-check'", 
		"'event'", "'unique'", "'entity'", "'MVD'", "'rule'", "'for'", "'as'", 
		"'set'", "'with'", "'begin'", "'end'", "'exist'", "'except'", "'macro'", 
		"'->>'", "'=>'", "'('", "')'", "'<'", "'>'", "'<='", "'>='", "','", "':'", 
		"'~'", "'False'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, "TRANSFER", "TRYEXPAND", 
		"RELATION", "FUTURECHECK", "EVENT", "UNIQUE", "ENTITY", "FD", "RULE", 
		"FOR", "DECLARE", "SET", "WITH", "BEGIN", "END", "EXIST", "EXCEPT", "MACRO", 
		"MARROW", "IMPLIES", "LPAR", "RPAR", "LT", "GT", "LEQ", "GEQ", "COMMA", 
		"TYPE", "NEG", "BOT", "VALUE", "STRING", "WS", "COMMENT", "LINE_COMMENT"
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


	public schemaLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "schema.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2,\u0145\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3"+
		"\13\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\21\3\21"+
		"\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\24\3\24\3\24\3\24"+
		"\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27"+
		"\3\27\3\30\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3\31"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\34\3\34\3\34\3\35"+
		"\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3!\3\"\3\"\3\"\3#\3#\3$\3$\3%\3"+
		"%\3&\3&\3&\3&\3&\3&\3\'\3\'\3\'\3\'\3(\6(\u0122\n(\r(\16(\u0123\3)\3)"+
		"\3*\6*\u0129\n*\r*\16*\u012a\3*\3*\3+\3+\3+\3+\7+\u0133\n+\f+\16+\u0136"+
		"\13+\3+\3+\3+\3+\3+\3,\3,\7,\u013f\n,\f,\16,\u0142\13,\3,\3,\3\u0134\2"+
		"-\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20"+
		"\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37"+
		"= ?!A\"C#E$G%I&K\'M(O)Q\2S*U+W,\3\2\5\5\2\60\60C\\c|\5\2\13\f\17\17\""+
		"\"\4\2\f\f\17\17\2\u0147\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2"+
		"\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25"+
		"\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2"+
		"\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2"+
		"\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3"+
		"\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2"+
		"\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2"+
		"S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\3Y\3\2\2\2\5[\3\2\2\2\7]\3\2\2\2\te\3"+
		"\2\2\2\13o\3\2\2\2\rw\3\2\2\2\17\177\3\2\2\2\21\u0087\3\2\2\2\23\u0090"+
		"\3\2\2\2\25\u009b\3\2\2\2\27\u00a4\3\2\2\2\31\u00b1\3\2\2\2\33\u00b7\3"+
		"\2\2\2\35\u00be\3\2\2\2\37\u00c5\3\2\2\2!\u00c9\3\2\2\2#\u00ce\3\2\2\2"+
		"%\u00d2\3\2\2\2\'\u00d5\3\2\2\2)\u00d9\3\2\2\2+\u00de\3\2\2\2-\u00e4\3"+
		"\2\2\2/\u00e8\3\2\2\2\61\u00ee\3\2\2\2\63\u00f5\3\2\2\2\65\u00fb\3\2\2"+
		"\2\67\u00ff\3\2\2\29\u0102\3\2\2\2;\u0104\3\2\2\2=\u0106\3\2\2\2?\u0108"+
		"\3\2\2\2A\u010a\3\2\2\2C\u010d\3\2\2\2E\u0110\3\2\2\2G\u0112\3\2\2\2I"+
		"\u0114\3\2\2\2K\u0116\3\2\2\2M\u011c\3\2\2\2O\u0121\3\2\2\2Q\u0125\3\2"+
		"\2\2S\u0128\3\2\2\2U\u012e\3\2\2\2W\u013c\3\2\2\2YZ\7=\2\2Z\4\3\2\2\2"+
		"[\\\7\60\2\2\\\6\3\2\2\2]^\7k\2\2^_\7p\2\2_`\7\"\2\2`a\7v\2\2ab\7k\2\2"+
		"bc\7o\2\2cd\7g\2\2d\b\3\2\2\2ef\7c\2\2fg\7p\2\2gh\7f\2\2hi\7\"\2\2ij\7"+
		"u\2\2jk\7r\2\2kl\7c\2\2lm\7e\2\2mn\7g\2\2n\n\3\2\2\2op\7^\2\2pq\7h\2\2"+
		"qr\7q\2\2rs\7t\2\2st\7c\2\2tu\7n\2\2uv\7n\2\2v\f\3\2\2\2wx\7^\2\2xy\7"+
		"g\2\2yz\7z\2\2z{\7k\2\2{|\7u\2\2|}\7v\2\2}~\7u\2\2~\16\3\2\2\2\177\u0080"+
		"\7p\2\2\u0080\u0081\7q\2\2\u0081\u0082\7v\2\2\u0082\u0083\7p\2\2\u0083"+
		"\u0084\7w\2\2\u0084\u0085\7n\2\2\u0085\u0086\7n\2\2\u0086\20\3\2\2\2\u0087"+
		"\u0088\7v\2\2\u0088\u0089\7t\2\2\u0089\u008a\7c\2\2\u008a\u008b\7p\2\2"+
		"\u008b\u008c\7u\2\2\u008c\u008d\7h\2\2\u008d\u008e\7g\2\2\u008e\u008f"+
		"\7t\2\2\u008f\22\3\2\2\2\u0090\u0091\7v\2\2\u0091\u0092\7t\2\2\u0092\u0093"+
		"\7{\2\2\u0093\u0094\7/\2\2\u0094\u0095\7g\2\2\u0095\u0096\7z\2\2\u0096"+
		"\u0097\7r\2\2\u0097\u0098\7c\2\2\u0098\u0099\7p\2\2\u0099\u009a\7f\2\2"+
		"\u009a\24\3\2\2\2\u009b\u009c\7t\2\2\u009c\u009d\7g\2\2\u009d\u009e\7"+
		"n\2\2\u009e\u009f\7c\2\2\u009f\u00a0\7v\2\2\u00a0\u00a1\7k\2\2\u00a1\u00a2"+
		"\7q\2\2\u00a2\u00a3\7p\2\2\u00a3\26\3\2\2\2\u00a4\u00a5\7h\2\2\u00a5\u00a6"+
		"\7w\2\2\u00a6\u00a7\7v\2\2\u00a7\u00a8\7w\2\2\u00a8\u00a9\7t\2\2\u00a9"+
		"\u00aa\7g\2\2\u00aa\u00ab\7/\2\2\u00ab\u00ac\7e\2\2\u00ac\u00ad\7j\2\2"+
		"\u00ad\u00ae\7g\2\2\u00ae\u00af\7e\2\2\u00af\u00b0\7m\2\2\u00b0\30\3\2"+
		"\2\2\u00b1\u00b2\7g\2\2\u00b2\u00b3\7x\2\2\u00b3\u00b4\7g\2\2\u00b4\u00b5"+
		"\7p\2\2\u00b5\u00b6\7v\2\2\u00b6\32\3\2\2\2\u00b7\u00b8\7w\2\2\u00b8\u00b9"+
		"\7p\2\2\u00b9\u00ba\7k\2\2\u00ba\u00bb\7s\2\2\u00bb\u00bc\7w\2\2\u00bc"+
		"\u00bd\7g\2\2\u00bd\34\3\2\2\2\u00be\u00bf\7g\2\2\u00bf\u00c0\7p\2\2\u00c0"+
		"\u00c1\7v\2\2\u00c1\u00c2\7k\2\2\u00c2\u00c3\7v\2\2\u00c3\u00c4\7{\2\2"+
		"\u00c4\36\3\2\2\2\u00c5\u00c6\7O\2\2\u00c6\u00c7\7X\2\2\u00c7\u00c8\7"+
		"F\2\2\u00c8 \3\2\2\2\u00c9\u00ca\7t\2\2\u00ca\u00cb\7w\2\2\u00cb\u00cc"+
		"\7n\2\2\u00cc\u00cd\7g\2\2\u00cd\"\3\2\2\2\u00ce\u00cf\7h\2\2\u00cf\u00d0"+
		"\7q\2\2\u00d0\u00d1\7t\2\2\u00d1$\3\2\2\2\u00d2\u00d3\7c\2\2\u00d3\u00d4"+
		"\7u\2\2\u00d4&\3\2\2\2\u00d5\u00d6\7u\2\2\u00d6\u00d7\7g\2\2\u00d7\u00d8"+
		"\7v\2\2\u00d8(\3\2\2\2\u00d9\u00da\7y\2\2\u00da\u00db\7k\2\2\u00db\u00dc"+
		"\7v\2\2\u00dc\u00dd\7j\2\2\u00dd*\3\2\2\2\u00de\u00df\7d\2\2\u00df\u00e0"+
		"\7g\2\2\u00e0\u00e1\7i\2\2\u00e1\u00e2\7k\2\2\u00e2\u00e3\7p\2\2\u00e3"+
		",\3\2\2\2\u00e4\u00e5\7g\2\2\u00e5\u00e6\7p\2\2\u00e6\u00e7\7f\2\2\u00e7"+
		".\3\2\2\2\u00e8\u00e9\7g\2\2\u00e9\u00ea\7z\2\2\u00ea\u00eb\7k\2\2\u00eb"+
		"\u00ec\7u\2\2\u00ec\u00ed\7v\2\2\u00ed\60\3\2\2\2\u00ee\u00ef\7g\2\2\u00ef"+
		"\u00f0\7z\2\2\u00f0\u00f1\7e\2\2\u00f1\u00f2\7g\2\2\u00f2\u00f3\7r\2\2"+
		"\u00f3\u00f4\7v\2\2\u00f4\62\3\2\2\2\u00f5\u00f6\7o\2\2\u00f6\u00f7\7"+
		"c\2\2\u00f7\u00f8\7e\2\2\u00f8\u00f9\7t\2\2\u00f9\u00fa\7q\2\2\u00fa\64"+
		"\3\2\2\2\u00fb\u00fc\7/\2\2\u00fc\u00fd\7@\2\2\u00fd\u00fe\7@\2\2\u00fe"+
		"\66\3\2\2\2\u00ff\u0100\7?\2\2\u0100\u0101\7@\2\2\u01018\3\2\2\2\u0102"+
		"\u0103\7*\2\2\u0103:\3\2\2\2\u0104\u0105\7+\2\2\u0105<\3\2\2\2\u0106\u0107"+
		"\7>\2\2\u0107>\3\2\2\2\u0108\u0109\7@\2\2\u0109@\3\2\2\2\u010a\u010b\7"+
		">\2\2\u010b\u010c\7?\2\2\u010cB\3\2\2\2\u010d\u010e\7@\2\2\u010e\u010f"+
		"\7?\2\2\u010fD\3\2\2\2\u0110\u0111\7.\2\2\u0111F\3\2\2\2\u0112\u0113\7"+
		"<\2\2\u0113H\3\2\2\2\u0114\u0115\7\u0080\2\2\u0115J\3\2\2\2\u0116\u0117"+
		"\7H\2\2\u0117\u0118\7c\2\2\u0118\u0119\7n\2\2\u0119\u011a\7u\2\2\u011a"+
		"\u011b\7g\2\2\u011bL\3\2\2\2\u011c\u011d\7$\2\2\u011d\u011e\5O(\2\u011e"+
		"\u011f\7$\2\2\u011fN\3\2\2\2\u0120\u0122\5Q)\2\u0121\u0120\3\2\2\2\u0122"+
		"\u0123\3\2\2\2\u0123\u0121\3\2\2\2\u0123\u0124\3\2\2\2\u0124P\3\2\2\2"+
		"\u0125\u0126\t\2\2\2\u0126R\3\2\2\2\u0127\u0129\t\3\2\2\u0128\u0127\3"+
		"\2\2\2\u0129\u012a\3\2\2\2\u012a\u0128\3\2\2\2\u012a\u012b\3\2\2\2\u012b"+
		"\u012c\3\2\2\2\u012c\u012d\b*\2\2\u012dT\3\2\2\2\u012e\u012f\7\61\2\2"+
		"\u012f\u0130\7,\2\2\u0130\u0134\3\2\2\2\u0131\u0133\13\2\2\2\u0132\u0131"+
		"\3\2\2\2\u0133\u0136\3\2\2\2\u0134\u0135\3\2\2\2\u0134\u0132\3\2\2\2\u0135"+
		"\u0137\3\2\2\2\u0136\u0134\3\2\2\2\u0137\u0138\7,\2\2\u0138\u0139\7\61"+
		"\2\2\u0139\u013a\3\2\2\2\u013a\u013b\b+\3\2\u013bV\3\2\2\2\u013c\u0140"+
		"\7%\2\2\u013d\u013f\n\4\2\2\u013e\u013d\3\2\2\2\u013f\u0142\3\2\2\2\u0140"+
		"\u013e\3\2\2\2\u0140\u0141\3\2\2\2\u0141\u0143\3\2\2\2\u0142\u0140\3\2"+
		"\2\2\u0143\u0144\b,\3\2\u0144X\3\2\2\2\7\2\u0123\u012a\u0134\u0140\4\2"+
		"\3\2\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}