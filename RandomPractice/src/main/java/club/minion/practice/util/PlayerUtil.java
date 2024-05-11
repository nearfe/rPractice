package club.minion.practice.util;

import club.minion.practice.Practice;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public final class PlayerUtil {

	private PlayerUtil() {
	}

	public static void setFirstSlotOfType(Player player, Material type, ItemStack itemStack) {
		for (int i = 0; i < player.getInventory().getContents().length; i++) {
			ItemStack itemStack1 = player.getInventory().getContents()[i];
			if (itemStack1 == null || itemStack1.getType() == type || itemStack1.getType() == Material.AIR) {
				player.getInventory().setItem(i, itemStack);
				break;
			}
		}
	}


	public static String toNiceString(String string) {
		string = ChatColor.stripColor(string).replace('_', ' ').toLowerCase();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < string.toCharArray().length; i++) {
			char c = string.toCharArray()[i];
			if (i > 0) {
				char prev = string.toCharArray()[i - 1];
				if (prev == ' ' || prev == '[' || prev == '(') {
					if (i == string.toCharArray().length - 1 || c != 'x' ||
							!Character.isDigit(string.toCharArray()[i + 1])) {
						c = Character.toUpperCase(c);
					}
				}
			} else {
				if (c != 'x' ||  !Character.isDigit(string.toCharArray()[i + 1])) {
					c = Character.toUpperCase(c);
				}
			}
			sb.append(c);
		}

		return sb.toString();
	}

	public static void clearPlayer(Player player) {
		player.setHealth(20.0D);
		player.setFoodLevel(20);
		player.setSaturation(12.8F);
		player.setMaximumNoDamageTicks(20);
		player.setFireTicks(0);
		player.setFallDistance(0.0F);
		player.setLevel(0);
		player.setExp(0.0F);
		player.setWalkSpeed(0.2F);
		player.getInventory().setHeldItemSlot(0);
		player.setAllowFlight(false);
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.closeInventory();
		player.setGameMode(GameMode.SURVIVAL);
		player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
		((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
		player.updateInventory();
	}

	@SneakyThrows
	public static void animateDeath(Player player) {
		int entityId = EntityUtils.getFakeEntityId();
		PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) player).getHandle());
		PacketPlayOutEntityStatus statusPacket = new PacketPlayOutEntityStatus();

		Field STATUS_PACKET_ID_FIELD;
		Field STATUS_PACKET_STATUS_FIELD;
		Field SPAWN_PACKET_ID_FIELD;

		STATUS_PACKET_ID_FIELD = PacketPlayOutEntityStatus.class.getDeclaredField("a");
		STATUS_PACKET_ID_FIELD.setAccessible(true);
		STATUS_PACKET_STATUS_FIELD = PacketPlayOutEntityStatus.class.getDeclaredField("b");
		STATUS_PACKET_STATUS_FIELD.setAccessible(true);
		SPAWN_PACKET_ID_FIELD = PacketPlayOutNamedEntitySpawn.class.getDeclaredField("a");
		SPAWN_PACKET_ID_FIELD.setAccessible(true);


		SPAWN_PACKET_ID_FIELD.set(spawnPacket, entityId);
		STATUS_PACKET_ID_FIELD.set(statusPacket, entityId);
		STATUS_PACKET_STATUS_FIELD.set(statusPacket, (byte) 3);

		final int radius = MinecraftServer.getServer().getPlayerList().d(); //Furthest viewable block
		final Set<Player> sentTo = new HashSet<>();

		for ( Entity entity : player.getNearbyEntities(radius, radius, radius) ) {

			if (!(entity instanceof Player)) continue;

			final Player watcher = (Player) entity;

			if (watcher.getUniqueId().equals(player.getUniqueId())) {
				continue;
			}

			((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(spawnPacket);
			((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(statusPacket);

			sentTo.add(watcher);
		}

		TasksUtil.runLater(() -> {
			for ( Player watcher : sentTo ) {
				((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entityId));
			}
		}, 40L);
	}

	public static void respawnPlayer(PlayerDeathEvent event){
		new BukkitRunnable() {
			public void run() {
				try {
					Object nmsPlayer = event.getEntity().getClass().getMethod("getHandle").invoke(event.getEntity());
					Object con = nmsPlayer.getClass().getDeclaredField("playerConnection").get(nmsPlayer);

					Class< ? > EntityPlayer = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".EntityPlayer");

					Field minecraftServer = con.getClass().getDeclaredField("minecraftServer");
					minecraftServer.setAccessible(true);
					Object mcserver = minecraftServer.get(con);

					Object playerlist = mcserver.getClass().getDeclaredMethod("getPlayerList").invoke(mcserver);
					Method moveToWorld = playerlist.getClass().getMethod("moveToWorld", EntityPlayer, int.class, boolean.class);
					moveToWorld.invoke(playerlist , nmsPlayer , 0 , false);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}.runTaskLater(Practice.getInstance(), 2L);
	}

	public static void sendMessage(String message, Player... players) {
		for (Player player : players) {
			player.sendMessage(message);
		}

	}


}
