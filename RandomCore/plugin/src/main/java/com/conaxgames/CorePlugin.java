package com.conaxgames;

import com.conaxgames.api.CoreProcessor;
import com.conaxgames.command.DisguiseCommand;
import com.conaxgames.command.MaxPlayersCommand;
import com.conaxgames.command.UndisguiseCommand;
import com.conaxgames.command.impl.*;
import com.conaxgames.command.impl.essentials.*;
import com.conaxgames.command.impl.punish.*;
import com.conaxgames.disguise.DisguiseManager;
import com.conaxgames.entity.EntityManager;
import com.conaxgames.gson.CustomLocationTypeAdapterFactory;
import com.conaxgames.gson.ItemStackTypeAdapterFactory;
import com.conaxgames.listener.*;
import com.conaxgames.manager.*;
import com.conaxgames.redis.*;
import com.conaxgames.register.UserDatabase;
import com.conaxgames.register.UserManager;
import com.conaxgames.server.ServerManager;
import com.conaxgames.settings.SettingsManager;
import com.conaxgames.task.*;
import com.conaxgames.timer.TimerManager;
import com.conaxgames.util.Config;
import com.conaxgames.util.RedisCommand;
import com.conaxgames.util.cmd.CommandManager;
import com.conaxgames.util.finalutil.CC;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BooleanSupplier;

@Getter
public class CorePlugin extends JavaPlugin {

	public static boolean SETUP = false;
	public static GsonBuilder GSONBUILDER;
	public static Gson GSON;

	private static Field bukkitCommandMap;

	@Getter
	private static CorePlugin instance;

	//	private final String PLUGIN_NAME = "Core";
	//	private final double PLUGIN_VERSION = 0.01;

	private String[] helpMessage;
	private List<String> announcements = new ArrayList<>();

	private Set<String> filteredPhrases;
	private Set<String> filteredWords;

	@Setter
	private ShutdownTask shutdownTask = null;
	private CoreRedisManager coreRedisManager;

	//	private AbstractRequestManager requestManager;
	private PunishmentManager punishmentManager;
	private SettingsManager settingsManager;
	private CoreProcessor requestProcessor;
	private CommandManager commandManager;
	private MinemanManager playerManager;
	private EntityManager entityManager;
	private ServerManager serverManager;
	private BoardManager boardManager;
	private FilterManager filterManager;
	private TimerManager timerManager;
	private DisguiseManager disguiseManager;

	private UserDatabase userDatabase;
	private UserManager userManager;

	private JedisConfig jedisConfig;

	private String apiUrl;
	private String apiKey;

	// test commit

	private boolean sendJoinMessages;

	@Setter
	private BooleanSupplier setupSupplier = new BooleanSupplier() {
		private int attempts;

		@Override
		public boolean getAsBoolean() {
			return this.attempts++ >= 3;
		}
	};

	public static String getRequestNameOrUUID(String name) {
		OfflinePlayer targetPlayer = CorePlugin.getInstance().getServer().getPlayerExact(name);
		if (targetPlayer == null) {
			targetPlayer = CorePlugin.getInstance().getServer().getOfflinePlayer(name);
		}

		return targetPlayer == null ? name : targetPlayer.getUniqueId().toString();
	}

	@Override
	public void onEnable() {
		CorePlugin.instance = this;
		CorePlugin.GSONBUILDER = new GsonBuilder()
				.registerTypeAdapterFactory(new CustomLocationTypeAdapterFactory())
				.registerTypeAdapterFactory(new ItemStackTypeAdapterFactory());
		CorePlugin.GSON = CorePlugin.GSONBUILDER.create();


		this.saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		saveConfig();
		this.apiUrl = this.getConfig().getString("api.url");
		this.apiKey = this.getConfig().getString("api.key");

		this.sendJoinMessages = this.getConfig().getBoolean("join-messages", true);

		this.jedisConfig = new JedisConfig(this);

		this.coreRedisManager = new CoreRedisManager(this);
		this.disguiseManager = new DisguiseManager(this);
		this.commandManager = new CommandManager();
		this.entityManager = new EntityManager();
		this.playerManager = new MinemanManager();
		this.filterManager = new FilterManager();
		this.settingsManager = new SettingsManager(this);
		this.serverManager = new ServerManager(this);
		this.requestProcessor = new CoreProcessor(this, this.apiUrl, this.apiKey);
		//		this.requestManager = new RequestManager(this);
		//		this.tabListManager = new TabListManager(this);
		this.punishmentManager = new PunishmentManager(this);
		this.timerManager = new TimerManager(this);

		//this.userDatabase = new UserDatabase();
		//this.userDatabase.handleConnection();
		//this.userManager = new UserManager(this.userDatabase);

		//		this.tabListManager.addTabHandler(new TabListHandler() {
		//			@Override public void updateTabList(Player player, TabList tabList) {
		//				tabList.setEntry("Test " + ThreadLocalRandom.current().nextInt(10), 2, 2);
		//			}
		//
		//			@Override public void setTabList(Player player, TabList tabList) {
		//				tabList.setEntry("Test", 2, 2);
		//			}
		//		});

		//EntityPacketHandler packetHandler = new EntityPacketHandler();
		//Helium.INSTANCE.addPacketHandler(packetHandler);

		Config config = new Config("filter", this);
		this.filteredPhrases = new HashSet<>(config.getConfig().getStringList("filtered-phrases"));
		this.filteredWords = new HashSet<>(config.getConfig().getStringList("filtered-words"));

		this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		this.getServer().getPluginManager().registerEvents(new ServerListener(this), this);

		/** We don't use, leave commented **/
		this.getServer().getPluginManager().registerEvents(new UIListener(), this);

		this.getServer().getPluginManager().registerEvents(new RankListener(), this);

		this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeListener(this));
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "Permissions");

		new JedisSubscriber<>(jedisConfig.toJedisSettings(), "proxy-core", JsonObject.class, new BungeeRedisManager());

		FreezeCommand command = new FreezeCommand();
		this.getServer().getPluginManager().registerEvents(command, this);

		Arrays.asList(
				new BanCommand(), new MuteCommand(), new KickCommand(), new BroadcastCommand(), new StoreAlertCommand(), new TopCommand(),
				new UnbanCommand(), new UnmuteCommand(), new BlacklistCommand(), new UnblacklistCommand(),
				new IPBanCommand(), new ReportCommand(this), new RequestCommand(this),
				new CreativeCommand(), new GamemodeCommand(), new SurvivalCommand(), new FeedCommand(),
				new HealCommand(), new InvseeCommand(), new PingCommand(), new FlyCommand(), new JoinCommand(), new RenameCommand(),
				new ClearCommand(), new WorldCommand(), new MoreCommand(), new SpawnerCommand(), new SpeedCommand(), new WhitelistCommand(),
				new DisguiseCommand(this), new UndisguiseCommand(this)
		).forEach(this::registerCommand);

		commandManager.registerAllClasses(Arrays.asList(new RankCommand(),
				new PunishmentHistoryCommand(), new BanInfoCommand(), command,
				new MessageCommand(), new ReplyCommand(), new IgnoreCommand(),
				new StaffChatCommand(), new VanishCommand(), new SilenceChatCommand(),
				new ToggleMessagesCommand(), new ToggleChatCommand(),
				new WhoCommand(), new ClearChatCommand(), new AltsCommand(),
				new ColorCommand(), new ShutdownCommand(), new SettingsCommand(),
				new DevChatCommand(), new SlowChatCommand(), new TeleportCommand(),
				new ClearEntitiesCommand(), new HostChatCommand(),
				new ToggleStaffMessagesCommand(), new MaxPlayersCommand(this),
				new RegisterCommand(), new InfoCommand(), new TimeCommands(),
				new StaffDisguiseCommands()));

		new BukkitRunnable() {
			@Override
			public void run() {
				if (setupSupplier.getAsBoolean()) {
					CorePlugin.SETUP = true;
					if (!getConfig().getBoolean("server-data.private-server")) {
						getServerManager().setJoinable(true);
					}

					getLogger().info("Server is now Up!");
					this.cancel();
				} else {
					getLogger().info("Waiting to be setup...");
				}
			}
		}.runTaskTimerAsynchronously(this, 0L, 20L);

		new ServerHeartbeatTask(this).runTaskTimerAsynchronously(this, 20L, 20L);
		new ServerHeartbeatTimeoutTask(this).runTaskTimerAsynchronously(this, 20L, 20L);
		new AutoMessageTask();
		//new BroadcastTask(this).runTaskTimerAsynchronously(this, 20L * 60L, 20L * 60L * 2L);
		new AutomaticShutdownTask().runTaskTimerAsynchronously(this,  0L, 20L);

		if (getConfig().getBoolean("server-data.private-server")) {
			playerManager.setStaffOnly(true);
		}

		// TODO remove when done testing
		if (serverManager.getServerName().equalsIgnoreCase("hcf")) {
			playerManager.setDevOwnerOnly(true);
		}

		String serverName = serverManager.getServerName();

		if(serverName.contains("practice")) {
			helpMessage = new String[] {
					"&7&m" + StringUtils.repeat("-", 45),
					CC.PRIMARY + "Practice Help",
					"",
					CC.AQUA + "Basic Commands:",
					CC.RESET + " /party " + CC.GRAY + "Displays all party commands",
					CC.RESET + " /duel <player> " + CC.GRAY + "Send a duel request",
					CC.RESET + " /accept " + CC.GRAY + "Accept a duel request",
					"",
					CC.AQUA + "Chat Channels:",
					CC.RESET + " Team: " + CC.GRAY + "Prefix your message with " + CC.RESET + "@",
					"",
					CC.AQUA + "Settings:",
					CC.RESET + " /settings " + CC.GRAY + "Displays all party commands",
					CC.RESET + " /tpm " + CC.GRAY + "Toggle private messages",
					CC.RESET + " /tgc " + CC.GRAY + "Toggle global chat",
					CC.RESET + " /tr " + CC.GRAY + "Toggle duel requests",
					CC.RESET + " /tsb " + CC.GRAY + "Toggle scoreboard",
					CC.RESET + " /tsp " + CC.GRAY + "Toggle spectators (in your matches)",
					"&7&m" + StringUtils.repeat("-", 45),
			};
		} else if(serverName.contains("uhc")) {
			helpMessage = new String[]{
					"&7&m" + StringUtils.repeat("-", 45),
					CC.PRIMARY + "UHC Help",
					"",
					CC.AQUA + "Basic Commands:",
					CC.RESET + " /config " + CC.GRAY + "Display the UHC game configuration",
					CC.RESET + " /team " + CC.GRAY + "Displays all team commands",
					CC.RESET + " /stats <player> " + CC.GRAY + "Display statistics of a player",
					"",
					CC.AQUA + "Chat Channels:",
					CC.RESET + " Team: " + CC.GRAY + "Prefix your message with " + CC.RESET + "@",
					"",
					CC.AQUA + "Settings:",
					CC.RESET + " /settings " + CC.GRAY + "Displays all party commands",
					CC.RESET + " /tpm " + CC.GRAY + "Toggle private messages",
					CC.RESET + " /tgc " + CC.GRAY + "Toggle global chat",
					CC.RESET + " /tr " + CC.GRAY + "Toggle duel requests",
					CC.RESET + " /tsb " + CC.GRAY + "Toggle scoreboard",
					"&7&m" + StringUtils.repeat("-", 45),
			};
		} else if(serverName.contains("hub") || serverName.contains("lobby")) {
			helpMessage = new String[] {
					CC.RED + "You are in the " + CC.B_RED + "Lobby" + CC.RED + ". Join a server to get started!"
			};
		} else {
			helpMessage = new String[] {
					CC.RED + "You do not need help on this server."
			};
		}

		Bukkit.getScheduler().runTaskTimer(this,
				() -> playerManager.getPlayers().values().removeIf(mineman -> mineman.getPlayer() == null),
				20 * 60L, 20 * 60L);

		//		this.getServer().getScheduler().runTaskTimer(this, () -> {
		//			this.tabListManager.updatePlayers();
		//		}, 20L, 10L);
	}

	@Override
	public void onDisable() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("server-name", this.getServerManager().getServerName());
		jsonObject.addProperty("action", "offline");
		this.getServerManager().getServerHeartbeatPublisher().write(jsonObject);

		JsonObject proxyObject = new JsonObject();
		proxyObject.addProperty("disable", true);
		proxyObject.addProperty("server", this.getServerManager().getServerName());
		this.getServerManager().getProxyPublisher().write(proxyObject);

		instance = null;
	}

	private void registerCommand(Command cmd) {
		this.registerCommand(cmd, this.getName());
	}

	public void registerCommand(Command cmd, String fallbackPrefix) {
		//MinecraftServer.getServer().server.getCommandMap().register(cmd.getName(), fallbackPrefix, cmd);
		try {
			if (bukkitCommandMap == null) {
				bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
				bukkitCommandMap.setAccessible(true);
			}
			CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
			commandMap.register(cmd.getName(), fallbackPrefix, cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setBoardManager(BoardManager boardManager) {
		this.boardManager = boardManager;

		long interval = this.boardManager.getAdapter().getInterval();

		this.getServer().getScheduler().runTaskTimerAsynchronously(this, this.boardManager, 0L, interval);
	}

	public void runRedisCommand(RedisCommand<Jedis> jedisExecutor) {
		JedisSettings jedisSettings = this.jedisConfig.toJedisSettings();
		Jedis jedis = jedisSettings.getJedisPool().getResource();

		jedisExecutor.execute(jedis);
	}

	public int getServerPlayerCount() {
	    return Bukkit.getOnlinePlayers().size();
    }
}
