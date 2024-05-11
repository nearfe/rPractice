package com.conaxgames.util.cmd.param.impl;

import org.bukkit.command.CommandSender;
import com.conaxgames.util.cmd.param.Parameter;

public class StringParameter extends Parameter<String> {
	@Override
	public String transfer(CommandSender sender, String argument) {
		return argument;
	}
}
