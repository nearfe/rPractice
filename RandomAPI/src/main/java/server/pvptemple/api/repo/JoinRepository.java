package server.pvptemple.api.repo;

import server.pvptemple.api.model.Join;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JoinRepository extends CrudRepository<Join, Integer> {

	List<Join> findByIp(String ip);

	List<Join> findByPlayerId(int playerId);

	Join findByPlayerIdAndIp(int playerId, String ip);

}
