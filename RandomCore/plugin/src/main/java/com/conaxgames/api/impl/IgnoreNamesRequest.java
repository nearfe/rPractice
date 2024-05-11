package com.conaxgames.api.impl;

import lombok.RequiredArgsConstructor;
import com.conaxgames.api.request.Request;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public final class IgnoreNamesRequest implements Request {

	private final UUID uniqueId;

	@Override public String getPath() {
		return "/player/" + this.uniqueId.toString() + "/ignoring";
	}

	@Override public Map<String, Object> toMap() {
		return null;
	}

}
