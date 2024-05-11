package server.pvptemple.api.controller;

import server.pvptemple.api.model.MemeLog;
import server.pvptemple.api.model.Player;
import server.pvptemple.api.repo.MemeRepository;
import server.pvptemple.api.repo.PlayerRepository;
import server.pvptemple.api.util.Constants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

@RestController
@RequestMapping("/api/{key}/anticheat")
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
public class MemeController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MemeRepository memeRepository;

    @RequestMapping("/fetch_by_uuid/{uuid}")
    public ResponseEntity<String> fetchByUuid(@PathVariable("key") String key, @PathVariable("uuid") String uuid) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        Player player = this.playerRepository.findFirstByUniqueId(uuid);

        if (player == null) {
            return new ResponseEntity<>(new JsonArray().toString(), HttpStatus.OK);
        }

        JsonArray anticheat = new JsonArray();

        for (MemeLog memeLog : this.memeRepository.findByUuid(player.getUniqueId())) {
            anticheat.add(memeLog.toJson());
        }

        return new ResponseEntity<>(anticheat.toString(), HttpStatus.OK);
    }

    @RequestMapping("/insert")
    public ResponseEntity<String> insert(@PathVariable(name = "key") String key, HttpServletRequest request) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        JsonObject data = new JsonParser().parse(request.getParameter("data")).getAsJsonObject();

        MemeLog memeLog = new MemeLog();
        memeLog.setUuid(data.get("uuid").getAsString());
        memeLog.setFlag(data.get("flag").getAsString());
        memeLog.setClient(data.get("client").getAsString());
        memeLog.setPing(data.get("ping").getAsInt());
        memeLog.setTps(data.get("tps").getAsDouble());
        memeLog.setTimestamp(new Timestamp(data.get("timestamp").getAsLong()));

        this.memeRepository.save(memeLog);

        return new ResponseEntity<>(memeLog.toJson().toString(), HttpStatus.OK);
    }

}
