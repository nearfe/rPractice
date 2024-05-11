package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.api.impl.PunishHistoryRequest;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Flag;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.HttpUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import org.bukkit.command.CommandSender;

import java.sql.Timestamp;

public class PunishmentHistoryCommand implements CommandHandler {

	@Command(name = {"history", "hist"}, rank = Rank.TRAINEE, description = "Get a player's punishment history.")
	public void history(CommandSender sender, @Param(name = "target") String name, @Flag(name = "u") boolean upload) {
		CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(new PunishHistoryRequest(name), data -> {
			JsonArray array = data.getAsJsonArray();
			if (array.size() == 0) {
				sender.sendMessage(CC.RED + "Player has not been punished.");
				return;
			}

			/*StringBuilder sb = new StringBuilder(CC.YELLOW + "Viewing punishment history for: " + CC.GRAY + name + "\n");

			for (JsonElement element : array) {
				JsonObject object = element.getAsJsonObject();

				sb.append(CC.GRAY).append("[").append(new Timestamp(object.get("timestamp").getAsLong()))
						.append("] ").append(CC.GOLD).append("[").append(object.get("type").getAsString().toUpperCase()).append("] ");

				if (upload) {
					sb.append("\t");
				}

				//sb.append(name).append(" was punished by ").append(object.get("punisher").getAsString())
				//		.append(" for \"").append(object.get("reason").getAsString()).append("\"");
                sb.append(CC.RED).append("Added by ").append(object.get("punisher").getAsString())
                        .append(" for \"").append(CC.YELLOW).append(object.get("reason").getAsString()).append(CC.RED).append("\"").append(CC.RED);

				JsonElement duration = object.get("expiry");
				if (duration != null && !(duration instanceof JsonNull)) {
					if (upload) {
						sb.append("\t");
					}
					sb.append(" until ").append(CC.GRAY).append(new Timestamp(duration.getAsLong()));
				}

				sb.append("\n");
			}*/

			StringBuilder sb = new StringBuilder(CC.SECONDARY + "Punishments for " + CC.PRIMARY + name + CC.SECONDARY + "...\n");

			sb.append("\n");

			array.forEach(element -> {
				JsonObject object = element.getAsJsonObject();

				sb.append(CC.SECONDARY).append(getType(object.get("type").getAsString()))
						.append(" by ")
						.append(CC.PRIMARY).append(object.get("punisher").getAsString())
						.append(CC.SECONDARY).append(" for ").append(CC.PRIMARY)
						.append(object.get("reason").getAsString()).append(CC.SECONDARY).append(". ")
						.append(CC.GRAY).append("(").append(new Timestamp(object.get("timestamp").getAsLong())).append(")");

				if(upload) {
					sb.append("\t");
				}

				JsonElement duration = object.get("expiry");
				if(duration != null && !(duration instanceof JsonNull)) {
					if(upload) {
						sb.append("\t");
					}

					sb.append(CC.SECONDARY).append("\n  Expires on ").append(CC.PRIMARY).append(new Timestamp(duration.getAsLong()));
					sb.append("\n");
				}

				sb.append("\n");
			});

			if (upload) {
				CorePlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> sender.sendMessage(CC.PRIMARY + "Player " + CC.SECONDARY + name + CC.PRIMARY + "'s Punishment History: " + CC.SECONDARY + "https://www.hastebin.com/" + HttpUtil.getHastebin(sb.toString()) + CC.PRIMARY + "."));
			} else {
                sender.sendMessage("");
				sender.sendMessage(sb.toString());
				sender.sendMessage("");
			}
		});
	}

	private String getType(String input) {
		return input.equalsIgnoreCase("PERM-IPBAN") || input.equalsIgnoreCase("PERM-BAN") ? "Banned"
				: input.equalsIgnoreCase("TEMP-BAN") ? "Temporarily Banned"
				: input.equalsIgnoreCase("TEMP-MUTE") ? "Temporarily Muted"
				: input.equalsIgnoreCase("PERM-MUTE") ? "Muted"
				: input.equalsIgnoreCase("PERM-BLACKLIST") ? "Blacklisted"
				: input.equalsIgnoreCase("UNBLACKLIST") ? "Unblacklisted"
				: input.equalsIgnoreCase("UNBAN") ? "Unbanned"
				: input.equalsIgnoreCase("UNMUTE") ? "Unmuted"
				: input.equalsIgnoreCase("PERM-KICK") || input.equalsIgnoreCase("TEMP-KICK") ? "Kicked" : "Unknown";
	}
}
