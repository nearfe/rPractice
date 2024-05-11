package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.stream.IntStream;

public class ClearChatCommand implements CommandHandler {

	@Command(name = {"clearchat", "cc"}, rank = Rank.TRAINEE, description = "Clear the chat.")
	public void clear(Player unused) {
		Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
			StringBuilder builder = new StringBuilder();

			IntStream.range(0, 100).forEach(i -> builder.append("\n"));

			String message = builder.toString();

			Bukkit.getOnlinePlayers().forEach(player -> {
				Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());

				if(mineman.getRank().isAbove(Rank.TRAINEE)) {
					mineman.getPlayer().sendMessage(CC.SECONDARY + "Chat has been cleared.");
				} else {
					mineman.getPlayer().sendMessage(message);
				}
			});
		});
	}

}
