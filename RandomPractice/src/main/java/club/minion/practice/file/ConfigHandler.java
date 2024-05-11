package club.minion.practice.file;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigHandler {
    private static ConfigHandler instance;
    private final JavaPlugin plugin;

    public ConfigHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        instance = this;
        this.createConfigs();
    }

    private void createConfigs() {
        Configs[] var1 = Configs.values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Configs config = var1[var3];
            config.init(this);
        }

    }

    public FileConfiguration createConfig(String name) {
        File conf = new File(this.plugin.getDataFolder(), name);
        if (!conf.exists()) {
            conf.getParentFile().mkdirs();
            this.plugin.saveResource(name, false);
        }

        YamlConfiguration yamlConfiguration = new YamlConfiguration();

        try {
            yamlConfiguration.load(conf);
            return yamlConfiguration;
        } catch (InvalidConfigurationException | IOException var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public void saveConfig(FileConfiguration config, String name) {
        try {
            config.save(new File(this.plugin.getDataFolder(), name));
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public void reloadConfig(FileConfiguration config, String name) {
        try {
            config.load(new File(this.plugin.getDataFolder(), name));
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public static enum Configs {
        EXTRA("extra.yml");

        private final String name;
        private FileConfiguration config;

        private Configs(String name) {
            this.name = name;
        }

        public void init(ConfigHandler handler) {
            this.config = handler.createConfig(this.name);
        }

        public FileConfiguration getConfig() {
            return this.config;
        }

        public void saveConfig() {
            ConfigHandler.instance.saveConfig(this.config, this.name);
        }

        public void reloadConfig() {
            ConfigHandler.instance.reloadConfig(this.config, this.name);
        }
    }
}
