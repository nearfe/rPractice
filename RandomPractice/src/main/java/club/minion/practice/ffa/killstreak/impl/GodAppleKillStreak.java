package club.minion.practice.ffa.killstreak.impl;

import club.minion.practice.ffa.killstreak.KillStreak;
import club.minion.practice.util.PlayerUtil;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GodAppleKillStreak implements KillStreak {

	@Override
	public void giveKillStreak(Player player) {
		PlayerUtil.setFirstSlotOfType(player, Material.MUSHROOM_SOUP, new ItemStack(Material.GOLDEN_APPLE, 1,
				(short) 1));
	}

	@Override
	public List<Integer> getStreaks() {
		return Arrays.asList(30, 40, 60, 75, 100);
	}

}
