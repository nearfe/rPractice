package com.conaxgames.command.impl.punish;

import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;

public class UnmuteCommand extends PunishCommand {
	public UnmuteCommand() {
		super(Rank.SENIORMOD, "unmute", "Unmute a player.", CC.RED + "Usage: /unmute <player>", PunishType.UNMUTE);
	}
}
