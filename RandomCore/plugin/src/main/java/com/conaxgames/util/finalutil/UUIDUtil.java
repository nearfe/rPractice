package com.conaxgames.util.finalutil;

import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.conaxgames.CorePlugin;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class UUIDUtil {
	private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";
	private static final Map<String, UUID> CACHE = new HashMap<>();
	private static final JSONParser PARSER = new JSONParser();

	private UUIDUtil() {
		throw new RuntimeException("Cannot instantiate a utility class.");
	}

	private static void writeBody(HttpURLConnection connection, String body) throws Exception {
		OutputStream stream = connection.getOutputStream();
		stream.write(body.getBytes());
		stream.flush();
		stream.close();
	}

	private static HttpURLConnection createConnection() throws Exception {
		URL url = new URL(PROFILE_URL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		return connection;
	}

	private static UUID getUUID(String id) {
		return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
	}

	public static UUID getUUIDFromName(String nig) {
		if (CACHE.containsKey(nig)) return CACHE.get(nig);
		return new AsyncUUIDFetcher(nig).getUniqueID();
	}

	@Getter
	private static class AsyncUUIDFetcher {
		private UUID uniqueID;

		private AsyncUUIDFetcher(String nig) {
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						HttpURLConnection connection = createConnection();
						String body = JSONArray.toJSONString(Collections.singletonList(nig));
						writeBody(connection, body);
						JSONArray array = (JSONArray) PARSER.parse(new InputStreamReader(connection.getInputStream()));
						for (Object profile : array) {
							JSONObject jsonProfile = (JSONObject) profile;
							String id = (String) jsonProfile.get("id");
							uniqueID = getUUID(id);
							CACHE.put(nig, uniqueID);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.runTaskAsynchronously(CorePlugin.getInstance());
		}
	}
}
