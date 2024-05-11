package com.conaxgames.api.impl;

import lombok.RequiredArgsConstructor;
import com.conaxgames.api.request.Request;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public final class AddIgnoreRequest implements Request {

	private final UUID uniqueId;
	private final String name;

	@Override public String getPath() {
		return "/player/" + this.uniqueId.toString() + "/ignore/" + this.name;
	}

	@Override public Map<String, Object> toMap() {
		return null;
	}

}
