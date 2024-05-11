package server.pvptemple.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.pvptemple.api.model.UHCData;
import server.pvptemple.api.model.UHCMeetupData;

@Repository
public interface UHCMeetupRepository extends CrudRepository<UHCMeetupData, Integer> {
    UHCMeetupData findFirstByPlayerId(int id);
}
