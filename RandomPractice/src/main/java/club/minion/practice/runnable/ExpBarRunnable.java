package club.minion.practice.runnable;

import com.conaxgames.CorePlugin;
import com.conaxgames.timer.impl.EnderpearlTimer;
import club.minion.practice.Practice;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class ExpBarRunnable implements Runnable {

	private final Practice plugin = Practice.getInstance();

	@Override
	public void run() {
		EnderpearlTimer timer = CorePlugin.getInstance().getTimerManager().getTimer(EnderpearlTimer.class);
		for (UUID uuid : timer.getCooldowns().keySet()) {
			Player player = this.plugin.getServer().getPlayer(uuid);

			if (player != null) {
				long time = timer.getRemaining(player);
				int seconds = (int) Math.round((double) time / 1000.0D);

				player.setLevel(seconds);
				player.setExp((float) time / 15000.0F);
			}
		}
	}
}
