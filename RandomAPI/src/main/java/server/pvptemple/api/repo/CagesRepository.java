package server.pvptemple.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.pvptemple.api.model.Cage;
import server.pvptemple.api.model.Kit;

import java.util.List;

/**
 * Created by Marko on 20.12.2018.
 */

@Repository
public interface CagesRepository extends CrudRepository<Cage, Integer> {

    Cage findByPlayerIdAndCageName(int playerId, String cageName);

    List<Cage> findByPlayerId(int playerId);

    List<Cage> findByCageName(String name);

    Long countByPlayerId(int playerId);

}

