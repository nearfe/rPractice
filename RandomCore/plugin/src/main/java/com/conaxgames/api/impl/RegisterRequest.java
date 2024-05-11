package com.conaxgames.api.impl;

import com.conaxgames.util.finalutil.MapUtil;
import lombok.RequiredArgsConstructor;
import com.conaxgames.api.request.Request;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class RegisterRequest implements Request {

	private final String path;

	@Override public String getPath() {
		return "/player/confirmation/" + this.path;
	}

	@Override public Map<String, Object> toMap() {
		return null;
	}

	public static final class InsertRequest extends RegisterRequest {

		private final UUID uuid;
		private final String confirmationId;
		private final String emailAddress;

		public InsertRequest(UUID uuid, String confirmationId, String emailAddress) {
			super("insert/" + uuid.toString() + "/" + confirmationId + "?email=" + emailAddress);

			this.uuid = uuid;
			this.confirmationId = confirmationId;
			this.emailAddress = emailAddress;
		}

		@Override public Map<String, Object> toMap() {
			return MapUtil.of(
					"uuid", this.uuid,
					"confirmationId", this.confirmationId,
					"emailAddress", this.emailAddress
			);
		}
	}

}
