package com.conaxgames.command.impl.punish;

import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;

public class IPBanCommand extends PunishCommand {

	public IPBanCommand() {
		super(Rank.ADMIN, "ipban", "IP-Ban a player.", CC.RED + "Usage: /ipban <player> [reason]", PunishType.IPBAN);
	}

}
