package de.redjulu.warPlugin.gui;

import de.redjulu.warPlugin.ScoreboardClass;
import de.redjulu.warPlugin.TablistClass;
import de.redjulu.warPlugin.WarPlugin;
import de.redjulu.warPlugin.utils.RankUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.Collections;

public class RankGUI extends AbstractGUI {

    private final Player target;
    private int tempRank;

    public RankGUI(Player player, Player target) {
        super(player, null, false);

        if (target != null) {
            System.out.println("DEBUG: Öffne RankGUI für RedJulu");
        }

        this.target = target;
        this.tempRank = RankUtils.getRank(target); // Startwert
        this.inventory = createInventory();
        setupButtons();
    }

    @Override
    protected Inventory createInventory() {
        return player.getServer().createInventory(null, 9*3, "Rang von " + target.getName());
    }

    @Override
    public void setupButtons() {
        setButton(4, Material.NAME_TAG,
                RankUtils.getRankColour(target) + target.getName() + " §7– Rang: " + RankUtils.getRankName(target),
                Collections.singletonList("§7Neuer Rang: " + tempRank),
                p -> {}
        );

        setButton(12, Material.BEACON,
                "§aUpgrade/Downgrade Beacon",
                Arrays.asList("§7Links-Klick: Upgrade", "§7Rechts-Klick: Downgrade"),
                p -> {}
        );

        setButton(14, Material.NETHER_STAR,
                "§bSpeichern & Up/Down",
                Arrays.asList("§7Klick: Up/Down und speichern"),
                p -> {}
        );
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        e.setCancelled(true);

        switch (e.getSlot()) {
            case 12 -> { // Beacon: tempRank ändern, GUI aktualisieren
                if (e.isLeftClick()) tempRank++;
                if (e.isRightClick()) tempRank--;

                if (tempRank < 0) tempRank = 0;
                if (tempRank > 2) tempRank = 2;

                // GUI neu öffnen, damit NameTag den neuen Wert anzeigt
                new RankGUI(player, target).open();
            }

            case 14 -> { // Stern: speichern
                tempRank++;
                if (tempRank > 2) tempRank = 0;

                WarPlugin.getInstance().ranks.put(target.getUniqueId(), tempRank);
                WarPlugin.getInstance().saveMap(WarPlugin.getInstance().ranksFile, WarPlugin.getInstance().ranks);

                // Scoreboard & Tablist updaten
                ScoreboardClass.updateScoreboard(target);
                TablistClass.updateTablist();

                new RankGUI(player, target).open();
            }
        }
    }
}
