package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.rank.Rank;
import com.conaxgames.task.ShutdownTask;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.commandTypes.BaseCommand;
import com.conaxgames.util.cmd.annotation.commandTypes.SubCommand;
import com.conaxgames.util.finalutil.CC;
import org.bukkit.command.CommandSender;

public class ShutdownCommand implements CommandHandler {


	@BaseCommand(name = {"shutdown", "restart"}, rank = Rank.MANAGER,
			description = "Schedule the server to shutdown.")
	public void shutdown(CommandSender sender) {
		sender.sendMessage(CC.RED + "Usage: /shutdown <check|cancel|time> [seconds]");
	}

	@SubCommand(baseCommand = "shutdown", name = {"check"})
	public void time(CommandSender sender) {
		if (CorePlugin.getInstance().getShutdownTask() == null) {
			sender.sendMessage(CC.RED + "The server is not scheduled to shut down.");
		} else {
			sender.sendMessage(CC.GREEN + "The server will shutdown in " +
					CorePlugin.getInstance().getShutdownTask().getSecondsUntilShutdown() + " seconds.");
		}
	}

	@SubCommand(baseCommand = "shutdown", name = {"cancel", "stop", "quit", "defuse"})
	public void cancel(CommandSender sender) {
		if (CorePlugin.getInstance().getShutdownTask() == null) {
			sender.sendMessage(CC.RED + "The server is not scheduled to shut down.");
		} else {
			CorePlugin.getInstance().getShutdownTask().cancel();
			CorePlugin.getInstance().setShutdownTask(null);
			sender.sendMessage(CC.RED + "The server shutdown has been canceled.");
		}
	}

	@SubCommand(baseCommand = "shutdown", name = {"time"})
	public void time(CommandSender sender, @Param(name = "seconds") int seconds) {
		if (seconds <= 0) {
			sender.sendMessage(CC.RED + "You must input a number greater than 0!");
			return;
		}

		if (CorePlugin.getInstance().getShutdownTask() == null) {
			CorePlugin.getInstance().setShutdownTask(new ShutdownTask(CorePlugin.getInstance(), seconds));
			CorePlugin.getInstance().getShutdownTask().runTaskTimer(CorePlugin.getInstance(), 20L, 20L);
		} else {
			CorePlugin.getInstance().getShutdownTask().setSecondsUntilShutdown(seconds);
		}

		sender.sendMessage(CC.RED + "The server will shutdown in " + CC.GOLD + seconds + CC.RED + " seconds.");
	}

}
