package server.pvptemple.api.repo;

import server.pvptemple.api.model.Command;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandRepository extends CrudRepository<Command, Integer> {

	List<Command> findByPlayerId(int playerId);

}
