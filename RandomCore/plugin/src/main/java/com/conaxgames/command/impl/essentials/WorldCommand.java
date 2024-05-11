package com.conaxgames.command.impl.essentials;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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
public class WorldCommand extends Command {

    public WorldCommand() {
        super("world");
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

        if(args.length == 0) {
            player.sendMessage(Color.translate("&cUsage: /world <world>"));
            sendWorlds(player);
            return false;
        }

        World world = Bukkit.getWorld(args[0]);

        if(world == null) {
            player.sendMessage(Color.translate("&cFailed to find that world."));
            sendWorlds(player);
            return false;
        }

        player.teleport(world.getSpawnLocation() != null ? world.getSpawnLocation() : new Location(world, 0, 100, 0));
        player.sendMessage(Color.translate(CC.SECONDARY + "Teleporting you to " + CC.PRIMARY + world.getName() + CC.SECONDARY + " world."));
        return false;
    }

    private void sendWorlds(Player player) {
        StringBuilder builder = new StringBuilder();

        Bukkit.getWorlds().forEach(world -> {
            if(builder.length() > 0) {
                builder.append(", ");
            }

            builder.append(world.getName());
        });

        player.sendMessage(Color.translate("&cAvailable worlds: " + builder.toString()));
    }
}
