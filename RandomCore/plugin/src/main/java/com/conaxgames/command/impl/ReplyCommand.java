package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Text;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReplyCommand implements CommandHandler {

	@Command(name = {"reply", "r"}, rank = Rank.NORMAL, description = "Reply to a player's message.")
	public void reply(Player player, @Text(value = "--==--", name = "message") String message) {
		Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
		String target = mineman.getLastConversation();
		if (target != null) {
			if (message.equalsIgnoreCase("--==--") || message.trim().equals("")) {
				player.sendMessage(CC.GREEN + "You are currently messaging " + CC.RESET + (Bukkit.getPlayer(target) != null ? Bukkit.getPlayer(target).getDisplayName() : target) + CC.GREEN + ".");
				return;
			}

			player.performCommand("msg " + target + " " + message);
		} else {
			player.sendMessage(CC.RED + "You are not messaging anyone right now.");
		}
	}

}
