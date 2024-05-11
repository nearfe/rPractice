package server.pvptemple.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.pvptemple.api.model.Cage;
import server.pvptemple.api.model.SWKit;

import java.util.List;

/**
 * Created by Marko on 20.12.2018.
 */

@Repository
public interface SWKitsRepository extends CrudRepository<SWKit, Integer> {

    SWKit findByPlayerIdAndKitName(int playerId, String kitName);

    List<SWKit> findByPlayerId(int playerId);

    List<SWKit> findByKitName(String name);

    Long countByPlayerId(int playerId);

}

