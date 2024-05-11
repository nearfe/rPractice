package server.pvptemple.api.model;

import server.pvptemple.api.util.Constants;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "practice_season_" + Constants.PRACTICE_SEASON + "_data")
public class PracticeData implements Serializable {

	@Id
	@Column(unique = true)
	private int playerId;

	private boolean allowingSpectators;
	private boolean scoreboardEnabled;
	private boolean dynamicPingRange;
	private boolean acceptingDuels;
	private boolean hidingPlayers;

	private int unrankedWins;
	private int rankedWins;

	private int pingRange;
	private int eloRange;

	private int globalElo;

	private int premiumMatchesPlayed;
	private int premiumMatchesExtra;
	private int premiumLosses;
	private int premiumWins;
	private int premiumElo;

	private int gappleEloParty;
	private int gappleLosses;
	private int gappleWins;
	private int gappleElo;

	private int soupEloParty;
	private int soupLosses;
	private int soupWins;
	private int soupElo;

	private int nodebuffEloParty;
	private int nodebuffLosses;
	private int nodebuffWins;
	private int nodebuffElo;

	private int debuffEloParty;
	private int debuffLosses;
	private int debuffWins;
	private int debuffElo;

	private int archerEloParty;
	private int archerLosses;
	private int archerWins;
	private int archerElo;

	private int classicEloParty;
	private int classicLosses;
	private int classicWins;
	private int classicElo;

	private int axeEloParty;
	private int axeLosses;
	private int axeWins;
	private int axeElo;

	private int hcfEloParty;
	private int hcfLosses;
	private int hcfWins;
	private int hcfElo;

	private int sumoEloParty;
	private int sumoLosses;
	private int sumoWins;
	private int sumoElo;

	private int builduhcEloParty;
	private int builduhcLosses;
	private int builduhcWins;
	private int builduhcElo;

	private int comboEloParty;
	private int comboLosses;
	private int comboWins;
	private int comboElo;
}
