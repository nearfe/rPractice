// Decompiled with: CFR 0.152
// Class Version: 8
package club.minion.practice.board;

import club.minion.practice.Practice;
import club.minion.practice.managers.QueueManager;
import club.minion.practice.match.Match;
import club.minion.practice.player.PlayerData;
import club.minion.practice.queue.QueueEntry;
import club.minion.practice.util.Animation;
import club.minion.practice.util.StatusCache;
import club.minion.practice.util.board.AssembleAdapter;
import com.conaxgames.util.finalutil.PlayerUtil;
import com.google.common.collect.Lists;
import dev.lugami.spigot.utils.CC;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BoardAdapter implements AssembleAdapter {
    private Practice plugin = Practice.getInstance();
    private String SPACER = "&7&m" + StringUtils.repeat("-", 20);

    @Override
    public String getTitle(Player player) {
        return CC.translate(Animation.getScoreboardTitle());
    }

    @Override
    public List<String> getLines(Player player) {
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null || !playerData.isScoreboardEnabled()) {
            return null;
        }
        switch (playerData.getPlayerState()) {
            case LOADING:
            case EDITING:
            case FFA:
            case SPAWN: {
                return this.getSpawnBoard(player, false);
            }
            case EVENT:
            case SPECTATING: {
                return this.getSpectateBoard(player);
            }
            case QUEUE: {
                return this.getSpawnBoard(player, true);
            }
            case FIGHTING: {
                return this.getGameBoard(player);
            }
        }
        return null;
    }

    private List<String> getSpawnBoard(Player player, boolean queueing) {
        ArrayList<String> strings = Lists.newArrayList();
        if (!queueing) {
            strings.addAll(Practice.getInstance().getScoreboardConfig().getConfig().getStringList("SCOREBOARD.LOBBY.DEFAULT").stream().map(a -> a.replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))).map(a -> a.replace("%fighting%", String.valueOf(StatusCache.getInstance().getFighting()))).map(a -> a.replace("%queueing%", String.valueOf(StatusCache.getInstance().getQueueing()))).collect(Collectors.toList()));
        } else if (queueing) {
            QueueManager manager = this.plugin.getQueueManager();
            QueueEntry entry = manager.getQueueEntry(player.getUniqueId());
            strings.addAll(Practice.getInstance().getScoreboardConfig().getConfig().getStringList("SCOREBOARD.LOBBY.QUEUE").stream().map(a -> a.replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))).map(a -> a.replace("%fighting%", String.valueOf(StatusCache.getInstance().getFighting()))).map(a -> a.replace("%queueing%", String.valueOf(StatusCache.getInstance().getQueueing()))).map(a -> a.replace("%kit%", entry.getKitName())).map(a -> a.replace("%type%", entry.getQueueType().getName())).collect(Collectors.toList()));
        }
        return strings;
    }

    private List<String> getGameBoard(Player player) {
        ArrayList<String> strings = Lists.newArrayList();
        Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
        if (!match.isFFA()) {
            Player opponentPlayer;
            Player player2 = opponentPlayer = match.getTeams().get(0).getPlayers().get(0) == player.getUniqueId() ? this.plugin.getServer().getPlayer(match.getTeams().get(1).getPlayers().get(0)) : this.plugin.getServer().getPlayer(match.getTeams().get(0).getPlayers().get(0));
            if (opponentPlayer == null) {
                this.plugin.getPlayerManager().sendToSpawnAndReset(player);
                return this.getSpawnBoard(player, false);
            }
            Player opponentPlayer1 = opponentPlayer;
            if (!match.getKit().isBoxing()) {
                strings.addAll(Practice.getInstance().getScoreboardConfig().getConfig().getStringList("SCOREBOARD.FIGHT.DEFAULT").stream().map(a -> a.replace("%own-ping%", PlayerUtil.getPing(player) + "ms")).map(a -> a.replace("%opponent-ping%", PlayerUtil.getPing(opponentPlayer1) + "ms")).collect(Collectors.toList()));
            } else if (match.getKit().isBoxing()) {
                int ownHits = match.getBoxingHits().getOrDefault(player.getUniqueId(), 0);
                int opponentHits = match.getBoxingHits().getOrDefault(opponentPlayer.getUniqueId(), 0);
                strings.addAll(Practice.getInstance().getScoreboardConfig().getConfig().getStringList("SCOREBOARD.FIGHT.BOXING").stream().map(a -> a.replace("%own-ping%", PlayerUtil.getPing(player) + "ms")).map(a -> a.replace("%opponent-ping%", PlayerUtil.getPing(opponentPlayer1) + "ms")).map(a -> a.replace("%combo%", this.getBoxingHits(ownHits, opponentHits))).map(a -> a.replace("%own-hits%", String.valueOf(ownHits))).map(a -> a.replace("%opponent-hits%", String.valueOf(opponentHits))).collect(Collectors.toList()));
            }
        }
        return strings;
    }

    private String getBoxingHits(int own, int opp) {
        if (own < opp) {
            return "&c(-" + (opp - own) + ')';
        }
        return "&a(+" + (own - opp) + ')';
    }

    private List<String> getSpectateBoard(Player player) {
        Player player2;
        ArrayList<String> strings = Lists.newArrayList();
        Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
        Player player1 = match.getTeams().get(0).getPlayers().get(0) == player.getUniqueId() ? this.plugin.getServer().getPlayer(match.getTeams().get(1).getPlayers().get(0)) : this.plugin.getServer().getPlayer(match.getTeams().get(0).getPlayers().get(0));
        player2 = match.getTeams().get(1).getPlayers().get(0) == player.getUniqueId() ? this.plugin.getServer().getPlayer(match.getTeams().get(1).getPlayers().get(0)) : this.plugin.getServer().getPlayer(match.getTeams().get(0).getPlayers().get(0));
        if (!match.getKit().isBoxing()) {
            strings.addAll(Practice.getInstance().getScoreboardConfig().getConfig().getStringList("SCOREBOARD.FIGHT.SPECTATE").stream().map(a -> a.replace("%player1%", player1.getName())).map(a -> a.replace("%player2%", player2.getName())).collect(Collectors.toList()));
        }
        return strings;
    }
}
