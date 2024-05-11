package server.pvptemple.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by Marko on 20.12.2018.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "prefixes")
public class Prefix implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int prefixId;

    private int playerId;
    private String prefix;
}
