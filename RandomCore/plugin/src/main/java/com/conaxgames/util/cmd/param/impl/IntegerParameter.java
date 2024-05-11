package com.conaxgames.util.cmd.param.impl;

import com.google.common.primitives.Ints;
import org.bukkit.command.CommandSender;
import com.conaxgames.util.cmd.param.Parameter;
import com.conaxgames.util.finalutil.CC;

public class IntegerParameter extends Parameter<Integer> {
	@Override
	public Integer transfer(CommandSender sender, String argument) {
		Integer integer = Ints.tryParse(argument);

		if (integer == null) {
			sender.sendMessage(CC.RED + "\'" + argument + "\' is not a number!");
			return null;
		}

		return integer;
	}
}
