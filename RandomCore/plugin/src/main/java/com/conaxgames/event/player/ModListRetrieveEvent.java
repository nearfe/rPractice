package com.conaxgames.event.player;

import com.conaxgames.event.PlayerEvent;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Map;

@Getter
public class ModListRetrieveEvent extends PlayerEvent {

	private final Map<String, String> mods;

	public ModListRetrieveEvent(Player player, Map<String, String> mods) {
		super(player);

		this.mods = mods;
	}

}
