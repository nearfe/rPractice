package com.conaxgames.task;

import com.conaxgames.event.PreShutdownEvent;
import com.conaxgames.util.finalutil.BungeeUtil;
import com.conaxgames.util.finalutil.CC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.scheduler.BukkitRunnable;
import com.conaxgames.CorePlugin;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ShutdownTask extends BukkitRunnable {

	private final static List<Integer> BROADCAST_TIMES = Arrays
			.asList(3600, 1800, 900, 600, 300, 180, 120, 60, 45, 30, 15, 10, 5, 4, 3, 2, 1);

	private CorePlugin plugin;

	private int secondsUntilShutdown;

	@Override
	public void run() {
		if (ShutdownTask.BROADCAST_TIMES.contains(secondsUntilShutdown)) {
			this.plugin.getServer().broadcastMessage(CC.PRIMARY + "The server will shutdown in " + CC.SECONDARY
					+ secondsUntilShutdown + CC.PRIMARY + " seconds.");
		}

		if (this.secondsUntilShutdown == 5) {
			// TODO send you to a random hub, don't use lowest player count
			this.plugin.getServer().getOnlinePlayers().forEach(player -> BungeeUtil.sendToServer(player, "hub1"));
		}

		if (this.secondsUntilShutdown <= 0) {
			PreShutdownEvent event = new PreShutdownEvent();
			this.plugin.getServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				return;
			}
			this.plugin.getServer().getOnlinePlayers()
					.forEach(player -> player.sendMessage(CC.RED + "The server has shut down."));
			this.plugin.getServer().shutdown();
		}

		this.secondsUntilShutdown--;
	}
}
