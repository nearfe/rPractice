package club.minion.practice.events.runner;

import club.minion.practice.events.EventCountdownTask;
import club.minion.practice.events.PracticeEvent;
import com.conaxgames.util.finalutil.CC;

import java.util.Arrays;

public class RunnerCountdownTask extends EventCountdownTask {

    public RunnerCountdownTask(PracticeEvent event) {
        super(event, 45);
    }

    @Override
    public boolean shouldAnnounce(int timeUntilStart) {
        return Arrays.asList(90, 60, 30, 15, 10, 5).contains(timeUntilStart);
    }

    @Override
    public boolean canStart() {
        return getEvent().getPlayers().size() >= 2;
    }

    @Override
    public void onCancel() {
        getEvent().sendMessage(CC.RED + "There were not enough players to start the event.");
        getEvent().end();
        getEvent().getPlugin().getEventManager().setCooldown(0L);
    }
}
