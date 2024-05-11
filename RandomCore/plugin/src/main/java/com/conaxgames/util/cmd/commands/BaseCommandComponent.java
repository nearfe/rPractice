package com.conaxgames.util.cmd.commands;

import com.conaxgames.util.cmd.annotation.commandTypes.SubCommand;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.PlayerUtil;
import com.conaxgames.util.finalutil.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Getter
public class BaseCommandComponent extends Command {

	private final Permission requiredPermission;
	private final boolean requiresOp;
	private Rank rank;
	private Map<String, SubCommandComponent> subCommands = new HashMap<>();
	@Setter
	private Pair<Method, Object> invoke;

	public BaseCommandComponent(String name, Rank rank, Permission permission, boolean requiresOp) {
		super(name);
		this.rank = rank;
		this.requiredPermission = permission;
		this.requiresOp = requiresOp;
	}

	@Override
	public boolean execute(CommandSender commandSender, String s, String[] args) {
		if (!PlayerUtil.testPermission(commandSender, this.rank, this.requiredPermission, this.requiresOp)) {
			commandSender.sendMessage(StringUtil.NO_PERMISSION);
			return false;
		}


		// Check if a subcommand should handle this first!
		if (args.length >= 1 && subCommands.containsKey(args[0].toLowerCase())) {
			String argumentCombined = String.join(" ", args);
			argumentCombined = argumentCombined.replace(args[0].toLowerCase(), "");
			subCommands.get(args[0].toLowerCase()).execute(commandSender, s, args[0], argumentCombined.split(" "));
			return true;
		}

		// Then check if we can invoke the command -- If its defined
		try {
			if (!invoke.getKey().isAnnotationPresent(SubCommand.class) && invoke.getKey().getParameters().length == 1) {
				this.invoke.getKey().setAccessible(true);
				this.invoke.getKey().invoke(this.invoke.getValue(), commandSender);
				return true;
			}
		} catch (InvocationTargetException ex) {
			if (!(ex.getTargetException() instanceof IllegalArgumentException)) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			if(!(e instanceof IllegalArgumentException)) {
				e.printStackTrace();
			}
		}

		// If no base command was defined, then lets make a usage message
		commandSender.sendMessage(CC.GRAY + CC.STRIKE_THROUGH + Strings.repeat("-", 52));
		for (Map.Entry<String, SubCommandComponent> st : subCommands.entrySet()) {
			commandSender.sendMessage(CC.RED + "/" + s + " " + st.getKey() + (st.getValue().getDescription().equals("") ? "" : CC.GRAY + ": " + CC.RED + st.getValue().getDescription()));
		}
		commandSender.sendMessage(CC.GRAY + CC.STRIKE_THROUGH + Strings.repeat("-", 52));
		return true;
	}
}
