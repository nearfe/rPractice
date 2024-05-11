package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.api.impl.ColorUpdateRequest;
import com.conaxgames.inventory.InventoryUI;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.ItemBuilder;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.commandTypes.BaseCommand;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.PlayerUtil;
import com.conaxgames.util.finalutil.WoolUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;
import java.util.stream.Stream;

public class ColorCommand implements CommandHandler {

	private String[] colors = {
			"Aqua", "Red", "Gold",
			"Light Purple", "Dark Aqua", "Green",
			"Yellow",
	};

	@BaseCommand(name = {"color", "colour"}, rank = Rank.BASIC, description = "Change your chat prefix color")
	public void usage(Player player) {
		if (PlayerUtil.testPermission(player, Rank.HOST)) {
			player.sendMessage(ChatColor.RED + "You can't use /color as staff.");
			return;
		}

		InventoryUI inventory = new InventoryUI("Color Editor", 3);

		Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
		String c = mineman.getCustomColor() != null && !mineman.getCustomColor().isEmpty() && mineman.hasRank(Rank.BASIC) ? mineman.getCustomColor() : mineman.getDisplayRank().getColor();

		inventory.setItem(4,
				new InventoryUI.EmptyClickableItem(new ItemBuilder(Material.WORKBENCH)
						.name(mineman.getDisplayRank().getPrefix() + c + player.getName() + "&f: hello!")
						.lore(Arrays.asList(
								"",
								"&7Your chat messages will",
								"&7be displayed like the example",
								"&7above."
						)).build()));

		int[] i = {10};

		Stream.of(colors).forEach(colorName -> {
			String color = getColor(colorName);

			inventory.setItem(i[0], new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.WOOL)
					.name(color + "&l" + colorName)

					.durability(WoolUtil.convertCCToWoolData(color)).lore(Arrays.asList(
							"",
							"&7Changes the color of",
							"&7of your name to " + colorName + ".",
							"",
							CC.PRIMARY + "Click to select this color...")).build()) {
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
					String customColor = getColor(name);

					String color = customColor != null ? customColor.replace("&", "§") : "";

					if(color.equals(mineman.getCustomColor())) {
						player.sendMessage(CC.RED + "You already have " + getColor(name) + name + CC.RED + " selected.");
						return;
					}

					mineman.setCustomColor(color);
					player.sendMessage(CC.GREEN + "Your name color has been updated to " + getColor(name) + name + CC.GREEN + ".");

					event.getClickedInventory().setItem(4, new ItemBuilder(Material.WORKBENCH)
							.name(mineman.getDisplayRank().getPrefix() + color + player.getName() + "&f: hello!")
							.lore(Arrays.asList(
									"",
									"&7Your chat messages will",
									"&7be displayed like the example",
									"&7above."
							)).build());

					CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(
							new ColorUpdateRequest(customColor == null ? null : customColor.substring(1, 2), mineman.getId())
					);
				}
			});

			i[0]++;
		});

		inventory.setItem(22, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.GLASS)
				.name("&7&lRemove Color")
				.lore(Arrays.asList(
						"",
						"&7Reverts your chat color back",
						"&7to the original color created",
						"&7by your rank.",
						"",
						CC.PRIMARY + "Click to reset your color...")).build()) {
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

				if(mineman.getCustomColor().equals("")) {
					return;
				}

				String name = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
				String customColor = getColor(name);

				mineman.setCustomColor(customColor != null ? customColor.replace("&", "§") : "");
				player.sendMessage(CC.GREEN + "Your name color has been reset.");

				event.getClickedInventory().setItem(4, new ItemBuilder(Material.WORKBENCH)
						.name(mineman.getDisplayRank().getPrefix() + mineman.getDisplayRank().getColor() + player.getName() + "&f: hello!")
						.lore(Arrays.asList(
								"",
								"&7Your chat messages will",
								"&7be displayed like the example",
								"&7above."
						)).build());

				CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(
						new ColorUpdateRequest(customColor == null ? null : customColor.substring(1, 2), mineman.getId())
				);
			}
		});

		player.openInventory(inventory.getCurrentPage());
	}

	private String getColor(String colorName) {
		switch (colorName) {
			case "Purple": return CC.DARK_PURPLE;
			case "Dark Aqua": return CC.DARK_AQUA;
			case "Light Gray": return CC.GRAY;
			case "Gray": return CC.DARK_GRAY;
			case "Light Purple": return CC.LIGHT_PURPLE;
			case "Green": return CC.GREEN;
			case "Aqua": return CC.AQUA;
			case "Gold": return CC.GOLD;
			case "Red": return CC.RED;
			case "Yellow": return CC.YELLOW;
			case "Dark Green": return CC.DARK_GREEN;
			case "Remove Color": return null;
			default: return "Invalid";
		}
	}
}
