package ca.softwareengineering.jcampfire.http;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Tag;

public class HtmlParserCallback extends HTMLEditorKit.ParserCallback {

	public static class HtmlAnchor {
		public final String href, text;

		public HtmlAnchor(String href, String text) {
			this.href = href;
			this.text = text;
		}
	}

	// This is a SAX-like API and we need to keep track of the context. We're
	// going super-simple by just parsing anchors now, but a more complex page
	// would need a stack of elements.
	enum Context {
		A
	};

	public List<HtmlAnchor> anchors = new ArrayList<HtmlAnchor>();
	String curAnchorHref = null;
	String curAnchorText = null;
	Context curContext = null;

	@Override
	public void handleEndTag(Tag t, int pos) {
		// System.out.println("END / " + t);
		if (t.equals(Tag.A)) {
			curContext = null;
			if (curAnchorHref != null) {
				anchors.add(new HtmlAnchor(curAnchorHref, curAnchorText));
				curAnchorHref = null;
				curAnchorText = null;
			}
		}
	}

	@Override
	public void handleError(String errorMsg, int pos) {
	}

	@Override
	public void handleSimpleTag(Tag t, MutableAttributeSet a, int pos) {
		// System.out.println("SIMPLETAG / " + t);
	}

	@Override
	public void handleStartTag(Tag t, MutableAttributeSet a, int pos) {
		// System.out.println("START / " + t);
		if (t.equals(Tag.A)) {
			curContext = Context.A;
			curAnchorHref = (String) a.getAttribute(HTML.Attribute.HREF);
		}
	}

	@Override
	public void handleText(char[] data, int pos) {
		// System.out.println("TEXT / " + new String(data));
		
		if (curContext == Context.A) {
			curAnchorText = new String(data);
		}
	}

}
