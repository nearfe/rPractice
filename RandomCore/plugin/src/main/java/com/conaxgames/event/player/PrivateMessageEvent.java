package com.conaxgames.event.player;

import com.conaxgames.event.CancellableEvent;
import com.conaxgames.mineman.Mineman;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PrivateMessageEvent extends CancellableEvent {

	private final Mineman from;
	private final Mineman to;
	@Setter
	private String fromDisplayName;
	@Setter
	private String toDisplayName;

	public PrivateMessageEvent(Mineman from, Mineman to, String fromDisplayName, String toDisplayName) {
		this.from = from;
		this.to = to;
		this.fromDisplayName = fromDisplayName;
		this.toDisplayName = toDisplayName;
	}

}
