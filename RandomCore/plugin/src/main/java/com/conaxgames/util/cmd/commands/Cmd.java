package com.conaxgames.util.cmd.commands;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import com.conaxgames.CorePlugin;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.ParamType;
import com.conaxgames.util.cmd.param.ParamData;
import com.conaxgames.util.cmd.param.Parameter;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.PlayerUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Cmd extends Command {

	private final Permission permission;
	private final boolean requiresOp;
	@Getter
	private List<Pair<Parameter<?>, ParamData>> arguments = new ArrayList<>();
	@Setter
	private Pair<Method, Object> invoke;
	private Rank rank;
	private boolean mineman;

	public Cmd(String label, Rank rank, Permission permission, boolean requiresOp, boolean mineman) {
		super(label);
		this.rank = rank;
		this.permission = permission;
		this.requiresOp = requiresOp;
		this.mineman = mineman;
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!PlayerUtil.testPermission(commandSender, this.rank, this.permission, this.requiresOp)) {
			return false;
		}

		int i = 0;
		ArrayList<Object> parameters = new ArrayList<>();
		if (commandSender instanceof Player && mineman) {
			Mineman mineman = CorePlugin.getInstance().getPlayerManager()
					.getPlayer(((Player) commandSender).getUniqueId());
			parameters.add(mineman);
		} else {
			parameters.add(commandSender);
		}
		StringBuilder usage = new StringBuilder(CC.RED + "Usage: /" + label + " ");

		for (Pair<Parameter<?>, ParamData> paramPair : arguments) {

			if (paramPair.getValue().getType() == ParamType.MULTI) {
				usage.append(CC.RED).append("[").append(paramPair.getValue().getName()).append(CC.RED)
						.append("] ");
				parameters
						.add(paramPair.getKey().transform(commandSender, StringUtils.join(args, ' ', i, args.length)));
				break;
			}

			if (paramPair.getValue().getType() == ParamType.FLAG) {
				usage.append(CC.RED).append("(").append(CC.AQUA).append("-")
						.append(paramPair.getValue().getName()).append(CC.RED).append(") ");
			} else {
				usage.append(CC.RED).append("<").append(paramPair.getValue().getName()).append(CC.RED)
						.append("> ");
			}

			String argument;
			try {
				argument = args[i];
			} catch (ArrayIndexOutOfBoundsException ex) {
				argument = null;
			}

			if (argument == null) {
				if (!paramPair.getValue().getDefaultValue().equalsIgnoreCase("")) {
					argument = paramPair.getValue().getDefaultValue();
				} else {
					if (paramPair.getValue().getType() != ParamType.FLAG) {
						i++;
						continue;
					} else {
						parameters.add(false);
						continue;
					}
				}
			}

			if (paramPair.getValue().getType() == ParamType.FLAG) {
				if (argument.startsWith("-") &&
						(argument).replace("-", "").equalsIgnoreCase(paramPair.getRight().getName())) {
					parameters.add(true);
					i++;
					continue;
				} else {
					parameters.add(false);
					continue;
				}
			}
			Object object = paramPair.getKey().transform(commandSender, argument);
			if (object == null) {
				return true;
			}


			parameters.add(object);
			i++;
		}

		try {
			this.invoke.getKey().setAccessible(true);
			this.invoke.getKey().invoke(this.invoke.getValue(), parameters.toArray());
			return true;
		} catch (IllegalArgumentException e) {
			commandSender.sendMessage(
					usage.toString() + (description.equals("") ? "" : CC.GRAY + ": " + description));
			return false;
		} catch (InvocationTargetException ite) {
			if (ite.getTargetException() instanceof IllegalArgumentException) {
				commandSender.sendMessage(
						usage.toString() + (description.equals("") ? "" : CC.GRAY + ": " + description));
				return false;
			} else {
				ite.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


}
