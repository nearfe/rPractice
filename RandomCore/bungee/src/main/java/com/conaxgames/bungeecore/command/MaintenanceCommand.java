package com.conaxgames.bungeecore.command;

import com.conaxgames.bungeecore.CorePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class MaintenanceCommand extends Command {
    private CorePlugin plugin;

    public MaintenanceCommand(CorePlugin plugin) {
        super("maintenance", "maintenance.use");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "/maintenance <status/list/toggle/add/remove> [player]"));
            return;
        }

        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            if (args.length == 1) {
                switch (args[0].toLowerCase()) {
                    case "status":
                        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Maintenance mode is currently " + (plugin.isMaintenance() ? ChatColor.RED + "enabled." : ChatColor.GREEN + "disabled.")));
                        return;
                    case "toggle":
                        plugin.setMaintenance(!plugin.isMaintenance());
                        plugin.sendMaintenance(plugin.isMaintenance());
                        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Maintenance mode is now " + (plugin.isMaintenance() ? ChatColor.RED + "enabled." : ChatColor.GREEN + "disabled.")));
                        return;
                    case "list":
                        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Whitelisted players: " + ChatColor.GREEN + String.join(", ", plugin.getWhitelistedPlayers())));
                        return;
                }
            } else if (args.length == 2) {
                switch (args[0].toLowerCase()) {
                    case "add":
                        plugin.getWhitelistedPlayers().add(args[1]);
                        plugin.sendMaintenanceList(plugin.getWhitelistedPlayers());
                        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Whitelisted player " + args[1] + "."));
                        return;
                    case "remove":
                        plugin.getWhitelistedPlayers().remove(args[1]);
                        plugin.sendMaintenanceList(plugin.getWhitelistedPlayers());
                        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Unwhitelisted player " + args[1] + "."));
                        return;
                }
            }

            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "/maintenance <status/list/toggle/add/remove> [player]"));
        });
    }
}
