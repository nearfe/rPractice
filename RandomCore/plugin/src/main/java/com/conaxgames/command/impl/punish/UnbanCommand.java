package com.conaxgames.command.impl.punish;

import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;

public class UnbanCommand extends PunishCommand {
	public UnbanCommand() {
		super(Rank.ADMIN, "unban", "Unban a player.", CC.RED + "Usage: /unban <player>", PunishType.UNBAN);
	}
}
