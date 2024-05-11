package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.redis.CoreRedisManager;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Text;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class HostChatCommand implements CommandHandler {

    @Command(name = {"host", "hc", "hchat", "hostchat"}, rank = Rank.HOST,
            description = "Enter host chat.")
    public void onHostChat(Mineman player, @Text(value = "--==--", name = "message") String message) {
        if(message.equalsIgnoreCase("--==--") || ChatColor.stripColor(message).trim().equals("")) {
            if(player.getChatType() == Mineman.ChatType.HOST) {
                player.setChatType(Mineman.ChatType.NORMAL);
                player.getPlayer().sendMessage(CC.RED + "You have left host chat.");
            } else {
                player.setChatType(Mineman.ChatType.HOST);
                player.getPlayer().sendMessage(CC.GREEN + "You have entered host chat.");
            }
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () ->
                    CoreRedisManager.handleMessage(player, message, Mineman.ChatType.HOST));
        }
    }

}