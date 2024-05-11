package com.conaxgames.command;

import com.conaxgames.CorePlugin;
import com.conaxgames.api.impl.DisguiseRequest;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.redis.CoreRedisManager;
import com.conaxgames.util.finalutil.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UndisguiseCommand extends Command {
    private final CorePlugin plugin;

    public UndisguiseCommand(CorePlugin plugin) {
        super("undg");
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        Mineman mineman = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (!mineman.hasRank(Rank.MASTER)) {
            player.sendMessage(CC.RED + "No permission.");
            return true;
        }

        String serverName = plugin.getServerManager().getServerName();
        boolean isGameLobby = serverName.endsWith("lobby") || serverName.equalsIgnoreCase("uhcgames");
        if (isGameLobby && mineman.getDisguiseName() != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(
                        new DisguiseRequest(player.getUniqueId(), false, null, null, null));
                plugin.getCoreRedisManager().sendMessage(
                        CoreRedisManager.getServerMessagePrefix() + mineman.getRank().getColor() +
                                mineman.getDisguiseName() + CC.AQUA + " has undisguised from " +
                                mineman.getDisguiseRank().getColor() + player.getDisplayName() + CC.AQUA + ".", Rank.DEVELOPER);
                mineman.setDisguiseName(null);
                player.sendMessage(CC.GREEN + "You have undisguised.");
            });
        }

        if (!plugin.getDisguiseManager().isDisguised(player)) {
            player.sendMessage(CC.RED + "You're not disguised.");
            return true;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getCoreRedisManager().sendMessage(
                        CoreRedisManager.getServerMessagePrefix() + mineman.getRank().getColor() +
                                mineman.getName() + CC.AQUA + " has undisguised from " +
                                mineman.getDisguiseRank().getColor() + player.getDisplayName() + CC.AQUA + ".", Rank.DEVELOPER);
                plugin.getDisguiseManager().undisguise(player);
                mineman.setDisguiseRank(null);
                player.sendMessage(CC.GREEN + "You have undisguised.");
            }
        }.runTaskAsynchronously(this.plugin);

        return true;
    }
}
