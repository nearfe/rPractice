package com.conaxgames.event.player;

import com.conaxgames.event.MinemanEvent;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.util.BanWrapper;
import lombok.Getter;

/**
 * Called when all the data for a Mineman is gathered from the DB.
 */
public class MinemanRetrieveEvent extends MinemanEvent {

	@Getter
	private final BanWrapper banWrapper;

	public MinemanRetrieveEvent(Mineman mineman, BanWrapper banWrapper) {
		super(mineman);
		this.banWrapper = banWrapper;
	}

}
