package server.pvptemple.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by Marko on 30.01.2019.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "sw_data")
public class SWData implements Serializable {
    @Id
    @Column(unique = true)
    private int playerId;

    private int kills;
    private int deaths;
    private int wins;
    private int kill_streak;
    private int played;
    private int coins;
    private int exp;
    private String cage;
    private String gameTrail;
}
