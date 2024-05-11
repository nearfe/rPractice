package com.conaxgames.command.impl.punish;

import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;

public class KickCommand extends PunishCommand {
	public KickCommand() {
		super(Rank.TRAINEE, "kick", "Kick a player.", CC.RED + "Usage: /kick <player> [reason]", PunishType.KICK);
	}
}
