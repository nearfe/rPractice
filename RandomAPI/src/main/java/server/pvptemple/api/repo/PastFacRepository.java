package server.pvptemple.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.pvptemple.api.model.PastFaction;

import java.util.List;

/**
 * Created by Marko on 30.12.2018.
 */

@Repository
public interface PastFacRepository extends CrudRepository<PastFaction, Integer> {

    PastFaction findByPlayerIdAndNameId(int playerId, String name);

    List<PastFaction> findByPlayerId(int playerId);

}
