package de.redjulu.warPlugin.listeners;

import de.redjulu.warPlugin.WarPlugin;
import de.redjulu.warPlugin.utils.EconomyUtils;
import de.redjulu.warPlugin.utils.RankUtils;
import de.redjulu.warPlugin.ScoreboardClass;
import de.redjulu.warPlugin.TablistClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MainListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        var player = e.getPlayer();

        ScoreboardClass.createScoreboard(player);


        var plugin = WarPlugin.getInstance();

        plugin.money.computeIfAbsent(player.getUniqueId(), k -> 0);
        plugin.ranks.computeIfAbsent(player.getUniqueId(), k -> 0);
        plugin.modify.computeIfAbsent(player.getUniqueId(), k -> false);
        boolean vanished = plugin.vanish.getOrDefault(player.getUniqueId(), false);

        if (vanished) {
            e.setJoinMessage(null);
        } else {
            plugin.vanish.putIfAbsent(player.getUniqueId(), false);
            e.setJoinMessage("§7§l[§a§l+§7§l] " + RankUtils.getRankColour(player) + player.getName());
        }

        Bukkit.getScheduler().runTaskLater(plugin, TablistClass::updateTablist, 1L);

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        var player = e.getPlayer();
        var plugin = WarPlugin.getInstance();

        Bukkit.getScheduler().runTaskLater(plugin, TablistClass::updateTablist, 1L);

        boolean vanished = plugin.vanish.getOrDefault(player.getUniqueId(), false);

        if (vanished) {
            e.setQuitMessage(null);
        } else {
            e.setQuitMessage("§7§l[§c§l-§7§l] " + RankUtils.getRankColour(player) + player.getName());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action action = e.getAction();

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = p.getInventory().getItemInMainHand();
        if (item == null || item.getType() != Material.PAPER || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName() || !meta.getDisplayName().equals(ChatColor.GREEN + "Banknote")) return;

        List<String> lore = meta.getLore();
        if (lore == null || lore.isEmpty()) return;

        int amount;
        try {
            String raw = ChatColor.stripColor(lore.get(lore.size() - 1));
            if (raw.contains(":")) raw = raw.split(":")[1];
            amount = Integer.parseInt(raw.trim());
        } catch (NumberFormatException ex) {
            p.sendMessage(ChatColor.RED + "Die Banknote enthält keinen gültigen Betrag!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        // Geld hinzufügen
        EconomyUtils.addMoney(p, amount);
        p.sendMessage(ChatColor.GREEN + "Du hast " + ChatColor.YELLOW + amount + " Münzen " + ChatColor.GREEN + "eingezahlt!");
        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);

        // Banknote entfernen
        int newAmount = item.getAmount() - 1;
        if (newAmount <= 0) {
            p.getInventory().setItemInMainHand(null);
        } else {
            item.setAmount(newAmount);
            p.getInventory().setItemInMainHand(item);
        }
    }
}
