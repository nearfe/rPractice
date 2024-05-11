package club.minion.practice.managers;

import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.ItemUtil;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class ItemManager {

	private final ItemStack spawnItems[];
	private final ItemStack queueItems[];
	private final ItemStack partyItems[];
	private final ItemStack tournamentItems[];
	private final ItemStack specItems[];
	private final ItemStack partySpecItems[];
	private final ItemStack eventitems[];
	private final ItemStack defaultBook;

	public ItemManager() {
		this.spawnItems = new ItemStack[]{
				ItemUtil.createItem(Material.STONE_SWORD, CC.YELLOW + "Join Unranked Queue"),
				ItemUtil.createItem(Material.IRON_SWORD, CC.GREEN + "Join Ranked Queue"),
				ItemUtil.createItem(Material.DIAMOND_SWORD, CC.GOLD + "Join Premium Queue"),
				null,
				null,
				null,
				ItemUtil.createItem(Material.EMERALD, CC.GREEN + "View LeaderBoards"),
				ItemUtil.createItem(Material.WATCH, CC.AQUA + "Settings"),
				ItemUtil.createItem(Material.BOOK, CC.GOLD + "Edit Kits")
		};
		this.queueItems = new ItemStack[]{
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				ItemUtil.createItem(Material.REDSTONE, CC.RED + "Leave Queue")
		};
		this.specItems = new ItemStack[]{
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				ItemUtil.createItem(Material.REDSTONE, CC.RED + "Leave Spectator Mode")
		};
		this.partySpecItems = new ItemStack[]{
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				ItemUtil.createItem(Material.NETHER_STAR, CC.RED + "Leave Party")
		};
		this.eventitems = new ItemStack[]{
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				ItemUtil.createItem(Material.NETHER_STAR, CC.RED + "Leave Event")
		};
		this.tournamentItems = new ItemStack[]{
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				ItemUtil.createItem(Material.NETHER_STAR, CC.RED + "Leave Tournament")
		};
		this.partyItems = new ItemStack[]{
				ItemUtil.createItem(Material.STONE_SWORD, CC.YELLOW + "Join 2v2 Unranked Queue"),
				ItemUtil.createItem(Material.IRON_SWORD, CC.GREEN + "Join 2v2 Ranked Queue"),
				null,
				null,
				ItemUtil.createItem(Material.DIAMOND_AXE, CC.AQUA + "Start Party Event"),
				ItemUtil.createItem(Material.IRON_AXE, CC.PRIMARY + "Fight Other Party"),
				null,
				null,
				ItemUtil.createItem(Material.NETHER_STAR, CC.RED + "Leave Party")
		};
		this.defaultBook = ItemUtil.createItem(Material.ENCHANTED_BOOK, CC.PRIMARY + "Default Kit");
	}
}
