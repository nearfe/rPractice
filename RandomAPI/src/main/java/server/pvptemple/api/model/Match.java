package server.pvptemple.api.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import static server.pvptemple.api.util.Constants.PRACTICE_SEASON;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "practice_season_" + PRACTICE_SEASON + "_matches")
public class Match implements Serializable {


	@Id
	private String id;

	private int inventory;

	private int winner;
	private int loser;

	private int winnerEloBefore;
	private int loserEloBefore;

	private int winnerEloAfter;
	private int loserEloAfter;

}
