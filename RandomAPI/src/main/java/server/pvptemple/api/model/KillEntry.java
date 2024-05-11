package server.pvptemple.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by Marko on 30.12.2018.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "killentries")
public class KillEntry implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    private String whom;
    private UUID whomUUID;
    private String item;
    private String cause;
    private Timestamp timestamp;
    private int playerId;

}
