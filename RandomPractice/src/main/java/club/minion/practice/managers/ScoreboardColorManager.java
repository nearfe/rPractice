package club.minion.practice.managers;

import club.minion.practice.Practice;
import club.minion.practice.player.PlayerData;
import club.minion.practice.util.CCUtil;
import com.conaxgames.util.finalutil.CC;
import com.qrakn.phoenix.lang.file.type.BasicConfigurationFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardColorManager {

    private final Practice plugin;
    private final BasicConfigurationFile configFile;

    private final Map<UUID, String> scoreboardColors = new HashMap<>();

    public ScoreboardColorManager(Practice plugin) {
        this.plugin = plugin;
        this.configFile = plugin.getScoreboardColorConfig();
        if (configFile.getConfiguration() == null) saveScoreboardColors();
        loadScoreboardColors();
    }

    public void setScoreboardColor(UUID playerUUID, String color) {
        plugin.getPlayerManager().getPlayerData(playerUUID).setScoreboardColor(CCUtil.getValue(color.toUpperCase()));
        scoreboardColors.put(playerUUID, color);
        saveScoreboardColors();
    }

    public String getScoreboardColor(UUID playerUUID) {
        PlayerData playerData = Practice.getInstance().getPlayerManager().getPlayerData(playerUUID);

        if (playerData != null && playerData.getScoreboardColor() != null) {
            return scoreboardColors.getOrDefault(playerData.getScoreboardColor(), CC.GOLD);
        } else {
            System.out.println("error when updating " + plugin.getServer().getPlayer(playerUUID).getName() + "'s scoreboard: playerData.getScoreboardColor() was apparently null (or the playerData itself)");
            if (playerData != null) playerData.setScoreboardColor(CC.GOLD);
            return CC.GOLD;
        }
    }

    private void loadScoreboardColors() {
        for (String key : configFile.getConfiguration().getKeys(false)) {
            UUID playerUUID = UUID.fromString(key);
            String color = configFile.getString(key);
            scoreboardColors.put(playerUUID, color);
        }
    }

    private void saveScoreboardColors() {
        scoreboardColors.forEach((playerUUID, color) -> configFile.getConfiguration().set(playerUUID.toString(), color));

        try {
            configFile.getConfiguration().save(configFile.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}