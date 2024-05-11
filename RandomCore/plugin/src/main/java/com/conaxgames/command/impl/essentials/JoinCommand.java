package com.conaxgames.command.impl.essentials;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import com.conaxgames.CorePlugin;
import com.conaxgames.rank.Rank;
import com.conaxgames.server.ServerData;
import com.conaxgames.util.finalutil.BungeeUtil;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.Color;
import com.conaxgames.util.finalutil.PlayerUtil;

import java.util.Comparator;

/**
 * Created by Marko on 04.11.2018.
 */
public class JoinCommand extends Command {

    public JoinCommand() {
        super("join");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(sender instanceof ConsoleCommandSender) {
            sender.sendMessage("no u men");
            return false;
        }

        Player player = (Player) sender;

        if(args.length == 0) {
            player.sendMessage(Color.translate("&cUsage: /join <server>"));
            return false;
        }
        
        if (CorePlugin.getInstance().getServerManager().getServerName().toLowerCase().contains("hub")) {
            player.sendMessage(ChatColor.RED + "You cannot use /join in hubs.");
            return false;
        }

        ServerData data = CorePlugin.getInstance().getServerManager().getServerDataByName(args[0]);

        if(data == null) {
            if(PlayerUtil.testPermission(player, Rank.TRAINEE)) {
                player.sendMessage(Color.translate("&cFailed to find that server."));
            } else {
                sendServers(player);
            }
            return false;
        }

        if(!data.isJoinable()) {
            player.sendMessage(Color.translate("&c" + data.getServerName() + " is not able to be joined."));
            return false;
        }

        if(PlayerUtil.testPermission(player, Rank.TRAINEE)) {
            player.sendMessage(Color.translate(CC.SECONDARY + "Sending you to " + CC.PRIMARY + data.getServerName() + CC.SECONDARY + "..."));
            BungeeUtil.sendToServer(player, data.getServerName());
        } else {
            if(!data.getServerName().startsWith("sw-") && !data.getServerName().startsWith("sg-") && !data.getServerName().startsWith("uhc-") && !data.getServerName().startsWith("uhcm-")) {
                sendServers(player);
                return false;
            }

            player.sendMessage(Color.translate(CC.SECONDARY + "Sending you to " + CC.PRIMARY + data.getServerName() + CC.SECONDARY + "..."));
            BungeeUtil.sendToServer(player, data.getServerName());
        }

        return false;
    }

    private void sendServers(Player player) {
        StringBuilder builder = new StringBuilder();
        CorePlugin.getInstance().getServerManager().getServers().values().stream().filter(server -> server.isJoinable() && (server.getServerName().startsWith("sw-") || server.getServerName().startsWith("sg-") || server.getServerName().startsWith("uhcm-") || server.getServerName().startsWith("uhc-")) && !server.getServerName().equalsIgnoreCase("uhcgames")).sorted(Comparator.comparing(ServerData::getServerName)).forEach(server -> {
            if(builder.length() > 0) {
                builder.append(CC.SECONDARY).append(", ");
            }

            builder.append(CC.PRIMARY).append(server.getServerName());
        });

        player.sendMessage("");
        player.sendMessage(Color.translate(CC.PRIMARY + "Available servers:"));
        player.sendMessage(Color.translate(builder.toString()));
        player.sendMessage("");
    }
}
