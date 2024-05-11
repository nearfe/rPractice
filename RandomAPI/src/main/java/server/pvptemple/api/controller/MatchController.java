package server.pvptemple.api.controller;

import server.pvptemple.api.model.Inventory;
import server.pvptemple.api.model.Match;
import server.pvptemple.api.repo.InventoryRepository;
import server.pvptemple.api.repo.MatchesRepository;
import server.pvptemple.api.util.Constants;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/{key}/matches")
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
public class MatchController {

	@Autowired private InventoryRepository inventoryRepository;
	@Autowired private MatchesRepository matchesRepository;

	@RequestMapping("/insert/inventory")
	public ResponseEntity<String> insertInventoryData(HttpServletRequest request,
	                                                  @PathVariable("key") String key) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		String inventoryA = request.getParameter("inventorty-a");
		String inventoryB = request.getParameter("inventorty-b");
		String matchId = request.getParameter("match-id");

		Inventory inventory = new Inventory();
		inventory.setMatchId(matchId);
		inventory.setWinnerInventory(inventoryA);
		inventory.setLoserInventory(inventoryB);

		this.inventoryRepository.save(inventory);

		return Constants.SUCCESS_STRING;
	}

	@RequestMapping("/insert/match")
	public ResponseEntity<String> insertMatchData(HttpServletRequest request,
	                                              @PathVariable("key") String key) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		String id = request.getParameter("match-id");

		int winner = Integer.parseInt(request.getParameter("winners"));
		int loser = Integer.parseInt(request.getParameter("losers"));

		int winnerEloBefore = Integer.parseInt(request.getParameter("winner-elo-before"));
		int loserEloBefore = Integer.parseInt(request.getParameter("loser-elo-before"));

		int winnerEloAfter = Integer.parseInt(request.getParameter("winner-elo-after"));
		int loserEloAfter = Integer.parseInt(request.getParameter("loser-elo-after"));

		int inventory = Integer.parseInt(request.getParameter("inventory"));

		Match match = new Match(id, inventory, winner, loser,
				winnerEloBefore, loserEloBefore,
				winnerEloAfter, loserEloAfter);

		this.matchesRepository.save(match);

		return Constants.SUCCESS_STRING;
	}

}
