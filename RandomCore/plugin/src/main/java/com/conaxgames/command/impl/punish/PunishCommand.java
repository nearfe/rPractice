package com.conaxgames.command.impl.punish;

import com.conaxgames.CorePlugin;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.PlayerUtil;
import com.conaxgames.util.finalutil.StringUtil;
import com.conaxgames.util.finalutil.TimeUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Timestamp;

public abstract class PunishCommand extends Command {

	protected final CorePlugin plugin = CorePlugin.getInstance();
	private final PunishType type;
	private final Rank rank;

	PunishCommand(Rank rank, String name, String desc, String usage, PunishType type) {
		super(name);
		this.rank = rank;
		this.description = desc;
		this.usageMessage = usage + " [-s]";
		this.type = type;
	}

	@Override
	public final boolean execute(CommandSender sender, String alias, String[] args) {
		if (!PlayerUtil.testPermission(sender, rank)) {
			return true;
		}

		if (args.length < 1) {
			sender.sendMessage(usageMessage);
			return true;
		}

		long timeUntilThing = args.length > 1 ? TimeUtil.parseTime(args[1]) : 1;
		boolean temporary = args.length > 1 && timeUntilThing != -1;
		if (this.type == PunishType.BLACKLIST || this.type == PunishType.IPBAN ||
		    this.type.getName().toLowerCase().startsWith("un")) {
			temporary = false; // Can't temp blacklist ip-ban or unban :v
		}

		long time = 0;
		if (args.length > 1) {
			time = timeUntilThing;
		}

		String reason = StringUtil.buildMessage(args, temporary ? 2 : 1);

		Timestamp expiry = null;
		if (temporary) {
			expiry = new Timestamp(System.currentTimeMillis() + time);
		}

		boolean silent = reason.toLowerCase().endsWith("-s");

		if (silent && args.length > 2 && reason.length() > 3) {
			reason = reason.substring(0, reason.length() - 3);
		}

		if (args.length == 1 || (silent && args.length == 2) || (temporary && args.length == 2)
				|| (silent && temporary && args.length == 3)) {
			switch (type) {
				case BLACKLIST:
				case BAN:
				case IPBAN:
					reason = "Unfair Advantage";
					break;
				case UNBAN:
				case UNMUTE:
				case UNBLACKLIST:
					reason = "Unpunished";
					break;
				case KICK:
				case MUTE:
				default:
					reason = "Misconduct";
					break;
			}
		}

		String ip = null;
		Player target = this.plugin.getServer().getPlayerExact(args[0]);

		String name = args[0];
		if (target != null) {
			ip = target.getAddress().getAddress().getHostAddress();
			name = target.getName();
		}

		this.plugin.getPunishmentManager().punish(sender, (!temporary && type == PunishType.BAN ? PunishType.IPBAN : type), name, reason, ip, expiry, !silent, temporary);
		return true;
	}

	@Getter
	@RequiredArgsConstructor
	public enum PunishType {
		IPBAN("ipban"),
		BAN("ban"),
		BLACKLIST("blacklist"),
		UNBLACKLIST("unblacklist"),
		UNBAN("unban"),
		MUTE("mute"),
		UNMUTE("unmute"),
		KICK("kick");

		private final String name;
	}

}
