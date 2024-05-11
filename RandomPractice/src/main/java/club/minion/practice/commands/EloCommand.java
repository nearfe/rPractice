package club.minion.practice.commands;

import club.minion.practice.leaderboard.LeaderboardsMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class EloCommand extends Command {

	public EloCommand() {
		super("leaderboards");
		this.setAliases(Arrays.asList("stats", "statistics", "elo", "topelo", "leaderboard", "lb"));
		this.setUsage(ChatColor.RED + "Usage: /leaderboards");
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		new LeaderboardsMenu().openMenu((Player) sender);
		return false;
	}
}
