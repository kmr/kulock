package net.necomimi.android.kulock.kuler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import net.necomimi.android.common.ComponentInitializeException;
import net.necomimi.android.common.SimpleContainer;

public class KulerPickerImpl implements KulerPicker {
	private static final String COMP_KULER_CLIENT = "kuler_client";
	private static final String KULER_NAMESPACE = "http://kuler.adobe.com/kuler/API/rss/";
	private static final String KULER_THEMEITEM_ELEM = "themeItem";
	private static final String KULER_THEME_ID_ELEM = "themeID";
	private static final String KULER_SWATCH_HEX_COLOR_ELEM = "swatchHexColor";
	
	private KulerClient kuler;
	
	public List<KulerEntry> getPopular(int page) throws KulerException {
		InputStream result = this.kuler.getPopular(page);

		return parseKulerResult(result);
	}

	private List<KulerEntry> parseKulerResult(InputStream result)
			throws KulerException {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	        factory.setNamespaceAware(true);
	        XmlPullParser xpp = factory.newPullParser();
	        xpp.setInput(result, null);
	        return processDocument(xpp);
		} catch (XmlPullParserException e) {
			throw new KulerException(e);
		} catch (IOException e) {
			throw new KulerException(e);
		}
	}

    public List<KulerEntry> processDocument(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
    	List<KulerEntry> entryList = new ArrayList<KulerEntry>();
    	State state = State.Unknown;
    	
		int eventType = xpp.getEventType();
		do {
//			if (eventType == XmlPullParser.START_DOCUMENT) {
//			} else
			if (eventType == XmlPullParser.END_DOCUMENT) {
				return entryList;
			} else if (eventType == XmlPullParser.START_TAG) {
				state = processStartElement(xpp, entryList, state);
			} else if (eventType == XmlPullParser.END_TAG) {
				state = processEndElement(xpp, entryList, state);
//			} else if (eventType == XmlPullParser.TEXT) {
//				processText(xpp, entryList, state);
			}
			eventType = xpp.next();
		} while (eventType != XmlPullParser.END_DOCUMENT);
		return entryList;
	}

	public State processStartElement(XmlPullParser xpp,
			List<KulerEntry> entryList, State state) throws XmlPullParserException, IOException {
		String name = xpp.getName();
		String uri = xpp.getNamespace();
		if (!KULER_NAMESPACE.equals(uri)) {
			return state;
		}
		
		if (State.Unknown.equals(state) &&
				KULER_THEMEITEM_ELEM.equals(name)) {
			KulerEntry entry = new KulerEntry();
			entryList.add(entry);
			return State.ThemeItemFound;
		} else if (State.ThemeItemFound.equals(state) &&
				KULER_THEME_ID_ELEM.equals(name)) {
			entryList.get(entryList.size()-1).setId(xpp.nextText());
		} else if (State.ThemeItemFound.equals(state) &&
				KULER_SWATCH_HEX_COLOR_ELEM.equals(name)) {
			entryList.get(entryList.size()-1).getColors().push(xpp.nextText());
		}
		return state;
	}

	public State processEndElement(XmlPullParser xpp,
			List<KulerEntry> entryList, State state) {
		String name = xpp.getName();
		String uri = xpp.getNamespace();
		if (!KULER_NAMESPACE.equals(uri)) {
			return state;
		}
		
		if (State.ThemeItemFound.equals(state) &&
				KULER_THEMEITEM_ELEM.equals(name)) {
			return State.Unknown;
		}
		return state;
	}

	public List<KulerEntry> getRandom(int page) throws KulerException {
		InputStream result = this.kuler.getRandom(page);
		return parseKulerResult(result);
	}

	public List<KulerEntry> getRating(int page) throws KulerException {
		InputStream result = this.kuler.getRating(page);
		return parseKulerResult(result);
	}

	public List<KulerEntry> getRecent(int page) throws KulerException {
		InputStream result = this.kuler.getRecent(page);
		return parseKulerResult(result);
	}

	public void init(String key, SimpleContainer container) throws ComponentInitializeException {
		this.kuler = (KulerClient)container.getConponent(COMP_KULER_CLIENT);
		this.kuler.init(key);
	}

	private enum State {
		Unknown,
		ThemeItemFound,
	}
}
