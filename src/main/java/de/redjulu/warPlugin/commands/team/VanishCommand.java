package de.redjulu.warPlugin.commands.team;

import de.redjulu.warPlugin.ScoreboardClass;
import de.redjulu.warPlugin.TablistClass;
import de.redjulu.warPlugin.WarPlugin;
import de.redjulu.warPlugin.utils.RankUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class VanishCommand implements CommandExecutor, TabCompleter, Listener {

    private final WarPlugin plugin;

    public VanishCommand(WarPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können diesen Command nutzen!");
            return true;
        }

        if (args.length == 0) {
            toggleVanish(player, player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "pickup" -> {
                boolean current = plugin.vanishPickup.getOrDefault(player.getUniqueId(), true);
                plugin.vanishPickup.put(player.getUniqueId(), !current);
                player.sendMessage("§7Pickup: " + (!current ? "§aAktiv" : "§cDeaktiviert"));
            }
            default -> {
                if (RankUtils.getRank(player) < 2) {
                    player.sendMessage("§cNur Admins können andere Spieler vanishen!");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage("§cSpieler nicht gefunden!");
                    return true;
                }

                toggleVanish(player, target);
            }
        }
        return true;
    }

    private void toggleVanish(Player executor, Player target) {
        boolean vanished = plugin.vanish.getOrDefault(target.getUniqueId(), false);
        plugin.vanish.put(target.getUniqueId(), !vanished);

        if (executor.equals(target)) {
            executor.sendMessage("§7Vanish: " + (vanished ? "§cAus" : "§aAn"));
        } else {
            executor.sendMessage("§7Du hast §e" + target.getName() + " §7Vanish " + (vanished ? "§cAus" : "§aAn") + " §7gesetzt.");
            target.sendMessage("§7Dein Vanish wurde von §e" + executor.getName() + " §7" + (vanished ? "§cDeaktiviert" : "§aAktiviert"));
        }

        TablistClass.updateTablist();
        ScoreboardClass.updateScoreboard(executor);
        ScoreboardClass.updateScoreboard(target);
    }

    // ---- Pickup Event ----

    @EventHandler
    public void onPickup(PlayerAttemptPickupItemEvent e) {
        Player p = e.getPlayer();
        if (plugin.vanish.getOrDefault(p.getUniqueId(), false)) {
            if (!plugin.vanishPickup.getOrDefault(p.getUniqueId(), true)) {
                e.setCancelled(true);
            }
        }
    }

    // ---- TabComplete ----

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();

            if ("pickup".startsWith(input)) list.add("pickup");

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(input)) {
                    list.add(p.getName());
                }
            }
        }

        return list;
    }
}
