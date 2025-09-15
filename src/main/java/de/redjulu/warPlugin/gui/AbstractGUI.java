package de.redjulu.warPlugin.gui;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class AbstractGUI {

    protected final Player player;
    protected Inventory inventory;
    private final Map<Integer, Consumer<Player>> buttons = new HashMap<>();
    private AbstractGUI previousGUI; // <- Parent/Back GUI

    public AbstractGUI(Player player) {
        this(player, null, true);
    }

    public AbstractGUI(Player player, AbstractGUI previousGUI) {
        this(player, previousGUI, true);
    }

    public AbstractGUI(Player player, AbstractGUI previousGUI, boolean initInventory) {
        this.player = player;
        this.previousGUI = previousGUI;
        if (initInventory) {
            this.inventory = createInventory();
            setupButtons();
        }
    }

    protected abstract Inventory createInventory();
    public abstract void setupButtons();
    public abstract void handleClick(org.bukkit.event.inventory.InventoryClickEvent e);

    public void handleClose() {}
    public Inventory getInventory() { return inventory; }

    protected void setButton(int slot, Material mat, String displayName, List<String> lore, Consumer<Player> action) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        setButton(slot, item, action);
    }

    protected void setButton(int slot, ItemStack item, Consumer<Player> action) {
        inventory.setItem(slot, item);
        buttons.put(slot, action);
    }

    protected void setButtonSkull(int slot, String base64, String displayName, List<String> lore, Consumer<Player> action) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta != null) {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), "skull");
            profile.setProperty(new ProfileProperty("textures", base64));

            meta.setPlayerProfile(profile);
            meta.setDisplayName(displayName);
            meta.setLore(lore);
            skull.setItemMeta(meta);
        }
        setButton(slot, skull, action);
    }

    protected void setButtonMap(int slot, int mapId, String displayName, List<String> lore, Consumer<Player> action) {
        ItemStack map = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) map.getItemMeta();
        if(meta != null){
            meta.setDisplayName(displayName);
            meta.setLore(lore);
            meta.setMapId(mapId); // die ID der Map, z.B. 0 für leere Map
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            map.setItemMeta(meta);
        }
        setButton(slot, map, action);
    }



    protected Map<Integer, Consumer<Player>> getButtons() { return buttons; }

    public void open() { GUIManager.openGUI(player, this); }
    public void close() { GUIManager.closeGUI(player); }

    protected void handleButtonClick(int slot) {
        if (buttons.containsKey(slot)) {
            buttons.get(slot).accept(player);
        }
    }

    protected void fillEmptySlots(Material material, String displayName, List<String> lore) {
        ItemStack filler = new ItemStack(material);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(lore);
            filler.setItemMeta(meta);
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }
}
