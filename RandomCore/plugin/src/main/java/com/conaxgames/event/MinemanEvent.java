package com.conaxgames.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.conaxgames.mineman.Mineman;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class MinemanEvent extends BaseEvent {

	private final Mineman mineman;

	public UUID getUniqueId() {
		return this.mineman.getUuid();
	}

}