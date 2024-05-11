package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.api.impl.ChatUpdateRequest;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;

public class ToggleMessagesCommand implements CommandHandler {

    @Command(name = {"togglemessages", "tpm", "toggleprivatemessages"}, rank = Rank.NORMAL,
            description = "Toggle private messages")
    public void toggleMessages(Mineman mineman){
        mineman.setCanSeeMessages(!mineman.isCanSeeMessages());
        mineman.getPlayer().sendMessage(CC.SECONDARY + "You are now " + CC.PRIMARY + (mineman.isCanSeeMessages() ? "able" : "unable") + CC.SECONDARY + " to see private messages.");

        CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(
                new ChatUpdateRequest("canSeeMessages", mineman.isCanSeeMessages(), mineman.getId())
        );
    }

}
