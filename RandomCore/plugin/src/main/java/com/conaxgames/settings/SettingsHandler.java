package com.conaxgames.settings;

import com.conaxgames.inventory.InventoryUI;
import org.bukkit.entity.Player;

public interface SettingsHandler {

	void onCreateSettings(InventoryUI inventoryUI, Player player);
}
