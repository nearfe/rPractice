package server.pvptemple.api.repo;

import server.pvptemple.api.model.Punishment;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PunishmentRepository extends CrudRepository<Punishment, Integer> {

	Punishment findFirstByPlayerIdAndTypeContainingOrderByTimestampDesc(int id, String reason);

	List<Punishment> findByPlayerId(int id);

	Long countByPlayerId(int id);

}
