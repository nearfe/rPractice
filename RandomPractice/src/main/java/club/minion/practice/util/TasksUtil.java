package club.minion.practice.util;

import club.minion.practice.Practice;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
@UtilityClass
public class TasksUtil {

    public void run(Runnable runnable) {
        Practice.getInstance().getServer().getScheduler().runTask(Practice.getInstance(), runnable);
    }

    public void runTimer(Runnable runnable, long delay, long timer) {
        Practice.getInstance().getServer().getScheduler().runTaskTimer(Practice.getInstance(), runnable, delay, timer);
    }

    public void runTimerAsync(Runnable runnable, long delay, long timer) {
        Practice.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(Practice.getInstance(), runnable, delay, timer);
    }

    public void runTimer(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimer(Practice.getInstance(), delay, timer);
    }

    public void runLater(Runnable runnable, long delay) {
        Practice.getInstance().getServer().getScheduler().runTaskLater(Practice.getInstance(), runnable, delay);
    }

    public void runLaterAsync(Runnable runnable, long delay) {
        Practice.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(Practice.getInstance(), runnable, delay);
    }

    public void runSync(Runnable runnable) {
        if (Bukkit.isPrimaryThread())
            runnable.run();
        else
            Bukkit.getScheduler().runTask(Practice.getInstance(), runnable);
    }

    public void runAsync(Runnable runnable) {
        if (Bukkit.isPrimaryThread())
            Bukkit.getScheduler().runTaskAsynchronously(Practice.getInstance(), runnable);
        else
            runnable.run();
    }
}
