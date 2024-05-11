package server.pvptemple.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pvptemple.api.model.Player;
import server.pvptemple.api.model.UHCData;
import server.pvptemple.api.repo.PlayerRepository;
import server.pvptemple.api.repo.UHCRepository;
import server.pvptemple.api.util.Constants;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.UUID;

@RestController
@RequestMapping("/api/{key}/uhc")
public class UHCController {
    static {
        // Run this first so whenever it caches reflection data
        UHCData uhcData = new UHCData();

        Field[] fields = uhcData.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            try {
                if (field.getType() == int.class) {
                    field.set(uhcData, 123);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Autowired private UHCRepository uhcRepository;
    @Autowired private PlayerRepository playerRepository;

    @RequestMapping("/{uuid}")
    public ResponseEntity<UHCData> getData(@PathVariable(name = "key") String key,
                                           @PathVariable(name = "uuid") UUID uuid) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        Player player = PlayerController.getPlayer(this.playerRepository, uuid);
        UHCData uhcData = this.uhcRepository.findFirstByPlayerId(player.getPlayerId());

        if (uhcData == null) {
            return null;
        }

        return new ResponseEntity<>(uhcData, HttpStatus.OK);
    }

    @RequestMapping("/{id}/update")
    public ResponseEntity<UHCData> updateData(HttpServletRequest request,
                                                   @PathVariable(name = "key") String key,
                                                   @PathVariable(name = "id") int id) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        UHCData uhcData = this.uhcRepository.findFirstByPlayerId(id);

        if (uhcData == null) {
            uhcData = new UHCData();
            uhcData.setPlayerId(id);
        }

        Field[] fields = uhcData.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            String name = this.deserializeName(field.getName());

            String param = request.getParameter(name);
            if (param == null) {
                continue;
            }

            try {
                if (field.getType() == int.class) {
                    field.set(uhcData, Integer.valueOf(param));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        this.uhcRepository.save(uhcData);

        return new ResponseEntity<>(uhcData, HttpStatus.OK);
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
