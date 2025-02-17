package systems.kscott.randomspawnplus3.listeners;

import com.earth2me.essentials.User;
import io.papermc.lib.PaperLib;
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
import systems.kscott.randomspawnplus3.exceptions.FinderTimedOutException;
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
                        try {
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
                                    PaperLib.teleportAsync(player, spawnLoc.add(0.5, 0, 0.5));

                                }
                            }.runTaskLater(plugin, 3);
                        } catch (FinderTimedOutException e) {
                            plugin.getLogger().warning("The spawn finder failed to find a valid spawn, and has not given " + player.getName() + " a random spawn. If you find this happening a lot, then raise the 'spawn-finder-tries-before-timeout' key in the config.");
                            return;
                        }
                        RSPLoginListener.firstJoinPlayers.remove(player.getName());
                    }
                }
            }
        }
    }
}