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

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marko on 30.12.2018.
 */

@RestController
@RequestMapping("/api/{key}/hcf")
public class HCFController {

    static {
        // Run this first so whenever it caches reflection data
        PracticeData practiceData = new PracticeData();

        Field[] fields = practiceData.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            try {
                if (field.getType() == boolean.class) {
                    field.set(practiceData, Boolean.valueOf("false"));
                } else if (field.getType() == int.class) {
                    field.set(practiceData, Integer.valueOf("123"));
                } else if (field.getType() == double.class) {
                    field.set(practiceData, Double.valueOf("123"));
                } else if (field.getType() == String.class) {
                    field.set(practiceData, "xd");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Autowired private HCFRepository practiceRepository;
    @Autowired private PlayerRepository playerRepository;
    @Autowired private KitsRepository kitsRepository;

    @RequestMapping("/{uuid}")
    public ResponseEntity<HCFData> getData(@PathVariable(name = "key") String key,
                                                @PathVariable(name = "uuid") UUID uuid) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        Player player = PlayerController.getPlayer(this.playerRepository, uuid);
        HCFData hcfData = this.practiceRepository.findFirstByPlayerId(player.getPlayerId());

        if (hcfData == null) {
            return null;
        }

        return new ResponseEntity<>(hcfData, HttpStatus.OK);
    }

    @RequestMapping("/{id}/update")
    public ResponseEntity<HCFData> updateData(HttpServletRequest request,
                                                   @PathVariable(name = "key") String key,
                                                   @PathVariable(name = "id") int id) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        HCFData hcfData = this.practiceRepository.findFirstByPlayerId(id);

        if (hcfData == null) {
            hcfData = new HCFData();
            hcfData.setPlayerId(id);
        }

        Field[] fields = hcfData.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            String name = this.deserializeName(field.getName());

            String param = request.getParameter(name);
            if (param == null) {
                continue;
            }

            try {
                if (field.getType() == boolean.class) {
                    field.set(hcfData, Boolean.valueOf(param));
                } else if (field.getType() == int.class) {
                    field.set(hcfData, Integer.valueOf(param));
                } else if (field.getType() == double.class) {
                    field.set(hcfData, Double.valueOf(param));
                } else if (field.getType() == String.class) {
                    field.set(hcfData, param);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        this.practiceRepository.save(hcfData);

        return new ResponseEntity<>(hcfData, HttpStatus.OK);
    }

    @Autowired
    private PastFacRepository pastFacRepository;
    @Autowired
    private KillEntryRepository killEntryRepository;
    @Autowired
    private DeathEntryRepository deathEntryRepository;

    @RequestMapping("/{uuid}/update-pastfactions/{name}")
    public ResponseEntity<String> updatePastFaction(@PathVariable(name = "key") String key,
                                               @PathVariable(name = "uuid") UUID uuid,
                                               @PathVariable(name = "name") String name) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        Player player = this.playerRepository.findFirstByUniqueId(uuid.toString());

        PastFaction pastFaction = this.pastFacRepository.findByPlayerIdAndNameId(player.getPlayerId(),
                name);

        if (pastFaction != null) {
            this.pastFacRepository.delete(pastFaction);
        } else {
            pastFaction = new PastFaction();

            pastFaction.setNameId(name);
            pastFaction.setPlayerId(player.getPlayerId());

            this.pastFacRepository.save(pastFaction);
        }

        JsonObject object = new JsonObject();
        object.addProperty("response", "success");
        return new ResponseEntity<>(object.toString(), HttpStatus.OK);
    }

    @RequestMapping("/{uuid}/fetch-pastfactions")
    public ResponseEntity<List<PastFaction>> getPastFactionsData(@PathVariable(name = "key") String key,
                                                      @PathVariable(name = "uuid") UUID uuid) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        Player player = this.playerRepository.findFirstByUniqueId(uuid.toString());
        if (player == null) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }

        return new ResponseEntity<>(this.pastFacRepository.findByPlayerId(player.getPlayerId()), HttpStatus.OK);
    }

    @RequestMapping("/update-kill-entries")
    public ResponseEntity<String> onUpdateKills(HttpServletRequest request,
                                           @PathVariable("key") String key) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        String whom = request.getParameter("whom");
        UUID whomUUID = UUID.fromString(request.getParameter("whomUUID"));
        String item = request.getParameter("item");
        String cause = request.getParameter("cause");

        String strExpiry = request.getParameter("expiry");
        Timestamp timestamp = null;
        if (strExpiry != null) {
            timestamp = Timestamp.valueOf(strExpiry);
        }

        int id = Integer.parseInt(request.getParameter("playerId"));

        KillEntry killEntry = new KillEntry();

        killEntry.setPlayerId(id);
        killEntry.setWhom(whom);
        killEntry.setWhomUUID(whomUUID);
        killEntry.setItem(item);
        killEntry.setCause(cause);
        killEntry.setTimestamp(timestamp);

        this.killEntryRepository.save(killEntry);
        return Constants.SUCCESS_STRING;
    }

    @RequestMapping("/update-death-entries")
    public ResponseEntity<String> onUpdateDeaths(HttpServletRequest request,
                                           @PathVariable("key") String key) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        String by = request.getParameter("by");
        UUID byUUID = UUID.fromString(request.getParameter("byUUID"));
        String item = request.getParameter("item");
        String cause = request.getParameter("cause");

        int id = Integer.parseInt(request.getParameter("playerId"));

        DeathEntry deathEntry = new DeathEntry();

        deathEntry.setPlayerId(id);
        deathEntry.setKilledBy(by);
        deathEntry.setByUUID(byUUID);
        deathEntry.setItem(item);
        deathEntry.setCause(cause);

        this.deathEntryRepository.save(deathEntry);
        return Constants.SUCCESS_STRING;
    }

    @RequestMapping("/{uuid}/kits")
    public ResponseEntity<List<Kit>> getKits(@PathVariable(name = "key") String key,
                                                                 @PathVariable(name = "uuid") UUID uuid) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        Player player = this.playerRepository.findFirstByUniqueId(uuid.toString());
        if (player == null) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }

        return new ResponseEntity<>(this.kitsRepository.findByPlayerId(player.getPlayerId()), HttpStatus.OK);
    }

    @RequestMapping("/{uuid}/update-kit/{name}")
    public ResponseEntity<String> onUpdateKit(HttpServletRequest request,
                                              @PathVariable(name = "key") String key,
                                              @PathVariable(name = "uuid") UUID uuid,
                                              @PathVariable(name = "name") String name) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        Player player = this.playerRepository.findFirstByUniqueId(uuid.toString());
        if (player == null) {
            return Constants.PLAYER_NOT_FOUND_STRING;
        }

        Kit kit = kitsRepository.findByPlayerIdAndKitName(player.getPlayerId(), name);
        boolean isRemoval = Boolean.valueOf(request.getParameter("remove"));

        if (isRemoval) {
            if (kit == null) {
                return Constants.KIT_NOT_FOUND_STRING;
            }

            kitsRepository.delete(kit);
        } else {
            if (kit != null) {
                return Constants.ALREADY_HAVE_KIT_STRING;
            }

            long expiry = Long.parseLong(request.getParameter("expiry"));

            Kit newKit = new Kit();
            newKit.setPlayerId(player.getPlayerId());
            newKit.setKitName(name);
            newKit.setExpiry(expiry);
            kitsRepository.save(newKit);
        }

        return Constants.SUCCESS_STRING;
    }

    @RequestMapping("/{uuid}/update-kit-cooldown/{name}")
    public ResponseEntity<String> onUpdateKitCooldown(HttpServletRequest request,
                                              @PathVariable(name = "key") String key,
                                              @PathVariable(name = "uuid") UUID uuid,
                                              @PathVariable(name = "name") String name) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        Player player = this.playerRepository.findFirstByUniqueId(uuid.toString());
        if (player == null) {
            return Constants.PLAYER_NOT_FOUND_STRING;
        }

        Kit kit = kitsRepository.findByPlayerIdAndKitName(player.getPlayerId(), name);
        if (kit == null) {
            return Constants.KIT_NOT_FOUND_STRING;
        }

        long cooldownExpiry = Long.parseLong(request.getParameter("cooldownExpiry"));
        kit.setCooldownExpiry(cooldownExpiry);
        kitsRepository.save(kit);

        return Constants.SUCCESS_STRING;
    }

    @RequestMapping("/delete-kit/{name}")
    public ResponseEntity<String> onDeleteKit(HttpServletRequest request,
                                                      @PathVariable(name = "key") String key,
                                                      @PathVariable(name = "name") String name) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        List<Kit> kits = kitsRepository.findByKitName(name);
        kitsRepository.delete(kits);
        return Constants.SUCCESS_STRING;
    }

    private String deserializeName(String name) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < name.length(); i++) {
            char c = name.toCharArray()[i];

            if (Character.isUpperCase(c)) {
                sb.append("_");
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

}
