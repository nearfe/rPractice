package server.pvptemple.api.model;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "anticheat")
public class MemeLog {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "flag")
    private String flag;

    @Column(name = "client")
    private String client;

    @Column(name = "ping")
    private int ping;

    @Column(name = "tps")
    private double tps;

    @Column(name = "time")
    private Timestamp timestamp;

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("uuid", this.getUuid());
        object.addProperty("flag", this.getFlag());
        object.addProperty("client", this.getClient());
        object.addProperty("ping", this.getPing());
        object.addProperty("tps", this.getTps());
        object.addProperty("timestamp", this.getTimestamp().getTime());
        return object;
    }

}