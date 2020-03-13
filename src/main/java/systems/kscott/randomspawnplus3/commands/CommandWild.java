package systems.kscott.randomspawnplus3.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import systems.kscott.randomspawnplus3.RandomSpawnPlus;
import systems.kscott.randomspawnplus3.exceptions.NoCooldownException;
import systems.kscott.randomspawnplus3.spawn.SpawnFinder;
import systems.kscott.randomspawnplus3.util.Chat;
import systems.kscott.randomspawnplus3.util.CooldownManager;

import java.time.Instant;
import java.util.HashMap;

@CommandAlias("wild|rtp")
@Description("Teleport to a random location")
public class CommandWild extends BaseCommand {

    private RandomSpawnPlus plugin;

    public CommandWild(RandomSpawnPlus plugin) {
        this.plugin = plugin;
    }

    @Default
    @CommandPermission("randomspawnplus.wild")
    public void wildSelf(Player player) {
        if (!player.hasPermission("randomspawnplus.wild.bypasscooldown")) {
            long cooldown = 0;

            try {
                cooldown = CooldownManager.getCooldown(player);
            } catch (NoCooldownException ignored) {

            }

            if ((cooldown - Instant.now().toEpochMilli()) >= 0) {
                if (plugin.getRootConfig().getNode("debug-mode").getBoolean())
                    plugin.getLogger().info(Long.toString(cooldown));

                int seconds = (int) ((cooldown - Instant.now().toEpochMilli()) / 1000) % 60;
                int minutes = (int) (((cooldown - Instant.now().toEpochMilli()) / (1000 * 60)) % 60);
                int hours = (int) (((cooldown - Instant.now().toEpochMilli()) / (1000 * 60 * 60)) % 24);

                HashMap<String, String> placeholders = new HashMap<>();

                placeholders.put("%h", Integer.toString(hours));
                placeholders.put("%m", Integer.toString(minutes));
                placeholders.put("%s", Integer.toString(seconds));

                if (hours != 1) {
                    placeholders.put("%a", "s");
                } else {
                    placeholders.put("%a", "");
                }

                if (minutes != 1) {
                    placeholders.put("%b", "s");
                } else {
                    placeholders.put("%b", "");
                }

                if (seconds != 1) {
                    placeholders.put("%c", "s");
                } else {
                    placeholders.put("%c", "");
                }

                if (hours == 0) {
                    if (minutes == 0) {
                        Chat.sendToSender(player, plugin.getRootLang().getNode("wild-tp-cooldown-seconds").getString(), placeholders);
                    } else {
                        Chat.sendToSender(player, plugin.getRootLang().getNode("wild-tp-cooldown-minutes").getString(), placeholders);
                    }
                } else {
                    Chat.sendToSender(player, plugin.getRootLang().getNode("wild-tp-cooldown").getString(), placeholders);
                }

            }
            return;
        }

        Location location = SpawnFinder.getInstance().findSpawn(true);

        HashMap<String, String> placeholders = new HashMap<String, String>();
        placeholders.put("%x", Integer.toString(location.getBlockX()));
        placeholders.put("%y", Integer.toString(location.getBlockY()));
        placeholders.put("%z", Integer.toString(location.getBlockZ()));
        Chat.sendToSender(player, plugin.getRootLang().getNode("wild-tp").getString(), placeholders);

        player.teleport(location.toCenterLocation().subtract(0, 0.5, 0));
        CooldownManager.addCooldown(player);
    }

    @Default
    @CommandPermission("randomspawnplus.wild.others")
    public void wildOther(Player player, String other) {
        Location location = SpawnFinder.getInstance().findSpawn(true);

        Player otherPlayer = Bukkit.getPlayer(other);

        if (otherPlayer == null) {
            Chat.sendToSender(player, plugin.getRootLang().getNode("wild-tp-doesnt-exist").getString());
            return;
        }

        HashMap<String, String> placeholders = new HashMap<String, String>();
        placeholders.put("%x", Integer.toString(location.getBlockX()));
        placeholders.put("%y", Integer.toString(location.getBlockY()));
        placeholders.put("%z", Integer.toString(location.getBlockZ()));
        Chat.sendToSender(otherPlayer, plugin.getRootLang().getNode("wild-tp").getString(), placeholders);

        placeholders = new HashMap<String, String>();
        placeholders.put("%player", otherPlayer.getName());
        Chat.sendToSender(player, plugin.getRootLang().getNode("wild-tp-other").getString(), placeholders);

        otherPlayer.teleport(location);
    }
}
