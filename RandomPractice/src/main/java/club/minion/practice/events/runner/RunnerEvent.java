package club.minion.practice.events.runner;


import com.conaxgames.CorePlugin;
import club.minion.practice.events.EventCountdownTask;
import club.minion.practice.events.PracticeEvent;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.CustomLocation;
import com.conaxgames.util.finalutil.CC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.NumberConversions;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
public class RunnerEvent extends PracticeEvent<RunnerPlayer> {

    private final Map<UUID, RunnerPlayer> players = new HashMap<>();
    private final RunnerCountdownTask countdownTask = new RunnerCountdownTask(this);
    private List<UUID> visibility;
    private RunnerGameTask gameTask;
    private MoveTask moveTask;
    private Map<Location, ItemStack> blocks;

    public RunnerEvent() {
        super("Runner");
    }

    @Override
    public Map<UUID, RunnerPlayer> getPlayers() {
        return players;
    }

    @Override
    public EventCountdownTask getCountdownTask() {
        return countdownTask;
    }

    @Override
    public List<CustomLocation> getSpawnLocations() {
        return getPlugin().getSpawnManager().getRunnerLocations();
    }

    @Override
    public void onStart() {
        gameTask = new RunnerGameTask();
        gameTask.runTaskTimerAsynchronously(getPlugin(), 0, 20L);

        //new WaterCheckTask().runTaskTimer(getPlugin(), 0, 20);
        visibility = new ArrayList<>();
        blocks = new HashMap<>();
    }

    public void cancelAll() {
        if(gameTask != null) {
            gameTask.cancel();
        }

        if(moveTask != null) {
            moveTask.cancel();
        }

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            blocks.forEach(((location, stack) ->
                    location.getBlock().setTypeIdAndData(stack.getTypeId(),
                            (byte) stack.getDurability(), true)));

            if(blocks.size() > 0) {
                blocks.clear();
            }
        }, 40L);
    }

    @Override
    public Consumer<Player> onJoin() {
        return player -> players.put(player.getUniqueId(), new RunnerPlayer(player.getUniqueId(), this));
    }

    @Override
    public Consumer<Player> onDeath() {
        return player -> {
            RunnerPlayer data = getPlayer(player);

            if(data.getState() != RunnerPlayer.RunnerState.INGAME) {
                return;
            }

            data.setState(RunnerPlayer.RunnerState.ELIMINATED);

            getPlugin().getEventManager().addSpectatorRunner(player, getPlugin().getPlayerManager().getPlayerData(player.getUniqueId()), this);

            sendMessage(CC.DARK_GRAY + "[" + CC.PRIMARY + "Event" + CC.DARK_GRAY + "] " + player.getDisplayName() + CC.SECONDARY + " was eliminated.");
            player.sendMessage(" ");
            player.sendMessage(CC.RED + "You have been eliminated from the event. Better luck next time!");
            Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
            if(mineman != null && !mineman.hasRank(Rank.PRIME)) {
                player.sendMessage(CC.GRAY + "Purchase a rank at shop.minion.club to host events of your own.");
            }
            player.sendMessage(" ");

            if(getByState(RunnerPlayer.RunnerState.INGAME).size() == 1) {
                Player winner = Bukkit.getPlayer(getByState(RunnerPlayer.RunnerState.INGAME).get(0));
                if(winner != null) {
                    handleWin(winner);
                }

                end();
                cancelAll();

                Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                    blocks.forEach(((location, stack) ->
                            location.getBlock().setTypeIdAndData(stack.getTypeId(),
                            (byte) stack.getDurability(), true)));

                    if(blocks.size() > 0) {
                        blocks.clear();
                    }
                }, 40L);
            }
        };
    }

    public List<UUID> getByState(RunnerPlayer.RunnerState state) {
        return players.values().stream().filter(player -> player.getState() == state).map(RunnerPlayer::getUuid).collect(Collectors.toList());
    }
    /**
     * To ensure that the fight does not go on forever and to
     * let the players know how much time they have left.
     */
    @Getter
    @RequiredArgsConstructor
    public class RunnerGameTask extends BukkitRunnable {

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

                getPlayers().values().forEach(player -> player.setState(RunnerPlayer.RunnerState.INGAME));
                getBukkitPlayers().forEach(player -> player.getInventory().clear());

                moveTask = new MoveTask();
                moveTask.runTaskTimer(getPlugin(), 0, 1L);
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
                Player winner = Bukkit.getPlayer(getByState(RunnerPlayer.RunnerState.INGAME).get(0));

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

    private Player getRandomPlayer() {
        if(getByState(RunnerPlayer.RunnerState.INGAME).size() == 0) {
            return null;
        }

        List<UUID> fighting = getByState(RunnerPlayer.RunnerState.INGAME);

        Collections.shuffle(fighting);

        UUID uuid = fighting.get(ThreadLocalRandom.current().nextInt(fighting.size()));

        return getPlugin().getServer().getPlayer(uuid);
    }

    @RequiredArgsConstructor
    private class MoveTask extends BukkitRunnable {
        @Override
        public void run() {
            getBukkitPlayers().forEach(player -> {
                if(getPlayer(player.getUniqueId()) != null && getPlayer(player.getUniqueId()).getState() == RunnerPlayer.RunnerState.INGAME) {
                    if(getPlayers().size() <= 1) {
                        return;
                    }

                    if(getPlayers().containsKey(player.getUniqueId())) {
                        Block legs = player.getLocation().getBlock();
                        Block head = legs.getRelative(BlockFace.UP);
                        if(legs.getType() == Material.WATER || legs.getType() == Material.STATIONARY_WATER || head.getType() == Material.WATER || head.getType() == Material.STATIONARY_WATER) {
                            onDeath().accept(player);
                        }
                    }

                    Location loc = player.getLocation().clone().add(0, -1, 0);
                    int y = loc.getBlockY();
                    Block block = null;
                    for(int i = 0; i <= 1; ++i) {
                        block = getBlockUnderPlayer(loc, y);
                        --y;

                        if(block != null) {
                            break;
                        }
                    }

                    if(getByState(RunnerPlayer.RunnerState.INGAME).size() == 1) {
                        return;
                    }

                    if(block != null) {
                        Block finalBlock = block;
                        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                            if(!getBlocks().containsKey(finalBlock.getLocation())) {
                                getBlocks().put(finalBlock.getLocation(),
                                        new ItemStack(finalBlock.getType(), 1,
                                                (short) finalBlock.getData()));
                            }

                            finalBlock.setType(Material.AIR);
                        }, 8L);
                    }
                }
            });
        }
    }

    private Block getBlockUnderPlayer(Location location, int y) {
        PlayerPosition loc = new PlayerPosition(location.getX(), y, location.getZ());

        Block b1 = loc.getBlock(location.getWorld(), 0.3, -0.3);
        if(b1.getType() != Material.AIR) {
            return b1;
        }

        Block b2 = loc.getBlock(location.getWorld(), -0.3, 0.3);
        if(b2.getType() != Material.AIR) {
            return b2;
        }

        Block b3 = loc.getBlock(location.getWorld(), 0.3, 0.3);
        if(b3.getType() != Material.AIR) {
            return b3;
        }

        Block b4 = loc.getBlock(location.getWorld(), -0.3, -0.3);
        if(b4.getType() != Material.AIR) {
            return b4;
        }

        return null;
    }

    @AllArgsConstructor
    private class PlayerPosition {
        private double x;
        private int y;
        private double z;

        public Block getBlock(World world, double addx, double addz) {
            return world.getBlockAt(NumberConversions.floor(x + addx), y, NumberConversions.floor(z + addz));
        }
    }
}
