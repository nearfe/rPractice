package com.conaxgames.api.impl;

import lombok.RequiredArgsConstructor;
import com.conaxgames.api.request.Request;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public final class IgnoreRequest implements Request {

	private final UUID uniqueId;

	@Override public String getPath() {
		return "/player/" + this.uniqueId.toString() + "/ignores";
	}

	@Override public Map<String, Object> toMap() {
		return null;
	}

}
