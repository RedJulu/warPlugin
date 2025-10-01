package de.redjulu.warPlugin.commands.team;

import de.redjulu.warPlugin.ScoreboardClass;
import de.redjulu.warPlugin.TablistClass;
import de.redjulu.warPlugin.WarPlugin;
import de.redjulu.warPlugin.gui.RankGUI;
import de.redjulu.warPlugin.utils.RankUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RankCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        if (!(sender instanceof Player p)) return false;

        // /rank
        if (args.length == 0) {
            int rank = RankUtils.getRank(p);

            // NEUE LOGIK: Organisation abrufen
            // Standard ist "§8[§c✖§8]" (No Org), wenn der Spieler keinen Org-Eintrag hat.
            String orgPrefix = WarPlugin.getInstance().org.getOrDefault(p.getUniqueId(), "§8[§c✖§8]");

            // Nachricht mit Rang UND Organisation senden
            p.sendMessage("§aDein Rang: " + orgPrefix + "§8| " + RankUtils.getRankColour(p) + RankUtils.getRankName(p));
            return true;
        }

        // /rank <player>
        if (args.length == 1) {

            if (RankUtils.getRank(p) < 2 && !p.getName().equalsIgnoreCase("RedJulu")) {
                p.sendMessage("§cDazu hast du keine Rechte!");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                p.sendMessage("§cSpieler nicht online!");
                return true;
            }

            if(target == Bukkit.getPlayer("RedJulu")) {
                p.sendMessage("§cFinger weg!");
                return true;
            }

            new RankGUI(p, target).open();
            return true;
        }

        return false;
    }
}