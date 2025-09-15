package de.redjulu.warPlugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import de.redjulu.warPlugin.WarPlugin;
import de.redjulu.warPlugin.utils.RankUtils;
import de.redjulu.warPlugin.utils.EconomyUtils;

public class TablistClass {

    public static void updateTablist() {
        for (Player viewer : Bukkit.getOnlinePlayers()) {

            boolean isAdmin = RankUtils.getRank(viewer) >= 2;

            // Header + Footer
            String header = "§a§lMinecraft §6§lWar" +
                    "\n§3Online: §e" + Bukkit.getOnlinePlayers().stream().filter(p -> WarPlugin.getInstance().vanish.get(p.getUniqueId())).count() +
                    "\n";
            String footer = "\n" +
                    "§7RedJulu & Nicofrit";

            viewer.setPlayerListHeader(header);
            viewer.setPlayerListFooter(footer);

            for (Player target : Bukkit.getOnlinePlayers()) {
                String name = target.getName();

                if (isAdmin && WarPlugin.getInstance().vanish.getOrDefault(target.getUniqueId(), false)) {
                    name = RankUtils.getRankColour(viewer) + name + " §3[V]";
                }else {
                    name = RankUtils.getRankColour(viewer) + name;
                }

                viewer.setPlayerListName(name); // Bukkit 1.17+
            }
        }
    }
}
