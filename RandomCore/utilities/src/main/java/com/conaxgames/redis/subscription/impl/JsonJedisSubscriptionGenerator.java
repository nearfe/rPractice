package com.conaxgames.redis.subscription.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.conaxgames.redis.subscription.JedisSubscriptionGenerator;

import java.io.StringReader;

/**
 * @since 2017-09-02
 */
public class JsonJedisSubscriptionGenerator implements JedisSubscriptionGenerator<JsonObject> {

	@Override
	public JsonObject generateSubscription(String message) {
		try {
			JsonReader jsonReader = new JsonReader(new StringReader(message));
			jsonReader.setLenient(true);
			return new JsonParser().parse(jsonReader).getAsJsonObject();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new JsonObject();
	}
}
