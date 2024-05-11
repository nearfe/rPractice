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
@Entity(name = "uhc_meetup_data")
public class UHCMeetupData implements Serializable {
    @Id
    @Column(unique = true)
    private int playerId;

    private int kills, deaths;
    private int kill_streak;

    private int played;
    private int wins;

    private int rerolls, elo;
}
