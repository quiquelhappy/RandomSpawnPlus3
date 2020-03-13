package systems.kscott.randomspawnplus3.listeners;

import com.earth2me.essentials.User;
import ninja.leaping.configurate.ConfigurationNode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import systems.kscott.randomspawnplus3.RandomSpawnPlus;
import systems.kscott.randomspawnplus3.spawn.SpawnFinder;

public class RSPFirstJoinListener implements Listener {

    private RandomSpawnPlus plugin;
    private ConfigurationNode config;

    public RSPFirstJoinListener(RandomSpawnPlus plugin) {
        this.plugin = plugin;
        this.config = plugin.getRootConfig();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void firstJoinHandler(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (config.getNode("randomspawn-enabled").getBoolean()) {
            if (config.getNode("on-first-join").getBoolean()) {
                if (RSPLoginListener.firstJoinPlayers.contains(player.getName())) {
                    if (config.getNode("use-permission-node").getBoolean() && !player.hasPermission("randomspawnplus.randomspawn")) {
                        RSPLoginListener.firstJoinPlayers.remove(player.getName());
                        return;
                    } else {
                        Location spawnLoc = SpawnFinder.getInstance().findSpawn(true);
                        if (config.getNode("essentials-home-on-first-spawn").getBoolean()) {
                            User user = plugin.getEssentials().getUser(player);
                            user.setHome("home", spawnLoc);
                            user.save();
                        }
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.teleport(spawnLoc.toCenterLocation().subtract(0, 0.5, 0));

                            }
                        }.runTaskLater(plugin, 3);
                    }
                    RSPLoginListener.firstJoinPlayers.remove(player.getName());
                }
            }
        }
    }
}