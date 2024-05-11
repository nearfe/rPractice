package server.pvptemple.api.util;

import server.pvptemple.api.model.Player;
import server.pvptemple.api.repo.PlayerRepository;

import java.util.List;
import java.util.UUID;

/**
 * Util class containing wrapper methods to fetch Player data. <p></p>
 *
 * @author Erouax
 * @since 10/18/2017
 */
public final class PlayerUtil {

	public static Player getByName(String name, PlayerRepository playerRepository) {
		if (name.length() > 16) {
			UUID uuid = UUID.fromString(name);

			return playerRepository.findFirstByUniqueId(uuid.toString());
		}

		List<Player> players = playerRepository.findByName(name);
		if (players.size() == 1) {
			return players.get(0);
		}

		try {
			UUID uuid = HttpUtil.getUniqueIdFromName(name);
			if (uuid == null) {
				return null;
			}

			return playerRepository.findFirstByUniqueId(uuid.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
