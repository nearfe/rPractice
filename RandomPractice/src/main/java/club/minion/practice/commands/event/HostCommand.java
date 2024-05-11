package club.minion.practice.commands.event;

import com.conaxgames.clickable.Clickable;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import club.minion.practice.Practice;
import club.minion.practice.events.EventState;
import club.minion.practice.events.PracticeEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class HostCommand extends org.bukkit.command.Command {
	private final Practice plugin = Practice.getInstance();

	// Constructor for HostCommand
	public HostCommand() {
		super("event host");
		this.setDescription("Host an event.");
		this.setUsage("/host <event_name>");
		this.setPermission("practice.hostEvent");
		this.setAliases(Arrays.asList("host"));
	}

	@Command(name = "event host", rank = Rank.BASIC, description = "Host an event.")
	public void hostEvent(Player player, @Param(name = "event") String eventName) {
		if (eventName == null) {
			return;
		}

		if (plugin.getEventManager().getByName(eventName) == null) {
			player.sendMessage(CC.RED + eventName + " doesn't exist.");
			return;
		}

		PracticeEvent event = plugin.getEventManager().getByName(eventName);
		if (event.getState() != EventState.UNANNOUNCED) {
			player.sendMessage(CC.RED + "This event is already in progress!");
			return;
		}

		boolean eventBeingHosted = plugin.getEventManager().getEvents().values().stream().anyMatch(e -> e.getState() != EventState.UNANNOUNCED);
		if (eventBeingHosted) {
			player.sendMessage(CC.RED + "An event is already being hosted!");
			return;
		}

		Clickable message = new Clickable(CC.B_GOLD + player.getName() + " is hosting " + event.getName() + "! Click to join!",
				CC.GREEN + "Click to join!",
				"/joinevent " + event.getName());
		plugin.getServer().getOnlinePlayers().forEach(message::sendToPlayer);

		Practice.getInstance().getEventManager().hostEvent(event, player);
	}

	@Override
	public boolean execute(CommandSender commandSender, String s, String[] strings) {
		if (commandSender instanceof Player) {
			Player player = (Player) commandSender;
			if (strings.length > 0) {
				hostEvent(player, strings[0]);
				return true;
			}
		}
		return false;
	}
}
