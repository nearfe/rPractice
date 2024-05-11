package server.pvptemple.api.repo;

import server.pvptemple.api.model.Player;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Integer> {

	List<Player> findByIpAddress(String ipAddress);

	List<Player> findByName(String name);

	Player findFirstByUniqueId(String uniqueId);

	List<Player> findAllByPlayerIdIn(int[] ids);

	Player findFirstByPlayerId(int id);

}
