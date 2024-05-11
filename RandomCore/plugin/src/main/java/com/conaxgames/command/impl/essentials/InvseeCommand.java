package com.conaxgames.command.impl.essentials;

import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.Color;
import com.conaxgames.util.finalutil.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Marko on 29.08.2018.
 */
public class InvseeCommand extends Command {

    public InvseeCommand() {
        super("invsee");

        setUsage(Color.translate("&cUsage: /invsee <player>"));
        setDescription("See players inventories.");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if(!PlayerUtil.testPermission(player, Rank.ADMIN)) {
            player.sendMessage(Color.translate("&cNo permission."));
            return false;
        }

        if(args.length == 0) {
            player.sendMessage(usageMessage);
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(target == null) {
            player.sendMessage(Color.translate("&cFailed to find that player."));
            return false;
        }

        player.sendMessage(Color.translate(CC.SECONDARY + "Viewing the inventory of: " + CC.PRIMARY + target.getName() + CC.SECONDARY + "."));
        getInventory(player, target);
        return false;
    }

    private void getInventory(Player player, Player target) {
        Inventory inventory = Bukkit.createInventory(null, 54, "Inventory: " + target.getDisplayName());

        inventory.setContents(target.getInventory().getContents());

        ItemStack[] armor = target.getInventory().getArmorContents();

        inventory.setItem(45, armor[0]);
        inventory.setItem(46, armor[1]);
        inventory.setItem(47, armor[2]);
        inventory.setItem(48, armor[3]);

        player.openInventory(inventory);
    }
}
