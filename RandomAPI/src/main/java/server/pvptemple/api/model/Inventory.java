package server.pvptemple.api.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import static server.pvptemple.api.util.Constants.PRACTICE_SEASON;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "practice_season_" + PRACTICE_SEASON + "_inventories")
public class Inventory implements Serializable {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private int id;

	private String winnerInventory;
	private String loserInventory;
	private String matchId;

}
