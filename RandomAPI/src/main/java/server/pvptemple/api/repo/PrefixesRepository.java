package server.pvptemple.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.pvptemple.api.model.Prefix;

import java.util.List;

/**
 * Created by Marko on 20.12.2018.
 */

@Repository
public interface PrefixesRepository extends CrudRepository<Prefix, Integer> {

    Prefix findByPlayerIdAndPrefix(int playerId, String prefix);

    List<Prefix> findByPlayerId(int playerId);

}

