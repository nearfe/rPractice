package server.pvptemple.api.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.pvptemple.api.model.DisguiseName;

import java.util.List;

@Repository
public interface DisguiseNameRepository extends JpaRepository<DisguiseName, Integer> {
    DisguiseName findById(int id);
    DisguiseName findFirstByName(String name);
}