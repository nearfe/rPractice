package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandHandler {

	@Command(name = {"vanish", "v"}, rank = Rank.MOD, description = "Vanish instantly.")
	public void vanish(Mineman mineman, @Param(name = "rank", defaultTo = "Trainee") Rank rank) {
		if (CorePlugin.getInstance().getServerManager().getServerName().toLowerCase().startsWith("practice-")) {
			mineman.getPlayer().sendMessage(CC.RED + "You cannot vanish in practice!");
			return;
		}
		if (rank.getPriority() >= mineman.getRank().getPriority()) {
			mineman.getPlayer().sendMessage(CC.RED + "You cannot hide yourself from players with the " +
			                                rank.getColor() + rank.getName() + CC.RED + " rank!");
			return;
		}

		if (mineman.isVanishMode()) {
			mineman.setVanishMode(false);
			Bukkit.getServer().getOnlinePlayers().stream().filter(other
					-> !other.canSee(mineman.getPlayer())).forEach(other -> other.showPlayer(mineman.getPlayer()));
			mineman.getPlayer().sendMessage(CC.GREEN + "You are now visible to all players.");
		} else {
			mineman.setVanishMode(true);
			for (Player other : Bukkit.getOnlinePlayers()) {
				if (mineman.getPlayer() == other) {
					continue;
				}

				Mineman otherMineman = CorePlugin.getInstance().getPlayerManager().getPlayer(other.getUniqueId());
				if (otherMineman.getRank().getPriority() > rank.getPriority()) {
					continue;
				}
				other.hidePlayer(mineman.getPlayer());
			}
			mineman.getPlayer().sendMessage(CC.GREEN + "You are now invisible to players ranked " +
			                                rank.getColor() + rank.getName() + CC.GREEN + " and below.");
		}
	}

}
