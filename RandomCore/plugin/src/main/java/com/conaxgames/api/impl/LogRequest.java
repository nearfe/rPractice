package com.conaxgames.api.impl;

import com.conaxgames.util.finalutil.MapUtil;
import lombok.RequiredArgsConstructor;
import com.conaxgames.api.request.Request;

import java.util.Map;

public final class LogRequest {

	@RequiredArgsConstructor
	public static final class CommandLogRequest implements Request {

		private final String command;
		private final int id;

		@Override public String getPath() {
			return "/log/command/" + this.id;
		}

		@Override public Map<String, Object> toMap() {
			return MapUtil.of("command", this.command);
		}

	}
}
