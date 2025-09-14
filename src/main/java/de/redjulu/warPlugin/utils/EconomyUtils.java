package de.redjulu.warPlugin.utils;

import de.redjulu.warPlugin.WarPlugin;
import org.bukkit.entity.Player;

public class EconomyUtils {

    public static Integer getMoney(Player player) {
        return WarPlugin.getInstance().money.getOrDefault(player.getUniqueId(), 0);
    }

    public static void setMoney(Player player, Integer money) {
        WarPlugin.getInstance().money.put(player.getUniqueId(), money);
        return;
    }
    public static void addMoney(Player player, Integer money) {
        WarPlugin.getInstance().money.put(player.getUniqueId(), getMoney(player) + money);
        return;
    }
    public static void removeMoney(Player player, Integer money) {
        WarPlugin.getInstance().money.put(player.getUniqueId(), getMoney(player) - money);
        return;
    }
    public static boolean hasEnoughMoney(Player player, Integer money) {
        return getMoney(player) >= money;
    }
}
