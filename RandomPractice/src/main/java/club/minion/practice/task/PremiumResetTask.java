package club.minion.practice.task;

import com.conaxgames.CorePlugin;
import com.conaxgames.api.abstr.AbstractBukkitCallback;
import club.minion.practice.Practice;
import club.minion.practice.request.PremiumRequest;
import com.google.gson.JsonElement;
import java.util.TimerTask;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PremiumResetTask extends TimerTask {

	private final Practice plugin = Practice.getInstance();

	@Override
	public void run() {
		CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(new PremiumRequest("reset", "", 0),
				new AbstractBukkitCallback() {
					@Override
					public void callback(JsonElement jsonElement) {
						plugin.getLogger().info("Successfully ran Practice Reset");
					}
				});
	}
}
