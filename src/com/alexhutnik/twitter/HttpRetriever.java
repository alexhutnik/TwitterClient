package com.alexhutnik.twitter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpRetriever {

	private static DefaultHttpClient client = new DefaultHttpClient();
	private static final String TWITTER_ENDPOINT = "http://search.twitter.com/search.json?q=";

	public static String retrieve(final String term) throws Exception {
		String result = null;
		HttpGet getRequest = new HttpGet(TWITTER_ENDPOINT+term);

		try {

			HttpResponse getResponse = client.execute(getRequest);
			final int statusCode = getResponse.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				throw new Exception();
			} else {

				HttpEntity getResponseEntity = getResponse.getEntity();

				if (getResponseEntity != null) {
					result = EntityUtils.toString(getResponseEntity);
				}
			}

		} catch (Exception e) {
			getRequest.abort();
			throw e;
		}

		return result;
	}
}
