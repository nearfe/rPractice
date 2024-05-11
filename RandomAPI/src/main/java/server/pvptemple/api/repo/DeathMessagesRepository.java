package server.pvptemple.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.pvptemple.api.model.Cage;
import server.pvptemple.api.model.DeathMessage;

import java.util.List;

/**
 * Created by Marko on 20.12.2018.
 */

@Repository
public interface DeathMessagesRepository extends CrudRepository<DeathMessage, Integer> {

    DeathMessage findByPlayerIdAndDmName(int playerId, String dmName);

    List<DeathMessage> findByPlayerId(int playerId);

    List<DeathMessage> findByDmName(String dmName);

}

