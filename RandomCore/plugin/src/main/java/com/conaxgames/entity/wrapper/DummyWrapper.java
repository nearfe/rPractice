package com.conaxgames.entity.wrapper;

import com.conaxgames.CorePlugin;
import com.conaxgames.entity.EntityInteraction;
import com.mojang.authlib.GameProfile;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class DummyWrapper extends EntityPlayer {

	private static Field GAME_PROFILE_FIELD;

	private final World world;
	@Getter
	@Setter
	private EntityInteraction entityInteraction;

	public DummyWrapper(World world, UUID uuid) {
		super(((CraftWorld) world).getHandle().getMinecraftServer(), ((CraftWorld) world).getHandle(),
				new GameProfile(uuid, Bukkit.getOfflinePlayer(uuid).getName()), new PlayerInteractWrapper(world));

		this.world = world;

		this.collidesWithEntities = false;

		this.playerConnection = new DummyConnectionWrapper(((CraftWorld) world).getHandle().getMinecraftServer(),
				new NetworkManager(EnumProtocolDirection.SERVERBOUND), this);

		if (GAME_PROFILE_FIELD == null) {
			try {
				GAME_PROFILE_FIELD = PacketPlayOutPlayerInfo.PlayerInfoData.class.getDeclaredField("d");
			} catch (NoSuchFieldException e1) {
				e1.printStackTrace();
			}
		}
	}

	// Call this method Async.
	public void spawn() {
        PlayerList playerList = ((CraftServer) Bukkit.getServer()).getHandle();

        String ip = ThreadLocalRandom.current().nextInt(11, 256) + "." + ThreadLocalRandom.current().nextInt(256) + "." + ThreadLocalRandom.current().nextInt(256) + "." + ThreadLocalRandom.current().nextInt(256); // me irl;
        AsyncPlayerPreLoginEvent asyncEvent;
        try {
            asyncEvent = new AsyncPlayerPreLoginEvent(this.getProfile().getName(), InetAddress.getByName(ip), this.getProfile().getId());
        } catch (Exception unused) {
            return;
        }
        Bukkit.getPluginManager().callEvent(asyncEvent);

        Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> {
            PlayerSpawnLocationEvent ev = new PlayerSpawnLocationEvent(this.getBukkitEntity(), this.getBukkitEntity().getLocation());
            Bukkit.getPluginManager().callEvent(ev);

            Location loc = ev.getSpawnLocation();
            WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();

            this.spawnIn(world);
            this.setPosition(loc.getX(), loc.getY(), loc.getZ());
            this.setYawPitch(loc.getYaw(), loc.getPitch());

            playerList.onPlayerJoin(this, null);
            //playerList.sendScoreboard((ScoreboardServer) playerList.getServer().getWorldServer(this.dimension).getScoreboard(), this);
        });
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
