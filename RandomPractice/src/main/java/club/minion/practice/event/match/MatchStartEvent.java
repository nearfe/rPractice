package club.minion.practice.event.match;

import club.minion.practice.match.Match;

public class MatchStartEvent extends MatchEvent {
	public MatchStartEvent(Match match) {
		super(match);
	}
}
