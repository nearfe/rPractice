package com.conaxgames.util.cmd.param.impl.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import com.conaxgames.util.cmd.param.Parameter;
import com.conaxgames.util.finalutil.CC;

public class WorldParameter extends Parameter<World> {

	@Override
	public World transfer(CommandSender sender, String argument) {
		World world = Bukkit.getWorld(argument);
		if (world == null) {
			sender.sendMessage(CC.RED + "World \'" + argument + "\' not found!");
			return null;
		}

		return world;
	}

}
