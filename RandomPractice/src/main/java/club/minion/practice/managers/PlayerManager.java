package club.minion.practice.managers;

import club.minion.practice.Practice;
import club.minion.practice.kit.Kit;
import club.minion.practice.kit.PlayerKit;
import club.minion.practice.mongo.Mongo;
import club.minion.practice.player.PlayerState;
import club.minion.practice.player.PracticePlayerData;
import club.minion.practice.util.InventoryUtil;
import club.minion.practice.util.PlayerUtil;
import com.conaxgames.CorePlugin;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.timer.impl.EnderpearlTimer;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.ItemUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static club.minion.practice.profile.Profile.DEFAULT_ELO;

public class PlayerManager {

	private final Practice plugin = Practice.getInstance();
	private final Map<UUID, PracticePlayerData> playerData = new ConcurrentHashMap<UUID, PracticePlayerData>();

	public void createPlayerData(Player player) {
		PracticePlayerData data = new PracticePlayerData(player.getUniqueId());

		this.playerData.put(data.getUniqueId(), data);
		this.loadData(data);
		plugin.getScoreboardColorConfig().getConfiguration().set(String.valueOf(player.getUniqueId()), CC.PRIMARY);
		plugin.getScoreboardColorManager().setScoreboardColor(data.getUniqueId(), CC.GOLD);
	}


	@SuppressWarnings("unchecked")
	private void loadData(PracticePlayerData practicePlayerData) {

		practicePlayerData.setPlayerState(PlayerState.SPAWN);

		// Loading player data and stats

		Document document = Mongo.getInstance().getPlayers().find(Filters.eq("uuid", practicePlayerData.getUniqueId().toString())).first();

		if (document == null) {
			for (Kit kit : this.plugin.getKitManager().getKits()) {
				practicePlayerData.setElo(kit.getName(), DEFAULT_ELO);
				practicePlayerData.setWins(kit.getName(), 0);
				practicePlayerData.setLosses(kit.getName(), 0);
			}

			this.saveData(practicePlayerData);
			return;
		}

		Document statisticsDocument = (Document) document.get("statistics");
		Document globalDocument = (Document) document.get("global");
		Document kitsDocument = (Document) document.get("kitsDocument");
		Document settingsDocument = (Document) document.get("settings");

		if (globalDocument == null) {
			practicePlayerData.setPremiumElo(1000);
			practicePlayerData.setPremiumMatches(0);
			practicePlayerData.setMatchesPlayed(0);
			return;
		}


		for (String key : kitsDocument.keySet()) {
			Kit ladder = Practice.getInstance().getKitManager().getKit(key);

			if (ladder == null) {
				continue;
			}

			JsonArray kitsArray = Practice.PARSER.parse(kitsDocument.getString(key)).getAsJsonArray();
			PlayerKit[] kits = new PlayerKit[4];

			for (JsonElement kitElement : kitsArray) {
				JsonObject kitObject = kitElement.getAsJsonObject();

				PlayerKit kit = new PlayerKit(kitObject.get("name").getAsString(), kitObject.get("index").getAsInt(), InventoryUtil.deserializeInventory(kitObject.get("contents").getAsString()), kitObject.get("name").getAsString());

				kit.setContents(InventoryUtil.deserializeInventory(kitObject.get("contents").getAsString()));

				kits[kitObject.get("index").getAsInt()] = kit;
			}
			practicePlayerData.getKits().put(ladder.getName(), kits);
		}

		if (globalDocument.containsKey("premiumElo")) {
			practicePlayerData.setPremiumElo(globalDocument.getInteger("premiumElo"));
		}
		if (globalDocument.containsKey("premiumMatches")) {
			practicePlayerData.setPremiumMatches(globalDocument.getInteger("premiumMatches"));
		}
		if (globalDocument.containsKey("matchesPlayed")) {
			practicePlayerData.setMatchesPlayed(globalDocument.getInteger("matchesPlayed"));
		}

		statisticsDocument.keySet().forEach(key -> {
			Document ladderDocument = (Document) statisticsDocument.get(key);
			if (ladderDocument.containsKey("ranked-elo")) {
				practicePlayerData.getRankedElo().put(key, ladderDocument.getInteger("ranked-elo"));
			}
			if (ladderDocument.containsKey("ranked-wins")) {
				practicePlayerData.getRankedWins().put(key, ladderDocument.getInteger("ranked-wins"));
			}
			if (ladderDocument.containsKey("ranked-losses")) {
				practicePlayerData.getRankedLosses().put(key, ladderDocument.getInteger("ranked-losses"));
			}
		});
	}


	@SuppressWarnings("unchecked")
	public void saveData(PracticePlayerData practicePlayerData) {

		// Saving player data and stats
		Document document = new Document();
		Document statisticsDocument = new Document();
		Document globalDocument = new Document();
		Document kitsDocument = new Document();
		Document settingsDocument = new Document();

		practicePlayerData.getRankedWins().forEach((key, value) -> {
			Document ladderDocument;
			if (statisticsDocument.containsKey(key)) {
				ladderDocument = (Document) statisticsDocument.get(key);
			} else {
				ladderDocument = new Document();
			}

			ladderDocument.put("ranked-wins", value);
			statisticsDocument.put(key, ladderDocument);
		});

		practicePlayerData.getRankedLosses().forEach((key, value) -> {
			Document ladderDocument;
			if (statisticsDocument.containsKey(key)) {
				ladderDocument = (Document) statisticsDocument.get(key);
			} else {
				ladderDocument = new Document();
			}

			ladderDocument.put("ranked-losses", value);
			statisticsDocument.put(key, ladderDocument);
		});

		practicePlayerData.getRankedElo().forEach((key, value) -> {
			Document ladderDocument;
			if (statisticsDocument.containsKey(key)) {
				ladderDocument = (Document) statisticsDocument.get(key);
			} else {
				ladderDocument = new Document();
			}

			ladderDocument.put("ranked-elo", value);
			statisticsDocument.put(key, ladderDocument);
		});

		for (Map.Entry<String, PlayerKit[]> entry : practicePlayerData.getKits().entrySet()) {
			JsonArray kitsArray = new JsonArray();

			for (int i = 0; i < 4; i++) {
				PlayerKit kit = entry.getValue()[i];

				if (kit != null) {
					JsonObject kitObject = new JsonObject();

					kitObject.addProperty("index", i);
					kitObject.addProperty("name", kit.getName());
					kitObject.addProperty("contents", InventoryUtil.serializeInventory(kit.getContents()));

					kitsArray.add(kitObject);
				}
			}

			kitsDocument.put(entry.getKey(), kitsArray.toString());
		}

		globalDocument.put("premiumElo", practicePlayerData.getPremiumElo());
		globalDocument.put("premiumMatches", practicePlayerData.getPremiumMatches());
		globalDocument.put("matchesPlayed", practicePlayerData.getMatchesPlayed());

		settingsDocument.put("duelRequests", practicePlayerData.getSettings().isDuelRequests());
		settingsDocument.put("partyInvites", practicePlayerData.getSettings().isPartyInvites());
		settingsDocument.put("deathLightning", practicePlayerData.getSettings().isDeathLightning());
		settingsDocument.put("scoreboardToggled", practicePlayerData.getSettings().isScoreboardToggled());
		settingsDocument.put("pingScoreboardToggled", practicePlayerData.getSettings().isPingScoreboardToggled());
		settingsDocument.put("spectatorsAllowed", practicePlayerData.getSettings().isSpectatorsAllowed());
		settingsDocument.put("playerVisibility", practicePlayerData.getSettings().isPlayerVisibility());

		document.put("uuid", practicePlayerData.getUniqueId().toString());
		document.put("statistics", statisticsDocument);
		document.put("global", globalDocument);
		document.put("kitsDocument", kitsDocument);
		document.put("settings", settingsDocument);

		Mongo.getInstance().getPlayers().replaceOne(Filters.eq("uuid", practicePlayerData.getUniqueId().toString()), document, new ReplaceOptions().upsert(true));
	}

	public Collection<PracticePlayerData> getAllData() {
		return this.playerData.values();
	}

	public PracticePlayerData getPlayerData(UUID uuid) {
		return this.playerData.get(uuid);
	}

	public void giveLobbyItems(Player player) {
		boolean inParty = this.plugin.getPartyManager().getParty(player.getUniqueId()) != null;
		boolean inTournament = this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null;
		boolean isRematching = this.plugin.getMatchManager().isRematching(player.getUniqueId());
		ItemStack[] items = this.plugin.getItemManager().getSpawnItems();

		if (inTournament) {
			items = this.plugin.getItemManager().getTournamentItems();
		} else if (inParty) {
			items = this.plugin.getItemManager().getPartyItems();
		}

		player.getInventory().setContents(items);

		if (isRematching && !inParty && !inTournament) {
			player.getInventory()
					.setItem(3, ItemUtil.createItem(Material.BLAZE_POWDER, CC.PRIMARY + "Request Rematch"));
			player.getInventory()
					.setItem(5, ItemUtil.createItem(Material.PAPER, CC.PRIMARY + "View Opponent's Inventory"));
		}

		player.updateInventory();
	}

	public void sendToSpawnAndReset(Player player) {
		PracticePlayerData playerData = this.getPlayerData(player.getUniqueId());

		playerData.setPlayerState(PlayerState.SPAWN);
		PlayerUtil.clearPlayer(player);
		CorePlugin.getInstance().getTimerManager().getTimer(EnderpearlTimer.class).clearCooldown(player.getUniqueId());

		this.giveLobbyItems(player);

		if (!player.isOnline()) {
			return;
		}

		this.plugin.getServer().getOnlinePlayers().forEach(p -> {
			player.hidePlayer(p);
			p.hidePlayer(player);
		});

		player.teleport(this.plugin.getSpawnManager().getSpawnLocation().toBukkitLocation());
	}

	public int getPremiumMatches(UUID uuid) {
		Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(uuid);
		switch (mineman.getRank()) {
			// Staff don't get any
			case MOD:
			case NORMAL:
			case ADMIN:
			case TRAINEE:
			case SENIORMOD:
			case DEVELOPER:
				return 0;

			// Donors get a set amount

			case ELITE:
				return 20;

			case BASIC:
				return 30;

			case PRIME:
				return 40;

			case YOUTUBER:
				return 15;

			// YouTubers and shit get unlimited
			case SENIORADMIN:
			case OWNER:
				return 1337;

			// Make IntelliJ happy
			default:
				return 0;
		}
	}

	public MongoCursor<Document> getPlayersSortByLadderElo(Kit ladder) {
		final Document sort = new Document();

		sort.put("statistics." + ladder.getName() + ".ranked-elo", -1);

		return Mongo.getInstance().getPlayers().find().sort(sort).limit(10).iterator();
	}
}
