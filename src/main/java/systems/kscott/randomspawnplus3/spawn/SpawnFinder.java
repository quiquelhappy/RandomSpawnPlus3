package systems.kscott.randomspawnplus3.spawn;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import systems.kscott.randomspawnplus3.RandomSpawnPlus;
import systems.kscott.randomspawnplus3.events.SpawnCheckEvent;
import systems.kscott.randomspawnplus3.exceptions.FinderTimedOutException;
import systems.kscott.randomspawnplus3.util.Blocks;
import systems.kscott.randomspawnplus3.util.Locations;
import systems.kscott.randomspawnplus3.util.Numbers;
import systems.kscott.randomspawnplus3.util.XMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpawnFinder {

    public static SpawnFinder INSTANCE;

    public static void initialize(RandomSpawnPlus plugin) {
        INSTANCE = new SpawnFinder(plugin);
    }

    public static SpawnFinder getInstance() {
        return INSTANCE;
    }

    ArrayList<Material> safeBlocks;

    public RandomSpawnPlus plugin;

    public FileConfiguration config;

    public SpawnFinder(RandomSpawnPlus plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        /* Setup safeblocks */
        List<String> safeBlockStrings = new ArrayList<>();
        safeBlockStrings = config.getStringList("safe-blocks");

        safeBlocks = new ArrayList<>();
        for (String string : safeBlockStrings) {
            safeBlocks.add(Material.matchMaterial(string));
        }

        safeBlocks.add(XMaterial.AIR.parseMaterial());
        safeBlocks.add(XMaterial.VOID_AIR.parseMaterial());
        safeBlocks.add(XMaterial.CAVE_AIR.parseMaterial());
    }

    public Location getCandidateLocation() {
        String worldString = config.getString("respawn-world");

        World world = Bukkit.getWorld(worldString);

        if (world == null) {
            plugin.getLogger().severe("The world '"+worldString+"' is invalid. Please change the 'respawn-world' key in the config.");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }

        int minX = config.getInt("spawn-range.min-x");
        int minZ = config.getInt("spawn-range.min-z");
        int maxX = config.getInt("spawn-range.max-x");
        int maxZ = config.getInt("spawn-range.max-z");

        int candidateX = Numbers.getRandomNumberInRange(minX, maxX);
        int candidateZ = Numbers.getRandomNumberInRange(minZ, maxZ);
        int candidateY = getHighestY(world, candidateX, candidateZ);

        return new Location(world, candidateX, candidateY, candidateZ);
    }

    private Location getValidLocation(boolean useSpawnCaching) throws FinderTimedOutException {

        FileConfiguration spawns = plugin.getSpawns();

        boolean useCache = config.getBoolean("enable-spawn-cacher");

        boolean valid = false;

        Location location = null;

        int tries = 0;
        while (!valid) {
            if (tries >= 30) {
                throw new FinderTimedOutException();
            }
            if (SpawnCacher.getInstance().getCachedSpawns().size() == 0) {
                plugin.getLogger().severe(plugin.getLangManager().getConfig().getString("no-spawns-cached"));
            }
            if (useCache && useSpawnCaching && SpawnCacher.getInstance().getCachedSpawns().size() != 0) {
                location = SpawnCacher.getInstance().getRandomSpawn();
            } else {
                location = getCandidateLocation();
            }
            valid = checkSpawn(location);

            if (!valid && useCache && useSpawnCaching) {
                SpawnCacher.getInstance().deleteSpawn(location);
            }
            tries = tries + 1;
        }

        return location;
    }

    public Location findSpawn(boolean useSpawnCaching) throws FinderTimedOutException {

        Location location = getValidLocation(useSpawnCaching);

        boolean debugMode = config.getBoolean("debug-mode");
        if (debugMode) {
            Location locClone = location.clone();
            plugin.getLogger().info(locClone.getBlock().getType().toString());
            plugin.getLogger().info(locClone.add(0, 1, 0).getBlock().getType().toString());
            plugin.getLogger().info(locClone.add(0, 1, 0).getBlock().getType().toString());
            plugin.getLogger().info("Spawned at "+location.getBlockX()+", "+location.getBlockY()+", "+location.getBlockZ());
        }
        return location.add(0, 1, 0);
    }

    public boolean checkSpawn(Location location) {
        boolean blockWaterSpawns = config.getBoolean("block-water-spawns");
        boolean blockLavaSpawns = config.getBoolean("block-lava-spawns");
        boolean debugMode = config.getBoolean("debug-mode");
        boolean blockedSpawnRange = config.getBoolean("blocked-spawns-zone.enabled");

        int blockedMaxX = config.getInt("blocked-spawns-zone.max-x");
        int blockedMinX = config.getInt("blocked-spawns-zone.min-x");
        int blockedMaxZ = config.getInt("blocked-spawns-zone.max-z");
        int blockedMinZ = config.getInt("blocked-spawns-zone.min-z");


        boolean isValid;

        Location locClone = location.clone();

        Block block0 = locClone.getBlock();
        Block block1 = locClone.add(0, 1, 0).getBlock();
        Block block2 = locClone.add(0, 1, 0).getBlock();

        SpawnCheckEvent spawnCheckEvent = new SpawnCheckEvent(location);

        Bukkit.getServer().getPluginManager().callEvent(spawnCheckEvent);

        isValid = spawnCheckEvent.isValid();

        if (!isValid) {
            if (debugMode)
                plugin.getLogger().info("Invalid spawn: "+spawnCheckEvent.getValidReason());
        }

        if (blockedSpawnRange) {
            if (Numbers.betweenExclusive((int) location.getX(), blockedMinX, blockedMaxX)) {
                isValid = false;
            }
            if (Numbers.betweenExclusive((int) location.getZ(), blockedMinZ, blockedMaxZ)) {
                isValid = false;
            }
        }

        if (Blocks.isEmpty(block0)) {
            if (debugMode) {
                plugin.getLogger().info("Invalid spawn: block0 isAir");
            }
            isValid = false;
        }

        if (!Blocks.isEmpty(block1) || !Blocks.isEmpty(block2)) {
            if (debugMode) {
                plugin.getLogger().info("Invalid spawn: block1 or block2 !isAir");
            }
            isValid = false;
        }

        if (!safeBlocks.contains(block1.getType())) {
            if (debugMode) {
                plugin.getLogger().info("Invalid spawn: "+block1.getType().toString()+" is not a safe block!");
            }
            isValid = false;
        }

        if (blockWaterSpawns) {
            if (block0.getType() == Material.WATER) {
                if (debugMode) {
                    plugin.getLogger().info("Invalid spawn: blockWaterSpawns");
                }
                isValid = false;
            }
        }

        if (blockLavaSpawns) {
            if (block0.getType() == Material.LAVA) {
                if (debugMode) {
                    plugin.getLogger().info("Invalid spawn: blockLavaSpawns");
                }
                isValid = false;
            }
        }

        return isValid;
    }

    public int getHighestY(World world, int x, int z) {
        int i = 255;
        while (i > 0) {
            if (!Blocks.isEmpty(new Location(world, x, i, z).getBlock())) {
                if (config.getBoolean("debug-mode"))
                    plugin.getLogger().info(Integer.toString(i));
                return i;
            }
            i--;
        }
        return i;
    }


}
