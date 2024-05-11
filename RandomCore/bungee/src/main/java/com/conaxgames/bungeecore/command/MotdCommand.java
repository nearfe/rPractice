package com.conaxgames.bungeecore.command;

import com.conaxgames.bungeecore.CorePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class MotdCommand extends Command {
    private CorePlugin plugin;

    public MotdCommand(CorePlugin plugin) {
        super("motd", "motd.use");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            return;
        }

        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            StringBuilder motd = new StringBuilder();
            for (String arg : args) {
                motd.append(arg).append(" ");
            }
            plugin.setMotd(ChatColor.translateAlternateColorCodes('&', motd.toString()));
            plugin.sendMotd(plugin.getMotd());
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Set motd to " + plugin.getMotd() + "."));
        });
    }
}
