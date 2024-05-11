package com.conaxgames.event;

import lombok.Getter;
import com.conaxgames.mineman.Mineman;

import java.util.UUID;

@Getter
public class MinemanPlayerEvent extends PlayerEvent {

	private final Mineman mineman;

	public MinemanPlayerEvent(Mineman mineman) {
		super(mineman.getPlayer());
		this.mineman = mineman;
	}

	public UUID getUniqueId() {
		return this.mineman.getUuid();
	}

}