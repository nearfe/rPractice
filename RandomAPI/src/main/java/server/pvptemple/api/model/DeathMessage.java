package server.pvptemple.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "deathmessages")
public class DeathMessage implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int dmId;

    private int playerId;
    private String dmName;
    private boolean value;
}
