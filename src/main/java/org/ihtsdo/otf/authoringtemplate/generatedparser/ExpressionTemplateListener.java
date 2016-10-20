// Generated from ExpressionTemplate.txt by ANTLR 4.5.3
package org.ihtsdo.otf.authoringtemplate.generatedparser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ExpressionTemplateParser}.
 */
public interface ExpressionTemplateListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#expressiontemplate}.
	 * @param ctx the parse tree
	 */
	void enterExpressiontemplate(ExpressionTemplateParser.ExpressiontemplateContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#expressiontemplate}.
	 * @param ctx the parse tree
	 */
	void exitExpressiontemplate(ExpressionTemplateParser.ExpressiontemplateContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#subexpression}.
	 * @param ctx the parse tree
	 */
	void enterSubexpression(ExpressionTemplateParser.SubexpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#subexpression}.
	 * @param ctx the parse tree
	 */
	void exitSubexpression(ExpressionTemplateParser.SubexpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#definitionstatus}.
	 * @param ctx the parse tree
	 */
	void enterDefinitionstatus(ExpressionTemplateParser.DefinitionstatusContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#definitionstatus}.
	 * @param ctx the parse tree
	 */
	void exitDefinitionstatus(ExpressionTemplateParser.DefinitionstatusContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#equivalentto}.
	 * @param ctx the parse tree
	 */
	void enterEquivalentto(ExpressionTemplateParser.EquivalenttoContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#equivalentto}.
	 * @param ctx the parse tree
	 */
	void exitEquivalentto(ExpressionTemplateParser.EquivalenttoContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#subtypeof}.
	 * @param ctx the parse tree
	 */
	void enterSubtypeof(ExpressionTemplateParser.SubtypeofContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#subtypeof}.
	 * @param ctx the parse tree
	 */
	void exitSubtypeof(ExpressionTemplateParser.SubtypeofContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#focusconcept}.
	 * @param ctx the parse tree
	 */
	void enterFocusconcept(ExpressionTemplateParser.FocusconceptContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#focusconcept}.
	 * @param ctx the parse tree
	 */
	void exitFocusconcept(ExpressionTemplateParser.FocusconceptContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#conceptreference}.
	 * @param ctx the parse tree
	 */
	void enterConceptreference(ExpressionTemplateParser.ConceptreferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#conceptreference}.
	 * @param ctx the parse tree
	 */
	void exitConceptreference(ExpressionTemplateParser.ConceptreferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#conceptid}.
	 * @param ctx the parse tree
	 */
	void enterConceptid(ExpressionTemplateParser.ConceptidContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#conceptid}.
	 * @param ctx the parse tree
	 */
	void exitConceptid(ExpressionTemplateParser.ConceptidContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(ExpressionTemplateParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(ExpressionTemplateParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#refinement}.
	 * @param ctx the parse tree
	 */
	void enterRefinement(ExpressionTemplateParser.RefinementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#refinement}.
	 * @param ctx the parse tree
	 */
	void exitRefinement(ExpressionTemplateParser.RefinementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#attributegroup}.
	 * @param ctx the parse tree
	 */
	void enterAttributegroup(ExpressionTemplateParser.AttributegroupContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#attributegroup}.
	 * @param ctx the parse tree
	 */
	void exitAttributegroup(ExpressionTemplateParser.AttributegroupContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#attributeset}.
	 * @param ctx the parse tree
	 */
	void enterAttributeset(ExpressionTemplateParser.AttributesetContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#attributeset}.
	 * @param ctx the parse tree
	 */
	void exitAttributeset(ExpressionTemplateParser.AttributesetContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterAttribute(ExpressionTemplateParser.AttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitAttribute(ExpressionTemplateParser.AttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#attributename}.
	 * @param ctx the parse tree
	 */
	void enterAttributename(ExpressionTemplateParser.AttributenameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#attributename}.
	 * @param ctx the parse tree
	 */
	void exitAttributename(ExpressionTemplateParser.AttributenameContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#attributevalue}.
	 * @param ctx the parse tree
	 */
	void enterAttributevalue(ExpressionTemplateParser.AttributevalueContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#attributevalue}.
	 * @param ctx the parse tree
	 */
	void exitAttributevalue(ExpressionTemplateParser.AttributevalueContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#expressionvalue}.
	 * @param ctx the parse tree
	 */
	void enterExpressionvalue(ExpressionTemplateParser.ExpressionvalueContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#expressionvalue}.
	 * @param ctx the parse tree
	 */
	void exitExpressionvalue(ExpressionTemplateParser.ExpressionvalueContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#stringvalue}.
	 * @param ctx the parse tree
	 */
	void enterStringvalue(ExpressionTemplateParser.StringvalueContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#stringvalue}.
	 * @param ctx the parse tree
	 */
	void exitStringvalue(ExpressionTemplateParser.StringvalueContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#numericvalue}.
	 * @param ctx the parse tree
	 */
	void enterNumericvalue(ExpressionTemplateParser.NumericvalueContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#numericvalue}.
	 * @param ctx the parse tree
	 */
	void exitNumericvalue(ExpressionTemplateParser.NumericvalueContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#integervalue}.
	 * @param ctx the parse tree
	 */
	void enterIntegervalue(ExpressionTemplateParser.IntegervalueContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#integervalue}.
	 * @param ctx the parse tree
	 */
	void exitIntegervalue(ExpressionTemplateParser.IntegervalueContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#decimalvalue}.
	 * @param ctx the parse tree
	 */
	void enterDecimalvalue(ExpressionTemplateParser.DecimalvalueContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#decimalvalue}.
	 * @param ctx the parse tree
	 */
	void exitDecimalvalue(ExpressionTemplateParser.DecimalvalueContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#sctid}.
	 * @param ctx the parse tree
	 */
	void enterSctid(ExpressionTemplateParser.SctidContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#sctid}.
	 * @param ctx the parse tree
	 */
	void exitSctid(ExpressionTemplateParser.SctidContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#ws}.
	 * @param ctx the parse tree
	 */
	void enterWs(ExpressionTemplateParser.WsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#ws}.
	 * @param ctx the parse tree
	 */
	void exitWs(ExpressionTemplateParser.WsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#sp}.
	 * @param ctx the parse tree
	 */
	void enterSp(ExpressionTemplateParser.SpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#sp}.
	 * @param ctx the parse tree
	 */
	void exitSp(ExpressionTemplateParser.SpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#htab}.
	 * @param ctx the parse tree
	 */
	void enterHtab(ExpressionTemplateParser.HtabContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#htab}.
	 * @param ctx the parse tree
	 */
	void exitHtab(ExpressionTemplateParser.HtabContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#cr}.
	 * @param ctx the parse tree
	 */
	void enterCr(ExpressionTemplateParser.CrContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#cr}.
	 * @param ctx the parse tree
	 */
	void exitCr(ExpressionTemplateParser.CrContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#lf}.
	 * @param ctx the parse tree
	 */
	void enterLf(ExpressionTemplateParser.LfContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#lf}.
	 * @param ctx the parse tree
	 */
	void exitLf(ExpressionTemplateParser.LfContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#qm}.
	 * @param ctx the parse tree
	 */
	void enterQm(ExpressionTemplateParser.QmContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#qm}.
	 * @param ctx the parse tree
	 */
	void exitQm(ExpressionTemplateParser.QmContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#bs}.
	 * @param ctx the parse tree
	 */
	void enterBs(ExpressionTemplateParser.BsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#bs}.
	 * @param ctx the parse tree
	 */
	void exitBs(ExpressionTemplateParser.BsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#digit}.
	 * @param ctx the parse tree
	 */
	void enterDigit(ExpressionTemplateParser.DigitContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#digit}.
	 * @param ctx the parse tree
	 */
	void exitDigit(ExpressionTemplateParser.DigitContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#zero}.
	 * @param ctx the parse tree
	 */
	void enterZero(ExpressionTemplateParser.ZeroContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#zero}.
	 * @param ctx the parse tree
	 */
	void exitZero(ExpressionTemplateParser.ZeroContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#digitnonzero}.
	 * @param ctx the parse tree
	 */
	void enterDigitnonzero(ExpressionTemplateParser.DigitnonzeroContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#digitnonzero}.
	 * @param ctx the parse tree
	 */
	void exitDigitnonzero(ExpressionTemplateParser.DigitnonzeroContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#nonwsnonpipe}.
	 * @param ctx the parse tree
	 */
	void enterNonwsnonpipe(ExpressionTemplateParser.NonwsnonpipeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#nonwsnonpipe}.
	 * @param ctx the parse tree
	 */
	void exitNonwsnonpipe(ExpressionTemplateParser.NonwsnonpipeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#anynonescapedchar}.
	 * @param ctx the parse tree
	 */
	void enterAnynonescapedchar(ExpressionTemplateParser.AnynonescapedcharContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#anynonescapedchar}.
	 * @param ctx the parse tree
	 */
	void exitAnynonescapedchar(ExpressionTemplateParser.AnynonescapedcharContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#escapedchar}.
	 * @param ctx the parse tree
	 */
	void enterEscapedchar(ExpressionTemplateParser.EscapedcharContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#escapedchar}.
	 * @param ctx the parse tree
	 */
	void exitEscapedchar(ExpressionTemplateParser.EscapedcharContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#utf8_2}.
	 * @param ctx the parse tree
	 */
	void enterUtf8_2(ExpressionTemplateParser.Utf8_2Context ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#utf8_2}.
	 * @param ctx the parse tree
	 */
	void exitUtf8_2(ExpressionTemplateParser.Utf8_2Context ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#utf8_3}.
	 * @param ctx the parse tree
	 */
	void enterUtf8_3(ExpressionTemplateParser.Utf8_3Context ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#utf8_3}.
	 * @param ctx the parse tree
	 */
	void exitUtf8_3(ExpressionTemplateParser.Utf8_3Context ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#utf8_4}.
	 * @param ctx the parse tree
	 */
	void enterUtf8_4(ExpressionTemplateParser.Utf8_4Context ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#utf8_4}.
	 * @param ctx the parse tree
	 */
	void exitUtf8_4(ExpressionTemplateParser.Utf8_4Context ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#utf8_tail}.
	 * @param ctx the parse tree
	 */
	void enterUtf8_tail(ExpressionTemplateParser.Utf8_tailContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#utf8_tail}.
	 * @param ctx the parse tree
	 */
	void exitUtf8_tail(ExpressionTemplateParser.Utf8_tailContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#templatereplaceslot}.
	 * @param ctx the parse tree
	 */
	void enterTemplatereplaceslot(ExpressionTemplateParser.TemplatereplaceslotContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#templatereplaceslot}.
	 * @param ctx the parse tree
	 */
	void exitTemplatereplaceslot(ExpressionTemplateParser.TemplatereplaceslotContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#templateremoveslot}.
	 * @param ctx the parse tree
	 */
	void enterTemplateremoveslot(ExpressionTemplateParser.TemplateremoveslotContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#templateremoveslot}.
	 * @param ctx the parse tree
	 */
	void exitTemplateremoveslot(ExpressionTemplateParser.TemplateremoveslotContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#replaceinfo}.
	 * @param ctx the parse tree
	 */
	void enterReplaceinfo(ExpressionTemplateParser.ReplaceinfoContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#replaceinfo}.
	 * @param ctx the parse tree
	 */
	void exitReplaceinfo(ExpressionTemplateParser.ReplaceinfoContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#replaceflag}.
	 * @param ctx the parse tree
	 */
	void enterReplaceflag(ExpressionTemplateParser.ReplaceflagContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#replaceflag}.
	 * @param ctx the parse tree
	 */
	void exitReplaceflag(ExpressionTemplateParser.ReplaceflagContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#templateslotinfo}.
	 * @param ctx the parse tree
	 */
	void enterTemplateslotinfo(ExpressionTemplateParser.TemplateslotinfoContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#templateslotinfo}.
	 * @param ctx the parse tree
	 */
	void exitTemplateslotinfo(ExpressionTemplateParser.TemplateslotinfoContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#templateslotname}.
	 * @param ctx the parse tree
	 */
	void enterTemplateslotname(ExpressionTemplateParser.TemplateslotnameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#templateslotname}.
	 * @param ctx the parse tree
	 */
	void exitTemplateslotname(ExpressionTemplateParser.TemplateslotnameContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#templateslotreference}.
	 * @param ctx the parse tree
	 */
	void enterTemplateslotreference(ExpressionTemplateParser.TemplateslotreferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#templateslotreference}.
	 * @param ctx the parse tree
	 */
	void exitTemplateslotreference(ExpressionTemplateParser.TemplateslotreferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#templatestring}.
	 * @param ctx the parse tree
	 */
	void enterTemplatestring(ExpressionTemplateParser.TemplatestringContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#templatestring}.
	 * @param ctx the parse tree
	 */
	void exitTemplatestring(ExpressionTemplateParser.TemplatestringContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#nonquotestring}.
	 * @param ctx the parse tree
	 */
	void enterNonquotestring(ExpressionTemplateParser.NonquotestringContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#nonquotestring}.
	 * @param ctx the parse tree
	 */
	void exitNonquotestring(ExpressionTemplateParser.NonquotestringContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#nondoublequotestring}.
	 * @param ctx the parse tree
	 */
	void enterNondoublequotestring(ExpressionTemplateParser.NondoublequotestringContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#nondoublequotestring}.
	 * @param ctx the parse tree
	 */
	void exitNondoublequotestring(ExpressionTemplateParser.NondoublequotestringContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#nonsinglequotestring}.
	 * @param ctx the parse tree
	 */
	void enterNonsinglequotestring(ExpressionTemplateParser.NonsinglequotestringContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#nonsinglequotestring}.
	 * @param ctx the parse tree
	 */
	void exitNonsinglequotestring(ExpressionTemplateParser.NonsinglequotestringContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#sqm}.
	 * @param ctx the parse tree
	 */
	void enterSqm(ExpressionTemplateParser.SqmContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#sqm}.
	 * @param ctx the parse tree
	 */
	void exitSqm(ExpressionTemplateParser.SqmContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#expressionconstrainttemplate}.
	 * @param ctx the parse tree
	 */
	void enterExpressionconstrainttemplate(ExpressionTemplateParser.ExpressionconstrainttemplateContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#expressionconstrainttemplate}.
	 * @param ctx the parse tree
	 */
	void exitExpressionconstrainttemplate(ExpressionTemplateParser.ExpressionconstrainttemplateContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#simpleexpressionconstraint}.
	 * @param ctx the parse tree
	 */
	void enterSimpleexpressionconstraint(ExpressionTemplateParser.SimpleexpressionconstraintContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#simpleexpressionconstraint}.
	 * @param ctx the parse tree
	 */
	void exitSimpleexpressionconstraint(ExpressionTemplateParser.SimpleexpressionconstraintContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#refinedexpressionconstraint}.
	 * @param ctx the parse tree
	 */
	void enterRefinedexpressionconstraint(ExpressionTemplateParser.RefinedexpressionconstraintContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#refinedexpressionconstraint}.
	 * @param ctx the parse tree
	 */
	void exitRefinedexpressionconstraint(ExpressionTemplateParser.RefinedexpressionconstraintContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#compoundexpressionconstraint}.
	 * @param ctx the parse tree
	 */
	void enterCompoundexpressionconstraint(ExpressionTemplateParser.CompoundexpressionconstraintContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#compoundexpressionconstraint}.
	 * @param ctx the parse tree
	 */
	void exitCompoundexpressionconstraint(ExpressionTemplateParser.CompoundexpressionconstraintContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#conjunctionexpressionconstraint}.
	 * @param ctx the parse tree
	 */
	void enterConjunctionexpressionconstraint(ExpressionTemplateParser.ConjunctionexpressionconstraintContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#conjunctionexpressionconstraint}.
	 * @param ctx the parse tree
	 */
	void exitConjunctionexpressionconstraint(ExpressionTemplateParser.ConjunctionexpressionconstraintContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#disjunctionexpressionconstraint}.
	 * @param ctx the parse tree
	 */
	void enterDisjunctionexpressionconstraint(ExpressionTemplateParser.DisjunctionexpressionconstraintContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#disjunctionexpressionconstraint}.
	 * @param ctx the parse tree
	 */
	void exitDisjunctionexpressionconstraint(ExpressionTemplateParser.DisjunctionexpressionconstraintContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#exclusionexpressionconstraint}.
	 * @param ctx the parse tree
	 */
	void enterExclusionexpressionconstraint(ExpressionTemplateParser.ExclusionexpressionconstraintContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#exclusionexpressionconstraint}.
	 * @param ctx the parse tree
	 */
	void exitExclusionexpressionconstraint(ExpressionTemplateParser.ExclusionexpressionconstraintContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#subexpressionconstraint}.
	 * @param ctx the parse tree
	 */
	void enterSubexpressionconstraint(ExpressionTemplateParser.SubexpressionconstraintContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#subexpressionconstraint}.
	 * @param ctx the parse tree
	 */
	void exitSubexpressionconstraint(ExpressionTemplateParser.SubexpressionconstraintContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#eclfocusconcept}.
	 * @param ctx the parse tree
	 */
	void enterEclfocusconcept(ExpressionTemplateParser.EclfocusconceptContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#eclfocusconcept}.
	 * @param ctx the parse tree
	 */
	void exitEclfocusconcept(ExpressionTemplateParser.EclfocusconceptContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#memberof}.
	 * @param ctx the parse tree
	 */
	void enterMemberof(ExpressionTemplateParser.MemberofContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#memberof}.
	 * @param ctx the parse tree
	 */
	void exitMemberof(ExpressionTemplateParser.MemberofContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#wildcard}.
	 * @param ctx the parse tree
	 */
	void enterWildcard(ExpressionTemplateParser.WildcardContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#wildcard}.
	 * @param ctx the parse tree
	 */
	void exitWildcard(ExpressionTemplateParser.WildcardContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#constraintoperator}.
	 * @param ctx the parse tree
	 */
	void enterConstraintoperator(ExpressionTemplateParser.ConstraintoperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#constraintoperator}.
	 * @param ctx the parse tree
	 */
	void exitConstraintoperator(ExpressionTemplateParser.ConstraintoperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#descendantof}.
	 * @param ctx the parse tree
	 */
	void enterDescendantof(ExpressionTemplateParser.DescendantofContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#descendantof}.
	 * @param ctx the parse tree
	 */
	void exitDescendantof(ExpressionTemplateParser.DescendantofContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#descendantorselfof}.
	 * @param ctx the parse tree
	 */
	void enterDescendantorselfof(ExpressionTemplateParser.DescendantorselfofContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#descendantorselfof}.
	 * @param ctx the parse tree
	 */
	void exitDescendantorselfof(ExpressionTemplateParser.DescendantorselfofContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#childof}.
	 * @param ctx the parse tree
	 */
	void enterChildof(ExpressionTemplateParser.ChildofContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#childof}.
	 * @param ctx the parse tree
	 */
	void exitChildof(ExpressionTemplateParser.ChildofContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#ancestorof}.
	 * @param ctx the parse tree
	 */
	void enterAncestorof(ExpressionTemplateParser.AncestorofContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#ancestorof}.
	 * @param ctx the parse tree
	 */
	void exitAncestorof(ExpressionTemplateParser.AncestorofContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#ancestororselfof}.
	 * @param ctx the parse tree
	 */
	void enterAncestororselfof(ExpressionTemplateParser.AncestororselfofContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#ancestororselfof}.
	 * @param ctx the parse tree
	 */
	void exitAncestororselfof(ExpressionTemplateParser.AncestororselfofContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#parentof}.
	 * @param ctx the parse tree
	 */
	void enterParentof(ExpressionTemplateParser.ParentofContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#parentof}.
	 * @param ctx the parse tree
	 */
	void exitParentof(ExpressionTemplateParser.ParentofContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#conjunction}.
	 * @param ctx the parse tree
	 */
	void enterConjunction(ExpressionTemplateParser.ConjunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#conjunction}.
	 * @param ctx the parse tree
	 */
	void exitConjunction(ExpressionTemplateParser.ConjunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#disjunction}.
	 * @param ctx the parse tree
	 */
	void enterDisjunction(ExpressionTemplateParser.DisjunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#disjunction}.
	 * @param ctx the parse tree
	 */
	void exitDisjunction(ExpressionTemplateParser.DisjunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#exclusion}.
	 * @param ctx the parse tree
	 */
	void enterExclusion(ExpressionTemplateParser.ExclusionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#exclusion}.
	 * @param ctx the parse tree
	 */
	void exitExclusion(ExpressionTemplateParser.ExclusionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#eclrefinement}.
	 * @param ctx the parse tree
	 */
	void enterEclrefinement(ExpressionTemplateParser.EclrefinementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#eclrefinement}.
	 * @param ctx the parse tree
	 */
	void exitEclrefinement(ExpressionTemplateParser.EclrefinementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#conjunctionrefinementset}.
	 * @param ctx the parse tree
	 */
	void enterConjunctionrefinementset(ExpressionTemplateParser.ConjunctionrefinementsetContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#conjunctionrefinementset}.
	 * @param ctx the parse tree
	 */
	void exitConjunctionrefinementset(ExpressionTemplateParser.ConjunctionrefinementsetContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#disjunctionrefinementset}.
	 * @param ctx the parse tree
	 */
	void enterDisjunctionrefinementset(ExpressionTemplateParser.DisjunctionrefinementsetContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#disjunctionrefinementset}.
	 * @param ctx the parse tree
	 */
	void exitDisjunctionrefinementset(ExpressionTemplateParser.DisjunctionrefinementsetContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#subrefinement}.
	 * @param ctx the parse tree
	 */
	void enterSubrefinement(ExpressionTemplateParser.SubrefinementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#subrefinement}.
	 * @param ctx the parse tree
	 */
	void exitSubrefinement(ExpressionTemplateParser.SubrefinementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#eclattributeset}.
	 * @param ctx the parse tree
	 */
	void enterEclattributeset(ExpressionTemplateParser.EclattributesetContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#eclattributeset}.
	 * @param ctx the parse tree
	 */
	void exitEclattributeset(ExpressionTemplateParser.EclattributesetContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#conjunctionattributeset}.
	 * @param ctx the parse tree
	 */
	void enterConjunctionattributeset(ExpressionTemplateParser.ConjunctionattributesetContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#conjunctionattributeset}.
	 * @param ctx the parse tree
	 */
	void exitConjunctionattributeset(ExpressionTemplateParser.ConjunctionattributesetContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#disjunctionattributeset}.
	 * @param ctx the parse tree
	 */
	void enterDisjunctionattributeset(ExpressionTemplateParser.DisjunctionattributesetContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#disjunctionattributeset}.
	 * @param ctx the parse tree
	 */
	void exitDisjunctionattributeset(ExpressionTemplateParser.DisjunctionattributesetContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#subattributeset}.
	 * @param ctx the parse tree
	 */
	void enterSubattributeset(ExpressionTemplateParser.SubattributesetContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#subattributeset}.
	 * @param ctx the parse tree
	 */
	void exitSubattributeset(ExpressionTemplateParser.SubattributesetContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#eclattributegroup}.
	 * @param ctx the parse tree
	 */
	void enterEclattributegroup(ExpressionTemplateParser.EclattributegroupContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#eclattributegroup}.
	 * @param ctx the parse tree
	 */
	void exitEclattributegroup(ExpressionTemplateParser.EclattributegroupContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#eclattribute}.
	 * @param ctx the parse tree
	 */
	void enterEclattribute(ExpressionTemplateParser.EclattributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#eclattribute}.
	 * @param ctx the parse tree
	 */
	void exitEclattribute(ExpressionTemplateParser.EclattributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#cardinality}.
	 * @param ctx the parse tree
	 */
	void enterCardinality(ExpressionTemplateParser.CardinalityContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#cardinality}.
	 * @param ctx the parse tree
	 */
	void exitCardinality(ExpressionTemplateParser.CardinalityContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#minvalue}.
	 * @param ctx the parse tree
	 */
	void enterMinvalue(ExpressionTemplateParser.MinvalueContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#minvalue}.
	 * @param ctx the parse tree
	 */
	void exitMinvalue(ExpressionTemplateParser.MinvalueContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#to}.
	 * @param ctx the parse tree
	 */
	void enterTo(ExpressionTemplateParser.ToContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#to}.
	 * @param ctx the parse tree
	 */
	void exitTo(ExpressionTemplateParser.ToContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#maxvalue}.
	 * @param ctx the parse tree
	 */
	void enterMaxvalue(ExpressionTemplateParser.MaxvalueContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#maxvalue}.
	 * @param ctx the parse tree
	 */
	void exitMaxvalue(ExpressionTemplateParser.MaxvalueContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#many}.
	 * @param ctx the parse tree
	 */
	void enterMany(ExpressionTemplateParser.ManyContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#many}.
	 * @param ctx the parse tree
	 */
	void exitMany(ExpressionTemplateParser.ManyContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#reverseflag}.
	 * @param ctx the parse tree
	 */
	void enterReverseflag(ExpressionTemplateParser.ReverseflagContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#reverseflag}.
	 * @param ctx the parse tree
	 */
	void exitReverseflag(ExpressionTemplateParser.ReverseflagContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#attributeoperator}.
	 * @param ctx the parse tree
	 */
	void enterAttributeoperator(ExpressionTemplateParser.AttributeoperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#attributeoperator}.
	 * @param ctx the parse tree
	 */
	void exitAttributeoperator(ExpressionTemplateParser.AttributeoperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#eclattributename}.
	 * @param ctx the parse tree
	 */
	void enterEclattributename(ExpressionTemplateParser.EclattributenameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#eclattributename}.
	 * @param ctx the parse tree
	 */
	void exitEclattributename(ExpressionTemplateParser.EclattributenameContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#expressionconstraintvalue}.
	 * @param ctx the parse tree
	 */
	void enterExpressionconstraintvalue(ExpressionTemplateParser.ExpressionconstraintvalueContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#expressionconstraintvalue}.
	 * @param ctx the parse tree
	 */
	void exitExpressionconstraintvalue(ExpressionTemplateParser.ExpressionconstraintvalueContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#expressioncomparisonoperator}.
	 * @param ctx the parse tree
	 */
	void enterExpressioncomparisonoperator(ExpressionTemplateParser.ExpressioncomparisonoperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#expressioncomparisonoperator}.
	 * @param ctx the parse tree
	 */
	void exitExpressioncomparisonoperator(ExpressionTemplateParser.ExpressioncomparisonoperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#numericcomparisonoperator}.
	 * @param ctx the parse tree
	 */
	void enterNumericcomparisonoperator(ExpressionTemplateParser.NumericcomparisonoperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#numericcomparisonoperator}.
	 * @param ctx the parse tree
	 */
	void exitNumericcomparisonoperator(ExpressionTemplateParser.NumericcomparisonoperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#stringcomparisonoperator}.
	 * @param ctx the parse tree
	 */
	void enterStringcomparisonoperator(ExpressionTemplateParser.StringcomparisonoperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#stringcomparisonoperator}.
	 * @param ctx the parse tree
	 */
	void exitStringcomparisonoperator(ExpressionTemplateParser.StringcomparisonoperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#nonnegativeintegervalue}.
	 * @param ctx the parse tree
	 */
	void enterNonnegativeintegervalue(ExpressionTemplateParser.NonnegativeintegervalueContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#nonnegativeintegervalue}.
	 * @param ctx the parse tree
	 */
	void exitNonnegativeintegervalue(ExpressionTemplateParser.NonnegativeintegervalueContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#mws}.
	 * @param ctx the parse tree
	 */
	void enterMws(ExpressionTemplateParser.MwsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#mws}.
	 * @param ctx the parse tree
	 */
	void exitMws(ExpressionTemplateParser.MwsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#comment}.
	 * @param ctx the parse tree
	 */
	void enterComment(ExpressionTemplateParser.CommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#comment}.
	 * @param ctx the parse tree
	 */
	void exitComment(ExpressionTemplateParser.CommentContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#nonstarchar}.
	 * @param ctx the parse tree
	 */
	void enterNonstarchar(ExpressionTemplateParser.NonstarcharContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#nonstarchar}.
	 * @param ctx the parse tree
	 */
	void exitNonstarchar(ExpressionTemplateParser.NonstarcharContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#starwithnonfslash}.
	 * @param ctx the parse tree
	 */
	void enterStarwithnonfslash(ExpressionTemplateParser.StarwithnonfslashContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#starwithnonfslash}.
	 * @param ctx the parse tree
	 */
	void exitStarwithnonfslash(ExpressionTemplateParser.StarwithnonfslashContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionTemplateParser#nonfslash}.
	 * @param ctx the parse tree
	 */
	void enterNonfslash(ExpressionTemplateParser.NonfslashContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionTemplateParser#nonfslash}.
	 * @param ctx the parse tree
	 */
	void exitNonfslash(ExpressionTemplateParser.NonfslashContext ctx);
}