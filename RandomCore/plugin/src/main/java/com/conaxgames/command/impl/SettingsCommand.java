package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.inventory.InventoryUI;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.ItemBuilder;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.PlayerUtil;
import com.conaxgames.util.finalutil.StringUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SettingsCommand implements CommandHandler {

	@Command(name = {"settings"}, rank = Rank.NORMAL)
	public void settings(Mineman mineman) {
		Player player = mineman.getPlayer();

		InventoryUI ui = new InventoryUI("Settings", 1);

		boolean isStaff = mineman.hasRank(Rank.TRAINEE);
		// 3, 4, 5, not staff
		// 1, 3, 5, 7 staff

		ui.setItem(isStaff ? 1 : 3, new InventoryUI.AbstractClickableItem(getItem(Material.SIGN, "Public Chat",
				Arrays.asList("Do you want to see", "public chat?"), mineman.isChatEnabled(), 0)) {
			@Override
			public void onClick(InventoryClickEvent event) {
				player.performCommand("tgc");
				event.getClickedInventory().setItem(event.getSlot(), getItem(Material.SIGN, "Public Chat",
						Arrays.asList("Do you want to see", "public chat?"), mineman.isChatEnabled(), 0));
			}
		});

		ui.setItem(isStaff ? 3 : 4, new InventoryUI.AbstractClickableItem(getItem(Material.PAPER, "Private Messages",
				Arrays.asList("Do you want to receive", "private messages?"), mineman.isCanSeeMessages(), 0)) {
			@Override
			public void onClick(InventoryClickEvent event) {
				player.performCommand("tpm");
				event.getClickedInventory().setItem(event.getSlot(), getItem(Material.PAPER, "Private Messages",
						Arrays.asList("Do you want to receive", "private messages?"), mineman.isCanSeeMessages(), 0));
			}
		});

		ui.setItem(5, new InventoryUI.AbstractClickableItem(getItem(Material.SLIME_BALL, mineman.getWorldTime(), Collections.singletonList("Toggle between day, sunset and night."))) {
			@Override
			public void onClick(InventoryClickEvent event) {
				player.performCommand(mineman.getWorldTime().equals("DAY") ? "sunset" : mineman.getWorldTime().equals("SUNSET") ? "night" : mineman.getWorldTime().equals("NIGHT") ? "day" : "day");
				event.getClickedInventory().setItem(event.getSlot(), getItem(Material.SLIME_BALL, mineman.getWorldTime(), Collections.singletonList("Toggle between day, sunset and night.")));
			}
		});

		if(PlayerUtil.testPermission(player, Rank.TRAINEE)) {
			ui.setItem(7, new InventoryUI.AbstractClickableItem(getItem(Material.ENCHANTED_BOOK, "Staff Messages",
					Arrays.asList("Do you want to receive", "staff messages?"), mineman.isCanSeeStaffMessages(), 0)) {
				@Override
				public void onClick(InventoryClickEvent event) {
					player.performCommand("tsm");
					event.getClickedInventory().setItem(event.getSlot(), getItem(Material.ENCHANTED_BOOK, "Staff Messages",
							Arrays.asList("Do you want to receive", "staff messages?"), mineman.isCanSeeStaffMessages(), 0));
				}
			});
		}

		CorePlugin.getInstance().getSettingsManager().getSettingsHandlers().forEach(settingsHandler -> settingsHandler.onCreateSettings(ui, player));

		player.openInventory(ui.getCurrentPage());
	}

	public static ItemStack getItem(Material material, String name, List<String> info, boolean value, int dura) {
		ItemBuilder builder = new ItemBuilder(material);

		builder.name((value ? "&a&l" : "&c&l") + name);
		builder.durability(dura);

		List<String> lore = new ArrayList<>();
		lore.add("");
		info.forEach(i -> lore.add("&7" + i));
		lore.add("");
		lore.add(CC.PRIMARY + (value ? "Click to disable..." : "Click to enable..."));

		builder.lore(lore);

		return builder.build();
	}

	public static ItemStack getItem(Material material, String time, List<String> info) {
		ItemBuilder builder = new ItemBuilder(material);

		builder.name("&a&lToggle Time");

		List<String> lore = new ArrayList<>();

		lore.add("");
		info.forEach(i -> lore.add("&7" + i));
		lore.add("");
		lore.add((time.equals("DAY") ? "  " + CC.PRIMARY + StringUtil.NICE_CHAR : "") + CC.SECONDARY + " Day");
		lore.add((time.equals("SUNSET") ? "  " + CC.PRIMARY + StringUtil.NICE_CHAR : "") + CC.SECONDARY + " Sunset");
		lore.add((time.equals("NIGHT") ? "  " + CC.PRIMARY + StringUtil.NICE_CHAR : "") + CC.SECONDARY + " Night");
		lore.add("");
		lore.add(CC.PRIMARY + "Click to toggle...");

		builder.lore(lore);

		return builder.build();
	}
}
