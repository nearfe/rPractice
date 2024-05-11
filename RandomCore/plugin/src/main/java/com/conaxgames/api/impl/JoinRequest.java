package com.conaxgames.api.impl;

import lombok.RequiredArgsConstructor;
import com.conaxgames.api.request.Request;

import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public final class JoinRequest implements Request {

	private final InetAddress address;
	private final UUID uniqueId;

	private final String name;

	@Override public String getPath() {
		return "/player/" + this.uniqueId.toString() + "/joins/update/" + this.address + "/" + this.name;
	}

	@Override public Map<String, Object> toMap() {
		return null;
	}

}
