package club.minion.practice.util;

import club.minion.practice.Practice;
import com.conaxgames.CorePlugin;
import club.minion.practice.match.Match;
import club.minion.practice.match.MatchTeam;
import club.minion.practice.player.PlayerData;
import club.minion.practice.player.PlayerState;
import com.conaxgames.board.Board;
import com.conaxgames.util.finalutil.CC;
import net.minecraft.server.v1_8_R3.EntityHuman;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

public final class NameTag {

    private NameTag() {
    }

    public static void updateNametag(Player player, boolean hearts) {
        Bukkit.getScheduler().runTaskAsynchronously(Practice.getInstance(), () -> {
            Board board = CorePlugin.getInstance().getBoardManager().getPlayerBoards().get(player.getUniqueId());

            if (board == null) {
                return;
            }

            /*if(hearts) {
                Objective objective = scoreboard.getObjective("showhealth");
                if (objective == null) {
                    objective = .registerNewObjective("showhealth", "health");
                    objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
                    objective.setDisplayName(CC.DARK_RED + "\u2764");
                }
            } else {
                Objective objective = board.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);

                if(objective != null) {
                    objective.unregister();
                }
            }*/

            Scoreboard scoreboard = board.getScoreboard();

            Team red = scoreboard.getTeam("red");

            if (red == null) {
                red = scoreboard.registerNewTeam("red");
            }
            Team green = scoreboard.getTeam("green");

            if (green == null) {
                green = scoreboard.registerNewTeam("green");
            }

            red.setPrefix(CC.RESET);
            green.setPrefix(CC.RESET);

            PlayerData playerData = Practice.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());

            if (playerData.getPlayerState() != PlayerState.FIGHTING) {
                for (String entry : red.getEntries()) {
                    if (green.hasEntry(entry)) {
                        green.removeEntry(entry);
                    } else {
                        red.removeEntry(entry);
                    }
                }

                for (String entry : green.getEntries()) {
                    if (red.hasEntry(entry)) {
                        red.removeEntry(entry);
                    } else {
                        green.removeEntry(entry);
                    }
                }
                return;
            }

            Match match = Practice.getInstance().getMatchManager().getMatch(player.getUniqueId());

            for (MatchTeam team : match.getTeams()) {
                for (UUID teamUUID : team.getAlivePlayers()) {
                    Player teamPlayer = Bukkit.getPlayer(teamUUID);
                    if (teamPlayer != null) {
                        String teamPlayerName = teamPlayer.getName();
                        if (team.getTeamID() == playerData.getTeamID() && !match.isFFA()) {
                            if (red.hasEntry(teamPlayerName)) {
                                red.removeEntry(teamPlayerName);
                            }

                            if (!green.hasEntry(teamPlayerName)) {
                                green.addEntry(teamPlayerName);
                            }
                        } else {
                            if (green.hasEntry(teamPlayerName)) {
                                green.removeEntry(teamPlayerName);
                            }

                            if (!red.hasEntry(teamPlayerName)) {
                                red.addEntry(teamPlayerName);
                            }
                        }
                    }
                }
            }
        });
    }
}