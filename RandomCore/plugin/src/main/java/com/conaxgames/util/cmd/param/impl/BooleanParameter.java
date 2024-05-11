package com.conaxgames.util.cmd.param.impl;

import org.bukkit.command.CommandSender;
import com.conaxgames.util.cmd.param.Parameter;

public class BooleanParameter extends Parameter<Boolean> {
	@Override
	public Boolean transfer(CommandSender sender, String argument) {

		if (argument.equalsIgnoreCase("yes") || argument.equalsIgnoreCase("yep") || argument.equalsIgnoreCase("yea") || argument.equalsIgnoreCase("true")) {
			return true;
		} else {
			return false;
		}

	}
}
