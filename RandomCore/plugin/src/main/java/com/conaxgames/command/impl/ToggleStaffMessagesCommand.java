package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.api.impl.ChatUpdateRequest;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;

public class ToggleStaffMessagesCommand implements CommandHandler {

	@Command(name = {"togglestaffmessages", "tsm"}, rank = Rank.TRAINEE, description = "Disable appearance of staff messages.")
	public void toggleStaffMessages(Mineman player) {
		player.setCanSeeStaffMessages(!player.isCanSeeStaffMessages());
		player.getPlayer().sendMessage(CC.SECONDARY + "You are now " + CC.PRIMARY + (player.isCanSeeStaffMessages() ? "able" : "unable") + CC.SECONDARY + " to see staff messages.");

		CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(
				new ChatUpdateRequest("canSeeStaffMessages", player.isCanSeeStaffMessages(), player.getId())
		);
	}
}
