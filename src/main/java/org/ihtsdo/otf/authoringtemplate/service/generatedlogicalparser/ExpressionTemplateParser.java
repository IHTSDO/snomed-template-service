// Generated from ExpressionTemplate.txt by ANTLR 4.5.3
package org.ihtsdo.otf.authoringtemplate.service.generatedlogicalparser;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ExpressionTemplateParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		TAB=1, LF=2, CR=3, SPACE=4, EXCLAMATION=5, QUOTE=6, POUND=7, DOLLAR=8, 
		PERCENT=9, AMPERSAND=10, APOSTROPHE=11, LEFT_PAREN=12, RIGHT_PAREN=13, 
		ASTERISK=14, PLUS=15, COMMA=16, DASH=17, PERIOD=18, SLASH=19, ZERO=20, 
		ONE=21, TWO=22, THREE=23, FOUR=24, FIVE=25, SIX=26, SEVEN=27, EIGHT=28, 
		NINE=29, COLON=30, SEMICOLON=31, LESS_THAN=32, EQUALS=33, GREATER_THAN=34, 
		QUESTION=35, AT=36, CAP_A=37, CAP_B=38, CAP_C=39, CAP_D=40, CAP_E=41, 
		CAP_F=42, CAP_G=43, CAP_H=44, CAP_I=45, CAP_J=46, CAP_K=47, CAP_L=48, 
		CAP_M=49, CAP_N=50, CAP_O=51, CAP_P=52, CAP_Q=53, CAP_R=54, CAP_S=55, 
		CAP_T=56, CAP_U=57, CAP_V=58, CAP_W=59, CAP_X=60, CAP_Y=61, CAP_Z=62, 
		LEFT_BRACE=63, BACKSLASH=64, RIGHT_BRACE=65, CARAT=66, UNDERSCORE=67, 
		ACCENT=68, A=69, B=70, C=71, D=72, E=73, F=74, G=75, H=76, I=77, J=78, 
		K=79, L=80, M=81, N=82, O=83, P=84, Q=85, R=86, S=87, T=88, U=89, V=90, 
		W=91, X=92, Y=93, Z=94, LEFT_CURLY_BRACE=95, PIPE=96, RIGHT_CURLY_BRACE=97, 
		TILDE=98, U_0080=99, U_0081=100, U_0082=101, U_0083=102, U_0084=103, U_0085=104, 
		U_0086=105, U_0087=106, U_0088=107, U_0089=108, U_008A=109, U_008B=110, 
		U_008C=111, U_008D=112, U_008E=113, U_008F=114, U_0090=115, U_0091=116, 
		U_0092=117, U_0093=118, U_0094=119, U_0095=120, U_0096=121, U_0097=122, 
		U_0098=123, U_0099=124, U_009A=125, U_009B=126, U_009C=127, U_009D=128, 
		U_009E=129, U_009F=130, U_00A0=131, U_00A1=132, U_00A2=133, U_00A3=134, 
		U_00A4=135, U_00A5=136, U_00A6=137, U_00A7=138, U_00A8=139, U_00A9=140, 
		U_00AA=141, U_00AB=142, U_00AC=143, U_00AD=144, U_00AE=145, U_00AF=146, 
		U_00B0=147, U_00B1=148, U_00B2=149, U_00B3=150, U_00B4=151, U_00B5=152, 
		U_00B6=153, U_00B7=154, U_00B8=155, U_00B9=156, U_00BA=157, U_00BB=158, 
		U_00BC=159, U_00BD=160, U_00BE=161, U_00BF=162, U_00C2=163, U_00C3=164, 
		U_00C4=165, U_00C5=166, U_00C6=167, U_00C7=168, U_00C8=169, U_00C9=170, 
		U_00CA=171, U_00CB=172, U_00CC=173, U_00CD=174, U_00CE=175, U_00CF=176, 
		U_00D0=177, U_00D1=178, U_00D2=179, U_00D3=180, U_00D4=181, U_00D5=182, 
		U_00D6=183, U_00D7=184, U_00D8=185, U_00D9=186, U_00DA=187, U_00DB=188, 
		U_00DC=189, U_00DD=190, U_00DE=191, U_00DF=192, U_00E0=193, U_00E1=194, 
		U_00E2=195, U_00E3=196, U_00E4=197, U_00E5=198, U_00E6=199, U_00E7=200, 
		U_00E8=201, U_00E9=202, U_00EA=203, U_00EB=204, U_00EC=205, U_00ED=206, 
		U_00EE=207, U_00EF=208, U_00F0=209, U_00F1=210, U_00F2=211, U_00F3=212, 
		U_00F4=213;
	public static final int
		RULE_expressiontemplate = 0, RULE_subexpression = 1, RULE_definitionstatus = 2, 
		RULE_equivalentto = 3, RULE_subtypeof = 4, RULE_focusconcept = 5, RULE_conceptreference = 6, 
		RULE_conceptid = 7, RULE_term = 8, RULE_refinement = 9, RULE_attributegroup = 10, 
		RULE_attributeset = 11, RULE_attribute = 12, RULE_attributename = 13, 
		RULE_attributevalue = 14, RULE_expressionvalue = 15, RULE_stringvalue = 16, 
		RULE_numericvalue = 17, RULE_integervalue = 18, RULE_decimalvalue = 19, 
		RULE_sctid = 20, RULE_ws = 21, RULE_sp = 22, RULE_htab = 23, RULE_cr = 24, 
		RULE_lf = 25, RULE_qm = 26, RULE_bs = 27, RULE_digit = 28, RULE_zero = 29, 
		RULE_digitnonzero = 30, RULE_nonwsnonpipe = 31, RULE_anynonescapedchar = 32, 
		RULE_escapedchar = 33, RULE_utf8_2 = 34, RULE_utf8_3 = 35, RULE_utf8_4 = 36, 
		RULE_utf8_tail = 37, RULE_templatereplaceslot = 38, RULE_templateremoveslot = 39, 
		RULE_replaceinfo = 40, RULE_replaceflag = 41, RULE_templateslotinfo = 42, 
		RULE_templateslotname = 43, RULE_templateslotreference = 44, RULE_templatestring = 45, 
		RULE_nonquotestring = 46, RULE_nondoublequotestring = 47, RULE_nonsinglequotestring = 48, 
		RULE_sqm = 49, RULE_expressionconstrainttemplate = 50, RULE_simpleexpressionconstraint = 51, 
		RULE_refinedexpressionconstraint = 52, RULE_compoundexpressionconstraint = 53, 
		RULE_conjunctionexpressionconstraint = 54, RULE_disjunctionexpressionconstraint = 55, 
		RULE_exclusionexpressionconstraint = 56, RULE_subexpressionconstraint = 57, 
		RULE_eclfocusconcept = 58, RULE_memberof = 59, RULE_wildcard = 60, RULE_constraintoperator = 61, 
		RULE_descendantof = 62, RULE_descendantorselfof = 63, RULE_childof = 64, 
		RULE_ancestorof = 65, RULE_ancestororselfof = 66, RULE_parentof = 67, 
		RULE_conjunction = 68, RULE_disjunction = 69, RULE_exclusion = 70, RULE_eclrefinement = 71, 
		RULE_conjunctionrefinementset = 72, RULE_disjunctionrefinementset = 73, 
		RULE_subrefinement = 74, RULE_eclattributeset = 75, RULE_conjunctionattributeset = 76, 
		RULE_disjunctionattributeset = 77, RULE_subattributeset = 78, RULE_eclattributegroup = 79, 
		RULE_eclattribute = 80, RULE_cardinality = 81, RULE_minvalue = 82, RULE_to = 83, 
		RULE_maxvalue = 84, RULE_many = 85, RULE_reverseflag = 86, RULE_attributeoperator = 87, 
		RULE_eclattributename = 88, RULE_expressionconstraintvalue = 89, RULE_expressioncomparisonoperator = 90, 
		RULE_numericcomparisonoperator = 91, RULE_stringcomparisonoperator = 92, 
		RULE_nonnegativeintegervalue = 93, RULE_mws = 94, RULE_comment = 95, RULE_nonstarchar = 96, 
		RULE_starwithnonfslash = 97, RULE_nonfslash = 98;
	public static final String[] ruleNames = {
		"expressiontemplate", "subexpression", "definitionstatus", "equivalentto", 
		"subtypeof", "focusconcept", "conceptreference", "conceptid", "term", 
		"refinement", "attributegroup", "attributeset", "attribute", "attributename", 
		"attributevalue", "expressionvalue", "stringvalue", "numericvalue", "integervalue", 
		"decimalvalue", "sctid", "ws", "sp", "htab", "cr", "lf", "qm", "bs", "digit", 
		"zero", "digitnonzero", "nonwsnonpipe", "anynonescapedchar", "escapedchar", 
		"utf8_2", "utf8_3", "utf8_4", "utf8_tail", "templatereplaceslot", "templateremoveslot", 
		"replaceinfo", "replaceflag", "templateslotinfo", "templateslotname", 
		"templateslotreference", "templatestring", "nonquotestring", "nondoublequotestring", 
		"nonsinglequotestring", "sqm", "expressionconstrainttemplate", "simpleexpressionconstraint", 
		"refinedexpressionconstraint", "compoundexpressionconstraint", "conjunctionexpressionconstraint", 
		"disjunctionexpressionconstraint", "exclusionexpressionconstraint", "subexpressionconstraint", 
		"eclfocusconcept", "memberof", "wildcard", "constraintoperator", "descendantof", 
		"descendantorselfof", "childof", "ancestorof", "ancestororselfof", "parentof", 
		"conjunction", "disjunction", "exclusion", "eclrefinement", "conjunctionrefinementset", 
		"disjunctionrefinementset", "subrefinement", "eclattributeset", "conjunctionattributeset", 
		"disjunctionattributeset", "subattributeset", "eclattributegroup", "eclattribute", 
		"cardinality", "minvalue", "to", "maxvalue", "many", "reverseflag", "attributeoperator", 
		"eclattributename", "expressionconstraintvalue", "expressioncomparisonoperator", 
		"numericcomparisonoperator", "stringcomparisonoperator", "nonnegativeintegervalue", 
		"mws", "comment", "nonstarchar", "starwithnonfslash", "nonfslash"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'\\u0009'", "'\\u000A'", "'\\u000D'", "' '", "'!'", "'\"'", "'#'", 
		"'$'", "'%'", "'&'", "'''", "'('", "')'", "'*'", "'+'", "','", "'-'", 
		"'.'", "'/'", "'0'", "'1'", "'2'", "'3'", "'4'", "'5'", "'6'", "'7'", 
		"'8'", "'9'", "':'", "';'", "'<'", "'='", "'>'", "'?'", "'@'", "'A'", 
		"'B'", "'C'", "'D'", "'E'", "'F'", "'G'", "'H'", "'I'", "'J'", "'K'", 
		"'L'", "'M'", "'N'", "'O'", "'P'", "'Q'", "'R'", "'S'", "'T'", "'U'", 
		"'V'", "'W'", "'X'", "'Y'", "'Z'", "'['", "'\\'", "']'", "'^'", "'_'", 
		"'`'", "'a'", "'b'", "'c'", "'d'", "'e'", "'f'", "'g'", "'h'", "'i'", 
		"'j'", "'k'", "'l'", "'m'", "'n'", "'o'", "'p'", "'q'", "'r'", "'s'", 
		"'t'", "'u'", "'v'", "'w'", "'x'", "'y'", "'z'", "'{'", "'|'", "'}'", 
		"'~'", "'\\u0080'", "'\\u0081'", "'\\u0082'", "'\\u0083'", "'\\u0084'", 
		"'\\u0085'", "'\\u0086'", "'\\u0087'", "'\\u0088'", "'\\u0089'", "'\\u008A'", 
		"'\\u008B'", "'\\u008C'", "'\\u008D'", "'\\u008E'", "'\\u008F'", "'\\u0090'", 
		"'\\u0091'", "'\\u0092'", "'\\u0093'", "'\\u0094'", "'\\u0095'", "'\\u0096'", 
		"'\\u0097'", "'\\u0098'", "'\\u0099'", "'\\u009A'", "'\\u009B'", "'\\u009C'", 
		"'\\u009D'", "'\\u009E'", "'\\u009F'", "'\\u00A0'", "'\\u00A1'", "'\\u00A2'", 
		"'\\u00A3'", "'\\u00A4'", "'\\u00A5'", "'\\u00A6'", "'\\u00A7'", "'\\u00A8'", 
		"'\\u00A9'", "'\\u00AA'", "'\\u00AB'", "'\\u00AC'", "'\\u00AD'", "'\\u00AE'", 
		"'\\u00AF'", "'\\u00B0'", "'\\u00B1'", "'\\u00B2'", "'\\u00B3'", "'\\u00B4'", 
		"'\\u00B5'", "'\\u00B6'", "'\\u00B7'", "'\\u00B8'", "'\\u00B9'", "'\\u00BA'", 
		"'\\u00BB'", "'\\u00BC'", "'\\u00BD'", "'\\u00BE'", "'\\u00BF'", "'\\u00C2'", 
		"'\\u00C3'", "'\\u00C4'", "'\\u00C5'", "'\\u00C6'", "'\\u00C7'", "'\\u00C8'", 
		"'\\u00C9'", "'\\u00CA'", "'\\u00CB'", "'\\u00CC'", "'\\u00CD'", "'\\u00CE'", 
		"'\\u00CF'", "'\\u00D0'", "'\\u00D1'", "'\\u00D2'", "'\\u00D3'", "'\\u00D4'", 
		"'\\u00D5'", "'\\u00D6'", "'\\u00D7'", "'\\u00D8'", "'\\u00D9'", "'\\u00DA'", 
		"'\\u00DB'", "'\\u00DC'", "'\\u00DD'", "'\\u00DE'", "'\\u00DF'", "'\\u00E0'", 
		"'\\u00E1'", "'\\u00E2'", "'\\u00E3'", "'\\u00E4'", "'\\u00E5'", "'\\u00E6'", 
		"'\\u00E7'", "'\\u00E8'", "'\\u00E9'", "'\\u00EA'", "'\\u00EB'", "'\\u00EC'", 
		"'\\u00ED'", "'\\u00EE'", "'\\u00EF'", "'\\u00F0'", "'\\u00F1'", "'\\u00F2'", 
		"'\\u00F3'", "'\\u00F4'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "TAB", "LF", "CR", "SPACE", "EXCLAMATION", "QUOTE", "POUND", "DOLLAR", 
		"PERCENT", "AMPERSAND", "APOSTROPHE", "LEFT_PAREN", "RIGHT_PAREN", "ASTERISK", 
		"PLUS", "COMMA", "DASH", "PERIOD", "SLASH", "ZERO", "ONE", "TWO", "THREE", 
		"FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "COLON", "SEMICOLON", 
		"LESS_THAN", "EQUALS", "GREATER_THAN", "QUESTION", "AT", "CAP_A", "CAP_B", 
		"CAP_C", "CAP_D", "CAP_E", "CAP_F", "CAP_G", "CAP_H", "CAP_I", "CAP_J", 
		"CAP_K", "CAP_L", "CAP_M", "CAP_N", "CAP_O", "CAP_P", "CAP_Q", "CAP_R", 
		"CAP_S", "CAP_T", "CAP_U", "CAP_V", "CAP_W", "CAP_X", "CAP_Y", "CAP_Z", 
		"LEFT_BRACE", "BACKSLASH", "RIGHT_BRACE", "CARAT", "UNDERSCORE", "ACCENT", 
		"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", 
		"O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "LEFT_CURLY_BRACE", 
		"PIPE", "RIGHT_CURLY_BRACE", "TILDE", "U_0080", "U_0081", "U_0082", "U_0083", 
		"U_0084", "U_0085", "U_0086", "U_0087", "U_0088", "U_0089", "U_008A", 
		"U_008B", "U_008C", "U_008D", "U_008E", "U_008F", "U_0090", "U_0091", 
		"U_0092", "U_0093", "U_0094", "U_0095", "U_0096", "U_0097", "U_0098", 
		"U_0099", "U_009A", "U_009B", "U_009C", "U_009D", "U_009E", "U_009F", 
		"U_00A0", "U_00A1", "U_00A2", "U_00A3", "U_00A4", "U_00A5", "U_00A6", 
		"U_00A7", "U_00A8", "U_00A9", "U_00AA", "U_00AB", "U_00AC", "U_00AD", 
		"U_00AE", "U_00AF", "U_00B0", "U_00B1", "U_00B2", "U_00B3", "U_00B4", 
		"U_00B5", "U_00B6", "U_00B7", "U_00B8", "U_00B9", "U_00BA", "U_00BB", 
		"U_00BC", "U_00BD", "U_00BE", "U_00BF", "U_00C2", "U_00C3", "U_00C4", 
		"U_00C5", "U_00C6", "U_00C7", "U_00C8", "U_00C9", "U_00CA", "U_00CB", 
		"U_00CC", "U_00CD", "U_00CE", "U_00CF", "U_00D0", "U_00D1", "U_00D2", 
		"U_00D3", "U_00D4", "U_00D5", "U_00D6", "U_00D7", "U_00D8", "U_00D9", 
		"U_00DA", "U_00DB", "U_00DC", "U_00DD", "U_00DE", "U_00DF", "U_00E0", 
		"U_00E1", "U_00E2", "U_00E3", "U_00E4", "U_00E5", "U_00E6", "U_00E7", 
		"U_00E8", "U_00E9", "U_00EA", "U_00EB", "U_00EC", "U_00ED", "U_00EE", 
		"U_00EF", "U_00F0", "U_00F1", "U_00F2", "U_00F3", "U_00F4"
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
	public String getGrammarFileName() { return "ExpressionTemplate.txt"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ExpressionTemplateParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ExpressiontemplateContext extends ParserRuleContext {
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public SubexpressionContext subexpression() {
			return getRuleContext(SubexpressionContext.class,0);
		}
		public DefinitionstatusContext definitionstatus() {
			return getRuleContext(DefinitionstatusContext.class,0);
		}
		public ExpressiontemplateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressiontemplate; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterExpressiontemplate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitExpressiontemplate(this);
		}
	}

	public final ExpressiontemplateContext expressiontemplate() throws RecognitionException {
		ExpressiontemplateContext _localctx = new ExpressiontemplateContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_expressiontemplate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(198);
			ws();
			setState(202);
			_la = _input.LA(1);
			if (_la==LESS_THAN || _la==EQUALS) {
				{
				setState(199);
				definitionstatus();
				setState(200);
				ws();
				}
			}

			setState(204);
			subexpression();
			setState(205);
			ws();
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

	public static class SubexpressionContext extends ParserRuleContext {
		public FocusconceptContext focusconcept() {
			return getRuleContext(FocusconceptContext.class,0);
		}
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public TerminalNode COLON() { return getToken(ExpressionTemplateParser.COLON, 0); }
		public RefinementContext refinement() {
			return getRuleContext(RefinementContext.class,0);
		}
		public SubexpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subexpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterSubexpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitSubexpression(this);
		}
	}

	public final SubexpressionContext subexpression() throws RecognitionException {
		SubexpressionContext _localctx = new SubexpressionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_subexpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(207);
			focusconcept();
			setState(213);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(208);
				ws();
				setState(209);
				match(COLON);
				setState(210);
				ws();
				setState(211);
				refinement();
				}
				break;
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

	public static class DefinitionstatusContext extends ParserRuleContext {
		public EquivalenttoContext equivalentto() {
			return getRuleContext(EquivalenttoContext.class,0);
		}
		public SubtypeofContext subtypeof() {
			return getRuleContext(SubtypeofContext.class,0);
		}
		public DefinitionstatusContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_definitionstatus; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterDefinitionstatus(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitDefinitionstatus(this);
		}
	}

	public final DefinitionstatusContext definitionstatus() throws RecognitionException {
		DefinitionstatusContext _localctx = new DefinitionstatusContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_definitionstatus);
		try {
			setState(217);
			switch (_input.LA(1)) {
			case EQUALS:
				enterOuterAlt(_localctx, 1);
				{
				setState(215);
				equivalentto();
				}
				break;
			case LESS_THAN:
				enterOuterAlt(_localctx, 2);
				{
				setState(216);
				subtypeof();
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

	public static class EquivalenttoContext extends ParserRuleContext {
		public List<TerminalNode> EQUALS() { return getTokens(ExpressionTemplateParser.EQUALS); }
		public TerminalNode EQUALS(int i) {
			return getToken(ExpressionTemplateParser.EQUALS, i);
		}
		public EquivalenttoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_equivalentto; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterEquivalentto(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitEquivalentto(this);
		}
	}

	public final EquivalenttoContext equivalentto() throws RecognitionException {
		EquivalenttoContext _localctx = new EquivalenttoContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_equivalentto);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(219);
			match(EQUALS);
			setState(220);
			match(EQUALS);
			setState(221);
			match(EQUALS);
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

	public static class SubtypeofContext extends ParserRuleContext {
		public List<TerminalNode> LESS_THAN() { return getTokens(ExpressionTemplateParser.LESS_THAN); }
		public TerminalNode LESS_THAN(int i) {
			return getToken(ExpressionTemplateParser.LESS_THAN, i);
		}
		public SubtypeofContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subtypeof; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterSubtypeof(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitSubtypeof(this);
		}
	}

	public final SubtypeofContext subtypeof() throws RecognitionException {
		SubtypeofContext _localctx = new SubtypeofContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_subtypeof);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(223);
			match(LESS_THAN);
			setState(224);
			match(LESS_THAN);
			setState(225);
			match(LESS_THAN);
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

	public static class FocusconceptContext extends ParserRuleContext {
		public List<ConceptreferenceContext> conceptreference() {
			return getRuleContexts(ConceptreferenceContext.class);
		}
		public ConceptreferenceContext conceptreference(int i) {
			return getRuleContext(ConceptreferenceContext.class,i);
		}
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public List<TerminalNode> PLUS() { return getTokens(ExpressionTemplateParser.PLUS); }
		public TerminalNode PLUS(int i) {
			return getToken(ExpressionTemplateParser.PLUS, i);
		}
		public FocusconceptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_focusconcept; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterFocusconcept(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitFocusconcept(this);
		}
	}

	public final FocusconceptContext focusconcept() throws RecognitionException {
		FocusconceptContext _localctx = new FocusconceptContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_focusconcept);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(227);
			conceptreference();
			setState(235);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(228);
					ws();
					setState(229);
					match(PLUS);
					setState(230);
					ws();
					setState(231);
					conceptreference();
					}
					} 
				}
				setState(237);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
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

	public static class ConceptreferenceContext extends ParserRuleContext {
		public TemplatereplaceslotContext templatereplaceslot() {
			return getRuleContext(TemplatereplaceslotContext.class,0);
		}
		public ConceptidContext conceptid() {
			return getRuleContext(ConceptidContext.class,0);
		}
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public List<TerminalNode> PIPE() { return getTokens(ExpressionTemplateParser.PIPE); }
		public TerminalNode PIPE(int i) {
			return getToken(ExpressionTemplateParser.PIPE, i);
		}
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public ConceptreferenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conceptreference; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterConceptreference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitConceptreference(this);
		}
	}

	public final ConceptreferenceContext conceptreference() throws RecognitionException {
		ConceptreferenceContext _localctx = new ConceptreferenceContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_conceptreference);
		try {
			setState(249);
			switch (_input.LA(1)) {
			case LEFT_BRACE:
				enterOuterAlt(_localctx, 1);
				{
				setState(238);
				templatereplaceslot();
				}
				break;
			case ONE:
			case TWO:
			case THREE:
			case FOUR:
			case FIVE:
			case SIX:
			case SEVEN:
			case EIGHT:
			case NINE:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(239);
				conceptid();
				setState(247);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
				case 1:
					{
					setState(240);
					ws();
					setState(241);
					match(PIPE);
					setState(242);
					ws();
					setState(243);
					term();
					setState(244);
					ws();
					setState(245);
					match(PIPE);
					}
					break;
				}
				}
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

	public static class ConceptidContext extends ParserRuleContext {
		public SctidContext sctid() {
			return getRuleContext(SctidContext.class,0);
		}
		public ConceptidContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conceptid; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterConceptid(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitConceptid(this);
		}
	}

	public final ConceptidContext conceptid() throws RecognitionException {
		ConceptidContext _localctx = new ConceptidContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_conceptid);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(251);
			sctid();
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

	public static class TermContext extends ParserRuleContext {
		public List<NonwsnonpipeContext> nonwsnonpipe() {
			return getRuleContexts(NonwsnonpipeContext.class);
		}
		public NonwsnonpipeContext nonwsnonpipe(int i) {
			return getRuleContext(NonwsnonpipeContext.class,i);
		}
		public List<SpContext> sp() {
			return getRuleContexts(SpContext.class);
		}
		public SpContext sp(int i) {
			return getRuleContext(SpContext.class,i);
		}
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitTerm(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_term);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(253);
			nonwsnonpipe();
			setState(263);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(257);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==SPACE) {
						{
						{
						setState(254);
						sp();
						}
						}
						setState(259);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(260);
					nonwsnonpipe();
					}
					} 
				}
				setState(265);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
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

	public static class RefinementContext extends ParserRuleContext {
		public AttributesetContext attributeset() {
			return getRuleContext(AttributesetContext.class,0);
		}
		public List<AttributegroupContext> attributegroup() {
			return getRuleContexts(AttributegroupContext.class);
		}
		public AttributegroupContext attributegroup(int i) {
			return getRuleContext(AttributegroupContext.class,i);
		}
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(ExpressionTemplateParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(ExpressionTemplateParser.COMMA, i);
		}
		public RefinementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_refinement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterRefinement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitRefinement(this);
		}
	}

	public final RefinementContext refinement() throws RecognitionException {
		RefinementContext _localctx = new RefinementContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_refinement);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(268);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				setState(266);
				attributeset();
				}
				break;
			case 2:
				{
				setState(267);
				attributegroup();
				}
				break;
			}
			setState(279);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(270);
					ws();
					setState(273);
					_la = _input.LA(1);
					if (_la==COMMA) {
						{
						setState(271);
						match(COMMA);
						setState(272);
						ws();
						}
					}

					setState(275);
					attributegroup();
					}
					} 
				}
				setState(281);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
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

	public static class AttributegroupContext extends ParserRuleContext {
		public TerminalNode LEFT_CURLY_BRACE() { return getToken(ExpressionTemplateParser.LEFT_CURLY_BRACE, 0); }
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public AttributesetContext attributeset() {
			return getRuleContext(AttributesetContext.class,0);
		}
		public TerminalNode RIGHT_CURLY_BRACE() { return getToken(ExpressionTemplateParser.RIGHT_CURLY_BRACE, 0); }
		public TemplateremoveslotContext templateremoveslot() {
			return getRuleContext(TemplateremoveslotContext.class,0);
		}
		public AttributegroupContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributegroup; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterAttributegroup(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitAttributegroup(this);
		}
	}

	public final AttributegroupContext attributegroup() throws RecognitionException {
		AttributegroupContext _localctx = new AttributegroupContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_attributegroup);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(285);
			_la = _input.LA(1);
			if (_la==LEFT_BRACE) {
				{
				setState(282);
				templateremoveslot();
				setState(283);
				ws();
				}
			}

			setState(287);
			match(LEFT_CURLY_BRACE);
			setState(288);
			ws();
			setState(289);
			attributeset();
			setState(290);
			ws();
			setState(291);
			match(RIGHT_CURLY_BRACE);
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

	public static class AttributesetContext extends ParserRuleContext {
		public List<AttributeContext> attribute() {
			return getRuleContexts(AttributeContext.class);
		}
		public AttributeContext attribute(int i) {
			return getRuleContext(AttributeContext.class,i);
		}
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(ExpressionTemplateParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(ExpressionTemplateParser.COMMA, i);
		}
		public AttributesetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributeset; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterAttributeset(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitAttributeset(this);
		}
	}

	public final AttributesetContext attributeset() throws RecognitionException {
		AttributesetContext _localctx = new AttributesetContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_attributeset);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(293);
			attribute();
			setState(301);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(294);
					ws();
					setState(295);
					match(COMMA);
					setState(296);
					ws();
					setState(297);
					attribute();
					}
					} 
				}
				setState(303);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
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

	public static class AttributeContext extends ParserRuleContext {
		public AttributenameContext attributename() {
			return getRuleContext(AttributenameContext.class,0);
		}
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public TerminalNode EQUALS() { return getToken(ExpressionTemplateParser.EQUALS, 0); }
		public AttributevalueContext attributevalue() {
			return getRuleContext(AttributevalueContext.class,0);
		}
		public TemplateremoveslotContext templateremoveslot() {
			return getRuleContext(TemplateremoveslotContext.class,0);
		}
		public AttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attribute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterAttribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitAttribute(this);
		}
	}

	public final AttributeContext attribute() throws RecognitionException {
		AttributeContext _localctx = new AttributeContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_attribute);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(307);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				setState(304);
				templateremoveslot();
				setState(305);
				ws();
				}
				break;
			}
			setState(309);
			attributename();
			setState(310);
			ws();
			setState(311);
			match(EQUALS);
			setState(312);
			ws();
			setState(313);
			attributevalue();
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

	public static class AttributenameContext extends ParserRuleContext {
		public ConceptreferenceContext conceptreference() {
			return getRuleContext(ConceptreferenceContext.class,0);
		}
		public AttributenameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributename; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterAttributename(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitAttributename(this);
		}
	}

	public final AttributenameContext attributename() throws RecognitionException {
		AttributenameContext _localctx = new AttributenameContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_attributename);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(315);
			conceptreference();
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

	public static class AttributevalueContext extends ParserRuleContext {
		public ExpressionvalueContext expressionvalue() {
			return getRuleContext(ExpressionvalueContext.class,0);
		}
		public List<QmContext> qm() {
			return getRuleContexts(QmContext.class);
		}
		public QmContext qm(int i) {
			return getRuleContext(QmContext.class,i);
		}
		public StringvalueContext stringvalue() {
			return getRuleContext(StringvalueContext.class,0);
		}
		public TerminalNode POUND() { return getToken(ExpressionTemplateParser.POUND, 0); }
		public NumericvalueContext numericvalue() {
			return getRuleContext(NumericvalueContext.class,0);
		}
		public AttributevalueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributevalue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterAttributevalue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitAttributevalue(this);
		}
	}

	public final AttributevalueContext attributevalue() throws RecognitionException {
		AttributevalueContext _localctx = new AttributevalueContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_attributevalue);
		try {
			setState(324);
			switch (_input.LA(1)) {
			case LEFT_PAREN:
			case ONE:
			case TWO:
			case THREE:
			case FOUR:
			case FIVE:
			case SIX:
			case SEVEN:
			case EIGHT:
			case NINE:
			case LEFT_BRACE:
				enterOuterAlt(_localctx, 1);
				{
				setState(317);
				expressionvalue();
				}
				break;
			case QUOTE:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(318);
				qm();
				setState(319);
				stringvalue();
				setState(320);
				qm();
				}
				}
				break;
			case POUND:
				enterOuterAlt(_localctx, 3);
				{
				{
				setState(322);
				match(POUND);
				setState(323);
				numericvalue();
				}
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

	public static class ExpressionvalueContext extends ParserRuleContext {
		public ConceptreferenceContext conceptreference() {
			return getRuleContext(ConceptreferenceContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(ExpressionTemplateParser.LEFT_PAREN, 0); }
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public SubexpressionContext subexpression() {
			return getRuleContext(SubexpressionContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(ExpressionTemplateParser.RIGHT_PAREN, 0); }
		public ExpressionvalueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionvalue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterExpressionvalue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitExpressionvalue(this);
		}
	}

	public final ExpressionvalueContext expressionvalue() throws RecognitionException {
		ExpressionvalueContext _localctx = new ExpressionvalueContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_expressionvalue);
		try {
			setState(333);
			switch (_input.LA(1)) {
			case ONE:
			case TWO:
			case THREE:
			case FOUR:
			case FIVE:
			case SIX:
			case SEVEN:
			case EIGHT:
			case NINE:
			case LEFT_BRACE:
				enterOuterAlt(_localctx, 1);
				{
				setState(326);
				conceptreference();
				}
				break;
			case LEFT_PAREN:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(327);
				match(LEFT_PAREN);
				setState(328);
				ws();
				setState(329);
				subexpression();
				setState(330);
				ws();
				setState(331);
				match(RIGHT_PAREN);
				}
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

	public static class StringvalueContext extends ParserRuleContext {
		public List<AnynonescapedcharContext> anynonescapedchar() {
			return getRuleContexts(AnynonescapedcharContext.class);
		}
		public AnynonescapedcharContext anynonescapedchar(int i) {
			return getRuleContext(AnynonescapedcharContext.class,i);
		}
		public List<EscapedcharContext> escapedchar() {
			return getRuleContexts(EscapedcharContext.class);
		}
		public EscapedcharContext escapedchar(int i) {
			return getRuleContext(EscapedcharContext.class,i);
		}
		public StringvalueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringvalue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterStringvalue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitStringvalue(this);
		}
	}

	public final StringvalueContext stringvalue() throws RecognitionException {
		StringvalueContext _localctx = new StringvalueContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_stringvalue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(337); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(337);
				switch (_input.LA(1)) {
				case TAB:
				case LF:
				case CR:
				case SPACE:
				case EXCLAMATION:
				case POUND:
				case DOLLAR:
				case PERCENT:
				case AMPERSAND:
				case APOSTROPHE:
				case LEFT_PAREN:
				case RIGHT_PAREN:
				case ASTERISK:
				case PLUS:
				case COMMA:
				case DASH:
				case PERIOD:
				case SLASH:
				case ZERO:
				case ONE:
				case TWO:
				case THREE:
				case FOUR:
				case FIVE:
				case SIX:
				case SEVEN:
				case EIGHT:
				case NINE:
				case COLON:
				case SEMICOLON:
				case LESS_THAN:
				case EQUALS:
				case GREATER_THAN:
				case QUESTION:
				case AT:
				case CAP_A:
				case CAP_B:
				case CAP_C:
				case CAP_D:
				case CAP_E:
				case CAP_F:
				case CAP_G:
				case CAP_H:
				case CAP_I:
				case CAP_J:
				case CAP_K:
				case CAP_L:
				case CAP_M:
				case CAP_N:
				case CAP_O:
				case CAP_P:
				case CAP_Q:
				case CAP_R:
				case CAP_S:
				case CAP_T:
				case CAP_U:
				case CAP_V:
				case CAP_W:
				case CAP_X:
				case CAP_Y:
				case CAP_Z:
				case LEFT_BRACE:
				case RIGHT_BRACE:
				case CARAT:
				case UNDERSCORE:
				case ACCENT:
				case A:
				case B:
				case C:
				case D:
				case E:
				case F:
				case G:
				case H:
				case I:
				case J:
				case K:
				case L:
				case M:
				case N:
				case O:
				case P:
				case Q:
				case R:
				case S:
				case T:
				case U:
				case V:
				case W:
				case X:
				case Y:
				case Z:
				case LEFT_CURLY_BRACE:
				case PIPE:
				case RIGHT_CURLY_BRACE:
				case TILDE:
				case U_00C2:
				case U_00C3:
				case U_00C4:
				case U_00C5:
				case U_00C6:
				case U_00C7:
				case U_00C8:
				case U_00C9:
				case U_00CA:
				case U_00CB:
				case U_00CC:
				case U_00CD:
				case U_00CE:
				case U_00CF:
				case U_00D0:
				case U_00D1:
				case U_00D2:
				case U_00D3:
				case U_00D4:
				case U_00D5:
				case U_00D6:
				case U_00D7:
				case U_00D8:
				case U_00D9:
				case U_00DA:
				case U_00DB:
				case U_00DC:
				case U_00DD:
				case U_00DE:
				case U_00DF:
				case U_00E0:
				case U_00E1:
				case U_00E2:
				case U_00E3:
				case U_00E4:
				case U_00E5:
				case U_00E6:
				case U_00E7:
				case U_00E8:
				case U_00E9:
				case U_00EA:
				case U_00EB:
				case U_00EC:
				case U_00ED:
				case U_00EE:
				case U_00EF:
				case U_00F0:
				case U_00F1:
				case U_00F2:
				case U_00F3:
				case U_00F4:
					{
					setState(335);
					anynonescapedchar();
					}
					break;
				case BACKSLASH:
					{
					setState(336);
					escapedchar();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(339); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TAB) | (1L << LF) | (1L << CR) | (1L << SPACE) | (1L << EXCLAMATION) | (1L << POUND) | (1L << DOLLAR) | (1L << PERCENT) | (1L << AMPERSAND) | (1L << APOSTROPHE) | (1L << LEFT_PAREN) | (1L << RIGHT_PAREN) | (1L << ASTERISK) | (1L << PLUS) | (1L << COMMA) | (1L << DASH) | (1L << PERIOD) | (1L << SLASH) | (1L << ZERO) | (1L << ONE) | (1L << TWO) | (1L << THREE) | (1L << FOUR) | (1L << FIVE) | (1L << SIX) | (1L << SEVEN) | (1L << EIGHT) | (1L << NINE) | (1L << COLON) | (1L << SEMICOLON) | (1L << LESS_THAN) | (1L << EQUALS) | (1L << GREATER_THAN) | (1L << QUESTION) | (1L << AT) | (1L << CAP_A) | (1L << CAP_B) | (1L << CAP_C) | (1L << CAP_D) | (1L << CAP_E) | (1L << CAP_F) | (1L << CAP_G) | (1L << CAP_H) | (1L << CAP_I) | (1L << CAP_J) | (1L << CAP_K) | (1L << CAP_L) | (1L << CAP_M) | (1L << CAP_N) | (1L << CAP_O) | (1L << CAP_P) | (1L << CAP_Q) | (1L << CAP_R) | (1L << CAP_S) | (1L << CAP_T) | (1L << CAP_U) | (1L << CAP_V) | (1L << CAP_W) | (1L << CAP_X) | (1L << CAP_Y) | (1L << CAP_Z) | (1L << LEFT_BRACE))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (BACKSLASH - 64)) | (1L << (RIGHT_BRACE - 64)) | (1L << (CARAT - 64)) | (1L << (UNDERSCORE - 64)) | (1L << (ACCENT - 64)) | (1L << (A - 64)) | (1L << (B - 64)) | (1L << (C - 64)) | (1L << (D - 64)) | (1L << (E - 64)) | (1L << (F - 64)) | (1L << (G - 64)) | (1L << (H - 64)) | (1L << (I - 64)) | (1L << (J - 64)) | (1L << (K - 64)) | (1L << (L - 64)) | (1L << (M - 64)) | (1L << (N - 64)) | (1L << (O - 64)) | (1L << (P - 64)) | (1L << (Q - 64)) | (1L << (R - 64)) | (1L << (S - 64)) | (1L << (T - 64)) | (1L << (U - 64)) | (1L << (V - 64)) | (1L << (W - 64)) | (1L << (X - 64)) | (1L << (Y - 64)) | (1L << (Z - 64)) | (1L << (LEFT_CURLY_BRACE - 64)) | (1L << (PIPE - 64)) | (1L << (RIGHT_CURLY_BRACE - 64)) | (1L << (TILDE - 64)))) != 0) || ((((_la - 163)) & ~0x3f) == 0 && ((1L << (_la - 163)) & ((1L << (U_00C2 - 163)) | (1L << (U_00C3 - 163)) | (1L << (U_00C4 - 163)) | (1L << (U_00C5 - 163)) | (1L << (U_00C6 - 163)) | (1L << (U_00C7 - 163)) | (1L << (U_00C8 - 163)) | (1L << (U_00C9 - 163)) | (1L << (U_00CA - 163)) | (1L << (U_00CB - 163)) | (1L << (U_00CC - 163)) | (1L << (U_00CD - 163)) | (1L << (U_00CE - 163)) | (1L << (U_00CF - 163)) | (1L << (U_00D0 - 163)) | (1L << (U_00D1 - 163)) | (1L << (U_00D2 - 163)) | (1L << (U_00D3 - 163)) | (1L << (U_00D4 - 163)) | (1L << (U_00D5 - 163)) | (1L << (U_00D6 - 163)) | (1L << (U_00D7 - 163)) | (1L << (U_00D8 - 163)) | (1L << (U_00D9 - 163)) | (1L << (U_00DA - 163)) | (1L << (U_00DB - 163)) | (1L << (U_00DC - 163)) | (1L << (U_00DD - 163)) | (1L << (U_00DE - 163)) | (1L << (U_00DF - 163)) | (1L << (U_00E0 - 163)) | (1L << (U_00E1 - 163)) | (1L << (U_00E2 - 163)) | (1L << (U_00E3 - 163)) | (1L << (U_00E4 - 163)) | (1L << (U_00E5 - 163)) | (1L << (U_00E6 - 163)) | (1L << (U_00E7 - 163)) | (1L << (U_00E8 - 163)) | (1L << (U_00E9 - 163)) | (1L << (U_00EA - 163)) | (1L << (U_00EB - 163)) | (1L << (U_00EC - 163)) | (1L << (U_00ED - 163)) | (1L << (U_00EE - 163)) | (1L << (U_00EF - 163)) | (1L << (U_00F0 - 163)) | (1L << (U_00F1 - 163)) | (1L << (U_00F2 - 163)) | (1L << (U_00F3 - 163)) | (1L << (U_00F4 - 163)))) != 0) );
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

	public static class NumericvalueContext extends ParserRuleContext {
		public DecimalvalueContext decimalvalue() {
			return getRuleContext(DecimalvalueContext.class,0);
		}
		public IntegervalueContext integervalue() {
			return getRuleContext(IntegervalueContext.class,0);
		}
		public NumericvalueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numericvalue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterNumericvalue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitNumericvalue(this);
		}
	}

	public final NumericvalueContext numericvalue() throws RecognitionException {
		NumericvalueContext _localctx = new NumericvalueContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_numericvalue);
		try {
			setState(343);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(341);
				decimalvalue();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(342);
				integervalue();
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

	public static class IntegervalueContext extends ParserRuleContext {
		public DigitnonzeroContext digitnonzero() {
			return getRuleContext(DigitnonzeroContext.class,0);
		}
		public List<DigitContext> digit() {
			return getRuleContexts(DigitContext.class);
		}
		public DigitContext digit(int i) {
			return getRuleContext(DigitContext.class,i);
		}
		public TerminalNode DASH() { return getToken(ExpressionTemplateParser.DASH, 0); }
		public TerminalNode PLUS() { return getToken(ExpressionTemplateParser.PLUS, 0); }
		public ZeroContext zero() {
			return getRuleContext(ZeroContext.class,0);
		}
		public IntegervalueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_integervalue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterIntegervalue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitIntegervalue(this);
		}
	}

	public final IntegervalueContext integervalue() throws RecognitionException {
		IntegervalueContext _localctx = new IntegervalueContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_integervalue);
		int _la;
		try {
			setState(356);
			switch (_input.LA(1)) {
			case PLUS:
			case DASH:
			case ONE:
			case TWO:
			case THREE:
			case FOUR:
			case FIVE:
			case SIX:
			case SEVEN:
			case EIGHT:
			case NINE:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(346);
				_la = _input.LA(1);
				if (_la==PLUS || _la==DASH) {
					{
					setState(345);
					_la = _input.LA(1);
					if ( !(_la==PLUS || _la==DASH) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
				}

				setState(348);
				digitnonzero();
				setState(352);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ZERO) | (1L << ONE) | (1L << TWO) | (1L << THREE) | (1L << FOUR) | (1L << FIVE) | (1L << SIX) | (1L << SEVEN) | (1L << EIGHT) | (1L << NINE))) != 0)) {
					{
					{
					setState(349);
					digit();
					}
					}
					setState(354);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				}
				break;
			case ZERO:
				enterOuterAlt(_localctx, 2);
				{
				setState(355);
				zero();
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

	public static class DecimalvalueContext extends ParserRuleContext {
		public IntegervalueContext integervalue() {
			return getRuleContext(IntegervalueContext.class,0);
		}
		public TerminalNode PERIOD() { return getToken(ExpressionTemplateParser.PERIOD, 0); }
		public List<DigitContext> digit() {
			return getRuleContexts(DigitContext.class);
		}
		public DigitContext digit(int i) {
			return getRuleContext(DigitContext.class,i);
		}
		public DecimalvalueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decimalvalue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterDecimalvalue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitDecimalvalue(this);
		}
	}

	public final DecimalvalueContext decimalvalue() throws RecognitionException {
		DecimalvalueContext _localctx = new DecimalvalueContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_decimalvalue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(358);
			integervalue();
			setState(359);
			match(PERIOD);
			setState(361); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(360);
				digit();
				}
				}
				setState(363); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ZERO) | (1L << ONE) | (1L << TWO) | (1L << THREE) | (1L << FOUR) | (1L << FIVE) | (1L << SIX) | (1L << SEVEN) | (1L << EIGHT) | (1L << NINE))) != 0) );
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

	public static class SctidContext extends ParserRuleContext {
		public DigitnonzeroContext digitnonzero() {
			return getRuleContext(DigitnonzeroContext.class,0);
		}
		public List<DigitContext> digit() {
			return getRuleContexts(DigitContext.class);
		}
		public DigitContext digit(int i) {
			return getRuleContext(DigitContext.class,i);
		}
		public SctidContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sctid; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterSctid(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitSctid(this);
		}
	}

	public final SctidContext sctid() throws RecognitionException {
		SctidContext _localctx = new SctidContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_sctid);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(365);
			digitnonzero();
			{
			setState(366);
			digit();
			}
			{
			setState(367);
			digit();
			}
			{
			setState(368);
			digit();
			}
			{
			setState(369);
			digit();
			}
			{
			setState(370);
			digit();
			}
			setState(462);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				{
				setState(372);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ZERO) | (1L << ONE) | (1L << TWO) | (1L << THREE) | (1L << FOUR) | (1L << FIVE) | (1L << SIX) | (1L << SEVEN) | (1L << EIGHT) | (1L << NINE))) != 0)) {
					{
					setState(371);
					digit();
					}
				}

				}
				break;
			case 2:
				{
				{
				{
				setState(374);
				digit();
				}
				{
				setState(375);
				digit();
				}
				}
				}
				break;
			case 3:
				{
				{
				{
				setState(377);
				digit();
				}
				{
				setState(378);
				digit();
				}
				{
				setState(379);
				digit();
				}
				}
				}
				break;
			case 4:
				{
				{
				{
				setState(381);
				digit();
				}
				{
				setState(382);
				digit();
				}
				{
				setState(383);
				digit();
				}
				{
				setState(384);
				digit();
				}
				}
				}
				break;
			case 5:
				{
				{
				{
				setState(386);
				digit();
				}
				{
				setState(387);
				digit();
				}
				{
				setState(388);
				digit();
				}
				{
				setState(389);
				digit();
				}
				{
				setState(390);
				digit();
				}
				}
				}
				break;
			case 6:
				{
				{
				{
				setState(392);
				digit();
				}
				{
				setState(393);
				digit();
				}
				{
				setState(394);
				digit();
				}
				{
				setState(395);
				digit();
				}
				{
				setState(396);
				digit();
				}
				{
				setState(397);
				digit();
				}
				}
				}
				break;
			case 7:
				{
				{
				{
				setState(399);
				digit();
				}
				{
				setState(400);
				digit();
				}
				{
				setState(401);
				digit();
				}
				{
				setState(402);
				digit();
				}
				{
				setState(403);
				digit();
				}
				{
				setState(404);
				digit();
				}
				{
				setState(405);
				digit();
				}
				}
				}
				break;
			case 8:
				{
				{
				{
				setState(407);
				digit();
				}
				{
				setState(408);
				digit();
				}
				{
				setState(409);
				digit();
				}
				{
				setState(410);
				digit();
				}
				{
				setState(411);
				digit();
				}
				{
				setState(412);
				digit();
				}
				{
				setState(413);
				digit();
				}
				{
				setState(414);
				digit();
				}
				}
				}
				break;
			case 9:
				{
				{
				{
				setState(416);
				digit();
				}
				{
				setState(417);
				digit();
				}
				{
				setState(418);
				digit();
				}
				{
				setState(419);
				digit();
				}
				{
				setState(420);
				digit();
				}
				{
				setState(421);
				digit();
				}
				{
				setState(422);
				digit();
				}
				{
				setState(423);
				digit();
				}
				{
				setState(424);
				digit();
				}
				}
				}
				break;
			case 10:
				{
				{
				{
				setState(426);
				digit();
				}
				{
				setState(427);
				digit();
				}
				{
				setState(428);
				digit();
				}
				{
				setState(429);
				digit();
				}
				{
				setState(430);
				digit();
				}
				{
				setState(431);
				digit();
				}
				{
				setState(432);
				digit();
				}
				{
				setState(433);
				digit();
				}
				{
				setState(434);
				digit();
				}
				{
				setState(435);
				digit();
				}
				}
				}
				break;
			case 11:
				{
				{
				{
				setState(437);
				digit();
				}
				{
				setState(438);
				digit();
				}
				{
				setState(439);
				digit();
				}
				{
				setState(440);
				digit();
				}
				{
				setState(441);
				digit();
				}
				{
				setState(442);
				digit();
				}
				{
				setState(443);
				digit();
				}
				{
				setState(444);
				digit();
				}
				{
				setState(445);
				digit();
				}
				{
				setState(446);
				digit();
				}
				{
				setState(447);
				digit();
				}
				}
				}
				break;
			case 12:
				{
				{
				{
				setState(449);
				digit();
				}
				{
				setState(450);
				digit();
				}
				{
				setState(451);
				digit();
				}
				{
				setState(452);
				digit();
				}
				{
				setState(453);
				digit();
				}
				{
				setState(454);
				digit();
				}
				{
				setState(455);
				digit();
				}
				{
				setState(456);
				digit();
				}
				{
				setState(457);
				digit();
				}
				{
				setState(458);
				digit();
				}
				{
				setState(459);
				digit();
				}
				{
				setState(460);
				digit();
				}
				}
				}
				break;
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

	public static class WsContext extends ParserRuleContext {
		public List<SpContext> sp() {
			return getRuleContexts(SpContext.class);
		}
		public SpContext sp(int i) {
			return getRuleContext(SpContext.class,i);
		}
		public List<HtabContext> htab() {
			return getRuleContexts(HtabContext.class);
		}
		public HtabContext htab(int i) {
			return getRuleContext(HtabContext.class,i);
		}
		public List<CrContext> cr() {
			return getRuleContexts(CrContext.class);
		}
		public CrContext cr(int i) {
			return getRuleContext(CrContext.class,i);
		}
		public List<LfContext> lf() {
			return getRuleContexts(LfContext.class);
		}
		public LfContext lf(int i) {
			return getRuleContext(LfContext.class,i);
		}
		public WsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ws; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterWs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitWs(this);
		}
	}

	public final WsContext ws() throws RecognitionException {
		WsContext _localctx = new WsContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_ws);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(470);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(468);
					switch (_input.LA(1)) {
					case SPACE:
						{
						setState(464);
						sp();
						}
						break;
					case TAB:
						{
						setState(465);
						htab();
						}
						break;
					case CR:
						{
						setState(466);
						cr();
						}
						break;
					case LF:
						{
						setState(467);
						lf();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					} 
				}
				setState(472);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
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

	public static class SpContext extends ParserRuleContext {
		public TerminalNode SPACE() { return getToken(ExpressionTemplateParser.SPACE, 0); }
		public SpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterSp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitSp(this);
		}
	}

	public final SpContext sp() throws RecognitionException {
		SpContext _localctx = new SpContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_sp);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(473);
			match(SPACE);
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

	public static class HtabContext extends ParserRuleContext {
		public TerminalNode TAB() { return getToken(ExpressionTemplateParser.TAB, 0); }
		public HtabContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_htab; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterHtab(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitHtab(this);
		}
	}

	public final HtabContext htab() throws RecognitionException {
		HtabContext _localctx = new HtabContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_htab);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(475);
			match(TAB);
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

	public static class CrContext extends ParserRuleContext {
		public TerminalNode CR() { return getToken(ExpressionTemplateParser.CR, 0); }
		public CrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterCr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitCr(this);
		}
	}

	public final CrContext cr() throws RecognitionException {
		CrContext _localctx = new CrContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_cr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(477);
			match(CR);
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

	public static class LfContext extends ParserRuleContext {
		public TerminalNode LF() { return getToken(ExpressionTemplateParser.LF, 0); }
		public LfContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lf; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterLf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitLf(this);
		}
	}

	public final LfContext lf() throws RecognitionException {
		LfContext _localctx = new LfContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_lf);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(479);
			match(LF);
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

	public static class QmContext extends ParserRuleContext {
		public TerminalNode QUOTE() { return getToken(ExpressionTemplateParser.QUOTE, 0); }
		public QmContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qm; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterQm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitQm(this);
		}
	}

	public final QmContext qm() throws RecognitionException {
		QmContext _localctx = new QmContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_qm);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(481);
			match(QUOTE);
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

	public static class BsContext extends ParserRuleContext {
		public TerminalNode BACKSLASH() { return getToken(ExpressionTemplateParser.BACKSLASH, 0); }
		public BsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterBs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitBs(this);
		}
	}

	public final BsContext bs() throws RecognitionException {
		BsContext _localctx = new BsContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_bs);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(483);
			match(BACKSLASH);
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

	public static class DigitContext extends ParserRuleContext {
		public TerminalNode ZERO() { return getToken(ExpressionTemplateParser.ZERO, 0); }
		public TerminalNode ONE() { return getToken(ExpressionTemplateParser.ONE, 0); }
		public TerminalNode TWO() { return getToken(ExpressionTemplateParser.TWO, 0); }
		public TerminalNode THREE() { return getToken(ExpressionTemplateParser.THREE, 0); }
		public TerminalNode FOUR() { return getToken(ExpressionTemplateParser.FOUR, 0); }
		public TerminalNode FIVE() { return getToken(ExpressionTemplateParser.FIVE, 0); }
		public TerminalNode SIX() { return getToken(ExpressionTemplateParser.SIX, 0); }
		public TerminalNode SEVEN() { return getToken(ExpressionTemplateParser.SEVEN, 0); }
		public TerminalNode EIGHT() { return getToken(ExpressionTemplateParser.EIGHT, 0); }
		public TerminalNode NINE() { return getToken(ExpressionTemplateParser.NINE, 0); }
		public DigitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_digit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterDigit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitDigit(this);
		}
	}

	public final DigitContext digit() throws RecognitionException {
		DigitContext _localctx = new DigitContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_digit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(485);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ZERO) | (1L << ONE) | (1L << TWO) | (1L << THREE) | (1L << FOUR) | (1L << FIVE) | (1L << SIX) | (1L << SEVEN) | (1L << EIGHT) | (1L << NINE))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
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

	public static class ZeroContext extends ParserRuleContext {
		public TerminalNode ZERO() { return getToken(ExpressionTemplateParser.ZERO, 0); }
		public ZeroContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_zero; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterZero(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitZero(this);
		}
	}

	public final ZeroContext zero() throws RecognitionException {
		ZeroContext _localctx = new ZeroContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_zero);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(487);
			match(ZERO);
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

	public static class DigitnonzeroContext extends ParserRuleContext {
		public TerminalNode ONE() { return getToken(ExpressionTemplateParser.ONE, 0); }
		public TerminalNode TWO() { return getToken(ExpressionTemplateParser.TWO, 0); }
		public TerminalNode THREE() { return getToken(ExpressionTemplateParser.THREE, 0); }
		public TerminalNode FOUR() { return getToken(ExpressionTemplateParser.FOUR, 0); }
		public TerminalNode FIVE() { return getToken(ExpressionTemplateParser.FIVE, 0); }
		public TerminalNode SIX() { return getToken(ExpressionTemplateParser.SIX, 0); }
		public TerminalNode SEVEN() { return getToken(ExpressionTemplateParser.SEVEN, 0); }
		public TerminalNode EIGHT() { return getToken(ExpressionTemplateParser.EIGHT, 0); }
		public TerminalNode NINE() { return getToken(ExpressionTemplateParser.NINE, 0); }
		public DigitnonzeroContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_digitnonzero; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterDigitnonzero(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitDigitnonzero(this);
		}
	}

	public final DigitnonzeroContext digitnonzero() throws RecognitionException {
		DigitnonzeroContext _localctx = new DigitnonzeroContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_digitnonzero);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(489);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ONE) | (1L << TWO) | (1L << THREE) | (1L << FOUR) | (1L << FIVE) | (1L << SIX) | (1L << SEVEN) | (1L << EIGHT) | (1L << NINE))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
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

	public static class NonwsnonpipeContext extends ParserRuleContext {
		public TerminalNode EXCLAMATION() { return getToken(ExpressionTemplateParser.EXCLAMATION, 0); }
		public TerminalNode QUOTE() { return getToken(ExpressionTemplateParser.QUOTE, 0); }
		public TerminalNode POUND() { return getToken(ExpressionTemplateParser.POUND, 0); }
		public TerminalNode DOLLAR() { return getToken(ExpressionTemplateParser.DOLLAR, 0); }
		public TerminalNode PERCENT() { return getToken(ExpressionTemplateParser.PERCENT, 0); }
		public TerminalNode AMPERSAND() { return getToken(ExpressionTemplateParser.AMPERSAND, 0); }
		public TerminalNode APOSTROPHE() { return getToken(ExpressionTemplateParser.APOSTROPHE, 0); }
		public TerminalNode LEFT_PAREN() { return getToken(ExpressionTemplateParser.LEFT_PAREN, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(ExpressionTemplateParser.RIGHT_PAREN, 0); }
		public TerminalNode ASTERISK() { return getToken(ExpressionTemplateParser.ASTERISK, 0); }
		public TerminalNode PLUS() { return getToken(ExpressionTemplateParser.PLUS, 0); }
		public TerminalNode COMMA() { return getToken(ExpressionTemplateParser.COMMA, 0); }
		public TerminalNode DASH() { return getToken(ExpressionTemplateParser.DASH, 0); }
		public TerminalNode PERIOD() { return getToken(ExpressionTemplateParser.PERIOD, 0); }
		public TerminalNode SLASH() { return getToken(ExpressionTemplateParser.SLASH, 0); }
		public TerminalNode ZERO() { return getToken(ExpressionTemplateParser.ZERO, 0); }
		public TerminalNode ONE() { return getToken(ExpressionTemplateParser.ONE, 0); }
		public TerminalNode TWO() { return getToken(ExpressionTemplateParser.TWO, 0); }
		public TerminalNode THREE() { return getToken(ExpressionTemplateParser.THREE, 0); }
		public TerminalNode FOUR() { return getToken(ExpressionTemplateParser.FOUR, 0); }
		public TerminalNode FIVE() { return getToken(ExpressionTemplateParser.FIVE, 0); }
		public TerminalNode SIX() { return getToken(ExpressionTemplateParser.SIX, 0); }
		public TerminalNode SEVEN() { return getToken(ExpressionTemplateParser.SEVEN, 0); }
		public TerminalNode EIGHT() { return getToken(ExpressionTemplateParser.EIGHT, 0); }
		public TerminalNode NINE() { return getToken(ExpressionTemplateParser.NINE, 0); }
		public TerminalNode COLON() { return getToken(ExpressionTemplateParser.COLON, 0); }
		public TerminalNode SEMICOLON() { return getToken(ExpressionTemplateParser.SEMICOLON, 0); }
		public TerminalNode LESS_THAN() { return getToken(ExpressionTemplateParser.LESS_THAN, 0); }
		public TerminalNode EQUALS() { return getToken(ExpressionTemplateParser.EQUALS, 0); }
		public TerminalNode GREATER_THAN() { return getToken(ExpressionTemplateParser.GREATER_THAN, 0); }
		public TerminalNode QUESTION() { return getToken(ExpressionTemplateParser.QUESTION, 0); }
		public TerminalNode AT() { return getToken(ExpressionTemplateParser.AT, 0); }
		public TerminalNode CAP_A() { return getToken(ExpressionTemplateParser.CAP_A, 0); }
		public TerminalNode CAP_B() { return getToken(ExpressionTemplateParser.CAP_B, 0); }
		public TerminalNode CAP_C() { return getToken(ExpressionTemplateParser.CAP_C, 0); }
		public TerminalNode CAP_D() { return getToken(ExpressionTemplateParser.CAP_D, 0); }
		public TerminalNode CAP_E() { return getToken(ExpressionTemplateParser.CAP_E, 0); }
		public TerminalNode CAP_F() { return getToken(ExpressionTemplateParser.CAP_F, 0); }
		public TerminalNode CAP_G() { return getToken(ExpressionTemplateParser.CAP_G, 0); }
		public TerminalNode CAP_H() { return getToken(ExpressionTemplateParser.CAP_H, 0); }
		public TerminalNode CAP_I() { return getToken(ExpressionTemplateParser.CAP_I, 0); }
		public TerminalNode CAP_J() { return getToken(ExpressionTemplateParser.CAP_J, 0); }
		public TerminalNode CAP_K() { return getToken(ExpressionTemplateParser.CAP_K, 0); }
		public TerminalNode CAP_L() { return getToken(ExpressionTemplateParser.CAP_L, 0); }
		public TerminalNode CAP_M() { return getToken(ExpressionTemplateParser.CAP_M, 0); }
		public TerminalNode CAP_N() { return getToken(ExpressionTemplateParser.CAP_N, 0); }
		public TerminalNode CAP_O() { return getToken(ExpressionTemplateParser.CAP_O, 0); }
		public TerminalNode CAP_P() { return getToken(ExpressionTemplateParser.CAP_P, 0); }
		public TerminalNode CAP_Q() { return getToken(ExpressionTemplateParser.CAP_Q, 0); }
		public TerminalNode CAP_R() { return getToken(ExpressionTemplateParser.CAP_R, 0); }
		public TerminalNode CAP_S() { return getToken(ExpressionTemplateParser.CAP_S, 0); }
		public TerminalNode CAP_T() { return getToken(ExpressionTemplateParser.CAP_T, 0); }
		public TerminalNode CAP_U() { return getToken(ExpressionTemplateParser.CAP_U, 0); }
		public TerminalNode CAP_V() { return getToken(ExpressionTemplateParser.CAP_V, 0); }
		public TerminalNode CAP_W() { return getToken(ExpressionTemplateParser.CAP_W, 0); }
		public TerminalNode CAP_X() { return getToken(ExpressionTemplateParser.CAP_X, 0); }
		public TerminalNode CAP_Y() { return getToken(ExpressionTemplateParser.CAP_Y, 0); }
		public TerminalNode CAP_Z() { return getToken(ExpressionTemplateParser.CAP_Z, 0); }
		public TerminalNode LEFT_BRACE() { return getToken(ExpressionTemplateParser.LEFT_BRACE, 0); }
		public TerminalNode BACKSLASH() { return getToken(ExpressionTemplateParser.BACKSLASH, 0); }
		public TerminalNode RIGHT_BRACE() { return getToken(ExpressionTemplateParser.RIGHT_BRACE, 0); }
		public TerminalNode CARAT() { return getToken(ExpressionTemplateParser.CARAT, 0); }
		public TerminalNode UNDERSCORE() { return getToken(ExpressionTemplateParser.UNDERSCORE, 0); }
		public TerminalNode ACCENT() { return getToken(ExpressionTemplateParser.ACCENT, 0); }
		public TerminalNode A() { return getToken(ExpressionTemplateParser.A, 0); }
		public TerminalNode B() { return getToken(ExpressionTemplateParser.B, 0); }
		public TerminalNode C() { return getToken(ExpressionTemplateParser.C, 0); }
		public TerminalNode D() { return getToken(ExpressionTemplateParser.D, 0); }
		public TerminalNode E() { return getToken(ExpressionTemplateParser.E, 0); }
		public TerminalNode F() { return getToken(ExpressionTemplateParser.F, 0); }
		public TerminalNode G() { return getToken(ExpressionTemplateParser.G, 0); }
		public TerminalNode H() { return getToken(ExpressionTemplateParser.H, 0); }
		public TerminalNode I() { return getToken(ExpressionTemplateParser.I, 0); }
		public TerminalNode J() { return getToken(ExpressionTemplateParser.J, 0); }
		public TerminalNode K() { return getToken(ExpressionTemplateParser.K, 0); }
		public TerminalNode L() { return getToken(ExpressionTemplateParser.L, 0); }
		public TerminalNode M() { return getToken(ExpressionTemplateParser.M, 0); }
		public TerminalNode N() { return getToken(ExpressionTemplateParser.N, 0); }
		public TerminalNode O() { return getToken(ExpressionTemplateParser.O, 0); }
		public TerminalNode P() { return getToken(ExpressionTemplateParser.P, 0); }
		public TerminalNode Q() { return getToken(ExpressionTemplateParser.Q, 0); }
		public TerminalNode R() { return getToken(ExpressionTemplateParser.R, 0); }
		public TerminalNode S() { return getToken(ExpressionTemplateParser.S, 0); }
		public TerminalNode T() { return getToken(ExpressionTemplateParser.T, 0); }
		public TerminalNode U() { return getToken(ExpressionTemplateParser.U, 0); }
		public TerminalNode V() { return getToken(ExpressionTemplateParser.V, 0); }
		public TerminalNode W() { return getToken(ExpressionTemplateParser.W, 0); }
		public TerminalNode X() { return getToken(ExpressionTemplateParser.X, 0); }
		public TerminalNode Y() { return getToken(ExpressionTemplateParser.Y, 0); }
		public TerminalNode Z() { return getToken(ExpressionTemplateParser.Z, 0); }
		public TerminalNode LEFT_CURLY_BRACE() { return getToken(ExpressionTemplateParser.LEFT_CURLY_BRACE, 0); }
		public TerminalNode RIGHT_CURLY_BRACE() { return getToken(ExpressionTemplateParser.RIGHT_CURLY_BRACE, 0); }
		public TerminalNode TILDE() { return getToken(ExpressionTemplateParser.TILDE, 0); }
		public Utf8_2Context utf8_2() {
			return getRuleContext(Utf8_2Context.class,0);
		}
		public Utf8_3Context utf8_3() {
			return getRuleContext(Utf8_3Context.class,0);
		}
		public Utf8_4Context utf8_4() {
			return getRuleContext(Utf8_4Context.class,0);
		}
		public NonwsnonpipeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nonwsnonpipe; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterNonwsnonpipe(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitNonwsnonpipe(this);
		}
	}

	public final NonwsnonpipeContext nonwsnonpipe() throws RecognitionException {
		NonwsnonpipeContext _localctx = new NonwsnonpipeContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_nonwsnonpipe);
		int _la;
		try {
			setState(496);
			switch (_input.LA(1)) {
			case EXCLAMATION:
			case QUOTE:
			case POUND:
			case DOLLAR:
			case PERCENT:
			case AMPERSAND:
			case APOSTROPHE:
			case LEFT_PAREN:
			case RIGHT_PAREN:
			case ASTERISK:
			case PLUS:
			case COMMA:
			case DASH:
			case PERIOD:
			case SLASH:
			case ZERO:
			case ONE:
			case TWO:
			case THREE:
			case FOUR:
			case FIVE:
			case SIX:
			case SEVEN:
			case EIGHT:
			case NINE:
			case COLON:
			case SEMICOLON:
			case LESS_THAN:
			case EQUALS:
			case GREATER_THAN:
			case QUESTION:
			case AT:
			case CAP_A:
			case CAP_B:
			case CAP_C:
			case CAP_D:
			case CAP_E:
			case CAP_F:
			case CAP_G:
			case CAP_H:
			case CAP_I:
			case CAP_J:
			case CAP_K:
			case CAP_L:
			case CAP_M:
			case CAP_N:
			case CAP_O:
			case CAP_P:
			case CAP_Q:
			case CAP_R:
			case CAP_S:
			case CAP_T:
			case CAP_U:
			case CAP_V:
			case CAP_W:
			case CAP_X:
			case CAP_Y:
			case CAP_Z:
			case LEFT_BRACE:
			case BACKSLASH:
			case RIGHT_BRACE:
			case CARAT:
			case UNDERSCORE:
			case ACCENT:
			case A:
			case B:
			case C:
			case D:
			case E:
			case F:
			case G:
			case H:
			case I:
			case J:
			case K:
			case L:
			case M:
			case N:
			case O:
			case P:
			case Q:
			case R:
			case S:
			case T:
			case U:
			case V:
			case W:
			case X:
			case Y:
			case Z:
			case LEFT_CURLY_BRACE:
				enterOuterAlt(_localctx, 1);
				{
				setState(491);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EXCLAMATION) | (1L << QUOTE) | (1L << POUND) | (1L << DOLLAR) | (1L << PERCENT) | (1L << AMPERSAND) | (1L << APOSTROPHE) | (1L << LEFT_PAREN) | (1L << RIGHT_PAREN) | (1L << ASTERISK) | (1L << PLUS) | (1L << COMMA) | (1L << DASH) | (1L << PERIOD) | (1L << SLASH) | (1L << ZERO) | (1L << ONE) | (1L << TWO) | (1L << THREE) | (1L << FOUR) | (1L << FIVE) | (1L << SIX) | (1L << SEVEN) | (1L << EIGHT) | (1L << NINE) | (1L << COLON) | (1L << SEMICOLON) | (1L << LESS_THAN) | (1L << EQUALS) | (1L << GREATER_THAN) | (1L << QUESTION) | (1L << AT) | (1L << CAP_A) | (1L << CAP_B) | (1L << CAP_C) | (1L << CAP_D) | (1L << CAP_E) | (1L << CAP_F) | (1L << CAP_G) | (1L << CAP_H) | (1L << CAP_I) | (1L << CAP_J) | (1L << CAP_K) | (1L << CAP_L) | (1L << CAP_M) | (1L << CAP_N) | (1L << CAP_O) | (1L << CAP_P) | (1L << CAP_Q) | (1L << CAP_R) | (1L << CAP_S) | (1L << CAP_T) | (1L << CAP_U) | (1L << CAP_V) | (1L << CAP_W) | (1L << CAP_X) | (1L << CAP_Y) | (1L << CAP_Z) | (1L << LEFT_BRACE))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (BACKSLASH - 64)) | (1L << (RIGHT_BRACE - 64)) | (1L << (CARAT - 64)) | (1L << (UNDERSCORE - 64)) | (1L << (ACCENT - 64)) | (1L << (A - 64)) | (1L << (B - 64)) | (1L << (C - 64)) | (1L << (D - 64)) | (1L << (E - 64)) | (1L << (F - 64)) | (1L << (G - 64)) | (1L << (H - 64)) | (1L << (I - 64)) | (1L << (J - 64)) | (1L << (K - 64)) | (1L << (L - 64)) | (1L << (M - 64)) | (1L << (N - 64)) | (1L << (O - 64)) | (1L << (P - 64)) | (1L << (Q - 64)) | (1L << (R - 64)) | (1L << (S - 64)) | (1L << (T - 64)) | (1L << (U - 64)) | (1L << (V - 64)) | (1L << (W - 64)) | (1L << (X - 64)) | (1L << (Y - 64)) | (1L << (Z - 64)) | (1L << (LEFT_CURLY_BRACE - 64)))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			case RIGHT_CURLY_BRACE:
			case TILDE:
				enterOuterAlt(_localctx, 2);
				{
				setState(492);
				_la = _input.LA(1);
				if ( !(_la==RIGHT_CURLY_BRACE || _la==TILDE) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			case U_00C2:
			case U_00C3:
			case U_00C4:
			case U_00C5:
			case U_00C6:
			case U_00C7:
			case U_00C8:
			case U_00C9:
			case U_00CA:
			case U_00CB:
			case U_00CC:
			case U_00CD:
			case U_00CE:
			case U_00CF:
			case U_00D0:
			case U_00D1:
			case U_00D2:
			case U_00D3:
			case U_00D4:
			case U_00D5:
			case U_00D6:
			case U_00D7:
			case U_00D8:
			case U_00D9:
			case U_00DA:
			case U_00DB:
			case U_00DC:
			case U_00DD:
			case U_00DE:
			case U_00DF:
				enterOuterAlt(_localctx, 3);
				{
				setState(493);
				utf8_2();
				}
				break;
			case U_00E0:
			case U_00E1:
			case U_00E2:
			case U_00E3:
			case U_00E4:
			case U_00E5:
			case U_00E6:
			case U_00E7:
			case U_00E8:
			case U_00E9:
			case U_00EA:
			case U_00EB:
			case U_00EC:
			case U_00ED:
			case U_00EE:
			case U_00EF:
				enterOuterAlt(_localctx, 4);
				{
				setState(494);
				utf8_3();
				}
				break;
			case U_00F0:
			case U_00F1:
			case U_00F2:
			case U_00F3:
			case U_00F4:
				enterOuterAlt(_localctx, 5);
				{
				setState(495);
				utf8_4();
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

	public static class AnynonescapedcharContext extends ParserRuleContext {
		public HtabContext htab() {
			return getRuleContext(HtabContext.class,0);
		}
		public CrContext cr() {
			return getRuleContext(CrContext.class,0);
		}
		public LfContext lf() {
			return getRuleContext(LfContext.class,0);
		}
		public TerminalNode SPACE() { return getToken(ExpressionTemplateParser.SPACE, 0); }
		public TerminalNode EXCLAMATION() { return getToken(ExpressionTemplateParser.EXCLAMATION, 0); }
		public TerminalNode POUND() { return getToken(ExpressionTemplateParser.POUND, 0); }
		public TerminalNode DOLLAR() { return getToken(ExpressionTemplateParser.DOLLAR, 0); }
		public TerminalNode PERCENT() { return getToken(ExpressionTemplateParser.PERCENT, 0); }
		public TerminalNode AMPERSAND() { return getToken(ExpressionTemplateParser.AMPERSAND, 0); }
		public TerminalNode APOSTROPHE() { return getToken(ExpressionTemplateParser.APOSTROPHE, 0); }
		public TerminalNode LEFT_PAREN() { return getToken(ExpressionTemplateParser.LEFT_PAREN, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(ExpressionTemplateParser.RIGHT_PAREN, 0); }
		public TerminalNode ASTERISK() { return getToken(ExpressionTemplateParser.ASTERISK, 0); }
		public TerminalNode PLUS() { return getToken(ExpressionTemplateParser.PLUS, 0); }
		public TerminalNode COMMA() { return getToken(ExpressionTemplateParser.COMMA, 0); }
		public TerminalNode DASH() { return getToken(ExpressionTemplateParser.DASH, 0); }
		public TerminalNode PERIOD() { return getToken(ExpressionTemplateParser.PERIOD, 0); }
		public TerminalNode SLASH() { return getToken(ExpressionTemplateParser.SLASH, 0); }
		public TerminalNode ZERO() { return getToken(ExpressionTemplateParser.ZERO, 0); }
		public TerminalNode ONE() { return getToken(ExpressionTemplateParser.ONE, 0); }
		public TerminalNode TWO() { return getToken(ExpressionTemplateParser.TWO, 0); }
		public TerminalNode THREE() { return getToken(ExpressionTemplateParser.THREE, 0); }
		public TerminalNode FOUR() { return getToken(ExpressionTemplateParser.FOUR, 0); }
		public TerminalNode FIVE() { return getToken(ExpressionTemplateParser.FIVE, 0); }
		public TerminalNode SIX() { return getToken(ExpressionTemplateParser.SIX, 0); }
		public TerminalNode SEVEN() { return getToken(ExpressionTemplateParser.SEVEN, 0); }
		public TerminalNode EIGHT() { return getToken(ExpressionTemplateParser.EIGHT, 0); }
		public TerminalNode NINE() { return getToken(ExpressionTemplateParser.NINE, 0); }
		public TerminalNode COLON() { return getToken(ExpressionTemplateParser.COLON, 0); }
		public TerminalNode SEMICOLON() { return getToken(ExpressionTemplateParser.SEMICOLON, 0); }
		public TerminalNode LESS_THAN() { return getToken(ExpressionTemplateParser.LESS_THAN, 0); }
		public TerminalNode EQUALS() { return getToken(ExpressionTemplateParser.EQUALS, 0); }
		public TerminalNode GREATER_THAN() { return getToken(ExpressionTemplateParser.GREATER_THAN, 0); }
		public TerminalNode QUESTION() { return getToken(ExpressionTemplateParser.QUESTION, 0); }
		public TerminalNode AT() { return getToken(ExpressionTemplateParser.AT, 0); }
		public TerminalNode CAP_A() { return getToken(ExpressionTemplateParser.CAP_A, 0); }
		public TerminalNode CAP_B() { return getToken(ExpressionTemplateParser.CAP_B, 0); }
		public TerminalNode CAP_C() { return getToken(ExpressionTemplateParser.CAP_C, 0); }
		public TerminalNode CAP_D() { return getToken(ExpressionTemplateParser.CAP_D, 0); }
		public TerminalNode CAP_E() { return getToken(ExpressionTemplateParser.CAP_E, 0); }
		public TerminalNode CAP_F() { return getToken(ExpressionTemplateParser.CAP_F, 0); }
		public TerminalNode CAP_G() { return getToken(ExpressionTemplateParser.CAP_G, 0); }
		public TerminalNode CAP_H() { return getToken(ExpressionTemplateParser.CAP_H, 0); }
		public TerminalNode CAP_I() { return getToken(ExpressionTemplateParser.CAP_I, 0); }
		public TerminalNode CAP_J() { return getToken(ExpressionTemplateParser.CAP_J, 0); }
		public TerminalNode CAP_K() { return getToken(ExpressionTemplateParser.CAP_K, 0); }
		public TerminalNode CAP_L() { return getToken(ExpressionTemplateParser.CAP_L, 0); }
		public TerminalNode CAP_M() { return getToken(ExpressionTemplateParser.CAP_M, 0); }
		public TerminalNode CAP_N() { return getToken(ExpressionTemplateParser.CAP_N, 0); }
		public TerminalNode CAP_O() { return getToken(ExpressionTemplateParser.CAP_O, 0); }
		public TerminalNode CAP_P() { return getToken(ExpressionTemplateParser.CAP_P, 0); }
		public TerminalNode CAP_Q() { return getToken(ExpressionTemplateParser.CAP_Q, 0); }
		public TerminalNode CAP_R() { return getToken(ExpressionTemplateParser.CAP_R, 0); }
		public TerminalNode CAP_S() { return getToken(ExpressionTemplateParser.CAP_S, 0); }
		public TerminalNode CAP_T() { return getToken(ExpressionTemplateParser.CAP_T, 0); }
		public TerminalNode CAP_U() { return getToken(ExpressionTemplateParser.CAP_U, 0); }
		public TerminalNode CAP_V() { return getToken(ExpressionTemplateParser.CAP_V, 0); }
		public TerminalNode CAP_W() { return getToken(ExpressionTemplateParser.CAP_W, 0); }
		public TerminalNode CAP_X() { return getToken(ExpressionTemplateParser.CAP_X, 0); }
		public TerminalNode CAP_Y() { return getToken(ExpressionTemplateParser.CAP_Y, 0); }
		public TerminalNode CAP_Z() { return getToken(ExpressionTemplateParser.CAP_Z, 0); }
		public TerminalNode LEFT_BRACE() { return getToken(ExpressionTemplateParser.LEFT_BRACE, 0); }
		public TerminalNode RIGHT_BRACE() { return getToken(ExpressionTemplateParser.RIGHT_BRACE, 0); }
		public TerminalNode CARAT() { return getToken(ExpressionTemplateParser.CARAT, 0); }
		public TerminalNode UNDERSCORE() { return getToken(ExpressionTemplateParser.UNDERSCORE, 0); }
		public TerminalNode ACCENT() { return getToken(ExpressionTemplateParser.ACCENT, 0); }
		public TerminalNode A() { return getToken(ExpressionTemplateParser.A, 0); }
		public TerminalNode B() { return getToken(ExpressionTemplateParser.B, 0); }
		public TerminalNode C() { return getToken(ExpressionTemplateParser.C, 0); }
		public TerminalNode D() { return getToken(ExpressionTemplateParser.D, 0); }
		public TerminalNode E() { return getToken(ExpressionTemplateParser.E, 0); }
		public TerminalNode F() { return getToken(ExpressionTemplateParser.F, 0); }
		public TerminalNode G() { return getToken(ExpressionTemplateParser.G, 0); }
		public TerminalNode H() { return getToken(ExpressionTemplateParser.H, 0); }
		public TerminalNode I() { return getToken(ExpressionTemplateParser.I, 0); }
		public TerminalNode J() { return getToken(ExpressionTemplateParser.J, 0); }
		public TerminalNode K() { return getToken(ExpressionTemplateParser.K, 0); }
		public TerminalNode L() { return getToken(ExpressionTemplateParser.L, 0); }
		public TerminalNode M() { return getToken(ExpressionTemplateParser.M, 0); }
		public TerminalNode N() { return getToken(ExpressionTemplateParser.N, 0); }
		public TerminalNode O() { return getToken(ExpressionTemplateParser.O, 0); }
		public TerminalNode P() { return getToken(ExpressionTemplateParser.P, 0); }
		public TerminalNode Q() { return getToken(ExpressionTemplateParser.Q, 0); }
		public TerminalNode R() { return getToken(ExpressionTemplateParser.R, 0); }
		public TerminalNode S() { return getToken(ExpressionTemplateParser.S, 0); }
		public TerminalNode T() { return getToken(ExpressionTemplateParser.T, 0); }
		public TerminalNode U() { return getToken(ExpressionTemplateParser.U, 0); }
		public TerminalNode V() { return getToken(ExpressionTemplateParser.V, 0); }
		public TerminalNode W() { return getToken(ExpressionTemplateParser.W, 0); }
		public TerminalNode X() { return getToken(ExpressionTemplateParser.X, 0); }
		public TerminalNode Y() { return getToken(ExpressionTemplateParser.Y, 0); }
		public TerminalNode Z() { return getToken(ExpressionTemplateParser.Z, 0); }
		public TerminalNode LEFT_CURLY_BRACE() { return getToken(ExpressionTemplateParser.LEFT_CURLY_BRACE, 0); }
		public TerminalNode PIPE() { return getToken(ExpressionTemplateParser.PIPE, 0); }
		public TerminalNode RIGHT_CURLY_BRACE() { return getToken(ExpressionTemplateParser.RIGHT_CURLY_BRACE, 0); }
		public TerminalNode TILDE() { return getToken(ExpressionTemplateParser.TILDE, 0); }
		public Utf8_2Context utf8_2() {
			return getRuleContext(Utf8_2Context.class,0);
		}
		public Utf8_3Context utf8_3() {
			return getRuleContext(Utf8_3Context.class,0);
		}
		public Utf8_4Context utf8_4() {
			return getRuleContext(Utf8_4Context.class,0);
		}
		public AnynonescapedcharContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_anynonescapedchar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterAnynonescapedchar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitAnynonescapedchar(this);
		}
	}

	public final AnynonescapedcharContext anynonescapedchar() throws RecognitionException {
		AnynonescapedcharContext _localctx = new AnynonescapedcharContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_anynonescapedchar);
		int _la;
		try {
			setState(507);
			switch (_input.LA(1)) {
			case TAB:
				enterOuterAlt(_localctx, 1);
				{
				setState(498);
				htab();
				}
				break;
			case CR:
				enterOuterAlt(_localctx, 2);
				{
				setState(499);
				cr();
				}
				break;
			case LF:
				enterOuterAlt(_localctx, 3);
				{
				setState(500);
				lf();
				}
				break;
			case SPACE:
			case EXCLAMATION:
				enterOuterAlt(_localctx, 4);
				{
				setState(501);
				_la = _input.LA(1);
				if ( !(_la==SPACE || _la==EXCLAMATION) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			case POUND:
			case DOLLAR:
			case PERCENT:
			case AMPERSAND:
			case APOSTROPHE:
			case LEFT_PAREN:
			case RIGHT_PAREN:
			case ASTERISK:
			case PLUS:
			case COMMA:
			case DASH:
			case PERIOD:
			case SLASH:
			case ZERO:
			case ONE:
			case TWO:
			case THREE:
			case FOUR:
			case FIVE:
			case SIX:
			case SEVEN:
			case EIGHT:
			case NINE:
			case COLON:
			case SEMICOLON:
			case LESS_THAN:
			case EQUALS:
			case GREATER_THAN:
			case QUESTION:
			case AT:
			case CAP_A:
			case CAP_B:
			case CAP_C:
			case CAP_D:
			case CAP_E:
			case CAP_F:
			case CAP_G:
			case CAP_H:
			case CAP_I:
			case CAP_J:
			case CAP_K:
			case CAP_L:
			case CAP_M:
			case CAP_N:
			case CAP_O:
			case CAP_P:
			case CAP_Q:
			case CAP_R:
			case CAP_S:
			case CAP_T:
			case CAP_U:
			case CAP_V:
			case CAP_W:
			case CAP_X:
			case CAP_Y:
			case CAP_Z:
			case LEFT_BRACE:
				enterOuterAlt(_localctx, 5);
				{
				setState(502);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << POUND) | (1L << DOLLAR) | (1L << PERCENT) | (1L << AMPERSAND) | (1L << APOSTROPHE) | (1L << LEFT_PAREN) | (1L << RIGHT_PAREN) | (1L << ASTERISK) | (1L << PLUS) | (1L << COMMA) | (1L << DASH) | (1L << PERIOD) | (1L << SLASH) | (1L << ZERO) | (1L << ONE) | (1L << TWO) | (1L << THREE) | (1L << FOUR) | (1L << FIVE) | (1L << SIX) | (1L << SEVEN) | (1L << EIGHT) | (1L << NINE) | (1L << COLON) | (1L << SEMICOLON) | (1L << LESS_THAN) | (1L << EQUALS) | (1L << GREATER_THAN) | (1L << QUESTION) | (1L << AT) | (1L << CAP_A) | (1L << CAP_B) | (1L << CAP_C) | (1L << CAP_D) | (1L << CAP_E) | (1L << CAP_F) | (1L << CAP_G) | (1L << CAP_H) | (1L << CAP_I) | (1L << CAP_J) | (1L << CAP_K) | (1L << CAP_L) | (1L << CAP_M) | (1L << CAP_N) | (1L << CAP_O) | (1L << CAP_P) | (1L << CAP_Q) | (1L << CAP_R) | (1L << CAP_S) | (1L << CAP_T) | (1L << CAP_U) | (1L << CAP_V) | (1L << CAP_W) | (1L << CAP_X) | (1L << CAP_Y) | (1L << CAP_Z) | (1L << LEFT_BRACE))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			case RIGHT_BRACE:
			case CARAT:
			case UNDERSCORE:
			case ACCENT:
			case A:
			case B:
			case C:
			case D:
			case E:
			case F:
			case G:
			case H:
			case I:
			case J:
			case K:
			case L:
			case M:
			case N:
			case O:
			case P:
			case Q:
			case R:
			case S:
			case T:
			case U:
			case V:
			case W:
			case X:
			case Y:
			case Z:
			case LEFT_CURLY_BRACE:
			case PIPE:
			case RIGHT_CURLY_BRACE:
			case TILDE:
				enterOuterAlt(_localctx, 6);
				{
				setState(503);
				_la = _input.LA(1);
				if ( !(((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (RIGHT_BRACE - 65)) | (1L << (CARAT - 65)) | (1L << (UNDERSCORE - 65)) | (1L << (ACCENT - 65)) | (1L << (A - 65)) | (1L << (B - 65)) | (1L << (C - 65)) | (1L << (D - 65)) | (1L << (E - 65)) | (1L << (F - 65)) | (1L << (G - 65)) | (1L << (H - 65)) | (1L << (I - 65)) | (1L << (J - 65)) | (1L << (K - 65)) | (1L << (L - 65)) | (1L << (M - 65)) | (1L << (N - 65)) | (1L << (O - 65)) | (1L << (P - 65)) | (1L << (Q - 65)) | (1L << (R - 65)) | (1L << (S - 65)) | (1L << (T - 65)) | (1L << (U - 65)) | (1L << (V - 65)) | (1L << (W - 65)) | (1L << (X - 65)) | (1L << (Y - 65)) | (1L << (Z - 65)) | (1L << (LEFT_CURLY_BRACE - 65)) | (1L << (PIPE - 65)) | (1L << (RIGHT_CURLY_BRACE - 65)) | (1L << (TILDE - 65)))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			case U_00C2:
			case U_00C3:
			case U_00C4:
			case U_00C5:
			case U_00C6:
			case U_00C7:
			case U_00C8:
			case U_00C9:
			case U_00CA:
			case U_00CB:
			case U_00CC:
			case U_00CD:
			case U_00CE:
			case U_00CF:
			case U_00D0:
			case U_00D1:
			case U_00D2:
			case U_00D3:
			case U_00D4:
			case U_00D5:
			case U_00D6:
			case U_00D7:
			case U_00D8:
			case U_00D9:
			case U_00DA:
			case U_00DB:
			case U_00DC:
			case U_00DD:
			case U_00DE:
			case U_00DF:
				enterOuterAlt(_localctx, 7);
				{
				setState(504);
				utf8_2();
				}
				break;
			case U_00E0:
			case U_00E1:
			case U_00E2:
			case U_00E3:
			case U_00E4:
			case U_00E5:
			case U_00E6:
			case U_00E7:
			case U_00E8:
			case U_00E9:
			case U_00EA:
			case U_00EB:
			case U_00EC:
			case U_00ED:
			case U_00EE:
			case U_00EF:
				enterOuterAlt(_localctx, 8);
				{
				setState(505);
				utf8_3();
				}
				break;
			case U_00F0:
			case U_00F1:
			case U_00F2:
			case U_00F3:
			case U_00F4:
				enterOuterAlt(_localctx, 9);
				{
				setState(506);
				utf8_4();
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

	public static class EscapedcharContext extends ParserRuleContext {
		public List<BsContext> bs() {
			return getRuleContexts(BsContext.class);
		}
		public BsContext bs(int i) {
			return getRuleContext(BsContext.class,i);
		}
		public QmContext qm() {
			return getRuleContext(QmContext.class,0);
		}
		public EscapedcharContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_escapedchar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterEscapedchar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitEscapedchar(this);
		}
	}

	public final EscapedcharContext escapedchar() throws RecognitionException {
		EscapedcharContext _localctx = new EscapedcharContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_escapedchar);
		try {
			setState(515);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(509);
				bs();
				setState(510);
				qm();
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(512);
				bs();
				setState(513);
				bs();
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

	public static class Utf8_2Context extends ParserRuleContext {
		public Utf8_tailContext utf8_tail() {
			return getRuleContext(Utf8_tailContext.class,0);
		}
		public TerminalNode U_00C2() { return getToken(ExpressionTemplateParser.U_00C2, 0); }
		public TerminalNode U_00C3() { return getToken(ExpressionTemplateParser.U_00C3, 0); }
		public TerminalNode U_00C4() { return getToken(ExpressionTemplateParser.U_00C4, 0); }
		public TerminalNode U_00C5() { return getToken(ExpressionTemplateParser.U_00C5, 0); }
		public TerminalNode U_00C6() { return getToken(ExpressionTemplateParser.U_00C6, 0); }
		public TerminalNode U_00C7() { return getToken(ExpressionTemplateParser.U_00C7, 0); }
		public TerminalNode U_00C8() { return getToken(ExpressionTemplateParser.U_00C8, 0); }
		public TerminalNode U_00C9() { return getToken(ExpressionTemplateParser.U_00C9, 0); }
		public TerminalNode U_00CA() { return getToken(ExpressionTemplateParser.U_00CA, 0); }
		public TerminalNode U_00CB() { return getToken(ExpressionTemplateParser.U_00CB, 0); }
		public TerminalNode U_00CC() { return getToken(ExpressionTemplateParser.U_00CC, 0); }
		public TerminalNode U_00CD() { return getToken(ExpressionTemplateParser.U_00CD, 0); }
		public TerminalNode U_00CE() { return getToken(ExpressionTemplateParser.U_00CE, 0); }
		public TerminalNode U_00CF() { return getToken(ExpressionTemplateParser.U_00CF, 0); }
		public TerminalNode U_00D0() { return getToken(ExpressionTemplateParser.U_00D0, 0); }
		public TerminalNode U_00D1() { return getToken(ExpressionTemplateParser.U_00D1, 0); }
		public TerminalNode U_00D2() { return getToken(ExpressionTemplateParser.U_00D2, 0); }
		public TerminalNode U_00D3() { return getToken(ExpressionTemplateParser.U_00D3, 0); }
		public TerminalNode U_00D4() { return getToken(ExpressionTemplateParser.U_00D4, 0); }
		public TerminalNode U_00D5() { return getToken(ExpressionTemplateParser.U_00D5, 0); }
		public TerminalNode U_00D6() { return getToken(ExpressionTemplateParser.U_00D6, 0); }
		public TerminalNode U_00D7() { return getToken(ExpressionTemplateParser.U_00D7, 0); }
		public TerminalNode U_00D8() { return getToken(ExpressionTemplateParser.U_00D8, 0); }
		public TerminalNode U_00D9() { return getToken(ExpressionTemplateParser.U_00D9, 0); }
		public TerminalNode U_00DA() { return getToken(ExpressionTemplateParser.U_00DA, 0); }
		public TerminalNode U_00DB() { return getToken(ExpressionTemplateParser.U_00DB, 0); }
		public TerminalNode U_00DC() { return getToken(ExpressionTemplateParser.U_00DC, 0); }
		public TerminalNode U_00DD() { return getToken(ExpressionTemplateParser.U_00DD, 0); }
		public TerminalNode U_00DE() { return getToken(ExpressionTemplateParser.U_00DE, 0); }
		public TerminalNode U_00DF() { return getToken(ExpressionTemplateParser.U_00DF, 0); }
		public Utf8_2Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_utf8_2; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterUtf8_2(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitUtf8_2(this);
		}
	}

	public final Utf8_2Context utf8_2() throws RecognitionException {
		Utf8_2Context _localctx = new Utf8_2Context(_ctx, getState());
		enterRule(_localctx, 68, RULE_utf8_2);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(517);
			_la = _input.LA(1);
			if ( !(((((_la - 163)) & ~0x3f) == 0 && ((1L << (_la - 163)) & ((1L << (U_00C2 - 163)) | (1L << (U_00C3 - 163)) | (1L << (U_00C4 - 163)) | (1L << (U_00C5 - 163)) | (1L << (U_00C6 - 163)) | (1L << (U_00C7 - 163)) | (1L << (U_00C8 - 163)) | (1L << (U_00C9 - 163)) | (1L << (U_00CA - 163)) | (1L << (U_00CB - 163)) | (1L << (U_00CC - 163)) | (1L << (U_00CD - 163)) | (1L << (U_00CE - 163)) | (1L << (U_00CF - 163)) | (1L << (U_00D0 - 163)) | (1L << (U_00D1 - 163)) | (1L << (U_00D2 - 163)) | (1L << (U_00D3 - 163)) | (1L << (U_00D4 - 163)) | (1L << (U_00D5 - 163)) | (1L << (U_00D6 - 163)) | (1L << (U_00D7 - 163)) | (1L << (U_00D8 - 163)) | (1L << (U_00D9 - 163)) | (1L << (U_00DA - 163)) | (1L << (U_00DB - 163)) | (1L << (U_00DC - 163)) | (1L << (U_00DD - 163)) | (1L << (U_00DE - 163)) | (1L << (U_00DF - 163)))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(518);
			utf8_tail();
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

	public static class Utf8_3Context extends ParserRuleContext {
		public TerminalNode U_00E0() { return getToken(ExpressionTemplateParser.U_00E0, 0); }
		public List<Utf8_tailContext> utf8_tail() {
			return getRuleContexts(Utf8_tailContext.class);
		}
		public Utf8_tailContext utf8_tail(int i) {
			return getRuleContext(Utf8_tailContext.class,i);
		}
		public TerminalNode U_00A0() { return getToken(ExpressionTemplateParser.U_00A0, 0); }
		public TerminalNode U_00A1() { return getToken(ExpressionTemplateParser.U_00A1, 0); }
		public TerminalNode U_00A2() { return getToken(ExpressionTemplateParser.U_00A2, 0); }
		public TerminalNode U_00A3() { return getToken(ExpressionTemplateParser.U_00A3, 0); }
		public TerminalNode U_00A4() { return getToken(ExpressionTemplateParser.U_00A4, 0); }
		public TerminalNode U_00A5() { return getToken(ExpressionTemplateParser.U_00A5, 0); }
		public TerminalNode U_00A6() { return getToken(ExpressionTemplateParser.U_00A6, 0); }
		public TerminalNode U_00A7() { return getToken(ExpressionTemplateParser.U_00A7, 0); }
		public TerminalNode U_00A8() { return getToken(ExpressionTemplateParser.U_00A8, 0); }
		public TerminalNode U_00A9() { return getToken(ExpressionTemplateParser.U_00A9, 0); }
		public TerminalNode U_00AA() { return getToken(ExpressionTemplateParser.U_00AA, 0); }
		public TerminalNode U_00AB() { return getToken(ExpressionTemplateParser.U_00AB, 0); }
		public TerminalNode U_00AC() { return getToken(ExpressionTemplateParser.U_00AC, 0); }
		public TerminalNode U_00AD() { return getToken(ExpressionTemplateParser.U_00AD, 0); }
		public TerminalNode U_00AE() { return getToken(ExpressionTemplateParser.U_00AE, 0); }
		public TerminalNode U_00AF() { return getToken(ExpressionTemplateParser.U_00AF, 0); }
		public TerminalNode U_00B0() { return getToken(ExpressionTemplateParser.U_00B0, 0); }
		public TerminalNode U_00B1() { return getToken(ExpressionTemplateParser.U_00B1, 0); }
		public TerminalNode U_00B2() { return getToken(ExpressionTemplateParser.U_00B2, 0); }
		public TerminalNode U_00B3() { return getToken(ExpressionTemplateParser.U_00B3, 0); }
		public TerminalNode U_00B4() { return getToken(ExpressionTemplateParser.U_00B4, 0); }
		public TerminalNode U_00B5() { return getToken(ExpressionTemplateParser.U_00B5, 0); }
		public TerminalNode U_00B6() { return getToken(ExpressionTemplateParser.U_00B6, 0); }
		public TerminalNode U_00B7() { return getToken(ExpressionTemplateParser.U_00B7, 0); }
		public TerminalNode U_00B8() { return getToken(ExpressionTemplateParser.U_00B8, 0); }
		public TerminalNode U_00B9() { return getToken(ExpressionTemplateParser.U_00B9, 0); }
		public TerminalNode U_00BA() { return getToken(ExpressionTemplateParser.U_00BA, 0); }
		public TerminalNode U_00BB() { return getToken(ExpressionTemplateParser.U_00BB, 0); }
		public TerminalNode U_00BC() { return getToken(ExpressionTemplateParser.U_00BC, 0); }
		public TerminalNode U_00BD() { return getToken(ExpressionTemplateParser.U_00BD, 0); }
		public TerminalNode U_00BE() { return getToken(ExpressionTemplateParser.U_00BE, 0); }
		public TerminalNode U_00BF() { return getToken(ExpressionTemplateParser.U_00BF, 0); }
		public TerminalNode U_00E1() { return getToken(ExpressionTemplateParser.U_00E1, 0); }
		public TerminalNode U_00E2() { return getToken(ExpressionTemplateParser.U_00E2, 0); }
		public TerminalNode U_00E3() { return getToken(ExpressionTemplateParser.U_00E3, 0); }
		public TerminalNode U_00E4() { return getToken(ExpressionTemplateParser.U_00E4, 0); }
		public TerminalNode U_00E5() { return getToken(ExpressionTemplateParser.U_00E5, 0); }
		public TerminalNode U_00E6() { return getToken(ExpressionTemplateParser.U_00E6, 0); }
		public TerminalNode U_00E7() { return getToken(ExpressionTemplateParser.U_00E7, 0); }
		public TerminalNode U_00E8() { return getToken(ExpressionTemplateParser.U_00E8, 0); }
		public TerminalNode U_00E9() { return getToken(ExpressionTemplateParser.U_00E9, 0); }
		public TerminalNode U_00EA() { return getToken(ExpressionTemplateParser.U_00EA, 0); }
		public TerminalNode U_00EB() { return getToken(ExpressionTemplateParser.U_00EB, 0); }
		public TerminalNode U_00EC() { return getToken(ExpressionTemplateParser.U_00EC, 0); }
		public TerminalNode U_00ED() { return getToken(ExpressionTemplateParser.U_00ED, 0); }
		public TerminalNode U_0080() { return getToken(ExpressionTemplateParser.U_0080, 0); }
		public TerminalNode U_0081() { return getToken(ExpressionTemplateParser.U_0081, 0); }
		public TerminalNode U_0082() { return getToken(ExpressionTemplateParser.U_0082, 0); }
		public TerminalNode U_0083() { return getToken(ExpressionTemplateParser.U_0083, 0); }
		public TerminalNode U_0084() { return getToken(ExpressionTemplateParser.U_0084, 0); }
		public TerminalNode U_0085() { return getToken(ExpressionTemplateParser.U_0085, 0); }
		public TerminalNode U_0086() { return getToken(ExpressionTemplateParser.U_0086, 0); }
		public TerminalNode U_0087() { return getToken(ExpressionTemplateParser.U_0087, 0); }
		public TerminalNode U_0088() { return getToken(ExpressionTemplateParser.U_0088, 0); }
		public TerminalNode U_0089() { return getToken(ExpressionTemplateParser.U_0089, 0); }
		public TerminalNode U_008A() { return getToken(ExpressionTemplateParser.U_008A, 0); }
		public TerminalNode U_008B() { return getToken(ExpressionTemplateParser.U_008B, 0); }
		public TerminalNode U_008C() { return getToken(ExpressionTemplateParser.U_008C, 0); }
		public TerminalNode U_008D() { return getToken(ExpressionTemplateParser.U_008D, 0); }
		public TerminalNode U_008E() { return getToken(ExpressionTemplateParser.U_008E, 0); }
		public TerminalNode U_008F() { return getToken(ExpressionTemplateParser.U_008F, 0); }
		public TerminalNode U_0090() { return getToken(ExpressionTemplateParser.U_0090, 0); }
		public TerminalNode U_0091() { return getToken(ExpressionTemplateParser.U_0091, 0); }
		public TerminalNode U_0092() { return getToken(ExpressionTemplateParser.U_0092, 0); }
		public TerminalNode U_0093() { return getToken(ExpressionTemplateParser.U_0093, 0); }
		public TerminalNode U_0094() { return getToken(ExpressionTemplateParser.U_0094, 0); }
		public TerminalNode U_0095() { return getToken(ExpressionTemplateParser.U_0095, 0); }
		public TerminalNode U_0096() { return getToken(ExpressionTemplateParser.U_0096, 0); }
		public TerminalNode U_0097() { return getToken(ExpressionTemplateParser.U_0097, 0); }
		public TerminalNode U_0098() { return getToken(ExpressionTemplateParser.U_0098, 0); }
		public TerminalNode U_0099() { return getToken(ExpressionTemplateParser.U_0099, 0); }
		public TerminalNode U_009A() { return getToken(ExpressionTemplateParser.U_009A, 0); }
		public TerminalNode U_009B() { return getToken(ExpressionTemplateParser.U_009B, 0); }
		public TerminalNode U_009C() { return getToken(ExpressionTemplateParser.U_009C, 0); }
		public TerminalNode U_009D() { return getToken(ExpressionTemplateParser.U_009D, 0); }
		public TerminalNode U_009E() { return getToken(ExpressionTemplateParser.U_009E, 0); }
		public TerminalNode U_009F() { return getToken(ExpressionTemplateParser.U_009F, 0); }
		public TerminalNode U_00EE() { return getToken(ExpressionTemplateParser.U_00EE, 0); }
		public TerminalNode U_00EF() { return getToken(ExpressionTemplateParser.U_00EF, 0); }
		public Utf8_3Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_utf8_3; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterUtf8_3(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitUtf8_3(this);
		}
	}

	public final Utf8_3Context utf8_3() throws RecognitionException {
		Utf8_3Context _localctx = new Utf8_3Context(_ctx, getState());
		enterRule(_localctx, 70, RULE_utf8_3);
		int _la;
		try {
			setState(534);
			switch (_input.LA(1)) {
			case U_00E0:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(520);
				match(U_00E0);
				setState(521);
				_la = _input.LA(1);
				if ( !(((((_la - 131)) & ~0x3f) == 0 && ((1L << (_la - 131)) & ((1L << (U_00A0 - 131)) | (1L << (U_00A1 - 131)) | (1L << (U_00A2 - 131)) | (1L << (U_00A3 - 131)) | (1L << (U_00A4 - 131)) | (1L << (U_00A5 - 131)) | (1L << (U_00A6 - 131)) | (1L << (U_00A7 - 131)) | (1L << (U_00A8 - 131)) | (1L << (U_00A9 - 131)) | (1L << (U_00AA - 131)) | (1L << (U_00AB - 131)) | (1L << (U_00AC - 131)) | (1L << (U_00AD - 131)) | (1L << (U_00AE - 131)) | (1L << (U_00AF - 131)) | (1L << (U_00B0 - 131)) | (1L << (U_00B1 - 131)) | (1L << (U_00B2 - 131)) | (1L << (U_00B3 - 131)) | (1L << (U_00B4 - 131)) | (1L << (U_00B5 - 131)) | (1L << (U_00B6 - 131)) | (1L << (U_00B7 - 131)) | (1L << (U_00B8 - 131)) | (1L << (U_00B9 - 131)) | (1L << (U_00BA - 131)) | (1L << (U_00BB - 131)) | (1L << (U_00BC - 131)) | (1L << (U_00BD - 131)) | (1L << (U_00BE - 131)) | (1L << (U_00BF - 131)))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(522);
				utf8_tail();
				}
				}
				break;
			case U_00E1:
			case U_00E2:
			case U_00E3:
			case U_00E4:
			case U_00E5:
			case U_00E6:
			case U_00E7:
			case U_00E8:
			case U_00E9:
			case U_00EA:
			case U_00EB:
			case U_00EC:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(523);
				_la = _input.LA(1);
				if ( !(((((_la - 194)) & ~0x3f) == 0 && ((1L << (_la - 194)) & ((1L << (U_00E1 - 194)) | (1L << (U_00E2 - 194)) | (1L << (U_00E3 - 194)) | (1L << (U_00E4 - 194)) | (1L << (U_00E5 - 194)) | (1L << (U_00E6 - 194)) | (1L << (U_00E7 - 194)) | (1L << (U_00E8 - 194)) | (1L << (U_00E9 - 194)) | (1L << (U_00EA - 194)) | (1L << (U_00EB - 194)) | (1L << (U_00EC - 194)))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				{
				setState(524);
				utf8_tail();
				}
				{
				setState(525);
				utf8_tail();
				}
				}
				}
				break;
			case U_00ED:
				enterOuterAlt(_localctx, 3);
				{
				{
				setState(527);
				match(U_00ED);
				setState(528);
				_la = _input.LA(1);
				if ( !(((((_la - 99)) & ~0x3f) == 0 && ((1L << (_la - 99)) & ((1L << (U_0080 - 99)) | (1L << (U_0081 - 99)) | (1L << (U_0082 - 99)) | (1L << (U_0083 - 99)) | (1L << (U_0084 - 99)) | (1L << (U_0085 - 99)) | (1L << (U_0086 - 99)) | (1L << (U_0087 - 99)) | (1L << (U_0088 - 99)) | (1L << (U_0089 - 99)) | (1L << (U_008A - 99)) | (1L << (U_008B - 99)) | (1L << (U_008C - 99)) | (1L << (U_008D - 99)) | (1L << (U_008E - 99)) | (1L << (U_008F - 99)) | (1L << (U_0090 - 99)) | (1L << (U_0091 - 99)) | (1L << (U_0092 - 99)) | (1L << (U_0093 - 99)) | (1L << (U_0094 - 99)) | (1L << (U_0095 - 99)) | (1L << (U_0096 - 99)) | (1L << (U_0097 - 99)) | (1L << (U_0098 - 99)) | (1L << (U_0099 - 99)) | (1L << (U_009A - 99)) | (1L << (U_009B - 99)) | (1L << (U_009C - 99)) | (1L << (U_009D - 99)) | (1L << (U_009E - 99)) | (1L << (U_009F - 99)))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(529);
				utf8_tail();
				}
				}
				break;
			case U_00EE:
			case U_00EF:
				enterOuterAlt(_localctx, 4);
				{
				{
				setState(530);
				_la = _input.LA(1);
				if ( !(_la==U_00EE || _la==U_00EF) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				{
				setState(531);
				utf8_tail();
				}
				{
				setState(532);
				utf8_tail();
				}
				}
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

	public static class Utf8_4Context extends ParserRuleContext {
		public TerminalNode U_00F0() { return getToken(ExpressionTemplateParser.U_00F0, 0); }
		public TerminalNode U_0090() { return getToken(ExpressionTemplateParser.U_0090, 0); }
		public TerminalNode U_0091() { return getToken(ExpressionTemplateParser.U_0091, 0); }
		public TerminalNode U_0092() { return getToken(ExpressionTemplateParser.U_0092, 0); }
		public TerminalNode U_0093() { return getToken(ExpressionTemplateParser.U_0093, 0); }
		public TerminalNode U_0094() { return getToken(ExpressionTemplateParser.U_0094, 0); }
		public TerminalNode U_0095() { return getToken(ExpressionTemplateParser.U_0095, 0); }
		public TerminalNode U_0096() { return getToken(ExpressionTemplateParser.U_0096, 0); }
		public TerminalNode U_0097() { return getToken(ExpressionTemplateParser.U_0097, 0); }
		public TerminalNode U_0098() { return getToken(ExpressionTemplateParser.U_0098, 0); }
		public TerminalNode U_0099() { return getToken(ExpressionTemplateParser.U_0099, 0); }
		public TerminalNode U_009A() { return getToken(ExpressionTemplateParser.U_009A, 0); }
		public TerminalNode U_009B() { return getToken(ExpressionTemplateParser.U_009B, 0); }
		public TerminalNode U_009C() { return getToken(ExpressionTemplateParser.U_009C, 0); }
		public TerminalNode U_009D() { return getToken(ExpressionTemplateParser.U_009D, 0); }
		public TerminalNode U_009E() { return getToken(ExpressionTemplateParser.U_009E, 0); }
		public TerminalNode U_009F() { return getToken(ExpressionTemplateParser.U_009F, 0); }
		public TerminalNode U_00A0() { return getToken(ExpressionTemplateParser.U_00A0, 0); }
		public TerminalNode U_00A1() { return getToken(ExpressionTemplateParser.U_00A1, 0); }
		public TerminalNode U_00A2() { return getToken(ExpressionTemplateParser.U_00A2, 0); }
		public TerminalNode U_00A3() { return getToken(ExpressionTemplateParser.U_00A3, 0); }
		public TerminalNode U_00A4() { return getToken(ExpressionTemplateParser.U_00A4, 0); }
		public TerminalNode U_00A5() { return getToken(ExpressionTemplateParser.U_00A5, 0); }
		public TerminalNode U_00A6() { return getToken(ExpressionTemplateParser.U_00A6, 0); }
		public TerminalNode U_00A7() { return getToken(ExpressionTemplateParser.U_00A7, 0); }
		public TerminalNode U_00A8() { return getToken(ExpressionTemplateParser.U_00A8, 0); }
		public TerminalNode U_00A9() { return getToken(ExpressionTemplateParser.U_00A9, 0); }
		public TerminalNode U_00AA() { return getToken(ExpressionTemplateParser.U_00AA, 0); }
		public TerminalNode U_00AB() { return getToken(ExpressionTemplateParser.U_00AB, 0); }
		public TerminalNode U_00AC() { return getToken(ExpressionTemplateParser.U_00AC, 0); }
		public TerminalNode U_00AD() { return getToken(ExpressionTemplateParser.U_00AD, 0); }
		public TerminalNode U_00AE() { return getToken(ExpressionTemplateParser.U_00AE, 0); }
		public TerminalNode U_00AF() { return getToken(ExpressionTemplateParser.U_00AF, 0); }
		public TerminalNode U_00B0() { return getToken(ExpressionTemplateParser.U_00B0, 0); }
		public TerminalNode U_00B1() { return getToken(ExpressionTemplateParser.U_00B1, 0); }
		public TerminalNode U_00B2() { return getToken(ExpressionTemplateParser.U_00B2, 0); }
		public TerminalNode U_00B3() { return getToken(ExpressionTemplateParser.U_00B3, 0); }
		public TerminalNode U_00B4() { return getToken(ExpressionTemplateParser.U_00B4, 0); }
		public TerminalNode U_00B5() { return getToken(ExpressionTemplateParser.U_00B5, 0); }
		public TerminalNode U_00B6() { return getToken(ExpressionTemplateParser.U_00B6, 0); }
		public TerminalNode U_00B7() { return getToken(ExpressionTemplateParser.U_00B7, 0); }
		public TerminalNode U_00B8() { return getToken(ExpressionTemplateParser.U_00B8, 0); }
		public TerminalNode U_00B9() { return getToken(ExpressionTemplateParser.U_00B9, 0); }
		public TerminalNode U_00BA() { return getToken(ExpressionTemplateParser.U_00BA, 0); }
		public TerminalNode U_00BB() { return getToken(ExpressionTemplateParser.U_00BB, 0); }
		public TerminalNode U_00BC() { return getToken(ExpressionTemplateParser.U_00BC, 0); }
		public TerminalNode U_00BD() { return getToken(ExpressionTemplateParser.U_00BD, 0); }
		public TerminalNode U_00BE() { return getToken(ExpressionTemplateParser.U_00BE, 0); }
		public TerminalNode U_00BF() { return getToken(ExpressionTemplateParser.U_00BF, 0); }
		public List<Utf8_tailContext> utf8_tail() {
			return getRuleContexts(Utf8_tailContext.class);
		}
		public Utf8_tailContext utf8_tail(int i) {
			return getRuleContext(Utf8_tailContext.class,i);
		}
		public TerminalNode U_00F1() { return getToken(ExpressionTemplateParser.U_00F1, 0); }
		public TerminalNode U_00F2() { return getToken(ExpressionTemplateParser.U_00F2, 0); }
		public TerminalNode U_00F3() { return getToken(ExpressionTemplateParser.U_00F3, 0); }
		public TerminalNode U_00F4() { return getToken(ExpressionTemplateParser.U_00F4, 0); }
		public TerminalNode U_0080() { return getToken(ExpressionTemplateParser.U_0080, 0); }
		public TerminalNode U_0081() { return getToken(ExpressionTemplateParser.U_0081, 0); }
		public TerminalNode U_0082() { return getToken(ExpressionTemplateParser.U_0082, 0); }
		public TerminalNode U_0083() { return getToken(ExpressionTemplateParser.U_0083, 0); }
		public TerminalNode U_0084() { return getToken(ExpressionTemplateParser.U_0084, 0); }
		public TerminalNode U_0085() { return getToken(ExpressionTemplateParser.U_0085, 0); }
		public TerminalNode U_0086() { return getToken(ExpressionTemplateParser.U_0086, 0); }
		public TerminalNode U_0087() { return getToken(ExpressionTemplateParser.U_0087, 0); }
		public TerminalNode U_0088() { return getToken(ExpressionTemplateParser.U_0088, 0); }
		public TerminalNode U_0089() { return getToken(ExpressionTemplateParser.U_0089, 0); }
		public TerminalNode U_008A() { return getToken(ExpressionTemplateParser.U_008A, 0); }
		public TerminalNode U_008B() { return getToken(ExpressionTemplateParser.U_008B, 0); }
		public TerminalNode U_008C() { return getToken(ExpressionTemplateParser.U_008C, 0); }
		public TerminalNode U_008D() { return getToken(ExpressionTemplateParser.U_008D, 0); }
		public TerminalNode U_008E() { return getToken(ExpressionTemplateParser.U_008E, 0); }
		public TerminalNode U_008F() { return getToken(ExpressionTemplateParser.U_008F, 0); }
		public Utf8_4Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_utf8_4; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterUtf8_4(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitUtf8_4(this);
		}
	}

	public final Utf8_4Context utf8_4() throws RecognitionException {
		Utf8_4Context _localctx = new Utf8_4Context(_ctx, getState());
		enterRule(_localctx, 72, RULE_utf8_4);
		int _la;
		try {
			setState(551);
			switch (_input.LA(1)) {
			case U_00F0:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(536);
				match(U_00F0);
				setState(537);
				_la = _input.LA(1);
				if ( !(((((_la - 115)) & ~0x3f) == 0 && ((1L << (_la - 115)) & ((1L << (U_0090 - 115)) | (1L << (U_0091 - 115)) | (1L << (U_0092 - 115)) | (1L << (U_0093 - 115)) | (1L << (U_0094 - 115)) | (1L << (U_0095 - 115)) | (1L << (U_0096 - 115)) | (1L << (U_0097 - 115)) | (1L << (U_0098 - 115)) | (1L << (U_0099 - 115)) | (1L << (U_009A - 115)) | (1L << (U_009B - 115)) | (1L << (U_009C - 115)) | (1L << (U_009D - 115)) | (1L << (U_009E - 115)) | (1L << (U_009F - 115)) | (1L << (U_00A0 - 115)) | (1L << (U_00A1 - 115)) | (1L << (U_00A2 - 115)) | (1L << (U_00A3 - 115)) | (1L << (U_00A4 - 115)) | (1L << (U_00A5 - 115)) | (1L << (U_00A6 - 115)) | (1L << (U_00A7 - 115)) | (1L << (U_00A8 - 115)) | (1L << (U_00A9 - 115)) | (1L << (U_00AA - 115)) | (1L << (U_00AB - 115)) | (1L << (U_00AC - 115)) | (1L << (U_00AD - 115)) | (1L << (U_00AE - 115)) | (1L << (U_00AF - 115)) | (1L << (U_00B0 - 115)) | (1L << (U_00B1 - 115)) | (1L << (U_00B2 - 115)) | (1L << (U_00B3 - 115)) | (1L << (U_00B4 - 115)) | (1L << (U_00B5 - 115)) | (1L << (U_00B6 - 115)) | (1L << (U_00B7 - 115)) | (1L << (U_00B8 - 115)) | (1L << (U_00B9 - 115)) | (1L << (U_00BA - 115)) | (1L << (U_00BB - 115)) | (1L << (U_00BC - 115)) | (1L << (U_00BD - 115)) | (1L << (U_00BE - 115)) | (1L << (U_00BF - 115)))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				{
				setState(538);
				utf8_tail();
				}
				{
				setState(539);
				utf8_tail();
				}
				}
				}
				break;
			case U_00F1:
			case U_00F2:
			case U_00F3:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(541);
				_la = _input.LA(1);
				if ( !(((((_la - 210)) & ~0x3f) == 0 && ((1L << (_la - 210)) & ((1L << (U_00F1 - 210)) | (1L << (U_00F2 - 210)) | (1L << (U_00F3 - 210)))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				{
				setState(542);
				utf8_tail();
				}
				{
				setState(543);
				utf8_tail();
				}
				{
				setState(544);
				utf8_tail();
				}
				}
				}
				break;
			case U_00F4:
				enterOuterAlt(_localctx, 3);
				{
				{
				setState(546);
				match(U_00F4);
				setState(547);
				_la = _input.LA(1);
				if ( !(((((_la - 99)) & ~0x3f) == 0 && ((1L << (_la - 99)) & ((1L << (U_0080 - 99)) | (1L << (U_0081 - 99)) | (1L << (U_0082 - 99)) | (1L << (U_0083 - 99)) | (1L << (U_0084 - 99)) | (1L << (U_0085 - 99)) | (1L << (U_0086 - 99)) | (1L << (U_0087 - 99)) | (1L << (U_0088 - 99)) | (1L << (U_0089 - 99)) | (1L << (U_008A - 99)) | (1L << (U_008B - 99)) | (1L << (U_008C - 99)) | (1L << (U_008D - 99)) | (1L << (U_008E - 99)) | (1L << (U_008F - 99)))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				{
				setState(548);
				utf8_tail();
				}
				{
				setState(549);
				utf8_tail();
				}
				}
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

	public static class Utf8_tailContext extends ParserRuleContext {
		public TerminalNode U_0080() { return getToken(ExpressionTemplateParser.U_0080, 0); }
		public TerminalNode U_0081() { return getToken(ExpressionTemplateParser.U_0081, 0); }
		public TerminalNode U_0082() { return getToken(ExpressionTemplateParser.U_0082, 0); }
		public TerminalNode U_0083() { return getToken(ExpressionTemplateParser.U_0083, 0); }
		public TerminalNode U_0084() { return getToken(ExpressionTemplateParser.U_0084, 0); }
		public TerminalNode U_0085() { return getToken(ExpressionTemplateParser.U_0085, 0); }
		public TerminalNode U_0086() { return getToken(ExpressionTemplateParser.U_0086, 0); }
		public TerminalNode U_0087() { return getToken(ExpressionTemplateParser.U_0087, 0); }
		public TerminalNode U_0088() { return getToken(ExpressionTemplateParser.U_0088, 0); }
		public TerminalNode U_0089() { return getToken(ExpressionTemplateParser.U_0089, 0); }
		public TerminalNode U_008A() { return getToken(ExpressionTemplateParser.U_008A, 0); }
		public TerminalNode U_008B() { return getToken(ExpressionTemplateParser.U_008B, 0); }
		public TerminalNode U_008C() { return getToken(ExpressionTemplateParser.U_008C, 0); }
		public TerminalNode U_008D() { return getToken(ExpressionTemplateParser.U_008D, 0); }
		public TerminalNode U_008E() { return getToken(ExpressionTemplateParser.U_008E, 0); }
		public TerminalNode U_008F() { return getToken(ExpressionTemplateParser.U_008F, 0); }
		public TerminalNode U_0090() { return getToken(ExpressionTemplateParser.U_0090, 0); }
		public TerminalNode U_0091() { return getToken(ExpressionTemplateParser.U_0091, 0); }
		public TerminalNode U_0092() { return getToken(ExpressionTemplateParser.U_0092, 0); }
		public TerminalNode U_0093() { return getToken(ExpressionTemplateParser.U_0093, 0); }
		public TerminalNode U_0094() { return getToken(ExpressionTemplateParser.U_0094, 0); }
		public TerminalNode U_0095() { return getToken(ExpressionTemplateParser.U_0095, 0); }
		public TerminalNode U_0096() { return getToken(ExpressionTemplateParser.U_0096, 0); }
		public TerminalNode U_0097() { return getToken(ExpressionTemplateParser.U_0097, 0); }
		public TerminalNode U_0098() { return getToken(ExpressionTemplateParser.U_0098, 0); }
		public TerminalNode U_0099() { return getToken(ExpressionTemplateParser.U_0099, 0); }
		public TerminalNode U_009A() { return getToken(ExpressionTemplateParser.U_009A, 0); }
		public TerminalNode U_009B() { return getToken(ExpressionTemplateParser.U_009B, 0); }
		public TerminalNode U_009C() { return getToken(ExpressionTemplateParser.U_009C, 0); }
		public TerminalNode U_009D() { return getToken(ExpressionTemplateParser.U_009D, 0); }
		public TerminalNode U_009E() { return getToken(ExpressionTemplateParser.U_009E, 0); }
		public TerminalNode U_009F() { return getToken(ExpressionTemplateParser.U_009F, 0); }
		public TerminalNode U_00A0() { return getToken(ExpressionTemplateParser.U_00A0, 0); }
		public TerminalNode U_00A1() { return getToken(ExpressionTemplateParser.U_00A1, 0); }
		public TerminalNode U_00A2() { return getToken(ExpressionTemplateParser.U_00A2, 0); }
		public TerminalNode U_00A3() { return getToken(ExpressionTemplateParser.U_00A3, 0); }
		public TerminalNode U_00A4() { return getToken(ExpressionTemplateParser.U_00A4, 0); }
		public TerminalNode U_00A5() { return getToken(ExpressionTemplateParser.U_00A5, 0); }
		public TerminalNode U_00A6() { return getToken(ExpressionTemplateParser.U_00A6, 0); }
		public TerminalNode U_00A7() { return getToken(ExpressionTemplateParser.U_00A7, 0); }
		public TerminalNode U_00A8() { return getToken(ExpressionTemplateParser.U_00A8, 0); }
		public TerminalNode U_00A9() { return getToken(ExpressionTemplateParser.U_00A9, 0); }
		public TerminalNode U_00AA() { return getToken(ExpressionTemplateParser.U_00AA, 0); }
		public TerminalNode U_00AB() { return getToken(ExpressionTemplateParser.U_00AB, 0); }
		public TerminalNode U_00AC() { return getToken(ExpressionTemplateParser.U_00AC, 0); }
		public TerminalNode U_00AD() { return getToken(ExpressionTemplateParser.U_00AD, 0); }
		public TerminalNode U_00AE() { return getToken(ExpressionTemplateParser.U_00AE, 0); }
		public TerminalNode U_00AF() { return getToken(ExpressionTemplateParser.U_00AF, 0); }
		public TerminalNode U_00B0() { return getToken(ExpressionTemplateParser.U_00B0, 0); }
		public TerminalNode U_00B1() { return getToken(ExpressionTemplateParser.U_00B1, 0); }
		public TerminalNode U_00B2() { return getToken(ExpressionTemplateParser.U_00B2, 0); }
		public TerminalNode U_00B3() { return getToken(ExpressionTemplateParser.U_00B3, 0); }
		public TerminalNode U_00B4() { return getToken(ExpressionTemplateParser.U_00B4, 0); }
		public TerminalNode U_00B5() { return getToken(ExpressionTemplateParser.U_00B5, 0); }
		public TerminalNode U_00B6() { return getToken(ExpressionTemplateParser.U_00B6, 0); }
		public TerminalNode U_00B7() { return getToken(ExpressionTemplateParser.U_00B7, 0); }
		public TerminalNode U_00B8() { return getToken(ExpressionTemplateParser.U_00B8, 0); }
		public TerminalNode U_00B9() { return getToken(ExpressionTemplateParser.U_00B9, 0); }
		public TerminalNode U_00BA() { return getToken(ExpressionTemplateParser.U_00BA, 0); }
		public TerminalNode U_00BB() { return getToken(ExpressionTemplateParser.U_00BB, 0); }
		public TerminalNode U_00BC() { return getToken(ExpressionTemplateParser.U_00BC, 0); }
		public TerminalNode U_00BD() { return getToken(ExpressionTemplateParser.U_00BD, 0); }
		public TerminalNode U_00BE() { return getToken(ExpressionTemplateParser.U_00BE, 0); }
		public TerminalNode U_00BF() { return getToken(ExpressionTemplateParser.U_00BF, 0); }
		public Utf8_tailContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_utf8_tail; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterUtf8_tail(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitUtf8_tail(this);
		}
	}

	public final Utf8_tailContext utf8_tail() throws RecognitionException {
		Utf8_tailContext _localctx = new Utf8_tailContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_utf8_tail);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(553);
			_la = _input.LA(1);
			if ( !(((((_la - 99)) & ~0x3f) == 0 && ((1L << (_la - 99)) & ((1L << (U_0080 - 99)) | (1L << (U_0081 - 99)) | (1L << (U_0082 - 99)) | (1L << (U_0083 - 99)) | (1L << (U_0084 - 99)) | (1L << (U_0085 - 99)) | (1L << (U_0086 - 99)) | (1L << (U_0087 - 99)) | (1L << (U_0088 - 99)) | (1L << (U_0089 - 99)) | (1L << (U_008A - 99)) | (1L << (U_008B - 99)) | (1L << (U_008C - 99)) | (1L << (U_008D - 99)) | (1L << (U_008E - 99)) | (1L << (U_008F - 99)) | (1L << (U_0090 - 99)) | (1L << (U_0091 - 99)) | (1L << (U_0092 - 99)) | (1L << (U_0093 - 99)) | (1L << (U_0094 - 99)) | (1L << (U_0095 - 99)) | (1L << (U_0096 - 99)) | (1L << (U_0097 - 99)) | (1L << (U_0098 - 99)) | (1L << (U_0099 - 99)) | (1L << (U_009A - 99)) | (1L << (U_009B - 99)) | (1L << (U_009C - 99)) | (1L << (U_009D - 99)) | (1L << (U_009E - 99)) | (1L << (U_009F - 99)) | (1L << (U_00A0 - 99)) | (1L << (U_00A1 - 99)) | (1L << (U_00A2 - 99)) | (1L << (U_00A3 - 99)) | (1L << (U_00A4 - 99)) | (1L << (U_00A5 - 99)) | (1L << (U_00A6 - 99)) | (1L << (U_00A7 - 99)) | (1L << (U_00A8 - 99)) | (1L << (U_00A9 - 99)) | (1L << (U_00AA - 99)) | (1L << (U_00AB - 99)) | (1L << (U_00AC - 99)) | (1L << (U_00AD - 99)) | (1L << (U_00AE - 99)) | (1L << (U_00AF - 99)) | (1L << (U_00B0 - 99)) | (1L << (U_00B1 - 99)) | (1L << (U_00B2 - 99)) | (1L << (U_00B3 - 99)) | (1L << (U_00B4 - 99)) | (1L << (U_00B5 - 99)) | (1L << (U_00B6 - 99)) | (1L << (U_00B7 - 99)) | (1L << (U_00B8 - 99)) | (1L << (U_00B9 - 99)) | (1L << (U_00BA - 99)) | (1L << (U_00BB - 99)) | (1L << (U_00BC - 99)) | (1L << (U_00BD - 99)) | (1L << (U_00BE - 99)) | (1L << (U_00BF - 99)))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
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

	public static class TemplatereplaceslotContext extends ParserRuleContext {
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public TerminalNode PLUS() { return getToken(ExpressionTemplateParser.PLUS, 0); }
		public List<TerminalNode> LEFT_BRACE() { return getTokens(ExpressionTemplateParser.LEFT_BRACE); }
		public TerminalNode LEFT_BRACE(int i) {
			return getToken(ExpressionTemplateParser.LEFT_BRACE, i);
		}
		public List<TerminalNode> RIGHT_BRACE() { return getTokens(ExpressionTemplateParser.RIGHT_BRACE); }
		public TerminalNode RIGHT_BRACE(int i) {
			return getToken(ExpressionTemplateParser.RIGHT_BRACE, i);
		}
		public ReplaceinfoContext replaceinfo() {
			return getRuleContext(ReplaceinfoContext.class,0);
		}
		public TemplateslotinfoContext templateslotinfo() {
			return getRuleContext(TemplateslotinfoContext.class,0);
		}
		public TemplatereplaceslotContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_templatereplaceslot; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterTemplatereplaceslot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitTemplatereplaceslot(this);
		}
	}

	public final TemplatereplaceslotContext templatereplaceslot() throws RecognitionException {
		TemplatereplaceslotContext _localctx = new TemplatereplaceslotContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_templatereplaceslot);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(555);
			match(LEFT_BRACE);
			setState(556);
			match(LEFT_BRACE);
			}
			setState(558);
			ws();
			setState(559);
			match(PLUS);
			setState(560);
			ws();
			setState(564);
			_la = _input.LA(1);
			if (((((_la - 73)) & ~0x3f) == 0 && ((1L << (_la - 73)) & ((1L << (E - 73)) | (1L << (I - 73)) | (1L << (S - 73)))) != 0)) {
				{
				setState(561);
				replaceinfo();
				setState(562);
				ws();
				}
			}

			setState(567);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				{
				setState(566);
				templateslotinfo();
				}
				break;
			}
			{
			setState(569);
			match(RIGHT_BRACE);
			setState(570);
			match(RIGHT_BRACE);
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

	public static class TemplateremoveslotContext extends ParserRuleContext {
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public TerminalNode TILDE() { return getToken(ExpressionTemplateParser.TILDE, 0); }
		public List<TerminalNode> LEFT_BRACE() { return getTokens(ExpressionTemplateParser.LEFT_BRACE); }
		public TerminalNode LEFT_BRACE(int i) {
			return getToken(ExpressionTemplateParser.LEFT_BRACE, i);
		}
		public List<TerminalNode> RIGHT_BRACE() { return getTokens(ExpressionTemplateParser.RIGHT_BRACE); }
		public TerminalNode RIGHT_BRACE(int i) {
			return getToken(ExpressionTemplateParser.RIGHT_BRACE, i);
		}
		public TemplateslotinfoContext templateslotinfo() {
			return getRuleContext(TemplateslotinfoContext.class,0);
		}
		public TemplateremoveslotContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_templateremoveslot; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterTemplateremoveslot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitTemplateremoveslot(this);
		}
	}

	public final TemplateremoveslotContext templateremoveslot() throws RecognitionException {
		TemplateremoveslotContext _localctx = new TemplateremoveslotContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_templateremoveslot);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(572);
			match(LEFT_BRACE);
			setState(573);
			match(LEFT_BRACE);
			}
			setState(575);
			ws();
			setState(576);
			match(TILDE);
			setState(577);
			ws();
			setState(579);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				{
				setState(578);
				templateslotinfo();
				}
				break;
			}
			{
			setState(581);
			match(RIGHT_BRACE);
			setState(582);
			match(RIGHT_BRACE);
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

	public static class ReplaceinfoContext extends ParserRuleContext {
		public ReplaceflagContext replaceflag() {
			return getRuleContext(ReplaceflagContext.class,0);
		}
		public WsContext ws() {
			return getRuleContext(WsContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(ExpressionTemplateParser.LEFT_PAREN, 0); }
		public ExpressionconstrainttemplateContext expressionconstrainttemplate() {
			return getRuleContext(ExpressionconstrainttemplateContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(ExpressionTemplateParser.RIGHT_PAREN, 0); }
		public ReplaceinfoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_replaceinfo; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterReplaceinfo(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitReplaceinfo(this);
		}
	}

	public final ReplaceinfoContext replaceinfo() throws RecognitionException {
		ReplaceinfoContext _localctx = new ReplaceinfoContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_replaceinfo);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(584);
			replaceflag();
			setState(590);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,35,_ctx) ) {
			case 1:
				{
				setState(585);
				ws();
				setState(586);
				match(LEFT_PAREN);
				setState(587);
				expressionconstrainttemplate();
				setState(588);
				match(RIGHT_PAREN);
				}
				break;
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

	public static class ReplaceflagContext extends ParserRuleContext {
		public TerminalNode I() { return getToken(ExpressionTemplateParser.I, 0); }
		public TerminalNode D() { return getToken(ExpressionTemplateParser.D, 0); }
		public TerminalNode S() { return getToken(ExpressionTemplateParser.S, 0); }
		public TerminalNode C() { return getToken(ExpressionTemplateParser.C, 0); }
		public TerminalNode G() { return getToken(ExpressionTemplateParser.G, 0); }
		public TerminalNode E() { return getToken(ExpressionTemplateParser.E, 0); }
		public TerminalNode L() { return getToken(ExpressionTemplateParser.L, 0); }
		public ReplaceflagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_replaceflag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterReplaceflag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitReplaceflag(this);
		}
	}

	public final ReplaceflagContext replaceflag() throws RecognitionException {
		ReplaceflagContext _localctx = new ReplaceflagContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_replaceflag);
		try {
			setState(600);
			switch (_input.LA(1)) {
			case I:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(592);
				match(I);
				setState(593);
				match(D);
				}
				}
				break;
			case S:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(594);
				match(S);
				setState(595);
				match(C);
				setState(596);
				match(G);
				}
				}
				break;
			case E:
				enterOuterAlt(_localctx, 3);
				{
				{
				setState(597);
				match(E);
				setState(598);
				match(C);
				setState(599);
				match(L);
				}
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

	public static class TemplateslotinfoContext extends ParserRuleContext {
		public CardinalityContext cardinality() {
			return getRuleContext(CardinalityContext.class,0);
		}
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public TemplateslotnameContext templateslotname() {
			return getRuleContext(TemplateslotnameContext.class,0);
		}
		public TemplateslotreferenceContext templateslotreference() {
			return getRuleContext(TemplateslotreferenceContext.class,0);
		}
		public TemplateslotinfoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_templateslotinfo; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterTemplateslotinfo(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitTemplateslotinfo(this);
		}
	}

	public final TemplateslotinfoContext templateslotinfo() throws RecognitionException {
		TemplateslotinfoContext _localctx = new TemplateslotinfoContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_templateslotinfo);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(605);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ZERO) | (1L << ONE) | (1L << TWO) | (1L << THREE) | (1L << FOUR) | (1L << FIVE) | (1L << SIX) | (1L << SEVEN) | (1L << EIGHT) | (1L << NINE))) != 0)) {
				{
				setState(602);
				cardinality();
				setState(603);
				ws();
				}
			}

			setState(610);
			_la = _input.LA(1);
			if (_la==AT) {
				{
				setState(607);
				templateslotname();
				setState(608);
				ws();
				}
			}

			setState(615);
			_la = _input.LA(1);
			if (_la==DOLLAR) {
				{
				setState(612);
				templateslotreference();
				setState(613);
				ws();
				}
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

	public static class TemplateslotnameContext extends ParserRuleContext {
		public TerminalNode AT() { return getToken(ExpressionTemplateParser.AT, 0); }
		public TemplatestringContext templatestring() {
			return getRuleContext(TemplatestringContext.class,0);
		}
		public TemplateslotnameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_templateslotname; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterTemplateslotname(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitTemplateslotname(this);
		}
	}

	public final TemplateslotnameContext templateslotname() throws RecognitionException {
		TemplateslotnameContext _localctx = new TemplateslotnameContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_templateslotname);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(617);
			match(AT);
			setState(618);
			templatestring();
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

	public static class TemplateslotreferenceContext extends ParserRuleContext {
		public TerminalNode DOLLAR() { return getToken(ExpressionTemplateParser.DOLLAR, 0); }
		public TemplatestringContext templatestring() {
			return getRuleContext(TemplatestringContext.class,0);
		}
		public TemplateslotreferenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_templateslotreference; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterTemplateslotreference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitTemplateslotreference(this);
		}
	}

	public final TemplateslotreferenceContext templateslotreference() throws RecognitionException {
		TemplateslotreferenceContext _localctx = new TemplateslotreferenceContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_templateslotreference);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(620);
			match(DOLLAR);
			setState(621);
			templatestring();
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

	public static class TemplatestringContext extends ParserRuleContext {
		public List<NonquotestringContext> nonquotestring() {
			return getRuleContexts(NonquotestringContext.class);
		}
		public NonquotestringContext nonquotestring(int i) {
			return getRuleContext(NonquotestringContext.class,i);
		}
		public List<QmContext> qm() {
			return getRuleContexts(QmContext.class);
		}
		public QmContext qm(int i) {
			return getRuleContext(QmContext.class,i);
		}
		public List<NondoublequotestringContext> nondoublequotestring() {
			return getRuleContexts(NondoublequotestringContext.class);
		}
		public NondoublequotestringContext nondoublequotestring(int i) {
			return getRuleContext(NondoublequotestringContext.class,i);
		}
		public List<SqmContext> sqm() {
			return getRuleContexts(SqmContext.class);
		}
		public SqmContext sqm(int i) {
			return getRuleContext(SqmContext.class,i);
		}
		public List<NonsinglequotestringContext> nonsinglequotestring() {
			return getRuleContexts(NonsinglequotestringContext.class);
		}
		public NonsinglequotestringContext nonsinglequotestring(int i) {
			return getRuleContext(NonsinglequotestringContext.class,i);
		}
		public TemplatestringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_templatestring; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterTemplatestring(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitTemplatestring(this);
		}
	}

	public final TemplatestringContext templatestring() throws RecognitionException {
		TemplatestringContext _localctx = new TemplatestringContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_templatestring);
		int _la;
		try {
			setState(647);
			switch (_input.LA(1)) {
			case TAB:
			case LF:
			case CR:
			case SPACE:
			case EXCLAMATION:
			case POUND:
			case DOLLAR:
			case PERCENT:
			case AMPERSAND:
			case LEFT_PAREN:
			case RIGHT_PAREN:
			case ASTERISK:
			case PLUS:
			case COMMA:
			case DASH:
			case PERIOD:
			case SLASH:
			case ZERO:
			case ONE:
			case TWO:
			case THREE:
			case FOUR:
			case FIVE:
			case SIX:
			case SEVEN:
			case EIGHT:
			case NINE:
			case COLON:
			case SEMICOLON:
			case LESS_THAN:
			case EQUALS:
			case GREATER_THAN:
			case QUESTION:
			case CAP_A:
			case CAP_B:
			case CAP_C:
			case CAP_D:
			case CAP_E:
			case CAP_F:
			case CAP_G:
			case CAP_H:
			case CAP_I:
			case CAP_J:
			case CAP_K:
			case CAP_L:
			case CAP_M:
			case CAP_N:
			case CAP_O:
			case CAP_P:
			case CAP_Q:
			case CAP_R:
			case CAP_S:
			case CAP_T:
			case CAP_U:
			case CAP_V:
			case CAP_W:
			case CAP_X:
			case CAP_Y:
			case CAP_Z:
			case BACKSLASH:
			case RIGHT_BRACE:
			case CARAT:
			case UNDERSCORE:
			case ACCENT:
			case A:
			case B:
			case C:
			case D:
			case E:
			case F:
			case G:
			case H:
			case I:
			case J:
			case K:
			case L:
			case M:
			case N:
			case O:
			case P:
			case Q:
			case R:
			case S:
			case T:
			case U:
			case V:
			case W:
			case X:
			case Y:
			case Z:
			case LEFT_CURLY_BRACE:
			case PIPE:
			case RIGHT_CURLY_BRACE:
			case TILDE:
				enterOuterAlt(_localctx, 1);
				{
				setState(626);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EXCLAMATION) | (1L << POUND) | (1L << PERCENT) | (1L << AMPERSAND) | (1L << LEFT_PAREN) | (1L << RIGHT_PAREN) | (1L << ASTERISK) | (1L << PLUS) | (1L << COMMA) | (1L << DASH) | (1L << PERIOD) | (1L << SLASH) | (1L << ZERO) | (1L << ONE) | (1L << TWO) | (1L << THREE) | (1L << FOUR) | (1L << FIVE) | (1L << SIX) | (1L << SEVEN) | (1L << EIGHT) | (1L << NINE) | (1L << COLON) | (1L << SEMICOLON) | (1L << LESS_THAN) | (1L << EQUALS) | (1L << GREATER_THAN) | (1L << QUESTION) | (1L << CAP_A) | (1L << CAP_B) | (1L << CAP_C) | (1L << CAP_D) | (1L << CAP_E) | (1L << CAP_F) | (1L << CAP_G) | (1L << CAP_H) | (1L << CAP_I) | (1L << CAP_J) | (1L << CAP_K) | (1L << CAP_L) | (1L << CAP_M) | (1L << CAP_N) | (1L << CAP_O) | (1L << CAP_P) | (1L << CAP_Q) | (1L << CAP_R) | (1L << CAP_S) | (1L << CAP_T) | (1L << CAP_U) | (1L << CAP_V) | (1L << CAP_W) | (1L << CAP_X) | (1L << CAP_Y) | (1L << CAP_Z))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (BACKSLASH - 64)) | (1L << (CARAT - 64)) | (1L << (UNDERSCORE - 64)) | (1L << (ACCENT - 64)) | (1L << (A - 64)) | (1L << (B - 64)) | (1L << (C - 64)) | (1L << (D - 64)) | (1L << (E - 64)) | (1L << (F - 64)) | (1L << (G - 64)) | (1L << (H - 64)) | (1L << (I - 64)) | (1L << (J - 64)) | (1L << (K - 64)) | (1L << (L - 64)) | (1L << (M - 64)) | (1L << (N - 64)) | (1L << (O - 64)) | (1L << (P - 64)) | (1L << (Q - 64)) | (1L << (R - 64)) | (1L << (S - 64)) | (1L << (T - 64)) | (1L << (U - 64)) | (1L << (V - 64)) | (1L << (W - 64)) | (1L << (X - 64)) | (1L << (Y - 64)) | (1L << (Z - 64)) | (1L << (LEFT_CURLY_BRACE - 64)) | (1L << (PIPE - 64)) | (1L << (RIGHT_CURLY_BRACE - 64)) | (1L << (TILDE - 64)))) != 0)) {
					{
					{
					setState(623);
					nonquotestring();
					}
					}
					setState(628);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case QUOTE:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(629);
				qm();
				setState(633);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EXCLAMATION) | (1L << POUND) | (1L << PERCENT) | (1L << AMPERSAND) | (1L << APOSTROPHE) | (1L << LEFT_PAREN) | (1L << RIGHT_PAREN) | (1L << ASTERISK) | (1L << PLUS) | (1L << COMMA) | (1L << DASH) | (1L << PERIOD) | (1L << SLASH) | (1L << ZERO) | (1L << ONE) | (1L << TWO) | (1L << THREE) | (1L << FOUR) | (1L << FIVE) | (1L << SIX) | (1L << SEVEN) | (1L << EIGHT) | (1L << NINE) | (1L << COLON) | (1L << SEMICOLON) | (1L << LESS_THAN) | (1L << EQUALS) | (1L << GREATER_THAN) | (1L << QUESTION) | (1L << CAP_A) | (1L << CAP_B) | (1L << CAP_C) | (1L << CAP_D) | (1L << CAP_E) | (1L << CAP_F) | (1L << CAP_G) | (1L << CAP_H) | (1L << CAP_I) | (1L << CAP_J) | (1L << CAP_K) | (1L << CAP_L) | (1L << CAP_M) | (1L << CAP_N) | (1L << CAP_O) | (1L << CAP_P) | (1L << CAP_Q) | (1L << CAP_R) | (1L << CAP_S) | (1L << CAP_T) | (1L << CAP_U) | (1L << CAP_V) | (1L << CAP_W) | (1L << CAP_X) | (1L << CAP_Y) | (1L << CAP_Z) | (1L << LEFT_BRACE))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (BACKSLASH - 64)) | (1L << (RIGHT_BRACE - 64)) | (1L << (CARAT - 64)) | (1L << (UNDERSCORE - 64)) | (1L << (ACCENT - 64)) | (1L << (A - 64)) | (1L << (B - 64)) | (1L << (C - 64)) | (1L << (D - 64)) | (1L << (E - 64)) | (1L << (F - 64)) | (1L << (G - 64)) | (1L << (H - 64)) | (1L << (I - 64)) | (1L << (J - 64)) | (1L << (K - 64)) | (1L << (L - 64)) | (1L << (M - 64)) | (1L << (N - 64)) | (1L << (O - 64)) | (1L << (P - 64)) | (1L << (Q - 64)) | (1L << (R - 64)) | (1L << (S - 64)) | (1L << (T - 64)) | (1L << (U - 64)) | (1L << (V - 64)) | (1L << (W - 64)) | (1L << (X - 64)) | (1L << (Y - 64)) | (1L << (Z - 64)) | (1L << (LEFT_CURLY_BRACE - 64)) | (1L << (PIPE - 64)) | (1L << (RIGHT_CURLY_BRACE - 64)) | (1L << (TILDE - 64)))) != 0)) {
					{
					{
					setState(630);
					nondoublequotestring();
					}
					}
					setState(635);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(636);
				qm();
				}
				}
				break;
			case APOSTROPHE:
				enterOuterAlt(_localctx, 3);
				{
				{
				setState(638);
				sqm();
				setState(642);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EXCLAMATION) | (1L << QUOTE) | (1L << POUND) | (1L << PERCENT) | (1L << AMPERSAND) | (1L << LEFT_PAREN) | (1L << RIGHT_PAREN) | (1L << ASTERISK) | (1L << PLUS) | (1L << COMMA) | (1L << DASH) | (1L << PERIOD) | (1L << SLASH) | (1L << ZERO) | (1L << ONE) | (1L << TWO) | (1L << THREE) | (1L << FOUR) | (1L << FIVE) | (1L << SIX) | (1L << SEVEN) | (1L << EIGHT) | (1L << NINE) | (1L << COLON) | (1L << SEMICOLON) | (1L << LESS_THAN) | (1L << EQUALS) | (1L << GREATER_THAN) | (1L << QUESTION) | (1L << CAP_A) | (1L << CAP_B) | (1L << CAP_C) | (1L << CAP_D) | (1L << CAP_E) | (1L << CAP_F) | (1L << CAP_G) | (1L << CAP_H) | (1L << CAP_I) | (1L << CAP_J) | (1L << CAP_K) | (1L << CAP_L) | (1L << CAP_M) | (1L << CAP_N) | (1L << CAP_O) | (1L << CAP_P) | (1L << CAP_Q) | (1L << CAP_R) | (1L << CAP_S) | (1L << CAP_T) | (1L << CAP_U) | (1L << CAP_V) | (1L << CAP_W) | (1L << CAP_X) | (1L << CAP_Y) | (1L << CAP_Z) | (1L << LEFT_BRACE))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (BACKSLASH - 64)) | (1L << (RIGHT_BRACE - 64)) | (1L << (CARAT - 64)) | (1L << (UNDERSCORE - 64)) | (1L << (ACCENT - 64)) | (1L << (A - 64)) | (1L << (B - 64)) | (1L << (C - 64)) | (1L << (D - 64)) | (1L << (E - 64)) | (1L << (F - 64)) | (1L << (G - 64)) | (1L << (H - 64)) | (1L << (I - 64)) | (1L << (J - 64)) | (1L << (K - 64)) | (1L << (L - 64)) | (1L << (M - 64)) | (1L << (N - 64)) | (1L << (O - 64)) | (1L << (P - 64)) | (1L << (Q - 64)) | (1L << (R - 64)) | (1L << (S - 64)) | (1L << (T - 64)) | (1L << (U - 64)) | (1L << (V - 64)) | (1L << (W - 64)) | (1L << (X - 64)) | (1L << (Y - 64)) | (1L << (Z - 64)) | (1L << (LEFT_CURLY_BRACE - 64)) | (1L << (PIPE - 64)) | (1L << (RIGHT_CURLY_BRACE - 64)) | (1L << (TILDE - 64)))) != 0)) {
					{
					{
					setState(639);
					nonsinglequotestring();
					}
					}
					setState(644);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(645);
				sqm();
				}
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

	public static class NonquotestringContext extends ParserRuleContext {
		public TerminalNode EXCLAMATION() { return getToken(ExpressionTemplateParser.EXCLAMATION, 0); }
		public TerminalNode POUND() { return getToken(ExpressionTemplateParser.POUND, 0); }
		public TerminalNode PERCENT() { return getToken(ExpressionTemplateParser.PERCENT, 0); }
		public TerminalNode AMPERSAND() { return getToken(ExpressionTemplateParser.AMPERSAND, 0); }
		public TerminalNode LEFT_PAREN() { return getToken(ExpressionTemplateParser.LEFT_PAREN, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(ExpressionTemplateParser.RIGHT_PAREN, 0); }
		public TerminalNode ASTERISK() { return getToken(ExpressionTemplateParser.ASTERISK, 0); }
		public TerminalNode PLUS() { return getToken(ExpressionTemplateParser.PLUS, 0); }
		public TerminalNode COMMA() { return getToken(ExpressionTemplateParser.COMMA, 0); }
		public TerminalNode DASH() { return getToken(ExpressionTemplateParser.DASH, 0); }
		public TerminalNode PERIOD() { return getToken(ExpressionTemplateParser.PERIOD, 0); }
		public TerminalNode SLASH() { return getToken(ExpressionTemplateParser.SLASH, 0); }
		public TerminalNode ZERO() { return getToken(ExpressionTemplateParser.ZERO, 0); }
		public TerminalNode ONE() { return getToken(ExpressionTemplateParser.ONE, 0); }
		public TerminalNode TWO() { return getToken(ExpressionTemplateParser.TWO, 0); }
		public TerminalNode THREE() { return getToken(ExpressionTemplateParser.THREE, 0); }
		public TerminalNode FOUR() { return getToken(ExpressionTemplateParser.FOUR, 0); }
		public TerminalNode FIVE() { return getToken(ExpressionTemplateParser.FIVE, 0); }
		public TerminalNode SIX() { return getToken(ExpressionTemplateParser.SIX, 0); }
		public TerminalNode SEVEN() { return getToken(ExpressionTemplateParser.SEVEN, 0); }
		public TerminalNode EIGHT() { return getToken(ExpressionTemplateParser.EIGHT, 0); }
		public TerminalNode NINE() { return getToken(ExpressionTemplateParser.NINE, 0); }
		public TerminalNode COLON() { return getToken(ExpressionTemplateParser.COLON, 0); }
		public TerminalNode SEMICOLON() { return getToken(ExpressionTemplateParser.SEMICOLON, 0); }
		public TerminalNode LESS_THAN() { return getToken(ExpressionTemplateParser.LESS_THAN, 0); }
		public TerminalNode EQUALS() { return getToken(ExpressionTemplateParser.EQUALS, 0); }
		public TerminalNode GREATER_THAN() { return getToken(ExpressionTemplateParser.GREATER_THAN, 0); }
		public TerminalNode QUESTION() { return getToken(ExpressionTemplateParser.QUESTION, 0); }
		public TerminalNode CAP_A() { return getToken(ExpressionTemplateParser.CAP_A, 0); }
		public TerminalNode CAP_B() { return getToken(ExpressionTemplateParser.CAP_B, 0); }
		public TerminalNode CAP_C() { return getToken(ExpressionTemplateParser.CAP_C, 0); }
		public TerminalNode CAP_D() { return getToken(ExpressionTemplateParser.CAP_D, 0); }
		public TerminalNode CAP_E() { return getToken(ExpressionTemplateParser.CAP_E, 0); }
		public TerminalNode CAP_F() { return getToken(ExpressionTemplateParser.CAP_F, 0); }
		public TerminalNode CAP_G() { return getToken(ExpressionTemplateParser.CAP_G, 0); }
		public TerminalNode CAP_H() { return getToken(ExpressionTemplateParser.CAP_H, 0); }
		public TerminalNode CAP_I() { return getToken(ExpressionTemplateParser.CAP_I, 0); }
		public TerminalNode CAP_J() { return getToken(ExpressionTemplateParser.CAP_J, 0); }
		public TerminalNode CAP_K() { return getToken(ExpressionTemplateParser.CAP_K, 0); }
		public TerminalNode CAP_L() { return getToken(ExpressionTemplateParser.CAP_L, 0); }
		public TerminalNode CAP_M() { return getToken(ExpressionTemplateParser.CAP_M, 0); }
		public TerminalNode CAP_N() { return getToken(ExpressionTemplateParser.CAP_N, 0); }
		public TerminalNode CAP_O() { return getToken(ExpressionTemplateParser.CAP_O, 0); }
		public TerminalNode CAP_P() { return getToken(ExpressionTemplateParser.CAP_P, 0); }
		public TerminalNode CAP_Q() { return getToken(ExpressionTemplateParser.CAP_Q, 0); }
		public TerminalNode CAP_R() { return getToken(ExpressionTemplateParser.CAP_R, 0); }
		public TerminalNode CAP_S() { return getToken(ExpressionTemplateParser.CAP_S, 0); }
		public TerminalNode CAP_T() { return getToken(ExpressionTemplateParser.CAP_T, 0); }
		public TerminalNode CAP_U() { return getToken(ExpressionTemplateParser.CAP_U, 0); }
		public TerminalNode CAP_V() { return getToken(ExpressionTemplateParser.CAP_V, 0); }
		public TerminalNode CAP_W() { return getToken(ExpressionTemplateParser.CAP_W, 0); }
		public TerminalNode CAP_X() { return getToken(ExpressionTemplateParser.CAP_X, 0); }
		public TerminalNode CAP_Y() { return getToken(ExpressionTemplateParser.CAP_Y, 0); }
		public TerminalNode CAP_Z() { return getToken(ExpressionTemplateParser.CAP_Z, 0); }
		public TerminalNode BACKSLASH() { return getToken(ExpressionTemplateParser.BACKSLASH, 0); }
		public TerminalNode CARAT() { return getToken(ExpressionTemplateParser.CARAT, 0); }
		public TerminalNode UNDERSCORE() { return getToken(ExpressionTemplateParser.UNDERSCORE, 0); }
		public TerminalNode ACCENT() { return getToken(ExpressionTemplateParser.ACCENT, 0); }
		public TerminalNode A() { return getToken(ExpressionTemplateParser.A, 0); }
		public TerminalNode B() { return getToken(ExpressionTemplateParser.B, 0); }
		public TerminalNode C() { return getToken(ExpressionTemplateParser.C, 0); }
		public TerminalNode D() { return getToken(ExpressionTemplateParser.D, 0); }
		public TerminalNode E() { return getToken(ExpressionTemplateParser.E, 0); }
		public TerminalNode F() { return getToken(ExpressionTemplateParser.F, 0); }
		public TerminalNode G() { return getToken(ExpressionTemplateParser.G, 0); }
		public TerminalNode H() { return getToken(ExpressionTemplateParser.H, 0); }
		public TerminalNode I() { return getToken(ExpressionTemplateParser.I, 0); }
		public TerminalNode J() { return getToken(ExpressionTemplateParser.J, 0); }
		public TerminalNode K() { return getToken(ExpressionTemplateParser.K, 0); }
		public TerminalNode L() { return getToken(ExpressionTemplateParser.L, 0); }
		public TerminalNode M() { return getToken(ExpressionTemplateParser.M, 0); }
		public TerminalNode N() { return getToken(ExpressionTemplateParser.N, 0); }
		public TerminalNode O() { return getToken(ExpressionTemplateParser.O, 0); }
		public TerminalNode P() { return getToken(ExpressionTemplateParser.P, 0); }
		public TerminalNode Q() { return getToken(ExpressionTemplateParser.Q, 0); }
		public TerminalNode R() { return getToken(ExpressionTemplateParser.R, 0); }
		public TerminalNode S() { return getToken(ExpressionTemplateParser.S, 0); }
		public TerminalNode T() { return getToken(ExpressionTemplateParser.T, 0); }
		public TerminalNode U() { return getToken(ExpressionTemplateParser.U, 0); }
		public TerminalNode V() { return getToken(ExpressionTemplateParser.V, 0); }
		public TerminalNode W() { return getToken(ExpressionTemplateParser.W, 0); }
		public TerminalNode X() { return getToken(ExpressionTemplateParser.X, 0); }
		public TerminalNode Y() { return getToken(ExpressionTemplateParser.Y, 0); }
		public TerminalNode Z() { return getToken(ExpressionTemplateParser.Z, 0); }
		public TerminalNode LEFT_CURLY_BRACE() { return getToken(ExpressionTemplateParser.LEFT_CURLY_BRACE, 0); }
		public TerminalNode PIPE() { return getToken(ExpressionTemplateParser.PIPE, 0); }
		public TerminalNode RIGHT_CURLY_BRACE() { return getToken(ExpressionTemplateParser.RIGHT_CURLY_BRACE, 0); }
		public TerminalNode TILDE() { return getToken(ExpressionTemplateParser.TILDE, 0); }
		public NonquotestringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nonquotestring; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterNonquotestring(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitNonquotestring(this);
		}
	}

	public final NonquotestringContext nonquotestring() throws RecognitionException {
		NonquotestringContext _localctx = new NonquotestringContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_nonquotestring);
		int _la;
		try {
			setState(656);
			switch (_input.LA(1)) {
			case EXCLAMATION:
				enterOuterAlt(_localctx, 1);
				{
				setState(649);
				match(EXCLAMATION);
				}
				break;
			case POUND:
				enterOuterAlt(_localctx, 2);
				{
				setState(650);
				match(POUND);
				}
				break;
			case PERCENT:
			case AMPERSAND:
				enterOuterAlt(_localctx, 3);
				{
				setState(651);
				_la = _input.LA(1);
				if ( !(_la==PERCENT || _la==AMPERSAND) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			case LEFT_PAREN:
			case RIGHT_PAREN:
			case ASTERISK:
			case PLUS:
			case COMMA:
			case DASH:
			case PERIOD:
			case SLASH:
			case ZERO:
			case ONE:
			case TWO:
			case THREE:
			case FOUR:
			case FIVE:
			case SIX:
			case SEVEN:
			case EIGHT:
			case NINE:
			case COLON:
			case SEMICOLON:
			case LESS_THAN:
			case EQUALS:
			case GREATER_THAN:
			case QUESTION:
				enterOuterAlt(_localctx, 4);
				{
				setState(652);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LEFT_PAREN) | (1L << RIGHT_PAREN) | (1L << ASTERISK) | (1L << PLUS) | (1L << COMMA) | (1L << DASH) | (1L << PERIOD) | (1L << SLASH) | (1L << ZERO) | (1L << ONE) | (1L << TWO) | (1L << THREE) | (1L << FOUR) | (1L << FIVE) | (1L << SIX) | (1L << SEVEN) | (1L << EIGHT) | (1L << NINE) | (1L << COLON) | (1L << SEMICOLON) | (1L << LESS_THAN) | (1L << EQUALS) | (1L << GREATER_THAN) | (1L << QUESTION))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			case CAP_A:
			case CAP_B:
			case CAP_C:
			case CAP_D:
			case CAP_E:
			case CAP_F:
			case CAP_G:
			case CAP_H:
			case CAP_I:
			case CAP_J:
			case CAP_K:
			case CAP_L:
			case CAP_M:
			case CAP_N:
			case CAP_O:
			case CAP_P:
			case CAP_Q:
			case CAP_R:
			case CAP_S:
			case CAP_T:
			case CAP_U:
			case CAP_V:
			case CAP_W:
			case CAP_X:
			case CAP_Y:
			case CAP_Z:
				enterOuterAlt(_localctx, 5);
				{
				setState(653);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CAP_A) | (1L << CAP_B) | (1L << CAP_C) | (1L << CAP_D) | (1L << CAP_E) | (1L << CAP_F) | (1L << CAP_G) | (1L << CAP_H) | (1L << CAP_I) | (1L << CAP_J) | (1L << CAP_K) | (1L << CAP_L) | (1L << CAP_M) | (1L << CAP_N) | (1L << CAP_O) | (1L << CAP_P) | (1L << CAP_Q) | (1L << CAP_R) | (1L << CAP_S) | (1L << CAP_T) | (1L << CAP_U) | (1L << CAP_V) | (1L << CAP_W) | (1L << CAP_X) | (1L << CAP_Y) | (1L << CAP_Z))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			case BACKSLASH:
				enterOuterAlt(_localctx, 6);
				{
				setState(654);
				match(BACKSLASH);
				}
				break;
			case CARAT:
			case UNDERSCORE:
			case ACCENT:
			case A:
			case B:
			case C:
			case D:
			case E:
			case F:
			case G:
			case H:
			case I:
			case J:
			case K:
			case L:
			case M:
			case N:
			case O:
			case P:
			case Q:
			case R:
			case S:
			case T:
			case U:
			case V:
			case W:
			case X:
			case Y:
			case Z:
			case LEFT_CURLY_BRACE:
			case PIPE:
			case RIGHT_CURLY_BRACE:
			case TILDE:
				enterOuterAlt(_localctx, 7);
				{
				setState(655);
				_la = _input.LA(1);
				if ( !(((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & ((1L << (CARAT - 66)) | (1L << (UNDERSCORE - 66)) | (1L << (ACCENT - 66)) | (1L << (A - 66)) | (1L << (B - 66)) | (1L << (C - 66)) | (1L << (D - 66)) | (1L << (E - 66)) | (1L << (F - 66)) | (1L << (G - 66)) | (1L << (H - 66)) | (1L << (I - 66)) | (1L << (J - 66)) | (1L << (K - 66)) | (1L << (L - 66)) | (1L << (M - 66)) | (1L << (N - 66)) | (1L << (O - 66)) | (1L << (P - 66)) | (1L << (Q - 66)) | (1L << (R - 66)) | (1L << (S - 66)) | (1L << (T - 66)) | (1L << (U - 66)) | (1L << (V - 66)) | (1L << (W - 66)) | (1L << (X - 66)) | (1L << (Y - 66)) | (1L << (Z - 66)) | (1L << (LEFT_CURLY_BRACE - 66)) | (1L << (PIPE - 66)) | (1L << (RIGHT_CURLY_BRACE - 66)) | (1L << (TILDE - 66)))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
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

	public static class NondoublequotestringContext extends ParserRuleContext {
		public TerminalNode EXCLAMATION() { return getToken(ExpressionTemplateParser.EXCLAMATION, 0); }
		public TerminalNode POUND() { return getToken(ExpressionTemplateParser.POUND, 0); }
		public TerminalNode PERCENT() { return getToken(ExpressionTemplateParser.PERCENT, 0); }
		public TerminalNode AMPERSAND() { return getToken(ExpressionTemplateParser.AMPERSAND, 0); }
		public TerminalNode APOSTROPHE() { return getToken(ExpressionTemplateParser.APOSTROPHE, 0); }
		public TerminalNode LEFT_PAREN() { return getToken(ExpressionTemplateParser.LEFT_PAREN, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(ExpressionTemplateParser.RIGHT_PAREN, 0); }
		public TerminalNode ASTERISK() { return getToken(ExpressionTemplateParser.ASTERISK, 0); }
		public TerminalNode PLUS() { return getToken(ExpressionTemplateParser.PLUS, 0); }
		public TerminalNode COMMA() { return getToken(ExpressionTemplateParser.COMMA, 0); }
		public TerminalNode DASH() { return getToken(ExpressionTemplateParser.DASH, 0); }
		public TerminalNode PERIOD() { return getToken(ExpressionTemplateParser.PERIOD, 0); }
		public TerminalNode SLASH() { return getToken(ExpressionTemplateParser.SLASH, 0); }
		public TerminalNode ZERO() { return getToken(ExpressionTemplateParser.ZERO, 0); }
		public TerminalNode ONE() { return getToken(ExpressionTemplateParser.ONE, 0); }
		public TerminalNode TWO() { return getToken(ExpressionTemplateParser.TWO, 0); }
		public TerminalNode THREE() { return getToken(ExpressionTemplateParser.THREE, 0); }
		public TerminalNode FOUR() { return getToken(ExpressionTemplateParser.FOUR, 0); }
		public TerminalNode FIVE() { return getToken(ExpressionTemplateParser.FIVE, 0); }
		public TerminalNode SIX() { return getToken(ExpressionTemplateParser.SIX, 0); }
		public TerminalNode SEVEN() { return getToken(ExpressionTemplateParser.SEVEN, 0); }
		public TerminalNode EIGHT() { return getToken(ExpressionTemplateParser.EIGHT, 0); }
		public TerminalNode NINE() { return getToken(ExpressionTemplateParser.NINE, 0); }
		public TerminalNode COLON() { return getToken(ExpressionTemplateParser.COLON, 0); }
		public TerminalNode SEMICOLON() { return getToken(ExpressionTemplateParser.SEMICOLON, 0); }
		public TerminalNode LESS_THAN() { return getToken(ExpressionTemplateParser.LESS_THAN, 0); }
		public TerminalNode EQUALS() { return getToken(ExpressionTemplateParser.EQUALS, 0); }
		public TerminalNode GREATER_THAN() { return getToken(ExpressionTemplateParser.GREATER_THAN, 0); }
		public TerminalNode QUESTION() { return getToken(ExpressionTemplateParser.QUESTION, 0); }
		public TerminalNode CAP_A() { return getToken(ExpressionTemplateParser.CAP_A, 0); }
		public TerminalNode CAP_B() { return getToken(ExpressionTemplateParser.CAP_B, 0); }
		public TerminalNode CAP_C() { return getToken(ExpressionTemplateParser.CAP_C, 0); }
		public TerminalNode CAP_D() { return getToken(ExpressionTemplateParser.CAP_D, 0); }
		public TerminalNode CAP_E() { return getToken(ExpressionTemplateParser.CAP_E, 0); }
		public TerminalNode CAP_F() { return getToken(ExpressionTemplateParser.CAP_F, 0); }
		public TerminalNode CAP_G() { return getToken(ExpressionTemplateParser.CAP_G, 0); }
		public TerminalNode CAP_H() { return getToken(ExpressionTemplateParser.CAP_H, 0); }
		public TerminalNode CAP_I() { return getToken(ExpressionTemplateParser.CAP_I, 0); }
		public TerminalNode CAP_J() { return getToken(ExpressionTemplateParser.CAP_J, 0); }
		public TerminalNode CAP_K() { return getToken(ExpressionTemplateParser.CAP_K, 0); }
		public TerminalNode CAP_L() { return getToken(ExpressionTemplateParser.CAP_L, 0); }
		public TerminalNode CAP_M() { return getToken(ExpressionTemplateParser.CAP_M, 0); }
		public TerminalNode CAP_N() { return getToken(ExpressionTemplateParser.CAP_N, 0); }
		public TerminalNode CAP_O() { return getToken(ExpressionTemplateParser.CAP_O, 0); }
		public TerminalNode CAP_P() { return getToken(ExpressionTemplateParser.CAP_P, 0); }
		public TerminalNode CAP_Q() { return getToken(ExpressionTemplateParser.CAP_Q, 0); }
		public TerminalNode CAP_R() { return getToken(ExpressionTemplateParser.CAP_R, 0); }
		public TerminalNode CAP_S() { return getToken(ExpressionTemplateParser.CAP_S, 0); }
		public TerminalNode CAP_T() { return getToken(ExpressionTemplateParser.CAP_T, 0); }
		public TerminalNode CAP_U() { return getToken(ExpressionTemplateParser.CAP_U, 0); }
		public TerminalNode CAP_V() { return getToken(ExpressionTemplateParser.CAP_V, 0); }
		public TerminalNode CAP_W() { return getToken(ExpressionTemplateParser.CAP_W, 0); }
		public TerminalNode CAP_X() { return getToken(ExpressionTemplateParser.CAP_X, 0); }
		public TerminalNode CAP_Y() { return getToken(ExpressionTemplateParser.CAP_Y, 0); }
		public TerminalNode CAP_Z() { return getToken(ExpressionTemplateParser.CAP_Z, 0); }
		public TerminalNode LEFT_BRACE() { return getToken(ExpressionTemplateParser.LEFT_BRACE, 0); }
		public TerminalNode BACKSLASH() { return getToken(ExpressionTemplateParser.BACKSLASH, 0); }
		public TerminalNode RIGHT_BRACE() { return getToken(ExpressionTemplateParser.RIGHT_BRACE, 0); }
		public TerminalNode CARAT() { return getToken(ExpressionTemplateParser.CARAT, 0); }
		public TerminalNode UNDERSCORE() { return getToken(ExpressionTemplateParser.UNDERSCORE, 0); }
		public TerminalNode ACCENT() { return getToken(ExpressionTemplateParser.ACCENT, 0); }
		public TerminalNode A() { return getToken(ExpressionTemplateParser.A, 0); }
		public TerminalNode B() { return getToken(ExpressionTemplateParser.B, 0); }
		public TerminalNode C() { return getToken(ExpressionTemplateParser.C, 0); }
		public TerminalNode D() { return getToken(ExpressionTemplateParser.D, 0); }
		public TerminalNode E() { return getToken(ExpressionTemplateParser.E, 0); }
		public TerminalNode F() { return getToken(ExpressionTemplateParser.F, 0); }
		public TerminalNode G() { return getToken(ExpressionTemplateParser.G, 0); }
		public TerminalNode H() { return getToken(ExpressionTemplateParser.H, 0); }
		public TerminalNode I() { return getToken(ExpressionTemplateParser.I, 0); }
		public TerminalNode J() { return getToken(ExpressionTemplateParser.J, 0); }
		public TerminalNode K() { return getToken(ExpressionTemplateParser.K, 0); }
		public TerminalNode L() { return getToken(ExpressionTemplateParser.L, 0); }
		public TerminalNode M() { return getToken(ExpressionTemplateParser.M, 0); }
		public TerminalNode N() { return getToken(ExpressionTemplateParser.N, 0); }
		public TerminalNode O() { return getToken(ExpressionTemplateParser.O, 0); }
		public TerminalNode P() { return getToken(ExpressionTemplateParser.P, 0); }
		public TerminalNode Q() { return getToken(ExpressionTemplateParser.Q, 0); }
		public TerminalNode R() { return getToken(ExpressionTemplateParser.R, 0); }
		public TerminalNode S() { return getToken(ExpressionTemplateParser.S, 0); }
		public TerminalNode T() { return getToken(ExpressionTemplateParser.T, 0); }
		public TerminalNode U() { return getToken(ExpressionTemplateParser.U, 0); }
		public TerminalNode V() { return getToken(ExpressionTemplateParser.V, 0); }
		public TerminalNode W() { return getToken(ExpressionTemplateParser.W, 0); }
		public TerminalNode X() { return getToken(ExpressionTemplateParser.X, 0); }
		public TerminalNode Y() { return getToken(ExpressionTemplateParser.Y, 0); }
		public TerminalNode Z() { return getToken(ExpressionTemplateParser.Z, 0); }
		public TerminalNode LEFT_CURLY_BRACE() { return getToken(ExpressionTemplateParser.LEFT_CURLY_BRACE, 0); }
		public TerminalNode PIPE() { return getToken(ExpressionTemplateParser.PIPE, 0); }
		public TerminalNode RIGHT_CURLY_BRACE() { return getToken(ExpressionTemplateParser.RIGHT_CURLY_BRACE, 0); }
		public TerminalNode TILDE() { return getToken(ExpressionTemplateParser.TILDE, 0); }
		public NondoublequotestringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nondoublequotestring; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterNondoublequotestring(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitNondoublequotestring(this);
		}
	}

	public final NondoublequotestringContext nondoublequotestring() throws RecognitionException {
		NondoublequotestringContext _localctx = new NondoublequotestringContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_nondoublequotestring);
		int _la;
		try {
			setState(662);
			switch (_input.LA(1)) {
			case EXCLAMATION:
				enterOuterAlt(_localctx, 1);
				{
				setState(658);
				match(EXCLAMATION);
				}
				break;
			case POUND:
				enterOuterAlt(_localctx, 2);
				{
				setState(659);
				match(POUND);
				}
				break;
			case PERCENT:
			case AMPERSAND:
			case APOSTROPHE:
			case LEFT_PAREN:
			case RIGHT_PAREN:
			case ASTERISK:
			case PLUS:
			case COMMA:
			case DASH:
			case PERIOD:
			case SLASH:
			case ZERO:
			case ONE:
			case TWO:
			case THREE:
			case FOUR:
			case FIVE:
			case SIX:
			case SEVEN:
			case EIGHT:
			case NINE:
			case COLON:
			case SEMICOLON:
			case LESS_THAN:
			case EQUALS:
			case GREATER_THAN:
			case QUESTION:
				enterOuterAlt(_localctx, 3);
				{
				setState(660);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PERCENT) | (1L << AMPERSAND) | (1L << APOSTROPHE) | (1L << LEFT_PAREN) | (1L << RIGHT_PAREN) | (1L << ASTERISK) | (1L << PLUS) | (1L << COMMA) | (1L << DASH) | (1L << PERIOD) | (1L << SLASH) | (1L << ZERO) | (1L << ONE) | (1L << TWO) | (1L << THREE) | (1L << FOUR) | (1L << FIVE) | (1L << SIX) | (1L << SEVEN) | (1L << EIGHT) | (1L << NINE) | (1L << COLON) | (1L << SEMICOLON) | (1L << LESS_THAN) | (1L << EQUALS) | (1L << GREATER_THAN) | (1L << QUESTION))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			case CAP_A:
			case CAP_B:
			case CAP_C:
			case CAP_D:
			case CAP_E:
			case CAP_F:
			case CAP_G:
			case CAP_H:
			case CAP_I:
			case CAP_J:
			case CAP_K:
			case CAP_L:
			case CAP_M:
			case CAP_N:
			case CAP_O:
			case CAP_P:
			case CAP_Q:
			case CAP_R:
			case CAP_S:
			case CAP_T:
			case CAP_U:
			case CAP_V:
			case CAP_W:
			case CAP_X:
			case CAP_Y:
			case CAP_Z:
			case LEFT_BRACE:
			case BACKSLASH:
			case RIGHT_BRACE:
			case CARAT:
			case UNDERSCORE:
			case ACCENT:
			case A:
			case B:
			case C:
			case D:
			case E:
			case F:
			case G:
			case H:
			case I:
			case J:
			case K:
			case L:
			case M:
			case N:
			case O:
			case P:
			case Q:
			case R:
			case S:
			case T:
			case U:
			case V:
			case W:
			case X:
			case Y:
			case Z:
			case LEFT_CURLY_BRACE:
			case PIPE:
			case RIGHT_CURLY_BRACE:
			case TILDE:
				enterOuterAlt(_localctx, 4);
				{
				setState(661);
				_la = _input.LA(1);
				if ( !(((((_la - 37)) & ~0x3f) == 0 && ((1L << (_la - 37)) & ((1L << (CAP_A - 37)) | (1L << (CAP_B - 37)) | (1L << (CAP_C - 37)) | (1L << (CAP_D - 37)) | (1L << (CAP_E - 37)) | (1L << (CAP_F - 37)) | (1L << (CAP_G - 37)) | (1L << (CAP_H - 37)) | (1L << (CAP_I - 37)) | (1L << (CAP_J - 37)) | (1L << (CAP_K - 37)) | (1L << (CAP_L - 37)) | (1L << (CAP_M - 37)) | (1L << (CAP_N - 37)) | (1L << (CAP_O - 37)) | (1L << (CAP_P - 37)) | (1L << (CAP_Q - 37)) | (1L << (CAP_R - 37)) | (1L << (CAP_S - 37)) | (1L << (CAP_T - 37)) | (1L << (CAP_U - 37)) | (1L << (CAP_V - 37)) | (1L << (CAP_W - 37)) | (1L << (CAP_X - 37)) | (1L << (CAP_Y - 37)) | (1L << (CAP_Z - 37)) | (1L << (LEFT_BRACE - 37)) | (1L << (BACKSLASH - 37)) | (1L << (RIGHT_BRACE - 37)) | (1L << (CARAT - 37)) | (1L << (UNDERSCORE - 37)) | (1L << (ACCENT - 37)) | (1L << (A - 37)) | (1L << (B - 37)) | (1L << (C - 37)) | (1L << (D - 37)) | (1L << (E - 37)) | (1L << (F - 37)) | (1L << (G - 37)) | (1L << (H - 37)) | (1L << (I - 37)) | (1L << (J - 37)) | (1L << (K - 37)) | (1L << (L - 37)) | (1L << (M - 37)) | (1L << (N - 37)) | (1L << (O - 37)) | (1L << (P - 37)) | (1L << (Q - 37)) | (1L << (R - 37)) | (1L << (S - 37)) | (1L << (T - 37)) | (1L << (U - 37)) | (1L << (V - 37)) | (1L << (W - 37)) | (1L << (X - 37)) | (1L << (Y - 37)) | (1L << (Z - 37)) | (1L << (LEFT_CURLY_BRACE - 37)) | (1L << (PIPE - 37)) | (1L << (RIGHT_CURLY_BRACE - 37)) | (1L << (TILDE - 37)))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
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

	public static class NonsinglequotestringContext extends ParserRuleContext {
		public TerminalNode EXCLAMATION() { return getToken(ExpressionTemplateParser.EXCLAMATION, 0); }
		public TerminalNode QUOTE() { return getToken(ExpressionTemplateParser.QUOTE, 0); }
		public TerminalNode POUND() { return getToken(ExpressionTemplateParser.POUND, 0); }
		public TerminalNode PERCENT() { return getToken(ExpressionTemplateParser.PERCENT, 0); }
		public TerminalNode AMPERSAND() { return getToken(ExpressionTemplateParser.AMPERSAND, 0); }
		public TerminalNode LEFT_PAREN() { return getToken(ExpressionTemplateParser.LEFT_PAREN, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(ExpressionTemplateParser.RIGHT_PAREN, 0); }
		public TerminalNode ASTERISK() { return getToken(ExpressionTemplateParser.ASTERISK, 0); }
		public TerminalNode PLUS() { return getToken(ExpressionTemplateParser.PLUS, 0); }
		public TerminalNode COMMA() { return getToken(ExpressionTemplateParser.COMMA, 0); }
		public TerminalNode DASH() { return getToken(ExpressionTemplateParser.DASH, 0); }
		public TerminalNode PERIOD() { return getToken(ExpressionTemplateParser.PERIOD, 0); }
		public TerminalNode SLASH() { return getToken(ExpressionTemplateParser.SLASH, 0); }
		public TerminalNode ZERO() { return getToken(ExpressionTemplateParser.ZERO, 0); }
		public TerminalNode ONE() { return getToken(ExpressionTemplateParser.ONE, 0); }
		public TerminalNode TWO() { return getToken(ExpressionTemplateParser.TWO, 0); }
		public TerminalNode THREE() { return getToken(ExpressionTemplateParser.THREE, 0); }
		public TerminalNode FOUR() { return getToken(ExpressionTemplateParser.FOUR, 0); }
		public TerminalNode FIVE() { return getToken(ExpressionTemplateParser.FIVE, 0); }
		public TerminalNode SIX() { return getToken(ExpressionTemplateParser.SIX, 0); }
		public TerminalNode SEVEN() { return getToken(ExpressionTemplateParser.SEVEN, 0); }
		public TerminalNode EIGHT() { return getToken(ExpressionTemplateParser.EIGHT, 0); }
		public TerminalNode NINE() { return getToken(ExpressionTemplateParser.NINE, 0); }
		public TerminalNode COLON() { return getToken(ExpressionTemplateParser.COLON, 0); }
		public TerminalNode SEMICOLON() { return getToken(ExpressionTemplateParser.SEMICOLON, 0); }
		public TerminalNode LESS_THAN() { return getToken(ExpressionTemplateParser.LESS_THAN, 0); }
		public TerminalNode EQUALS() { return getToken(ExpressionTemplateParser.EQUALS, 0); }
		public TerminalNode GREATER_THAN() { return getToken(ExpressionTemplateParser.GREATER_THAN, 0); }
		public TerminalNode QUESTION() { return getToken(ExpressionTemplateParser.QUESTION, 0); }
		public TerminalNode CAP_A() { return getToken(ExpressionTemplateParser.CAP_A, 0); }
		public TerminalNode CAP_B() { return getToken(ExpressionTemplateParser.CAP_B, 0); }
		public TerminalNode CAP_C() { return getToken(ExpressionTemplateParser.CAP_C, 0); }
		public TerminalNode CAP_D() { return getToken(ExpressionTemplateParser.CAP_D, 0); }
		public TerminalNode CAP_E() { return getToken(ExpressionTemplateParser.CAP_E, 0); }
		public TerminalNode CAP_F() { return getToken(ExpressionTemplateParser.CAP_F, 0); }
		public TerminalNode CAP_G() { return getToken(ExpressionTemplateParser.CAP_G, 0); }
		public TerminalNode CAP_H() { return getToken(ExpressionTemplateParser.CAP_H, 0); }
		public TerminalNode CAP_I() { return getToken(ExpressionTemplateParser.CAP_I, 0); }
		public TerminalNode CAP_J() { return getToken(ExpressionTemplateParser.CAP_J, 0); }
		public TerminalNode CAP_K() { return getToken(ExpressionTemplateParser.CAP_K, 0); }
		public TerminalNode CAP_L() { return getToken(ExpressionTemplateParser.CAP_L, 0); }
		public TerminalNode CAP_M() { return getToken(ExpressionTemplateParser.CAP_M, 0); }
		public TerminalNode CAP_N() { return getToken(ExpressionTemplateParser.CAP_N, 0); }
		public TerminalNode CAP_O() { return getToken(ExpressionTemplateParser.CAP_O, 0); }
		public TerminalNode CAP_P() { return getToken(ExpressionTemplateParser.CAP_P, 0); }
		public TerminalNode CAP_Q() { return getToken(ExpressionTemplateParser.CAP_Q, 0); }
		public TerminalNode CAP_R() { return getToken(ExpressionTemplateParser.CAP_R, 0); }
		public TerminalNode CAP_S() { return getToken(ExpressionTemplateParser.CAP_S, 0); }
		public TerminalNode CAP_T() { return getToken(ExpressionTemplateParser.CAP_T, 0); }
		public TerminalNode CAP_U() { return getToken(ExpressionTemplateParser.CAP_U, 0); }
		public TerminalNode CAP_V() { return getToken(ExpressionTemplateParser.CAP_V, 0); }
		public TerminalNode CAP_W() { return getToken(ExpressionTemplateParser.CAP_W, 0); }
		public TerminalNode CAP_X() { return getToken(ExpressionTemplateParser.CAP_X, 0); }
		public TerminalNode CAP_Y() { return getToken(ExpressionTemplateParser.CAP_Y, 0); }
		public TerminalNode CAP_Z() { return getToken(ExpressionTemplateParser.CAP_Z, 0); }
		public TerminalNode LEFT_BRACE() { return getToken(ExpressionTemplateParser.LEFT_BRACE, 0); }
		public TerminalNode BACKSLASH() { return getToken(ExpressionTemplateParser.BACKSLASH, 0); }
		public TerminalNode RIGHT_BRACE() { return getToken(ExpressionTemplateParser.RIGHT_BRACE, 0); }
		public TerminalNode CARAT() { return getToken(ExpressionTemplateParser.CARAT, 0); }
		public TerminalNode UNDERSCORE() { return getToken(ExpressionTemplateParser.UNDERSCORE, 0); }
		public TerminalNode ACCENT() { return getToken(ExpressionTemplateParser.ACCENT, 0); }
		public TerminalNode A() { return getToken(ExpressionTemplateParser.A, 0); }
		public TerminalNode B() { return getToken(ExpressionTemplateParser.B, 0); }
		public TerminalNode C() { return getToken(ExpressionTemplateParser.C, 0); }
		public TerminalNode D() { return getToken(ExpressionTemplateParser.D, 0); }
		public TerminalNode E() { return getToken(ExpressionTemplateParser.E, 0); }
		public TerminalNode F() { return getToken(ExpressionTemplateParser.F, 0); }
		public TerminalNode G() { return getToken(ExpressionTemplateParser.G, 0); }
		public TerminalNode H() { return getToken(ExpressionTemplateParser.H, 0); }
		public TerminalNode I() { return getToken(ExpressionTemplateParser.I, 0); }
		public TerminalNode J() { return getToken(ExpressionTemplateParser.J, 0); }
		public TerminalNode K() { return getToken(ExpressionTemplateParser.K, 0); }
		public TerminalNode L() { return getToken(ExpressionTemplateParser.L, 0); }
		public TerminalNode M() { return getToken(ExpressionTemplateParser.M, 0); }
		public TerminalNode N() { return getToken(ExpressionTemplateParser.N, 0); }
		public TerminalNode O() { return getToken(ExpressionTemplateParser.O, 0); }
		public TerminalNode P() { return getToken(ExpressionTemplateParser.P, 0); }
		public TerminalNode Q() { return getToken(ExpressionTemplateParser.Q, 0); }
		public TerminalNode R() { return getToken(ExpressionTemplateParser.R, 0); }
		public TerminalNode S() { return getToken(ExpressionTemplateParser.S, 0); }
		public TerminalNode T() { return getToken(ExpressionTemplateParser.T, 0); }
		public TerminalNode U() { return getToken(ExpressionTemplateParser.U, 0); }
		public TerminalNode V() { return getToken(ExpressionTemplateParser.V, 0); }
		public TerminalNode W() { return getToken(ExpressionTemplateParser.W, 0); }
		public TerminalNode X() { return getToken(ExpressionTemplateParser.X, 0); }
		public TerminalNode Y() { return getToken(ExpressionTemplateParser.Y, 0); }
		public TerminalNode Z() { return getToken(ExpressionTemplateParser.Z, 0); }
		public TerminalNode LEFT_CURLY_BRACE() { return getToken(ExpressionTemplateParser.LEFT_CURLY_BRACE, 0); }
		public TerminalNode PIPE() { return getToken(ExpressionTemplateParser.PIPE, 0); }
		public TerminalNode RIGHT_CURLY_BRACE() { return getToken(ExpressionTemplateParser.RIGHT_CURLY_BRACE, 0); }
		public TerminalNode TILDE() { return getToken(ExpressionTemplateParser.TILDE, 0); }
		public NonsinglequotestringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nonsinglequotestring; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterNonsinglequotestring(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitNonsinglequotestring(this);
		}
	}

	public final NonsinglequotestringContext nonsinglequotestring() throws RecognitionException {
		NonsinglequotestringContext _localctx = new NonsinglequotestringContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_nonsinglequotestring);
		int _la;
		try {
			setState(668);
			switch (_input.LA(1)) {
			case EXCLAMATION:
			case QUOTE:
			case POUND:
				enterOuterAlt(_localctx, 1);
				{
				setState(664);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EXCLAMATION) | (1L << QUOTE) | (1L << POUND))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			case PERCENT:
			case AMPERSAND:
				enterOuterAlt(_localctx, 2);
				{
				setState(665);
				_la = _input.LA(1);
				if ( !(_la==PERCENT || _la==AMPERSAND) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			case LEFT_PAREN:
			case RIGHT_PAREN:
			case ASTERISK:
			case PLUS:
			case COMMA:
			case DASH:
			case PERIOD:
			case SLASH:
			case ZERO:
			case ONE:
			case TWO:
			case THREE:
			case FOUR:
			case FIVE:
			case SIX:
			case SEVEN:
			case EIGHT:
			case NINE:
			case COLON:
			case SEMICOLON:
			case LESS_THAN:
			case EQUALS:
			case GREATER_THAN:
			case QUESTION:
				enterOuterAlt(_localctx, 3);
				{
				setState(666);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LEFT_PAREN) | (1L << RIGHT_PAREN) | (1L << ASTERISK) | (1L << PLUS) | (1L << COMMA) | (1L << DASH) | (1L << PERIOD) | (1L << SLASH) | (1L << ZERO) | (1L << ONE) | (1L << TWO) | (1L << THREE) | (1L << FOUR) | (1L << FIVE) | (1L << SIX) | (1L << SEVEN) | (1L << EIGHT) | (1L << NINE) | (1L << COLON) | (1L << SEMICOLON) | (1L << LESS_THAN) | (1L << EQUALS) | (1L << GREATER_THAN) | (1L << QUESTION))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			case CAP_A:
			case CAP_B:
			case CAP_C:
			case CAP_D:
			case CAP_E:
			case CAP_F:
			case CAP_G:
			case CAP_H:
			case CAP_I:
			case CAP_J:
			case CAP_K:
			case CAP_L:
			case CAP_M:
			case CAP_N:
			case CAP_O:
			case CAP_P:
			case CAP_Q:
			case CAP_R:
			case CAP_S:
			case CAP_T:
			case CAP_U:
			case CAP_V:
			case CAP_W:
			case CAP_X:
			case CAP_Y:
			case CAP_Z:
			case LEFT_BRACE:
			case BACKSLASH:
			case RIGHT_BRACE:
			case CARAT:
			case UNDERSCORE:
			case ACCENT:
			case A:
			case B:
			case C:
			case D:
			case E:
			case F:
			case G:
			case H:
			case I:
			case J:
			case K:
			case L:
			case M:
			case N:
			case O:
			case P:
			case Q:
			case R:
			case S:
			case T:
			case U:
			case V:
			case W:
			case X:
			case Y:
			case Z:
			case LEFT_CURLY_BRACE:
			case PIPE:
			case RIGHT_CURLY_BRACE:
			case TILDE:
				enterOuterAlt(_localctx, 4);
				{
				setState(667);
				_la = _input.LA(1);
				if ( !(((((_la - 37)) & ~0x3f) == 0 && ((1L << (_la - 37)) & ((1L << (CAP_A - 37)) | (1L << (CAP_B - 37)) | (1L << (CAP_C - 37)) | (1L << (CAP_D - 37)) | (1L << (CAP_E - 37)) | (1L << (CAP_F - 37)) | (1L << (CAP_G - 37)) | (1L << (CAP_H - 37)) | (1L << (CAP_I - 37)) | (1L << (CAP_J - 37)) | (1L << (CAP_K - 37)) | (1L << (CAP_L - 37)) | (1L << (CAP_M - 37)) | (1L << (CAP_N - 37)) | (1L << (CAP_O - 37)) | (1L << (CAP_P - 37)) | (1L << (CAP_Q - 37)) | (1L << (CAP_R - 37)) | (1L << (CAP_S - 37)) | (1L << (CAP_T - 37)) | (1L << (CAP_U - 37)) | (1L << (CAP_V - 37)) | (1L << (CAP_W - 37)) | (1L << (CAP_X - 37)) | (1L << (CAP_Y - 37)) | (1L << (CAP_Z - 37)) | (1L << (LEFT_BRACE - 37)) | (1L << (BACKSLASH - 37)) | (1L << (RIGHT_BRACE - 37)) | (1L << (CARAT - 37)) | (1L << (UNDERSCORE - 37)) | (1L << (ACCENT - 37)) | (1L << (A - 37)) | (1L << (B - 37)) | (1L << (C - 37)) | (1L << (D - 37)) | (1L << (E - 37)) | (1L << (F - 37)) | (1L << (G - 37)) | (1L << (H - 37)) | (1L << (I - 37)) | (1L << (J - 37)) | (1L << (K - 37)) | (1L << (L - 37)) | (1L << (M - 37)) | (1L << (N - 37)) | (1L << (O - 37)) | (1L << (P - 37)) | (1L << (Q - 37)) | (1L << (R - 37)) | (1L << (S - 37)) | (1L << (T - 37)) | (1L << (U - 37)) | (1L << (V - 37)) | (1L << (W - 37)) | (1L << (X - 37)) | (1L << (Y - 37)) | (1L << (Z - 37)) | (1L << (LEFT_CURLY_BRACE - 37)) | (1L << (PIPE - 37)) | (1L << (RIGHT_CURLY_BRACE - 37)) | (1L << (TILDE - 37)))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
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

	public static class SqmContext extends ParserRuleContext {
		public TerminalNode APOSTROPHE() { return getToken(ExpressionTemplateParser.APOSTROPHE, 0); }
		public SqmContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sqm; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterSqm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitSqm(this);
		}
	}

	public final SqmContext sqm() throws RecognitionException {
		SqmContext _localctx = new SqmContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_sqm);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(670);
			match(APOSTROPHE);
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

	public static class ExpressionconstrainttemplateContext extends ParserRuleContext {
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public RefinedexpressionconstraintContext refinedexpressionconstraint() {
			return getRuleContext(RefinedexpressionconstraintContext.class,0);
		}
		public CompoundexpressionconstraintContext compoundexpressionconstraint() {
			return getRuleContext(CompoundexpressionconstraintContext.class,0);
		}
		public SimpleexpressionconstraintContext simpleexpressionconstraint() {
			return getRuleContext(SimpleexpressionconstraintContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(ExpressionTemplateParser.LEFT_PAREN, 0); }
		public ExpressionconstrainttemplateContext expressionconstrainttemplate() {
			return getRuleContext(ExpressionconstrainttemplateContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(ExpressionTemplateParser.RIGHT_PAREN, 0); }
		public ExpressionconstrainttemplateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionconstrainttemplate; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterExpressionconstrainttemplate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitExpressionconstrainttemplate(this);
		}
	}

	public final ExpressionconstrainttemplateContext expressionconstrainttemplate() throws RecognitionException {
		ExpressionconstrainttemplateContext _localctx = new ExpressionconstrainttemplateContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_expressionconstrainttemplate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(672);
			ws();
			setState(682);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
			case 1:
				{
				setState(673);
				refinedexpressionconstraint();
				}
				break;
			case 2:
				{
				setState(674);
				compoundexpressionconstraint();
				}
				break;
			case 3:
				{
				setState(675);
				simpleexpressionconstraint();
				}
				break;
			case 4:
				{
				{
				setState(676);
				match(LEFT_PAREN);
				setState(677);
				ws();
				setState(678);
				expressionconstrainttemplate();
				setState(679);
				ws();
				setState(680);
				match(RIGHT_PAREN);
				}
				}
				break;
			}
			setState(684);
			ws();
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

	public static class SimpleexpressionconstraintContext extends ParserRuleContext {
		public EclfocusconceptContext eclfocusconcept() {
			return getRuleContext(EclfocusconceptContext.class,0);
		}
		public ConstraintoperatorContext constraintoperator() {
			return getRuleContext(ConstraintoperatorContext.class,0);
		}
		public WsContext ws() {
			return getRuleContext(WsContext.class,0);
		}
		public SimpleexpressionconstraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simpleexpressionconstraint; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterSimpleexpressionconstraint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitSimpleexpressionconstraint(this);
		}
	}

	public final SimpleexpressionconstraintContext simpleexpressionconstraint() throws RecognitionException {
		SimpleexpressionconstraintContext _localctx = new SimpleexpressionconstraintContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_simpleexpressionconstraint);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(689);
			_la = _input.LA(1);
			if (_la==LESS_THAN || _la==GREATER_THAN) {
				{
				setState(686);
				constraintoperator();
				setState(687);
				ws();
				}
			}

			setState(691);
			eclfocusconcept();
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

	public static class RefinedexpressionconstraintContext extends ParserRuleContext {
		public SimpleexpressionconstraintContext simpleexpressionconstraint() {
			return getRuleContext(SimpleexpressionconstraintContext.class,0);
		}
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public TerminalNode COLON() { return getToken(ExpressionTemplateParser.COLON, 0); }
		public EclrefinementContext eclrefinement() {
			return getRuleContext(EclrefinementContext.class,0);
		}
		public RefinedexpressionconstraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_refinedexpressionconstraint; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterRefinedexpressionconstraint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitRefinedexpressionconstraint(this);
		}
	}

	public final RefinedexpressionconstraintContext refinedexpressionconstraint() throws RecognitionException {
		RefinedexpressionconstraintContext _localctx = new RefinedexpressionconstraintContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_refinedexpressionconstraint);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(693);
			simpleexpressionconstraint();
			setState(694);
			ws();
			setState(695);
			match(COLON);
			setState(696);
			ws();
			setState(697);
			eclrefinement();
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

	public static class CompoundexpressionconstraintContext extends ParserRuleContext {
		public ConjunctionexpressionconstraintContext conjunctionexpressionconstraint() {
			return getRuleContext(ConjunctionexpressionconstraintContext.class,0);
		}
		public DisjunctionexpressionconstraintContext disjunctionexpressionconstraint() {
			return getRuleContext(DisjunctionexpressionconstraintContext.class,0);
		}
		public ExclusionexpressionconstraintContext exclusionexpressionconstraint() {
			return getRuleContext(ExclusionexpressionconstraintContext.class,0);
		}
		public CompoundexpressionconstraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compoundexpressionconstraint; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterCompoundexpressionconstraint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitCompoundexpressionconstraint(this);
		}
	}

	public final CompoundexpressionconstraintContext compoundexpressionconstraint() throws RecognitionException {
		CompoundexpressionconstraintContext _localctx = new CompoundexpressionconstraintContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_compoundexpressionconstraint);
		try {
			setState(702);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,49,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(699);
				conjunctionexpressionconstraint();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(700);
				disjunctionexpressionconstraint();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(701);
				exclusionexpressionconstraint();
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

	public static class ConjunctionexpressionconstraintContext extends ParserRuleContext {
		public List<SubexpressionconstraintContext> subexpressionconstraint() {
			return getRuleContexts(SubexpressionconstraintContext.class);
		}
		public SubexpressionconstraintContext subexpressionconstraint(int i) {
			return getRuleContext(SubexpressionconstraintContext.class,i);
		}
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public List<ConjunctionContext> conjunction() {
			return getRuleContexts(ConjunctionContext.class);
		}
		public ConjunctionContext conjunction(int i) {
			return getRuleContext(ConjunctionContext.class,i);
		}
		public ConjunctionexpressionconstraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conjunctionexpressionconstraint; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterConjunctionexpressionconstraint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitConjunctionexpressionconstraint(this);
		}
	}

	public final ConjunctionexpressionconstraintContext conjunctionexpressionconstraint() throws RecognitionException {
		ConjunctionexpressionconstraintContext _localctx = new ConjunctionexpressionconstraintContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_conjunctionexpressionconstraint);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(704);
			subexpressionconstraint();
			setState(710); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(705);
					ws();
					setState(706);
					conjunction();
					setState(707);
					ws();
					setState(708);
					subexpressionconstraint();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(712); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,50,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	public static class DisjunctionexpressionconstraintContext extends ParserRuleContext {
		public List<SubexpressionconstraintContext> subexpressionconstraint() {
			return getRuleContexts(SubexpressionconstraintContext.class);
		}
		public SubexpressionconstraintContext subexpressionconstraint(int i) {
			return getRuleContext(SubexpressionconstraintContext.class,i);
		}
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public List<DisjunctionContext> disjunction() {
			return getRuleContexts(DisjunctionContext.class);
		}
		public DisjunctionContext disjunction(int i) {
			return getRuleContext(DisjunctionContext.class,i);
		}
		public DisjunctionexpressionconstraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_disjunctionexpressionconstraint; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterDisjunctionexpressionconstraint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitDisjunctionexpressionconstraint(this);
		}
	}

	public final DisjunctionexpressionconstraintContext disjunctionexpressionconstraint() throws RecognitionException {
		DisjunctionexpressionconstraintContext _localctx = new DisjunctionexpressionconstraintContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_disjunctionexpressionconstraint);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(714);
			subexpressionconstraint();
			setState(720); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(715);
					ws();
					setState(716);
					disjunction();
					setState(717);
					ws();
					setState(718);
					subexpressionconstraint();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(722); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,51,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	public static class ExclusionexpressionconstraintContext extends ParserRuleContext {
		public List<SubexpressionconstraintContext> subexpressionconstraint() {
			return getRuleContexts(SubexpressionconstraintContext.class);
		}
		public SubexpressionconstraintContext subexpressionconstraint(int i) {
			return getRuleContext(SubexpressionconstraintContext.class,i);
		}
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public ExclusionContext exclusion() {
			return getRuleContext(ExclusionContext.class,0);
		}
		public ExclusionexpressionconstraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exclusionexpressionconstraint; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterExclusionexpressionconstraint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitExclusionexpressionconstraint(this);
		}
	}

	public final ExclusionexpressionconstraintContext exclusionexpressionconstraint() throws RecognitionException {
		ExclusionexpressionconstraintContext _localctx = new ExclusionexpressionconstraintContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_exclusionexpressionconstraint);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(724);
			subexpressionconstraint();
			setState(725);
			ws();
			setState(726);
			exclusion();
			setState(727);
			ws();
			setState(728);
			subexpressionconstraint();
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

	public static class SubexpressionconstraintContext extends ParserRuleContext {
		public SimpleexpressionconstraintContext simpleexpressionconstraint() {
			return getRuleContext(SimpleexpressionconstraintContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(ExpressionTemplateParser.LEFT_PAREN, 0); }
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public ExpressionconstrainttemplateContext expressionconstrainttemplate() {
			return getRuleContext(ExpressionconstrainttemplateContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(ExpressionTemplateParser.RIGHT_PAREN, 0); }
		public SubexpressionconstraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subexpressionconstraint; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterSubexpressionconstraint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitSubexpressionconstraint(this);
		}
	}

	public final SubexpressionconstraintContext subexpressionconstraint() throws RecognitionException {
		SubexpressionconstraintContext _localctx = new SubexpressionconstraintContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_subexpressionconstraint);
		try {
			setState(737);
			switch (_input.LA(1)) {
			case ASTERISK:
			case ONE:
			case TWO:
			case THREE:
			case FOUR:
			case FIVE:
			case SIX:
			case SEVEN:
			case EIGHT:
			case NINE:
			case LESS_THAN:
			case GREATER_THAN:
			case LEFT_BRACE:
			case CARAT:
				enterOuterAlt(_localctx, 1);
				{
				setState(730);
				simpleexpressionconstraint();
				}
				break;
			case LEFT_PAREN:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(731);
				match(LEFT_PAREN);
				setState(732);
				ws();
				setState(733);
				expressionconstrainttemplate();
				setState(734);
				ws();
				setState(735);
				match(RIGHT_PAREN);
				}
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

	public static class EclfocusconceptContext extends ParserRuleContext {
		public ConceptreferenceContext conceptreference() {
			return getRuleContext(ConceptreferenceContext.class,0);
		}
		public WildcardContext wildcard() {
			return getRuleContext(WildcardContext.class,0);
		}
		public MemberofContext memberof() {
			return getRuleContext(MemberofContext.class,0);
		}
		public WsContext ws() {
			return getRuleContext(WsContext.class,0);
		}
		public EclfocusconceptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eclfocusconcept; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterEclfocusconcept(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitEclfocusconcept(this);
		}
	}

	public final EclfocusconceptContext eclfocusconcept() throws RecognitionException {
		EclfocusconceptContext _localctx = new EclfocusconceptContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_eclfocusconcept);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(742);
			_la = _input.LA(1);
			if (_la==CARAT) {
				{
				setState(739);
				memberof();
				setState(740);
				ws();
				}
			}

			setState(746);
			switch (_input.LA(1)) {
			case ONE:
			case TWO:
			case THREE:
			case FOUR:
			case FIVE:
			case SIX:
			case SEVEN:
			case EIGHT:
			case NINE:
			case LEFT_BRACE:
				{
				setState(744);
				conceptreference();
				}
				break;
			case ASTERISK:
				{
				setState(745);
				wildcard();
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static class MemberofContext extends ParserRuleContext {
		public TerminalNode CARAT() { return getToken(ExpressionTemplateParser.CARAT, 0); }
		public MemberofContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_memberof; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterMemberof(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitMemberof(this);
		}
	}

	public final MemberofContext memberof() throws RecognitionException {
		MemberofContext _localctx = new MemberofContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_memberof);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(748);
			match(CARAT);
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

	public static class WildcardContext extends ParserRuleContext {
		public TerminalNode ASTERISK() { return getToken(ExpressionTemplateParser.ASTERISK, 0); }
		public WildcardContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_wildcard; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterWildcard(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitWildcard(this);
		}
	}

	public final WildcardContext wildcard() throws RecognitionException {
		WildcardContext _localctx = new WildcardContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_wildcard);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(750);
			match(ASTERISK);
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

	public static class ConstraintoperatorContext extends ParserRuleContext {
		public ChildofContext childof() {
			return getRuleContext(ChildofContext.class,0);
		}
		public DescendantorselfofContext descendantorselfof() {
			return getRuleContext(DescendantorselfofContext.class,0);
		}
		public DescendantofContext descendantof() {
			return getRuleContext(DescendantofContext.class,0);
		}
		public ParentofContext parentof() {
			return getRuleContext(ParentofContext.class,0);
		}
		public AncestororselfofContext ancestororselfof() {
			return getRuleContext(AncestororselfofContext.class,0);
		}
		public AncestorofContext ancestorof() {
			return getRuleContext(AncestorofContext.class,0);
		}
		public ConstraintoperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constraintoperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterConstraintoperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitConstraintoperator(this);
		}
	}

	public final ConstraintoperatorContext constraintoperator() throws RecognitionException {
		ConstraintoperatorContext _localctx = new ConstraintoperatorContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_constraintoperator);
		try {
			setState(758);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,55,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(752);
				childof();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(753);
				descendantorselfof();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(754);
				descendantof();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(755);
				parentof();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(756);
				ancestororselfof();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(757);
				ancestorof();
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

	public static class DescendantofContext extends ParserRuleContext {
		public TerminalNode LESS_THAN() { return getToken(ExpressionTemplateParser.LESS_THAN, 0); }
		public DescendantofContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_descendantof; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterDescendantof(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitDescendantof(this);
		}
	}

	public final DescendantofContext descendantof() throws RecognitionException {
		DescendantofContext _localctx = new DescendantofContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_descendantof);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(760);
			match(LESS_THAN);
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

	public static class DescendantorselfofContext extends ParserRuleContext {
		public List<TerminalNode> LESS_THAN() { return getTokens(ExpressionTemplateParser.LESS_THAN); }
		public TerminalNode LESS_THAN(int i) {
			return getToken(ExpressionTemplateParser.LESS_THAN, i);
		}
		public DescendantorselfofContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_descendantorselfof; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterDescendantorselfof(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitDescendantorselfof(this);
		}
	}

	public final DescendantorselfofContext descendantorselfof() throws RecognitionException {
		DescendantorselfofContext _localctx = new DescendantorselfofContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_descendantorselfof);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(762);
			match(LESS_THAN);
			setState(763);
			match(LESS_THAN);
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

	public static class ChildofContext extends ParserRuleContext {
		public TerminalNode LESS_THAN() { return getToken(ExpressionTemplateParser.LESS_THAN, 0); }
		public TerminalNode EXCLAMATION() { return getToken(ExpressionTemplateParser.EXCLAMATION, 0); }
		public ChildofContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_childof; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterChildof(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitChildof(this);
		}
	}

	public final ChildofContext childof() throws RecognitionException {
		ChildofContext _localctx = new ChildofContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_childof);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(765);
			match(LESS_THAN);
			setState(766);
			match(EXCLAMATION);
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

	public static class AncestorofContext extends ParserRuleContext {
		public TerminalNode GREATER_THAN() { return getToken(ExpressionTemplateParser.GREATER_THAN, 0); }
		public AncestorofContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ancestorof; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterAncestorof(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitAncestorof(this);
		}
	}

	public final AncestorofContext ancestorof() throws RecognitionException {
		AncestorofContext _localctx = new AncestorofContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_ancestorof);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(768);
			match(GREATER_THAN);
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

	public static class AncestororselfofContext extends ParserRuleContext {
		public List<TerminalNode> GREATER_THAN() { return getTokens(ExpressionTemplateParser.GREATER_THAN); }
		public TerminalNode GREATER_THAN(int i) {
			return getToken(ExpressionTemplateParser.GREATER_THAN, i);
		}
		public AncestororselfofContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ancestororselfof; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterAncestororselfof(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitAncestororselfof(this);
		}
	}

	public final AncestororselfofContext ancestororselfof() throws RecognitionException {
		AncestororselfofContext _localctx = new AncestororselfofContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_ancestororselfof);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(770);
			match(GREATER_THAN);
			setState(771);
			match(GREATER_THAN);
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

	public static class ParentofContext extends ParserRuleContext {
		public TerminalNode GREATER_THAN() { return getToken(ExpressionTemplateParser.GREATER_THAN, 0); }
		public TerminalNode EXCLAMATION() { return getToken(ExpressionTemplateParser.EXCLAMATION, 0); }
		public ParentofContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parentof; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterParentof(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitParentof(this);
		}
	}

	public final ParentofContext parentof() throws RecognitionException {
		ParentofContext _localctx = new ParentofContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_parentof);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(773);
			match(GREATER_THAN);
			setState(774);
			match(EXCLAMATION);
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

	public static class ConjunctionContext extends ParserRuleContext {
		public MwsContext mws() {
			return getRuleContext(MwsContext.class,0);
		}
		public TerminalNode A() { return getToken(ExpressionTemplateParser.A, 0); }
		public TerminalNode CAP_A() { return getToken(ExpressionTemplateParser.CAP_A, 0); }
		public TerminalNode N() { return getToken(ExpressionTemplateParser.N, 0); }
		public TerminalNode CAP_N() { return getToken(ExpressionTemplateParser.CAP_N, 0); }
		public TerminalNode D() { return getToken(ExpressionTemplateParser.D, 0); }
		public TerminalNode CAP_D() { return getToken(ExpressionTemplateParser.CAP_D, 0); }
		public TerminalNode COMMA() { return getToken(ExpressionTemplateParser.COMMA, 0); }
		public ConjunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conjunction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterConjunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitConjunction(this);
		}
	}

	public final ConjunctionContext conjunction() throws RecognitionException {
		ConjunctionContext _localctx = new ConjunctionContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_conjunction);
		int _la;
		try {
			setState(781);
			switch (_input.LA(1)) {
			case CAP_A:
			case A:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(776);
				_la = _input.LA(1);
				if ( !(_la==CAP_A || _la==A) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(777);
				_la = _input.LA(1);
				if ( !(_la==CAP_N || _la==N) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(778);
				_la = _input.LA(1);
				if ( !(_la==CAP_D || _la==D) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(779);
				mws();
				}
				}
				break;
			case COMMA:
				enterOuterAlt(_localctx, 2);
				{
				setState(780);
				match(COMMA);
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

	public static class DisjunctionContext extends ParserRuleContext {
		public MwsContext mws() {
			return getRuleContext(MwsContext.class,0);
		}
		public TerminalNode O() { return getToken(ExpressionTemplateParser.O, 0); }
		public TerminalNode CAP_O() { return getToken(ExpressionTemplateParser.CAP_O, 0); }
		public TerminalNode R() { return getToken(ExpressionTemplateParser.R, 0); }
		public TerminalNode CAP_R() { return getToken(ExpressionTemplateParser.CAP_R, 0); }
		public DisjunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_disjunction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterDisjunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitDisjunction(this);
		}
	}

	public final DisjunctionContext disjunction() throws RecognitionException {
		DisjunctionContext _localctx = new DisjunctionContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_disjunction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(783);
			_la = _input.LA(1);
			if ( !(_la==CAP_O || _la==O) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(784);
			_la = _input.LA(1);
			if ( !(_la==CAP_R || _la==R) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(785);
			mws();
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

	public static class ExclusionContext extends ParserRuleContext {
		public MwsContext mws() {
			return getRuleContext(MwsContext.class,0);
		}
		public TerminalNode M() { return getToken(ExpressionTemplateParser.M, 0); }
		public TerminalNode CAP_M() { return getToken(ExpressionTemplateParser.CAP_M, 0); }
		public TerminalNode I() { return getToken(ExpressionTemplateParser.I, 0); }
		public TerminalNode CAP_I() { return getToken(ExpressionTemplateParser.CAP_I, 0); }
		public TerminalNode N() { return getToken(ExpressionTemplateParser.N, 0); }
		public TerminalNode CAP_N() { return getToken(ExpressionTemplateParser.CAP_N, 0); }
		public TerminalNode U() { return getToken(ExpressionTemplateParser.U, 0); }
		public TerminalNode CAP_U() { return getToken(ExpressionTemplateParser.CAP_U, 0); }
		public TerminalNode S() { return getToken(ExpressionTemplateParser.S, 0); }
		public TerminalNode CAP_S() { return getToken(ExpressionTemplateParser.CAP_S, 0); }
		public ExclusionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exclusion; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterExclusion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitExclusion(this);
		}
	}

	public final ExclusionContext exclusion() throws RecognitionException {
		ExclusionContext _localctx = new ExclusionContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_exclusion);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(787);
			_la = _input.LA(1);
			if ( !(_la==CAP_M || _la==M) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(788);
			_la = _input.LA(1);
			if ( !(_la==CAP_I || _la==I) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(789);
			_la = _input.LA(1);
			if ( !(_la==CAP_N || _la==N) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(790);
			_la = _input.LA(1);
			if ( !(_la==CAP_U || _la==U) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(791);
			_la = _input.LA(1);
			if ( !(_la==CAP_S || _la==S) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(792);
			mws();
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

	public static class EclrefinementContext extends ParserRuleContext {
		public SubrefinementContext subrefinement() {
			return getRuleContext(SubrefinementContext.class,0);
		}
		public WsContext ws() {
			return getRuleContext(WsContext.class,0);
		}
		public ConjunctionrefinementsetContext conjunctionrefinementset() {
			return getRuleContext(ConjunctionrefinementsetContext.class,0);
		}
		public DisjunctionrefinementsetContext disjunctionrefinementset() {
			return getRuleContext(DisjunctionrefinementsetContext.class,0);
		}
		public EclrefinementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eclrefinement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterEclrefinement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitEclrefinement(this);
		}
	}

	public final EclrefinementContext eclrefinement() throws RecognitionException {
		EclrefinementContext _localctx = new EclrefinementContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_eclrefinement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(794);
			subrefinement();
			setState(795);
			ws();
			setState(798);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
			case 1:
				{
				setState(796);
				conjunctionrefinementset();
				}
				break;
			case 2:
				{
				setState(797);
				disjunctionrefinementset();
				}
				break;
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

	public static class ConjunctionrefinementsetContext extends ParserRuleContext {
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public List<ConjunctionContext> conjunction() {
			return getRuleContexts(ConjunctionContext.class);
		}
		public ConjunctionContext conjunction(int i) {
			return getRuleContext(ConjunctionContext.class,i);
		}
		public List<SubrefinementContext> subrefinement() {
			return getRuleContexts(SubrefinementContext.class);
		}
		public SubrefinementContext subrefinement(int i) {
			return getRuleContext(SubrefinementContext.class,i);
		}
		public ConjunctionrefinementsetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conjunctionrefinementset; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterConjunctionrefinementset(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitConjunctionrefinementset(this);
		}
	}

	public final ConjunctionrefinementsetContext conjunctionrefinementset() throws RecognitionException {
		ConjunctionrefinementsetContext _localctx = new ConjunctionrefinementsetContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_conjunctionrefinementset);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(805); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(800);
					ws();
					setState(801);
					conjunction();
					setState(802);
					ws();
					setState(803);
					subrefinement();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(807); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,58,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	public static class DisjunctionrefinementsetContext extends ParserRuleContext {
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public List<DisjunctionContext> disjunction() {
			return getRuleContexts(DisjunctionContext.class);
		}
		public DisjunctionContext disjunction(int i) {
			return getRuleContext(DisjunctionContext.class,i);
		}
		public List<SubrefinementContext> subrefinement() {
			return getRuleContexts(SubrefinementContext.class);
		}
		public SubrefinementContext subrefinement(int i) {
			return getRuleContext(SubrefinementContext.class,i);
		}
		public DisjunctionrefinementsetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_disjunctionrefinementset; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterDisjunctionrefinementset(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitDisjunctionrefinementset(this);
		}
	}

	public final DisjunctionrefinementsetContext disjunctionrefinementset() throws RecognitionException {
		DisjunctionrefinementsetContext _localctx = new DisjunctionrefinementsetContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_disjunctionrefinementset);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(814); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(809);
					ws();
					setState(810);
					disjunction();
					setState(811);
					ws();
					setState(812);
					subrefinement();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(816); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,59,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	public static class SubrefinementContext extends ParserRuleContext {
		public EclattributesetContext eclattributeset() {
			return getRuleContext(EclattributesetContext.class,0);
		}
		public EclattributegroupContext eclattributegroup() {
			return getRuleContext(EclattributegroupContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(ExpressionTemplateParser.LEFT_PAREN, 0); }
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public EclrefinementContext eclrefinement() {
			return getRuleContext(EclrefinementContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(ExpressionTemplateParser.RIGHT_PAREN, 0); }
		public SubrefinementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subrefinement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterSubrefinement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitSubrefinement(this);
		}
	}

	public final SubrefinementContext subrefinement() throws RecognitionException {
		SubrefinementContext _localctx = new SubrefinementContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_subrefinement);
		try {
			setState(826);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(818);
				eclattributeset();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(819);
				eclattributegroup();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				{
				setState(820);
				match(LEFT_PAREN);
				setState(821);
				ws();
				setState(822);
				eclrefinement();
				setState(823);
				ws();
				setState(824);
				match(RIGHT_PAREN);
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

	public static class EclattributesetContext extends ParserRuleContext {
		public SubattributesetContext subattributeset() {
			return getRuleContext(SubattributesetContext.class,0);
		}
		public WsContext ws() {
			return getRuleContext(WsContext.class,0);
		}
		public ConjunctionattributesetContext conjunctionattributeset() {
			return getRuleContext(ConjunctionattributesetContext.class,0);
		}
		public DisjunctionattributesetContext disjunctionattributeset() {
			return getRuleContext(DisjunctionattributesetContext.class,0);
		}
		public EclattributesetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eclattributeset; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterEclattributeset(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitEclattributeset(this);
		}
	}

	public final EclattributesetContext eclattributeset() throws RecognitionException {
		EclattributesetContext _localctx = new EclattributesetContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_eclattributeset);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(828);
			subattributeset();
			setState(829);
			ws();
			setState(832);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
			case 1:
				{
				setState(830);
				conjunctionattributeset();
				}
				break;
			case 2:
				{
				setState(831);
				disjunctionattributeset();
				}
				break;
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

	public static class ConjunctionattributesetContext extends ParserRuleContext {
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public List<ConjunctionContext> conjunction() {
			return getRuleContexts(ConjunctionContext.class);
		}
		public ConjunctionContext conjunction(int i) {
			return getRuleContext(ConjunctionContext.class,i);
		}
		public List<SubattributesetContext> subattributeset() {
			return getRuleContexts(SubattributesetContext.class);
		}
		public SubattributesetContext subattributeset(int i) {
			return getRuleContext(SubattributesetContext.class,i);
		}
		public ConjunctionattributesetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conjunctionattributeset; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterConjunctionattributeset(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitConjunctionattributeset(this);
		}
	}

	public final ConjunctionattributesetContext conjunctionattributeset() throws RecognitionException {
		ConjunctionattributesetContext _localctx = new ConjunctionattributesetContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_conjunctionattributeset);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(839); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(834);
					ws();
					setState(835);
					conjunction();
					setState(836);
					ws();
					setState(837);
					subattributeset();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(841); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,62,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	public static class DisjunctionattributesetContext extends ParserRuleContext {
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public List<DisjunctionContext> disjunction() {
			return getRuleContexts(DisjunctionContext.class);
		}
		public DisjunctionContext disjunction(int i) {
			return getRuleContext(DisjunctionContext.class,i);
		}
		public List<SubattributesetContext> subattributeset() {
			return getRuleContexts(SubattributesetContext.class);
		}
		public SubattributesetContext subattributeset(int i) {
			return getRuleContext(SubattributesetContext.class,i);
		}
		public DisjunctionattributesetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_disjunctionattributeset; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterDisjunctionattributeset(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitDisjunctionattributeset(this);
		}
	}

	public final DisjunctionattributesetContext disjunctionattributeset() throws RecognitionException {
		DisjunctionattributesetContext _localctx = new DisjunctionattributesetContext(_ctx, getState());
		enterRule(_localctx, 154, RULE_disjunctionattributeset);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(848); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(843);
					ws();
					setState(844);
					disjunction();
					setState(845);
					ws();
					setState(846);
					subattributeset();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(850); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,63,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	public static class SubattributesetContext extends ParserRuleContext {
		public EclattributeContext eclattribute() {
			return getRuleContext(EclattributeContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(ExpressionTemplateParser.LEFT_PAREN, 0); }
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public EclattributesetContext eclattributeset() {
			return getRuleContext(EclattributesetContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(ExpressionTemplateParser.RIGHT_PAREN, 0); }
		public SubattributesetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subattributeset; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterSubattributeset(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitSubattributeset(this);
		}
	}

	public final SubattributesetContext subattributeset() throws RecognitionException {
		SubattributesetContext _localctx = new SubattributesetContext(_ctx, getState());
		enterRule(_localctx, 156, RULE_subattributeset);
		try {
			setState(859);
			switch (_input.LA(1)) {
			case ASTERISK:
			case ONE:
			case TWO:
			case THREE:
			case FOUR:
			case FIVE:
			case SIX:
			case SEVEN:
			case EIGHT:
			case NINE:
			case LESS_THAN:
			case CAP_R:
			case LEFT_BRACE:
				enterOuterAlt(_localctx, 1);
				{
				setState(852);
				eclattribute();
				}
				break;
			case LEFT_PAREN:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(853);
				match(LEFT_PAREN);
				setState(854);
				ws();
				setState(855);
				eclattributeset();
				setState(856);
				ws();
				setState(857);
				match(RIGHT_PAREN);
				}
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

	public static class EclattributegroupContext extends ParserRuleContext {
		public TerminalNode LEFT_CURLY_BRACE() { return getToken(ExpressionTemplateParser.LEFT_CURLY_BRACE, 0); }
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public EclattributesetContext eclattributeset() {
			return getRuleContext(EclattributesetContext.class,0);
		}
		public TerminalNode RIGHT_CURLY_BRACE() { return getToken(ExpressionTemplateParser.RIGHT_CURLY_BRACE, 0); }
		public TerminalNode LEFT_BRACE() { return getToken(ExpressionTemplateParser.LEFT_BRACE, 0); }
		public CardinalityContext cardinality() {
			return getRuleContext(CardinalityContext.class,0);
		}
		public TerminalNode RIGHT_BRACE() { return getToken(ExpressionTemplateParser.RIGHT_BRACE, 0); }
		public EclattributegroupContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eclattributegroup; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterEclattributegroup(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitEclattributegroup(this);
		}
	}

	public final EclattributegroupContext eclattributegroup() throws RecognitionException {
		EclattributegroupContext _localctx = new EclattributegroupContext(_ctx, getState());
		enterRule(_localctx, 158, RULE_eclattributegroup);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(866);
			_la = _input.LA(1);
			if (_la==LEFT_BRACE) {
				{
				setState(861);
				match(LEFT_BRACE);
				setState(862);
				cardinality();
				setState(863);
				ws();
				setState(864);
				match(RIGHT_BRACE);
				}
			}

			setState(868);
			match(LEFT_CURLY_BRACE);
			setState(869);
			ws();
			setState(870);
			eclattributeset();
			setState(871);
			ws();
			setState(872);
			match(RIGHT_CURLY_BRACE);
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

	public static class EclattributeContext extends ParserRuleContext {
		public EclattributenameContext eclattributename() {
			return getRuleContext(EclattributenameContext.class,0);
		}
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public TerminalNode LEFT_BRACE() { return getToken(ExpressionTemplateParser.LEFT_BRACE, 0); }
		public CardinalityContext cardinality() {
			return getRuleContext(CardinalityContext.class,0);
		}
		public TerminalNode RIGHT_BRACE() { return getToken(ExpressionTemplateParser.RIGHT_BRACE, 0); }
		public ReverseflagContext reverseflag() {
			return getRuleContext(ReverseflagContext.class,0);
		}
		public AttributeoperatorContext attributeoperator() {
			return getRuleContext(AttributeoperatorContext.class,0);
		}
		public ExpressioncomparisonoperatorContext expressioncomparisonoperator() {
			return getRuleContext(ExpressioncomparisonoperatorContext.class,0);
		}
		public ExpressionconstraintvalueContext expressionconstraintvalue() {
			return getRuleContext(ExpressionconstraintvalueContext.class,0);
		}
		public NumericcomparisonoperatorContext numericcomparisonoperator() {
			return getRuleContext(NumericcomparisonoperatorContext.class,0);
		}
		public TerminalNode POUND() { return getToken(ExpressionTemplateParser.POUND, 0); }
		public NumericvalueContext numericvalue() {
			return getRuleContext(NumericvalueContext.class,0);
		}
		public StringcomparisonoperatorContext stringcomparisonoperator() {
			return getRuleContext(StringcomparisonoperatorContext.class,0);
		}
		public List<QmContext> qm() {
			return getRuleContexts(QmContext.class);
		}
		public QmContext qm(int i) {
			return getRuleContext(QmContext.class,i);
		}
		public StringvalueContext stringvalue() {
			return getRuleContext(StringvalueContext.class,0);
		}
		public EclattributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eclattribute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterEclattribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitEclattribute(this);
		}
	}

	public final EclattributeContext eclattribute() throws RecognitionException {
		EclattributeContext _localctx = new EclattributeContext(_ctx, getState());
		enterRule(_localctx, 160, RULE_eclattribute);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(879);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,66,_ctx) ) {
			case 1:
				{
				setState(874);
				match(LEFT_BRACE);
				setState(875);
				cardinality();
				setState(876);
				match(RIGHT_BRACE);
				setState(877);
				ws();
				}
				break;
			}
			setState(884);
			_la = _input.LA(1);
			if (_la==CAP_R) {
				{
				setState(881);
				reverseflag();
				setState(882);
				ws();
				}
			}

			setState(889);
			_la = _input.LA(1);
			if (_la==LESS_THAN) {
				{
				setState(886);
				attributeoperator();
				setState(887);
				ws();
				}
			}

			setState(891);
			eclattributename();
			setState(892);
			ws();
			setState(908);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,69,_ctx) ) {
			case 1:
				{
				{
				setState(893);
				expressioncomparisonoperator();
				setState(894);
				ws();
				setState(895);
				expressionconstraintvalue();
				}
				}
				break;
			case 2:
				{
				{
				setState(897);
				numericcomparisonoperator();
				setState(898);
				ws();
				setState(899);
				match(POUND);
				setState(900);
				numericvalue();
				}
				}
				break;
			case 3:
				{
				{
				setState(902);
				stringcomparisonoperator();
				setState(903);
				ws();
				setState(904);
				qm();
				setState(905);
				stringvalue();
				setState(906);
				qm();
				}
				}
				break;
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

	public static class CardinalityContext extends ParserRuleContext {
		public MinvalueContext minvalue() {
			return getRuleContext(MinvalueContext.class,0);
		}
		public ToContext to() {
			return getRuleContext(ToContext.class,0);
		}
		public MaxvalueContext maxvalue() {
			return getRuleContext(MaxvalueContext.class,0);
		}
		public CardinalityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cardinality; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterCardinality(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitCardinality(this);
		}
	}

	public final CardinalityContext cardinality() throws RecognitionException {
		CardinalityContext _localctx = new CardinalityContext(_ctx, getState());
		enterRule(_localctx, 162, RULE_cardinality);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(910);
			minvalue();
			setState(911);
			to();
			setState(912);
			maxvalue();
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

	public static class MinvalueContext extends ParserRuleContext {
		public NonnegativeintegervalueContext nonnegativeintegervalue() {
			return getRuleContext(NonnegativeintegervalueContext.class,0);
		}
		public MinvalueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_minvalue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterMinvalue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitMinvalue(this);
		}
	}

	public final MinvalueContext minvalue() throws RecognitionException {
		MinvalueContext _localctx = new MinvalueContext(_ctx, getState());
		enterRule(_localctx, 164, RULE_minvalue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(914);
			nonnegativeintegervalue();
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

	public static class ToContext extends ParserRuleContext {
		public List<TerminalNode> PERIOD() { return getTokens(ExpressionTemplateParser.PERIOD); }
		public TerminalNode PERIOD(int i) {
			return getToken(ExpressionTemplateParser.PERIOD, i);
		}
		public ToContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_to; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterTo(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitTo(this);
		}
	}

	public final ToContext to() throws RecognitionException {
		ToContext _localctx = new ToContext(_ctx, getState());
		enterRule(_localctx, 166, RULE_to);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(916);
			match(PERIOD);
			setState(917);
			match(PERIOD);
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

	public static class MaxvalueContext extends ParserRuleContext {
		public NonnegativeintegervalueContext nonnegativeintegervalue() {
			return getRuleContext(NonnegativeintegervalueContext.class,0);
		}
		public ManyContext many() {
			return getRuleContext(ManyContext.class,0);
		}
		public MaxvalueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_maxvalue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterMaxvalue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitMaxvalue(this);
		}
	}

	public final MaxvalueContext maxvalue() throws RecognitionException {
		MaxvalueContext _localctx = new MaxvalueContext(_ctx, getState());
		enterRule(_localctx, 168, RULE_maxvalue);
		try {
			setState(921);
			switch (_input.LA(1)) {
			case ZERO:
			case ONE:
			case TWO:
			case THREE:
			case FOUR:
			case FIVE:
			case SIX:
			case SEVEN:
			case EIGHT:
			case NINE:
				enterOuterAlt(_localctx, 1);
				{
				setState(919);
				nonnegativeintegervalue();
				}
				break;
			case ASTERISK:
				enterOuterAlt(_localctx, 2);
				{
				setState(920);
				many();
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

	public static class ManyContext extends ParserRuleContext {
		public TerminalNode ASTERISK() { return getToken(ExpressionTemplateParser.ASTERISK, 0); }
		public ManyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_many; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterMany(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitMany(this);
		}
	}

	public final ManyContext many() throws RecognitionException {
		ManyContext _localctx = new ManyContext(_ctx, getState());
		enterRule(_localctx, 170, RULE_many);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(923);
			match(ASTERISK);
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

	public static class ReverseflagContext extends ParserRuleContext {
		public TerminalNode CAP_R() { return getToken(ExpressionTemplateParser.CAP_R, 0); }
		public ReverseflagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_reverseflag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterReverseflag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitReverseflag(this);
		}
	}

	public final ReverseflagContext reverseflag() throws RecognitionException {
		ReverseflagContext _localctx = new ReverseflagContext(_ctx, getState());
		enterRule(_localctx, 172, RULE_reverseflag);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(925);
			match(CAP_R);
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

	public static class AttributeoperatorContext extends ParserRuleContext {
		public DescendantorselfofContext descendantorselfof() {
			return getRuleContext(DescendantorselfofContext.class,0);
		}
		public DescendantofContext descendantof() {
			return getRuleContext(DescendantofContext.class,0);
		}
		public AttributeoperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributeoperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterAttributeoperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitAttributeoperator(this);
		}
	}

	public final AttributeoperatorContext attributeoperator() throws RecognitionException {
		AttributeoperatorContext _localctx = new AttributeoperatorContext(_ctx, getState());
		enterRule(_localctx, 174, RULE_attributeoperator);
		try {
			setState(929);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,71,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(927);
				descendantorselfof();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(928);
				descendantof();
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

	public static class EclattributenameContext extends ParserRuleContext {
		public ConceptreferenceContext conceptreference() {
			return getRuleContext(ConceptreferenceContext.class,0);
		}
		public WildcardContext wildcard() {
			return getRuleContext(WildcardContext.class,0);
		}
		public EclattributenameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eclattributename; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterEclattributename(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitEclattributename(this);
		}
	}

	public final EclattributenameContext eclattributename() throws RecognitionException {
		EclattributenameContext _localctx = new EclattributenameContext(_ctx, getState());
		enterRule(_localctx, 176, RULE_eclattributename);
		try {
			setState(933);
			switch (_input.LA(1)) {
			case ONE:
			case TWO:
			case THREE:
			case FOUR:
			case FIVE:
			case SIX:
			case SEVEN:
			case EIGHT:
			case NINE:
			case LEFT_BRACE:
				enterOuterAlt(_localctx, 1);
				{
				setState(931);
				conceptreference();
				}
				break;
			case ASTERISK:
				enterOuterAlt(_localctx, 2);
				{
				setState(932);
				wildcard();
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

	public static class ExpressionconstraintvalueContext extends ParserRuleContext {
		public SimpleexpressionconstraintContext simpleexpressionconstraint() {
			return getRuleContext(SimpleexpressionconstraintContext.class,0);
		}
		public TerminalNode LEFT_PAREN() { return getToken(ExpressionTemplateParser.LEFT_PAREN, 0); }
		public List<WsContext> ws() {
			return getRuleContexts(WsContext.class);
		}
		public WsContext ws(int i) {
			return getRuleContext(WsContext.class,i);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(ExpressionTemplateParser.RIGHT_PAREN, 0); }
		public RefinedexpressionconstraintContext refinedexpressionconstraint() {
			return getRuleContext(RefinedexpressionconstraintContext.class,0);
		}
		public CompoundexpressionconstraintContext compoundexpressionconstraint() {
			return getRuleContext(CompoundexpressionconstraintContext.class,0);
		}
		public ExpressionconstraintvalueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionconstraintvalue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterExpressionconstraintvalue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitExpressionconstraintvalue(this);
		}
	}

	public final ExpressionconstraintvalueContext expressionconstraintvalue() throws RecognitionException {
		ExpressionconstraintvalueContext _localctx = new ExpressionconstraintvalueContext(_ctx, getState());
		enterRule(_localctx, 178, RULE_expressionconstraintvalue);
		try {
			setState(945);
			switch (_input.LA(1)) {
			case ASTERISK:
			case ONE:
			case TWO:
			case THREE:
			case FOUR:
			case FIVE:
			case SIX:
			case SEVEN:
			case EIGHT:
			case NINE:
			case LESS_THAN:
			case GREATER_THAN:
			case LEFT_BRACE:
			case CARAT:
				enterOuterAlt(_localctx, 1);
				{
				setState(935);
				simpleexpressionconstraint();
				}
				break;
			case LEFT_PAREN:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(936);
				match(LEFT_PAREN);
				setState(937);
				ws();
				setState(940);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,73,_ctx) ) {
				case 1:
					{
					setState(938);
					refinedexpressionconstraint();
					}
					break;
				case 2:
					{
					setState(939);
					compoundexpressionconstraint();
					}
					break;
				}
				setState(942);
				ws();
				setState(943);
				match(RIGHT_PAREN);
				}
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

	public static class ExpressioncomparisonoperatorContext extends ParserRuleContext {
		public TerminalNode EQUALS() { return getToken(ExpressionTemplateParser.EQUALS, 0); }
		public TerminalNode EXCLAMATION() { return getToken(ExpressionTemplateParser.EXCLAMATION, 0); }
		public ExpressioncomparisonoperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressioncomparisonoperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterExpressioncomparisonoperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitExpressioncomparisonoperator(this);
		}
	}

	public final ExpressioncomparisonoperatorContext expressioncomparisonoperator() throws RecognitionException {
		ExpressioncomparisonoperatorContext _localctx = new ExpressioncomparisonoperatorContext(_ctx, getState());
		enterRule(_localctx, 180, RULE_expressioncomparisonoperator);
		try {
			setState(950);
			switch (_input.LA(1)) {
			case EQUALS:
				enterOuterAlt(_localctx, 1);
				{
				setState(947);
				match(EQUALS);
				}
				break;
			case EXCLAMATION:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(948);
				match(EXCLAMATION);
				setState(949);
				match(EQUALS);
				}
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

	public static class NumericcomparisonoperatorContext extends ParserRuleContext {
		public TerminalNode EQUALS() { return getToken(ExpressionTemplateParser.EQUALS, 0); }
		public TerminalNode EXCLAMATION() { return getToken(ExpressionTemplateParser.EXCLAMATION, 0); }
		public TerminalNode LESS_THAN() { return getToken(ExpressionTemplateParser.LESS_THAN, 0); }
		public TerminalNode GREATER_THAN() { return getToken(ExpressionTemplateParser.GREATER_THAN, 0); }
		public NumericcomparisonoperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numericcomparisonoperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterNumericcomparisonoperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitNumericcomparisonoperator(this);
		}
	}

	public final NumericcomparisonoperatorContext numericcomparisonoperator() throws RecognitionException {
		NumericcomparisonoperatorContext _localctx = new NumericcomparisonoperatorContext(_ctx, getState());
		enterRule(_localctx, 182, RULE_numericcomparisonoperator);
		try {
			setState(961);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,76,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(952);
				match(EQUALS);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(953);
				match(EXCLAMATION);
				setState(954);
				match(EQUALS);
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				{
				setState(955);
				match(LESS_THAN);
				setState(956);
				match(EQUALS);
				}
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(957);
				match(LESS_THAN);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				{
				setState(958);
				match(GREATER_THAN);
				setState(959);
				match(EQUALS);
				}
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(960);
				match(GREATER_THAN);
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

	public static class StringcomparisonoperatorContext extends ParserRuleContext {
		public TerminalNode EQUALS() { return getToken(ExpressionTemplateParser.EQUALS, 0); }
		public TerminalNode EXCLAMATION() { return getToken(ExpressionTemplateParser.EXCLAMATION, 0); }
		public StringcomparisonoperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringcomparisonoperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterStringcomparisonoperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitStringcomparisonoperator(this);
		}
	}

	public final StringcomparisonoperatorContext stringcomparisonoperator() throws RecognitionException {
		StringcomparisonoperatorContext _localctx = new StringcomparisonoperatorContext(_ctx, getState());
		enterRule(_localctx, 184, RULE_stringcomparisonoperator);
		try {
			setState(966);
			switch (_input.LA(1)) {
			case EQUALS:
				enterOuterAlt(_localctx, 1);
				{
				setState(963);
				match(EQUALS);
				}
				break;
			case EXCLAMATION:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(964);
				match(EXCLAMATION);
				setState(965);
				match(EQUALS);
				}
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

	public static class NonnegativeintegervalueContext extends ParserRuleContext {
		public DigitnonzeroContext digitnonzero() {
			return getRuleContext(DigitnonzeroContext.class,0);
		}
		public List<DigitContext> digit() {
			return getRuleContexts(DigitContext.class);
		}
		public DigitContext digit(int i) {
			return getRuleContext(DigitContext.class,i);
		}
		public ZeroContext zero() {
			return getRuleContext(ZeroContext.class,0);
		}
		public NonnegativeintegervalueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nonnegativeintegervalue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterNonnegativeintegervalue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitNonnegativeintegervalue(this);
		}
	}

	public final NonnegativeintegervalueContext nonnegativeintegervalue() throws RecognitionException {
		NonnegativeintegervalueContext _localctx = new NonnegativeintegervalueContext(_ctx, getState());
		enterRule(_localctx, 186, RULE_nonnegativeintegervalue);
		int _la;
		try {
			setState(976);
			switch (_input.LA(1)) {
			case ONE:
			case TWO:
			case THREE:
			case FOUR:
			case FIVE:
			case SIX:
			case SEVEN:
			case EIGHT:
			case NINE:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(968);
				digitnonzero();
				setState(972);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ZERO) | (1L << ONE) | (1L << TWO) | (1L << THREE) | (1L << FOUR) | (1L << FIVE) | (1L << SIX) | (1L << SEVEN) | (1L << EIGHT) | (1L << NINE))) != 0)) {
					{
					{
					setState(969);
					digit();
					}
					}
					setState(974);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				}
				break;
			case ZERO:
				enterOuterAlt(_localctx, 2);
				{
				setState(975);
				zero();
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

	public static class MwsContext extends ParserRuleContext {
		public List<SpContext> sp() {
			return getRuleContexts(SpContext.class);
		}
		public SpContext sp(int i) {
			return getRuleContext(SpContext.class,i);
		}
		public List<HtabContext> htab() {
			return getRuleContexts(HtabContext.class);
		}
		public HtabContext htab(int i) {
			return getRuleContext(HtabContext.class,i);
		}
		public List<CrContext> cr() {
			return getRuleContexts(CrContext.class);
		}
		public CrContext cr(int i) {
			return getRuleContext(CrContext.class,i);
		}
		public List<LfContext> lf() {
			return getRuleContexts(LfContext.class);
		}
		public LfContext lf(int i) {
			return getRuleContext(LfContext.class,i);
		}
		public List<CommentContext> comment() {
			return getRuleContexts(CommentContext.class);
		}
		public CommentContext comment(int i) {
			return getRuleContext(CommentContext.class,i);
		}
		public MwsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mws; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterMws(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitMws(this);
		}
	}

	public final MwsContext mws() throws RecognitionException {
		MwsContext _localctx = new MwsContext(_ctx, getState());
		enterRule(_localctx, 188, RULE_mws);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(983); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					setState(983);
					switch (_input.LA(1)) {
					case SPACE:
						{
						setState(978);
						sp();
						}
						break;
					case TAB:
						{
						setState(979);
						htab();
						}
						break;
					case CR:
						{
						setState(980);
						cr();
						}
						break;
					case LF:
						{
						setState(981);
						lf();
						}
						break;
					case SLASH:
						{
						setState(982);
						comment();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(985); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,81,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	public static class CommentContext extends ParserRuleContext {
		public List<TerminalNode> SLASH() { return getTokens(ExpressionTemplateParser.SLASH); }
		public TerminalNode SLASH(int i) {
			return getToken(ExpressionTemplateParser.SLASH, i);
		}
		public List<TerminalNode> ASTERISK() { return getTokens(ExpressionTemplateParser.ASTERISK); }
		public TerminalNode ASTERISK(int i) {
			return getToken(ExpressionTemplateParser.ASTERISK, i);
		}
		public List<NonstarcharContext> nonstarchar() {
			return getRuleContexts(NonstarcharContext.class);
		}
		public NonstarcharContext nonstarchar(int i) {
			return getRuleContext(NonstarcharContext.class,i);
		}
		public List<StarwithnonfslashContext> starwithnonfslash() {
			return getRuleContexts(StarwithnonfslashContext.class);
		}
		public StarwithnonfslashContext starwithnonfslash(int i) {
			return getRuleContext(StarwithnonfslashContext.class,i);
		}
		public CommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitComment(this);
		}
	}

	public final CommentContext comment() throws RecognitionException {
		CommentContext _localctx = new CommentContext(_ctx, getState());
		enterRule(_localctx, 190, RULE_comment);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(987);
			match(SLASH);
			setState(988);
			match(ASTERISK);
			}
			setState(994);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,83,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(992);
					switch (_input.LA(1)) {
					case TAB:
					case LF:
					case CR:
					case SPACE:
					case EXCLAMATION:
					case QUOTE:
					case POUND:
					case DOLLAR:
					case PERCENT:
					case AMPERSAND:
					case APOSTROPHE:
					case LEFT_PAREN:
					case RIGHT_PAREN:
					case PLUS:
					case COMMA:
					case DASH:
					case PERIOD:
					case SLASH:
					case ZERO:
					case ONE:
					case TWO:
					case THREE:
					case FOUR:
					case FIVE:
					case SIX:
					case SEVEN:
					case EIGHT:
					case NINE:
					case COLON:
					case SEMICOLON:
					case LESS_THAN:
					case EQUALS:
					case GREATER_THAN:
					case QUESTION:
					case AT:
					case CAP_A:
					case CAP_B:
					case CAP_C:
					case CAP_D:
					case CAP_E:
					case CAP_F:
					case CAP_G:
					case CAP_H:
					case CAP_I:
					case CAP_J:
					case CAP_K:
					case CAP_L:
					case CAP_M:
					case CAP_N:
					case CAP_O:
					case CAP_P:
					case CAP_Q:
					case CAP_R:
					case CAP_S:
					case CAP_T:
					case CAP_U:
					case CAP_V:
					case CAP_W:
					case CAP_X:
					case CAP_Y:
					case CAP_Z:
					case LEFT_BRACE:
					case BACKSLASH:
					case RIGHT_BRACE:
					case CARAT:
					case UNDERSCORE:
					case ACCENT:
					case A:
					case B:
					case C:
					case D:
					case E:
					case F:
					case G:
					case H:
					case I:
					case J:
					case K:
					case L:
					case M:
					case N:
					case O:
					case P:
					case Q:
					case R:
					case S:
					case T:
					case U:
					case V:
					case W:
					case X:
					case Y:
					case Z:
					case LEFT_CURLY_BRACE:
					case PIPE:
					case RIGHT_CURLY_BRACE:
					case TILDE:
					case U_00C2:
					case U_00C3:
					case U_00C4:
					case U_00C5:
					case U_00C6:
					case U_00C7:
					case U_00C8:
					case U_00C9:
					case U_00CA:
					case U_00CB:
					case U_00CC:
					case U_00CD:
					case U_00CE:
					case U_00CF:
					case U_00D0:
					case U_00D1:
					case U_00D2:
					case U_00D3:
					case U_00D4:
					case U_00D5:
					case U_00D6:
					case U_00D7:
					case U_00D8:
					case U_00D9:
					case U_00DA:
					case U_00DB:
					case U_00DC:
					case U_00DD:
					case U_00DE:
					case U_00DF:
					case U_00E0:
					case U_00E1:
					case U_00E2:
					case U_00E3:
					case U_00E4:
					case U_00E5:
					case U_00E6:
					case U_00E7:
					case U_00E8:
					case U_00E9:
					case U_00EA:
					case U_00EB:
					case U_00EC:
					case U_00ED:
					case U_00EE:
					case U_00EF:
					case U_00F0:
					case U_00F1:
					case U_00F2:
					case U_00F3:
					case U_00F4:
						{
						setState(990);
						nonstarchar();
						}
						break;
					case ASTERISK:
						{
						setState(991);
						starwithnonfslash();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					} 
				}
				setState(996);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,83,_ctx);
			}
			{
			setState(997);
			match(ASTERISK);
			setState(998);
			match(SLASH);
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

	public static class NonstarcharContext extends ParserRuleContext {
		public SpContext sp() {
			return getRuleContext(SpContext.class,0);
		}
		public HtabContext htab() {
			return getRuleContext(HtabContext.class,0);
		}
		public CrContext cr() {
			return getRuleContext(CrContext.class,0);
		}
		public LfContext lf() {
			return getRuleContext(LfContext.class,0);
		}
		public TerminalNode EXCLAMATION() { return getToken(ExpressionTemplateParser.EXCLAMATION, 0); }
		public TerminalNode QUOTE() { return getToken(ExpressionTemplateParser.QUOTE, 0); }
		public TerminalNode POUND() { return getToken(ExpressionTemplateParser.POUND, 0); }
		public TerminalNode DOLLAR() { return getToken(ExpressionTemplateParser.DOLLAR, 0); }
		public TerminalNode PERCENT() { return getToken(ExpressionTemplateParser.PERCENT, 0); }
		public TerminalNode AMPERSAND() { return getToken(ExpressionTemplateParser.AMPERSAND, 0); }
		public TerminalNode APOSTROPHE() { return getToken(ExpressionTemplateParser.APOSTROPHE, 0); }
		public TerminalNode LEFT_PAREN() { return getToken(ExpressionTemplateParser.LEFT_PAREN, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(ExpressionTemplateParser.RIGHT_PAREN, 0); }
		public TerminalNode PLUS() { return getToken(ExpressionTemplateParser.PLUS, 0); }
		public TerminalNode COMMA() { return getToken(ExpressionTemplateParser.COMMA, 0); }
		public TerminalNode DASH() { return getToken(ExpressionTemplateParser.DASH, 0); }
		public TerminalNode PERIOD() { return getToken(ExpressionTemplateParser.PERIOD, 0); }
		public TerminalNode SLASH() { return getToken(ExpressionTemplateParser.SLASH, 0); }
		public TerminalNode ZERO() { return getToken(ExpressionTemplateParser.ZERO, 0); }
		public TerminalNode ONE() { return getToken(ExpressionTemplateParser.ONE, 0); }
		public TerminalNode TWO() { return getToken(ExpressionTemplateParser.TWO, 0); }
		public TerminalNode THREE() { return getToken(ExpressionTemplateParser.THREE, 0); }
		public TerminalNode FOUR() { return getToken(ExpressionTemplateParser.FOUR, 0); }
		public TerminalNode FIVE() { return getToken(ExpressionTemplateParser.FIVE, 0); }
		public TerminalNode SIX() { return getToken(ExpressionTemplateParser.SIX, 0); }
		public TerminalNode SEVEN() { return getToken(ExpressionTemplateParser.SEVEN, 0); }
		public TerminalNode EIGHT() { return getToken(ExpressionTemplateParser.EIGHT, 0); }
		public TerminalNode NINE() { return getToken(ExpressionTemplateParser.NINE, 0); }
		public TerminalNode COLON() { return getToken(ExpressionTemplateParser.COLON, 0); }
		public TerminalNode SEMICOLON() { return getToken(ExpressionTemplateParser.SEMICOLON, 0); }
		public TerminalNode LESS_THAN() { return getToken(ExpressionTemplateParser.LESS_THAN, 0); }
		public TerminalNode EQUALS() { return getToken(ExpressionTemplateParser.EQUALS, 0); }
		public TerminalNode GREATER_THAN() { return getToken(ExpressionTemplateParser.GREATER_THAN, 0); }
		public TerminalNode QUESTION() { return getToken(ExpressionTemplateParser.QUESTION, 0); }
		public TerminalNode AT() { return getToken(ExpressionTemplateParser.AT, 0); }
		public TerminalNode CAP_A() { return getToken(ExpressionTemplateParser.CAP_A, 0); }
		public TerminalNode CAP_B() { return getToken(ExpressionTemplateParser.CAP_B, 0); }
		public TerminalNode CAP_C() { return getToken(ExpressionTemplateParser.CAP_C, 0); }
		public TerminalNode CAP_D() { return getToken(ExpressionTemplateParser.CAP_D, 0); }
		public TerminalNode CAP_E() { return getToken(ExpressionTemplateParser.CAP_E, 0); }
		public TerminalNode CAP_F() { return getToken(ExpressionTemplateParser.CAP_F, 0); }
		public TerminalNode CAP_G() { return getToken(ExpressionTemplateParser.CAP_G, 0); }
		public TerminalNode CAP_H() { return getToken(ExpressionTemplateParser.CAP_H, 0); }
		public TerminalNode CAP_I() { return getToken(ExpressionTemplateParser.CAP_I, 0); }
		public TerminalNode CAP_J() { return getToken(ExpressionTemplateParser.CAP_J, 0); }
		public TerminalNode CAP_K() { return getToken(ExpressionTemplateParser.CAP_K, 0); }
		public TerminalNode CAP_L() { return getToken(ExpressionTemplateParser.CAP_L, 0); }
		public TerminalNode CAP_M() { return getToken(ExpressionTemplateParser.CAP_M, 0); }
		public TerminalNode CAP_N() { return getToken(ExpressionTemplateParser.CAP_N, 0); }
		public TerminalNode CAP_O() { return getToken(ExpressionTemplateParser.CAP_O, 0); }
		public TerminalNode CAP_P() { return getToken(ExpressionTemplateParser.CAP_P, 0); }
		public TerminalNode CAP_Q() { return getToken(ExpressionTemplateParser.CAP_Q, 0); }
		public TerminalNode CAP_R() { return getToken(ExpressionTemplateParser.CAP_R, 0); }
		public TerminalNode CAP_S() { return getToken(ExpressionTemplateParser.CAP_S, 0); }
		public TerminalNode CAP_T() { return getToken(ExpressionTemplateParser.CAP_T, 0); }
		public TerminalNode CAP_U() { return getToken(ExpressionTemplateParser.CAP_U, 0); }
		public TerminalNode CAP_V() { return getToken(ExpressionTemplateParser.CAP_V, 0); }
		public TerminalNode CAP_W() { return getToken(ExpressionTemplateParser.CAP_W, 0); }
		public TerminalNode CAP_X() { return getToken(ExpressionTemplateParser.CAP_X, 0); }
		public TerminalNode CAP_Y() { return getToken(ExpressionTemplateParser.CAP_Y, 0); }
		public TerminalNode CAP_Z() { return getToken(ExpressionTemplateParser.CAP_Z, 0); }
		public TerminalNode LEFT_BRACE() { return getToken(ExpressionTemplateParser.LEFT_BRACE, 0); }
		public TerminalNode BACKSLASH() { return getToken(ExpressionTemplateParser.BACKSLASH, 0); }
		public TerminalNode RIGHT_BRACE() { return getToken(ExpressionTemplateParser.RIGHT_BRACE, 0); }
		public TerminalNode CARAT() { return getToken(ExpressionTemplateParser.CARAT, 0); }
		public TerminalNode UNDERSCORE() { return getToken(ExpressionTemplateParser.UNDERSCORE, 0); }
		public TerminalNode ACCENT() { return getToken(ExpressionTemplateParser.ACCENT, 0); }
		public TerminalNode A() { return getToken(ExpressionTemplateParser.A, 0); }
		public TerminalNode B() { return getToken(ExpressionTemplateParser.B, 0); }
		public TerminalNode C() { return getToken(ExpressionTemplateParser.C, 0); }
		public TerminalNode D() { return getToken(ExpressionTemplateParser.D, 0); }
		public TerminalNode E() { return getToken(ExpressionTemplateParser.E, 0); }
		public TerminalNode F() { return getToken(ExpressionTemplateParser.F, 0); }
		public TerminalNode G() { return getToken(ExpressionTemplateParser.G, 0); }
		public TerminalNode H() { return getToken(ExpressionTemplateParser.H, 0); }
		public TerminalNode I() { return getToken(ExpressionTemplateParser.I, 0); }
		public TerminalNode J() { return getToken(ExpressionTemplateParser.J, 0); }
		public TerminalNode K() { return getToken(ExpressionTemplateParser.K, 0); }
		public TerminalNode L() { return getToken(ExpressionTemplateParser.L, 0); }
		public TerminalNode M() { return getToken(ExpressionTemplateParser.M, 0); }
		public TerminalNode N() { return getToken(ExpressionTemplateParser.N, 0); }
		public TerminalNode O() { return getToken(ExpressionTemplateParser.O, 0); }
		public TerminalNode P() { return getToken(ExpressionTemplateParser.P, 0); }
		public TerminalNode Q() { return getToken(ExpressionTemplateParser.Q, 0); }
		public TerminalNode R() { return getToken(ExpressionTemplateParser.R, 0); }
		public TerminalNode S() { return getToken(ExpressionTemplateParser.S, 0); }
		public TerminalNode T() { return getToken(ExpressionTemplateParser.T, 0); }
		public TerminalNode U() { return getToken(ExpressionTemplateParser.U, 0); }
		public TerminalNode V() { return getToken(ExpressionTemplateParser.V, 0); }
		public TerminalNode W() { return getToken(ExpressionTemplateParser.W, 0); }
		public TerminalNode X() { return getToken(ExpressionTemplateParser.X, 0); }
		public TerminalNode Y() { return getToken(ExpressionTemplateParser.Y, 0); }
		public TerminalNode Z() { return getToken(ExpressionTemplateParser.Z, 0); }
		public TerminalNode LEFT_CURLY_BRACE() { return getToken(ExpressionTemplateParser.LEFT_CURLY_BRACE, 0); }
		public TerminalNode PIPE() { return getToken(ExpressionTemplateParser.PIPE, 0); }
		public TerminalNode RIGHT_CURLY_BRACE() { return getToken(ExpressionTemplateParser.RIGHT_CURLY_BRACE, 0); }
		public TerminalNode TILDE() { return getToken(ExpressionTemplateParser.TILDE, 0); }
		public Utf8_2Context utf8_2() {
			return getRuleContext(Utf8_2Context.class,0);
		}
		public Utf8_3Context utf8_3() {
			return getRuleContext(Utf8_3Context.class,0);
		}
		public Utf8_4Context utf8_4() {
			return getRuleContext(Utf8_4Context.class,0);
		}
		public NonstarcharContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nonstarchar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterNonstarchar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitNonstarchar(this);
		}
	}

	public final NonstarcharContext nonstarchar() throws RecognitionException {
		NonstarcharContext _localctx = new NonstarcharContext(_ctx, getState());
		enterRule(_localctx, 192, RULE_nonstarchar);
		int _la;
		try {
			setState(1009);
			switch (_input.LA(1)) {
			case SPACE:
				enterOuterAlt(_localctx, 1);
				{
				setState(1000);
				sp();
				}
				break;
			case TAB:
				enterOuterAlt(_localctx, 2);
				{
				setState(1001);
				htab();
				}
				break;
			case CR:
				enterOuterAlt(_localctx, 3);
				{
				setState(1002);
				cr();
				}
				break;
			case LF:
				enterOuterAlt(_localctx, 4);
				{
				setState(1003);
				lf();
				}
				break;
			case EXCLAMATION:
			case QUOTE:
			case POUND:
			case DOLLAR:
			case PERCENT:
			case AMPERSAND:
			case APOSTROPHE:
			case LEFT_PAREN:
			case RIGHT_PAREN:
				enterOuterAlt(_localctx, 5);
				{
				setState(1004);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EXCLAMATION) | (1L << QUOTE) | (1L << POUND) | (1L << DOLLAR) | (1L << PERCENT) | (1L << AMPERSAND) | (1L << APOSTROPHE) | (1L << LEFT_PAREN) | (1L << RIGHT_PAREN))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			case PLUS:
			case COMMA:
			case DASH:
			case PERIOD:
			case SLASH:
			case ZERO:
			case ONE:
			case TWO:
			case THREE:
			case FOUR:
			case FIVE:
			case SIX:
			case SEVEN:
			case EIGHT:
			case NINE:
			case COLON:
			case SEMICOLON:
			case LESS_THAN:
			case EQUALS:
			case GREATER_THAN:
			case QUESTION:
			case AT:
			case CAP_A:
			case CAP_B:
			case CAP_C:
			case CAP_D:
			case CAP_E:
			case CAP_F:
			case CAP_G:
			case CAP_H:
			case CAP_I:
			case CAP_J:
			case CAP_K:
			case CAP_L:
			case CAP_M:
			case CAP_N:
			case CAP_O:
			case CAP_P:
			case CAP_Q:
			case CAP_R:
			case CAP_S:
			case CAP_T:
			case CAP_U:
			case CAP_V:
			case CAP_W:
			case CAP_X:
			case CAP_Y:
			case CAP_Z:
			case LEFT_BRACE:
			case BACKSLASH:
			case RIGHT_BRACE:
			case CARAT:
			case UNDERSCORE:
			case ACCENT:
			case A:
			case B:
			case C:
			case D:
			case E:
			case F:
			case G:
			case H:
			case I:
			case J:
			case K:
			case L:
			case M:
			case N:
			case O:
			case P:
			case Q:
			case R:
			case S:
			case T:
			case U:
			case V:
			case W:
			case X:
			case Y:
			case Z:
			case LEFT_CURLY_BRACE:
			case PIPE:
			case RIGHT_CURLY_BRACE:
			case TILDE:
				enterOuterAlt(_localctx, 6);
				{
				setState(1005);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << COMMA) | (1L << DASH) | (1L << PERIOD) | (1L << SLASH) | (1L << ZERO) | (1L << ONE) | (1L << TWO) | (1L << THREE) | (1L << FOUR) | (1L << FIVE) | (1L << SIX) | (1L << SEVEN) | (1L << EIGHT) | (1L << NINE) | (1L << COLON) | (1L << SEMICOLON) | (1L << LESS_THAN) | (1L << EQUALS) | (1L << GREATER_THAN) | (1L << QUESTION) | (1L << AT) | (1L << CAP_A) | (1L << CAP_B) | (1L << CAP_C) | (1L << CAP_D) | (1L << CAP_E) | (1L << CAP_F) | (1L << CAP_G) | (1L << CAP_H) | (1L << CAP_I) | (1L << CAP_J) | (1L << CAP_K) | (1L << CAP_L) | (1L << CAP_M) | (1L << CAP_N) | (1L << CAP_O) | (1L << CAP_P) | (1L << CAP_Q) | (1L << CAP_R) | (1L << CAP_S) | (1L << CAP_T) | (1L << CAP_U) | (1L << CAP_V) | (1L << CAP_W) | (1L << CAP_X) | (1L << CAP_Y) | (1L << CAP_Z) | (1L << LEFT_BRACE))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (BACKSLASH - 64)) | (1L << (RIGHT_BRACE - 64)) | (1L << (CARAT - 64)) | (1L << (UNDERSCORE - 64)) | (1L << (ACCENT - 64)) | (1L << (A - 64)) | (1L << (B - 64)) | (1L << (C - 64)) | (1L << (D - 64)) | (1L << (E - 64)) | (1L << (F - 64)) | (1L << (G - 64)) | (1L << (H - 64)) | (1L << (I - 64)) | (1L << (J - 64)) | (1L << (K - 64)) | (1L << (L - 64)) | (1L << (M - 64)) | (1L << (N - 64)) | (1L << (O - 64)) | (1L << (P - 64)) | (1L << (Q - 64)) | (1L << (R - 64)) | (1L << (S - 64)) | (1L << (T - 64)) | (1L << (U - 64)) | (1L << (V - 64)) | (1L << (W - 64)) | (1L << (X - 64)) | (1L << (Y - 64)) | (1L << (Z - 64)) | (1L << (LEFT_CURLY_BRACE - 64)) | (1L << (PIPE - 64)) | (1L << (RIGHT_CURLY_BRACE - 64)) | (1L << (TILDE - 64)))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			case U_00C2:
			case U_00C3:
			case U_00C4:
			case U_00C5:
			case U_00C6:
			case U_00C7:
			case U_00C8:
			case U_00C9:
			case U_00CA:
			case U_00CB:
			case U_00CC:
			case U_00CD:
			case U_00CE:
			case U_00CF:
			case U_00D0:
			case U_00D1:
			case U_00D2:
			case U_00D3:
			case U_00D4:
			case U_00D5:
			case U_00D6:
			case U_00D7:
			case U_00D8:
			case U_00D9:
			case U_00DA:
			case U_00DB:
			case U_00DC:
			case U_00DD:
			case U_00DE:
			case U_00DF:
				enterOuterAlt(_localctx, 7);
				{
				setState(1006);
				utf8_2();
				}
				break;
			case U_00E0:
			case U_00E1:
			case U_00E2:
			case U_00E3:
			case U_00E4:
			case U_00E5:
			case U_00E6:
			case U_00E7:
			case U_00E8:
			case U_00E9:
			case U_00EA:
			case U_00EB:
			case U_00EC:
			case U_00ED:
			case U_00EE:
			case U_00EF:
				enterOuterAlt(_localctx, 8);
				{
				setState(1007);
				utf8_3();
				}
				break;
			case U_00F0:
			case U_00F1:
			case U_00F2:
			case U_00F3:
			case U_00F4:
				enterOuterAlt(_localctx, 9);
				{
				setState(1008);
				utf8_4();
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

	public static class StarwithnonfslashContext extends ParserRuleContext {
		public TerminalNode ASTERISK() { return getToken(ExpressionTemplateParser.ASTERISK, 0); }
		public NonfslashContext nonfslash() {
			return getRuleContext(NonfslashContext.class,0);
		}
		public StarwithnonfslashContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_starwithnonfslash; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterStarwithnonfslash(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitStarwithnonfslash(this);
		}
	}

	public final StarwithnonfslashContext starwithnonfslash() throws RecognitionException {
		StarwithnonfslashContext _localctx = new StarwithnonfslashContext(_ctx, getState());
		enterRule(_localctx, 194, RULE_starwithnonfslash);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1011);
			match(ASTERISK);
			setState(1012);
			nonfslash();
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

	public static class NonfslashContext extends ParserRuleContext {
		public SpContext sp() {
			return getRuleContext(SpContext.class,0);
		}
		public HtabContext htab() {
			return getRuleContext(HtabContext.class,0);
		}
		public CrContext cr() {
			return getRuleContext(CrContext.class,0);
		}
		public LfContext lf() {
			return getRuleContext(LfContext.class,0);
		}
		public TerminalNode EXCLAMATION() { return getToken(ExpressionTemplateParser.EXCLAMATION, 0); }
		public TerminalNode QUOTE() { return getToken(ExpressionTemplateParser.QUOTE, 0); }
		public TerminalNode POUND() { return getToken(ExpressionTemplateParser.POUND, 0); }
		public TerminalNode DOLLAR() { return getToken(ExpressionTemplateParser.DOLLAR, 0); }
		public TerminalNode PERCENT() { return getToken(ExpressionTemplateParser.PERCENT, 0); }
		public TerminalNode AMPERSAND() { return getToken(ExpressionTemplateParser.AMPERSAND, 0); }
		public TerminalNode APOSTROPHE() { return getToken(ExpressionTemplateParser.APOSTROPHE, 0); }
		public TerminalNode LEFT_PAREN() { return getToken(ExpressionTemplateParser.LEFT_PAREN, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(ExpressionTemplateParser.RIGHT_PAREN, 0); }
		public TerminalNode ASTERISK() { return getToken(ExpressionTemplateParser.ASTERISK, 0); }
		public TerminalNode PLUS() { return getToken(ExpressionTemplateParser.PLUS, 0); }
		public TerminalNode COMMA() { return getToken(ExpressionTemplateParser.COMMA, 0); }
		public TerminalNode DASH() { return getToken(ExpressionTemplateParser.DASH, 0); }
		public TerminalNode PERIOD() { return getToken(ExpressionTemplateParser.PERIOD, 0); }
		public TerminalNode ZERO() { return getToken(ExpressionTemplateParser.ZERO, 0); }
		public TerminalNode ONE() { return getToken(ExpressionTemplateParser.ONE, 0); }
		public TerminalNode TWO() { return getToken(ExpressionTemplateParser.TWO, 0); }
		public TerminalNode THREE() { return getToken(ExpressionTemplateParser.THREE, 0); }
		public TerminalNode FOUR() { return getToken(ExpressionTemplateParser.FOUR, 0); }
		public TerminalNode FIVE() { return getToken(ExpressionTemplateParser.FIVE, 0); }
		public TerminalNode SIX() { return getToken(ExpressionTemplateParser.SIX, 0); }
		public TerminalNode SEVEN() { return getToken(ExpressionTemplateParser.SEVEN, 0); }
		public TerminalNode EIGHT() { return getToken(ExpressionTemplateParser.EIGHT, 0); }
		public TerminalNode NINE() { return getToken(ExpressionTemplateParser.NINE, 0); }
		public TerminalNode COLON() { return getToken(ExpressionTemplateParser.COLON, 0); }
		public TerminalNode SEMICOLON() { return getToken(ExpressionTemplateParser.SEMICOLON, 0); }
		public TerminalNode LESS_THAN() { return getToken(ExpressionTemplateParser.LESS_THAN, 0); }
		public TerminalNode EQUALS() { return getToken(ExpressionTemplateParser.EQUALS, 0); }
		public TerminalNode GREATER_THAN() { return getToken(ExpressionTemplateParser.GREATER_THAN, 0); }
		public TerminalNode QUESTION() { return getToken(ExpressionTemplateParser.QUESTION, 0); }
		public TerminalNode AT() { return getToken(ExpressionTemplateParser.AT, 0); }
		public TerminalNode CAP_A() { return getToken(ExpressionTemplateParser.CAP_A, 0); }
		public TerminalNode CAP_B() { return getToken(ExpressionTemplateParser.CAP_B, 0); }
		public TerminalNode CAP_C() { return getToken(ExpressionTemplateParser.CAP_C, 0); }
		public TerminalNode CAP_D() { return getToken(ExpressionTemplateParser.CAP_D, 0); }
		public TerminalNode CAP_E() { return getToken(ExpressionTemplateParser.CAP_E, 0); }
		public TerminalNode CAP_F() { return getToken(ExpressionTemplateParser.CAP_F, 0); }
		public TerminalNode CAP_G() { return getToken(ExpressionTemplateParser.CAP_G, 0); }
		public TerminalNode CAP_H() { return getToken(ExpressionTemplateParser.CAP_H, 0); }
		public TerminalNode CAP_I() { return getToken(ExpressionTemplateParser.CAP_I, 0); }
		public TerminalNode CAP_J() { return getToken(ExpressionTemplateParser.CAP_J, 0); }
		public TerminalNode CAP_K() { return getToken(ExpressionTemplateParser.CAP_K, 0); }
		public TerminalNode CAP_L() { return getToken(ExpressionTemplateParser.CAP_L, 0); }
		public TerminalNode CAP_M() { return getToken(ExpressionTemplateParser.CAP_M, 0); }
		public TerminalNode CAP_N() { return getToken(ExpressionTemplateParser.CAP_N, 0); }
		public TerminalNode CAP_O() { return getToken(ExpressionTemplateParser.CAP_O, 0); }
		public TerminalNode CAP_P() { return getToken(ExpressionTemplateParser.CAP_P, 0); }
		public TerminalNode CAP_Q() { return getToken(ExpressionTemplateParser.CAP_Q, 0); }
		public TerminalNode CAP_R() { return getToken(ExpressionTemplateParser.CAP_R, 0); }
		public TerminalNode CAP_S() { return getToken(ExpressionTemplateParser.CAP_S, 0); }
		public TerminalNode CAP_T() { return getToken(ExpressionTemplateParser.CAP_T, 0); }
		public TerminalNode CAP_U() { return getToken(ExpressionTemplateParser.CAP_U, 0); }
		public TerminalNode CAP_V() { return getToken(ExpressionTemplateParser.CAP_V, 0); }
		public TerminalNode CAP_W() { return getToken(ExpressionTemplateParser.CAP_W, 0); }
		public TerminalNode CAP_X() { return getToken(ExpressionTemplateParser.CAP_X, 0); }
		public TerminalNode CAP_Y() { return getToken(ExpressionTemplateParser.CAP_Y, 0); }
		public TerminalNode CAP_Z() { return getToken(ExpressionTemplateParser.CAP_Z, 0); }
		public TerminalNode LEFT_BRACE() { return getToken(ExpressionTemplateParser.LEFT_BRACE, 0); }
		public TerminalNode BACKSLASH() { return getToken(ExpressionTemplateParser.BACKSLASH, 0); }
		public TerminalNode RIGHT_BRACE() { return getToken(ExpressionTemplateParser.RIGHT_BRACE, 0); }
		public TerminalNode CARAT() { return getToken(ExpressionTemplateParser.CARAT, 0); }
		public TerminalNode UNDERSCORE() { return getToken(ExpressionTemplateParser.UNDERSCORE, 0); }
		public TerminalNode ACCENT() { return getToken(ExpressionTemplateParser.ACCENT, 0); }
		public TerminalNode A() { return getToken(ExpressionTemplateParser.A, 0); }
		public TerminalNode B() { return getToken(ExpressionTemplateParser.B, 0); }
		public TerminalNode C() { return getToken(ExpressionTemplateParser.C, 0); }
		public TerminalNode D() { return getToken(ExpressionTemplateParser.D, 0); }
		public TerminalNode E() { return getToken(ExpressionTemplateParser.E, 0); }
		public TerminalNode F() { return getToken(ExpressionTemplateParser.F, 0); }
		public TerminalNode G() { return getToken(ExpressionTemplateParser.G, 0); }
		public TerminalNode H() { return getToken(ExpressionTemplateParser.H, 0); }
		public TerminalNode I() { return getToken(ExpressionTemplateParser.I, 0); }
		public TerminalNode J() { return getToken(ExpressionTemplateParser.J, 0); }
		public TerminalNode K() { return getToken(ExpressionTemplateParser.K, 0); }
		public TerminalNode L() { return getToken(ExpressionTemplateParser.L, 0); }
		public TerminalNode M() { return getToken(ExpressionTemplateParser.M, 0); }
		public TerminalNode N() { return getToken(ExpressionTemplateParser.N, 0); }
		public TerminalNode O() { return getToken(ExpressionTemplateParser.O, 0); }
		public TerminalNode P() { return getToken(ExpressionTemplateParser.P, 0); }
		public TerminalNode Q() { return getToken(ExpressionTemplateParser.Q, 0); }
		public TerminalNode R() { return getToken(ExpressionTemplateParser.R, 0); }
		public TerminalNode S() { return getToken(ExpressionTemplateParser.S, 0); }
		public TerminalNode T() { return getToken(ExpressionTemplateParser.T, 0); }
		public TerminalNode U() { return getToken(ExpressionTemplateParser.U, 0); }
		public TerminalNode V() { return getToken(ExpressionTemplateParser.V, 0); }
		public TerminalNode W() { return getToken(ExpressionTemplateParser.W, 0); }
		public TerminalNode X() { return getToken(ExpressionTemplateParser.X, 0); }
		public TerminalNode Y() { return getToken(ExpressionTemplateParser.Y, 0); }
		public TerminalNode Z() { return getToken(ExpressionTemplateParser.Z, 0); }
		public TerminalNode LEFT_CURLY_BRACE() { return getToken(ExpressionTemplateParser.LEFT_CURLY_BRACE, 0); }
		public TerminalNode PIPE() { return getToken(ExpressionTemplateParser.PIPE, 0); }
		public TerminalNode RIGHT_CURLY_BRACE() { return getToken(ExpressionTemplateParser.RIGHT_CURLY_BRACE, 0); }
		public TerminalNode TILDE() { return getToken(ExpressionTemplateParser.TILDE, 0); }
		public Utf8_2Context utf8_2() {
			return getRuleContext(Utf8_2Context.class,0);
		}
		public Utf8_3Context utf8_3() {
			return getRuleContext(Utf8_3Context.class,0);
		}
		public Utf8_4Context utf8_4() {
			return getRuleContext(Utf8_4Context.class,0);
		}
		public NonfslashContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nonfslash; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).enterNonfslash(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionTemplateListener ) ((ExpressionTemplateListener)listener).exitNonfslash(this);
		}
	}

	public final NonfslashContext nonfslash() throws RecognitionException {
		NonfslashContext _localctx = new NonfslashContext(_ctx, getState());
		enterRule(_localctx, 196, RULE_nonfslash);
		int _la;
		try {
			setState(1023);
			switch (_input.LA(1)) {
			case SPACE:
				enterOuterAlt(_localctx, 1);
				{
				setState(1014);
				sp();
				}
				break;
			case TAB:
				enterOuterAlt(_localctx, 2);
				{
				setState(1015);
				htab();
				}
				break;
			case CR:
				enterOuterAlt(_localctx, 3);
				{
				setState(1016);
				cr();
				}
				break;
			case LF:
				enterOuterAlt(_localctx, 4);
				{
				setState(1017);
				lf();
				}
				break;
			case EXCLAMATION:
			case QUOTE:
			case POUND:
			case DOLLAR:
			case PERCENT:
			case AMPERSAND:
			case APOSTROPHE:
			case LEFT_PAREN:
			case RIGHT_PAREN:
			case ASTERISK:
			case PLUS:
			case COMMA:
			case DASH:
			case PERIOD:
				enterOuterAlt(_localctx, 5);
				{
				setState(1018);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EXCLAMATION) | (1L << QUOTE) | (1L << POUND) | (1L << DOLLAR) | (1L << PERCENT) | (1L << AMPERSAND) | (1L << APOSTROPHE) | (1L << LEFT_PAREN) | (1L << RIGHT_PAREN) | (1L << ASTERISK) | (1L << PLUS) | (1L << COMMA) | (1L << DASH) | (1L << PERIOD))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			case ZERO:
			case ONE:
			case TWO:
			case THREE:
			case FOUR:
			case FIVE:
			case SIX:
			case SEVEN:
			case EIGHT:
			case NINE:
			case COLON:
			case SEMICOLON:
			case LESS_THAN:
			case EQUALS:
			case GREATER_THAN:
			case QUESTION:
			case AT:
			case CAP_A:
			case CAP_B:
			case CAP_C:
			case CAP_D:
			case CAP_E:
			case CAP_F:
			case CAP_G:
			case CAP_H:
			case CAP_I:
			case CAP_J:
			case CAP_K:
			case CAP_L:
			case CAP_M:
			case CAP_N:
			case CAP_O:
			case CAP_P:
			case CAP_Q:
			case CAP_R:
			case CAP_S:
			case CAP_T:
			case CAP_U:
			case CAP_V:
			case CAP_W:
			case CAP_X:
			case CAP_Y:
			case CAP_Z:
			case LEFT_BRACE:
			case BACKSLASH:
			case RIGHT_BRACE:
			case CARAT:
			case UNDERSCORE:
			case ACCENT:
			case A:
			case B:
			case C:
			case D:
			case E:
			case F:
			case G:
			case H:
			case I:
			case J:
			case K:
			case L:
			case M:
			case N:
			case O:
			case P:
			case Q:
			case R:
			case S:
			case T:
			case U:
			case V:
			case W:
			case X:
			case Y:
			case Z:
			case LEFT_CURLY_BRACE:
			case PIPE:
			case RIGHT_CURLY_BRACE:
			case TILDE:
				enterOuterAlt(_localctx, 6);
				{
				setState(1019);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ZERO) | (1L << ONE) | (1L << TWO) | (1L << THREE) | (1L << FOUR) | (1L << FIVE) | (1L << SIX) | (1L << SEVEN) | (1L << EIGHT) | (1L << NINE) | (1L << COLON) | (1L << SEMICOLON) | (1L << LESS_THAN) | (1L << EQUALS) | (1L << GREATER_THAN) | (1L << QUESTION) | (1L << AT) | (1L << CAP_A) | (1L << CAP_B) | (1L << CAP_C) | (1L << CAP_D) | (1L << CAP_E) | (1L << CAP_F) | (1L << CAP_G) | (1L << CAP_H) | (1L << CAP_I) | (1L << CAP_J) | (1L << CAP_K) | (1L << CAP_L) | (1L << CAP_M) | (1L << CAP_N) | (1L << CAP_O) | (1L << CAP_P) | (1L << CAP_Q) | (1L << CAP_R) | (1L << CAP_S) | (1L << CAP_T) | (1L << CAP_U) | (1L << CAP_V) | (1L << CAP_W) | (1L << CAP_X) | (1L << CAP_Y) | (1L << CAP_Z) | (1L << LEFT_BRACE))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (BACKSLASH - 64)) | (1L << (RIGHT_BRACE - 64)) | (1L << (CARAT - 64)) | (1L << (UNDERSCORE - 64)) | (1L << (ACCENT - 64)) | (1L << (A - 64)) | (1L << (B - 64)) | (1L << (C - 64)) | (1L << (D - 64)) | (1L << (E - 64)) | (1L << (F - 64)) | (1L << (G - 64)) | (1L << (H - 64)) | (1L << (I - 64)) | (1L << (J - 64)) | (1L << (K - 64)) | (1L << (L - 64)) | (1L << (M - 64)) | (1L << (N - 64)) | (1L << (O - 64)) | (1L << (P - 64)) | (1L << (Q - 64)) | (1L << (R - 64)) | (1L << (S - 64)) | (1L << (T - 64)) | (1L << (U - 64)) | (1L << (V - 64)) | (1L << (W - 64)) | (1L << (X - 64)) | (1L << (Y - 64)) | (1L << (Z - 64)) | (1L << (LEFT_CURLY_BRACE - 64)) | (1L << (PIPE - 64)) | (1L << (RIGHT_CURLY_BRACE - 64)) | (1L << (TILDE - 64)))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			case U_00C2:
			case U_00C3:
			case U_00C4:
			case U_00C5:
			case U_00C6:
			case U_00C7:
			case U_00C8:
			case U_00C9:
			case U_00CA:
			case U_00CB:
			case U_00CC:
			case U_00CD:
			case U_00CE:
			case U_00CF:
			case U_00D0:
			case U_00D1:
			case U_00D2:
			case U_00D3:
			case U_00D4:
			case U_00D5:
			case U_00D6:
			case U_00D7:
			case U_00D8:
			case U_00D9:
			case U_00DA:
			case U_00DB:
			case U_00DC:
			case U_00DD:
			case U_00DE:
			case U_00DF:
				enterOuterAlt(_localctx, 7);
				{
				setState(1020);
				utf8_2();
				}
				break;
			case U_00E0:
			case U_00E1:
			case U_00E2:
			case U_00E3:
			case U_00E4:
			case U_00E5:
			case U_00E6:
			case U_00E7:
			case U_00E8:
			case U_00E9:
			case U_00EA:
			case U_00EB:
			case U_00EC:
			case U_00ED:
			case U_00EE:
			case U_00EF:
				enterOuterAlt(_localctx, 8);
				{
				setState(1021);
				utf8_3();
				}
				break;
			case U_00F0:
			case U_00F1:
			case U_00F2:
			case U_00F3:
			case U_00F4:
				enterOuterAlt(_localctx, 9);
				{
				setState(1022);
				utf8_4();
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\u00d7\u0404\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\4W\tW\4X\tX\4Y\tY\4Z\tZ\4[\t[\4\\\t\\\4]\t]\4^\t^\4_\t_\4"+
		"`\t`\4a\ta\4b\tb\4c\tc\4d\td\3\2\3\2\3\2\3\2\5\2\u00cd\n\2\3\2\3\2\3\2"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\5\3\u00d8\n\3\3\4\3\4\5\4\u00dc\n\4\3\5\3\5\3"+
		"\5\3\5\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\7\7\u00ec\n\7\f\7\16\7"+
		"\u00ef\13\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b\u00fa\n\b\5\b\u00fc"+
		"\n\b\3\t\3\t\3\n\3\n\7\n\u0102\n\n\f\n\16\n\u0105\13\n\3\n\7\n\u0108\n"+
		"\n\f\n\16\n\u010b\13\n\3\13\3\13\5\13\u010f\n\13\3\13\3\13\3\13\5\13\u0114"+
		"\n\13\3\13\3\13\7\13\u0118\n\13\f\13\16\13\u011b\13\13\3\f\3\f\3\f\5\f"+
		"\u0120\n\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\7\r\u012e\n"+
		"\r\f\r\16\r\u0131\13\r\3\16\3\16\3\16\5\16\u0136\n\16\3\16\3\16\3\16\3"+
		"\16\3\16\3\16\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\5\20\u0147"+
		"\n\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\5\21\u0150\n\21\3\22\3\22\6\22"+
		"\u0154\n\22\r\22\16\22\u0155\3\23\3\23\5\23\u015a\n\23\3\24\5\24\u015d"+
		"\n\24\3\24\3\24\7\24\u0161\n\24\f\24\16\24\u0164\13\24\3\24\5\24\u0167"+
		"\n\24\3\25\3\25\3\25\6\25\u016c\n\25\r\25\16\25\u016d\3\26\3\26\3\26\3"+
		"\26\3\26\3\26\3\26\5\26\u0177\n\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26"+
		"\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26"+
		"\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26"+
		"\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26"+
		"\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26"+
		"\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26"+
		"\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\5\26\u01d1\n\26"+
		"\3\27\3\27\3\27\3\27\7\27\u01d7\n\27\f\27\16\27\u01da\13\27\3\30\3\30"+
		"\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37"+
		"\3 \3 \3!\3!\3!\3!\3!\5!\u01f3\n!\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\""+
		"\5\"\u01fe\n\"\3#\3#\3#\3#\3#\3#\5#\u0206\n#\3$\3$\3$\3%\3%\3%\3%\3%\3"+
		"%\3%\3%\3%\3%\3%\3%\3%\3%\5%\u0219\n%\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3"+
		"&\3&\3&\3&\3&\5&\u022a\n&\3\'\3\'\3(\3(\3(\3(\3(\3(\3(\3(\3(\5(\u0237"+
		"\n(\3(\5(\u023a\n(\3(\3(\3(\3)\3)\3)\3)\3)\3)\3)\5)\u0246\n)\3)\3)\3)"+
		"\3*\3*\3*\3*\3*\3*\5*\u0251\n*\3+\3+\3+\3+\3+\3+\3+\3+\5+\u025b\n+\3,"+
		"\3,\3,\5,\u0260\n,\3,\3,\3,\5,\u0265\n,\3,\3,\3,\5,\u026a\n,\3-\3-\3-"+
		"\3.\3.\3.\3/\7/\u0273\n/\f/\16/\u0276\13/\3/\3/\7/\u027a\n/\f/\16/\u027d"+
		"\13/\3/\3/\3/\3/\7/\u0283\n/\f/\16/\u0286\13/\3/\3/\5/\u028a\n/\3\60\3"+
		"\60\3\60\3\60\3\60\3\60\3\60\5\60\u0293\n\60\3\61\3\61\3\61\3\61\5\61"+
		"\u0299\n\61\3\62\3\62\3\62\3\62\5\62\u029f\n\62\3\63\3\63\3\64\3\64\3"+
		"\64\3\64\3\64\3\64\3\64\3\64\3\64\3\64\5\64\u02ad\n\64\3\64\3\64\3\65"+
		"\3\65\3\65\5\65\u02b4\n\65\3\65\3\65\3\66\3\66\3\66\3\66\3\66\3\66\3\67"+
		"\3\67\3\67\5\67\u02c1\n\67\38\38\38\38\38\38\68\u02c9\n8\r8\168\u02ca"+
		"\39\39\39\39\39\39\69\u02d3\n9\r9\169\u02d4\3:\3:\3:\3:\3:\3:\3;\3;\3"+
		";\3;\3;\3;\3;\5;\u02e4\n;\3<\3<\3<\5<\u02e9\n<\3<\3<\5<\u02ed\n<\3=\3"+
		"=\3>\3>\3?\3?\3?\3?\3?\3?\5?\u02f9\n?\3@\3@\3A\3A\3A\3B\3B\3B\3C\3C\3"+
		"D\3D\3D\3E\3E\3E\3F\3F\3F\3F\3F\5F\u0310\nF\3G\3G\3G\3G\3H\3H\3H\3H\3"+
		"H\3H\3H\3I\3I\3I\3I\5I\u0321\nI\3J\3J\3J\3J\3J\6J\u0328\nJ\rJ\16J\u0329"+
		"\3K\3K\3K\3K\3K\6K\u0331\nK\rK\16K\u0332\3L\3L\3L\3L\3L\3L\3L\3L\5L\u033d"+
		"\nL\3M\3M\3M\3M\5M\u0343\nM\3N\3N\3N\3N\3N\6N\u034a\nN\rN\16N\u034b\3"+
		"O\3O\3O\3O\3O\6O\u0353\nO\rO\16O\u0354\3P\3P\3P\3P\3P\3P\3P\5P\u035e\n"+
		"P\3Q\3Q\3Q\3Q\3Q\5Q\u0365\nQ\3Q\3Q\3Q\3Q\3Q\3Q\3R\3R\3R\3R\3R\5R\u0372"+
		"\nR\3R\3R\3R\5R\u0377\nR\3R\3R\3R\5R\u037c\nR\3R\3R\3R\3R\3R\3R\3R\3R"+
		"\3R\3R\3R\3R\3R\3R\3R\3R\3R\5R\u038f\nR\3S\3S\3S\3S\3T\3T\3U\3U\3U\3V"+
		"\3V\5V\u039c\nV\3W\3W\3X\3X\3Y\3Y\5Y\u03a4\nY\3Z\3Z\5Z\u03a8\nZ\3[\3["+
		"\3[\3[\3[\5[\u03af\n[\3[\3[\3[\5[\u03b4\n[\3\\\3\\\3\\\5\\\u03b9\n\\\3"+
		"]\3]\3]\3]\3]\3]\3]\3]\3]\5]\u03c4\n]\3^\3^\3^\5^\u03c9\n^\3_\3_\7_\u03cd"+
		"\n_\f_\16_\u03d0\13_\3_\5_\u03d3\n_\3`\3`\3`\3`\3`\6`\u03da\n`\r`\16`"+
		"\u03db\3a\3a\3a\3a\3a\7a\u03e3\na\fa\16a\u03e6\13a\3a\3a\3a\3b\3b\3b\3"+
		"b\3b\3b\3b\3b\3b\5b\u03f4\nb\3c\3c\3c\3d\3d\3d\3d\3d\3d\3d\3d\3d\5d\u0402"+
		"\nd\3d\2\2e\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\66"+
		"8:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088\u008a"+
		"\u008c\u008e\u0090\u0092\u0094\u0096\u0098\u009a\u009c\u009e\u00a0\u00a2"+
		"\u00a4\u00a6\u00a8\u00aa\u00ac\u00ae\u00b0\u00b2\u00b4\u00b6\u00b8\u00ba"+
		"\u00bc\u00be\u00c0\u00c2\u00c4\u00c6\2\'\4\2\21\21\23\23\3\2\26\37\3\2"+
		"\27\37\3\2\7a\3\2cd\3\2\6\7\3\2\tA\3\2Cd\3\2\u00a5\u00c2\3\2\u0085\u00a4"+
		"\3\2\u00c4\u00cf\3\2e\u0084\3\2\u00d1\u00d2\3\2u\u00a4\3\2\u00d4\u00d6"+
		"\3\2et\3\2e\u00a4\3\2\13\f\3\2\16%\3\2\'@\3\2Dd\3\2\13%\3\2\'d\3\2\7\t"+
		"\4\2\'\'GG\4\2\64\64TT\4\2**JJ\4\2\65\65UU\4\288XX\4\2\63\63SS\4\2//O"+
		"O\4\2;;[[\4\299YY\3\2\7\17\3\2\21d\3\2\7\24\3\2\26d\u043b\2\u00c8\3\2"+
		"\2\2\4\u00d1\3\2\2\2\6\u00db\3\2\2\2\b\u00dd\3\2\2\2\n\u00e1\3\2\2\2\f"+
		"\u00e5\3\2\2\2\16\u00fb\3\2\2\2\20\u00fd\3\2\2\2\22\u00ff\3\2\2\2\24\u010e"+
		"\3\2\2\2\26\u011f\3\2\2\2\30\u0127\3\2\2\2\32\u0135\3\2\2\2\34\u013d\3"+
		"\2\2\2\36\u0146\3\2\2\2 \u014f\3\2\2\2\"\u0153\3\2\2\2$\u0159\3\2\2\2"+
		"&\u0166\3\2\2\2(\u0168\3\2\2\2*\u016f\3\2\2\2,\u01d8\3\2\2\2.\u01db\3"+
		"\2\2\2\60\u01dd\3\2\2\2\62\u01df\3\2\2\2\64\u01e1\3\2\2\2\66\u01e3\3\2"+
		"\2\28\u01e5\3\2\2\2:\u01e7\3\2\2\2<\u01e9\3\2\2\2>\u01eb\3\2\2\2@\u01f2"+
		"\3\2\2\2B\u01fd\3\2\2\2D\u0205\3\2\2\2F\u0207\3\2\2\2H\u0218\3\2\2\2J"+
		"\u0229\3\2\2\2L\u022b\3\2\2\2N\u022d\3\2\2\2P\u023e\3\2\2\2R\u024a\3\2"+
		"\2\2T\u025a\3\2\2\2V\u025f\3\2\2\2X\u026b\3\2\2\2Z\u026e\3\2\2\2\\\u0289"+
		"\3\2\2\2^\u0292\3\2\2\2`\u0298\3\2\2\2b\u029e\3\2\2\2d\u02a0\3\2\2\2f"+
		"\u02a2\3\2\2\2h\u02b3\3\2\2\2j\u02b7\3\2\2\2l\u02c0\3\2\2\2n\u02c2\3\2"+
		"\2\2p\u02cc\3\2\2\2r\u02d6\3\2\2\2t\u02e3\3\2\2\2v\u02e8\3\2\2\2x\u02ee"+
		"\3\2\2\2z\u02f0\3\2\2\2|\u02f8\3\2\2\2~\u02fa\3\2\2\2\u0080\u02fc\3\2"+
		"\2\2\u0082\u02ff\3\2\2\2\u0084\u0302\3\2\2\2\u0086\u0304\3\2\2\2\u0088"+
		"\u0307\3\2\2\2\u008a\u030f\3\2\2\2\u008c\u0311\3\2\2\2\u008e\u0315\3\2"+
		"\2\2\u0090\u031c\3\2\2\2\u0092\u0327\3\2\2\2\u0094\u0330\3\2\2\2\u0096"+
		"\u033c\3\2\2\2\u0098\u033e\3\2\2\2\u009a\u0349\3\2\2\2\u009c\u0352\3\2"+
		"\2\2\u009e\u035d\3\2\2\2\u00a0\u0364\3\2\2\2\u00a2\u0371\3\2\2\2\u00a4"+
		"\u0390\3\2\2\2\u00a6\u0394\3\2\2\2\u00a8\u0396\3\2\2\2\u00aa\u039b\3\2"+
		"\2\2\u00ac\u039d\3\2\2\2\u00ae\u039f\3\2\2\2\u00b0\u03a3\3\2\2\2\u00b2"+
		"\u03a7\3\2\2\2\u00b4\u03b3\3\2\2\2\u00b6\u03b8\3\2\2\2\u00b8\u03c3\3\2"+
		"\2\2\u00ba\u03c8\3\2\2\2\u00bc\u03d2\3\2\2\2\u00be\u03d9\3\2\2\2\u00c0"+
		"\u03dd\3\2\2\2\u00c2\u03f3\3\2\2\2\u00c4\u03f5\3\2\2\2\u00c6\u0401\3\2"+
		"\2\2\u00c8\u00cc\5,\27\2\u00c9\u00ca\5\6\4\2\u00ca\u00cb\5,\27\2\u00cb"+
		"\u00cd\3\2\2\2\u00cc\u00c9\3\2\2\2\u00cc\u00cd\3\2\2\2\u00cd\u00ce\3\2"+
		"\2\2\u00ce\u00cf\5\4\3\2\u00cf\u00d0\5,\27\2\u00d0\3\3\2\2\2\u00d1\u00d7"+
		"\5\f\7\2\u00d2\u00d3\5,\27\2\u00d3\u00d4\7 \2\2\u00d4\u00d5\5,\27\2\u00d5"+
		"\u00d6\5\24\13\2\u00d6\u00d8\3\2\2\2\u00d7\u00d2\3\2\2\2\u00d7\u00d8\3"+
		"\2\2\2\u00d8\5\3\2\2\2\u00d9\u00dc\5\b\5\2\u00da\u00dc\5\n\6\2\u00db\u00d9"+
		"\3\2\2\2\u00db\u00da\3\2\2\2\u00dc\7\3\2\2\2\u00dd\u00de\7#\2\2\u00de"+
		"\u00df\7#\2\2\u00df\u00e0\7#\2\2\u00e0\t\3\2\2\2\u00e1\u00e2\7\"\2\2\u00e2"+
		"\u00e3\7\"\2\2\u00e3\u00e4\7\"\2\2\u00e4\13\3\2\2\2\u00e5\u00ed\5\16\b"+
		"\2\u00e6\u00e7\5,\27\2\u00e7\u00e8\7\21\2\2\u00e8\u00e9\5,\27\2\u00e9"+
		"\u00ea\5\16\b\2\u00ea\u00ec\3\2\2\2\u00eb\u00e6\3\2\2\2\u00ec\u00ef\3"+
		"\2\2\2\u00ed\u00eb\3\2\2\2\u00ed\u00ee\3\2\2\2\u00ee\r\3\2\2\2\u00ef\u00ed"+
		"\3\2\2\2\u00f0\u00fc\5N(\2\u00f1\u00f9\5\20\t\2\u00f2\u00f3\5,\27\2\u00f3"+
		"\u00f4\7b\2\2\u00f4\u00f5\5,\27\2\u00f5\u00f6\5\22\n\2\u00f6\u00f7\5,"+
		"\27\2\u00f7\u00f8\7b\2\2\u00f8\u00fa\3\2\2\2\u00f9\u00f2\3\2\2\2\u00f9"+
		"\u00fa\3\2\2\2\u00fa\u00fc\3\2\2\2\u00fb\u00f0\3\2\2\2\u00fb\u00f1\3\2"+
		"\2\2\u00fc\17\3\2\2\2\u00fd\u00fe\5*\26\2\u00fe\21\3\2\2\2\u00ff\u0109"+
		"\5@!\2\u0100\u0102\5.\30\2\u0101\u0100\3\2\2\2\u0102\u0105\3\2\2\2\u0103"+
		"\u0101\3\2\2\2\u0103\u0104\3\2\2\2\u0104\u0106\3\2\2\2\u0105\u0103\3\2"+
		"\2\2\u0106\u0108\5@!\2\u0107\u0103\3\2\2\2\u0108\u010b\3\2\2\2\u0109\u0107"+
		"\3\2\2\2\u0109\u010a\3\2\2\2\u010a\23\3\2\2\2\u010b\u0109\3\2\2\2\u010c"+
		"\u010f\5\30\r\2\u010d\u010f\5\26\f\2\u010e\u010c\3\2\2\2\u010e\u010d\3"+
		"\2\2\2\u010f\u0119\3\2\2\2\u0110\u0113\5,\27\2\u0111\u0112\7\22\2\2\u0112"+
		"\u0114\5,\27\2\u0113\u0111\3\2\2\2\u0113\u0114\3\2\2\2\u0114\u0115\3\2"+
		"\2\2\u0115\u0116\5\26\f\2\u0116\u0118\3\2\2\2\u0117\u0110\3\2\2\2\u0118"+
		"\u011b\3\2\2\2\u0119\u0117\3\2\2\2\u0119\u011a\3\2\2\2\u011a\25\3\2\2"+
		"\2\u011b\u0119\3\2\2\2\u011c\u011d\5P)\2\u011d\u011e\5,\27\2\u011e\u0120"+
		"\3\2\2\2\u011f\u011c\3\2\2\2\u011f\u0120\3\2\2\2\u0120\u0121\3\2\2\2\u0121"+
		"\u0122\7a\2\2\u0122\u0123\5,\27\2\u0123\u0124\5\30\r\2\u0124\u0125\5,"+
		"\27\2\u0125\u0126\7c\2\2\u0126\27\3\2\2\2\u0127\u012f\5\32\16\2\u0128"+
		"\u0129\5,\27\2\u0129\u012a\7\22\2\2\u012a\u012b\5,\27\2\u012b\u012c\5"+
		"\32\16\2\u012c\u012e\3\2\2\2\u012d\u0128\3\2\2\2\u012e\u0131\3\2\2\2\u012f"+
		"\u012d\3\2\2\2\u012f\u0130\3\2\2\2\u0130\31\3\2\2\2\u0131\u012f\3\2\2"+
		"\2\u0132\u0133\5P)\2\u0133\u0134\5,\27\2\u0134\u0136\3\2\2\2\u0135\u0132"+
		"\3\2\2\2\u0135\u0136\3\2\2\2\u0136\u0137\3\2\2\2\u0137\u0138\5\34\17\2"+
		"\u0138\u0139\5,\27\2\u0139\u013a\7#\2\2\u013a\u013b\5,\27\2\u013b\u013c"+
		"\5\36\20\2\u013c\33\3\2\2\2\u013d\u013e\5\16\b\2\u013e\35\3\2\2\2\u013f"+
		"\u0147\5 \21\2\u0140\u0141\5\66\34\2\u0141\u0142\5\"\22\2\u0142\u0143"+
		"\5\66\34\2\u0143\u0147\3\2\2\2\u0144\u0145\7\t\2\2\u0145\u0147\5$\23\2"+
		"\u0146\u013f\3\2\2\2\u0146\u0140\3\2\2\2\u0146\u0144\3\2\2\2\u0147\37"+
		"\3\2\2\2\u0148\u0150\5\16\b\2\u0149\u014a\7\16\2\2\u014a\u014b\5,\27\2"+
		"\u014b\u014c\5\4\3\2\u014c\u014d\5,\27\2\u014d\u014e\7\17\2\2\u014e\u0150"+
		"\3\2\2\2\u014f\u0148\3\2\2\2\u014f\u0149\3\2\2\2\u0150!\3\2\2\2\u0151"+
		"\u0154\5B\"\2\u0152\u0154\5D#\2\u0153\u0151\3\2\2\2\u0153\u0152\3\2\2"+
		"\2\u0154\u0155\3\2\2\2\u0155\u0153\3\2\2\2\u0155\u0156\3\2\2\2\u0156#"+
		"\3\2\2\2\u0157\u015a\5(\25\2\u0158\u015a\5&\24\2\u0159\u0157\3\2\2\2\u0159"+
		"\u0158\3\2\2\2\u015a%\3\2\2\2\u015b\u015d\t\2\2\2\u015c\u015b\3\2\2\2"+
		"\u015c\u015d\3\2\2\2\u015d\u015e\3\2\2\2\u015e\u0162\5> \2\u015f\u0161"+
		"\5:\36\2\u0160\u015f\3\2\2\2\u0161\u0164\3\2\2\2\u0162\u0160\3\2\2\2\u0162"+
		"\u0163\3\2\2\2\u0163\u0167\3\2\2\2\u0164\u0162\3\2\2\2\u0165\u0167\5<"+
		"\37\2\u0166\u015c\3\2\2\2\u0166\u0165\3\2\2\2\u0167\'\3\2\2\2\u0168\u0169"+
		"\5&\24\2\u0169\u016b\7\24\2\2\u016a\u016c\5:\36\2\u016b\u016a\3\2\2\2"+
		"\u016c\u016d\3\2\2\2\u016d\u016b\3\2\2\2\u016d\u016e\3\2\2\2\u016e)\3"+
		"\2\2\2\u016f\u0170\5> \2\u0170\u0171\5:\36\2\u0171\u0172\5:\36\2\u0172"+
		"\u0173\5:\36\2\u0173\u0174\5:\36\2\u0174\u01d0\5:\36\2\u0175\u0177\5:"+
		"\36\2\u0176\u0175\3\2\2\2\u0176\u0177\3\2\2\2\u0177\u01d1\3\2\2\2\u0178"+
		"\u0179\5:\36\2\u0179\u017a\5:\36\2\u017a\u01d1\3\2\2\2\u017b\u017c\5:"+
		"\36\2\u017c\u017d\5:\36\2\u017d\u017e\5:\36\2\u017e\u01d1\3\2\2\2\u017f"+
		"\u0180\5:\36\2\u0180\u0181\5:\36\2\u0181\u0182\5:\36\2\u0182\u0183\5:"+
		"\36\2\u0183\u01d1\3\2\2\2\u0184\u0185\5:\36\2\u0185\u0186\5:\36\2\u0186"+
		"\u0187\5:\36\2\u0187\u0188\5:\36\2\u0188\u0189\5:\36\2\u0189\u01d1\3\2"+
		"\2\2\u018a\u018b\5:\36\2\u018b\u018c\5:\36\2\u018c\u018d\5:\36\2\u018d"+
		"\u018e\5:\36\2\u018e\u018f\5:\36\2\u018f\u0190\5:\36\2\u0190\u01d1\3\2"+
		"\2\2\u0191\u0192\5:\36\2\u0192\u0193\5:\36\2\u0193\u0194\5:\36\2\u0194"+
		"\u0195\5:\36\2\u0195\u0196\5:\36\2\u0196\u0197\5:\36\2\u0197\u0198\5:"+
		"\36\2\u0198\u01d1\3\2\2\2\u0199\u019a\5:\36\2\u019a\u019b\5:\36\2\u019b"+
		"\u019c\5:\36\2\u019c\u019d\5:\36\2\u019d\u019e\5:\36\2\u019e\u019f\5:"+
		"\36\2\u019f\u01a0\5:\36\2\u01a0\u01a1\5:\36\2\u01a1\u01d1\3\2\2\2\u01a2"+
		"\u01a3\5:\36\2\u01a3\u01a4\5:\36\2\u01a4\u01a5\5:\36\2\u01a5\u01a6\5:"+
		"\36\2\u01a6\u01a7\5:\36\2\u01a7\u01a8\5:\36\2\u01a8\u01a9\5:\36\2\u01a9"+
		"\u01aa\5:\36\2\u01aa\u01ab\5:\36\2\u01ab\u01d1\3\2\2\2\u01ac\u01ad\5:"+
		"\36\2\u01ad\u01ae\5:\36\2\u01ae\u01af\5:\36\2\u01af\u01b0\5:\36\2\u01b0"+
		"\u01b1\5:\36\2\u01b1\u01b2\5:\36\2\u01b2\u01b3\5:\36\2\u01b3\u01b4\5:"+
		"\36\2\u01b4\u01b5\5:\36\2\u01b5\u01b6\5:\36\2\u01b6\u01d1\3\2\2\2\u01b7"+
		"\u01b8\5:\36\2\u01b8\u01b9\5:\36\2\u01b9\u01ba\5:\36\2\u01ba\u01bb\5:"+
		"\36\2\u01bb\u01bc\5:\36\2\u01bc\u01bd\5:\36\2\u01bd\u01be\5:\36\2\u01be"+
		"\u01bf\5:\36\2\u01bf\u01c0\5:\36\2\u01c0\u01c1\5:\36\2\u01c1\u01c2\5:"+
		"\36\2\u01c2\u01d1\3\2\2\2\u01c3\u01c4\5:\36\2\u01c4\u01c5\5:\36\2\u01c5"+
		"\u01c6\5:\36\2\u01c6\u01c7\5:\36\2\u01c7\u01c8\5:\36\2\u01c8\u01c9\5:"+
		"\36\2\u01c9\u01ca\5:\36\2\u01ca\u01cb\5:\36\2\u01cb\u01cc\5:\36\2\u01cc"+
		"\u01cd\5:\36\2\u01cd\u01ce\5:\36\2\u01ce\u01cf\5:\36\2\u01cf\u01d1\3\2"+
		"\2\2\u01d0\u0176\3\2\2\2\u01d0\u0178\3\2\2\2\u01d0\u017b\3\2\2\2\u01d0"+
		"\u017f\3\2\2\2\u01d0\u0184\3\2\2\2\u01d0\u018a\3\2\2\2\u01d0\u0191\3\2"+
		"\2\2\u01d0\u0199\3\2\2\2\u01d0\u01a2\3\2\2\2\u01d0\u01ac\3\2\2\2\u01d0"+
		"\u01b7\3\2\2\2\u01d0\u01c3\3\2\2\2\u01d1+\3\2\2\2\u01d2\u01d7\5.\30\2"+
		"\u01d3\u01d7\5\60\31\2\u01d4\u01d7\5\62\32\2\u01d5\u01d7\5\64\33\2\u01d6"+
		"\u01d2\3\2\2\2\u01d6\u01d3\3\2\2\2\u01d6\u01d4\3\2\2\2\u01d6\u01d5\3\2"+
		"\2\2\u01d7\u01da\3\2\2\2\u01d8\u01d6\3\2\2\2\u01d8\u01d9\3\2\2\2\u01d9"+
		"-\3\2\2\2\u01da\u01d8\3\2\2\2\u01db\u01dc\7\6\2\2\u01dc/\3\2\2\2\u01dd"+
		"\u01de\7\3\2\2\u01de\61\3\2\2\2\u01df\u01e0\7\5\2\2\u01e0\63\3\2\2\2\u01e1"+
		"\u01e2\7\4\2\2\u01e2\65\3\2\2\2\u01e3\u01e4\7\b\2\2\u01e4\67\3\2\2\2\u01e5"+
		"\u01e6\7B\2\2\u01e69\3\2\2\2\u01e7\u01e8\t\3\2\2\u01e8;\3\2\2\2\u01e9"+
		"\u01ea\7\26\2\2\u01ea=\3\2\2\2\u01eb\u01ec\t\4\2\2\u01ec?\3\2\2\2\u01ed"+
		"\u01f3\t\5\2\2\u01ee\u01f3\t\6\2\2\u01ef\u01f3\5F$\2\u01f0\u01f3\5H%\2"+
		"\u01f1\u01f3\5J&\2\u01f2\u01ed\3\2\2\2\u01f2\u01ee\3\2\2\2\u01f2\u01ef"+
		"\3\2\2\2\u01f2\u01f0\3\2\2\2\u01f2\u01f1\3\2\2\2\u01f3A\3\2\2\2\u01f4"+
		"\u01fe\5\60\31\2\u01f5\u01fe\5\62\32\2\u01f6\u01fe\5\64\33\2\u01f7\u01fe"+
		"\t\7\2\2\u01f8\u01fe\t\b\2\2\u01f9\u01fe\t\t\2\2\u01fa\u01fe\5F$\2\u01fb"+
		"\u01fe\5H%\2\u01fc\u01fe\5J&\2\u01fd\u01f4\3\2\2\2\u01fd\u01f5\3\2\2\2"+
		"\u01fd\u01f6\3\2\2\2\u01fd\u01f7\3\2\2\2\u01fd\u01f8\3\2\2\2\u01fd\u01f9"+
		"\3\2\2\2\u01fd\u01fa\3\2\2\2\u01fd\u01fb\3\2\2\2\u01fd\u01fc\3\2\2\2\u01fe"+
		"C\3\2\2\2\u01ff\u0200\58\35\2\u0200\u0201\5\66\34\2\u0201\u0206\3\2\2"+
		"\2\u0202\u0203\58\35\2\u0203\u0204\58\35\2\u0204\u0206\3\2\2\2\u0205\u01ff"+
		"\3\2\2\2\u0205\u0202\3\2\2\2\u0206E\3\2\2\2\u0207\u0208\t\n\2\2\u0208"+
		"\u0209\5L\'\2\u0209G\3\2\2\2\u020a\u020b\7\u00c3\2\2\u020b\u020c\t\13"+
		"\2\2\u020c\u0219\5L\'\2\u020d\u020e\t\f\2\2\u020e\u020f\5L\'\2\u020f\u0210"+
		"\5L\'\2\u0210\u0219\3\2\2\2\u0211\u0212\7\u00d0\2\2\u0212\u0213\t\r\2"+
		"\2\u0213\u0219\5L\'\2\u0214\u0215\t\16\2\2\u0215\u0216\5L\'\2\u0216\u0217"+
		"\5L\'\2\u0217\u0219\3\2\2\2\u0218\u020a\3\2\2\2\u0218\u020d\3\2\2\2\u0218"+
		"\u0211\3\2\2\2\u0218\u0214\3\2\2\2\u0219I\3\2\2\2\u021a\u021b\7\u00d3"+
		"\2\2\u021b\u021c\t\17\2\2\u021c\u021d\5L\'\2\u021d\u021e\5L\'\2\u021e"+
		"\u022a\3\2\2\2\u021f\u0220\t\20\2\2\u0220\u0221\5L\'\2\u0221\u0222\5L"+
		"\'\2\u0222\u0223\5L\'\2\u0223\u022a\3\2\2\2\u0224\u0225\7\u00d7\2\2\u0225"+
		"\u0226\t\21\2\2\u0226\u0227\5L\'\2\u0227\u0228\5L\'\2\u0228\u022a\3\2"+
		"\2\2\u0229\u021a\3\2\2\2\u0229\u021f\3\2\2\2\u0229\u0224\3\2\2\2\u022a"+
		"K\3\2\2\2\u022b\u022c\t\22\2\2\u022cM\3\2\2\2\u022d\u022e\7A\2\2\u022e"+
		"\u022f\7A\2\2\u022f\u0230\3\2\2\2\u0230\u0231\5,\27\2\u0231\u0232\7\21"+
		"\2\2\u0232\u0236\5,\27\2\u0233\u0234\5R*\2\u0234\u0235\5,\27\2\u0235\u0237"+
		"\3\2\2\2\u0236\u0233\3\2\2\2\u0236\u0237\3\2\2\2\u0237\u0239\3\2\2\2\u0238"+
		"\u023a\5V,\2\u0239\u0238\3\2\2\2\u0239\u023a\3\2\2\2\u023a\u023b\3\2\2"+
		"\2\u023b\u023c\7C\2\2\u023c\u023d\7C\2\2\u023dO\3\2\2\2\u023e\u023f\7"+
		"A\2\2\u023f\u0240\7A\2\2\u0240\u0241\3\2\2\2\u0241\u0242\5,\27\2\u0242"+
		"\u0243\7d\2\2\u0243\u0245\5,\27\2\u0244\u0246\5V,\2\u0245\u0244\3\2\2"+
		"\2\u0245\u0246\3\2\2\2\u0246\u0247\3\2\2\2\u0247\u0248\7C\2\2\u0248\u0249"+
		"\7C\2\2\u0249Q\3\2\2\2\u024a\u0250\5T+\2\u024b\u024c\5,\27\2\u024c\u024d"+
		"\7\16\2\2\u024d\u024e\5f\64\2\u024e\u024f\7\17\2\2\u024f\u0251\3\2\2\2"+
		"\u0250\u024b\3\2\2\2\u0250\u0251\3\2\2\2\u0251S\3\2\2\2\u0252\u0253\7"+
		"O\2\2\u0253\u025b\7J\2\2\u0254\u0255\7Y\2\2\u0255\u0256\7I\2\2\u0256\u025b"+
		"\7M\2\2\u0257\u0258\7K\2\2\u0258\u0259\7I\2\2\u0259\u025b\7R\2\2\u025a"+
		"\u0252\3\2\2\2\u025a\u0254\3\2\2\2\u025a\u0257\3\2\2\2\u025bU\3\2\2\2"+
		"\u025c\u025d\5\u00a4S\2\u025d\u025e\5,\27\2\u025e\u0260\3\2\2\2\u025f"+
		"\u025c\3\2\2\2\u025f\u0260\3\2\2\2\u0260\u0264\3\2\2\2\u0261\u0262\5X"+
		"-\2\u0262\u0263\5,\27\2\u0263\u0265\3\2\2\2\u0264\u0261\3\2\2\2\u0264"+
		"\u0265\3\2\2\2\u0265\u0269\3\2\2\2\u0266\u0267\5Z.\2\u0267\u0268\5,\27"+
		"\2\u0268\u026a\3\2\2\2\u0269\u0266\3\2\2\2\u0269\u026a\3\2\2\2\u026aW"+
		"\3\2\2\2\u026b\u026c\7&\2\2\u026c\u026d\5\\/\2\u026dY\3\2\2\2\u026e\u026f"+
		"\7\n\2\2\u026f\u0270\5\\/\2\u0270[\3\2\2\2\u0271\u0273\5^\60\2\u0272\u0271"+
		"\3\2\2\2\u0273\u0276\3\2\2\2\u0274\u0272\3\2\2\2\u0274\u0275\3\2\2\2\u0275"+
		"\u028a\3\2\2\2\u0276\u0274\3\2\2\2\u0277\u027b\5\66\34\2\u0278\u027a\5"+
		"`\61\2\u0279\u0278\3\2\2\2\u027a\u027d\3\2\2\2\u027b\u0279\3\2\2\2\u027b"+
		"\u027c\3\2\2\2\u027c\u027e\3\2\2\2\u027d\u027b\3\2\2\2\u027e\u027f\5\66"+
		"\34\2\u027f\u028a\3\2\2\2\u0280\u0284\5d\63\2\u0281\u0283\5b\62\2\u0282"+
		"\u0281\3\2\2\2\u0283\u0286\3\2\2\2\u0284\u0282\3\2\2\2\u0284\u0285\3\2"+
		"\2\2\u0285\u0287\3\2\2\2\u0286\u0284\3\2\2\2\u0287\u0288\5d\63\2\u0288"+
		"\u028a\3\2\2\2\u0289\u0274\3\2\2\2\u0289\u0277\3\2\2\2\u0289\u0280\3\2"+
		"\2\2\u028a]\3\2\2\2\u028b\u0293\7\7\2\2\u028c\u0293\7\t\2\2\u028d\u0293"+
		"\t\23\2\2\u028e\u0293\t\24\2\2\u028f\u0293\t\25\2\2\u0290\u0293\7B\2\2"+
		"\u0291\u0293\t\26\2\2\u0292\u028b\3\2\2\2\u0292\u028c\3\2\2\2\u0292\u028d"+
		"\3\2\2\2\u0292\u028e\3\2\2\2\u0292\u028f\3\2\2\2\u0292\u0290\3\2\2\2\u0292"+
		"\u0291\3\2\2\2\u0293_\3\2\2\2\u0294\u0299\7\7\2\2\u0295\u0299\7\t\2\2"+
		"\u0296\u0299\t\27\2\2\u0297\u0299\t\30\2\2\u0298\u0294\3\2\2\2\u0298\u0295"+
		"\3\2\2\2\u0298\u0296\3\2\2\2\u0298\u0297\3\2\2\2\u0299a\3\2\2\2\u029a"+
		"\u029f\t\31\2\2\u029b\u029f\t\23\2\2\u029c\u029f\t\24\2\2\u029d\u029f"+
		"\t\30\2\2\u029e\u029a\3\2\2\2\u029e\u029b\3\2\2\2\u029e\u029c\3\2\2\2"+
		"\u029e\u029d\3\2\2\2\u029fc\3\2\2\2\u02a0\u02a1\7\r\2\2\u02a1e\3\2\2\2"+
		"\u02a2\u02ac\5,\27\2\u02a3\u02ad\5j\66\2\u02a4\u02ad\5l\67\2\u02a5\u02ad"+
		"\5h\65\2\u02a6\u02a7\7\16\2\2\u02a7\u02a8\5,\27\2\u02a8\u02a9\5f\64\2"+
		"\u02a9\u02aa\5,\27\2\u02aa\u02ab\7\17\2\2\u02ab\u02ad\3\2\2\2\u02ac\u02a3"+
		"\3\2\2\2\u02ac\u02a4\3\2\2\2\u02ac\u02a5\3\2\2\2\u02ac\u02a6\3\2\2\2\u02ad"+
		"\u02ae\3\2\2\2\u02ae\u02af\5,\27\2\u02afg\3\2\2\2\u02b0\u02b1\5|?\2\u02b1"+
		"\u02b2\5,\27\2\u02b2\u02b4\3\2\2\2\u02b3\u02b0\3\2\2\2\u02b3\u02b4\3\2"+
		"\2\2\u02b4\u02b5\3\2\2\2\u02b5\u02b6\5v<\2\u02b6i\3\2\2\2\u02b7\u02b8"+
		"\5h\65\2\u02b8\u02b9\5,\27\2\u02b9\u02ba\7 \2\2\u02ba\u02bb\5,\27\2\u02bb"+
		"\u02bc\5\u0090I\2\u02bck\3\2\2\2\u02bd\u02c1\5n8\2\u02be\u02c1\5p9\2\u02bf"+
		"\u02c1\5r:\2\u02c0\u02bd\3\2\2\2\u02c0\u02be\3\2\2\2\u02c0\u02bf\3\2\2"+
		"\2\u02c1m\3\2\2\2\u02c2\u02c8\5t;\2\u02c3\u02c4\5,\27\2\u02c4\u02c5\5"+
		"\u008aF\2\u02c5\u02c6\5,\27\2\u02c6\u02c7\5t;\2\u02c7\u02c9\3\2\2\2\u02c8"+
		"\u02c3\3\2\2\2\u02c9\u02ca\3\2\2\2\u02ca\u02c8\3\2\2\2\u02ca\u02cb\3\2"+
		"\2\2\u02cbo\3\2\2\2\u02cc\u02d2\5t;\2\u02cd\u02ce\5,\27\2\u02ce\u02cf"+
		"\5\u008cG\2\u02cf\u02d0\5,\27\2\u02d0\u02d1\5t;\2\u02d1\u02d3\3\2\2\2"+
		"\u02d2\u02cd\3\2\2\2\u02d3\u02d4\3\2\2\2\u02d4\u02d2\3\2\2\2\u02d4\u02d5"+
		"\3\2\2\2\u02d5q\3\2\2\2\u02d6\u02d7\5t;\2\u02d7\u02d8\5,\27\2\u02d8\u02d9"+
		"\5\u008eH\2\u02d9\u02da\5,\27\2\u02da\u02db\5t;\2\u02dbs\3\2\2\2\u02dc"+
		"\u02e4\5h\65\2\u02dd\u02de\7\16\2\2\u02de\u02df\5,\27\2\u02df\u02e0\5"+
		"f\64\2\u02e0\u02e1\5,\27\2\u02e1\u02e2\7\17\2\2\u02e2\u02e4\3\2\2\2\u02e3"+
		"\u02dc\3\2\2\2\u02e3\u02dd\3\2\2\2\u02e4u\3\2\2\2\u02e5\u02e6\5x=\2\u02e6"+
		"\u02e7\5,\27\2\u02e7\u02e9\3\2\2\2\u02e8\u02e5\3\2\2\2\u02e8\u02e9\3\2"+
		"\2\2\u02e9\u02ec\3\2\2\2\u02ea\u02ed\5\16\b\2\u02eb\u02ed\5z>\2\u02ec"+
		"\u02ea\3\2\2\2\u02ec\u02eb\3\2\2\2\u02edw\3\2\2\2\u02ee\u02ef\7D\2\2\u02ef"+
		"y\3\2\2\2\u02f0\u02f1\7\20\2\2\u02f1{\3\2\2\2\u02f2\u02f9\5\u0082B\2\u02f3"+
		"\u02f9\5\u0080A\2\u02f4\u02f9\5~@\2\u02f5\u02f9\5\u0088E\2\u02f6\u02f9"+
		"\5\u0086D\2\u02f7\u02f9\5\u0084C\2\u02f8\u02f2\3\2\2\2\u02f8\u02f3\3\2"+
		"\2\2\u02f8\u02f4\3\2\2\2\u02f8\u02f5\3\2\2\2\u02f8\u02f6\3\2\2\2\u02f8"+
		"\u02f7\3\2\2\2\u02f9}\3\2\2\2\u02fa\u02fb\7\"\2\2\u02fb\177\3\2\2\2\u02fc"+
		"\u02fd\7\"\2\2\u02fd\u02fe\7\"\2\2\u02fe\u0081\3\2\2\2\u02ff\u0300\7\""+
		"\2\2\u0300\u0301\7\7\2\2\u0301\u0083\3\2\2\2\u0302\u0303\7$\2\2\u0303"+
		"\u0085\3\2\2\2\u0304\u0305\7$\2\2\u0305\u0306\7$\2\2\u0306\u0087\3\2\2"+
		"\2\u0307\u0308\7$\2\2\u0308\u0309\7\7\2\2\u0309\u0089\3\2\2\2\u030a\u030b"+
		"\t\32\2\2\u030b\u030c\t\33\2\2\u030c\u030d\t\34\2\2\u030d\u0310\5\u00be"+
		"`\2\u030e\u0310\7\22\2\2\u030f\u030a\3\2\2\2\u030f\u030e\3\2\2\2\u0310"+
		"\u008b\3\2\2\2\u0311\u0312\t\35\2\2\u0312\u0313\t\36\2\2\u0313\u0314\5"+
		"\u00be`\2\u0314\u008d\3\2\2\2\u0315\u0316\t\37\2\2\u0316\u0317\t \2\2"+
		"\u0317\u0318\t\33\2\2\u0318\u0319\t!\2\2\u0319\u031a\t\"\2\2\u031a\u031b"+
		"\5\u00be`\2\u031b\u008f\3\2\2\2\u031c\u031d\5\u0096L\2\u031d\u0320\5,"+
		"\27\2\u031e\u0321\5\u0092J\2\u031f\u0321\5\u0094K\2\u0320\u031e\3\2\2"+
		"\2\u0320\u031f\3\2\2\2\u0320\u0321\3\2\2\2\u0321\u0091\3\2\2\2\u0322\u0323"+
		"\5,\27\2\u0323\u0324\5\u008aF\2\u0324\u0325\5,\27\2\u0325\u0326\5\u0096"+
		"L\2\u0326\u0328\3\2\2\2\u0327\u0322\3\2\2\2\u0328\u0329\3\2\2\2\u0329"+
		"\u0327\3\2\2\2\u0329\u032a\3\2\2\2\u032a\u0093\3\2\2\2\u032b\u032c\5,"+
		"\27\2\u032c\u032d\5\u008cG\2\u032d\u032e\5,\27\2\u032e\u032f\5\u0096L"+
		"\2\u032f\u0331\3\2\2\2\u0330\u032b\3\2\2\2\u0331\u0332\3\2\2\2\u0332\u0330"+
		"\3\2\2\2\u0332\u0333\3\2\2\2\u0333\u0095\3\2\2\2\u0334\u033d\5\u0098M"+
		"\2\u0335\u033d\5\u00a0Q\2\u0336\u0337\7\16\2\2\u0337\u0338\5,\27\2\u0338"+
		"\u0339\5\u0090I\2\u0339\u033a\5,\27\2\u033a\u033b\7\17\2\2\u033b\u033d"+
		"\3\2\2\2\u033c\u0334\3\2\2\2\u033c\u0335\3\2\2\2\u033c\u0336\3\2\2\2\u033d"+
		"\u0097\3\2\2\2\u033e\u033f\5\u009eP\2\u033f\u0342\5,\27\2\u0340\u0343"+
		"\5\u009aN\2\u0341\u0343\5\u009cO\2\u0342\u0340\3\2\2\2\u0342\u0341\3\2"+
		"\2\2\u0342\u0343\3\2\2\2\u0343\u0099\3\2\2\2\u0344\u0345\5,\27\2\u0345"+
		"\u0346\5\u008aF\2\u0346\u0347\5,\27\2\u0347\u0348\5\u009eP\2\u0348\u034a"+
		"\3\2\2\2\u0349\u0344\3\2\2\2\u034a\u034b\3\2\2\2\u034b\u0349\3\2\2\2\u034b"+
		"\u034c\3\2\2\2\u034c\u009b\3\2\2\2\u034d\u034e\5,\27\2\u034e\u034f\5\u008c"+
		"G\2\u034f\u0350\5,\27\2\u0350\u0351\5\u009eP\2\u0351\u0353\3\2\2\2\u0352"+
		"\u034d\3\2\2\2\u0353\u0354\3\2\2\2\u0354\u0352\3\2\2\2\u0354\u0355\3\2"+
		"\2\2\u0355\u009d\3\2\2\2\u0356\u035e\5\u00a2R\2\u0357\u0358\7\16\2\2\u0358"+
		"\u0359\5,\27\2\u0359\u035a\5\u0098M\2\u035a\u035b\5,\27\2\u035b\u035c"+
		"\7\17\2\2\u035c\u035e\3\2\2\2\u035d\u0356\3\2\2\2\u035d\u0357\3\2\2\2"+
		"\u035e\u009f\3\2\2\2\u035f\u0360\7A\2\2\u0360\u0361\5\u00a4S\2\u0361\u0362"+
		"\5,\27\2\u0362\u0363\7C\2\2\u0363\u0365\3\2\2\2\u0364\u035f\3\2\2\2\u0364"+
		"\u0365\3\2\2\2\u0365\u0366\3\2\2\2\u0366\u0367\7a\2\2\u0367\u0368\5,\27"+
		"\2\u0368\u0369\5\u0098M\2\u0369\u036a\5,\27\2\u036a\u036b\7c\2\2\u036b"+
		"\u00a1\3\2\2\2\u036c\u036d\7A\2\2\u036d\u036e\5\u00a4S\2\u036e\u036f\7"+
		"C\2\2\u036f\u0370\5,\27\2\u0370\u0372\3\2\2\2\u0371\u036c\3\2\2\2\u0371"+
		"\u0372\3\2\2\2\u0372\u0376\3\2\2\2\u0373\u0374\5\u00aeX\2\u0374\u0375"+
		"\5,\27\2\u0375\u0377\3\2\2\2\u0376\u0373\3\2\2\2\u0376\u0377\3\2\2\2\u0377"+
		"\u037b\3\2\2\2\u0378\u0379\5\u00b0Y\2\u0379\u037a\5,\27\2\u037a\u037c"+
		"\3\2\2\2\u037b\u0378\3\2\2\2\u037b\u037c\3\2\2\2\u037c\u037d\3\2\2\2\u037d"+
		"\u037e\5\u00b2Z\2\u037e\u038e\5,\27\2\u037f\u0380\5\u00b6\\\2\u0380\u0381"+
		"\5,\27\2\u0381\u0382\5\u00b4[\2\u0382\u038f\3\2\2\2\u0383\u0384\5\u00b8"+
		"]\2\u0384\u0385\5,\27\2\u0385\u0386\7\t\2\2\u0386\u0387\5$\23\2\u0387"+
		"\u038f\3\2\2\2\u0388\u0389\5\u00ba^\2\u0389\u038a\5,\27\2\u038a\u038b"+
		"\5\66\34\2\u038b\u038c\5\"\22\2\u038c\u038d\5\66\34\2\u038d\u038f\3\2"+
		"\2\2\u038e\u037f\3\2\2\2\u038e\u0383\3\2\2\2\u038e\u0388\3\2\2\2\u038f"+
		"\u00a3\3\2\2\2\u0390\u0391\5\u00a6T\2\u0391\u0392\5\u00a8U\2\u0392\u0393"+
		"\5\u00aaV\2\u0393\u00a5\3\2\2\2\u0394\u0395\5\u00bc_\2\u0395\u00a7\3\2"+
		"\2\2\u0396\u0397\7\24\2\2\u0397\u0398\7\24\2\2\u0398\u00a9\3\2\2\2\u0399"+
		"\u039c\5\u00bc_\2\u039a\u039c\5\u00acW\2\u039b\u0399\3\2\2\2\u039b\u039a"+
		"\3\2\2\2\u039c\u00ab\3\2\2\2\u039d\u039e\7\20\2\2\u039e\u00ad\3\2\2\2"+
		"\u039f\u03a0\78\2\2\u03a0\u00af\3\2\2\2\u03a1\u03a4\5\u0080A\2\u03a2\u03a4"+
		"\5~@\2\u03a3\u03a1\3\2\2\2\u03a3\u03a2\3\2\2\2\u03a4\u00b1\3\2\2\2\u03a5"+
		"\u03a8\5\16\b\2\u03a6\u03a8\5z>\2\u03a7\u03a5\3\2\2\2\u03a7\u03a6\3\2"+
		"\2\2\u03a8\u00b3\3\2\2\2\u03a9\u03b4\5h\65\2\u03aa\u03ab\7\16\2\2\u03ab"+
		"\u03ae\5,\27\2\u03ac\u03af\5j\66\2\u03ad\u03af\5l\67\2\u03ae\u03ac\3\2"+
		"\2\2\u03ae\u03ad\3\2\2\2\u03af\u03b0\3\2\2\2\u03b0\u03b1\5,\27\2\u03b1"+
		"\u03b2\7\17\2\2\u03b2\u03b4\3\2\2\2\u03b3\u03a9\3\2\2\2\u03b3\u03aa\3"+
		"\2\2\2\u03b4\u00b5\3\2\2\2\u03b5\u03b9\7#\2\2\u03b6\u03b7\7\7\2\2\u03b7"+
		"\u03b9\7#\2\2\u03b8\u03b5\3\2\2\2\u03b8\u03b6\3\2\2\2\u03b9\u00b7\3\2"+
		"\2\2\u03ba\u03c4\7#\2\2\u03bb\u03bc\7\7\2\2\u03bc\u03c4\7#\2\2\u03bd\u03be"+
		"\7\"\2\2\u03be\u03c4\7#\2\2\u03bf\u03c4\7\"\2\2\u03c0\u03c1\7$\2\2\u03c1"+
		"\u03c4\7#\2\2\u03c2\u03c4\7$\2\2\u03c3\u03ba\3\2\2\2\u03c3\u03bb\3\2\2"+
		"\2\u03c3\u03bd\3\2\2\2\u03c3\u03bf\3\2\2\2\u03c3\u03c0\3\2\2\2\u03c3\u03c2"+
		"\3\2\2\2\u03c4\u00b9\3\2\2\2\u03c5\u03c9\7#\2\2\u03c6\u03c7\7\7\2\2\u03c7"+
		"\u03c9\7#\2\2\u03c8\u03c5\3\2\2\2\u03c8\u03c6\3\2\2\2\u03c9\u00bb\3\2"+
		"\2\2\u03ca\u03ce\5> \2\u03cb\u03cd\5:\36\2\u03cc\u03cb\3\2\2\2\u03cd\u03d0"+
		"\3\2\2\2\u03ce\u03cc\3\2\2\2\u03ce\u03cf\3\2\2\2\u03cf\u03d3\3\2\2\2\u03d0"+
		"\u03ce\3\2\2\2\u03d1\u03d3\5<\37\2\u03d2\u03ca\3\2\2\2\u03d2\u03d1\3\2"+
		"\2\2\u03d3\u00bd\3\2\2\2\u03d4\u03da\5.\30\2\u03d5\u03da\5\60\31\2\u03d6"+
		"\u03da\5\62\32\2\u03d7\u03da\5\64\33\2\u03d8\u03da\5\u00c0a\2\u03d9\u03d4"+
		"\3\2\2\2\u03d9\u03d5\3\2\2\2\u03d9\u03d6\3\2\2\2\u03d9\u03d7\3\2\2\2\u03d9"+
		"\u03d8\3\2\2\2\u03da\u03db\3\2\2\2\u03db\u03d9\3\2\2\2\u03db\u03dc\3\2"+
		"\2\2\u03dc\u00bf\3\2\2\2\u03dd\u03de\7\25\2\2\u03de\u03df\7\20\2\2\u03df"+
		"\u03e4\3\2\2\2\u03e0\u03e3\5\u00c2b\2\u03e1\u03e3\5\u00c4c\2\u03e2\u03e0"+
		"\3\2\2\2\u03e2\u03e1\3\2\2\2\u03e3\u03e6\3\2\2\2\u03e4\u03e2\3\2\2\2\u03e4"+
		"\u03e5\3\2\2\2\u03e5\u03e7\3\2\2\2\u03e6\u03e4\3\2\2\2\u03e7\u03e8\7\20"+
		"\2\2\u03e8\u03e9\7\25\2\2\u03e9\u00c1\3\2\2\2\u03ea\u03f4\5.\30\2\u03eb"+
		"\u03f4\5\60\31\2\u03ec\u03f4\5\62\32\2\u03ed\u03f4\5\64\33\2\u03ee\u03f4"+
		"\t#\2\2\u03ef\u03f4\t$\2\2\u03f0\u03f4\5F$\2\u03f1\u03f4\5H%\2\u03f2\u03f4"+
		"\5J&\2\u03f3\u03ea\3\2\2\2\u03f3\u03eb\3\2\2\2\u03f3\u03ec\3\2\2\2\u03f3"+
		"\u03ed\3\2\2\2\u03f3\u03ee\3\2\2\2\u03f3\u03ef\3\2\2\2\u03f3\u03f0\3\2"+
		"\2\2\u03f3\u03f1\3\2\2\2\u03f3\u03f2\3\2\2\2\u03f4\u00c3\3\2\2\2\u03f5"+
		"\u03f6\7\20\2\2\u03f6\u03f7\5\u00c6d\2\u03f7\u00c5\3\2\2\2\u03f8\u0402"+
		"\5.\30\2\u03f9\u0402\5\60\31\2\u03fa\u0402\5\62\32\2\u03fb\u0402\5\64"+
		"\33\2\u03fc\u0402\t%\2\2\u03fd\u0402\t&\2\2\u03fe\u0402\5F$\2\u03ff\u0402"+
		"\5H%\2\u0400\u0402\5J&\2\u0401\u03f8\3\2\2\2\u0401\u03f9\3\2\2\2\u0401"+
		"\u03fa\3\2\2\2\u0401\u03fb\3\2\2\2\u0401\u03fc\3\2\2\2\u0401\u03fd\3\2"+
		"\2\2\u0401\u03fe\3\2\2\2\u0401\u03ff\3\2\2\2\u0401\u0400\3\2\2\2\u0402"+
		"\u00c7\3\2\2\2X\u00cc\u00d7\u00db\u00ed\u00f9\u00fb\u0103\u0109\u010e"+
		"\u0113\u0119\u011f\u012f\u0135\u0146\u014f\u0153\u0155\u0159\u015c\u0162"+
		"\u0166\u016d\u0176\u01d0\u01d6\u01d8\u01f2\u01fd\u0205\u0218\u0229\u0236"+
		"\u0239\u0245\u0250\u025a\u025f\u0264\u0269\u0274\u027b\u0284\u0289\u0292"+
		"\u0298\u029e\u02ac\u02b3\u02c0\u02ca\u02d4\u02e3\u02e8\u02ec\u02f8\u030f"+
		"\u0320\u0329\u0332\u033c\u0342\u034b\u0354\u035d\u0364\u0371\u0376\u037b"+
		"\u038e\u039b\u03a3\u03a7\u03ae\u03b3\u03b8\u03c3\u03c8\u03ce\u03d2\u03d9"+
		"\u03db\u03e2\u03e4\u03f3\u0401";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}