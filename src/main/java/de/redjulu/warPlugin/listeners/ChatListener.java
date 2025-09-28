package de.redjulu.warPlugin.listeners;

import de.redjulu.warPlugin.WarPlugin;
import de.redjulu.warPlugin.utils.RankUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String msg = e.getMessage();

        // Teamchat
        if (msg.startsWith("#")) {
            msg = msg.substring(1); // # entfernen
            msg = ChatColor.translateAlternateColorCodes('&', msg); // Farben übersetzen
            e.setCancelled(true);

            String format = "§3§l[Team] " + RankUtils.getRankColour(player) + player.getName() + " §r" + msg;
            sendTeamMessage(player, format);
            return;
        }

        // Normales Chatformat
        msg = ChatColor.translateAlternateColorCodes('&', msg); // Farben übersetzen
        String format = RankUtils.getRankColour(player) + RankUtils.getRankName(player) + " §8| " +
                RankUtils.getRankColour(player) + player.getName() + " §8>> §r" + msg;
        e.setFormat(format);
    }

    private void sendTeamMessage(Player sender, String msg) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (RankUtils.getRank(onlinePlayer) >= 2) { // Teamchat für Admins/VIPs
                onlinePlayer.sendMessage(msg);
            }
        }
    }
}
