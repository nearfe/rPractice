package com.conaxgames.listener;

import com.conaxgames.mineman.Mineman;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.conaxgames.CorePlugin;
import com.conaxgames.event.player.RankChangeEvent;

public class RankListener implements Listener {

	@EventHandler
	public void onRankChange(RankChangeEvent e) {
		Player player = Bukkit.getPlayer(e.getUuid());
		if (player != null) {
			if (CorePlugin.getInstance().getDisguiseManager().isDisguised(e.getUuid())) {
				return;
			}

			Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(e.getUuid());
			if (mineman != null) {
				mineman.updateTabList(e.getTo());
			}
		}
	}

}
