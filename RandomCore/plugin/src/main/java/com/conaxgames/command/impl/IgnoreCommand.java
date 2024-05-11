package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.api.impl.AddIgnoreRequest;
import com.conaxgames.api.impl.IgnoreNamesRequest;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

public class IgnoreCommand implements CommandHandler {

	@Command(name = { "ignore", "unignore" }, rank = Rank.NORMAL, description = "Ignore a player.")
	public void ignore(Player player, @Param(name = "player") String target) {
		Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());

		if(target.equals("list")) {
			CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(
					new IgnoreNamesRequest(mineman.getUuid()), element -> {
						JsonArray array = element.getAsJsonArray();

						StringBuilder builder = new StringBuilder();

						array.forEach(element1 -> {
							if(builder.length() > 0) {
								builder.append(CC.SECONDARY).append(", ");
							}

							builder.append(CC.PRIMARY).append(element1.getAsJsonObject().get("name").getAsString());
						});

						player.sendMessage(new String[] {
								"",
								CC.SECONDARY + "All users you have ignored...",
								array.size() == 0 ? CC.I_GRAY + "You have no users ignored."
										: CC.SECONDARY + "Users: " + CC.GRAY + "(" + array.size() + ")" + CC.SECONDARY + ": " + builder.toString(),
								""
						});
					});
			return;
		}

		CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(
				new AddIgnoreRequest(mineman.getUuid(), target), element -> {
					JsonObject data = element.getAsJsonObject();

					String response = data.get("response").getAsString();
					switch (response) {
						case "cant-ignore":
							player.sendMessage(CC.RED + "You cannot ignore " + target);
							break;
						case "player-not-found":
							player.sendMessage(CC.RED + "Failed to find that player.");
							break;
						case "success":
							if(mineman.toggleIgnore(data.get("target-id").getAsInt())) {
								player.sendMessage(CC.SECONDARY + "You are now ignoring " + CC.PRIMARY + target + CC.SECONDARY + ".");
							} else {
								player.sendMessage(CC.SECONDARY + "You are no longer ignoring " + CC.PRIMARY + target + CC.SECONDARY + ".");
							}
							break;
					}
				});
	}

}
