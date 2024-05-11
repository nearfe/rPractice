package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.api.impl.PrefixRequest;
import com.conaxgames.inventory.InventoryUI;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.ItemBuilder;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.commandTypes.BaseCommand;
import com.conaxgames.util.cmd.annotation.commandTypes.SubCommand;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.Color;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class PrefixCommand implements CommandHandler {

	@BaseCommand(name = {"prefix", "prefixes"}, description = "Change your chat prefix")
	public void prefix(Player player) {
		InventoryUI inventory = new InventoryUI("Prefix Editor", 4);

		Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
		String c = mineman.getCustomColor() != null && !mineman.getCustomColor().isEmpty() && mineman.hasRank(Rank.BASIC) ? mineman.getCustomColor() : mineman.getDisplayRank().getColor();
		String p = mineman.getCustomPrefix().equals("") ? "" : mineman.getCustomPrefix() + " ";

		inventory.setItem(4,
				new InventoryUI.EmptyClickableItem(new ItemBuilder(Material.WORKBENCH)
						.name(p + mineman.getDisplayRank().getPrefix() + c + player.getName() + "&f: hello!")
						.lore(Arrays.asList(
								"",
								"&7Your chat messages will",
								"&7be displayed like the example",
								"&7above."
						)).build()));

		int count = 10;

		for(Prefix prefix : Prefix.PREFIXES) {
			String name = prefix.getStyle();

			boolean isSet = mineman.getCustomPrefix().equals(name);
			boolean hasPrefix = mineman.getPrefixes().contains(name);

			ItemBuilder builder = new ItemBuilder(Material.WOOL);
			builder.name((isSet ? "&a&l" : "&c&l") + prefix.getName());
			builder.durability(isSet ? 5 : hasPrefix ? 3 : 14);

			List<String> lore = new ArrayList<>();

			lore.add("");
			Stream.of(prefix.getDescription()).forEach(text -> lore.add("&7" + text));
			lore.add("");
			
			String color = mineman.getCustomColor() != null && !mineman.getCustomColor().isEmpty() && mineman.hasRank(Rank.BASIC) ? mineman.getCustomColor() : mineman.getRank().getColor();
			String rankPrefix = mineman.getRank().getPrefix();

			lore.add(name + " " + (rankPrefix != null ? rankPrefix : "") + color + player.getName());
			lore.add("");
			lore.add(hasPrefix ? CC.PRIMARY + "Click to select..." : CC.PRIMARY + "You do not own this prefix.");

			inventory.setItem(count, new InventoryUI.AbstractClickableItem(builder.lore(lore).build()) {
				@Override
				public void onClick(InventoryClickEvent event) {
					Player player = (Player) event.getWhoClicked();
					Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());

					if(mineman == null || !mineman.isDataLoaded()) {
						player.sendMessage(CC.RED + "Please wait for your data to load.");
						return;
					}

					if(mineman.isErrorLoadingData()) {
						player.sendMessage(CC.RED + "There was an error loading your data. Please relog and try again, or contact " + "an Admin if this error persists.");
						return;
					}

					String name = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());

					Prefix customPrefix = Prefix.getByName(name);
					String style = customPrefix.getStyle();
					
					if(!mineman.getPrefixes().contains(style)) {
						player.sendMessage(Color.translate("&cYou do not own this prefix. \nYou can purchase it on our shop &eshop.minion.lol&c."));
						return;
					}

					if(style.equals(mineman.getCustomPrefix())) {
						player.sendMessage(CC.RED + "You already have " + style + CC.RED + " selected.");
						return;
					}

					mineman.setCustomPrefix(style);
					player.sendMessage(Color.translate("&aYou changed your prefix to " + style + "&a."));

					updateInventory(player, event.getClickedInventory());

					CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(
							new PrefixRequest.PrefixUpdateRequest(prefix.getName(), mineman.getName())
					);
				}
			});

			count = count == 16 ? count + 3 : count + 1;
		}

		inventory.setItem(31, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.GLASS)
				.name("&7&lReset Prefix")
				.lore(Arrays.asList(
						"",
						"&7Reset your chat prefix.",
						"",
						CC.PRIMARY + "Click to reset your prefix...")).build()) {
			@Override
			public void onClick(InventoryClickEvent event) {
				Player player = (Player) event.getWhoClicked();
				Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());

				if(mineman == null || !mineman.isDataLoaded()) {
					player.sendMessage(CC.RED + "Please wait for your data to load.");
					return;
				}

				if(mineman.isErrorLoadingData()) {
					player.sendMessage(CC.RED + "There was an error loading your data. Please relog and try again, or contact " + "an Admin if this error persists.");
					return;
				}

				if(mineman.getCustomPrefix().equals("")) {
					return;
				}

				mineman.setCustomPrefix("");
				player.sendMessage(CC.GREEN + "Your prefix has been reset.");

				updateInventory(player, event.getClickedInventory());

				CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(
						new PrefixRequest.PrefixUpdateRequest(mineman.getCustomPrefix(), mineman.getName())
				);
			}
		});

		player.openInventory(inventory.getCurrentPage());
	}

	public void updateInventory(Player player, Inventory inventory) {
		inventory.clear();

		Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
		String c = mineman.getCustomColor() != null && !mineman.getCustomColor().isEmpty() && mineman.hasRank(Rank.BASIC) ? mineman.getCustomColor() : mineman.getDisplayRank().getColor();
		String p = mineman.getCustomPrefix().equals("") ? "" : mineman.getCustomPrefix() + " ";

		inventory.setItem(4,
				new ItemBuilder(Material.WORKBENCH)
						.name(p + mineman.getDisplayRank().getPrefix() + c + player.getName() + "&f: hello!")
						.lore(Arrays.asList(
								"",
								"&7Your chat messages will",
								"&7be displayed like the example",
								"&7above."
						)).build());

		int count = 10;

		for(Prefix prefix : Prefix.PREFIXES) {
			String name = prefix.getStyle();

			boolean isSet = mineman.getCustomPrefix().equals(name);
			boolean hasPrefix = mineman.getPrefixes().contains(name);

			ItemBuilder builder = new ItemBuilder(Material.WOOL);
			builder.name((isSet ? "&a&l" : "&c&l") + prefix.getName());
			builder.durability(isSet ? 5 : hasPrefix ? 3 : 14);

			List<String> lore = new ArrayList<>();

			lore.add("");
			Stream.of(prefix.getDescription()).forEach(text -> lore.add("&7" + text));
			lore.add("");

			String color = mineman.getCustomColor() != null && !mineman.getCustomColor().isEmpty() && mineman.hasRank(Rank.BASIC) ? mineman.getCustomColor() : mineman.getRank().getColor();
			String rankPrefix = mineman.getRank().getPrefix();

			lore.add(name + " " + (rankPrefix != null ? rankPrefix : "") + color + player.getName());
			lore.add("");
			lore.add(hasPrefix ? CC.PRIMARY + "Click to select..." : CC.PRIMARY + "You do not own this prefix.");

			inventory.setItem(count, builder.lore(lore).build());

			count = count == 16 ? count + 3 : count + 1;
		}

		inventory.setItem(31, new ItemBuilder(Material.GLASS)
				.name("&7&lReset Prefix")
				.lore(Arrays.asList(
						"",
						"&7Reset your chat prefix.",
						"",
						CC.PRIMARY + "Click to reset your prefix...")).build());
	}

	@SubCommand(baseCommand = "prefixes", name = {"toggle"}, rank = Rank.MANAGER)
	public void togglePrefixes(CommandSender sender, @Param(name = "name") String name, @Param(name = "prefix") String toAdd) {
		Prefix prefix = Prefix.getByName(toAdd);

		if(prefix == null) {
			sender.sendMessage(CC.RED + "Failed to find that prefix.");
			return;
		}

		String style = prefix.getStyle();

		CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(
				new PrefixRequest.PrefixAddRequest(prefix.getName(), name), element -> {
					JsonObject data = element.getAsJsonObject();

					String response = data.get("response").getAsString();

					if(response.equalsIgnoreCase("player-not-found")) {
						sender.sendMessage(CC.RED + "Failed to find that player.");
						return;
					}

					if(response.equalsIgnoreCase("success")) {
						sender.sendMessage(CC.SECONDARY + "You gave " + CC.PRIMARY + prefix.getName() + CC.SECONDARY + " prefix to " + CC.PRIMARY + name + CC.SECONDARY + ".");

						Player player = Bukkit.getPlayer(name);

						if(player != null) {
							Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());

							if(mineman.togglePrefix(style)) {
								player.sendMessage(CC.SECONDARY + "You now have " + CC.PRIMARY + style + CC.SECONDARY + " prefix.");
							} else {
								player.sendMessage(CC.SECONDARY + "You no longer have " + CC.PRIMARY + style + CC.SECONDARY + " prefix.");
								if (mineman.getCustomPrefix().equals(style)) {
									mineman.setCustomPrefix("");
									CorePlugin.getInstance().getRequestProcessor().sendRequest(
											new PrefixRequest.PrefixUpdateRequest(mineman.getCustomPrefix(), mineman.getName())
									);
								}
							}
						}
					}
				});
	}

	@Getter
	public enum Prefix {
		VERIFIED("Verified", "&2✔", "Unlocked when you have", "synced your Discord account or", "liked our server on NameMC!"),
		L("L", "&7[&c#L&7]", "Prove that you are willing", "to hold the L."),
		GG("GG", "&7[&eGG&7]", "Good game dude!"),
		BEST_EU("BestEU", "&7[&5BestEU&7]", "No, I'm the best EU!"),
		EZ("EZ", "&7[&bEZ&7]", "You're EZ man."),
		OOF("OOF", "&7[&dOOF&7]", "OOF! OOF! OOF!"),
		LOVE("Love", "&4❤", "We love you too!"),
		GOLDSTAR("Star", "&6★", "You deserve a gold star!"),
		DAB("Dab", "&c<o/", "Skrt and hit the dab", "like Wiz Khalifa!"),
		RICH("Rich", "&7[&2$&7]", "We love money, do you?"),
		HITORMISS("HitOrMiss", "&7[&fHitOrMiss&7]", "Hit or miss, I guess they", "never miss, huh?"),
		ALESSIO("Alessio", "&7[&5Alessio&7]", "Show your support for", "Alessio26gas!"),
		ZIBLACKING("ZIBLACKING", "&7[&4ZIBLACKING&7]", "Show your support for", "ZIBLACKING!"),
		HAXSHW("HAXSHW", "&7[&9HAXSHW&7]", "Show your support for", "HAXSHW!");

		public static final Prefix[] PREFIXES = Prefix.values();

		private String name, style;
		private String[] description;

		Prefix(String name, String style, String... description) {
			this.name = name;
			this.style = style.replace("&", "§");
			this.description = description;
		}

		public static Prefix getByName(String name) {
			return Arrays.stream(PREFIXES)
					.filter(rank -> rank.getName().equalsIgnoreCase(name) || rank.name().equalsIgnoreCase(name))
					.findFirst().orElse(null);
		}
	}
}
