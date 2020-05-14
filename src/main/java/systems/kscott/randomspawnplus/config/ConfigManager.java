package systems.kscott.randomspawnplus.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    @Getter
    private static ConfigManager instance;

    public static void initialize(JavaPlugin plugin) {
        instance = new ConfigManager(plugin);
    }

    @Getter
    private ConfigFile lang;

    @Getter
    private ConfigFile config;

    public ConfigManager(JavaPlugin plugin) {
        lang = new ConfigFile("lang.yml", plugin);
        config = new ConfigFile("config.yml", plugin);
    }

    public FileConfiguration getConfig() {
        return config.getConfig();
    }

    public FileConfiguration getLang() {
        return lang.getConfig();
    }

    public String langString(String key) {
        return lang.getConfig().getString(key);
    }

}
