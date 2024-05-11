package com.conaxgames.command.impl.essentials;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.Color;
import com.conaxgames.util.finalutil.PlayerUtil;

/**
 * Created by Marko on 29.08.2018.
 */
public class FeedCommand extends Command {

    public FeedCommand() {
        super("feed");

        setUsage(Color.translate("&cUsage: /feed <player>"));
        setDescription("Feed players.");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if(!PlayerUtil.testPermission(player, Rank.ADMIN)) {
            player.sendMessage(Color.translate("&cNo permission."));
            return false;
        }

        if(args.length == 0) {
            player.setFoodLevel(20);
            player.setSaturation(10);
            player.sendMessage(Color.translate(CC.SECONDARY + "You have fed yourself."));
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(target == null) {
            player.sendMessage(Color.translate("&cFailed to find that player."));
            return false;
        }

        target.setFoodLevel(20);
        target.setSaturation(10);

        target.sendMessage(Color.translate(CC.SECONDARY + "You have been fed by " + CC.PRIMARY + player.getDisplayName() + CC.SECONDARY + "."));
        player.sendMessage(Color.translate(CC.SECONDARY + "You have fed " + CC.PRIMARY + target.getDisplayName() + CC.SECONDARY + "."));
        return false;
    }
}
