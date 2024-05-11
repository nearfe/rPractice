package club.minion.practice.commands;

import club.minion.practice.Practice;
import club.minion.practice.kit.Kit;
import com.conaxgames.util.finalutil.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class SetStatsCommand extends Command {

    private final Practice plugin = Practice.getInstance();

    public SetStatsCommand() {
        super("setstats");
        this.setUsage(ChatColor.RED + "Usage: /setstats [player] [kit] [elo]");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("practice." + this.getName().toLowerCase() + ".command")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
                return true;
            }
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /setstats <player> <kit> <elo>");
            return true;
        }

        Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
            return true;
        }

        if (args.length == 3) {
            int elo = Integer.parseInt(args[2]);
            String kitName = args[1];
            Kit kit = this.plugin.getKitManager().getKit(kitName);
            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "That kit does not exist.");
                return true;
            }
            this.plugin.getPlayerManager().getPlayerData(target.getUniqueId()).setElo(kitName, elo);
            sender.sendMessage(ChatColor.GREEN + target.getName() + "'s " + kitName + " elo has been modified. New ELO: " + elo);
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /setstats <player> <kit> <elo>");
        }
        return true;
    }
}