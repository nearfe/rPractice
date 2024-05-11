package com.conaxgames.entity.wrapper;

import com.conaxgames.CorePlugin;
import com.conaxgames.entity.EntityInteraction;
import com.conaxgames.util.CustomLocation;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlayerWrapper extends EntityPlayer {

	private static Field GAME_PROFILE_FIELD;

	private final World world;
	@Getter
	@Setter
	private EntityInteraction entityInteraction;

	public PlayerWrapper(World world, String name) {
		super(((CraftWorld) world).getHandle().getMinecraftServer(), ((CraftWorld) world).getHandle(),
				new GameProfile(UUID.randomUUID(), name), new PlayerInteractWrapper(world));

		this.world = world;

		this.collidesWithEntities = false;

		this.playerConnection = new PlayerConnectionWrapper(((CraftWorld) world).getHandle().getMinecraftServer(),
				new NetworkManagerWrapper(), this);

		if (GAME_PROFILE_FIELD == null) {
			try {
				GAME_PROFILE_FIELD = PacketPlayOutPlayerInfo.PlayerInfoData.class.getDeclaredField("d");
			} catch (NoSuchFieldException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void lookAtLocation(CustomLocation focusLocation) {
		float yaw = (float) (90 + (180 * Math.atan2(this.locZ - focusLocation.getZ(), this.locX - focusLocation.getX()) / Math.PI));
		this.setRotation(yaw, this.pitch);

		double targetYaw = Math.floor(this.yaw * 256.0f / 360.0f);
		PacketPlayOutEntityHeadRotation rotation = new PacketPlayOutEntityHeadRotation(this, (byte) targetYaw);

		PacketPlayOutEntity.PacketPlayOutEntityLook look = new PacketPlayOutEntity.PacketPlayOutEntityLook(
				this.getId(), (byte) targetYaw, (byte) 0, true);

		PacketPlayOutAnimation animation = new PacketPlayOutAnimation(this, 0);

		this.world.getPlayers().forEach(player -> {
			EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
			PlayerConnection connection = entityPlayer.playerConnection;

			connection.sendPacket(rotation);
			connection.sendPacket(look);
			connection.sendPacket(animation);
		});
	}

	public void spawn() {

		PacketPlayOutNamedEntitySpawn namedEntitySpawn = new PacketPlayOutNamedEntitySpawn(this);

		PacketPlayOutPlayerInfo playerInfoRemove = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, this);

		this.world.getPlayers().forEach(player -> {
			EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
			PlayerConnection connection = entityPlayer.playerConnection;

			PlayerInfoPacketWrapper packetWrapper = new PlayerInfoPacketWrapper(
					new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, this)
			);

			GameProfile playerProfile = entityPlayer.getProfile();
			Collection<Property> properties = playerProfile.getProperties().asMap().get("textures");
			if (!properties.isEmpty()) {
				Property property = properties.iterator().next();

				List<PacketPlayOutPlayerInfo.PlayerInfoData> infoData = packetWrapper.getInfoData();
				infoData.forEach(playerInfoData -> {
					try {
						GAME_PROFILE_FIELD.setAccessible(true);
						GameProfile profile = (GameProfile) GAME_PROFILE_FIELD.get(playerInfoData);
						profile.getProperties().put("textures", property);
					} catch (IllegalAccessException e1) {
						e1.printStackTrace();
					}
				});
			}

			PacketPlayOutPlayerInfo playerInfoAdd = packetWrapper.getPacket();

			connection.sendPacket(playerInfoAdd);
			connection.sendPacket(namedEntitySpawn);
		});

		CorePlugin.getInstance().getServer().getScheduler().runTaskLater(CorePlugin.getInstance(),
				() -> this.world.getPlayers().forEach(player -> {
					EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
					PlayerConnection connection = entityPlayer.playerConnection;

					connection.sendPacket(playerInfoRemove);
				}), 10L);
	}

	public void setLocation(CustomLocation location) {
		this.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	public void setRotation(float yaw, float pitch) {
		this.setPositionRotation(this.locX, this.locY, this.locZ, yaw, pitch);
	}

	@Override
	public void g(float f, float f1) {
		// Can't damage these niggers
	}

	@Override
	public boolean isSpectator() {
		return false;
	}
}
