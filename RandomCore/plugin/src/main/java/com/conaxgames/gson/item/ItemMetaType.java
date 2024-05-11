package com.conaxgames.gson.item;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.potion.PotionEffect;

import java.util.List;

@Getter
@Setter
public class ItemMetaType {

	private String displayName;
	private List<String> lore;
	private List<EnchantType> storedEnchants;
	private Integer repairCost;
	private Integer leatherArmorColor;
	private List<PotionEffect> potionEffects;
}
