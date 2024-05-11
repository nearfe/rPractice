package club.minion.practice.commands;

import dev.lugami.spigot.utils.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitHelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("kithelp")) {
            if (player.hasPermission("op")) {
                player.sendMessage(CC.CHAT_BAR);
                player.sendMessage(CC.translate("&7&oKit Setup Help"));
                player.sendMessage(CC.CHAT_BAR);
                player.sendMessage(CC.translate("&6 * &e/kit <create/delete> <kitName>"));
                player.sendMessage(CC.translate("&6 * &e/kit <enable/disable> <kitName>"));
                player.sendMessage(CC.translate("&6 * &e/kit ranked <kitName>"));
                player.sendMessage(CC.translate("&6 * &e/kit icon <kitName>"));
                player.sendMessage(CC.translate("&6 * &e/kit <setInv/getInv> <kitName>"));
                player.sendMessage(CC.translate("&6 * &e/kit <setEditInv/getEditInv> <kitName>"));
                player.sendMessage(CC.translate("&6 * &e/kit <setRefillInv/getRefillInv> <kitName>"));
                player.sendMessage(CC.translate("&6 * &e/kit <build/combo/sumo/spleef/boxing> <kitName>"));
                player.sendMessage(CC.translate("&6 * &e/kit whitelistArena <kitName> <arenaName>"));
                player.sendMessage(CC.translate("&6 * &e/kit excludeArena <kitName> <arenaName>"));
                player.sendMessage(CC.translate("&6 * &e/kit excludeArenaFromAllKitsBut <kitName> <arenaName>"));
                player.sendMessage(CC.CHAT_BAR);
                return true;
            }
        }
        return false;
    }
}
