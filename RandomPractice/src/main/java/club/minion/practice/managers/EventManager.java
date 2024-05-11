package club.minion.practice.managers;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import club.minion.practice.Practice;
import com.conaxgames.CorePlugin;
import club.minion.practice.events.EventState;
import club.minion.practice.events.PracticeEvent;
import club.minion.practice.events.oitc.OITCEvent;
import club.minion.practice.events.parkour.ParkourEvent;
import club.minion.practice.events.runner.RunnerEvent;
import club.minion.practice.events.sumo.SumoEvent;
import club.minion.practice.player.PlayerData;
import club.minion.practice.player.PlayerState;
import com.conaxgames.util.CustomLocation;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class EventManager {
	private final Map<Class<? extends PracticeEvent>, PracticeEvent> events = new HashMap<>();
	private HashMap<UUID, PracticeEvent> spectators;

	private final Practice plugin = Practice.getInstance();

	private final World eventWorld;

	@Setter
	private boolean enabled = true;

	@Setter
	private long cooldown;

	public EventManager() {
		Arrays.asList(
				OITCEvent.class,
				ParkourEvent.class,
				SumoEvent.class,
				RunnerEvent.class
		).forEach(this::addEvent);

		boolean newWorld;

		if(Bukkit.getWorld("event") == null) {
			eventWorld = Bukkit.createWorld(new WorldCreator("event"));
			newWorld = true;
		} else {
			eventWorld = Bukkit.getWorld("event");
			newWorld = false;
		}

		cooldown = 0L;
		spectators = new HashMap<>();

		if(eventWorld != null) {

			if(newWorld) {
				Bukkit.getWorlds().add(eventWorld);
			}

			eventWorld.setTime(2000L);
			eventWorld.setGameRuleValue("doDaylightCycle", "false");
			eventWorld.setGameRuleValue("doMobSpawning", "false");
			eventWorld.setStorm(false);
			eventWorld.getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
		}
	}

	public PracticeEvent getByName(String name) {
		return events.values().stream().filter(event -> event.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase())).findFirst().orElse(null);
	}

	public void hostEvent(PracticeEvent event, Player host) {
		event.setState(EventState.WAITING);
		event.setHost(host);
		event.startCountdown();
	}

	private int getLimit(Player player) {
		switch (CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).getDisplayRank()) {
			case OWNER:
			case MANAGER:
			case DEVELOPER: return 150;
			case MASTER: return 50;
		}

		return 30;
	}

	private void addEvent(Class<? extends PracticeEvent> clazz) {
		PracticeEvent event = null;

		try {
			event = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		events.put(clazz, event);
	}

	public boolean isPlaying(Player player, PracticeEvent event) {
		return event.getPlayers().containsKey(player.getUniqueId());
	}

	public void addSpectatorSumo(Player player, PlayerData playerData, SumoEvent event) {
		this.addSpectator(player, playerData, event);

		if(event.getSpawnLocations().size() == 1) {
			player.teleport(event.getSpawnLocations().get(0).toBukkitLocation());
		} else {
			List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
			player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
		}

		for(Player eventPlayer : event.getBukkitPlayers()) {
			player.showPlayer(eventPlayer);
		}

		player.setGameMode(GameMode.ADVENTURE);

		player.setAllowFlight(true);
		player.setFlying(true);
	}

	public void addSpectatorRunner(Player player, PlayerData playerData, RunnerEvent event) {
		this.addSpectator(player, playerData, event);

		List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
		player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());

		for(Player eventPlayer : event.getBukkitPlayers()) {
			player.showPlayer(eventPlayer);
		}

		player.setGameMode(GameMode.ADVENTURE);

		player.setAllowFlight(true);
		player.setFlying(true);
	}

	public void addSpectatorLMS(Player player, PlayerData playerData) {
		if(player.isDead()) {
			player.setHealth(20.0);
		}

		player.setGameMode(GameMode.ADVENTURE);

		player.setAllowFlight(true);
		player.setFlying(true);
	}

	public void addSpectatorParkour(Player player, PlayerData playerData, ParkourEvent event) {
		this.addSpectator(player, playerData, event);

		if(event.getSpawnLocations().size() == 1) {
			player.teleport(event.getSpawnLocations().get(0).toBukkitLocation());
		} else {
			List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
			player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
		}

		for(Player eventPlayer : event.getBukkitPlayers()) {
			player.showPlayer(eventPlayer);
		}

		player.setGameMode(GameMode.ADVENTURE);

		player.setAllowFlight(true);
		player.setFlying(true);
	}

	public void addSpectatorOITC(Player player, PlayerData playerData, OITCEvent event) {
		this.addSpectator(player, playerData, event);

		if(event.getSpawnLocations().size() == 1) {
			player.teleport(event.getSpawnLocations().get(0).toBukkitLocation());
		} else {
			List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
			player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
		}

		for(Player eventPlayer : event.getBukkitPlayers()) {
			player.showPlayer(eventPlayer);
		}

		player.setGameMode(GameMode.ADVENTURE);

		player.setAllowFlight(true);
		player.setFlying(true);
	}

	private void addSpectator(Player player, PlayerData playerData, PracticeEvent event) {
		playerData.setPlayerState(PlayerState.SPECTATING);
		spectators.put(player.getUniqueId(), event);

		player.getInventory().setContents(this.plugin.getItemManager().getSpecItems());
		player.updateInventory();

		Bukkit.getOnlinePlayers().forEach(online -> {
			online.hidePlayer(player);
			player.hidePlayer(online);
		});
	}

	public void removeSpectator(Player player, PracticeEvent eventPlaying) {
		spectators.remove(player.getUniqueId());
		if(plugin.getEventManager().getEventPlaying(player) != null) {
			plugin.getEventManager().getEventPlaying(player).getPlayers().remove(player.getUniqueId());
		}
		plugin.getPlayerManager().sendToSpawnAndReset(player);
	}

	public PracticeEvent getEventPlaying(Player player) {
		return this.events.values().stream().filter(event -> isPlaying(player, event)).findFirst().orElse(null);
	}
}