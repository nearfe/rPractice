package com.conaxgames.command.impl.essentials;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.Color;
import com.conaxgames.util.finalutil.PlayerUtil;

import java.util.Arrays;

/**
 * Created by Marko on 25.11.2018.
 */
public class ClearCommand extends Command {

    public ClearCommand() {
        super("clear");

        setAliases(Arrays.asList("ci", "clearinventory", "cleari", "cinventory"));
        setUsage(Color.translate("&cUsage: /clear <player>"));
        setDescription("Clear inventories of players.");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if(!PlayerUtil.testPermission(player, Rank.MOD)) {
            player.sendMessage(Color.translate("&cNo permission."));
            return false;
        }

        if(args.length == 0) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.updateInventory();
            player.sendMessage(Color.translate(CC.SECONDARY + "You have cleared your inventory."));
            return false;
        }

        if(!PlayerUtil.testPermission(player, Rank.ADMIN)) {
            player.sendMessage(Color.translate("&cNo permission."));
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(target == null) {
            player.sendMessage(Color.translate("&cFailed to find that player."));
            return false;
        }

        target.getInventory().clear();
        target.getInventory().setArmorContents(null);
        target.updateInventory();

        target.sendMessage(CC.SECONDARY + "Your inventory was cleared by " + CC.PRIMARY + player.getName() + CC.SECONDARY + ".");
        player.sendMessage(CC.SECONDARY + "You have cleared inventory of " + CC.PRIMARY + target.getName() + CC.SECONDARY + ".");
        return false;
    }
}
