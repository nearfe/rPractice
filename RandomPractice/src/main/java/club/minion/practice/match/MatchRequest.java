package club.minion.practice.match;

import club.minion.practice.arena.Arena;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MatchRequest {
	private final UUID requester;
	private final UUID requested;

	private final Arena arena;
	private final String kitName;
	private final boolean party;
}
