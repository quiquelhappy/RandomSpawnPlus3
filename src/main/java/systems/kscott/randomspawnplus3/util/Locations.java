package systems.kscott.randomspawnplus3.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Locations {
    public static Location deserializeLocationString(String locString) {

        String[] array = locString.split("\\|");

        World world = Bukkit.getWorld(array[0]);

        int x = Integer.parseInt(array[1]);
        int y = Integer.parseInt(array[2]);
        int z = Integer.parseInt(array[3]);

        return new Location(world, x, y, z);
    }

    public static String serializeString(Location location) {
        String world = location.getWorld().getName();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return world+"|"+x+"|"+y+"|"+z;
    }

}
