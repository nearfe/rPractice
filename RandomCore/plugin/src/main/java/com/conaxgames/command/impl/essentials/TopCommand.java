package com.conaxgames.command.impl.essentials;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.Color;
import com.conaxgames.util.finalutil.PlayerUtil;

/**
 * Created by Marko on 25.11.2018.
 */
public class TopCommand extends Command {
    
    public TopCommand() {
        super("top");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(!(sender instanceof Player)) {
            return false;
        }
        
        if(!PlayerUtil.testPermission(sender, Rank.ADMIN)) {
            sender.sendMessage(Color.translate("&cNo permission."));
            return false;
        }
        
        Player player = (Player) sender;
        
        Location l = player.getLocation();
        player.teleport(new Location(l.getWorld(), l.getX(), l.getWorld().getHighestBlockYAt(l.getBlockX(), l.getBlockZ()), l.getZ(), l.getYaw(), l.getPitch()));
        player.sendMessage(Color.translate(CC.SECONDARY +"Teleporting you to the highest location."));
        return false;
    }
}
