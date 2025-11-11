package cc.dreamcode.bans.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageBroadcaster {

    public static void broadcast(String rawMessage,
                                 String target,
                                 String staff,
                                 String type,
                                 String reason,
                                 long expireAt) {

        String formattedExpire = expireAt > 0
                ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(expireAt))
                : "Permanent";

        ChatColor headerColor = type.toLowerCase().contains("mute") ? ChatColor.AQUA : ChatColor.RED;

        ComponentBuilder hover = new ComponentBuilder(type + " Info").color(headerColor)
                .append("\n").reset()
                .append("Powód » ").color(ChatColor.RED).append(reason)
                .append("\n").reset()
                .append("Przez » ").color(ChatColor.YELLOW).append(staff)
                .append("\n").reset()
                .append("Wygasa » ").color(ChatColor.GOLD).append(formattedExpire);

        String parsedMessage = rawMessage
                .replace("{player}", target)
                .replace("{staff}", staff)
                .replace("{bannedBy}", staff)
                .replace("{mutedBy}", staff)
                .replace("{reason}", reason)
                .replace("{banExpire}", formattedExpire)
                .replace("{muteExpire}", formattedExpire);

        TextComponent base = new TextComponent(ChatColor.translateAlternateColorCodes('&', parsedMessage));
        base.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.create()));

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("dream-chat.punish.hover")) {
                TextComponent withHover = new TextComponent(base);
                withHover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.create()));
                p.spigot().sendMessage(withHover);
            } else {
                p.spigot().sendMessage(base);
            }
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.stripColor(base.getText()));
    }
}
