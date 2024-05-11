package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.api.impl.PlayerRequest;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.PlayerUtil;
import com.conaxgames.util.finalutil.TimeUtil;
import com.google.gson.JsonObject;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.conaxgames.event.player.RankChangeEvent;

public class RankCommand implements CommandHandler {

	@Command(name = "group", rank = Rank.ADMIN, description = "Set the rank of a player.")
	public void rank(CommandSender sender, @Param(name = "target") String target,
	                 @Param(name = "rank") Rank rank,
	                 @Param(name = "duration", defaultTo = "perm") String time) {

		if (!PlayerUtil.testPermission(sender, rank)) {
			return;
		}

		if (sender instanceof Player) {
			Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(((Player) sender).getUniqueId());
			if (mineman.hasRank(Rank.ADMIN) && !mineman.hasRank(Rank.MANAGER)) {
				if (rank.hasRank(Rank.TRAINEE)) {
					sender.sendMessage(CC.RED + "You cannot set staff ranks.");
					return;
				}
			}
		}
		long duration = TimeUtil.toMillis(time.equals("perm") ? null : time);
		int giverId = sender instanceof Player ?
				CorePlugin.getInstance().getPlayerManager().getPlayer(((Player) sender).getUniqueId()).getId() : -1;

		CorePlugin.getInstance().getRequestProcessor()
				.sendRequestAsync(new PlayerRequest.RankUpdateRequest(target, rank, duration, giverId),
						element -> {
							JsonObject data = element.getAsJsonObject();

							String response = data.get("response").getAsString();
							switch (response) {
								case "success":
									CorePlugin.getInstance().getServer().getLogger().info(sender.getName() + " updated " + target + " permissive rank to " + rank.getName());

									sender.sendMessage(CC.SECONDARY + "You have given " + CC.PRIMARY + target + CC.SECONDARY + " the " + rank.getColor() + rank.getName() + CC.SECONDARY + " rank.");

									OfflinePlayer player = CorePlugin.getInstance().getServer().getOfflinePlayer(target);
									if(player.isOnline()) {
										Mineman targetMineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());

										if(targetMineman != null) {
											CorePlugin.getInstance().getServer().getPluginManager().callEvent(new RankChangeEvent(player.getUniqueId(), targetMineman.getRank(), rank));
											targetMineman.setRank(rank);
										}
									} else {
										CorePlugin.getInstance().getServer().getPluginManager().callEvent(new RankChangeEvent(player.getUniqueId(), null, rank));
									}
									break;
								case "player-not-found":
									sender.sendMessage(CC.RED + "Failed to find that player.");
									break;
							}
						});
	}

}
