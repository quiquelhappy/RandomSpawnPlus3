package systems.kscott.randomspawnplus3.listeners;

import ninja.leaping.configurate.ConfigurationNode;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import systems.kscott.randomspawnplus3.RandomSpawnPlus;

import java.util.ArrayList;

public class RSPLoginListener implements Listener {

    private ConfigurationNode config;

    public RSPLoginListener(RandomSpawnPlus plugin) {
        this.config = plugin.getRootConfig();
    }


    public static ArrayList<String> firstJoinPlayers = new ArrayList<>();

    @EventHandler
    public void preLoginHandler(AsyncPlayerPreLoginEvent event) {
        if (config.getNode("randomspawn-enabled").getBoolean()) {
            if (config.getNode("on-first-join").getBoolean()) {
                String playerName = event.getName();

                boolean hasPlayed = Bukkit.getServer().getOfflinePlayer(playerName).hasPlayedBefore();

                if (!hasPlayed) {
                    firstJoinPlayers.add(playerName);
                }
            }
        }
    }
}