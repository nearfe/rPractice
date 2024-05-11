package com.conaxgames.util.cmd.param.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.conaxgames.CorePlugin;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.param.Parameter;
import com.conaxgames.util.finalutil.CC;

public class RankParameter extends Parameter<Rank> {

	@Override
	public Rank transfer(CommandSender sender, String argument) {
		Rank rank = Rank.getByName(argument);
		if (argument.equalsIgnoreCase("")) {
			rank = Rank.NORMAL;
		}

		if (argument.equalsIgnoreCase("self") && sender instanceof Player) {
			rank = CorePlugin.getInstance().getPlayerManager().getPlayer(((Player) sender).getUniqueId()).getRank();
		}

		if (rank == null) {
			sender.sendMessage(CC.RED + "There is no rank with the name \'" + argument + "\'.");
			return null;
		}


		return rank;
	}
}
