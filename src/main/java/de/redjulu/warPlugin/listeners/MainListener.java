package de.redjulu.warPlugin.listeners;

import de.redjulu.warPlugin.ScoreboardClass;
import de.redjulu.warPlugin.TablistClass;
import de.redjulu.warPlugin.WarPlugin;
import de.redjulu.warPlugin.utils.RankUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MainListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        var player = e.getPlayer();

        ScoreboardClass.createScoreboard(player);
        TablistClass.updateTablist();

        var plugin = WarPlugin.getInstance();

        plugin.money.computeIfAbsent(player.getUniqueId(), k -> 0);
        plugin.ranks.computeIfAbsent(player.getUniqueId(), k -> 0);
        plugin.modify.computeIfAbsent(player.getUniqueId(), k -> false);
        boolean vanished = plugin.vanish.getOrDefault(player.getUniqueId(), false);

        if (vanished) {
            e.setJoinMessage(null);
        } else {
            plugin.vanish.putIfAbsent(player.getUniqueId(), false);
            e.setJoinMessage("§7§l[§a§l+§7§l] " + RankUtils.getRankColour(player) + player.getName());
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        var player = e.getPlayer();
        var plugin = WarPlugin.getInstance();

        Bukkit.getScheduler().runTaskLater(plugin, TablistClass::updateTablist, 1L);

        boolean vanished = plugin.vanish.getOrDefault(player.getUniqueId(), false);

        if (vanished) {
            e.setQuitMessage(null);
        } else {
            e.setQuitMessage("§7§l[§c§l-§7§l] " + RankUtils.getRankColour(player) + player.getName());
        }
    }

}
