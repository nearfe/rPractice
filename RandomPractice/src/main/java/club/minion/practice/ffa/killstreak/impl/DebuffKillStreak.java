package club.minion.practice.ffa.killstreak.impl;

import club.minion.practice.ffa.killstreak.KillStreak;
import club.minion.practice.util.PlayerUtil;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DebuffKillStreak implements KillStreak {

	private static final ItemStack SLOWNESS = new ItemStack(Material.POTION, 1, (short) 16394);
	private static final ItemStack POISON = new ItemStack(Material.POTION, 1, (short) 16388);

	@Override
	public void giveKillStreak(Player player) {
		PlayerUtil.setFirstSlotOfType(player, Material.MUSHROOM_SOUP, SLOWNESS.clone());
		PlayerUtil.setFirstSlotOfType(player, Material.MUSHROOM_SOUP, POISON.clone());
	}

	@Override
	public List<Integer> getStreaks() {
		return Arrays.asList(7, 25);
	}

}
