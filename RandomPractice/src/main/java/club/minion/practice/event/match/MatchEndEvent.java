package club.minion.practice.event.match;

import club.minion.practice.match.Match;
import club.minion.practice.match.MatchTeam;
import lombok.Getter;

@Getter
public class MatchEndEvent extends MatchEvent {

	private final MatchTeam winningTeam;
	private final MatchTeam losingTeam;

	public MatchEndEvent(Match match, MatchTeam winningTeam, MatchTeam losingTeam) {
		super(match);

		this.winningTeam = winningTeam;
		this.losingTeam = losingTeam;
	}

}
