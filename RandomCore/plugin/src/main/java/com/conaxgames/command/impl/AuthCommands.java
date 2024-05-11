package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.api.impl.AuthenticationRequest;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.auth.TimeBasedOneTimePasswordUtil;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.PlayerUtil;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.security.GeneralSecurityException;

public class AuthCommands implements CommandHandler {
    @Command(name = {"auth", "2fa"})
    public void authCommand(Mineman mineman,
                            @Param(name = "code") String code) {
        Player player = mineman.getPlayer();
        if (mineman.isAuthExempt()) {
            player.sendMessage(CC.RED + "You're exempt from 2FA.");
            return;
        }

        if (mineman.getAuthSecret() == null) {
            player.sendMessage(CC.RED + "You have not yet setup 2FA. Use /setup2fa to begin.");
            return;
        }

        String correctCode;
        try {
            correctCode = TimeBasedOneTimePasswordUtil.generateCurrentNumberString(mineman.getAuthSecret());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            player.sendMessage(CC.RED + "An error occurred, try again later.");
            return;
        }

        if (!correctCode.equals(code)) {
            player.sendMessage(CC.RED + "Invalid code.");
            return;
        }


        if (mineman.getAuthSecret() != null) {
            player.sendMessage(CC.RED + "You already have 2FA setup. If you need to remove it, contact a manager.");
            return;
        }

        mineman.setSetupAuth(false);
        mineman.setAuthSecret(TimeBasedOneTimePasswordUtil.generateBase32Secret());
        mineman.giveAuthMap();
    }

    @Command(name = "remove2fa", rank = Rank.MANAGER)
    public void remove2fa(CommandSender sender,
                       @Param(name = "target") String target,
                       @Param(name = "exempt") boolean exempt) {
        CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(
                new AuthenticationRequest(target, null, null, true, exempt),
                element -> {
                    JsonObject data = element.getAsJsonObject();

                    String response = data.get("response").getAsString();
                    switch (response) {
                        case "player-not-found":
                            sender.sendMessage(CC.RED + "Failed to find that player.");
                            break;
                        case "success":
                            sender.sendMessage(CC.GREEN +
                                    (exempt ? "Removed and exempted " + target + " from 2FA."
                                    : "Removed " + target + "'s 2FA."));
                            break;
                    }
                });
    }
}
