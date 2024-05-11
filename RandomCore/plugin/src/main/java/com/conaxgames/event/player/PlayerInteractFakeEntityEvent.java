package com.conaxgames.event.player;

import com.conaxgames.entity.wrapper.PlayerWrapper;
import com.conaxgames.event.PlayerEvent;
import lombok.Getter;
import org.bukkit.entity.Player;

public class PlayerInteractFakeEntityEvent extends PlayerEvent {

	@Getter
	private final PlayerWrapper playerWrapper;

	public PlayerInteractFakeEntityEvent(Player player, PlayerWrapper playerWrapper) {
		super(player);

		this.playerWrapper = playerWrapper;
	}

}
