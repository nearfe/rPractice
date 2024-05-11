package club.minion.practice;

import club.minion.practice.board.BoardAdapter;
import club.minion.practice.commands.*;
import club.minion.practice.commands.duel.AcceptCommand;
import club.minion.practice.commands.duel.DuelCommand;
import club.minion.practice.commands.duel.SpecCommand;
import club.minion.practice.commands.event.HostCommand;
import club.minion.practice.commands.event.JoinEventCommand;
import club.minion.practice.commands.management.ArenaCommand;
import club.minion.practice.commands.management.KitCommand;
import club.minion.practice.commands.management.RankedCommand;
import club.minion.practice.commands.management.SpawnsCommand;
import club.minion.practice.commands.time.DayCommand;
import club.minion.practice.commands.time.NightCommand;
import club.minion.practice.commands.time.SunsetCommand;
import club.minion.practice.commands.toggle.ToggleDuelCommand;
import club.minion.practice.commands.toggle.ToggleScoreboardCommand;
import club.minion.practice.commands.toggle.ToggleSpectatorsCommand;
import club.minion.practice.commands.warp.WarpCommand;
import club.minion.practice.ffa.FFAManager;
import club.minion.practice.file.ConfigHandler;
import club.minion.practice.jedis.JedisHandler;
import club.minion.practice.leaderboard.LeaderboardsMenu;
import club.minion.practice.listeners.*;
import club.minion.practice.managers.*;
import club.minion.practice.menu.ButtonListener;
import club.minion.practice.player.PracticePlayerData;
import club.minion.practice.runnable.ExpBarRunnable;
import club.minion.practice.runnable.SaveDataRunnable;
import club.minion.practice.settings.PracticeSettingsHandler;
import club.minion.practice.task.PremiumResetTask;
import club.minion.practice.task.RespawnTask;
import club.minion.practice.util.Animation;
import club.minion.practice.util.StatusCache;
import club.minion.practice.util.board.Assemble;
import club.minion.practice.util.menu.MenuListener;
import com.conaxgames.CorePlugin;
import com.conaxgames.redis.JedisPublisher;
import com.conaxgames.redis.JedisSubscriber;
import com.conaxgames.timer.impl.EnderpearlTimer;
import com.conaxgames.util.Config;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.qrakn.phoenix.lang.file.type.BasicConfigurationFile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class Practice extends JavaPlugin {

	@Getter
	private static Practice instance;

	private Config mainConfig;
	@Getter private Config scoreboardConfig;

	@Getter public static JsonParser PARSER = new JsonParser();


	private InventoryManager inventoryManager;
	private EditorManager editorManager;
	private PlayerManager playerManager;
	private ArenaManager arenaManager;
	private MatchManager matchManager;
	private PartyManager partyManager;
	private QueueManager queueManager;
	private EventManager eventManager;
	private ItemManager itemManager;
	private KitManager kitManager;
	private ScoreboardColorManager scoreboardColorManager;
	public FFAManager getFfaManager() {
		return this.ffaManager;
	}
	private FFAManager ffaManager;
	private SpawnManager spawnManager;
	private TournamentManager tournamentManager;
	public FileConfiguration getExtra() {
		return ConfigHandler.Configs.EXTRA.getConfig();
	}
	private ChunkManager chunkManager;

	private JedisSubscriber<? extends JsonObject> practiceSubscriber;
	private JedisPublisher practicePublisher;
	private LeaderboardsMenu leaderboardsMenu;
	private LeaderboardsMenu lbsManager;
	private BasicConfigurationFile scoreboardColorConfig;
	private Assemble assemble;

	@Override
	public void onDisable() {
		this.arenaManager.saveArenas();
		this.kitManager.saveKits();
		this.spawnManager.saveConfig();
		for (final PracticePlayerData playerData : this.playerManager.getAllData()) {
			this.playerManager.saveData(playerData);
		}
	}


	@Override
	public void onEnable() {
		Practice.instance = this;
		scoreboardColorConfig = new BasicConfigurationFile(this, "scoreboard-color");

		this.mainConfig = new Config("config", this);
		this.scoreboardConfig = new Config("scoreboard", this);

		if (CorePlugin.getInstance().getTimerManager().getTimer(EnderpearlTimer.class) == null) {
			CorePlugin.getInstance().getTimerManager().registerTimer(new EnderpearlTimer());
		}

		assemble = new Assemble(this, new BoardAdapter());
		disableLoggers();
		Animation.init();
		CorePlugin.getInstance().getCommandManager().registerAllClasses(Collections.singletonList(
				new PremiumCommand()
		));
		CorePlugin.getInstance().getSettingsManager().addSettingsHandler(new PracticeSettingsHandler());

		this.practiceSubscriber = new JedisSubscriber<>(CorePlugin.getInstance().getJedisConfig().toJedisSettings(),
				"practice", JsonObject.class, new JedisHandler());
		this.practicePublisher = new JedisPublisher(CorePlugin.getInstance().getJedisConfig().toJedisSettings(),
				"practice");

		this.registerCommands();
		this.registerListeners();
		this.registerManagers();
		this.registerPremiumTimer();

		this.getServer().getScheduler().runTaskTimerAsynchronously(this, new SaveDataRunnable(),
				20L * 60L * 5L, 20L * 60L * 5L);

		this.getServer().getScheduler().runTaskTimerAsynchronously(this, new ExpBarRunnable(), 2L, 2L);

		for (Entity entity : Bukkit.getWorlds().get(0).getEntities()) {
			if (entity instanceof Player) {
				continue;
			}
			entity.remove();
		}

		lbsManager = new LeaderboardsMenu();

		for (World world : Bukkit.getWorlds()) {
			world.setGameRuleValue("doDaylightCycle", "false");
			world.setGameRuleValue("doMobSpawning", "false");
			world.setGameRuleValue("doFireTicks", "false");
			world.setGameRuleValue("showDeathMessage", "false");
		   world.setTime(6_000L);
		}

		//new Mongo();
	}

	private void registerCommands() {
		CorePlugin.getInstance().getCommandManager().registerAllClasses(Arrays.asList(
				new FlyCommand()
		));

		Arrays.asList(
				new ToggleDuelCommand(),
				new ToggleSpectatorsCommand(),
				new ToggleScoreboardCommand(),
				new ResetStatsCommand(),
				new AcceptCommand(),
				new RankedCommand(),
				new HostCommand(),
				new SunsetCommand(),
				new JoinEventCommand(),
				new ArenaCommand(),
				new NightCommand(),
				new PartyCommand(),
				new DuelCommand(),
				new LagCommand(),
				new TrollCommand(),
				new SpecCommand(),
				new KillMessageCommand(),
				new KillEffectCommand(),
				new DayCommand(),
				new KitCommand(),
				new EloCommand(),
				new InvCommand(),
				new SpawnsCommand(),
				new WarpCommand(),
				new TournamentCommand()

		).forEach(command -> CorePlugin.getInstance().registerCommand((Command) command, getName()));
	}

	private void registerListeners() {
		Arrays.asList(
				new EntityListener(),
				new PlayerListener(),
				new MatchListener(),
				new WorldListener(),
				new ShutdownListener(),
				new MenuListener(),
				new club.minion.practice.util.menu.ButtonListener(),
				new ButtonListener(),
				new InventoryListener()
		).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
	}

	private void registerManagers() {
		this.spawnManager = new SpawnManager();
		this.arenaManager = new ArenaManager();
		this.chunkManager = new ChunkManager();
		this.editorManager = new EditorManager();
		this.itemManager = new ItemManager();
		this.kitManager = new KitManager();
		this.matchManager = new MatchManager();
		this.partyManager = new PartyManager();
		this.leaderboardsMenu = new LeaderboardsMenu();
		this.scoreboardColorManager = new ScoreboardColorManager(this);
		this.playerManager = new PlayerManager();
		this.queueManager = new QueueManager();
		this.inventoryManager = new InventoryManager();
		this.eventManager = new EventManager();
		this.tournamentManager = new TournamentManager();
		new StatusCache().start();
	}

		/*this.ffaManager = new FFAManager(this, CustomLocation.fromBukkitLocation(PlayerState.SPAWN),
				this.kitManager.getKit("SoupRefill"));

		 */

	private void disableLoggers() {
		Logger.getLogger("org.mongodb.driver.connection").setLevel(Level.OFF);
		Logger.getLogger("org.mongodb.driver.cluster").setLevel(Level.OFF);
	}



	private void registerPremiumTimer() {
		if (this.getConfig().getBoolean("parent")) {
			CorePlugin.getInstance().runRedisCommand(redis -> {
				String lastUpdateTime = redis.get("practice:premium:match_reset");

				if (!lastUpdateTime.isEmpty()) {
					long lastTime = Long.parseLong(lastUpdateTime);
				}

			});
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("EST"));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		Date date = calendar.getTime();

		Timer timer = new Timer();
		timer.schedule(new PremiumResetTask(), date.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));

		for (Player player : Bukkit.getOnlinePlayers()) {
			Location respawnLocation = player.getLocation();
			RespawnTask respawnTask = new RespawnTask(player);
			respawnTask.runTaskLater(this, 20L);
		}
	}
}
