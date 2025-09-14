package de.redjulu.warPlugin.utils;

import de.redjulu.warPlugin.WarPlugin;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class RankUtils {
    private static final Map<UUID, Integer> ranks = WarPlugin.getInstance().ranks;

    public static String getRankColour(Player player) {
        if(ranks.getOrDefault(player.getUniqueId(), 0) == 1) {
            return "§6";
        }
        if(ranks.getOrDefault(player.getUniqueId(), 0) == 2) {
            return "§c";
        }
        return "§7";
    }

    public static String getRankName(Player player) {
        if(ranks.getOrDefault(player.getUniqueId(), 0) == 1) {
            return "VIP";
        }
        if(ranks.getOrDefault(player.getUniqueId(), 0) == 2) {
            return "Admin";
        }
        return "Spieler";
    }

    public static Integer getRank(Player player) {
        if(ranks.getOrDefault(player.getUniqueId(), 0) == 1) {
            return 1;
        }
        if(ranks.getOrDefault(player.getUniqueId(), 0) == 2) {
            return 2;
        }
        return 0;
    }
}
