package com.conaxgames.command.impl.essentials;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.Color;
import com.conaxgames.util.finalutil.PlayerUtil;

import java.util.Collections;

/**
 * Created by Marko on 29.08.2018.
 */
public class GamemodeCommand extends Command {

    public GamemodeCommand() {
        super("gamemode");

        setAliases(Collections.singletonList("gm"));
        setUsage(Color.translate("&cUsage: /gamemode <c|s> <player>"));
        setDescription("Gamemode commands.");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(!PlayerUtil.testPermission(sender, Rank.ADMIN)) {
            sender.sendMessage(Color.translate("&cNo permission."));
            return false;
        }

        if(args.length == 0) {
            sender.sendMessage(usageMessage);
            return false;
        }

        Player player = (Player) sender;

        if(args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("creative")) {
            if(args.length == 1) {
                if(sender instanceof ConsoleCommandSender) {
                    sender.sendMessage(usageMessage);
                } else {
                    player.setGameMode(GameMode.CREATIVE);
                    player.sendMessage(Color.translate(CC.SECONDARY + "You are now in " + CC.PRIMARY + "CREATIVE" + CC.SECONDARY + " mode."));
                }
                return false;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if(target == null) {
                sender.sendMessage(Color.translate("&cFailed to find that player."));
                return false;
            }

            target.setGameMode(GameMode.CREATIVE);
            target.sendMessage(Color.translate(CC.SECONDARY + "You are now in " + CC.PRIMARY + "CREATIVE" + CC.SECONDARY + " mode."));
            player.sendMessage(Color.translate(CC.PRIMARY + target.getDisplayName() + CC.SECONDARY + " is now in " + CC.PRIMARY + "CREATIVE" + CC.SECONDARY + " mode."));
        } else if(args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("survival")) {
            if(args.length == 1) {
                if(sender instanceof ConsoleCommandSender) {
                    sender.sendMessage(usageMessage);
                } else {
                    player.setGameMode(GameMode.SURVIVAL);
                    player.sendMessage(Color.translate(CC.SECONDARY + "You are now in " + CC.PRIMARY + "SURVIVAL" + CC.SECONDARY + " mode."));
                }
                return false;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if(target == null) {
                sender.sendMessage(Color.translate("&cFailed to find that player."));
                return false;
            }

            target.setGameMode(GameMode.SURVIVAL);
            target.sendMessage(Color.translate(CC.SECONDARY + "You are now in " + CC.PRIMARY + "SURVIVAL" + CC.SECONDARY + " mode."));
            player.sendMessage(Color.translate(CC.PRIMARY + target.getDisplayName() + CC.SECONDARY + " is now in " + CC.PRIMARY + "SURVIVAL" + CC.SECONDARY + " mode."));
        }
        return false;
    }
}