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

        if(!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        if(!(RankUtils.getRank(p) == 2)) {
            p.sendMessage("§cDazu hast du keine Rechte!");
            return false;
        }

        if(args.length == 0) {
            if(WarPlugin.getInstance().vanish.containsKey(p.getUniqueId())) {
                if(WarPlugin.getInstance().vanish.get(p.getUniqueId())) {
                    p.sendMessage("§6Du bist nun im §bVanish§6!");
                    WarPlugin.getInstance().vanish.put(p.getUniqueId(), false);
                } else {
                    p.sendMessage("§6Du bist nun nicht mehr im §bVanish§6!");
                    WarPlugin.getInstance().vanish.put(p.getUniqueId(), true);
                }
                handleVanish(p);
                return true;
            }
            p.kickPlayer("§cInterner Fehler. Bitte wieder rejoinen.");
            return false;

        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if(target == null) {
                p.sendMessage("§cDer Spieler ist nicht online!");
                return false;
            }


            if(WarPlugin.getInstance().vanish.get(target.getUniqueId())) {
                p.sendMessage("§6Der Spieler ist nun nicht mehr im §bVanish§6!");
                target.sendMessage("§6Du wurdest von " + RankUtils.getRankColour(p) + p.getName() + "§6 aus dem §bVanish§6 gesetzt!");
                WarPlugin.getInstance().vanish.put(target.getUniqueId(), false);
            } else {
                p.sendMessage("§6Der Spieler ist nun im §bVanish§6!");
                target.sendMessage("§6Du wurdest von " + RankUtils.getRankColour(p) + p.getName() + "§6 in den §bVanish§6 gesetzt!");
                WarPlugin.getInstance().vanish.put(target.getUniqueId(), true);
            }
            handleVanish(target);
            return true;

        }


        return false;
    }

    public void handleVanish(Player p) {

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(RankUtils.getRank(onlinePlayer) == 2){
                return;
            }
            onlinePlayer.hidePlayer(p);
        }

    }
}
