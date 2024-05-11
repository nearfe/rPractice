package com.conaxgames.command.impl.essentials;

import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.Color;
import com.conaxgames.util.finalutil.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Created by Marko on 25.11.2018.
 */
public class SpawnerCommand extends Command {

    public SpawnerCommand() {
        super("spawner");
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
            player.sendMessage(Color.translate("&cUsage: /spawner <type>"));
            return false;
        }

        try {
            EntityType.valueOf(args[0].toUpperCase());
        } catch (Exception e) {
            player.sendMessage(Color.translate("&cSpawner " + args[0] + " doesn't exists."));
            return false;
        }

        Block block = player.getTargetBlock((Set<Material>) null, 5);

        if(block == null || block.getType() != Material.MOB_SPAWNER) {
            player.sendMessage(Color.translate("&cYou must be looking at spawner."));
            return false;
        }

        EntityType entityType = EntityType.valueOf(args[0].toUpperCase());

        CreatureSpawner spawner = (CreatureSpawner) block.getState();

        spawner.setSpawnedType(entityType);
        spawner.update();

        player.sendMessage(Color.translate(CC.SECONDARY + "You have updated spawner to " + CC.PRIMARY + entityType + CC.SECONDARY + "."));
        return false;
    }
}