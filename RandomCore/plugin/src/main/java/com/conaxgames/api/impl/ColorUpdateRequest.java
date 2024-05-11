package com.conaxgames.api.impl;

import com.conaxgames.util.finalutil.MapUtil;
import lombok.RequiredArgsConstructor;
import com.conaxgames.api.request.Request;

import java.util.Map;

@RequiredArgsConstructor
public final class ColorUpdateRequest implements Request {

	private final String color;
	private final int id;

	@Override public String getPath() {
		return "/player/" + this.id + "/update-color";
	}

	@Override public Map<String, Object> toMap() {
		return MapUtil.of("color", this.color);
	}

		/*@Override
	public Map<String, Object> toMap() {
		return ImmutableMap.of(
				"color", (this.color == null ? "" : this.color),
				"id", this.id
		);
	}*/

}
