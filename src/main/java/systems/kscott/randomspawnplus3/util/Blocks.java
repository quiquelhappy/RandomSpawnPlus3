package systems.kscott.randomspawnplus3.util;

import org.bukkit.block.Block;

public class Blocks {

    public static boolean isEmpty(Block block) {
        if (XMaterial.isOneEight()) {
            return (block.getType() == XMaterial.AIR.parseMaterial() || block.getType() == XMaterial.VOID_AIR.parseMaterial() || block.getType() == XMaterial.CAVE_AIR.parseMaterial());
        }
        return (block.isEmpty() || block.getType().isAir());
    }
}
