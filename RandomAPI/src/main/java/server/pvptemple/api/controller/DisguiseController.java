package server.pvptemple.api.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pvptemple.api.model.DisguiseName;
import server.pvptemple.api.model.Player;
import server.pvptemple.api.repo.DisguiseNameRepository;
import server.pvptemple.api.repo.PlayerRepository;
import server.pvptemple.api.util.Constants;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/{key}/disguise")
public class DisguiseController {
    @PersistenceContext private EntityManager entityManager;
    @Autowired private PlayerRepository playerRepository;
    @Autowired private DisguiseNameRepository disguiseRepository;

    @RequestMapping("/{uuid}/update")
    public ResponseEntity<String> updateDisguise(HttpServletRequest request,
                                                 @PathVariable("key") String key,
                                                 @PathVariable("uuid") UUID uuid) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        Player player = playerRepository.findFirstByUniqueId(uuid.toString());
        if (player == null) {
            return null;
        }

        boolean status = Boolean.valueOf(request.getParameter("status"));
        if (status) {
            player.setDisguiseName(request.getParameter("disguiseName"));
            player.setDisguiseRank(request.getParameter("disguiseRank"));
            player.setDisguiseSkin(request.getParameter("disguiseSkin"));
            player.setLastDisguise(System.currentTimeMillis());
        } else {
            player.setDisguiseName(null);
            player.setDisguiseRank(null);
            player.setDisguiseSkin(null);
        }
        playerRepository.save(player);

        return Constants.SUCCESS_STRING;
    }

    @RequestMapping("/random-name")
    public ResponseEntity<DisguiseName> randomName(@PathVariable("key") String key) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        Query countQuery = entityManager.createNativeQuery("SELECT COUNT(*) FROM disguise_names");
        BigInteger count = (BigInteger) countQuery.getSingleResult();

        int id = ThreadLocalRandom.current().nextInt(count.intValue()) + 1;
        DisguiseName name = disguiseRepository.findById(id);

        return new ResponseEntity<>(name, HttpStatus.OK);
    }

    @RequestMapping("/list")
    public ResponseEntity<List<DisguiseName>> list(@PathVariable("key") String key) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        return new ResponseEntity<>(disguiseRepository.findAll(), HttpStatus.OK);
    }

    @RequestMapping("/update")
    public ResponseEntity<String> list(HttpServletRequest request,
                                                   @PathVariable("key") String key) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        boolean add = Boolean.valueOf(request.getParameter("add"));
        if (add) {
            String name = request.getParameter("name");
            String skin = request.getParameter("skin");

            if (disguiseRepository.findFirstByName(name) != null) {
                return Constants.ALREADY_HAVE_DISGUISE_STRING;
            }

            DisguiseName disguise = new DisguiseName();
            disguise.setName(name);
            disguise.setSkin(skin);
            disguiseRepository.save(disguise);
        } else {
            int id = Integer.parseInt(request.getParameter("id"));
            disguiseRepository.delete(id);
        }

        return Constants.SUCCESS_STRING;
    }
}