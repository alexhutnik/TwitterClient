package com.alexhutnik.twitter;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpRetriever {

	private static DefaultHttpClient client = new DefaultHttpClient();

	public static String retrieve(String url) {
		String result = null;
		HttpGet getRequest = new HttpGet(url);

		try {

			HttpResponse getResponse = client.execute(getRequest);
			final int statusCode = getResponse.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				//TODO add exception handling with a Toast
			} else {

				HttpEntity getResponseEntity = getResponse.getEntity();

				if (getResponseEntity != null) {
					result = EntityUtils.toString(getResponseEntity);
				}
			}

		} catch (IOException e) {
			getRequest.abort();
			//TODO Add Exception handling with a Toast
		}

		return result;
	}
}
