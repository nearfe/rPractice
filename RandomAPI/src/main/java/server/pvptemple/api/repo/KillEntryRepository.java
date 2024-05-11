package server.pvptemple.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.pvptemple.api.model.KillEntry;

import java.util.List;

/**
 * Created by Marko on 30.12.2018.
 */

@Repository
public interface KillEntryRepository extends CrudRepository<KillEntry, Integer> {

    KillEntry findById(int id);

    List<KillEntry> findByPlayerId(int id);

}
