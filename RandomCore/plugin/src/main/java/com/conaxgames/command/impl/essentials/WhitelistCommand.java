package com.conaxgames.command.impl.essentials;

import com.conaxgames.CorePlugin;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.Color;
import com.conaxgames.util.finalutil.PlayerUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

/**
 * Created by Marko on 04.01.2019.
 */
public class WhitelistCommand extends Command {

    private CorePlugin plugin = CorePlugin.getInstance();

    public WhitelistCommand() {
        super("whitelist");

        setAliases(Collections.singletonList("wl"));
        setUsage(Color.translate("&cUsage: /whitelist <on(only devs and owners)|off(disable whitelist for all)|donor(only donors)|staff(only staff)>"));
        setDescription("Set your gamemode to survival.");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if(!PlayerUtil.testPermission(player, Rank.MANAGER)) {
            player.sendMessage(Color.translate("&cNo permission."));
            return false;
        }

        if(args.length == 0) {
            player.sendMessage(usageMessage);
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "on":
                plugin.getPlayerManager().setDevOwnerOnly(!plugin.getPlayerManager().isDevOwnerOnly());
                player.sendMessage(CC.SECONDARY + "You have " + CC.PRIMARY
                        + (plugin.getPlayerManager().isDevOwnerOnly()
                        ? "enabled" : "disabled") + CC.SECONDARY
                        + " whitelist. " + CC.GRAY + "(Only devs and owners)");
                break;
            case "off":
                plugin.getPlayerManager().setDevOwnerOnly(false);
                plugin.getPlayerManager().setStaffOnly(false);
                plugin.getPlayerManager().setDonorOnly(false);

                player.sendMessage(CC.SECONDARY + "You have " + CC.PRIMARY
                        + "disabled" + CC.SECONDARY
                        + " whitelist. " + CC.GRAY + "(For all)");
                break;
            case "donor":
                plugin.getPlayerManager().setDonorOnly(!plugin.getPlayerManager().isDonorOnly());
                player.sendMessage(CC.SECONDARY + "You have " + CC.PRIMARY
                        + (plugin.getPlayerManager().isDonorOnly()
                        ? "enabled" : "disabled") + CC.SECONDARY
                        + " whitelist. " + CC.GRAY + "(Only donors)");
                break;
            case "staff":
                plugin.getPlayerManager().setStaffOnly(!plugin.getPlayerManager().isStaffOnly());
                player.sendMessage(CC.SECONDARY + "You have " + CC.PRIMARY
                        + (plugin.getPlayerManager().isStaffOnly()
                        ? "enabled" : "disabled") + CC.SECONDARY
                        + " whitelist. " + CC.GRAY + "(Only staff)");
                break;
            default:
                player.sendMessage(usageMessage);
                break;

        }

        return false;
    }
}
