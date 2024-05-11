package com.conaxgames.command.impl.essentials;

import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.Color;
import com.conaxgames.util.finalutil.PlayerUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Marko on 28.11.2018.
 */
public class SpeedCommand extends Command {

    public SpeedCommand() {
        super("speed");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if(!PlayerUtil.testPermission(sender, Rank.ADMIN)) {
            player.sendMessage(Color.translate("&cNo permission."));
            return false;
        }

        if(args.length < 2) {
            player.sendMessage(Color.translate("&cUsage: /speed <fly|walk> <speed>"));
            return false;
        }

        int amount;
        
        switch (args[0].toLowerCase()) {
            case "f":
            case "fly":
                if(!isInteger(args[1])) {
                    player.sendMessage(Color.translate("&cFailed to parse integer."));
                    return false;
                }

                amount = Integer.parseInt(args[1]);

                if(amount < 1 || amount > 10) {
                    player.sendMessage(Color.translate("&cSpeed limit is 10."));
                    return false;
                }

                player.setFlySpeed(amount * 0.1F);

                player.sendMessage(Color.translate(CC.SECONDARY + "You have set your fly speed to " + CC.PRIMARY + amount + CC.SECONDARY + "."));
                break;
            case "w":    
            case "walk":
                if(!isInteger(args[1])) {
                    player.sendMessage(Color.translate("&cFailed to parse integer."));
                    return false;
                }

                amount = Integer.parseInt(args[1]);

                if(amount < 1 || amount > 10) {
                    player.sendMessage(Color.translate("&cSpeed limit is 10."));
                    return false;
                }

                player.setWalkSpeed(amount * 0.1F);
                player.sendMessage(Color.translate(CC.SECONDARY + "You have set your walk speed to " + CC.PRIMARY + amount + CC.SECONDARY + "."));
                break;
        }

        return false;
    }

    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }
        
        return true;
    }
}