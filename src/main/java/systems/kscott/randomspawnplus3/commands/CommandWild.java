package systems.kscott.randomspawnplus3.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandContexts;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import systems.kscott.randomspawnplus3.RandomSpawnPlus;
import systems.kscott.randomspawnplus3.events.RandomSpawnEvent;
import systems.kscott.randomspawnplus3.events.SpawnCheckEvent;
import systems.kscott.randomspawnplus3.events.SpawnType;
import systems.kscott.randomspawnplus3.exceptions.FinderTimedOutException;
import systems.kscott.randomspawnplus3.exceptions.NoCooldownException;
import systems.kscott.randomspawnplus3.spawn.SpawnFinder;
import systems.kscott.randomspawnplus3.util.Chat;
import systems.kscott.randomspawnplus3.util.CooldownManager;

import java.time.Instant;
import java.util.HashMap;
import java.util.Objects;

@CommandAlias("wild|rtp")
@Description("Teleport to a random location")
public class CommandWild extends BaseCommand {

    private RandomSpawnPlus plugin;
    private FileConfiguration config;

    public CommandWild(RandomSpawnPlus plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Default
    public void onCommand(CommandSender sender, @Optional OnlinePlayer otherPlayer) {
        if (Objects.isNull(otherPlayer)) {
            if (!(sender instanceof Player)) {
                Chat.msg(sender, Chat.get("console-cannot-use"));
                return;
            }

            Player player = (Player) sender;

            long cooldown = 0;

            try {
                cooldown = CooldownManager.getCooldown(player);
            } catch (NoCooldownException ignored) {

            }

            if (player.hasPermission("randomspawnplus.wild.bypasscooldown")) {
                cooldown = 0;
            }

            if ((cooldown - Instant.now().toEpochMilli()) >= 0) {
                if (config.getBoolean("debug-mode"))
                    plugin.getLogger().info(Long.toString(cooldown));

                int seconds = (int) ((cooldown - Instant.now().toEpochMilli()) / 1000) % 60;
                int minutes = (int) (((cooldown - Instant.now().toEpochMilli()) / (1000 * 60)) % 60);
                int hours = (int) (((cooldown - Instant.now().toEpochMilli()) / (1000 * 60 * 60)) % 24);

                String message = "";

                if (hours == 0) {
                    if (minutes == 0) {
                        message = Chat.get("wild-tp-cooldown-seconds");
                    } else {
                        message = Chat.get("wild-tp-cooldown-minutes");
                    }
                } else {
                    message = Chat.get("wild-tp-cooldown");
                }

                message = message.replace("%h", Integer.toString(hours));
                message = message.replace("%m", Integer.toString(minutes));
                message = message.replace("%s", Integer.toString(seconds));

                if (hours != 1) {
                    message = message.replace("%a", "s");
                } else {
                    message = message.replace("%a", "");
                }

                if (minutes != 1) {
                    message = message.replace("%b", "s");
                } else {
                    message = message.replace("%b", "");
                }

                if (seconds != 1) {
                    message = message.replace("%c", "s");
                } else {
                    message = message.replace("%c", "");
                }
                Chat.msg(player, message);
                return;
            }

            Location location = null;
            try {
                location = SpawnFinder.
                        getInstance().
                        findSpawn(true);
            } catch (FinderTimedOutException e) {
                Chat.msg(player, Chat.get("error-finding-spawn"));
                return;
            }

            String message = Chat.get("wild-tp")
                    .replace("%x", Integer.toString(location.getBlockX()))
                    .replace("%y", Integer.toString(location.getBlockY()))
                    .replace("%z", Integer.toString(location.getBlockZ()));
            Chat.msg(player, message);

            RandomSpawnEvent randomSpawnEvent = new RandomSpawnEvent(location, player, SpawnType.WILD_COMMAND);

            Bukkit.getServer().getPluginManager().callEvent(randomSpawnEvent);
            player.teleport(location.add(0.5, 0, 0.5));
            CooldownManager.addCooldown(player);
        } else {
            Location location = null;
            try {
                location = SpawnFinder.getInstance().findSpawn(true);
            } catch (FinderTimedOutException e) {
                Chat.msg(otherPlayer.getPlayer(), Chat.get("error-finding-spawn"));
                return;
            }
            String message = Chat.get("wild-tp")
                    .replace("%x", Integer.toString(location.getBlockX()))
                    .replace("%y", Integer.toString(location.getBlockY()))
                    .replace("%z", Integer.toString(location.getBlockZ()));

            Chat.msg(otherPlayer.getPlayer(), message);

            message = Chat.get("wild-tp-other");
            message = message.replace("%player", otherPlayer.getPlayer().getName());
            Chat.msg(sender, message);

            RandomSpawnEvent randomSpawnEvent = new RandomSpawnEvent(location, otherPlayer.getPlayer(), SpawnType.WILD_COMMAND);

            Bukkit.getServer().getPluginManager().callEvent(randomSpawnEvent);
            if (location.isChunkLoaded()) {
                location.getChunk().load();
            }
            otherPlayer.getPlayer().teleport(location.add(0.5, 0, 0.5));
        }
    }

}
