package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.event.player.PrivateMessageEvent;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.MessageFilter;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.Text;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.*;
import org.bukkit.entity.Player;

import java.sql.Timestamp;

public class MessageCommand implements CommandHandler {

	@Command(name = {"msg", "tell", "w", "m", "message"}, rank = Rank.NORMAL, description = "Message a player.")
	public void message(Player player,
	                    @Param(name = "player") String targetName,
	                    @Text(name = "message") String message) {
		Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
		if (mineman.isBanned()) {
			player.sendMessage(CC.RED + "You cannot speak while you are banned.");
			return;
		}

		if (mineman.isMuted()) {
			if (mineman.getMuteTime() != null && System.currentTimeMillis() - mineman.getMuteTime().getTime() > 0L) {
				mineman.setMuted(false);
				mineman.setMuteTime(new Timestamp(0L));
			} else {
				if (mineman.getMuteTime() == null) {
					player.sendMessage(StringUtil.PERMANENT_MUTE);
				} else {
					player.sendMessage(String.format(StringUtil.TEMPORARY_MUTE,
							TimeUtil.millisToRoundedTime(
									Math.abs(System.currentTimeMillis() - mineman.getMuteTime().getTime()))));
				}
				return;
			}
		}

		Player target = CorePlugin.getInstance().getServer().getPlayer(targetName);
		if (target == null) {
			/*
			CorePlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(CorePlugin.getInstance(),
					() -> {
						JedisStorage storage = CorePlugin.getInstance().getCoreRedisManager().getStorage();

						String targetUuid = storage.get("player-names", targetName);
						if (targetUuid == null) {
							player.sendMessage(CC.RED + "Player is not online.");
							return;
						}

						String targetRank = storage.get("player-ranks", targetName);

						boolean shouldFilter = MessageFilter.shouldFilter(message);
						if (!shouldFilter || mineman.hasRank(Rank.TRAINEE)) {
							if (shouldFilter) {
								player.sendMessage(CC.RED + "That would of been filtered.");
							}
						} else {
							PlayerUtil.messageRank(
									CC.RED + "[Filtered] " + CC.PRIMARY + "(" + mineman.getRank().getColor() + player.getName() +
											CC.PRIMARY + " -> " + targetRank + targetName + CC.PRIMARY + ") " +
											message);
						}
						CorePlugin.getInstance().getCoreRedisManager().sendMessage(message,
								mineman.getRank(), player.getName(), UUID.fromString(targetUuid), shouldFilter);
					}); */
			player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, targetName));
			return;
		} else if (target.getUniqueId() == player.getUniqueId()) {
			player.sendMessage(CC.RED + "You cannot message yourself.");
			return;
		}

		Mineman targetMineman = CorePlugin.getInstance().getPlayerManager().getPlayer(target.getUniqueId());
		if (targetMineman == null) {
			player.sendMessage(CC.RED + "Failed to find that player.");
			return;
		}
		if (!mineman.hasRank(Rank.HOST) && targetMineman.isMuted()) {
			player.sendMessage(CC.RED + "This player is currently muted.");
			return;
		}
		if (!targetMineman.isCanSeeMessages() && !mineman.hasRank(Rank.HOST)) {
			player.sendMessage(CC.RED + "This player has messages toggled off.");
			return;
		}

		PrivateMessageEvent privateMessageEvent = new PrivateMessageEvent(mineman, targetMineman,
				mineman.getDisplayRank().getColor() + player.getName(),
				targetMineman.getDisplayRank().getColor() + target.getName());

		CorePlugin.getInstance().getServer().getPluginManager().callEvent(privateMessageEvent);

		if (privateMessageEvent.isCancelled()) {
			return;
		}

		String[] messages = StringUtil.formatPrivateMessage(mineman.getDisplayRank().getColor() + player.getName(),
				targetMineman.getDisplayRank().getColor() + target.getName(), message);

		player.sendMessage(messages[0]);

		mineman.setLastConversation(target.getName());

		boolean shouldFilter = MessageFilter.shouldFilter(message);

		if(message.contains("IÌ‡") || message.contains("İ")) {
			CorePlugin.getInstance().getFilterManager().handleCommand("mute " + player.getName() + " Sending crash codes -s");
			player.sendMessage(Color.translate("&cYou have been muted for &eCrash Codes&c."));
			player.sendMessage(Color.translate("&cIf you beleive this is false, join our TeamSpeak (ts.minion.lol)"));
			return;
		}

		if (!shouldFilter || mineman.hasRank(Rank.HOST)) {
			if (shouldFilter) {
				player.sendMessage(CC.RED + "That would have been filtered.");
			}

			if (!targetMineman.isIgnoring(mineman.getId()) || mineman.hasRank(Rank.HOST)) {
				target.sendMessage(messages[1]);
				targetMineman.setLastConversation(player.getName());
			}
		} else {
			PlayerUtil.messageRank(
					CC.RED + "[Filtered] " + CC.DARK_GRAY + "(" + mineman.getDisplayRank().getColor() + player.getName() +
							CC.DARK_GRAY + " -> " + targetMineman.getDisplayRank().getColor() + target.getName() + CC.DARK_GRAY + ") " + CC.RED +
							message);
		}
	}

}
