package systems.kscott.randomspawnplus3.util;

import org.bukkit.block.Block;

public class Blocks {

    public static boolean isEmpty(Block block) {
        return (block.isEmpty() || block.getType().isAir());
    }
}
