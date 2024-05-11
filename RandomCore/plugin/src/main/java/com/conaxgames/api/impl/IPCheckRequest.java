package com.conaxgames.api.impl;

import lombok.RequiredArgsConstructor;
import com.conaxgames.api.request.Request;

import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public final class IPCheckRequest implements Request {

	private final InetAddress address;
	private final UUID uniqueId;

	@Override public String getPath() {
		return "/player/" + this.uniqueId.toString() + "/ip-check/" +
		       this.address.getHostAddress();
	}

	@Override public Map<String, Object> toMap() {
		return null;
	}

}
