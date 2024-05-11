package server.pvptemple.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.pvptemple.api.model.HCFData;

/**
 * Created by Marko on 30.12.2018.
 */

@Repository
public interface HCFRepository extends CrudRepository<HCFData, Integer> {

    HCFData findFirstByPlayerId(int id);

}
