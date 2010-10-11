package util;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

public class LinkTextilePhraseModifier extends PatternBasedElement{
	String hrefpattern = "%s";
	public LinkTextilePhraseModifier(String hrefpattern) {
		this.hrefpattern = hrefpattern;
	}
	@Override
	protected String getPattern(int arg0) {
		return "#([^\\s#]\\S*[^\\s#])#";
	}

	@Override
	protected int getPatternGroupCount() {
		return 1;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new PatternBasedElementProcessor() {
			
			@Override
			public void emit() {
				String text = group(1);
				LinkAttributes attribute = new LinkAttributes();
				attribute.setHref(String.format(hrefpattern, text));
				builder.beginSpan(SpanType.LINK, attribute);
				markupLanguage.emitMarkupLine(parser, state, text, 0);
				builder.endSpan();
			}
		};
	}

}
