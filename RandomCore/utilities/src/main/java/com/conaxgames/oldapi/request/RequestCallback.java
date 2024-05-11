package com.conaxgames.oldapi.request;

import org.json.simple.JSONObject;

public interface RequestCallback {

	void callback(JSONObject data);

	void error(String message);

}
