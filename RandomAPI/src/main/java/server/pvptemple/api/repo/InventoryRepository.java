package server.pvptemple.api.repo;

import server.pvptemple.api.model.Inventory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends CrudRepository<Inventory, Integer> {

	Inventory findByMatchId(String matchId);

	Inventory findById(int id);

}
