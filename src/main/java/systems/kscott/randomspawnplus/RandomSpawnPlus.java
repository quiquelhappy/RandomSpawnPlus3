package systems.kscott.randomspawnplus;

import org.bukkit.plugin.java.JavaPlugin;
import systems.kscott.randomspawnplus.config.ConfigManager;
import systems.kscott.randomspawnplus.spawn.Cacher;
import systems.kscott.randomspawnplus.spawn.Finder;

public class RandomSpawnPlus extends JavaPlugin {

    @Override
    public void onEnable() {
        Cacher.initialize();
        Finder.initialize();
        ConfigManager.initialize(this);
    }

}
