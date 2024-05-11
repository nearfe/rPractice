package server.pvptemple.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.pvptemple.api.model.SGData;

@Repository
public interface SGRepository extends CrudRepository<SGData, Integer> {
    SGData findFirstByPlayerId(int id);
}
