package server.pvptemple.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.pvptemple.api.model.Booster;
import server.pvptemple.api.model.Kit;

import java.util.List;

/**
 * Created by Marko on 20.12.2018.
 */

@Repository
public interface BoostersRepository extends CrudRepository<Booster, Integer> {

    Booster findByPlayerIdAndBoosterName(int playerId, String kitName);

    List<Booster> findByPlayerId(int playerId);

    List<Booster> findByBoosterName(String kitName);

}

