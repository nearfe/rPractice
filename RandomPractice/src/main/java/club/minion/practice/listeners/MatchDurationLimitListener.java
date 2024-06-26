package club.minion.practice.listeners;

import com.conaxgames.util.finalutil.CC;
import club.minion.practice.Practice;
import club.minion.practice.event.match.MatchStartEvent;
import club.minion.practice.match.Match;
import club.minion.practice.match.MatchState;
import club.minion.practice.util.TimeUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MatchDurationLimitListener implements Listener {

    @Getter private final Practice plugin = Practice.getInstance();

    @Getter private final int DURATION_LIMIT_SECONDS = (int) TimeUnit.MINUTES.toSeconds(20);

    private static final String TIME_WARNING_MESSAGE = CC.RED + "The match will forcefully end in %s";
    private static final String TIME_EXCEEDED_MESSAGE = CC.RED + CC.BOLD + "Match time exceeded %s. Ending match...";

    @EventHandler
    public void onMatchCountdownEnd(MatchStartEvent event) {
        Match match = event.getMatch();

        new BukkitRunnable() {

            int secondsRemaining = DURATION_LIMIT_SECONDS;

            @Override
            public void run() {
                if (match.getMatchState() != MatchState.FIGHTING) {
                    cancel();
                    return;
                }

                if (match.getKit().isSumo() || match.getKit().isBoxing()) {
                    match.getTeams().forEach(t -> t.getAlivePlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(p -> {
                        p.setHealth(20);
                        p.setFoodLevel(20);
                        p.setSaturation(20);
                    }));
                }

                switch (secondsRemaining) {
                    case 120:
                    case 60:
                    case 30:
                    case 15:
                    case 10:
                    case 5:
                        match.broadcastMessage(String.format(TIME_WARNING_MESSAGE, TimeUtil.formatTime(secondsRemaining)));
                    case 0:
                        match.broadcastMessage(String.format(TIME_EXCEEDED_MESSAGE, TimeUtil.formatTime(DURATION_LIMIT_SECONDS)));
                        plugin.getMatchManager().removeMatch(match);
                    default:
                        break;
                }
                secondsRemaining--;
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
}
