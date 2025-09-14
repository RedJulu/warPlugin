package de.redjulu.warPlugin.commands;

import de.redjulu.warPlugin.WarPlugin;
import de.redjulu.warPlugin.utils.RankUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VanishCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command cmd,
                             @NotNull String label,
                             @NotNull String[] args) {

        if (!(sender instanceof Player p)) return false;

        if (RankUtils.getRank(p) != 2) {
            p.sendMessage("§cDazu hast du keine Rechte!");
            return false;
        }

        if (args.length == 0) {
            toggleVanish(p, p);
            return true;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                p.sendMessage("§cDer Spieler ist nicht online!");
                return false;
            }
            toggleVanish(p, target);
            return true;
        }

        return false;
    }

    private void toggleVanish(Player executor, Player target) {
        boolean vanished = WarPlugin.getInstance().vanish.computeIfAbsent(target.getUniqueId(), k -> false);
        boolean newState = !vanished;

        WarPlugin.getInstance().vanish.put(target.getUniqueId(), newState);

        String stateMsg = newState ? "§6 im §bVanish§6!" : "§6 nicht mehr im §bVanish§6!";

        if (executor.equals(target)) {
            executor.sendMessage("§6Du bist nun" + stateMsg);
        } else {
            executor.sendMessage("§6Der Spieler ist nun" + stateMsg);
            target.sendMessage("§6Du wurdest von " + RankUtils.getRankColour(executor) + executor.getName() + stateMsg);
        }

        handleVanish(target);
    }

    private void handleVanish(Player p) {
        boolean vanished = WarPlugin.getInstance().vanish.getOrDefault(p.getUniqueId(), false);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (RankUtils.getRank(onlinePlayer) == 2) {
                onlinePlayer.showPlayer(p);
                continue;
            }
            if (vanished) {
                onlinePlayer.hidePlayer(p);
            } else {
                onlinePlayer.showPlayer(p);
            }
        }
    }
}
