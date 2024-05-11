package com.conaxgames.util.cmd.param;

import lombok.Getter;
import org.bukkit.command.CommandSender;

public abstract class Parameter<T> {

	@Getter
	private String argument;

	public T transform(CommandSender sender, String arguments) {
		this.argument = arguments;
		return transfer(sender, arguments);
	}

	public abstract T transfer(CommandSender sender, String argument);
}
