package de.redjulu.warPlugin.commands;

import de.redjulu.warPlugin.utils.EconomyUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BankCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command cmd,
                             @NotNull String label,
                             @NotNull String[] args) {

        if (!(sender instanceof Player p)) return false;

        // temporärer create-befehl
        if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
            int value;
            try {
                value = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                p.sendMessage(ChatColor.RED + "Bitte eine gültige Zahl angeben!");
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return true;
            }

            ItemStack note = new ItemStack(Material.PAPER);
            ItemMeta meta = note.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "Banknote");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.LIGHT_PURPLE + "Wert:" + value);
            meta.setLore(lore);
            note.setItemMeta(meta);

            p.getInventory().addItem(note);
            p.sendMessage(ChatColor.GREEN + "Banknote im Wert von " + ChatColor.YELLOW + value + " Münzen " + ChatColor.GREEN + "erstellt!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
            return true;
        }

        // einzahlen
        if (args.length == 1 && args[0].equalsIgnoreCase("einzahlen")) {
            ItemStack item = p.getInventory().getItemInMainHand();
            if (item == null || item.getType() != Material.PAPER || !item.hasItemMeta()) {
                p.sendMessage(ChatColor.RED + "Du musst die Banknote(n) in der Hand halten!");
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return true;
            }

            ItemMeta meta = item.getItemMeta();
            if (!meta.hasDisplayName() || !meta.getDisplayName().equals(ChatColor.GREEN + "Banknote")) {
                p.sendMessage(ChatColor.RED + "Das ist keine gültige Banknote!");
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return true;
            }

            List<String> lore = meta.getLore();
            if (lore == null || lore.isEmpty()) {
                p.sendMessage(ChatColor.RED + "Die Banknote ist ungültig!");
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return true;
            }

            int amount;
            try {
                String raw = ChatColor.stripColor(lore.get(lore.size() - 1));
                if (raw.contains(":")) raw = raw.split(":")[1];
                amount = Integer.parseInt(raw.trim());
            } catch (NumberFormatException e) {
                p.sendMessage(ChatColor.RED + "Die Banknote enthält keinen gültigen Betrag!");
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return true;
            }

            EconomyUtils.addMoney(p, amount);
            p.sendMessage(ChatColor.GREEN + "Du hast " + ChatColor.YELLOW + amount + " Münzen " + ChatColor.GREEN + "eingezahlt!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);

            p.getInventory().setItemInMainHand(null); // Banknote entfernen
            return true;
        }

        // Info-Befehl
        sendInfo(p);
        return true;
    }

    private void sendInfo(Player p) {
        p.sendMessage(ChatColor.YELLOW + "-------" + ChatColor.DARK_AQUA + "ⓘ" + ChatColor.YELLOW + "---------");
        p.sendMessage("");
        p.sendMessage(ChatColor.GREEN + "Kontostand: " + ChatColor.YELLOW + EconomyUtils.getMoney(p));
        p.sendMessage("");
        p.sendMessage(ChatColor.YELLOW + "--" + ChatColor.GOLD + "Nutzung" + ChatColor.YELLOW + "--");
        p.sendMessage(ChatColor.GRAY + "/bank einzahlen [<all>]");
        p.sendMessage(ChatColor.GRAY + "/bank einzahlen <Betrag> [<Anzahl>]");
        p.sendMessage(ChatColor.GRAY + "/bank create <Zahl>  (temporär zum Testen)");
        p.sendMessage("");
        p.sendMessage(ChatColor.YELLOW + "-------------------");
    }
}
