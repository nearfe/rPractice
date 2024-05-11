package club.minion.practice.settings;

import club.minion.practice.Practice;
import club.minion.practice.killeffects.DeathEffectsMenu;
import club.minion.practice.killmessages.menu.KillMessagesMenu;
import club.minion.practice.player.PlayerData;
import club.minion.practice.util.CCUtil;
import com.conaxgames.CorePlugin;
import com.conaxgames.inventory.InventoryUI;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.settings.SettingsHandler;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;
import java.util.List;

public class PracticeSettingsHandler implements SettingsHandler  {

	private static final List<Integer> PING_RANGES = Arrays.asList(
			50, 75, 100, 125, 150, 200, 250, 300, -1
	);

	private static final String SETTINGS_COLOR = CC.LIGHT_PURPLE;

	private static final List<Integer> ELO_RANGES = Arrays.asList(
			250, 350, 500, 600, 750, 1000, -1
	);


	private final Practice plugin = Practice.getInstance();

	@Override
	public void onCreateSettings(InventoryUI inventoryUI, Player player) {
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
		Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
		inventoryUI.addItem(new InventoryUI.AbstractClickableItem(
				ItemUtil.createItem(Material.ENDER_PEARL,
						CC.PRIMARY + "Allow Spectators: "
								+ (playerData.isAllowingSpectators() ? CC.GREEN + "Enabled" : CC.RED + "Disabled"))) {
			@Override
			public void onClick(InventoryClickEvent inventoryClickEvent) {
				player.performCommand("tsp");
				player.closeInventory();
			}
		});
		inventoryUI.addItem(new InventoryUI.AbstractClickableItem(
				ItemUtil.createItem(Material.DIAMOND_SWORD,
						CC.PRIMARY + "Duel Requests: "
								+ (playerData.isAcceptingDuels() ? CC.GREEN + "Enabled" : CC.RED + "Disabled"))) {
			@Override
			public void onClick(InventoryClickEvent inventoryClickEvent) {
				player.performCommand("td");
				player.closeInventory();
			}
		});
		inventoryUI.addItem(new InventoryUI.AbstractClickableItem(
				ItemUtil.createItem(Material.BLAZE_POWDER,
						CC.PRIMARY	 + "Scoreboard: "
								+ (playerData.isScoreboardEnabled() ? CC.GREEN + "Enabled" : CC.RED + "Disabled"))) {
			@Override
			public void onClick(InventoryClickEvent inventoryClickEvent) {
				player.performCommand("tsb");
				player.closeInventory();
			}
		});

		inventoryUI.addItem(new InventoryUI.AbstractClickableItem(
				ItemUtil.createItem(Material.ENCHANTED_BOOK,
						CC.PRIMARY + "Matchmaking Settings")) {


			@Override
			public void onClick(InventoryClickEvent inventoryClickEvent) {
				if (!mineman.hasRank(Rank.ELITE)) {
					player.closeInventory();
					player.sendMessage(CC.RED + "You do not have permission to use this.");
					return;
				}

				player.closeInventory();
				PracticeSettingsHandler.this.openMatchmakingSettings(player, playerData, mineman);
			}
		});
		/**
		 * no dia que isso funcionar eu tiro do comentario
		 * - lugami
		inventoryUI.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.PAINTING, CC.PRIMARY + "Scoreboard Color")) {
			@Override
			public void onClick(InventoryClickEvent inventoryClickEvent) {
				player.closeInventory();
				PracticeSettingsHandler.this.openScoreboardColorMenu(player, playerData);
			}
		});
		 **/
		inventoryUI.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.BANNER, CC.PRIMARY + "Kill Messages")) {
			@Override
			public void onClick(InventoryClickEvent inventoryClickEvent) {
				player.closeInventory();
				new KillMessagesMenu().openMenu(player);
			}
		});
		inventoryUI.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.SKULL_ITEM, CC.PRIMARY + "Kill Effects")) {
			@Override
			public void onClick(InventoryClickEvent inventoryClickEvent) {
				player.closeInventory();
				new DeathEffectsMenu().openMenu(player);
			}
		});
	}
	public void openScoreboardColorMenu(Player player, PlayerData playerData) {
		InventoryUI colorMenu = new InventoryUI(SETTINGS_COLOR + "Scoreboard Color", 1);

		ChatColor[] colors = ChatColor.values();
			for (ChatColor color : colors) {
				if (color.isColor() && color != ChatColor.RESET && !color.toString().contains("§l") && !color.toString().contains("§o") && !color.toString().contains("§n") && !color.toString().contains("§m")) {
					String colorName = color.name();
					colorMenu.addItem(new InventoryUI.AbstractClickableItem(
							ItemUtil.createItem(Material.WOOL, SETTINGS_COLOR + colorName, 1)) {
						@Override
						public void onClick(InventoryClickEvent event) {
							plugin.getScoreboardColorManager().setScoreboardColor(player.getUniqueId(), CCUtil.getValue(colorName));
							player.sendMessage(SETTINGS_COLOR + "Scoreboard color set to " + colorName);
							player.closeInventory();
						}
					});
				}
			}

		player.openInventory(colorMenu.getCurrentPage());
	}
	private void openMatchmakingSettings(Player player, PlayerData playerData, Mineman mineman) {
		InventoryUI matchmakingUI = new InventoryUI(CC.PRIMARY + "Matchmaking Settings", 1);

		matchmakingUI.addItem(new InventoryUI.AbstractClickableItem(
				ItemUtil.createItem(Material.STICK, CC.PRIMARY + "Ping Range: " + CC.SECONDARY
						+ (playerData.getPingRange() == -1 ? "Unrestricted" : playerData.getPingRange()))) {
			@Override
			public void onClick(InventoryClickEvent event) {
				if (!mineman.hasRank(Rank.ELITE)) {
					player.sendMessage(CC.RED + "You do not have permission to use this");
					player.closeInventory();
					return;
				}

				String[] args = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).split(":");
				int range = PracticeSettingsHandler.this.handleRangeClick(event.getClick(),
						PracticeSettingsHandler.PING_RANGES, PracticeSettingsHandler.this.parseOrDefault(args[1], -1));

				playerData.setPingRange(range);
				event.getClickedInventory().setItem(0, ItemUtil.createItem(Material.STICK, CC.PRIMARY + "Ping Range: " + CC.SECONDARY
						+ (playerData.getPingRange() == -1 ? "Unrestricted" : playerData.getPingRange())));
			}
		});

		matchmakingUI.addItem(new InventoryUI.AbstractClickableItem(
				ItemUtil.createItem(Material.BLAZE_ROD, CC.PRIMARY + "ELO Range: " + CC.SECONDARY
						+ (playerData.getEloRange() == -1 ? "Unrestricted" : playerData.getEloRange()))) {
			@Override
			public void onClick(InventoryClickEvent event) {
				String[] args = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).split(":");
				int range = PracticeSettingsHandler.this.handleRangeClick(event.getClick(),
						PracticeSettingsHandler.ELO_RANGES, PracticeSettingsHandler.this.parseOrDefault(args[1], -1));

				playerData.setEloRange(range);
				event.getClickedInventory().setItem(1, ItemUtil.createItem(Material.BLAZE_ROD, CC.PRIMARY + "ELO Range: " + CC.SECONDARY
						+ (playerData.getEloRange() == -1 ? "Unrestricted" : playerData.getEloRange())));
			}
		});

		player.openInventory(matchmakingUI.getCurrentPage());
	}

	private int handleRangeClick(ClickType clickType, List<Integer> ranges, int current) {
		int min = ranges.get(0);
		int max = ranges.get(ranges.size() - 1);

		if (clickType == ClickType.LEFT) {
			if (current == max) {
				current = min;
			} else {
				current = ranges.get(ranges.indexOf(current) + 1);
			}
		} else if (clickType == ClickType.RIGHT) {
			if (current == min) {
				current = max;
			} else {
				current = ranges.get(ranges.indexOf(current) - 1);
			}
		}

		return current;
	}

	private int parseOrDefault(String string, int def) {
		try {
			return Integer.parseInt(string.replace(" ", ""));
		} catch (NumberFormatException e) {
			return def;
		}
	}
}