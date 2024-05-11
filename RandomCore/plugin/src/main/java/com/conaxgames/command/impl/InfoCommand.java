package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.Color;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

/**
 * Created by Marko on 28.11.2018.
 */
public class InfoCommand implements CommandHandler {

    @Command(name = {"teamspeak", "ts", "teams", "tspeak"}, rank = Rank.NORMAL)
    public void teamspeak(Player player) {
        player.sendMessage(CC.SECONDARY + "Teamspeak ip is " + CC.PRIMARY + "minemen.club/teamspeak");
    }

    @Command(name = {"help", "?"}, rank = Rank.NORMAL)
    public void help(Player player) {
        Stream.of(CorePlugin.getInstance().getHelpMessage()).forEach(msg ->
                player.sendMessage(Color.translate(msg)));
    }

    @Command(name = {"discord"}, rank = Rank.NORMAL)
    public void discord(Player player) {
        player.sendMessage(CC.SECONDARY + "Discord link is " + CC.PRIMARY + "minemen.club/discord");
    }

    @Command(name = {"store", "shop", "donate"}, rank = Rank.NORMAL)
    public void store(Player player) {
        player.sendMessage(CC.SECONDARY + "Shop link is " + CC.PRIMARY + "minemen.club/ranks");
    }
}
