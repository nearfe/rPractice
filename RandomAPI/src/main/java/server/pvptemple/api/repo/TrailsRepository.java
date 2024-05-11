package server.pvptemple.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.pvptemple.api.model.Trail;

import java.util.List;

/**
 * Created by Marko on 20.12.2018.
 */

@Repository
public interface TrailsRepository extends CrudRepository<Trail, Integer> {

    Trail findByPlayerIdAndTrailName(int playerId, String cageName);

    List<Trail> findByPlayerId(int playerId);

    List<Trail> findByTrailName(String name);

}

