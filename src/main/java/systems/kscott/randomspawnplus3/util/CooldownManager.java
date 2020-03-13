package systems.kscott.randomspawnplus3.util;

import org.bukkit.entity.Player;
import systems.kscott.randomspawnplus3.RandomSpawnPlus;
import systems.kscott.randomspawnplus3.exceptions.NoCooldownException;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CooldownManager {
    public static HashMap<String, Long> cooldowns = new HashMap<>();

    public static void addCooldown(Player p) {
        int cooldown = RandomSpawnPlus.getInstance().getRootConfig().getNode("wild-cooldown").getInt();
        long now = Instant.now().toEpochMilli();
        long future = now + TimeUnit.SECONDS.toMillis(cooldown);
        cooldowns.put(p.getUniqueId().toString(), future);
    }

    public static long getCooldown(Player p) throws NoCooldownException {
        if (!cooldowns.containsKey(p.getUniqueId().toString())) {
            throw new NoCooldownException();
        }
        return cooldowns.get(p.getUniqueId().toString());
    }
}
