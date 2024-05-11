package server.pvptemple.api.controller;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pvptemple.api.model.*;
import server.pvptemple.api.repo.*;
import server.pvptemple.api.util.Constants;

import java.util.UUID;

@RestController
@RequestMapping("/api/{key}/global")
public class HubController {
    @Autowired private PlayerRepository playerRepository;
    @Autowired private PunishmentRepository punishmentRepository;
    @Autowired private HCFRepository hcfRepository;
    @Autowired private PracticeRepository practiceRepository;
    @Autowired private UHCRepository uhcRepository;
    @Autowired private UHCMeetupRepository meetupRepository;
    @Autowired private SGRepository sgRepository;

    @Autowired private SWRepository swRepository;
    @Autowired private CagesRepository cagesRepository;
    @Autowired private SWKitsRepository swKitsRepository;

    @RequestMapping("/{uuid}")
    public ResponseEntity<String> getGlobalStats(@PathVariable(name = "key") String key,
                                                 @PathVariable(name = "uuid") UUID uuid) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        Player player = playerRepository.findFirstByUniqueId(uuid.toString());
        if (player == null) {
            return Constants.PLAYER_NOT_FOUND_STRING;
        }

        JsonObject object = new JsonObject();
        object.addProperty("name", player.getName());
        object.addProperty("firstSeen", player.getFirstLogin().getTime());
        object.addProperty("lastSeen", player.getLastLogin().getTime());
        object.addProperty("punishCount", punishmentRepository.countByPlayerId(player.getPlayerId()));
        object.addProperty("rank", player.getRank());

        UHCData data = uhcRepository.findFirstByPlayerId(player.getPlayerId());
        if (data != null) {
            JsonObject uhcObject = new JsonObject();
            uhcObject.addProperty("soloWins", data.getWins());
            uhcObject.addProperty("soloKills", data.getKills());
            uhcObject.addProperty("deaths", data.getDeaths());
            uhcObject.addProperty("diamondsMined", data.getDiamonds());
            uhcObject.addProperty("spawnersMined", data.getSpawners());
            uhcObject.addProperty("gamesPlayed", data.getPlayed());

            object.add("uhc", uhcObject);
        }

        PracticeData practiceData = practiceRepository.findFirstByPlayerId(player.getPlayerId());
        if (practiceData != null) {
            JsonObject pracObject = new JsonObject();
            pracObject.addProperty("unrankedWins", practiceData.getUnrankedWins());
            pracObject.addProperty("rankedWins", practiceData.getRankedWins());
            pracObject.addProperty("globalElo", practiceData.getGlobalElo());
            object.add("practice", pracObject);
        }

        HCFData hcfData = hcfRepository.findFirstByPlayerId(player.getPlayerId());
        if (hcfData != null) {
            JsonObject hcfObject = new JsonObject();
            hcfObject.addProperty("kills", hcfData.getKills());
            hcfObject.addProperty("deaths", hcfData.getDeaths());
            hcfObject.addProperty("highestKillstreak", hcfData.getKillstreak());
            hcfObject.addProperty("currentKillstreak", hcfData.getHighestKillstreak());
            object.add("hcf", hcfObject);
        }

        UHCMeetupData meetupData = meetupRepository.findFirstByPlayerId(player.getPlayerId());
        if (meetupData != null) {
            JsonObject meetupObject = new JsonObject();
            meetupObject.addProperty("wins", meetupData.getWins());
            meetupObject.addProperty("kills", meetupData.getKills());
            meetupObject.addProperty("deaths", meetupData.getDeaths());
            meetupObject.addProperty("gamesPlayed", meetupData.getPlayed());
            meetupObject.addProperty("current", meetupData.getKill_streak());
            meetupObject.addProperty("elo", meetupData.getElo());
            object.add("meetup", meetupObject);
        }

        SGData sgData = sgRepository.findFirstByPlayerId(player.getPlayerId());
        if (sgData != null) {
            JsonObject sgObject = new JsonObject();
            sgObject.addProperty("wins", sgData.getWins());
            sgObject.addProperty("kills", sgData.getKills());
            sgObject.addProperty("deaths", sgData.getDeaths());
            sgObject.addProperty("gamesPlayed", sgData.getPlayed());
            object.add("sg", sgObject);
        }

        SWData swData = swRepository.findFirstByPlayerId(player.getPlayerId());
        if (swData != null) {
            JsonObject swObject = new JsonObject();
            swObject.addProperty("wins", swData.getWins());
            swObject.addProperty("kills", swData.getKills());
            swObject.addProperty("deaths", swData.getDeaths());
            swObject.addProperty("gamesPlayed", swData.getPlayed());
            swObject.addProperty("coins", swData.getCoins());
            swObject.addProperty("kitsUnlocked", swKitsRepository.countByPlayerId(player.getPlayerId()));
            swObject.addProperty("cagesUnlocked", cagesRepository.countByPlayerId(player.getPlayerId()));

            object.add("skywars", swObject);
        }

        return new ResponseEntity<>(object.toString(), HttpStatus.OK);
    }
}
