package com.conaxgames.command.impl.punish;

import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;

public class BlacklistCommand extends PunishCommand {

	public BlacklistCommand() {
		super(Rank.MANAGER, "blacklist", "Blacklist a player.", CC.RED + "Usage: /blacklist <player> [reason]",
				PunishType.BLACKLIST);
	}

}
