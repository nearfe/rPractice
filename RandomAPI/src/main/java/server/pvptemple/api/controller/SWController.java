package server.pvptemple.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pvptemple.api.model.*;
import server.pvptemple.api.repo.*;
import server.pvptemple.api.util.Constants;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/{key}/skywars")
public class SWController {

	static {
		// Run this first so whenever it caches reflection data
		SWData SWData = new SWData();

		Field[] fields = SWData.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);

			try {
				if (field.getType() == boolean.class) {
					field.set(SWData, Boolean.valueOf("false"));
				} else if (field.getType() == int.class) {
					field.set(SWData, Integer.valueOf("123"));
				} else if (field.getType() == String.class) {
					field.set(SWData, "xd");
				} else if (field.getType() == double.class) {
					field.set(SWData, Double.valueOf("123"));
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Autowired private SWRepository swRepository;
	@Autowired private DeathMessagesRepository dmRepository;
	@Autowired private CagesRepository cagesRepository;
	@Autowired private SWKitsRepository kitsRepository;
	@Autowired private PlayerRepository playerRepository;
	@Autowired private TrailsRepository trailsRepository;
	@Autowired private BoostersRepository boostersRepository;

	@RequestMapping("/{uuid}/update-cages/{name}")
	public ResponseEntity<String> onUpdateKit(HttpServletRequest request,
											  @PathVariable(name = "key") String key,
											  @PathVariable(name = "uuid") UUID uuid,
											  @PathVariable(name = "name") String name) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = this.playerRepository.findFirstByUniqueId(uuid.toString());
		if (player == null) {
			return Constants.PLAYER_NOT_FOUND_STRING;
		}

		boolean value = Boolean.valueOf(request.getParameter("value"));

		Cage cage = new Cage();
		cage.setPlayerId(player.getPlayerId());
		cage.setCageName(name);
		cage.setValue(value);
		cagesRepository.save(cage);
		return Constants.SUCCESS_STRING;
	}

	@RequestMapping("/{uuid}/cages")
	public ResponseEntity<List<Cage>> getCages(@PathVariable(name = "key") String key,
											 @PathVariable(name = "uuid") UUID uuid) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = this.playerRepository.findFirstByUniqueId(uuid.toString());
		if (player == null) {
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
		}

		return new ResponseEntity<>(this.cagesRepository.findByPlayerId(player.getPlayerId()), HttpStatus.OK);
	}

	@RequestMapping("/{uuid}/update-kits/{name}")
	public ResponseEntity<String> updateKIt(HttpServletRequest request,
											  @PathVariable(name = "key") String key,
											  @PathVariable(name = "uuid") UUID uuid,
											  @PathVariable(name = "name") String name) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = this.playerRepository.findFirstByUniqueId(uuid.toString());
		if (player == null) {
			return Constants.PLAYER_NOT_FOUND_STRING;
		}

		boolean value = Boolean.valueOf(request.getParameter("value"));

		SWKit kit = new SWKit();
		kit.setPlayerId(player.getPlayerId());
		kit.setKitName(name);
		kit.setValue(value);
		kitsRepository.save(kit);
		return Constants.SUCCESS_STRING;
	}

	@RequestMapping("/{uuid}/kits")
	public ResponseEntity<List<SWKit>> getKits(@PathVariable(name = "key") String key,
											   @PathVariable(name = "uuid") UUID uuid) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = this.playerRepository.findFirstByUniqueId(uuid.toString());
		if (player == null) {
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
		}

		return new ResponseEntity<>(this.kitsRepository.findByPlayerId(player.getPlayerId()), HttpStatus.OK);
	}

	@RequestMapping("/{uuid}/update-death-messages/{name}")
	public ResponseEntity<String> updateDms(HttpServletRequest request,
											@PathVariable(name = "key") String key,
											@PathVariable(name = "uuid") UUID uuid,
											@PathVariable(name = "name") String name) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = this.playerRepository.findFirstByUniqueId(uuid.toString());
		if (player == null) {
			return Constants.PLAYER_NOT_FOUND_STRING;
		}

		boolean value = Boolean.valueOf(request.getParameter("value"));

		DeathMessage dm = new DeathMessage();
		dm.setPlayerId(player.getPlayerId());
		dm.setDmName(name);
		dm.setValue(value);
		dmRepository.save(dm);
		return Constants.SUCCESS_STRING;
	}

	@RequestMapping("/{uuid}/death-messages")
	public ResponseEntity<List<DeathMessage>> getDms(@PathVariable(name = "key") String key,
											   @PathVariable(name = "uuid") UUID uuid) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = this.playerRepository.findFirstByUniqueId(uuid.toString());
		if (player == null) {
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
		}

		return new ResponseEntity<>(this.dmRepository.findByPlayerId(player.getPlayerId()), HttpStatus.OK);
	}

	@RequestMapping("/{uuid}/update-trails/{name}")
	public ResponseEntity<String> updateTrails(HttpServletRequest request,
											@PathVariable(name = "key") String key,
											@PathVariable(name = "uuid") UUID uuid,
											@PathVariable(name = "name") String name) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = this.playerRepository.findFirstByUniqueId(uuid.toString());
		if (player == null) {
			return Constants.PLAYER_NOT_FOUND_STRING;
		}

		boolean value = Boolean.valueOf(request.getParameter("value"));

		Trail trail = new Trail();
		trail.setPlayerId(player.getPlayerId());
		trail.setTrailName(name);
		trail.setValue(value);
		trailsRepository.save(trail);
		return Constants.SUCCESS_STRING;
	}

	@RequestMapping("/{uuid}/trails")
	public ResponseEntity<List<Trail>> getTrails(@PathVariable(name = "key") String key,
													 @PathVariable(name = "uuid") UUID uuid) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = this.playerRepository.findFirstByUniqueId(uuid.toString());
		if (player == null) {
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
		}

		return new ResponseEntity<>(this.trailsRepository.findByPlayerId(player.getPlayerId()), HttpStatus.OK);
	}

	@RequestMapping("/{uuid}/boosters")
	public ResponseEntity<List<Booster>> getBoosters(@PathVariable(name = "key") String key,
											 @PathVariable(name = "uuid") UUID uuid) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = this.playerRepository.findFirstByUniqueId(uuid.toString());
		if (player == null) {
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
		}

		return new ResponseEntity<>(this.boostersRepository.findByPlayerId(player.getPlayerId()), HttpStatus.OK);
	}

	@RequestMapping("/{uuid}/update-boosters/{name}")
	public ResponseEntity<String> onUpdateBooster(HttpServletRequest request,
											  @PathVariable(name = "key") String key,
											  @PathVariable(name = "uuid") UUID uuid,
											  @PathVariable(name = "name") String name) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = this.playerRepository.findFirstByUniqueId(uuid.toString());
		if (player == null) {
			return Constants.PLAYER_NOT_FOUND_STRING;
		}

		Booster booster = boostersRepository.findByPlayerIdAndBoosterName(player.getPlayerId(), name);
		boolean isRemoval = Boolean.valueOf(request.getParameter("remove"));

		if (isRemoval) {
			if (booster == null) {
				return Constants.KIT_NOT_FOUND_STRING;
			}

			boostersRepository.delete(booster);
		} else {
			if (booster != null) {
				return Constants.ALREADY_HAVE_KIT_STRING;
			}

			long expiry = Long.parseLong(request.getParameter("expiry"));

			Booster newBooster = new Booster();
			newBooster.setPlayerId(player.getPlayerId());
			newBooster.setBoosterName(name);
			newBooster.setExpiry(expiry);
			boostersRepository.save(newBooster);
		}

		return Constants.SUCCESS_STRING;
	}

	@RequestMapping("/{uuid}/update-booster-cooldown/{name}")
	public ResponseEntity<String> onUpdateKitCooldown(HttpServletRequest request,
													  @PathVariable(name = "key") String key,
													  @PathVariable(name = "uuid") UUID uuid,
													  @PathVariable(name = "name") String name) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = this.playerRepository.findFirstByUniqueId(uuid.toString());
		if (player == null) {
			return Constants.PLAYER_NOT_FOUND_STRING;
		}

		Booster booster = boostersRepository.findByPlayerIdAndBoosterName(player.getPlayerId(), name);

		if(booster == null) {
			return Constants.KIT_NOT_FOUND_STRING;
		}

		long cooldownExpiry = Long.parseLong(request.getParameter("cooldownExpiry"));
		booster.setCooldownExpiry(cooldownExpiry);
		boostersRepository.save(booster);

		return Constants.SUCCESS_STRING;
	}

	@RequestMapping("/{uuid}")
	public ResponseEntity<SWData> getData(@PathVariable(name = "key") String key,
	                                            @PathVariable(name = "uuid") UUID uuid) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = PlayerController.getPlayer(this.playerRepository, uuid);
		SWData swData = this.swRepository.findFirstByPlayerId(player.getPlayerId());

		if (swData == null) {
			return null;
		}

		return new ResponseEntity<>(swData, HttpStatus.OK);
	}

	@RequestMapping("/{id}/update")
	public ResponseEntity<SWData> updateData(HttpServletRequest request,
	                                               @PathVariable(name = "key") String key,
	                                               @PathVariable(name = "id") int id) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		SWData swData = this.swRepository.findFirstByPlayerId(id);

		if (swData == null) {
			swData = new SWData();
			swData.setPlayerId(id);
		}

		Field[] fields = swData.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);

			String name = this.deserializeName(field.getName());

			String param = request.getParameter(name);
			if (param == null) {
				continue;
			}

			try {
				if (field.getType() == boolean.class) {
					field.set(swData, Boolean.valueOf(param));
				} else if (field.getType() == int.class) {
					field.set(swData, Integer.valueOf(param));
				} else if (field.getType() == String.class) {
					field.set(swData, param);
				} else if (field.getType() == double.class) {
					field.set(swData, Double.valueOf(param));
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		this.swRepository.save(swData);

		return new ResponseEntity<>(swData, HttpStatus.OK);
	}

	private String deserializeName(String name) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < name.length(); i++) {
			char c = name.toCharArray()[i];

			if (Character.isUpperCase(c)) {
				sb.append("_");
			}

			sb.append(Character.toLowerCase(c));
		}

		return sb.toString();
	}

}
