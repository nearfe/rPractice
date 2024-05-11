package com.conaxgames.util;

import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.PlayerUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.conaxgames.CorePlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class PlayerList {

	private final List<Player> players;

	public static PlayerList getVisiblyOnline(CommandSender sender) {
		return getOnline().visibleTo(sender);
	}

	public static PlayerList getOnline() {
		return new PlayerList(new ArrayList<>(CorePlugin.getInstance().getServer().getOnlinePlayers()));
	}

	public PlayerList visibleTo(CommandSender sender) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			this.players.removeIf(other -> other != player && !player.canSee(other));
		}
		return this;
	}

	public PlayerList canSee(CommandSender sender) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			this.players.removeIf(other -> other == player || !other.canSee(player));
		}
		return this;
	}

	public PlayerList visibleRankSorted() {
		this.players.sort(PlayerUtil.VISIBLE_RANK_ORDER);
		return this;
	}

	public List<String> asColoredNames() {
		return this.players.stream()
				.map(Player::getUniqueId)
				.map(uuid -> CorePlugin.getInstance().getPlayerManager().getPlayer(uuid))
				.map(mineman -> mineman.getDisplayRank().getColor() + mineman.getDisplayName() + CC.RED)
				.collect(Collectors.toList());
	}

}
