package com.conaxgames.api.impl;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import com.conaxgames.api.request.Request;

import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public final class GlobalRequest implements Request {

	private final InetAddress address;
	private final UUID uniqueId;

	private final String name;

	@Override public String getPath() {
		return "/player/" + this.uniqueId.toString() + "/global";
	}

	@Override public Map<String, Object> toMap() {
		return ImmutableMap.of(
				"name", this.name,
				"ip", this.address.getHostAddress()
		);
	}
}
