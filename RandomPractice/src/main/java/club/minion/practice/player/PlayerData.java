package club.minion.practice.player;

import club.minion.practice.Practice;
import club.minion.practice.killeffects.SpecialEffects;
import club.minion.practice.killmessages.KillMessages;
import club.minion.practice.kit.Kit;
import club.minion.practice.kit.PlayerKit;
import club.minion.practice.settings.SettingsInfo;
import club.minion.practice.util.CCUtil;
import com.conaxgames.util.finalutil.CC;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
	public static final int DEFAULT_ELO = 1000;
	private final Map<String, Map<Integer, PlayerKit>> playerKits = new HashMap<String, Map<Integer, PlayerKit>>();
	private final Map<String, Integer> rankedLosses = new HashMap<String, Integer>();
	private final Map<String, Integer> rankedWins = new HashMap<String, Integer>();
	private final Map<String, Integer> rankedElo = new HashMap<String, Integer>();
	private final Map<String, Integer> partyElo = new HashMap<String, Integer>();
	private UUID uniqueId = null;
	private PlayerState playerState = PlayerState.LOADING;
	private SettingsInfo settings = new SettingsInfo();
	private UUID currentMatchID;
	private int blockedHits;
	private double wastedHP;
	private UUID duelSelecting;
	private int matchesPlayed;
	private boolean acceptingDuels = true;
	private boolean allowingSpectators = true;
	private boolean scoreboardEnabled = true;
	private int cheatFreeMatches;
	private int minemanID = -1;
	private int eloRange = 250;
	private int pingRange = 50;
	private int teamID = -1;
	private int rematchID = -1;
	private int missedPots;
	private int longestCombo;
	private int combo;
	private int hits;
	private int criticalHits;
	private int premiumMatchesPlayed;
	private int premiumMatchesExtra;
	private int premiumLosses;
	public int potionsThrown;
	private int premiumWins;
	private int premiumElo = 1000;
	private KillMessages killMessage = KillMessages.NONE;
	private SpecialEffects killEffect = SpecialEffects.NONE;
	private String scoreboardColor = CC.GOLD;

	public PlayerData(UUID uid) {
		this.uniqueId = uid;
	}

	public int getPremiumMatches() {
		return Math.max(Practice.getInstance().getPlayerManager().getPremiumMatches(this.uniqueId) + this.premiumMatchesExtra - this.premiumMatchesPlayed, 0);
	}

	public int getWins(String kitName) {
		return this.rankedWins.computeIfAbsent(kitName, k -> 0);
	}

	public void setWins(String kitName, int wins) {
		this.rankedWins.put(kitName, wins);
	}

	public int getLosses(String kitName) {
		return this.rankedLosses.computeIfAbsent(kitName, k -> 0);
	}

	public void setLosses(String kitName, int losses) {
		this.rankedLosses.put(kitName, losses);
	}

	public String getScoreboardColor() {
		return this.scoreboardColor;
	}

	public void incrementBlockedHits() {
		++this.blockedHits;
	}

	public void setScoreboardColor(String color) {
		this.scoreboardColor = CCUtil.getValue(color);
	}

	public int getElo(String kitName) {
		return this.rankedElo.computeIfAbsent(kitName, k -> 1000);
	}

	public void setElo(String kitName, int elo) {
		this.rankedElo.put(kitName, elo);
	}

	public int getPartyElo(String kitName) {
		return this.partyElo.computeIfAbsent(kitName, k -> 1000);
	}

	public void setPartyElo(String kitName, int elo) {
		this.partyElo.put(kitName, elo);
	}

	public void addPlayerKit(int index, PlayerKit playerKit) {
		this.getPlayerKits(playerKit.getName()).put(index, playerKit);
	}

	public double getPotionAccuracy() {
		if (this.missedPots == 0) {
			return 100.0;
		}
		if (this.potionsThrown == this.missedPots) {
			return 50.0;
		}
		return Math.round(100.0 - (double)this.missedPots / (double)this.potionsThrown * 100.0);
	}

	public void incrementPotionsThrown() {
		++this.potionsThrown;
	}

	public int getGlobalStats(String type) {
		int i = 0;
		int count = 0;
		for (Kit kit : Practice.getInstance().getKitManager().getKits()) {
			switch (type.toUpperCase()) {
				case "ELO": {
					i += this.getElo(kit.getName());
					break;
				}
				case "WINS": {
					i += this.getWins(kit.getName());
					break;
				}
				case "LOSSES": {
					i += this.getLosses(kit.getName());
				}
			}
			++count;
		}
		if (i == 0) {
			i = 1;
		}
		if (count == 0) {
			count = 1;
		}
		return type.toUpperCase().equalsIgnoreCase("ELO") ? Math.round(i / count) : i;
	}

	public PlayerState getPlayerState() {
		return this.playerState;
	}

	public Map<Integer, PlayerKit> getPlayerKits(String kitName) {
		return this.playerKits.computeIfAbsent(kitName, k -> new HashMap());
	}

	public void setUniqueId(UUID uniqueId) {
		this.uniqueId = uniqueId;
	}

	public void setPlayerState(PlayerState playerState) {
		this.playerState = playerState;
	}

	public void setSettings(SettingsInfo settings) {
		this.settings = settings;
	}

	public void setCurrentMatchID(UUID currentMatchID) {
		this.currentMatchID = currentMatchID;
	}

	public void setBlockedHits(int blockedHits) {
		this.blockedHits = blockedHits;
	}

	public void setWastedHP(double wastedHP) {
		this.wastedHP = wastedHP;
	}

	public void setDuelSelecting(UUID duelSelecting) {
		this.duelSelecting = duelSelecting;
	}

	public void setMatchesPlayed(int matchesPlayed) {
		this.matchesPlayed = matchesPlayed;
	}

	public void setAcceptingDuels(boolean acceptingDuels) {
		this.acceptingDuels = acceptingDuels;
	}

	public void setAllowingSpectators(boolean allowingSpectators) {
		this.allowingSpectators = allowingSpectators;
	}

	public void setScoreboardEnabled(boolean scoreboardEnabled) {
		this.scoreboardEnabled = scoreboardEnabled;
	}

	public void setCheatFreeMatches(int cheatFreeMatches) {
		this.cheatFreeMatches = cheatFreeMatches;
	}

	public void setMinemanID(int minemanID) {
		this.minemanID = minemanID;
	}

	public void setEloRange(int eloRange) {
		this.eloRange = eloRange;
	}

	public void setPingRange(int pingRange) {
		this.pingRange = pingRange;
	}

	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}

	public void setRematchID(int rematchID) {
		this.rematchID = rematchID;
	}

	public void setMissedPots(int missedPots) {
		this.missedPots = missedPots;
	}

	public void setLongestCombo(int longestCombo) {
		this.longestCombo = longestCombo;
	}

	public void setCombo(int combo) {
		this.combo = combo;
	}

	public void setHits(int hits) {
		this.hits = hits;
	}

	public void setCriticalHits(int criticalHits) {
		this.criticalHits = criticalHits;
	}

	public void setPremiumMatchesPlayed(int premiumMatchesPlayed) {
		this.premiumMatchesPlayed = premiumMatchesPlayed;
	}

	public void setPremiumMatchesExtra(int premiumMatchesExtra) {
		this.premiumMatchesExtra = premiumMatchesExtra;
	}

	public void setPremiumLosses(int premiumLosses) {
		this.premiumLosses = premiumLosses;
	}

	public void setPotionsThrown(int potionsThrown) {
		this.potionsThrown = potionsThrown;
	}

	public void setPremiumWins(int premiumWins) {
		this.premiumWins = premiumWins;
	}

	public void setPremiumElo(int premiumElo) {
		this.premiumElo = premiumElo;
	}

	public PlayerData() {
	}

	public UUID getUniqueId() {
		return this.uniqueId;
	}

	public SettingsInfo getSettings() {
		return this.settings;
	}

	public UUID getCurrentMatchID() {
		return this.currentMatchID;
	}

	public int getBlockedHits() {
		return this.blockedHits;
	}

	public double getWastedHP() {
		return this.wastedHP;
	}

	public UUID getDuelSelecting() {
		return this.duelSelecting;
	}

	public int getMatchesPlayed() {
		return this.matchesPlayed;
	}

	public boolean isAcceptingDuels() {
		return this.acceptingDuels;
	}

	public boolean isAllowingSpectators() {
		return this.allowingSpectators;
	}

	public boolean isScoreboardEnabled() {
		return this.scoreboardEnabled;
	}

	public int getCheatFreeMatches() {
		return this.cheatFreeMatches;
	}

	public int getMinemanID() {
		return this.minemanID;
	}

	public int getEloRange() {
		return this.eloRange;
	}

	public int getPingRange() {
		return this.pingRange;
	}

	public int getTeamID() {
		return this.teamID;
	}

	public int getRematchID() {
		return this.rematchID;
	}

	public int getMissedPots() {
		return this.missedPots;
	}

	public int getLongestCombo() {
		return this.longestCombo;
	}

	public int getCombo() {
		return this.combo;
	}

	public int getHits() {
		return this.hits;
	}

	public int getCriticalHits() {
		return this.criticalHits;
	}

	public int getPremiumMatchesPlayed() {
		return this.premiumMatchesPlayed;
	}

	public int getPremiumMatchesExtra() {
		return this.premiumMatchesExtra;
	}

	public int getPremiumLosses() {
		return this.premiumLosses;
	}

	public int getPotionsThrown() {
		return this.potionsThrown;
	}

	public int getPremiumWins() {
		return this.premiumWins;
	}

	public int getPremiumElo() {
		return this.premiumElo;
	}

	public KillMessages getKillMessage() {
		return this.killMessage;
	}

	public void setKillMessage(KillMessages killMessage) {
		this.killMessage = killMessage;
	}

	public SpecialEffects getKillEffect() {
		return this.killEffect;
	}

	public void setKillEffect(SpecialEffects killEffect) {
		this.killEffect = killEffect;
	}
}
 