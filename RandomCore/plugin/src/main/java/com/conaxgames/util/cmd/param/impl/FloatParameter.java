package com.conaxgames.util.cmd.param.impl;

import com.google.common.primitives.Floats;
import org.bukkit.command.CommandSender;
import com.conaxgames.util.cmd.param.Parameter;
import com.conaxgames.util.finalutil.CC;

public class FloatParameter extends Parameter<Float> {

	@Override
	public Float transfer(CommandSender sender, String argument) {
		Float floatValue = Floats.tryParse(argument);

		if (argument.toLowerCase().contains("e")) {
			sender.sendMessage(CC.RED + "\'" + argument + "\' is not a valid number!");
			return null;
		}

		if (floatValue == null) {
			sender.sendMessage(CC.RED + "\'" + argument + "\' is not a valid number!");
			return null;
		}

		if (Float.isNaN(floatValue) || !Float.isFinite(floatValue)) {
			sender.sendMessage(CC.RED + "\'" + argument + "\' is not a valid number!");
			return null;
		}

		return floatValue;
	}


}
