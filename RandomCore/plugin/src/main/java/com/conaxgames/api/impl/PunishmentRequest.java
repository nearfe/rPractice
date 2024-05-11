package com.conaxgames.api.impl;

import com.conaxgames.util.finalutil.MapUtil;
import lombok.RequiredArgsConstructor;
import com.conaxgames.api.request.Request;

import java.sql.Timestamp;
import java.util.Map;

@RequiredArgsConstructor
public final class PunishmentRequest implements Request {

	private final Timestamp expiry;
	private final String ipAddress;
	private final String reason;
	private final String name;
	private final String type;

	private final int playerId;
	private final int id;

	@Override public String getPath() {
		return "/punishments/punish";
	}

	@Override public Map<String, Object> toMap() {
		return MapUtil.of(
				this.name != null ? "name" : "player-id", this.name != null ? this.name : this.playerId,
				"ip-address", this.ipAddress == null ? "UNKNOWN" : this.ipAddress,
				"expiry", this.expiry == null ? "PERM" : this.expiry,
				"reason", this.reason,
				"punisher", this.id,
				"type", this.type
		);
	}

}
