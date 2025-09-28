package de.redjulu.warPlugin.commands.team;

import de.redjulu.warPlugin.utils.EconomyUtils;
import de.redjulu.warPlugin.utils.RankUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoneyCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command cmd,
                             @NotNull String label,
                             @NotNull String[] args) {

        if (!(sender instanceof Player p)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl benutzen!");
            return true;
        }

        int rank = RankUtils.getRank(p);

        // /money -> Kontostand
        if (args.length == 0) {
            p.sendMessage("§aDein Kontostand: §e" + EconomyUtils.getMoney(p) + " §aMünzen.");
            return true;
        }

        // Admin Commands
        if (rank < 2) {
            p.sendMessage("§cDafür hast du keine Rechte!");
            return true;
        }

        // /money <Spieler>
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                p.sendMessage("§cSpieler nicht gefunden!");
                return true;
            }
            p.sendMessage("§aKontostand von " + RankUtils.getRankColour(target) + target.getName() + "§a: §e" + EconomyUtils.getMoney(target) + " §aMünzen.");
            return true;
        }

        // /money <set|add|remove> <Spieler|@a> <Betrag>
        if (args.length == 3) {
            String action = args[0].toLowerCase();
            String targetArg = args[1];

            int amount;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                p.sendMessage("§cBitte gib eine Zahl an!");
                return true;
            }

            // alle Spieler
            if (targetArg.equalsIgnoreCase("@a")) {
                switch (action) {
                    case "set" -> {
                        for (Player online : Bukkit.getOnlinePlayers()) {
                            EconomyUtils.setMoney(online, amount);
                            online.sendMessage("§aDein Kontostand wurde von " + RankUtils.getRankColour(p) + p.getName() + " §aauf §e" + amount + " §aMünzen gesetzt.");
                        }
                        p.sendMessage("§aDu hast den Kontostand von allen auf §e" + amount + " §aMünzen gesetzt.");
                    }
                    case "add" -> {
                        for (Player online : Bukkit.getOnlinePlayers()) {
                            EconomyUtils.addMoney(online, amount);
                            online.sendMessage("§aDir wurden von " + RankUtils.getRankColour(p) + p.getName() + " §e" + amount + " §aMünzen hinzugefügt.");
                        }
                        p.sendMessage("§aDu hast allen §e" + amount + " §aMünzen hinzugefügt.");
                    }
                    case "remove" -> {
                        for (Player online : Bukkit.getOnlinePlayers()) {
                            EconomyUtils.removeMoney(online, amount);
                            online.sendMessage("§aDir wurden von " + RankUtils.getRankColour(p) + p.getName() + " §e" + amount + " §aMünzen abgezogen.");
                        }
                        p.sendMessage("§aDu hast allen §e" + amount + " §aMünzen abgezogen.");
                    }
                    default -> p.sendMessage("§cBenutze: /money [set|add|remove] <Spieler|@a> <Betrag>");
                }
                return true;
            }

            // einzelner Spieler
            Player target = Bukkit.getPlayer(targetArg);
            if (target == null) {
                p.sendMessage("§cSpieler nicht gefunden!");
                return true;
            }

            switch (action) {
                case "set" -> {
                    EconomyUtils.setMoney(target, amount);
                    p.sendMessage("§aDu hast den Kontostand von " + RankUtils.getRankColour(target) + target.getName() + " §aauf §e" + amount + " §aMünzen gesetzt.");
                    target.sendMessage("§aDein Kontostand wurde von " + RankUtils.getRankColour(p) + p.getName() + " §aauf §e" + amount + " §aMünzen gesetzt.");
                }
                case "add" -> {
                    EconomyUtils.addMoney(target, amount);
                    p.sendMessage("§aDu hast " + RankUtils.getRankColour(target) + target.getName() + " §e" + amount + " §aMünzen hinzugefügt.");
                    target.sendMessage("§aDir wurden von " + RankUtils.getRankColour(p) + p.getName() + " §e" + amount + " §aMünzen hinzugefügt.");
                }
                case "remove" -> {
                    EconomyUtils.removeMoney(target, amount);
                    p.sendMessage("§aDu hast " + RankUtils.getRankColour(target) + target.getName() + " §e" + amount + " §aMünzen abgezogen.");
                    target.sendMessage("§aDir wurden von " + RankUtils.getRankColour(p) + p.getName() + " §e" + amount + " §aMünzen abgezogen.");
                }
                default -> p.sendMessage("§cBenutze: /money [set|add|remove] <Spieler|@a> <Betrag>");
            }
            return true;
        }

        p.sendMessage("§cBenutze: /money [set|add|remove] <Spieler|@a> <Betrag>");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command cmd,
                                                @NotNull String alias,
                                                @NotNull String[] args) {

        if (!(sender instanceof Player p)) return null;
        int rank = RankUtils.getRank(p);

        List<String> completions = new ArrayList<>();

        if (args.length == 1 && rank >= 2) {
            List<String> actions = Arrays.asList("set", "add", "remove");
            for (String action : actions) {
                if (action.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(action);
                }
            }
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(online.getName());
                }
            }
        } else if (args.length == 2 && rank >= 2) {
            if (args[0].equalsIgnoreCase("set") ||
                    args[0].equalsIgnoreCase("add") ||
                    args[0].equalsIgnoreCase("remove")) {


                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (online.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(online.getName());
                    }
                }
            }
        } else if (args.length == 3 && rank >= 2) {
            if ("<Betrag>".startsWith(args[2])) completions.add("<Betrag>");
        }

        return completions;
    }
}
