package club.minion.practice.match;

import club.minion.practice.Practice;
import club.minion.practice.arena.Arena;
import club.minion.practice.arena.StandaloneArena;
import club.minion.practice.inventory.InventorySnapshot;
import club.minion.practice.kit.Kit;
import club.minion.practice.queue.QueueType;
import com.conaxgames.clickable.Clickable;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.*;
import java.util.stream.Stream;

@Setter
public class Match {

	private final Practice plugin = Practice.getInstance();

	@Getter
	private final Map<UUID, InventorySnapshot> snapshots = new HashMap<>();

	@Getter
	private final Set<Entity> entitiesToRemove = new HashSet<>();
	@Getter
	private final Set<BlockState> originalBlockChanges = Sets.newConcurrentHashSet();
	@Getter
	private final Set<Location> placedBlockLocations = Sets.newConcurrentHashSet();
	@Getter
	private static final Set<UUID> spectators = new ConcurrentSet<>();
	@Getter
	private final Set<Integer> runnables = new HashSet<>();

	private final Set<UUID> haveSpectated = new HashSet<>();

	@Getter
	private final List<MatchTeam> teams;

	@Getter
	private Map<UUID, Integer> boxingHits;

	@Getter
	private final UUID matchId = UUID.randomUUID();
	@Getter
	private final QueueType type;
	@Getter
	private long startTime;
	@Getter
	private final Arena arena;
	@Getter
	private final Kit kit;
	@Getter
	private final boolean redrover;

	@Getter
	private StandaloneArena standaloneArena;
	@Getter
	private MatchState matchState = MatchState.STARTING;
	@Getter
	private int winningTeamId;
	@Getter
	private int countdown = 6;

	public Match(Arena arena, Kit kit, QueueType type, MatchTeam... teams) {
		this(arena, kit, type, false, teams);
	}

	public Match(Arena arena, Kit kit, QueueType type, boolean redrover, MatchTeam... teams) {
		this.arena = arena;
		this.kit = kit;
		this.type = type;
		this.redrover = redrover;
		this.teams = Arrays.asList(teams);
		this.boxingHits = Maps.newHashMap();
	}


	public void addSpectator(UUID uuid) {
		this.spectators.add(uuid);
	}

	public void removeSpectator(UUID uuid) {
		this.spectators.remove(uuid);
	}

	public void addHaveSpectated(UUID uuid) {
		this.haveSpectated.add(uuid);
	}

	public boolean haveSpectated(UUID uuid) {
		return this.haveSpectated.contains(uuid);
	}

	public void broadcastMessage(String message) {
		teams.forEach(team -> team.alivePlayers().forEach(player -> player.sendMessage(message)));
		spectatorPlayers().forEach(spectator -> spectator.sendMessage(message));
	}

	public void checkBoxingHits(EntityDamageByEntityEvent event) {
		Player damager = (Player) event.getDamager();
		UUID playerAId = teams.get(0).getPlayers().get(0);
		Player player = (Player) event.getEntity();
		UUID playerBId = teams.get(1).getPlayers().get(0);
		int playerA = boxingHits.getOrDefault(playerAId, 0);
		int playerB = boxingHits.getOrDefault(playerBId, 0);

		if (playerA <= 100) {
			Bukkit.getPlayer(playerBId).setHealth(20);
			event.setDamage(0);
			player.setHealthScale(20.0);
			player.setFoodLevel(20);

		} else if (playerB <= 100) {
			Bukkit.getPlayer(playerAId).setHealth(20);
			event.setDamage(0);
			player.setHealthScale(20.0);
			player.setFoodLevel(20);
		}

		if (playerA == 100) {
			Practice.getInstance().getMatchManager().removeFighter(Bukkit.getPlayer(playerBId), Practice.getInstance().getPlayerManager().getPlayerData(playerBId), true);
		} else if (playerB == 100) {
			Practice.getInstance().getMatchManager().removeFighter(Bukkit.getPlayer(playerAId), Practice.getInstance().getPlayerManager().getPlayerData(playerAId), true);
		}
	}

	public void broadcastMessage(Clickable message) {
		teams.forEach(team -> team.alivePlayers().forEach(message::sendToPlayer));
		spectatorPlayers().forEach(message::sendToPlayer);
	}
	public void addSnapshot(Player player) {
		this.snapshots.put(player.getUniqueId(), new InventorySnapshot(player, this));
	}

	public boolean hasSnapshot(UUID uuid) {
		return this.snapshots.containsKey(uuid);
	}

	public InventorySnapshot getSnapshot(UUID uuid) {
		return this.snapshots.get(uuid);
	}

	public void addEntityToRemove(Entity entity) {
		this.entitiesToRemove.add(entity);
	}

	public void removeEntityToRemove(Entity entity) {
		this.entitiesToRemove.remove(entity);
	}

	public void clearEntitiesToRemove() {
		this.entitiesToRemove.clear();
	}

	public void addRunnable(int id) {
		this.runnables.add(id);
	}

	public void addOriginalBlockChange(BlockState blockState) {
		this.originalBlockChanges.add(blockState);
	}

	public void removeOriginalBlockChange(BlockState blockState) {
		this.originalBlockChanges.remove(blockState);
	}

	public void addPlacedBlockLocation(Location location) {
		this.placedBlockLocations.add(location);
	}

	public void removePlacedBlockLocation(Location location) {
		this.placedBlockLocations.remove(location);
	}

	public void broadcastWithSound(String message, Sound sound) {
		this.teams.forEach(team -> team.alivePlayers().forEach(player -> {
			player.sendMessage(message);
			player.playSound(player.getLocation(), sound, 10, 1);
		}));
		this.spectatorPlayers().forEach(spectator -> {
			spectator.sendMessage(message);
			spectator.playSound(spectator.getLocation(), sound, 10, 1);
		});
	}

	public void broadcast(String message) {
		this.teams.forEach(team -> team.alivePlayers().forEach(player -> player.sendMessage(message)));
		this.spectatorPlayers().forEach(spectator -> spectator.sendMessage(message));
	}

	public void broadcast(Clickable message) {
		this.teams.forEach(team -> team.alivePlayers().forEach(message::sendToPlayer));
		this.spectatorPlayers().forEach(message::sendToPlayer);
	}

	public Stream<Player> spectatorPlayers() {
		return this.spectators.stream().map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull);
	}

	public int decrementCountdown() {
		return --this.countdown;
	}


	public boolean isParty() {
		return this.isFFA() || this.teams.get(0).getPlayers().size() != 1 && this.teams.get(1).getPlayers().size() != 1;
	}

	public boolean isFFA() {
		return this.teams.size() == 1;
	}

}
