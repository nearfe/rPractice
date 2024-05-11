package club.minion.practice.util.trails;

import club.minion.practice.Practice;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

public class ProjectileTrail {

    public static void play(Entity item, ParticleEffect effect) {
        BukkitRunnable runnable = new BukkitRunnable() {

            @Override
            public void run() {
                if(item == null || item.isOnGround() || item.isDead()) {
                    cancel();
                    return;
                }

                effect.display(0, 0, 0, 0, 1, item.getLocation(), 256);
            }
        };

        runnable.runTaskTimer(Practice.getInstance(), 0L, 1L);
    }
}
