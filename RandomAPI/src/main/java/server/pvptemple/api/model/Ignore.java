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
@Entity(name = "ignores")
public class Ignore implements Serializable {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private int ignoreId;

	private int ignoredId;
	private int playerId;

}
