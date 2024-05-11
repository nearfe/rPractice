package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.api.abstr.AbstractBukkitCallback;
import com.conaxgames.api.impl.PlayerRequest;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Timestamp;
import java.text.MessageFormat;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
public class BanInfoCommand implements CommandHandler {

	private static void appendBuilder(StringBuilder sb, boolean state, long time, JsonElement reason) {
		sb.append(CC.PRIMARY);
		if (time != 0 && state) {
			String timeStamp = new Timestamp(time).toString();
			timeStamp = timeStamp.substring(0, timeStamp.indexOf("."));

			sb.append(CC.PRIMARY).append("Yes").append(CC.SECONDARY).append(" until ").append(CC.PRIMARY).append(timeStamp);

		} else if (state) {
			sb.append(CC.PRIMARY).append("Forever");
		} else {
			sb.append(CC.PRIMARY).append("No");
		}

		if (reason != null && !reason.isJsonNull()) {
			sb.append(CC.GRAY).append(" (").append(reason.getAsString()).append(")");
		}
	}

	@Command(name = {"staffinfo", "sa"}, rank = Rank.ADMIN, description = "Get info on a staff member's activity")
	public void onStaffInfo(Player sender,
							@Param(name = "target") String name,
							@Param(name = "time", defaultTo = "lifetime") String time) {

		// TODO: Revert this command
		sender.sendMessage(CC.YELLOW + "TODO: Revert this command");
		/*CorePlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(CorePlugin.getInstance(),
				() -> {
					Timestamp timestamp = null;
					if (!time.equalsIgnoreCase("lifetime")) {
						long millis = TimeUtil.parseTime(time);
						if (millis == -1) {
							sender.sendMessage(CC.RED + "Invalid time frame");
							return;
						}

						timestamp = TimeUtil.fromMillis(System.currentTimeMillis() - millis);
					}

					Connection connection = null;
					PreparedStatement preparedStatement = null;
					ResultSet resultSet = null;

					int id = -1;
					try {
						connection = CorePlugin.getInstance().getMainDatabase().getConnection();
						preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE name = ?;");
						preparedStatement.setString(1, name);

						resultSet = preparedStatement.executeQuery();

						if (!resultSet.next()) {
							sender.sendMessage(CC.RED + "Failed to find that player.");
							return;
						}

						id = resultSet.getInt("player_id");
					} catch (SQLException e) {
						sender.sendMessage(CC.RED + "Error fetching staff info. Check console for details.");
						e.printStackTrace();
					} finally {
						CorePlugin.getInstance().getMainDatabase().cleanup(preparedStatement, resultSet);
					}

					if (id == -1) {
						return;
					}

					try {
						preparedStatement = connection
								.prepareStatement("SELECT * FROM punishments WHERE punisher_id = ?" +
								                  (timestamp != null ? ", timestamp > ?;" : ";"));
						preparedStatement.setInt(1, id);
						if (timestamp != null) {
							preparedStatement.setTimestamp(2, timestamp);
						}

						resultSet = preparedStatement.executeQuery();

						int bans = 0;
						int mutes = 0;

						while (resultSet.next()) {
							String type = resultSet.getString("type");
							if (time.toUpperCase().contains("UN")) {
								continue;
							}

							if (type.toUpperCase().contains("BAN")) {
								bans++;
							} else if (type.toUpperCase().contains("MUTE")) {
								mutes++;
							}
						}

						sender.sendMessage(CC.PRIMARY + name + CC.SECONDARY + " Staff Info" +
						                   (timestamp != null ? " since " + timestamp.toString() : "") + ":");
						sender.sendMessage(CC.SECONDARY + "Bans: " + CC.PRIMARY + bans);
						sender.sendMessage(CC.SECONDARY + "Mutes: " + CC.PRIMARY + mutes);
					} catch (SQLException e) {
						sender.sendMessage(CC.RED + "Error fetching staff info. Check console for details.");
						e.printStackTrace();
					} finally {
						CorePlugin.getInstance().getMainDatabase().cleanup(connection, preparedStatement, resultSet);
					}
				});*/
	}

	@Command(name = {"baninfo", "bminfo", "binfo"}, rank = Rank.TRAINEE,
			description = "Get info on a player's punishments.")
	public void onBanInfo(Player sender, @Param(name = "target") String name) {
		new BukkitRunnable() {
			@Override
			public void run() {
				CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(new PlayerRequest.BanInfoRequest(CorePlugin.getRequestNameOrUUID(name)),
						new AbstractBukkitCallback() {
							@Override
							public void callback(JsonElement element) {
								JsonObject data = element.getAsJsonObject();

								String response = data.get("response").getAsString();

								if (response.equalsIgnoreCase("player-not-found")) {
									sender.sendMessage(CC.RED + "Failed to find that player.");
									return;
								}

								String rName = data.get("name").getAsString();

								JsonElement blacklistReason = data.get("blacklist-reason");
								JsonElement muteReason = data.get("mute-reason");
								JsonElement banReason = data.get("ban-reason");

								boolean blacklisted = data.get("blacklisted").getAsBoolean();
								boolean banned = data.get("banned").getAsBoolean();
								boolean muted = data.get("muted").getAsBoolean();

								long muteTime = data.get("mute-time").getAsLong();
								long banTime = data.get("ban-time").getAsLong();

								StringBuilder sb = new StringBuilder(
										CC.SECONDARY + "Punishment status for " + CC.PRIMARY + rName + CC.SECONDARY + "\n\n");

								sb.append("\n\n");

								sb.append(CC.SECONDARY).append("Muted: ");
								appendBuilder(sb, muted, muteTime, muteReason);
								sb.append("\n");

								sb.append(CC.SECONDARY).append("Banned: ");
								appendBuilder(sb, banned, banTime, banReason);
								sb.append("\n");

								sb.append(CC.SECONDARY).append("Blacklisted: ").append(CC.PRIMARY).append(blacklisted ? "Yes" : "No");
								if(blacklistReason != null && !blacklistReason.isJsonNull()) {
									sb.append(CC.GRAY).append(" (").append(blacklistReason.getAsString()).append(")");
								}

								sender.sendMessage("");
								sender.sendMessage(sb.toString());
								sender.sendMessage("");
							}

							@Override
							public void onError(String message) {
								super.onError(message);
								sender.sendMessage(MessageFormat.format("{0}Something went wrong while fetching the ban information of ''{1}''.", CC.RED, name));
							}
						});
			}
		}.runTaskAsynchronously(CorePlugin.getInstance());
	}
}
