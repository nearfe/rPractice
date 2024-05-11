package com.conaxgames.command.impl.essentials;

import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.Color;
import com.conaxgames.util.finalutil.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

/**
 * Created by Marko on 29.08.2018.
 */
public class SurvivalCommand extends Command {

    public SurvivalCommand() {
        super("survival");

        setAliases(Collections.singletonList("gms"));
        setUsage(Color.translate("&cUsage: /gms <player>"));
        setDescription("Set your gamemode to survival.");
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
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(Color.translate(CC.SECONDARY + "You are now in " + CC.PRIMARY + "SURVIVAL " + CC.SECONDARY + "mode."));
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(target == null) {
            player.sendMessage(Color.translate("&cFailed to find that player."));
            return false;
        }

        target.setGameMode(GameMode.SURVIVAL);
        target.sendMessage(Color.translate(CC.SECONDARY + "You are now in " + CC.PRIMARY + "SURVIVAL " + CC.SECONDARY + "mode."));
        player.sendMessage(Color.translate(CC.PRIMARY + target.getDisplayName() + CC.SECONDARY + " is now in " + CC.PRIMARY + "SURVIVAL " + CC.SECONDARY + "mode."));
        return false;
    }
}