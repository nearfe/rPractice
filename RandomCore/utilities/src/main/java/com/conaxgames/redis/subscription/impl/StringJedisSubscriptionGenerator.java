package com.conaxgames.redis.subscription.impl;

import com.conaxgames.redis.subscription.JedisSubscriptionGenerator;

/**
 * @since 2017-09-02
 */
public class StringJedisSubscriptionGenerator implements JedisSubscriptionGenerator<String> {

	@Override
	public String generateSubscription(String message) {
		return message;
	}
}
