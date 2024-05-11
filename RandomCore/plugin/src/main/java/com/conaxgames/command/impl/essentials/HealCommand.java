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
public class HealCommand extends Command {

    public HealCommand() {
        super("heal");

        setUsage(Color.translate("&cUsage: /heal <player>"));
        setDescription("Heal players.");
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
            player.setHealth(20.0);
            player.sendMessage(Color.translate(CC.SECONDARY + "You have healed yourself."));
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(target == null) {
            player.sendMessage(Color.translate("&cFailed to find that player."));
            return false;
        }

        target.setHealth(20.0);
        target.sendMessage(Color.translate(CC.SECONDARY + "You have been healed by " + CC.PRIMARY + player.getDisplayName() + CC.SECONDARY + "."));
        player.sendMessage(Color.translate(CC.SECONDARY + "You have healed " + CC.PRIMARY + target.getDisplayName() + CC.SECONDARY + "."));
        return false;
    }
}
