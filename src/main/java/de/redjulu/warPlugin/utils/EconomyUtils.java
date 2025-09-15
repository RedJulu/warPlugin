package de.redjulu.warPlugin.utils;

import de.redjulu.warPlugin.ScoreboardClass;
import de.redjulu.warPlugin.WarPlugin;
import org.bukkit.entity.Player;

public class EconomyUtils {

    public static int getMoney(Player player) {
        return WarPlugin.getInstance().money.getOrDefault(player.getUniqueId(), 0);

    }

    public static void setMoney(Player player, int money) {
        WarPlugin.getInstance().money.put(player.getUniqueId(), money);
        WarPlugin.getInstance().saveMap(WarPlugin.getInstance().moneyFile, WarPlugin.getInstance().money);
        ScoreboardClass.updateScoreboard(player);
    }

    public static void addMoney(Player player, int money) {
        setMoney(player, getMoney(player) + money);
        ScoreboardClass.updateScoreboard(player);
    }

    public static void removeMoney(Player player, int money) {
        setMoney(player, Math.max(0, getMoney(player) - money));
        ScoreboardClass.updateScoreboard(player);
    }

    public static boolean hasEnoughMoney(Player player, int money) {
        return getMoney(player) >= money;
    }
}
