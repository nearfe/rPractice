package com.conaxgames.api.impl;

import com.conaxgames.util.finalutil.MapUtil;
import com.conaxgames.util.finalutil.TimeUtil;
import lombok.RequiredArgsConstructor;
import com.conaxgames.api.request.Request;
import com.conaxgames.rank.Rank;

import java.util.Map;

@RequiredArgsConstructor
public abstract class PlayerRequest implements Request {

	private final String path;
	private final String name;

	@Override public String getPath() {
		return "/player/" + this.name + "/" + this.path;
	}

	@Override public Map<String, Object> toMap() {
		return null;
	}

	/**
	 * Wrapper classes for various Player requests.
	 */

	public static final class AltsRequest extends PlayerRequest {

		public AltsRequest(String name) {
			super("alts", name);
		}

	}

	public static final class BanInfoRequest extends PlayerRequest {

		public BanInfoRequest(String name) {
			super("ban-info", name);
		}

	}

	public static final class RankUpdateRequest extends PlayerRequest {

		private final Rank rank;

		private final long duration;
		private final int givenBy;

		public RankUpdateRequest(String name, Rank rank, long duration, int givenBy) {
			super("update-rank", name);
			this.rank = rank;

			this.duration = duration;
			this.givenBy = givenBy;
		}

		@Override public Map<String, Object> toMap() {
			return MapUtil.of(
					"given-by", this.givenBy,
					"rank", this.rank.getName(),
					"start-time", TimeUtil.getCurrentTimestamp(),
					"end-time", this.duration == -1 ? "PERM" : TimeUtil.addDuration(this.duration)
			);
		}
	}

}
