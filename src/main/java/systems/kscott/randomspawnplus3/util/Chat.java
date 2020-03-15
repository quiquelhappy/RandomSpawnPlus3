package systems.kscott.randomspawnplus3.util;

import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Chat {

    @Setter
    private static FileConfiguration lang;

    public static void sendToSender(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendToSender(CommandSender sender, String message, HashMap<String, String> placeholders) {
        for (String key : placeholders.keySet()) {
            message = message.replace(key, placeholders.get(key));
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void send(Player sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void send(Player sender, String message, HashMap<String, String> placeholders) {
        for (String key : placeholders.keySet()) {
            message = message.replace(key, placeholders.get(key));
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static String get(String key) {
        return ChatColor.translateAlternateColorCodes('&', lang.getString(key));
    }
}
