package de.redjulu.warPlugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import de.redjulu.warPlugin.utils.RankUtils;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TablistClass {

    public static void updateTablist() {
        var plugin = WarPlugin.getInstance();
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        Runtime runtime = Runtime.getRuntime();

        for (Player viewer : Bukkit.getOnlinePlayers()) {
            boolean isAdmin = RankUtils.getRank(viewer) >= 2;

            long onlineCount = Bukkit.getOnlinePlayers().stream()
                    .filter(p -> !plugin.vanish.getOrDefault(p.getUniqueId(), false))
                    .count();

            double tps = Bukkit.getServer().getTPS()[0];

            String header = "§a§lMinecraft §6§lWar" +
                    "\n§3Online: §e" + onlineCount +
                    " §3| TPS: §e" + String.format("%.1f", tps);

            if (isAdmin) {
                double usedMemGb = (runtime.totalMemory() - runtime.freeMemory()) / 1024.0 / 1024.0 / 1024.0;
                double maxMemGb = runtime.maxMemory() / 1024.0 / 1024.0 / 1024.0;
                double cpuLoad = getProcessCpuLoad(osBean);

                header += "\n§3RAM: §e" + String.format("%.1f", usedMemGb) + "GB/§e" + String.format("%.1f", maxMemGb) + "GB §3| CPU: §e" + String.format("%.1f%%", cpuLoad * 100);
            }

            String footer = "\n§7RedJulu & Nicofrit";

            viewer.setPlayerListHeader(header);
            viewer.setPlayerListFooter(footer);

            // Sortierung: Höchster Rang oben, dann alphabetisch
            List<Player> sorted = Bukkit.getOnlinePlayers().stream()
                    .sorted(Comparator.comparingInt((Player p) -> RankUtils.getRank(p)).reversed()
                            .thenComparing(Player::getName))
                    .collect(Collectors.toList());

            for (Player target : sorted) {
                int targetRank = RankUtils.getRank(target);
                String colour = RankUtils.getRankColour(target);
                String name = colour + target.getName();

                boolean vanished = plugin.vanish.getOrDefault(target.getUniqueId(), false);

                // org Map safe holen
                String prefix = plugin.org.get(target.getUniqueId());
                if (prefix == null) prefix = "§8[§c✖§8] ";
                plugin.org.put(target.getUniqueId(), prefix);

                if (vanished) {
                    if (isAdmin || viewer.equals(target)) {
                        viewer.showPlayer(plugin, target);
                        target.setPlayerListName(prefix + name + " §3[V]");
                    } else {
                        viewer.hidePlayer(plugin, target);
                    }
                } else {
                    viewer.showPlayer(plugin, target);
                    target.setPlayerListName(prefix + name);
                }
            }
        }
    }

    // CPU Load als Double (0.0-1.0)
    private static double getProcessCpuLoad(OperatingSystemMXBean osBean) {
        try {
            if (osBean instanceof com.sun.management.OperatingSystemMXBean bean) {
                double load = bean.getProcessCpuLoad();
                return load < 0 ? 0.0 : load;
            }
        } catch (Exception ignored) {}
        return 0.0;
    }

    // Scheduler starten: Live-Update alle 5 Ticks
    public static void startUpdater() {
        var plugin = WarPlugin.getInstance();
        Bukkit.getScheduler().runTaskTimer(plugin, TablistClass::updateTablist, 5L, 5L);
    }
}
