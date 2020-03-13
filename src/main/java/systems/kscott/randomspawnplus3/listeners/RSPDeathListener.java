package systems.kscott.randomspawnplus3.listeners;

import ninja.leaping.configurate.ConfigurationNode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import systems.kscott.randomspawnplus3.RandomSpawnPlus;
import systems.kscott.randomspawnplus3.spawn.SpawnFinder;

public class RSPDeathListener implements Listener {

    private RandomSpawnPlus plugin;
    private ConfigurationNode config;

    public RSPDeathListener(RandomSpawnPlus plugin) {
        this.plugin = plugin;
        this.config = plugin.getRootConfig();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerRespawnEvent event) {

        Player player = event.getPlayer();

        if (config.getNode("randomspawn-enabled").getBoolean()) {
            if (config.getNode("on-death").getBoolean()) {
                if (player.isDead()) {
                    if (!config.getNode("use-permission-node").getBoolean() || (config.getNode("use-permission-node").getBoolean() && player.hasPermission("randomspawnplus.randomspawn"))) {
                        if (config.getNode("spawn-at-bed").getBoolean()) {
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