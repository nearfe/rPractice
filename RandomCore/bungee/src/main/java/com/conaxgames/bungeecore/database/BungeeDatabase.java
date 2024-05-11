package com.conaxgames.bungeecore.database;

import com.conaxgames.bungeecore.server.ServerData;
import lombok.Getter;
import net.md_5.bungee.api.config.ServerInfo;
import com.conaxgames.bungeecore.CorePlugin;
import com.conaxgames.database.HikariDatabase;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @since 2017-10-06
 */
public class BungeeDatabase extends HikariDatabase {

	private final CorePlugin plugin;
	@Getter
	private Set<UUID> whitelisted = new HashSet<>();

	public BungeeDatabase(CorePlugin plugin) throws Exception {
		super(plugin.getConfiguration().getString("database.host"), plugin.getConfiguration().getInt("database.port"),
				plugin.getConfiguration().getString("database.db"),
				plugin.getConfiguration().getString("database.username"),
				plugin.getConfiguration().getString("database.password"));
		this.plugin = plugin;

		this.init(this.getConnection());
	}

	@Override
	public void init(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(
				"CREATE TABLE IF NOT EXISTS `bungee_ranks` (" +
				"  `uuid` VARCHAR(64) NOT NULL," +
				"  `username` VARCHAR(16) NULL," +
				"  `groups` VARCHAR(128) NULL," +
				"  `proxy` VARCHAR(256) NOT NULL DEFAULT '.*'," +
				"  PRIMARY KEY (`uuid`));"
		);
		preparedStatement.execute();

		preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `bungee_servers` (" +
		                                                "  `server-name` VARCHAR(128) NOT NULL," +
		                                                "  `server-address` VARCHAR(128) NULL," +
		                                                "  `motd` VARCHAR(128) NULL," +
		                                                "  `restricted` VARCHAR(5) NOT NULL DEFAULT 'false'," +
		                                                "  `proxy` VARCHAR(256) NOT NULL DEFAULT '.*'," +
		                                                "  PRIMARY KEY (`server-name`));"
		);
		preparedStatement.execute();

		preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `bungee_whitelist` (" +
		                                                "  `uuid` VARCHAR(128) NOT NULL," +
		                                                "  `username` VARCHAR(128) NULL," +
		                                                "  PRIMARY KEY (`uuid`));"
		);
		preparedStatement.execute();

		preparedStatement.close();
		connection.close();

		this.plugin.getLogger().info("The connection to the database has been made!");
	}

	public void updateUserName(UUID uuid, String userName, boolean whitelist) {
		Connection connection = null;
		try {
			connection = this.getConnection();

			PreparedStatement preparedStatement = connection
					.prepareStatement("UPDATE `" + (whitelist ? "bungee_whitelist" : "bungee_ranks") +
					                  "` SET `username`=? WHERE uuid=?");
			preparedStatement.setString(1, userName);
			preparedStatement.setString(2, uuid.toString());
			preparedStatement.execute();
			preparedStatement.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public List<String> getUsersGroups(UUID uuid) {
		/*List<String> userGroups = new ArrayList<>();

		Connection connection = null;
		try {
			connection = this.getConnection();

			PreparedStatement preparedStatement = connection
					.prepareStatement("SELECT * FROM `bungee_ranks` WHERE uuid=?");
			preparedStatement.setString(1, uuid.toString());
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				if (RedisBungee.getApi().getServerId().matches(resultSet.getString("proxy"))) {
					userGroups.addAll(Arrays.asList(resultSet.getString("groups").split(",")));
				}

			}

			resultSet.close();
			preparedStatement.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return userGroups;*/

		return new ArrayList<>();
	}

	public Map<String, ServerData> getAllServers() {
		Map<String, ServerData> servers = new HashMap<>();

		Connection connection = null;
		try {
			connection = this.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `bungee_servers`");
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				try {
					servers.put(resultSet.getString("server-name"), new ServerData(this.plugin.getProxy()
					                                                                          .constructServerInfo(
							                                                                          resultSet
									                                                                          .getString(
											                                                                          "server-name"),
							                                                                          new InetSocketAddress(
									                                                                          resultSet
											                                                                          .getString(
													                                                                          "server-address")
											                                                                          .split(":")[0],
									                                                                          Integer.parseInt(
											                                                                          resultSet
													                                                                          .getString(
															                                                                          "server-address")
													                                                                          .split(":")[1])),
							                                                                          resultSet
									                                                                          .getString(
											                                                                          "motd"),
							                                                                          resultSet
									                                                                          .getBoolean(
											                                                                          "restricted")),
							resultSet.getString("proxy")));
				} catch (Exception e) {
					e.printStackTrace();
					this.plugin.getLogger().severe("Ignoring adding " + resultSet.getString("server-name"));
				}
			}

			resultSet.close();
			preparedStatement.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return servers;
	}

	public Set<UUID> fetchWhitelistedPlayers() {
		Set<UUID> whitelisted = new HashSet<>();

		Connection connection = null;
		try {
			connection = this.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `bungee_whitelist`");
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				try {
					whitelisted.add(UUID.fromString(resultSet.getString("uuid")));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			resultSet.close();
			preparedStatement.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return whitelisted;
	}
}
