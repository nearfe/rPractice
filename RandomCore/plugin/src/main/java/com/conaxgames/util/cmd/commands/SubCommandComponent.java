package com.conaxgames.util.cmd.commands;

import com.conaxgames.util.cmd.param.ParamData;
import com.conaxgames.util.cmd.param.Parameter;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import com.conaxgames.CorePlugin;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.ParamType;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.PlayerUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Data
public class SubCommandComponent {

	private final Permission permission;
	private final boolean requiresOp;
	private String commandName;
	private String description;
	private Rank rank;
	private List<String> alias;
	private List<Pair<Parameter<?>, ParamData>> arguments = new ArrayList<>();
	private Pair<Method, Object> invoke;
	private boolean mineman;

	public SubCommandComponent(String name, String description, Rank rank, Permission permission, boolean requiresOp, List<String> alias, boolean mineman) {
		this.commandName = name;
		this.description = description;
		this.rank = rank;
		this.permission = permission;
		this.requiresOp = requiresOp;
		this.alias = alias;
		this.mineman = mineman;
	}

	public void execute(CommandSender sender, String commandLabel, String argumentLabel, String[] args) {
		if (!PlayerUtil.testPermission(sender, this.rank, this.permission, this.requiresOp)) {
			return;
		}

		List<Object> parameters = new ArrayList<>();
		if (sender instanceof Player && mineman) {
			Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(((Player) sender).getUniqueId());
			parameters.add(mineman);
		} else {
			parameters.add(sender);
		}

		int i = 1;
		StringBuilder usage = new StringBuilder(CC.RED + "Usage: /" + commandLabel + " " + argumentLabel + " ");

		for (Pair<Parameter<?>, ParamData> paramPair : arguments) {

			if (paramPair.getValue().getType() == ParamType.MULTI) {
				usage.append(CC.RED).append("[").append(paramPair.getValue().getName()).append(CC.RED)
						.append("] ");
				parameters.add(paramPair.getKey().transform(sender, StringUtils.join(args, ' ', i, args.length)));
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
					if (paramPair.getValue().getType() == ParamType.FLAG) {
						parameters.add(false);
						continue;
					} else {
						i++;
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

			Object object = paramPair.getKey().transform(sender, argument);
			if (object == null) {
				sender.sendMessage(CC.RED + "Illegal argument: " + argument + " was not expected.");
				return;
			}

			parameters.add(object);
			i++;
		}

		try {
			this.invoke.getKey().setAccessible(true);
			this.invoke.getKey().invoke(this.invoke.getValue(), parameters.toArray());
		} catch (IllegalArgumentException e) {
			sender.sendMessage(usage.toString() + (description.equals("") ? "" : CC.GRAY + ": " + description));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
