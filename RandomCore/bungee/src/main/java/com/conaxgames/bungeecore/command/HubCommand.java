package com.conaxgames.bungeecore.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Command;
import com.conaxgames.bungeecore.CorePlugin;

public class HubCommand extends Command {
    private CorePlugin plugin;

    public HubCommand(CorePlugin plugin) {
        super("hub", null, "lobby");
        this.plugin = plugin;
    }

    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "u wot?"));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (player.getServer().getInfo().getName().startsWith("hub")) {
            player.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "You're already in a hub."));
            return;
        }

        ServerInfo hub = plugin.getBestHub(player);
        if (hub != null) {
            player.connect(hub, ServerConnectEvent.Reason.COMMAND);
            player.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "Sending you to " + ChatColor.DARK_AQUA + hub.getName() + ChatColor.AQUA + "..."));
            return;
        }

        player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "There are no available hubs to send you to!"));
    }
}
