package com.conaxgames.bungeecore.redis;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import com.conaxgames.bungeecore.CorePlugin;
import com.conaxgames.redis.subscription.JedisSubscriptionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * @since 10/23/2017
 */
@RequiredArgsConstructor
public class BungeeCoreSubscriber implements JedisSubscriptionHandler<JsonObject> {

	private final CorePlugin plugin;

	@Override
	public void handleMessage(JsonObject object) {
		if (object.get("maintenance") != null) {
			boolean newMaintenance = object.get("maintenance").getAsBoolean();

			this.plugin.getLogger().info(object.get("server-id").getAsString() + " has issued maintenance: " + newMaintenance);

			this.plugin.setMaintenance(newMaintenance);
		} else if (object.get("maintenancekick") != null) {
			this.plugin.getLogger().info(object.get("server-id").getAsString() + " has issued maintenance kick");

			for (ProxiedPlayer proxiedPlayer : this.plugin.getProxy().getPlayers()) {
				if (this.plugin.getWhitelistedPlayers().contains(proxiedPlayer.getName())) {
					continue;
				}

				proxiedPlayer.disconnect(new TextComponent(CorePlugin.MAINTENANCE_STRING));
			}
		} else if (object.get("maintenance-list") != null) {
			plugin.setWhitelistedPlayers(new ArrayList<>(Arrays.asList(object.get("maintenance-list").getAsString().split(","))));
			this.plugin.getLogger().info(object.get("server-id").getAsString() + " has issued a maintenance list change: " + plugin.getMotd());
		} else if (object.get("motd") != null) {
			plugin.setMotd(ChatColor.translateAlternateColorCodes('&', object.get("motd").getAsString()));
			this.plugin.getLogger().info(object.get("server-id").getAsString() + " has issued a MOTD change: " + plugin.getMotd());
		}
	}
}

