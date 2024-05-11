package com.conaxgames.command.impl.essentials;

import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.Color;
import com.conaxgames.util.finalutil.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by Marko on 25.11.2018.
 */
public class BroadcastCommand extends Command {

    public BroadcastCommand() {
        super("broadcast");

        setAliases(Arrays.asList("bc", "bcast", "broadc"));
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(!PlayerUtil.testPermission(sender, Rank.SENIORADMIN)) {
            sender.sendMessage(Color.translate("&cNo permission."));
            return false;
        }

        if(args.length == 0) {
            sender.sendMessage(Color.translate("&cUsage: /broadcast <message>"));
            return false;
        }

        StringBuilder message = new StringBuilder();
        Stream.of(args).forEach(arg -> message.append(arg).append(" "));
        Bukkit.broadcastMessage(Color.translate(message.toString().replace("{nl}", "\n")));
        return false;
    }
}
