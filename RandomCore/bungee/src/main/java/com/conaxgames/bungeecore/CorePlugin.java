package com.conaxgames.bungeecore;

/**
 * @since 2017-09-21
 */

import com.conaxgames.bungeecore.command.BungeeCoreCommand;
import com.conaxgames.bungeecore.command.HubCommand;
import com.conaxgames.bungeecore.command.MaintenanceCommand;
import com.conaxgames.bungeecore.database.BungeeDatabase;
import com.conaxgames.bungeecore.listener.PlayerListener;
import com.conaxgames.bungeecore.redis.BungeeCoreSubscriber;
import com.google.common.base.Joiner;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import com.pvptemple.xenon.Xenon;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import redis.clients.jedis.Jedis;
import com.conaxgames.redis.JedisPublisher;
import com.conaxgames.redis.JedisSettings;
import com.conaxgames.redis.JedisSubscriber;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public final class CorePlugin extends Plugin {

	public static final String MAINTENANCE_STRING = ChatColor.RED + "Sorry, but the server is currently in maintenance.";

	@Getter
	private Configuration configuration;
	@Getter
	private BungeeDatabase database;
	@Getter
	private Map<String, ServerInfo> defaultServers;

	@Getter @Setter
	private boolean isMaintenance;
	@Getter @Setter
	private List<String> whitelistedPlayers;

	@Getter @Setter
	private String motd, region;

	@Getter
	private List<ServerInfo> hubServers;

	private JedisPublisher<JsonObject> corePublisher;
	private JedisSubscriber<JsonObject> coreSubscriber;

	@Override
	public void onEnable() {
		try {
			this.loadConfig();
		} catch (Exception e) {
			e.printStackTrace();
			this.getLogger().severe("Could not load config");
			this.getProxy().stop();
			return;
		}
		region = configuration.getString("region");

		try {
			this.database = new BungeeDatabase(this);
		} catch (Exception e) {
			e.printStackTrace();
			this.getLogger().severe("Could not connect to database");
			this.getProxy().stop();
			return;
		}

		try {
			Configuration configuration = YamlConfiguration.getProvider(YamlConfiguration.class).load(new File(
					this.getDataFolder().getParent() + "/Xenon/config.yml"));

			JedisSettings jedisSettings = new JedisSettings(configuration.getString("redis.server"),
					configuration.getInt("redis.port"), configuration.getString("redis.password"));

			try (Jedis jedis = jedisSettings.getJedisPool().getResource()) {
				if (jedis.get("maintenance") != null) {
					isMaintenance = Boolean.valueOf(jedis.get("maintenance"));
				}
				whitelistedPlayers = jedis.get("maintenance-list") != null
						? new ArrayList<>(Arrays.asList(jedis.get("maintenance-list").split(",")))
						: (configuration.getStringList("whitelisted-players") == null ? new ArrayList<>() : configuration.getStringList("whitelisted-players"));
				motd = jedis.get("motd") != null
						? jedis.get("motd")
						: configuration.getString("motd");
			}

			this.corePublisher = new JedisPublisher<>(jedisSettings, "bungeecore");
			this.coreSubscriber = new JedisSubscriber<>(jedisSettings, "bungeecore", JsonObject.class, new
                    BungeeCoreSubscriber(this));
		} catch (Exception e) {
			e.printStackTrace();
			this.getProxy().stop();
			return;
		}

		hubServers = new ArrayList<>();

		getProxy().getServers().values().stream()
				.filter(info -> info.getName().contains("hub"))
				.forEach(hubServers::add);

		this.registerCrap();

		this.getProxy().getScheduler().runAsync(this, CorePlugin.this::fetchServers);

		this.getProxy().getScheduler()
		    .schedule(this, CorePlugin.this::fetchWhitelistedPlayers, 0, 60, TimeUnit.SECONDS);
	}

	private void registerCrap() {
		this.getProxy().registerChannel("Permissions");
		this.getProxy().getPluginManager().registerListener(this, new PlayerListener(this));
		this.getProxy().getPluginManager().registerCommand(this, new BungeeCoreCommand(this));
		this.getProxy().getPluginManager().registerCommand(this, new HubCommand(this));
		this.getProxy().getPluginManager().registerCommand(this, new MaintenanceCommand(this));

		this.defaultServers = this.getProxy().getServers();
	}

	public void fetchWhitelistedPlayers() {
		Set<UUID> newWhitelisted = getDatabase().fetchWhitelistedPlayers();
		getDatabase().getWhitelisted().clear();
		getDatabase().getWhitelisted().addAll(newWhitelisted);
		this.getLogger().info("Fetched all whitelisted players");
	}

	public ServerInfo getBestHub() {
		ServerInfo server = hubServers.get(ThreadLocalRandom.current().nextInt(hubServers.size()));
		return server;
	}

	public ServerInfo getBestHub(ProxiedPlayer player) {
		ServerInfo hub;
		if (player.getServer() == null) {
			return getBestHub();
		} else {
			String name = player.getServer().getInfo().getName();

			if (name.startsWith("sg-")) {
				hub = getProxy().getServerInfo("sglobby");
			} else if (name.startsWith("sw-")) {
				hub = getProxy().getServerInfo("swlobby");
			} else if (name.startsWith("uhcm-") || name.startsWith("uhc-")) {
				hub = getProxy().getServerInfo("uhcgames");
			} else {
				hub = getBestHub();
			}

			return hub;
		}
	}

	public void fetchServers() {
		/*// TODO: Convert to a Redis system which will have a master bungee
		// TODO: It will have a ADD/REMOVE message and all the other proxies will ADD/REMOVE said server
		Map<String, ServerInfo> availableServers = CorePlugin.this.getDatabase().getAvailableServers();
		Map<String, ServerInfo> newServers = new HashMap<>();

		for (String key : availableServers.keySet()) {
			ServerInfo currentServer = CorePlugin.this.getProxy().getServerInfo(key);

			if (currentServer != null) {
				newServers.put(key, currentServer);
			} else {
				newServers.put(key, availableServers.get(key));
			}
		}

		this.getProxy().getServers().clear();
		this.getProxy().getServers().putAll(this.defaultServers);
		this.getProxy().getServers().putAll(newServers);

		this.getLogger().info("Added " + newServers.size() + " servers");
		this.getLogger().info("Fetched all servers");*/
	}

	private void loadConfig() throws IOException {
		File configFile = new File(this.getDataFolder(), "config.yml");

		if (!this.getDataFolder().exists()) {
			this.getDataFolder().mkdir();
		}

		if (!configFile.exists()) {
			configFile.createNewFile();

			try (InputStream in = getResourceAsStream("config.yml");
			     OutputStream out = new FileOutputStream(configFile)) {
				ByteStreams.copy(in, out);
			}
		}

		this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
	}

	@Override
	public void onDisable() {

	}

	private JsonObject getJsonObject() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("server-id", Xenon.getInstance().getProxyId());
		return jsonObject;
	}

	public void sendMaintenance(boolean newMaintenance) {
		JsonObject jsonObject = getJsonObject();
		jsonObject.addProperty("maintenance", newMaintenance);

		try (Jedis jedis = corePublisher.getJedisSettings().getJedisPool().getResource()) {
			jedis.set("maintenance", String.valueOf(newMaintenance));
		}

		this.corePublisher.write(jsonObject);
	}

	public void sendMaintenanceList(List<String> list) {
		JsonObject jsonObject = getJsonObject();
		jsonObject.addProperty("maintenance-list", Joiner.on(",").join(list));

		try (Jedis jedis = corePublisher.getJedisSettings().getJedisPool().getResource()) {
			jedis.set("maintenance-list", Joiner.on(",").join(list));
		}

		this.corePublisher.write(jsonObject);
	}

	public void sendMotd(String newMotd) {
		JsonObject jsonObject = getJsonObject();
		jsonObject.addProperty("motd", newMotd);

		try (Jedis jedis = corePublisher.getJedisSettings().getJedisPool().getResource()) {
			jedis.set("motd", newMotd);
		}

		this.corePublisher.write(jsonObject);
	}

	public void sendMaintenanceKick() {
		JsonObject jsonObject = getJsonObject();
		jsonObject.addProperty("maintenancekick", "");
		this.corePublisher.write(jsonObject);
	}
}
