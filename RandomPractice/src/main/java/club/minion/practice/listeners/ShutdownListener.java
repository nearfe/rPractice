package club.minion.practice.listeners;

import club.minion.practice.Practice;
import club.minion.practice.match.Match;
import club.minion.practice.player.PracticePlayerData;
import com.conaxgames.event.PreShutdownEvent;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ShutdownListener implements Listener {

	private final Practice plugin = Practice.getInstance();

	@EventHandler
	public void onPreShutdown(PreShutdownEvent event) {
		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
			for (PracticePlayerData playerData : ShutdownListener.this.plugin.getPlayerManager().getAllData()) {
				ShutdownListener.this.plugin.getPlayerManager().saveData(playerData);
			}
		});

		for (Match match : this.plugin.getMatchManager().getMatches().values()) {
			match.getPlacedBlockLocations().forEach(location -> location.getBlock().setType(Material.AIR));
			match.getOriginalBlockChanges().forEach((blockState) -> blockState.getLocation().getBlock().setType(blockState.getType()));
			match.getEntitiesToRemove().forEach(Entity::remove);
		}
	}
}
