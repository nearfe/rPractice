package server.pvptemple.api.repo;

import server.pvptemple.api.model.Ignore;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IgnoresRepository extends CrudRepository<Ignore, Integer> {

	Ignore findByPlayerIdAndIgnoredId(int playerId, int ignoredId);

	List<Ignore> findByPlayerId(int playerId);

}
