package com.conaxgames.command.impl.punish;

import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;

public class UnblacklistCommand extends PunishCommand {

	public UnblacklistCommand() {
		super(Rank.MANAGER, "unblacklist", "Un-blacklist a player.", CC.RED + "Usage: /unblacklist <player>", PunishType
				.UNBLACKLIST);
	}

}
