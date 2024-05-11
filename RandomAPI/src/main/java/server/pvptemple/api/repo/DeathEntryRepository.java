package server.pvptemple.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.pvptemple.api.model.DeathEntry;

import java.util.List;

/**
 * Created by Marko on 30.12.2018.
 */

@Repository
public interface DeathEntryRepository extends CrudRepository<DeathEntry, Integer> {

    DeathEntry findById(int id);

    List<DeathEntry> findByPlayerId(int id);

}