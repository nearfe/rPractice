package com.conaxgames.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import lombok.Getter;
import lombok.Setter;
import com.conaxgames.CorePlugin;
import com.conaxgames.redis.JedisPublisher;
import com.conaxgames.redis.JedisSubscriber;

import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @since 8/29/2017
 */
@Getter
public class ServerManager {

	private final CorePlugin plugin;
	private final Map<String, ServerData> servers;

	private JedisPublisher<JsonObject> serverHeartbeatPublisher;
	private JedisPublisher<JsonObject> proxyPublisher;

	private JedisSubscriber serverHeartbeatSubscriber;

	@Setter
	private String serverName;

	@Setter
	private boolean joinable = false;

	@Setter
	private JsonObject extraData;

	public ServerManager(CorePlugin plugin) {
		this.plugin = plugin;
		this.servers = new ConcurrentHashMap<>();
		this.serverName = this.plugin.getConfig().getString("server-data.default-name");

		this.serverHeartbeatPublisher = new JedisPublisher<>(this.plugin.getJedisConfig().toJedisSettings(),
				"server_heartbeat");
		this.proxyPublisher = new JedisPublisher<>(this.plugin.getJedisConfig().toJedisSettings(),
				"proxy-core");
		this.serverHeartbeatSubscriber = new JedisSubscriber<>(this.plugin.getJedisConfig().toJedisSettings(),
				"server_heartbeat", String.class, message -> {
			try {
				JsonReader jsonReader = new JsonReader(new StringReader(message));
				jsonReader.setLenient(true);
				JsonObject jsonObject = new JsonParser().parse(jsonReader).getAsJsonObject();
				String serverName = jsonObject.get("server-name").getAsString();
				String action = jsonObject.get("action") != null ? jsonObject.get("action").getAsString() : null;
				if (action != null) {
					if (action.equals("online")) {
						return;
					} else if (action.equals("offline")) {
						ServerManager.this.servers.remove(serverName);
						return;
					}
				}
				try {
					ServerData serverData = ServerManager.this.servers.get(serverName);
					if (serverData == null) {
						serverData = new ServerData();
						ServerManager.this.servers.put(serverName, serverData);
					}

					int playersOnline = jsonObject.get("player-count").getAsInt();
					int maxPlayers = jsonObject.get("player-max").getAsInt();
					boolean whitelisted = jsonObject.get("whitelisted").getAsBoolean();
					boolean joinable = jsonObject.get("joinable").getAsBoolean();

					serverData.setServerName(serverName);
					serverData.setOnlinePlayers(playersOnline);
					serverData.setMaxPlayers(maxPlayers);
					serverData.setWhitelisted(whitelisted);
					serverData.setLastUpdate(System.currentTimeMillis());
					serverData.setJoinable(joinable);
					if (jsonObject.get("extra") != null) {
						serverData.setExtra(jsonObject.get("extra").getAsJsonObject());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		});

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("server-name", this.getServerName());
		jsonObject.addProperty("action", "online");
            this.serverHeartbeatPublisher.write(jsonObject);
	}

	public ServerData getServerDataByName(String name) {
		for (String serverKey : this.getServers().keySet()) {
			if (serverKey.equalsIgnoreCase(name)) {
				return this.getServers().get(serverKey);
			} else {
				if (this.getServers().get(serverKey).getServerName().equalsIgnoreCase(name)) {
					return this.getServers().get(serverKey);
				}
			}
		}

		return null;
	}
}
