package com.conaxgames.util.cmd.param.impl.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.conaxgames.util.cmd.param.Parameter;
import com.conaxgames.util.finalutil.CC;

public class PlayerParameter extends Parameter<Player> {
	@Override
	public Player transfer(CommandSender sender, String value) {
		if (sender instanceof Player && (value.equalsIgnoreCase("self") || value.equals(""))) {
			return (Player) sender;
		}

		Player player = Bukkit.getPlayer(value);

		if (player == null) {
			sender.sendMessage(CC.RED + "No player with the name \"" + value + "\" found.");
			return null;
		}

		return player;
	}
}
