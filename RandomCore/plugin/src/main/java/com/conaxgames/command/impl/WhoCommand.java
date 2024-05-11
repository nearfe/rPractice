package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.PlayerList;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WhoCommand implements CommandHandler {

    @Command(name = {"who", "list"}, rank = Rank.NORMAL, description = "View all online players.")
    public void who(CommandSender sender) {
        StringBuilder builder = new StringBuilder();

        Rank[] ranks = Arrays.copyOf(Rank.RANKS, Rank.RANKS.length);
        ArrayUtils.reverse(ranks);

        Arrays.stream(ranks).forEach(rank
                -> builder.append(rank.getColor()).append(rank.getName()).append(CC.WHITE).append(", "));

        builder.setCharAt(builder.length() - 2, '.');

        builder.append("\n");

        List<String> players = new PlayerList(CorePlugin.getInstance().getPlayerManager().getPlayers()
                .keySet().stream().filter(uuid -> Bukkit.getPlayer(uuid) != null)
                .map(CorePlugin.getInstance().getServer()::getPlayer).collect(Collectors.toList()))
                .visibleRankSorted().asColoredNames();

        builder.append(CC.R).append("(").append(Bukkit.getOnlinePlayers().size()).append("/")
                .append(CorePlugin.getInstance().getServer().getMaxPlayers()).append("): ")
                .append(players.toString().replace("[", "").replace("]", ""));

        sender.sendMessage(builder.toString());

        /*if (PlayerUtil.testPermission(sender, Rank.DEVELOPER)) {
            List<UUID> nullPlayers = CorePlugin.getInstance().getPlayerManager().getPlayers()
                    .keySet().stream().filter(uuid -> Bukkit.getPlayer(uuid) == null).collect(Collectors.toList());
            sender.sendMessage(CC.I_GRAY + "(dev only) " + CC.R + Joiner.on(", ").join(nullPlayers));
        }*/
    }

}
