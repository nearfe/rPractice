package com.conaxgames.redis.subscription;

/**
 * @since 2017-09-02
 */
public interface JedisSubscriptionHandler<K> {

	void handleMessage(K object);

}
