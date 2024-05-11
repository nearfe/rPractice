package com.conaxgames.command;

import com.conaxgames.CorePlugin;
import com.conaxgames.disguise.DisguiseManager;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.haoshoku.nick.api.NickAPI;

/**
 * SKIDADO POR LUGAMI TLGD
 */
public class DisguiseCommand extends BaseCommand {
    private final CorePlugin plugin;

    public DisguiseCommand(CorePlugin plugin) {
        super("disguise");
        this.plugin = plugin;
        setUsage(ChatColor.translateAlternateColorCodes('&',"&cUsage: /disguise <nick>"));
    }

    @Override
    public boolean onExecute(CommandSender sender, String var2, String[] args) throws Exception {
        Player player = (Player) sender;
        if (args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cUsage: /disguise <nick>"));
            return false;
        } else if (args.length == 1) {
            if (NickAPI.nickExists(args[0])) {
                player.sendMessage(CC.RED + "That name is already used.");
                return false;
            }

            plugin.getDisguiseManager().disguise(player, Rank.NORMAL, args[0], args[0]);

            Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> {
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    player1.hidePlayer(player);
                    player1.showPlayer(player);
                }
            });
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&aYou are now disguised as " + args[0]));
        } else if (args.length == 2) {
            if (NickAPI.nickExists(args[0])) {
                player.sendMessage(CC.RED + "That name is already used.");
                return false;
            }

            plugin.getDisguiseManager().disguise(player, Rank.NORMAL, args[1], args[0]);

            Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> {
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    player1.hidePlayer(player);
                    player1.showPlayer(player);
                }
            });
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&aYou are now disguised as " + args[0] + " and the skin of" + args[1]));
        }
        return false;
    }
}
