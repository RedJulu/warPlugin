package de.redjulu.warPlugin.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.Map;

public class GUIManager implements Listener {

    private static final Map<Player, AbstractGUI> openGUIs = new HashMap<>();

    public static void openGUI(Player player, AbstractGUI gui) {
        openGUIs.put(player, gui);
        player.openInventory(gui.getInventory());
    }

    public static void closeGUI(Player player) {
        openGUIs.remove(player);
        player.closeInventory();
    }

    public static AbstractGUI getOpenGUI(Player player) {
        return openGUIs.get(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        AbstractGUI gui = getOpenGUI(player);
        if (gui == null) return;

        e.setCancelled(true);
        gui.handleClick(e);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        AbstractGUI gui = getOpenGUI(player);
        if (gui != null) {
            gui.handleClose();
            openGUIs.remove(player);
        }
    }
}
