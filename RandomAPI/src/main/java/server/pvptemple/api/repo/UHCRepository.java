package server.pvptemple.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.pvptemple.api.model.UHCData;

@Repository
public interface UHCRepository extends CrudRepository<UHCData, Integer> {
    UHCData findFirstByPlayerId(int id);
}
