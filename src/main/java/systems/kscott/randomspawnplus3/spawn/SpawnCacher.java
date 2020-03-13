package systems.kscott.randomspawnplus3.spawn;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.Location;
import systems.kscott.randomspawnplus3.RandomSpawnPlus;
import systems.kscott.randomspawnplus3.util.Locations;

import java.nio.file.Path;
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

        List<String> locationStrings = null;
        try {
            locationStrings = plugin.getRootSpawns().getNode("spawns").getList(TypeToken.of(String.class));
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }

        if (locationStrings.size() >= plugin.getRootConfig().getNode("spawn-cache-target").getInt()) {
            return;
        }

        ArrayList<String> locations = new ArrayList<>();

        int spawns = plugin.getRootConfig().getNode("spawn-cache-target").getInt();

        for (int i = 0; i <= spawns; i++) {
            Location location = null;
            boolean valid = false;

            while (!valid) {
                location = SpawnFinder.getInstance().getCandidateLocation();
                valid = SpawnFinder.getInstance().checkSpawn(location);
            }
            locations.add(Locations.serializeString(location));
        }
        plugin.getRootSpawns().getNode("spawns").setValue(locations);
        plugin.saveSpawns();
    }

}
