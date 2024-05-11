package com.conaxgames.mineman;

import com.conaxgames.CorePlugin;
import com.conaxgames.api.impl.*;
import com.conaxgames.command.impl.PrefixCommand;
import com.conaxgames.event.player.MinemanRetrieveEvent;
import com.conaxgames.event.player.RankChangeEvent;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.BanWrapper;
import com.conaxgames.util.auth.ImageMapRenderer;
import com.conaxgames.util.auth.TimeBasedOneTimePasswordUtil;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.PlayerUtil;
import com.conaxgames.util.finalutil.StringUtil;
import com.conaxgames.util.finalutil.TimeUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.zxing.WriterException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.permissions.PermissionAttachment;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Setter
@Getter
@RequiredArgsConstructor
public class Mineman {

	private final Set<Integer> ignoring = new HashSet<>();
	private final Set<String> prefixes = new HashSet<>();

	private final UUID uuid;
	private final String name;
	private final InetAddress ipAddress;

	private Rank rank = Rank.NORMAL;
	private Rank disguiseRank;

	private BanWrapper banData;

	private Timestamp muteTime;
	private Timestamp banTime;

	private String lastConversation;
	private String customColor;
	private String worldTime = "DAY";
	private String customPrefix = "";
	private String reportingPlayer;
	private String authSecret;
	private String lastAuthAddress;

	// only use this for logging in, don't use this in any plugins
	private String disguiseName;
	private String disguiseSkin;

	private ChatType chatType;

	private long reportCooldown;
	private long commandCooldown;
	private long chatCooldown;
	private long silentSpam;

	private boolean canSeeMessages = true;
	private boolean canSeeStaffMessages = true;
	private boolean chatEnabled = true;
	private boolean authExempt;
	private boolean setupAuth = true;
	private boolean errorLoadingData;
	private boolean blacklisted;
	private boolean dataLoaded;
	private boolean vanishMode;
	private boolean ipBanned;
	private boolean banned;
	private boolean muted;

	private long lastRegister;
	private long lastDisguise;

	private int id;
	private int filter;
	private int spam;
	private int dataLoadStage;

	public boolean isIgnoring(int id) {
		return this.ignoring.contains(id);
	}

	public boolean hasPrefix(String id) {
		return this.prefixes.contains(id);
	}

	public boolean toggleIgnore(int id) {
		if (!this.ignoring.remove(id)) {
			this.ignoring.add(id);
			return true;
		}
		return false;
	}

	public boolean togglePrefix(String id) {
		if (!this.prefixes.remove(id)) {
			this.prefixes.add(id);
			return true;
		}
		return false;
	}

	public BanWrapper fetchData() {
		JsonElement data = CorePlugin.getInstance().getRequestProcessor().sendRequest(new GlobalRequest(this.ipAddress, this.uuid, this.name));

		this.banData = this.parsePlayerData(data);

		if (!this.isBanned() && !this.isIpBanned()) {
			JsonArray array = CorePlugin.getInstance().getRequestProcessor().sendRequest(new IgnoreRequest(this.uuid)).getAsJsonArray();

			for (JsonElement ignoreData : array) {
				JsonObject object = ignoreData.getAsJsonObject();

				this.ignoring.add(object.get("ignoredId").getAsInt());
			}

			array = CorePlugin.getInstance().getRequestProcessor().sendRequest(new PrefixRequest.PrefixListRequest(this.uuid)).getAsJsonArray();

			if(array != null) {
				for(JsonElement prefixDta : array) {
					JsonObject object = prefixDta.getAsJsonObject();

					this.prefixes.add(PrefixCommand.Prefix.getByName(object.get("prefix").getAsString()).getStyle());
				}
			}

		}

		this.dataLoaded = true;

		CorePlugin.getInstance().getServer().getPluginManager()
		          .callEvent(new MinemanRetrieveEvent(this, this.banData));

		return this.banData;
	}

	private BanWrapper parsePlayerData(JsonElement element) {
		JsonObject object = element.getAsJsonObject();

		JsonElement idElement = object.get("playerId");
		this.id = idElement.getAsInt();

		String rank = object.get("rank").getAsString();

		if (rank != null) {
			try {
				this.rank = Rank.getByName(rank);
			} catch (Exception e) {
				this.rank = Rank.NORMAL;
			}
		} else {
			this.rank = Rank.NORMAL;
		}

		Timestamp currentTime = new Timestamp(System.currentTimeMillis());

		JsonElement element1 = object.get("chatColor");
		if (!element1.isJsonNull()) {
			if (!element1.getAsString().isEmpty()) {
				this.customColor = ChatColor.COLOR_CHAR + element1.getAsString();
			}
		}

		/*
		element1 = object.get("prefix");
		if (!element1.isJsonNull()) {
			if (!element1.getAsString().isEmpty()) {
				this.customPrefix = element1.getAsString();
			}
		}
		*/

		element1 = object.get("authSecret");
		if (element1 != null && !element1.isJsonNull()) {
			this.authSecret = element1.getAsString();
		}

		element1 = object.get("lastAuthAddress");
		if (element1 != null && !element1.isJsonNull()) {
			this.lastAuthAddress = element1.getAsString();
		}

		element1 = object.get("authExempt");
		if (element1 != null && !element1.isJsonNull()) {
			this.authExempt = element1.getAsBoolean();
		}

		element1 = object.get("customPrefix");
		if (element1 != null && !element1.isJsonNull()) {
			String prefix = element1.getAsString();
			this.customPrefix = StringUtils.isEmpty(prefix) ? prefix : PrefixCommand.Prefix.getByName(prefix).getStyle();
		}

		element1 = object.get("disguiseName");
		if (element1 != null && !element1.isJsonNull()) {
			disguiseName = element1.getAsString();
		}

		element1 = object.get("lastDisguise");
		if (element1 != null && !element1.isJsonNull()) {
			this.lastDisguise = element1.getAsLong();
		}

		if (CorePlugin.getInstance().getDisguiseManager().getAllowedDisguising().test(getPlayer())) {
			element1 = object.get("disguiseSkin");
			if (element1 != null && !element1.isJsonNull()) {
				disguiseSkin = element1.getAsString();
			}

			element1 = object.get("disguiseRank");
			if (element1 != null && !element1.isJsonNull() && disguiseName != null) {
				disguiseRank = Rank.getByName(element1.getAsString());
			}
		}

		element1 = object.get("canSeeMessages");
		if (element1 != null && !element1.isJsonNull()) {
			this.canSeeMessages = element1.getAsBoolean();
		}

		element1 = object.get("canSeeStaffMessages");
		if (element1 != null && !element1.isJsonNull()) {
			this.canSeeStaffMessages = element1.getAsBoolean();
		}

		element1 = object.get("chatEnabled");
		if (element1 != null && !element1.isJsonNull()) {
			this.chatEnabled = element1.getAsBoolean();
		}

		element1 = object.get("worldTime");
		if (element1 != null && !element1.isJsonNull()) {
			this.worldTime = element1.getAsString();
		}

		element1 = object.get("muted");
		if (!element1.isJsonNull()) {
			this.muted = element1.getAsBoolean();
		}

		element1 = object.get("muteTime");
		if (!element1.isJsonNull()) {
			long muteTime = element1.getAsLong();

			this.muteTime = new Timestamp(muteTime);

			if (this.muteTime.before(currentTime)) {
				this.muteTime = null;
				this.muted = false;
			} else {
				this.muted = true;
			}
		}

		element1 = object.get("blacklisted");
		if (!element1.isJsonNull()) {
			this.blacklisted = element1.getAsBoolean();
		}

		element1 = object.get("ipBanned");
		if (!element1.isJsonNull()) {
			this.ipBanned = element1.getAsBoolean();
		}

		element1 = object.get("banned");
		if (!element1.isJsonNull()) {
			this.banned = element1.getAsBoolean();
		}

		element1 = object.get("banTime");
		if (!element1.isJsonNull()) {
			long banTime = element1.getAsLong();

			this.banTime = new Timestamp(banTime);

			if (this.banTime.before(currentTime)) {
				this.ipBanned = false;
				this.banTime = null;
				this.banned = false;
			} else {
				this.banned = true;
			}
		}

		// We're first checking player
		// Its better to check this first because Alt thing need some time to load.
		// Soo if player is banned or whatever we'll just return and thats all :P
		if (this.blacklisted) {
			return new BanWrapper(StringUtil.BLACKLIST.replace("\n", ""), true);
		} else if (this.banned && this.banTime == null) {
			return new BanWrapper(StringUtil.PERMANENT_BAN.replace("\n", ""), true);
		} else if (this.banned && this.banTime != null && this.banTime.after(new Timestamp(System.currentTimeMillis()))) {
			return new BanWrapper(String.format(StringUtil.TEMPORARY_BAN.replace("\n", ""), TimeUtil.millisToRoundedTime(Math.abs(System.currentTimeMillis() - this.banTime.getTime()))), this.banTime.after(new Timestamp(System.currentTimeMillis())));
		} else if (this.ipBanned) {
			return new BanWrapper(StringUtil.IP_BAN.replace("\n", ""), true);
		}

		// IP BAN and BLACKLIST check
		for(JsonElement obj : CorePlugin.getInstance().getRequestProcessor().sendRequest(new JoinCheckRequest(uuid)).getAsJsonArray()) {
			JsonObject e = obj.getAsJsonObject();

			if(e.get("blacklisted").getAsBoolean() || ((e.get("banned").getAsBoolean() || e.get("ip_banned").getAsBoolean()) && !CorePlugin.getInstance().getServerManager().getServerName().toLowerCase().contains("hub"))) {
				if(getPlayer() == null) {
					return new BanWrapper(e.get("blacklisted").getAsBoolean() ? StringUtil.BLACKLIST.replace("\n", "") : StringUtil.IP_BAN_OTHER.replace("\n", "").replace("%s", e.get("name").getAsString()), true);
				}

				getPlayer().kickPlayer(e.get("blacklisted").getAsBoolean() ? StringUtil.BLACKLIST.replace("\n", "") : StringUtil.IP_BAN_OTHER.replace("\n", "").replace("%s", e.get("name").getAsString()));
			}
		}


		CorePlugin.getInstance().getServer().getPluginManager().callEvent(new RankChangeEvent(uuid, null, this.rank));

		return new BanWrapper("", false);
	}

	public void onJoin() {
		Player player = getPlayer();

		if (this.hasRank(Rank.TRAINEE)) {
			CorePlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
				String message = CC.BLUE + "[Staff] " + rank.getColor() + getPlayer().getName() + CC.AQUA + " joined " +
						(CorePlugin.getInstance().getConfig().getBoolean("serverdata.nice-name-global-chat")
								? StringUtil.toNiceString(CorePlugin.getInstance().getServerManager().getServerName())
								: CorePlugin.getInstance().getServerManager().getServerName()) + ".";

				JsonObject data = new JsonObject();
				data.addProperty("rank", getRank().name());

				CorePlugin.getInstance().getCoreRedisManager().sendMessage(message, Rank.TRAINEE, data);
			});
		}

		setupName();

		PermissionAttachment attachment = player.addAttachment(CorePlugin.getInstance());
		switch (rank) {
			case BASIC:
				attachment.setPermission("perm.basic", true);
				break;
            case PRIME:
                attachment.setPermission("perm.prime", true);
                break;
            case ELITE:
                attachment.setPermission("perm.elite", true);
                break;
            case MASTER:
                attachment.setPermission("perm.master", true);
                break;
            case YOUTUBER:
                attachment.setPermission("perm.youtuber", true);
                break;
            case FAMOUS:
                attachment.setPermission("perm.famous", true);
                break;
            case PARTNER:
                attachment.setPermission("perm.partner", true);
                break;
            default:
                break;
        }

		if (this.hasRank(Rank.NORMAL)) {
			attachment.setPermission("minecraft.command.me", false);
			attachment.setPermission("minecraft.command.tell", false);
			attachment.setPermission("minecraft.command.tell", true);
			attachment.setPermission("hcf.command.lives", true);
			attachment.setPermission("hcf.command.lives.argument.check", true);
			attachment.setPermission("hcf.command.lives.argument.give", true);
			attachment.setPermission("hcf.command.lives.argument.revive", true);
			attachment.setPermission("hcf.command.koth", true);
			attachment.setPermission("hcf.command.koth.argument.help", true);
			attachment.setPermission("hcf.command.koth.argument.next", true);
			attachment.setPermission("hcf.command.koth.argument.schedule", true);
			attachment.setPermission("hcf.command.economy", true);
			attachment.setPermission("hcf.command.pay", true);
			attachment.setPermission("hcf.command.logout", true);
			attachment.setPermission("hcf.command.pvptimer", true);
			attachment.setPermission("hcf.command.playertime", true);
			attachment.setPermission("sidebar.lines", true);
			attachment.setPermission("hcf.command.mapkit", true);
			attachment.setPermission("hcf.command.list", true);
			attachment.setPermission("hcf.command.ores", true);
			attachment.setPermission("hcf.command.blockfilter", true);

		}

		if (this.hasRank(Rank.MASTER)) {
			attachment.setPermission("hcf.command.nightvision", true);
		}

		if (this.hasRank(Rank.TRAINEE)) {
			attachment.setPermission("hcf.command.revive", true);
			attachment.setPermission("hcf.deathban.bypass", true);
			attachment.setPermission("hcf.command.playertime.viewstaff", true);
			attachment.setPermission("hcf.bypassanticommandtab", true);
			attachment.setPermission("hcf.bypass.syntaxblocked", true);
			attachment.setPermission("server.staff", true);
			attachment.setPermission("hcf.bypass.syntaxblocked", true);
			attachment.setPermission("hcf.bypass.syntaxblocked", true);
			attachment.setPermission("hcf.bypass.syntaxblocked", true);
		}

		if (this.hasRank(Rank.MOD)) {
			attachment.setPermission("hcf.command.faction.argument.setdtr", true);
			attachment.setPermission("hcf.command.faction.argument.setdtrregen", true);
			attachment.setPermission("hcf.command.lives.argument.checkdeathban", true);
			attachment.setPermission("hcf.command.inv", true);
			attachment.setPermission("oxygen.general", true);
			attachment.setPermission("oxygen.alerts", true);
		}

		if (this.hasRank(Rank.ADMIN)) {
			attachment.setPermission("worldedit.*", true);
			attachment.setPermission("hcf.command.event", true);
			attachment.setPermission("hcf.command.conquest", true);
			attachment.setPermission("hcf.command.fury", true);
			attachment.setPermission("hcf.command.event", true);
			attachment.setPermission("hcf.command.event", true);
			attachment.setPermission("hcf.command.event", true);
			attachment.setPermission("hcf.command.koth.argument.setcapdelay", true);
			attachment.setPermission("hcf.economy.take", true);
			attachment.setPermission("hcf.economy.set", true);
			attachment.setPermission("hcf.command.timer", true);
			attachment.setPermission("hcf.faction.protection.bypass", true);
			attachment.setPermission("hcf.command.toggleend", true);
			attachment.setPermission("hcf.command.loadout", true);
			attachment.setPermission("hcf.command.mountain", true);
		}

		if (this.hasRank(Rank.SENIORADMIN)) {
			attachment.setPermission("hcf.command.faction.argument.forcedemote", true);
			attachment.setPermission("hcf.command.faction.argument.forcejoin", true);
			attachment.setPermission("hcf.command.faction.argument.forcekick", true);
			attachment.setPermission("hcf.command.faction.argument.forceleader", true);
			attachment.setPermission("hcf.command.faction.argument.forcepromote", true);
			attachment.setPermission("hcf.command.faction.argument.forcename", true);
			attachment.setPermission("hcf.command.faction.argument.forceunclaimhere", true);
			attachment.setPermission("hcf.command.lives.argument.set", true);
			attachment.setPermission("hcf.command.lives.argument.setdeathbantime", true);
			attachment.setPermission("hcf.command.reclaim", true);
			attachment.setPermission("oxygen.lookup", true);
		}

		if (this.hasRank(Rank.MANAGER)) {
			attachment.setPermission("hcf.command.faction.argument.ban", true);
			attachment.setPermission("hcf.command.faction.argument.claimfor", true);
			attachment.setPermission("hcf.command.faction.argument.clearclaims", true);
			attachment.setPermission("hcf.command.faction.argument.mute", true);
			attachment.setPermission("hcf.command.faction.argument.remove", true);
			attachment.setPermission("hcf.command.faction.argument.setdeathbanmultiplier", true);
			attachment.setPermission("hcf.command.lives.argument.cleardeathbans", true);
			attachment.setPermission("hcf.command.sotw", true);
			attachment.setPermission("hcf.command.eotw", true);
			attachment.setPermission("hcf.command.savedata", true);
			attachment.setPermission("freezeserver.freeze", true);
			attachment.setPermission("freezeserver.bypass", true);
			attachment.setPermission("hcf.command.freezeserver", true);

			Stream.of(PrefixCommand.Prefix.PREFIXES).filter(prefix -> !prefixes.contains(prefix.getStyle())).forEach(prefix ->
					prefixes.add(prefix.getStyle()));
		}

		if (this.hasRank(Rank.DEVELOPER)) {
			attachment.setPermission("oxygen.alerts.dev", true);
		}

		if (this.hasRank(Rank.OWNER)) {
			attachment.setPermission("hcf.command.faction.argument.reload", true);
		}

		player.recalculatePermissions();

		CorePlugin.getInstance().getRequestProcessor()
		          .sendRequestAsync(new JoinRequest(this.ipAddress, this.uuid, this.name));


		if (this.disguiseName != null) {
			if (CorePlugin.getInstance().getServerManager().getServerName().startsWith("hub")) {
				if (System.currentTimeMillis() - lastDisguise >= TimeUnit.MINUTES.toMillis(30)) {
					CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(
							new DisguiseRequest(uuid, false, null, null, null));

					Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () -> player.sendMessage(ChatColor.RED + "You have been automatically undisguised globally."), 20L);
				} else {
					Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () -> player.sendMessage(ChatColor.GREEN + "You will be disguised as " + this.disguiseName + CC.GREEN + " when you join a game server."), 20L);
				}
			} else {
				if (this.disguiseRank != null && CorePlugin.getInstance().getDisguiseManager().getAllowedDisguising().test(player)) {
					Bukkit.getScheduler().runTaskLaterAsynchronously(CorePlugin.getInstance(), () -> {
						try {
							if (CorePlugin.getInstance().getDisguiseManager().disguise(player, disguiseRank, disguiseSkin, disguiseName)) {
								player.sendMessage(ChatColor.GREEN + "Automatically disguised as " + disguiseName + ".");
							} else {
								player.sendMessage(ChatColor.RED + "An error occurred automatically disguising you.");
							}
						} catch (Exception e) {
							e.printStackTrace();
							player.sendMessage(ChatColor.RED + "An error occurred automatically disguising you.");
						}
					}, 1L);
				}
			}
		}
	}

	public Player getPlayer() {
		return CorePlugin.getInstance().getServer().getPlayer(this.uuid);
	}

	public boolean hasRank(Rank rank) {
		return this.rank.hasRank(rank);
	}

	private BanWrapper checkIPBan() {
		Timestamp now = new Timestamp(System.currentTimeMillis());

		if(this.banned || this.ipBanned || (this.banTime != null && this.banTime.after(now))) {
			return null;
		}

		for(JsonElement obj : CorePlugin.getInstance().getRequestProcessor().sendRequest(new PlayerRequest.AltsRequest(this.name)).getAsJsonArray()) {
			JsonObject element = obj.getAsJsonObject();

			if(element.get("banned").getAsBoolean() || element.get("ip_banned").getAsBoolean() || element.get("blacklisted").getAsBoolean()) {
				this.ipBanned = true;

				Player player = this.getPlayer();
				if (player == null) {
					return new BanWrapper(StringUtil.IP_BAN_OTHER, true);
				}

				player.kickPlayer(StringUtil.IP_BAN_OTHER);
			}
		}

		/*JsonElement data = CorePlugin.getInstance().getRequestProcessor()
		                             .sendRequest(new IPCheckRequest(this.ipAddress, this.uuid));

		if (data.getAsBoolean()) {
			this.ipBanned = true;

			Player player = this.getPlayer();
			if (player == null) {
				return new BanWrapper(StringUtil.IP_BAN_OTHER, true);
			}

			player.kickPlayer(StringUtil.IP_BAN_OTHER);
		}*/

		return null;
	}

	public ChatType getChatType() {
		return (this.chatType == null ? ChatType.NORMAL : this.chatType);
	}

	public String getDisplayName() {
		return this.getPlayer().getDisplayName();
	}

	public Rank getDisplayRank() {
		if (this.disguiseRank != null) {
			return this.disguiseRank;
		}

		return this.rank;
	}

	public String getDisguiseNiceName() {
		if (this.disguiseName != null && disguiseRank != null) {
			return disguiseRank.getColor() + disguiseName;
		}

		return rank.getColor() + getPlayer().getName();
	}

	public String getDisguiseName() {
		if (this.disguiseName != null) {
			return this.disguiseName;
		}

		return this.getPlayer().getName();
	}

	public void setupName() {
		Player player = getPlayer();
		String color = getDisplayRank().getColor();
		if(!player.getDisplayName().equals(color + player.getName())) {
			player.setDisplayName(color + player.getName());
		}
	}

	public void updateTabList(Rank rank) {
		this.getPlayer().setPlayerListName(rank.getColor() + this.getDisplayName() + CC.R);
	}

	public void giveAuthMap() {
		Player player = getPlayer();
		ImageMapRenderer mapRenderer;
		try {
			mapRenderer = new ImageMapRenderer(getName(), authSecret, "minemen.club");
		} catch (WriterException e) {
			player.sendMessage(CC.RED + "An error occurred setting up your 2FA. Try again later.");
			e.printStackTrace();
			return;
		}

		ItemStack stack = new ItemStack(Material.MAP);
		MapView view = Bukkit.createMap(player.getWorld());
		stack.setDurability(view.getId());
		stack.setAmount(0);
		player.getInventory().setHeldItemSlot(4);
		player.setItemInHand(stack);

		Location down = player.getLocation();
		down.setPitch(90);
		player.teleport(down);

		view.getRenderers().forEach(view::removeRenderer);
		view.addRenderer(mapRenderer);
		player.sendMap(view);
		player.updateInventory();
	}


	public enum ChatType {
		NORMAL,
		STAFF,
		HOST,
		DEV
	}
}