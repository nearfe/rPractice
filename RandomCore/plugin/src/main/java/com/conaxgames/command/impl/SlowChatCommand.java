package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.manager.MinemanManager;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Text;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.PlayerUtil;
import org.bukkit.entity.Player;

public class SlowChatCommand implements CommandHandler {

	@Command(name = {"slowchat"}, rank = Rank.TRAINEE, description = "Slow down the chat.")
	public void slowChat(Player sender, @Text(name = "time") String text) {
		try {
			int time = Integer.parseInt(text);
			MinemanManager minemanManager = CorePlugin.getInstance().getPlayerManager();
			minemanManager.setChatSlowDownTime((long) time * 1000L);
			PlayerUtil.messageRank(minemanManager.getChatSlowDownTime() > 0 ? CC.SECONDARY + "Public chat is now in slow mode. " + CC.GRAY + "(" + time + " seconds)" : CC.SECONDARY + "Public chat is no longer in slow mode.");
		} catch (NumberFormatException e) {
			sender.sendMessage(CC.RED + "Invalid number.");
		}
	}

}
