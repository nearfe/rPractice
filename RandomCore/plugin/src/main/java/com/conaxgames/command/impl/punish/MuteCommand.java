package com.conaxgames.command.impl.punish;

import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;

public class MuteCommand extends PunishCommand {
	public MuteCommand() {
		super(Rank.TRAINEE, "mute", "Mute a player.", CC.RED + "Usage: /mute <player> [time] [reason]", PunishType.MUTE);
	}
}
