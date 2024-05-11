package club.minion.practice.events.runner;


import club.minion.practice.events.EventPlayer;
import club.minion.practice.events.PracticeEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class RunnerPlayer extends EventPlayer {

    private RunnerState state = RunnerState.WAITING;
    private boolean wasEliminated = false;

    public RunnerPlayer(UUID uuid, PracticeEvent event) {
        super(uuid, event);
    }

    public enum RunnerState {
        WAITING, INGAME, ELIMINATED
    }
}
