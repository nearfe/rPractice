package club.minion.practice.task;

import club.minion.practice.Practice;
import club.minion.practice.player.PlayerData;
import club.minion.practice.player.PlayerState;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class RespawnTask extends BukkitRunnable {
    private final Player player;
    private final Practice plugin = Practice.getInstance();

    public RespawnTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        playerData.setPlayerState(PlayerState.SPECTATING);
        player.spigot().respawn();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
    }

    public static void scheduleRespawn(Player player) {
        RespawnTask respawnTask = new RespawnTask(player);
        respawnTask.runTaskLater(Practice.getInstance(), 20L);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location respawnLocation = player.getLocation();
        RespawnTask.scheduleRespawn(player);
    }
}
