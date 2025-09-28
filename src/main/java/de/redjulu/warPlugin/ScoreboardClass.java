package de.redjulu.warPlugin;

import de.redjulu.warPlugin.utils.EconomyUtils;
import de.redjulu.warPlugin.utils.RankUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardClass {

    private static final Map<UUID, Long> scoreboardStart = new HashMap<>();

    public static Scoreboard createScoreboard(Player player) {
        var plugin = WarPlugin.getInstance();
        plugin.vanish.computeIfAbsent(player.getUniqueId(), k -> false);
        scoreboardStart.put(player.getUniqueId(), System.currentTimeMillis());

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("scoreboard", "dummy");
        objective.setDisplayName(" §a§lMinecraft §6§lWar");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Einzigartige Entries
        Score p1 = objective.getScore(" ");
        Score rank = objective.getScore("§7▶ §6Rang: " + RankUtils.getRankColour(player) + RankUtils.getRankName(player) + "§r");
        Score v = objective.getScore(plugin.vanish.get(player.getUniqueId()) ? "§7▶ §3[\uD83D\uDC41] Vanish" : "  ");
        Score money = objective.getScore("§7▶ §eMoney: §a" + EconomyUtils.getMoney(player));
        Score p3 = objective.getScore("   ");
        Score placeholder = objective.getScore(getPingLine(player));

        p1.setScore(5);
        rank.setScore(4);
        v.setScore(3);
        money.setScore(2);
        p3.setScore(1);
        placeholder.setScore(0);

        player.setScoreboard(scoreboard);
        return scoreboard;
    }

    public static void updateScoreboard(Player player) {
        var plugin = WarPlugin.getInstance();
        plugin.vanish.computeIfAbsent(player.getUniqueId(), k -> false);

        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective("scoreboard");
        if (objective == null) return;

        // Alte Scores resetten
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        Score p1 = objective.getScore(" ");
        Score rank = objective.getScore("§7▶ §6Rang: " + RankUtils.getRankColour(player) + RankUtils.getRankName(player) + "§r");
        Score v = objective.getScore(plugin.vanish.get(player.getUniqueId()) ? "§7▶ §3[\uD83D\uDC41] Vanish" : "  ");
        Score money = objective.getScore("§7▶ §eMoney: §a" + EconomyUtils.getMoney(player));
        Score p3 = objective.getScore("   ");
        Score placeholder = objective.getScore(getPingLine(player));

        p1.setScore(5);
        rank.setScore(4);
        v.setScore(3);
        money.setScore(2);
        p3.setScore(1);
        placeholder.setScore(0);
    }

    public static void updatePing(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective("scoreboard");
        if (objective == null) return;

        // Alte Ping-Zeilen entfernen (egal ob Animation oder finaler Ping)
        for (String entry : scoreboard.getEntries()) {
            if (entry.contains("Ping") || entry.contains("●") || entry.contains("•")) {
                scoreboard.resetScores(entry);
            }
        }

        Score placeholder = objective.getScore(getPingLine(player));
        placeholder.setScore(0);
    }


    private static String getPingLine(Player player) {
        long start = scoreboardStart.getOrDefault(player.getUniqueId(), System.currentTimeMillis());
        long elapsed = System.currentTimeMillis() - start;

        // Animation für die ersten 30 Sekunden
        if (elapsed < 30_000) {
            String[] frames = {
                    "§7▶ §7• §7• §7•",   // alle Punkte grau
                    "§7▶ §e● §7• §7•",   // linker Punkt aktiv
                    "§7▶ §7• §e● §7•",   // mittlerer Punkt aktiv
                    "§7▶ §7• §7• §e●",   // rechter Punkt aktiv
            };
            int frame = (int) ((elapsed / 500) % frames.length);
            return frames[frame];
        } else {
            return "§7▶ §ePing: " + player.getPing() + "ms";
        }
    }

    public static void startUpdater() {
        var plugin = WarPlugin.getInstance();
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updatePing(player);
            }
        }, 5L, 5L);
    }
}
