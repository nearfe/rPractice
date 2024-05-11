package com.conaxgames.redis;

import com.conaxgames.redis.JedisPublisher;
import com.conaxgames.redis.JedisSubscriber;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.Color;
import com.conaxgames.util.finalutil.StringUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.*;
import org.apache.http.message.BasicNameValuePair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.conaxgames.CorePlugin;
import com.conaxgames.clickable.Clickable;
import com.conaxgames.redis.subscription.JedisSubscriptionHandler;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoreRedisManager {

	private static final Pattern CLICKABLE_PATTERN = Pattern.compile("(.*)(\\{clickable::command=\"(.*)\"})(.*)(\\{/clickable})(.*)");
	private final JedisSubscriber<JsonObject> messagesSubscriber;
	private final JedisPublisher<JsonObject> messagesPublisher;
	//@Getter
	//private final JedisStorage storage;
	private final CorePlugin plugin;

	public CoreRedisManager(CorePlugin plugin) {
		this.plugin = plugin;

		this.messagesSubscriber = new JedisSubscriber<>(this.plugin.getJedisConfig().toJedisSettings(),
				"global-messages", JsonObject.class, new GlobalMessageSubscriptionHandler());

		this.messagesPublisher = new JedisPublisher<>(this.plugin.getJedisConfig().toJedisSettings(),
				"global-messages");

		//this.storage = new JedisStorage(this.plugin.getJedisConfig().toJedisSettings());
	}

	public static String getServerMessagePrefix() {
		return CC.GRAY + "[" +
		       (CorePlugin.getInstance().getConfig().getBoolean("serverdata.nice-name-global-chat")
		        ? StringUtil.toNiceString(CorePlugin.getInstance().getServerManager().getServerName())
		        : CorePlugin.getInstance().getServerManager().getServerName()) + "] ";
	}

	public static boolean handleMessage(Mineman mineman, String message) {
		return handleMessage(mineman, message, mineman.getChatType());
	}

	public static boolean handleMessage(Mineman mineman, String message, Mineman.ChatType chatType) {
		if(!mineman.isCanSeeStaffMessages()) {
			return false;
		}

		Rank rank = mineman.getRank();

		String suffix = rank.getColor() + mineman.getName() + CC.AQUA + ": " + message;

		if (chatType == Mineman.ChatType.STAFF) {
			CorePlugin.getInstance().getCoreRedisManager().sendMessage(
					getServerMessagePrefix() + CC.BLUE + "[Staff] " + suffix, Rank.TRAINEE);
			return true;
		} else if (chatType == Mineman.ChatType.DEV) {
			CorePlugin.getInstance().getCoreRedisManager().sendMessage(
					getServerMessagePrefix() + CC.BLUE + "[Dev] " + suffix, Rank.DEVELOPER);
			return true;
		} else if (chatType == Mineman.ChatType.HOST) {
			CorePlugin.getInstance().getCoreRedisManager().sendMessage(
					getServerMessagePrefix() + CC.BLUE + "[Host] " + suffix, Rank.HOST);
			return true;
		}
		return false;
	}

	private static BaseComponent[] parseMessage(String message) {
		Clickable clickable = new Clickable();

		Matcher clickableMatcher = CLICKABLE_PATTERN.matcher(message);
		if (clickableMatcher.matches()) {
			clickable.add(clickableMatcher.group(1));
			clickable.add(clickableMatcher.group(4), null, clickableMatcher.group(3));
			clickable.add(clickableMatcher.group(6));
		} else {
			clickable.add(message);
		}

		return clickable.asComponents();
	}

	public void broadcastGloballyClickable(String message, String hover, String command) {
		this.broadcastClickable(message, "clickable", hover, command);
	}

	public void broadcastClickable(String message, String type, String hover, String command) {
		JsonObject jsonObject = this.generateBaseMessage(message);

		if (type != null) {
			jsonObject.addProperty("type", type);
		}

		if (hover != null) {
			jsonObject.addProperty("hover", hover);
		}

		if (command != null) {
			jsonObject.addProperty("command", command);
		}

		this.messagesPublisher.write(jsonObject);
	}

	public void broadcastGlobally(String message) {
		this.broadcast(message, "global", null);
	}

	public void kickPlayer(String player, String server, String message) {
		this.punishPlayer(player, server, message, "kick");
	}

	public void mutePlayer(String player, String server, String message, Timestamp expiry) {
		this.punishPlayer(player, server, message, "mute",
				new BasicNameValuePair("time", String.valueOf(expiry == null ? null : expiry.getTime())));
	}

	private void punishPlayer(String player, String server, String message, String type,
	                          BasicNameValuePair... args) {
		JsonObject jsonObject = this.generateBaseMessage(message);

		jsonObject.addProperty("type", type);
		jsonObject.addProperty("player", player);
		jsonObject.addProperty("server", server);

		for (BasicNameValuePair arg : args) {
			jsonObject.addProperty(arg.getName(), arg.getValue());
		}

		this.messagesPublisher.write(jsonObject);
	}

	public void broadcastServer(String message, String regex) {
		this.broadcast(message, "server", regex);
	}

	public void broadcast(String message, String type, String server) {
		JsonObject jsonObject = this.generateBaseMessage(message);

		if (type != null) {
			jsonObject.addProperty("type", type);
		}

		if (server != null) {
			jsonObject.addProperty("server", server);
		}

		this.messagesPublisher.write(jsonObject);
	}

	/**
	 * Generates a base message for sending across the network.
	 *
	 * @param message
	 * @return
	 */
	private JsonObject generateBaseMessage(String message) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("server-id", this.plugin.getServerManager().getServerName());
		jsonObject.addProperty("message", message);
		return jsonObject;
	}

	public void sendMessage(String message, Rank rank, JsonObject append) {
		JsonObject jsonObject = this.generateBaseMessage(message);
		jsonObject.addProperty("rank", rank.getName());

		if (append != null) {
			jsonObject.add("extra", append);
		}

		this.messagesPublisher.write(jsonObject);
	}

	public void sendMessage(String message, Rank fromRank, String from, UUID target, boolean filter) {
		JsonObject jsonObject = this.generateBaseMessage(message);

		jsonObject.addProperty("from", from);
		jsonObject.addProperty("from-rank", fromRank.getName());
		jsonObject.addProperty("target", target.toString());
		jsonObject.addProperty("type", "direct-message");
		jsonObject.addProperty("filter", filter);

		this.messagesPublisher.write(jsonObject);
	}

	public void sendMessage(String message, Rank rank) {
		this.sendMessage(message, rank, null);
	}

	class GlobalMessageSubscriptionHandler implements JedisSubscriptionHandler<JsonObject> {

		@Override
		public void handleMessage(JsonObject object) {
			String serverId = object.get("server-id").getAsString();

			String message = ChatColor.translateAlternateColorCodes('&', object.get("message").getAsString());

			if (object.get("rank") != null) {
				Bukkit.getOnlinePlayers().stream()
						.filter(o -> plugin.getPlayerManager().getPlayer(o.getUniqueId()) != null
								&& plugin.getPlayerManager().getPlayer(o.getUniqueId()).hasRank(Rank.getByName(object.get("rank").getAsString()))
								&& plugin.getPlayerManager().getPlayer(o.getUniqueId()).isCanSeeStaffMessages())
						.forEach(o -> o.sendMessage(Color.translate(message)));
			} else {
				JsonElement jsonElement = object.get("type");
				if (jsonElement != null) {
					String type = jsonElement.getAsString();
					switch (type.toLowerCase()) {
						case "global":
							CoreRedisManager.this.plugin.getServer().broadcastMessage(message);
							break;
						case "clickable":
							TextComponent click = new TextComponent(message);

							click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, object.get("command").getAsString()));
							click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Color.translate(object.get("hover").getAsString())).create()));

							Bukkit.getOnlinePlayers().forEach(o -> o.sendMessage(click));
							break;
						case "kick":
							jsonElement = object.get("target");
							if (jsonElement != null) {
								String target = jsonElement.getAsString();
								Player player = plugin.getServer().getPlayer(target);

								if (player != null) {
									player.kickPlayer(message);

									if(player.getAddress().getAddress() != null) {
										Bukkit.getOnlinePlayers().stream()
												.filter(o -> o.getAddress().getAddress()
														.equals(player.getAddress().getAddress()))
												.forEach(o -> o.kickPlayer(message));
									}
								}
							}
							break;
						case "mute":
							jsonElement = object.get("target");
							if (jsonElement != null) {
								String target = jsonElement.getAsString();
								Player player = plugin.getServer().getPlayer(target);

								if (player != null) {
									Mineman mineman = plugin.getPlayerManager().getPlayer(player.getUniqueId());

									Timestamp expiry = null;
									jsonElement = object.get("time");
									if (!jsonElement.isJsonNull()) {
										expiry = new Timestamp(Long.parseLong(jsonElement.getAsString()));
									}

									if (mineman != null) {
										mineman.setMuteTime(expiry);
										mineman.setMuted(true);
									}

									player.sendMessage(CC.RED + "You have been muted for " + message);
								}
							}
							break;
						case "server":
							jsonElement = object.get("server");
							if (jsonElement != null) {
								String server = jsonElement.getAsString();
								if (CorePlugin.getInstance().getServerManager().getServerName().matches(server)) {
									BaseComponent[] components = parseMessage(message);
									for (Player player : plugin.getServer().getOnlinePlayers()) {
										player.sendMessage(components);
									}
								}
							}
							break;
						case "direct-message":
							jsonElement = object.get("target");
							if (jsonElement != null) {
								String targetUuid = jsonElement.getAsString();
								UUID uuid = UUID.fromString(targetUuid);

								Player target = CorePlugin.getInstance().getServer().getPlayer(uuid);
								Mineman targetMineman = CorePlugin.getInstance().getPlayerManager().getPlayer(uuid);

								if (target != null) {
									String from = object.get("from").getAsString();
									if (!object.get("filter").getAsBoolean()) {
										Rank fromRank = Rank.getByName(object.get("from-rank").getAsString());
										if ((targetMineman.isCanSeeMessages() && !targetMineman.isIgnoring(targetMineman.getId())) ||
												fromRank.hasRank(Rank.TRAINEE)) {

											String formatted = StringUtil.formatPrivateMessage(targetMineman.getRank().getColor() + target.getName(),
													fromRank.getColor() + from, message)[1];

											target.sendMessage(formatted);

											targetMineman.setLastConversation(from);
										}
									}

									JsonObject jsonObject = generateBaseMessage(message);

									jsonObject.addProperty("from", from);
									jsonObject.addProperty("target-name", target.getName());
									jsonObject.addProperty("type", "direct-message-received");
									jsonObject.addProperty("target-rank", targetMineman.getRank().getColor());

									messagesPublisher.write(jsonObject);
								}
							}
							break;
						case "direct-message-received":
							String from = object.get("from").getAsString();
							Player player = CorePlugin.getInstance().getServer().getPlayer(from);

							if (player != null) {
								String name = object.get("target-name").getAsString();
								String rank = object.get("target-rank").getAsString();

								String formatted = StringUtil.formatPrivateMessage(
										rank + name, player.getName(), message)[0];
								player.sendMessage(formatted);

								Mineman mineman = CorePlugin.getInstance().getPlayerManager()
										.getPlayer(player.getUniqueId());
								mineman.setLastConversation(name);
							}
							break;
					}
				} else {
					plugin.getLogger().warning("Unknown message type for global message from " + serverId);
				}
			}
		}
	}

}
