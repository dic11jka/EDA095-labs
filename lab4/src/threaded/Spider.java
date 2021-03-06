package threaded;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

public class Spider extends HTMLEditorKit.ParserCallback {
	private URLMonitor mon;
	private String baseURL;
	private Processor p;

	public Spider(Processor p, URLMonitor mon, String baseUrl) {
		this.mon = mon;
		this.baseURL = baseUrl;
		this.p = p;
	}

	public void handleStartTag(HTML.Tag tag, MutableAttributeSet a, int position) {
		String href = null;
		if (tag == HTML.Tag.A) {
			href = (String) a.getAttribute(HTML.Attribute.HREF);
		}
		if (tag == HTML.Tag.FRAME) {
			href = (String) a.getAttribute(HTML.Attribute.SRC);
		}
		add(href, false);
	}

	public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
		String href = null;
		if (t == HTML.Tag.BASE) {
			href = (String) a.getAttribute(HTML.Attribute.HREF);
			baseURL = href;
			p.setBaseURL(baseURL);
		}
		if (t == HTML.Tag.IMG) {
			href = (String) a.getAttribute(HTML.Attribute.SRC);
			add(href, true);
		}
	}

	public void add(String href, boolean traversed) {
		if (href != null) {
			if (href.startsWith("javascript") || href.startsWith("news")) return;
			if (href.startsWith("http") || href.startsWith("mailto")) {
				if (traversed) {
					mon.addTraversed(href);
				} else {
					mon.addRemaining(href);
				}
			} else {
				if (traversed) {
					try {
						mon.addTraversed(new URL(new URL(baseURL), href).toString());
					} catch (MalformedURLException e) {
						System.err.println("Fail: " + baseURL + href);
					}
				} else {
					try {
						mon.addRemaining(new URL(new URL(baseURL), href).toString());
					} catch (MalformedURLException e) {
						System.err.println("Fail: " + baseURL + href);
					}
				}
			}
		}
	}
}
