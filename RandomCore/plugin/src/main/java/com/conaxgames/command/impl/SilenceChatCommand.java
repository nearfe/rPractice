package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.manager.MinemanManager;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import org.bukkit.entity.Player;

public class SilenceChatCommand implements CommandHandler {

	@Command(name = {"silencechat", "mutechat"}, rank = Rank.TRAINEE, description = "Silence the chat.")
	public void onMuteChat(Player sender) {
		MinemanManager minemanManager = CorePlugin.getInstance().getPlayerManager();
		minemanManager.setChatSilenced(!minemanManager.isChatSilenced());
		CorePlugin.getInstance().getServer().broadcastMessage(
				minemanManager.isChatSilenced() ? CC.SECONDARY + "Public chat is now silenced." :
						CC.SECONDARY + "Public chat is no longer silenced.");
	}

}
