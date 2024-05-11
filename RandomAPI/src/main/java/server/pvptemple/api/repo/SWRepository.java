package server.pvptemple.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.pvptemple.api.model.SGData;
import server.pvptemple.api.model.SWData;
import server.pvptemple.api.model.SWKit;

@Repository
public interface SWRepository extends CrudRepository<SWData, Integer> {
    SWData findFirstByPlayerId(int id);
}
