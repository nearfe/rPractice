package server.pvptemple.api.repo;

import server.pvptemple.api.model.Match;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchesRepository extends CrudRepository<Match, String> {

	List<Match> findByWinner(int winner);

	List<Match> findByLoser(int winner);

	Match findById(int id);

}
