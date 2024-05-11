package com.conaxgames.disguise;

import com.conaxgames.api.impl.DisguiseRequest;
import com.conaxgames.event.player.PlayerDisguiseEvent;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.other.GameProfileUtil;
import com.conaxgames.util.other.UUIDFetcher;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import com.conaxgames.CorePlugin;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;

/**
 * @since 10/9/2017
 */
public class DisguiseManager {

	public static final String API_UID_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
	public static final String API_UID_NAME = "name";
	public static final String API_UID_PROPERTIES = "properties";
	public static final String API_UID_SIGNATURE = "signature";
	public static final String API_UID_VALUE = "value";
	private final CorePlugin plugin;
	private final Map<String, GameProfile> skinCache = new HashMap<>();
	@Getter
	private final Map<UUID, GameProfile> originalCache = new HashMap<>();
	@Getter
	private final Map<UUID, DisguiseData> disguiseData = new HashMap<>();

	@Getter
	private Predicate<Player> allowedDisguising = player -> {
		String name = CorePlugin.getInstance().getServerManager().getServerName();
		return name.startsWith("sw-")
				|| name.startsWith("sg-")
				|| name.startsWith("uhc-")
				|| name.startsWith("uhcm-")
				|| name.endsWith("-practice");
	};

	public DisguiseManager(CorePlugin plugin) {
		this.plugin = plugin;
	}

	public boolean disguise(Player player, Rank rank, String skinName, String displayName) throws Exception {
		if (Bukkit.getPlayer(displayName) != null) {
			player.sendMessage(CC.RED + "The player you were assigned is already online, try again.");
			return false;
		}

		GameProfile targetProfile = this.skinCache.get(skinName.toLowerCase());

		if (targetProfile == null) {
			UUIDFetcher uuidFetcher = new UUIDFetcher(Collections.singletonList(skinName));
			Map<String, UUID> fetched = uuidFetcher.call();

			Optional<UUID> fetchedUuid = fetched.values().stream().findFirst();
			if (!fetchedUuid.isPresent()) {
				// default skin to steve if doesn't exist
				targetProfile = this.loadGameProfile(UUID.fromString("8667ba71-b85a-4004-af54-457a9734eed7"), "Steve");
			} else {
				targetProfile = this.loadGameProfile(fetchedUuid.get(), skinName);
			}
		}

		Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());

		DisguiseData data = getDisguiseData(player.getUniqueId()); // TODO: does this class even matter
		data.disguiseRank = rank;
		data.displayName = displayName;
		mineman.setDisguiseRank(data.disguiseRank());

		// Make sure we don't cache another game profile that isn't actually theirs
		if (!this.originalCache.containsKey(player.getUniqueId())) {
			this.originalCache
					.put(player.getUniqueId(), GameProfileUtil.clone(((CraftPlayer) player).getHandle().getProfile()));
		}

		new UpdateSkinTask(this.plugin, player, targetProfile, displayName).runTask(this.plugin);
		new PlayerDisguiseEvent(mineman, displayName, rank).call();

		return true;
	}

	public void undisguise(Player player) {
		GameProfile originalProfile = this.originalCache.remove(player.getUniqueId());
		if (originalProfile != null) {
			new UpdateSkinTask(this.plugin, player, originalProfile, originalProfile.getName()).runTask(this.plugin);
			Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
			if (mineman != null) {
				mineman.setDisguiseRank(null);
				mineman.updateTabList(mineman.getRank());
			}

			disguiseData.remove(player.getUniqueId());
			MinecraftServer.getServer().getPlayerList().setPlayerName(originalProfile.getName(), ((CraftPlayer) player).getHandle());
			MinecraftServer.getServer().getPlayerList().removeFromPlayerNames(player.getName());
		}
	}

	public GameProfile loadGameProfile(UUID uniqueId, String skinName) {
		GameProfile profile = this.skinCache.get(skinName.toLowerCase());

		BufferedReader reader = null;
		try {
			if (profile == null || !profile.getProperties().containsKey("textures")) {
				URL url = new URL(API_UID_URL + uniqueId.toString().replace("-", "") + "?unsigned=false");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.addRequestProperty("User-Agent", "Core");
				connection.setDoOutput(true);
				connection.connect();

				if (connection.getResponseCode() == 200) {
					reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String response = reader.readLine();
					JSONObject object = (JSONObject) JSONValue.parse(response);
					skinName = (String) object.get(API_UID_NAME);
					if (profile == null) {
						profile = new GameProfile(uniqueId, skinName);
					}
					JSONArray array = (JSONArray) object.get(API_UID_PROPERTIES);
					for (Object obj : array) {
						JSONObject property = (JSONObject) obj;
						String propertyName = (String) property.get(API_UID_NAME);
						profile.getProperties().put(propertyName,
								new Property(propertyName, (String) property.get(API_UID_VALUE),
										(String) property.get(API_UID_SIGNATURE)));
					}

					this.skinCache.put(skinName.toLowerCase(), profile);
					MinecraftServer.getServer().getUserCache().a(profile);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}

		return profile;
	}

	public boolean isDisguised(UUID uuid) {
		return this.originalCache.containsKey(uuid);
	}

	public boolean isDisguised(Player player) {
		return this.isDisguised(player.getUniqueId());
	}

	public DisguiseData getDisguiseData(UUID uuid) {
		DisguiseData data = this.disguiseData.get(uuid);
		if (data == null) {
			data = new DisguiseData();
			this.disguiseData.put(uuid, data);
		}

		return data;
	}

	@Getter
	@Setter
	@Accessors(fluent = true)
	public class DisguiseData {

		private String displayName;
		private String skinName;
		private Rank disguiseRank;
	}
}
