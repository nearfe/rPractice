package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.api.impl.TimeUpdateRequest;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;

/**
 * Created by Marko on 19.12.2018.
 */
public class TimeCommands implements CommandHandler {

    @Command(name = {"day"}, rank = Rank.NORMAL, description = "Change your time to day.")
    public void setDay(Mineman player) {
        player.setWorldTime("DAY");
        player.getPlayer().setPlayerTime(6000L, false);
        player.getPlayer().sendMessage(CC.SECONDARY + "Your time has been updated to " + CC.PRIMARY + "Day" + CC.SECONDARY + ".");

        CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(
                new TimeUpdateRequest(player.getWorldTime(), player.getId())
        );
    }

    @Command(name = {"night"}, rank = Rank.NORMAL, description = "Change your time to night.")
    public void setNight(Mineman player) {
        player.setWorldTime("NIGHT");
        player.getPlayer().setPlayerTime(18000L, false);
        player.getPlayer().sendMessage(CC.SECONDARY + "Your time has been updated to " + CC.PRIMARY + "Night" + CC.SECONDARY + ".");

        CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(
                new TimeUpdateRequest(player.getWorldTime(), player.getId())
        );
    }

    @Command(name = {"sunset"}, rank = Rank.NORMAL, description = "Change your time to sunset.")
    public void setSunset(Mineman player) {
        player.setWorldTime("SUNSET");
        player.getPlayer().setPlayerTime(12000L, false);
        player.getPlayer().sendMessage(CC.SECONDARY + "Your time has been updated to " + CC.PRIMARY + "Sunset" + CC.SECONDARY + ".");

        CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(
                new TimeUpdateRequest(player.getWorldTime(), player.getId())
        );
    }
}