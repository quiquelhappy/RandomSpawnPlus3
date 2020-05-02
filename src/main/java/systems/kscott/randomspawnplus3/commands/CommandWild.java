package systems.kscott.randomspawnplus3.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import io.papermc.lib.PaperLib;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import systems.kscott.randomspawnplus3.RandomSpawnPlus;
import systems.kscott.randomspawnplus3.events.RandomSpawnEvent;
import systems.kscott.randomspawnplus3.events.SpawnType;
import systems.kscott.randomspawnplus3.exceptions.FinderTimedOutException;
import systems.kscott.randomspawnplus3.spawn.SpawnFinder;
import systems.kscott.randomspawnplus3.util.Chat;
import systems.kscott.randomspawnplus3.util.CooldownManager;

import java.time.Instant;

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
    @CommandPermission("randomspawnplus.wild")
    public void wild(CommandSender sender) {
        if (!(sender instanceof Player)) {
            Chat.msg(sender, Chat.get("console-cannot-use"));
            return;
        }

        Player player = (Player) sender;

        Bukkit.getLogger().info(Boolean.toString(player.hasPermission("randomspawnplus.wild")));

        long cooldown = 0;

        cooldown = CooldownManager.getCooldown(player);


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

        if (plugin.getEconomy() != null && config.getInt("wild-cost") != 0) {
            if (!player.hasPermission("randomspawnplus.wild.bypasscost")) {
                if (plugin.getEconomy().has(player, config.getInt("wild-cost"))) {
                    plugin.getEconomy().withdrawPlayer(player, config.getInt("wild-cost"));
                } else {
                    Chat.msg(player, plugin.getLang().getString("wild-no-money"));
                    return;
                }
            }
        }

        Location location = null;
        try {
            location = SpawnFinder.getInstance().findSpawn(true);
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
        PaperLib.teleportAsync(player, location.add(0.5, 0, 0.5));
        CooldownManager.addCooldown(player);
    }

    @Default
    @CommandPermission("randomspawnplus.wild.others")
    public void wildOther(CommandSender sender, String otherPlayerString) {

        Player otherPlayer = Bukkit.getPlayer(otherPlayerString);

        if (otherPlayer == null) {
            Chat.msg(sender, plugin.getLang().getString("invalid-player"));
            return;
        }

        Location location = null;
        try {
            location = SpawnFinder.getInstance().findSpawn(true);
        } catch (FinderTimedOutException e) {
            Chat.msg(otherPlayer, Chat.get("error-finding-spawn"));
            return;
        }
        String message = Chat.get("wild-tp")
                .replace("%x", Integer.toString(location.getBlockX()))
                .replace("%y", Integer.toString(location.getBlockY()))
                .replace("%z", Integer.toString(location.getBlockZ()));

        Chat.msg(otherPlayer, message);

        message = Chat.get("wild-tp-other");
        message = message.replace("%player", otherPlayer.getName());
        Chat.msg(sender, message);

        RandomSpawnEvent randomSpawnEvent = new RandomSpawnEvent(location, otherPlayer.getPlayer(), SpawnType.WILD_COMMAND);

        Bukkit.getServer().getPluginManager().callEvent(randomSpawnEvent);
        if (location.isChunkLoaded()) {
            location.getChunk().load();
        }
        PaperLib.teleportAsync(otherPlayer, location.add(0.5, 0, 0.5));
    }
}
