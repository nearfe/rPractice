package com.conaxgames.task;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import com.conaxgames.CorePlugin;

/**
 * @since 8/29/2017
 */
@RequiredArgsConstructor
public class ServerHeartbeatTask extends BukkitRunnable {
	private final CorePlugin plugin;

	@Override
	public void run() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("server-name", this.plugin.getServerManager().getServerName());
		jsonObject.addProperty("player-count", this.plugin.getServer().getOnlinePlayers().size());
		jsonObject.addProperty("player-max", this.plugin.getServer().getMaxPlayers());
		jsonObject.addProperty("whitelisted", this.plugin.getPlayerManager().isWhitelisted());
		jsonObject.addProperty("joinable", this.plugin.getServerManager().isJoinable());
		if(this.plugin.getServerManager().getExtraData() != null) {
			jsonObject.add("extra", this.plugin.getServerManager().getExtraData());
		}

		this.plugin.getServerManager().getServerHeartbeatPublisher().write(jsonObject);
	}
}
