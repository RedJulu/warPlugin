package de.redjulu.warPlugin.commands;

import de.redjulu.warPlugin.utils.EconomyUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BankCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command cmd,
                             @NotNull String label,
                             @NotNull String[] args) {

        if (!(sender instanceof Player p)) return false;

        if (args.length == 1 && args[0].equalsIgnoreCase("einzahlen")) {
            int total = 0;

            for (ItemStack item : p.getInventory().getContents()) {
                if (item == null || item.getType() != Material.PAPER || !item.hasItemMeta()) continue;

                ItemMeta meta = item.getItemMeta();
                if (!meta.hasDisplayName() || !meta.getDisplayName().equals(ChatColor.GREEN + "Banknote")) continue;

                List<String> lore = meta.getLore();
                if (lore == null || lore.isEmpty()) continue;

                int amount;
                try {
                    String raw = ChatColor.stripColor(lore.get(lore.size() - 1));
                    if (raw.contains(":")) raw = raw.split(":")[1];
                    amount = Integer.parseInt(raw.trim());
                } catch (NumberFormatException e) {
                    continue;
                }

                total += amount * item.getAmount();
                item.setAmount(0);
            }

            if (total == 0) {
                p.sendMessage(ChatColor.RED + "Du hast keine Banknoten im Inventar!");
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return true;
            }

            EconomyUtils.addMoney(p, total);
            p.sendMessage(ChatColor.GREEN + "Du hast " + ChatColor.YELLOW + total + " Münzen " + ChatColor.GREEN + "eingezahlt!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("auszahlen")) {
            int amount;
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                p.sendMessage(ChatColor.RED + "Bitte einen gültigen Betrag angeben!");
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return true;
            }

            if (EconomyUtils.getMoney(p) < amount) {
                p.sendMessage(ChatColor.RED + "Du hast nicht genug Münzen!");
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return true;
            }

            if(amount == 0) {
                p.sendMessage(ChatColor.RED + "Du musst einen gültigen Betrag angeben!");
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return true;
            }

            EconomyUtils.removeMoney(p, amount);

            ItemStack note = new ItemStack(Material.PAPER);
            ItemMeta meta = note.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "Banknote");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.LIGHT_PURPLE + "Wert: " + amount);
            meta.setLore(lore);
            note.setItemMeta(meta);

            p.getInventory().addItem(note);
            p.sendMessage(ChatColor.GREEN + "Du hast eine Banknote im Wert von " + ChatColor.YELLOW + amount + " Münzen " + ChatColor.GREEN + "erhalten!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
            return true;
        }

        sendInfo(p);
        return true;
    }

    private void sendInfo(Player p) {
        p.sendMessage(ChatColor.YELLOW + "------ Bank ------");
        p.sendMessage(ChatColor.GRAY + "Kontostand: " + ChatColor.YELLOW + EconomyUtils.getMoney(p));
        p.sendMessage(ChatColor.GRAY + "/bank einzahlen  (alle Banknoten einzahlen)");
        p.sendMessage(ChatColor.GRAY + "/bank auszahlen <Betrag>");
        p.sendMessage(ChatColor.YELLOW + "-----------------");
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String alias,
                                      @NotNull String[] args) {

        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            if ("einzahlen".startsWith(args[0].toLowerCase())) options.add("einzahlen");
            if ("auszahlen".startsWith(args[0].toLowerCase())) options.add("auszahlen");
            return options;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("auszahlen")) {
            List<String> notes = List.of("5", "10", "20", "50", "100", "200", "500");
            List<String> suggestions = new ArrayList<>();
            for (String note : notes) {
                if (note.startsWith(args[1])) suggestions.add(note);
            }
            return suggestions;
        }

        return Collections.emptyList();
    }
}
