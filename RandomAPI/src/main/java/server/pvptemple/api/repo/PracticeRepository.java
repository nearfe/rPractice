package server.pvptemple.api.repo;

import server.pvptemple.api.model.PracticeData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PracticeRepository extends CrudRepository<PracticeData, Integer> {

	PracticeData findFirstByPlayerId(int id);

}
