package club.minion.practice.inventory;

import club.minion.practice.Practice;
import club.minion.practice.match.Match;
import club.minion.practice.player.PlayerData;
import club.minion.practice.util.MathUtil;
import com.conaxgames.inventory.InventoryUI;
import com.conaxgames.util.ItemBuilder;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.ItemUtil;
import com.conaxgames.util.finalutil.StringUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.json.simple.JSONObject;

import java.util.*;

@lombok.Getter
public class InventorySnapshot {

	private final InventoryUI inventoryUI;
	private final ItemStack[] originalInventory;
	private final ItemStack[] originalArmor;

	@lombok.Getter
	private final UUID snapshotId = UUID.randomUUID();

	public InventorySnapshot(Player player, Match match) {
		ItemStack[] contents = player.getInventory().getContents();
		ItemStack[] armor = player.getInventory().getArmorContents();

		this.originalInventory = contents;
		this.originalArmor = armor;

		PlayerData profile = Practice.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());

		double health = player.getHealth();
		double food = (double) player.getFoodLevel();

		List<String> potionEffectStrings = new ArrayList<>();

		for (PotionEffect potionEffect : player.getActivePotionEffects()) {
			String romanNumeral = MathUtil.convertToRomanNumeral(potionEffect.getAmplifier() + 1);
			String effectName = StringUtil.toNiceString(potionEffect.getType().getName().toLowerCase());
			String duration = MathUtil.convertTicksToMinutes(potionEffect.getDuration());

			potionEffectStrings.add(CC.PRIMARY + effectName + " " + romanNumeral + CC.SECONDARY + " (" + duration + ")");
		}

		this.inventoryUI = new InventoryUI(CC.B_GRAY + "Inventory of" + CC.PRIMARY + player.getName(), 6);

		for (int i = 0; i < 9; i++) {
			this.inventoryUI.setItem(i + 27, new InventoryUI.EmptyClickableItem(contents[i]));
			this.inventoryUI.setItem(i + 18, new InventoryUI.EmptyClickableItem(contents[i + 27]));
			this.inventoryUI.setItem(i + 9, new InventoryUI.EmptyClickableItem(contents[i + 18]));
			this.inventoryUI.setItem(i, new InventoryUI.EmptyClickableItem(contents[i + 9]));
		}

		boolean potionMatch = false;
		boolean soupMatch = false;

		for (ItemStack item : match.getKit().getContents()) {
			if (item == null) {
				continue;
			}
			if (item.getType() == Material.MUSHROOM_SOUP) {
				soupMatch = true;
				break;
			} else if (item.getType() == Material.POTION && item.getDurability() == (short) 16421) {
				potionMatch = true;
				break;
			}
		}

		if (potionMatch) {
			int potCount = (int) Arrays.stream(contents).filter(Objects::nonNull).map(ItemStack::getDurability).filter(d -> d == 16421).count();

			this.inventoryUI.setItem(49, new InventoryUI.EmptyClickableItem(
					new ItemBuilder(Material.POTION)
							.name(CC.SECONDARY + "Health Potions: " + CC.PRIMARY + potCount)
							.amount(potCount).durability(16421)
							.lore(CC.SECONDARY + "Potions Thrown: " + CC.PRIMARY + profile.getPotionsThrown())
							.lore(CC.SECONDARY + "Missed Potions: " + CC.PRIMARY + profile.getMissedPots())
							.lore(CC.SECONDARY + "Potions Wasted: " + CC.PRIMARY + profile.getWastedHP())
							.lore(CC.SECONDARY + "Potion Accuracy: " + CC.PRIMARY + (profile.getMissedPots() > 0 ? (int) (((28.0 - (double) profile.getMissedPots()) / 28.0) * 100.0) + "%" : "100%"))
							.build()));

		} else if (soupMatch) {
			int soupCount = (int) Arrays.stream(contents).filter(Objects::nonNull).map(ItemStack::getType).filter(d -> d == Material.MUSHROOM_SOUP).count();

			this.inventoryUI.setItem(49, new InventoryUI.EmptyClickableItem(ItemUtil.createItem(
					Material.MUSHROOM_SOUP, CC.SECONDARY + "Soups Left: " + CC.PRIMARY + soupCount, soupCount, (short) 16421)));
		}

		final double roundedHealth = Math.round(health / 2.0 * 2.0) / 2.0;

		this.inventoryUI.setItem(45,
				new InventoryUI.EmptyClickableItem(ItemUtil.createItem(Material.MELON, CC.SECONDARY + "Health: " + CC.PRIMARY + roundedHealth + "/10.0 " + StringEscapeUtils.unescapeJava("\u2764"), (int) Math.round(health / 2.0D))));

		final double roundedFood = Math.round(health / 2.0 * 2.0) / 2.0;

		this.inventoryUI.setItem(46,
				new InventoryUI.EmptyClickableItem(ItemUtil.createItem(Material.COOKED_BEEF, CC.SECONDARY + "Hunger: " + CC.PRIMARY + roundedFood + "/10.0", (int) Math.round(food / 2.0D))));

		this.inventoryUI.setItem(47,
				new InventoryUI.EmptyClickableItem(ItemUtil.reloreItem(
						ItemUtil.createItem(Material.POTION, CC.SECONDARY + "Potion Effects", potionEffectStrings.size())
						, potionEffectStrings.toArray(new String[]{}))));

		this.inventoryUI.setItem(48, new InventoryUI.EmptyClickableItem(
				new ItemBuilder(Material.DIAMOND_SWORD).name(CC.SECONDARY + "Statistics").
						lore(CC.SECONDARY + "Longest Combo: " + CC.PRIMARY + profile.getLongestCombo() + " Hit" + (profile.getLongestCombo() > 1 ? "s" : "")).
						lore(CC.SECONDARY + "Total Hits: " + CC.PRIMARY + profile.getHits() + " Hit" + (profile.getHits() > 1 ? "s" : "")).
						lore(CC.SECONDARY + "Critical Hits: " + CC.PRIMARY + profile.getCriticalHits()).
						lore(CC.SECONDARY + "Blocked Hits: " + CC.PRIMARY + profile.getBlockedHits())
						.build()));


		if (!match.isParty()) {
			this.inventoryUI.setItem(53, new InventoryUI.AbstractClickableItem(
					ItemUtil.reloreItem(ItemUtil.createItem(Material.LEVER, CC.SECONDARY + "Opponent's Inventory"), CC.GRAY + "See your opponent's inventory", CC.GRAY + "and combat details.")) {
					@Override
					public void onClick(InventoryClickEvent inventoryClickEvent) {
						Player clicker = (Player) inventoryClickEvent.getWhoClicked();

						if (Practice.getInstance().getMatchManager().isRematching(player.getUniqueId())) {
							clicker.closeInventory();
							Practice.getInstance().getServer().dispatchCommand(clicker, "inv " + Practice.getInstance().getMatchManager().getRematcherInventory(player.getUniqueId()));
						}
					}
				});
			}
		}

	public JSONObject toJson() {
		JSONObject object = new JSONObject();

		JSONObject inventoryObject = new JSONObject();
		for (int i = 0; i < this.originalInventory.length; i++) {
			inventoryObject.put(i, this.encodeItem(this.originalInventory[i]));
		}
		object.put("inventory", inventoryObject);

		JSONObject armourObject = new JSONObject();
		for (int i = 0; i < this.originalArmor.length; i++) {
			armourObject.put(i, this.encodeItem(this.originalArmor[i]));
		}
		object.put("armour", armourObject);

		return object;
	}

	private JSONObject encodeItem(ItemStack itemStack) {
		if (itemStack == null || itemStack.getType() == Material.AIR) {
			return null;
		}

		JSONObject object = new JSONObject();
		object.put("material", itemStack.getType().name());
		object.put("durability", itemStack.getDurability());
		object.put("amount", itemStack.getAmount());

		JSONObject enchants = new JSONObject();
		for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
			enchants.put(enchantment.getName(), itemStack.getEnchantments().get(enchantment));
		}
		object.put("enchants", enchants);

		return object;
	}

}
