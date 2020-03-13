package systems.kscott.randomspawnplus3;

import co.aikar.commands.PaperCommandManager;
import net.ess3.api.IEssentials;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.bukkit.plugin.java.JavaPlugin;
import systems.kscott.randomspawnplus3.commands.CommandRSP;
import systems.kscott.randomspawnplus3.commands.CommandWild;
import systems.kscott.randomspawnplus3.listeners.RSPDeathListener;
import systems.kscott.randomspawnplus3.listeners.RSPFirstJoinListener;
import systems.kscott.randomspawnplus3.listeners.RSPLoginListener;
import systems.kscott.randomspawnplus3.spawn.SpawnCacher;
import systems.kscott.randomspawnplus3.spawn.SpawnFinder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public final class RandomSpawnPlus extends JavaPlugin {

    public static RandomSpawnPlus INSTANCE;

    ConfigurationNode config;
    ConfigurationNode lang;
    ConfigurationNode spawns;

    YAMLConfigurationLoader spawnsLoader;

    @Override
    public void onEnable() {
        loadConfig();
        loadLang();
        loadSpawns();

        registerEvents();
        registerCommands();

        SpawnFinder.initialize(this);
        SpawnCacher.initialize(this);
        SpawnCacher.getInstance().cacheSpawns();
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public void loadConfig() {
        String configName = "config.yml";
        File file = new File(getDataFolder().toString(), configName);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            saveResource(configName, true);
        }
        YAMLConfigurationLoader loader = YAMLConfigurationLoader.builder().setPath(Paths.get(getDataFolder().toString(), configName).toAbsolutePath()).build();
        ConfigurationNode rootNode;
        try {
            rootNode = loader.load();
            config = rootNode;
            //getLogger().info(config.toString());
        } catch(IOException e) {
            getServer().getLogger().severe("Failed to load config!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void loadLang() {
        String configName = "lang.yml";
        File file = new File(getDataFolder().toString(), configName);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            saveResource(configName, true);
        }
        YAMLConfigurationLoader loader = YAMLConfigurationLoader.builder().setPath(Paths.get(getDataFolder().toString(), configName).toAbsolutePath()).build();
        ConfigurationNode rootNode;
        try {
            rootNode = loader.load();
            lang = rootNode;
        } catch(IOException e) {
            getServer().getLogger().severe("Failed to load lang!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void loadSpawns() {
        String configName = "spawns.yml";
        File file = new File(getDataFolder().toString(), configName);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            saveResource(configName, true);
        }
        spawnsLoader = YAMLConfigurationLoader.builder().setPath(Paths.get(getDataFolder().toString(), configName).toAbsolutePath()).build();
        ConfigurationNode rootNode;
        try {
            rootNode = spawnsLoader.load();
            spawns = rootNode;
        } catch(IOException e) {
            getServer().getLogger().severe("Failed to load lang!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void registerEvents() {
        getServer().getPluginManager().registerEvents(new RSPDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new RSPLoginListener(this), this);
        getServer().getPluginManager().registerEvents(new RSPFirstJoinListener(this), this);
    }

    public void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new CommandWild(this));
        manager.registerCommand(new CommandRSP(this));
    }

    public ConfigurationNode getRootConfig() {
        return config;
    }

    public ConfigurationNode getRootLang() {
        return lang;
    }

    public ConfigurationNode getRootSpawns() {return spawns;}

    public void saveSpawns() {
        try {
            spawnsLoader.save(spawns);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RandomSpawnPlus getInstance() {
        return INSTANCE;
    }

    public IEssentials getEssentials() {
        return (IEssentials) getServer().getPluginManager().getPlugin("Essentials");
    }
}
