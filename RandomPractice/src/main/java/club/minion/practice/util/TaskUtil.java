package club.minion.practice.util;

import club.minion.practice.Practice;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskUtil {
    public static void run(Plugin plugin, Runnable runnable) {
        plugin.getServer().getScheduler().runTask(plugin, runnable);
    }

    public static void runAsync(Plugin plugin, Runnable runnable) {
        try {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
        } catch (IllegalStateException e2) {
            plugin.getServer().getScheduler().runTask(plugin, runnable);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void runTimer(Plugin plugin, Runnable runnable, long delay, long timer) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, delay, timer);
    }

    public static int runTimer(Plugin plugin, BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimer(plugin, delay, timer);
        return runnable.getTaskId();
    }

    public static void runLater(Plugin plugin, Runnable runnable, long delay) {
        plugin.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
    }

    public static void runLaterAsync(Plugin plugin, Runnable runnable, long delay) {
        try {
            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
        } catch (IllegalStateException e2) {
            plugin.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void runTimerAsync(Plugin plugin, Runnable runnable, long delay, long timer) {
        try {
            plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, timer);
        } catch (IllegalStateException e2) {
            plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, delay, timer);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void runTimerAsync(Runnable runnable, long delay, long timer) {
        try {
            Practice.getInstance().getServer().getScheduler().runTaskTimerAsynchronously((Plugin)Practice.getInstance(), runnable, delay, timer);
        }
        catch (IllegalStateException e) {
            Practice.getInstance().getServer().getScheduler().runTaskTimer(Practice.getInstance(), runnable, delay, timer);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void runTimerAsync(Plugin plugin, BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimerAsynchronously(plugin, delay, timer);
    }
}
