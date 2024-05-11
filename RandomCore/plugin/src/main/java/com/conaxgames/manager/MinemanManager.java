package com.conaxgames.manager;

import com.conaxgames.CorePlugin;
import com.conaxgames.mineman.Mineman;
import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.util.*;

@Getter
public class MinemanManager {

	private final Map<UUID, Mineman> players = new HashMap<>();
	private final List<UUID> dummyPlayers = new ArrayList<>();

	@Setter
	private long chatSlowDownTime;

	@Setter
	private boolean chatSilenced;

	private boolean
			donorOnly = CorePlugin.getInstance().getConfig().getBoolean("donor_only"),
			staffOnly = CorePlugin.getInstance().getConfig().getBoolean("staff_only"),
			devOwnerOnly = CorePlugin.getInstance().getConfig().getBoolean("dev_owner_only");

	public Mineman addPlayer(UUID uuid, String name, InetAddress ipAddress) {
		Mineman mineman = new Mineman(uuid, name, ipAddress);

		this.players.put(uuid, mineman);

		return mineman;
	}

	public Mineman getPlayer(UUID uuid) {
		return this.players.get(uuid);
	}

	public void removePlayer(UUID player) {
		this.players.remove(player);
	}

	public boolean isDonorOnly() {
		return CorePlugin.getInstance().getConfig().getBoolean("donor_only");
	}

	public boolean isStaffOnly() {
		return CorePlugin.getInstance().getConfig().getBoolean("staff_only");
	}

	public boolean isDevOwnerOnly() {
		return CorePlugin.getInstance().getConfig().getBoolean("dev_owner_only");
	}

	public void setDonorOnly(boolean value) {
		CorePlugin.getInstance().getConfig().set("donor_only", value);
		CorePlugin.getInstance().saveConfig();
	}

	public void setStaffOnly(boolean value) {
		CorePlugin.getInstance().getConfig().set("staff_only", value);
		CorePlugin.getInstance().saveConfig();
	}

	public void setDevOwnerOnly(boolean value) {
		CorePlugin.getInstance().getConfig().set("dev_owner_only", value);
		CorePlugin.getInstance().saveConfig();
	}

	public boolean isWhitelisted() {
		return isDonorOnly() || isStaffOnly() || isDevOwnerOnly();
	}
}
