package club.minion.practice.commands.event;

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

public class JoinEventCommand extends org.bukkit.command.Command {
	private final Practice plugin = Practice.getInstance();

	public JoinEventCommand() {
		super("event join");
		this.setDescription("Join an event.");
		this.setUsage("/event join <event_name>");
		this.setPermission("practice.joinEvent");
	}

	@Command(name = "event join", rank = Rank.NORMAL, description = "Join an event.")
	public void joinEvent(Player player, @Param(name = "join") String eventName) {
		if (eventName == null) {
			return;
		}

		if (plugin.getEventManager().getByName(eventName) == null) {
			player.sendMessage(CC.RED + eventName + " doesn't exist.");
			return;
		}

		PracticeEvent event = plugin.getEventManager().getByName(eventName);
		if (event.getState() != EventState.WAITING) {
			player.sendMessage(CC.RED + "You cannot join this event!");
			return;
		}

		if (event.getPlayers().containsKey(player.getUniqueId())) {
			player.sendMessage(CC.RED + "You are already playing " + event.getName() + "!");
			return;
		}

		event.join(player);
	}

	@Override
	public boolean execute(CommandSender commandSender, String s, String[] strings) {
		return false;
	}
}
