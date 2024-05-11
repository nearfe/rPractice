package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.api.impl.ChatUpdateRequest;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;

public class ToggleChatCommand implements CommandHandler {

	@Command(name = {"togglechat", "tgc", "globalchat"}, rank = Rank.NORMAL,
			description = "Toggle chat to all messages except staff messages")
	public void toggleChat(Mineman mineman) {
		mineman.setChatEnabled(!mineman.isChatEnabled());
		mineman.getPlayer().sendMessage(CC.SECONDARY + "You are now " + CC.PRIMARY + (mineman.isChatEnabled() ? "able" : "unable") + CC.SECONDARY + " to see public chat.");
		CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(
				new ChatUpdateRequest("chatEnabled", mineman.isChatEnabled(), mineman.getId())
		);
	}
}
