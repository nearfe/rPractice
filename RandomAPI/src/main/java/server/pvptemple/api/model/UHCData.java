package server.pvptemple.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "uhc_data")
public class UHCData implements Serializable {
    @Id
    @Column(unique = true)
    private int playerId;

    private int kills, deaths;
    private int kill_streak;

    private int played;
    private int wins;
    
    private int diamonds;
    private int spawners;
}
