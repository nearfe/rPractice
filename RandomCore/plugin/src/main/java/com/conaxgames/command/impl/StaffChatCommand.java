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

public class StaffChatCommand implements CommandHandler {

	private static final String DEFAULT_BLANK = " §§ ";

	@Command(name = {"staffchat", "sc"}, rank = Rank.TRAINEE, description = "Enter staff chat.")
	public void staffChat(Mineman player, @Text(name = "message", value = DEFAULT_BLANK) String message) {
		if (message.equalsIgnoreCase(StaffChatCommand.DEFAULT_BLANK) || ChatColor.stripColor(message).trim().equals("")) {
			if (player.getChatType() == Mineman.ChatType.STAFF) {
				player.setChatType(Mineman.ChatType.NORMAL);
				player.getPlayer().sendMessage(CC.RED + "You have left staff chat.");
			} else {
				player.setChatType(Mineman.ChatType.STAFF);
				player.getPlayer().sendMessage(CC.GREEN + "You have entered staff chat");
			}
		} else {
			Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () ->
					CoreRedisManager.handleMessage(player, message, Mineman.ChatType.STAFF));
		}
	}

}
