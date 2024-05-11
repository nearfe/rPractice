package club.minion.practice.events.parkour;

import club.minion.practice.events.EventPlayer;
import club.minion.practice.events.PracticeEvent;
import com.conaxgames.util.CustomLocation;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class ParkourPlayer extends EventPlayer {

	private ParkourState state = ParkourState.WAITING;
	private CustomLocation lastCheckpoint;
	private int checkpointId;

	public ParkourPlayer(UUID uuid, PracticeEvent event) {
		super(uuid, event);
	}

	public enum ParkourState {
		WAITING, INGAME
	}
}
