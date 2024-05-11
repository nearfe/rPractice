package club.minion.practice.commands;

import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import club.minion.practice.Practice;
import club.minion.practice.player.PlayerData;
import club.minion.practice.player.PlayerState;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class FlyCommand implements CommandHandler {

	private final Practice plugin = Practice.getInstance();

	@Command(name = "fly", description = "Toggle flight", rank = Rank.ELITE)
	public void onFly(Player player) {
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

		if (playerData.getPlayerState() != PlayerState.SPAWN) {
			player.sendMessage(CC.RED + "You can't do that in this state.");
			return;
		}

		player.setAllowFlight(!player.getAllowFlight());

		if (player.getAllowFlight()) {
			player.sendMessage(CC.GREEN + "You are now able to fly.");
		} else {
			player.sendMessage(CC.RED + "You are no longer able to fly.");
		}
	}

}
