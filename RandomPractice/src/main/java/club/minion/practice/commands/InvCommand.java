package club.minion.practice.commands;

import com.conaxgames.util.finalutil.CC;
import club.minion.practice.Practice;
import club.minion.practice.inventory.InventorySnapshot;
import java.util.UUID;
import java.util.regex.Pattern;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
public class InvCommand extends Command {
	private final static Pattern UUID_PATTERN = Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");
	private final static String INVENTORY_NOT_FOUND = CC.RED + "Inventory not found.";

	private final Practice plugin = Practice.getInstance();

	public InvCommand() {
		super("inv");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		if (args.length == 0) {
			return true;
		}
		if (!args[0].matches(InvCommand.UUID_PATTERN.pattern())) {
			sender.sendMessage(InvCommand.INVENTORY_NOT_FOUND);
			return true;
		}

		InventorySnapshot snapshot = this.plugin.getInventoryManager().getSnapshot(UUID.fromString(args[0]));
		if (snapshot == null) {
			sender.sendMessage(InvCommand.INVENTORY_NOT_FOUND);
		} else {
			((Player) sender).openInventory(snapshot.getInventoryUI().getCurrentPage());
		}
		return true;
	}
}
