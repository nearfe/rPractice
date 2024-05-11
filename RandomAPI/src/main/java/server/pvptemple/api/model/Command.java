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
@Entity(name = "command_logs")
public class Command implements Serializable {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private int id;

	private String command;

	private int playerId;

}
