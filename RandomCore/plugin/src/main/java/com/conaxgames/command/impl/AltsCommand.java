package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.api.impl.PlayerRequest;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

public class AltsCommand implements CommandHandler {

	@Command(name = {"alts", "alt"}, rank = Rank.SENIORMOD, description = "View a player's alts.")
	public void altsCommand(Player player, @Param(name = "player") String target) {
		CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(new PlayerRequest.AltsRequest(target), data -> {
			JsonArray array = data.getAsJsonArray();

			StringBuilder sb = new StringBuilder();

			array.forEach(obj -> {
				if(sb.length() > 0) {
					sb.append(CC.SECONDARY).append(", ");
				}

				JsonObject element = obj.getAsJsonObject();

				sb.append(CC.PRIMARY).append(element.get("banned").getAsBoolean() || element.get("ip_banned").getAsBoolean() || element.get("blacklisted").getAsBoolean() ? CC.ITALIC + CC.BOLD : "").append(element.get("name").getAsString());
				//sb.append(CC.PRIMARY).append(obj == null ? null : obj.toString().replace("\"", ""));
			});

			player.sendMessage(new String[] {
					"",
					CC.SECONDARY + "Binded accounts of " + CC.PRIMARY + target + CC.SECONDARY + "...",
					array.size() == 0 ? CC.I_GRAY + "This player has no binded accounts." : CC.SECONDARY + "Accounts: " + CC.GRAY + "(" + array.size() + ")" + CC.SECONDARY + ": " + sb.toString(),
					""
			});
		});
	}

}
