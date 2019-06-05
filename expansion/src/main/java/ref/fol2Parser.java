// Generated from ref/fol2.g4 by ANTLR 4.7.1
package ref;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class fol2Parser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, IN=8, FOR=9, VAR=10, 
		BIND=11, LPAR=12, RPAR=13, LT=14, GT=15, STRING=16, WS=17, COMMENT=18, 
		LINE_COMMENT=19, VALUE=20;
	public static final int
		RULE_expansion = 0, RULE_commands = 1, RULE_fol_two = 2, RULE_stringlist = 3, 
		RULE_expansion_opts = 4;
	public static final String[] ruleNames = {
		"expansion", "commands", "fol_two", "stringlist", "expansion_opts"
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

	@Override
	public String getGrammarFileName() { return "fol2.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public fol2Parser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ExpansionContext extends ParserRuleContext {
		public List<CommandsContext> commands() {
			return getRuleContexts(CommandsContext.class);
		}
		public CommandsContext commands(int i) {
			return getRuleContext(CommandsContext.class,i);
		}
		public ExpansionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expansion; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof fol2Listener ) ((fol2Listener)listener).enterExpansion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof fol2Listener ) ((fol2Listener)listener).exitExpansion(this);
		}
	}

	public final ExpansionContext expansion() throws RecognitionException {
		ExpansionContext _localctx = new ExpansionContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_expansion);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(15);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(10);
					commands();
					setState(11);
					match(T__0);
					}
					} 
				}
				setState(17);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(18);
			commands();
			setState(19);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CommandsContext extends ParserRuleContext {
		public TerminalNode BIND() { return getToken(fol2Parser.BIND, 0); }
		public TerminalNode FOR() { return getToken(fol2Parser.FOR, 0); }
		public TerminalNode LPAR() { return getToken(fol2Parser.LPAR, 0); }
		public TerminalNode RPAR() { return getToken(fol2Parser.RPAR, 0); }
		public List<Fol_twoContext> fol_two() {
			return getRuleContexts(Fol_twoContext.class);
		}
		public Fol_twoContext fol_two(int i) {
			return getRuleContext(Fol_twoContext.class,i);
		}
		public List<Expansion_optsContext> expansion_opts() {
			return getRuleContexts(Expansion_optsContext.class);
		}
		public Expansion_optsContext expansion_opts(int i) {
			return getRuleContext(Expansion_optsContext.class,i);
		}
		public CommandsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_commands; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof fol2Listener ) ((fol2Listener)listener).enterCommands(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof fol2Listener ) ((fol2Listener)listener).exitCommands(this);
		}
	}

	public final CommandsContext commands() throws RecognitionException {
		CommandsContext _localctx = new CommandsContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_commands);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(21);
			match(BIND);
			setState(23); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(22);
				fol_two();
				}
				}
				setState(25); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==STRING );
			setState(27);
			match(FOR);
			setState(28);
			match(LPAR);
			setState(32);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VAR) | (1L << BIND) | (1L << VALUE))) != 0)) {
				{
				{
				setState(29);
				expansion_opts();
				}
				}
				setState(34);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(35);
			match(RPAR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fol_twoContext extends ParserRuleContext {
		public Token variable;
		public TerminalNode IN() { return getToken(fol2Parser.IN, 0); }
		public StringlistContext stringlist() {
			return getRuleContext(StringlistContext.class,0);
		}
		public TerminalNode STRING() { return getToken(fol2Parser.STRING, 0); }
		public Fol_twoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fol_two; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof fol2Listener ) ((fol2Listener)listener).enterFol_two(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof fol2Listener ) ((fol2Listener)listener).exitFol_two(this);
		}
	}

	public final Fol_twoContext fol_two() throws RecognitionException {
		Fol_twoContext _localctx = new Fol_twoContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_fol_two);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(37);
			((Fol_twoContext)_localctx).variable = match(STRING);
			setState(38);
			match(IN);
			setState(39);
			match(T__2);
			setState(40);
			stringlist();
			setState(41);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StringlistContext extends ParserRuleContext {
		public List<TerminalNode> VALUE() { return getTokens(fol2Parser.VALUE); }
		public TerminalNode VALUE(int i) {
			return getToken(fol2Parser.VALUE, i);
		}
		public StringlistContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringlist; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof fol2Listener ) ((fol2Listener)listener).enterStringlist(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof fol2Listener ) ((fol2Listener)listener).exitStringlist(this);
		}
	}

	public final StringlistContext stringlist() throws RecognitionException {
		StringlistContext _localctx = new StringlistContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_stringlist);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(47);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(43);
					match(VALUE);
					setState(44);
					match(T__4);
					}
					} 
				}
				setState(49);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			setState(50);
			match(VALUE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Expansion_optsContext extends ParserRuleContext {
		public TerminalNode VAR() { return getToken(fol2Parser.VAR, 0); }
		public TerminalNode VALUE() { return getToken(fol2Parser.VALUE, 0); }
		public CommandsContext commands() {
			return getRuleContext(CommandsContext.class,0);
		}
		public Expansion_optsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expansion_opts; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof fol2Listener ) ((fol2Listener)listener).enterExpansion_opts(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof fol2Listener ) ((fol2Listener)listener).exitExpansion_opts(this);
		}
	}

	public final Expansion_optsContext expansion_opts() throws RecognitionException {
		Expansion_optsContext _localctx = new Expansion_optsContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_expansion_opts);
		try {
			setState(58);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VAR:
				enterOuterAlt(_localctx, 1);
				{
				setState(52);
				match(VAR);
				setState(53);
				match(T__5);
				setState(54);
				match(VALUE);
				setState(55);
				match(T__6);
				}
				break;
			case VALUE:
				enterOuterAlt(_localctx, 2);
				{
				setState(56);
				match(VALUE);
				}
				break;
			case BIND:
				enterOuterAlt(_localctx, 3);
				{
				setState(57);
				commands();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\26?\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\3\2\3\2\7\2\20\n\2\f\2\16\2\23\13\2\3\2"+
		"\3\2\3\2\3\3\3\3\6\3\32\n\3\r\3\16\3\33\3\3\3\3\3\3\7\3!\n\3\f\3\16\3"+
		"$\13\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\7\5\60\n\5\f\5\16\5\63"+
		"\13\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\5\6=\n\6\3\6\2\2\7\2\4\6\b\n\2\2"+
		"\2?\2\21\3\2\2\2\4\27\3\2\2\2\6\'\3\2\2\2\b\61\3\2\2\2\n<\3\2\2\2\f\r"+
		"\5\4\3\2\r\16\7\3\2\2\16\20\3\2\2\2\17\f\3\2\2\2\20\23\3\2\2\2\21\17\3"+
		"\2\2\2\21\22\3\2\2\2\22\24\3\2\2\2\23\21\3\2\2\2\24\25\5\4\3\2\25\26\7"+
		"\4\2\2\26\3\3\2\2\2\27\31\7\r\2\2\30\32\5\6\4\2\31\30\3\2\2\2\32\33\3"+
		"\2\2\2\33\31\3\2\2\2\33\34\3\2\2\2\34\35\3\2\2\2\35\36\7\13\2\2\36\"\7"+
		"\16\2\2\37!\5\n\6\2 \37\3\2\2\2!$\3\2\2\2\" \3\2\2\2\"#\3\2\2\2#%\3\2"+
		"\2\2$\"\3\2\2\2%&\7\17\2\2&\5\3\2\2\2\'(\7\22\2\2()\7\n\2\2)*\7\5\2\2"+
		"*+\5\b\5\2+,\7\6\2\2,\7\3\2\2\2-.\7\26\2\2.\60\7\7\2\2/-\3\2\2\2\60\63"+
		"\3\2\2\2\61/\3\2\2\2\61\62\3\2\2\2\62\64\3\2\2\2\63\61\3\2\2\2\64\65\7"+
		"\26\2\2\65\t\3\2\2\2\66\67\7\f\2\2\678\7\b\2\289\7\26\2\29=\7\t\2\2:="+
		"\7\26\2\2;=\5\4\3\2<\66\3\2\2\2<:\3\2\2\2<;\3\2\2\2=\13\3\2\2\2\7\21\33"+
		"\"\61<";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}