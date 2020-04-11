package systems.kscott.randomspawnplus3.listeners;

import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import systems.kscott.randomspawnplus3.RandomSpawnPlus;
import systems.kscott.randomspawnplus3.events.RandomSpawnEvent;
import systems.kscott.randomspawnplus3.events.SpawnType;
import systems.kscott.randomspawnplus3.spawn.SpawnFinder;

public class RSPFirstJoinListener implements Listener {

    private RandomSpawnPlus plugin;
    private FileConfiguration config;

    public RSPFirstJoinListener(RandomSpawnPlus plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void firstJoinHandler(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (config.getBoolean("randomspawn-enabled")) {
            if (config.getBoolean("on-first-join")) {
                if (RSPLoginListener.firstJoinPlayers.contains(player.getName())) {
                    if (config.getBoolean("use-permission-node") && !player.hasPermission("randomspawnplus.randomspawn")) {
                        RSPLoginListener.firstJoinPlayers.remove(player.getName());
                        return;
                    } else {
                        Location spawnLoc = SpawnFinder.getInstance().findSpawn(true);
                        if (config.getBoolean("essentials-home-on-first-spawn")) {
                            User user = plugin.getEssentials().getUser(player);
                            user.setHome("home", spawnLoc);
                            user.save();
                        }
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                RandomSpawnEvent randomSpawnEvent = new RandomSpawnEvent(spawnLoc, player, SpawnType.FIRST_JOIN);

                                Bukkit.getServer().getPluginManager().callEvent(randomSpawnEvent);
                                player.teleport(spawnLoc.add(0.5, 0, 0.5));

                            }
                        }.runTaskLater(plugin, 3);
                    }
                    RSPLoginListener.firstJoinPlayers.remove(player.getName());
                }
            }
        }
    }
}