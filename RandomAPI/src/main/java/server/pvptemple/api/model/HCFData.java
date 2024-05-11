package server.pvptemple.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Marko on 30.12.2018.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "hcf_data")
public class HCFData implements Serializable {
    @Id
    @Column(unique = true)
    private int playerId;

    private int kills;
    private int deaths;
    private Timestamp lastFactionLeaveMillis;
    private boolean nightVision;
    private int lives;
    private int diamonds;
    private int gold;
    private int iron;
    private int coal;
    private int lapis;
    private int redstone;
    private int emerald;
    private int killstreak;
    private int highestKillstreak;

    private String deathBanReason;
    private Timestamp deathBanCreationMillis;
    private Timestamp deathBanExpiryMillis;
    private String deathBanDeathPoint;
    private boolean deathBanEotwDeathban;
}
