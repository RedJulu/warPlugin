package de.redjulu.warPlugin.commands.team;

import de.redjulu.warPlugin.utils.RankUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class InvseeCommand implements CommandExecutor, Listener {

    private final Map<Player, Player> invseeMap = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cNur ingame nutzbar!");
            return true;
        }

        Player p = (Player) sender;

        if (RankUtils.getRank(p) < 2) {
            p.sendMessage("§cDu brauchst mindestens Rang 2!");
            return true;
        }

        if (args.length != 1) {
            p.sendMessage("§cBenutzung: /invsee <spieler>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            p.sendMessage("§cSpieler nicht gefunden!");
            return true;
        }

        Inventory inv = Bukkit.createInventory(null, 54, "§8Inv von §c" + target.getName());

        // Normales Inventar (0-35)
        for (int i = 0; i < 36; i++) {
            ItemStack item = target.getInventory().getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                inv.setItem(i, item.clone());
            }
        }

        // Armor + Offhand in letzte Reihe
        int base = 45;
        inv.setItem(base, target.getInventory().getHelmet());
        inv.setItem(base + 1, target.getInventory().getChestplate());
        inv.setItem(base + 2, target.getInventory().getLeggings());
        inv.setItem(base + 3, target.getInventory().getBoots());
        inv.setItem(base + 4, target.getInventory().getItemInOffHand());

        p.openInventory(inv);
        invseeMap.put(p, target);

        return true;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        HumanEntity viewer = e.getWhoClicked();
        if (!(viewer instanceof Player)) return;

        Player p = (Player) viewer;
        if (!invseeMap.containsKey(p)) return;

        Player target = invseeMap.get(p);
        Inventory inv = e.getInventory();

        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("WarPlugin"), () -> {
            // Normales Inventar syncen
            for (int i = 0; i < 36; i++) {
                target.getInventory().setItem(i, inv.getItem(i));
            }

            // Armor + Offhand zurück
            int base = 45;
            target.getInventory().setHelmet(inv.getItem(base));
            target.getInventory().setChestplate(inv.getItem(base + 1));
            target.getInventory().setLeggings(inv.getItem(base + 2));
            target.getInventory().setBoots(inv.getItem(base + 3));
            target.getInventory().setItemInOffHand(inv.getItem(base + 4));

            target.updateInventory();
        }, 1L); // ein Tick später, damit Bukkit das Click-Update fertig hat
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        HumanEntity viewer = e.getPlayer();
        if (!(viewer instanceof Player)) return;

        Player p = (Player) viewer;
        if (invseeMap.containsKey(p)) {
            invseeMap.remove(p);
        }
    }
}
