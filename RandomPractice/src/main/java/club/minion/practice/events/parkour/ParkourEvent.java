package club.minion.practice.events.parkour;

import club.minion.practice.events.EventCountdownTask;
import club.minion.practice.events.PracticeEvent;
import com.conaxgames.util.CustomLocation;
import com.conaxgames.util.ItemBuilder;
import club.minion.practice.util.PlayerUtil;
import com.conaxgames.util.finalutil.CC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ParkourEvent extends PracticeEvent<ParkourPlayer> {

	private final Map<UUID, ParkourPlayer> players = new HashMap<>();

	@Getter
	private ParkourGameTask gameTask = null;
	private final ParkourCountdownTask countdownTask = new ParkourCountdownTask(this);
	@Getter
	private WaterCheckTask waterCheckTask;
	private List<UUID> visibility;

	public ParkourEvent() {
		super("Parkour");
	}

	@Override
	public Map<UUID, ParkourPlayer> getPlayers() {
		return players;
	}

	@Override
	public EventCountdownTask getCountdownTask() {
		return countdownTask;
	}

	@Override
	public List<CustomLocation> getSpawnLocations() {
		return Collections.singletonList(this.getPlugin().getSpawnManager().getParkourLocation());
	}

	@Override
	public void onStart() {
		this.gameTask = new ParkourGameTask();
		this.gameTask.runTaskTimerAsynchronously(getPlugin(), 0, 20L);
		this.waterCheckTask = new WaterCheckTask();
		this.waterCheckTask.runTaskTimer(getPlugin(), 0, 10L);
		this.visibility = new ArrayList<>();
	}

	@Override
	public Consumer<Player> onJoin() {
		return player -> players.put(player.getUniqueId(), new ParkourPlayer(player.getUniqueId(), this));
	}

	@Override
	public Consumer<Player> onDeath() {
		return player -> sendMessage(CC.DARK_GRAY + "[" + CC.PRIMARY + "Event" + CC.DARK_GRAY + "] " + player.getDisplayName() + CC.SECONDARY + " has left the game.");
	}

	public void toggleVisibility(Player player) {
		if(visibility.contains(player.getUniqueId())) {
			getBukkitPlayers().forEach(player::showPlayer);
			visibility.remove(player.getUniqueId());
			player.sendMessage(CC.SECONDARY + "You are now " + CC.PRIMARY + "able" + " to see players.");
			return;
		}

		getBukkitPlayers().forEach(player::hidePlayer);
		visibility.add(player.getUniqueId());
		player.sendMessage(CC.SECONDARY + "You are now " + CC.PRIMARY + "unable" + " to see players.");

	}

	private void teleportToSpawnOrCheckpoint(Player player) {
		ParkourPlayer parkourPlayer = this.getPlayer(player.getUniqueId());

		if(parkourPlayer != null && parkourPlayer.getLastCheckpoint() != null) {
			player.teleport(parkourPlayer.getLastCheckpoint().toBukkitLocation());
			player.sendMessage(CC.DARK_GRAY + "[" + CC.PRIMARY + "Event" + CC.DARK_GRAY + "] " + CC.SECONDARY + "Teleporting back to last checkpoint.");
			return;
		}

		player.sendMessage(CC.DARK_GRAY + "[" + CC.PRIMARY + "Event" + CC.DARK_GRAY + "] " + CC.SECONDARY + "Teleporting back to the beginning.");
		player.teleport(this.getPlugin().getSpawnManager().getParkourGameLocation().toBukkitLocation());
	}

	private void giveItems(Player player) {
		this.getPlugin().getServer().getScheduler().runTask(this.getPlugin(), () -> {
			PlayerUtil.clearPlayer(player);
			player.getInventory().setItem(0, new ItemBuilder(Material.EYE_OF_ENDER).name("&aToggle Visibility").build());
			player.getInventory().setItem(4, new ItemBuilder(Material.NETHER_STAR).name("&cLeave Event").build());
			player.updateInventory();
		});
	}

	private Player getRandomPlayer() {
		if(getByState(ParkourPlayer.ParkourState.INGAME).size() == 0) {
			return null;
		}

		List<UUID> fighting = getByState(ParkourPlayer.ParkourState.INGAME);

		Collections.shuffle(fighting);

		UUID uuid = fighting.get(ThreadLocalRandom.current().nextInt(fighting.size()));

		return getPlugin().getServer().getPlayer(uuid);
	}

	public List<UUID> getByState(ParkourPlayer.ParkourState state) {
		return players.values().stream().filter(player -> player.getState() == state).map(ParkourPlayer::getUuid).collect(Collectors.toList());
	}

	/**
	 * To ensure that the fight does not go on forever and to
	 * let the players know how much time they have left.
	 */
	@Getter
	@RequiredArgsConstructor
	public class ParkourGameTask extends BukkitRunnable {

		private int time = 303;

		@Override
		public void run() {
			if(time == 303) {
				sendMessage(CC.DARK_GRAY + "[" + CC.PRIMARY + "Event" + CC.DARK_GRAY + "] " + CC.SECONDARY + "Starting in " + CC.PRIMARY + "3 seconds" + CC.SECONDARY + ".");
			} else if(time == 302) {
				sendMessage(CC.DARK_GRAY + "[" + CC.PRIMARY + "Event" + CC.DARK_GRAY + "] " + CC.SECONDARY + "Starting in " + CC.PRIMARY + "2 seconds" + CC.SECONDARY + ".");
			} else if(time == 301) {
				sendMessage(CC.DARK_GRAY + "[" + CC.PRIMARY + "Event" + CC.DARK_GRAY + "] " + CC.SECONDARY + "Starting in " + CC.PRIMARY + "1 second" + CC.SECONDARY + ".");
			} else if(time == 300) {
				sendMessage(CC.DARK_GRAY + "[" + CC.PRIMARY + "Event" + CC.DARK_GRAY + "] " + CC.SECONDARY + "The game has started.");

				for(ParkourPlayer player : getPlayers().values()) {
					player.setLastCheckpoint(null);
					player.setState(ParkourPlayer.ParkourState.INGAME);
					player.setCheckpointId(0);
				}

				for(Player player : getBukkitPlayers()) {
					teleportToSpawnOrCheckpoint(player);
					giveItems(player);
				}

			} else if(time <= 0) {
				Player winner = getRandomPlayer();

				if(winner != null) {
					handleWin(winner);
				}

				end();
				cancel();
				return;
			}

			if(getPlayers().size() == 1) {
				Player winner = Bukkit.getPlayer(getByState(ParkourPlayer.ParkourState.INGAME).get(0));

				if(winner != null) {
					handleWin(winner);
				}

				end();
				cancel();
				return;
			}


			if(Arrays.asList(60, 50, 40, 30, 25, 20, 15, 10).contains(time)) {
				sendMessage(CC.DARK_GRAY + "[" + CC.PRIMARY + "Event" + CC.DARK_GRAY + "] " + CC.SECONDARY + "Game ends in " + CC.PRIMARY + time + " seconds" + CC.SECONDARY + ".");
			} else if(Arrays.asList(5, 4, 3, 2, 1).contains(time)) {
				sendMessage(CC.DARK_GRAY + "[" + CC.PRIMARY + "Event" + CC.DARK_GRAY + "] " + CC.SECONDARY + "Game is ending in " + CC.PRIMARY + time + " seconds" + CC.SECONDARY + ".");
			}

			time--;
		}
	}

	@Getter
	@RequiredArgsConstructor
	public class WaterCheckTask extends BukkitRunnable {
		@Override
		public void run() {
			if(getPlayers().size() <= 1) {
				return;
			}

			getBukkitPlayers().forEach(player -> {
				if(getPlayer(player) != null && getPlayer(player).getState() != ParkourPlayer.ParkourState.INGAME) {
					return;
				}

				if(isStandingOn(player, Material.WATER) || isStandingOn(player, Material.STATIONARY_WATER)) {
					teleportToSpawnOrCheckpoint(player);
				} else if(isStandingOn(player, Material.STONE_PLATE) || isStandingOn(player, Material.IRON_PLATE) || isStandingOn(player, Material.WOOD_PLATE)) {
					ParkourPlayer parkourPlayer = getPlayer(player.getUniqueId());

					if(parkourPlayer != null) {

						boolean checkpoint = false;

						if(parkourPlayer.getLastCheckpoint() == null) {
							checkpoint = true;
							parkourPlayer.setLastCheckpoint(CustomLocation.fromBukkitLocation(player.getLocation()));
						} else if(parkourPlayer.getLastCheckpoint() != null && !isSameLocation(player.getLocation(), parkourPlayer.getLastCheckpoint().toBukkitLocation())) {
							checkpoint = true;
							parkourPlayer.setLastCheckpoint(CustomLocation.fromBukkitLocation(player.getLocation()));
						}

						if(checkpoint) {
							parkourPlayer.setCheckpointId(parkourPlayer.getCheckpointId() + 1);
							player.sendMessage(CC.DARK_GRAY + "[" + CC.PRIMARY + "Event" + CC.DARK_GRAY + "] " + CC.SECONDARY + "Checkpoint " + CC.PRIMARY + "#" + parkourPlayer.getCheckpointId() + CC.SECONDARY + " has been set.");
						}
					}
				} else if(isStandingOn(player, Material.GOLD_PLATE)) {
					handleWin(player);
					end();
					cancel();
				}
			});
		}
	}

	private boolean isStandingOn(Player player, Material material) {
		Block legs = player.getLocation().getBlock();
		Block head = legs.getRelative(BlockFace.UP);
		return legs.getType() == material || head.getType() == material;
	}

	private boolean isSameLocation(Location location, Location check) {
		return location.getWorld().getName().equalsIgnoreCase(check.getWorld().getName()) && location.getBlockX() == check.getBlockX() && location.getBlockY() == check.getBlockY() && location.getBlockZ() == check.getBlockZ();
	}
}
