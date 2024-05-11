package com.conaxgames.command.impl.essentials;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.Color;
import com.conaxgames.util.finalutil.PlayerUtil;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.stream.Stream;

/**
 * Created by Marko on 25.11.2018.
 */
public class RenameCommand extends Command {

    public RenameCommand() {
        super("rename");
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
            player.sendMessage(Color.translate("&cUsage: /rename <message>"));
            return false;
        }

        ItemStack stack = player.getItemInHand();

        if(stack == null || stack.getType().equals(Material.AIR)) {
            player.sendMessage(Color.translate("&cYou must hold an item to do this."));
            return false;
        }

        StringBuilder message = new StringBuilder();
        Stream.of(args).forEach(arg -> message.append(arg).append(" "));

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Color.translate(message.toString().trim()));
        stack.setItemMeta(meta);

        player.updateInventory();
        player.sendMessage(Color.translate(CC.SECONDARY + "Renaming your " + CC.PRIMARY + StringUtils.capitalize(stack.getType().name().toLowerCase().replace("_", "")) + CC.SECONDARY + " to " + message + CC.SECONDARY + "."));
        return false;
    }
}