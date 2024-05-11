package com.conaxgames.command.impl.essentials;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.Color;
import com.conaxgames.util.finalutil.PlayerUtil;

public class PingCommand extends Command {

    public PingCommand() {
        super("ping");

        setDescription("Get ping of players.");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if(args.length == 0) {
            player.sendMessage(Color.translate(CC.SECONDARY + "Your ping: " + CC.PRIMARY + PlayerUtil.getPing(player) + " ms"));
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(target == null) {
            player.sendMessage(Color.translate("&cFailed to find that player."));
            return false;
        }

        player.sendMessage(Color.translate(target.getDisplayName() + CC.SECONDARY + "'s ping: " + CC.PRIMARY + PlayerUtil.getPing(target) + " ms"));
        return false;
    }
}
