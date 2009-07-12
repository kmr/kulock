package net.necomimi.android.kulock.kuler;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class KulerClientImpl implements KulerClient {
	private static final String KULER_BASE_URL = "http://kuler-api.adobe.com/rss/get.cfm?";
	private static final String KEY_TYPE = "listtype=";
	private static final String KEY_API_KEY = "key=";
	private static final String KEY_START_INDEX = "startIndex=";
	private static final String KEY_ITEM_PER_PAGE = "itemPerPage=";
	private static final String TYPE_RECENT = "recent";
	private static final String TYPE_POPULAR = "popular";
	private static final String TYPE_RATING = "rating";
	private static final String TYPE_RANDOM = "random";
	private static final String AMP = "&";
	private static final int itemPerPage = 20;
	private HttpClient client;
	private String commonUri;

	public InputStream getPopular(int page) throws KulerException {
		StringBuffer uri = new StringBuffer();
		uri.append(this.commonUri);
		uri.append(AMP);
		uri.append(KEY_TYPE);
		uri.append(TYPE_POPULAR);
		uri.append(AMP);
		uri.append(KEY_START_INDEX);
		uri.append((page - 1) * itemPerPage);

		return request(uri.toString());
	}

	private InputStream request(String uri) throws KulerException {
		HttpGet httpGet = new HttpGet(uri.toString());
		try {
			HttpResponse httpResponse = this.client.execute(httpGet);
			return httpResponse.getEntity().getContent();
		} catch (ClientProtocolException e) {
			throw new KulerException(e);
		} catch (IOException e) {
			throw new KulerException(e);
		} catch (RuntimeException e) {
			httpGet.abort();
			throw new KulerException(e);
		}
	}

	public InputStream getRandom(int page) throws KulerException {
		StringBuffer uri = new StringBuffer();
		uri.append(this.commonUri);
		uri.append(AMP);
		uri.append(KEY_TYPE);
		uri.append(TYPE_RANDOM);
		uri.append(AMP);
		uri.append(KEY_START_INDEX);
		uri.append((page - 1) * itemPerPage);

		return request(uri.toString());
	}

	public InputStream getRating(int page) throws KulerException {
		StringBuffer uri = new StringBuffer();
		uri.append(this.commonUri);
		uri.append(AMP);
		uri.append(KEY_TYPE);
		uri.append(TYPE_RATING);
		uri.append(AMP);
		uri.append(KEY_START_INDEX);
		uri.append((page - 1) * itemPerPage);

		return request(uri.toString());
	}

	public InputStream getRecent(int page) throws KulerException {
		StringBuffer uri = new StringBuffer();
		uri.append(this.commonUri);
		uri.append(AMP);
		uri.append(KEY_TYPE);
		uri.append(TYPE_RECENT);
		uri.append(AMP);
		uri.append(KEY_START_INDEX);
		uri.append((page - 1) * itemPerPage);

		return request(uri.toString());
	}

	public void init(String key) {
		this.client = new DefaultHttpClient();
		this.commonUri = KULER_BASE_URL + KEY_API_KEY + key + AMP +
			KEY_ITEM_PER_PAGE + itemPerPage;
	}
}
