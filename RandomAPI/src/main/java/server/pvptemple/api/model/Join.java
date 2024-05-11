package server.pvptemple.api.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "joins")
public class Join implements Serializable {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private int joinId;

	private int playerId;
	private int count;

	private String ip;

}
