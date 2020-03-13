package systems.kscott.randomspawnplus3.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.entity.Player;
import systems.kscott.randomspawnplus3.RandomSpawnPlus;
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
        Chat.send(player, "&8[&3RandomSpawnPlus&8] &7Running &bv"+plugin.getDescription().getVersion()+"&7, made with love &a:^)");
        Chat.send(player, "");
        Chat.send(player, "&b/rsp reload &8- &7Reload the plugin configuration.");
        Chat.send(player, "&b/wild &8- &7Randomly teleport yourself.");
        Chat.send(player, "&b/wild <other> &8- &7Randomly teleport another player.");
    }

    @Subcommand("reload")
    public void _reload(Player player) {
        plugin.loadConfig();
        plugin.loadLang();
        Chat.send(player, "&8[&3RandomSpawnPlus&8] &7Reloaded &ball configs&7,&b all langs&7,&b and all spawns&7.");
    }
}
