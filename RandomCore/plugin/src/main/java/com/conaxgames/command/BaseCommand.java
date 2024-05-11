package com.conaxgames.command;

import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.PlayerUtil;
import com.conaxgames.util.finalutil.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @since 2017-09-08
 */
@Getter
@Setter
public abstract class BaseCommand extends Command {

	private Rank requiredRank = Rank.NORMAL;
	private boolean playerOnly = false;

	public BaseCommand(String name) {
		super(name);
	}

	public BaseCommand(String name, String description, String usageMessage, List<String> aliases) {
		super(name, description, usageMessage, aliases);
	}

	public abstract boolean onExecute(CommandSender commandSender, String label, String[] args) throws Exception;

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (this.playerOnly && !(commandSender instanceof Player)) {
			commandSender.sendMessage(StringUtil.PLAYER_ONLY);
			return true;
		}

		try {
			return !PlayerUtil.testPermission(commandSender, this.requiredRank) ||
				   this.onExecute(commandSender, label, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
