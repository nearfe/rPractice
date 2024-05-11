package com.conaxgames.command.impl.essentials;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.conaxgames.CorePlugin;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.Color;
import com.conaxgames.util.finalutil.PlayerUtil;

import java.util.stream.Stream;

/**
 * Created by Marko on 25.11.2018.
 */
public class StoreAlertCommand extends Command {

    public StoreAlertCommand() {
        super("storealert");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(!PlayerUtil.testPermission(sender, Rank.MANAGER)) {
            sender.sendMessage(Color.translate("&cNo permission."));
            return false;
        }

        if(args.length == 0) {
            sender.sendMessage(Color.translate("&cUsage: /storealert <message>"));
            return false;
        }

        StringBuilder message = new StringBuilder();
        Stream.of(args).forEach(arg -> message.append(arg).append(" "));
        CorePlugin.getInstance().getCoreRedisManager().broadcastGlobally(Color.translate(message.toString().replace("{nl}", "\n")));
        return false;
    }
}
