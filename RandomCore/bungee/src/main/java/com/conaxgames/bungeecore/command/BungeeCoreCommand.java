package com.conaxgames.bungeecore.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import com.conaxgames.bungeecore.CorePlugin;

/**
 * @since 10/21/2017
 */
public class BungeeCoreCommand extends Command {

	private final CorePlugin plugin;

	public BungeeCoreCommand(CorePlugin plugin) {
		super("bungeecore", "bungeecore.command");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(TextComponent.fromLegacyText(
					ChatColor.RED + "/bungeecore reload\n" +
					ChatColor.RED + "/bungeecore maintenance\n" +
					ChatColor.RED + "/bungeecore maintenancekick\n" +
					ChatColor.RED + "/bungeecord motd (motd)"
			));
			return;
		}

		if (args[0].equalsIgnoreCase("motd")) {
			if (args.length > 1) {
				StringBuilder motd = new StringBuilder();
				for (String arg : args) {
					if (!arg.equalsIgnoreCase("motd")) {
						motd.append(arg).append(" ");
					}
				}

				if (motd.length() > 0) {
					plugin.sendMotd(ChatColor.translateAlternateColorCodes('&', motd.toString()));
					sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Updated MOTD."));
				}
			} else {
				sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Current MOTD: " + plugin.getMotd()));
			}
			return;
		}

		switch (args[0].toLowerCase()) {
			case "reload":
				this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
					sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + "Reloading servers..."));
					BungeeCoreCommand.this.plugin.fetchServers();
					sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Reloaded servers!"));
				});
				break;
			case "maintenance":
				this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
					boolean opposingMaintenance = !this.plugin.isMaintenance();

					this.plugin.sendMaintenance(opposingMaintenance);
					sender.sendMessage(TextComponent.fromLegacyText(
							opposingMaintenance ? ChatColor.RED + "Maintenance mode is now on."
							                    : ChatColor.GREEN + "Maintenance mode is now off."));
				});
				break;
			case "maintenancekick":
				this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
					this.plugin.sendMaintenanceKick();
					sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + "All players have been kicked " +
					                                                "for maintenance."));
				});
				break;
			default:
				sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "fuck off invalid shit"));
				break;
		}
	}
}
