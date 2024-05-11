package com.conaxgames.util.cmd;

import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.annotation.Flag;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.Text;
import com.conaxgames.util.cmd.annotation.commandTypes.BaseCommand;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.cmd.annotation.commandTypes.SubCommand;
import com.conaxgames.util.cmd.commands.BaseCommandComponent;
import com.conaxgames.util.cmd.commands.Cmd;
import com.conaxgames.util.cmd.commands.SubCommandComponent;
import com.conaxgames.util.cmd.param.ParamData;
import com.conaxgames.util.cmd.param.Parameter;
import com.conaxgames.util.cmd.param.impl.*;
import com.conaxgames.util.cmd.param.impl.bukkit.PlayerParameter;
import com.conaxgames.util.cmd.param.impl.bukkit.WorldParameter;
import com.conaxgames.util.cmd.param.impl.serverdata.ServerDataParameter;
import com.conaxgames.util.cmd.param.impl.serverdata.WrappedServerData;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class CommandManager {

	@Getter
	private Map<Class, Parameter<?>> parameterMap = new HashMap<>();
	private Map<String, String> baseCommandMap = new HashMap<>();
	private CommandMap commandMap;

	public CommandManager() {
		try {
			final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			bukkitCommandMap.setAccessible(true);
			commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
		} catch (Exception e) {
			e.printStackTrace();
		}

		registerParameter(String.class, new StringParameter());
		registerParameter(boolean.class, new BooleanParameter());
		registerParameter(Player.class, new PlayerParameter());
		registerParameter(double.class, new DoubleParameter());
		registerParameter(float.class, new FloatParameter());
		registerParameter(int.class, new IntegerParameter());
		registerParameter(World.class, new WorldParameter());
		registerParameter(Rank.class, new RankParameter());
		registerParameter(WrappedServerData.class, new ServerDataParameter());
	}

	public void registerParameter(Class clazz, Parameter<?> parameter) {
		parameterMap.put(clazz, parameter);
	}


	public void registerAllClasses(Collection<CommandHandler> classes) {
		for (CommandHandler commandHandler : classes) {
			// We check this one first because for sub command to work, we need to make sure ALL baseCommands are
			// registered first.
			for (Method method : commandHandler.getClass().getMethods()) {
				if (method.isAnnotationPresent(BaseCommand.class)) {
					handleBaseCommand(method, commandHandler);
				}

				if (method.isAnnotationPresent(Command.class)) {
					handleCommand(method, commandHandler);
				}
			}

			for (Method method : commandHandler.getClass().getMethods()) {
				if (method.isAnnotationPresent(SubCommand.class)) {
					handleSubCommand(method, commandHandler);
				}
			}
		}
	}

	private void handleSubCommand(Method method, CommandHandler commandHandler) {
		SubCommand subCommand = method.getAnnotation(SubCommand.class);
		String mainCommandName = baseCommandMap.get(subCommand.baseCommand());
		if (mainCommandName == null) {
			throw new NullPointerException("SubCommand has a null name (" + subCommand.baseCommand() + ")");
		}
		BaseCommandComponent baseCommandComponent = (BaseCommandComponent) commandMap.getCommand(mainCommandName);
		if (baseCommandComponent == null) {
			throw new NullPointerException("BaseCommand was null for a subcommand (" + subCommand.baseCommand() + ")");
		}

		String commandName = subCommand.name()[0];
		List<String> alias = Arrays
				.asList(StringUtils.join(subCommand.name(), ' ', 1, subCommand.name().length).split(" "));
		alias.remove(commandName);
		Rank rank = baseCommandComponent.getRank().max(subCommand.rank());

		Permission permission = null;
		if (!subCommand.permission().isEmpty()) {
			permission = new Permission(subCommand.permission(), subCommand.permissionDefault());
		}

		SubCommandComponent subCommandComponent = new SubCommandComponent(subCommand.name()[0],
				subCommand.description(), rank, permission, subCommand.requiresOp(), alias, method.getParameters()[0].getType() == Mineman.class);
		subCommandComponent.setInvoke(Pair.of(method, commandHandler));
		subCommandComponent.getArguments().addAll(getArguments(method.getParameters()));
		for (String subCommands : subCommand.name()) {
			baseCommandComponent.getSubCommands().put(subCommands.toLowerCase(), subCommandComponent);
		}
	}

	private void handleBaseCommand(Method method, CommandHandler classWithCommand) {
		BaseCommand baseCommand = method.getAnnotation(BaseCommand.class);
		String commandName = baseCommand.name()[0];
		for (String alias : baseCommand.name()) {
			baseCommandMap.put(alias, commandName);
		}

		Permission permission = null;
		if (!baseCommand.permission().isEmpty()) {
			permission = new Permission(baseCommand.permission(), baseCommand.permissionDefault());
		}

		BaseCommandComponent baseCommandComponent = new BaseCommandComponent(commandName, baseCommand.rank(), permission, baseCommand.requiresOp());
		List<String> alias = Arrays
				.asList(StringUtils.join(baseCommand.name(), ' ', 1, baseCommand.name().length).split(" "));
		baseCommandComponent.setAliases(alias);
		baseCommandComponent.setInvoke(Pair.of(method, classWithCommand));
		if (method.getParameters().length > 1) {
			throw new IllegalStateException("There should only be 1 param for a basecommand (The sender)");
		}
		commandMap.register(commandName, baseCommandComponent);
	}

	private void handleCommand(Method method, CommandHandler classWithCommand) {
		Command command = method.getAnnotation(Command.class);
		String commandName = command.name()[0];

		List<String> alias = Arrays.asList(StringUtils.join(command.name(), ' ', 1, command.name().length).split(" "));

		Permission permission = null;
		if (!command.permission().isEmpty()) {
			permission = new Permission(command.permission(), command.permissionDefault());
		}

		Cmd cmd = new Cmd(commandName, command.rank(), permission, command.requiresOp(), method.getParameters()[0].getType() == Mineman.class);
		cmd.setAliases(alias);
		cmd.setDescription(command.description());
		cmd.getArguments().addAll(getArguments(method.getParameters()));
		cmd.setInvoke(Pair.of(method, classWithCommand));

		commandMap.register(commandName, cmd);
	}

	private List<Pair<Parameter<?>, ParamData>> getArguments(java.lang.reflect.Parameter[] parameters) {
		List<Pair<Parameter<?>, ParamData>> arguments = new ArrayList<>();
		boolean finalValue = false;
		boolean firstRun = true;
		for (java.lang.reflect.Parameter parameter : parameters) {
			if (firstRun) {
				firstRun = false;
				continue;
			}
			String paramName = parameter.getType().getSimpleName();
			String defaultValue = "";
			ParamType type = ParamType.ARGUMENT;

			if (finalValue) {
				throw new IllegalArgumentException("There was a argument after a multi argument, idk what to do!");
			}

			if (!parameterMap.containsKey(parameter.getType())) {
				throw new IllegalArgumentException("Parameter: " + parameter.getType() + " not registered!");
			}

			if (parameter.isAnnotationPresent(Text.class)) {
				finalValue = true;
				type = ParamType.MULTI;
				defaultValue = parameter.getAnnotation(Text.class).value();
				paramName = parameter.getAnnotation(Text.class).name();
			}

			if (parameter.isAnnotationPresent(Param.class)) {
				paramName = parameter.getAnnotation(Param.class).name();
				defaultValue = parameter.getAnnotation(Param.class).defaultTo();
			}

			if (parameter.isAnnotationPresent(Flag.class)) {
				if (parameter.getType() != boolean.class) {
					throw new IllegalArgumentException("Parameter: " + parameter.getType() + " must be a boolean!");
				}

				paramName = parameter.getAnnotation(Flag.class).name();
				arguments.add(Pair.of(null, new ParamData(paramName, defaultValue, ParamType.FLAG)));
				continue;
			}

			arguments.add(Pair.of(parameterMap.get(parameter.getType()), new ParamData(paramName, defaultValue,
					type)));
		}
		return arguments;
	}

}
