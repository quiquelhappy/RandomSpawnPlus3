package systems.kscott.randomspawnplus3.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.entity.Player;
import systems.kscott.randomspawnplus3.RandomSpawnPlus;
import systems.kscott.randomspawnplus3.spawn.SpawnCacher;
import systems.kscott.randomspawnplus3.util.Chat;

@CommandAlias("rsp|randomspawnplus")
@Description("Manage the plugin")
@CommandPermission("randomspawnplus.manage")
public class CommandRSP extends BaseCommand {

    private RandomSpawnPlus plugin;

    public CommandRSP(RandomSpawnPlus plugin) {
        this.plugin = plugin;
    }

    @Default
    @Subcommand("help|h")
    public void _main(Player player) {
        Chat.msg(player, "&8[&3RandomSpawnPlus&8] &7Running &bv"+plugin.getDescription().getVersion()+"&7, made with love &a:^)");
        Chat.msg(player, "");
        Chat.msg(player, "&b/rsp reload &8- &7Reload the plugin configuration.");
        Chat.msg(player, "&b/wild &8- &7Randomly teleport yourself.");
        Chat.msg(player, "&b/wild <other> &8- &7Randomly teleport another player.");
    }

    @Subcommand("reload")
    public void _reload(Player player) {
        plugin.getConfigManager().reload();
        plugin.getLangManager().reload();
        plugin.getSpawnsManager().reload();
        SpawnCacher.getInstance().cacheSpawns();
        Chat.setLang(plugin.getLangManager().getConfig());
        Chat.msg(player, "&8[&3RandomSpawnPlus&8] &7Reloaded &bconfig.yml&7, &blang.yml&7, and &bspawns.yml&7.");
    }
}
