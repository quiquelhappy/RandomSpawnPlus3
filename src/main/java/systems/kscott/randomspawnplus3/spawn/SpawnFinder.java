package systems.kscott.randomspawnplus3.spawn;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import systems.kscott.randomspawnplus3.RandomSpawnPlus;
import systems.kscott.randomspawnplus3.events.SpawnCheckEvent;
import systems.kscott.randomspawnplus3.util.Blocks;
import systems.kscott.randomspawnplus3.util.Locations;
import systems.kscott.randomspawnplus3.util.Numbers;

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

    public ConfigurationNode config;

    public SpawnFinder(RandomSpawnPlus plugin) {
        this.plugin = plugin;
        this.config = plugin.getRootConfig();

        /* Setup safeblocks */
        List<String> safeBlockStrings = new ArrayList<>();
        try {
            safeBlockStrings = config.getNode("safe-blocks").getList(TypeToken.of(String.class));
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }

        safeBlocks = new ArrayList<>();
        for (String string : safeBlockStrings) {
            safeBlocks.add(Material.matchMaterial(string));
        }

        safeBlocks.add(Material.AIR);
        safeBlocks.add(Material.VOID_AIR);
        safeBlocks.add(Material.CAVE_AIR);
    }

    public Location getCandidateLocation() {
        String worldString = config.getNode("respawn-world").getString();

        World world = Bukkit.getWorld(worldString);

        if (world == null) {
            plugin.getLogger().severe("The world '"+worldString+"' is invalid. Please change the 'respawn-world' key in the config.");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }

        ConfigurationNode rangeNode = config.getNode("spawn-range");
        int maxX = rangeNode.getNode("max-x").getInt();
        int minX = rangeNode.getNode("min-x").getInt();
        int maxZ = rangeNode.getNode("max-z").getInt();
        int minZ = rangeNode.getNode("min-z").getInt();

        int candidateX = Numbers.getRandomNumberInRange(minX, maxX);
        int candidateZ = Numbers.getRandomNumberInRange(minZ, maxZ);
        int candidateY = getHighestY(world, candidateX, candidateZ);

        return new Location(world, candidateX, candidateY, candidateZ);
    }

    private Location getValidLocation(boolean useSpawnCaching) {
        boolean useCache = plugin.getRootConfig().getNode("enable-spawn-cacher").getBoolean();

        boolean valid = false;

        Location location = null;

        while (!valid) {
            if (useCache && useSpawnCaching) {

                List<String> locations = null;

                try {
                    locations = plugin.getRootSpawns().getNode("spawns").getList(TypeToken.of(String.class));
                } catch (ObjectMappingException e) {
                    e.printStackTrace();
                }

                int i = new Random().nextInt(locations.size());
                location = Locations.deserializeLocationString(locations.get(i));
            } else {
                location = getCandidateLocation();
            }
            valid = checkSpawn(location);
        }

        return location;
    }

    public Location findSpawn(boolean useSpawnCaching) {

        Location location = getValidLocation(useSpawnCaching);

        boolean debugMode = config.getNode("debug-mode").getBoolean();
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
        boolean blockWaterSpawns = config.getNode("block-water-spawns").getBoolean();
        boolean blockLavaSpawns = config.getNode("block-lava-spawns").getBoolean();
        boolean debugMode = config.getNode("debug-mode").getBoolean();

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
                plugin.getLogger().info("Invalid spawn: "+spawnCheckEvent.getReason());
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
            if (!new Location(world, x, i, z).getBlock().getType().isAir()) {
                if (config.getNode("debug-mode").getBoolean())
                    plugin.getLogger().info(Integer.toString(i));
                return i;
            }
            i--;
        }
        return i;
    }


}
