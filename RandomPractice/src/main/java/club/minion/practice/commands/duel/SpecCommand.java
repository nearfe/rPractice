package club.minion.practice.commands.duel;

import com.conaxgames.CorePlugin;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.StringUtil;
import club.minion.practice.Practice;
import club.minion.practice.match.Match;
import club.minion.practice.match.MatchTeam;
import club.minion.practice.party.Party;
import club.minion.practice.player.PlayerData;
import club.minion.practice.player.PlayerState;
import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpecCommand extends Command {
	private final Practice plugin = Practice.getInstance();

	public SpecCommand() {
		super("spec");
		this.setDescription("Spectate a player's match.");
		this.setUsage(CC.RED + "Usage: /spec <player>");
		this.setAliases(Arrays.asList("sp", "spect", "spectate"));
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player player = (Player) sender;

		if (args.length < 1) {
			player.sendMessage(usageMessage);
			return true;
		}
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
		Party party = this.plugin.getPartyManager().getParty(playerData.getUniqueId());

		if (party != null || (playerData.getPlayerState() != PlayerState.SPAWN && playerData.getPlayerState() != PlayerState.SPECTATING)) {
			player.sendMessage(CC.RED + "You can't do this in your current state.");
			return true;
		}

		Player target = this.plugin.getServer().getPlayer(args[0]);

		if (target == null) {
			player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
			return true;
		}
		PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());

		if (targetData.getPlayerState() != PlayerState.FIGHTING) {
			player.sendMessage(CC.RED + "Player is not in a match.");
			return true;
		}
		Match targetMatch = this.plugin.getMatchManager().getMatch(targetData);
		Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());

		if (!targetMatch.isParty()) {
			if (!mineman.hasRank(Rank.TRAINEE)) {
				if (!targetData.isAllowingSpectators()) {
					player.sendMessage(CC.RED + "This player is not allowing spectators.");
					return true;
				}
				MatchTeam team = targetMatch.getTeams().get(0);
				MatchTeam team2 = targetMatch.getTeams().get(1);
				PlayerData otherPlayerData = this.plugin.getPlayerManager().getPlayerData(team.getPlayers().get(0) == target.getUniqueId() ? team2.getPlayers().get(0) : team.getPlayers().get(0));
				if (otherPlayerData != null && !otherPlayerData.isAllowingSpectators()) {
					player.sendMessage(CC.RED + "The player this player is dueling is not allowing spectators.");
					return true;
				}
			}
		}
		if (playerData.getPlayerState() == PlayerState.SPECTATING) {
			Match match = this.plugin.getMatchManager().getSpectatingMatch(player.getUniqueId());

			if (match.equals(targetMatch)) {
				player.sendMessage(CC.RED + "You are already spectating this match.");
				return true;
			}
			match.removeSpectator(player.getUniqueId());
		}
		player.sendMessage(CC.PRIMARY + "You are now spectating " + CC.SECONDARY + target.getName() + CC.PRIMARY + ".");
		this.plugin.getMatchManager().addSpectator(player, playerData, target, targetMatch);
		return true;
	}
}
