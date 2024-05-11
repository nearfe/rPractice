package com.conaxgames.util.cmd.param.impl;

import com.google.common.primitives.Doubles;
import org.bukkit.command.CommandSender;
import com.conaxgames.util.cmd.param.Parameter;
import com.conaxgames.util.finalutil.CC;

public class DoubleParameter extends Parameter<Double> {
	@Override
	public Double transfer(CommandSender sender, String argument) {
		Double doubleValue = Doubles.tryParse(argument);

		if (argument.toLowerCase().contains("e")) {
			sender.sendMessage(CC.RED + "\'" + argument + "\' is not a valid number!");
			return null;
		}

		if (doubleValue == null) {
			sender.sendMessage(CC.RED + "\'" + argument + "\' is not a valid number!");
			return null;
		}

		if (doubleValue.isNaN() || !Double.isFinite(doubleValue)) {
			sender.sendMessage(CC.RED + "\'" + argument + "\' is not a valid number!");
			return null;
		}

		return doubleValue;
	}
}
