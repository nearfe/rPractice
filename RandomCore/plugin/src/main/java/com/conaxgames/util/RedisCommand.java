package com.conaxgames.util;

public interface RedisCommand<T> {

	void execute(T t);
}
