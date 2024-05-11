package club.minion.practice.util;

import com.conaxgames.util.finalutil.CC;
import club.minion.practice.Practice;
import club.minion.practice.team.KillableTeam;
import club.minion.practice.tournament.TournamentTeam;
import java.util.UUID;
import org.bukkit.entity.Player;

public class TeamUtil {
	public static String getNames(KillableTeam team) {
		String names = "";

		for (int i = 0; i < team.getPlayers().size(); i++) {
			UUID teammateUUID = team.getPlayers().get(i);
			Player teammate = Practice.getInstance().getServer().getPlayer(teammateUUID);

			String name = "";

			if (teammate == null) {
				if (team instanceof TournamentTeam) {
					name = ((TournamentTeam) team).getPlayerName(teammateUUID);
				}
			} else {
				name = teammate.getName();
			}

			int players = team.getPlayers().size();

			if (teammate != null) {
				names += CC.SECONDARY + name + CC.PRIMARY + (((players - 1) == i) ? "" :
						((players - 2) == i) ? (players > 2 ? "," : "") + " and " : ", ");
			}
		}

		return names;
	}
}
