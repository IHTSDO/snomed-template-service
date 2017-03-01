package org.ihtsdo.otf.authoringtemplate.service;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.ihtsdo.otf.authoringtemplate.domain.logical.Attribute;
import org.ihtsdo.otf.authoringtemplate.domain.logical.AttributeGroup;
import org.ihtsdo.otf.authoringtemplate.domain.logical.HasCardinality;
import org.ihtsdo.otf.authoringtemplate.domain.logical.LogicalTemplate;
import org.ihtsdo.otf.authoringtemplate.service.generatedlogicalparser.ExpressionTemplateBaseListener;
import org.ihtsdo.otf.authoringtemplate.service.generatedlogicalparser.ExpressionTemplateLexer;
import org.ihtsdo.otf.authoringtemplate.service.generatedlogicalparser.ExpressionTemplateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class LogicalTemplateParserService {

	private static final Logger logger = LoggerFactory.getLogger(LogicalTemplateParserService.class);

	public LogicalTemplate parseTemplate(String templateString) throws IOException {
		return doParseTemplate(new ANTLRInputStream(templateString));
	}

	public LogicalTemplate parseTemplate(InputStream templateStream) throws IOException {
		return doParseTemplate(new ANTLRInputStream(templateStream));
	}

	private LogicalTemplate doParseTemplate(ANTLRInputStream input) throws IOException {
		final ExpressionTemplateLexer lexer = new ExpressionTemplateLexer(input);
		final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		final ExpressionTemplateParser parser = new ExpressionTemplateParser(tokenStream);
		final List<RecognitionException> exceptions = new ArrayList<>();
		parser.setErrorHandler(getErrorHandler(exceptions));

		ParserRuleContext tree;
		try {
			tree = parser.expressiontemplate();
		} catch (NullPointerException e) {
			throw new IOException("Failed to parse template", e);
		}
		final ParseTreeWalker walker = new ParseTreeWalker();
		final ExpressionTemplateListener listener = new ExpressionTemplateListener();
		walker.walk(listener, tree);

		if (!exceptions.isEmpty()) {
			// Throw the first exception we have
			throw exceptions.iterator().next();
		}

		return listener.getTemplate();
	}

	protected static final class ExpressionTemplateListener extends ExpressionTemplateBaseListener {

		private LogicalTemplate template;
		private AttributeGroup currentAttributeGroup;
		private Attribute currentAttribute;

		@Override
		public void enterExpressiontemplate(ExpressionTemplateParser.ExpressiontemplateContext ctx) {
			template = new LogicalTemplate();
		}

		@Override
		public void enterFocusconcept(ExpressionTemplateParser.FocusconceptContext ctx) {
			for (ExpressionTemplateParser.ConceptreferenceContext conceptRef : ctx.conceptreference()) {
				template.addFocusConcept(conceptRef.conceptid().getText());
			}
		}

		@Override
		public void enterAttributegroup(ExpressionTemplateParser.AttributegroupContext ctx) {
			currentAttributeGroup = new AttributeGroup();
			setSlotInfo(currentAttributeGroup, ctx.templateremoveslot());
		}

		@Override
		public void exitAttributegroup(ExpressionTemplateParser.AttributegroupContext ctx) {
			template.addAttributeGroup(currentAttributeGroup);
			currentAttributeGroup = null;
		}

		@Override
		public void enterAttribute(ExpressionTemplateParser.AttributeContext ctx) {
			currentAttribute = new Attribute();
			setSlotInfo(currentAttribute, ctx.templateremoveslot());
			final ExpressionTemplateParser.AttributenameContext attributename = ctx.attributename();
			if (attributename.conceptreference().conceptid() == null) {
				throw unsupported("Attribute name other than conceptId");
			}

			currentAttribute.setType(attributename.conceptreference().conceptid().getText());

			final ExpressionTemplateParser.ConceptreferenceContext valueConceptReference = ctx.attributevalue().expressionvalue().conceptreference();
			if (valueConceptReference.conceptid() != null) {
				currentAttribute.setValue(valueConceptReference.conceptid().getText());
			} else {
				final ExpressionTemplateParser.TemplatereplaceslotContext replaceSlot = valueConceptReference.templatereplaceslot();
				if (replaceSlot.replaceinfo() != null) {
					final String replaceFlag = replaceSlot.replaceinfo().replaceflag().getText();
					if (!"id".equals(replaceFlag)) {
						throw new UnsupportedOperationException("Replace flag of type '" + replaceFlag + "' are not supported. Only replacement flags of type 'id' are supported.");
					}
					final ExpressionTemplateParser.ExpressionconstrainttemplateContext ecl = replaceSlot.replaceinfo().expressionconstrainttemplate();
					if (ecl != null) {
						currentAttribute.setAllowableRangeECL(ecl.getText());
					}

					final ExpressionTemplateParser.TemplateslotinfoContext slotInfo = replaceSlot.templateslotinfo();
					if (slotInfo != null) {
						if (slotInfo.templateslotname() != null) {
							currentAttribute.setSlotName(slotInfo.templateslotname().templatestring().getText());
						}
						if (slotInfo.templateslotreference() != null) {
							currentAttribute.setSlotReference(slotInfo.templateslotreference().templatestring().getText());
						}
					}
				}
			}
			if (currentAttributeGroup == null) {
				template.addUngroupedAttribute(currentAttribute);
			} else {
				currentAttributeGroup.addAttribute(currentAttribute);
			}
		}

		private void setSlotInfo(HasCardinality hasCardinality, ExpressionTemplateParser.TemplateremoveslotContext templateremoveslot) {
			if (templateremoveslot != null) {
				final ExpressionTemplateParser.TemplateslotinfoContext slotInfo = templateremoveslot.templateslotinfo();
				if (slotInfo != null) {
					final ExpressionTemplateParser.CardinalityContext cardinality = slotInfo.cardinality();
					if (cardinality != null) {
						hasCardinality.setCardinalityMin(cardinality.minvalue().getText());
						hasCardinality.setCardinalityMax(cardinality.maxvalue().getText());
					}
				}
			}
		}

		private UnsupportedOperationException unsupported(String message) {
			return new UnsupportedOperationException(message + " is not supported at the moment.");
		}

		public LogicalTemplate getTemplate() {
			return template;
		}
	}

	private ANTLRErrorStrategy getErrorHandler(final List<RecognitionException> exceptions) {
		return new ANTLRErrorStrategy() {
			@Override
			public void reportError(Parser parser, RecognitionException e) {
				logger.info("Parsing error.", e);
				exceptions.add(e);
			}

			@Override
			public void reset(Parser parser) {}

			@Override
			public Token recoverInline(Parser parser) throws RecognitionException {
				return null;
			}

			@Override
			public void recover(Parser parser, RecognitionException e) throws RecognitionException {}

			@Override
			public void sync(Parser parser) throws RecognitionException {}

			@Override
			public boolean inErrorRecoveryMode(Parser parser) {
				return false;
			}

			@Override
			public void reportMatch(Parser parser) {}
		};
	}

}
