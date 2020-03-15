package systems.kscott.randomspawnplus3.spawn;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import systems.kscott.randomspawnplus3.RandomSpawnPlus;
import systems.kscott.randomspawnplus3.util.Locations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpawnCacher {

    public static SpawnCacher INSTANCE;

    public static void initialize(RandomSpawnPlus plugin) {
        INSTANCE = new SpawnCacher(plugin);
    }

    public static SpawnCacher getInstance() {
        return INSTANCE;
    }

    private RandomSpawnPlus plugin;

    public SpawnCacher(RandomSpawnPlus plugin) {
        this.plugin = plugin;
    }

    public void cacheSpawns() {

        FileConfiguration spawns = plugin.getSpawns();
        FileConfiguration config = plugin.getConfig();


        List<String> locationStrings = null;
        locationStrings = spawns.getStringList("spawns");


        if (locationStrings.size() >= config.getInt("spawn-cache-target")) {
            return;
        }

        ArrayList<String> locations = new ArrayList<>();

        int spawnCount = config.getInt("spawn-cache-target");

        for (int i = 0; i <= spawnCount; i++) {
            Location location = null;
            boolean valid = false;

            while (!valid) {
                location = SpawnFinder.getInstance().getCandidateLocation();
                valid = SpawnFinder.getInstance().checkSpawn(location);
            }
            locations.add(Locations.serializeString(location));
        }

        spawns.set("spawns", locations);
        try {
            spawns.save("spawns.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
