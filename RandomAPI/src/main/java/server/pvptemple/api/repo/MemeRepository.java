package server.pvptemple.api.repo;

import server.pvptemple.api.model.MemeLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemeRepository extends CrudRepository<MemeLog, Integer> {

    List<MemeLog> findByUuid(String uuid);

}
