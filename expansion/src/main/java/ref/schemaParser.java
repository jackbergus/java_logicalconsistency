// Generated from ref/schema.g4 by ANTLR 4.7.1
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
public class schemaParser extends Parser {
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
	public static final int
		RULE_program = 0, RULE_commands = 1, RULE_except = 2, RULE_intime = 3, 
		RULE_inspace = 4, RULE_setOptions = 5, RULE_paramDeclared = 6, RULE_stringlist = 7, 
		RULE_moreClauses = 8, RULE_clause = 9, RULE_forall = 10, RULE_exists = 11, 
		RULE_predicates = 12, RULE_angestrengend = 13, RULE_lbound = 14, RULE_ubound = 15;
	public static final String[] ruleNames = {
		"program", "commands", "except", "intime", "inspace", "setOptions", "paramDeclared", 
		"stringlist", "moreClauses", "clause", "forall", "exists", "predicates", 
		"angestrengend", "lbound", "ubound"
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

	@Override
	public String getGrammarFileName() { return "schema.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public schemaParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ProgramContext extends ParserRuleContext {
		public List<CommandsContext> commands() {
			return getRuleContexts(CommandsContext.class);
		}
		public CommandsContext commands(int i) {
			return getRuleContext(CommandsContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitProgram(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(37);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(32);
					commands();
					setState(33);
					match(T__0);
					}
					} 
				}
				setState(39);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(40);
			commands();
			setState(41);
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
		public CommandsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_commands; }
	 
		public CommandsContext() { }
		public void copyFrom(CommandsContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class Fdep_declareContext extends CommandsContext {
		public TerminalNode FD() { return getToken(schemaParser.FD, 0); }
		public TerminalNode FOR() { return getToken(schemaParser.FOR, 0); }
		public TerminalNode STRING() { return getToken(schemaParser.STRING, 0); }
		public TerminalNode DECLARE() { return getToken(schemaParser.DECLARE, 0); }
		public List<StringlistContext> stringlist() {
			return getRuleContexts(StringlistContext.class);
		}
		public StringlistContext stringlist(int i) {
			return getRuleContext(StringlistContext.class,i);
		}
		public TerminalNode MARROW() { return getToken(schemaParser.MARROW, 0); }
		public Fdep_declareContext(CommandsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterFdep_declare(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitFdep_declare(this);
		}
	}
	public static class Beginend_declareContext extends CommandsContext {
		public StringlistContext orig;
		public TerminalNode STRING() { return getToken(schemaParser.STRING, 0); }
		public TerminalNode LPAR() { return getToken(schemaParser.LPAR, 0); }
		public TerminalNode RPAR() { return getToken(schemaParser.RPAR, 0); }
		public IntimeContext intime() {
			return getRuleContext(IntimeContext.class,0);
		}
		public TerminalNode BEGIN() { return getToken(schemaParser.BEGIN, 0); }
		public TerminalNode END() { return getToken(schemaParser.END, 0); }
		public StringlistContext stringlist() {
			return getRuleContext(StringlistContext.class,0);
		}
		public TerminalNode UNIQUE() { return getToken(schemaParser.UNIQUE, 0); }
		public Beginend_declareContext(CommandsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterBeginend_declare(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitBeginend_declare(this);
		}
	}
	public static class RuleContext extends CommandsContext {
		public TerminalNode RULE() { return getToken(schemaParser.RULE, 0); }
		public List<MoreClausesContext> moreClauses() {
			return getRuleContexts(MoreClausesContext.class);
		}
		public MoreClausesContext moreClauses(int i) {
			return getRuleContext(MoreClausesContext.class,i);
		}
		public TerminalNode IMPLIES() { return getToken(schemaParser.IMPLIES, 0); }
		public TerminalNode BOT() { return getToken(schemaParser.BOT, 0); }
		public TerminalNode WITH() { return getToken(schemaParser.WITH, 0); }
		public List<PredicatesContext> predicates() {
			return getRuleContexts(PredicatesContext.class);
		}
		public PredicatesContext predicates(int i) {
			return getRuleContext(PredicatesContext.class,i);
		}
		public RuleContext(CommandsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitRule(this);
		}
	}
	public static class Macro_expandContext extends CommandsContext {
		public TerminalNode TRYEXPAND() { return getToken(schemaParser.TRYEXPAND, 0); }
		public StringlistContext stringlist() {
			return getRuleContext(StringlistContext.class,0);
		}
		public TerminalNode FOR() { return getToken(schemaParser.FOR, 0); }
		public TerminalNode RELATION() { return getToken(schemaParser.RELATION, 0); }
		public TerminalNode EVENT() { return getToken(schemaParser.EVENT, 0); }
		public Macro_expandContext(CommandsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterMacro_expand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitMacro_expand(this);
		}
	}
	public static class Macro_definitionContext extends CommandsContext {
		public TerminalNode MACRO() { return getToken(schemaParser.MACRO, 0); }
		public List<TerminalNode> STRING() { return getTokens(schemaParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(schemaParser.STRING, i);
		}
		public TerminalNode LPAR() { return getToken(schemaParser.LPAR, 0); }
		public StringlistContext stringlist() {
			return getRuleContext(StringlistContext.class,0);
		}
		public TerminalNode RPAR() { return getToken(schemaParser.RPAR, 0); }
		public TerminalNode DECLARE() { return getToken(schemaParser.DECLARE, 0); }
		public TerminalNode COMMA() { return getToken(schemaParser.COMMA, 0); }
		public TerminalNode RULE() { return getToken(schemaParser.RULE, 0); }
		public List<MoreClausesContext> moreClauses() {
			return getRuleContexts(MoreClausesContext.class);
		}
		public MoreClausesContext moreClauses(int i) {
			return getRuleContext(MoreClausesContext.class,i);
		}
		public TerminalNode IMPLIES() { return getToken(schemaParser.IMPLIES, 0); }
		public TerminalNode BOT() { return getToken(schemaParser.BOT, 0); }
		public TerminalNode WITH() { return getToken(schemaParser.WITH, 0); }
		public List<PredicatesContext> predicates() {
			return getRuleContexts(PredicatesContext.class);
		}
		public PredicatesContext predicates(int i) {
			return getRuleContext(PredicatesContext.class,i);
		}
		public Macro_definitionContext(CommandsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterMacro_definition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitMacro_definition(this);
		}
	}
	public static class Transfer_macroContext extends CommandsContext {
		public StringlistContext orig;
		public StringlistContext dest;
		public TerminalNode TRANSFER() { return getToken(schemaParser.TRANSFER, 0); }
		public TerminalNode STRING() { return getToken(schemaParser.STRING, 0); }
		public TerminalNode LPAR() { return getToken(schemaParser.LPAR, 0); }
		public TerminalNode RPAR() { return getToken(schemaParser.RPAR, 0); }
		public IntimeContext intime() {
			return getRuleContext(IntimeContext.class,0);
		}
		public TerminalNode DECLARE() { return getToken(schemaParser.DECLARE, 0); }
		public List<StringlistContext> stringlist() {
			return getRuleContexts(StringlistContext.class);
		}
		public StringlistContext stringlist(int i) {
			return getRuleContext(StringlistContext.class,i);
		}
		public Transfer_macroContext(CommandsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterTransfer_macro(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitTransfer_macro(this);
		}
	}
	public static class Rel_delcareContext extends CommandsContext {
		public TerminalNode STRING() { return getToken(schemaParser.STRING, 0); }
		public TerminalNode LPAR() { return getToken(schemaParser.LPAR, 0); }
		public StringlistContext stringlist() {
			return getRuleContext(StringlistContext.class,0);
		}
		public TerminalNode RPAR() { return getToken(schemaParser.RPAR, 0); }
		public TerminalNode RELATION() { return getToken(schemaParser.RELATION, 0); }
		public TerminalNode EVENT() { return getToken(schemaParser.EVENT, 0); }
		public Rel_delcareContext(CommandsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterRel_delcare(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitRel_delcare(this);
		}
	}
	public static class EnexistsContext extends CommandsContext {
		public TerminalNode ENTITY() { return getToken(schemaParser.ENTITY, 0); }
		public TerminalNode EXIST() { return getToken(schemaParser.EXIST, 0); }
		public IntimeContext intime() {
			return getRuleContext(IntimeContext.class,0);
		}
		public StringlistContext stringlist() {
			return getRuleContext(StringlistContext.class,0);
		}
		public ExceptContext except() {
			return getRuleContext(ExceptContext.class,0);
		}
		public InspaceContext inspace() {
			return getRuleContext(InspaceContext.class,0);
		}
		public EnexistsContext(CommandsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterEnexists(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitEnexists(this);
		}
	}

	public final CommandsContext commands() throws RecognitionException {
		CommandsContext _localctx = new CommandsContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_commands);
		int _la;
		try {
			setState(132);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case RELATION:
			case EVENT:
				_localctx = new Rel_delcareContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(43);
				_la = _input.LA(1);
				if ( !(_la==RELATION || _la==EVENT) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(44);
				match(STRING);
				setState(45);
				match(LPAR);
				setState(46);
				stringlist();
				setState(47);
				match(RPAR);
				}
				break;
			case FD:
				_localctx = new Fdep_declareContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(49);
				match(FD);
				setState(50);
				match(FOR);
				setState(51);
				match(STRING);
				setState(52);
				match(DECLARE);
				setState(53);
				stringlist();
				setState(54);
				match(MARROW);
				setState(55);
				stringlist();
				}
				break;
			case RULE:
				_localctx = new RuleContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(57);
				match(RULE);
				setState(58);
				moreClauses();
				setState(65);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WITH) {
					{
					setState(59);
					match(WITH);
					setState(61); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(60);
						predicates();
						}
						}
						setState(63); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==STRING );
					}
				}

				setState(67);
				match(IMPLIES);
				setState(70);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__4:
				case T__5:
				case NEG:
				case STRING:
					{
					setState(68);
					moreClauses();
					}
					break;
				case BOT:
					{
					setState(69);
					match(BOT);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case ENTITY:
				_localctx = new EnexistsContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(72);
				match(ENTITY);
				setState(73);
				match(EXIST);
				setState(75);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VALUE || _la==STRING) {
					{
					setState(74);
					stringlist();
					}
				}

				setState(78);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EXCEPT) {
					{
					setState(77);
					except();
					}
				}

				setState(80);
				intime();
				setState(82);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__3) {
					{
					setState(81);
					inspace();
					}
				}

				}
				break;
			case MACRO:
				_localctx = new Macro_definitionContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(84);
				match(MACRO);
				setState(85);
				match(STRING);
				setState(86);
				match(LPAR);
				setState(87);
				stringlist();
				setState(88);
				match(RPAR);
				setState(89);
				match(DECLARE);
				setState(90);
				match(STRING);
				setState(91);
				match(COMMA);
				setState(92);
				match(RULE);
				setState(93);
				moreClauses();
				setState(100);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WITH) {
					{
					setState(94);
					match(WITH);
					setState(96); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(95);
						predicates();
						}
						}
						setState(98); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==STRING );
					}
				}

				setState(102);
				match(IMPLIES);
				setState(105);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__4:
				case T__5:
				case NEG:
				case STRING:
					{
					setState(103);
					moreClauses();
					}
					break;
				case BOT:
					{
					setState(104);
					match(BOT);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case TRYEXPAND:
				_localctx = new Macro_expandContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(107);
				match(TRYEXPAND);
				setState(108);
				stringlist();
				setState(111);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FOR) {
					{
					setState(109);
					match(FOR);
					setState(110);
					_la = _input.LA(1);
					if ( !(_la==RELATION || _la==EVENT) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				}
				break;
			case UNIQUE:
			case BEGIN:
			case END:
				_localctx = new Beginend_declareContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(114);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==UNIQUE) {
					{
					setState(113);
					match(UNIQUE);
					}
				}

				setState(116);
				_la = _input.LA(1);
				if ( !(_la==BEGIN || _la==END) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(117);
				match(STRING);
				setState(118);
				match(LPAR);
				setState(119);
				((Beginend_declareContext)_localctx).orig = stringlist();
				setState(120);
				match(RPAR);
				setState(121);
				intime();
				}
				break;
			case TRANSFER:
				_localctx = new Transfer_macroContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(123);
				match(TRANSFER);
				setState(124);
				match(STRING);
				setState(125);
				match(LPAR);
				setState(126);
				((Transfer_macroContext)_localctx).orig = stringlist();
				setState(127);
				match(RPAR);
				setState(128);
				intime();
				setState(129);
				match(DECLARE);
				setState(130);
				((Transfer_macroContext)_localctx).dest = stringlist();
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

	public static class ExceptContext extends ParserRuleContext {
		public TerminalNode EXCEPT() { return getToken(schemaParser.EXCEPT, 0); }
		public StringlistContext stringlist() {
			return getRuleContext(StringlistContext.class,0);
		}
		public ExceptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_except; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterExcept(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitExcept(this);
		}
	}

	public final ExceptContext except() throws RecognitionException {
		ExceptContext _localctx = new ExceptContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_except);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(134);
			match(EXCEPT);
			setState(135);
			stringlist();
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

	public static class IntimeContext extends ParserRuleContext {
		public StringlistContext stringlist() {
			return getRuleContext(StringlistContext.class,0);
		}
		public IntimeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_intime; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterIntime(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitIntime(this);
		}
	}

	public final IntimeContext intime() throws RecognitionException {
		IntimeContext _localctx = new IntimeContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_intime);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(137);
			match(T__2);
			setState(138);
			stringlist();
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

	public static class InspaceContext extends ParserRuleContext {
		public StringlistContext stringlist() {
			return getRuleContext(StringlistContext.class,0);
		}
		public InspaceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inspace; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterInspace(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitInspace(this);
		}
	}

	public final InspaceContext inspace() throws RecognitionException {
		InspaceContext _localctx = new InspaceContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_inspace);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(140);
			match(T__3);
			setState(141);
			stringlist();
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

	public static class SetOptionsContext extends ParserRuleContext {
		public SetOptionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_setOptions; }
	 
		public SetOptionsContext() { }
		public void copyFrom(SetOptionsContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class Set_relationContext extends SetOptionsContext {
		public TerminalNode RELATION() { return getToken(schemaParser.RELATION, 0); }
		public List<TerminalNode> STRING() { return getTokens(schemaParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(schemaParser.STRING, i);
		}
		public TerminalNode LPAR() { return getToken(schemaParser.LPAR, 0); }
		public TerminalNode COMMA() { return getToken(schemaParser.COMMA, 0); }
		public TerminalNode RPAR() { return getToken(schemaParser.RPAR, 0); }
		public Set_relationContext(SetOptionsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterSet_relation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitSet_relation(this);
		}
	}
	public static class Set_entityContext extends SetOptionsContext {
		public TerminalNode ENTITY() { return getToken(schemaParser.ENTITY, 0); }
		public TerminalNode STRING() { return getToken(schemaParser.STRING, 0); }
		public Set_entityContext(SetOptionsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterSet_entity(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitSet_entity(this);
		}
	}
	public static class Set_eventContext extends SetOptionsContext {
		public TerminalNode EVENT() { return getToken(schemaParser.EVENT, 0); }
		public TerminalNode STRING() { return getToken(schemaParser.STRING, 0); }
		public TerminalNode LPAR() { return getToken(schemaParser.LPAR, 0); }
		public StringlistContext stringlist() {
			return getRuleContext(StringlistContext.class,0);
		}
		public TerminalNode RPAR() { return getToken(schemaParser.RPAR, 0); }
		public Set_eventContext(SetOptionsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterSet_event(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitSet_event(this);
		}
	}

	public final SetOptionsContext setOptions() throws RecognitionException {
		SetOptionsContext _localctx = new SetOptionsContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_setOptions);
		int _la;
		try {
			setState(162);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case RELATION:
				_localctx = new Set_relationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(143);
				match(RELATION);
				setState(150);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==STRING) {
					{
					setState(144);
					match(STRING);
					setState(145);
					match(LPAR);
					setState(146);
					match(STRING);
					setState(147);
					match(COMMA);
					setState(148);
					match(STRING);
					setState(149);
					match(RPAR);
					}
				}

				}
				break;
			case EVENT:
				_localctx = new Set_eventContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(152);
				match(EVENT);
				setState(158);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==STRING) {
					{
					setState(153);
					match(STRING);
					setState(154);
					match(LPAR);
					setState(155);
					stringlist();
					setState(156);
					match(RPAR);
					}
				}

				}
				break;
			case ENTITY:
				_localctx = new Set_entityContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(160);
				match(ENTITY);
				setState(161);
				match(STRING);
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

	public static class ParamDeclaredContext extends ParserRuleContext {
		public TerminalNode FOR() { return getToken(schemaParser.FOR, 0); }
		public TerminalNode STRING() { return getToken(schemaParser.STRING, 0); }
		public ParamDeclaredContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_paramDeclared; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterParamDeclared(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitParamDeclared(this);
		}
	}

	public final ParamDeclaredContext paramDeclared() throws RecognitionException {
		ParamDeclaredContext _localctx = new ParamDeclaredContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_paramDeclared);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(164);
			match(FOR);
			setState(165);
			match(STRING);
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
		public List<AngestrengendContext> angestrengend() {
			return getRuleContexts(AngestrengendContext.class);
		}
		public AngestrengendContext angestrengend(int i) {
			return getRuleContext(AngestrengendContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(schemaParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(schemaParser.COMMA, i);
		}
		public StringlistContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringlist; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterStringlist(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitStringlist(this);
		}
	}

	public final StringlistContext stringlist() throws RecognitionException {
		StringlistContext _localctx = new StringlistContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_stringlist);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(172);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(167);
					angestrengend();
					setState(168);
					match(COMMA);
					}
					} 
				}
				setState(174);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			}
			setState(175);
			angestrengend();
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

	public static class MoreClausesContext extends ParserRuleContext {
		public List<ClauseContext> clause() {
			return getRuleContexts(ClauseContext.class);
		}
		public ClauseContext clause(int i) {
			return getRuleContext(ClauseContext.class,i);
		}
		public MoreClausesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_moreClauses; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterMoreClauses(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitMoreClauses(this);
		}
	}

	public final MoreClausesContext moreClauses() throws RecognitionException {
		MoreClausesContext _localctx = new MoreClausesContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_moreClauses);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(178); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(177);
				clause();
				}
				}
				setState(180); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__4) | (1L << T__5) | (1L << NEG) | (1L << STRING))) != 0) );
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

	public static class ClauseContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(schemaParser.STRING, 0); }
		public TerminalNode LPAR() { return getToken(schemaParser.LPAR, 0); }
		public StringlistContext stringlist() {
			return getRuleContext(StringlistContext.class,0);
		}
		public TerminalNode RPAR() { return getToken(schemaParser.RPAR, 0); }
		public List<ForallContext> forall() {
			return getRuleContexts(ForallContext.class);
		}
		public ForallContext forall(int i) {
			return getRuleContext(ForallContext.class,i);
		}
		public List<ExistsContext> exists() {
			return getRuleContexts(ExistsContext.class);
		}
		public ExistsContext exists(int i) {
			return getRuleContext(ExistsContext.class,i);
		}
		public TerminalNode NEG() { return getToken(schemaParser.NEG, 0); }
		public ClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitClause(this);
		}
	}

	public final ClauseContext clause() throws RecognitionException {
		ClauseContext _localctx = new ClauseContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(185);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(182);
				forall();
				}
				}
				setState(187);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(191);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(188);
				exists();
				}
				}
				setState(193);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(195);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NEG) {
				{
				setState(194);
				match(NEG);
				}
			}

			setState(197);
			match(STRING);
			setState(198);
			match(LPAR);
			setState(199);
			stringlist();
			setState(200);
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

	public static class ForallContext extends ParserRuleContext {
		public List<TerminalNode> STRING() { return getTokens(schemaParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(schemaParser.STRING, i);
		}
		public TerminalNode TYPE() { return getToken(schemaParser.TYPE, 0); }
		public TerminalNode WITH() { return getToken(schemaParser.WITH, 0); }
		public List<PredicatesContext> predicates() {
			return getRuleContexts(PredicatesContext.class);
		}
		public PredicatesContext predicates(int i) {
			return getRuleContext(PredicatesContext.class,i);
		}
		public ForallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forall; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterForall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitForall(this);
		}
	}

	public final ForallContext forall() throws RecognitionException {
		ForallContext _localctx = new ForallContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_forall);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(202);
			match(T__4);
			setState(203);
			match(STRING);
			setState(204);
			match(TYPE);
			setState(205);
			match(STRING);
			setState(212);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WITH) {
				{
				setState(206);
				match(WITH);
				setState(208); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(207);
					predicates();
					}
					}
					setState(210); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==STRING );
				}
			}

			setState(214);
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

	public static class ExistsContext extends ParserRuleContext {
		public List<TerminalNode> STRING() { return getTokens(schemaParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(schemaParser.STRING, i);
		}
		public TerminalNode TYPE() { return getToken(schemaParser.TYPE, 0); }
		public TerminalNode WITH() { return getToken(schemaParser.WITH, 0); }
		public List<PredicatesContext> predicates() {
			return getRuleContexts(PredicatesContext.class);
		}
		public PredicatesContext predicates(int i) {
			return getRuleContext(PredicatesContext.class,i);
		}
		public ExistsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exists; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterExists(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitExists(this);
		}
	}

	public final ExistsContext exists() throws RecognitionException {
		ExistsContext _localctx = new ExistsContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_exists);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(216);
			match(T__5);
			setState(217);
			match(STRING);
			setState(218);
			match(TYPE);
			setState(219);
			match(STRING);
			setState(226);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WITH) {
				{
				setState(220);
				match(WITH);
				setState(222); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(221);
					predicates();
					}
					}
					setState(224); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==STRING );
				}
			}

			setState(228);
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

	public static class PredicatesContext extends ParserRuleContext {
		public PredicatesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicates; }
	 
		public PredicatesContext() { }
		public void copyFrom(PredicatesContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class QuantificationContext extends PredicatesContext {
		public TerminalNode STRING() { return getToken(schemaParser.STRING, 0); }
		public LboundContext lbound() {
			return getRuleContext(LboundContext.class,0);
		}
		public UboundContext ubound() {
			return getRuleContext(UboundContext.class,0);
		}
		public QuantificationContext(PredicatesContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterQuantification(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitQuantification(this);
		}
	}
	public static class Var_notnullContext extends PredicatesContext {
		public TerminalNode STRING() { return getToken(schemaParser.STRING, 0); }
		public Var_notnullContext(PredicatesContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterVar_notnull(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitVar_notnull(this);
		}
	}

	public final PredicatesContext predicates() throws RecognitionException {
		PredicatesContext _localctx = new PredicatesContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_predicates);
		int _la;
		try {
			setState(239);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				_localctx = new Var_notnullContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(230);
				match(STRING);
				setState(231);
				match(T__6);
				}
				break;
			case 2:
				_localctx = new QuantificationContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(233);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
				case 1:
					{
					setState(232);
					lbound();
					}
					break;
				}
				setState(235);
				match(STRING);
				setState(237);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==GT || _la==GEQ) {
					{
					setState(236);
					ubound();
					}
				}

				}
				break;
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

	public static class AngestrengendContext extends ParserRuleContext {
		public AngestrengendContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_angestrengend; }
	 
		public AngestrengendContext() { }
		public void copyFrom(AngestrengendContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class IsstringContext extends AngestrengendContext {
		public TerminalNode STRING() { return getToken(schemaParser.STRING, 0); }
		public IsstringContext(AngestrengendContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterIsstring(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitIsstring(this);
		}
	}
	public static class IsvalueContext extends AngestrengendContext {
		public TerminalNode VALUE() { return getToken(schemaParser.VALUE, 0); }
		public IsvalueContext(AngestrengendContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterIsvalue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitIsvalue(this);
		}
	}

	public final AngestrengendContext angestrengend() throws RecognitionException {
		AngestrengendContext _localctx = new AngestrengendContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_angestrengend);
		try {
			setState(243);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING:
				_localctx = new IsstringContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(241);
				match(STRING);
				}
				break;
			case VALUE:
				_localctx = new IsvalueContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(242);
				match(VALUE);
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

	public static class LboundContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(schemaParser.STRING, 0); }
		public TerminalNode LT() { return getToken(schemaParser.LT, 0); }
		public TerminalNode LEQ() { return getToken(schemaParser.LEQ, 0); }
		public LboundContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lbound; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterLbound(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitLbound(this);
		}
	}

	public final LboundContext lbound() throws RecognitionException {
		LboundContext _localctx = new LboundContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_lbound);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(245);
			match(STRING);
			setState(246);
			_la = _input.LA(1);
			if ( !(_la==LT || _la==LEQ) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
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

	public static class UboundContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(schemaParser.STRING, 0); }
		public TerminalNode GT() { return getToken(schemaParser.GT, 0); }
		public TerminalNode GEQ() { return getToken(schemaParser.GEQ, 0); }
		public UboundContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ubound; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).enterUbound(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof schemaListener ) ((schemaListener)listener).exitUbound(this);
		}
	}

	public final UboundContext ubound() throws RecognitionException {
		UboundContext _localctx = new UboundContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_ubound);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(248);
			_la = _input.LA(1);
			if ( !(_la==GT || _la==GEQ) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(249);
			match(STRING);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3,\u00fe\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\3\2\3\2\3"+
		"\2\7\2&\n\2\f\2\16\2)\13\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\6\3@\n\3\r\3\16\3A\5\3D\n\3\3"+
		"\3\3\3\3\3\5\3I\n\3\3\3\3\3\3\3\5\3N\n\3\3\3\5\3Q\n\3\3\3\3\3\5\3U\n\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\6\3c\n\3\r\3\16\3d\5"+
		"\3g\n\3\3\3\3\3\3\3\5\3l\n\3\3\3\3\3\3\3\3\3\5\3r\n\3\3\3\5\3u\n\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3\u0087"+
		"\n\3\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5"+
		"\7\u0099\n\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7\u00a1\n\7\3\7\3\7\5\7\u00a5\n"+
		"\7\3\b\3\b\3\b\3\t\3\t\3\t\7\t\u00ad\n\t\f\t\16\t\u00b0\13\t\3\t\3\t\3"+
		"\n\6\n\u00b5\n\n\r\n\16\n\u00b6\3\13\7\13\u00ba\n\13\f\13\16\13\u00bd"+
		"\13\13\3\13\7\13\u00c0\n\13\f\13\16\13\u00c3\13\13\3\13\5\13\u00c6\n\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\6\f\u00d3\n\f\r\f\16"+
		"\f\u00d4\5\f\u00d7\n\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\6\r\u00e1\n\r\r"+
		"\r\16\r\u00e2\5\r\u00e5\n\r\3\r\3\r\3\16\3\16\3\16\5\16\u00ec\n\16\3\16"+
		"\3\16\5\16\u00f0\n\16\5\16\u00f2\n\16\3\17\3\17\5\17\u00f6\n\17\3\20\3"+
		"\20\3\20\3\21\3\21\3\21\3\21\2\2\22\2\4\6\b\n\f\16\20\22\24\26\30\32\34"+
		"\36 \2\6\4\2\f\f\16\16\3\2\27\30\4\2  \"\"\4\2!!##\2\u0111\2\'\3\2\2\2"+
		"\4\u0086\3\2\2\2\6\u0088\3\2\2\2\b\u008b\3\2\2\2\n\u008e\3\2\2\2\f\u00a4"+
		"\3\2\2\2\16\u00a6\3\2\2\2\20\u00ae\3\2\2\2\22\u00b4\3\2\2\2\24\u00bb\3"+
		"\2\2\2\26\u00cc\3\2\2\2\30\u00da\3\2\2\2\32\u00f1\3\2\2\2\34\u00f5\3\2"+
		"\2\2\36\u00f7\3\2\2\2 \u00fa\3\2\2\2\"#\5\4\3\2#$\7\3\2\2$&\3\2\2\2%\""+
		"\3\2\2\2&)\3\2\2\2\'%\3\2\2\2\'(\3\2\2\2(*\3\2\2\2)\'\3\2\2\2*+\5\4\3"+
		"\2+,\7\4\2\2,\3\3\2\2\2-.\t\2\2\2./\7)\2\2/\60\7\36\2\2\60\61\5\20\t\2"+
		"\61\62\7\37\2\2\62\u0087\3\2\2\2\63\64\7\21\2\2\64\65\7\23\2\2\65\66\7"+
		")\2\2\66\67\7\24\2\2\678\5\20\t\289\7\34\2\29:\5\20\t\2:\u0087\3\2\2\2"+
		";<\7\22\2\2<C\5\22\n\2=?\7\26\2\2>@\5\32\16\2?>\3\2\2\2@A\3\2\2\2A?\3"+
		"\2\2\2AB\3\2\2\2BD\3\2\2\2C=\3\2\2\2CD\3\2\2\2DE\3\2\2\2EH\7\35\2\2FI"+
		"\5\22\n\2GI\7\'\2\2HF\3\2\2\2HG\3\2\2\2I\u0087\3\2\2\2JK\7\20\2\2KM\7"+
		"\31\2\2LN\5\20\t\2ML\3\2\2\2MN\3\2\2\2NP\3\2\2\2OQ\5\6\4\2PO\3\2\2\2P"+
		"Q\3\2\2\2QR\3\2\2\2RT\5\b\5\2SU\5\n\6\2TS\3\2\2\2TU\3\2\2\2U\u0087\3\2"+
		"\2\2VW\7\33\2\2WX\7)\2\2XY\7\36\2\2YZ\5\20\t\2Z[\7\37\2\2[\\\7\24\2\2"+
		"\\]\7)\2\2]^\7$\2\2^_\7\22\2\2_f\5\22\n\2`b\7\26\2\2ac\5\32\16\2ba\3\2"+
		"\2\2cd\3\2\2\2db\3\2\2\2de\3\2\2\2eg\3\2\2\2f`\3\2\2\2fg\3\2\2\2gh\3\2"+
		"\2\2hk\7\35\2\2il\5\22\n\2jl\7\'\2\2ki\3\2\2\2kj\3\2\2\2l\u0087\3\2\2"+
		"\2mn\7\13\2\2nq\5\20\t\2op\7\23\2\2pr\t\2\2\2qo\3\2\2\2qr\3\2\2\2r\u0087"+
		"\3\2\2\2su\7\17\2\2ts\3\2\2\2tu\3\2\2\2uv\3\2\2\2vw\t\3\2\2wx\7)\2\2x"+
		"y\7\36\2\2yz\5\20\t\2z{\7\37\2\2{|\5\b\5\2|\u0087\3\2\2\2}~\7\n\2\2~\177"+
		"\7)\2\2\177\u0080\7\36\2\2\u0080\u0081\5\20\t\2\u0081\u0082\7\37\2\2\u0082"+
		"\u0083\5\b\5\2\u0083\u0084\7\24\2\2\u0084\u0085\5\20\t\2\u0085\u0087\3"+
		"\2\2\2\u0086-\3\2\2\2\u0086\63\3\2\2\2\u0086;\3\2\2\2\u0086J\3\2\2\2\u0086"+
		"V\3\2\2\2\u0086m\3\2\2\2\u0086t\3\2\2\2\u0086}\3\2\2\2\u0087\5\3\2\2\2"+
		"\u0088\u0089\7\32\2\2\u0089\u008a\5\20\t\2\u008a\7\3\2\2\2\u008b\u008c"+
		"\7\5\2\2\u008c\u008d\5\20\t\2\u008d\t\3\2\2\2\u008e\u008f\7\6\2\2\u008f"+
		"\u0090\5\20\t\2\u0090\13\3\2\2\2\u0091\u0098\7\f\2\2\u0092\u0093\7)\2"+
		"\2\u0093\u0094\7\36\2\2\u0094\u0095\7)\2\2\u0095\u0096\7$\2\2\u0096\u0097"+
		"\7)\2\2\u0097\u0099\7\37\2\2\u0098\u0092\3\2\2\2\u0098\u0099\3\2\2\2\u0099"+
		"\u00a5\3\2\2\2\u009a\u00a0\7\16\2\2\u009b\u009c\7)\2\2\u009c\u009d\7\36"+
		"\2\2\u009d\u009e\5\20\t\2\u009e\u009f\7\37\2\2\u009f\u00a1\3\2\2\2\u00a0"+
		"\u009b\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1\u00a5\3\2\2\2\u00a2\u00a3\7\20"+
		"\2\2\u00a3\u00a5\7)\2\2\u00a4\u0091\3\2\2\2\u00a4\u009a\3\2\2\2\u00a4"+
		"\u00a2\3\2\2\2\u00a5\r\3\2\2\2\u00a6\u00a7\7\23\2\2\u00a7\u00a8\7)\2\2"+
		"\u00a8\17\3\2\2\2\u00a9\u00aa\5\34\17\2\u00aa\u00ab\7$\2\2\u00ab\u00ad"+
		"\3\2\2\2\u00ac\u00a9\3\2\2\2\u00ad\u00b0\3\2\2\2\u00ae\u00ac\3\2\2\2\u00ae"+
		"\u00af\3\2\2\2\u00af\u00b1\3\2\2\2\u00b0\u00ae\3\2\2\2\u00b1\u00b2\5\34"+
		"\17\2\u00b2\21\3\2\2\2\u00b3\u00b5\5\24\13\2\u00b4\u00b3\3\2\2\2\u00b5"+
		"\u00b6\3\2\2\2\u00b6\u00b4\3\2\2\2\u00b6\u00b7\3\2\2\2\u00b7\23\3\2\2"+
		"\2\u00b8\u00ba\5\26\f\2\u00b9\u00b8\3\2\2\2\u00ba\u00bd\3\2\2\2\u00bb"+
		"\u00b9\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc\u00c1\3\2\2\2\u00bd\u00bb\3\2"+
		"\2\2\u00be\u00c0\5\30\r\2\u00bf\u00be\3\2\2\2\u00c0\u00c3\3\2\2\2\u00c1"+
		"\u00bf\3\2\2\2\u00c1\u00c2\3\2\2\2\u00c2\u00c5\3\2\2\2\u00c3\u00c1\3\2"+
		"\2\2\u00c4\u00c6\7&\2\2\u00c5\u00c4\3\2\2\2\u00c5\u00c6\3\2\2\2\u00c6"+
		"\u00c7\3\2\2\2\u00c7\u00c8\7)\2\2\u00c8\u00c9\7\36\2\2\u00c9\u00ca\5\20"+
		"\t\2\u00ca\u00cb\7\37\2\2\u00cb\25\3\2\2\2\u00cc\u00cd\7\7\2\2\u00cd\u00ce"+
		"\7)\2\2\u00ce\u00cf\7%\2\2\u00cf\u00d6\7)\2\2\u00d0\u00d2\7\26\2\2\u00d1"+
		"\u00d3\5\32\16\2\u00d2\u00d1\3\2\2\2\u00d3\u00d4\3\2\2\2\u00d4\u00d2\3"+
		"\2\2\2\u00d4\u00d5\3\2\2\2\u00d5\u00d7\3\2\2\2\u00d6\u00d0\3\2\2\2\u00d6"+
		"\u00d7\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8\u00d9\7\4\2\2\u00d9\27\3\2\2"+
		"\2\u00da\u00db\7\b\2\2\u00db\u00dc\7)\2\2\u00dc\u00dd\7%\2\2\u00dd\u00e4"+
		"\7)\2\2\u00de\u00e0\7\26\2\2\u00df\u00e1\5\32\16\2\u00e0\u00df\3\2\2\2"+
		"\u00e1\u00e2\3\2\2\2\u00e2\u00e0\3\2\2\2\u00e2\u00e3\3\2\2\2\u00e3\u00e5"+
		"\3\2\2\2\u00e4\u00de\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5\u00e6\3\2\2\2\u00e6"+
		"\u00e7\7\4\2\2\u00e7\31\3\2\2\2\u00e8\u00e9\7)\2\2\u00e9\u00f2\7\t\2\2"+
		"\u00ea\u00ec\5\36\20\2\u00eb\u00ea\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec\u00ed"+
		"\3\2\2\2\u00ed\u00ef\7)\2\2\u00ee\u00f0\5 \21\2\u00ef\u00ee\3\2\2\2\u00ef"+
		"\u00f0\3\2\2\2\u00f0\u00f2\3\2\2\2\u00f1\u00e8\3\2\2\2\u00f1\u00eb\3\2"+
		"\2\2\u00f2\33\3\2\2\2\u00f3\u00f6\7)\2\2\u00f4\u00f6\7(\2\2\u00f5\u00f3"+
		"\3\2\2\2\u00f5\u00f4\3\2\2\2\u00f6\35\3\2\2\2\u00f7\u00f8\7)\2\2\u00f8"+
		"\u00f9\t\4\2\2\u00f9\37\3\2\2\2\u00fa\u00fb\t\5\2\2\u00fb\u00fc\7)\2\2"+
		"\u00fc!\3\2\2\2\37\'ACHMPTdfkqt\u0086\u0098\u00a0\u00a4\u00ae\u00b6\u00bb"+
		"\u00c1\u00c5\u00d4\u00d6\u00e2\u00e4\u00eb\u00ef\u00f1\u00f5";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}