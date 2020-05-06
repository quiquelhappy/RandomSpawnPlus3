package systems.kscott.randomspawnplus3.spawn;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import systems.kscott.randomspawnplus3.RandomSpawnPlus;
import systems.kscott.randomspawnplus3.util.Locations;

import java.util.*;

public class SpawnCacher {

    public static SpawnCacher INSTANCE;


    public static void initialize(RandomSpawnPlus plugin) {
        INSTANCE = new SpawnCacher(plugin);
    }

    public static SpawnCacher getInstance() {
        return INSTANCE;
    }

    private RandomSpawnPlus plugin;

    @Getter
    private boolean spawnsRequireSaving;

    @Getter
    private List<String> cachedSpawns;

    public SpawnCacher(RandomSpawnPlus plugin) {
        this.plugin = plugin;
        this.spawnsRequireSaving = false;
        this.cachedSpawns = new ArrayList<>();
        cacheSpawns();
        runWatchdog();
    }

    private void cacheSpawns() {
        boolean debugMode = plugin.getConfigManager().getConfig().getBoolean("debug-mode");

        FileConfiguration spawns = plugin.getSpawns();
        FileConfiguration config = plugin.getConfig();

        SpawnFinder finder = SpawnFinder.getInstance();

        List<String> locationStrings = spawns.getStringList("spawns");

        cachedSpawns.addAll(locationStrings);

        int missingLocations = config.getInt("spawn-cache-target") - locationStrings.size();

        if (missingLocations <= 0) {
            return;
        }

        List<String> newLocations = new ArrayList<>();

        Bukkit.getLogger().info("Caching "+missingLocations+" spawns.");
        for (int i = 0; i <= missingLocations; i++) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    Location location = null;
                    boolean valid = false;

                    while (!valid) {
                        location = finder.getCandidateLocation();
                        valid = finder.checkSpawn(location);
                    }

                    newLocations.add(Locations.serializeString(location));
                }
            }.runTaskLater(plugin, 1);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                /* Wait for all spawns to be cached */
                if (newLocations.size() <= missingLocations) {
                    if (debugMode)
                        Bukkit.getLogger().info(newLocations.size() +", "+ missingLocations);
                } else {
                    cachedSpawns.addAll(newLocations);
                    /* Save spawns to file */
                    save();
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(plugin, 10, 10);
    }

    public Location getRandomSpawn() {
        int element = new Random().nextInt(cachedSpawns.size());
        return Locations.deserializeLocationString(cachedSpawns.get(element));
    }

    public void deleteSpawn(Location location) {
        for (Iterator<String> iterator = cachedSpawns.iterator(); iterator.hasNext();) {
            String locationString = iterator.next();
            //Bukkit.getLogger().info(Locations.serializeString(location)+", "+locationString);
            if (Locations.serializeString(location).equals(locationString)) {
                iterator.remove();
            }
        }
        cachedSpawns.removeIf(locationString -> locationString.equals(Locations.serializeString(location)));
        spawnsRequireSaving = true;
    }

    public void save() {
        plugin.getSpawnsManager().getConfig().set("spawns", cachedSpawns);
        plugin.getSpawnsManager().save();
    }

    private void runWatchdog() {
        new BukkitRunnable() {

            List<String> spawnCopy = new ArrayList<>(cachedSpawns);

            @Override
            public void run() {
                if (spawnCopy != cachedSpawns) {
                    List<String> spawnCopyCopy = new ArrayList<>(spawnCopy);
                    List<String> cachedSpawnsCopy = new ArrayList<>(cachedSpawns);

                    spawnCopyCopy.forEach(cachedSpawnsCopy::remove);
                    //plugin.getLogger().info(Arrays.toString(cachedSpawnsCopy.toArray()));

                    save();
                    spawnCopy = new ArrayList<>(cachedSpawns);
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 200);
    }
}
