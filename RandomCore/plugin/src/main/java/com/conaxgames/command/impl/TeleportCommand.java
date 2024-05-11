package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.Color;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportCommand implements CommandHandler {

    @Command(name = {"tpall", "teleportall"}, rank = Rank.MANAGER)
    public void teleportAllCommand(Player player) {
        if(CorePlugin.getInstance().getServerManager().getServerName().contains("practice")) {
            player.sendMessage(Color.translate("&cThis commmand cannot be used on practice servers!"));
            return;
        }

        Bukkit.getOnlinePlayers().stream().filter(o -> !o.equals(player)).forEach(o -> o.teleport(player));
        player.sendMessage(Color.translate(CC.SECONDARY + "Teleported all online players to yourself."));
    }

    @Command(name = {"tp", "goto", "tpto"}, rank = Rank.TRAINEE)
    public void teleportCommand(Player player, @Param(name = "player") String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            player.sendMessage(CC.RED + "Invalid player.");
            return;
        }

        player.teleport(target.getLocation());
        player.sendMessage(Color.translate(CC.SECONDARY  + "Teleported you to " + target.getDisplayName() + CC.SECONDARY + "."));
    }

    @Command(name = {"tphere", "s", "tph", "there"}, rank = Rank.MOD)
    public void teleportHereCommand(Player player, @Param(name = "player") String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            player.sendMessage(CC.RED + "Invalid player.");
            return;
        }

        player.sendMessage(Color.translate(CC.SECONDARY + "Teleporting " + target.getDisplayName() + CC.SECONDARY + " to you."));
        target.teleport(player.getLocation());
        target.sendMessage(Color.translate(player.getDisplayName() + CC.SECONDARY + " teleported you."));
    }

    @Command(name = "tppos", rank = Rank.MOD)
    public void teleportPosCommand(Player player, @Param(name = "x") String x, @Param(name = "y") String y, @Param(name = "z") String z) {
        int x1, y1, z1;
        try {
            x1 = Integer.parseInt(x);
            y1 = Integer.parseInt(y);
            z1 = Integer.parseInt(z);
        } catch (Exception e) {
            return;
        }

        player.teleport(new Location(player.getWorld(), x1, y1, z1, 0, 0));
        player.sendMessage(Color.translate(CC.SECONDARY  + "Teleported you to " + CC.PRIMARY + x + ", " + y + ", " + z + CC.SECONDARY + " coordinates."));
    }
}
