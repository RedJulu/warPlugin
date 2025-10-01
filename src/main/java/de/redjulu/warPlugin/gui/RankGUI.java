package de.redjulu.warPlugin.gui;

import de.redjulu.warPlugin.ScoreboardClass;
import de.redjulu.warPlugin.TablistClass;
import de.redjulu.warPlugin.WarPlugin;
import de.redjulu.warPlugin.utils.RankUtils;
import org.bukkit.Material;
import org.bukkit.Sound; // WICHTIG: Sound importieren
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;

public class RankGUI extends AbstractGUI {

    private final Player target;
    private int tempRank;
    private String tempOrg; // Variable für die temporäre Org-Auswahl

    public RankGUI(Player player, Player target) {
        super(player);
        this.target = target;

        // Beim ersten Öffnen den aktuellen Rang und die Org des Targets laden
        this.tempRank = RankUtils.getRank(target);
        this.tempOrg = WarPlugin.getInstance().org.getOrDefault(target.getUniqueId(), "§8[§c✖§8]");

        this.inventory = createInventory();
        setupButtons();
    }

    @Override
    protected org.bukkit.inventory.Inventory createInventory() {
        return player.getServer().createInventory(null, 9 * 3, "§8§l» §eRang Verwaltung §8| §6" + target.getName());
    }

    @Override
    public void setupButtons() {
        // Rang-Anzeige
        updateNameTag();

        // Beacon: Simulation Up/Downgrade
        setButton(12, Material.BEACON,
                "§b§lRang Anpassen",
                Arrays.asList(
                        "§7Links-Klick: §a§lRang erhöhen",
                        "§7Rechts-Klick: §c§lRang senken"
                ),
                p -> {}
        );

        // Netherstar: Speichern & Anwenden
        setButton(14, Material.NETHER_STAR,
                "§a§lÄnderungen Speichern & Anwenden",
                Collections.singletonList("§7Klicke hier, um den Wunschrang und die §bOrganisation§7 zu §bübernehmen§7."),
                p -> {}
        );

        // ORG-BUTTONS
        setButton(21, Material.BARRIER,
                "§8[§c✖§8] §cKeine Organisation",
                Collections.singletonList("§7Setzt die Organisation auf §cNichts§7."),
                p -> {}
        );

        setButton(22, Material.WHITE_BANNER,
                "§8[§eDE§8] §eDeutschland",
                Collections.singletonList("§7Wählt die Organisation §eDeutschland§7."),
                p -> {}
        );

        setButton(23, Material.BLUE_BANNER,
                "§8[§9RU§8] §9Russland",
                Collections.singletonList("§7Wählt die Organisation §9Russland§7."),
                p -> {}
        );

        // Füller füllen
        fillEmptySlots(Material.GRAY_STAINED_GLASS_PANE, "§7", Collections.emptyList());
    }

    private void updateNameTag() {
        ItemStack nameTag = new ItemStack(Material.NAME_TAG, tempRank + 1);
        ItemMeta meta = nameTag.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(tempOrg + RankUtils.getRankColour(target) + target.getName() + " §7– Rang: " + RankUtils.getRankName(target));

            meta.setLore(Arrays.asList(
                    "§7Gewählter Wunschrang: §e§l" + tempRank,
                    "§7Gewählte Organisation: " + tempOrg
            ));
            nameTag.setItemMeta(meta);
        }
        setButton(4, nameTag, p -> {});
    }

    private void updateOrgDisplay(String newOrgPrefix) {
        this.tempOrg = newOrgPrefix;
        updateNameTag();
        // Sound bei erfolgreicher Org-Änderung
        player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0f, 2.0f);
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;

        e.setCancelled(true);

        switch (e.getSlot()) {
            case 12 -> { // Beacon: Simulation (Rang)
                boolean rankChanged = false;

                // Grenze oben erreicht
                if (e.isLeftClick() && tempRank >= 2) {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    return;
                }
                // Grenze unten erreicht
                if (e.isRightClick() && tempRank <= 0) {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    return;
                }

                // Rang erhöhen
                if (e.isLeftClick()) {
                    tempRank++;
                    rankChanged = true;
                }
                // Rang senken
                else if (e.isRightClick()) {
                    tempRank--;
                    rankChanged = true;
                }

                if (rankChanged) {
                    updateNameTag();
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f); // Positiver Klick-Sound
                }
            }

            // ORG-ÄNDERUNGEN (Sounds sind in updateOrgDisplay() eingebaut)
            case 21 -> updateOrgDisplay("§8[§c✖§8] ");
            case 22 -> updateOrgDisplay("§8[§eDE§8] ");
            case 23 -> updateOrgDisplay("§8[§9RU§8] ");

            case 14 -> { // Netherstar: Apply Rang und ORG
                // 1. Rang speichern (IN MAP)
                WarPlugin.getInstance().ranks.put(target.getUniqueId(), tempRank);

                // 2. ORG speichern (IN MAP)
                if (this.tempOrg.equals("§8[§c✖§8]")) {
                    WarPlugin.getInstance().org.remove(target.getUniqueId());
                } else {
                    WarPlugin.getInstance().org.put(target.getUniqueId(), this.tempOrg);
                }

                // 3. Scoreboard/Tablist aktualisieren
                ScoreboardClass.updateScoreboard(target);
                TablistClass.updateTablist();

                // 4. GUI schließen und Feedback geben
                player.closeInventory();
                // Erfolgssound beim Speichern
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                player.sendMessage("§8§l» §aDer Rang und die Organisation von " + RankUtils.getRankColour(target) + target.getName() + "§a wurden erfolgreich gesetzt. (§e" + this.tempOrg + "§a)");
            }
        }
    }
}