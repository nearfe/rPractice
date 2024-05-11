package club.minion.practice.events.oitc;

import club.minion.practice.events.EventPlayer;
import club.minion.practice.events.PracticeEvent;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OITCPlayer extends EventPlayer {
	private int score = 0;

	public OITCPlayer(UUID uuid, PracticeEvent event) {
		super(uuid, event);
	}
}
