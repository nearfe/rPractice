package club.minion.practice.commands;

import club.minion.practice.Practice;
import club.minion.practice.killmessages.menu.KillMessagesMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillMessageCommand extends Command {
    private final Practice plugin = Practice.getInstance();

    public KillMessageCommand() {
        super("killmessage");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }

        Player player = (Player) commandSender;
        new KillMessagesMenu().openMenu(player);

        return true;
    }
}
