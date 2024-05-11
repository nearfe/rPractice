package server.pvptemple.api.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import server.pvptemple.api.model.*;
import server.pvptemple.api.repo.*;
import server.pvptemple.api.util.Constants;
import server.pvptemple.api.util.PlayerUtil;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/api/{key}/player")
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
public class PlayerController {

	@Autowired
	private PunishmentRepository punishmentRepository;
	@Autowired
	private IgnoresRepository ignoresRepository;
	@Autowired
	private PrefixesRepository prefixesRepository;
	@Autowired
	private PlayerRepository playerRepository;
	@Autowired
	private JoinRepository joinRepository;

	public static Player getPlayer(PlayerRepository playerRepository, UUID uuid) {
		Player player = playerRepository.findFirstByUniqueId(uuid.toString());
		if (player == null) {
			player = new Player();

			player.setLastLogin(new Timestamp(System.currentTimeMillis()));
			player.setFirstLogin(new Timestamp(System.currentTimeMillis()));
			player.setUniqueId(uuid.toString());
			player.setRank("Normal");
			player.setName("");
			player.setIpAddress("");

			playerRepository.save(player);
		}

		return player;
	}

	public static Player getPlayer(PlayerRepository playerRepository, String name) {
		List<Player> players = playerRepository.findByName(name);

		if (players.size() > 0) {
			for (Player player : players) {
				if (player.getName().equalsIgnoreCase(name)) {
					return player;
				}
			}
		}

		return null;
	}

	@RequestMapping("/from_name/{name}")
	public Player getData(@PathVariable(name = "key") String key,
						  @PathVariable(name = "name") String name,
						  HttpServletRequest request) {

		if (!Constants.validServerKey(key)) {
			return null;
		}

		return getPlayer(this.playerRepository, name);
	}

	@RequestMapping("/{uuid}")
	public Player getData(@PathVariable(name = "key") String key,
						  @PathVariable(name = "uuid") UUID uuid,
						  HttpServletRequest request) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		return this.getGlobalData(key, uuid, request);
	}

	@RequestMapping("/{uuid}/global")
	public Player getGlobalData(@PathVariable(name = "key") String key,
								@PathVariable(name = "uuid") UUID uuid,
								HttpServletRequest request) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		String name = request.getParameter("name");
		String ip = request.getParameter("ip");

		Player player = getPlayer(this.playerRepository, uuid);
		boolean changesApplied = false;
		if (player.getIpAddress() == null || ip != null && !player.getIpAddress().equals(ip)) {
			player.setIpAddress(ip);
			changesApplied = true;
		} else if (player.getName() == null || name != null && !player.getName().equals(name)) {
			player.setName(name);
			changesApplied = true;
		}

		if (changesApplied) {
			this.playerRepository.save(player);
		}

		return player;
	}

	@RequestMapping("/{uuid}/ignore/{name}")
	public ResponseEntity<String> updateIgnores(@PathVariable(name = "key") String key,
												@PathVariable(name = "uuid") UUID uuid,
												@PathVariable(name = "name") String name) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = this.playerRepository.findFirstByUniqueId(uuid.toString());

		Player ignorePlayer = PlayerUtil.getByName(name, this.playerRepository);
		if (ignorePlayer == null) {
			return Constants.PLAYER_NOT_FOUND_STRING;
		}

		String rank = ignorePlayer.getRank();

		switch (rank.toUpperCase()) {
			case "TRAINEE":
			case "MOD":
			case "SR_MOD":
			case "ADMIN":
			case "PLAT_ADMIN":
			case "MANAGER":
			case "DEVELOPER":
			case "OWNER":
				return new ResponseEntity<>("{\"response\":\"cant-ignore\"}", HttpStatus.OK);
		}

		Ignore ignore = this.ignoresRepository.findByPlayerIdAndIgnoredId(player.getPlayerId(),
				ignorePlayer.getPlayerId());

		if (ignore != null) {
			this.ignoresRepository.delete(ignore);
		} else {
			ignore = new Ignore();

			ignore.setIgnoredId(ignorePlayer.getPlayerId());
			ignore.setPlayerId(player.getPlayerId());

			this.ignoresRepository.save(ignore);
		}

		JsonObject object = new JsonObject();
		object.addProperty("target-id", ignore.getIgnoredId());
		object.addProperty("response", "success");
		return new ResponseEntity<>(object.toString(), HttpStatus.OK);
	}

	@RequestMapping("/{uuid}/ignores")
	public ResponseEntity<List<Ignore>> getIgnoreData(@PathVariable(name = "key") String key,
													  @PathVariable(name = "uuid") UUID uuid) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = this.playerRepository.findFirstByUniqueId(uuid.toString());
		if (player == null) {
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
		}

		return new ResponseEntity<>(this.ignoresRepository.findByPlayerId(player.getPlayerId()), HttpStatus.OK);
	}

	@RequestMapping("/{uuid}/ignoring")
	public ResponseEntity<List<Player>> getIgnoring(@PathVariable(name = "key") String key,
													@PathVariable(name = "uuid") UUID uuid) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = this.playerRepository.findFirstByUniqueId(uuid.toString());
		if (player == null) {
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
		}

		List<Ignore> ignores = this.ignoresRepository.findByPlayerId(player.getPlayerId());

		int[] integers = new int[ignores.size()];
		for (int i = 0; i < ignores.size(); i++) {
			integers[i] = ignores.get(i).getIgnoredId();
		}

		List<Player> players = this.playerRepository.findAllByPlayerIdIn(integers);
		return new ResponseEntity<>(players, HttpStatus.OK);
	}

	@RequestMapping("/{name}/prefixes/{prefix}")
	public ResponseEntity<String> updatePrefixes(@PathVariable(name = "key") String key,
												 @PathVariable(name = "name") String playerName,
												 @PathVariable(name = "prefix") String name) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = PlayerUtil.getByName(playerName, this.playerRepository);
		if (player == null) {
			return Constants.PLAYER_NOT_FOUND_STRING;
		}

		Prefix prefix = this.prefixesRepository.findByPlayerIdAndPrefix(player.getPlayerId(), name);

		if(prefix != null) {
			this.prefixesRepository.delete(prefix);
		} else {
			prefix = new Prefix();

			prefix.setPrefix(name);
			prefix.setPlayerId(player.getPlayerId());

			this.prefixesRepository.save(prefix);
		}

		JsonObject object = new JsonObject();
		object.addProperty("prefix", name);
		object.addProperty("response", "success");
		return new ResponseEntity<>(object.toString(), HttpStatus.OK);
	}

	@RequestMapping("/{uuid}/prefixes")
	public ResponseEntity<List<Prefix>> getPrefixes(@PathVariable(name = "key") String key,
													  @PathVariable(name = "uuid") UUID uuid) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = this.playerRepository.findFirstByUniqueId(uuid.toString());
		if (player == null) {
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
		}

		return new ResponseEntity<>(this.prefixesRepository.findByPlayerId(player.getPlayerId()), HttpStatus.OK);
	}

	@RequestMapping("/{uuid}/get-alts")
	public ResponseEntity<String> fetchByIp(@PathVariable("key") String key,
											@PathVariable("uuid") String uuid) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = this.playerRepository.findFirstByUniqueId(uuid);
		JsonArray alts = new JsonArray();

		for(Player check : this.playerRepository.findByIpAddress(player.getIpAddress())) {
			alts.add(check.toJson());
		}

		return new ResponseEntity<>(alts.toString(), HttpStatus.OK);
	}

	@RequestMapping("/{name}/ban-info")
	public ResponseEntity<String> getBanInfo(@PathVariable(name = "key") String key,
											 @PathVariable(name = "name") String name) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		JsonObject object = new JsonObject();

		Player player = PlayerUtil.getByName(name, this.playerRepository);
		if (player == null) {
			object.addProperty("response", "player-not-found");
			return new ResponseEntity<>(object.toString(), HttpStatus.OK);
		}

		object.addProperty("response", "success");

		object.addProperty("name", player.getName());
		object.addProperty("muted", player.isMuted());
		object.addProperty("banned", player.isBanned());
		object.addProperty("ip-banned", player.isIpBanned());
		object.addProperty("blacklisted", player.isBlacklisted());
		object.addProperty("ban-time", player.getBanTime() != null ? player.getBanTime().getTime() : 0);
		object.addProperty("mute-time", player.getMuteTime() != null ? player.getMuteTime().getTime() : 0);

		if (player.isMuted()) {
			Punishment punishment = this.punishmentRepository
					.findFirstByPlayerIdAndTypeContainingOrderByTimestampDesc(
							player.getPlayerId(), "MUTE");
			//			Punishment punishment = punishments.get(punishments.size() - 1);


			object.addProperty("mute-reason", punishment.getReason());
		}

		if (player.isBanned()) {
			Punishment punishment = this.punishmentRepository
					.findFirstByPlayerIdAndTypeContainingOrderByTimestampDesc(
							player.getPlayerId(), "%BAN%");
			//			Punishment punishment = punishments.get(punishments.size() - 1);

			object.addProperty("ban-reason", punishment.getReason());
		}

		if (player.isBlacklisted()) {
			Punishment punishment = this.punishmentRepository
					.findFirstByPlayerIdAndTypeContainingOrderByTimestampDesc(
							player.getPlayerId(), "%BLACKLIST%");
			//			Punishment punishment = punishments.get(punishments.size() - 1);

			object.addProperty("ban-reason", punishment.getReason());
		}

		return new ResponseEntity<>(object.toString(), HttpStatus.OK);
	}

	@RequestMapping("/{uuid}/ip-check/{ip}")
	public ResponseEntity<Boolean> doIPCheck(@PathVariable("key") String key,
											 @PathVariable("ip") String ip) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		List<Player> players = this.playerRepository.findByIpAddress(ip);

		for (Player player : players) {
			if (player.isBanned() || player.isIpBanned() || player.isBlacklisted()) {
				return new ResponseEntity<>(true, HttpStatus.OK);
			}
		}

		return new ResponseEntity<>(false, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping("/{uuid}/joins/update/{ip}/{name}")
	public ResponseEntity<String> updateJoins(@PathVariable("key") String key,
											  @PathVariable("uuid") UUID uuid,
											  @PathVariable("name") String name,
											  @PathVariable("ip") String ip) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = this.playerRepository.findFirstByUniqueId(uuid.toString());
		if (player == null) {
			return Constants.PLAYER_NOT_FOUND_STRING;
		}

		player.setLastLogin(new Timestamp(System.currentTimeMillis()));
		player.setIpAddress(ip);
		player.setName(name);

		this.playerRepository.save(player);

		Join current = this.joinRepository.findByPlayerIdAndIp(player.getPlayerId(), ip);
		if (current == null) {
			current = new Join();

			current.setPlayerId(player.getPlayerId());
			current.setIp(ip);
		}

		current.setCount(current.getCount() + 1);

		this.joinRepository.save(current);

		return Constants.SUCCESS_STRING;
	}

	@RequestMapping("/{name}/alts")
	public /*ResponseEntity<Set<String>>*/ ResponseEntity<String> getAlts(@PathVariable("key") String key,
												   @PathVariable("name") String name) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		List<Player> players = this.playerRepository.findByName(name);

		Set<Integer> allPlayerIds = new HashSet<>();
		//Set<String> allPlayers = new HashSet<>();
		JsonArray alts = new JsonArray();

		for (Player player : players) {
			allPlayerIds.add(player.getPlayerId());

			if(!alts.contains(player.toJson())) {
				alts.add(player.toJson());
			}

			//allPlayers.add(player.getName());

			List<Join> joins = this.joinRepository.findByPlayerId(player.getPlayerId());

			for (Join join : joins) {
				List<Join> joinsOnIp = this.joinRepository.findByIp(join.getIp());

				for (Join join1 : joinsOnIp) {
					if (allPlayerIds.contains(join1.getPlayerId())) {
						continue;
					}

					Player player1 = this.playerRepository.findFirstByPlayerId(join1.getPlayerId());

					if (player1 != null) {
						if(!alts.contains(player1.toJson())) {
							alts.add(player1.toJson());
						}
						//allPlayers.add(player1.getName());
					}

					allPlayerIds.add(join1.getPlayerId());
				}
			}
		}

		//return new ResponseEntity<>(allPlayers, HttpStatus.OK);
		return new ResponseEntity<>(alts.toString(), HttpStatus.OK);
	}

	@RequestMapping("/{id}/update-color")
	public ResponseEntity<String> updateColor(HttpServletRequest request,
											  @PathVariable("key") String key,
											  @PathVariable("id") int id) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = this.playerRepository.findFirstByPlayerId(id);
		if (player == null) {
			return Constants.PLAYER_NOT_FOUND_STRING;
		}

		player.setChatColor(request.getParameter("color"));
		this.playerRepository.save(player);

		return Constants.SUCCESS_STRING;
	}

	@RequestMapping("/{name}/update-prefix")
	public ResponseEntity<String> updatePrefix(HttpServletRequest request,
											  @PathVariable("key") String key,
											  @PathVariable("name") String name) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = PlayerUtil.getByName(name, this.playerRepository);
		if (player == null) {
			return Constants.PLAYER_NOT_FOUND_STRING;
		}

		player.setCustomPrefix(request.getParameter("prefix"));
		this.playerRepository.save(player);

		return Constants.SUCCESS_STRING;
	}

	@RequestMapping("/{id}/update-time")
	public ResponseEntity<String> updateTime(HttpServletRequest request,
											  @PathVariable("key") String key,
											  @PathVariable("id") int id) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = this.playerRepository.findFirstByPlayerId(id);
		if (player == null) {
			return Constants.PLAYER_NOT_FOUND_STRING;
		}

		player.setWorldTime(request.getParameter("time"));
		this.playerRepository.save(player);

		return Constants.SUCCESS_STRING;
	}

	@RequestMapping("/{id}/update-chat")
	public ResponseEntity<String> updateChatThings(HttpServletRequest request,
												   @PathVariable("key") String key,
												   @PathVariable("id") int id) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = this.playerRepository.findFirstByPlayerId(id);
		if (player == null) {
			return Constants.PLAYER_NOT_FOUND_STRING;
		}

		String type = request.getParameter("type");
		boolean value = Boolean.valueOf(request.getParameter("value"));

		switch (type) {
			case "canSeeMessages":
				player.setCanSeeMessages(value);
				break;
			case "canSeeStaffMessages":
				player.setCanSeeStaffMessages(value);
				break;
			case "chatEnabled":
				player.setChatEnabled(value);
				break;
		}

		this.playerRepository.save(player);

		return Constants.SUCCESS_STRING;
	}

	@RequestMapping("/{name}/update-rank")
	public ResponseEntity<String> updateRank(HttpServletRequest request,
											 @PathVariable("key") String key,
											 @PathVariable("name") String name) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = PlayerUtil.getByName(name, this.playerRepository);
		if (player == null) {
			return Constants.PLAYER_NOT_FOUND_STRING;
		}

		player.setRank(request.getParameter("rank"));

		this.playerRepository.save(player);

		return Constants.SUCCESS_STRING;
	}

	@RequestMapping("{name}/authentication")
	public ResponseEntity<String> handleAuthentication(HttpServletRequest request,
													   @PathVariable("key") String key,
													   @PathVariable("name") String name) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = PlayerUtil.getByName(name, this.playerRepository);
		if (player == null) {
			return Constants.PLAYER_NOT_FOUND_STRING;
		}

		boolean success = Boolean.valueOf(request.getParameter("success"));
		boolean exempt = Boolean.valueOf(request.getParameter("exempt"));
		String secret = request.getParameter("secret");
		String address = request.getParameter("address");

		if (address.isEmpty()) {
			address = null;
		}

		if (secret.isEmpty()) {
			secret = null;
		}

		player.setAuthExempt(exempt);
		player.setAuthSecret(secret);
		if (success) {
			player.setLastAuthAddress(address);
		}

		this.playerRepository.save(player);
		return Constants.SUCCESS_STRING;
	}
}
