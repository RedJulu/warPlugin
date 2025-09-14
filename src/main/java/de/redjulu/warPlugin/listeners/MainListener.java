package de.redjulu.warPlugin.listeners;

import de.redjulu.warPlugin.WarPlugin;
import de.redjulu.warPlugin.utils.RankUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MainListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!WarPlugin.getInstance().money.containsKey(e.getPlayer().getUniqueId())) {
            WarPlugin.getInstance().money.put(e.getPlayer().getUniqueId(), 0);
        }

        if (!WarPlugin.getInstance().ranks.containsKey(e.getPlayer().getUniqueId())) {
            WarPlugin.getInstance().ranks.put(e.getPlayer().getUniqueId(), 0);
        }

        if(WarPlugin.getInstance().vanish.containsKey(e.getPlayer().getUniqueId())) {
            if(WarPlugin.getInstance().vanish.get(e.getPlayer().getUniqueId())) {
                e.setJoinMessage(null);
                return;
            }
            WarPlugin.getInstance().vanish.put(e.getPlayer().getUniqueId(), false);
            e.setJoinMessage("§7§l[§a§l+§7§l] " + RankUtils.getRankColour(e.getPlayer()) + e.getPlayer().getName());
        }
        e.setJoinMessage("§7§l[§a§l+§7§l] " + RankUtils.getRankColour(e.getPlayer()) + e.getPlayer().getName());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if(WarPlugin.getInstance().vanish.containsKey(e.getPlayer().getUniqueId())) {
            if(WarPlugin.getInstance().vanish.get(e.getPlayer().getUniqueId())) {
                e.setQuitMessage(null);
                return;
            }
            e.setQuitMessage("§7§l[§c§l+§7§l] " + RankUtils.getRankColour(e.getPlayer()) + e.getPlayer().getName());
        }
        e.setQuitMessage("§7§l[§c§l+§7§l] " + RankUtils.getRankColour(e.getPlayer()) + e.getPlayer().getName());
    }

}
