package club.minion.practice.events.oitc;

import com.conaxgames.util.finalutil.CC;
import club.minion.practice.events.EventCountdownTask;
import club.minion.practice.events.PracticeEvent;

public class OITCCountdownTask extends EventCountdownTask {
	public OITCCountdownTask(PracticeEvent event) {
		super(event, 120);
	}

	@Override
	public boolean shouldAnnounce(int timeUntilStart) {
		return true;
	}

	@Override
	public boolean canStart() {
		return getEvent().getPlayers().size() >= 2;
	}

	@Override
	public void onCancel() {
		getEvent().sendMessage(CC.RED + "There were not enough players to start the event, so it has been canceled.");

		getEvent().end();
	}
}
