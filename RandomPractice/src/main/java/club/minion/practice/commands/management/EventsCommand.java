package club.minion.practice.commands.management;

import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.PlayerUtil;
import club.minion.practice.Practice;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class EventsCommand extends Command {

    private final Practice plugin = Practice.getInstance();

    public EventsCommand() {
        super("events");
        setAliases(Arrays.asList("toggleevents", "togglee", "te"));
        this.setDescription("Manage server events.");
        this.setUsage(CC.RED + "Usage: /events");
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if(!(sender instanceof Player) || !PlayerUtil.testPermission(sender, Rank.MANAGER)) {
            return true;
        }

        boolean enabled = this.plugin.getEventManager().isEnabled();

        this.plugin.getEventManager().setEnabled(!enabled);
        sender.sendMessage(CC.GREEN + "Events are now " + (!enabled ? CC.GREEN + "enabled" : CC.RED + "disabled") + CC.GREEN + ".");

        return true;
    }
}

