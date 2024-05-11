package com.conaxgames.task;

import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import com.conaxgames.CorePlugin;
import com.conaxgames.server.ServerData;

/**
 * @since 8/29/2017
 */
@RequiredArgsConstructor
public class ServerHeartbeatTimeoutTask extends BukkitRunnable {

	private static final long TIME_OUT_DELAY = 15_000L;
	private final CorePlugin plugin;

	@Override
	public void run() {
		for (String serverName : this.plugin.getServerManager().getServers().keySet()) {
			ServerData serverData = this.plugin.getServerManager().getServers().get(serverName);

			if (serverData != null) {
				if (System.currentTimeMillis() - serverData.getLastUpdate() >= ServerHeartbeatTimeoutTask.TIME_OUT_DELAY) {
					this.plugin.getServerManager().getServers().remove(serverName);
					this.plugin.getLogger().warning("The server \"" + serverName + "\" was removed due to it exceeding the timeout delay for heartbeats.");
				}
			}
		}
	}
}
