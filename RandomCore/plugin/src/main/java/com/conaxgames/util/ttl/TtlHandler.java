package com.conaxgames.util.ttl;

public interface TtlHandler<E> {

	void onExpire(E element);

	long getTimestamp(E element);

}
