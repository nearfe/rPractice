package server.pvptemple.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.pvptemple.api.model.Booster;
import server.pvptemple.api.model.Kit;
import server.pvptemple.api.model.Prefix;

import java.util.List;

/**
 * Created by Marko on 20.12.2018.
 */

@Repository
public interface KitsRepository extends CrudRepository<Kit, Integer> {

    Kit findByPlayerIdAndKitName(int playerId, String kitName);

    List<Kit> findByPlayerId(int playerId);

    List<Kit> findByKitName(String kitName);

}

