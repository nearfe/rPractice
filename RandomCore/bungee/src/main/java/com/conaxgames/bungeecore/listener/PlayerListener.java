package com.conaxgames.bungeecore.listener;

import com.google.common.collect.Iterables;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import com.conaxgames.bungeecore.CorePlugin;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @since 2017-10-11
 */
@RequiredArgsConstructor
public class PlayerListener implements Listener {

	private final CorePlugin plugin;

	@EventHandler
	public void onPostLogin(PostLoginEvent event) {
		final ProxiedPlayer player = event.getPlayer();

		this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
			List<String> groups = plugin.getDatabase().getUsersGroups(player.getUniqueId());

			if (groups.isEmpty()) {
				return;
			}

			player.addGroups(groups.toArray(new String[0]));
			// Update their username in the database after they are given the groups to not add more delay
			plugin.getDatabase().updateUserName(player.getUniqueId(), player.getName(), false);
		});
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogin(PreLoginEvent event) {
		if (plugin.isMaintenance() && !plugin.getWhitelistedPlayers().contains(event.getConnection().getName())) {
			event.setCancelReason(TextComponent.fromLegacyText(ChatColor.RED + "The network is currently whitelisted.\nFollow our Twitter @ThePvPTemple for more information."));
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onServerConnect(ServerConnectEvent event) {
		if ((event.getTarget().getName().startsWith("hub") || event.getReason() == ServerConnectEvent.Reason.JOIN_PROXY)
				&& event.getReason() != ServerConnectEvent.Reason.COMMAND) {
			ServerInfo hub = plugin.getBestHub(event.getPlayer());
			if (hub != null && hub != event.getTarget()) {
				event.setTarget(hub);
				event.getPlayer().sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "Sending you to " + ChatColor.DARK_AQUA + hub.getName() + ChatColor.AQUA + "..."));
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onServerKick(ServerKickEvent event) {
		if (event.getPlayer().getServer() == null) {
			// Have not connected before, so we cannot do much.
			return;
		}

		if (!event.getPlayer().getServer().getInfo().equals(event.getKickedFrom())) {
			// We aren't even on that server, so ignore it.
			return;
		}

		ServerInfo hub = plugin.getBestHub(event.getPlayer());
		if (hub != null) {
			event.setCancelled(true);
			event.setCancelServer(hub);

			event.getPlayer().sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "Sending you to " + ChatColor.DARK_AQUA + hub.getName() + ChatColor.AQUA + "..."));
		}
	}

	@EventHandler
	public void onReload(ProxyReloadEvent event) {
		this.plugin.fetchWhitelistedPlayers();
		// Run in Async to then we avoid a greload issue
		this.plugin.getProxy().getScheduler().schedule(this.plugin, plugin::fetchServers, 1, TimeUnit.SECONDS);

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onProxyPing(ProxyPingEvent event) {
		if (event.getResponse() == null) {
			return;
		}

		if (this.plugin.isMaintenance()) {
			final ServerPing.Protocol responseProtocol = event.getResponse().getVersion();
			responseProtocol.setName("Whitelisted");
			responseProtocol.setProtocol(-1);
			event.getResponse().setPlayers(new ServerPing.Players(0, 0, new ServerPing.PlayerInfo[] {}));
			event.getResponse().setVersion(responseProtocol);
		}

		event.getResponse().setDescription(plugin.getMotd() == null ? ChatColor.GOLD + ChatColor.BOLD.toString() + "PvPTemple" : plugin.getMotd().replace("%REGION%", plugin.getRegion()).replace("%NEWLINE%", "\n"));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPluginMessage(PluginMessageEvent event) {
		if(event.isCancelled()) {
			return;
		}

		try {
			if(event.getTag().equalsIgnoreCase("Permissions")) {
				DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));

				String channel = in.readUTF();

				if(channel.equals("PermissionsChannel")) {
					ProxiedPlayer player = plugin.getProxy().getPlayer(in.readUTF());
					String permission = in.readUTF();
					String displayName = in.readUTF();

					if(player != null) {
						player.setPermission(permission, true);
						player.setDisplayName(displayName);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
