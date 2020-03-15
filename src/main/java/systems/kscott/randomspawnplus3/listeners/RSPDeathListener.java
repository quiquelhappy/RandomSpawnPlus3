package systems.kscott.randomspawnplus3.listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import systems.kscott.randomspawnplus3.RandomSpawnPlus;
import systems.kscott.randomspawnplus3.spawn.SpawnFinder;

public class RSPDeathListener implements Listener {

    private RandomSpawnPlus plugin;
    private FileConfiguration config;

    public RSPDeathListener(RandomSpawnPlus plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerRespawnEvent event) {

        Player player = event.getPlayer();

        if (config.getBoolean("randomspawn-enabled")) {
            if (config.getBoolean("on-death")) {
                if (player.isDead()) {
                    if (!config.getBoolean("use-permission-node") || (config.getBoolean("use-permission-node") && player.hasPermission("randomspawnplus.randomspawn"))) {
                        if (config.getBoolean("spawn-at-bed")) {
                            if (player.getBedSpawnLocation() != null) {
                                event.setRespawnLocation(player.getBedSpawnLocation());
                                return;
                            }
                        }
                        event.setRespawnLocation(SpawnFinder.getInstance().findSpawn(true));
                    }
                }
            }
        }
    }
}