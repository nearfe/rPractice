package server.pvptemple.api.model;

import java.io.Serializable;
import java.sql.Timestamp;
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
@Entity(name = "punishments")
public class Punishment implements Serializable {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private int id;

	private Timestamp timestamp;
	private Timestamp expiry;

	private String serverIp;
	private String reason;
	private String type;
	private String ip;

	private int punisherId;
	private int playerId;

}
