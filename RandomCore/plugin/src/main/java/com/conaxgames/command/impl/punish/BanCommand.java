package com.conaxgames.command.impl.punish;

import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;

public class BanCommand extends PunishCommand {
	public BanCommand() {
		super(Rank.MOD, "ban", "Ban a player.", CC.RED + "Usage: /ban <player> [time] [reason]", PunishType.BAN);
	}
}
