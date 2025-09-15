package de.redjulu.warPlugin;

import de.redjulu.warPlugin.utils.EconomyUtils;
import de.redjulu.warPlugin.utils.RankUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardClass {

    public static Scoreboard createScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("scoreboard", "dummy");

        objective.setDisplayName(" §a§lMinecraft §6§lWar");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score p1 = objective.getScore(" ");
        Score rank = objective.getScore("§7▶ §6Rang: " + RankUtils.getRankColour(player) + RankUtils.getRankName(player));
        Score p2 = objective.getScore("  ");
        Score money = objective.getScore("§7▶ §eMoney: §a" + EconomyUtils.getMoney(player));
        Score p3 = objective.getScore("   ");
        Score placeholder = objective.getScore("§7▶ §aPlaceholder xd");

        p1.setScore(5);
        rank.setScore(4);
        p2.setScore(3);
        money.setScore(2);
        p3.setScore(1);
        placeholder.setScore(0);


        player.setScoreboard(scoreboard);
        return scoreboard;
    }

    public static void updateScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective("scoreboard");
        if (objective == null) return;

        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        Score p1 = objective.getScore(" ");
        Score rank = objective.getScore("§7▶ §6Rang: " + RankUtils.getRankColour(player) + RankUtils.getRankName(player));
        Score p2 = objective.getScore("  ");
        Score money = objective.getScore("§7▶ §eMoney: §a" + EconomyUtils.getMoney(player));
        Score p3 = objective.getScore("   ");
        Score placeholder = objective.getScore("§7▶ §aPlaceholder xd");

        p1.setScore(5);
        rank.setScore(4);
        p2.setScore(3);
        money.setScore(2);
        p3.setScore(1);
        placeholder.setScore(0);
    }
}
