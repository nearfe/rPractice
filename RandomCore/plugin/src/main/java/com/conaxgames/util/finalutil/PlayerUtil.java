package com.conaxgames.util.finalutil;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import com.conaxgames.CorePlugin;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;

import java.util.Comparator;
import java.util.Map;
import java.util.WeakHashMap;

public final class PlayerUtil {
	private static Map<Player, Integer> entityIds = new WeakHashMap<>();
	public static final Comparator<Player> VISIBLE_RANK_ORDER = (a, b) -> {
		Mineman minemanA = CorePlugin.getInstance().getPlayerManager().getPlayer(a.getUniqueId());
		Mineman minemanB = CorePlugin.getInstance().getPlayerManager().getPlayer(b.getUniqueId());
		return -minemanA.getDisplayRank().compareTo(minemanB.getDisplayRank());
	};

	public PlayerUtil() {
		throw new RuntimeException("Cannot instantiate a utility class.");
	}

	public static void messageRank(String message) {
		PlayerUtil.messageRank(message, Rank.TRAINEE);
	}

	public static void messageRank(String message, Rank rank) {
		CorePlugin.getInstance().getServer().getConsoleSender().sendMessage(Color.translate(message));
		for (Player player : CorePlugin.getInstance().getServer().getOnlinePlayers()) {
			Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
			if (mineman == null || !mineman.hasRank(rank)) {
				continue;
			}
			if (rank.hasRank(Rank.TRAINEE) && !mineman.isCanSeeStaffMessages()) {
				continue;
			}
			player.sendMessage(Color.translate(message));
		}
	}

	public static boolean testPermission(CommandSender sender, Rank requiredRank, Permission permission, boolean requiresOp) {
		if (requiresOp && !sender.isOp()) {
			return false;
		}

		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (permission != null) {
				if (!player.hasPermission(permission)) {
					return false;
				}

				return true;
			}

			return PlayerUtil.testPermission(sender, requiredRank);
		}

		return true;
	}


	public static boolean testPermission(CommandSender sender, Rank requiredRank) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
			if (mineman == null || !mineman.hasRank(requiredRank)) {
				return false;
			}
		}

		return true;
	}

	public static void sitPlayer(Player player) {
		EntityPlayer ep = ((CraftPlayer) player).getHandle();
		Location loc = player.getLocation();

		EntityBat bat = new EntityBat(((CraftWorld) player.getWorld()).getHandle());
		bat.setPosition(loc.getX(), loc.getY(), loc.getZ());
		bat.setInvisible(true);
		bat.setHealth(6F);

		entityIds.put(player, bat.getId());

		ep.playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(bat));
		ep.playerConnection.sendPacket(new PacketPlayOutAttachEntity(0, ep, bat));
	}

	public static void unsitPlayer(Player player) {
		if (entityIds.containsKey(player)) {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entityIds.get(player)));
		}
	}

	public static int getPing(Player player) {
		return ((CraftPlayer) player).getHandle().ping;
	}
}
