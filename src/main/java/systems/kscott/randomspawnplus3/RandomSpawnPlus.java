package systems.kscott.randomspawnplus3;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import net.ess3.api.IEssentials;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import systems.kscott.randomspawnplus3.commands.CommandRSP;
import systems.kscott.randomspawnplus3.commands.CommandWild;
import systems.kscott.randomspawnplus3.listeners.RSPDeathListener;
import systems.kscott.randomspawnplus3.listeners.RSPFirstJoinListener;
import systems.kscott.randomspawnplus3.listeners.RSPLoginListener;
import systems.kscott.randomspawnplus3.spawn.SpawnCacher;
import systems.kscott.randomspawnplus3.spawn.SpawnFinder;
import systems.kscott.randomspawnplus3.util.Chat;
import systems.kscott.randomspawnplus3.util.ConfigFile;
import systems.kscott.randomspawnplus3.util.Metrics;

public final class RandomSpawnPlus extends JavaPlugin {

    public static RandomSpawnPlus INSTANCE;

    @Getter
    private ConfigFile configManager;
    @Getter
    private ConfigFile langManager;
    @Getter
    private ConfigFile spawnsManager;

    @Override
    public void onEnable() {

        configManager = new ConfigFile(this, "config.yml");
        langManager = new ConfigFile(this, "lang.yml");
        spawnsManager = new ConfigFile(this, "spawns.yml");

        Chat.setLang(langManager.getConfig());

        registerEvents();
        registerCommands();

        SpawnFinder.initialize(this);
        SpawnCacher.initialize(this);
        INSTANCE = this;

        new Metrics(this, 6465);
    }

    @Override
    public void onDisable() {
        SpawnCacher.getInstance().save();
    }


    public void registerEvents() {
        getServer().getPluginManager().registerEvents(new RSPDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new RSPLoginListener(this), this);
        getServer().getPluginManager().registerEvents(new RSPFirstJoinListener(this), this);
    }

    public void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new CommandWild(this));
        if (configManager.getConfig().getBoolean("wild-enabled")) {
            manager.registerCommand(new CommandRSP(this));
        }
    }

    public static RandomSpawnPlus getInstance() {
        return INSTANCE;
    }

    public IEssentials getEssentials() {
        return (IEssentials) getServer().getPluginManager().getPlugin("Essentials");
    }

    public FileConfiguration getConfig() {
        return configManager.getConfig();
    }

    public FileConfiguration getLang() {
        return langManager.getConfig();
    }

    public FileConfiguration getSpawns() {
        return spawnsManager.getConfig();
    }

}
