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
@Entity(name = "kits")
public class Kit implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int kitId;

    private int playerId;
    private String kitName;
    private long cooldownExpiry;
    private long expiry;
}
