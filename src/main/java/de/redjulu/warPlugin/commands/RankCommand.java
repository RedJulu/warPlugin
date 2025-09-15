package de.redjulu.warPlugin.commands;

import de.redjulu.warPlugin.gui.RankGUI;
import de.redjulu.warPlugin.utils.RankUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RankCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        if (!(sender instanceof Player p)) return false;

        // /rang -> eigenen Rang anzeigen
        if (args.length == 0) {
            p.sendMessage("§aDein Rang: " + RankUtils.getRankColour(p) + RankUtils.getRankName(p) + "§a.");
            return true;
        }

        // /rang <Spieler> -> GUI öffnen (Rang 2 only)
        if (args.length == 1) {
//            if (RankUtils.getRank(p) < 2 ) {
//                p.sendMessage("§cDazu hast du keine Rechte!");
//                return true;
//            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                p.sendMessage("§cSpieler nicht gefunden!");
                return true;
            }

            // GUI öffnen mit Target
            new RankGUI(p, target).open();
            return true;
        }

        p.sendMessage("§cBenutzung: /rang [Spieler]");
        return true;
    }
}
