package server.pvptemple.api.controller;

import server.pvptemple.api.model.Player;
import server.pvptemple.api.model.PracticeData;
import server.pvptemple.api.repo.PlayerRepository;
import server.pvptemple.api.repo.PracticeRepository;
import server.pvptemple.api.util.Constants;
import server.pvptemple.api.util.PlayerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/{key}/premium")
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
public class PremiumController {

	@Autowired private PracticeRepository PracticeRepository;
	@Autowired private PlayerRepository playerRepository;

	@RequestMapping("/reset")
	public ResponseEntity<String> resetPremiumMatches(@PathVariable("key") String key) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		for (PracticeData practiceData : this.PracticeRepository.findAll()) {
			practiceData.setPremiumMatchesPlayed(0);
			practiceData.setPremiumMatchesExtra(0);
			this.PracticeRepository.save(practiceData);
		}

		return Constants.SUCCESS_STRING;
	}

	@RequestMapping("/{name}/add/{amount}")
	public ResponseEntity<String> addPremiumMatches(@PathVariable("key") String key,
	                                                @PathVariable("name") String name,
	                                                @PathVariable("amount") int amount) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = PlayerUtil.getByName(name, this.playerRepository);
		if (player == null) {
			return Constants.PLAYER_NOT_FOUND_STRING;
		}

		PracticeData practiceData = this.PracticeRepository.findFirstByPlayerId(player.getPlayerId());
		practiceData.setPremiumMatchesExtra(practiceData.getPremiumMatchesExtra() + amount);

		this.PracticeRepository.save(practiceData);

		return Constants.SUCCESS_STRING;
	}

	@RequestMapping("/{name}/remove/{amount}")
	public ResponseEntity<String> removePremiumMatches(@PathVariable("key") String key,
	                                                   @PathVariable("name") String name,
	                                                   @PathVariable("amount") int amount) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = PlayerUtil.getByName(name, this.playerRepository);
		if (player == null) {
			return Constants.PLAYER_NOT_FOUND_STRING;
		}

		PracticeData practiceData = this.PracticeRepository.findFirstByPlayerId(player.getPlayerId());
		practiceData.setPremiumMatchesExtra(practiceData.getPremiumMatchesExtra() - amount);

		this.PracticeRepository.save(practiceData);

		return Constants.SUCCESS_STRING;
	}

}
