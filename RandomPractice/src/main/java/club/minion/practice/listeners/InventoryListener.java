package club.minion.practice.listeners;

import club.minion.practice.Practice;
import club.minion.practice.player.PlayerData;
import club.minion.practice.player.PlayerState;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListener implements Listener {

	private final Practice plugin = Practice.getInstance();

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (!player.getGameMode().equals(GameMode.CREATIVE)) {
			PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
			if (playerData.getPlayerState() == PlayerState.SPAWN) {
				event.setCancelled(true);
			}
		}
	}
}
