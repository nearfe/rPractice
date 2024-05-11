package club.minion.practice.events;

import club.minion.practice.Practice;
import club.minion.practice.event.EventStartEvent;
import club.minion.practice.player.PlayerData;
import club.minion.practice.player.PlayerState;
import club.minion.practice.util.PlayerUtil;
import com.conaxgames.util.CustomLocation;
import com.conaxgames.util.finalutil.CC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class PracticeEvent<K extends EventPlayer> {
	private final Practice plugin = Practice.getInstance();

	private final String name;

	private Player host;
	private EventState state = EventState.UNANNOUNCED;


    public void startCountdown() {
		// Restart Logic
		if (getCountdownTask().isEnded()) {
			getCountdownTask().setTimeUntilStart(getCountdownTask().getCountdownTime());
			getCountdownTask().setEnded(false);
		} else {
			getCountdownTask().runTaskTimer(plugin, 20L, 20L);
		}
	}

	public void sendMessage(String message) {
		getBukkitPlayers().forEach(player -> player.sendMessage(message));
	}

	public Set<Player> getBukkitPlayers() {
		return getPlayers().keySet().stream()
				.filter(uuid -> plugin.getServer().getPlayer(uuid) != null)
				.map(plugin.getServer()::getPlayer)
				.collect(Collectors.toSet());
	}

	public void join(Player player) {
		PlayerData playerData = plugin.getPlayerManager().getPlayerData(player.getUniqueId());
		playerData.setPlayerState(PlayerState.EVENT);

		PlayerUtil.clearPlayer(player);

		if (onJoin() != null) {
			onJoin().accept(player);
		}

		getBukkitPlayers().forEach(other -> other.showPlayer(player));
		getBukkitPlayers().forEach(other -> player.showPlayer(other));

		player.sendMessage(CC.SECONDARY + "You are now playing " + CC.PRIMARY + name + CC.SECONDARY + ".");
	}

	public void leave(Player player, boolean disconnect) {
		getPlayers().remove(player.getUniqueId());

		if (!disconnect) {
			plugin.getPlayerManager().sendToSpawnAndReset(player);

			if (onDeath() != null) {
				onDeath().accept(player);
			}
		}
	}

	public void start() {
		new EventStartEvent(this).call();

		setState(EventState.STARTED);

		onStart();
	}

	public void end() {
		plugin.getEventManager().getEventWorld().getPlayers().forEach(player -> plugin.getPlayerManager().sendToSpawnAndReset(player));

		getPlayers().clear();

		setState(EventState.UNANNOUNCED);
	}

	public K getPlayer(Player player) {
		return getPlayer(player.getUniqueId());
	}

	public K getPlayer(UUID uuid) {
		return getPlayers().get(uuid);
	}
	public void handleWin(Player winner) {
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(winner.getDisplayName() + CC.SECONDARY + " has won the " + CC.PRIMARY + name + CC.SECONDARY + " event!");
		Bukkit.broadcastMessage("");
	}


	public abstract Map<UUID, K> getPlayers();

	public abstract EventCountdownTask getCountdownTask();

	public abstract List<CustomLocation> getSpawnLocations();

	public abstract void onStart();

	public abstract Consumer<Player> onJoin();

	public abstract Consumer<Player> onDeath();
}
