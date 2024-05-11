package com.conaxgames.entity;

import org.bukkit.entity.Player;
import com.conaxgames.event.player.PlayerInteractFakeEntityEvent;

/**
 * @since 2017-09-18
 */
public interface EntityInteraction {

	boolean interact(Player player, PlayerInteractFakeEntityEvent event);
}
