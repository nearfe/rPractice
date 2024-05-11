package com.conaxgames.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder implements Listener {

	private final ItemStack itemStack;

	public ItemBuilder(final Material mat) {
		itemStack = new ItemStack(mat);
	}

	public ItemBuilder(final ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public ItemBuilder amount(final int amount) {
		this.itemStack.setAmount(amount);
		return this;
	}

	public ItemBuilder name(final String name) {
		final ItemMeta meta = this.itemStack.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		this.itemStack.setItemMeta(meta);
		return this;
	}

	public ItemBuilder lore(final String name) {
		final ItemMeta meta = this.itemStack.getItemMeta();
		List<String> lore = meta.getLore();
		if (lore == null) {
			lore = new ArrayList<>();
		}
        lore.add(ChatColor.translateAlternateColorCodes('&', name));
		meta.setLore(lore);
		this.itemStack.setItemMeta(meta);
		return this;
	}

	public ItemBuilder lore(final List<String> lore) {
		List<String> toSet = new ArrayList<>();
		ItemMeta meta = this.itemStack.getItemMeta();

		for (String string : lore) {
			toSet.add(ChatColor.translateAlternateColorCodes('&', string));
		}

		meta.setLore(toSet);
		this.itemStack.setItemMeta(meta);
		return this;
	}

	public ItemBuilder durability(final int durability) {
		this.itemStack.setDurability((short) durability);
		return this;
	}

	@SuppressWarnings("deprecation")
	public ItemBuilder data(final int data) {
		this.itemStack.setData(new MaterialData(this.itemStack.getType(), (byte) data));
		return this;
	}

	public ItemBuilder enchantment(final Enchantment enchantment, final int level) {
		this.itemStack.addUnsafeEnchantment(enchantment, level);
		return this;
	}

	public ItemBuilder enchantment(final Enchantment enchantment) {
		this.itemStack.addUnsafeEnchantment(enchantment, 1);
		return this;
	}

	public ItemBuilder type(final Material material) {
		this.itemStack.setType(material);
		return this;
	}

	public ItemBuilder clearLore() {
		final ItemMeta meta = this.itemStack.getItemMeta();
		meta.setLore(new ArrayList<String>());
		this.itemStack.setItemMeta(meta);
		return this;
	}

	public ItemBuilder clearEnchantments() {
		for (final Enchantment e : this.itemStack.getEnchantments().keySet()) {
			this.itemStack.removeEnchantment(e);
		}
		return this;
	}

	public ItemBuilder color(Color color) {
		if (this.itemStack.getType() == Material.LEATHER_BOOTS || this.itemStack.getType() == Material.LEATHER_CHESTPLATE || this.itemStack.getType() == Material.LEATHER_HELMET
				|| this.itemStack.getType() == Material.LEATHER_LEGGINGS) {
			LeatherArmorMeta meta = (LeatherArmorMeta) this.itemStack.getItemMeta();
			meta.setColor(color);
			this.itemStack.setItemMeta(meta);
			return this;
		} else {
			throw new IllegalArgumentException("color() only applicable for leather armor!");
		}
	}

	public ItemStack build() {
		return itemStack;
	}

}