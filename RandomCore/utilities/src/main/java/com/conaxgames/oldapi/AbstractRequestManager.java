package com.conaxgames.oldapi;

import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.conaxgames.oldapi.request.RequestCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractRequestManager {

	private final String apiUrl;
	private final String apiKey;

	public abstract boolean shouldSend();

	public abstract void runTask(Runnable runnable);

	public abstract void runTaskAsynchronously(Runnable runnable);

	public void sendRequest(APIMessage message, RequestCallback callback) {
		this.sendRequest(message, callback, true);
	}

	public JSONObject sendRequestNow(APIMessage message) {
		if (!this.shouldSend()) {
			try {
				throw new Exception("Request was sent on the main thread");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		Map<String, Object> encoded = new HashMap<>();
		encoded.put("message", message.getChannel());
		encoded.put("api-key", this.apiKey);
		encoded.putAll(message.toMap());

		HttpClient httpclient = HttpClients.createDefault();
		List<NameValuePair> params = new ArrayList<>(2);
		for (String key : encoded.keySet()) {
			Object value = encoded.get(key);
			params.add(new BasicNameValuePair(key, value == null ? null : value.toString()));
		}

		try {
			HttpGet httpGet = new HttpGet("http://" + this.apiUrl + "/api?" + URLEncodedUtils.format(params, "utf-8"));
			HttpResponse response;
			try {
				response = httpclient.execute(httpGet);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			StatusLine statusLine = response.getStatusLine();
			if (statusLine != null) {
				int code = statusLine.getStatusCode();
				if (code != 200) {
					return null;
				}
			}

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()))) {
					JSONParser parser = new JSONParser();
					return (JSONObject) parser.parse(reader);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Sends a request to our Web API and handles the response
	 *
	 * @param message  - Message to send to the API
	 * @param callback - Callback to handle the response and any errors
	 */
	public void sendRequest(APIMessage message, RequestCallback callback, boolean async) {
		Map<String, Object> encoded = new HashMap<>();
		encoded.put("message", message.getChannel());
		encoded.put("api-key", this.apiKey);
		encoded.putAll(message.toMap());

		// Run the request async/sync then use the callback system
		if (async) {
			this.runTaskAsynchronously(() -> this.handleRequest(callback, encoded));
		} else {
			this.handleRequest(callback, encoded);
		}
	}

	private void handleRequest(RequestCallback callback, Map<String, Object> encoded) {
		HttpClient httpclient = HttpClients.createDefault();
		List<NameValuePair> params = new ArrayList<>(2);
		for (String key : encoded.keySet()) {
			Object value = encoded.get(key);
			params.add(new BasicNameValuePair(key, value == null ? null : value.toString()));
		}

		try {
			HttpGet httpGet = new HttpGet("http://" + this.apiUrl + "/api?" + URLEncodedUtils.format(params, "utf-8"));
			HttpResponse response;
			try {
				response = httpclient.execute(httpGet);
			} catch (Exception e) {
				e.printStackTrace();
				this.runTask(() -> callback
						.error("Error connecting to " + httpGet.getURI().getHost() + " : " + e.getMessage()));
				return;
			}

			StatusLine statusLine = response.getStatusLine();
			if (statusLine != null) {
				int code = statusLine.getStatusCode();
				if (code != 200) {
					this.runTask(() -> callback.error("Request error code " + code));
					return;
				}
			}

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()))) {
					JSONParser parser = new JSONParser();
					JSONObject jsonObject = (JSONObject) parser.parse(reader);
					this.runTask(() -> callback.callback(jsonObject));
				} catch (ParseException e) {
					e.printStackTrace();
					this.runTask(() -> callback.error("ParseException: " + e.getMessage()));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			this.runTask(() -> callback.error("IOException: " + e.getMessage()));
		}
	}

}
