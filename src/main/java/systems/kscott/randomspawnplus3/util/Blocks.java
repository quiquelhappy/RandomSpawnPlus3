package systems.kscott.randomspawnplus3.util;

import org.bukkit.block.Block;

public class Blocks {

    public static boolean isEmpty(Block block) {
        if (XMaterial.getVersion() <= 13) {
            return (block.getType() == Material.AIR.parseMaterial() || block.getType() == XMaterial.VOID_AIR.parseMaterial() || block.getType() == XMaterial.CAVE_AIR.parseMaterial());
        }
        return (block.isEmpty() || block.getType().isAir());
    }
}
