package com.conaxgames.api.impl;

import lombok.RequiredArgsConstructor;
import com.conaxgames.api.request.Request;

import java.util.Map;

@RequiredArgsConstructor
public final class PunishHistoryRequest implements Request {

	private final String name;

	@Override public String getPath() {
		return "/punishments/fetch/" + this.name;
	}

	@Override public Map<String, Object> toMap() {
		return null;
	}

}
